/* شرح الملف: اختبارات إعدادات الربط مع الهيئة: عرض حالة الربط دون تسجيل خارجي.
 */
package CoverageTest;

import Plugin_ERP.System_Modules.Login.Coverage.ZatcaConfigurationPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.Story;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ZatcaConfigurationCoverageTest extends BaseTest {
    @Test
    @Story("عرض حالة الربط مع الهيئة دون التسجيل أو إرسال فواتير")
    public void rendersConfigurationStateWithoutExternalRegistration() {
        var page = new ZatcaConfigurationPage(driver);
        page.open("posConfigurations", "الربط مع هيئة الزكاة");
        var state = page.awaitState();
        if (state == ZatcaConfigurationPage.State.CONFIGURED) {
            Assert.assertFalse(
                    page.visible(By.cssSelector("input[type='password']")),
                    "Configured state must replace registration form");
            Assert.assertFalse(
                    page.visible(By.cssSelector("button[type='submit']")),
                    "Configured device must not offer a registration submit");
        } else if (state == ZatcaConfigurationPage.State.INCOMPLETE) {
            Assert.assertTrue(
                    page.incompleteControlsVisible(),
                    "يجب ظهور رمز التحقق وزر التسجيل مع رسالة الإعدادات الناقصة");
        } else
            Assert.assertTrue(
                    page.visible(By.cssSelector("input[type='password']")),
                    "Administrator password must be a password input");
        Assert.assertFalse(driver.getCurrentUrl().contains("sendInvoiceToZatca"));
    }
}
