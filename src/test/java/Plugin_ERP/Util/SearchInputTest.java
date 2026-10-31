/* شرح الملف: يختبر البحث عندما تستبدل الشاشة الحقل أثناء الكتابة، ويمنع إرسال البحث بحقل قديم أو عبارة ناقصة. */
package Plugin_ERP.Util;

import java.lang.reflect.Proxy;
import java.util.List;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class SearchInputTest {
    @Test
    public void reacquiresTheReplacedFieldBeforeSubmittingExactlyOnce() {
        var harness = new Harness();
        SearchInput.submit(harness.driver(), By.id("search"), "owned safe");
        Assert.assertEquals(harness.value, "owned safe");
        Assert.assertEquals(harness.submits, 1);
        Assert.assertTrue(harness.reads >= 2);
    }

    @Test
    public void nullClearsTheOldSearchInsteadOfSubmittingIt() {
        var harness = new Harness();
        SearchInput.submit(harness.driver(), By.id("search"), null);
        Assert.assertEquals(harness.value, "");
        Assert.assertEquals(harness.submits, 1);
    }

    @Test
    public void retriesOnlyTheStaleFieldAndKeepsTheCompleteSearchText() {
        var harness = new Harness();
        harness.staleBeforeTyping = true;
        SearchInput.submit(harness.driver(), By.id("search"), "english name");
        Assert.assertEquals(harness.value, "english name");
        Assert.assertEquals(harness.submits, 1);
    }

    private static final class Harness {
        String value = "previous search";
        int generation, reads, submits;
        boolean staleBeforeTyping;

        WebDriver driver() {
            return (WebDriver)
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebDriver.class},
                            (proxy, method, args) -> {
                                if (method.getName().equals("findElements")) {
                                    reads++;
                                    return List.of(input(generation));
                                }
                                throw new AssertionError(method.getName());
                            });
        }

        WebElement input(int version) {
            return (WebElement)
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebElement.class},
                            (proxy, method, args) -> {
                                if (version != generation)
                                    throw new StaleElementReferenceException("field replaced");
                                return switch (method.getName()) {
                                    case "isDisplayed", "isEnabled" -> true;
                                    case "getDomProperty" -> value;
                                    case "sendKeys" -> {
                                        if (staleBeforeTyping) {
                                            staleBeforeTyping = false;
                                            generation++;
                                            throw new StaleElementReferenceException(
                                                    "field replaced before input");
                                        }
                                        CharSequence[] keys = (CharSequence[]) args[0];
                                        if (keys.length == 1 && keys[0] == Keys.ENTER) submits++;
                                        else {
                                            Assert.assertTrue(
                                                    keys[0].toString()
                                                            .contains(Keys.CONTROL.toString()));
                                            value =
                                                    keys[1] == Keys.DELETE
                                                            ? ""
                                                            : keys[1].toString();
                                            generation++;
                                        }
                                        yield null;
                                    }
                                    default -> throw new AssertionError(method.getName());
                                };
                            });
        }
    }
}
