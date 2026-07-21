package Plugin_ERP.Bots;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ValidationActions extends BaseAction{

    public ValidationActions(
            WebDriver driver,
            WebDriverWait wait,
            WaitMangment waitMangment
    ) {
        super(driver, wait, waitMangment);
    }

    private String getText(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return element.getText();
    }


    public String normalizePrice(String value) {
        return value
                .replaceAll("[\\u200E\\u200F\\u202A-\\u202E]", "") // إزالة RTL/LTR marks
                .replace(",", "")
                .replace(".000000", "")
                .replace(".00000", "")
                .replace(".0000", "")
                .replace(".000", "")
                .replace(".00", "")
                .replace(".0", "")
                .replaceAll("\\s+", "")
                .trim();
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

                return parsedDate.format(
                        DateTimeFormatter.ofPattern("yyyyMMdd")
                );

            } catch (Exception ignored) {
            }
        }

        return date;
    }

    private double normalizeNumericValue(String value) {

        return Double.parseDouble(
                value.replace(",", "")
                        .trim()
        );
    }



    public void assertRequiredMsg(By locator, String expectedMsg) {
        String actualMsg = getText(locator);
        if (!actualMsg.equals(expectedMsg)) {
            throw new AssertionError("❌ Expected: '" + expectedMsg + "' but got: '" + actualMsg + "'");
        }
    }


    public void assertValue(String expected, By locator) {

        WebElement element = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        String actual;

        // لو العنصر input أو textarea اقرأ value
        String tagName = element.getTagName();

        if (tagName.equalsIgnoreCase("input") ||
                tagName.equalsIgnoreCase("textarea")) {

            actual = element.getAttribute("value").trim();

        } else {

            // باقي العناصر عادي getText
            actual = element.getText().trim();
        }

        // تنظيف القيم
        String cleanActual = actual.replace(",", "").replaceAll("\\s+", "").trim();
        String cleanExpected = expected.replace(",", "").replaceAll("\\s+", "").trim();

        try {

            double actualNum = Double.parseDouble(cleanActual);
            double expectedNum = Double.parseDouble(cleanExpected);

            if (Double.compare(actualNum, expectedNum) != 0) {

                throw new AssertionError(
                        "❌ Expected: '" + expected + "' but got: '" + actual + "'"
                );
            }

        } catch (NumberFormatException e) {

            if (!cleanActual.equals(cleanExpected)) {

                throw new AssertionError(
                        "❌ Expected: '" + expected + "' but got: '" + actual + "'"
                );
            }
        }

        System.out.println(
                "✅ ASSERT PASSED: Expected='" + expected + "' | Actual='" + actual + "'"
        );
    }


    public boolean IsTextDisplayed(String text) {

        try {

            List<WebElement> elements = driver.findElements(
                    By.xpath("//*[contains(normalize-space(),'" + text + "')]")
            );


            return elements.stream().anyMatch(WebElement::isDisplayed);

        } catch (Exception e) {

            System.out.println("Search Failed For = [" + text + "]");
            return false;
        }
    }

    public boolean IsPriceDisplayed(String expectedPrice) {

        try {

            String normalizedExpected = normalizePrice(expectedPrice);

            List<WebElement> elements = driver.findElements(
                    By.xpath("//*[normalize-space()]")
            );

            for (WebElement element : elements) {

                String actualText = element.getText().trim();

                if (actualText.isEmpty()) {
                    continue;
                }

                String normalizedActual = normalizePrice(actualText);

                if (normalizedActual.contains(normalizedExpected)
                        && element.isDisplayed()) {

                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }


    public boolean IsPriceDisplayedForFin(String expectedPrice) {

        String normalizedExpected = normalizePrice(expectedPrice);

        List<WebElement> elements =
                driver.findElements(By.xpath("//*[normalize-space()]"));

        for (WebElement element : elements) {

            if (!element.isDisplayed()) {
                continue;
            }

            String actualText = element.getText().trim();

            if (actualText.isEmpty()) {
                continue;
            }

            String normalizedActual = normalizePrice(actualText);

            if (normalizedActual.equals(normalizedExpected)) {

                System.out.println("PRICE FOUND = " + actualText);
                return true;
            }
        }

        System.out.println("PRICE NOT FOUND = " + expectedPrice);
        return false;
    }

    public boolean IsDateDisplayed(String expectedDate) {

        try {

            String normalizedExpected = normalizeDate(expectedDate);

            List<WebElement> elements =
                    driver.findElements(By.xpath("//*[normalize-space()]"));

            for (WebElement element : elements) {

                String actualText = element.getText().trim();

                if (actualText.isEmpty()) {
                    continue;
                }

                String normalizedActual = normalizeDate(actualText);

                if (normalizedActual.equals(normalizedExpected)
                        && element.isDisplayed()) {

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

        double expectedValue = normalizeNumericValue(expectedPrice);

        List<WebElement> elements =
                driver.findElements(By.xpath("//*[normalize-space()]"));

        for (WebElement element : elements) {

            if (!element.isDisplayed()) {
                continue;
            }

            String actualText = element.getText().trim();

            if (actualText.isEmpty()) {
                continue;
            }

            try {

                double actualValue =
                        normalizeNumericValue(actualText);

                if (Math.abs(actualValue - expectedValue) <= 0.01) {

                    System.out.println("PRICE FOUND = " + actualText);
                    return true;
                }

            } catch (Exception ignored) {
            }
        }

        System.out.println("PRICE NOT FOUND = " + expectedPrice);
        return false;
    }


}
