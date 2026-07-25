package StoreTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.StoreModel.TransBetweenStors;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class TransBetweenStoreTest extends BaseTest {

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويل بين المخازن")
    @Story("إنشاء تحويل بين المخازن باختيار الأصناف من القائمة")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل بين مخزنين عن طريق اختيار مخزن المصدر ومخزن الوجهة، ثم إضافة الأصناف من القائمة، وحفظ التحويل وطباعة التقرير، والتحقق من تطابق بيانات الأصناف والكميات مع التقرير والتأكد من إضافة عملية التحويل بنجاح.")
    @Test(priority = 1)
    public void TransBetweenStoreTest() {

        TransBetweenStors t = new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.ClickNew();
        t.SelectFromDropDawn("مخزن الكافيه", "من مخزن");
        t.SelectFromDropDawn("المخزن الرئيسى", "الي مخزن");
        t.ClickAddItemsBtn();
        t.ClickPlusButtonByRow(2, 2);
        t.ClickSaveForProductBtn();
        Map<String, String> dataBeforeSave = t.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        t.ClickSaveAndPrintButton();
        t.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        t.CloserReport();
        t.AssertReceiptAdded("مخزن الكافيه");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويل بين المخازن")
    @Story("إنشاء تحويل بين المخازن باستخدام الباركود")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل بين المخازن عن طريق البحث عن الأصناف باستخدام الباركود، وتحديد الكميات وإضافة ملاحظة، ثم حفظ العملية والتحقق من صحة بيانات التقرير وإضافة التحويل بنجاح.")
    @Test(priority = 2)
    public void TransBetweenStoreAddWWithBarcodeTest() {

        TransBetweenStors t = new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.ClickNew();
        t.SelectFromDropDawn("مخزن الكافيه", "من مخزن");
        t.SelectFromDropDawn("المخزن الرئيسى", "الي مخزن");
        t.ClickOnBarcodeRadioBtn();
        t.Search("8218648422");
        t.ClickEnter();
        t.SetQuantitiesForRows(1, 10);
        t.AddNoteForStorgTrans("ملاحظات عامه");
        Map<String, String> dataBeforeSave = t.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        t.ClickSaveAndPrintButton();
        t.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        t.CloserReport();
        t.AssertReceiptAdded("مخزن الكافيه");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويل بين المخازن")
    @Story("إنشاء تحويل بين المخازن باستخدام اسم الصنف")
    @Description("يتحقق هذا الاختبار من إنشاء عملية تحويل بين المخازن عن طريق البحث عن الأصناف باستخدام أسمائها، وتحديد الكميات وإضافة ملاحظة، ثم حفظ العملية والتحقق من صحة بيانات التقرير وإضافة التحويل بنجاح.")
    @Test(priority = 3)
    public void TransBetweenStoreAddWithNameTest() {

        TransBetweenStors t = new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.ClickNew();
        t.SelectFromDropDawn("مخزن الكافيه", "من مخزن");
        t.SelectFromDropDawn("المخزن الرئيسى", "الي مخزن");
        t.ClickOnNameRadioBtn();
        t.SearchAndSelectProduct("جبنه رودس");
        t.SearchAndSelectProduct("جبنه فيتا");
        t.SearchAndSelectProduct("قمح");
        t.SetQuantitiesForRows(1, 50, 2, 50);
        t.AddNoteForStorgTrans("تست");
        Map<String, String> dataBeforeSave = t.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        t.ClickSaveAndPrintButton();
        t.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        t.CloserReport();
        t.AssertReceiptAdded("مخزن الكافيه");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويل بين المخازن")
    @Story("تعديل عملية تحويل بين المخازن")
    @Description("يتحقق هذا الاختبار من تعديل عملية تحويل بين المخازن عن طريق تحديث الكميات وإضافة أصناف جديدة باستخدام الاسم والباركود وتعديل الملاحظات، ثم حفظ التعديلات والتحقق من صحة التقرير بعد التعديل.")
    @Test(priority = 4)
    public void EditTransBetweenStore() {

        TransBetweenStors t = new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.Edit();
        t.SetQuantitiesForRows(1, 100, 2, 300);
        t.ClickOnNameRadioBtn();
        t.SearchAndSelectProduct("سلك لحام");
        t.ClickOnBarcodeRadioBtn();
        t.Search("5170383250");
        t.ClickEnter();
        t.AddNoteForStorgTrans("تست معدل");
        Map<String, String> dataBeforeSave = t.GetNamesAndQuantities();
        System.out.println("البيانات قبل الحفظ: " + dataBeforeSave);
        t.ClickSaveAndPrintButton();
        t.CompareNamesAndQuantitiesWithReport(dataBeforeSave);
        t.CloserReport();
        t.AssertReceiptAdded("مخزن الكافيه");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("التحويل بين المخازن")
    @Story("حذف جميع عمليات التحويل")
    @Description("يتحقق هذا الاختبار من حذف جميع عمليات التحويل بين المخازن المسجلة داخل النظام والتأكد من تنفيذ عملية الحذف بنجاح.")
    @Test(priority = 5)
    public void DeleteAllTest() {

        TransBetweenStors t = new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.DeleteAll();
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("التحويل بين المخازن")
    @Story("التحقق من إلزامية اختيار مخزن الوجهة")
    @Description("يتحقق هذا الاختبار من ظهور رسالة التحقق الخاصة بإلزامية اختيار مخزن الوجهة قبل تنفيذ عملية التحويل بين المخازن.")
    @Test(priority = 6)
    public void AssertToStoreRequired() {

        TransBetweenStors t = new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.ClickNew();
        Assert.assertTrue(t.AssertToStore(), "الرسالة مظهرتش");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("التحويل بين المخازن")
    @Story("التحقق من عدم إضافة أصناف قبل اختيار مخزن الوجهة")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند محاولة إضافة أصناف بعد اختيار مخزن المصدر فقط دون تحديد مخزن الوجهة.")
    @Test(priority = 7)
    public void AssertErorrWhenChosenOneStore() {

        TransBetweenStors t = new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.ClickNew();
        t.SelectFromDropDawn("مخزن الكافيه", "من مخزن");
        t.ClickAddItemsBtn();
        Assert.assertTrue(t.isErrorPopupDisplayed(), "الرسالة مظهرتش");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("التحويل بين المخازن")
    @Story("التحقق من عدم اختيار نفس المخزن كمصدر ووجهة")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند اختيار نفس المخزن كمخزن مصدر ومخزن وجهة، وذلك لمنع إنشاء عملية تحويل غير صحيحة.")
    @Test(priority = 8)
    public void CantChosenSameStore() {

        TransBetweenStors t = new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.ClickNew();
        t.SelectFromDropDawn("مخزن الكافيه", "من مخزن");
        t.SelectFromDropDawn("مخزن الكافيه", "الي مخزن");
        Assert.assertTrue(t.isErrorPopupDisplayed(), "الرسالة مظهرتش");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("التحويل بين المخازن")
    @Story("التحقق من عدم حفظ التحويل بدون أصناف")
    @Description("يتحقق هذا الاختبار من عدم السماح بحفظ عملية تحويل بين المخازن في حالة عدم إضافة أي أصناف، والتأكد من بقاء زر الحفظ غير مفعل.")
    @Test(priority = 9)
    public void CantSaveWithOutChosenProduct() {

        TransBetweenStors t = new TransBetweenStors(driver);
        t.NavigationHelper(Pages.STORGETRANS);
        t.ClickNew();
        t.SelectFromDropDawn("مخزن الكافيه", "من مخزن");
        t.SelectFromDropDawn("المخزن الرئيسى", "الي مخزن");
        Assert.assertTrue(t.AssertSaveButtonIsNotChecked());
    }
}