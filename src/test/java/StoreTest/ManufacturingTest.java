package StoreTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.StoreModel.ManufacturingPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class ManufacturingTest extends BaseTest {





@Test(priority = 1)
    public void AddManufacturingTest()  {
    ManufacturingPage m = new ManufacturingPage(driver);
    m.NavigationHelper(Pages.MANUFACTURING);
    m.SelectFromDropDawn("مخزن الكافيه","المخزن");
    m.ClickAddItemsBtn();
    m.ClickPlusButtonByRow(1,2);
    m.ClickSaveForProductBtn();
    Map<String, String> dataBeforeSave = m.GetNamesAndQuantities();
    System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
    m.ClickSaveAndPrintButton();
    m.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
    m.NavigationHelper(Pages.ORDERS);
    m.AssertReceiptAdded("إذن تصنيع");

}


@Test(priority = 2)
    public void AddManufacturingByBarcodeTest()  {


    ManufacturingPage m = new ManufacturingPage(driver);
    m.NavigationHelper(Pages.MANUFACTURING);
    m.SelectFromDropDawn("مخزن الكافيه","المخزن");
    m.ClickOnBarcodeRadioBtn();
    m.Search("7291659936");
    m.ClickEnter();
    m.ClickSaveButton();

    m.NavigationHelper(Pages.ORDERS);
    m.AssertReceiptAdded("إذن تصنيع");

}

@Test(priority = 3)
public void AddManufacturingByNameTest()  {
    ManufacturingPage m = new ManufacturingPage(driver);
    m.NavigationHelper(Pages.MANUFACTURING);
    m.SelectFromDropDawn("مخزن الكافيه","المخزن");
    m.ClickOnNameRadioBtn();
    m.SearchAndSelectProduct("كرواسون");
    m.ClickSaveButton();
    m.NavigationHelper(Pages.ORDERS);
    m.AssertReceiptAdded("إذن تصنيع");
}


@Test(priority = 4)
    public void DeleteManufacturingByBarcodeTest()  {
    ManufacturingPage m = new ManufacturingPage(driver);
    m.NavigationHelper(Pages.ORDERS);
    m.DeleteByName("إذن تصنيع");
}

@Test(priority = 5)
    public void AssertStoreRequired()
{
    ManufacturingPage m = new ManufacturingPage(driver);
    m.NavigationHelper(Pages.MANUFACTURING);
    Assert.assertTrue(m.AssertSotreRequired());
}

@Test(priority = 6)
public void AssertErorrPopUpWhenAddProuctWithOutStore()
{
    ManufacturingPage m = new ManufacturingPage(driver);
    m.NavigationHelper(Pages.MANUFACTURING);
    m.ClickAddItemsBtn();

    Assert.assertTrue(m.isErrorPopupDisplayed());
}



}
