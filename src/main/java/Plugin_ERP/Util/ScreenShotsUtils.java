/* شرح الملف: حفظ صورة للشاشة لتوضيح ما حدث أثناء الاختبار.
 */
package Plugin_ERP.Util;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Util.Allur.AllureUtils;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenShotsUtils {

    public static void takeScreenShot(WebDriver driver, String name) {
        Plugin_ERP.Util.Allur.ScenarioAccess.capture(driver, "الشاشة المصورة: " + name);
        try {
            // خد Screenshot من الـ driver
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // جهّز المسار
            File dest =
                    new File(
                            "screenshots/"
                                    + name
                                    + "-"
                                    + TimeManage.getTimestamp()
                                    + "-"
                                    + java.util.UUID.randomUUID()
                                    + ".png");

            // اعمل فولدر screenshots لو مش موجود
            dest.getParentFile().mkdirs();

            // انسخ الصورة
            FileUtils.copyFile(src, dest);

            // Attach للصورة في Allure
            AllureUtils.attachScreenshotToAllure(name, dest.getAbsolutePath());

        } catch (Exception e) {
            LogUtils.info("Failed to take screenshot: " + e.getMessage());
        }
    }
}
