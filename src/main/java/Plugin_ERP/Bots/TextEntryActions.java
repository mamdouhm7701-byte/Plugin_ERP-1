/* شرح الملف: كتابة النص في الحقل والتأكد من ظهور نفس النص.
 */
package Plugin_ERP.Bots;

import java.util.Objects;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
                if (!password && Plugin_ERP.Util.Allur.ScenarioAssertions.isRecording()) {
                    String field = input.getDomAttribute("name");
                    if (!Plugin_ERP.Util.Allur.ReadableChecks.businessField(field))
                        field = input.getDomAttribute("id");
                    if (!password && Plugin_ERP.Util.Allur.ReadableChecks.businessField(field))
                        Plugin_ERP.Util.Allur.ScenarioAssertions.recordComparison(
                                input.getDomProperty("value"),
                                expected,
                                "التحقق من " + Plugin_ERP.Util.Allur.ReadableChecks.field(field),
                                null);
                }
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
