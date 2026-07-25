package StoreTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.StoreModel.DamageAdjustmentPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class DamageAdjustmentTest extends BaseTest {

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تسوية التوالف")
    @Story("إضافة تسوية توالف باختيار الأصناف من القائمة")
    @Description("يتحقق هذا الاختبار من إنشاء تسوية توالف جديدة عن طريق اختيار المخزن وإضافة الأصناف من القائمة مع تحديد كميات الزيادة والنقص، ثم حفظ العملية وطباعة التقرير والتحقق من تطابق بيانات الأصناف والكميات مع التقرير والتأكد من إضافة التسوية بنجاح.")
    @Test(priority = 1)
    public void AddDamageAdjustmentTest() {
        DamageAdjustmentPage d = new DamageAdjustmentPage(driver);
        d.NavigationHelper(Pages.DAMAGEADUSTMENT);
        d.ClickNew();
        d.AddDate();
        d.waitUntilMessageDisappear("المخزن مطلوب");
        d.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        d.ClickAddItemsBtn();
        d.ClickPlusButtonByRow(3, 5);
        d.ClickMinusButtonByRow(2, 2);
        d.ClickSaveForProductBtn();
        d.SetQuantitiesForRows(1, 10, 2, 15);
        Map<String, String> dataBeforeSave = d.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        d.ClickSaveAndPrintButton();
        d.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        d.CloserReport();
        d.AssertReceiptAdded("مخزن الكافيه");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تسوية التوالف")
    @Story("إضافة تسوية توالف باستخدام اسم الصنف")
    @Description("يتحقق هذا الاختبار من إنشاء تسوية توالف جديدة عن طريق البحث عن الأصناف باستخدام أسمائها وتحديد الكميات المطلوبة، ثم حفظ العملية والتحقق من صحة بيانات التقرير وإضافة التسوية بنجاح.")
    @Test(priority = 2)
    public void AddDamageAdjustmentByNameTest() {
        DamageAdjustmentPage d = new DamageAdjustmentPage(driver);
        d.NavigationHelper(Pages.DAMAGEADUSTMENT);
        d.ClickNew();
        d.waitUntilMessageDisappear("المخزن مطلوب");
        d.AddDate();
        d.ClickOnNameRadioBtn();
        d.SearchAndSelectProduct("سكر");
        d.SearchAndSelectProduct("كرواسون");
        d.SearchAndSelectProduct("دقيق");
        d.SetQuantitiesForRows(1, 50, 2, 50);
        Map<String, String> dataBeforeSave = d.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        d.ClickSaveAndPrintButton();
        d.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        d.CloserReport();
        d.AssertReceiptAdded("مخزن الكافيه");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تسوية التوالف")
    @Story("إضافة تسوية توالف باستخدام الباركود")
    @Description("يتحقق هذا الاختبار من إنشاء تسوية توالف جديدة عن طريق البحث عن الأصناف باستخدام الباركود، ثم تحديد الكميات وحفظ العملية والتحقق من صحة بيانات التقرير وإضافة التسوية بنجاح.")
    @Test(priority = 3)
    public void AddDamageAdjustmentByBarcodeTest() {
        DamageAdjustmentPage d = new DamageAdjustmentPage(driver);
        d.NavigationHelper(Pages.DAMAGEADUSTMENT);
        d.ClickNew();
        d.waitUntilMessageDisappear("المخزن مطلوب");
        d.AddDate();
        d.ClickOnBarcodeRadioBtn();
        d.Search("7291659936");
        d.ClickEnter();
        d.Search("6683295925");
        d.ClickEnter();
        d.Search("2515122316");
        d.ClickEnter();
        d.SetQuantitiesForRows(1, 10, 2, 50);
        Map<String, String> dataBeforeSave = d.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        d.ClickSaveAndPrintButton();
        d.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        d.CloserReport();
        d.AssertReceiptAdded("مخزن الكافيه");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تسوية التوالف")
    @Story("تعديل تسوية توالف")
    @Description("يتحقق هذا الاختبار من تعديل تسوية توالف موجودة عن طريق إضافة أصناف جديدة باستخدام الاسم والباركود، وتعديل الكميات وحذف أحد الأصناف، ثم حفظ التعديلات والتحقق من صحة بيانات التقرير بعد التعديل.")
    @Test(priority = 4)
    public void EditAddDamageAdjustment() {

        DamageAdjustmentPage d = new DamageAdjustmentPage(driver);
        d.NavigationHelper(Pages.DAMAGEADUSTMENT);
        d.EditForDamage();
        d.ClickOnNameRadioBtn();
        d.SearchAndSelectProduct("شاى");
        d.ClickOnBarcodeRadioBtn();
        d.ClickOnBarcodeRadioBtn();
        d.Search("3804821073");
        d.ClickEnter();
        d.SetQuantitiesForRows(1, 10, 2, 50);
        d.clickDeleteRows(2);

        Map<String, String> dataBeforeSave = d.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        d.ClickSaveAndPrintButton();
        d.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        d.CloserReport();
        d.AssertReceiptAdded("مخزن الكافيه");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تسوية التوالف")
    @Story("حذف تسوية توالف")
    @Description("يتحقق هذا الاختبار من حذف تسوية توالف موجودة داخل النظام والتأكد من تنفيذ عملية الحذف بنجاح.")
    @Test(priority = 5)
    public void DeleteDamageAdjustment() {
        DamageAdjustmentPage d = new DamageAdjustmentPage(driver);
        d.NavigationHelper(Pages.DAMAGEADUSTMENT);
        d.DeleteForDamage();
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تسوية التوالف")
    @Story("التحقق من عدم السماح بالحفظ بتاريخ قديم")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند محاولة إنشاء تسوية توالف بتاريخ قديم غير مسموح به، والتأكد من منع حفظ العملية.")
    @Test(priority = 6)
    public void AddOldDateTest() {
        DamageAdjustmentPage d = new DamageAdjustmentPage(driver);
        d.NavigationHelper(Pages.DAMAGEADUSTMENT);
        d.ClickNew();
        d.AddPastYearDate();
        d.ClickOnBarcodeRadioBtn();
        d.Search("3804821073");
        d.ClickEnter();
        d.ClickSaveButton();
        Assert.assertTrue(d.isErrorPopupDisplayed());
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تسوية التوالف")
    @Story("التحقق من عدم إضافة صنف بدون كمية")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند محاولة إضافة صنف داخل تسوية التوالف بدون تحديد الكمية المطلوبة، والتأكد من منع استكمال العملية.")
    @Test(priority = 7)
    public void AddProductWithoutQuantityTest() {
        DamageAdjustmentPage d = new DamageAdjustmentPage(driver);
        d.NavigationHelper(Pages.DAMAGEADUSTMENT);
        d.ClickNew();
        d.AddPastYearDate();
        d.ClickOnBarcodeRadioBtn();
        d.Search("0253590003");
        d.ClickEnter();
        Assert.assertTrue(d.isErrorPopupDisplayed());
    }
}