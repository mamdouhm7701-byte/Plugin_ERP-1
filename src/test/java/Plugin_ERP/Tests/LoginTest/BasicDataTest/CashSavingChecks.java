/* شرح الملف: تنفيذ حالات الخزائن الخاصة بـحفظ السجلات ومراجعة بياناتها.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.CashPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.*;

final class CashSavingChecks {
    private final CashTest owner;

    CashSavingChecks(CashTest owner) {
        this.owner = owner;
    }

    void ValidCashTest() {
        owner.WH_003_CreateValidSafeAndVerifyFields();
    }

    void EditCash() {
        owner.WH_021_EditOwnedTestSafeAndSave();
    }

    void DeleteCash() {
        owner.WH_023_DeleteOnlyOwnedTestSafe();
    }

    void WH_003_CreateValidSafeAndVerifyFields() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        String suffix = String.valueOf(System.currentTimeMillis());
        String arabicName = owner.caseName("Safes", "arabicName");
        String englishName = "Automation Safe " + suffix;
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            owner.createSafe(
                    cashPage,
                    arabicName,
                    englishName,
                    owner.caseData().account("safe").name(),
                    false,
                    "Created by automation");
            cashPage.openSafeForEditByName(arabicName);
            Assert.assertEquals(
                    cashPage.getArabicNameInputValue().trim(),
                    arabicName,
                    "WH-003: يجب حفظ الاسم العربي المدخل");
            Assert.assertEquals(
                    cashPage.getEnglishNameInputValue().trim(),
                    englishName,
                    "WH-003: يجب حفظ الاسم الإنجليزي المدخل");
            Assert.assertEquals(
                    cashPage.isSafeGeneralChecked(),
                    false,
                    "WH-003: يجب أن تبقى خانة عام غير محددة");
            cashPage.ClickCloseButton();
        } finally {
            owner.cleanupTestSafe(cashPage, arabicName);
        }
    }

    void WH_007_SaveSafeWithoutAccount() {
        owner.verifyOptionalSafeScenario(null, false, "WH-007");
    }

    void WH_008_SaveSafeWithGeneralUnchecked() {
        owner.verifyOptionalSafeScenario(owner.caseData().account("safe").name(), false, "WH-008");
    }

    void WH_009_SaveSafeWithGeneralChecked() {
        owner.verifyOptionalSafeScenario(owner.caseData().account("safe").name(), true, "WH-009");
    }

    void WH_012_RapidDoubleSaveCreatesSingleSafe() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        String accountName = owner.prepareSafeAccount();
        String suffix = String.valueOf(System.currentTimeMillis());
        String arabicName = owner.caseName("Safes", "arabicName");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            cashPage.ClickNew();
            cashPage.AddArabicName(arabicName);
            cashPage.AddEnglishName("Double Save " + suffix);
            cashPage.SelectFromDropDawn(accountName, "رقم الحساب");
            cashPage.clickSaveTwiceRapidly();
            cashPage.searchSafes(arabicName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(arabicName),
                    1,
                    "WH-012: يجب إنشاء سجل واحد فقط لكل حفظ ناجح");
        } finally {
            owner.cleanupTestSafe(cashPage, arabicName);
        }
    }

    void WH_021_EditOwnedTestSafeAndSave() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        String suffix = String.valueOf(System.currentTimeMillis());
        String originalName = owner.caseName("Safes", "originalName");
        String editedName = owner.caseName("Safes", "edited");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            owner.createSafe(
                    cashPage,
                    originalName,
                    "Edit Safe " + suffix,
                    owner.caseData().account("safe").name(),
                    false,
                    "");
            cashPage.openSafeForEditByName(originalName);
            cashPage.AddArabicName(editedName);
            cashPage.ClickSaveButton();
            cashPage.searchSafes(editedName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(editedName),
                    1,
                    "WH-021: يجب تحديث الاسم العربي في السجل المملوك للاختبار");
        } finally {
            owner.cleanupTestSafe(cashPage, editedName);
        }
    }

    void WH_023_DeleteOnlyOwnedTestSafe() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        String safeName = owner.caseName("Safes", "safeName");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            owner.createSafe(
                    cashPage,
                    safeName,
                    "Delete Safe " + System.currentTimeMillis(),
                    owner.caseData().account("safe").name(),
                    false,
                    "");
            cashPage.searchSafes(safeName);
            cashPage.requestDeleteSafeByName(safeName);
            cashPage.confirmSafeDelete();
            owner.caseDriver().navigate().refresh();
            cashPage.NavigationHelper(Pages.SAFES);
            cashPage.searchSafes(safeName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(safeName),
                    0,
                    "WH-023: يجب اختفاء سجل الاختبار بعد تأكيد حذفه");
        } finally {
            owner.cleanupTestSafe(cashPage, safeName);
        }
    }
}
