package Purches;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import Plugin_ERP.System_Modules.Login.purches.ReturnOrderPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class ReturnOrderTest extends BaseTest {


    @Test(priority = 1)
public void AddReturnOrderTest()
{
    ReturnOrderPage returnOrderPage = new ReturnOrderPage(driver);
    returnOrderPage.NavigationHelper(Pages.RETURNPURCH);
    returnOrderPage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
    returnOrderPage.SelectByKeys("محمد ممدوح");
    returnOrderPage.SelectFromDropDawn("تست", "مندوب");
    returnOrderPage.AddNotes("test notes");
    returnOrderPage.ClickOnNameRadioBtn();
    returnOrderPage.SearchAndSelectProduct("سكر");
    returnOrderPage.SearchAndSelectProduct("كرواسون");
    returnOrderPage.SearchAndSelectProduct("دقيق");
    returnOrderPage.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
    returnOrderPage.SetValueByRow("السعر", 1, 200);
    returnOrderPage.SetValueByRow("السعر", 2, 200);
    returnOrderPage.SetValueByRow("السعر", 3, 200);
    returnOrderPage.validateInvoiceAllBusinessForReturnOrder();
    returnOrderPage.NavigationHelper(Pages.PURCHESINVOICELIST);
    returnOrderPage.AssertReceiptAdded("محمد ممدوح");
    returnOrderPage.AssertReceiptAdded("آجل");
}




    @Test(priority = 2)
    public void AddReturnOrderTestWithDiscount() {

        ReturnOrderPage returnOrderPage = new ReturnOrderPage(driver);

        returnOrderPage.NavigationHelper(Pages.RETURNPURCH);
        returnOrderPage.AddDate();
        returnOrderPage.SelectFromDropDawn("نقدي", "نوع السداد");
        returnOrderPage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        returnOrderPage.ClickShift();
        returnOrderPage.SelectByKeys("محمد ممدوح");
        returnOrderPage.SelectFromDropDawn("تست", "مندوب");
        returnOrderPage.ClickAddItemsBtn();
        returnOrderPage.ClickPlusButtonByRow(2, 2);
        returnOrderPage.ClickSaveForProductBtn();
        returnOrderPage.AddDiscountValue("10");

        // ================= Business Validation =================

        returnOrderPage.validateInvoiceAllBusinessForReturnOrder();
        // ================= Receipt Validation =================

        returnOrderPage.NavigationHelper(Pages.PURCHESINVOICELIST);

        returnOrderPage.AssertReceiptAdded("محمد ممدوح");
        returnOrderPage.AssertReceiptAdded("آجل");
    }

    @Test(priority = 3)

    public void AddPurchesBillsByBarcodTest() {

        ReturnOrderPage returnOrderPage = new ReturnOrderPage(driver);

        returnOrderPage.NavigationHelper(Pages.RETURNPURCH);
        returnOrderPage.AddDate();
        returnOrderPage.SelectFromDropDawn("نقدي", "نوع السداد");
        returnOrderPage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        returnOrderPage.ClickShift();
        returnOrderPage.SelectByKeys("محمد ممدوح");
        returnOrderPage.SelectFromDropDawn("تست", "مندوب");
        returnOrderPage.ClickOnBarcodeRadioBtn();
        returnOrderPage.Search("8218648422");
        returnOrderPage.ClickEnter();
        returnOrderPage.SetQuantitiesForRows(1, 10);
        returnOrderPage.SetValueByRow("السعر", 1, 200);
        returnOrderPage.AddDiscountPercntge("10");
        // ================= Business Validation =================
        InvoiceTotals totals = returnOrderPage.validateBusinessTotalsForReturnOrder();

        Map<String, String> dataBeforeSave =
                returnOrderPage. GetNamesQuantitiesAndPricesForReturnOrder();

        returnOrderPage.ClickOtherPay();
        returnOrderPage.SelectFromDropDawn("تست", "طريقة الدفع");
        returnOrderPage.ClickPlusToAddPayForReturnOrder();
        returnOrderPage.ClickSaveAndPrintButton();
        returnOrderPage.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
        returnOrderPage.validateReport(totals);

        returnOrderPage.NavigationHelper(Pages.PURCHESINVOICELIST);

        returnOrderPage.AssertReceiptAdded("محمد ممدوح");
        returnOrderPage.AssertReceiptAdded("نقدي");
    }

    @Test(priority = 4)
    public void AddPurchesBillsByNameTest() {

        ReturnOrderPage returnOrderPage = new ReturnOrderPage(driver);

        returnOrderPage.NavigationHelper(Pages.RETURNPURCH);
        returnOrderPage.SelectFromDropDawn("آجل", "نوع السداد");
        returnOrderPage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        returnOrderPage.ClickShift();
        returnOrderPage.SelectByKeys("محمد ممدوح");
        returnOrderPage.ClickOnNameRadioBtn();
        returnOrderPage.SearchAndSelectProduct("سكر");
        returnOrderPage.SearchAndSelectProduct("كرواسون");
        returnOrderPage.SearchAndSelectProduct("دقيق");
        returnOrderPage.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        returnOrderPage.SetValueByRow("السعر", 1, 200);
        returnOrderPage.SetValueByRow("السعر", 2, 200);
        returnOrderPage.SetValueByRow("السعر", 3, 200);
        returnOrderPage.validateInvoiceAllBusinessForReturnOrder();
        returnOrderPage.NavigationHelper(Pages.PURCHESINVOICELIST);

        returnOrderPage.AssertReceiptAdded("محمد ممدوح");
    }
    @Test(priority = 5)
    public void AddPurchesBillsWithSaveBuutonTest() {
        ReturnOrderPage returnOrderPage = new ReturnOrderPage(driver);

        returnOrderPage.NavigationHelper(Pages.RETURNPURCH);

        returnOrderPage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        returnOrderPage.ClickShift();
        returnOrderPage.SelectByKeys("محمد ممدوح");
        returnOrderPage.ClickOnNameRadioBtn();
        returnOrderPage.SearchAndSelectProduct("سكر");
        returnOrderPage.SearchAndSelectProduct("كرواسون");
        returnOrderPage.SearchAndSelectProduct("دقيق");
        returnOrderPage.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        returnOrderPage.SetValueByRow("السعر", 1, 200);
        returnOrderPage.SetValueByRow("السعر", 2, 200);
        returnOrderPage.SetValueByRow("السعر", 3, 200);
        // ================= Business Validation =================
        InvoiceTotals totals = returnOrderPage.validateBusinessTotalsForReturnOrder();

        Map<String, String> dataBeforeSave =
                returnOrderPage.GetNamesQuantitiesAndPricesForReturnOrder();
        returnOrderPage.ClickCash();
        returnOrderPage.ClickPlusToAddPayForReturnOrder();
        returnOrderPage.ClickSaveButton();
        returnOrderPage.NavigationHelper(Pages.PURCHESINVOICELIST);
        returnOrderPage.AssertReceiptAdded("محمد ممدوح");
        returnOrderPage.AssertReceiptAdded("آجل");

    }

@Test(priority = 6)
public void DeleteReturnOrderTest()
{ReturnOrderPage returnOrderPage = new ReturnOrderPage(driver);
    returnOrderPage.NavigationHelper(Pages.PURCHESINVOICELIST);
    returnOrderPage.DeleteAll();
}


    @Test(priority = 7)
    public void AddWithOutSupplier() {

        ReturnOrderPage returnOrderPage = new ReturnOrderPage(driver);
        returnOrderPage.NavigationHelper(Pages.RETURNPURCH);

        returnOrderPage.SelectFromDropDawn("مخزن الكافيه", "المخزن");

        Assert.assertTrue(returnOrderPage.AssertSupplierForReturnOrder());
    }

    @Test(priority = 8)
    public void AddWithOutProduct() {

        ReturnOrderPage returnOrderPage = new ReturnOrderPage(driver);
        returnOrderPage.NavigationHelper(Pages.RETURNPURCH);
        driver.navigate().refresh();
        returnOrderPage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        returnOrderPage.ClickShift();
        returnOrderPage.SelectByKeys("محمد ممدوح");
        returnOrderPage.SelectFromDropDawn("آجل", "نوع السداد");
        Assert.assertTrue(returnOrderPage.AssertSaveButtonIsNotChecked());
    }

}
