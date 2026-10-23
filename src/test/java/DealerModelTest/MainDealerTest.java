package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.MainDealer;
import io.qameta.allure.*;
import java.io.IOException;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MainDealerTest extends DealerScenarioTest {
    private String unique(String suffix) {
        return ownedName(new MainDealer(driver), Pages.MAINDEALERS, suffix);
    }

    private MainDealer open() {
        MainDealer page = new MainDealer(driver);
        page.NavigationHelper(Pages.MAINDEALERS);
        return page;
    }

    private void create(MainDealer page, String name, String kind) {
        page.ClickNew();
        page.AddArabicName(name);
        page.AddEnglishName("Test " + name);
        page.SelectFromDropDawn(kind, "النوع");
        page.AddNoteForDealer("سجل اختبار آلي");
        page.ClickSaveButton();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ignored -> page.GetExactMatchCount(name) == 1);
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-001 بنية قائمة المتعامل الرئيسي")
    @Description("يتحقق من ظهور أعمدة الكود واسم المتعامل الرئيسي والنوع.")
    public void MD_001_ListStructure() {
        Assert.assertTrue(open().IsListStructureDisplayed());
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-002 عناصر نموذج المتعامل الرئيسي")
    @Description("يتحقق من الحقول الأساسية الظاهرة في نموذج الإضافة.")
    public void MD_002_FormControls() {
        MainDealer page = open();
        page.ClickNew();
        Assert.assertEquals(page.GetMissingFormControls(), "");
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-009 إغلاق نموذج إضافة متعامل دون حفظ")
    @Description("يتحقق من عدم إنشاء متعامل رئيسي عند إغلاق نموذج الإضافة قبل الحفظ.")
    public void MD_009_CloseCreationFormWithoutSaving() {
        MainDealer page = open();
        DealerModelScreenAssertions.assertNewFormCanBeClosedWithoutSaving(page);
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-010 إلغاء تعديل المتعامل دون حفظ")
    @Description(
            "يتحقق من تحميل بيانات المتعامل المحدد ومن عدم حفظ التغيير عند إغلاق نموذج"
                    + " التعديل.")
    public void MD_010_CancelEditWithoutSaving() {
        assertOwnedEditCancelled(open(), Pages.MAINDEALERS);
    }

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-011 إلغاء حذف المتعامل الرئيسي")
    @Description("يتحقق من ظهور تأكيد الحذف ومن بقاء المتعامل عند إلغاء العملية.")
    public void MD_011_CancelDelete() {
        assertOwnedDeleteCancelled(open(), Pages.MAINDEALERS);
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-003 الاسم العربي مطلوب")
    @Description("يتحقق من رفض حفظ نموذج دون الاسم العربي.")
    public void MD_003_ArabicNameRequired() {
        MainDealer page = open();
        page.ClickNew();
        page.SelectFromDropDawn("العملاء", "النوع");
        page.AddEnglishName(ownedName(page, Pages.MAINDEALERS, "INVALID-ARABIC-NAME"));
        page.ClickSaveButton();
        Assert.assertTrue(page.IsArabicNameRequiredDisplayed());
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-004 نوع المتعامل مطلوب")
    @Description("يتحقق من رفض حفظ متعامل رئيسي دون اختيار النوع.")
    public void MD_004_KindRequired() {
        MainDealer page = open();
        page.ClickNew();
        page.AddArabicName(unique("NO-KIND"));
        page.ClickSaveButton();
        Assert.assertTrue(page.IsKindRequiredDisplayed());
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-005 إضافة متعامل رئيسي من نوع عميل")
    @Description("ينشئ متعاملًا فريدًا من نوع العملاء ويتحقق من بياناته بعد الحفظ.")
    public void MD_005_CreateCustomer() {
        MainDealer page = open();
        String name = unique("CUSTOMER");
        create(page, name, "العملاء");
        page.OpenForEditByName(name);
        Assert.assertEquals(page.GetArabicNameValue(), name);
        Assert.assertEquals(page.GetEnglishNameValue(), "Test " + name);
        Assert.assertEquals(page.GetSelectedKindValue(), "العملاء");
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-006 إضافة متعامل رئيسي من نوع مورد")
    @Description("ينشئ متعاملًا فريدًا من نوع الموردين ويتحقق من النوع المحفوظ.")
    public void MD_006_CreateSupplier() {
        MainDealer page = open();
        String name = unique("SUPPLIER");
        create(page, name, "الموردين");
        page.OpenForEditByName(name);
        Assert.assertEquals(page.GetSelectedKindValue(), "الموردين");
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-007 تعديل متعامل مملوك للاختبار")
    @Description("يعدل اسم سجل أنشأه الاختبار ويتحقق من الاسم الجديد.")
    public void MD_007_EditOwnedRecord() {
        MainDealer page = open();
        String originalName = unique("EDIT");
        String updatedName = unique("UPDATED");
        create(page, originalName, "العملاء");
        page.OpenForEditByName(originalName);
        page.AddArabicName(updatedName);
        page.ClickSaveButton();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ignored -> page.GetExactMatchCount(updatedName) == 1);
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-008 حذف متعامل مملوك للاختبار")
    @Description("يحذف سجلًا أنشأه الاختبار فقط بعد تأكيد نافذة الحذف.")
    public void MD_008_DeleteOwnedRecord() {
        MainDealer page = open();
        String name = unique("DELETE");
        create(page, name, "العملاء");
        page.RequestDeleteByName(name);
        page.CancelDelete();
        Assert.assertEquals(
                page.GetExactMatchCount(name), 1, "إلغاء الحذف يجب أن يبقي سجل الاختبار");

        page.RequestDeleteByName(name);
        page.ConfirmDeleteByName(name);
        Assert.assertEquals(page.GetExactMatchCount(name), 0);
    }

    @Test(priority = 90)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-090 تصدير PDF")
    @Description("يتحقق من تنزيل ملف PDF صالح لقائمة المتعامل الرئيسي.")
    public void MD_090_ExportPdf() throws IOException {
        MainDealer page = open();
        DealerModelExportAssertions.assertExportDownloaded(
                driver, () -> page.ExportList("pdf"), "pdf");
    }

    @Test(priority = 91)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("المتعامل الرئيسي")
    @Story("MD-091 تصدير Excel")
    @Description("يتحقق من تنزيل ملف Excel صالح لقائمة المتعامل الرئيسي.")
    public void MD_091_ExportExcel() throws IOException {
        MainDealer page = open();
        DealerModelExportAssertions.assertExportDownloaded(
                driver, () -> page.ExportList("excel"), "excel");
    }
}
