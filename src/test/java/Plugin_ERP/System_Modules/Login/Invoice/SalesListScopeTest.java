/* شرح الملف: اختبار سلامة A retained filter panel must not make repeated owned-list preparation
 * fail.
 */
package Plugin_ERP.System_Modules.Login.Invoice;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class SalesListScopeTest {
    @Test
    public void alreadyOpenPanelRemainsOpenAcrossRepeatedPreparation() {
        Panel panel = new Panel(true);
        panel.scope();
        panel.scope();
        Assert.assertEquals(panel.opens, 0);
        Assert.assertEquals(panel.selections, 2);
        Assert.assertEquals(panel.applies, 2);
    }

    @Test
    public void closedPanelOpensOnceAndAppliesTheOwnedCustomer() {
        Panel panel = new Panel(false);
        panel.scope();
        panel.scope();
        Assert.assertEquals(panel.opens, 1);
        Assert.assertEquals(panel.selections, 2);
        Assert.assertEquals(panel.applies, 2);
    }

    private static final class Panel {
        boolean visible;
        int opens, selections, applies;
        final SalesDocumentList list;

        Panel(boolean visible) {
            this.visible = visible;
            WebElement input =
                    (WebElement)
                            Proxy.newProxyInstance(
                                    getClass().getClassLoader(),
                                    new Class<?>[] {WebElement.class},
                                    (proxy, method, args) ->
                                            switch (method.getName()) {
                                                case "isDisplayed" -> this.visible;
                                                case "isEnabled" -> true;
                                                case "sendKeys" -> {
                                                    applies++;
                                                    yield null;
                                                }
                                                case "toString" -> "filter-input";
                                                default ->
                                                        throw new AssertionError(method.getName());
                                            });
            WebDriver driver =
                    (WebDriver)
                            Proxy.newProxyInstance(
                                    getClass().getClassLoader(),
                                    new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                    (proxy, method, args) ->
                                            switch (method.getName()) {
                                                case "findElements" -> List.of(input);
                                                case "executeScript" -> true;
                                                case "toString" -> "filter-driver";
                                                default ->
                                                        throw new AssertionError(method.getName());
                                            });
            SalesModulePage page =
                    new SalesModulePage(driver) {
                        @Override
                        public void slOpen(String route) {
                            Assert.assertEquals(route, "invoice-list");
                        }

                        @Override
                        public void slButton(String text) {
                            Assert.assertEquals(text, "عرض التصفية");
                            Assert.assertFalse(
                                    Panel.this.visible,
                                    "Visible filters must not be toggled closed");
                            opens++;
                            Panel.this.visible = true;
                        }

                        @Override
                        public void slChoose(String label, String name) {
                            Assert.assertEquals(label, "العميل");
                            Assert.assertEquals(name, "QA-owned-customer");
                            selections++;
                        }
                    };
            list =
                    new SalesDocumentList(
                            page, driver, new WebDriverWait(driver, Duration.ofSeconds(1)));
        }

        void scope() {
            list.slListScope("QA-owned-customer");
        }
    }
}
