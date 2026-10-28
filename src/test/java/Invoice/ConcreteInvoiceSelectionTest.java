/* شرح الملف: مراجعة انتظار انتهاء التحميل قبل تأكيد اختيار إذن التسليم.
 */
package Invoice;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ConcreteInvoiceSelectionTest {
    @Test
    public void blockedModalConfirmationRetriesUntilNativeClickCanSucceed() {
        var attempts = new AtomicInteger();
        var accepted = new AtomicInteger();
        WebElement button =
                (WebElement)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebElement.class},
                                (p, m, a) ->
                                        switch (m.getName()) {
                                            case "isDisplayed", "isEnabled" -> true;
                                            case "getText" -> "حفظ";
                                            case "click" -> {
                                                if (attempts.incrementAndGet() == 1)
                                                    throw new ElementClickInterceptedException(
                                                            "loading overlay");
                                                accepted.incrementAndGet();
                                                yield null;
                                            }
                                            default -> throw new AssertionError(m.getName());
                                        });
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class},
                                (p, m, a) -> {
                                    if (m.getName().equals("findElements")) return List.of(button);
                                    throw new AssertionError(m.getName());
                                });
        ConcreteInvoiceControls.confirmSelection(driver, Duration.ofSeconds(2));
        Assert.assertEquals(attempts.get(), 2);
        Assert.assertEquals(accepted.get(), 1);
    }
}
