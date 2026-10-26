package CoverageTest;

import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.Story;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Configuration presentation only: never registers a device or sends invoices to the authority. */
public final class ZatcaConfigurationCoverageTest extends BaseTest {
    @Test
    @Story("عرض حالة الربط مع الهيئة دون التسجيل أو إرسال فواتير")
    public void rendersConfigurationStateWithoutExternalRegistration() {
        var page = new ScreenPage(driver);
        page.open("posConfigurations", "الربط مع هيئة الزكاة");
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(25))
                .until(
                        d ->
                                page.bodyText().contains("تم تهيئة جهاز من قبل")
                                        || page.visible(By.cssSelector("input[type='password']")));
        if (page.bodyText().contains("تم تهيئة جهاز من قبل")) {
            Assert.assertFalse(
                    page.visible(By.cssSelector("input[type='password']")),
                    "Configured state must replace registration form");
            Assert.assertFalse(
                    page.visible(By.cssSelector("button[type='submit']")),
                    "Configured device must not offer a registration submit");
        } else
            Assert.assertTrue(
                    page.visible(By.cssSelector("input[type='password']")),
                    "Administrator password must be a password input");
        Assert.assertFalse(driver.getCurrentUrl().contains("sendInvoiceToZatca"));
    }
}
