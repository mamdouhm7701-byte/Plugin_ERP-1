package Plugin_ERP.System_Modules.Login;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Purchase Invoice Snapshot: focused operations used by the original page facade. */
final class PurchaseInvoiceSnapshot {
    private final DocumentPage owner;
    private final WebDriver driver;

    PurchaseInvoiceSnapshot(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    Map<String, String> GetNamesQuantitiesAndPrices() {

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
                                        "//th[@role='columnheader'][.//div[normalize-space()='سعر الشراء']]"))
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
