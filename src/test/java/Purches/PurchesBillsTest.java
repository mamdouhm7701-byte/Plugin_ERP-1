package Purches;


import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Models.*;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
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

        // ================= Business Validation =================

        TaxMode taxMode = TaxMode.INCLUSIVE;

        List<InvoiceRow> rows = p.getInvoiceRows();
        for (InvoiceRow row : rows) {
            System.out.println("----------------");
            System.out.println("Qty       = " + row.getQuantity());
            System.out.println("Price     = " + row.getPrice());
            System.out.println("LineTotal = " + row.getLineTotal());
        }
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

        System.out.println("----------------");

        System.out.println("Calc Total     = " + totals.getTotal());
        System.out.println("Calc BeforeTax = " + totals.getBeforeTax());
        System.out.println("Calc Tax       = " + totals.getTax());
        System.out.println("Calc Net       = " + totals.getNet());

        ReportTotals reportTotals = p.getReportTotals();

        p.attachReportValidation(reportTotals, totals);

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
