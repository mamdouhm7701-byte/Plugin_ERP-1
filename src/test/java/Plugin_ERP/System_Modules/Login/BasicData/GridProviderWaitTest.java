package Plugin_ERP.System_Modules.Login.BasicData;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class GridProviderWaitTest {
    @Test
    public void doesNotReadRowsUntilProviderSnapshotStopsChanging() {
        AtomicInteger reads = new AtomicInteger();
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (proxy, method, args) -> {
                                    if (method.getName().equals("executeScript")) {
                                        String script = (String) args[0];
                                        if (script.contains("querySelectorAll('#mainTable")) {
                                            Assert.assertTrue(
                                                    reads.get() >= 4,
                                                    "Count must follow the completed, stable provider");
                                            return 1L;
                                        }
                                        return switch (reads.incrementAndGet()) {
                                            case 1 -> null;
                                            case 2 -> "[]";
                                            default -> "[{\"id\":\"owned\"}]";
                                        };
                                    }
                                    if (method.getName().equals("toString"))
                                        return "changing-provider";
                                    throw new AssertionError(
                                            "Grid count must use one DOM snapshot: "
                                                    + method.getName());
                                });
        Assert.assertEquals(new GridProviderWait(driver, "areas").exactMatches("owned"), 1);
    }
}
