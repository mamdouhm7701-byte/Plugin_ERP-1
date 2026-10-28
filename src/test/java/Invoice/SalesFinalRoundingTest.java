/* شرح الملف: اختبارات تقريب صافي الفاتورة في النهاية فقط: تقريب صافي الفاتورة في النهاية فقط.
 */
package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.Business.Calculator.*;
import Plugin_ERP.Models.DiscountType;
import TestSupport.DecimalAmountAssertions;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.*;

public final class SalesFinalRoundingTest extends SalesScenarioTest {
    @DataProvider
    public Object[][] itemCounts() {
        return new Object[][] {{1}, {2}};
    }

    @Test(dataProvider = "itemCounts")
    public void savedInvoiceRoundsFinalNetWithoutRoundingEveryInput(int count) {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertTrue(
                Set.of(ActivityType.PUBLIC, ActivityType.CONSTRUCTION)
                        .contains(profile.expected().activityType()));
        prepare("Credit");
        var items = new ArrayList<SalesTestData.Record>();
        var lines = new ArrayList<PolicyInvoiceLine>();
        String price =
                profile.expected().taxPolicy() == TaxPolicy.PRICE_INCLUDED ? "28.7914" : "25.036";
        for (int index = 0; index < count; index++) {
            var recipe = TestSupport.PolicyItemRecipe.create(data.itemValues(), price, false);
            for (Object store : (List<?>) recipe.get("stores"))
                ((Map<String, Object>) store).put("costPrice", 10);
            var owned = data.fixture("precision-item-" + index, "Items", recipe);
            sameNumber(data.read(owned).get("salesPrice"), price);
            allowedItems.add(owned.id());
            items.add(owned);
            lines.add(
                    new PolicyInvoiceLine(
                            "precision-" + index,
                            BigDecimal.ONE,
                            new BigDecimal(price),
                            BigDecimal.ZERO,
                            DiscountType.VALUE,
                            false));
        }
        item = items.getFirst();
        page.slCapture(marker, data.store(1).id(), item.id(), "ItemTransactions-Sales");
        for (var owned : items) add(owned, "picker");
        var expected =
                BranchInvoiceCalculator.calculateFinalRounded(
                        lines, profile.expected().taxPolicy(), 2);
        Allure.addAttachment(
                "Independent final-only sales rounding",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "price",
                                        price,
                                        "items",
                                        count,
                                        "beforeTax",
                                        expected.getBeforeTax(),
                                        "vat",
                                        expected.getTax(),
                                        "net",
                                        expected.getNet(),
                                        "actual",
                                        page.slDocument()))
                        .toString(2),
                ".json");
        page.slWaitNet(expected.getNet().toPlainString());
        DecimalAmountAssertions.atDisplayedPrecision(
                page.slDocument().get("netBeforeTaxes"), expected.getBeforeTax());
        DecimalAmountAssertions.atDisplayedPrecision(
                page.slDocument().get("totalTaxes"), expected.getTax());
        var source = save(false);
        var stored = data.read(source);
        DecimalAmountAssertions.exact(stored.get("net"), expected.getNet());
        for (var owned : items) sameNumber(data.balance(owned, 1), "999");
    }
}
