/* شرح الملف: اختبارات الخزائن: إنشاء خزنة ومراجعة البيانات المحفوظة؛ فتح سجل للتعديل مع تحميل
 * البيانات الحالية؛ إلغاء حذف سجل الخزائن؛ البحث عن الخزنة بالكود والاسم؛ رفض اسم عربي فارغ في
 * الخزائن.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import io.qameta.allure.*;
import org.testng.annotations.*;

public class CashTest extends CashCaseSupport {

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-001 فتح شاشة الخزن وعرض القائمة")
    @Description("يتحقق من ظهور أعمدة قائمة الخزن وأزرار التعديل والحذف للصفوف الموجودة.")
    public void WH_001_SafeListStructureIsDisplayed() {
        new CashListsChecks(this).WH_001_SafeListStructureIsDisplayed();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-002 فتح نافذة إضافة خزنة")
    @Description("يتحقق من ظهور الحقول والأزرار المحددة في مواصفة WH-002.")
    public void WH_002_CashCreationFormControlsAreDisplayed() {
        new CashFormsChecks(this).WH_002_CashCreationFormControlsAreDisplayed();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-014 إغلاق نافذة الإضافة دون حفظ")
    @Description(
            "يتحقق من إغلاق نافذة الإضافة بعد إدخال قيمة غير محفوظة، وعدم ظهورها ضمن نتائج"
                    + " القائمة.")
    public void WH_014_CloseCreationFormWithoutSaving() {
        new CashFormsChecks(this).WH_014_CloseCreationFormWithoutSaving();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-020 فتح سجل للتعديل مع تحميل البيانات الحالية")
    @Description(
            "يقارن كود الخزنة واسمها العربي ورقم الحساب في الصف بالقيم المحملة في نموذج"
                    + " التعديل، ثم يغلق دون حفظ.")
    public void WH_020_EditFormLoadsCurrentSafeName() {
        new CashFormsChecks(this).WH_020_EditFormLoadsCurrentSafeName();
    }

    @Test(priority = 0)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-022 إلغاء التعديل دون حفظ")
    @Description(
            "يغيّر الاسم داخل نموذج التعديل ثم يغلقه دون حفظ، ويتأكد من بقاء الاسم الأصلي بعد"
                    + " إعادة فتح السجل.")
    public void WH_022_CancelSafeEditWithoutSaving() {
        new CashFormsChecks(this).WH_022_CancelSafeEditWithoutSaving();
    }

    // Compatibility alias; the WH scenario is the executable TestNG test.
    @Deprecated
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("إضافة خزنة ببيانات صحيحة")
    @Description(
            "يتحقق هذا الاختبار من إضافة خزنة جديدة ببيانات صحيحة، ويشمل إدخال الاسم العربي"
                    + " والإنجليزي والملاحظات، واختيار رقم الحساب وتفعيل خانة (عام)، وحفظ البيانات"
                    + " والتحقق من إضافة السجل بنجاح.")
    public void ValidCashTest() {
        new CashSavingChecks(this).ValidCashTest();
    }

    // Compatibility alias; the WH scenario is the executable TestNG test.
    @Deprecated
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("تعديل خزنة ببيانات صحيحة")
    @Description(
            "يتحقق هذا الاختبار من تعديل خزنة موجودة ببيانات صحيحة، ويشمل تعديل الاسم العربي"
                    + " وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    public void EditCash() {
        new CashSavingChecks(this).EditCash();
    }

    // Compatibility alias; the WH scenario is the executable TestNG test.
    @Deprecated
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("حذف خزنة")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف خزنة موجودة بنجاح.")
    public void DeleteCash() {
        new CashSavingChecks(this).DeleteCash();
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-004 التحقق من إلزامية الاسم العربي")
    @Description(
            "يتحقق هذا الاختبار من أن النظام يمنع إضافة خزنة بدون إدخال الاسم العربي، ويتحقق"
                    + " من ظهور رسالة خطأ تفيد بأن الاسم العربي حقل إلزامي.")
    public void InvalidCashTest() {
        new CashValidationChecks(this).InvalidCashTest();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-006 محاولة الحفظ مع جميع الحقول فارغة")
    @Description("يتحقق من أن حفظ نموذج خزنة فارغ يعرض تحقق الاسم العربي المطلوب ولا ينشئ سجلًا.")
    public void EmptyCashFormIsRejected() {
        new CashValidationChecks(this).EmptyCashFormIsRejected();
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-005 منع الاسم العربي المكوّن من مسافات فقط")
    @Description("يتحقق من معاملة المسافات فقط في الاسم العربي كقيمة فارغة.")
    public void CashArabicNameWhitespaceIsRejected() {
        new CashValidationChecks(this).CashArabicNameWhitespaceIsRejected();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-013 Correct Arabic name after validation error")
    @Description("يتحقق من إمكانية تصحيح الاسم العربي بعد إظهار رسالة الإلزامية ثم حفظ الخزنة.")
    public void CorrectArabicNameAfterValidationError() {
        new CashValidationChecks(this).CorrectArabicNameAfterValidationError();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-007/008/010 Optional fields and unchecked General")
    @Description(
            "يتحقق من قبول الاسم العربي وحده عند ترك رقم الحساب والملاحظات فارغين وخيار عام"
                    + " غير محدد.")
    public void OptionalCashFieldsCanBeEmpty() {
        new CashValidationChecks(this).OptionalCashFieldsCanBeEmpty();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-002 Open cash creation form")
    @Description("يتحقق من فتح نموذج إضافة الخزنة عند الضغط على زر جديد.")
    public void OpenCashCreationForm() {
        new CashFormsChecks(this).OpenCashCreationForm();
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-001 Display safe list")
    @Description("يتحقق من ظهور سجلات الخزن في الجدول دون فتح نموذج الإضافة.")
    public void CashListDisplaysRecords() {
        new CashListsChecks(this).CashListDisplaysRecords();
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-015/016/018/019 Search safe list")
    @Description(
            "يتحقق من البحث بالكود والاسم، ومن ظهور نتائج فارغة لقيمة غير موجودة ثم استعادة"
                    + " القائمة بعد مسح البحث.")
    public void SearchSafesByCodeAndName() {
        new CashListsChecks(this).SearchSafesByCodeAndName();
    }

    @Test(priority = 12)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-017 البحث بالاسم الإنجليزي")
    @Description("يتحقق من البحث باستخدام نص الاسم الإنجليزي الموجود في أول سجل دون تعديل بياناته.")
    public void WH_017_SearchSafesByEnglishName() {
        new CashListsChecks(this).WH_017_SearchSafesByEnglishName();
    }

    @Test(priority = 13)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-028 ترتيب قائمة الخزن")
    @Description(
            "يتحقق من إمكانية تغيير ترتيب القائمة بالنقر على عنوان عمود الكود وملاحظة تغير"
                    + " ترتيب الصفوف.")
    public void WH_028_SafeListCanBeSortedByCode() {
        new CashListsChecks(this).WH_028_SafeListCanBeSortedByCode();
    }

    @Test(priority = 14)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.MINOR)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-029 تغيير عدد الصفوف المعروضة")
    @Description(
            "يتحقق من أن تغيير اختيار حجم الصفحة يغير عدد الصفوف المعروضة إلى القيمة"
                    + " المختارة.")
    public void WH_029_ChangingPageSizeUpdatesVisibleRows() {
        new CashListsChecks(this).WH_029_ChangingPageSizeUpdatesVisibleRows();
    }

    @Test(priority = 15)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-030 التنقل بين صفحات قائمة الخزن")
    @Description("يتحقق من الانتقال للصفحة التالية والعودة للسابق فقط عندما تتوفر صفحات متعددة.")
    public void WH_030_NavigateSafeListPagesWhenAvailable() {
        new CashListsChecks(this).WH_030_NavigateSafeListPagesWhenAvailable();
    }

    @Test(priority = 16)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-003 إضافة خزنة ببيانات صحيحة")
    public void WH_003_CreateValidSafeAndVerifyFields() {
        new CashSavingChecks(this).WH_003_CreateValidSafeAndVerifyFields();
    }

    @Test(priority = 17)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-007 رقم الحساب غير محدد")
    public void WH_007_SaveSafeWithoutAccount() {
        new CashSavingChecks(this).WH_007_SaveSafeWithoutAccount();
    }

    @Test(priority = 18)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-008 حفظ الخزنة وخيار عام غير محدد")
    public void WH_008_SaveSafeWithGeneralUnchecked() {
        new CashSavingChecks(this).WH_008_SaveSafeWithGeneralUnchecked();
    }

    @Test(priority = 19)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-009 حفظ الخزنة وخيار عام محدد")
    public void WH_009_SaveSafeWithGeneralChecked() {
        new CashSavingChecks(this).WH_009_SaveSafeWithGeneralChecked();
    }

    @Test(priority = 20)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.MINOR)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-010 حفظ الملاحظات فارغة")
    public void WH_010_SaveSafeWithEmptyNotes() {
        new CashValidationChecks(this).WH_010_SaveSafeWithEmptyNotes();
    }

    @Test(priority = 22)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-012 منع إنشاء سجل مكرر عند الضغط على حفظ مرتين")
    public void WH_012_RapidDoubleSaveCreatesSingleSafe() {
        new CashSavingChecks(this).WH_012_RapidDoubleSaveCreatesSingleSafe();
    }

    @Test(priority = 23)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-021 تعديل اسم الخزنة وحفظ التغيير")
    public void WH_021_EditOwnedTestSafeAndSave() {
        new CashSavingChecks(this).WH_021_EditOwnedTestSafeAndSave();
    }

    @Test(priority = 24)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-023 طلب حذف خزنة اختبار")
    public void WH_023_DeleteOnlyOwnedTestSafe() {
        new CashSavingChecks(this).WH_023_DeleteOnlyOwnedTestSafe();
    }

    @Test(priority = 25)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-024 إلغاء حذف الخزنة")
    public void WH_024_CancelDeleteOfOwnedTestSafe() {
        new CashFormsChecks(this).WH_024_CancelDeleteOfOwnedTestSafe();
    }

    @Test(priority = 26)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-031 تحديث القائمة بعد الإضافة")
    public void WH_031_NewSafeAppearsAfterListRefresh() {
        new CashListsChecks(this).WH_031_NewSafeAppearsAfterListRefresh();
    }

    @Test(priority = 27)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-032 استمرارية حفظ السجل بعد تحديث الصفحة")
    public void WH_032_SafePersistsAfterBrowserRefresh() {
        new CashListsChecks(this).WH_032_SafePersistsAfterBrowserRefresh();
    }

    @Test(priority = 28)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("WH-035 عدم تكرار السجل بعد إعادة فتح القائمة")
    public void WH_035_ReopeningListDoesNotDuplicateSafe() {
        new CashValidationChecks(this).WH_035_ReopeningListDoesNotDuplicateSafe();
    }

    @Test(priority = 29)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("تصدير قائمة الخزن إلى PDF")
    @Description("يتحقق من تنزيل ملف PDF غير فارغ يبدأ بتوقيع PDF الصحيح.")
    public void ExportSafesToPdf() throws Exception {
        new CashExportsChecks(this).ExportSafesToPdf();
    }

    @Test(priority = 30)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الخزن")
    @Story("تصدير قائمة الخزن إلى Excel")
    @Description("يتحقق من تنزيل ملف Excel غير فارغ بصيغة XLS أو XLSX.")
    public void ExportSafesToExcel() throws Exception {
        new CashExportsChecks(this).ExportSafesToExcel();
    }
}
