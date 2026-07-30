package Purches;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Models.*;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PurchesBillsTest extends BaseTest {

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المشتريات")
    @Story("إضافة فاتورة مشتريات باختيار الأصناف من القائمة")
    @Description("يتحقق هذا الاختبار من إنشاء فاتورة مشتريات جديدة عن طريق اختيار المورد والمخزن ونوع السداد وإضافة الأصناف من القائمة" +
            "، ثم تطبيق الخصم والتحقق من صحة العمليات الحسابية ومطابقة بيانات التقرير والتأكد من إضافة الفاتورة بنجاح.")
    @Test(priority = 1)
    public void PurchaseBillsTest() {

        PurchesBillsPage p = new PurchesBillsPage(driver);

        p.NavigationHelper(Pages.PURCHINVOICE);
        p.AddDate();
        p.SelectFromDropDawn("نقدي", "نوع السداد");
        p.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        p.ClickShift();
        p.SelectByKeys("محمد ممدوح");
        p.SelectFromDropDawn("تست", "مندوب");
        p.ClickAddItemsBtn();
        p.ClickPlusButtonByRow(2, 2);
        p.ClickSaveForProductBtn();
        p.AddDiscountValue("10");

        // ================= Business Validation =================
        InvoiceTotals totals = p.validateBusinessTotals();

        Map<String, String> dataBeforeSave =
               p. GetNamesQuantitiesAndPrices();
        p.ClickCash();
        p.ClickPlusToAddPay();
        p.ClickSaveAndPrintButton();
        p.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
        p.validateReport(totals);

        // ================= Receipt Validation =================

        p.NavigationHelper(Pages.PURCHESINVOICELIST);

        p.AssertReceiptAdded("محمد ممدوح");
        p.AssertReceiptAdded("نقدي");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المشتريات")
    @Story("إضافة فاتورة مشتريات باستخدام الباركود")
    @Description("يتحقق هذا الاختبار من إنشاء فاتورة مشتريات جديدة باستخدام البحث عن الأصناف بالباركود مع تحديد الكميات وسعر الشراء" +
            " وتطبيق الخصم، ثم التحقق من صحة العمليات الحسابية ومطابقة التقرير وإضافة الفاتورة بنجاح.")
    @Test(priority = 2)
    public void AddPurchesBillsByBarcodTest() {

        PurchesBillsPage p = new PurchesBillsPage(driver);

        p.NavigationHelper(Pages.PURCHINVOICE);
        p.AddDate();
        p.SelectFromDropDawn("نقدي", "نوع السداد");
        p.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        p.ClickShift();
        p.SelectByKeys("محمد ممدوح");
        p.SelectFromDropDawn("تست", "مندوب");
        p.ClickOnBarcodeRadioBtn();
        p.Search("8218648422");
        p.ClickEnter();
        p.SetQuantitiesForRows(1, 10);
        p.SetValueByRow("سعر الشراء", 1, 200);
        p.AddDiscountPercntge("10");
        // ================= Business Validation =================
        InvoiceTotals totals = p.validateBusinessTotals();

        Map<String, String> dataBeforeSave =
        p. GetNamesQuantitiesAndPrices();

        p.ClickOtherPay();
        p.SelectFromDropDawn("تست", "طريقة الدفع");
        p.ClickPlusToAddPay();
        p.ClickSaveAndPrintButton();
        p.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
        p.validateReport(totals);

        p.NavigationHelper(Pages.PURCHESINVOICELIST);

        p.AssertReceiptAdded("محمد ممدوح");
        p.AssertReceiptAdded("نقدي");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المشتريات")
    @Story("إضافة فاتورة مشتريات باستخدام اسم الصنف")
    @Description("يتحقق هذا الاختبار من إنشاء فاتورة مشتريات جديدة عن طريق البحث عن الأصناف باستخدام الاسم وتحديد الكميات وأسعار الشراء" +
            "، ثم التحقق من صحة العمليات الحسابية ومطابقة بيانات التقرير والتأكد من إضافة الفاتورة بنجاح.")
    @Test(priority = 3)
    public void AddPurchesBillsByNameTest() {

        PurchesBillsPage p = new PurchesBillsPage(driver);

        p.NavigationHelper(Pages.PURCHINVOICE);
        p.SelectFromDropDawn("آجل", "نوع السداد");
        p.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        p.ClickShift();
        p.SelectByKeys("محمد ممدوح");
        p.ClickOnNameRadioBtn();
        p.SearchAndSelectProduct("سكر");
        p.SearchAndSelectProduct("كرواسون");
        p.SearchAndSelectProduct("دقيق");
        p.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        p.SetValueByRow("سعر الشراء", 1, 200);
        p.SetValueByRow("سعر الشراء", 2, 200);
        p.SetValueByRow("سعر الشراء", 3, 200);
        p.validateInvoiceAllBusiness();
        p.NavigationHelper(Pages.PURCHESINVOICELIST);

        p.AssertReceiptAdded("محمد ممدوح");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المشتريات")
    @Story("إضافة فاتورة مشتريات مع السداد النقدي")
    @Description("يتحقق هذا الاختبار من إنشاء فاتورة مشتريات جديدة واختيار طريقة السداد النقدي،" +
            " ثم التحقق من صحة العمليات الحسابية ومطابقة بيانات التقرير والتأكد من تسجيل الفاتورة بطريقة السداد النقدية بنجاح.")
    @Test(priority = 4)
    public void AddPurchesBillsByPriceWithCashPayTest() {

        PurchesBillsPage p = new PurchesBillsPage(driver);

        p.NavigationHelper(Pages.PURCHINVOICE);

        p.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        p.ClickShift();
        p.SelectByKeys("محمد ممدوح");
        p.ClickOnNameRadioBtn();
        p.SearchAndSelectProduct("سكر");
        p.SearchAndSelectProduct("كرواسون");
        p.SearchAndSelectProduct("دقيق");
        p.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        p.SetValueByRow("سعر الشراء", 1, 200);
        p.SetValueByRow("سعر الشراء", 2, 200);
        p.SetValueByRow("سعر الشراء", 3, 200);


        p.validateInvoiceAllBusiness();
        p.NavigationHelper(Pages.PURCHESINVOICELIST);

        p.AssertReceiptAdded("محمد ممدوح");
        p.AssertReceiptAdded("آجل");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المشتريات")
    @Story("إضافة فاتورة مشتريات مع طريقة سداد أخرى")
    @Description("يتحقق هذا الاختبار من إنشاء فاتورة مشتريات جديدة باستخدام طريقة سداد أخرى،" +
            " ثم التحقق من صحة العمليات الحسابية ومطابقة بيانات التقرير والتأكد من حفظ الفاتورة بنجاح بطريقة السداد المحددة.")
    @Test(priority = 5)
    public void AddPurchesBillsByPriceWithOtherPayTest() {

        PurchesBillsPage p = new PurchesBillsPage(driver);

        p.NavigationHelper(Pages.PURCHINVOICE);

        p.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        p.ClickShift();
        p.SelectByKeys("محمد ممدوح");
        p.ClickOnNameRadioBtn();
        p.SearchAndSelectProduct("سكر");
        p.SearchAndSelectProduct("كرواسون");
        p.SearchAndSelectProduct("دقيق");
        p.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        p.SetValueByRow("سعر الشراء", 1, 200);
        p.SetValueByRow("سعر الشراء", 2, 200);
        p.SetValueByRow("سعر الشراء", 3, 200);


        p.validateInvoiceAllBusiness();
        p.NavigationHelper(Pages.PURCHESINVOICELIST);

        p.AssertReceiptAdded("محمد ممدوح");
        p.AssertReceiptAdded("آجل");
    }


    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المشتريات")
    @Story("إضافة فاتورة مشتريات والضغط على حفظ")
    @Description("يتحقق هذا الاختبار من إنشاء فاتورة مشتريات بالضغط على حفظ" )
    @Test(priority = 6)
    public void AddPurchesBillsWithSaveBuutonTest() {
        PurchesBillsPage p = new PurchesBillsPage(driver);

        p.NavigationHelper(Pages.PURCHINVOICE);

        p.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        p.ClickShift();
        p.SelectByKeys("محمد ممدوح");
        p.ClickOnNameRadioBtn();
        p.SearchAndSelectProduct("سكر");
        p.SearchAndSelectProduct("كرواسون");
        p.SearchAndSelectProduct("دقيق");
        p.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        p.SetValueByRow("سعر الشراء", 1, 200);
        p.SetValueByRow("سعر الشراء", 2, 200);
        p.SetValueByRow("سعر الشراء", 3, 200);
        // ================= Business Validation =================
        InvoiceTotals totals = p.validateBusinessTotals();

        Map<String, String> dataBeforeSave =
                p. GetNamesQuantitiesAndPrices();
        p.ClickCash();
        p.ClickPlusToAddPay();
        p.ClickSaveButton();
        p.NavigationHelper(Pages.PURCHESINVOICELIST);
        p.AssertReceiptAdded("محمد ممدوح");
        p.AssertReceiptAdded("آجل");



    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("فواتير المشتريات")
    @Story("حذف جميع فواتير المشتريات")
    @Description("يتحقق هذا الاختبار من حذف جميع فواتير المشتريات الموجودة داخل النظام والتأكد من تنفيذ عملية الحذف بنجاح.")
    @Test(priority = 6)
    public void DeleteAll() {

        PurchesBillsPage p = new PurchesBillsPage(driver);

        p.NavigationHelper(Pages.PURCHESINVOICELIST);
        p.DeleteByName("فاتورة مشتريات");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المشتريات")
    @Story("التحقق من إلزامية اختيار المورد")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند محاولة حفظ فاتورة مشتريات بدون اختيار المورد، والتأكد من منع حفظ الفاتورة.")
    @Test(priority = 8)
    public void AddWithOutSupplier() {

        PurchesBillsPage p = new PurchesBillsPage(driver);
        p.NavigationHelper(Pages.PURCHINVOICE);

        p.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        p.ClickShift();
        p.ClickOnNameRadioBtn();
        p.SearchAndSelectProduct("سكر");
        p.SelectFromDropDawn("آجل", "نوع السداد");
        p.ClickSaveButton();
        Assert.assertTrue(p.isErrorPopupDisplayed());
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("فواتير المشتريات")
    @Story("التحقق من عدم حفظ فاتورة بدون أصناف")
    @Description("يتحقق هذا الاختبار من عدم السماح بحفظ فاتورة مشتريات بدون إضافة أي أصناف، والتأكد من بقاء زر الحفظ غير مفعل.")
    @Test(priority = 9)
    public void AddWithOutProduct() {

        PurchesBillsPage p = new PurchesBillsPage(driver);
        p.NavigationHelper(Pages.PURCHINVOICE);
        driver.navigate().refresh();
        p.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        p.ClickShift();
        p.SelectByKeys("محمد ممدوح");
        p.SelectFromDropDawn("آجل", "نوع السداد");
        Assert.assertTrue(p.AssertSaveButtonIsNotChecked());
    }
}