/* شرح الملف: اختبارات مطابقة القيد المحفوظ مع الحسابات والمبالغ المتوقعة.
 */
package TestSupport;

import Plugin_ERP.BranchProfiles.BranchPolicies.StocktakingMethod;
import Plugin_ERP.Business.Calculator.InventoryPostingExpectation;
import java.math.BigDecimal;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class OwnedPostingReconciliationTest {
    private final InventoryPostingExpectation.Accounts accounts =
            new InventoryPostingExpectation.Accounts(
                    "customer", "sales", "vat", "additional", "stock", "cost");

    private Map<String, BigDecimal> expected(StocktakingMethod method, boolean returned) {
        return InventoryPostingExpectation.signedBalances(
                new InventoryPostingExpectation.Sale(
                        new BigDecimal("100"),
                        BigDecimal.ZERO,
                        new BigDecimal("15"),
                        new BigDecimal("80"),
                        returned),
                method,
                accounts);
    }

    private List<OwnedPostingReconciliation.Entry> entries(Map<String, BigDecimal> balances) {
        return balances.entrySet().stream()
                .map(
                        e ->
                                new OwnedPostingReconciliation.Entry(
                                        "owned",
                                        e.getKey(),
                                        e.getValue().max(BigDecimal.ZERO),
                                        e.getValue().negate().max(BigDecimal.ZERO)))
                .toList();
    }

    @Test
    public void periodicAndContinuousHaveDifferentCostPostingContracts() {
        var periodic = expected(StocktakingMethod.PERIODIC, false);
        var continuous = expected(StocktakingMethod.CONTINUOUS, false);
        Assert.assertFalse(periodic.containsKey("stock"));
        Assert.assertEquals(continuous.get("stock").compareTo(new BigDecimal("-80")), 0);
        Assert.assertEquals(continuous.get("cost").compareTo(new BigDecimal("80")), 0);
        OwnedPostingReconciliation.verify("owned", periodic, entries(periodic));
        OwnedPostingReconciliation.verify("owned", continuous, entries(continuous));
    }

    @Test
    public void returnReversesEachOriginalAccountAndCostAmount() {
        var sale = expected(StocktakingMethod.CONTINUOUS, false);
        var returned = expected(StocktakingMethod.CONTINUOUS, true);
        sale.forEach(
                (account, value) ->
                        Assert.assertEquals(returned.get(account).compareTo(value.negate()), 0));
        OwnedPostingReconciliation.verify("owned", returned, entries(returned));
    }

    @Test
    public void balancedButWrongAccountOrCostCannotPass() {
        var expected = expected(StocktakingMethod.CONTINUOUS, false);
        var wrong = new HashMap<>(expected);
        wrong.put("wrong-sales", wrong.remove("sales"));
        var wrongAccount = wrong;
        Assert.expectThrows(
                AssertionError.class,
                () -> OwnedPostingReconciliation.verify("owned", expected, entries(wrongAccount)));
        wrong = new HashMap<>(expected);
        wrong.put("stock", new BigDecimal("-79"));
        wrong.put("cost", new BigDecimal("79"));
        var wrongCost = wrong;
        Assert.expectThrows(
                AssertionError.class,
                () -> OwnedPostingReconciliation.verify("owned", expected, entries(wrongCost)));
    }

    @Test
    public void matchingForeignSourceAndEmptyResultsCannotPass() {
        var expected = expected(StocktakingMethod.PERIODIC, false);
        Assert.expectThrows(
                AssertionError.class,
                () -> OwnedPostingReconciliation.verify("foreign", expected, entries(expected)));
        Assert.expectThrows(
                AssertionError.class,
                () -> OwnedPostingReconciliation.verify("owned", expected, List.of()));
    }
}
