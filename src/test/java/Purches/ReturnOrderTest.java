package Purches;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import Plugin_ERP.System_Modules.Login.purches.ReturnOrderPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

import java.util.Map;

public class ReturnOrderTest extends BaseTest {


    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("ERP Web")
    @Feature("مرتجعات المشتريات")
    @Story("إنشاء مرتجع مشتريات والتحقق من الإيصال")
    @Description("ينشئ مرتجع مشتريات بأصناف مختارة بالاسم ثم يتحقق من ظهور الإيصال في قائمة فواتير المشتريات.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("مرتجعات المشتريات")
    @Story("إنشاء مرتجع مشتريات مع خصم")
    @Description("ينشئ مرتجعًا مع خصم ويتحقق من تسجيل الإيصال في قائمة فواتير المشتريات.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("مرتجعات المشتريات")
    @Story("إضافة صنف مرتجع المشتريات بالباركود")
    @Description("يضيف صنف المرتجع بالباركود، ويطبق خصمًا، ثم يطابق الإجماليات وبيانات التقرير.")

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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("مرتجعات المشتريات")
    @Story("إضافة أصناف مرتجع المشتريات بالاسم")
    @Description("يضيف الأصناف بالاسم ويتحقق من إجماليات مرتجع المشتريات قبل الحفظ.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("مرتجعات المشتريات")
    @Story("حفظ مرتجع المشتريات بزر الحفظ")
    @Description("ينشئ مرتجعًا بأصناف مختارة بالاسم ويتحقق من الإجماليات ثم يحفظه.")
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
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("مرتجعات المشتريات")
    @Story("حذف مرتجع المشتريات")
    @Description("ينفذ مسار حذف مرتجع المشتريات للتحقق من إمكانية إزالة السجل.")
public void DeleteReturnOrderTest()
{ReturnOrderPage returnOrderPage = new ReturnOrderPage(driver);
    returnOrderPage.NavigationHelper(Pages.PURCHESINVOICELIST);
    returnOrderPage.DeleteAll();
}


    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("مرتجعات المشتريات")
    @Story("التحقق من إلزامية المورد في المرتجع")
    @Description("يتحقق من سلوك نموذج مرتجع المشتريات عند عدم تحديد المورد.")
    public void AddWithOutSupplier() {

        ReturnOrderPage returnOrderPage = new ReturnOrderPage(driver);
        returnOrderPage.NavigationHelper(Pages.RETURNPURCH);

        returnOrderPage.SelectFromDropDawn("مخزن الكافيه", "المخزن");

        Assert.assertTrue(returnOrderPage.AssertSupplierForReturnOrder());
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("ERP Web")
    @Feature("مرتجعات المشتريات")
    @Story("منع إنشاء مرتجع بدون أصناف")
    @Description("يتحقق من أن النظام يمنع حفظ مرتجع مشتريات لا يحتوي على أصناف.")
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
