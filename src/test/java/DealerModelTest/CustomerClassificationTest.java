package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.CustomerClassification;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

public class CustomerClassificationTest extends BaseTest {



    @Test(priority=1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("إضافة تصنيف عميل")
    @Description("يتحقق هذا الاختبار من إضافة تصنيف جديد من نوع (عملاء)، ويشمل إدخال الاسم العربي والإنجليزي والملاحظات، واختيار نوع التصنيف (العملاء)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddCustomerClassification()
    {
        CustomerClassification customerClassification = new CustomerClassification(driver);
        customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
        customerClassification.ClickNew();
        customerClassification.AddArabicName("تصنيف عميل");
        customerClassification.AddEnglishName("Customer Classification");
        customerClassification.AddNoteForDealer("This is a test description for customer classification.");
        customerClassification.SelectFromDropDawn("العملاء","نوع التصنيف");
        customerClassification.ClickSaveButton();
        customerClassification.AssertReceiptAdded("تصنيف عميل");
    }

    @Test(priority=2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("إضافة تصنيف مورد")
    @Description("يتحقق هذا الاختبار من إضافة تصنيف جديد من نوع (موردين)، ويشمل إدخال الاسم العربي والإنجليزي والملاحظات، واختيار نوع التصنيف (الموردين)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddSupplierClassification()
    {
        CustomerClassification customerClassification = new CustomerClassification(driver);
customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
customerClassification.ClickNew();
        customerClassification.AddArabicName("تصنيف مورد");
        customerClassification.AddEnglishName("Supplier Classification");
        customerClassification.AddNoteForDealer("This is a test description for supplier classification.");
        customerClassification.SelectFromDropDawn("الموردين","نوع التصنيف");
        customerClassification.ClickSaveButton();
        customerClassification.AssertReceiptAdded("تصنيف مورد");
    }

    @Test(priority=3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("إضافة تصنيف مندوب")
    @Description("يتحقق هذا الاختبار من إضافة تصنيف جديد من نوع (مندوبين)، ويشمل إدخال الاسم العربي والإنجليزي والملاحظات، واختيار نوع التصنيف (المندوبين)، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddAgentClassification()
    {
        CustomerClassification customerClassification = new CustomerClassification(driver);
customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
customerClassification.ClickNew();
        customerClassification.AddArabicName("تصنيف مندوب");
        customerClassification.AddEnglishName("Agent Classification");
        customerClassification.AddNoteForDealer("This is a test description for agent classification.");
        customerClassification.SelectFromDropDawn("المندوبين","نوع التصنيف");
        customerClassification.ClickSaveButton();
        customerClassification.AssertReceiptAdded("تصنيف مندوب");
    }

    @Test(priority=4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("تعديل تصنيف")
    @Description("يتحقق هذا الاختبار من تعديل تصنيف موجود، ويشمل تعديل الاسم العربي والإنجليزي والملاحظات وتغيير نوع التصنيف إلى (المندوبين)، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void Edit()
    {
        CustomerClassification customerClassification = new CustomerClassification(driver);
customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
customerClassification.Edit();
        customerClassification.AddArabicName("تصنيف معدل");
        customerClassification.AddEnglishName("Agent Classification");
        customerClassification.AddNoteForDealer("This is a test description for agent classification.");
        customerClassification.SelectFromDropDawn("المندوبين","نوع التصنيف");
        customerClassification.ClickSaveButton();
        customerClassification.AssertReceiptAdded("تصنيف معدل");
    }

    @Test(priority=5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("حذف تصنيف")
    @Description("يتحقق هذا الاختبار من حذف التصنيفات المضافة، ويقوم بحذف ثلاثة سجلات متتالية للتأكد من أن وظيفة الحذف تعمل بشكل صحيح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void Delete()
    {
        CustomerClassification customerClassification = new CustomerClassification(driver);
customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
customerClassification.Delete();
        customerClassification.Delete();

    }

    @Test(priority=6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع الحفظ في حال عدم إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertMessageArabicName()
    {
        CustomerClassification customerClassification = new CustomerClassification(driver);
customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
customerClassification.ClickNew();
        customerClassification.SelectFromDropDawn("المندوبين","نوع التصنيف");
        customerClassification.ClickSaveButton();
        customerClassification.IsArabicNameRequiredErrorDisplayed();
    }

    @Test(priority=7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("التحقق - نوع التصنيف إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع الحفظ في حال عدم اختيار نوع التصنيف، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل نوع التصنيف إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertMessageClassification()
    {
        CustomerClassification customerClassification = new CustomerClassification(driver);
customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
customerClassification.ClickNew();
        customerClassification.AddArabicName("تصنيف معدل");
        customerClassification.ClickSaveButton();
        customerClassification.AssertClassification();
    }
}