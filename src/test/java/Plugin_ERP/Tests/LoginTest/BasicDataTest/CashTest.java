package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.CashPage;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Utiel.PropertyReader;
import Plugin_ERP.Listners.TestNGListeners;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

public class CashTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("إضافة خزنة ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة خزنة جديدة ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي والملاحظات، واختيار رقم الحساب وتفعيل خانة (عام)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidCashTest() {
        System.out.println("Cash test started");
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();
        cashPage.AddArabicName("55");
        cashPage.AddEnglishName("55");
        cashPage.AddNoteForDealer("تست");
        cashPage.SelectFromDropDawn("المبيعات","رقم الحساب");
        cashPage.SelectCheckboxByText("عام",true);
        cashPage.ClickSaveButton();
        cashPage.AssertReceiptAdded("55");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("تعديل خزنة ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل خزنة موجودة ببيانات صحيحة، ويشمل تعديل الاسم العربي وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditCash() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.Edit();
        cashPage.AddArabicName("معدل");
        cashPage.ClickSaveButton();
        cashPage.AssertReceiptAdded("معدل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("حذف خزنة")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف خزنة موجودة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteCash() {
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.Delete();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة خزنة بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidCashTest() {
        System.out.println("Invalid Cash test started");
        CashPage cashPage = new CashPage(driver);
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();
        cashPage.AddArabicName("");
        Assert.assertTrue(cashPage.IsArabicNameRequiredErrorDisplayed());
    }

}