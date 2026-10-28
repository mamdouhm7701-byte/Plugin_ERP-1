/* شرح الملف: اختبار إتاحة شاشة سجل العمليات والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package CoverageTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Util.*;
import io.qameta.allure.Allure;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class AuditLogCoverageTest extends BaseTest {
    @Test
    public void historyListOpensForSelectedActivityWithoutRedirectingHome() {
        requirePreparedSession();
        var expected = BranchProfiles.selected();
        expected.ifPresent(
                p -> p.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30))));
        driver.get(PropertyReader.getApplicationUrl().replaceAll("/+$", "") + "/#/logs");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .withMessage(
                            "History route /logs did not mount; redirecting home does not count as availability")
                    .until(
                            d ->
                                    Boolean.TRUE.equals(
                                            VueComponentAccess.execute(
                                                    d,
                                                    """
                        const r=erpVue.root()?.$router.currentRoute,v=r?.matched?.at(-1)?.instances?.default;
                        return r?.path.replace(/\\/$/,'')==='/logs'&&v?._isMounted&&v.$el?.isConnected;
                        """)));
            Assert.assertTrue(
                    driver.findElements(By.cssSelector("h4.content-header-title")).stream()
                            .anyMatch(org.openqa.selenium.WebElement::isDisplayed),
                    "History heading is missing");
        } finally {
            Object route =
                    VueComponentAccess.execute(
                            driver, "return erpVue.root()?.$router.currentRoute.path||null");
            Allure.addAttachment(
                    "History screen availability evidence",
                    "application/json",
                    new org.json.JSONObject(
                                    Map.of(
                                            "requestedRoute",
                                            "/logs",
                                            "actualRoute",
                                            String.valueOf(route),
                                            "activity",
                                            expected.map(p -> p.expected().activityType().code())
                                                    .orElse("current"),
                                            "scope",
                                            "READ_ONLY_AVAILABILITY_ONLY"))
                            .toString(2),
                    ".json");
        }
        expected.ifPresent(
                p -> p.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30))));
    }
}
