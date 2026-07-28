package FinancialMovementsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.FinancialMovements.CustomerDiscountNoticePage;
import Plugin_ERP.System_Modules.Login.FinancialMovements.SupplierDiscountNoticePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SupplierDiscountNoticeTest extends BaseTest {



    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إشعار خصم المورد")
    @Story("إضافة إشعار خصم مورد مع التحقق من الحسابات والتقرير")
    @Description("يتحقق هذا الاختبار من إنشاء إشعار خصم مورد جديد مع إدخال قيمة الخصم واختيار المورد والمندوب وإضافة الملاحظات، ثم التحقق من صحة حسابات صافي المبلغ قبل الضريبة والضريبة والإجمالي، ومطابقة بيانات التقرير والتأكد من إضافة الإشعار بنجاح.")
    @Test(priority = 1)
    public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndReport() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
        supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("100");
        supplierDiscountNoticePage.SelectFromDropDawn("مورد عام","المورد");
        supplierDiscountNoticePage.AddNotes("auto test");
        double netBeforeTax = supplierDiscountNoticePage.getNetBeforeTax();
        double tax = supplierDiscountNoticePage.getTax();
        double total = supplierDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                supplierDiscountNoticePage.getTax();

        double expectedTax =
                supplierDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                supplierDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                supplierDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        supplierDiscountNoticePage.SelectFromDropDawn("تست","مندوب");
        supplierDiscountNoticePage.ClickSaveAndPrintButton();
        ;
        supplierDiscountNoticePage.AssertTextInReport("مورد عام");
        supplierDiscountNoticePage.AssertNumberInReport("100");
        supplierDiscountNoticePage.AssertDateInReport(supplierDiscountNoticePage.GetTodayDate());
        supplierDiscountNoticePage.AssertTextInReport("auto test");
        supplierDiscountNoticePage.AssertNumberInReport("86.96");
        supplierDiscountNoticePage.AssertNumberInReport("13.04");
        supplierDiscountNoticePage.CloserReport();
        supplierDiscountNoticePage.AssertReceiptAdded("مورد عام");

    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إشعار خصم المورد")
    @Story("تعديل إشعار خصم مورد")
    @Description("يتحقق هذا الاختبار من تعديل إشعار خصم مورد موجود مع تحديث المورد والقيمة والملاحظات والمندوب، ثم التحقق من صحة الحسابات المالية ومطابقة بيانات التقرير والتأكد من حفظ التعديلات بنجاح.")
    @Test(priority = 2)
    public void EditSupplierDiscountNoticeAnd_VerifyCalculationsAndReport() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.Edit();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("200");
        supplierDiscountNoticePage.SelectFromDropDawn("مورد جديد","المورد");
        supplierDiscountNoticePage.AddNotes("auto test edit");
        double netBeforeTax = supplierDiscountNoticePage.getNetBeforeTax();
        double tax = supplierDiscountNoticePage.getTax();
        double total = supplierDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                supplierDiscountNoticePage.getTax();

        double expectedTax =
                supplierDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                supplierDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                supplierDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        supplierDiscountNoticePage.SelectFromDropDawn("محمود","مندوب");
        supplierDiscountNoticePage.ClickSaveAndPrintButton();
        supplierDiscountNoticePage.AssertTextInReport("مورد جديد");
        supplierDiscountNoticePage.AssertNumberInReport("200");
        supplierDiscountNoticePage.AssertDateInReport(supplierDiscountNoticePage.GetTodayDate());

        supplierDiscountNoticePage.AssertTextInReport("auto test edit");
        supplierDiscountNoticePage.AssertNumberInReport("173.91");
        supplierDiscountNoticePage.AssertNumberInReport("26.09");
        supplierDiscountNoticePage.CloserReport();
        supplierDiscountNoticePage.AssertReceiptAdded("مورد جديد");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إشعار خصم المورد")
    @Story("إضافة إشعار خصم مورد مع سند مخصص")
    @Description("يتحقق هذا الاختبار من إنشاء إشعار خصم مورد جديد باستخدام السند المخصص، ثم معالجة بيانات السند والتأكد من صحة الحسابات المالية وإضافة الإشعار بنجاح.")
    @Test(priority = 3)
    public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndCustom() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("10");
        supplierDiscountNoticePage.SelectFromDropDawn("مورد عام","المورد");
        supplierDiscountNoticePage.AddNotes("auto test");
        double netBeforeTax = supplierDiscountNoticePage.getNetBeforeTax();
        double tax = supplierDiscountNoticePage.getTax();
        double total = supplierDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                supplierDiscountNoticePage.getTax();

        double expectedTax =
                supplierDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                supplierDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                supplierDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        supplierDiscountNoticePage.SelectFromDropDawn("تست","مندوب");
        supplierDiscountNoticePage.SaveCustomButton();
        supplierDiscountNoticePage.HandelCustomReceipt(1, "10");
        supplierDiscountNoticePage.ClickSaveButton();
        supplierDiscountNoticePage.AssertReceiptAdded("مورد عام");

    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إشعار خصم المورد")
    @Story("إضافة إشعار خصم مورد وحفظه مباشرة")
    @Description("يتحقق هذا الاختبار من إنشاء إشعار خصم مورد جديد وحفظه مباشرة بدون طباعة، مع التحقق من صحة الحسابات المالية والتأكد من إضافة الإشعار بنجاح.")
    @Test(priority = 4)
    public void AddSupplierDiscountNoticeAnd_VerifyCalculationsAndSave() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("100");
        supplierDiscountNoticePage.SelectFromDropDawn("مورد عام","المورد");
        supplierDiscountNoticePage.AddNotes("auto test");
        double netBeforeTax = supplierDiscountNoticePage.getNetBeforeTax();
        double tax = supplierDiscountNoticePage.getTax();
        double total = supplierDiscountNoticePage.getTotal();

        Allure.step("Verify Total = Net Before Tax + Tax");

        Assert.assertEquals(
                total,
                netBeforeTax + tax,
                0.01,
                "Screen Calculation Failed"
        );
        double actualTax =
                supplierDiscountNoticePage.getTax();

        double expectedTax =
                supplierDiscountNoticePage.getExpectedTax();

        Allure.step("Verify Tax = 15% Of Net Before Tax");

        Assert.assertEquals(
                actualTax,
                expectedTax,
                0.01,
                "Tax Calculation Failed"
        );
        double actualTotal =
                supplierDiscountNoticePage.getTotal();
        ;

        double expectedTotal =
                supplierDiscountNoticePage.getExpectedTotal();

        Allure.step("Verify Total Based On 15% Tax");

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                0.01,
                "Total Calculation Failed"
        );
        supplierDiscountNoticePage.SelectFromDropDawn("تست","مندوب");
        supplierDiscountNoticePage.ClickSaveButton();
        supplierDiscountNoticePage.AssertReceiptAdded("مورد عام");

    }


    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("إشعار خصم المورد")
    @Story("حذف جميع إشعارات خصم المورد")
    @Description("يتحقق هذا الاختبار من حذف جميع إشعارات خصم المورد الموجودة داخل النظام والتأكد من تنفيذ عملية الحذف بنجاح.")
    @Test(priority = 5)
    public void Delete() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.DeleteAll();






    }


    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إشعار خصم المورد")
    @Story("التحقق من عدم الحفظ ببيانات تاريخ غير صحيحة")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند محاولة حفظ إشعار خصم مورد ببيانات تاريخ غير صحيحة، والتأكد من منع حفظ الإشعار.")
    @Test(priority = 6)
    public void InvalidDate() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("100");
        supplierDiscountNoticePage.SelectFromDropDawn("مورد عام","المورد");
        supplierDiscountNoticePage.ClickSaveButton();
        supplierDiscountNoticePage.isErrorPopupDisplayed();

    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إشعار خصم المورد")
    @Story("التحقق من إلزامية اختيار المورد")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند محاولة حفظ إشعار خصم مورد بدون اختيار المورد، والتأكد من منع حفظ الإشعار.")
    @Test(priority = 7)
    public void InvalidAgentName() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("100");
        supplierDiscountNoticePage.ClickSaveButton();
        supplierDiscountNoticePage.isErrorPopupDisplayed();

    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("إشعار خصم المورد")
    @Story("التحقق من إلزامية إدخال قيمة الخصم")
    @Description("يتحقق هذا الاختبار من ظهور رسالة خطأ عند محاولة حفظ إشعار خصم مورد بدون إدخال قيمة الخصم، والتأكد من منع حفظ الإشعار.")
    @Test(priority = 8)
    public void InvalidValue() {
        SupplierDiscountNoticePage supplierDiscountNoticePage = new SupplierDiscountNoticePage(driver);
supplierDiscountNoticePage.NavigationHelper(Pages.SUPPLIERDISCOUNT);
supplierDiscountNoticePage.ClickNew();
        supplierDiscountNoticePage.AddDate();
        supplierDiscountNoticePage.AddValue("100");
        supplierDiscountNoticePage.ClickSaveButton();
        supplierDiscountNoticePage.IsAmountRequiredErrorDisplayed();
    }

}
