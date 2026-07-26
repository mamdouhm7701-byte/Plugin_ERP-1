package Plugin_ERP.Business.Calculator;

import Plugin_ERP.Models.DiscountType;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceCalculator {
    public static InvoiceTotals calculate(
            List<InvoiceRow> rows,
            BigDecimal discount,
            DiscountType discountType,
            BigDecimal taxPercentage) {

        InvoiceTotals totals = new InvoiceTotals();

        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceRow row : rows) {
            total = total.add(row.getLineTotal());
        }

        totals.setTotal(total);

        BigDecimal discountValue;

        if (discountType == DiscountType.VALUE) {
            discountValue = discount;
        } else {
            discountValue = total
                    .multiply(discount)
                    .divide(BigDecimal.valueOf(100));
        }

        totals.setDiscount(discountValue);

        BigDecimal beforeTax = total.subtract(discountValue);

        totals.setBeforeTax(beforeTax);

        BigDecimal tax = beforeTax
                .multiply(taxPercentage)
                .divide(BigDecimal.valueOf(100));

        totals.setTax(tax);

        BigDecimal net = beforeTax.add(tax);

        totals.setNet(net);

        return totals;
    }
}