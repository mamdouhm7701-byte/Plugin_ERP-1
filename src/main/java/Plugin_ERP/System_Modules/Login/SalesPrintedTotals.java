package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Sales Printed Totals: focused operations used by the original page facade. */
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
