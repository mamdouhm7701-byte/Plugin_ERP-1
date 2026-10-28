/* شرح الملف: تنفيذ حالات المناطق الخاصة بـرفض المدخلات غير الصحيحة.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.System_Modules.Login.BasicData.AreaPage;
import io.qameta.allure.*;
import org.testng.Assert;

final class AreaValidationChecks {
    private final AreaTest owner;

    AreaValidationChecks(AreaTest owner) {
        this.owner = owner;
    }

    void AR_004_ArabicNameIsRequired() {
        AreaPage page = owner.openAreas();
        int before = page.GetVisibleAreaRowCount();
        page.ClickNew();
        page.AddNoteForDealer(owner.uniqueName("INVALID-NAME"));
        page.ClickSaveButton();
        Assert.assertTrue(
                page.IsAreaArabicNameRequiredDisplayed(), "يجب إظهار رسالة الاسم العربي مطلوب");
        Assert.assertEquals(page.GetVisibleAreaRowCount(), before, "لا يجب إنشاء سجل دون اسم عربي");
        page.ClickCloseButton();
    }

    void AR_005_WhitespaceNameIsRejected() {
        AreaPage page = owner.openAreas();
        int before = page.GetVisibleAreaRowCount();
        page.ClickNew();
        page.AddArabicName("   ");
        page.AddNoteForDealer(owner.uniqueName("WHITESPACE"));
        page.ClickSaveButton();
        Assert.assertTrue(
                page.IsAreaArabicNameRequiredDisplayed(), "يجب رفض الاسم المكوّن من مسافات");
        Assert.assertEquals(page.GetVisibleAreaRowCount(), before, "لا يجب إنشاء صف للمسافات فقط");
        page.ClickCloseButton();
    }

    void AR_013_CorrectArabicNameAfterValidationError() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("CORRECT");
        page.ClickNew();
        page.AddNoteForDealer(owner.uniqueName("INVALID-NAME"));
        page.ClickSaveButton();
        Assert.assertTrue(page.IsAreaArabicNameRequiredDisplayed(), "يجب إظهار الخطأ قبل التصحيح");
        page.AddArabicName(name);
        page.AddEnglishName("Corrected area");
        page.ClickSaveButton();
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 1);
        owner.caseDriver().navigate().refresh();
        page = owner.openAreas();
        page.SearchArea(name);
        Assert.assertEquals(
                page.GetExactAreaMatchCount(name), 1, "يجب بقاء المنطقة بعد تحديث الصفحة");
    }

    void AR_007_OptionalFieldsCanBeEmpty() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("OPTIONAL");
        owner.createArea(page, name, null, null);
        page.EditAreaByName(name);
        Assert.assertEquals(page.GetArabicNameInputValue(), name);
        Assert.assertEquals(
                page.GetEnglishNameInputValue(),
                name,
                "عند ترك الاسم الإنجليزي فارغًا، الواجهة تحفظه مساويًا للاسم العربي");
        Assert.assertTrue(page.GetAreaNotesValue().isEmpty(), "الملاحظات يجب أن تبقى اختيارية");
        page.ClickCloseButton();
    }

    void AR_035_ReopeningAreaListDoesNotDuplicateRecord() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("REOPEN");
        owner.createArea(page, name, "Reopen Area", "");
        owner.caseDriver().navigate().refresh();
        page = owner.openAreas();
        page.SearchArea(name);
        Assert.assertEquals(page.GetExactAreaMatchCount(name), 1);
    }
}
