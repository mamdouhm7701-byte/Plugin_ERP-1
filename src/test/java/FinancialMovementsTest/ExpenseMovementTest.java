package FinancialMovementsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.FinancialMovements.ExpenseMovementPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExpenseMovementTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حركة المصروفات")
    @Story("إضافة حركة مصروف مع الضريبة والحفظ والطباعة والتحقق من الحسابات")
    @Description("يتحقق هذا الاختبار من إضافة حركة مصروف مع تفعيل الضريبة، والتحقق من صحة الحسابات (صافي ما قبل الضريبة، الضريبة 15%، الإجمالي)، ثم الحفظ والطباعة والتحقق من صحة بيانات التقرير.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddExpenseMovementWithSelectTax_AssertValue() {
        ExpenseMovementPage expenseMovementPage = new ExpenseMovementPage(driver);
        expenseMovementPage.NavigationHelper(Pages.EXPENSE);
        expenseMovementPage.ClickNew();
        expenseMovementPage.AddDate();
        expenseMovementPage.SelectExpenseType();
        expenseMovementPage.SelectFromDropDawn("جديد","النوع");
        expenseMovementPage.AddReference("12");
        expenseMovementPage.AddValue("1000");
        expenseMovementPage.SetCheckboxValue("خاضع للضريبة",true);

        double netBeforeTax = expenseMovementPage.getNetBeforeTax();
        double tax = expenseMovementPage.getTax();
        double total = expenseMovementPage.getTotal();

        Allure.step("التحقق من أن الإجمالي = صافي ما قبل الضريبة + الضريبة");
        Assert.assertEquals(total, netBeforeTax + tax, 0.01, "Screen Calculation Failed");

        double actualTax = expenseMovementPage.getTax();
        double expectedTax = expenseMovementPage.getExpectedTax();

        Allure.step("التحقق من أن الضريبة = 15% من صافي ما قبل الضريبة");
        Assert.assertEquals(actualTax, expectedTax, 0.01, "Tax Calculation Failed");

        double actualTotal = expenseMovementPage.getTotal();
        double expectedTotal = expenseMovementPage.getExpectedTotal();

        Allure.step("التحقق من الإجمالي بناءً على ضريبة 15%");
        Assert.assertEquals(actualTotal, expectedTotal, 0.01, "Total Calculation Failed");

        expenseMovementPage.AddNotes("Test Notes");
        expenseMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        expenseMovementPage.SelectByKeys("تست");
        expenseMovementPage.ClickSaveAndPrintButton();
        expenseMovementPage.AssertTextInReport("جديد");
        expenseMovementPage.AssertNumberInReport("1000");
        expenseMovementPage.AssertDateInReport( expenseMovementPage.GetTodayDate());
        expenseMovementPage.AssertNumberInReport("12");
        expenseMovementPage.AssertTextInReport("Test Notes");
        expenseMovementPage.AssertTextInReport("اخري");
        expenseMovementPage.AssertTextInReport("بنك القاهرة");
        expenseMovementPage.CloserReport();
        expenseMovementPage.AssertReceiptAdded("جديد");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حركة المصروفات")
    @Story("تعديل حركة مصروف مع الضريبة والحفظ والطباعة والتحقق من الحسابات")
    @Description("يتحقق هذا الاختبار من تعديل حركة مصروف موجودة مع تفعيل الضريبة، والتحقق من صحة الحسابات (صافي ما قبل الضريبة، الضريبة 15%، الإجمالي)، ثم الحفظ والطباعة والتحقق من صحة بيانات التقرير بعد التعديل.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditExpenseMovementWithSelectTax_AssertValue() {
        ExpenseMovementPage expenseMovementPage = new ExpenseMovementPage(driver);
        expenseMovementPage.NavigationHelper(Pages.EXPENSE);
        expenseMovementPage.Edit();
        expenseMovementPage.AddDate();
        expenseMovementPage.SelectExpenseType();
        expenseMovementPage.SelectFromDropDawn("أجور ورواتب","النوع");
        expenseMovementPage.AddReference("15");
        expenseMovementPage.AddValue("2000");
        expenseMovementPage.SetCheckboxValue("خاضع للضريبة",true);

        double netBeforeTax = expenseMovementPage.getNetBeforeTax();
        double tax = expenseMovementPage.getTax();
        double total = expenseMovementPage.getTotal();

        Allure.step("التحقق من أن الإجمالي = صافي ما قبل الضريبة + الضريبة");
        Assert.assertEquals(total, netBeforeTax + tax, 0.01, "Screen Calculation Failed");

        double actualTax = expenseMovementPage.getTax();
        double expectedTax = expenseMovementPage.getExpectedTax();

        Allure.step("التحقق من أن الضريبة = 15% من صافي ما قبل الضريبة");
        Assert.assertEquals(actualTax, expectedTax, 0.01, "Tax Calculation Failed");

        double actualTotal = expenseMovementPage.getTotal();
        double expectedTotal = expenseMovementPage.getExpectedTotal();

        Allure.step("التحقق من الإجمالي بناءً على ضريبة 15%");
        Assert.assertEquals(actualTotal, expectedTotal, 0.01, "Total Calculation Failed");

        expenseMovementPage.AddNotes("Test Notes");
        expenseMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        expenseMovementPage.SelectByKeys("دفع جافا");
        expenseMovementPage.ClickSaveAndPrintButton();
        expenseMovementPage.AssertNumberInReport("2000");
        expenseMovementPage.AssertDateInReport(expenseMovementPage.GetTodayDate());
        expenseMovementPage.AssertNumberInReport("15");
        expenseMovementPage.AssertTextInReport("Test Notes");
        expenseMovementPage.AssertTextInReport("اخري");
        expenseMovementPage.AssertTextInReport("بنك القاهرة");
        expenseMovementPage.CloserReport();
        expenseMovementPage.AssertReceiptAdded("أجور ورواتب");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حركة المصروفات")
    @Story("إضافة حركة مصروف بنوع حساب محاسبي والحفظ")
    @Description("يتحقق هذا الاختبار من إضافة حركة مصروف باختيار نوع الحساب المحاسبي، ويشمل إدخال التاريخ ورقم الحساب والمرجع والقيمة والملاحظات وطريقة الدفع، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddExpenseMovementWithSelectAccountantAndClickSave() {
        ExpenseMovementPage expenseMovementPage = new ExpenseMovementPage(driver);
        expenseMovementPage.NavigationHelper(Pages.EXPENSE);
        expenseMovementPage.ClickNew();
        expenseMovementPage.AddDate();
        expenseMovementPage.SelectAcountType();
        expenseMovementPage.SelectFromDropDawn("المبيعات","رقم الحساب");
        expenseMovementPage.AddReference("13");
        expenseMovementPage.AddValue("1000");
        expenseMovementPage.AddNotes("Test Notes");
        expenseMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        expenseMovementPage.SelectByKeys("تست");
        expenseMovementPage.ClickSaveButton();
        expenseMovementPage.AssertReceiptAdded("المبيعات");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حركة المصروفات")
    @Story("إضافة حركة مصروف بنوع مصروف والحفظ")
    @Description("يتحقق هذا الاختبار من إضافة حركة مصروف باختيار نوع المصروف، ويشمل إدخال التاريخ ونوع المصروف والمرجع والقيمة والملاحظات وطريقة الدفع، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddExpenseMovementWithSelectExpenseAndClickSave() {
        ExpenseMovementPage expenseMovementPage = new ExpenseMovementPage(driver);
        expenseMovementPage.NavigationHelper(Pages.EXPENSE);
        expenseMovementPage.ClickNew();
        expenseMovementPage.AddDate();
        expenseMovementPage.SelectExpenseType();
        expenseMovementPage.SelectFromDropDawn("جديد","النوع");
        expenseMovementPage.AddReference("12");
        expenseMovementPage.AddValue("1000");
        expenseMovementPage.AddNotes("Test Notes");
        expenseMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        expenseMovementPage.SelectByKeys("تست");
        expenseMovementPage.ClickSaveButton();
        expenseMovementPage.AssertReceiptAdded("جديد");
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("حركة المصروفات")
    @Story("حذف حركة مصروف")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف جميع حركات المصروفات المضافة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteExpenseMovement() {
        ExpenseMovementPage expenseMovementPage = new ExpenseMovementPage(driver);
        expenseMovementPage.NavigationHelper(Pages.EXPENSE);
        expenseMovementPage.DeleteAll();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حركة المصروفات")
    @Story("التحقق - نوع المصروف إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ حركة المصروف بدون اختيار نوع المصروف، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل النوع إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertTypeExpenseErrorMessage() {
        ExpenseMovementPage expenseMovementPage = new ExpenseMovementPage(driver);
        expenseMovementPage.NavigationHelper(Pages.EXPENSE);
        expenseMovementPage.ClickNew();
        expenseMovementPage.AddDate();
        expenseMovementPage.AddReference("12");
        expenseMovementPage.AddValue("1000");
        expenseMovementPage.AddNotes("Test Notes");
        expenseMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        expenseMovementPage.SelectByKeys("تست");
        expenseMovementPage.ClickSaveButton();
        Assert.assertTrue(expenseMovementPage.IsTypeRequiredErrorDisplayed(), "Type Expense Error Message Not Displayed");
        driver.navigate().back();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حركة المصروفات")
    @Story("التحقق - طريقة الدفع إلزامية في حركة المصروف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ حركة المصروف بدون اختيار طريقة الدفع، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل طريقة الدفع إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertPaymentMethodErrorMessage() {
        ExpenseMovementPage expenseMovementPage = new ExpenseMovementPage(driver);
        expenseMovementPage.NavigationHelper(Pages.EXPENSE);
        expenseMovementPage.ClickNew();
        expenseMovementPage.AddDate();
        expenseMovementPage.SelectExpenseType();
        expenseMovementPage.SelectFromDropDawn("جديد","النوع");
        expenseMovementPage.AddReference("12");
        expenseMovementPage.AddValue("1000");
        expenseMovementPage.AddNotes("Test Notes");
        expenseMovementPage.ClickSaveButton();
        Assert.assertTrue(expenseMovementPage.IsPaymentMethodRequiredErrorDisplayed(), "Payment Method Error Message Not Displayed");
        driver.navigate().back();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حركة المصروفات")
    @Story("التحقق - خيار طريقة الدفع إلزامي في حركة المصروف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ حركة المصروف عند اختيار طريقة الدفع (أخرى) بدون تحديد الخيار الفرعي، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل خيار طريقة الدفع إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertPaymentOptionMethodErrorMessage() {
        ExpenseMovementPage expenseMovementPage = new ExpenseMovementPage(driver);
        expenseMovementPage.NavigationHelper(Pages.EXPENSE);
        expenseMovementPage.ClickNew();
        expenseMovementPage.SelectExpenseType();
        expenseMovementPage.SelectFromDropDawn("جديد","النوع");
        expenseMovementPage.AddReference("12");
        expenseMovementPage.AddValue("1000");
        expenseMovementPage.AddNotes("Test Notes");
        expenseMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        expenseMovementPage.ClickSaveButton();
        Assert.assertTrue(expenseMovementPage.IsPaymentMethodRequiredErrorDisplayed(), "Payment Method Error Message Not Displayed");
        driver.navigate().back();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("حركة المصروفات")
    @Story("التحقق - المبلغ إلزامي في حركة المصروف")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ حركة المصروف بدون إدخال المبلغ، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل المبلغ إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertValueErrorMessage() {
        ExpenseMovementPage expenseMovementPage = new ExpenseMovementPage(driver);
        expenseMovementPage.NavigationHelper(Pages.EXPENSE);
        expenseMovementPage.ClickNew();
        expenseMovementPage.AddDate();
        expenseMovementPage.SelectExpenseType();
        expenseMovementPage.SelectFromDropDawn("أجور ورواتب","النوع");
        expenseMovementPage.AddReference("12");
        expenseMovementPage.AddNotes("Test Notes");
        expenseMovementPage.SelectFromDropDawn("اخري","طريقة الدفع");
        expenseMovementPage.SelectByKeys("تست");
        expenseMovementPage.ClickSaveButton();
        Assert.assertTrue(expenseMovementPage.IsAmountRequiredErrorDisplayed(), "Value Error Message Not Displayed");
        driver.navigate().back();
    }
}