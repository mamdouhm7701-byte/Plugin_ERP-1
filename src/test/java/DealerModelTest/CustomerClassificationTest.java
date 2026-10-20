package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.CustomerClassification;
import io.qameta.allure.*;
import java.io.IOException;
import org.testng.annotations.Test;

public class CustomerClassificationTest extends DealerScenarioTest {

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("CC-001 بنية قائمة تصنيفات المتعاملين وأوامر الصف")
    @Description("يتحقق من ظهور قائمة التصنيفات وسجلاتها وأوامر تعديلها وحذفها.")
    public void CC_001_ClassificationListStructure() {
        CustomerClassification page = new CustomerClassification(driver);
        page.NavigationHelper(Pages.CUSTOMERCLASS);
        seedRecord(page, Pages.CUSTOMERCLASS, "LIST");
        DealerModelScreenAssertions.assertListAndActionsDisplayed(page);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("CC-002 إغلاق نموذج إضافة تصنيف دون حفظ")
    @Description("يتحقق من عدم إنشاء تصنيف عند إغلاق نموذج الإضافة قبل الحفظ.")
    public void CC_002_CloseClassificationFormWithoutSaving() {
        CustomerClassification page = new CustomerClassification(driver);
        page.NavigationHelper(Pages.CUSTOMERCLASS);
        DealerModelScreenAssertions.assertNewFormCanBeClosedWithoutSaving(page);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("CC-003 إلغاء تعديل التصنيف دون حفظ")
    @Description(
            "يتحقق من تحميل بيانات التصنيف المحدد ومن عدم حفظ التغيير عند إغلاق نموذج"
                    + " التعديل.")
    public void CC_003_CancelClassificationEditWithoutSaving() {
        CustomerClassification page = new CustomerClassification(driver);
        page.NavigationHelper(Pages.CUSTOMERCLASS);
        assertOwnedEditCancelled(page, Pages.CUSTOMERCLASS);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("CC-004 إلغاء حذف تصنيف")
    @Description("يتحقق من ظهور تأكيد الحذف ومن بقاء التصنيف عند إلغاء العملية.")
    public void CC_004_CancelClassificationDelete() {
        CustomerClassification page = new CustomerClassification(driver);
        page.NavigationHelper(Pages.CUSTOMERCLASS);
        assertOwnedDeleteCancelled(page, Pages.CUSTOMERCLASS);
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("CC-005 دورة حياة تصنيف مملوك للاختبار")
    @Description("ينشئ تصنيفًا فريدًا، يعدله، ثم يحذف السجل نفسه ويتحقق من كل انتقال.")
    public void CC_005_OwnedClassificationCreateEditDelete() {
        String suffix = String.valueOf(System.currentTimeMillis());
        String originalName = "QA-CLASSIFICATION-" + suffix;
        String editedName = originalName + "-EDITED";
        CustomerClassification page = new CustomerClassification(driver);
        page.NavigationHelper(Pages.CUSTOMERCLASS);

        DealerModelScreenAssertions.assertOwnedCreateEditDelete(
                driver,
                page,
                originalName,
                editedName,
                form -> {
                    form.AddEnglishName("QA Classification " + suffix);
                    form.AddNoteForDealer("Owned automated test record");
                    form.SelectFromDropDawn("العملاء", "نوع التصنيف");
                },
                () -> page.NavigationHelper(Pages.CUSTOMERCLASS));
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("إضافة تصنيف عميل")
    @Description(
            "يتحقق هذا الاختبار من إضافة تصنيف جديد من نوع (عملاء)، ويشمل إدخال الاسم العربي"
                    + " والإنجليزي والملاحظات، واختيار نوع التصنيف (العملاء)، وحفظ البيانات والتحقق من"
                    + " إضافة السجل بنجاح.")
    public void AddCustomerClassification() {
        CustomerClassification customerClassification = new CustomerClassification(driver);
        String recordName =
                ownedName(customerClassification, Pages.CUSTOMERCLASS, "AddCustomerClassification");
        customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
        customerClassification.ClickNew();
        customerClassification.AddArabicName(recordName);
        customerClassification.AddEnglishName("Customer Classification");
        customerClassification.AddNoteForDealer(
                "This is a test description for customer classification.");
        customerClassification.SelectFromDropDawn("العملاء", "نوع التصنيف");
        customerClassification.ClickSaveButton();
        waitForRecord(customerClassification, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("إضافة تصنيف مورد")
    @Description(
            "يتحقق هذا الاختبار من إضافة تصنيف جديد من نوع (موردين)، ويشمل إدخال الاسم العربي"
                    + " والإنجليزي والملاحظات، واختيار نوع التصنيف (الموردين)، وحفظ البيانات والتحقق من"
                    + " إضافة السجل بنجاح.")
    public void AddSupplierClassification() {
        CustomerClassification customerClassification = new CustomerClassification(driver);
        String recordName =
                ownedName(customerClassification, Pages.CUSTOMERCLASS, "AddSupplierClassification");
        customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
        customerClassification.ClickNew();
        customerClassification.AddArabicName(recordName);
        customerClassification.AddEnglishName("Supplier Classification");
        customerClassification.AddNoteForDealer(
                "This is a test description for supplier classification.");
        customerClassification.SelectFromDropDawn("الموردين", "نوع التصنيف");
        customerClassification.ClickSaveButton();
        waitForRecord(customerClassification, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("إضافة تصنيف مندوب")
    @Description(
            "يتحقق هذا الاختبار من إضافة تصنيف جديد من نوع (مندوبين)، ويشمل إدخال الاسم"
                    + " العربي والإنجليزي والملاحظات، واختيار نوع التصنيف (المندوبين)، وحفظ البيانات"
                    + " والتحقق من إضافة السجل بنجاح.")
    public void AddAgentClassification() {
        CustomerClassification customerClassification = new CustomerClassification(driver);
        String recordName =
                ownedName(customerClassification, Pages.CUSTOMERCLASS, "AddAgentClassification");
        customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
        customerClassification.ClickNew();
        customerClassification.AddArabicName(recordName);
        customerClassification.AddEnglishName("Agent Classification");
        customerClassification.AddNoteForDealer(
                "This is a test description for agent classification.");
        customerClassification.SelectFromDropDawn("المندوبين", "نوع التصنيف");
        customerClassification.ClickSaveButton();
        waitForRecord(customerClassification, recordName);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("تعديل تصنيف")
    @Description(
            "يتحقق هذا الاختبار من تعديل تصنيف موجود، ويشمل تعديل الاسم العربي والإنجليزي"
                    + " والملاحظات وتغيير نوع التصنيف إلى (المندوبين)، وحفظ التعديلات والتحقق من تحديث"
                    + " السجل بنجاح.")
    public void Edit() {
        CustomerClassification customerClassification = new CustomerClassification(driver);
        customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
        String originalName =
                seedRecord(customerClassification, Pages.CUSTOMERCLASS, "EDIT-ORIGINAL");
        customerClassification.openDealerForEditByName(originalName);
        String editedName = ownedName(customerClassification, Pages.CUSTOMERCLASS, "EDIT-UPDATED");
        customerClassification.AddArabicName(editedName);
        customerClassification.AddEnglishName("Agent Classification");
        customerClassification.AddNoteForDealer(
                "This is a test description for agent classification.");
        customerClassification.SelectFromDropDawn("المندوبين", "نوع التصنيف");
        customerClassification.ClickSaveButton();
        waitForRecord(customerClassification, editedName);
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("حذف تصنيف")
    @Description(
            "يتحقق هذا الاختبار من حذف التصنيفات المضافة، ويقوم بحذف ثلاثة سجلات متتالية"
                    + " للتأكد من أن وظيفة الحذف تعمل بشكل صحيح.")
    @Test(priority = 0)
    public void Delete() {
        CustomerClassification customerClassification = new CustomerClassification(driver);
        deleteOwnedRecords(customerClassification, Pages.CUSTOMERCLASS, 2);
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("التحقق - الاسم العربي إلزامي")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع الحفظ في حال عدم إدخال الاسم العربي، ويتحقق"
                    + " من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    public void AssertMessageArabicName() {
        CustomerClassification customerClassification = new CustomerClassification(driver);
        customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
        customerClassification.ClickNew();
        customerClassification.SelectFromDropDawn("المندوبين", "نوع التصنيف");
        customerClassification.ClickSaveButton();
        org.testng.Assert.assertTrue(
                customerClassification.IsArabicNameRequiredErrorDisplayed(),
                "يجب إظهار خطأ الاسم العربي الإلزامي");
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("التحقق - نوع التصنيف إلزامي")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع الحفظ في حال عدم اختيار نوع التصنيف، ويتحقق"
                    + " من ظهور رسالة خطأ تفيد بأن حقل نوع التصنيف إلزامي.")
    public void AssertMessageClassification() {
        CustomerClassification customerClassification = new CustomerClassification(driver);
        customerClassification.NavigationHelper(Pages.CUSTOMERCLASS);
        customerClassification.ClickNew();
        customerClassification.AddArabicName(
                ownedName(customerClassification, Pages.CUSTOMERCLASS, "VALIDATION"));
        customerClassification.ClickSaveButton();
        org.testng.Assert.assertTrue(
                customerClassification.AssertClassification(),
                "يجب إظهار خطأ نوع التصنيف الإلزامي");
    }

    @Test(priority = 90)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("تصدير قائمة التصنيفات إلى PDF")
    @Description("يتحقق من تنزيل PDF صالح من قائمة تصنيفات المتعاملين.")
    public void ExportClassificationsToPdf() throws IOException {
        CustomerClassification page = new CustomerClassification(driver);
        page.NavigationHelper(Pages.CUSTOMERCLASS);
        DealerModelExportAssertions.assertExportDownloaded(
                driver, () -> page.ExportList("pdf"), "pdf");
    }

    @Test(priority = 91)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("تصنيف المتعاملين")
    @Story("تصدير قائمة التصنيفات إلى Excel")
    @Description("يتحقق من تنزيل Excel صالح من قائمة تصنيفات المتعاملين.")
    public void ExportClassificationsToExcel() throws IOException {
        CustomerClassification page = new CustomerClassification(driver);
        page.NavigationHelper(Pages.CUSTOMERCLASS);
        DealerModelExportAssertions.assertExportDownloaded(
                driver, () -> page.ExportList("excel"), "excel");
    }
}
