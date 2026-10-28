/* شرح الملف: تشغيل حصر الشاشات المتاحة للنشاط المختار.
 */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.ActivityScreenInventory;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Util.PropertyReader;
import java.nio.file.*;

public final class ActivityScreenInventoryCommand {
    private ActivityScreenInventoryCommand() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Expected output JSON path");
        Path output = Path.of(args[0]);
        var driver = WebDriverFactory.initDriver();
        try {
            driver.get(PropertyReader.getApplicationUrl().replaceAll("/+$", "") + "/#/login");
            var login = new LoginPage(driver);
            login.Login(PropertyReader.getValidUsername(), PropertyReader.getValidPassword());
            login.AssertLoginPageTitle();
            var selected = Plugin_ERP.BranchProfiles.BranchProfiles.selected();
            if (selected.isPresent()) {
                if (Boolean.getBoolean("branch.profile.select"))
                    Plugin_ERP.BranchProfiles.BranchSessionSelector.select(
                            driver, selected.orElseThrow());
                selected.orElseThrow()
                        .verify(
                                Plugin_ERP.BranchProfiles.BranchProfileReader.capture(
                                        driver, java.time.Duration.ofSeconds(30)));
            }
            var inventory = ActivityScreenInventory.capture(driver);
            Files.createDirectories(output.toAbsolutePath().getParent());
            Files.writeString(output, new org.json.JSONObject(inventory).toString(2));
            System.out.println("Activity screen inventory saved; no application data changed.");
        } finally {
            WebDriverFactory.quitDriver();
        }
    }
}
