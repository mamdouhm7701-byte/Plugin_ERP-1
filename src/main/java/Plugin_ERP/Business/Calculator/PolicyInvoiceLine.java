package Plugin_ERP.Business.Calculator;

import Plugin_ERP.Models.DiscountType;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Recipe inputs, never a line net or tax copied from the application. Fixed discount is per line.
 */
public record PolicyInvoiceLine(
        String name,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal discount,
        DiscountType discountType,
        boolean taxExempt) {
    public PolicyInvoiceLine {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Line name is required");
        Objects.requireNonNull(quantity, "Quantity is required");
        Objects.requireNonNull(price, "Price is required");
        Objects.requireNonNull(discount, "Discount is required");
        Objects.requireNonNull(discountType, "Discount type is required");
        if (quantity.signum() <= 0 || price.signum() < 0 || discount.signum() < 0)
            throw new IllegalArgumentException(
                    "Expected positive quantity and nonnegative price/discount");
        if (discountType == DiscountType.PERCENTAGE
                && discount.compareTo(new BigDecimal("100")) > 0)
            throw new IllegalArgumentException("Percentage discount exceeds 100");
        if (discountType == DiscountType.VALUE && discount.compareTo(quantity.multiply(price)) > 0)
            throw new IllegalArgumentException("Line discount exceeds line amount");
    }

    public BigDecimal amount() {
        return quantity.multiply(price);
    }

    public BigDecimal discountAmount() {
        return discountType == DiscountType.VALUE
                ? discount
                : amount().multiply(discount).movePointLeft(2);
    }
}
