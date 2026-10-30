/* شرح الملف: مراجعة المبالغ بسماح الكسور المحدد في الإعدادات، مع تطابق المبلغ الصحيح تمامًا.
 */
package TestSupport;

import java.math.*;
import org.testng.Assert;

public final class DecimalAmountAssertions {
    private DecimalAmountAssertions() {}

    public static void atDisplayedPrecision(Object value, BigDecimal expected) {
        matchesExpected(value, expected);
    }

    public static void matchesExpected(Object value, BigDecimal expected) {
        Assert.assertTrue(
                Plugin_ERP.Util.DecimalAmountComparison.matches(decimal(value), expected),
                "المبلغ غير مطابق: المتوقع "
                        + expected
                        + "، الفعلي "
                        + value
                        + "؛ السماح للعشري ±"
                        + Plugin_ERP.Util.DecimalAmountComparison.tolerance()
                        + "، والمبلغ الصحيح مطابق تمامًا");
    }

    @io.qameta.allure.Step("مراجعة المبلغ: المتوقع {expected}، الفعلي {value}؛ السماح للعشري فقط")
    public static void amount(Object value, String expected) {
        matchesExpected(value, new BigDecimal(expected.replace(",", "")));
    }

    private static BigDecimal decimal(Object value) {
        Assert.assertNotNull(value, "Required amount is missing");
        return Plugin_ERP.Util.DecimalAmountComparison.decimal(value);
    }
}
