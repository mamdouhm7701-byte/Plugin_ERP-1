package StoreTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.StoreModel.TransBetweenStors;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import org.testng.annotations.Test;

import java.util.Map;

public class TransBetweenStoreTest extends BaseTest {

    @Test(priority=1)
    public void TransBetweenStoreTest() {

        TransBetweenStors t=new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.ClickNew();
        t.SelectFromDropDawn("مخزن الكافيه","من مخزن");
        t.SelectFromDropDawn("المخزن الرئيسى","الي مخزن");
        t.ClickAddItemsBtn();
        t.ClickPlusButtonByRow(2,2);
        t.ClickSaveForProductBtn();
        Map<String, String> dataBeforeSave = t.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        t.ClickSaveAndPrintButton();
        t.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        t.CloserReport();
        t.AssertReceiptAdded("مخزن الكافيه");

    }

    @Test(priority =2)
    public void TransBetweenStoreAddWWithBarcodeTest() {

        TransBetweenStors t=new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.ClickNew();
        t.SelectFromDropDawn("مخزن الكافيه","من مخزن");
        t.SelectFromDropDawn("المخزن الرئيسى","الي مخزن");
        t.ClickOnBarcodeRadioBtn();
        t.Search("8218648422");
        t.ClickEnter();
        t.SetQuantitiesForRows(1,10);
        t.AddNoteForStorgTrans("ملاحظات عامه");
        Map<String, String> dataBeforeSave = t.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        t.ClickSaveAndPrintButton();
        t.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        t.CloserReport();
        t.AssertReceiptAdded("مخزن الكافيه");
    }

    @Test(priority = 3)
    public void TransBetweenStoreAddWithNameTest() {
        TransBetweenStors t=new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.ClickNew();
        t.SelectFromDropDawn("مخزن الكافيه","من مخزن");
        t.SelectFromDropDawn("المخزن الرئيسى","الي مخزن");
        t.SearchAndSelectProduct("جبنه رودس");
        t.SearchAndSelectProduct("جبنه فيتا");
        t.SearchAndSelectProduct("قمح");
        t.SetQuantitiesForRows(1,50,2,50);
        t.AddNoteForStorgTrans("تست");
        Map<String, String> dataBeforeSave = t.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        t.ClickSaveAndPrintButton();
        t.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        t.CloserReport();
        t.AssertReceiptAdded("مخزن الكافيه");
    }



    @Test(priority=5)
    public void DeleteAllTest() {
        TransBetweenStors t=new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.DeleteAll();
    }


}
