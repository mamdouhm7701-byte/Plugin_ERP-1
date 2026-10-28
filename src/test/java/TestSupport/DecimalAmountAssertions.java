package TestSupport;

import java.math.*;
import org.testng.Assert;

/** Compare at the actual field precision, tolerating only insignificant browser floating noise. */
public final class DecimalAmountAssertions {
    private DecimalAmountAssertions() {}

    public static void atDisplayedPrecision(Object value, BigDecimal expected) {
        BigDecimal actual = decimal(value);
        int precision = Math.max(2, Math.min(8, actual.stripTrailingZeros().scale()));
        Assert.assertEquals(
                actual.setScale(precision, RoundingMode.HALF_UP)
                        .compareTo(expected.setScale(precision, RoundingMode.HALF_UP)),
                0,
                "Intermediate amount differs: actual=" + actual + ", expected=" + expected);
    }

    public static void exact(Object value, BigDecimal expected) {
        Assert.assertEquals(decimal(value).compareTo(expected), 0, "Final amount differs");
    }

    private static BigDecimal decimal(Object value) {
        Assert.assertNotNull(value, "Required amount is missing");
        return new BigDecimal(value.toString());
    }
}
