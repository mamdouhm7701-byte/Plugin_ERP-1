package Plugin_ERP.Bots;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ReadinessWaitTest {
    @Test
    public void rowKeyWaitRetriesMissingAndReplacedRows() {
        AtomicInteger reads = new AtomicInteger();
        WebElement row = (WebElement) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{WebElement.class}, (proxy, method, args) -> {
                    if (method.getName().equals("getAttribute")) {
                        return switch (reads.incrementAndGet()) {
                            case 1 -> "";
                            case 2 -> throw new StaleElementReferenceException("provider replaced row");
                            case 3 -> "null";
                            default -> " 42 ";
                        };
                    }
                    throw new AssertionError("Readiness must not modify a row: " + method.getName());
                });
        WebDriver driver = browser((method, args) -> {
            if (method.equals("findElements")) return List.of(row);
            throw new AssertionError(method);
        });
        Assert.assertEquals(new GridActions(driver, null, null).getLastRowPk(), "42");
        Assert.assertEquals(new PriceListActions(driver, null, null).getLastRowPk(), "42");
        Assert.assertEquals(reads.get(), 5);
    }

    @Test
    public void overlayMustRemainHiddenAfterAStaleSnapshot() {
        AtomicInteger polls = new AtomicInteger();
        long[] staleAt = {0};
        WebDriver driver = browser((method, args) -> {
            if (method.equals("findElements")) {
                if (polls.incrementAndGet() == 3) {
                    staleAt[0] = System.nanoTime();
                    throw new StaleElementReferenceException("overlay changed");
                }
                return List.of();
            }
            if (method.equals("executeScript")) return "complete";
            throw new AssertionError(method);
        });
        new WaitActions(driver).waitForOverlayToDisappear();
        Assert.assertTrue(staleAt[0] > 0);
        Assert.assertTrue(System.nanoTime() - staleAt[0] >= java.time.Duration.ofSeconds(1).toNanos(),
                "A stale snapshot must restart the hidden stability interval");
    }

    private interface Call { Object apply(String method, Object[] args); }

    private WebDriver browser(Call call) {
        return (WebDriver) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{WebDriver.class, JavascriptExecutor.class},
                (proxy, method, args) -> call.apply(method.getName(), args));
    }
}
