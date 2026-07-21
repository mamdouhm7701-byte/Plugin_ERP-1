package ProductsTest;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Products.UnitePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UnitTest  extends BaseTest {



    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("شاشة الوحدات")
    @Story("إنشاء وحدة بنجاح")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع إنشاء وحدة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddNewUnite() {
        UnitePage unitePage = new UnitePage(driver);

        unitePage.NavigationHelper(Pages.UNITS);
        unitePage.ClickNew();
        unitePage.AddArabicName("وحدة اختبار");
        unitePage.AddEnglishName("Test Unit");
        unitePage.ClickSaveButton();
        unitePage.AssertReceiptAdded("وحدة اختبار");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("شاشة الوحدات")
    @Story("تعديل وحدة بنجاح")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع تعديل وحدة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditUnite(){
        UnitePage unitePage = new UnitePage(driver);
        unitePage.NavigationHelper(Pages.UNITS);
        unitePage.Edit();
        unitePage.AddArabicName("وحدة اختبار معدلة");
        unitePage.AddEnglishName("Test Unit Modified");
        unitePage.ClickSaveButton();
        unitePage.AssertReceiptAdded("وحدة اختبار معدلة");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("شاشة الوحدات")
    @Story("حذف وحدة بنجاح")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف وحدة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteUnite(){
        UnitePage unitePage = new UnitePage(driver);
        unitePage.NavigationHelper(Pages.UNITS);

        unitePage.Delete();
    }


    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("شاشة الوحدات")
    @Story("التحقق - إضافة وحدة بدون اسم عربي")
    @Description("يتحقق هذا الاختبار من أن المستخدم لا يستطيع إنشاء وحدة بدون إدخال اسم عربي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddUniteWithoutArabicName()  {
        UnitePage unitePage = new UnitePage(driver);
        unitePage.NavigationHelper(Pages.UNITS);

        unitePage.ClickNew();
        unitePage.AddEnglishName("Test Unit Without Arabic Name");
        unitePage.ClickSaveButton();
        Assert.assertTrue(unitePage.AssertUniteNameRequired());
    }



}