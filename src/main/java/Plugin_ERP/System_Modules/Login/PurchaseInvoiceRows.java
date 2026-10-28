/* شرح الملف: مساعدة لقراءة بنود فاتورة الشراء. فائدتها مراجعة بيانات الأصناف والمبالغ المسجلة بكل
 * بند.
 */
package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Models.InvoiceRow;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

final class PurchaseInvoiceRows {
    private final WebDriver driver;

    PurchaseInvoiceRows(WebDriver driver) {
        this.driver = driver;
    }

    List<InvoiceRow> getInvoiceRows() {

        List<InvoiceRow> invoiceRows = new ArrayList<>();

        String nameColIndex =
                driver.findElement(
                                By.xpath(
                                        "//th[@role='columnheader'][.//div[normalize-space()='الاسم']]"))
                        .getAttribute("aria-colindex");

        String quantityColIndex =
                driver.findElement(
                                By.xpath(
                                        "//th[@role='columnheader'][.//div[normalize-space()='الكمية']]"))
                        .getAttribute("aria-colindex");

        String priceColIndex =
                driver.findElement(
                                By.xpath(
                                        "//th[@role='columnheader'][.//div[normalize-space()='سعر الشراء']]"))
                        .getAttribute("aria-colindex");

        List<WebElement> rows = driver.findElements(By.xpath("//tbody//tr[@role='row']"));

        for (WebElement row : rows) {

            List<WebElement> nameCells =
                    row.findElements(By.xpath(".//td[@aria-colindex='" + nameColIndex + "']"));

            if (nameCells.isEmpty()) continue;

            String name = nameCells.get(0).getText().trim();

            BigDecimal quantity =
                    new BigDecimal(
                            row.findElement(
                                            By.xpath(
                                                    ".//td[@aria-colindex='"
                                                            + quantityColIndex
                                                            + "']//input[not(@type='hidden')]"))
                                    .getAttribute("value")
                                    .trim());

            BigDecimal price =
                    new BigDecimal(
                            row.findElement(
                                            By.xpath(
                                                    ".//td[@aria-colindex='"
                                                            + priceColIndex
                                                            + "']//input[not(@type='hidden')]"))
                                    .getAttribute("value")
                                    .trim());

            invoiceRows.add(new InvoiceRow(name, quantity, price));
        }

        return invoiceRows;
    }
}
