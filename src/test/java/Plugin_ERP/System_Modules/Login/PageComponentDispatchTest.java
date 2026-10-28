/* شرح الملف: اختبار سلامة Extraction must retain overridden readers, assertion policies and
 * rounding tolerance.
 */
package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.Models.TaxMode;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PageComponentDispatchTest {
    private static WebDriver noBrowser() {
        return (WebDriver)
                Proxy.newProxyInstance(
                        PageComponentDispatchTest.class.getClassLoader(),
                        new Class<?>[] {WebDriver.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("toString")) return "no-browser";
                            throw new AssertionError(
                                    "Unexpected browser access: " + method.getName());
                        });
    }

    private static final class PurchaseReaders extends AbstractForPurchases {
        int assertions;
        int attachments;

        PurchaseReaders() {
            super(noBrowser());
        }

        @Override
        public List<InvoiceRow> getInvoiceRows() {
            return List.of(new InvoiceRow("owned", new BigDecimal("2"), new BigDecimal("73.25")));
        }

        @Override
        public BigDecimal getDiscount() {
            return BigDecimal.ZERO;
        }

        @Override
        public void attachBusinessValidation(InvoiceTotals totals) {
            attachments++;
        }

        @Override
        public void assertInvoiceTotals(InvoiceTotals totals) {
            assertions++;
        }
    }

    @Test
    public void purchaseCalculationRetainsSubclassReadersAndPolicies() {
        PurchaseReaders page = new PurchaseReaders();
        InvoiceTotals totals = page.validateBusinessTotals();
        Assert.assertEquals(totals.getTotal().compareTo(new BigDecimal("146.50")), 0);
        Assert.assertEquals(page.assertions, 1);
        Assert.assertEquals(page.attachments, 1);
    }

    @DataProvider
    public Object[][] tolerances() {
        return new Object[][] {
            {"100.02", true}, {"99.98", true}, {"100.021", false}, {"99.979", false}
        };
    }

    @Test(dataProvider = "tolerances")
    public void invoiceAssertionsRetainRoundingTolerance(String actualNet, boolean accepted) {
        AbstractForPurchases page =
                new AbstractForPurchases(noBrowser()) {
                    @Override
                    public BigDecimal getTotal() {
                        return new BigDecimal("100");
                    }

                    @Override
                    public BigDecimal getBeforeTax() {
                        return new BigDecimal("100");
                    }

                    @Override
                    public BigDecimal getTax() {
                        return BigDecimal.ZERO;
                    }

                    @Override
                    public BigDecimal getNet() {
                        return new BigDecimal(actualNet);
                    }
                };
        InvoiceTotals expected = totals();
        if (accepted) page.assertInvoiceTotals(expected);
        else Assert.expectThrows(AssertionError.class, () -> page.assertInvoiceTotals(expected));
    }

    @DataProvider
    public Object[][] taxModes() {
        return new Object[][] {{TaxMode.INCLUSIVE}, {TaxMode.EXCLUSIVE}};
    }

    @Test(dataProvider = "taxModes")
    public void reportAssertionsRetainTaxModeAndDetectWrongNet(TaxMode mode) {
        AbstractForPurchases page = new AbstractForPurchases(noBrowser());
        InvoiceTotals expected = totals();
        expected.setTotal(new BigDecimal("115"));
        ReportTotals report = new ReportTotals();
        report.setTotal(mode == TaxMode.INCLUSIVE ? expected.getBeforeTax() : expected.getTotal());
        report.setDiscount(expected.getDiscount());
        report.setBeforeTax(expected.getBeforeTax());
        report.setTax(expected.getTax());
        report.setNet(expected.getNet());
        page.assertReportTotals(report, expected, mode);
        report.setNet(new BigDecimal("999"));
        Assert.expectThrows(
                AssertionError.class, () -> page.assertReportTotals(report, expected, mode));
    }

    private static InvoiceTotals totals() {
        InvoiceTotals expected = new InvoiceTotals();
        expected.setTotal(new BigDecimal("100"));
        expected.setBeforeTax(new BigDecimal("100"));
        expected.setNet(new BigDecimal("100"));
        return expected;
    }
}
