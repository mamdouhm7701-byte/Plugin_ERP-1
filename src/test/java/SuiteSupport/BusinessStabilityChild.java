/* شرح الملف: تنفيذ جولة واحدة من فحص الثبات بفرع جديد يحمل نفس اختيارات النشاط والتننت.
 * فائدته: ينظف الفرع بالمسار المعتاد ويسجل عدد المتصفحات الفعلي دون خلط الجولات.
 */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

public final class BusinessStabilityChild {
    private BusinessStabilityChild() {}

    public static void main(String[] args) throws Exception {
        Path request = Path.of(args[0]), output = Path.of(args[1]);
        var values = new JSONObject(Files.readString(request));
        var secrets =
                new JSONObject(
                        new String(
                                System.in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
        String fallback =
                values.getString("environment").equals("Test")
                        ? "Test"
                        : values.getString("tenant");
        var target =
                new BusinessRunTarget(
                        values.getString("environment"),
                        values.getString("tenant"),
                        values.getString("url"),
                        secrets.getString(fallback + ".Username"),
                        secrets.getString(fallback + ".Password").toCharArray());
        var business =
                new BusinessRunRequest(
                        ActivityType.valueOf(values.getString("activity")),
                        DiscountLevel.valueOf(values.getString("discount")),
                        TaxPolicy.valueOf(values.getString("tax")),
                        StocktakingMethod.valueOf(values.getString("stocktaking")),
                        values.getBoolean("additionalTax"));
        var choice =
                new BusinessRunDialog.Selection(
                        business,
                        values.getString("level"),
                        values.getString("execution"),
                        target,
                        values.getInt("workers"));
        var listener = new RoundListener(choice, output);
        int exit;
        try {
            var testng = engine();
            testng.addListener(listener);
            testng.addListener(new BranchIsolationListener());
            testng.setOutputDirectory(output.resolve("testng").toString());
            testng.run();
            exit =
                    !listener.cleaned
                                    || Plugin_ERP.Util.RunEnvironmentGate.current()
                                            .block()
                                            .isPresent()
                            ? 2
                            : testng.getStatus() == 0 ? 0 : 1;
        } catch (Exception | AssertionError failure) {
            System.err.println(
                    "Stability round stopped: "
                            + failure.getClass().getSimpleName()
                            + "; preserve logs and recovery journals.");
            exit = 2;
        } finally {
            target.clearSecrets();
        }
        Files.writeString(
                output.resolve("round-completion.json"),
                new JSONObject(Map.of("cleaned", listener.cleaned, "exitCode", exit)).toString(2));
        System.exit(exit);
    }

    static TestNG engine() {
        var testng = new TestNG();
        var suite = new XmlSuite();
        suite.setName("Business stability round");
        testng.setXmlSuites(new ArrayList<>(List.of(suite)));
        return testng;
    }

    private static final class RoundListener extends BusinessRunListener {
        private final BusinessRunDialog.Selection choice;
        private final Path output;
        private boolean cleaned;

        RoundListener(BusinessRunDialog.Selection choice, Path output) {
            this.choice = choice;
            this.output = output;
        }

        @Override
        BusinessRunDialog.Selection selectBusiness() {
            return choice;
        }

        @Override
        List<XmlSuite> plan(Path project, BusinessRunDialog.Selection selection, Path folder)
                throws Exception {
            var suites = super.plan(project, selection, folder);
            StabilitySelectionEvidence.write(output, suites, "CAPTURED_BEFORE_RUN");
            var fields = new LinkedHashMap<String, Object>(selection.business().fields());
            fields.putAll(selection.target().description());
            fields.put("workers", selection.workers());
            fields.put("level", selection.level());
            fields.put("execution", selection.execution());
            Files.writeString(
                    output.resolve("effective-settings.json"), new JSONObject(fields).toString(2));
            return suites;
        }

        @Override
        public void onExecutionFinish() {
            super.onExecutionFinish();
            cleaned = true;
        }
    }
}
