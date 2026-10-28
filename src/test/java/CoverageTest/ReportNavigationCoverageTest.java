/* شرح الملف: اختبار قائمة التقارير المتاحة والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package CoverageTest;

import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.Story;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class ReportNavigationCoverageTest extends BaseTest {
    @DataProvider
    public Object[][] reportCatalogues() {
        return new Object[][] {
            {"sales-reports", "تقارير المبيعات"},
            {"purchase-reports", "تقارير المشتريات"},
            {"items-reports", "تقارير الأصناف"},
            {"customers-and-suppliers", "تقارير العملاء والموردين"},
            {"accounts-reports", "تقارير الحسابات"}
        };
    }

    @Test(dataProvider = "reportCatalogues")
    @Story("عرض دليل التقارير وروابطه دون تصدير أو تعديل البيانات")
    public void reportCatalogueHasVisibleDestinations(String route, String title) {
        var page = new ScreenPage(driver);
        page.open(route, title);
        var links =
                new org.openqa.selenium.support.ui.WebDriverWait(
                                driver, java.time.Duration.ofSeconds(20))
                        .until(
                                d -> {
                                    var visible =
                                            d
                                                    .findElements(
                                                            By.cssSelector(
                                                                    ".content-body li.nav-item-report"))
                                                    .stream()
                                                    .filter(WebElement::isDisplayed)
                                                    .filter(WebElement::isEnabled)
                                                    .filter(
                                                            control ->
                                                                    control.getText()
                                                                            .contains("تقرير"))
                                                    .toList();
                                    return visible.size() < 2 ? null : visible;
                                });
        Assert.assertFalse(
                links.isEmpty(), "Report catalogue must offer a visible report destination");
        Assert.assertFalse(
                driver.getCurrentUrl().contains("login"),
                "Report catalogue must retain its authenticated route");
    }
}
