/* شرح الملف: حساب مبالغ الخزينة المتوقعة من بيانات التجربة نفسها.
 */
package TestSupport;

import java.math.BigDecimal;
import java.util.Map;

public record TreasuryAmounts(BigDecimal cash, BigDecimal total, BigDecimal actual) {
    /** Only valid for an owned handover with no sales, receipts, payments or expenses. */
    public static TreasuryAmounts openingOnly(BigDecimal openingBalance, BigDecimal actual) {
        java.util.Objects.requireNonNull(openingBalance, "Opening balance is required");
        java.util.Objects.requireNonNull(actual, "Actual balance is required");
        return new TreasuryAmounts(openingBalance, openingBalance, actual);
    }

    private static final String[] IN = {
        "cashSales",
        "cashReturnPurchase",
        "cashCollectionTransaction",
        "cashIncomes",
        "openingBalance"
    };
    private static final String[] OUT = {
        "cashReturnSales", "cashPurchase", "cashSupplierPayment", "cashExpenses"
    };
    private static final String[] OTHER_IN = {
        "otherSales", "otherCollectionTransaction", "otherIncomes", "otherReturnPurchase"
    };
    private static final String[] OTHER_OUT = {
        "otherReturnSales", "otherPurchase", "otherSupplierPayment", "otherExpenses"
    };

    public static TreasuryAmounts from(Map<String, Object> totals, BigDecimal actual) {
        BigDecimal cash = sum(totals, IN).subtract(sum(totals, OUT));
        return new TreasuryAmounts(
                cash, cash.add(sum(totals, OTHER_IN)).subtract(sum(totals, OTHER_OUT)), actual);
    }

    private static BigDecimal sum(Map<String, Object> row, String[] fields) {
        BigDecimal value = BigDecimal.ZERO;
        for (String field : fields) {
            Object raw = row.get(field);
            if (raw == null) throw new AssertionError("Treasury total missing: " + field);
            value = value.add(new BigDecimal(raw.toString()));
        }
        return value;
    }

    public BigDecimal deficit() {
        return actual.subtract(cash);
    }
}
