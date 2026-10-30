/* شرح الملف: يتأكد من قبول حدود 39.9 و40.5 عند توقع 40.2، ورفض ما خارجها أو أي فرق في مبلغ صحيح. */
package Plugin_ERP.Util;

import java.math.BigDecimal;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DecimalAmountComparisonTest {
    @Test
    public void zeroSettingRestoresExactAmountsAndNegativeToleranceIsRejected() {
        String key = "assertion.amount.decimal.tolerance", previous = System.getProperty(key);
        try {
            System.setProperty(key, "0");
            Assert.assertFalse(DecimalAmountComparison.matches("40.21", "40.2"));
            System.setProperty(key, "-0.3");
            Assert.expectThrows(
                    IllegalArgumentException.class,
                    () -> DecimalAmountComparison.matches("40.2", "40.2"));
        } finally {
            if (previous == null) System.clearProperty(key);
            else System.setProperty(key, previous);
        }
    }

    @Test
    public void decimalAmountsAcceptInclusiveFixedBounds() {
        for (String value :
                new String[] {
                    "39.8999", "39.895", "39.9", "39.99", "40", "40.2", "40.5", "40.5001", "40.5049"
                }) Assert.assertTrue(DecimalAmountComparison.matches(value, "40.2"), value);
        for (String value : new String[] {"39.8949", "40.505", "38.994", "41.406"})
            Assert.assertFalse(DecimalAmountComparison.matches(value, "40.2"), value);
    }

    @Test
    public void wholeAmountsAndZeroRemainExactRegardlessOfFormatting() {
        for (String expected : new String[] {"40", "40.0", "40.00"}) {
            Assert.assertTrue(DecimalAmountComparison.matches("40.000", expected));
            Assert.assertFalse(DecimalAmountComparison.matches("40.01", expected));
            Assert.assertFalse(DecimalAmountComparison.matches("39.99", expected));
        }
        Assert.assertFalse(DecimalAmountComparison.matches("0.01", "0"));
        Assert.assertFalse(DecimalAmountComparison.matches((BigDecimal) null, BigDecimal.ONE));
    }

    @Test
    public void negativeDecimalsUseTheSameBounds() {
        Assert.assertTrue(DecimalAmountComparison.matches("-40.5", "-40.2"));
        Assert.assertTrue(DecimalAmountComparison.matches("-39.9", "-40.2"));
        Assert.assertFalse(DecimalAmountComparison.matches("-39.89", "-40.2"));
    }
}
