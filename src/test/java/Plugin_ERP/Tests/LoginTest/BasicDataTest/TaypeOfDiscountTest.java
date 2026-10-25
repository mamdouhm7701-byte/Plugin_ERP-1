package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.BasicData.TypesofdiscountsPage;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import TestSupport.BasicDataScenarioTest;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

public class TaypeOfDiscountTest extends BasicDataScenarioTest {

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع الخصومات")
    @Story("إضافة نوع خصم ببيانات صحيحة")
    @Description(
            "يتحقق هذا الاختبار من إضافة نوع خصم جديد ببيانات صحيحة، ويشمل إدخال الاسم العربي"
                    + " والإنجليزي والكود السري وقيمة الخصم، وإضافة المستخدمين المصرح لهم، وحفظ"
                    + " البيانات والتحقق من إضافة السجل بنجاح.")

    public void ValidTaypeOfDiscountTest() {
        int userCode = Integer.parseInt(basicData().user("discount").values().get("code").toString());
        System.out.println("TaypeOfDiscount test started");
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.ClickNew();
        String name = ownedName("DiscountTypes", "create");
        typesofdiscountsPage.AddArabicName(name);
        typesofdiscountsPage.AddEnglishName("Test Discount");
        typesofdiscountsPage.AddSecretCode(typesofdiscountsPage.generateRandomNumber());
        typesofdiscountsPage.AddDiscount(5);
        typesofdiscountsPage.ClickToAddUsers();
        typesofdiscountsPage.ClickSelectUsers(userCode);
        typesofdiscountsPage.ClickCloseButton();
        typesofdiscountsPage.ClickSaveButton();
        typesofdiscountsPage.AssertReceiptAdded(name);
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع الخصومات")
    @Story("تعديل نوع خصم ببيانات صحيحة")
    @Description(
            "يتحقق هذا الاختبار من تعديل نوع خصم موجود ببيانات صحيحة، ويشمل تعديل الاسم"
                    + " العربي والإنجليزي وتفعيل خانة (فعال) وتغيير الكود السري وقيمة الخصم وإضافة"
                    + " مستخدمين جدد، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")

    public void EditTypeOfDiscount() {
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        String original = seedDiscount("edit");
        int editedUserCode = Integer.parseInt(basicData().user("discount-edit").values().get("code").toString());
        driver.navigate().refresh();
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.OpenOwnedForEdit(original);
        String edited = ownedName("DiscountTypes", "edited");
        typesofdiscountsPage.AddArabicName(edited);
        typesofdiscountsPage.AddEnglishName("Test Discount");
        typesofdiscountsPage.SmartCheck("فعال", true);
        typesofdiscountsPage.SmartCheck("فعال", true);
        typesofdiscountsPage.AddSecretCode(typesofdiscountsPage.generateRandomNumber());
        typesofdiscountsPage.AddDiscount(10);
        typesofdiscountsPage.ClickToAddUsers();
        typesofdiscountsPage.ClickSelectUsers(editedUserCode);
        typesofdiscountsPage.ClickCloseButton();
        typesofdiscountsPage.ClickSaveButton();
        typesofdiscountsPage.AssertReceiptAdded(edited);
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("أنواع الخصومات")
    @Story("حذف نوع خصم")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف نوع خصم موجود بنجاح.")

    public void DeleteTypeOfDiscount() {
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        String original = seedDiscount("delete");
        driver.navigate().refresh();
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.DeleteOwned(original);
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("أنواع الخصومات")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع إضافة نوع خصم بدون إدخال الاسم العربي،"
                    + " ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")

    public void AssertArabicNameRequired() {
        System.out.println("RequiredFieldsTaypeOfDiscount test started");
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.ClickNew();
        typesofdiscountsPage.AddArabicName("");
        typesofdiscountsPage.AddEnglishName(ownedName("DiscountTypes", "invalid-name"));
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
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع إضافة نوع خصم بدون إدخال الكود السري،"
                    + " ويتحقق من ظهور رسالة خطأ تفيد بأن حقل الكود السري إلزامي.")

    public void AssertSecretCodeRequired() {
        System.out.println("RequiredFieldsTaypeOfDiscount test started");
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.ClickNew();
        typesofdiscountsPage.AddArabicName(ownedName("DiscountTypes", "validation"));
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
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع إضافة نوع خصم بدون إدخال نسبة الخصم، ويتحقق"
                    + " من ظهور رسالة خطأ تفيد بأن حقل نسبة الخصم إلزامي.")

    public void AssertPercentageRequired() {
        System.out.println("RequiredFieldsTaypeOfDiscount test started");
        TypesofdiscountsPage typesofdiscountsPage = new TypesofdiscountsPage(driver);
        typesofdiscountsPage.NavigationHelper(Pages.DISCONTTAYPES);
        typesofdiscountsPage.ClickNew();
        typesofdiscountsPage.AddArabicName(ownedName("DiscountTypes", "validation"));
        typesofdiscountsPage.AddSecretCode(typesofdiscountsPage.generateRandomNumber());
        typesofdiscountsPage.ClickSaveButton();
        Assert.assertTrue(typesofdiscountsPage.IsPercentageRequiredErrorDisplayed());
    }
    private String seedDiscount(String key) {
        return basicData().fixture(key, "DiscountTypes", TestSupport.BasicDataFixtures.fields(
                "secretCode", String.valueOf(System.nanoTime()), "discountPercentage", 5,
                "users", java.util.List.of(TestSupport.BasicDataFixtures.fields("userId", basicData().user("discount").id())))).name();
    }
}
