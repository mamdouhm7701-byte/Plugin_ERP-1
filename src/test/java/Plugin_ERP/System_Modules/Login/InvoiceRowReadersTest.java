package Plugin_ERP.System_Modules.Login;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Checks the DOM-to-model boundary, including different sales and purchase visibility policies. */
public class InvoiceRowReadersTest {
    @Test
    public void salesReadsFractionalValuesAndSkipsHiddenRows() {
        var rows = new AbstractForSales(grid()).getInvoiceRows();
        Assert.assertEquals(rows.size(), 1);
        Assert.assertEquals(rows.getFirst().getItemName(), "visible item");
        Assert.assertEquals(rows.getFirst().getQuantity(), new BigDecimal("2.5"));
        Assert.assertEquals(rows.getFirst().getPrice(), new BigDecimal("7.30"));
        Assert.assertEquals(rows.getFirst().getLineTotal(), new BigDecimal("18.250"));
    }

    @Test
    public void purchaseRetainsItsExistingAllRowsPolicyAndPurchasePriceColumn() {
        var rows = new AbstractForPurchases(grid()).getInvoiceRows();
        Assert.assertEquals(rows.size(), 2);
        Assert.assertEquals(rows.getFirst().getPrice(), new BigDecimal("6.20"));
        Assert.assertEquals(rows.get(1).getItemName(), "hidden item");
    }

    @Test
    public void legacySnapshotEntryPointsKeepNamesQuantitiesAndSalesPrices() {
        var page = new AbstractForSales(grid());
        var expected = Map.of("visible item", "2.5|7.30", "hidden item", "2.5|7.30");
        Assert.assertEquals(page.getNamesQuantitiesAndPrices(), expected);
        Assert.assertEquals(page.GetNamesQuantitiesAndPricesForInv(), expected);
    }

    private static WebDriver grid() {
        List<WebElement> rows = List.of(row("visible item", true), row("hidden item", false));
        return (WebDriver)
                Proxy.newProxyInstance(
                        InvoiceRowReadersTest.class.getClassLoader(),
                        new Class<?>[] {WebDriver.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("findElements")) return rows;
                            if (method.getName().equals("findElement")) {
                                String by = args[0].toString();
                                String index =
                                        by.contains("سعر الشراء")
                                                ? "4"
                                                : by.contains("السعر")
                                                        ? "3"
                                                        : by.contains("الكمية") ? "2" : "1";
                                return cell(index);
                            }
                            if (method.getName().equals("toString")) return "invoice-grid";
                            throw new AssertionError("Unexpected driver call: " + method.getName());
                        });
    }

    private static WebElement row(String name, boolean visible) {
        return (WebElement)
                Proxy.newProxyInstance(
                        InvoiceRowReadersTest.class.getClassLoader(),
                        new Class<?>[] {WebElement.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("isDisplayed")) return visible;
                            if (method.getName().equals("findElements")
                                    || method.getName().equals("findElement")) {
                                String by = args[0].toString();
                                WebElement value =
                                        cell(
                                                by.contains("aria-colindex='1'")
                                                        ? name
                                                        : by.contains("aria-colindex='2'")
                                                                ? "2.5"
                                                                : by.contains("aria-colindex='4'")
                                                                        ? "6.20"
                                                                        : "7.30");
                                return method.getName().equals("findElement")
                                        ? value
                                        : List.of(value);
                            }
                            throw new AssertionError("Unexpected row call: " + method.getName());
                        });
    }

    private static WebElement cell(String value) {
        return (WebElement)
                Proxy.newProxyInstance(
                        InvoiceRowReadersTest.class.getClassLoader(),
                        new Class<?>[] {WebElement.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("getAttribute")
                                    || method.getName().equals("getText")) return value;
                            throw new AssertionError("Unexpected cell call: " + method.getName());
                        });
    }
}
