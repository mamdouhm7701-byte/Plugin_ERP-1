package SpecialSalesTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.Business.Calculator.RestaurantAdditionalTaxCalculator;
import Plugin_ERP.System_Modules.Login.SpecialSales.*;
import java.math.BigDecimal;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * VAT/exemption recipe using owned non-additional items, independent of branch additional-tax
 * switch.
 */
public final class SelectedRestaurantPolicyTest extends SpecialSalesScenario {
    protected SpecialSalesPage createPage() {
        return new RestaurantInvoicePage(driver);
    }

    @Test
    public void selectedVatPolicySavesTaxableItemsAndSupportedExemptions() {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(profile.expected().activityType(), ActivityType.RESTAURANT);
        String price = profile.expected().taxPolicy() == TaxPolicy.PRICE_INCLUDED ? "115" : "100";
        var taxable = taxItem("policy-taxable", price, false);
        boolean exemptionSupported = profile.expected().taxPolicy() == TaxPolicy.ITEM_BASED;
        var exempt =
                taxItem(
                        exemptionSupported ? "policy-exempt" : "policy-second-taxable",
                        price,
                        exemptionSupported);
        var payment = data.payment();
        var recipes =
                List.of(
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.ONE,
                                new BigDecimal(price),
                                false,
                                BigDecimal.ZERO,
                                false),
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.ONE,
                                new BigDecimal(price),
                                false,
                                BigDecimal.ZERO,
                                exemptionSupported));
        var expected =
                RestaurantAdditionalTaxCalculator.calculate(
                        recipes,
                        profile.expected().taxPolicy(),
                        Boolean.parseBoolean(System.getProperty("business.additionalTax")),
                        new BigDecimal("100"),
                        2);
        restartPage();
        for (var record : List.of(taxable, exempt))
            page.addItem(
                    record.name(),
                    record.values().get("mainUnitBarcode").toString(),
                    data.category().name());
        io.qameta.allure.Allure.addAttachment(
                "Selected restaurant tax expectation",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "business",
                                        profile.expected().fields(),
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
        amounts(page.document(), expected);
        var restaurant = (RestaurantInvoicePage) page;
        restaurant.openPayment();
        page.addOtherPayment(payment.name(), expected.net().toPlainString());
        prepareSave();
        restaurant.payAndClose();
        var invoice = saved();
        amounts(data.read(invoice), expected);
        number(data.balance(taxable, 1), "999");
        number(data.balance(exempt, 1), "999");
    }

    private SpecialSalesTestData.Record taxItem(String key, String price, boolean exempt) {
        var values = TestSupport.PolicyItemRecipe.create(data.itemValues(), price, exempt);
        var record = data.fixture(key, "Items", values);
        var stored = data.read(record);
        Assert.assertEquals(stored.get("isTaxable"), !exempt);
        number(stored.get("itemTax"), exempt ? "0" : "15");
        return record;
    }

    private void amounts(
            Map<String, Object> document, RestaurantAdditionalTaxCalculator.Totals expected) {
        number(document.get("totalAdditionalTaxes"), "0");
        number(document.get("netBeforeTaxes"), expected.beforeVat().toPlainString());
        number(document.get("totalTaxes"), expected.vat().toPlainString());
        number(document.get("net"), expected.net().toPlainString());
    }
}
