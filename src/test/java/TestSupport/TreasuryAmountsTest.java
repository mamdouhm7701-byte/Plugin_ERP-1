/* شرح الملف: اختبارات حساب مبالغ الخزينة المتوقعة من بيانات التجربة نفسها.
 */
package TestSupport;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TreasuryAmountsTest {
    private Map<String, Object> ledger() {
        var row = new LinkedHashMap<String, Object>();
        for (String key :
                ("cashSales cashReturnPurchase cashCollectionTransaction cashIncomes openingBalance "
                                + "cashReturnSales cashPurchase cashSupplierPayment cashExpenses otherSales otherCollectionTransaction "
                                + "otherIncomes otherReturnPurchase otherReturnSales otherPurchase otherSupplierPayment otherExpenses")
                        .split(" ")) row.put(key, 0);
        return row;
    }

    @Test
    public void openingOnlyExpectationDoesNotDependOnApplicationTotals() {
        var expected =
                TreasuryAmounts.openingOnly(new BigDecimal("1000.25"), new BigDecimal("900.10"));
        Assert.assertEquals(expected.cash(), new BigDecimal("1000.25"));
        Assert.assertEquals(expected.total(), new BigDecimal("1000.25"));
        Assert.assertEquals(expected.deficit(), new BigDecimal("-100.15"));
        var corruptedLedger = ledger();
        corruptedLedger.put("openingBalance", "9999");
        Assert.assertNotEquals(
                TreasuryAmounts.from(corruptedLedger, expected.actual()).cash(), expected.cash());
    }

    @Test
    public void reconcilesMixedMovementsAndPrintedDeficit() {
        var row = ledger();
        row.put("openingBalance", 1000);
        row.put("cashSales", "125.50");
        row.put("cashExpenses", "25.25");
        row.put("otherSales", 80);
        row.put("otherSupplierPayment", 30);
        var result = TreasuryAmounts.from(row, new BigDecimal("1100"));
        Assert.assertEquals(result.cash(), new BigDecimal("1100.25"));
        Assert.assertEquals(result.total(), new BigDecimal("1150.25"));
        Assert.assertEquals(result.deficit(), new BigDecimal("-0.25"));
    }

    @Test
    public void missingMovementIsNotSilentlyTreatedAsZero() {
        var row = ledger();
        row.remove("cashExpenses");
        Assert.expectThrows(AssertionError.class, () -> TreasuryAmounts.from(row, BigDecimal.ZERO));
    }
}
