package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Models.ReportTotals;
import java.math.BigDecimal;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

/** Document Print Checks: focused operations used by the original page facade. */
final class DocumentPrintChecks {
    private final DocumentPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;

    DocumentPrintChecks(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }

    void CompareNamesQuantitiesAndPricesWithReport(Map<String, String> expectedData) {

        for (Map.Entry<String, String> entry : expectedData.entrySet()) {

            String name = entry.getKey();

            String[] values = entry.getValue().split("\\|");

            String quantity = values[0];
            String price = values[1];

            Assert.assertTrue(
                    utilites.validation().IsTextDisplayed(name),
                    "الاسم غير موجود في التقرير: " + name);

            Assert.assertTrue(
                    utilites.validation().IsTextDisplayed(quantity),
                    "الكمية غير موجودة في التقرير للصنف " + name + ": " + quantity);

            Assert.assertTrue(
                    utilites.validation().IsTextDisplayed(price),
                    "سعر الشراء غير موجود في التقرير للصنف " + name + ": " + price);

            System.out.println(
                    "✅ تم التأكد من: " + name + " - الكمية: " + quantity + " - السعر: " + price);
        }
    }

    BigDecimal getReportTotal(String label) {
        String xpath =
                String.format("//tr[.//*[normalize-space()='%s']]//td[1]//div[last()]", label);

        return new BigDecimal(driver.findElement(By.xpath(xpath)).getText().trim());
    }

    ReportTotals getReportTotals() {

        ReportTotals totals = new ReportTotals();

        totals.setTotal(owner.getReportTotal("الاجمالي (غير شامل ضريبة القيمة المضافة)"));

        totals.setDiscount(owner.getReportTotal("اجمالي الخصومات"));

        totals.setBeforeTax(
                owner.getReportTotal("الاجمالي الخاضع للضريبة (غير شامل ضريبة القيمة المضافة)"));

        totals.setTax(owner.getReportTotal("ضريبة القيمة المضافة %15"));

        totals.setNet(owner.getReportTotal("الصافي بعد الضريبة"));

        return totals;
    }
}
