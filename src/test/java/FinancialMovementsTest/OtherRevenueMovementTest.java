package FinancialMovementsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.FinancialMovements.OtherRevenueMovementPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OtherRevenueMovementTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حركة الإيرادات الأخرى")
    @Story("إضافة حركة إيراد مع الحفظ والطباعة والتحقق من التقرير")
    @Description("يتحقق هذا الاختبار من إضافة حركة إيراد جديدة مع الحفظ والطباعة، والتحقق من صحة بيانات التقرير (نوع الإيراد، التاريخ، الملاحظات، المبلغ، طريقة الدفع، البنك)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddOtherRevenueMovementWithSaveAndPrint() {
        OtherRevenueMovementPage otherRevenueMovementPage = new OtherRevenueMovementPage(driver);
        otherRevenueMovementPage.NavigationHelper(Pages.OTHERREVENUE);
        otherRevenueMovementPage.ClickNew();
        otherRevenueMovementPage.AddDate();
        otherRevenueMovementPage.SelectRevenueTypeButton();
        otherRevenueMovementPage.SelectFromDropDawn("ايرادات اخرى","أنواع الإيرادات");
        otherRevenueMovementPage.AddValue("1000");
        otherRevenueMovementPage.AddNotes("Test Notes");
        otherRevenueMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        otherRevenueMovementPage.SelectByKeys("تست");
        otherRevenueMovementPage.ClickSaveAndPrintButton();
        otherRevenueMovementPage.AssertTextInReport("ايرادات اخرى");
        otherRevenueMovementPage.AssertDateInReport(otherRevenueMovementPage.GetTodayDate());
        otherRevenueMovementPage.AssertTextInReport("Test Notes");
        otherRevenueMovementPage.AssertNumberInReport("1000");
        otherRevenueMovementPage.AssertTextInReport("اخري");
        otherRevenueMovementPage.AssertTextInReport("بنك القاهرة");
        otherRevenueMovementPage.CloserReport();
        otherRevenueMovementPage.NavigationHelper(Pages.OTHERREVENUE);
        otherRevenueMovementPage.AssertReceiptAdded("ايرادات اخرى");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حركة الإيرادات الأخرى")
    @Story("تعديل حركة إيراد مع الحفظ والطباعة والتحقق من التقرير")
    @Description("يتحقق هذا الاختبار من تعديل حركة إيراد موجودة مع الحفظ والطباعة، والتحقق من صحة بيانات التقرير بعد التعديل (نوع الإيراد، التاريخ، الملاحظات، المبلغ، طريقة الدفع، البنك)، والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditOtherRevenueMovement() {
        OtherRevenueMovementPage otherRevenueMovementPage = new OtherRevenueMovementPage(driver);
        otherRevenueMovementPage.NavigationHelper(Pages.OTHERREVENUE);
        otherRevenueMovementPage.Edit();
        otherRevenueMovementPage.AddDate();
        otherRevenueMovementPage.SelectAccountTypeButton();
        otherRevenueMovementPage.SelectFromDropDawn("المبيعات","أنواع الإيرادات");
        otherRevenueMovementPage.AddValue("2000");
        otherRevenueMovementPage.AddNotes("Test Notes Edit");
        otherRevenueMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        otherRevenueMovementPage.SelectByKeys("تست");
        otherRevenueMovementPage.ClickSaveAndPrintButton();
        otherRevenueMovementPage.AssertTextInReport("المبيعات");
        otherRevenueMovementPage.AssertDateInReport(otherRevenueMovementPage.GetTodayDate());
        otherRevenueMovementPage.AssertTextInReport("Test Notes Edit");
        otherRevenueMovementPage.AssertNumberInReport("2000");
        otherRevenueMovementPage.AssertTextInReport("اخري");
        otherRevenueMovementPage.AssertTextInReport("بنك القاهرة");
        otherRevenueMovementPage.CloserReport();
        otherRevenueMovementPage.AssertReceiptAdded("المبيعات");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حركة الإيرادات الأخرى")
    @Story("إضافة حركة إيراد بالحفظ فقط")
    @Description("يتحقق هذا الاختبار من إضافة حركة إيراد جديدة عن طريق الحفظ المباشر، ويشمل إدخال التاريخ ونوع الإيراد والقيمة والملاحظات وطريقة الدفع، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddOtherRevenueMovementWithSave() {
        OtherRevenueMovementPage otherRevenueMovementPage = new OtherRevenueMovementPage(driver);
        otherRevenueMovementPage.NavigationHelper(Pages.OTHERREVENUE);
        otherRevenueMovementPage.ClickNew();
        otherRevenueMovementPage.AddDate();
        otherRevenueMovementPage.SelectRevenueTypeButton();
        otherRevenueMovementPage.SelectFromDropDawn("ايرادات اخرى","أنواع الإيرادات");
        otherRevenueMovementPage.AddValue("1000");
        otherRevenueMovementPage.AddNotes("Test Notes");
        otherRevenueMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        otherRevenueMovementPage.SelectByKeys("تست");
        otherRevenueMovementPage.ClickSaveButton();
        otherRevenueMovementPage.AssertReceiptAdded("ايرادات اخرى");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حركة الإيرادات الأخرى")
    @Story("حذف حركة إيراد")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف حركات الإيرادات المضافة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteOtherRevenueMovement() {
        OtherRevenueMovementPage otherRevenueMovementPage = new OtherRevenueMovementPage(driver);
        otherRevenueMovementPage.NavigationHelper(Pages.OTHERREVENUE);
        otherRevenueMovementPage.Delete();
        otherRevenueMovementPage.Delete();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حركة الإيرادات الأخرى")
    @Story("التحقق - نوع الإيراد إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ حركة الإيراد بدون اختيار نوع الإيراد، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل النوع إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertTypeRequiredErrorMessage() {
        OtherRevenueMovementPage otherRevenueMovementPage = new OtherRevenueMovementPage(driver);
        otherRevenueMovementPage.NavigationHelper(Pages.OTHERREVENUE);
        otherRevenueMovementPage.ClickNew();
        otherRevenueMovementPage.AddDate();
        otherRevenueMovementPage.AddValue("1000");
        otherRevenueMovementPage.AddNotes("Test Notes");
        otherRevenueMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        otherRevenueMovementPage.SelectByKeys("تست");
        otherRevenueMovementPage.ClickSaveButton();
        Assert.assertTrue(otherRevenueMovementPage.IsTypeRequiredErrorDisplayed(), "Type Other Revenue M Error Message Not Displayed");
        driver.navigate().back();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حركة الإيرادات الأخرى")
    @Story("التحقق - المبلغ إلزامي في حركة الإيراد")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ حركة الإيراد بدون إدخال المبلغ، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل المبلغ إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertAmountRequiredErrorMessage() {
        OtherRevenueMovementPage otherRevenueMovementPage = new OtherRevenueMovementPage(driver);
        otherRevenueMovementPage.NavigationHelper(Pages.OTHERREVENUE);
        otherRevenueMovementPage.ClickNew();
        otherRevenueMovementPage.AddDate();
        otherRevenueMovementPage.SelectRevenueTypeButton();
        otherRevenueMovementPage.SelectFromDropDawn("ايرادات اخرى","أنواع الإيرادات");
        otherRevenueMovementPage.AddNotes("Test Notes");
        otherRevenueMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        otherRevenueMovementPage.SelectByKeys("تست");
        otherRevenueMovementPage.ClickSaveButton();
        Assert.assertTrue(otherRevenueMovementPage.IsAmountRequiredErrorDisplayed(), "Type Other Revenue M Error Message Not Displayed");
        driver.navigate().back();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حركة الإيرادات الأخرى")
    @Story("التحقق - طريقة الدفع إلزامية في حركة الإيراد")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ حركة الإيراد بدون اختيار طريقة الدفع، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل طريقة الدفع إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertPaymentMethodRequiredErrorMessage() {
        OtherRevenueMovementPage otherRevenueMovementPage = new OtherRevenueMovementPage(driver);
        otherRevenueMovementPage.NavigationHelper(Pages.OTHERREVENUE);
        otherRevenueMovementPage.ClickNew();
        otherRevenueMovementPage.AddDate();
        otherRevenueMovementPage.SelectRevenueTypeButton();
        otherRevenueMovementPage.SelectFromDropDawn("ايرادات اخرى","أنواع الإيرادات");
        otherRevenueMovementPage.AddValue("1000");
        otherRevenueMovementPage.AddNotes("Test Notes");
        otherRevenueMovementPage.ClickSaveButton();
        Assert.assertTrue(otherRevenueMovementPage.IsPaymentMethodRequiredErrorDisplayed(), "Type Other Revenue M Error Message Not Displayed");
        driver.navigate().back();
    }
}
