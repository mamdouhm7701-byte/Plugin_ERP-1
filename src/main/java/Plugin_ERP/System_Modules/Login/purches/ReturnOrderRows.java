package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.System_Modules.Login.AbstractForPurchases;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Return Order Rows: focused operations used by the original page facade. */
final class ReturnOrderRows {
    private final ReturnOrderPage owner;
    private final WebDriver driver;
    ReturnOrderRows(ReturnOrderPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    List<InvoiceRow> getInvoiceRowsForReturnOrder() {

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

    Map<String, String> GetNamesQuantitiesAndPricesForReturnOrder() {

        Map<String, String> data = new LinkedHashMap<>();

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

        List<WebElement> rows = driver.findElements(By.xpath("//tbody//tr[@role='row']"));

        for (WebElement row : rows) {

            List<WebElement> nameCells =
                    row.findElements(By.xpath(".//td[@aria-colindex='" + nameColIndex + "']"));

            if (nameCells.isEmpty()) continue;

            String name = nameCells.get(0).getText().trim();

            String quantity =
                    row.findElement(
                                    By.xpath(
                                            ".//td[@aria-colindex='"
                                                    + quantityColIndex
                                                    + "']//input[not(@type='hidden')]"))
                            .getAttribute("value")
                            .trim();

            String price =
                    row.findElement(
                                    By.xpath(
                                            ".//td[@aria-colindex='"
                                                    + priceColIndex
                                                    + "']//input[not(@type='hidden')]"))
                            .getAttribute("value")
                            .trim();

            System.out.printf("Item=%s , Quantity=%s , Price=%s%n", name, quantity, price);

            data.put(name, quantity + "|" + price);
        }

        return data;
    }
}
