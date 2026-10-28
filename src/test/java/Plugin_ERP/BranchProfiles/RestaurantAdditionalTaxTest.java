package Plugin_ERP.BranchProfiles;

import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import Plugin_ERP.Business.Calculator.RestaurantAdditionalTaxCalculator;
import java.math.BigDecimal;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.*;

public final class RestaurantAdditionalTaxTest {
    @Test
    public void roundsOnlyFinalTotalAndKeepsIntermediatePrecision() {
        var line =
                new RestaurantAdditionalTaxCalculator.Line(
                        BigDecimal.ONE, new BigDecimal("25.036"), true);
        var totals =
                RestaurantAdditionalTaxCalculator.calculate(
                        List.of(line), TaxPolicy.NET_BASED, true, new BigDecimal("100"), 2);
        same(totals.originalBeforeVat(), "25.036");
        same(totals.additionalTax(), "25.036");
        same(totals.beforeVat(), "50.072");
        same(totals.vat(), "7.5108");
        same(totals.net(), "57.58");
    }

    @Test
    public void doesNotRoundEachLineBeforeAddingInvoiceTotal() {
        var lines =
                List.of(
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.ONE, new BigDecimal("25.036"), true),
                        new RestaurantAdditionalTaxCalculator.Line(
                                BigDecimal.ONE, new BigDecimal("25.036"), true));
        var totals =
                RestaurantAdditionalTaxCalculator.calculate(
                        lines, TaxPolicy.NET_BASED, true, new BigDecimal("100"), 2);
        same(totals.net(), "115.17");
        same(totals.beforeVat(), "100.144");
    }

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
        Assert.assertEquals(result.beforeVat().add(result.vat()).compareTo(result.net()), 0);
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

    @DataProvider
    public Object[][] minimumPerUnitExamples() {
        return new Object[][] {
            {TaxPolicy.NET_BASED, "10", "1", "0", "25", "35", "5.25", "40.25"},
            {TaxPolicy.NET_BASED, "10", "3", "0", "75", "105", "15.75", "120.75"},
            {TaxPolicy.PRICE_INCLUDED, "11.5", "1", "0", "25", "35", "5.25", "40.25"},
            {TaxPolicy.PRICE_INCLUDED, "11.5", "3", "0", "75", "105", "15.75", "120.75"},
            {TaxPolicy.ITEM_BASED, "10", "3", "0", "75", "105", "15.75", "120.75"},
            {TaxPolicy.NET_BASED, "25", "3", "0", "75", "150", "22.5", "172.5"},
            {TaxPolicy.NET_BASED, "30", "3", "0", "90", "180", "27", "207"},
            {TaxPolicy.NET_BASED, "30", "3", "18", "75", "147", "22.05", "169.05"},
            {TaxPolicy.NET_BASED, "10", "0.5", "0", "12.5", "17.5", "2.625", "20.13"}
        };
    }

    @Test(dataProvider = "minimumPerUnitExamples")
    public void minimumAdditionalTaxIsAppliedPerDiscountedUnitBeforeQuantity(
            TaxPolicy policy,
            String price,
            String quantity,
            String discount,
            String additional,
            String base,
            String vat,
            String net) {
        int precision = 2;
        var result =
                RestaurantAdditionalTaxCalculator.calculate(
                        List.of(
                                new RestaurantAdditionalTaxCalculator.Line(
                                        new BigDecimal(quantity),
                                        new BigDecimal(price),
                                        true,
                                        new BigDecimal(discount))),
                        policy,
                        true,
                        new BigDecimal("100"),
                        precision);
        same(result.additionalTax(), additional);
        same(result.beforeVat(), base);
        same(result.vat(), vat);
        same(result.net(), net);
    }

    @Test
    public void minimumDoesNotTaxNonSubjectItemsOrDisabledAdditionalTax() {
        var line =
                new RestaurantAdditionalTaxCalculator.Line(
                        new BigDecimal("3"), new BigDecimal("10"), false);
        var result =
                RestaurantAdditionalTaxCalculator.calculate(
                        List.of(line), TaxPolicy.NET_BASED, true, new BigDecimal("100"), 2);
        same(result.additionalTax(), "0");
        same(result.net(), "34.5");
        var subject =
                new RestaurantAdditionalTaxCalculator.Line(
                        new BigDecimal("3"), new BigDecimal("10"), true);
        same(
                RestaurantAdditionalTaxCalculator.calculate(
                                List.of(subject),
                                TaxPolicy.NET_BASED,
                                false,
                                new BigDecimal("100"),
                                2)
                        .additionalTax(),
                "0");
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
