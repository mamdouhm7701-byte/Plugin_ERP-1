package Plugin_ERP.Bots;

import java.util.Objects;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Native text entry with bounded retries and no value-bearing diagnostics. */
final class TextEntryActions {
    private static final int MAX_ATTEMPTS = 3;
    private final WebDriver driver;
    private final WaitManagement waits;

    TextEntryActions(WebDriver driver, WaitManagement waits) {
        this.driver = driver;
        this.waits = waits;
    }

    void enter(By locator, String value, boolean normalizeWhitespace) {
        Objects.requireNonNull(value, "Text value is required");
        WebDriverException lastFailure = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                WebElement input =
                        waits.fluentWait().until(ExpectedConditions.elementToBeClickable(locator));
                boolean password = "password".equalsIgnoreCase(input.getAttribute("type"));
                String expected =
                        normalizeWhitespace && !password
                                ? value.trim().replaceAll("\\s+", " ")
                                : value;
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block:'center'});", input);
                input.click();
                input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                input.sendKeys(Keys.DELETE);
                waits.fluentWait()
                        .until(
                                d -> {
                                    String actual = input.getDomProperty("value");
                                    return actual == null || actual.isEmpty();
                                });
                input.sendKeys(expected);
                waits.fluentWait().until(d -> expected.equals(input.getDomProperty("value")));
                return;
            } catch (WebDriverException failure) {
                lastFailure = failure;
                LogUtils.debug(
                        "Text entry attempt "
                                + attempt
                                + "/"
                                + MAX_ATTEMPTS
                                + " failed ("
                                + failure.getClass().getSimpleName()
                                + ")");
            }
        }
        throw new IllegalStateException(
                "Text entry failed after " + MAX_ATTEMPTS + " attempts", lastFailure);
    }
}
