package Purches;


import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Models.*;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;

import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PurchesBillsTest extends BaseTest {

    @Test(priority = 1)
    public void PurchaseBillsTest() {

        PurchesBillsPage p = new PurchesBillsPage(driver);

        p.NavigationHelper(Pages.PURCHINVOICE);
        p.AddDate();
        p.SelectFromDropDawn("آجل", "نوع السداد");
        p.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        p.ClickShift();
        p.SelectByKeys("محمد ممدوح");
        p.SelectFromDropDawn("تست", "مندوب");
        p.ClickAddItemsBtn();
        p.ClickPlusButtonByRow(2, 2);
        p.ClickSaveForProductBtn();
        p.AddDiscountValue("10");

// ================= Business Validation =================

        TaxMode taxMode = TaxMode.EXCLUSIVE;
        boolean isExclusive = taxMode == TaxMode.EXCLUSIVE;

        List<InvoiceRow> rows = p.getInvoiceRows();

        InvoiceTotals totals = InvoiceCalculator.calculate(
                rows,
                p.getDiscount(),
                DiscountType.VALUE,
                new BigDecimal("15"),
                taxMode,
                2
        );

        p.attachBusinessValidation(totals);

        System.out.println(totals);

        p.assertInvoiceTotals(totals);

// ================= Report Validation =================

        Map<String, String> dataBeforeSave = p.GetNamesQuantitiesAndPrices();

        p.ClickSaveAndPrintButton();

        p.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);

        ReportTotals reportTotals = p.getReportTotals();

        p.attachReportValidation(reportTotals, totals, isExclusive);

        p.assertReportTotals(reportTotals, totals, taxMode);

        p.CloserReport();

        // ================= Receipt Validation =================

        p.NavigationHelper(Pages.PURCHESINVOICELIST);

        p.AssertReceiptAdded("محمد ممدوح");
    }


@Test
public void AddPurchesBillsByBarcodTest() {

    PurchesBillsPage p = new PurchesBillsPage(driver);

    p.NavigationHelper(Pages.PURCHINVOICE);
    p.AddDate();
    p.SelectFromDropDawn("آجل", "نوع السداد");
    p.SelectFromDropDawn("مخزن الكافيه", "المخزن");
    p.ClickShift();
    p.SelectByKeys("محمد ممدوح");
    p.SelectFromDropDawn("تست", "مندوب");
    p.ClickOnBarcodeRadioBtn();
    p.Search("8218648422");
    p.ClickEnter();
    p.SetQuantitiesForRows( 1, 10);


// ================= Business Validation =================

    TaxMode taxMode = TaxMode.EXCLUSIVE;
    boolean isExclusive = taxMode == TaxMode.EXCLUSIVE;

    List<InvoiceRow> rows = p.getInvoiceRows();

    InvoiceTotals totals = InvoiceCalculator.calculate(
            rows,
            p.getDiscount(),
            DiscountType.VALUE,
            new BigDecimal("15"),
            taxMode,
            2
    );

    p.attachBusinessValidation(totals);

    System.out.println(totals);

    p.assertInvoiceTotals(totals);

// ================= Report Validation =================

    Map<String, String> dataBeforeSave = p.GetNamesQuantitiesAndPrices();

    p.ClickSaveAndPrintButton();

    p.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);

    ReportTotals reportTotals = p.getReportTotals();

    p.attachReportValidation(reportTotals, totals, isExclusive);

    p.assertReportTotals(reportTotals, totals, taxMode);

    p.CloserReport();

    // ================= Receipt Validation =================

    p.NavigationHelper(Pages.PURCHESINVOICELIST);

    p.AssertReceiptAdded("محمد ممدوح");
}


















@Test
    public void DeleteAll(){

    PurchesBillsPage p = new PurchesBillsPage(driver);

    p.NavigationHelper(Pages.PURCHESINVOICELIST);
    p.DeleteAll();
}








}
