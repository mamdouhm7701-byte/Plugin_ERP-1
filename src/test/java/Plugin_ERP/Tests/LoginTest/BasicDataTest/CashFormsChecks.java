/* شرح الملف: تنفيذ حالات الخزائن الخاصة بـفتح النماذج وإلغاء التغييرات.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.CashPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.*;

final class CashFormsChecks {
    private final CashTest owner;

    CashFormsChecks(CashTest owner) {
        this.owner = owner;
    }

    void WH_002_CashCreationFormControlsAreDisplayed() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();

        String missingControls = cashPage.getMissingCashCreationFormControls();
        Assert.assertTrue(
                missingControls.isEmpty(),
                "WH-002: لم تظهر عناصر النموذج التالية: " + missingControls);
        cashPage.ClickCloseButton();
    }

    void WH_014_CloseCreationFormWithoutSaving() {
        CashPage cashPage = new CashPage(owner.caseDriver());

        cashPage.NavigationHelper(Pages.SAFES);
        String unsavedName = owner.caseName("Safes", "unsavedName");
        cashPage.ClickNew();
        cashPage.AddArabicName(unsavedName);
        cashPage.ClickCloseButton();
        cashPage.searchSafes(unsavedName);

        Assert.assertFalse(
                cashPage.isSafeVisibleInSearchResults(unsavedName),
                "WH-014: لا يجب إنشاء سجل عند إغلاق نافذة الإضافة دون حفظ");
        cashPage.searchSafes("");
    }

    void WH_020_EditFormLoadsCurrentSafeName() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.searchSafes(owner.seedSafe("edit-form"));
        String codeInList = cashPage.getFirstSafeCellByHeader("الكود");
        String nameInList = cashPage.getFirstSafeCellByHeader("اسم الخزنة");
        String accountInList = cashPage.getFirstSafeCellByHeader("رقم الحساب");

        cashPage.openFirstSafeForEdit();
        Assert.assertEquals(
                cashPage.getSafeCodeInputValue(),
                codeInList,
                "WH-020/034: يجب أن يتطابق كود النموذج مع كود الصف");
        Assert.assertEquals(
                cashPage.getArabicNameInputValue().trim(),
                nameInList,
                "WH-020: يجب تحميل الاسم العربي الحالي من السجل المحدد");
        Assert.assertEquals(
                cashPage.getSafeAccountValue(),
                owner.caseData().account("safe").name(),
                "WH-034: يجب أن يطابق اسم الحساب المختار حساب بيانات الاختبار");
        Assert.assertEquals(
                accountInList,
                owner.caseData().read(owner.caseData().account("safe")).get("code").toString(),
                "WH-034: يجب أن يطابق كود الحساب في الصف نفس الحساب المختار");
        cashPage.ClickCloseButton();
    }

    void WH_022_CancelSafeEditWithoutSaving() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        String originalName;
        String temporaryName = owner.caseName("Safes", "temporaryName");

        cashPage.NavigationHelper(Pages.SAFES);
        originalName = owner.seedSafe("cancel-edit");
        cashPage.searchSafes(originalName);
        cashPage.openFirstSafeForEdit();
        cashPage.AddArabicName(temporaryName);
        cashPage.ClickCloseButton();

        cashPage.openFirstSafeForEdit();
        Assert.assertEquals(
                cashPage.getArabicNameInputValue().trim(),
                originalName,
                "WH-022: يجب أن تظل القيمة الأصلية بعد إغلاق التعديل دون حفظ");
        cashPage.ClickCloseButton();
    }

    void OpenCashCreationForm() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();

        Assert.assertTrue(
                cashPage.isCashCreationFormDisplayed(),
                "يجب أن يظهر حقل الاسم العربي في نموذج إضافة الخزنة");
    }

    void WH_024_CancelDeleteOfOwnedTestSafe() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        String safeName = owner.caseName("Safes", "safeName");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            owner.createSafe(
                    cashPage,
                    safeName,
                    "Cancel Delete " + System.currentTimeMillis(),
                    owner.caseData().account("safe").name(),
                    false,
                    "");
            cashPage.searchSafes(safeName);
            cashPage.requestDeleteSafeByName(safeName);
            cashPage.cancelSafeDelete();
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(safeName),
                    1,
                    "WH-024: يجب أن يبقى سجل الاختبار بعد إلغاء الحذف");
        } finally {
            owner.cleanupTestSafe(cashPage, safeName);
        }
    }
}
