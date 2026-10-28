/* شرح الملف: مراجعة مبالغ الضريبة والصافي المحفوظة، ضمن المطعم ونقاط البيع.
 */
package SpecialSalesTest;

import java.math.BigDecimal;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class RestaurantTaxAssertionsTest {
    @Test
    public void verifiesExactPaymentIdentityAndValue() {
        RestaurantTaxAssertions.payment(
                java.util.Map.of(
                        "payments",
                        java.util.List.of(
                                java.util.Map.of(
                                        "otherPaymentMethodId", "owned", "value", "40.25"))),
                "owned",
                new BigDecimal("40.25"));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        RestaurantTaxAssertions.payment(
                                java.util.Map.of(
                                        "payments",
                                        java.util.List.of(
                                                java.util.Map.of(
                                                        "otherPaymentMethodId",
                                                        "foreign",
                                                        "value",
                                                        "40.25"))),
                                "owned",
                                new BigDecimal("40.25")));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        RestaurantTaxAssertions.payment(
                                java.util.Map.of(
                                        "payments",
                                        java.util.List.of(
                                                java.util.Map.of(
                                                        "otherPaymentMethodId",
                                                        "owned",
                                                        "value",
                                                        "40.24"))),
                                "owned",
                                new BigDecimal("40.25")));
    }

    @Test
    public void acceptsTwoOrMoreIntermediateDecimalsWithoutRoundingEveryInput() {
        RestaurantTaxAssertions.intermediate("50.07", new BigDecimal("50.072"));
        RestaurantTaxAssertions.intermediate("50.072", new BigDecimal("50.072"));
        RestaurantTaxAssertions.intermediate("50.072000000000004", new BigDecimal("50.072"));
    }

    @Test
    public void rejectsPrematurelyRoundedLineSumAndIncorrectExtraDigits() {
        Assert.expectThrows(
                AssertionError.class,
                () -> RestaurantTaxAssertions.intermediate("50.08", new BigDecimal("50.072")));
        Assert.expectThrows(
                AssertionError.class,
                () -> RestaurantTaxAssertions.intermediate("50.073", new BigDecimal("50.072")));
    }
}
