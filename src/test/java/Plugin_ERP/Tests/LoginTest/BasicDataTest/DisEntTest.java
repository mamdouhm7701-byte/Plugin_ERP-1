package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.DistributionEntityPage;
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

public class DisEntTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("إضافة جهة صرف ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة جهة صرف جديدة ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي واختيار رقم الحساب، والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidDisEntTest() {
        System.out.println("Distribution Entity test started");
        DistributionEntityPage distributionEntityPage = new DistributionEntityPage(driver);
        distributionEntityPage.NavigationHelper(Pages.DISTRIBUTION);
        distributionEntityPage.ClickNew();
        distributionEntityPage.AddArabicName("جهة صرف جديدة");
        distributionEntityPage.AddEnglishName("New DisEnt");
        distributionEntityPage.SelectFromDropDawn("تسويق جوجل","رقم الحساب");
        distributionEntityPage.ClickSaveButton();
        distributionEntityPage.AssertReceiptAdded("جهة صرف جديدة");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("تعديل جهة صرف ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل جهة صرف موجودة ببيانات صحيحة، ويشمل تعديل الاسم العربي والإنجليزي وتغيير رقم الحساب، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditeDistributionEntity() {
        DistributionEntityPage distributionEntityPage = new DistributionEntityPage(driver);
        distributionEntityPage.NavigationHelper(Pages.DISTRIBUTION);
        distributionEntityPage.Edit();
        distributionEntityPage.AddArabicName("جهة صرف جديدة تعديل");
        distributionEntityPage.AddEnglishName("New DisEnt Edit");
        distributionEntityPage.SelectFromDropDawn("تسويق جوجل","رقم الحساب");
        distributionEntityPage.ClickSaveButton();
        distributionEntityPage.AssertReceiptAdded("جهة صرف جديدة تعديل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("حذف جهة صرف")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف جميع جهات الصرف المضافة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteDistributionEntity() {
        DistributionEntityPage distributionEntityPage = new DistributionEntityPage(driver);
        distributionEntityPage.NavigationHelper(Pages.DISTRIBUTION);
        distributionEntityPage.DeleteAll();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("جهات الصرف")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة جهة صرف بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidDisEntTest() {
        System.out.println("Invalid Distribution Entity test started");
        DistributionEntityPage distributionEntityPage = new DistributionEntityPage(driver);
        distributionEntityPage.NavigationHelper(Pages.DISTRIBUTION);
        distributionEntityPage.ClickNew();
        distributionEntityPage.AddArabicName("");
        distributionEntityPage.ClickSaveButton();
        distributionEntityPage.IsArabicNameRequiredErrorDisplayed();
    }
}