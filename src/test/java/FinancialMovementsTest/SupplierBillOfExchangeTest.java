package FinancialMovementsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.FinancialMovements.CustomerReceiptPage;
import Plugin_ERP.System_Modules.Login.FinancialMovements.SupplierBillOfExchangePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SupplierBillOfExchangeTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("إضافة سند صرف مورد مع توزيع على الفواتير")
    @Description("يتحقق هذا الاختبار من إضافة سند صرف مورد بنجاح مع توزيع المبلغ على الفواتير، ويشمل إدخال التاريخ والقيمة واختيار المورد وطريقة الدفع والمندوب وإضافة الملاحظات، ثم توزيع المبلغ على الفاتورة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddSupplierBillOfExchangeTest() {
        SupplierBillOfExchangePage supplierBillOfExchangePage = new SupplierBillOfExchangePage(driver);
        supplierBillOfExchangePage.NavigationHelper(Pages.SUPPLIERBILLS);
        supplierBillOfExchangePage.ClickNew();
        supplierBillOfExchangePage.AddDate();
        supplierBillOfExchangePage.AddValue("10");
        supplierBillOfExchangePage.SelectFromDropDawn("مورد عام","المورد");
        supplierBillOfExchangePage.SelectFromDropDawn("اخري","طريقة الدفع");
        supplierBillOfExchangePage.SelectByKeys("تست");
        supplierBillOfExchangePage.SelectFromDropDawn("تست","مندوب");
        supplierBillOfExchangePage.AddNotes("Test Notes");
        supplierBillOfExchangePage.SaveCustomButton();
        supplierBillOfExchangePage.HandelCustomReceipt(1,"10");
        supplierBillOfExchangePage.ClickSaveButton();
        supplierBillOfExchangePage.AssertReceiptAdded("مورد عام");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("إضافة سند صرف مورد بالحفظ فقط")
    @Description("يتحقق هذا الاختبار من إضافة سند صرف مورد بنجاح عن طريق الحفظ المباشر، ويشمل إدخال التاريخ والقيمة واختيار المورد وطريقة الدفع والمندوب وإضافة الملاحظات، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddSupplierBillWithSaveTest() {
        SupplierBillOfExchangePage supplierBillOfExchangePage = new SupplierBillOfExchangePage(driver);
        supplierBillOfExchangePage.NavigationHelper(Pages.SUPPLIERBILLS);
        supplierBillOfExchangePage.ClickNew();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplierBillOfExchangePage.AddDate();
        supplierBillOfExchangePage.AddValue("1000");
        supplierBillOfExchangePage.SelectFromDropDawn("اخري","طريقة الدفع");
        supplierBillOfExchangePage.SelectByKeys("تست");
        supplierBillOfExchangePage.SelectFromDropDawn("مورد عام","المورد");
        supplierBillOfExchangePage.SelectFromDropDawn("تست","مندوب");
        supplierBillOfExchangePage.AddNotes("Test Notes");
        supplierBillOfExchangePage.ClickSaveButton();
        supplierBillOfExchangePage.AssertReceiptAdded("مورد عام");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("إضافة سند صرف مورد مع الحفظ والطباعة والتحقق من التقرير")
    @Description("يتحقق هذا الاختبار من إضافة سند صرف مورد بنجاح مع الحفظ والطباعة، والتحقق من صحة بيانات التقرير (المورد، المبلغ، الملاحظات، طريقة الدفع، البنك، التاريخ)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddSupplierBillWithSaveAndPrint() {
        SupplierBillOfExchangePage supplierBillOfExchangePage = new SupplierBillOfExchangePage(driver);
        supplierBillOfExchangePage.NavigationHelper(Pages.SUPPLIERBILLS);
        supplierBillOfExchangePage.ClickNew();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplierBillOfExchangePage.AddDate();
        supplierBillOfExchangePage.AddValue("1500.01");
        supplierBillOfExchangePage.SelectFromDropDawn("مورد عام","المورد");
        supplierBillOfExchangePage.SelectFromDropDawn("اخري","طريقة الدفع");
        supplierBillOfExchangePage.SelectByKeys("تست");
        supplierBillOfExchangePage.SelectFromDropDawn("تست","مندوب");
        supplierBillOfExchangePage.AddNotes("Test Notes");
        supplierBillOfExchangePage.ClickSaveAndPrintButton();
        supplierBillOfExchangePage.AssertTextInReport("مورد عام");
        supplierBillOfExchangePage.AssertNumberInReport("1500.01");
        supplierBillOfExchangePage.AssertTextInReport("Test Notes");
        supplierBillOfExchangePage.AssertTextInReport("اخري");
        supplierBillOfExchangePage.AssertTextInReport("بنك القاهرة");
        supplierBillOfExchangePage.AssertDateInReport(supplierBillOfExchangePage.GetTodayDate());
        supplierBillOfExchangePage.CloserReport();
        supplierBillOfExchangePage.AssertReceiptAdded("مورد عام");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("تعديل سند صرف مورد مع الحفظ والطباعة والتحقق من التقرير")
    @Description("يتحقق هذا الاختبار من تعديل سند صرف مورد موجود مع الحفظ والطباعة، والتحقق من صحة بيانات التقرير بعد التعديل (المورد، المبلغ، الملاحظات، طريقة الدفع، التاريخ)، والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditSupplierBillAfterReport() {
        SupplierBillOfExchangePage supplierBillOfExchangePage = new SupplierBillOfExchangePage(driver);
        supplierBillOfExchangePage.NavigationHelper(Pages.SUPPLIERBILLS);
        supplierBillOfExchangePage.Edit();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplierBillOfExchangePage.AddDate();
        supplierBillOfExchangePage.AddValue("2000");
        supplierBillOfExchangePage.SelectFromDropDawn("اخري","طريقة الدفع");
        supplierBillOfExchangePage.SelectByKeys("55");
        supplierBillOfExchangePage.SelectFromDropDawn("مورد عام","المورد");
        supplierBillOfExchangePage.SelectFromDropDawn("تست","مندوب");
        supplierBillOfExchangePage.AddNotes("Test Second Notes");
        supplierBillOfExchangePage.ClickSaveAndPrintButton();
        supplierBillOfExchangePage.AssertTextInReport("مورد عام");
        supplierBillOfExchangePage.AssertNumberInReport("2000");
        supplierBillOfExchangePage.AssertTextInReport("Test Second Notes");
        supplierBillOfExchangePage.AssertTextInReport("اخري");
        supplierBillOfExchangePage.AssertTextInReport("تست");
        supplierBillOfExchangePage.AssertDateInReport(supplierBillOfExchangePage.GetTodayDate());
        supplierBillOfExchangePage.CloserReport();
        supplierBillOfExchangePage.AssertReceiptAdded("مورد عام");
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("حذف سند صرف مورد")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف جميع سندات صرف الموردين المضافة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteSupplierBillTest() {
        SupplierBillOfExchangePage supplierBillOfExchangePage = new SupplierBillOfExchangePage(driver);
        supplierBillOfExchangePage.NavigationHelper(Pages.SUPPLIERBILLS);
        supplierBillOfExchangePage.DeleteAll();
        driver.navigate().refresh();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - المورد إلزامي في سند الصرف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ سند الصرف بدون اختيار المورد، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل المورد إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertRequiredErrorsMessageForSupplier() {
        SupplierBillOfExchangePage supplierBillOfExchangePage = new SupplierBillOfExchangePage(driver);
        supplierBillOfExchangePage.NavigationHelper(Pages.SUPPLIERBILLS);
        supplierBillOfExchangePage.ClickNew();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplierBillOfExchangePage.AddDate();
        supplierBillOfExchangePage.AddValue("100");
        supplierBillOfExchangePage.SelectFromDropDawn("اخري","طريقة الدفع");
        supplierBillOfExchangePage.SelectByKeys("تست");
        supplierBillOfExchangePage.ClickSaveButton();
        Assert.assertTrue(supplierBillOfExchangePage.IsSupplierRequiredErrorDisplayed(), "Supplier error message should be displayed");
        driver.navigate().back();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - المبلغ وطريقة الدفع إلزاميان في سند الصرف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ سند الصرف بدون إدخال المبلغ وطريقة الدفع، ويتحقق من ظهور رسالتي خطأ تفيدان بأن حقلي المبلغ وطريقة الدفع إلزاميان.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertRequiredErrorsMessageForAmount() {
        SupplierBillOfExchangePage supplierBillOfExchangePage = new SupplierBillOfExchangePage(driver);
        supplierBillOfExchangePage.NavigationHelper(Pages.SUPPLIERBILLS);
        supplierBillOfExchangePage.ClickNew();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplierBillOfExchangePage.AddDate();
        supplierBillOfExchangePage.SelectFromDropDawn("مورد عام","المورد");
        supplierBillOfExchangePage.ClickSaveButton();
        Assert.assertTrue(supplierBillOfExchangePage.IsAmountRequiredErrorDisplayed(), "Amount error message should be displayed");
        Assert.assertTrue(supplierBillOfExchangePage.IsPaymentMethodRequiredErrorDisplayed(), "Payment method error message should be displayed");
        driver.navigate().back();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - طريقة الدفع إلزامية في سند الصرف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ سند الصرف بدون اختيار طريقة الدفع، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل طريقة الدفع إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertRequiredErrorsMessageForPaymentMethod() {
        SupplierBillOfExchangePage supplierBillOfExchangePage = new SupplierBillOfExchangePage(driver);
        supplierBillOfExchangePage.NavigationHelper(Pages.SUPPLIERBILLS);
        supplierBillOfExchangePage.ClickNew();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplierBillOfExchangePage.AddDate();
        supplierBillOfExchangePage.SelectFromDropDawn("مورد عام","المورد");
        supplierBillOfExchangePage.AddValue("1000");
        supplierBillOfExchangePage.ClickSaveButton();
        Assert.assertTrue(supplierBillOfExchangePage.IsPaymentMethodRequiredErrorDisplayed(), "Payment method error message should be displayed");
        driver.navigate().back();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - خيار طريقة الدفع إلزامي في سند الصرف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ سند الصرف عند اختيار طريقة الدفع (أخرى) بدون تحديد الخيار الفرعي، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل خيار طريقة الدفع إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertRequiredErrorsMessageForPaymentMethodOption() {
        SupplierBillOfExchangePage supplierBillOfExchangePage = new SupplierBillOfExchangePage(driver);
        supplierBillOfExchangePage.NavigationHelper(Pages.SUPPLIERBILLS);
        supplierBillOfExchangePage.ClickNew();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplierBillOfExchangePage.AddDate();
        supplierBillOfExchangePage.SelectFromDropDawn("مورد عام","المورد");
        supplierBillOfExchangePage.AddValue("1000");
        supplierBillOfExchangePage.SelectFromDropDawn("اخري","طريقة الدفع");
        supplierBillOfExchangePage.ClickSaveButton();
        Assert.assertTrue(supplierBillOfExchangePage.IsPaymentMethodRequiredErrorDisplayed(), "Payment method option error message should be displayed");
        driver.navigate().back();
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - خطأ عند إدخال تاريخ قديم في سند الصرف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة سند صرف مورد بتاريخ قديم، ويتحقق من ظهور رسالة خطأ تفيد بعدم إمكانية استخدام تاريخ سابق.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddOldDate() {
        SupplierBillOfExchangePage supplierBillOfExchangePage = new SupplierBillOfExchangePage(driver);
        supplierBillOfExchangePage.NavigationHelper(Pages.SUPPLIERBILLS);
        supplierBillOfExchangePage.ClickNew();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplierBillOfExchangePage.AddPastYearDate();
        supplierBillOfExchangePage.AddValue("1000");
        supplierBillOfExchangePage.SelectFromDropDawn("مورد عام","المورد");
        supplierBillOfExchangePage.SelectFromDropDawn("اخري","طريقة الدفع");
        supplierBillOfExchangePage.SelectByKeys("تست");
        supplierBillOfExchangePage.SelectFromDropDawn("تست","مندوب");
        supplierBillOfExchangePage.AddNotes("Test Notes");
        supplierBillOfExchangePage.ClickSaveButton();
        Assert.assertTrue(supplierBillOfExchangePage.isErrorPopupDisplayed(), "Old date error message should be displayed");
        driver.navigate().back();
    }
}