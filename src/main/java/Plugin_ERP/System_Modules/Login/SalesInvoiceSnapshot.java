package Plugin_ERP.System_Modules.Login;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Sales row snapshots used before saving or printing. */
final class SalesInvoiceSnapshot {
    private final WebDriver driver;
    SalesInvoiceSnapshot(WebDriver driver) {
        this.driver = driver;
    }

    Map<String, String> getNamesQuantitiesAndPrices() {

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

            if (nameCells.isEmpty()) {
                continue;
            }

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

    Map<String, String> GetNamesQuantitiesAndPricesForInv() {

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
