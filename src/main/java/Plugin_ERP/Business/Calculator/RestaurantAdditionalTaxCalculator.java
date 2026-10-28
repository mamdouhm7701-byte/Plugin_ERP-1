/* شرح الملف: حساب إضافية المطعم وحدها الأدنى وضريبة القيمة المضافة بعد الخصم.
 */
package Plugin_ERP.Business.Calculator;

import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import java.math.*;
import java.util.*;

public final class RestaurantAdditionalTaxCalculator {
    public record Line(
            BigDecimal quantity,
            BigDecimal price,
            boolean subjectToAdditionalTax,
            BigDecimal discount,
            boolean vatExempt) {
        public Line(BigDecimal quantity, BigDecimal price, boolean subjectToAdditionalTax) {
            this(quantity, price, subjectToAdditionalTax, BigDecimal.ZERO, false);
        }

        public Line(
                BigDecimal quantity,
                BigDecimal price,
                boolean subjectToAdditionalTax,
                BigDecimal discount) {
            this(quantity, price, subjectToAdditionalTax, discount, false);
        }

        public Line {
            Objects.requireNonNull(quantity);
            Objects.requireNonNull(price);
            Objects.requireNonNull(discount);
            if (discount.signum() < 0 || discount.compareTo(quantity.multiply(price)) > 0)
                throw new IllegalArgumentException("Invalid line discount");
            if (quantity.signum() <= 0 || price.signum() < 0)
                throw new IllegalArgumentException(
                        "Expected positive quantity and nonnegative price");
        }
    }

    public record Totals(
            BigDecimal originalBeforeVat,
            BigDecimal additionalTax,
            BigDecimal beforeVat,
            BigDecimal vat,
            BigDecimal net) {}

    private static final BigDecimal MINIMUM_ADDITIONAL_TAX_PER_UNIT = new BigDecimal("25");

    private RestaurantAdditionalTaxCalculator() {}

    public static Totals calculate(
            List<Line> lines,
            TaxPolicy policy,
            boolean enabled,
            BigDecimal additionalPercentage,
            int precision) {
        Objects.requireNonNull(policy);
        Objects.requireNonNull(additionalPercentage);
        if (precision < 0 || precision > 6 || additionalPercentage.signum() < 0)
            throw new IllegalArgumentException("Invalid precision or additional tax percentage");
        BigDecimal original = BigDecimal.ZERO,
                additional = BigDecimal.ZERO,
                taxable = BigDecimal.ZERO;
        for (var line : List.copyOf(lines)) {
            BigDecimal amount = line.quantity().multiply(line.price()).subtract(line.discount());
            BigDecimal base =
                    policy == TaxPolicy.PRICE_INCLUDED
                            ? amount.divide(new BigDecimal("1.15"), MathContext.DECIMAL128)
                            : amount;
            original = original.add(base);
            boolean applied =
                    enabled && line.subjectToAdditionalTax() && additionalPercentage.signum() > 0;
            BigDecimal extra =
                    applied
                            ? base.multiply(additionalPercentage)
                                    .movePointLeft(2)
                                    .max(MINIMUM_ADDITIONAL_TAX_PER_UNIT.multiply(line.quantity()))
                            : BigDecimal.ZERO;
            additional = additional.add(extra);
            if (policy != TaxPolicy.ITEM_BASED || !line.vatExempt() || applied)
                taxable = taxable.add(base).add(extra);
        }
        BigDecimal beforeVat = original.add(additional);
        BigDecimal vat = taxable.multiply(new BigDecimal("0.15"));
        return new Totals(
                original, additional, beforeVat, vat, money(beforeVat.add(vat), precision));
    }

    private static BigDecimal money(BigDecimal value, int precision) {
        return value.setScale(precision, RoundingMode.HALF_UP);
    }
}
