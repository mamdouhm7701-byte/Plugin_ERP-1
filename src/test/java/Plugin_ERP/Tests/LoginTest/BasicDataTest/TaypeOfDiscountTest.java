package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.TypesofdiscountsPage;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

public class TaypeOfDiscountTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع الخصومات")
    @Story("إضافة نوع خصم ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة نوع خصم جديد ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي والكود السري وقيمة الخصم، وإضافة المستخدمين المصرح لهم، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidTaypeOfDiscountTest() {
        System.out.println("TaypeOfDiscount test started");
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.ClickNew();
        typesofdiscountsPage.AddArabicName("خصم تجريبي");
        typesofdiscountsPage.AddEnglishName("Test Discount");
        typesofdiscountsPage.AddSecretCode(typesofdiscountsPage.generateRandomNumber());
        typesofdiscountsPage.AddDiscount(5);
        typesofdiscountsPage.ClickToAddUsers();
        typesofdiscountsPage.ClickSelectUsers(1);
        typesofdiscountsPage.ClickCloseButton();
        typesofdiscountsPage.ClickSaveButton();
        typesofdiscountsPage.AssertReceiptAdded("خصم تجريبي");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع الخصومات")
    @Story("تعديل نوع خصم ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل نوع خصم موجود ببيانات صحيحة، ويشمل تعديل الاسم العربي والإنجليزي وتفعيل خانة (فعال) وتغيير الكود السري وقيمة الخصم وإضافة مستخدمين جدد، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditTypeOfDiscount() {
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.Edit();
        typesofdiscountsPage.AddArabicName("خصم معدل");
        typesofdiscountsPage.AddEnglishName("Test Discount");
        typesofdiscountsPage.SmartCheck("فعال",true);
        typesofdiscountsPage.SmartCheck("فعال",true);
        typesofdiscountsPage.AddSecretCode(typesofdiscountsPage.generateRandomNumber());
        typesofdiscountsPage.AddDiscount(10);
        typesofdiscountsPage.ClickToAddUsers();
        typesofdiscountsPage.ClickSelectUsers(1);
        typesofdiscountsPage.ClickCloseButton();
        typesofdiscountsPage.ClickSaveButton();
        typesofdiscountsPage.AssertReceiptAdded("خصم معدل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع الخصومات")
    @Story("حذف نوع خصم")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف نوع خصم موجود بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteTypeOfDiscount() {
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.Delete();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("أنواع الخصومات")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة نوع خصم بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertArabicNameRequired() {
        System.out.println("RequiredFieldsTaypeOfDiscount test started");
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.ClickNew();
        typesofdiscountsPage.AddArabicName("");
        typesofdiscountsPage.AddSecretCode(typesofdiscountsPage.generateRandomNumber());
        typesofdiscountsPage.AddDiscount(10);
        typesofdiscountsPage.ClickSaveButton();
        Assert.assertTrue(typesofdiscountsPage.IsArabicNameRequiredErrorDisplayed());
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("أنواع الخصومات")
    @Story("التحقق - الكود السري إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة نوع خصم بدون إدخال الكود السري، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل الكود السري إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertSecretCodeRequired() {
        System.out.println("RequiredFieldsTaypeOfDiscount test started");
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.ClickNew();
        typesofdiscountsPage.AddArabicName("test");
        typesofdiscountsPage.AddDiscount(10);
        typesofdiscountsPage.ClickSaveButton();
        Assert.assertTrue(typesofdiscountsPage.IsSecretCodeRequiredErrorDisplayed());
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("أنواع الخصومات")
    @Story("التحقق - نسبة الخصم إلزامية")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة نوع خصم بدون إدخال نسبة الخصم، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل نسبة الخصم إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertPercentageRequired() {
        System.out.println("RequiredFieldsTaypeOfDiscount test started");
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.ClickNew();
        typesofdiscountsPage.AddArabicName("test");
        typesofdiscountsPage.AddSecretCode(typesofdiscountsPage.generateRandomNumber());
        typesofdiscountsPage.ClickSaveButton();
        Assert.assertTrue(typesofdiscountsPage.IsPercentageRequiredErrorDisplayed());
    }
}