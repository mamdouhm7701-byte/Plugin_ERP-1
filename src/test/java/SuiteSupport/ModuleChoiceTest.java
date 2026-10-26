package SuiteSupport;

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

/** Local UI contract: a dropdown redraw must not abort an owned selection. */
public final class ModuleChoiceTest {
    @DataProvider
    public Object[][] redrawStages() {
        return new Object[][] {
            {"sales", "click"},
            {"sales", "sendKeys"},
            {"purchase", "click"},
            {"purchase", "sendKeys"},
            {"store", "click"},
            {"store", "sendKeys"},
            {"products", "click"},
            {"products", "sendKeys"},
            {"financial", "click"},
            {"financial", "sendKeys"}
        };
    }

    @Test(dataProvider = "redrawStages")
    public void reacquiresInputAfterDropdownRedraw(String module, String failingOperation) {
        Dropdown dropdown = new Dropdown(failingOperation, false);
        dropdown.page(module).accept("المخزن", "QA-ST-owned-store");
        Assert.assertTrue(dropdown.failedOnce, "The first input must simulate the redraw");
        Assert.assertTrue(dropdown.inputReads >= 2, "The input must be reacquired after redraw");
        Assert.assertEquals(dropdown.selected, 1);
    }

    @DataProvider
    public Object[][] modules() {
        return new Object[][] {{"sales"}, {"purchase"}, {"store"}, {"products"}, {"financial"}};
    }

    @Test(dataProvider = "modules")
    public void ambiguousOwnedOptionsStillStopSelection(String module) {
        Dropdown dropdown = new Dropdown(null, true);
        AssertionError failure =
                Assert.expectThrows(
                        AssertionError.class,
                        () -> dropdown.page(module).accept("المخزن", "QA-ST-owned-store"));
        Assert.assertTrue(failure.getMessage().startsWith("Ambiguous "));
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

        java.util.function.BiConsumer<String, String> page(String module) {
            WebElement input = element(true);
            WebElement option = element(false);
            WebDriver driver =
                    (WebDriver)
                            Proxy.newProxyInstance(
                                    getClass().getClassLoader(),
                                    new Class<?>[] {
                                        WebDriver.class,
                                        org.openqa.selenium.JavascriptExecutor.class
                                    },
                                    (proxy, method, args) -> {
                                        if (method.getName().equals("executeScript")) return null;
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
            if (module.equals("sales")) {
                var page =
                        new Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage(driver) {
                            {
                                ready.pollingEvery(Duration.ofMillis(1));
                                ready.withTimeout(Duration.ofSeconds(2));
                            }

                            @Override
                            protected Object js(String script, Object... args) {
                                return null;
                            }
                        };
                return page::slChoose;
            }
            if (module.equals("purchase")) {
                var page =
                        new Plugin_ERP.System_Modules.Login.purches.PurchaseModulePage(driver) {
                            {
                                ready.pollingEvery(Duration.ofMillis(1));
                                ready.withTimeout(Duration.ofSeconds(2));
                            }

                            @Override
                            protected Object js(String script, Object... args) {
                                return null;
                            }
                        };
                return page::puChoose;
            }
            if (module.equals("store")) {
                var page =
                        new StoreModulePage(driver) {
                            {
                                ready.pollingEvery(Duration.ofMillis(1));
                                ready.withTimeout(Duration.ofSeconds(2));
                            }

                            @Override
                            protected Object js(String script, Object... args) {
                                return null;
                            }
                        };
                return page::smChoose;
            }
            if (module.equals("products")) {
                var page =
                        new Plugin_ERP.System_Modules.Login.Products.ProductsModulePage(driver) {
                            {
                                ready.pollingEvery(Duration.ofMillis(1));
                                ready.withTimeout(Duration.ofSeconds(2));
                            }
                        };
                return page::prChoose;
            }
            if (module.equals("financial")) {
                var page =
                        new Plugin_ERP.System_Modules.Login.FinancialMovements
                                .FinancialMovementPage(driver);
                return page::choose;
            }
            throw new IllegalArgumentException(module);
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
