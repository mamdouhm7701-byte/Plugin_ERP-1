/* شرح الملف: تنفيذ حالات القيود اليومية الخاصة بـرفض المدخلات غير الصحيحة.
 */
package GenralAccountantTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.GenralAccountant.VouchersPage;
import org.openqa.selenium.By;
import org.testng.Assert;

final class VouchersValidationChecks {
    private final VouchersTest owner;

    VouchersValidationChecks(VouchersTest owner) {
        this.owner = owner;
    }

    void RejectEmptyVoucherTest() {
        VouchersPage page = owner.openNewVoucher();
        page.setNotes(owner.caseName("Vouchers", "invalid-empty"));
        page.ClickSaveButton();

        Assert.assertTrue(
                page.isVoucherFormDisplayed(),
                "The empty voucher must remain unsaved in the entry form.");
    }

    void ZeroAmountsKeepZeroDraftTotalsTest() {
        owner.caseData().account("debit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));

        page.assertTotals("0", "0");
    }

    void UnbalancedEntryTotalsTest() {
        owner.caseData().account("debit");
        owner.caseData().account("credit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        page.selectAccountByCode(owner.accountCode("credit"));
        page.setDebit(1, "1000");
        page.setCredit(2, "900");

        page.assertTotals("1000", "900");
        Assert.assertFalse(
                page.isBalanced(), "An entry with unequal totals must be reported as unbalanced.");
    }

    void AmountFieldsAreNumericTest() {
        owner.caseData().account("debit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        By debit =
                By.xpath(
                        "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])[1]//td[@aria-colindex='3']//input");
        By credit =
                By.xpath(
                        "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])[1]//td[@aria-colindex='4']//input");

        Assert.assertTrue(page.isNumericAmountInput(debit));
        Assert.assertTrue(page.isNumericAmountInput(credit));
    }

    void RejectUnbalancedVoucherOnSaveTest() {
        owner.caseData().account("debit");
        owner.caseData().account("credit");

        String marker = owner.caseName("Vouchers", "unbalanced");
        VouchersPage page = owner.openNewVoucher();
        boolean markerExists = false;
        boolean unexpectedlySaved = false;
        try {
            page.selectAccountByCode(owner.accountCode("debit"));
            page.selectAccountByCode(owner.accountCode("credit"));
            page.setDebit(1, "1000");
            page.setCredit(2, "900");
            page.setNotes(marker);
            page.ClickSaveButton();
            page.NavigationHelper(Pages.VOUCHERS);
            markerExists = page.hasVoucherMarkerInList(marker);
            unexpectedlySaved = markerExists;
            if (markerExists) {
                page.deleteVoucherListRowByMarker(marker);
                markerExists = false;
            }
            Assert.assertFalse(
                    unexpectedlySaved,
                    "An unbalanced voucher must not persist; any accepted test record is deleted immediately.");
        } finally {
            if (markerExists) {
                page.NavigationHelper(Pages.VOUCHERS);
                if (page.hasVoucherMarkerInList(marker)) page.deleteVoucherListRowByMarker(marker);
            }
        }
    }

    void RejectZeroValueVoucherOnSaveTest() {
        owner.caseData().account("debit");

        owner.assertMarkedVoucherRejected(
                "ZERO",
                page -> {
                    page.selectAccountByCode(owner.accountCode("debit"));
                });
    }

    void RejectNegativeJournalAmountTest() {
        owner.caseData().account("debit");
        owner.caseData().account("credit");

        owner.assertMarkedVoucherRejected(
                "NEGATIVE",
                page -> {
                    page.selectAccountByCode(owner.accountCode("debit"));
                    page.selectAccountByCode(owner.accountCode("credit"));
                    page.setDebit(1, "-100");
                    page.setCredit(2, "-100");
                });
    }

    void NoMarkedValidationVouchersRemainTest() {
        owner.caseData().collectUi();
        owner.caseData().close();
    }

    void RejectDebitAndCreditOnSameLineTest() {
        owner.caseData().account("debit");

        owner.assertMarkedVoucherRejected(
                "SAME-LINE",
                page -> {
                    page.selectAccountByCode(owner.accountCode("debit"));
                    page.setDebit(1, "100");
                    page.setCredit(1, "100");
                });
    }
}
