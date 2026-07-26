package Purches;


import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import org.testng.annotations.Test;

import java.util.Map;

public class PurchesBillsTest extends BaseTest {

    @Test(priority = 1)
    public void PurchaseBillsTest(){
        PurchesBillsPage p=new PurchesBillsPage(driver);
        p.NavigationHelper(Pages.PURCHINVOICE);
        p.AddDate();
        p.SelectFromDropDawn("آجل","نوع السداد");
        p.SelectFromDropDawn("مخزن الكافيه","المخزن");
        p.ClickShift();
        p.SelectByKeys("محمد ممدوح");
        p.SelectFromDropDawn("تست","مندوب");
        p.ClickAddItemsBtn();
        p.ClickPlusButtonByRow(2, 2);
        p.ClickSaveForProductBtn();
        Map<String, String> dataBeforeSave = p.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        p.ClickSaveAndPrintButton();
        p.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        p.CloserReport();
        p.NavigationHelper(Pages.PURCHESINVOICELIST);
        p.AssertReceiptAdded("محمد ممدوح");


    }




















}
