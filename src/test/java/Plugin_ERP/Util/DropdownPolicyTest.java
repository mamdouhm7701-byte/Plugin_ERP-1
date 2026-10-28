/* شرح الملف: مراجعة دقة اختيار القيم من القوائم.
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

public final class DropdownPolicyTest {
    @DataProvider
    public Object[][] policies() {
        return new Object[][] {
            {"EXACT", List.of("owned"), "owned"},
            {"EXACT", List.of("other", "owned"), "owned"},
            {"EXACT_THEN_CONTAINS", List.of("owned - 1", "owned"), "owned"},
            {"EXACT_THEN_CONTAINS", List.of("owned - 1", "owned - 2", "owned"), "owned"},
            {"EXACT_THEN_CONTAINS", List.of("123 owned"), "123 owned"},
            {"EXACT_THEN_CONTAINS", List.of("owned 123"), "owned 123"}
        };
    }

    @Test(dataProvider = "policies")
    public void selectsAccordingToModulePolicy(String mode, List<String> labels, String expected) {
        Harness harness = new Harness(labels);
        harness.actions()
                .choose(By.id("search"), "owned", FormInteractions.OptionMatch.valueOf(mode));
        Assert.assertEquals(harness.selected, List.of(expected));
    }

    @Test
    public void exactPolicyDoesNotAcceptContainedName() {
        Assert.assertFalse(FormInteractions.OptionMatch.EXACT.matches("123 owned", "owned"));
        Assert.assertFalse(FormInteractions.OptionMatch.EXACT.matches("owned - 123", "owned"));
    }

    @Test
    public void ambiguousFallbackKeepsDiagnosticMessageAndSelectsNothing() {
        Harness harness = new Harness(List.of("owned - 1", "owned - 2"));
        AssertionError failure =
                Assert.expectThrows(
                        AssertionError.class,
                        () ->
                                harness.actions()
                                        .choose(
                                                By.id("search"),
                                                "owned",
                                                FormInteractions.OptionMatch.EXACT_THEN_CONTAINS,
                                                "Ambiguous owned dropdown option: "));
        Assert.assertEquals(failure.getMessage(), "Ambiguous owned dropdown option: owned");
        Assert.assertTrue(harness.selected.isEmpty());
    }

    @Test
    public void duplicateExactMatchesAreRejectedBeforeFallback() {
        Harness harness = new Harness(List.of("owned", "owned", "owned - 1"));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        harness.actions()
                                .choose(
                                        By.id("search"),
                                        "owned",
                                        FormInteractions.OptionMatch.EXACT_THEN_CONTAINS));
        Assert.assertTrue(harness.selected.isEmpty());
    }

    private static final class Harness {
        final List<String> labels;
        final List<String> selected = new ArrayList<>();

        Harness(List<String> labels) {
            this.labels = labels;
        }

        FormInteractions actions() {
            WebDriver driver =
                    (WebDriver)
                            Proxy.newProxyInstance(
                                    getClass().getClassLoader(),
                                    new Class<?>[] {WebDriver.class},
                                    (p, m, a) -> {
                                        if (m.getName().equals("findElements"))
                                            return a[0].toString().contains("search")
                                                    ? List.of(element(null))
                                                    : labels.stream().map(this::element).toList();
                                        throw new UnsupportedOperationException(m.getName());
                                    });
            return new FormInteractions(new WebDriverWait(driver, Duration.ofSeconds(1)), e -> {});
        }

        WebElement element(String label) {
            return (WebElement)
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebElement.class},
                            (p, m, a) ->
                                    switch (m.getName()) {
                                        case "isDisplayed", "isEnabled" -> true;
                                        case "getDomProperty" -> "owned";
                                        case "getDomAttribute" -> "options";
                                        case "getText" -> label;
                                        case "click" -> {
                                            selected.add(label);
                                            yield null;
                                        }
                                        default ->
                                                throw new UnsupportedOperationException(
                                                        m.getName());
                                    });
        }
    }
}
