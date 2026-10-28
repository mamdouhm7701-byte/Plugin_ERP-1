package Plugin_ERP.BranchProfiles;

import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.Business.Calculator.*;
import Plugin_ERP.Models.*;
import java.math.BigDecimal;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.*;

public final class BranchInvoiceCalculatorTest {
    private PolicyInvoiceLine line(
            String price, String discount, DiscountType kind, boolean exempt) {
        return new PolicyInvoiceLine(
                "recipe",
                BigDecimal.ONE,
                new BigDecimal(price),
                new BigDecimal(discount),
                kind,
                exempt);
    }

    @Test
    public void finalRoundingRetainsIntermediatePrecisionAcrossLines() {
        var rows =
                List.of(
                        line("25.036", "0", DiscountType.VALUE, false),
                        line("25.036", "0", DiscountType.VALUE, false));
        var result = BranchInvoiceCalculator.calculateFinalRounded(rows, TaxPolicy.NET_BASED, 2);
        same(result.getBeforeTax(), "50.072");
        same(result.getTax(), "7.5108");
        same(result.getNet(), "57.58");
    }

    @Test
    public void finalRoundingPreservesItemExemptionAndIncludedPrice() {
        var rows =
                List.of(
                        line("25.036", "0", DiscountType.VALUE, false),
                        line("25.036", "0", DiscountType.VALUE, true));
        var itemBased =
                BranchInvoiceCalculator.calculateFinalRounded(rows, TaxPolicy.ITEM_BASED, 2);
        same(itemBased.getTax(), "3.7554");
        same(itemBased.getNet(), "53.83");
        var included =
                BranchInvoiceCalculator.calculateFinalRounded(
                        List.of(line("28.7914", "0", DiscountType.VALUE, false)),
                        TaxPolicy.PRICE_INCLUDED,
                        2);
        same(included.getBeforeTax(), "25.036");
        same(included.getTax(), "3.7554");
        same(included.getNet(), "28.79");
    }

    @DataProvider
    public Object[][] examples() {
        return new Object[][] {
            {TaxPolicy.NET_BASED, false, "190", "28.50", "218.50"},
            {TaxPolicy.PRICE_INCLUDED, false, "165.22", "24.78", "190"},
            {TaxPolicy.ITEM_BASED, false, "190", "28.50", "218.50"},
            {TaxPolicy.ITEM_BASED, true, "190", "13.50", "203.50"},
            {TaxPolicy.NET_BASED, true, "190", "28.50", "218.50"},
            {TaxPolicy.PRICE_INCLUDED, true, "165.22", "24.78", "190"}
        };
    }

    @Test(dataProvider = "examples")
    public void screenshotAndUserConfirmedExemptionRules(
            TaxPolicy policy, boolean secondExempt, String base, String tax, String net) {
        for (var kind : DiscountType.values()) {
            var rows =
                    List.of(line("100", "10", kind, false), line("100", "0", kind, secondExempt));
            var result =
                    BranchInvoiceCalculator.calculate(
                            rows, policy, 2, BranchInvoiceCalculator.TaxRounding.INVOICE);
            same(result.getTotal(), "200");
            same(result.getDiscount(), "10");
            same(result.getBeforeTax(), base);
            same(result.getTax(), tax);
            same(result.getNet(), net);
            Assert.assertEquals(result.getBeforeTax().add(result.getTax()), result.getNet());
        }
    }

    @Test
    public void roundingChoiceIsExplicitAndCannotBeInferredFromTheScreenshot() {
        var rows =
                List.of(
                        line("0.03", "0", DiscountType.VALUE, false),
                        line("0.03", "0", DiscountType.VALUE, false));
        same(
                BranchInvoiceCalculator.calculate(
                                rows,
                                TaxPolicy.NET_BASED,
                                2,
                                BranchInvoiceCalculator.TaxRounding.INVOICE)
                        .getTax(),
                "0.01");
        same(
                BranchInvoiceCalculator.calculate(
                                rows,
                                TaxPolicy.NET_BASED,
                                2,
                                BranchInvoiceCalculator.TaxRounding.LINE)
                        .getTax(),
                "0.00");
        Assert.expectThrows(
                NullPointerException.class,
                () -> BranchInvoiceCalculator.calculate(rows, TaxPolicy.NET_BASED, 2, null));
    }

    @Test
    public void fullDiscountExemptAndFractionalRecipesHaveIndependentExpectedAmounts() {
        var fractional =
                new PolicyInvoiceLine(
                        "fractional",
                        new BigDecimal("2.5"),
                        new BigDecimal("10"),
                        new BigDecimal("10"),
                        DiscountType.PERCENTAGE,
                        false);
        same(
                BranchInvoiceCalculator.calculate(
                                List.of(fractional),
                                TaxPolicy.NET_BASED,
                                2,
                                BranchInvoiceCalculator.TaxRounding.INVOICE)
                        .getNet(),
                "25.88");
        for (var policy : TaxPolicy.values()) {
            var full =
                    BranchInvoiceCalculator.calculate(
                            List.of(line("100", "100", DiscountType.PERCENTAGE, true)),
                            policy,
                            2,
                            BranchInvoiceCalculator.TaxRounding.INVOICE);
            same(full.getNet(), "0");
            same(full.getTax(), "0");
        }
        same(
                BranchInvoiceCalculator.calculate(
                                List.of(line("100", "0", DiscountType.VALUE, true)),
                                TaxPolicy.ITEM_BASED,
                                2,
                                BranchInvoiceCalculator.TaxRounding.INVOICE)
                        .getNet(),
                "100");
    }

    @Test
    public void unconfirmedActivitiesAndMovementDiscountCannotSilentlyReuseSalesContract() {
        var snapshot =
                new BranchSnapshot(
                        "https://erp.example",
                        "owned",
                        ActivityType.PUBLIC,
                        DiscountLevel.ITEMS,
                        TaxPolicy.NET_BASED,
                        StocktakingMethod.PERIODIC);
        var result =
                BranchInvoiceCalculator.calculate(
                        new BranchProfile("reference", snapshot),
                        List.of(line("100", "0", DiscountType.VALUE, false)),
                        2,
                        BranchInvoiceCalculator.TaxRounding.INVOICE);
        same(result.getNet(), "115");
        for (var activity : ActivityType.values()) {
            if (activity == ActivityType.PUBLIC) continue;
            var other =
                    new BranchSnapshot(
                            snapshot.origin(),
                            snapshot.branchId(),
                            activity,
                            snapshot.discountLevel(),
                            snapshot.taxPolicy(),
                            snapshot.stocktakingMethod());
            Assert.expectThrows(
                    IllegalArgumentException.class,
                    () ->
                            BranchInvoiceCalculator.calculate(
                                    new BranchProfile("other", other),
                                    List.of(),
                                    2,
                                    BranchInvoiceCalculator.TaxRounding.INVOICE));
        }
        var movement =
                new BranchSnapshot(
                        snapshot.origin(),
                        snapshot.branchId(),
                        ActivityType.PUBLIC,
                        DiscountLevel.TRANSACTION,
                        snapshot.taxPolicy(),
                        snapshot.stocktakingMethod());
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        BranchInvoiceCalculator.calculate(
                                new BranchProfile("movement", movement),
                                List.of(),
                                2,
                                BranchInvoiceCalculator.TaxRounding.INVOICE));
    }

    @Test
    public void invalidRecipeIsRejectedBeforeCalculation() {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> line("100", "101", DiscountType.PERCENTAGE, false));
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> line("100", "101", DiscountType.VALUE, false));
        Assert.expectThrows(
                IllegalArgumentException.class, () -> line("-1", "0", DiscountType.VALUE, false));
    }

    @Test
    public void existingFixtureHelperCanCalculateTaxableItemsExplicitly() {
        var helper = new TestSupport.SalesTaxProfile("ItemBased", List.of(new BigDecimal("15")));
        Assert.assertEquals(helper.net("190", false), "218.50");
        Assert.assertEquals(helper.net("190", true), "190.00");
    }

    @Test
    public void movementDiscountUsesWholeDocumentAndRejectsUnverifiedMixedAllocation() {
        var inputs =
                List.of(
                        line("100", "0", DiscountType.VALUE, false),
                        line("100", "0", DiscountType.VALUE, true));
        same(
                BranchInvoiceCalculator.calculateMovementDiscount(
                                inputs,
                                new BigDecimal("10"),
                                DiscountType.VALUE,
                                TaxPolicy.NET_BASED,
                                2)
                        .getNet(),
                "218.50");
        same(
                BranchInvoiceCalculator.calculateMovementDiscount(
                                inputs,
                                new BigDecimal("5"),
                                DiscountType.PERCENTAGE,
                                TaxPolicy.PRICE_INCLUDED,
                                2)
                        .getNet(),
                "190");
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        BranchInvoiceCalculator.calculateMovementDiscount(
                                inputs,
                                BigDecimal.TEN,
                                DiscountType.VALUE,
                                TaxPolicy.ITEM_BASED,
                                2));
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        BranchInvoiceCalculator.calculateMovementDiscount(
                                List.of(line("100", "10", DiscountType.VALUE, false)),
                                BigDecimal.TEN,
                                DiscountType.VALUE,
                                TaxPolicy.NET_BASED,
                                2));
    }

    private void same(BigDecimal actual, String expected) {
        Assert.assertEquals(
                actual.compareTo(new BigDecimal(expected)), 0, actual + " != " + expected);
    }
}
