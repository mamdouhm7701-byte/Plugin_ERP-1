/* شرح الملف: يراجع أرقام الفواتير المتوقعة والتمييز بين الصافي115 وأرقام مختلفة في الإيصال دون الاتصال بالبرنامج. */
package SpecialSalesTest;

import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import TestSupport.InvoiceReviewRecipe;
import java.math.BigDecimal;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class InvoiceReviewRecipeTest {
    @Test
    public void exemptionOnlyAppliesUnderItemPolicy() {
        Assert.assertTrue(InvoiceReviewRecipe.exempt(TaxPolicy.ITEM_BASED, true));
        Assert.assertFalse(InvoiceReviewRecipe.exempt(TaxPolicy.NET_BASED, true));
        Assert.assertFalse(InvoiceReviewRecipe.exempt(TaxPolicy.PRICE_INCLUDED, true));
        InvoiceReviewRecipe.verify(
                Map.of("netBeforeTaxes", 200, "totalTaxes", 30, "net", 230), false, 2);
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        InvoiceReviewRecipe.verify(
                                Map.of("netBeforeTaxes", 200, "totalTaxes", 15, "net", 215),
                                false,
                                2));
    }

    @Test
    public void receiptRequiresExactNumberRatherThanSubstring() {
        Assert.assertTrue(
                SpecialInvoiceTaxReview.containsAmount("الصافي 115.00", new BigDecimal("115")));
        Assert.assertFalse(
                SpecialInvoiceTaxReview.containsAmount("الصافي 1115.00", new BigDecimal("115")));
        Assert.assertFalse(
                SpecialInvoiceTaxReview.containsAmount("الصافي 115.50", new BigDecimal("115")));
    }

    @Test
    public void reviewedItemHasBothStoresAndItsOwnBarcode() {
        var defaults =
                Map.<String, Object>of(
                        "mainUnitBarcode",
                        "new-review-barcode",
                        "stores",
                        java.util.List.of(Map.of("storeId", "first")));
        var reference =
                Map.<String, Object>of(
                        "mainUnitBarcode",
                        "existing-barcode",
                        "stores",
                        java.util.List.of(
                                Map.of(
                                        "storeId",
                                        "first",
                                        "currentBalance",
                                        0,
                                        "openingBalance",
                                        0),
                                Map.of(
                                        "storeId",
                                        "second",
                                        "currentBalance",
                                        0,
                                        "openingBalance",
                                        0)));
        var result = InvoiceReviewRecipe.stockedItem(defaults, reference, "100", false);
        Assert.assertEquals(result.get("mainUnitBarcode"), "new-review-barcode");
        var stores = (java.util.List<?>) result.get("stores");
        Assert.assertEquals(stores.size(), 2);
        Assert.assertEquals(
                TestSupport.FixtureStockBalance.read(Map.of("stores", stores), () -> "first"),
                1000);
        Assert.assertEquals(
                TestSupport.FixtureStockBalance.read(Map.of("stores", stores), () -> "second"), 0);
        Assert.assertEquals(((java.util.List<?>) defaults.get("stores")).size(), 1);
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> InvoiceReviewRecipe.stockedItem(defaults, defaults, "100", false));
    }

    @Test
    public void exemptCasesRunOnlyOnItemTaxWithoutRepeatingTaxableCases() {
        var actions = java.util.List.of("إضافة", "تعديل", "حذف", "طباعة");
        Assert.assertEquals(
                InvoiceReviewRecipe.actions(TaxPolicy.NET_BASED, actions, false).length, 4);
        Assert.assertEquals(
                InvoiceReviewRecipe.actions(TaxPolicy.PRICE_INCLUDED, actions, true).length, 8);
        Assert.assertEquals(
                InvoiceReviewRecipe.actions(TaxPolicy.ITEM_BASED, actions, false).length, 8);
        Assert.assertEquals(
                InvoiceReviewRecipe.actions(TaxPolicy.ITEM_BASED, actions, true).length, 12);
        Assert.assertTrue(
                java.util.Arrays.stream(
                                InvoiceReviewRecipe.actions(TaxPolicy.NET_BASED, actions, true))
                        .noneMatch(row -> (boolean) row[1]));
    }

    @Test
    public void customStockRecipeCreatesBothStoreRowsWithoutCreatingAnUnusedItem() {
        var first =
                Map.<String, Object>of(
                        "storeId",
                        "main",
                        "openingBalance",
                        0,
                        "currentBalance",
                        0,
                        "costPrice",
                        80);
        var defaults =
                Map.<String, Object>of(
                        "stores", java.util.List.of(first), "mainUnitBarcode", "unique");
        var result = InvoiceReviewRecipe.stockedItemForStores(defaults, "secondary", "115", false);
        Assert.assertEquals(TestSupport.FixtureStockBalance.read(result, () -> "main"), 1000);
        Assert.assertEquals(TestSupport.FixtureStockBalance.read(result, () -> "secondary"), 0);
        Assert.assertEquals(result.get("mainUnitBarcode"), "unique");
        Assert.assertEquals(((java.util.List<?>) defaults.get("stores")).size(), 1);
    }
}
