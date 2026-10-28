/* شرح الملف: تنفيذ حالات القيود اليومية الخاصة بـفتح النماذج وإلغاء التغييرات.
 */
package GenralAccountantTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.GenralAccountant.VouchersPage;
import org.testng.Assert;

final class VouchersFormsChecks {
    private final VouchersTest owner;

    VouchersFormsChecks(VouchersTest owner) {
        this.owner = owner;
    }

    void OpenVouchersPageTest() {
        new VouchersPage(owner.caseDriver()).NavigationHelper(Pages.VOUCHERS);

        Assert.assertTrue(
                owner.caseDriver().getCurrentUrl().contains("/vouchers/"),
                "The vouchers page URL should contain /vouchers/.");
    }

    void CloseAccountPickerKeepsSelectedLineTest() {
        owner.caseData().account("debit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));
        page.openAccountPicker();
        page.closeAccountPicker();

        Assert.assertEquals(
                page.getSelectedLineCount(),
                1,
                "Closing the account picker should preserve the selected draft line.");
    }

    void VoucherDateFormatTest() {
        VouchersPage page = owner.openNewVoucher();

        Assert.assertFalse(page.getVoucherDate().isBlank());
        Assert.assertTrue(page.isVoucherDateValid());
    }
}
