package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.BankPage;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

public class BankTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("إضافة بنك ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة بنك جديد ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي والعنوان ورقم الهاتف والبريد الإلكتروني ورقم الحساب والآيبان والملاحظات، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidBankTest() {
        BankPage bankPage = new BankPage(driver);
        bankPage.NavigationHelper(Pages.BANK);
        bankPage.ClickNew();
        bankPage.AddArabicName("تست");
        bankPage.AddEnglishName("Test");
        bankPage.AddAdress("عنوان تفصيلى");
        bankPage.AddPhoneNumber(bankPage.generateRandomNumber());
        bankPage.AddEmail("mm@Example.com");
        bankPage.SelectFromDropDawn("المبيعات","رقم الحساب");
        bankPage.AddAccountNumber(bankPage.generateRandomNumber());
        bankPage.AddIban(bankPage.generateRandomNumber());
        bankPage.AddNoteForDealer("تفاصيل البنك ");
        bankPage.ClickSaveButton();
        bankPage.AssertReceiptAdded("تست");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("تعديل بنك ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل بنك موجود ببيانات صحيحة، ويشمل تعديل الاسم العربي والإنجليزي والعنوان ورقم الهاتف، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditBankTest() {
        BankPage bankPage = new BankPage(driver);
        bankPage.NavigationHelper(Pages.BANK);
        bankPage.Edit();
        bankPage.AddArabicName("تست تعديل");
        bankPage.AddEnglishName("Test Edit");
        bankPage.AddAdress("عنوان تفصيلى تعديل");
        bankPage.AddPhoneNumber(bankPage.generateRandomNumber());
        bankPage.ClickSaveButton();
        bankPage.AssertReceiptAdded("تست تعديل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("حذف بنك")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف بنك موجود بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteBankTest() {
        BankPage bankPage = new BankPage(driver);
        bankPage.NavigationHelper(Pages.BANK);
        bankPage.Delete();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة بنك بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidBankTest() {
        System.out.println("Invalid Bank test started");
        BankPage bankPage = new BankPage(driver);
        bankPage.NavigationHelper(Pages.BANK);
        bankPage.ClickNew();
        bankPage.ClickSaveButton();
        bankPage.IsArabicNameRequiredErrorDisplayed();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("التحقق - البريد الإلكتروني غير صحيح")
    @Description("يتحقق هذا الاختبار من أن النظام يرفض البريد الإلكتروني غير الصحيح عند إضافة بنك، ويتحقق من ظهور رسالة خطأ تفيد بأن صيغة البريد الإلكتروني غير صحيحة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidEmailBankTest() {
        System.out.println("Invalid Email Bank test started");
        BankPage bankPage = new BankPage(driver);
        bankPage.NavigationHelper(Pages.BANK);
        bankPage.ClickNew();
        bankPage.AddArabicName(("Invalid"));
        bankPage.AddEmail("InvalidEmail");
        bankPage.ClickSaveButton();
        bankPage.IsRightEmailErrorDisplayed();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("البنوك")
    @Story("التحقق - رقم الحساب البنكي يحتوي على حروف")
    @Description("يتحقق هذا الاختبار من أن النظام يرفض رقم الحساب البنكي الذي يحتوي على أحرف، ويتحقق من ظهور رسالة خطأ تفيد بأن رقم الحساب يجب أن يحتوي على أرقام فقط.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidBankNumberTest() {
        System.out.println("Invalid Bank Number test started");
        BankPage bankPage = new BankPage(driver);
        bankPage.NavigationHelper(Pages.BANK);
        bankPage.ClickNew();
        bankPage.AddArabicName(("Invalid"));
        bankPage.AddAccountNumber("Invalid");
        bankPage.ClickSaveButton();
        bankPage.AssertNumberOfBankWithOutLetter();
    }
}