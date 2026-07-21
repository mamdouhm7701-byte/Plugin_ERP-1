package FinancialMovementsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.FinancialMovements.CustomerReceiptPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CustomerReceiptTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("إضافة سند قبض عميل مع توزيع على الفواتير")
    @Description("يتحقق هذا الاختبار من إضافة سند قبض عميل بنجاح مع توزيع المبلغ على الفواتير، ويشمل إدخال التاريخ والقيمة واختيار العميل وطريقة الدفع والمندوب وإضافة الملاحظات، ثم توزيع المبلغ على الفاتورة وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddCustomerWithSaveAndCustomerReceiptTest() {
        CustomerReceiptPage customerReceiptPage = new CustomerReceiptPage(driver);
        customerReceiptPage.NavigationHelper(Pages.COLLICTION);
        customerReceiptPage.ClickNew();
        customerReceiptPage.AddDate();
        customerReceiptPage.SelectFromDropDawn("pre","العميل");
        customerReceiptPage.AddValue("100");
        customerReceiptPage.SelectFromDropDawn("عميل ضريبى نيو كود","العميل");
        customerReceiptPage.SelectFromDropDawn("اخري","طريقة الدفع");
        customerReceiptPage.SelectByKeys("تست");
        customerReceiptPage.SelectFromDropDawn("محمد السيد","مندوب");
        customerReceiptPage.AddNotes("Test Notes");
        customerReceiptPage.SaveCustomButton();
        customerReceiptPage.HandelCustomReceipt(1,"100");
        customerReceiptPage.ClickSaveButton();
        customerReceiptPage.AssertReceiptAdded("عميل ضريبى نيو كود");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("إضافة سند قبض عميل بالحفظ فقط")
    @Description("يتحقق هذا الاختبار من إضافة سند قبض عميل بنجاح عن طريق الحفظ المباشر، ويشمل إدخال التاريخ والقيمة واختيار العميل وطريقة الدفع والمندوب وإضافة الملاحظات، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddResetWithSaveTest() {
        CustomerReceiptPage customerReceiptPage = new CustomerReceiptPage(driver);
        customerReceiptPage.NavigationHelper(Pages.COLLICTION);
        customerReceiptPage.ClickNew();
        customerReceiptPage.AddDate();
        customerReceiptPage.AddValue("1000");
        customerReceiptPage.SelectFromDropDawn("pre","العميل");
        customerReceiptPage.SelectFromDropDawn("اخري","طريقة الدفع");
        customerReceiptPage.SelectByKeys("تست");
        customerReceiptPage.SelectFromDropDawn("محمد السيد","مندوب");
        customerReceiptPage.AddNotes("Test Notes");
        customerReceiptPage.ClickSaveButton();
        customerReceiptPage.AssertReceiptAdded("pre");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("إضافة سند قبض عميل مع الحفظ والطباعة والتحقق من التقرير")
    @Description("يتحقق هذا الاختبار من إضافة سند قبض عميل بنجاح مع الحفظ والطباعة، والتحقق من صحة بيانات التقرير (العميل، المبلغ، الملاحظات، طريقة الدفع، البنك، التاريخ)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddNewResetWithSaveAndPrint() {
        CustomerReceiptPage customerReceiptPage = new CustomerReceiptPage(driver);
        customerReceiptPage.NavigationHelper(Pages.COLLICTION);
        customerReceiptPage.ClickNew();
        customerReceiptPage.AddDate();
        customerReceiptPage.AddValue("1500.01");
        customerReceiptPage.SelectFromDropDawn("pre","العميل");
        customerReceiptPage.SelectFromDropDawn("اخري","طريقة الدفع");
        customerReceiptPage.SelectByKeys("تست");
        customerReceiptPage.SelectFromDropDawn("محمد السيد","مندوب");
        customerReceiptPage.AddNotes("Test Notes");
        customerReceiptPage.ClickSaveAndPrintButton();
        customerReceiptPage.AssertTextInReport("pre");
        customerReceiptPage.AssertNumberInReport("1500.01");
        customerReceiptPage.AssertTextInReport("Test Notes");
        customerReceiptPage.AssertTextInReport("اخري");
        customerReceiptPage.AssertTextInReport("بنك القاهرة");
        customerReceiptPage.AssertDateInReport(customerReceiptPage.GetTodayDate());
        customerReceiptPage.CloserReport();
        customerReceiptPage.AssertReceiptAdded("pre");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("تعديل سند قبض عميل مع الحفظ والطباعة والتحقق من التقرير")
    @Description("يتحقق هذا الاختبار من تعديل سند قبض عميل موجود مع الحفظ والطباعة، والتحقق من صحة بيانات التقرير بعد التعديل (العميل، المبلغ، الملاحظات، البنك، التاريخ)، والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditeAfterReportTest() {
        CustomerReceiptPage customerReceiptPage = new CustomerReceiptPage(driver);
        customerReceiptPage.NavigationHelper(Pages.COLLICTION);
        customerReceiptPage.Edit();
        customerReceiptPage.AddDate();
        customerReceiptPage.AddValue("2000");
        customerReceiptPage.SelectFromDropDawn("موقع","العميل");
        customerReceiptPage.SelectFromDropDawn("اخري","طريقة الدفع");
        customerReceiptPage.SelectByKeys("55");
        customerReceiptPage.SelectFromDropDawn("محمود","مندوب");
        customerReceiptPage.AddNotes("Test Second Notes");
        customerReceiptPage.ClickSaveAndPrintButton();
        customerReceiptPage.AssertTextInReport("موقع");
        customerReceiptPage.AssertNumberInReport("2000");
        customerReceiptPage.AssertTextInReport("Test Second Notes");
        customerReceiptPage.AssertTextInReport("تست");
        customerReceiptPage.AssertDateInReport(customerReceiptPage.GetTodayDate());
        customerReceiptPage.CloserReport();
        customerReceiptPage.AssertReceiptAdded("موقع");
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("حذف سند قبض عميل")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف جميع سندات قبض العملاء المضافة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteCustomerReceiptTest() {
        CustomerReceiptPage customerReceiptPage = new CustomerReceiptPage(driver);
        customerReceiptPage.NavigationHelper(Pages.COLLICTION);
        customerReceiptPage.DeleteAll();
        driver.navigate().refresh();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - العميل إلزامي في سند القبض")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ سند القبض بدون اختيار العميل، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل العميل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertRequiredErrorsMessageForAgent() {
        CustomerReceiptPage customerReceiptPage = new CustomerReceiptPage(driver);
        customerReceiptPage.NavigationHelper(Pages.COLLICTION);
        customerReceiptPage.ClickNew();
        customerReceiptPage.AddDate();
        customerReceiptPage.ClickSaveButton();
        Assert.assertTrue(customerReceiptPage.IsAgentRequiredErrorDisplayed(), "Agent error message should be displayed");
        driver.navigate().back();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - المبلغ وطريقة الدفع إلزاميان في سند القبض")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ سند القبض بدون إدخال المبلغ وطريقة الدفع، ويتحقق من ظهور رسالتي خطأ تفيدان بأن حقلي المبلغ وطريقة الدفع إلزاميان.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertRequiredErrorsMessageForAmount() {
        CustomerReceiptPage customerReceiptPage = new CustomerReceiptPage(driver);
        customerReceiptPage.NavigationHelper(Pages.COLLICTION);
        customerReceiptPage.ClickNew();
        customerReceiptPage.AddDate();
        customerReceiptPage.SelectFromDropDawn("pre","العميل");
        customerReceiptPage.ClickSaveButton();
        Assert.assertTrue(customerReceiptPage.IsAmountRequiredErrorDisplayed(), "Amount error message should be displayed");
        Assert.assertTrue(customerReceiptPage.IsPaymentMethodRequiredErrorDisplayed(), "Payment method error message should be displayed");
        driver.navigate().back();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - طريقة الدفع إلزامية في سند القبض")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ سند القبض بدون اختيار طريقة الدفع، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل طريقة الدفع إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertRequiredErrorsMessageForPaymentMethod() {
        CustomerReceiptPage customerReceiptPage = new CustomerReceiptPage(driver);
        customerReceiptPage.NavigationHelper(Pages.COLLICTION);
        customerReceiptPage.ClickNew();
        customerReceiptPage.AddDate();
        customerReceiptPage.SelectFromDropDawn("pre","العميل");
        customerReceiptPage.AddValue("1000");
        customerReceiptPage.ClickSaveButton();
        Assert.assertTrue(customerReceiptPage.IsPaymentMethodRequiredErrorDisplayed(), "Payment method error message should be displayed");
        driver.navigate().back();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - خيار طريقة الدفع إلزامي في سند القبض")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ سند القبض عند اختيار طريقة الدفع (أخرى) بدون تحديد الخيار الفرعي، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل خيار طريقة الدفع إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertRequiredErrorsMessageForPaymentMethodOption() {
        CustomerReceiptPage customerReceiptPage = new CustomerReceiptPage(driver);
        customerReceiptPage.NavigationHelper(Pages.COLLICTION);
        customerReceiptPage.ClickNew();
        customerReceiptPage.AddDate();
        customerReceiptPage.SelectFromDropDawn("pre","العميل");
        customerReceiptPage.AddValue("1000");
        customerReceiptPage.SelectFromDropDawn("اخري","طريقة الدفع");
        customerReceiptPage.ClickSaveButton();
        Assert.assertTrue(customerReceiptPage.IsPaymentMethodRequiredErrorDisplayed(), "Payment method option error message should be displayed");
        driver.navigate().back();
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الحركات المالية")
    @Story("التحقق - خطأ عند إدخال تاريخ قديم في سند القبض")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة سند قبض بتاريخ قديم، ويتحقق من ظهور رسالة خطأ تفيد بعدم إمكانية استخدام تاريخ سابق.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddOldDate() {
        CustomerReceiptPage customerReceiptPage = new CustomerReceiptPage(driver);
        customerReceiptPage.NavigationHelper(Pages.COLLICTION);
        customerReceiptPage.ClickNew();
        customerReceiptPage.AddPastYearDate();
        customerReceiptPage.AddValue("1000");
        customerReceiptPage.SelectFromDropDawn("pre","العميل");
        customerReceiptPage.SelectFromDropDawn("اخري","طريقة الدفع");
        customerReceiptPage.SelectByKeys("تست");
        customerReceiptPage.SelectFromDropDawn("محمد السيد","مندوب");
        customerReceiptPage.AddNotes("Test Notes");
        customerReceiptPage.ClickSaveButton();
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue(customerReceiptPage.isErrorPopupDisplayed(), "Old date error message should be displayed");
        driver.navigate().back();
    }
}