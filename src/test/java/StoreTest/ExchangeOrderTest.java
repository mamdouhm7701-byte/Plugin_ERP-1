package StoreTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.StoreModel.ExchangeOrderPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class ExchangeOrderTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("إضافة إذن صرف باختيار الأصناف من القائمة")
    @Description("يتحقق هذا الاختبار من إضافة إذن صرف جديد باختيار الأصناف من قائمة الأصناف، ويشمل تحديد نوع البند واختيار العميل وإضافة الأصناف وضبط الكميات، ثم الحفظ والطباعة والتحقق من صحة بيانات التقرير وإضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddExchangeWithItemsList() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.EXCHANGES);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("العملاء","نوع البند");
        e.SelectByKeys("تستر");
        e.ClickAddItemsBtn();
        e.ClickPlusButtonByRow(3,5);
        e.ClickMinusButtonByRow(2,2);
        e.ClickSaveForProductBtn();
        e.SetQuantitiesForRows(1,10,2,15);
        e.ClickSaveAndPrintButton();
        e.AssertTextInReport("سكر");
        e.AssertTextInReport("العملاء");
        e.CloserReport();
        e.NavigationHelper(Pages.ORDERS);
        e.AssertReceiptAdded("تستر");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("إضافة إذن صرف عن طريق البحث بالاسم")
    @Description("يتحقق هذا الاختبار من إضافة إذن صرف جديد عن طريق البحث عن الأصناف بالاسم، ويشمل تحديد نوع البند واختيار المندوب والبحث عن عدة أصناف وضبط الكميات وإضافة الملاحظات، ثم الحفظ والطباعة والتحقق من تطابق البيانات مع التقرير وإضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddExchangeByName() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.EXCHANGES);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("المندوبين","نوع البند");
        e.SelectByKeys("تست");
        e.ClickOnNameRadioBtn();
        e.SearchAndSelectProduct("سكر");
        e.SearchAndSelectProduct("كرواسون");
        e.SearchAndSelectProduct("دقيق");
        e.SetQuantitiesForRows(1,50,2,50);
        e.AddNotes("تست");
        Map<String, String> dataBeforeSave = e.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        e.ClickSaveAndPrintButton();
        e.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        e.AssertTextInReport("سكر");
        e.AssertTextInReport("كرواسون");
        e.AssertTextInReport("دقيق");
        e.AssertTextInReport("تست");
        e.CloserReport();
        e.NavigationHelper(Pages.ORDERS);
        e.AssertReceiptAdded("تست");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("إضافة إذن صرف عن طريق البحث بالباركود")
    @Description("يتحقق هذا الاختبار من إضافة إذن صرف جديد عن طريق البحث عن الأصناف بالباركود، ويشمل تحديد نوع البند واختيار المندوب والبحث عن عدة أصناف بأكوادها وضبط الكميات وإضافة الملاحظات، ثم الحفظ والطباعة والتحقق من تطابق البيانات مع التقرير وإضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddExchangeByBarcode() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.EXCHANGES);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("المندوبين","نوع البند");
        e.SelectByKeys("تست");
        e.ClickOnBarcodeRadioBtn();
        e.Search("7291659936");
        e.ClickEnter();
        e.Search("6683295925");
        e.ClickEnter();
        e.Search("2515122316");
        e.ClickEnter();
        e.SetQuantitiesForRows(1,10,2,50);
        e.AddNotes("تست");
        Map<String, String> dataBeforeSave = e.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        e.ClickSaveAndPrintButton();
        e.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        e.AssertTextInReport("تست");
        e.CloserReport();
        e.NavigationHelper(Pages.ORDERS);
        e.AssertReceiptAdded("تست");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("تعديل إذن صرف موجود")
    @Description("يتحقق هذا الاختبار من تعديل إذن صرف موجود، ويشمل إضافة صنف بالاسم وآخر بالباركود وضبط الكميات وحذف صف، ثم الحفظ والطباعة والتحقق من تحديث البيانات.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditExchange() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.ORDERS);
        e.EditForBills();
        e.ClickOnNameRadioBtn();
        e.SearchAndSelectProduct("سكر");
        e.ClickOnBarcodeRadioBtn();
        e.ClickOnBarcodeRadioBtn();
        e.Search("7291659936");
        e.ClickEnter();
        e.SetQuantitiesForRows(1,10,2,50);
        e.clickDeleteRows(2);
        e.ClickSaveAndPrintButton();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("حذف إذن صرف")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف جميع أذونات الصرف المضافة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteExchange() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.ORDERS);
        e.DeleteAll();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("التحقق - نوع البند إلزامي في إذن الصرف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف بدون اختيار نوع البند، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل نوع البند إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertItemTypeRequired() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.EXCHANGES);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        Assert.assertTrue(e.AssertExsistItemTaype(), "نوع البند غير مطلوب");
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("التحقق - العميل إلزامي عند اختيار نوع البند (عملاء)")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف بدون اختيار العميل عند تحديد نوع البند (عملاء)، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل العميل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertCustomerRequired() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.EXCHANGES);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("العملاء","نوع البند");
        e.ClickSaveButton();
        Assert.assertTrue(e.AssertExsistCustomre(), "نوع العملاء غير مطلوب");
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("التحقق - المندوب إلزامي عند اختيار نوع البند (مندوبين)")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف بدون اختيار المندوب عند تحديد نوع البند (مندوبين)، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل المندوب إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertAgentRequired() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.EXCHANGES);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("المندوبين","نوع البند");
        e.ClickSaveButton();
        Assert.assertTrue(e.AssertAgnt(), "نوع المندوبين غير مطلوب");
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("التحقق - جهة الصرف إلزامية عند اختيار نوع البند (جهات الصرف)")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف بدون اختيار جهة الصرف عند تحديد نوع البند (جهات الصرف)، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل جهة الصرف إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertDistributionEntityRequired() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.EXCHANGES);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("جهات الصرف","نوع البند");
        e.ClickSaveButton();
        Assert.assertTrue(e.AssertExpenditureCategories(), "نوع جهات الصرف غير مطلوب");
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("التحقق - خطأ عند حفظ إذن صرف بدون أصناف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف بدون إضافة أي أصناف، ويتحقق من ظهور رسالة خطأ تفيد بضرورة إضافة صنف واحد على الأقل.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertErrorWhenSavingWithoutProducts() {
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.EXCHANGES);
        e.waitUntilMessageDisappear("المخزن مطلوب");

        e.SelectFromDropDawn("المندوبين","نوع البند");
        e.SelectByKeys("تست");
        e.ClickSaveButton();
        e.isErrorPopupDisplayed();
    }



    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن الصرف")
    @Story("التحقق - خطأ عند حفظ إذن الصرف بتاريخ قديم")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ إذن الصرف مع تاريخ قديم")
    @Test(priority = 11)
    public void AddOldDate(){
        ExchangeOrderPage e = new ExchangeOrderPage(driver);
        e.NavigationHelper(Pages.EXCHANGES);
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