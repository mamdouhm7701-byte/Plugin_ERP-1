/* شرح الملف: خطوات تجهيز وتحقق مشتركة لحالات القيود اليومية، على نفس جلسة وبيانات الحالة الأصلية.
 */
package GenralAccountantTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.GenralAccountant.VouchersPage;
import TestSupport.BasicDataScenarioTest;
import org.openqa.selenium.By;
import org.testng.Assert;

abstract class VouchersCaseSupport extends BasicDataScenarioTest {
    final org.openqa.selenium.WebDriver caseDriver() {
        return driver;
    }

    final TestSupport.BasicDataFixtures caseData() {
        return basicData();
    }

    final String caseName(String endpoint, String label) {
        return ownedName(endpoint, label);
    }

    final long caseRecordCount(String endpoint) {
        return recordCount(endpoint);
    }

    final String accountCode(String key) {
        return basicData().account(key).values().get("code").toString();
    }

    @io.qameta.allure.Step("فتح نموذج قيد يومية جديد")
    final VouchersPage openNewVoucher() {
        VouchersPage page = new VouchersPage(driver);
        page.NavigationHelper(Pages.VOUCHERS);
        driver.findElement(By.xpath("//button[normalize-space()='جديد']")).click();
        waitForVoucherForm();
        return page;
    }

    final void waitForVoucherForm() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                .until(d -> !d.findElements(By.cssSelector("form #textarea")).isEmpty());
        new VouchersPage(driver).waitForFormReady();
    }

    final void assertMarkedVoucherRejected(
            String scenario, java.util.function.Consumer<VouchersPage> setup) {
        String marker = ownedName("Vouchers", scenario);
        VouchersPage page = openNewVoucher();
        boolean wasPersisted = false;
        boolean unexpectedlySaved = false;
        try {
            setup.accept(page);
            page.setNotes(marker);
            page.ClickSaveButton();
            page.NavigationHelper(Pages.VOUCHERS);
            wasPersisted = page.hasVoucherMarkerInList(marker);
            unexpectedlySaved = wasPersisted;
            if (wasPersisted) {
                page.deleteVoucherListRowByMarker(marker);
                wasPersisted = false;
            }
            Assert.assertFalse(
                    unexpectedlySaved,
                    "A "
                            + scenario
                            + " voucher must be rejected; any accepted test record is deleted immediately.");
        } finally {
            if (wasPersisted) {
                page.NavigationHelper(Pages.VOUCHERS);
                if (page.hasVoucherMarkerInList(marker)) page.deleteVoucherListRowByMarker(marker);
            }
        }
    }
}
