/* شرح الملف: اختبارات طلبات داخلي وخارجي وتوصيل تحفظ الضريبة وتخصم المخزون مرة واحدة: طلبات داخلي
 * وخارجي وتوصيل تحفظ الضريبة وتخصم المخزون مرة واحدة.
 */
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

public final class RestaurantAdditionalTaxOrderTest extends SpecialSalesScenario {
    @Override
    protected SpecialSalesPage createPage() {
        return new RestaurantInvoicePage(driver);
    }

    @DataProvider
    public Object[][] orderTypes() {
        return new Object[][] {
            {"داخلية", "Internal"}, {"خارجية", "External"}, {"توصيل", "Delivery"}
        };
    }

    @Test(dataProvider = "orderTypes")
    public void savedOrderPreservesAdditionalTaxAndDeductsStockOnce(String label, String type) {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(
                profile.expected().activityType(), BranchPolicies.ActivityType.RESTAURANT);
        Assert.assertTrue(Boolean.parseBoolean(System.getProperty("business.additionalTax")));
        String price = profile.expected().taxPolicy() == TaxPolicy.PRICE_INCLUDED ? "11.5" : "10";
        var values = TestSupport.PolicyItemRecipe.create(data.itemValues(), price, false);
        values.put("isAdditionalTaxable", true);
        var subject = data.fixture("order-additional-item", "Items", values);
        var payment = data.payment();
        var table = type.equals("Internal") ? data.table() : null;
        restartPage();
        var restaurant = (RestaurantInvoicePage) page;
        restaurant.type(label);
        if (table != null) restaurant.selectTable(data.read(table).get("code").toString());
        page.addItem(
                subject.name(),
                subject.values().get("mainUnitBarcode").toString(),
                data.category().name());
        page.quantity(subject.name(), "3");
        var expected =
                RestaurantAdditionalTaxCalculator.calculate(
                        List.of(
                                new RestaurantAdditionalTaxCalculator.Line(
                                        new BigDecimal("3"), new BigDecimal(price), true)),
                        profile.expected().taxPolicy(),
                        true,
                        new BigDecimal("100"),
                        2);
        Allure.addAttachment(
                "Restaurant order tax expectation",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "orderType",
                                        type,
                                        "deliveryFee",
                                        "NOT_APPLICABLE",
                                        "quantity",
                                        3,
                                        "additional",
                                        expected.additionalTax(),
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
        Object heldId = null;
        if (table != null) {
            prepareSave();
            restaurant.hold();
            var held = saved();
            heldId = held.id();
            Assert.assertEquals(data.read(held).get("isOnHold"), true);
            number(data.balance(subject, 1), "997");
            restaurant.type(label);
            restaurant.selectTable(data.read(table).get("code").toString());
            restaurant.awaitInvoice(held.id());
            RestaurantTaxAssertions.amounts(page.document(), expected);
            number(data.balance(subject, 1), "997");
            page.notes(page.captureMarker());
        }
        restaurant.openPayment();
        page.addOtherPayment(payment.name(), expected.net().toPlainString());
        prepareSave();
        restaurant.payAndClose();
        var paid = saved();
        var stored = data.read(paid);
        if (heldId != null)
            Assert.assertEquals(paid.id(), heldId, "Held payment must update the same invoice");
        Assert.assertEquals(paid.values().get("restaurantInvoiceType"), type);
        Assert.assertEquals(stored.get("isOnHold"), false);
        RestaurantTaxAssertions.payment(stored, payment.id(), expected.net());
        RestaurantTaxAssertions.amounts(stored, expected);
        var line = (Map<?, ?>) ((List<?>) stored.get("lines")).getFirst();
        Assert.assertEquals(line.get("itemId"), subject.id());
        number(line.get("quantity"), "3");
        number(data.balance(subject, 1), "997");
        Allure.addAttachment(
                "Saved restaurant order additional tax",
                "application/json",
                new org.json.JSONObject(stored).toString(2),
                ".json");
    }
}
