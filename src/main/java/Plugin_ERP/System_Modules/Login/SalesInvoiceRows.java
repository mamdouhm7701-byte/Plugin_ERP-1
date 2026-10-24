package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Models.InvoiceRow;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Visible sales invoice rows mapped to calculation models. */
final class SalesInvoiceRows {
    private final WebDriver driver;
    SalesInvoiceRows(WebDriver driver) {
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
                                        "//th[@role='columnheader'][.//div[normalize-space()='السعر']]"))
                        .getAttribute("aria-colindex");

        List<WebElement> rows =
                driver.findElements(By.xpath("//tbody//tr[@role='row']")).stream()
                        .filter(WebElement::isDisplayed)
                        .collect(Collectors.toList());

        for (WebElement row : rows) {

            List<WebElement> nameCells =
                    row.findElements(By.xpath(".//td[@aria-colindex='" + nameColIndex + "']"));

            if (nameCells.isEmpty()) {
                continue;
            }

            List<WebElement> quantityInputs =
                    row.findElements(
                            By.xpath(
                                    ".//td[@aria-colindex='"
                                            + quantityColIndex
                                            + "']//input[not(@type='hidden')]"));
            List<WebElement> priceInputs =
                    row.findElements(
                            By.xpath(
                                    ".//td[@aria-colindex='"
                                            + priceColIndex
                                            + "']//input[not(@type='hidden')]"));
            if (quantityInputs.isEmpty() || priceInputs.isEmpty()) {
                continue;
            }

            String name = nameCells.get(0).getText().trim();

            BigDecimal quantity =
                    new BigDecimal(quantityInputs.get(0).getAttribute("value").trim());

            BigDecimal price = new BigDecimal(priceInputs.get(0).getAttribute("value").trim());

            invoiceRows.add(new InvoiceRow(name, quantity, price));
        }

        return invoiceRows;
    }
}
