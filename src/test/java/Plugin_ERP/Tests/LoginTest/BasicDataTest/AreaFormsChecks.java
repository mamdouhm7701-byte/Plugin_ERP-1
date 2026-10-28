/* شرح الملف: تنفيذ حالات المناطق الخاصة بـفتح النماذج وإلغاء التغييرات.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.System_Modules.Login.BasicData.AreaPage;
import io.qameta.allure.*;
import org.testng.Assert;

final class AreaFormsChecks {
    private final AreaTest owner;

    AreaFormsChecks(AreaTest owner) {
        this.owner = owner;
    }

    void AR_002_AreaCreationFormControlsAreDisplayed() {
        AreaPage page = owner.openAreas();
        page.ClickNew();
        Assert.assertTrue(
                page.GetMissingAreaFormControls().isEmpty(),
                "AR-002: عناصر النموذج غير الظاهرة: " + page.GetMissingAreaFormControls());
        page.ClickCloseButton();
    }

    void AR_014_CloseCreationFormWithoutSaving() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("UNSAVED");
        page.ClickNew();
        page.AddArabicName(name);
        page.ClickCloseButton();
        page.SearchArea(name);
        Assert.assertEquals(
                page.GetExactAreaMatchCount(name), 0, "AR-014: لا يجب إنشاء سجل دون الضغط على حفظ");
    }

    void AR_020_EditFormLoadsCurrentAreaValues() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("READ");
        owner.createArea(page, name, null, null);
        String code = page.GetAreaCellByName(name, "الكود");
        page.EditAreaByName(name);
        Assert.assertEquals(page.GetAreaCodeInputValue(), code, "يجب تحميل الكود الحالي");
        Assert.assertEquals(page.GetArabicNameInputValue(), name, "يجب تحميل الاسم العربي الحالي");
        page.ClickCloseButton();
    }

    void AR_022_CancelAreaEditWithoutSaving() {
        AreaPage page = owner.openAreas();
        String original = owner.uniqueName("CANCEL-ORIGINAL");
        owner.createArea(page, original, null, null);
        page.EditAreaByName(original);
        page.AddArabicName(owner.uniqueName("EDIT-CANCEL"));
        page.ClickCloseButton();
        page.EditAreaByName(original);
        Assert.assertEquals(
                page.GetArabicNameInputValue(), original, "يجب تجاهل التعديل غير المحفوظ");
        page.ClickCloseButton();
    }

    void AR_024_CancelDeleteOfOwnedArea() {
        AreaPage page = owner.openAreas();
        String name = owner.uniqueName("CANCEL-DELETE");
        owner.createArea(page, name, "Cancel Delete Area", "");
        page.RequestDeleteAreaByName(name);
        Assert.assertTrue(page.IsAreaDeleteConfirmationDisplayed());
        page.CancelAreaDelete();
        Assert.assertEquals(
                page.GetExactAreaMatchCount(name), 1, "يجب أن يبقى السجل بعد إلغاء الحذف");
    }
}
