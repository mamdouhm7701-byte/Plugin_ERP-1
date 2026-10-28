/* شرح الملف: مساعدة لقراءة إجماليات فاتورة البيع المطبوعة. فائدتها مقارنة المبلغ المطبوع بقيمة
 * الفاتورة المسجلة.
 */
package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Models.ReportTotals;
import java.math.BigDecimal;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class SalesPrintedTotals {
    private final AbstractForSales owner;
    private final WebDriver driver;

    SalesPrintedTotals(AbstractForSales owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    ReportTotals getReportTotals() {

        ReportTotals totals = new ReportTotals();

        totals.setTotal(getInvoiceReportTotal("الاجمالي (غير شامل ضريبة القيمة المضافة)"));

        totals.setDiscount(getInvoiceReportTotal("اجمالي الخصومات"));

        totals.setBeforeTax(getInvoiceReportTotal("الاجمالي الخاضع للضريبة (غير شامل الضريبة )"));

        totals.setTax(getInvoiceReportTotal("ضريبة القيمة المضافة %15"));

        totals.setNet(getInvoiceReportTotal("الصافي بعد الضريبة"));

        return totals;
    }

    BigDecimal getInvoiceReportTotal(String label) {
        String xpath =
                String.format("//tr[.//*[normalize-space()='%s']]//td[1]//div[last()]", label);
        String displayedValue = driver.findElement(By.xpath(xpath)).getText().trim();
        String normalizedValue =
                displayedValue
                        .replace(",", "")
                        .replace("٬", "")
                        .replace("٫", ".")
                        .replaceAll("\\s+", "");
        return new BigDecimal(normalizedValue);
    }
}
