package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.CustomerClassification;
import Plugin_ERP.System_Modules.Login.DealerModel.MainDealer;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

public class MainDealerTest extends BaseTest {



    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("إضافة عميل رئيسي")
    @Description("يتحقق هذا الاختبار من إضافة متعامل رئيسي جديد من نوع (عملاء)، ويشمل إدخال الاسم العربي والإنجليزي والملاحظات، واختيار النوع (العملاء)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddCustomerMain()
    {
        MainDealer mainDealer = new MainDealer(driver);
        mainDealer.NavigationHelper(Pages.MAINDEALERS);
        mainDealer.ClickNew();
        mainDealer.AddArabicName("عميل رئيسى");
        mainDealer.AddEnglishName("Main Customer");
        mainDealer.AddNoteForDealer("This is a test description for main customer.");
        mainDealer.SelectFromDropDawn("العملاء","النوع");
        mainDealer.ClickSaveButton();
        mainDealer.AssertReceiptAdded("عميل رئيسى");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("إضافة مورد رئيسي")
    @Description("يتحقق هذا الاختبار من إضافة متعامل رئيسي جديد من نوع (موردين)، ويشمل إدخال الاسم العربي والإنجليزي والملاحظات، واختيار النوع (الموردين)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddSupplierMain()
    {
        MainDealer mainDealer = new MainDealer(driver);
        mainDealer.NavigationHelper(Pages.MAINDEALERS);
        mainDealer.ClickNew();
        mainDealer.AddArabicName("مورد رئيسي");
        mainDealer.AddEnglishName("Supplier Classification");
        mainDealer.AddNoteForDealer("This is a test description for supplier classification.");
        mainDealer.SelectFromDropDawn("الموردين","النوع");
        mainDealer.ClickSaveButton();
        mainDealer.AssertReceiptAdded("مورد رئيسي");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("تعديل متعامل رئيسي")
    @Description("يتحقق هذا الاختبار من تعديل متعامل رئيسي موجود، ويشمل تعديل الاسم العربي والإنجليزي والملاحظات وتغيير النوع إلى (العملاء)، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void Edit() {
        MainDealer mainDealer = new MainDealer(driver);
        mainDealer.NavigationHelper(Pages.MAINDEALERS);
        mainDealer.Edit();
        mainDealer.AddArabicName("تصنيف معدل");
        mainDealer.AddEnglishName("Agent Classification");
        mainDealer.AddNoteForDealer("This is a test description for agent classification.");
        mainDealer.SelectFromDropDawn("العملاء", "النوع");
        mainDealer.ClickSaveButton();
        mainDealer.AssertReceiptAdded("تصنيف معدل");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("حذف متعامل رئيسي")
    @Description("يتحقق هذا الاختبار من حذف المتعاملين الرئيسيين المضافين، ويقوم بحذف سجلين متتاليين للتأكد من أن وظيفة الحذف تعمل بشكل صحيح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void Delete() {
        MainDealer mainDealer = new MainDealer(driver);
        mainDealer.NavigationHelper(Pages.MAINDEALERS);
        mainDealer.Delete();
        mainDealer.Delete();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع الحفظ في حال عدم إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertMessageArabicName()
    {
        MainDealer mainDealer = new MainDealer(driver);
        mainDealer.NavigationHelper(Pages.MAINDEALERS);
        mainDealer.ClickNew();
        mainDealer.SelectFromDropDawn("العملاء","النوع");
        mainDealer.ClickSaveButton();
        mainDealer.IsArabicNameRequiredErrorDisplayed();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("التحقق - النوع إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع الحفظ في حال عدم اختيار نوع المتعامل، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل النوع إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertMessageEnglishName() {
        MainDealer mainDealer = new MainDealer(driver);
        mainDealer.NavigationHelper(Pages.MAINDEALERS);
        mainDealer.ClickNew();
        mainDealer.AddArabicName("تصنيف معدل");
        mainDealer.SelectFromDropDawn("العملاء", "النوع");
        mainDealer.ClickSaveButton();
        mainDealer.AssertKind();
    }
}