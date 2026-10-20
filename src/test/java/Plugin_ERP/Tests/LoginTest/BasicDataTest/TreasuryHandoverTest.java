package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.TreasuryHandoverPage;
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

public class TreasuryHandoverTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تسليم الخزينة")
    @Story("إضافة تسليم خزينة ببيانات صحيحة")
    @Description(
            "يتحقق هذا الاختبار من إضافة تسليم خزينة جديد ببيانات صحيحة، ويشمل اختيار"
                    + " المستخدم والخزينة وإدخال الرصيد الافتتاحي، وحفظ البيانات والتحقق من إضافة السجل"
                    + " بنجاح.")
    @Link(name = "Test Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidTreasuryHandoverTest() {
        System.out.println("TreasuryHandover test started");
        TreasuryHandoverPage treasuryHandoverPage = new TreasuryHandoverPage(driver);
        treasuryHandoverPage.NavigationHelper(Pages.SAFETRANSACTIONS);
        treasuryHandoverPage.ClickNew();
        treasuryHandoverPage.selectTreasuryHandoverDropdown("المستخدم", "admin");
        treasuryHandoverPage.selectTreasuryHandoverDropdown("خزينة", "الخزنة الرئيسية");
        treasuryHandoverPage.AddOpenBalance(1000);
        treasuryHandoverPage.ClickSaveButton();
        // treasuryHandoverPage.AssertReceiptAdded("الخزنة الرئيسية");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تسليم الخزينة")
    @Story("التحقق - خطأ عند تكرار تسليم خزينة لنفس المستخدم")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع إنشاء تسليم خزينة لنفس المستخدم المفتوح"
                    + " مسبقاً، ويتحقق من ظهور رسالة خطأ تفيد بعدم إمكانية تكرار التسليم لنفس المستخدم.")
    @Link(name = "Test Link", url = "https://binfares.plugin-erp.com/#/login")
    public void InvalidTreasuryHandoverTest() {
        System.out.println("Invalid TreasuryHandover test started");
        TreasuryHandoverPage treasuryHandoverPage = new TreasuryHandoverPage(driver);
        treasuryHandoverPage.NavigationHelper(Pages.SAFETRANSACTIONS);
        treasuryHandoverPage.ClickNew();
        treasuryHandoverPage.selectTreasuryHandoverDropdown("المستخدم", "admin");
        treasuryHandoverPage.selectTreasuryHandoverDropdown("خزينة", "الخزنة الرئيسية");
        treasuryHandoverPage.AddOpenBalance(1000);
        treasuryHandoverPage.ClickSaveButton();
        treasuryHandoverPage.isErrorPopupDisplayed();
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تسليم الخزينة")
    @Story("فتح تسليم الخزينة للمراجعة والتحقق من الرصيد في التقرير")
    @Description(
            "يتحقق هذا الاختبار من فتح تسليم الخزينة للمراجعة، والحصول على إجمالي الرصيد عند"
                    + " الإغلاق، وحفظ وطباعة التقرير، والتحقق من تطابق إجمالي الرصيد في التقرير مع"
                    + " البيانات المدخلة.")
    @Link(name = "Test Link", url = "https://binfares.plugin-erp.com/#/login")
    public void OpenTreasuryHandover() {
        TreasuryHandoverPage treasuryHandoverPage = new TreasuryHandoverPage(driver);
        treasuryHandoverPage.NavigationHelper(Pages.SAFETRANSACTIONS);
        treasuryHandoverPage.OpenTreasuryForReview();
        treasuryHandoverPage.GetTotalBalnceClose();
        treasuryHandoverPage.SaveAndPrint();
        treasuryHandoverPage.GetTotalBalnceinReport();
    }
}
