/* شرح الملف: اختبارات تنفيذ الضغط والكتابة والاختيار مع مراجعة نجاح الخطوة.
 */
package Plugin_ERP.Util;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class FormInteractionsTest {
    @DataProvider
    public Object[][] matchingPolicies() {
        return new Object[][] {
            {"CODE_SEGMENT", "123 - owned - active", true},
            {"NAME_AT_EDGE", "123 - owned - active", false},
            {"CODE_SEGMENT", "owned-extra", false},
            {"NAME_AT_EDGE", "owned-extra", false},
            {"CODE_SEGMENT", "123 - owned", true},
            {"NAME_AT_EDGE", "123 - owned", true},
            {"CODE_SEGMENT", "owned - 123", true},
            {"NAME_AT_EDGE", "owned - 123", true},
            {"CODE_SEGMENT", "  owned  ", true},
            {"NAME_AT_EDGE", "  owned  ", true}
        };
    }

    @Test(dataProvider = "matchingPolicies")
    public void preservesModuleOptionMatching(String policy, String text, boolean expected) {
        Assert.assertEquals(
                FormInteractions.OptionMatch.valueOf(policy).matches(text, "owned"), expected);
    }

    @DataProvider
    public Object[][] inputModes() {
        return new Object[][] {
            {"CLICK_REPLACE", "new value", true}, {"CLICK_REPLACE", "", false},
            {"CLEAR_REPLACE", "new value", true}, {"CLEAR_REPLACE", "", false}
        };
    }

    @Test(dataProvider = "inputModes")
    public void preservesInputReplacementAndBlur(String mode, String value, boolean blur) {
        Harness harness = new Harness();
        harness.actions()
                .type(By.id("field"), value, blur, FormInteractions.InputMode.valueOf(mode));
        Assert.assertEquals(harness.clicks, mode.equals("CLICK_REPLACE") ? 1 : 0);
        Assert.assertEquals(
                harness.keys.stream().filter(k -> k.equals(Keys.TAB.toString())).count(),
                blur ? 1L : 0L);
        Assert.assertTrue(harness.keys.stream().anyMatch(k -> k.contains(Keys.CONTROL.toString())));
        Assert.assertEquals(
                harness.keys.contains(Keys.DELETE.toString()),
                value.isEmpty() || mode.equals("CLEAR_REPLACE"));
        if (!value.isEmpty()) Assert.assertTrue(harness.keys.contains(value));
    }

    @Test
    public void ignoresHiddenAndDisabledInputs() {
        Harness harness = new Harness();
        harness.skipUnavailable = true;
        harness.actions()
                .type(By.id("field"), "owned", false, FormInteractions.InputMode.CLICK_REPLACE);
        Assert.assertEquals(harness.clicks, 1);
        Assert.assertTrue(harness.keys.contains("owned"));
    }

    @Test
    public void clicksOnlyAvailableElement() {
        Harness harness = new Harness();
        harness.skipUnavailable = true;
        harness.actions().click(By.id("button"));
        Assert.assertEquals(harness.clicks, 1);
    }

    @DataProvider
    public Object[][] exactAndFallbackOptions() {
        return new Object[][] {
            {List.of("owned extra", "owned"), "owned"},
            {List.of("owned extra"), "owned extra"},
            {List.of("owned first", "owned second"), ""}
        };
    }

    @Test(dataProvider = "exactAndFallbackOptions")
    public void oneDropdownSnapshotPrefersExactMatchAndRejectsAmbiguity(
            List<String> labels, String expected) {
        var reads = new java.util.concurrent.atomic.AtomicInteger();
        var selected = new ArrayList<String>();
        java.util.function.Function<String, WebElement> option =
                label ->
                        (WebElement)
                                Proxy.newProxyInstance(
                                        getClass().getClassLoader(),
                                        new Class<?>[] {WebElement.class},
                                        (proxy, method, args) ->
                                                switch (method.getName()) {
                                                    case "isDisplayed", "isEnabled" -> true;
                                                    case "getDomProperty" -> "owned";
                                                    case "getDomAttribute" -> "owned-dropdown";
                                                    case "getText" -> {
                                                        reads.incrementAndGet();
                                                        yield label;
                                                    }
                                                    case "click" -> {
                                                        selected.add(label);
                                                        yield null;
                                                    }
                                                    default ->
                                                            throw new AssertionError(
                                                                    method.getName());
                                                });
        var options = labels.stream().map(option).toList();
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class},
                                (proxy, method, args) -> {
                                    if (method.getName().equals("findElements"))
                                        return args[0].toString().contains("//ul")
                                                ? options
                                                : List.of(option.apply("input"));
                                    throw new AssertionError(method.getName());
                                });
        var actions =
                new FormInteractions(
                        new WebDriverWait(driver, Duration.ofSeconds(1)), element -> {});
        Runnable choose =
                () ->
                        actions.choose(
                                By.id("field"),
                                "owned",
                                FormInteractions.OptionMatch.EXACT_THEN_CONTAINS);
        if (expected.isEmpty()) {
            Assert.expectThrows(AssertionError.class, choose::run);
            Assert.assertTrue(selected.isEmpty());
        } else {
            choose.run();
            Assert.assertEquals(selected, List.of(expected));
        }
        Assert.assertEquals(reads.get(), labels.size());
    }

    private static final class Harness {
        final List<String> keys = new ArrayList<>();
        int clicks;
        boolean skipUnavailable;

        FormInteractions actions() {
            WebElement available = element(true, true);
            WebDriver driver =
                    (WebDriver)
                            Proxy.newProxyInstance(
                                    getClass().getClassLoader(),
                                    new Class<?>[] {WebDriver.class},
                                    (proxy, method, args) -> {
                                        if (method.getName().equals("findElements"))
                                            return skipUnavailable
                                                    ? List.of(
                                                            element(false, true),
                                                            element(true, false),
                                                            available)
                                                    : List.of(available);
                                        throw new UnsupportedOperationException(method.getName());
                                    });
            return new FormInteractions(
                    new WebDriverWait(driver, Duration.ofSeconds(1)), element -> {});
        }

        WebElement element(boolean shown, boolean enabled) {
            return (WebElement)
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebElement.class},
                            (proxy, method, args) -> {
                                return switch (method.getName()) {
                                    case "isDisplayed" -> shown;
                                    case "isEnabled" -> enabled;
                                    case "getDomProperty" -> "old value";
                                    case "click" -> {
                                        Assert.assertTrue(shown && enabled);
                                        clicks++;
                                        yield null;
                                    }
                                    case "sendKeys" -> {
                                        Assert.assertTrue(shown && enabled);
                                        for (CharSequence key : (CharSequence[]) args[0])
                                            keys.add(key.toString());
                                        yield null;
                                    }
                                    default ->
                                            throw new UnsupportedOperationException(
                                                    method.getName());
                                };
                            });
        }
    }
}
