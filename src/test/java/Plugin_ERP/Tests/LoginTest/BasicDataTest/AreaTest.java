package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.AreaPage;
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

public class AreaTest extends BaseTest {



    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("إضافة منطقة ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع إضافة منطقة جديدة ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي والملاحظات وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidAreaTest() {
        AreaPage areaPage = new AreaPage(driver);
        areaPage.NavigationHelper(Pages.AREA);
        areaPage.ClickNew();
        areaPage.AddArabicName("تست");
        areaPage.AddEnglishName("Test");
        areaPage.AddNoteForDealer("Test Notes");
        areaPage.ClickSaveButton();
        areaPage.AssertReceiptAdded("تست");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("تعديل منطقة ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع تعديل منطقة موجودة ببيانات صحيحة، ويشمل تعديل الاسم العربي والإنجليزي والملاحظات وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditArea(){
        AreaPage areaPage = new AreaPage(driver);
        areaPage.NavigationHelper(Pages.AREA);
        areaPage.Edit();
        areaPage.AddArabicName("تست تعديل");
        areaPage.AddEnglishName("Test Edit");
        areaPage.AddNoteForDealer("Test Edit Notes");
        areaPage.ClickSaveButton();
        areaPage.AssertReceiptAdded("تست تعديل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("حذف منطقة")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف منطقة موجودة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteArea(){
        AreaPage areaPage = new AreaPage(driver);
        areaPage.NavigationHelper(Pages.AREA);
        areaPage.Delete();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المناطق")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة منطقة جديدة بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidAreaTest() {
        System.out.println("Invalid Area test started");
        AreaPage areaPage = new AreaPage(driver);
        areaPage.NavigationHelper(Pages.AREA);
        areaPage.ClickNew();
        areaPage.AddArabicName("");
        areaPage.ClickSaveButton();
        areaPage.IsArabicNameRequiredErrorDisplayed();
    }
}