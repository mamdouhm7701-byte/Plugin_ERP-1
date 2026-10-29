/* شرح الملف: يعرض حالات XML مباشرة في TestNG، ويجهز فرعًا لكل سياسة قبل حالاتها وينظفه بعدها. يمنع الجولة التالية إذا لم يكتمل التجهيز أو التنظيف. */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import java.nio.file.*;
import java.util.*;
import org.testng.*;
import org.testng.xml.XmlSuite;

public class XmlReviewSuiteListener extends NativeInteractiveSuiteListener
        implements ISuiteListener, IInvokedMethodListener {
    private final Map<XmlSuite, Round> rounds = new LinkedHashMap<>();
    private String blocked;
    private Round active;
    private Path output;

    private static final class Round {
        final BusinessRunDialog.Selection selection;
        final Path output;
        BusinessRunListener preparation;
        boolean ready, finished, cleanupAttempted;
        final Set<XmlSuite> completed = Collections.newSetFromMap(new IdentityHashMap<>());
        int expectedSuites;
        long preparationMillis, suiteMillis, cleanupMillis, executionStarted;
        String startedUtc = "", finishedUtc = "", previousEvidence;
        boolean evidencePinned;

        Round(BusinessRunDialog.Selection selection, Path output) {
            this.selection = selection;
            this.output = output;
        }
    }

    BusinessRunDialog.Selection selection() throws Exception {
        return BusinessRunDialog.choose(true);
    }

    Path output(Path project) {
        return project.resolve("target/validation/xml-" + UUID.randomUUID());
    }

    @Override
    public void alter(List<XmlSuite> suites) {
        try {
            Path project = Path.of("").toAbsolutePath().normalize();
            acquire(project);
            var choice = selection();
            suites.clear();
            if (choice == null) {
                super.onExecutionFinish();
                return;
            }
            try {
                output = output(project);
                suites.addAll(plan(project, choice, output));
            } finally {
                choice.target().clearSecrets();
            }
            System.out.println("Native XML TestNG run: " + output);
        } catch (Exception failure) {
            onExecutionFinish();
            throw new IllegalStateException(startupMessage(failure), failure);
        }
    }

    static String startupMessage(Exception failure) {
        String cause = Objects.toString(failure.getMessage(), failure.getClass().getSimpleName());
        if (cause.contains("Another ERP run is active")
                || cause.contains("ERP script run is still active"))
            return "لم يبدأ تشغيل XML: فيه تشغيل ERP آخر شغال في الخلفية. انتظر اكتماله أو أوقفه ونظف بياناته قبل إعادة التشغيل. لا تشغل مجموعتين معًا على نفس البيانات.";
        return "تعذر بدء مراجعة XML: " + cause;
    }

    List<XmlSuite> plan(Path project, BusinessRunDialog.Selection choice, Path folder)
            throws Exception {
        boolean matrix = choice.level().equals("XML:" + XmlSuiteCatalog.INVOICE_REVIEW);
        if (matrix && choice.business().activity() != ActivityType.RESTAURANT)
            throw new IllegalArgumentException("مراجعة الفواتير الأربع تحتاج نشاط المطاعم");
        var credentials = choice.target().settings();
        var result = new ArrayList<XmlSuite>();
        try {
            String key =
                    choice.target().description().get("environment").equals("Test")
                            ? "Test"
                            : choice.target().description().get("tenant");
            for (var tax : XmlReviewPolicies.policies(matrix, choice.business().tax())) {
                var original = choice.business();
                var request =
                        new BusinessRunRequest(
                                original.activity(),
                                original.discount(),
                                tax,
                                original.stocktaking(),
                                original.additionalTax());
                var details = choice.target().description();
                var target =
                        new BusinessRunTarget(
                                details.get("environment"),
                                details.get("tenant"),
                                details.get("url"),
                                credentials.get(key + ".Username"),
                                credentials.get(key + ".Password").toCharArray());
                var selection =
                        new BusinessRunDialog.Selection(
                                request, choice.level(), choice.execution(), target, 1);
                Path roundOutput = folder.resolve(tax.name());
                var selected =
                        XmlSuiteCatalog.plan(project, choice.level().substring(4), 1, roundOutput);
                // A policy owns one root suite so its branch survives all nested XML suites.
                var root = new XmlSuite();
                root.setName("XML / " + tax.code());
                root.setParallel(XmlSuite.ParallelMode.NONE);
                for (var child : selected) {
                    child.setParentSuite(root);
                    child.setName(tax.code() + " / " + child.getName());
                }
                root.getChildSuites().addAll(selected);
                var round = new Round(selection, roundOutput);
                round.expectedSuites = count(root);
                rounds.put(root, round);
                StabilitySelectionEvidence.write(roundOutput, selected, "CAPTURED_BEFORE_RUN");
                result.add(root);
            }
            return result;
        } finally {
            credentials.clear();
        }
    }

    private static int count(XmlSuite suite) {
        return 1 + suite.getChildSuites().stream().mapToInt(XmlReviewSuiteListener::count).sum();
    }

    BusinessRunListener preparer(BusinessRunDialog.Selection choice, XmlSuite suite) {
        return new BusinessRunListener() {
            @Override
            BusinessRunDialog.Selection selectBusiness() {
                return choice;
            }

            @Override
            void acquire(Path project) {
                /* The outer native run owns the lock. */
            }

            @Override
            List<XmlSuite> plan(Path project, BusinessRunDialog.Selection selection, Path output)
                    throws Exception {
                Files.writeString(
                        output.resolve("xml-policy-settings.json"),
                        new org.json.JSONObject(selection.business().fields()).toString(2));
                return List.of(suite);
            }
        };
    }

    @Override
    public synchronized void onStart(ISuite suite) {
        var round = round(suite.getXmlSuite());
        if (round == null) return;
        if (round.ready || round.preparation != null || blocked != null) return;
        if (active != null) {
            blocked = "Overlapping XML policies are not allowed";
            return;
        }
        active = round;
        long preparationStarted = System.nanoTime();
        round.startedUtc = java.time.Instant.now().toString();
        round.previousEvidence = System.getProperty("erp.run.evidence.output");
        System.setProperty("erp.run.evidence.output", round.output.toString());
        round.evidencePinned = true;
        try {
            if (Plugin_ERP.Util.RunEnvironmentGate.current().block().isPresent())
                throw new IllegalStateException("Environment blocked the previous policy");
            round.preparation = preparer(round.selection, suite.getXmlSuite());
            round.preparation.alter(new ArrayList<>());
            round.ready = true;
            round.executionStarted = System.nanoTime();
        } catch (Exception | AssertionError failure) {
            blocked = "Policy preparation failed: " + failure.getClass().getSimpleName();
            suite.setAttribute("xmlReviewFailure", failure);
            System.err.println(blocked + "; preserve recovery journals.");
        } finally {
            round.preparationMillis = (System.nanoTime() - preparationStarted) / 1_000_000;
        }
    }

    private Round round(XmlSuite suite) {
        for (var current = suite; current != null; current = current.getParentSuite()) {
            var found = rounds.get(current);
            if (found != null) return found;
        }
        return null;
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        var round = round(result.getTestContext().getSuite().getXmlSuite());
        if (round != null && (!round.ready || round.finished))
            throw new SkipException(
                    "لم تنفذ الحالة لأن تجهيز سياسة الضريبة أو تنظيف السابقة لم يكتمل: " + blocked);
    }

    @Override
    public synchronized void onFinish(ISuite suite) {
        var round = round(suite.getXmlSuite());
        if (round == null) return;
        round.completed.add(suite.getXmlSuite());
        if (round.completed.size() != round.expectedSuites) return;
        round.cleanupAttempted = true;
        long cleanupStarted = System.nanoTime();
        if (round.executionStarted != 0)
            round.suiteMillis = (cleanupStarted - round.executionStarted) / 1_000_000;
        try {
            if (round.preparation != null) round.preparation.onExecutionFinish();
            round.finished = round.ready;
            if (Plugin_ERP.Util.RunEnvironmentGate.current().block().isPresent())
                blocked = "Environment blocked further policies";
        } catch (Exception | AssertionError failure) {
            blocked = "Policy cleanup failed: " + failure.getClass().getSimpleName();
            suite.setAttribute("xmlReviewFailure", failure);
        } finally {
            round.cleanupMillis = (System.nanoTime() - cleanupStarted) / 1_000_000;
            round.finishedUtc = java.time.Instant.now().toString();
            restoreEvidence(round);
            round.selection.target().clearSecrets();
            if (active == round) active = null;
            writeSummary();
        }
    }

    private void writeSummary() {
        if (output == null) return;
        try {
            Files.writeString(
                    output.resolve("summary.json"),
                    new org.json.JSONObject(
                                    Map.of(
                                            "blocked",
                                            Objects.toString(blocked, ""),
                                            "policies",
                                            rounds.values().stream()
                                                    .map(
                                                            r ->
                                                                    Map.of(
                                                                            "tax",
                                                                            r.selection
                                                                                    .business()
                                                                                    .tax()
                                                                                    .name(),
                                                                            "prepared",
                                                                            r.ready,
                                                                            "cleaned",
                                                                            r.finished,
                                                                            "folder",
                                                                            r.output.toString(),
                                                                            "preparationMillis",
                                                                            r.preparationMillis,
                                                                            "suiteIncludingFixturesMillis",
                                                                            r.suiteMillis,
                                                                            "branchCleanupMillis",
                                                                            r.cleanupMillis,
                                                                            "startedUtc",
                                                                            r.startedUtc,
                                                                            "finishedUtc",
                                                                            r.finishedUtc))
                                                    .toList()))
                            .toString(2));
        } catch (java.io.IOException failure) {
            throw new IllegalStateException("Could not save XML run summary", failure);
        }
    }

    private static void restoreEvidence(Round round) {
        if (!round.evidencePinned) return;
        if (round.previousEvidence == null) System.clearProperty("erp.run.evidence.output");
        else System.setProperty("erp.run.evidence.output", round.previousEvidence);
        round.evidencePinned = false;
    }

    @Override
    public void onExecutionFinish() {
        try {
            for (var round : rounds.values()) {
                try {
                    if (round.preparation != null && !round.cleanupAttempted) {
                        round.cleanupAttempted = true;
                        round.preparation.onExecutionFinish();
                    }
                } finally {
                    restoreEvidence(round);
                    round.selection.target().clearSecrets();
                }
            }
        } finally {
            super.onExecutionFinish();
        }
        if (blocked != null) throw new IllegalStateException("XML review blocked: " + blocked);
    }
}
