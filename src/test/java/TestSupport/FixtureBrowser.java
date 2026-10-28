/* شرح الملف: توفير المتصفح المستخدم لتجهيز بيانات الاختبار.
 */
package TestSupport;

import java.lang.reflect.*;
import java.util.*;
import org.openqa.selenium.*;

public final class FixtureBrowser implements AutoCloseable {
    public final WebDriver driver;
    private final Field field;
    private final Map<Object, Object> previous = new IdentityHashMap<>();
    private String active = "main";
    private final Set<String> windows = new LinkedHashSet<>(List.of("main"));
    public int closed;

    public FixtureBrowser(WebDriver api) throws Exception {
        WebDriver[] self = new WebDriver[1];
        Object locator =
                java.lang.reflect.Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebDriver.TargetLocator.class},
                        (p, m, a) -> {
                            if (m.getName().equals("window")) {
                                active = (String) a[0];
                                return self[0];
                            }
                            return null;
                        });
        driver =
                (WebDriver)
                        java.lang.reflect.Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (p, m, a) -> {
                                    return switch (m.getName()) {
                                        case "getWindowHandle" -> active;
                                        case "getWindowHandles" -> new LinkedHashSet<>(windows);
                                        case "getCurrentUrl" -> "https://fixture.test/#/home";
                                        case "getPageSource" -> "<fixture-case/>";
                                        case "switchTo" -> locator;
                                        case "close" -> {
                                            windows.remove(active);
                                            closed++;
                                            yield null;
                                        }
                                        default -> {
                                            try {
                                                yield m.invoke(api, a);
                                            } catch (InvocationTargetException e) {
                                                throw e.getCause();
                                            }
                                        }
                                    };
                                });
        self[0] = driver;
        field = Plugin_ERP.Tests.LoginTest.BaseTest.class.getDeclaredField("driver");
        field.setAccessible(true);
    }

    /** Bind only the scenario instance under test; other classes keep their own browser. */
    public void bind(Plugin_ERP.Tests.LoginTest.BaseTest scenario) throws IllegalAccessException {
        if (!previous.containsKey(scenario)) previous.put(scenario, field.get(scenario));
        field.set(scenario, driver);
    }

    public void addAuxiliaryWindow() {
        windows.add("report-" + closed);
    }

    @Override
    public void close() throws IllegalAccessException {
        for (var entry : previous.entrySet()) field.set(entry.getKey(), entry.getValue());
    }
}
