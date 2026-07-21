package StoreTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.StoreModel.AdditionOrderPage;
import Plugin_ERP.System_Modules.Login.StoreModel.ExchangeOrderPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class AdditionOrderTest extends BaseTest {

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("إضافة إذن استلام باختيار الأصناف من القائمة")
    @Description("يتحقق هذا الاختبار من إضافة إذن صرف جديد باختيار الأصناف من قائمة الأصناف، ويشمل تحديد نوع البند واختيار العميل وإضافة الأصناف وضبط الكميات،" +
            " ثم الحفظ والطباعة والتحقق من صحة بيانات التقرير وإضافة السجل بنجاح.")

    @Test(priority=1)
    public void AdditionOrderTest() {

        AdditionOrderPage a = new AdditionOrderPage(driver);
        a.NavigationHelper(Pages.ADDITION);
       // a.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        a.waitUntilMessageDisappear("المخزن مطلوب");
        a.SelectFromDropDawn("الموردين", "نوع البند");
        a.SelectByKeys("محمد ممدوح");
        a.ClickAddItemsBtn();
        // e.debugPrintAllRows();
        a.ClickPlusButtonByRow(3, 5);
        a.ClickMinusButtonByRow(2, 2);
        a.ClickSaveForProductBtn();
        a.SetQuantitiesForRows(1, 10, 2, 15);

        a.AddNoteForDealer("Test");

        Map<String, String> dataBeforeSave = a.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        a.ClickSaveAndPrintButton();
        a.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        a.AssertTextInReport("سكر");
        a.AssertTextInReport("الموردين");
        a.CloserReport();
        a.NavigationHelper(Pages.ORDERS);
        a.AssertReceiptAdded("محمد ممدوح");

    }


    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("إضافة إذن استلام عن طريق البحث بالاسم")
    @Description("يتحقق هذا الاختبار من إضافة إذن استلام جديد عن طريق البحث عن الأصناف بالاسم،" +
            " ويشمل تحديد نوع البند واختيار المندوب والبحث عن عدة أصناف وضبط الكميات وإضافة الملاحظات، " +
            "ثم الحفظ والطباعة والتحقق من تطابق البيانات مع التقرير وإضافة السجل بنجاح.")

    @Test(priority=2)
    public void AddAdditionOrderTestByName() {
        AdditionOrderPage a = new AdditionOrderPage(driver);
        a.NavigationHelper(Pages.ADDITION);
        driver.navigate().refresh();
      //  a.SelectFromDropDawn("مخزن الكافيه", "المخزن");

        a.waitUntilMessageDisappear("المخزن مطلوب");

        a.SelectFromDropDawn("المندوبين","نوع البند");
        a.SelectByKeys("تست");
        a.ClickOnNameRadioBtn();
       a.SearchAndSelectProduct("سكر");
        a.SearchAndSelectProduct("كرواسون");
        a.SearchAndSelectProduct("دقيق");
        a.SetQuantitiesForRows(1,50,2,50);
        a.AddNoteForDealer("تست");
        Map<String, String> dataBeforeSave = a.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        a.ClickSaveAndPrintButton();
        a.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        a.AssertTextInReport("سكر");
        a.AssertTextInReport("كرواسون");
        a.AssertTextInReport("دقيق");
        a.AssertTextInReport("تست");
        a.CloserReport();
        a.NavigationHelper(Pages.ORDERS);
        a.AssertReceiptAdded("تست");
    }





    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("إضافة إذن الاستلام عن طريق البحث بالباركود")
    @Description("يتحقق هذا الاختبار من إضافة إذن صرف جديد عن طريق البحث عن الأصناف بالباركود، ويشمل تحديد نوع البند واختيار المندوب والبحث عن عدة أصناف بأكوادها وضبط الكميات وإضافة الملاحظات، ثم الحفظ والطباعة والتحقق من تطابق البيانات مع التقرير وإضافة السجل بنجاح.")

    @Test(priority=3)
    public void AddAdditionByBarcode() {
        AdditionOrderPage a = new AdditionOrderPage(driver);

        a.NavigationHelper(Pages.ADDITION);
        driver.navigate().refresh();
       // a.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        a.waitUntilMessageDisappear("المخزن مطلوب");

        a.SelectFromDropDawn("المندوبين","نوع البند");
        a.SelectByKeys("تست");
        a.ClickOnBarcodeRadioBtn();
        a.Search("7291659936");
        a.ClickEnter();
        a.Search("6683295925");
        a.ClickEnter();
        a.Search("2515122316");
        a.ClickEnter();
        a.AddNoteForDealer("تست");

        a.SetQuantitiesForRows(1,10,2,50);
        a.AddNoteForDealer("تست جديد");
        Map<String, String> dataBeforeSave = a.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        a.ClickSaveAndPrintButton();
        a.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        a.AssertTextInReport("تست");
        a.CloserReport();
        a.NavigationHelper(Pages.ORDERS);
        a.AssertReceiptAdded("تست");
    }




    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("تعديل إذن استلام موجود")
    @Description("يتحقق هذا الاختبار من تعديل إذن استلام موجود، ويشمل إضافة صنف بالاسم وآخر بالباركود وضبط الكميات وحذف صف، ثم الحفظ والطباعة والتحقق من تحديث البيانات.")

    @Test(priority=4)
    public void EditAddition() {
        AdditionOrderPage e = new AdditionOrderPage(driver);
        e.NavigationHelper(Pages.ORDERS);
        e.EditForBills();
        e.ClickOnNameRadioBtn();
        e.SearchAndSelectProduct("سكر");
        e.ClickOnBarcodeRadioBtn();
        e.ClickOnBarcodeRadioBtn();
        e.Search("7291659936");
        e.ClickEnter();
        e.SetQuantitiesForRows(1,10,2,50);
        e.AddNoteForDealer("تست");
        e.clickDeleteRows(2);
        e.ClickSaveAndPrintButton();
    }



    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("حذف إذن استلام")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف جميع أذونات الاستلام المضافة بنجاح.")
    @Test(priority=5)
    public void DeleteAddition() {
        AdditionOrderPage a = new AdditionOrderPage(driver);
        a.NavigationHelper(Pages.ORDERS);
        a.DeleteAll();
    }


    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("التحقق - نوع البند إلزامي في إذن الاستلام")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف بدون اختيار نوع البند، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل نوع البند إلزامي.")
    @Test(priority=6)
    public void AssertItemTypeRequired() {
        AdditionOrderPage a = new AdditionOrderPage(driver);
    a.NavigationHelper(Pages.ADDITION);
       a.waitUntilMessageDisappear("المخزن مطلوب");

        Assert.assertTrue(a.AssertExsistItemTaype(), "نوع البند غير مطلوب");
    }





    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("التحقق - المورد إلزامي عند اختيار نوع البند (موردين)")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف بدون اختيار العميل عند تحديد نوع البند (موردين)، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل العميل إلزامي.")

    @Test(priority=7)
    public void AssertCustomerRequired() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.ADDITION);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("الموردين","نوع البند");
    e.Search("2515122316");
    e.ClickEnter();
        e.ClickSaveButton();
        Assert.assertTrue(e.AssertSupplier(), "نوع المورد غير مطلوب");
    }





    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("التحقق - المندوب إلزامي عند اختيار نوع البند (مندوبين)")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف بدون اختيار المندوب عند تحديد نوع البند (مندوبين)، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل المندوب إلزامي.")

    @Test(priority=8)
    public void AssertAgentRequired() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.ADDITION);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("المندوبين","نوع البند");
    e.Search("2515122316");
    e.ClickEnter();
        e.ClickSaveButton();
        Assert.assertTrue(e.AssertAgnt(), "نوع المندوبين غير مطلوب");
    }



    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("التحقق - جهة الصرف إلزامية عند اختيار نوع البند (جهات الصرف)")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف بدون اختيار جهة الصرف عند تحديد نوع البند (جهات الصرف)، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل جهة الصرف إلزامي.")

    @Test(priority=9)
    public void AssertDistributionEntityRequired() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.ADDITION);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("جهات الصرف","نوع البند");
    e.Search("2515122316");
    e.ClickEnter();
        e.ClickSaveButton();
        Assert.assertTrue(e.AssertExpenditureCategories(), "نوع جهات الصرف غير مطلوب");
    }



    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("التحقق - خطأ عند حفظ إذن استلام بدون أصناف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف بدون إضافة أي أصناف، ويتحقق من ظهور رسالة خطأ تفيد بضرورة إضافة صنف واحد على الأقل.")

    @Test(priority=10)
    public void AssertErrorWhenSavingWithoutProducts() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
    e.NavigationHelper(Pages.ADDITION);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("المندوبين","نوع البند");
        e.SelectByKeys("تست");
        e.ClickSaveButton();
        e.isErrorPopupDisplayed();
    }



    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الاستلام")
    @Story("التحقق - خطأ عند حفظ إذن استلام بتاريخ قديم")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الاستلام مع تاريخ قديم")

    @Test(priority = 11)
    public void AddOldDate(){
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.ADDITION);
        e.waitUntilMessageDisappear("المخزن مطلوب");
        e.AddPastYearDate();

        e.clickOnEmptyArea();

        e.SelectFromDropDawn("المندوبين","نوع البند");
        e.SelectByKeys("تست");
        e.ClickOnBarcodeRadioBtn();
        e.Search("7291659936");
        e.ClickEnter();
        e.ClickSaveButton();
        e.isErrorPopupDisplayed();
    }


}


