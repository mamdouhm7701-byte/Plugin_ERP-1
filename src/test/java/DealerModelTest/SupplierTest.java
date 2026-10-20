package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Supplier;
import io.qameta.allure.*;
import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SupplierTest extends DealerScenarioTest {

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("SU-001 بنية قائمة الموردين وأوامر الصف")
    @Description("يتحقق من ظهور قائمة الموردين وسجلاتها وأوامر تعديلها وحذفها.")
    public void SU_001_SupplierListStructure() {
        Supplier page = new Supplier(driver);
        page.NavigationHelper(Pages.SUPPLIERS);
        seedRecord(page, Pages.SUPPLIERS, "LIST");
        DealerModelScreenAssertions.assertListAndActionsDisplayed(page);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("SU-002 إغلاق نموذج إضافة مورد دون حفظ")
    @Description("يتحقق من عدم إنشاء مورد عند إغلاق نموذج الإضافة قبل الحفظ.")
    public void SU_002_CloseSupplierFormWithoutSaving() {
        Supplier page = new Supplier(driver);
        page.NavigationHelper(Pages.SUPPLIERS);
        DealerModelScreenAssertions.assertNewFormCanBeClosedWithoutSaving(page);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("SU-003 إلغاء تعديل المورد دون حفظ")
    @Description("يتحقق من تحميل بيانات المورد المحدد ومن عدم حفظ التغيير عند إغلاق نموذج التعديل.")
    public void SU_003_CancelSupplierEditWithoutSaving() {
        Supplier page = new Supplier(driver);
        page.NavigationHelper(Pages.SUPPLIERS);
        assertOwnedEditCancelled(page, Pages.SUPPLIERS);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("SU-004 إلغاء حذف مورد")
    @Description("يتحقق من ظهور تأكيد الحذف ومن بقاء المورد عند إلغاء العملية.")
    public void SU_004_CancelSupplierDelete() {
        Supplier page = new Supplier(driver);
        page.NavigationHelper(Pages.SUPPLIERS);
        assertOwnedDeleteCancelled(page, Pages.SUPPLIERS);
    }

    @Test(priority = 20)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("SU-005 دورة حياة مورد مملوك للاختبار")
    @Description("ينشئ موردًا فريدًا ببياناته الأساسية، يعدله، ثم يحذف السجل نفسه.")
    public void SU_005_OwnedSupplierCreateEditDelete() {
        String suffix = String.valueOf(System.currentTimeMillis());
        String originalName = "QA-SUPPLIER-" + suffix;
        String editedName = originalName + "-EDITED";
        Supplier page = new Supplier(driver);
        page.NavigationHelper(Pages.SUPPLIERS);

        DealerModelScreenAssertions.assertOwnedCreateEditDelete(
                driver,
                page,
                originalName,
                editedName,
                form -> {
                    form.ClickBasicDataTab();
                    form.AddEnglishName("QA Supplier " + suffix);
                },
                () -> page.NavigationHelper(Pages.SUPPLIERS));
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("إضافة مورد - الصفحة الأولى")
    @Description(
            "يتحقق هذا الاختبار من إضافة مورد جديد من الصفحة الأولى، ويشمل إدخال الاسم العربي"
                    + " والإنجليزي والرقم الضريبي، واختيار المنطقة ورقم الحساب والمندوب وتصنيف المورد،"
                    + " وإضافة السجل التجاري وأيام السماح والتاريخ، والتحقق من حالة خانات الاختيار (خصم"
                    + " المورد، فعال، عام، خاضع للضريبة)، وإضافة قيمة الخصم والملاحظات، وحفظ البيانات"
                    + " والتحقق من إضافة السجل بنجاح.")
    public void AddSupplierFirstPage() {
        prepareScenarioReferences("AddSupplierFirstPage");
        Supplier supplier = new Supplier(driver);
        String recordName = ownedName(supplier, Pages.SUPPLIERS, "AddSupplierFirstPage");
        supplier.NavigationHelper(Pages.SUPPLIERS);
        supplier.ClickNewButtonForDealer();
        supplier.AddArabicName(recordName);
        supplier.AddEnglishName("Test " + recordName);
        supplier.AddTaxNumber(uniqueTaxNumber());
        supplier.SelectFromDropDawn(data().area().name(), "اسم المنطقة");
        supplier.SelectFromDropDawn(data().account().name(), "رقم الحساب");
        supplier.SelectFromDropDawn(data().agent("Purch").name(), "المندوب");
        supplier.SelectFromDropDawn(data().classification("Suppliers").name(), "تصنيف المورد");
        supplier.SelectFromDropDawn(data().mainDealer("Suppliers").name(), "اسم المتعامل الرئيسي");
        supplier.AddCommercialRegisterNumber("203040");
        supplier.AddAllowanceDays("30");
        supplier.AddDateForDealer();
        supplier.SmartCheck("خصم المورد", true);
        supplier.AssertCheckboxIsChecked("خصم المورد", true);
        // supplier.AddDiscountValue("10");
        supplier.SmartCheck("فعال", true);
        supplier.SmartCheck("عام", true);
        supplier.SmartCheck("خاضع للضريبة", true);
        supplier.AssertCheckboxIsChecked("فعال", true);
        supplier.AssertCheckboxIsChecked("عام", true);
        supplier.AssertCheckboxIsChecked("خاضع للضريبة", true);
        supplier.AddNoteForDealer("ملاحظات المورد");
        supplier.ClickSaveButton();
        waitForRecord(supplier, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("إضافة مورد - بيانات الاتصال والضريبة")
    @Description(
            "يتحقق هذا الاختبار من إضافة مورد جديد مع بيانات الاتصال والبيانات الضريبية،"
                    + " ويشمل إدخال اسم جهة الاتصال والعنوان ورقم الجوال والهاتف الأرضي والبريد"
                    + " الإلكتروني، وإدخال الدولة والمدينة والحي ورقم المبنى والرمز البريدي واسم"
                    + " الشارع، وإضافة المعرف الآخر وبيانات البنك (اسم البنك ورقم الحساب) وبيانات"
                    + " المندوب والآيبان، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    public void AddSupplierSecondPage() {
        Supplier supplier = new Supplier(driver);
        String recordName = ownedName(supplier, Pages.SUPPLIERS, "AddSupplierSecondPage");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplier.NavigationHelper(Pages.SUPPLIERS);
        supplier.ClickNewButtonForDealer();
        supplier.AddArabicName(recordName);
        supplier.AddEnglishName("Test " + recordName);
        supplier.ClickContactDataTab();
        supplier.AddContactPersonName("اسم جهة الاتصال");
        supplier.AddAdressForSupplier("عنوان المورد");
        supplier.AddMobileNumberForSupplier(uniquePhone());
        supplier.AddLandLine("0112345678");
        supplier.AddEmail(uniqueEmail());
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
        waitForRecord(supplier, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("إضافة مورد - تبويب التحكم")
    @Description(
            "يتحقق هذا الاختبار من إضافة مورد جديد مع إعدادات التحكم في المورد، ويشمل إلغاء"
                    + " تفعيل جميع الصلاحيات (مشتريات نقدية، مشتريات آجلة، مرتجع مشتريات، أمر شراء)،"
                    + " والتحقق من أن جميع هذه الخانات غير مفعلة، وحفظ البيانات والتحقق من إضافة السجل"
                    + " بنجاح.")
    public void AddSupplierSupplierControlTab() {
        Supplier supplier = new Supplier(driver);
        String recordName = ownedName(supplier, Pages.SUPPLIERS, "AddSupplierSupplierControlTab");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplier.NavigationHelper(Pages.SUPPLIERS);
        supplier.ClickNewButtonForDealer();
        supplier.AddArabicName(recordName);
        supplier.ClickSupplierControlTab();
        supplier.SmartCheck("مشتريات نقدية", false);
        supplier.SmartCheck("مشتريات اجلة", false);
        supplier.SmartCheck("مرتجع مشتريات", false);
        supplier.SmartCheck("امر شراء", false);
        supplier.AssertCheckboxIsChecked("مشتريات نقدية", false);
        supplier.AssertCheckboxIsChecked("مشتريات اجلة", false);
        supplier.AssertCheckboxIsChecked("مرتجع مشتريات", false);
        supplier.AssertCheckboxIsChecked("امر شراء", false);
        supplier.ClickSaveButton();
        waitForRecord(supplier, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("تعديل مورد")
    @Description(
            "يتحقق هذا الاختبار من تعديل بيانات مورد موجود بالكامل، ويشمل تعديل البيانات"
                    + " الأساسية (الاسم، الرقم الضريبي، المنطقة، رقم الحساب، المندوب، تصنيف المورد،"
                    + " السجل التجاري، أيام السماح، التاريخ)، وتغيير حالة خانات الاختيار (خصم المورد،"
                    + " فعال، عام، خاضع للضريبة)، وتعديل بيانات الاتصال والبيانات الضريبية، وتعديل"
                    + " صلاحيات التحكم وتفعيلها بالكامل (مشتريات نقدية، مشتريات آجلة، مرتجع مشتريات،"
                    + " أمر شراء).")
    public void Edit() {
        prepareScenarioReferences("Edit");
        Supplier supplier = new Supplier(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplier.NavigationHelper(Pages.SUPPLIERS);
        String originalName = seedRecord(supplier, Pages.SUPPLIERS, "EDIT-ORIGINAL");
        supplier.openDealerForEditByName(originalName);
        String editedName = ownedName(supplier, Pages.SUPPLIERS, "EDIT-UPDATED");
        supplier.AddArabicName(editedName);
        supplier.AddEnglishName("Test " + editedName);
        supplier.AddTaxNumber(uniqueTaxNumber());
        supplier.SelectFromDropDawn(data().area().name(), "اسم المنطقة");
        supplier.SelectFromDropDawn(data().account().name(), "رقم الحساب");
        supplier.SelectFromDropDawn(data().agent("Purch").name(), "المندوب");
        supplier.SelectFromDropDawn(data().classification("Suppliers").name(), "تصنيف المورد");
        supplier.SelectFromDropDawn(data().mainDealer("Suppliers").name(), "اسم المتعامل الرئيسي");
        supplier.AddCommercialRegisterNumber("3030");
        supplier.AddAllowanceDays("10");
        supplier.AddDateForDealer();
        supplier.SmartCheck("خصم المورد", false);
        supplier.AssertCheckboxIsChecked("خصم المورد", false);
        supplier.SmartCheck("فعال", false);
        supplier.SmartCheck("عام", false);
        supplier.SmartCheck("خاضع للضريبة", false);
        supplier.AssertCheckboxIsChecked("فعال", false);
        supplier.AssertCheckboxIsChecked("عام", false);
        supplier.AssertCheckboxIsChecked("خاضع للضريبة", false);
        supplier.AddNoteForDealer("ملاحظات المورد");
        supplier.ClickContactDataTab();
        supplier.AddContactPersonName("اسم جهة الاتصال");
        supplier.AddAdressForSupplier("عنوان المورد");
        supplier.AddMobileNumberForSupplier(uniquePhone());
        supplier.AddLandLine("0112345678");
        supplier.AddEmail(uniqueEmail());
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
        supplier.SmartCheck("مشتريات نقدية", true);
        supplier.SmartCheck("مشتريات اجلة", true);
        supplier.SmartCheck("مرتجع مشتريات", true);
        supplier.SmartCheck("امر شراء", true);
        supplier.AssertCheckboxIsChecked("مشتريات نقدية", true);
        supplier.AssertCheckboxIsChecked("مشتريات اجلة", true);
        supplier.AssertCheckboxIsChecked("مرتجع مشتريات", true);
        supplier.AssertCheckboxIsChecked("امر شراء", true);
        supplier.ClickSaveButton();
        waitForRecord(supplier, editedName);
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("حذف مورد")
    @Description(
            "يتحقق هذا الاختبار من حذف الموردين المضافين، ويقوم بحذف ثلاثة سجلات متتالية"
                    + " للتأكد من أن وظيفة الحذف تعمل بشكل صحيح.")
    @Test(priority = 0)
    public void Delete() {
        Supplier supplier = new Supplier(driver);
        deleteOwnedRecords(supplier, Pages.SUPPLIERS, 3);
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع حفظ المورد في حال عدم إدخال الاسم العربي،"
                    + " ويتحقق من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    public void AddSupplierWithOutArabicName() {
        Supplier supplier = new Supplier(driver);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        supplier.NavigationHelper(Pages.SUPPLIERS);
        supplier.ClickNewButtonForDealer();
        supplier.ClickSaveButton();
        Assert.assertTrue(supplier.IsArabicNameRequiredErrorDisplayed(), "الاسم العربي غير مطلوب");
    }

    @Test(priority = 90)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("تصدير قائمة الموردين إلى PDF")
    @Description("يتحقق من تنزيل PDF صالح من قائمة الموردين.")
    public void ExportSuppliersToPdf() throws IOException {
        Supplier supplier = new Supplier(driver);
        supplier.NavigationHelper(Pages.SUPPLIERS);
        DealerModelExportAssertions.assertExportDownloaded(
                driver, () -> supplier.ExportList("pdf"), "pdf");
    }

    @Test(priority = 91)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الموردين")
    @Story("تصدير قائمة الموردين إلى Excel")
    @Description("يتحقق من تنزيل Excel صالح من قائمة الموردين.")
    public void ExportSuppliersToExcel() throws IOException {
        Supplier supplier = new Supplier(driver);
        supplier.NavigationHelper(Pages.SUPPLIERS);
        DealerModelExportAssertions.assertExportDownloaded(
                driver, () -> supplier.ExportList("excel"), "excel");
    }
}
