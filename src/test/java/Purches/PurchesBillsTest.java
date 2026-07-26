package Purches;


import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Models.DiscountType;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
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

        List<InvoiceRow> rows = p.getInvoiceRows();

        InvoiceTotals totals = InvoiceCalculator.calculate(
                rows,
                p.getDiscount(),
                DiscountType.VALUE,
                new BigDecimal("15")
        );
        p.attachBusinessValidation(p, totals);
        p.assertInvoiceTotals(p, totals);

        // ================= Report Validation =================

        Map<String, String> dataBeforeSave = p.GetNamesQuantitiesAndPrices();

        p.ClickSaveAndPrintButton();

        p.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);

        ReportTotals reportTotals = p.getReportTotals();
        p. attachReportValidation(reportTotals, totals);
        p.assertReportTotals(reportTotals, totals);

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
