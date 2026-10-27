package Plugin_ERP.BranchProfiles;

import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import Plugin_ERP.Business.Calculator.RestaurantAdditionalTaxCalculator;
import java.math.BigDecimal;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.*;

public final class RestaurantAdditionalTaxTest {
    @DataProvider
    public Object[][] examples() {
        return new Object[][] {
            {TaxPolicy.NET_BASED, "100", true, true, "100", "200", "30", "230"},
            {TaxPolicy.PRICE_INCLUDED, "115", true, true, "100", "200", "30", "230"},
            {TaxPolicy.NET_BASED, "100", false, true, "0", "100", "15", "115"},
            {TaxPolicy.PRICE_INCLUDED, "115", false, true, "0", "100", "15", "115"},
            {TaxPolicy.NET_BASED, "100", true, false, "0", "100", "15", "115"}
        };
    }

    @Test(dataProvider = "examples")
    public void userExamplesAndBothEnablementGates(
            TaxPolicy policy,
            String price,
            boolean itemSubject,
            boolean enabled,
            String additional,
            String base,
            String vat,
            String net) {
        var line =
                new RestaurantAdditionalTaxCalculator.Line(
                        BigDecimal.ONE, new BigDecimal(price), itemSubject);
        var result =
                RestaurantAdditionalTaxCalculator.calculate(
                        List.of(line), policy, enabled, new BigDecimal("100"), 2);
        same(result.additionalTax(), additional);
        same(result.beforeVat(), base);
        same(result.vat(), vat);
        same(result.net(), net);
        Assert.assertEquals(result.beforeVat().add(result.vat()), result.net());
    }

    @Test
    public void mixedItemsApplyAdditionalTaxOnlyToSubjectItems() {
        var lines =
                List.of(
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.ONE, new BigDecimal("100"), true),
                        new RestaurantAdditionalTaxCalculator.Line(
                                new BigDecimal("2"), new BigDecimal("100"), false));
        var result =
                RestaurantAdditionalTaxCalculator.calculate(
                        lines, TaxPolicy.NET_BASED, true, new BigDecimal("100"), 2);
        same(result.additionalTax(), "100");
        same(result.beforeVat(), "400");
        same(result.vat(), "60");
        same(result.net(), "460");
    }

    @Test
    public void invalidInputsAreRejected() {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.ZERO, BigDecimal.ONE, true));
    }

    private void same(BigDecimal value, String expected) {
        Assert.assertEquals(value.compareTo(new BigDecimal(expected)), 0);
    }

    @Test
    public void additionalTaxUsesDiscountedAmountUnderBothPricePolicies() {
        for (var policy : List.of(TaxPolicy.NET_BASED, TaxPolicy.PRICE_INCLUDED)) {
            String price = policy == TaxPolicy.NET_BASED ? "100" : "115";
            String discount = policy == TaxPolicy.NET_BASED ? "10" : "11.50";
            var line =
                    new RestaurantAdditionalTaxCalculator.Line(
                            BigDecimal.ONE, new BigDecimal(price), true, new BigDecimal(discount));
            var totals =
                    RestaurantAdditionalTaxCalculator.calculate(
                            List.of(line), policy, true, new BigDecimal("100"), 2);
            same(totals.additionalTax(), "90");
            same(totals.beforeVat(), "180");
            same(totals.vat(), "27");
            same(totals.net(), "207");
        }
    }

    @Test
    public void discountCannotExceedLineAmount() {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.ONE,
                                new BigDecimal("100"),
                                true,
                                new BigDecimal("101")));
    }

    @Test
    public void additionalSubjectOverridesItemVatExemptionButNotOtherItems() {
        var subject =
                new RestaurantAdditionalTaxCalculator.Line(
                        BigDecimal.ONE, new BigDecimal("100"), true, BigDecimal.ZERO, true);
        var exempt =
                new RestaurantAdditionalTaxCalculator.Line(
                        BigDecimal.ONE, new BigDecimal("100"), false, BigDecimal.ZERO, true);
        var result =
                RestaurantAdditionalTaxCalculator.calculate(
                        List.of(subject, exempt),
                        TaxPolicy.ITEM_BASED,
                        true,
                        new BigDecimal("100"),
                        2);
        same(result.additionalTax(), "100");
        same(result.beforeVat(), "300");
        same(result.vat(), "30");
        same(result.net(), "330");
        var off =
                RestaurantAdditionalTaxCalculator.calculate(
                        List.of(subject, exempt),
                        TaxPolicy.ITEM_BASED,
                        false,
                        new BigDecimal("100"),
                        2);
        same(off.additionalTax(), "0");
        same(off.vat(), "0");
        same(off.net(), "200");
    }
}
