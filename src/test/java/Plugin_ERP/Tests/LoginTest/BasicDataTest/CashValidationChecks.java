/* شرح الملف: تنفيذ حالات الخزائن الخاصة بـرفض المدخلات غير الصحيحة.
 */
package Plugin_ERP.Tests.LoginTest.BasicDataTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.BasicData.CashPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.*;

final class CashValidationChecks {
    private final CashTest owner;

    CashValidationChecks(CashTest owner) {
        this.owner = owner;
    }

    void InvalidCashTest() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        String accountName = owner.prepareSafeAccount();
        cashPage.NavigationHelper(Pages.SAFES);
        cashPage.ClickNew();
        cashPage.AddArabicName("");
        cashPage.AddEnglishName(owner.caseName("Safes", "required-field"));
        cashPage.SelectFromDropDawn(accountName, "رقم الحساب");
        long rowCountBefore = owner.caseRecordCount("Safes");
        cashPage.ClickSaveButton();
        Assert.assertTrue(
                cashPage.IsArabicNameRequiredErrorDisplayed(),
                "WH-004: يجب إظهار رسالة تحقق الاسم العربي الفارغ");
        Assert.assertEquals(
                owner.caseRecordCount("Safes"),
                rowCountBefore,
                "WH-004: يجب ألا ينشأ سجل عند غياب الاسم العربي");
        cashPage.ClickCloseButton();
    }

    void EmptyCashFormIsRejected() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        cashPage.NavigationHelper(Pages.SAFES);
        long rowCountBefore = owner.caseRecordCount("Safes");
        cashPage.ClickNew();
        cashPage.AddEnglishName(owner.caseName("Safes", "required-name"));
        cashPage.ClickSaveButton();

        Assert.assertTrue(
                cashPage.IsArabicNameRequiredErrorDisplayed(),
                "يجب أن يظهر تحقق الاسم العربي عند حفظ نموذج فارغ");
        Assert.assertEquals(
                owner.caseRecordCount("Safes"),
                rowCountBefore,
                "WH-006: يجب ألا ينشأ سجل عند ترك النموذج فارغًا");
        cashPage.ClickCloseButton();
    }

    void CashArabicNameWhitespaceIsRejected() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        String accountName = owner.prepareSafeAccount();
        cashPage.NavigationHelper(Pages.SAFES);
        long rowCountBefore = owner.caseRecordCount("Safes");
        cashPage.ClickNew();
        cashPage.AddArabicName("   ");
        cashPage.AddEnglishName(owner.caseName("Safes", "whitespace"));
        cashPage.SelectFromDropDawn(accountName, "رقم الحساب");
        cashPage.ClickSaveButton();

        Assert.assertTrue(
                cashPage.IsArabicNameRequiredErrorDisplayed(),
                "يجب رفض الاسم العربي المكوّن من مسافات فقط");
        Assert.assertEquals(
                owner.caseRecordCount("Safes"),
                rowCountBefore,
                "WH-005: يجب ألا ينشأ سجل للاسم المكوّن من مسافات");
        cashPage.ClickCloseButton();
    }

    void CorrectArabicNameAfterValidationError() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        String accountName = owner.prepareSafeAccount();
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String arabicName = owner.caseName("Safes", "arabicName");

        cashPage.NavigationHelper(Pages.SAFES);
        try {
            cashPage.ClickNew();
            cashPage.AddEnglishName(owner.caseName("Safes", "correctable-invalid"));
            cashPage.ClickSaveButton();
            Assert.assertTrue(
                    cashPage.IsArabicNameRequiredErrorDisplayed(),
                    "WH-013: يجب إظهار تحقق الاسم العربي قبل التصحيح");

            cashPage.AddArabicName(arabicName);
            cashPage.AddEnglishName("Corrected Safe " + uniqueSuffix);
            cashPage.SelectFromDropDawn(accountName, "رقم الحساب");
            cashPage.ClickSaveButton();
            cashPage.searchSafes(arabicName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(arabicName), 1, "WH-013: يجب حفظ الاسم المصحح");

            owner.caseDriver().navigate().refresh();
            cashPage.NavigationHelper(Pages.SAFES);
            cashPage.searchSafes(arabicName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(arabicName),
                    1,
                    "WH-013: يجب استمرار السجل بعد التحديث");
        } finally {
            owner.cleanupTestSafe(cashPage, arabicName);
        }
    }

    void OptionalCashFieldsCanBeEmpty() {
        owner.verifyOptionalSafeScenario(null, false, "WH-007/008/010");
    }

    void WH_010_SaveSafeWithEmptyNotes() {
        owner.verifyOptionalSafeScenario(owner.caseData().account("safe").name(), false, "WH-010");
    }

    void WH_035_ReopeningListDoesNotDuplicateSafe() {
        CashPage cashPage = new CashPage(owner.caseDriver());
        String safeName = owner.caseName("Safes", "safeName");
        cashPage.NavigationHelper(Pages.SAFES);
        try {
            owner.createSafe(
                    cashPage,
                    safeName,
                    "Reopen Safe " + System.currentTimeMillis(),
                    owner.caseData().account("safe").name(),
                    false,
                    "");
            owner.caseDriver().navigate().refresh();
            cashPage.NavigationHelper(Pages.SAFES);
            cashPage.searchSafes(safeName);
            Assert.assertEquals(
                    cashPage.getExactSafeMatchCount(safeName),
                    1,
                    "WH-035: يجب أن يظهر سجل الاختبار مرة واحدة بعد إعادة فتح القائمة");
        } finally {
            owner.cleanupTestSafe(cashPage, safeName);
        }
    }
}
