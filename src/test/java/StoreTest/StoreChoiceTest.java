/* شرح الملف: اختبار اختيار مخزن التجربة والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.StoreModulePage;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class StoreChoiceTest {
    @DataProvider
    public Object[][] redrawStages() {
        return new Object[][] {{"click"}, {"sendKeys"}};
    }

    @Test(dataProvider = "redrawStages")
    public void reacquiresInputAfterDropdownRedraw(String failingOperation) {
        Dropdown dropdown = new Dropdown(failingOperation, false);
        dropdown.page().smChoose("المخزن", "QA-ST-owned-store");
        Assert.assertTrue(dropdown.failedOnce, "The first input must simulate the redraw");
        Assert.assertTrue(dropdown.inputReads >= 2, "The input must be reacquired after redraw");
        Assert.assertEquals(dropdown.selected, 1);
    }

    @Test
    public void ambiguousOwnedOptionsStillStopSelection() {
        Dropdown dropdown = new Dropdown(null, true);
        AssertionError failure =
                Assert.expectThrows(
                        AssertionError.class,
                        () -> dropdown.page().smChoose("المخزن", "QA-ST-owned-store"));
        Assert.assertTrue(failure.getMessage().contains("Ambiguous owned option"));
        Assert.assertEquals(dropdown.selected, 0);
    }

    private static final class Dropdown {
        private final String failingOperation;
        private final boolean ambiguous;
        private boolean failedOnce;
        private int inputReads;
        private int selected;

        Dropdown(String failingOperation, boolean ambiguous) {
            this.failingOperation = failingOperation;
            this.ambiguous = ambiguous;
        }

        StoreModulePage page() {
            WebElement input = element(true);
            WebElement option = element(false);
            WebDriver driver =
                    (WebDriver)
                            Proxy.newProxyInstance(
                                    getClass().getClassLoader(),
                                    new Class<?>[] {WebDriver.class},
                                    (proxy, method, args) -> {
                                        if (method.getName().equals("findElements")) {
                                            if (args[0].toString().contains("vs__search")) {
                                                inputReads++;
                                                return List.of(input);
                                            }
                                            return ambiguous
                                                    ? List.of(option, option)
                                                    : List.of(option);
                                        }
                                        throw new UnsupportedOperationException(method.getName());
                                    });
            return new StoreModulePage(driver) {
                {
                    ready.pollingEvery(Duration.ofMillis(1));
                    ready.withTimeout(Duration.ofSeconds(2));
                }

                @Override
                protected Object js(String script, Object... args) {
                    return null;
                }
            };
        }

        private WebElement element(boolean input) {
            return (WebElement)
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebElement.class},
                            (proxy, method, args) -> {
                                String operation = method.getName();
                                if (input && operation.equals(failingOperation) && !failedOnce) {
                                    failedOnce = true;
                                    throw new ElementNotInteractableException(
                                            "Dropdown is updating");
                                }
                                return switch (operation) {
                                    case "isDisplayed", "isEnabled" -> true;
                                    case "getDomProperty" -> "";
                                    case "getDomAttribute" -> "owned-store-options";
                                    case "getText" -> "QA-ST-owned-store";
                                    case "click" -> {
                                        if (!input) selected++;
                                        yield null;
                                    }
                                    case "sendKeys" -> null;
                                    default -> throw new UnsupportedOperationException(operation);
                                };
                            });
        }
    }
}
