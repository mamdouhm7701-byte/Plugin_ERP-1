/* شرح الملف: تنفيذ حالات القيود اليومية الخاصة بـحفظ السجلات ومراجعة بياناتها.
 */
package GenralAccountantTest;

import Plugin_ERP.System_Modules.Login.GenralAccountant.VouchersPage;
import org.testng.Assert;

final class VouchersSavingChecks {
    private final VouchersTest owner;

    VouchersSavingChecks(VouchersTest owner) {
        this.owner = owner;
    }

    void VoucherDateIsPrepopulatedTest() {
        VouchersPage page = owner.openNewVoucher();

        Assert.assertFalse(
                page.getVoucherDate().isBlank(), "A default voucher date should be displayed.");
    }

    void SaveActionsAvailableTest() {
        VouchersPage page = owner.openNewVoucher();

        Assert.assertTrue(page.isSaveAvailable(), "The Save action should be available.");
        Assert.assertTrue(
                page.isSaveAndPrintAvailable(), "The Save and Print action should be available.");
    }

    void NotesAcceptArabicEnglishAndNumbersTest() {
        VouchersPage page = owner.openNewVoucher();
        String notes = "مراجعة Test 123";
        page.setNotes(notes);

        Assert.assertEquals(page.getNotes(), notes);
    }

    void AddingLinePreservesPreviousValuesTest() {
        owner.caseData().account("debit");
        owner.caseData().account("extra");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        page.setDebit(1, "125.50");
        page.selectAccountByCode(owner.accountCode("extra"));

        Assert.assertEquals(page.getSelectedLineCount(), 2);
        page.assertTotals("125.50", "0");
    }

    void BalancedEntryTotalsTest() {
        owner.caseData().account("debit");
        owner.caseData().account("credit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        page.selectAccountByCode(owner.accountCode("credit"));
        page.setDebit(1, "1000");
        page.setCredit(2, "1000");

        page.assertTotals("1000", "1000");
        Assert.assertTrue(page.isBalanced(), "Equal debit and credit totals should be balanced.");
    }

    void RecalculateBalanceAfterAmountEditTest() {
        owner.caseData().account("debit");
        owner.caseData().account("credit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        page.selectAccountByCode(owner.accountCode("credit"));
        page.setDebit(1, "1000");
        page.setCredit(2, "1000");
        Assert.assertTrue(page.isBalanced());

        page.setDebit(1, "900");

        page.assertTotals("900", "1000");
        Assert.assertFalse(page.isBalanced());
    }

    void MultiLineDecimalTotalsTest() {
        owner.caseData().account("debit");
        owner.caseData().account("extra");
        owner.caseData().account("credit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        page.selectAccountByCode(owner.accountCode("extra"));
        page.selectAccountByCode(owner.accountCode("credit"));
        page.setDebit(1, "100.25");
        page.setDebit(2, "25.25");
        page.setCredit(3, "125.50");

        page.assertTotals("125.50", "125.50");
        Assert.assertTrue(page.isBalanced());
    }

    void RemoveLineUpdatesDraftTotalsTest() {
        owner.caseData().account("debit");
        owner.caseData().account("credit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        page.selectAccountByCode(owner.accountCode("credit"));
        page.setDebit(1, "1000");
        page.setCredit(2, "1000");
        page.removeLine(1);

        Assert.assertEquals(page.getSelectedLineCount(), 1);
        page.assertTotals("0", "1000");
    }

    void SameLineDebitCreditDraftTest() {
        owner.caseData().account("debit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        page.setDebit(1, "100");
        page.setCredit(1, "100");

        Assert.assertEquals(page.getDebitValue(1), "100");
        Assert.assertEquals(page.getCreditValue(1), "100");
        page.assertTotals("100", "100");
    }

    void SelectAccountFromLineDropdownTest() {
        owner.caseData().account("debit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        page.selectAccountFromLineDropdown(1, "سيارة رقم 1");

        Assert.assertTrue(page.isLineAccountSelected(1));
    }

    void LineNotesAcceptMixedTextTest() {
        owner.caseData().account("debit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        page.setLineNotes(1, "بند Test 123");

        Assert.assertEquals(page.getLineNotes(1), "بند Test 123");
    }

    void TaxSettlementTest() {
        owner.caseData().account("tax");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("tax"));
        Assert.assertTrue(
                page.getSettlementCheckboxCount() > 0,
                "The voucher UI should expose the tax-settlement checkbox.");
        page.setSettlementChecked(true);
        Assert.assertTrue(page.isSettlementChecked(1));
        page.setSettlementChecked(false);
        Assert.assertFalse(page.isSettlementChecked(1));
    }
}
