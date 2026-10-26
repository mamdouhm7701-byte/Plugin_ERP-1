package Plugin_ERP.Business.Calculator;

import Plugin_ERP.Models.*;
import Plugin_ERP.Util.PropertyReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InvoiceCalculator {

    public static InvoiceTotals calculate(
            List<InvoiceRow> rows,
            BigDecimal discount,
            DiscountType discountType,
            BigDecimal taxPercentage,
            TaxMode taxMode,
            int precision) {

        switch (taxMode) {
            case EXCLUSIVE:
                return calculateExclusive(rows, discount, discountType, taxPercentage, precision);

            case INCLUSIVE:
                return calculateInclusive(rows, discount, discountType, taxPercentage, precision);

            default:
                throw new IllegalArgumentException("Unsupported TaxMode");
        }
    }

    private static BigDecimal round(BigDecimal value, int precision) {
        return value.setScale(precision, RoundingMode.HALF_UP);
    }

    private static InvoiceTotals calculateExclusive(
            List<InvoiceRow> rows,
            BigDecimal discount,
            DiscountType discountType,
            BigDecimal taxPercentage,
            int precision) {

        InvoiceTotals totals = new InvoiceTotals();

        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceRow row : rows) {
            total = total.add(row.getLineTotal());
        }

        total = round(total, precision);
        totals.setTotal(total);

        BigDecimal discountValue;

        if (discountType == DiscountType.VALUE) {
            discountValue = discount;
        } else {
            discountValue =
                    total.multiply(discount)
                            .divide(BigDecimal.valueOf(100), precision + 4, RoundingMode.HALF_UP);
        }

        discountValue = round(discountValue, precision);
        totals.setDiscount(discountValue);

        BigDecimal beforeTax = round(total.subtract(discountValue), precision);
        totals.setBeforeTax(beforeTax);

        BigDecimal tax =
                beforeTax
                        .multiply(taxPercentage)
                        .divide(BigDecimal.valueOf(100), precision + 4, RoundingMode.HALF_UP);

        tax = round(tax, precision);
        totals.setTax(tax);

        BigDecimal net = round(beforeTax.add(tax), precision);
        totals.setNet(net);

        return totals;
    }

    private static InvoiceTotals calculateInclusive(
            List<InvoiceRow> rows,
            BigDecimal discount,
            DiscountType discountType,
            BigDecimal taxPercentage,
            int precision) {

        InvoiceTotals totals = new InvoiceTotals();

        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceRow row : rows) {
            total = total.add(row.getLineTotal());
        }

        total = round(total, precision);
        totals.setTotal(total);

        BigDecimal discountValue;

        if (discountType == DiscountType.VALUE) {
            discountValue = discount;
        } else {
            discountValue =
                    total.multiply(discount)
                            .divide(BigDecimal.valueOf(100), precision + 4, RoundingMode.HALF_UP);
        }

        discountValue = round(discountValue, precision);
        totals.setDiscount(discountValue);

        BigDecimal net = round(total.subtract(discountValue), precision);
        totals.setNet(net);

        BigDecimal divisor =
                BigDecimal.ONE.add(
                        taxPercentage.divide(
                                BigDecimal.valueOf(100), precision + 4, RoundingMode.HALF_UP));

        BigDecimal beforeTax = net.divide(divisor, precision, RoundingMode.HALF_UP);

        beforeTax = round(beforeTax, precision);
        totals.setBeforeTax(beforeTax);

        BigDecimal tax = round(net.subtract(beforeTax), precision);
        totals.setTax(tax);

        return totals;
    }

    public static InvoiceTotals calculate(
            List<InvoiceRow> rows,
            BigDecimal discount,
            DiscountType discountType,
            BigDecimal taxPercentage,
            int precision) {

        return calculate(
                rows,
                discount,
                discountType,
                taxPercentage,
                PropertyReader.PolicyManager.getTaxMode(),
                precision);
    }

    public static InvoiceTotals calculate(List<InvoiceRow> rows, BigDecimal discount) {

        return calculate(
                rows,
                discount,
                PropertyReader.PolicyManager.getDiscountType(),
                PropertyReader.PolicyManager.getTaxPercentage(),
                PropertyReader.PolicyManager.getTaxMode(),
                PropertyReader.PolicyManager.getPrecision());
    }
}
