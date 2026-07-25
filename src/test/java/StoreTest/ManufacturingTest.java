package StoreTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.StoreModel.ManufacturingPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class ManufacturingTest extends BaseTest {

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن التصنيع")
    @Story("إضافة إذن تصنيع باختيار الأصناف من القائمة")
    @Description("يتحقق هذا الاختبار من إنشاء إذن تصنيع جديد عن طريق اختيار المخزن وإضافة الأصناف من قائمة المنتجات، ثم حفظ بيانات الأصناف، والتحقق من صحة أسماء الأصناف والكميات داخل تقرير الطباعة، والتأكد من إضافة إذن التصنيع بنجاح داخل سجل الأوامر.")
    @Test(priority = 1)
    public void AddManufacturingTest() {
        ManufacturingPage m = new ManufacturingPage(driver);
        m.NavigationHelper(Pages.MANUFACTURING);
        m.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        m.ClickAddItemsBtn();
        m.ClickPlusButtonByRow(1, 2);
        m.ClickSaveForProductBtn();
        Map<String, String> dataBeforeSave = m.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        m.ClickSaveAndPrintButton();
        m.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        m.NavigationHelper(Pages.ORDERS);
        m.AssertReceiptAdded("إذن تصنيع");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن التصنيع")
    @Story("إضافة إذن تصنيع باستخدام الباركود")
    @Description("يتحقق هذا الاختبار من إنشاء إذن تصنيع جديد عن طريق البحث عن المنتج باستخدام الباركود، ثم حفظ الإذن والتأكد من إضافته بنجاح داخل سجل الأوامر.")
    @Test(priority = 2)
    public void AddManufacturingByBarcodeTest() {

        ManufacturingPage m = new ManufacturingPage(driver);
        m.NavigationHelper(Pages.MANUFACTURING);
        m.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        m.ClickOnBarcodeRadioBtn();
        m.Search("7291659936");
        m.ClickEnter();
        m.ClickSaveButton();

        m.NavigationHelper(Pages.ORDERS);
        m.AssertReceiptAdded("إذن تصنيع");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن التصنيع")
    @Story("إضافة إذن تصنيع باستخدام اسم الصنف")
    @Description("يتحقق هذا الاختبار من إنشاء إذن تصنيع جديد عن طريق البحث عن المنتج باستخدام اسم الصنف، ثم حفظ الإذن والتأكد من إضافته بنجاح داخل سجل الأوامر.")
    @Test(priority = 3)
    public void AddManufacturingByNameTest() {
        ManufacturingPage m = new ManufacturingPage(driver);
        m.NavigationHelper(Pages.MANUFACTURING);
        m.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        m.ClickOnNameRadioBtn();
        m.SearchAndSelectProduct("كرواسون");
        m.ClickSaveButton();
        m.NavigationHelper(Pages.ORDERS);
        m.AssertReceiptAdded("إذن تصنيع");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إذن التصنيع")
    @Story("حذف إذن تصنيع")
    @Description("يتحقق هذا الاختبار من حذف إذن تصنيع موجود مسبقًا من سجل الأوامر والتأكد من تنفيذ عملية الحذف بنجاح.")
    @Test(priority = 4)
    public void DeleteManufacturingByBarcodeTest() {
        ManufacturingPage m = new ManufacturingPage(driver);
        m.NavigationHelper(Pages.ORDERS);
        m.DeleteByName("إذن تصنيع");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن التصنيع")
    @Story("التحقق من إلزامية اختيار المخزن")
    @Description("يتحقق هذا الاختبار من ظهور التحقق الخاص بإلزامية اختيار المخزن قبل تنفيذ أي عملية داخل شاشة إذن التصنيع.")
    @Test(priority = 5)
    public void AssertStoreRequired() {
        ManufacturingPage m = new ManufacturingPage(driver);
        m.NavigationHelper(Pages.MANUFACTURING);
        Assert.assertTrue(m.AssertSotreRequired());
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن التصنيع")
    @Story("التحقق من ظهور رسالة خطأ عند إضافة أصناف بدون اختيار مخزن")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ مناسبة عند محاولة إضافة أصناف داخل إذن التصنيع بدون اختيار المخزن أولًا.")
    @Test(priority = 6)
    public void AssertErorrPopUpWhenAddProuctWithOutStore() {
        ManufacturingPage m = new ManufacturingPage(driver);
        m.NavigationHelper(Pages.MANUFACTURING);
        m.ClickAddItemsBtn();

        Assert.assertTrue(m.isErrorPopupDisplayed());
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إذن التصنيع")
    @Story("التحقق من ظهور رسالة خطأ عند تجاوز الرصيد المتاح")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند إدخال كمية تصنيع أكبر من الرصيد المتاح داخل المخزن، مما يمنع حفظ إذن التصنيع ببيانات غير صحيحة.")
    @Test(priority = 7)
    public void AssertStorePopUpWhenAddProuctWithOutBalancee() {
        ManufacturingPage m = new ManufacturingPage(driver);
        m.NavigationHelper(Pages.MANUFACTURING);
        m.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        m.ClickAddItemsBtn();
        m.ClickPlusButtonByRow(1, 2);
        m.ClickSaveForProductBtn();
        m.SetQuantitiesForRows(1, 1000);
        m.ClickSaveButton();
        Assert.assertTrue(m.isErrorPopupDisplayed());
    }
}