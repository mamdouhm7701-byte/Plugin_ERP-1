package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.ExpensesPage;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

public class ExpensesTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المصروفات")
    @Story("إضافة مصروف ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة مصروف جديد ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي واختيار رقم الحساب وتفعيل خانة (عام)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidExpensesTest() {
        System.out.println("Expenses test started");
        ExpensesPage expensesPage = new ExpensesPage(driver);
        expensesPage.NavigationHelper(Pages.EXPENSES);
        expensesPage.ClickNew();
        expensesPage.AddArabicName("مصروفات");
        expensesPage.AddEnglishName("Expenses");
        expensesPage.SelectFromDropDawn("تسويق جوجل", "رقم الحساب");
        expensesPage.SelectCheckboxByText("عام", true);
        expensesPage.ClickSaveButton();
        expensesPage.AssertReceiptAdded("مصروفات");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المصروفات")
    @Story("تعديل مصروف ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل مصروف موجود ببيانات صحيحة، ويشمل تعديل الاسم العربي وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditeExpenses() {
        ExpensesPage expensesPage = new ExpensesPage(driver);
        expensesPage.NavigationHelper(Pages.EXPENSES);
        expensesPage.Edit();
        expensesPage.AddArabicName("مصروفات تعديل");
        expensesPage.ClickSaveButton();
        expensesPage.AssertReceiptAdded("مصروفات تعديل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المصروفات")
    @Story("حذف مصروف")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف مصروف موجود بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteExpenses() {
        ExpensesPage expensesPage = new ExpensesPage(driver);
        expensesPage.NavigationHelper(Pages.EXPENSES);
        expensesPage.Delete();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المصروفات")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة مصروف بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidExpensesTest() {
        System.out.println("Invalid Expenses test started");
        ExpensesPage expensesPage = new ExpensesPage(driver);
        expensesPage.NavigationHelper(Pages.EXPENSES);
        expensesPage.ClickNew();
        expensesPage.ClickSaveButton();
        expensesPage.IsArabicNameRequiredErrorDisplayed();
    }
}