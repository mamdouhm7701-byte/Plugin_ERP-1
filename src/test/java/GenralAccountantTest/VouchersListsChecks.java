/* شرح الملف: تنفيذ حالات القيود اليومية الخاصة بـعرض القوائم والبحث واستمرار السجلات.
 */
package GenralAccountantTest;

import Plugin_ERP.System_Modules.Login.GenralAccountant.VouchersPage;
import org.openqa.selenium.By;
import org.testng.Assert;

final class VouchersListsChecks {
    private final VouchersTest owner;

    VouchersListsChecks(VouchersTest owner) {
        this.owner = owner;
    }

    void SearchAccountByCodeTest() {
        owner.caseData().account("search");

        VouchersPage page = owner.openNewVoucher();
        page.openAccountPicker();
        page.searchAccount(owner.accountCode("search"));

        Assert.assertTrue(
                owner.caseDriver()
                        .findElement(By.cssSelector("div[role='dialog'] table tbody"))
                        .getText()
                        .contains(owner.accountCode("search")),
                "The account search should show the matching code.");

        page.closeAccountPicker();
    }

    void SearchAccountByNameTest() {
        owner.caseData().account("search-name");

        VouchersPage page = owner.openNewVoucher();
        page.openAccountPicker();
        page.searchAccount(owner.caseData().account("search-name").name());

        Assert.assertTrue(
                owner.caseDriver()
                        .findElement(By.cssSelector("div[role='dialog'] table tbody"))
                        .getText()
                        .contains(owner.caseData().account("search-name").name()),
                "The account search should show the matching name.");

        page.closeAccountPicker();
    }

    void SearchAccountWithoutResultsTest() {
        VouchersPage page = owner.openNewVoucher();
        page.openAccountPicker();
        page.searchAccount("__NO_ACCOUNT_MATCH__");

        Assert.assertEquals(
                page.getAccountSearchResultCount(),
                0,
                "An unmatched search should not return account rows.");

        page.closeAccountPicker();
    }

    void SelectAccountFromSearchTest() {
        owner.caseData().account("debit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));

        Assert.assertEquals(page.getSelectedLineCount(), 1);
    }

    void AccountSearchAvailableInLinesTest() {
        owner.caseData().account("debit");
        owner.caseData().account("credit");

        VouchersPage page = owner.openNewVoucher();
        page.selectAccountByCode(owner.accountCode("debit"));

        Assert.assertTrue(page.isAccountDropdownSearchPresent());
        page.selectAccountByCode(owner.accountCode("credit"));
        page.assertLineAccountSearch(
                owner.caseData().account("debit").name(),
                owner.caseData().account("credit").name());
        Assert.assertEquals(
                page.getSelectedLineCount(),
                2,
                "Searching account choices must preserve both existing voucher lines.");
    }
}
