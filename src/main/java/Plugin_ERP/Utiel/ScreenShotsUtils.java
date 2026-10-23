package Plugin_ERP.Utiel;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Utiel.Allur.AllureUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class ScreenShotsUtils {

    public static void takeScreenShot(WebDriver driver, String name) {
        Plugin_ERP.Utiel.Allur.ScenarioAccess.capture(driver, "الشاشة المصورة: " + name);
        try {
            // خد Screenshot من الـ driver
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // جهّز المسار
            File dest = new File("screenshots/" + name + "-" + TimeManage.getTimestamp() + ".png");

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
