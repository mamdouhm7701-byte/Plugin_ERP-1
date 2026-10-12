package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.StorePage;
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

public class StoreTest extends BaseTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("إضافة مخزن ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة مخزن جديد ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي والملاحظات واختيار رقم الحساب، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidStoreTest() {
        System.out.println("Store test started");
        StorePage storePage = new StorePage(driver);
        storePage.OpenStorePage();
        storePage.ClickNew();
        storePage.AddArabicName("مخزن جديد");
        storePage.AddEnglishName("New Store");
        storePage.AddNoteForDealer("This is a new store");
        storePage.SelectFromDropDawn("تسويق جوجل","رقم الحساب");
        storePage.ClickSaveButton();
        storePage.AssertReceiptAdded("مخزن جديد");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("تعديل مخزن ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل مخزن موجود ببيانات صحيحة، ويشمل تعديل الاسم العربي والإنجليزي والملاحظات وتغيير رقم الحساب، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditStore() {
        StorePage storePage = new StorePage(driver);
        storePage.NavigationHelper(Pages.WAREHOUSES);
        storePage.Edit();
        storePage.AddArabicName("مخزن معدل");
        storePage.AddEnglishName("Edited Store");
        storePage.AddNoteForDealer("This is an edited store");
        storePage.SelectFromDropDawn("تسويق جوجل","رقم الحساب");
        storePage.ClickSaveButton();
        storePage.AssertReceiptAdded("مخزن معدل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("حذف مخزن")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف مخزن موجود بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteStore() {
        StorePage storePage = new StorePage(driver);
        storePage.NavigationHelper(Pages.WAREHOUSES);
        storePage.DeleteStore();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المخازن")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة مخزن بدون إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void ValidateRequiredFieldsTest() {
        StorePage storePage = new StorePage(driver);
        storePage.NavigationHelper(Pages.WAREHOUSES);
        storePage.ClickNew();
        storePage.IsArabicNameRequiredErrorDisplayed();
    }
}