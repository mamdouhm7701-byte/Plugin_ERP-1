/* شرح الملف: تجهيز فرع الاختبار ثم بدء الحزمة وتنظيف الفرع عند الانتهاء.
 */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import java.nio.file.*;
import java.util.*;
import org.testng.xml.XmlSuite;

public class BusinessRunListener extends NativeInteractiveSuiteListener {
    private BranchProfile ownedProfile;
    private Path runOutput;
    private final Map<String, String> previous = new LinkedHashMap<>();

    private void set(String key, String value) {
        if (!previous.containsKey(key)) previous.put(key, System.getProperty(key));
        System.setProperty(key, value);
    }

    BusinessRunDialog.Selection selectBusiness() throws Exception {
        return BusinessRunDialog.choose();
    }

    void confirmedBranchCreated(BranchProfile profile) {
        ownedProfile = profile;
    }

    void cleanupOwnedBranch(BranchProfile profile, Path output) throws Exception {
        BusinessBranchCleanup.remove(Path.of("").toAbsolutePath(), profile, output);
    }

    BranchProfile prepare(BusinessRunRequest request, Path project, Path output) throws Exception {
        return BusinessBranchPreparation.prepare(
                request, project, output, this::confirmedBranchCreated);
    }

    List<XmlSuite> plan(Path project, BusinessRunDialog.Selection selection, Path output)
            throws Exception {
        return BusinessRunSuites.plan(
                project, selection.business(), selection.level(), selection.workers(), output);
    }

    @Override
    public void alter(List<XmlSuite> suites) {
        Plugin_ERP.Util.Allur.AllureContextNoiseFilter.install();
        try {
            var choice = selectBusiness();
            if (choice == null) {
                suites.clear();
                System.out.println("Canceled. No business settings or tests changed.");
                return;
            }
            Path project = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
            acquire(project);
            set("ExecutionType", choice.execution());
            try {
                choice.target().settings().forEach(this::set);
            } finally {
                choice.target().clearSecrets();
            }
            double freeGb =
                    ((com.sun.management.OperatingSystemMXBean)
                                            java.lang.management.ManagementFactory
                                                    .getOperatingSystemMXBean())
                                    .getFreeMemorySize()
                            / (1024d * 1024 * 1024);
            int workers =
                    safeWorkers(
                            choice.workers(), freeGb, Runtime.getRuntime().availableProcessors());
            Path output = project.resolve("target/validation/business-" + UUID.randomUUID());
            runOutput = output;
            Files.createDirectories(output);
            Files.writeString(
                    output.resolve("run-settings.json"),
                    new org.json.JSONObject(
                                    Map.of(
                                            "target",
                                            choice.target().description(),
                                            "execution",
                                            choice.execution(),
                                            "requestedBrowsers",
                                            choice.workers(),
                                            "selectedBrowsers",
                                            workers))
                            .toString(2));
            var profile = prepare(choice.business(), project, output.resolve("preparation"));
            ownedProfile = profile;
            int capacityWorkers =
                    CapacityPreflight.workerLimit(workers, output.resolve("preparation"));
            if (capacityWorkers < workers) {
                workers = capacityWorkers;
                Files.writeString(
                        output.resolve("run-settings.json"),
                        new org.json.JSONObject(
                                        Map.of(
                                                "target",
                                                choice.target().description(),
                                                "execution",
                                                choice.execution(),
                                                "requestedBrowsers",
                                                choice.workers(),
                                                "selectedBrowsers",
                                                workers,
                                                "reason",
                                                "Available store headroom limits concurrent fixture setup"))
                                .toString(2));
                System.out.println(
                        "Browser limit reduced to " + workers + " by available store capacity.");
            }
            set("branch.profile", profile.name());
            set("branch.profile.fingerprint", profile.fingerprint());
            set("branch.profile.select", "true");
            set("business.additionalTax", Boolean.toString(choice.business().additionalTax()));
            BranchProfileCommand.write(
                    profile.name(),
                    project.resolve("config/branch-profiles"),
                    output.resolve("branch-profile.json"),
                    profile.fingerprint());
            var planned =
                    plan(
                            project,
                            new BusinessRunDialog.Selection(
                                    choice.business(),
                                    choice.level(),
                                    choice.execution(),
                                    choice.target(),
                                    workers),
                            output);
            suites.clear();
            suites.addAll(planned);
            System.out.println("Business verified: " + choice.business().fields());
            System.out.println(
                    "Target: "
                            + choice.target().description()
                            + "; requested browsers: "
                            + choice.workers()
                            + "; selected maximum: "
                            + workers);
            System.out.println(
                    "Branch: " + profile.expected().branchId() + "; profile: " + profile.name());
            System.out.println("Native TestNG run; evidence and coverage limits: " + output);
        } catch (Exception | AssertionError failure) {
            try {
                onExecutionFinish();
            } catch (Exception | AssertionError cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw new IllegalStateException(
                    "Business run stopped before tests: " + failure.getMessage(), failure);
        }
    }

    @Override
    public void onExecutionFinish() {
        try {
            if (ownedProfile != null) {
                var profile = ownedProfile;
                ownedProfile = null;
                try {
                    cleanupOwnedBranch(profile, runOutput);
                } catch (Exception | AssertionError failure) {
                    throw new IllegalStateException(
                            "Owned branch cleanup did not complete; preserve recovery evidence",
                            failure);
                }
            }
        } finally {
            super.onExecutionFinish();
            previous.forEach(
                    (key, value) -> {
                        if (value == null) System.clearProperty(key);
                        else System.setProperty(key, value);
                    });
            previous.clear();
        }
    }
}
