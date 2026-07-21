package Plugin_ERP.Bots;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class
UtilityActions extends BaseAction {

    public UtilityActions(
            WebDriver driver,
            WebDriverWait wait,
            WaitMangment waitMangment
    ) {
        super(driver, wait, waitMangment);
    }

public Void printLocatorCount(By locator) {
    List<WebElement> elements = driver.findElements(locator);

    for (int i = 0; i < elements.size(); i++) {
        System.out.println(
                i +
                        " displayed=" + elements.get(i).isDisplayed() +
                        " enabled=" + elements.get(i).isEnabled());
    }
    System.out.println(
            driver.findElements(
                    locator
            ).size()
    );
    //لطباعه عدد الانبوتات اللي موجوده في صفحة وحدات الصنف
    List<WebElement> inputs = driver.findElements(locator
    );

    System.out.println(inputs.size());
    System.out.println(
            driver.findElements(
                    locator
            ).size()
    );
    WebElement input = driver.findElement(
            locator
    );
    System.out.println(input.isDisplayed());
    System.out.println(input.isEnabled());
    WebElement e = driver.findElement(locator);

    System.out.println("Displayed = " + e.isDisplayed());
    System.out.println("Enabled   = " + e.isEnabled());
    System.out.println("Readonly  = " + e.getAttribute("readonly"));
    System.out.println("Value     = " + e.getAttribute("value"));
    return null;
}

    public String TypeTodayDate(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }


    public String TypePastYearDate(String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }

    public String generateRandomNumber() {
        StringBuilder number = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }

    public double getNumber(By locator) {

        String value = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(driver -> {

                    String text = driver.findElement(locator)
                            .getAttribute("value");

                    return text != null && !text.trim().isEmpty()
                            ? text
                            : null;
                });

        return Double.parseDouble(
                value.replace(",", "").trim()
        );
    }

    public BigDecimal getNormalizedAmount(By locator) {

        String value = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(driver -> {

                    String text = driver.findElement(locator)
                            .getAttribute("value");

                    return text != null && !text.trim().isEmpty()
                            ? text
                            : null;
                });

        value = value.replace(",", "").trim();

        BigDecimal amount = new BigDecimal(value);

        Allure.step("Amount = " + amount);

        return amount;
    }

    public void getInputValue(By locator, String expectedValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // انتظر حتى تتحمل القيمة (مش فارغة بس)
        wait.until(d -> {
            try {
                WebElement el = d.findElement(locator);
                String val = el.getDomProperty("value");
                return val != null && !val.trim().isEmpty();
            } catch (Exception e) {
                return false;
            }
        });

        // تحقق من القيمة
        WebElement element = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", element
        );

        String actualValue = normalizeNumber(element.getDomProperty("value"));
        expectedValue = normalizeNumber(expectedValue);

        LogUtils.info("ACTUAL VALUE = " + actualValue);
        LogUtils.info("EXPECTED VALUE = " + expectedValue);

        Assert.assertEquals(actualValue, expectedValue);
    }


    private String normalizeNumber(String value) {

        if (value == null) {
            return "";
        }

        value = value.trim();

        // يشيل الفواصل
        value = value.replace(",", "");

        // لو رقم عشري بصفر فقط يحوله صحيح
        if (value.matches("\\d+\\.0+")) {
            value = value.split("\\.")[0];
        }

        return value;
    }





}