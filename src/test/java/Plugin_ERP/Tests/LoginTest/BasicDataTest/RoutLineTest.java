package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.BankPage;
import Plugin_ERP.System_Modules.Login.BasicData.RoutLine;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.v140.page.Page;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public class RoutLineTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("خط السير")
    @Story("إضافة خط سير ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة خط سير جديد ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي واختيار نوع خط السير (فاتورة) وإضافة الملاحظات وتفعيل خانة (تفعيل موقع العميل)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidRoutLineTest() {
        System.out.println("RoutLine test started");
        RoutLine routLine = new RoutLine(driver);
        routLine.OpenRoutLinePage();
        routLine.ClickNew();
        routLine.AddArabicName("فاتورة");
        routLine.AddEnglishName("Bills");
        routLine.SelectRadiBouton("فاتورة");
        routLine.AddNoteForDealer("فاتورة");
        routLine.SelectCheckboxByText("تفعيل موقع العميل", true);
        routLine.ClickSaveButton();
        routLine.AssertReceiptAdded("فاتورة");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("خط السير")
    @Story("تعديل خط سير ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل خط سير موجود ببيانات صحيحة، ويشمل تعديل الاسم العربي والإنجليزي وتغيير نوع خط السير إلى (تحصيل) وتعديل الملاحظات وإلغاء تفعيل خانة (تفعيل موقع العميل)، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditRoutLine() {
        RoutLine routLine = new RoutLine(driver);
        routLine.NavigationHelper(Pages.ROUTELINESTATUES);
        routLine.Edit();
        routLine.AddArabicName("تحصيل");
        routLine.AddEnglishName("Collection");
        routLine.SelectRadiBouton("تحصيل");
        routLine.AddNoteForDealer("تحصيل");
        routLine.SelectCheckboxByText("تفعيل موقع العميل", false);
        routLine.ClickSaveButton();
        routLine.AssertReceiptAdded("تحصيل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("خط السير")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة خط سير بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidRoutLineTest() {
        System.out.println("RoutLine test started");
        RoutLine routLine = new RoutLine(driver);
        routLine.NavigationHelper(Pages.ROUTELINESTATUES);
        routLine.ClickNew();
        routLine.IsArabicNameRequiredErrorDisplayed();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("خط السير")
    @Story("التحقق - خطأ عند تكرار نوع خط السير")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة خط سير بنوع مكرر موجود مسبقاً، ويتحقق من ظهور رسالة خطأ تفيد بأن هذا النوع مستخدم بالفعل.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DuplicateRoutLineTest() {
        System.out.println("Duplicate RoutLine test started");
        RoutLine routLine = new RoutLine(driver);
        routLine.NavigationHelper(Pages.ROUTELINESTATUES);
        routLine.ClickNew();
        routLine.AddArabicName("راديوم بوتوم مكرر");
        routLine.SelectRadiBouton("تحصيل");
        routLine.ClickSaveButton();
        routLine.isErrorPopupDisplayed();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("خط السير")
    @Story("حذف خط سير")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف خط سير موجود بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteRoutLine() {
        RoutLine routLine = new RoutLine(driver);
        routLine.NavigationHelper(Pages.ROUTELINESTATUES);
        routLine.Delete();
    }
}