/* شرح الملف: مقارنة القيم والرسائل الظاهرة بالنتيجة المطلوبة.
 */
package Plugin_ERP.Bots;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ValidationActions extends BaseAction {

    public ValidationActions(WebDriver driver, WebDriverWait wait, WaitManagement waitManagement) {
        super(driver, wait, waitManagement);
    }

    private String getText(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return element.getText();
    }

    public String normalizePrice(String value) {
        String clean =
                value.replaceAll("[\\u200E\\u200F\\u202A-\\u202E\\u2066-\\u2069]", "")
                        .replace(",", "")
                        .replace("\u066c", "")
                        .replace('\u066b', '.')
                        .replaceAll("[\\s\\u00a0\\u202f]+", "");
        StringBuilder digits = new StringBuilder();
        for (char ch : clean.toCharArray()) {
            int digit = Character.digit(ch, 10);
            digits.append(digit >= 0 ? (char) ('0' + digit) : ch);
        }
        return new java.math.BigDecimal(digits.toString()).stripTrailingZeros().toPlainString();
    }

    private String normalizeDate(String date) {

        date = date.trim();

        DateTimeFormatter[] formats = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
        };

        for (DateTimeFormatter format : formats) {
            try {
                LocalDate parsedDate = LocalDate.parse(date, format);

                return parsedDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            } catch (Exception ignored) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "ValidationActions fallback: " + ignored.getClass().getSimpleName());
            }
        }

        return date;
    }

    private double normalizeNumericValue(String value) {

        return Double.parseDouble(value.replace(",", "").trim());
    }

    public void assertRequiredMsg(By locator, String expectedMsg) {
        String actualMsg = getText(locator);
        if (!actualMsg.equals(expectedMsg)) {
            throw new AssertionError(
                    "❌ Expected: '" + expectedMsg + "' but got: '" + actualMsg + "'");
        }
    }

    public void assertValue(String expected, By locator) {
        AssertionError failure = null;
        try {
            compareValue(expected, locator);
        } catch (AssertionError mismatch) {
            failure = mismatch;
            throw mismatch;
        } finally {
            try {
                WebElement field = driver.findElement(locator);
                String key = field.getDomAttribute("name");
                if (!Plugin_ERP.Util.Allur.ReadableChecks.businessField(key))
                    key = field.getDomAttribute("id");
                if (!"password".equalsIgnoreCase(field.getDomAttribute("type"))
                        && Plugin_ERP.Util.Allur.ReadableChecks.businessField(key)) {
                    String actual =
                            List.of("input", "textarea").contains(field.getTagName())
                                    ? field.getDomProperty("value")
                                    : field.getText();
                    Plugin_ERP.Util.Allur.ScenarioAssertions.recordComparison(
                            actual,
                            expected,
                            "التحقق من " + Plugin_ERP.Util.Allur.ReadableChecks.field(key),
                            failure);
                }
            } catch (RuntimeException reportingFailure) {
                LogUtils.debug(
                        "Field comparison reporting unavailable: "
                                + reportingFailure.getClass().getSimpleName());
            }
        }
    }

    private void compareValue(String expected, By locator) {

        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

        String actual;

        // لو العنصر input أو textarea اقرأ value
        String tagName = element.getTagName();

        if (tagName.equalsIgnoreCase("input") || tagName.equalsIgnoreCase("textarea")) {

            actual = element.getAttribute("value").trim();

        } else {

            // باقي العناصر عادي getText
            actual = element.getText().trim();
        }

        // تنظيف القيم
        String cleanActual = actual.replace(",", "").replaceAll("\\s+", "").trim();
        String cleanExpected = expected.replace(",", "").replaceAll("\\s+", "").trim();

        try {

            java.math.BigDecimal actualNum = new java.math.BigDecimal(normalizePrice(cleanActual));
            java.math.BigDecimal expectedNum =
                    new java.math.BigDecimal(normalizePrice(cleanExpected));

            if (actualNum.compareTo(expectedNum) != 0) {

                throw new AssertionError(
                        "❌ Expected: '" + expected + "' but got: '" + actual + "'");
            }

        } catch (NumberFormatException e) {

            if (!cleanActual.equals(cleanExpected)) {

                throw new AssertionError(
                        "❌ Expected: '" + expected + "' but got: '" + actual + "'");
            }
        }

        System.out.println(
                "✅ ASSERT PASSED: Expected='" + expected + "' | Actual='" + actual + "'");
    }

    public boolean IsTextDisplayed(String text) {

        try {

            List<WebElement> elements =
                    driver.findElements(
                            By.xpath("//*[contains(normalize-space(),'" + text + "')]"));

            return elements.stream()
                    .anyMatch(element -> element.isDisplayed() && element.getText().contains(text));

        } catch (Exception e) {

            System.out.println("Search Failed For = [" + text + "]");
            return false;
        }
    }

    public boolean IsPriceDisplayed(String expectedPrice) {
        return IsPriceDisplayedForFin(expectedPrice);
    }

    public boolean IsPriceDisplayedForFin(String expectedPrice) {

        String normalizedExpected = normalizePrice(expectedPrice);

        List<WebElement> elements = driver.findElements(By.xpath("//*[normalize-space()]"));

        for (WebElement element : elements) {

            if (!element.isDisplayed()) {
                continue;
            }

            String actualText = element.getText().trim();

            if (actualText.isEmpty()) {
                continue;
            }

            try {
                if (normalizePrice(actualText).equals(normalizedExpected)) {

                    System.out.println("PRICE FOUND = " + actualText);
                    return true;
                }
            } catch (NumberFormatException nonNumericLabel) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "ValidationActions fallback: "
                                + nonNumericLabel.getClass().getSimpleName());

                // A report also contains headings; never match a number inside their text.
            }
        }

        System.out.println("PRICE NOT FOUND = " + expectedPrice);
        return false;
    }

    public boolean IsDateDisplayed(String expectedDate) {

        try {

            String normalizedExpected = normalizeDate(expectedDate);

            List<WebElement> elements = driver.findElements(By.xpath("//*[normalize-space()]"));

            for (WebElement element : elements) {

                String actualText = element.getText().trim();

                if (actualText.isEmpty()) {
                    continue;
                }

                String normalizedActual = normalizeDate(actualText);

                if (normalizedActual.equals(normalizedExpected) && element.isDisplayed()) {

                    System.out.println("DATE FOUND = " + actualText);
                    return true;
                }
            }

            System.out.println("DATE NOT FOUND = " + expectedDate);
            return false;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    public boolean IsNumericPriceDisplayed(String expectedPrice) {
        return IsPriceDisplayedForFin(expectedPrice);
    }
}
