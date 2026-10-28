/* شرح الملف: اختبار فتح تقارير الخرسانة على الفرع الصحيح والتأكد من حفظ البيانات وظهور النتيجة
 * المطلوبة.
 */
package CoverageTest;

import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Util.VueComponentAccess;
import io.qameta.allure.Allure;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class ConcreteReportRequestTest extends BaseTest {
    @DataProvider
    public Object[][] reports() {
        return new Object[][] {
            {"carsTransactionReport", "استعراض", "CarsTransactions-ar"},
            {"driversTransactionReport", "استعراض", "DriversTransactions-ar"},
            {"customersProjectsReport", "استعراض تفصيلي", "CustomersProjectsDetails-ar"},
            {"customersProjectsReport", "استعراض اجمالي", "CustomersProjectsTotal-ar"},
            {"reservationReport", "استعراض تفصيلي", "ReservationReport-ar"},
            {"reservationReport", "استعراض اجمالي", "ReservationReportTotal-ar"}
        };
    }

    @Test(dataProvider = "reports")
    public void nativePreviewUsesSelectedReportAndCurrentBranch(
            String report, String button, String expectedName) {
        var page = new ScreenPage(driver);
        page.open("concrete-reports", "تقارير الخرسانة");
        String title =
                ((String)
                                VueComponentAccess.execute(
                                        driver, "return erpVue.root().$t(arguments[0])", report))
                        .trim();
        page.click(
                By.xpath(
                        "//li[contains(@class,'nav-item-report')][.//span[normalize-space(.)="
                                + ScreenPage.literal(title)
                                + "]]"));
        page.settled();
        String branchId =
                (String)
                        VueComponentAccess.execute(
                                driver,
                                "return erpVue.root().$store.getters['app/currentBranch'].id");
        String original = driver.getWindowHandle();
        Set<String> previous = new HashSet<>(driver.getWindowHandles());
        try {
            page.click(button);
            String preview =
                    new WebDriverWait(driver, Duration.ofSeconds(30))
                            .until(
                                    d ->
                                            d.getWindowHandles().stream()
                                                    .filter(h -> !previous.contains(h))
                                                    .findFirst()
                                                    .orElse(null));
            driver.switchTo().window(preview);
            // Decode in the browser and export only an allowlist. Signed tokens never enter
            // evidence.
            Object safe =
                    new WebDriverWait(driver, Duration.ofSeconds(30))
                            .until(
                                    d ->
                                            VueComponentAccess.execute(
                                                    d,
                                                    """
                    const u=new URL(location.href),q=u.searchParams.get('q');
                    if(!q)return null;
                    const p=JSON.parse(decodeURIComponent(escape(atob(q))));
                    return {reportName:u.searchParams.get('reportName'),branchId:p.branchId,
                        dateFrom:p.dateFrom??null,dateTo:p.dateTo??null,
                        isCarsReport:p.isCarsReport??null,isDriversReport:p.isDriversReport??null};
                    """));
            var request = new org.json.JSONObject((Map<?, ?>) safe);
            Allure.addAttachment(
                    "Sanitized native concrete report request",
                    "application/json",
                    request.toString(2),
                    ".json");
            Assert.assertEquals(request.getString("reportName"), expectedName);
            Assert.assertEquals(request.getString("branchId"), branchId);
            Assert.assertFalse(
                    request.isNull("dateFrom"), "Native report requires a starting date");
            Assert.assertFalse(request.isNull("dateTo"), "Native report requires an ending date");
            if (report.equals("carsTransactionReport")) {
                Assert.assertTrue(request.getBoolean("isCarsReport"));
                Assert.assertFalse(request.getBoolean("isDriversReport"));
            } else if (report.equals("driversTransactionReport")) {
                Assert.assertFalse(request.getBoolean("isCarsReport"));
                Assert.assertTrue(request.getBoolean("isDriversReport"));
            }
        } finally {
            for (String window : new HashSet<>(driver.getWindowHandles())) {
                if (!previous.contains(window)) {
                    driver.switchTo().window(window);
                    driver.close();
                }
            }
            driver.switchTo().window(original);
        }
    }
}
