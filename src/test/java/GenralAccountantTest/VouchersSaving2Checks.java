/* شرح الملف: تنفيذ حالات القيود اليومية الخاصة بـحفظ السجلات ومراجعة بياناتها.
 */
package GenralAccountantTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.GenralAccountant.VouchersPage;
import java.math.BigDecimal;
import org.testng.Assert;

final class VouchersSaving2Checks {
    private final VouchersTest owner;

    VouchersSaving2Checks(VouchersTest owner) {
        this.owner = owner;
    }

    void SavePrintEditAndDeleteTestVoucher() {
        owner.caseData().account("debit");
        owner.caseData().account("credit");
        owner.caseData().account("extra");

        String marker = owner.caseName("Vouchers", "saved-entry");
        VouchersPage page = new VouchersPage(owner.caseDriver());
        boolean saved = false;
        try {
            page = owner.openNewVoucher();
            String originalDate = page.getVoucherDate();
            page.selectAccountByCode(owner.accountCode("debit"));
            page.selectAccountByCode(owner.accountCode("credit"));
            page.selectAccountByCode(owner.accountCode("extra"));
            page.setDebit(1, "125.50");
            page.setCredit(2, "125.50");
            Assert.assertEquals(page.getSelectedLineCount(), 3);
            page.removeLine(3);
            Assert.assertEquals(page.getSelectedLineCount(), 2);
            page.setNotes(marker);
            page.assertTotals("125.50", "125.50");
            page.ClickSaveButton();
            saved = true;

            page.NavigationHelper(Pages.VOUCHERS);
            Assert.assertTrue(
                    page.hasVoucherMarkerInList(marker),
                    "Saved voucher should appear in the list.");
            page.editVoucherListRowByMarker(marker);
            Assert.assertEquals(
                    page.getVoucherDate(),
                    originalDate,
                    "The default voucher date should persist when the saved entry is reopened.");
            Assert.assertEquals(page.getNotes(), marker);
            Assert.assertEquals(
                    new BigDecimal(page.getDebitValue(1)).compareTo(new BigDecimal("125.50")), 0);
            Assert.assertEquals(
                    new BigDecimal(page.getCreditValue(2)).compareTo(new BigDecimal("125.50")), 0);

            page.setDebit(1, "150.75");
            page.setCredit(2, "150.75");
            page.ClickSaveAndPrintButton();
            String report = page.getVoucherReportText();
            Assert.assertTrue(
                    report.contains("150.75"), "Print report should reflect edited amount.");
            owner.caseDriver().close();
            owner.caseDriver()
                    .switchTo()
                    .window(owner.caseDriver().getWindowHandles().iterator().next());

            page.NavigationHelper(Pages.VOUCHERS);
            Assert.assertTrue(page.hasVoucherMarkerInList(marker));
            page.deleteVoucherListRowByMarker(marker);
            saved = false;
        } finally {
            if (saved) {
                try {
                    if (owner.caseDriver().getWindowHandles().size() > 1) {
                        owner.caseDriver().close();
                        owner.caseDriver()
                                .switchTo()
                                .window(owner.caseDriver().getWindowHandles().iterator().next());
                    }
                    page.NavigationHelper(Pages.VOUCHERS);
                    if (page.hasVoucherMarkerInList(marker)) {
                        page.deleteVoucherListRowByMarker(marker);
                    }
                } catch (Exception cleanupFailure) {
                    throw new AssertionError(
                            "Could not clean up the test voucher marked " + marker, cleanupFailure);
                }
            }
        }
    }
}
