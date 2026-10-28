/* شرح الملف: مراجعة مبالغ الضريبة والصافي المحفوظة، ضمن المطعم ونقاط البيع.
 */
package SpecialSalesTest;

import Plugin_ERP.Business.Calculator.RestaurantAdditionalTaxCalculator.Totals;
import java.math.*;
import java.util.Map;
import org.testng.Assert;

final class RestaurantTaxAssertions {
    private RestaurantTaxAssertions() {}

    static void amounts(Map<String, Object> actual, Totals expected) {
        intermediate(actual.get("totalAdditionalTaxes"), expected.additionalTax());
        intermediate(actual.get("netBeforeTaxes"), expected.beforeVat());
        intermediate(actual.get("totalTaxes"), expected.vat());
        Assert.assertEquals(
                decimal(actual.get("net")).compareTo(expected.net()), 0, "Final restaurant net");
    }

    static void intermediate(Object value, BigDecimal expected) {
        TestSupport.DecimalAmountAssertions.atDisplayedPrecision(value, expected);
    }

    static void payment(Map<String, Object> document, Object methodId, BigDecimal expected) {
        Assert.assertTrue(
                document.get("payments") instanceof java.util.List<?>,
                "Saved payments are missing");
        var owned =
                ((java.util.List<?>) document.get("payments"))
                        .stream()
                                .filter(
                                        p ->
                                                p instanceof Map<?, ?>
                                                        && methodId.equals(
                                                                ((Map<?, ?>) p)
                                                                        .get(
                                                                                "otherPaymentMethodId")))
                                .map(p -> (Map<?, ?>) p)
                                .toList();
        Assert.assertEquals(owned.size(), 1, "Expected one exact owned payment method");
        Assert.assertEquals(
                decimal(owned.getFirst().get("value")).compareTo(expected),
                0,
                "Saved payment amount differs from independently expected invoice net");
    }

    private static BigDecimal decimal(Object value) {
        Assert.assertNotNull(value, "Required restaurant amount is missing");
        return new BigDecimal(value.toString());
    }
}
