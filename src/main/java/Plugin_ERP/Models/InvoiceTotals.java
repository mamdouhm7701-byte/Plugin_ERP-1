package Plugin_ERP.Models;

import java.math.BigDecimal;

public class InvoiceTotals {

    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal beforeTax = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private BigDecimal net = BigDecimal.ZERO;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getBeforeTax() {
        return beforeTax;
    }

    public void setBeforeTax(BigDecimal beforeTax) {
        this.beforeTax = beforeTax;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getNet() {
        return net;
    }

    public void setNet(BigDecimal net) {
        this.net = net;
    }

    @Override
    public String toString() {
        return "InvoiceTotals{" +
                "total=" + total +
                ", discount=" + discount +
                ", beforeTax=" + beforeTax +
                ", tax=" + tax +
                ", net=" + net +
                '}';
    }
}