package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.PaymentPage;
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

public class PaymentTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("إضافة طريقة دفع ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة طريقة دفع جديدة ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي والملاحظات واختيار اسم البنك، وتفعيل خانتي (عام) و(طريقة دفع افتراضية للتكامل)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidPaymentTest() {
        System.out.println("Payment test started");
        PaymentPage paymentPage = new PaymentPage(driver);
        paymentPage.NavigationHelper(Pages.PAYMENT);
        paymentPage.ClickNew();
        paymentPage.AddArabicName("طريقة دفع");
        paymentPage.AddEnglishName("Payment Method");
        paymentPage.AddNoteForDealer("Payment Method");
        paymentPage.SelectFromDropDawn("البنك الاول","اسم البنك");
        paymentPage.SelectCheckboxByText("عام",true);
        paymentPage.SelectCheckboxByText("طريقة دفع افتراضية للتكامل",true);
        paymentPage.ClickSaveButton();
        paymentPage.AssertReceiptAdded("طريقة دفع");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("تعديل طريقة دفع ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل طريقة دفع موجودة ببيانات صحيحة، ويشمل تعديل الاسم العربي والإنجليزي والملاحظات وتغيير اسم البنك، وإلغاء تفعيل خانتي (عام) و(طريقة دفع افتراضية للتكامل)، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditPayment() {
        PaymentPage paymentPage = new PaymentPage(driver);
        paymentPage.NavigationHelper(Pages.PAYMENT);
        paymentPage.Edit();
        paymentPage.AddArabicName("طريقة دفع معدلة");
        paymentPage.AddEnglishName("Payment Method Modified");
        paymentPage.AddNoteForDealer("Payment Method Modified");
        paymentPage.SelectFromDropDawn("بنك اتوميشن","اسم البنك");
        paymentPage.SelectCheckboxByText("عام",false);
        paymentPage.SelectCheckboxByText("طريقة دفع افتراضية للتكامل",false);
        paymentPage.ClickSaveButton();
        paymentPage.AssertReceiptAdded("طريقة دفع معدلة");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("حذف طريقة دفع")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف طريقة دفع موجودة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeletePayment() {
        PaymentPage paymentPage = new PaymentPage(driver);
        paymentPage.NavigationHelper(Pages.PAYMENT);
        paymentPage.Delete();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة طريقة دفع بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidPaymentTest() {
        LogUtils.info("Invalid Payment test started");
        PaymentPage paymentPage = new PaymentPage(driver);
        paymentPage.NavigationHelper(Pages.PAYMENT);
        paymentPage.ClickNew();
        paymentPage.AddArabicName("");
        paymentPage.ClickSaveButton();
        paymentPage.IsArabicNameRequiredErrorDisplayed();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("طرق الدفع")
    @Story("التحقق - اسم البنك إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة طريقة دفع بدون اختيار اسم البنك، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل اسم البنك إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertBankNameRequiredMessage() {
        LogUtils.info("Assert Bank Name Required Message test started");
        PaymentPage paymentPage = new PaymentPage(driver);
        paymentPage.NavigationHelper(Pages.PAYMENT);
        paymentPage.ClickNew();
        paymentPage.AddArabicName("طريقة دفع");
        paymentPage.ClickSaveButton();
        paymentPage.IsBankRequiredErrorDisplayed();
    }
}