package Plugin_ERP.System_Modules.Login.BasicData;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class CashListControlsTest {
    @Test
    public void waitsForFirstRowWhenProviderFinishesBeforeDomRowsRender() {
        AtomicInteger rowReads = new AtomicInteger();
        WebElement header = element("الكود", List.of());
        WebElement row = element("12", List.of(element("12", List.of())));
        WebDriver driver = (WebDriver) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {WebDriver.class, JavascriptExecutor.class}, (proxy, method, args) -> {
                    if (method.getName().equals("executeScript")) return true;
                    if (method.getName().equals("findElements")) {
                        if (args[0].toString().contains("thead")) return List.of(header);
                        return rowReads.incrementAndGet() == 1 ? List.of() : List.of(row);
                    }
                    if (method.getName().equals("toString")) return "delayed-safe-grid";
                    throw new AssertionError(method.getName());
                });
        Assert.assertEquals(new CashListControls(null, driver, () -> {}).getFirstSafeCellByHeader("الكود"), "12");
        Assert.assertTrue(rowReads.get() >= 2);
    }

    @Test
    public void waitsForTheProviderBeforeReadingGridRows() {
        AtomicInteger readinessChecks = new AtomicInteger();
        WebElement value = element("12", List.of());
        WebElement header = element("الكود", List.of());
        WebElement row = element("12", List.of(value));
        WebDriver driver = (WebDriver) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {WebDriver.class, JavascriptExecutor.class}, (proxy, method, args) -> {
                    if (method.getName().equals("executeScript")) return readinessChecks.incrementAndGet() >= 2;
                    if (method.getName().equals("findElements")) {
                        Assert.assertTrue(readinessChecks.get() >= 2,
                                "A visible search input must not stand in for a loaded grid");
                        return args[0].toString().contains("thead") ? List.of(header) : List.of(row);
                    }
                    if (method.getName().equals("toString")) return "safe-grid-driver";
                    throw new AssertionError("Unexpected browser call: " + method.getName());
                });
        Assert.assertEquals(new CashListControls(null, driver, () -> {}).getSafeColumnValues("الكود"),
                List.of("12"));
        Assert.assertTrue(readinessChecks.get() >= 2);
    }

    private WebElement element(String text, List<WebElement> children) {
        return (WebElement) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {WebElement.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getText" -> text;
                    case "isDisplayed" -> true;
                    case "findElements" -> children;
                    case "toString" -> "safe-grid-element";
                    default -> throw new AssertionError("Unexpected element call: " + method.getName());
                });
    }

    @DataProvider
    public Object[][] numericDirections() {
        return new Object[][] {
                {true, List.of("2", "11", "101")},
                {false, List.of("101", "11", "2")}
        };
    }

    @Test(dataProvider = "numericDirections")
    public void ordersNumericCodesWithoutMutatingTheDisplayedSnapshot(
            boolean ascending, List<String> expected) {
        List<String> original = new ArrayList<>(List.of("11", "101", "2"));
        var controls = new CashListControls(null, null, () -> {});
        Assert.assertEquals(controls.sortCodes(original, ascending), expected);
        Assert.assertEquals(original, List.of("11", "101", "2"));
    }

    @Test
    public void keepsTheCaseInsensitiveFallbackForNonNumericCodes() {
        var controls = new CashListControls(null, null, () -> {});
        Assert.assertEquals(controls.sortCodes(List.of("B20", "a10", "C30"), true),
                List.of("a10", "B20", "C30"));
    }
}
