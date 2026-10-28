package Plugin_ERP.Business.Calculator;

import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.BranchProfiles.BranchProfile;
import Plugin_ERP.Models.InvoiceTotals;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/** User-confirmed 15% sales arithmetic. Additional restaurant tax is excluded. */
public final class BranchInvoiceCalculator {
    /**
     * Aggregate-precision contract: keep intermediate amounts and round only the final invoice net.
     */
    public static InvoiceTotals calculateFinalRounded(
            List<PolicyInvoiceLine> lines, TaxPolicy policy, int precision) {
        Objects.requireNonNull(policy);
        if (precision < 0 || precision > 6)
            throw new IllegalArgumentException("Precision must be 0–6");
        BigDecimal total = BigDecimal.ZERO, discount = BigDecimal.ZERO, taxable = BigDecimal.ZERO;
        for (var line : List.copyOf(lines)) {
            total = total.add(line.amount());
            discount = discount.add(line.discountAmount());
            if (policy != TaxPolicy.ITEM_BASED || !line.taxExempt())
                taxable = taxable.add(line.amount().subtract(line.discountAmount()));
        }
        BigDecimal afterDiscount = total.subtract(discount);
        BigDecimal base =
                policy == TaxPolicy.PRICE_INCLUDED
                        ? afterDiscount.divide(VAT_FACTOR, java.math.MathContext.DECIMAL128)
                        : afterDiscount;
        BigDecimal tax =
                policy == TaxPolicy.PRICE_INCLUDED
                        ? afterDiscount.subtract(base)
                        : taxable.multiply(VAT_PERCENTAGE).movePointLeft(2);
        var result = new InvoiceTotals();
        result.setTotal(total);
        result.setDiscount(discount);
        result.setBeforeTax(base);
        result.setTax(tax);
        result.setNet(money(base.add(tax), precision));
        return result;
    }

    public static final BigDecimal VAT_PERCENTAGE = new BigDecimal("15");
    private static final BigDecimal VAT_FACTOR = new BigDecimal("1.15");

    /** Half-cent behavior is an explicit contract choice until demonstrated on the application. */
    public enum TaxRounding {
        INVOICE,
        LINE
    }

    private BranchInvoiceCalculator() {}

    public static InvoiceTotals calculate(
            BranchProfile profile,
            List<PolicyInvoiceLine> lines,
            int precision,
            TaxRounding rounding) {
        Objects.requireNonNull(profile, "Profile is required");
        if (profile.expected().activityType() != ActivityType.PUBLIC)
            throw new IllegalArgumentException(
                    "Financial contract currently verified for Public activity only");
        if (profile.expected().discountLevel() != DiscountLevel.ITEMS)
            throw new IllegalArgumentException(
                    "Movement discount contract still needs a reference example");
        return calculate(lines, profile.expected().taxPolicy(), precision, rounding);
    }

    public static InvoiceTotals calculate(
            List<PolicyInvoiceLine> lines, TaxPolicy policy, int precision, TaxRounding rounding) {
        lines = List.copyOf(lines);
        Objects.requireNonNull(policy, "Tax policy is required");
        Objects.requireNonNull(rounding, "Tax rounding contract is required");
        if (precision < 0 || precision > 6)
            throw new IllegalArgumentException("Precision must be 0–6");
        BigDecimal total = BigDecimal.ZERO, discount = BigDecimal.ZERO;
        BigDecimal taxable = BigDecimal.ZERO, lineTax = BigDecimal.ZERO, lineBase = BigDecimal.ZERO;
        for (var line : lines) {
            BigDecimal amount = money(line.amount(), precision);
            BigDecimal reduction = money(line.discountAmount(), precision);
            BigDecimal afterDiscount = amount.subtract(reduction);
            total = total.add(amount);
            discount = discount.add(reduction);
            // Exemption affects only ItemBased; NetBased and PriceIncluded apply to all items.
            if (policy != TaxPolicy.ITEM_BASED || !line.taxExempt()) {
                taxable = taxable.add(afterDiscount);
                lineTax =
                        lineTax.add(
                                money(
                                        afterDiscount.multiply(VAT_PERCENTAGE).movePointLeft(2),
                                        precision));
            }
            if (policy == TaxPolicy.PRICE_INCLUDED)
                lineBase =
                        lineBase.add(
                                afterDiscount.divide(VAT_FACTOR, precision, RoundingMode.HALF_UP));
        }
        BigDecimal afterDiscount = total.subtract(discount);
        var result = new InvoiceTotals();
        result.setTotal(total.setScale(precision));
        result.setDiscount(discount.setScale(precision));
        if (policy == TaxPolicy.PRICE_INCLUDED) {
            BigDecimal base =
                    rounding == TaxRounding.LINE
                            ? lineBase
                            : afterDiscount.divide(VAT_FACTOR, precision, RoundingMode.HALF_UP);
            result.setBeforeTax(base.setScale(precision));
            result.setTax(afterDiscount.subtract(base).setScale(precision));
            result.setNet(afterDiscount.setScale(precision));
        } else {
            BigDecimal tax =
                    rounding == TaxRounding.LINE
                            ? lineTax
                            : money(taxable.multiply(VAT_PERCENTAGE).movePointLeft(2), precision);
            result.setBeforeTax(afterDiscount.setScale(precision));
            result.setTax(tax.setScale(precision));
            result.setNet(afterDiscount.add(tax).setScale(precision));
        }
        return result;
    }

    /** Uniform-rate movement discount; mixed ItemBased allocation still needs a contract. */
    public static InvoiceTotals calculateMovementDiscount(
            List<PolicyInvoiceLine> lines,
            BigDecimal discount,
            Plugin_ERP.Models.DiscountType kind,
            TaxPolicy policy,
            int precision) {
        Objects.requireNonNull(policy, "Tax policy is required");
        if (policy == TaxPolicy.ITEM_BASED)
            throw new IllegalArgumentException(
                    "Movement discount allocation for ItemBased is not verified");
        if (precision < 0 || precision > 6)
            throw new IllegalArgumentException("Precision must be 0–6");
        BigDecimal total = BigDecimal.ZERO;
        for (var line : List.copyOf(lines)) {
            if (line.discount().signum() != 0)
                throw new IllegalArgumentException(
                        "Cannot combine movement and item discounts without a contract");
            total = total.add(money(line.amount(), precision));
        }
        var movement =
                new PolicyInvoiceLine("movement", BigDecimal.ONE, total, discount, kind, false);
        return calculate(List.of(movement), policy, precision, TaxRounding.INVOICE);
    }

    private static BigDecimal money(BigDecimal amount, int precision) {
        return amount.setScale(precision, RoundingMode.HALF_UP);
    }
}
