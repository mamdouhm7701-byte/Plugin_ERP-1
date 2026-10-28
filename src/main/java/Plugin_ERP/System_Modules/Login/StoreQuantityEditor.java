/* شرح الملف: مساعدة لتعديل كميات الأصناف وقراءة الكميات المسجلة. فائدتها ضبط كمية كل صنف في حركة
 * المخزون.
 */
package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

final class StoreQuantityEditor {
    private final DocumentPage owner;
    private final WebDriver driver;

    StoreQuantityEditor(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    void SetQuantityByRow(int rowNumber, int quantity) {
        WebElement quantityInput = owner.utilites.waits().waitForQuantityInputByRow(rowNumber);

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", quantityInput);

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                                quantityInput));

        quantityInput.click();
        quantityInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        quantityInput.sendKeys(Keys.DELETE);
        quantityInput.sendKeys(String.valueOf(quantity));
        ((JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                                + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                        quantityInput);

        // اخرج من الحقل عشان تتفعل أي معالجة تلقائية للرقم (blur)
        quantityInput.sendKeys(Keys.TAB);

        // استنى وتأكد إن القيمة فعلاً اتحدثت في الـ DOM (بغض النظر عن شكل الفورمات)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(
                d -> {
                    try {
                        WebElement input =
                                owner.utilites.waits().waitForQuantityInputByRow(rowNumber);
                        String actualValue = input.getAttribute("value");
                        return actualValue != null
                                && owner.normalizeQuantity(actualValue)
                                        .equals(String.valueOf(quantity));
                    } catch (Exception e) {
                        Plugin_ERP.Bots.LogUtils.debug(
                                "StoreQuantityEditor condition unavailable: "
                                        + e.getClass().getSimpleName());
                        return false;
                    }
                });

        LogUtils.info("الكمية فى صف " + rowNumber + " التغير الى " + quantity);
    }

    String normalizeQuantity(String value) {
        if (value == null) return "";
        String v = value.trim();
        if (v.contains(".")) {
            String[] parts = v.split("\\.");
            if (parts[1].matches("0+")) {
                return parts[0];
            }
        }
        return v;
    }

    void SetQuantitiesForRows(int... values) {
        owner.utilites.waits().waitForOverlayToDisappear();
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("Values must be pairs: row number, quantity");
        }

        for (int i = 0; i < values.length; i += 2) {
            int rowNumber = values[i];
            int quantity = values[i + 1];
            owner.SetQuantityByRow(rowNumber, quantity);
        }
    }

    Map<String, String> GetNamesAndQuantities() {
        Map<String, String> data = new LinkedHashMap<>();

        String quantityColIndex =
                driver.findElement(
                                By.xpath(
                                        "//th[@role='columnheader'][.//div[normalize-space()='الكمية']]"))
                        .getAttribute("aria-colindex");

        List<WebElement> rows = driver.findElements(By.xpath("//tbody//tr[@role='row']"));

        for (WebElement row : rows) {
            String name = row.findElement(By.xpath(".//td[@aria-colindex='3']")).getText().trim();

            String quantity =
                    row.findElement(
                                    By.xpath(
                                            ".//td[@aria-colindex='"
                                                    + quantityColIndex
                                                    + "']//input[not(@type='hidden')]"))
                            .getAttribute("value")
                            .trim();

            System.out.printf("Item=%s , Quantity=%s%n", name, quantity);

            data.put(name, quantity);
        }

        return data;
    }
}
