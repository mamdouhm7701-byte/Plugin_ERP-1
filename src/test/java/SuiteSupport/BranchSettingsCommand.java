/* شرح الملف: تشغيل مراجعة إعدادات الفرع المطلوب.
 */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Util.PropertyReader;
import java.nio.file.*;
import java.time.Duration;
import java.util.Map;

public final class BranchSettingsCommand {
    private BranchSettingsCommand() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 3 || !(args[2].equals("preview") || args[2].equals("apply")))
            throw new IllegalArgumentException(
                    "Expected profile name, new evidence directory, preview|apply");
        var target =
                BranchProfiles.selected(
                                args[0],
                                Path.of("config/branch-profiles"),
                                System.getProperty("branch.profile.fingerprint"))
                        .orElseThrow();
        Path output = Path.of(args[1]);
        Files.createDirectories(output);
        var driver = WebDriverFactory.initDriver();
        try {
            driver.get(PropertyReader.getApplicationUrl().replaceAll("/+$", "") + "/#/login");
            var login = new LoginPage(driver);
            login.Login(PropertyReader.getValidUsername(), PropertyReader.getValidPassword());
            login.AssertLoginPageTitle();
            var before = BranchProfileReader.capture(driver, Duration.ofSeconds(30));
            var plan = new BranchConfigurationPlan(before, target);
            Files.writeString(
                    output.resolve("before.json"),
                    new org.json.JSONObject(
                                    Map.of(
                                            "before",
                                            before.fields(),
                                            "target",
                                            target.expected().fields(),
                                            "fingerprint",
                                            target.fingerprint(),
                                            "changes",
                                            plan.changes(),
                                            "mode",
                                            args[2]))
                            .toString(2),
                    StandardOpenOption.CREATE_NEW);
            var settings = new BranchSettingsPage(driver, before);
            if (args[2].equals("apply")) BranchConfigurationWorkflow.apply(plan, settings);
            else {
                settings.openForm();
                new BranchProfile("preview-before", before)
                        .verify(
                                BranchConfigurationPlan.fromForm(
                                        before.origin(), settings.formValues()));
            }
            Files.writeString(
                    output.resolve("result.json"),
                    new org.json.JSONObject(
                                    Map.of(
                                            "mode",
                                            args[2],
                                            "verified",
                                            true,
                                            "changed",
                                            args[2].equals("apply") && !plan.changes().isEmpty(),
                                            "profile",
                                            target.name(),
                                            "fingerprint",
                                            target.fingerprint()))
                            .toString(2),
                    StandardOpenOption.CREATE_NEW);
            System.out.println("Branch settings " + args[2] + " completed; evidence preserved.");
        } finally {
            WebDriverFactory.quitDriver();
        }
    }
}
