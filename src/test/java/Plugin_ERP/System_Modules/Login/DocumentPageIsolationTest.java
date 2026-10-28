/* شرح الملف: اختبار سلامة Protects the architectural boundary that previously made sales depend on
 * three unrelated domains.
 */
package Plugin_ERP.System_Modules.Login;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DocumentPageIsolationTest {
    private static WebDriver browser() {
        return (WebDriver)
                Proxy.newProxyInstance(
                        DocumentPageIsolationTest.class.getClassLoader(),
                        new Class<?>[] {WebDriver.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("toString")) return "isolated-page";
                            throw new AssertionError(
                                    "Construction must not access the browser: "
                                            + method.getName());
                        });
    }

    @Test
    public void documentsDoNotInheritDealerOrAnotherTransactionDomain() {
        Assert.assertFalse(AbstractForDealer.class.isAssignableFrom(AbstractForStore.class));
        Assert.assertFalse(AbstractForStore.class.isAssignableFrom(AbstractForPurchases.class));
        Assert.assertFalse(AbstractForPurchases.class.isAssignableFrom(AbstractForSales.class));
        Assert.assertEquals(AbstractForSales.class.getSuperclass(), DocumentPage.class);
    }

    @Test
    public void onePageUsesOneSessionContextAndDoesNotShareStateWithAnother() {
        WebDriver firstDriver = browser(), secondDriver = browser();
        var first = new AbstractForPurchases(firstDriver);
        var second = new AbstractForSales(secondDriver);
        Assert.assertSame(first.context().driver(), firstDriver);
        Assert.assertSame(second.context().driver(), secondDriver);
        Assert.assertNotSame(first.context(), second.context());
        Assert.assertNotSame(first.context().elements(), second.context().elements());
        Assert.assertEquals(first.normalizeQuantity(" 2.000 "), "2");
        Assert.assertEquals(second.normalizeQuantity("2.50"), "2.50");
    }

    @Test
    public void inheritedSummaryReadersStillDispatchToSalesOverrides() {
        var page =
                new AbstractForSales(browser()) {
                    @Override
                    protected BigDecimal getSummaryValue(String label) {
                        return switch (label) {
                            case "قبل الضريبة" -> new BigDecimal("100");
                            case "الضريبة" -> new BigDecimal("15");
                            case "الصافي" -> new BigDecimal("115");
                            case "خصم" -> new BigDecimal("5");
                            default -> throw new AssertionError(label);
                        };
                    }
                };
        Assert.assertEquals(page.getBeforeTax(), new BigDecimal("100"));
        Assert.assertEquals(page.getTax(), new BigDecimal("15"));
        Assert.assertEquals(page.getNet(), new BigDecimal("115"));
        Assert.assertEquals(page.getDiscount(), new BigDecimal("5"));
    }
}
