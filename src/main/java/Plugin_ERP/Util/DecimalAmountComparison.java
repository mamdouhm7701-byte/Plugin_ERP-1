/* شرح الملف: يسمح بفرق بسيط في المبلغ المتوقع ذي الكسور فقط. المبلغ الصحيح، مثل 40 أو 40.00، يظل مطابقًا تمامًا. */
package Plugin_ERP.Util;

import java.math.BigDecimal;

public final class DecimalAmountComparison {
    private DecimalAmountComparison() {}

    public static BigDecimal tolerance() {
        String configured = PropertyReader.getProperty("assertion.amount.decimal.tolerance");
        BigDecimal value = new BigDecimal(configured == null ? "0.3" : configured);
        if (value.signum() < 0)
            throw new IllegalArgumentException("Amount tolerance cannot be negative");
        return value;
    }

    public static boolean matches(BigDecimal actual, BigDecimal expected) {
        if (actual == null || expected == null) return false;
        if (expected.stripTrailingZeros().scale() <= 0) return actual.compareTo(expected) == 0;
        BigDecimal allowed = tolerance();
        if (allowed.signum() == 0) return actual.compareTo(expected) == 0;
        return actual.setScale(2, java.math.RoundingMode.HALF_UP)
                        .subtract(expected.setScale(2, java.math.RoundingMode.HALF_UP))
                        .abs()
                        .compareTo(allowed)
                <= 0;
    }

    public static boolean matches(Object actual, String expected) {
        return actual != null && matches(decimal(actual), decimal(expected));
    }

    public static BigDecimal decimal(Object value) {
        return new BigDecimal(value.toString().replace(",", ""));
    }
}
