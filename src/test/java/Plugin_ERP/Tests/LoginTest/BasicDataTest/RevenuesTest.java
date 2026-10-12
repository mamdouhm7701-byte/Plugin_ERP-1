package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.RevenuesPage;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.testng.AllureTestNg;
import Plugin_ERP.System_Modules.Login.BasicData.BankPage;
import Plugin_ERP.System_Modules.Login.BasicData.RoutLine;
import io.qameta.allure.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public class RevenuesTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الإيرادات")
    @Story("إضافة إيراد ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة إيراد جديد ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي واختيار رقم الحساب وتفعيل خانة (عام)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidRevenuesTest() {
        System.out.println("Revenues test started");
        RevenuesPage revenuesPage = new RevenuesPage(driver);
        revenuesPage.OpenRevenuesPage();
        revenuesPage.ClickNew();
        revenuesPage.AddArabicName("ايراد جديد");
        revenuesPage.AddEnglishName("New Revenues");
        revenuesPage.SelectFromDropDawn("تسويق جوجل", "رقم الحساب");
        revenuesPage.SelectCheckboxByText("عام",true);
        revenuesPage.ClickSaveButton();
        revenuesPage.AssertReceiptAdded("ايراد جديد");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الإيرادات")
    @Story("تعديل إيراد ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل إيراد موجود ببيانات صحيحة، ويشمل تعديل الاسم العربي والإنجليزي وتغيير رقم الحساب وإلغاء تفعيل خانة (عام)، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditRevenuesTest() {
        RevenuesPage revenuesPage = new RevenuesPage(driver);
        revenuesPage.NavigationHelper(Pages.REVENUES);
        revenuesPage.Edit();
        revenuesPage.AddArabicName("ايراد جديد معدل");
        revenuesPage.AddEnglishName("New Revenues");
        revenuesPage.SelectFromDropDawn("تسويق جوجل", "رقم الحساب");
        revenuesPage.SelectCheckboxByText("عام",false);
        revenuesPage.ClickSaveButton();
        revenuesPage.AssertReceiptAdded("ايراد جديد معدل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الإيرادات")
    @Story("حذف إيراد")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف إيراد موجود بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteRevenuesTest() {
        RevenuesPage revenuesPage = new RevenuesPage(driver);
        revenuesPage.NavigationHelper(Pages.REVENUES);
        revenuesPage.Delete();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الإيرادات")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة إيراد بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EmptyRevenuesTest() {
        System.out.println("Empty Revenues test started");
        RevenuesPage revenuesPage = new RevenuesPage(driver);
        revenuesPage.NavigationHelper(Pages.REVENUES);
        revenuesPage.ClickNew();
        revenuesPage.IsArabicNameRequiredErrorDisplayed();
    }
}