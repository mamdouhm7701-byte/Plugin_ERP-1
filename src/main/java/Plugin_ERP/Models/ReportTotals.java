/* شرح الملف: تجميع المبالغ المقروءة من التقرير للمراجعة.
 */
package Plugin_ERP.Models;

import java.math.BigDecimal;

public class ReportTotals {

    private BigDecimal total;
    private BigDecimal discount;
    private BigDecimal beforeTax;
    private BigDecimal tax;
    private BigDecimal net;

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
}
