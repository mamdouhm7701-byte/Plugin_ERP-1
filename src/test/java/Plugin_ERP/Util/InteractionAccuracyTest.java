package Plugin_ERP.Util;

import Plugin_ERP.Bots.*;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Regressions for false monetary matches, synthetic clicks and already-open report tabs. */
public final class InteractionAccuracyTest {
    @Test
    public void hiddenTextInsideVisibleAncestorDoesNotCountAsDisplayed() {
        WebElement ancestor = element("visible sibling", true, false, new AtomicInteger());
        WebElement hidden = element("hidden target", false, false, new AtomicInteger());
        WebDriver driver =
                browser(
                        (method, args) -> {
                            if (method.equals("findElements")) return List.of(ancestor, hidden);
                            throw new UnsupportedOperationException(method);
                        });
        Assert.assertFalse(
                new ValidationActions(driver, null, null).IsTextDisplayed("hidden target"));
    }

    @Test
    public void decimalDigitsAreNeverRemovedAndArabicAmountsAreSupported() {
        var values = new ValidationActions(null, null, null);
        Assert.assertEquals(values.normalizePrice("100.0100"), "100.01");
        Assert.assertEquals(values.normalizePrice("\u200f١٬٠٠٠٫٠١"), "1000.01");
        Assert.assertEquals(values.normalizePrice("1,000.00"), "1000");
        Assert.assertNotEquals(values.normalizePrice("100.01"), values.normalizePrice("1001"));
        Assert.expectThrows(NumberFormatException.class, () -> values.normalizePrice("total 100"));
    }

    @Test
    public void financialLookupRejectsCollisionSubstringAndOneCentDifference() {
        WebElement label = element("Amount", true, false, new AtomicInteger());
        WebElement amount = element("1001", true, false, new AtomicInteger());
        WebDriver driver =
                browser(
                        (method, args) -> {
                            if (method.equals("findElements")) return List.of(label, amount);
                            throw new UnsupportedOperationException(method);
                        });
        var values = new ValidationActions(driver, null, null);
        Assert.assertFalse(values.IsPriceDisplayedForFin("100.01"));
        Assert.assertFalse(values.IsPriceDisplayed("100"));
        Assert.assertFalse(values.IsNumericPriceDisplayed("1001.01"));
        Assert.assertTrue(values.IsPriceDisplayedForFin("1001.00"));
    }

    @Test
    public void interceptedClickIsNeverConvertedToJavascriptClick() {
        String previous = System.getProperty("defultwait");
        System.setProperty("defultwait", "0");
        AtomicInteger clicks = new AtomicInteger(), synthetic = new AtomicInteger();
        WebElement blocked = element("", true, true, clicks);
        WebDriver driver =
                browser(
                        (method, args) -> {
                            if (method.equals("findElement")) return blocked;
                            if (method.equals("executeScript")) {
                                if (args[0].toString().contains(".click("))
                                    synthetic.incrementAndGet();
                                return null;
                            }
                            throw new UnsupportedOperationException(method);
                        });
        try {
            Assert.expectThrows(
                    TimeoutException.class,
                    () -> new ElementActions(driver).ClickElement(By.id("blocked")));
            Assert.assertTrue(clicks.get() > 0);
            Assert.assertEquals(synthetic.get(), 0);
        } finally {
            if (previous == null) System.clearProperty("defultwait");
            else System.setProperty("defultwait", previous);
        }
    }

    @Test
    public void reportOpenedDuringClickIsSelectedAgainstPreClickHandles() {
        List<String> selected = new ArrayList<>();
        WebDriver.TargetLocator target =
                (WebDriver.TargetLocator)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.TargetLocator.class},
                                (p, method, args) -> {
                                    if (method.getName().equals("window")) {
                                        selected.add(args[0].toString());
                                        return null;
                                    }
                                    throw new UnsupportedOperationException(method.getName());
                                });
        WebDriver driver =
                browser(
                        (method, args) ->
                                switch (method) {
                                    case "getWindowHandles" ->
                                            Set.of("main", "existing-help", "report");
                                    case "switchTo" -> target;
                                    case "getCurrentUrl" -> "https://fixture.test/ReportViewer";
                                    default -> throw new UnsupportedOperationException(method);
                                });
        new WaitActions(driver, null, null).switchToNewTab(Set.of("main", "existing-help"));
        Assert.assertEquals(selected, List.of("report"));
    }

    private interface Call {
        Object invoke(String method, Object[] args);
    }

    private WebDriver browser(Call call) {
        return (WebDriver)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                        (p, method, args) -> call.invoke(method.getName(), args));
    }

    private WebElement element(
            String text, boolean enabled, boolean blocked, AtomicInteger clicks) {
        return (WebElement)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebElement.class},
                        (p, method, args) ->
                                switch (method.getName()) {
                                    case "isDisplayed", "isEnabled" -> enabled;
                                    case "getText" -> text;
                                    case "click" -> {
                                        clicks.incrementAndGet();
                                        if (blocked)
                                            throw new ElementClickInterceptedException("covered");
                                        yield null;
                                    }
                                    default ->
                                            throw new UnsupportedOperationException(
                                                    method.getName());
                                });
    }
}
