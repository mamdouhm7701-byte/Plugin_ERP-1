package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.Models.TaxMode;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import org.testng.Assert;

/** Invoice calculation validation and Allure evidence, using the page's overridden readers. */
final class InvoiceBusinessValidation {
    private final DocumentPage owner;
    InvoiceBusinessValidation(DocumentPage owner) {
        this.owner = owner;
    }
    private static final BigDecimal TOLERANCE = new BigDecimal("0.02");

    void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {

        BigDecimal difference = expected.subtract(actual).abs();

        Assert.assertTrue(
                difference.compareTo(TOLERANCE) <= 0,
                "Expected: " + expected + " , Actual: " + actual + " , Difference: " + difference);
    }

    void attachBusinessValidation(InvoiceTotals totals) {

        String report =
                String.format(
                        """
            Business Validation

            UI Total              = %s
            Expected Total        = %s

            UI Before Tax         = %s
            Expected Before Tax   = %s

            UI Tax                = %s
            Expected Tax          = %s

            UI Net                = %s
            Expected Net          = %s
            """,
                        owner.getTotal(),
                        totals.getTotal(),
                        owner.getBeforeTax(),
                        totals.getBeforeTax(),
                        owner.getTax(),
                        totals.getTax(),
                        owner.getNet(),
                        totals.getNet());

        Allure.addAttachment("Invoice Calculation", report);
    }

    void attachReportValidation(
            ReportTotals reportTotals, InvoiceTotals totals, boolean isExclusive) {

        BigDecimal expectedReportTotal = isExclusive ? totals.getTotal() : totals.getBeforeTax();

        String report =
                String.format(
                        """
        Report Validation

        Report Total          = %s
        Expected Total        = %s

        Report Discount       = %s
        Expected Discount     = %s

        Report Before Tax     = %s
        Expected Before Tax   = %s

        Report Tax            = %s
        Expected Tax          = %s

        Report Net            = %s
        Expected Net          = %s
        """,
                        reportTotals.getTotal(),
                        expectedReportTotal,
                        reportTotals.getDiscount(),
                        totals.getDiscount(),
                        reportTotals.getBeforeTax(),
                        totals.getBeforeTax(),
                        reportTotals.getTax(),
                        totals.getTax(),
                        reportTotals.getNet(),
                        totals.getNet());

        Allure.addAttachment("Report Validation", report);
    }

    void assertInvoiceTotals(InvoiceTotals totals) {

        assertBigDecimalEquals(totals.getTotal(), owner.getTotal());
        assertBigDecimalEquals(totals.getBeforeTax(), owner.getBeforeTax());
        assertBigDecimalEquals(totals.getTax(), owner.getTax());
        assertBigDecimalEquals(totals.getNet(), owner.getNet());
    }

    void assertReportTotals(ReportTotals report, InvoiceTotals expected, TaxMode taxMode) {

        if (taxMode == TaxMode.INCLUSIVE) {
            assertBigDecimalEquals(expected.getBeforeTax(), report.getTotal());
        } else {
            assertBigDecimalEquals(expected.getTotal(), report.getTotal());
        }

        assertBigDecimalEquals(expected.getDiscount(), report.getDiscount());
        assertBigDecimalEquals(expected.getBeforeTax(), report.getBeforeTax());
        assertBigDecimalEquals(expected.getTax(), report.getTax());
        assertBigDecimalEquals(expected.getNet(), report.getNet());
    }

    void attachReportValidation(ReportTotals reportTotals, InvoiceTotals totals) {

        owner.attachReportValidation(reportTotals, totals, PropertyReader.PolicyManager.isExclusive());
    }

    void assertReportTotals(ReportTotals report, InvoiceTotals expected) {

        owner.assertReportTotals(report, expected, PropertyReader.PolicyManager.getTaxMode());
    }

    InvoiceTotals validateBusinessTotals() {

        InvoiceTotals totals = InvoiceCalculator.calculate(owner.getInvoiceRows(), owner.getDiscount());

        owner.attachBusinessValidation(totals);

        System.out.println(totals);

        owner.assertInvoiceTotals(totals);

        return totals;
    }
}
