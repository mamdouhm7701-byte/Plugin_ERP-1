package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Supplier;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SupplierTest extends BaseTest {



    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("إضافة مورد - الصفحة الأولى")
    @Description("يتحقق هذا الاختبار من إضافة مورد جديد من الصفحة الأولى، ويشمل إدخال الاسم العربي والإنجليزي والرقم الضريبي، واختيار المنطقة ورقم الحساب والمندوب وتصنيف المورد، وإضافة السجل التجاري وأيام السماح والتاريخ، والتحقق من حالة خانات الاختيار (خصم المورد، فعال، عام، خاضع للضريبة)، وإضافة قيمة الخصم والملاحظات، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddSupplierFirstPage(){
        Supplier supplier = new Supplier(driver);
        supplier.NavigationHelper(Pages.SUPPLIERS);
        supplier.ClickNewButtonForDealer();
        supplier.AddArabicName("التحكم فى المورد");
        supplier.AddEnglishName("English Name");
        supplier.AddTaxNumber("123456789123456");
        supplier.SelectFromDropDawn("نيو كود","اسم المنطقة");
        supplier.SelectFromDropDawn("المبيعات","رقم الحساب");
        supplier.SelectFromDropDawn("تست","المندوب");
        supplier.SelectFromDropDawn("موردين","تصنيف المورد");
        supplier.SelectFromDropDawn("موردين","اسم المتعامل الرئيسي");
        supplier.AddCommercialRegisterNumber("203040");
        supplier.AddAllowanceDays("30");
        supplier.AddDateForDealer();
        supplier.SmartCheck("خصم المورد",true);
        supplier.AssertCheckboxIsChecked("خصم المورد",true);
        supplier.AddDiscountValue("10");
        supplier.SmartCheck("فعال",true);
        supplier.SmartCheck("عام",true);
        supplier.SmartCheck("خاضع للضريبة",true);
        supplier.AssertCheckboxIsChecked("فعال",true);
        supplier.AssertCheckboxIsChecked("عام",true);
        supplier.AssertCheckboxIsChecked("خاضع للضريبة",true);
        supplier.AddNoteForDealer("ملاحظات المورد");
        supplier.ClickSaveButton();
        supplier.AssertReceiptAdded("التحكم فى المورد");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("إضافة مورد - بيانات الاتصال والضريبة")
    @Description("يتحقق هذا الاختبار من إضافة مورد جديد مع بيانات الاتصال والبيانات الضريبية، ويشمل إدخال اسم جهة الاتصال والعنوان ورقم الجوال والهاتف الأرضي والبريد الإلكتروني، وإدخال الدولة والمدينة والحي ورقم المبنى والرمز البريدي واسم الشارع، وإضافة المعرف الآخر وبيانات البنك (اسم البنك ورقم الحساب) وبيانات المندوب والآيبان، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddSupplierSecondPage() {
        Supplier supplier = new Supplier(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplier.NavigationHelper(Pages.SUPPLIERS);
        supplier.ClickNewButtonForDealer();
        supplier.AddArabicName("التحكم فى المورد");
        supplier.AddEnglishName("English Name");
        supplier.ClickContactDataTab();
        supplier.AddContactPersonName("اسم جهة الاتصال");
        supplier.AddAdressForSupplier("عنوان المورد");
        supplier.AddMobileNumberForSupplier("0591234567");
        supplier.AddLandLine("0112345678");
        supplier.AddEmail("eyd@Examble.com");
        supplier.AddCountry("المملكة العربية السعودية");
        supplier.AddCity("الرياض");
        supplier.AddDistrict("الرياض");
        supplier.AddBuildingNumber("123");
        supplier.AddPostalCode("12345");
        supplier.AddStreetName("شارع المورد");
        supplier.AddAnotherId("123456789");
        supplier.AddBankName("بنك المورد");
        supplier.AddAccountNumber("1234567890123456");
        supplier.AddAgentPhone("0591234567");
        supplier.AddAgentNameForSupplier("اسم المندوب");
        supplier.AddIban("SA1234567890123456789012");
        supplier.ClickSaveButton();
        supplier.AssertReceiptAdded("التحكم فى المورد");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("إضافة مورد - تبويب التحكم")
    @Description("يتحقق هذا الاختبار من إضافة مورد جديد مع إعدادات التحكم في المورد، ويشمل إلغاء تفعيل جميع الصلاحيات (مشتريات نقدية، مشتريات آجلة، مرتجع مشتريات، أمر شراء)، والتحقق من أن جميع هذه الخانات غير مفعلة، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddSupplierSupplierControlTab() {
        Supplier supplier = new Supplier(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplier.NavigationHelper(Pages.SUPPLIERS);
        supplier.ClickNewButtonForDealer();
        supplier.AddArabicName("التحكم فى المورد");
        supplier.ClickSupplierControlTab();
        supplier.SmartCheck("مشتريات نقدية",false);
        supplier.SmartCheck("مشتريات اجلة",false);
        supplier.SmartCheck("مرتجع مشتريات",false);
        supplier.SmartCheck("امر شراء",false);
        supplier.AssertCheckboxIsChecked("مشتريات نقدية",false);
        supplier.AssertCheckboxIsChecked("مشتريات اجلة",false);
        supplier.AssertCheckboxIsChecked("مرتجع مشتريات",false);
        supplier.AssertCheckboxIsChecked("امر شراء",false);
        supplier.ClickSaveButton();
        supplier.AssertReceiptAdded("التحكم فى المورد");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("تعديل مورد")
    @Description("يتحقق هذا الاختبار من تعديل بيانات مورد موجود بالكامل، ويشمل تعديل البيانات الأساسية (الاسم، الرقم الضريبي، المنطقة، رقم الحساب، المندوب، تصنيف المورد، السجل التجاري، أيام السماح، التاريخ)، وتغيير حالة خانات الاختيار (خصم المورد، فعال، عام، خاضع للضريبة)، وتعديل بيانات الاتصال والبيانات الضريبية، وتعديل صلاحيات التحكم وتفعيلها بالكامل (مشتريات نقدية، مشتريات آجلة، مرتجع مشتريات، أمر شراء).")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void Edit(){
        Supplier supplier = new Supplier(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplier.NavigationHelper(Pages.SUPPLIERS);
        supplier.Edit();
        supplier.AddArabicName("التعديل على المورد");
        supplier.AddEnglishName("English Edit Name");
        supplier.AddTaxNumber("56456");
        supplier.SelectFromDropDawn("نيو كود","اسم المنطقة");
        supplier.SelectFromDropDawn("ممدوح","رقم الحساب");
        supplier.SelectFromDropDawn("محمود","المندوب");
        supplier.SelectFromDropDawn("موردين","تصنيف المورد");
        supplier.SelectFromDropDawn("موردين","اسم المتعامل الرئيسي");
        supplier.AddCommercialRegisterNumber("3030");
        supplier.AddAllowanceDays("10");
        supplier.AddDateForDealer();
        supplier.SmartCheck("خصم المورد",false);
        supplier.AssertCheckboxIsChecked("خصم المورد",false);
        supplier.SmartCheck("فعال",false);
        supplier.SmartCheck("عام",false);
        supplier.SmartCheck("خاضع للضريبة",false);
        supplier.AssertCheckboxIsChecked("فعال",false);
        supplier.AssertCheckboxIsChecked("عام",false);
        supplier.AssertCheckboxIsChecked("خاضع للضريبة",false);
        supplier.AddNoteForDealer("ملاحظات المورد");
        supplier.ClickContactDataTab();
        supplier.AddContactPersonName("اسم جهة الاتصال");
        supplier.AddAdressForSupplier("عنوان المورد");
        supplier.AddMobileNumberForSupplier("0591234567");
        supplier.AddLandLine("0112345678");
        supplier.AddEmail("eyd@Examble.com");
        supplier.AddCountry("المملكة العربية السعودية");
        supplier.AddCity("الرياض");
        supplier.AddDistrict("الرياض");
        supplier.AddBuildingNumber("123");
        supplier.AddPostalCode("12345");
        supplier.AddStreetName("شارع المورد");
        supplier.AddAnotherId("123456789");
        supplier.AddBankName("بنك المورد");
        supplier.AddAccountNumber("1234567890123456");
        supplier.AddAgentPhone("0591234567");
        supplier.AddAgentNameForSupplier("اسم المندوب");
        supplier.AddIban("SA1234567890123456789012");
        supplier.ClickSupplierControlTab();
        supplier.SmartCheck("مشتريات نقدية",true);
        supplier.SmartCheck("مشتريات اجلة",true);
        supplier.SmartCheck("مرتجع مشتريات",true);
        supplier.SmartCheck("امر شراء",true);
        supplier.AssertCheckboxIsChecked("مشتريات نقدية",true);
        supplier.AssertCheckboxIsChecked("مشتريات اجلة",true);
        supplier.AssertCheckboxIsChecked("مرتجع مشتريات",true);
        supplier.AssertCheckboxIsChecked("امر شراء",true);
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("حذف مورد")
    @Description("يتحقق هذا الاختبار من حذف الموردين المضافين، ويقوم بحذف ثلاثة سجلات متتالية للتأكد من أن وظيفة الحذف تعمل بشكل صحيح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void Delete(){
        Supplier supplier = new Supplier(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplier.NavigationHelper(Pages.SUPPLIERS);
        supplier.Delete();
        supplier.Delete();
        supplier.Delete();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع حفظ المورد في حال عدم إدخال الاسم العربي، ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddSupplierWithOutArabicName(){
        Supplier supplier = new Supplier(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplier.NavigationHelper(Pages.SUPPLIERS);
        supplier.ClickNewButtonForDealer();
        supplier.ClickSaveButton();
        Assert.assertTrue(supplier.IsArabicNameRequiredErrorDisplayed(),"الاسم العربي غير مطلوب");
    }
}