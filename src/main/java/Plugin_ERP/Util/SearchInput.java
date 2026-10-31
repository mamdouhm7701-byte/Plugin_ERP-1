/* شرح الملف: يكتب عبارة البحث ويطلب عرض نتائجها، ويعيد العثور على الحقل إذا استبدلته الشاشة أثناء الكتابة. */
package Plugin_ERP.Util;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class SearchInput {
    private SearchInput() {}

    public static void submit(WebDriver driver, By locator, String value) {
        String expected = value == null ? "" : value;
        UiPolling.configure(new WebDriverWait(driver, Duration.ofSeconds(10)))
                .ignoring(StaleElementReferenceException.class)
                .withMessage("تعذر إدخال عبارة البحث أو تأكيدها في الحقل " + locator)
                .until(
                        current -> {
                            for (WebElement input : current.findElements(locator)) {
                                if (!input.isDisplayed() || !input.isEnabled()) continue;
                                if (!expected.equals(input.getDomProperty("value"))) {
                                    input.sendKeys(
                                            Keys.chord(Keys.CONTROL, "a"),
                                            expected.isEmpty() ? Keys.DELETE : expected);
                                    return false;
                                }
                                input.sendKeys(Keys.ENTER);
                                return true;
                            }
                            return false;
                        });
    }
}
