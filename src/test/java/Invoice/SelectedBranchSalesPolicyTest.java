package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.Business.Calculator.*;
import Plugin_ERP.Models.*;
import java.math.BigDecimal;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Exact owned recipes, evaluated from the selected persisted policy instead of legacy tax defaults.
 */
public final class SelectedBranchSalesPolicyTest extends SalesScenarioTest {
    @Test
    public void taxableItemsAndSupportedExemptionsSaveUsingSelectedTaxPolicy() {
        check(false);
    }

    @Test
    public void selectedDiscountLevelReducesTaxableBaseBeforeVat() {
        check(true);
    }

    private void check(boolean discounted) {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertNotEquals(
                profile.expected().activityType(),
                ActivityType.CONCRETE,
                "Concrete invoices need their own document workflow");
        boolean inclusive = profile.expected().taxPolicy() == TaxPolicy.PRICE_INCLUDED;
        String price = inclusive ? "115" : "100";
        String reduction = discounted ? (inclusive ? "11.50" : "10") : "0";
        var taxable = taxItem("policy-taxable", price, false);
        boolean exemptionSupported = profile.expected().taxPolicy() == TaxPolicy.ITEM_BASED;
        var exempt =
                discounted
                        ? null
                        : taxItem(
                                exemptionSupported ? "policy-exempt" : "policy-second-taxable",
                                price,
                                exemptionSupported);
        prepare("Credit");
        allowedItems.add(taxable.id());
        if (exempt != null) allowedItems.add(exempt.id());
        add(taxable, "picker");
        if (exempt != null) add(exempt, "picker");
        if (discounted) {
            if (profile.expected().discountLevel() == DiscountLevel.ITEMS)
                page.slSetLine(taxable.name(), "الخصم", reduction);
            else page.slDiscount(reduction, false);
        }
        var lines = new ArrayList<PolicyInvoiceLine>();
        lines.add(
                new PolicyInvoiceLine(
                        "taxable",
                        BigDecimal.ONE,
                        new BigDecimal(price),
                        new BigDecimal(reduction),
                        DiscountType.VALUE,
                        false));
        if (exempt != null)
            lines.add(
                    new PolicyInvoiceLine(
                            "exempt",
                            BigDecimal.ONE,
                            new BigDecimal(price),
                            BigDecimal.ZERO,
                            DiscountType.VALUE,
                            exemptionSupported));
        var expected =
                BranchInvoiceCalculator.calculate(
                        lines,
                        profile.expected().taxPolicy(),
                        2,
                        BranchInvoiceCalculator.TaxRounding.INVOICE);
        // Single-line movement discount has no mixed-item allocation ambiguity.
        io.qameta.allure.Allure.addAttachment(
                "Independent selected-policy expectation", "text/plain", expected.toString());
        page.slWaitNet(expected.getNet().toPlainString());
        var record = save(false);
        var stored = data.read(record);
        sameNumber(stored.get("netBeforeTaxes"), expected.getBeforeTax().toPlainString());
        sameNumber(stored.get("totalTaxes"), expected.getTax().toPlainString());
        sameNumber(stored.get("net"), expected.getNet().toPlainString());
        sameNumber(data.balance(taxable, 1), "999");
        if (exempt != null) sameNumber(data.balance(exempt, 1), "999");
    }

    private SalesTestData.Record taxItem(String key, String price, boolean exempt) {
        var values = TestSupport.PolicyItemRecipe.create(data.itemValues(), price, exempt);
        var record = data.fixture(key, "Items", values);
        var stored = data.read(record);
        Assert.assertEquals(stored.get("isTaxable"), !exempt);
        sameNumber(stored.get("itemTax"), exempt ? "0" : "15");
        return record;
    }
}
