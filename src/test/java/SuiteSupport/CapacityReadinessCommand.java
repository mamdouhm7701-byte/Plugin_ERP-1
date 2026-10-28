/* شرح الملف: تسجيل الدخول للبيئة المحددة وقراءة سعة الاشتراك دون إنشاء فرع أو تغيير بيانات.
 * فائدته: يحدد هل التأهيل الحي يمكن أن يبدأ ويحفظ دليلًا مستقلًا مع احترام قفل التشغيل.
 */
package SuiteSupport;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Util.*;
import java.nio.file.*;

public final class CapacityReadinessCommand {
    private CapacityReadinessCommand() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Expected evidence directory");
        Plugin_ERP.Util.Allur.AllureContextNoiseFilter.install();
        var gate = new NativeInteractiveSuiteListener();
        gate.acquire(Path.of(System.getProperty("user.dir")).toAbsolutePath());
        try {
            PropertyReader.loadProperties();
            var driver = WebDriverFactory.initDriver();
            driver.get(PropertyReader.getApplicationUrl().replaceAll("/+$", "") + "/#/login");
            var login = new LoginPage(driver);
            login.Login(PropertyReader.getValidUsername(), PropertyReader.getValidPassword());
            login.AssertLoginPageTitle();
            CapacityPreflight.verify(driver, Path.of(args[0]));
            System.out.println(
                    "Read-only capacity snapshot recorded; no branch or fixture created.");
        } finally {
            try {
                WebDriverFactory.quitDriver();
            } finally {
                gate.onExecutionFinish();
            }
        }
    }
}
