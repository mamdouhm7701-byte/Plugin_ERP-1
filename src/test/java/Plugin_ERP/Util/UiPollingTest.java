/* شرح الملف: يثبت أن الفحص الأسرع ينتظر إتاحة الحقل فعلًا ولا يكرر الكتابة، ويرفض إعداد فاصل غير مناسب. */
package Plugin_ERP.Util;

import java.lang.reflect.Proxy;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class UiPollingTest {
    @Test
    public void theActionWaitsForTheFieldWithoutWritingTwice() {
        var sleeps = new ArrayList<Duration>();
        var polls = new AtomicInteger();
        var clicks = new AtomicInteger();
        WebElement field =
                (WebElement)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebElement.class},
                                (proxy, method, args) ->
                                        switch (method.getName()) {
                                            case "isDisplayed", "isEnabled" -> true;
                                            case "click" -> {
                                                clicks.incrementAndGet();
                                                yield null;
                                            }
                                            default -> throw new AssertionError(method.getName());
                                        });
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class},
                                (proxy, method, args) -> {
                                    if (method.getName().equals("findElements"))
                                        return polls.incrementAndGet() == 1
                                                ? List.of()
                                                : List.of(field);
                                    throw new AssertionError(method.getName());
                                });
        var wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(35),
                        Duration.ofMillis(500),
                        Clock.fixed(Instant.EPOCH, ZoneOffset.UTC),
                        sleeps::add);
        new FormInteractions(wait, element -> {}).click(By.id("owned-field"));
        Assert.assertEquals(clicks.get(), 1);
        Assert.assertEquals(
                sleeps,
                List.of(UiPolling.interval(System.getProperty("ui.wait.poll.millis", "200"))));
    }

    @Test
    public void invalidPollingDoesNotStartABusyLoopOrAnUnboundedPause() {
        for (String value : List.of("0", "-1", "99", "1001", "bad"))
            Assert.expectThrows(IllegalArgumentException.class, () -> UiPolling.interval(value));
        Assert.assertEquals(UiPolling.interval("500"), Duration.ofMillis(500));
    }
}
