package Plugin_ERP.Models;

import java.math.BigDecimal;

public class InvoiceRow {

    private final String itemName;
    private final BigDecimal quantity;
    private final BigDecimal price;

    public InvoiceRow(String itemName, BigDecimal quantity, BigDecimal price) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getLineTotal() {
        return quantity.multiply(price);
    }
}
