package SpecialSalesTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import Plugin_ERP.Business.Calculator.RestaurantAdditionalTaxCalculator;
import Plugin_ERP.System_Modules.Login.SpecialSales.*;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.*;

/** Per-unit floor boundaries and final-only rounding, saved and independently reread. */
public final class RestaurantAdditionalTaxBoundaryTest extends SpecialSalesScenario {
    @Override
    protected SpecialSalesPage createPage() {
        return new RestaurantInvoicePage(driver);
    }

    @DataProvider
    public Object[][] recipes() {
        return new Object[][] {
            {"24", "1", 1}, {"25", "3", 1}, {"26", "3", 1},
            {"25.036", "1", 1}, {"25.036", "1", 2}, {"10", "0.5", 1}
        };
    }

    @Test(dataProvider = "recipes")
    public void savedBoundaryAndFractionalTotalsMatchIndependentRecipe(
            String exclusiveUnitPrice, String quantity, int itemCount) {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(
                profile.expected().activityType(), BranchPolicies.ActivityType.RESTAURANT);
        Assert.assertTrue(Boolean.parseBoolean(System.getProperty("business.additionalTax")));
        BigDecimal price = new BigDecimal(exclusiveUnitPrice);
        if (profile.expected().taxPolicy() == TaxPolicy.PRICE_INCLUDED)
            price = price.multiply(new BigDecimal("1.15"));
        BigDecimal units = new BigDecimal(quantity);
        var items = new ArrayList<SpecialSalesTestData.Record>();
        var lines = new ArrayList<RestaurantAdditionalTaxCalculator.Line>();
        for (int i = 0; i < itemCount; i++) {
            var values =
                    TestSupport.PolicyItemRecipe.create(
                            data.itemValues(), price.toPlainString(), false);
            values.put("isAdditionalTaxable", true);
            var owned = data.fixture("additional-boundary-" + i, "Items", values);
            var persistedItem = data.read(owned);
            Assert.assertEquals(persistedItem.get("isAdditionalTaxable"), true);
            number(persistedItem.get("salesPrice"), price.toPlainString());
            items.add(owned);
            lines.add(new RestaurantAdditionalTaxCalculator.Line(units, price, true));
        }
        var payment = data.payment();
        restartPage();
        for (var owned : items) {
            page.addItem(
                    owned.name(),
                    owned.values().get("mainUnitBarcode").toString(),
                    data.category().name());
            page.quantity(owned.name(), quantity);
        }
        var expected =
                RestaurantAdditionalTaxCalculator.calculate(
                        lines, profile.expected().taxPolicy(), true, new BigDecimal("100"), 2);
        Allure.addAttachment(
                "Additional boundary independent expectation",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "exclusiveUnitPrice",
                                        exclusiveUnitPrice,
                                        "selectedPrice",
                                        price,
                                        "quantity",
                                        quantity,
                                        "items",
                                        itemCount,
                                        "additional",
                                        expected.additionalTax(),
                                        "beforeVat",
                                        expected.beforeVat(),
                                        "vat",
                                        expected.vat(),
                                        "net",
                                        expected.net(),
                                        "actual",
                                        page.document()))
                        .toString(2),
                ".json");
        page.waitNet(expected.net().toPlainString());
        RestaurantTaxAssertions.amounts(page.document(), expected);
        var restaurant = (RestaurantInvoicePage) page;
        restaurant.openPayment();
        page.addOtherPayment(payment.name(), expected.net().toPlainString());
        prepareSave();
        restaurant.payAndClose();
        var invoice = saved();
        var stored = data.read(invoice);
        RestaurantTaxAssertions.amounts(stored, expected);
        RestaurantTaxAssertions.payment(stored, payment.id(), expected.net());
        var storedLines = (List<?>) stored.get("lines");
        Assert.assertEquals(storedLines.size(), itemCount);
        for (var owned : items) {
            var line =
                    (Map<?, ?>)
                            storedLines.stream()
                                    .filter(v -> owned.id().equals(((Map<?, ?>) v).get("itemId")))
                                    .findFirst()
                                    .orElseThrow();
            number(line.get("quantity"), quantity);
            number(data.balance(owned, 1), new BigDecimal("1000").subtract(units).toPlainString());
        }
        Allure.addAttachment(
                "Saved additional boundary invoice",
                "application/json",
                new org.json.JSONObject(stored).toString(2),
                ".json");
    }
}
