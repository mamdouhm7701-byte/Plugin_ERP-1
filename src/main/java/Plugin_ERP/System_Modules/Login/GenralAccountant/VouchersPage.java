package Plugin_ERP.System_Modules.Login.GenralAccountant;

import Plugin_ERP.System_Modules.Login.Abstract;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;

import java.math.BigDecimal;
import java.util.List;

/** Page object for the general journal vouchers list and entry form. */
public class VouchersPage extends Abstract {
    private final VoucherRecordList recordControls;
    private final VoucherLines lines;
    private final VoucherAccountPicker accounts;

    private static final By SAVE_BUTTON = By.cssSelector("form button[data-action-type='save']");
    private static final By SAVE_AND_PRINT_BUTTON =
            By.cssSelector("form button[data-action-type='saveAndPrint']");
    private static final By VALIDATION_MESSAGES = By.cssSelector("form small.text-danger");

    public VouchersPage(WebDriver driver) {
        super(driver);
        lines = new VoucherLines(this, driver);
        accounts = new VoucherAccountPicker(this, driver, utilites, waitMangment);
        recordControls = new VoucherRecordList(this, driver);
    }

    @Step("عدد بنود القيد التي تم اختيار حساباتها")
    public int getSelectedLineCount() {
        return lines.getSelectedLineCount();
    }

    @Step("فتح نافذة اختيار الحساب")
    public void openAccountPicker() {
        accounts.openAccountPicker();
    }

    @Step("البحث عن حساب: {query}")
    public void searchAccount(String query) {
        accounts.searchAccount(query);
    }

    @Step("عدد نتائج بحث الحسابات")
    public int getAccountSearchResultCount() {
        return accounts.getAccountSearchResultCount();
    }

    @Step("هل يظهر الحساب في صفوف البحث: {text}")
    public boolean accountSearchContains(String text) {
        return accounts.accountSearchContains(text);
    }

    @Step("عدد صفوف الحسابات في نافذة الاختيار")
    public int getAccountPickerRowCount() {
        return accounts.getAccountPickerRowCount();
    }

    @Step("اختيار الحساب ذي الكود: {accountCode}")
    public void selectAccountByCode(String accountCode) {
        accounts.selectAccountByCode(accountCode);
    }

    @Step("البحث عن الحساب واختياره: {query}")
    public void searchAccountAndSelectFirst(String query) {
        accounts.searchAccountAndSelectFirst(query);
    }

    @Step("إغلاق نافذة الحسابات دون اختيار")
    public void closeAccountPicker() {
        accounts.closeAccountPicker();
    }

    @Step("عدد علامات التسوية الظاهرة في بنود القيد")
    public int getSettlementCheckboxCount() {
        return lines.getSettlementCheckboxCount();
    }

    @Step("تحديد علامة التسوية في البند {lineIndex}: {checked}")
    public void setSettlementChecked(boolean checked) {
        lines.setSettlementChecked(checked);
    }

    @Step("قراءة حالة علامة التسوية في البند {lineIndex}")
    public boolean isSettlementChecked(int lineIndex) {
        return lines.isSettlementChecked(lineIndex);
    }

    @Step("اختيار حساب الضريبة: {accountText}")
    public void selectTaxAccountFromLine(String accountText) {
        searchAccountAndSelectFirst(accountText);
    }

    @Step("اختيار الحساب الخاضع للضريبة بكود {accountCode}")
    public void selectAccountTaxableByCode(String accountCode) {
        selectAccountByCode(accountCode);
        if (getSettlementCheckboxCount() == 0) {
            throw new AssertionError(
                    "Account " + accountCode + " did not expose the 15% VAT settlement checkbox.");
        }
    }

    @Step("هل يوجد إجراء توزيع ضريبة في البند {lineIndex}")
    public boolean hasTaxDistributionAction(int lineIndex) {
        return lines.hasTaxDistributionAction(lineIndex);
    }

    @Step("فتح توزيع الضريبة في البند {lineIndex}")
    public void openTaxDistribution(int lineIndex) {
        lines.openTaxDistribution(lineIndex);
    }

    @Step("قراءة مبلغ الضريبة في البند {lineIndex}")
    public BigDecimal getTaxAmountValue(int lineIndex) {
        return lines.getTaxAmountValue(lineIndex);
    }

    @Step("إدخال المدين في البند {lineIndex}: {amount}")
    public void setDebit(int lineIndex, String amount) {
        lines.setDebit(lineIndex, amount);
    }

    @Step("إدخال الدائن في البند {lineIndex}: {amount}")
    public void setCredit(int lineIndex, String amount) {
        lines.setCredit(lineIndex, amount);
    }

    @Step("قيمة المدين في البند {lineIndex}")
    public String getDebitValue(int lineIndex) {
        return lines.getDebitValue(lineIndex);
    }

    @Step("قيمة الدائن في البند {lineIndex}")
    public String getCreditValue(int lineIndex) {
        return lines.getCreditValue(lineIndex);
    }

    @Step("هل حقل المبلغ من نوع رقمي")
    public boolean isNumericAmountInput(By locator) {
        return lines.isNumericAmountInput(locator);
    }

    @Step("اختيار حساب من القائمة المنسدلة في البند {lineIndex}: {accountText}")
    public void selectAccountFromLineDropdown(int lineIndex, String accountText) {
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        List<WebElement> searches =
                driver.findElements(
                        By.xpath(
                                "//form//table[.//th[normalize-space()='مدين']]//tbody/tr/td[@aria-colindex='2']//input[@type='search']"));
        WebElement input = searches.get(lineIndex - 1);
        input.click();
        input.sendKeys(accountText);
        By option =
                By.xpath(
                        "(//li[contains(@class,'vs__dropdown-option') and contains(normalize-space(),\""
                                + accountText
                                + "\")])[1]");
        waitMangment.fluentWait().until(d -> d.findElement(option).isDisplayed());
        utilites.ClickElement(option);
    }

    public String getVoucherReportText() {
        return driver
                .findElements(
                        By.cssSelector(
                                "div.e-reportviewer-paragrap div[style*='white-space: pre-wrap']"))
                .stream()
                .map(WebElement::getText)
                .reduce("", (a, b) -> a + "\n" + b);
    }

    @Step("التحقق من اختيار حساب للبند {lineIndex}")
    public boolean isLineAccountSelected(int lineIndex) {
        return lines.isLineAccountSelected(lineIndex);
    }

    @Step("إدخال ملاحظة للبند {lineIndex}")
    public void setLineNotes(int lineIndex, String notes) {
        lines.setLineNotes(lineIndex, notes);
    }

    @Step("قراءة ملاحظة البند {lineIndex}")
    public String getLineNotes(int lineIndex) {
        return lines.getLineNotes(lineIndex);
    }

    public boolean isVoucherFormDisplayed() {
        return driver.findElements(By.cssSelector("form #textarea")).stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public void waitForFormReady() {
        utilites.waits().waitForOverlayToDisappear();
        waitMangment.fluentWait().until(d -> !d.findElements(SAVE_BUTTON).isEmpty());
    }

    @Step("هل البحث داخل بنود الحسابات متاح")
    public boolean isAccountDropdownSearchPresent() {
        return accounts.isAccountDropdownSearchPresent();
    }

    @Step("التحقق من بحث خيارات الحساب داخل البند دون حذف البنود")
    public void assertLineAccountSearch(String matchingCode,String otherCode) {
        accounts.assertLineAccountSearch(matchingCode, otherCode);
    }

    @Step("البحث في بنود القيد عن: {query}")
    public void searchVoucherLines(String query) {
        By lineSearch =
                By.xpath(
                        "//input[contains(@placeholder,'بحث') and not(ancestor::div[@role='dialog'])]");
        WebElement input = driver.findElement(lineSearch);
        input.click();
        input.clear();
        input.sendKeys(query);
        waitMangment
                .fluentWait()
                .until(d -> query.equals(d.findElement(lineSearch).getDomProperty("value")));
    }

    @Step("هل توجد رسالة خطأ منبثقة")
    public boolean isErrorPopupDisplayed() {
        return driver.findElements(By.cssSelector(".swal2-container.swal2-backdrop-show")).stream()
                .anyMatch(WebElement::isDisplayed);
    }

    @Step("هل التاريخ بصيغة ISO")
    public boolean isDateInIsoFormat() {
        return getVoucherDate().matches("\\d{4}-\\d{2}-\\d{2}");
    }

    @Step("هل التاريخ صالح وفق المتصفح")
    public boolean isVoucherDateValid() {
        WebElement date =
                driver.findElement(By.cssSelector("form fieldset[label-text='date'] input"));
        Object valid =
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("return arguments[0].validity.valid", date);
        return !date.getAttribute("value").isBlank() && Boolean.TRUE.equals(valid);
    }

    @Step("حذف بند القيد رقم {lineIndex} من المسودة")
    public void removeLine(int lineIndex) {
        lines.removeLine(lineIndex);
    }

    @Step("قراءة الملاحظات في المسودة")
    public String getNotes() {
        return driver.findElement(By.id("textarea")).getDomProperty("value");
    }

    @Step("إدخال الملاحظات العامة: {notes}")
    public void setNotes(String notes) {
        utilites.TaypeTextB(By.id("textarea"), notes);
    }

    @Step("قراءة تاريخ القيد في النموذج")
    public String getVoucherDate() {
        return driver.findElement(By.cssSelector("form fieldset[label-text='date'] input"))
                .getAttribute("value");
    }

    @Step("قراءة إجمالي المدين")
    public BigDecimal getDebitTotal() {
        return lines.getDebitTotal();
    }

    @Step("قراءة إجمالي الدائن")
    public BigDecimal getCreditTotal() {
        return lines.getCreditTotal();
    }

    @Step("مطابقة إجمالي المدين {debit} والدائن {credit}")
    public void assertTotals(String debit, String credit) {
        lines.assertTotals(debit, credit);
    }

    @Step("التحقق من اتزان القيد")
    public boolean isBalanced() {
        return lines.isBalanced();
    }

    @Step("التحقق من رسائل الحقول المطلوبة")
    public boolean hasValidationErrors() {
        List<WebElement> errors = driver.findElements(VALIDATION_MESSAGES);
        return errors.stream().anyMatch(e -> e.isDisplayed() && !e.getText().isBlank());
    }

    @Step("التحقق من وجود زر الحفظ")
    public boolean isSaveAvailable() {
        return driver.findElements(SAVE_BUTTON).stream().anyMatch(WebElement::isDisplayed);
    }

    @Step("التحقق من وجود زر الحفظ والطباعة")
    public boolean isSaveAndPrintAvailable() {
        return driver.findElements(SAVE_AND_PRINT_BUTTON).stream()
                .anyMatch(WebElement::isDisplayed);
    }

    @Step("التحقق من أن المرجع للقراءة فقط")
    public boolean isReferenceReadOnly() {
        return driver.findElement(By.id("reference")).getDomAttribute("readonly") != null;
    }

    @Step("التحقق من أن نوع الحركة للقراءة فقط")
    public boolean isTransactionTypeReadOnly() {
        return driver.findElement(By.id("sourceTransactionType")).getDomAttribute("readonly")
                != null;
    }

    @Step("فتح نافذة اختيار الحساب للبند الأول")
    public void openFirstAccountPicker() {
        accounts.openFirstAccountPicker();
    }

    @Step("اختيار الحساب {accountText} للبند الأول")
    public void selectAccountFromFirstRow(String accountText) {
        accounts.selectAccountFromFirstRow(accountText);
    }

    public boolean hasVoucherMarkerInList(String marker) {
        return recordControls.hasVoucherMarkerInList(marker);
    }

    public boolean hasVoucherMarkerPrefixInList(String markerPrefix) {
        return recordControls.hasVoucherMarkerPrefixInList(markerPrefix);
    }

    public List<String> getVoucherRowsContaining(String marker) {
        return recordControls.getVoucherRowsContaining(marker);
    }

    @Step("حذف القيود المطابقة للمرجع {markerPrefix}")
    public void deleteVoucherRowsContaining(String markerPrefix) {
        recordControls.deleteVoucherRowsContaining(markerPrefix);
    }

    @Step("فتح القيد المحدد للتعديل: {marker}")
    public void editVoucherListRowByMarker(String marker) {
        recordControls.editVoucherListRowByMarker(marker);
    }

    @Step("حذف القيد المحدد: {marker}")
    public void deleteVoucherListRowByMarker(String marker) {
        recordControls.deleteVoucherListRowByMarker(marker);
    }
    void clickLineElement(By locator) { utilites.ClickElement(locator); }
    Plugin_ERP.Bots.WaitMangment lineWaits() { return waitMangment; }
}
