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

    private static final By SAVE_BUTTON = By.cssSelector("form button[data-action-type='save']");
    private static final By SAVE_AND_PRINT_BUTTON =
            By.cssSelector("form button[data-action-type='saveAndPrint']");
    private static final By ADD_ACCOUNT_BUTTON =
            By.xpath("//form//button[normalize-space()='اضافة حساب']");
    private static final By ACCOUNT_DIALOG = By.cssSelector("div[role='dialog']");
    private static final By ACCOUNT_DIALOG_SEARCH =
            By.cssSelector("div[role='dialog'] input[placeholder='بحث ....']");
    private static final By ACCOUNT_DIALOG_CLOSE =
            By.cssSelector("div[role='dialog'] button[aria-label='Close']");
    private static final By VALIDATION_MESSAGES = By.cssSelector("form small.text-danger");
    private static final By ROW_ACCOUNT_SELECTS =
            By.xpath(
                    "//form//table[.//th[normalize-space()='مدين']]//tbody/tr/td[@aria-colindex='2']//input[@type='search']");

    public VouchersPage(WebDriver driver) {
        super(driver);
    }

    @Step("عدد بنود القيد التي تم اختيار حساباتها")
    public int getSelectedLineCount() {
        return (int) driver.findElements(
                        By.xpath(
                                "//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!='']"))
                .stream().filter(WebElement::isDisplayed).count();
    }

    @Step("فتح نافذة اختيار الحساب")
    public void openAccountPicker() {
        utilites.ClickElement(ADD_ACCOUNT_BUTTON);
        waitMangment.fluentWait().until(d -> d.findElement(ACCOUNT_DIALOG).isDisplayed());
    }

    @Step("البحث عن حساب: {query}")
    public void searchAccount(String query) {
        WebElement input = waitMangment.fluentWait().until(
                org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(ACCOUNT_DIALOG_SEARCH));
        input.click();
        input.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        input.sendKeys(query);
        waitMangment.fluentWait().until(d -> query.equals(input.getDomProperty("value")));
    }

    @Step("عدد نتائج بحث الحسابات")
    public int getAccountSearchResultCount() {
        return driver.findElements(
                        By.xpath(
                                "//div[@role='dialog']//table/tbody/tr[not(contains(@class,'b-table-empty-row'))]"))
                .size();
    }

    @Step("هل يظهر الحساب في صفوف البحث: {text}")
    public boolean accountSearchContains(String text) {
        return driver.findElements(By.cssSelector("div[role='dialog'] table tbody")).stream()
                .anyMatch(e -> e.getText().contains(text));
    }

    @Step("عدد صفوف الحسابات في نافذة الاختيار")
    public int getAccountPickerRowCount() {
        return getAccountSearchResultCount();
    }

    @Step("اختيار الحساب ذي الكود: {accountCode}")
    public void selectAccountByCode(String accountCode) {
        openAccountPicker();
        searchAccount(accountCode);
        By accountRowAddButton =
                By.xpath(
                        "//div[@role='dialog']//table/tbody/tr[td[1][normalize-space()='"
                                + accountCode
                                + "']]//button");
        utilites.ClickElement(accountRowAddButton);
        // The picker stays open after adding an account; close it before interacting with the form.
        waitMangment.fluentWait().until(d -> !d.findElements(accountRowAddButton).isEmpty());
        closeAccountPicker();
    }

    @Step("البحث عن الحساب واختياره: {query}")
    public void searchAccountAndSelectFirst(String query) {
        openAccountPicker();
        searchAccount(query);
        waitMangment.fluentWait().until(d -> getAccountSearchResultCount() > 0);
        WebElement row =
                driver.findElement(
                        By.xpath(
                                "//div[@role='dialog']//table/tbody/tr[not(contains(@class,'b-table-empty-row'))][1]"));
        String summary = row.getText();
        row.findElement(By.cssSelector("button")).click();
        closeAccountPicker();
        io.qameta.allure.Allure.step("Selected account from VAT search: " + summary);
    }

    @Step("إغلاق نافذة الحسابات دون اختيار")
    public void closeAccountPicker() {
        utilites.ClickElement(ACCOUNT_DIALOG_CLOSE);
        waitMangment.fluentWait().until(d -> d.findElements(ACCOUNT_DIALOG).isEmpty());
    }

    private By lineAmountInput(int lineIndex, int columnIndex) {
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        if (columnIndex != 3 && columnIndex != 4)
            throw new IllegalArgumentException("Amount column must be debit (3) or credit (4)");
        return By.xpath(
                "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])["
                        + lineIndex
                        + "]//td[@aria-colindex='"
                        + columnIndex
                        + "']//input[@type='number']");
    }

    private By selectedLine(int lineIndex) {
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        return By.xpath(
                "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])["
                        + lineIndex
                        + "]");
    }

    private By lineElement(int lineIndex, String selector) {
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        return By.xpath(
                "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])["
                        + lineIndex
                        + "]//"
                        + selector);
    }

    @Step("عدد علامات التسوية الظاهرة في بنود القيد")
    public int getSettlementCheckboxCount() {
        return driver.findElements(
                        By.cssSelector("form input[type='checkbox'], form [role='checkbox']"))
                .size();
    }

    @Step("تحديد علامة التسوية في البند {lineIndex}: {checked}")
    public void setSettlementChecked(boolean checked) {
        By locator = settlementCheckbox();
        WebElement checkbox = driver.findElement(locator);
        if (checkbox.isSelected() != checked) {
            String id = checkbox.getAttribute("id");
            List<WebElement> labels =
                    id == null || id.isBlank()
                            ? List.of()
                            : driver.findElements(By.cssSelector("form label[for='" + id + "']"));
            if (!labels.isEmpty()) {
                WebElement label = labels.get(0);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", label);
            } else {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", checkbox);
            }
        }
    }

    @Step("قراءة حالة علامة التسوية في البند {lineIndex}")
    public boolean isSettlementChecked(int lineIndex) {
        return driver.findElement(settlementCheckbox()).isSelected();
    }

    private By settlementCheckbox() {
        return By.cssSelector("form input[type='checkbox'], form [role='checkbox']");
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
        return !driver.findElements(
                        lineElement(lineIndex, "button[@data-action-type='shareToTaxAccount']"))
                .isEmpty();
    }

    @Step("فتح توزيع الضريبة في البند {lineIndex}")
    public void openTaxDistribution(int lineIndex) {
        driver.findElement(lineElement(lineIndex, "button[@data-action-type='shareToTaxAccount']"))
                .click();
    }

    @Step("قراءة مبلغ الضريبة في البند {lineIndex}")
    public BigDecimal getTaxAmountValue(int lineIndex) {
        String text =
                driver.findElement(
                                lineElement(
                                        lineIndex,
                                        "input[@data-tax-amount or contains(@name,'tax') or contains(@aria-label,'ضريبة')]"))
                        .getDomProperty("value");
        return new BigDecimal(text);
    }

    @Step("إدخال المدين في البند {lineIndex}: {amount}")
    public void setDebit(int lineIndex, String amount) {
        setInputValue(lineAmountInput(lineIndex, 3), amount);
    }

    @Step("إدخال الدائن في البند {lineIndex}: {amount}")
    public void setCredit(int lineIndex, String amount) {
        setInputValue(lineAmountInput(lineIndex, 4), amount);
    }

    @Step("قيمة المدين في البند {lineIndex}")
    public String getDebitValue(int lineIndex) {
        return driver.findElement(lineAmountInput(lineIndex, 3)).getDomProperty("value");
    }

    @Step("قيمة الدائن في البند {lineIndex}")
    public String getCreditValue(int lineIndex) {
        return driver.findElement(lineAmountInput(lineIndex, 4)).getDomProperty("value");
    }

    @Step("هل حقل المبلغ من نوع رقمي")
    public boolean isNumericAmountInput(By locator) {
        return "number".equals(driver.findElement(locator).getAttribute("type"));
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
        return !driver.findElement(
                        By.xpath(
                                "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr)["
                                        + lineIndex
                                        + "]//td[@aria-colindex='2']//span[contains(@class,'vs__selected')]"))
                .getText()
                .trim()
                .isEmpty();
    }

    @Step("إدخال ملاحظة للبند {lineIndex}")
    public void setLineNotes(int lineIndex, String notes) {
        setInputValue(lineNoteInput(lineIndex), notes);
    }

    @Step("قراءة ملاحظة البند {lineIndex}")
    public String getLineNotes(int lineIndex) {
        return driver.findElement(lineNoteInput(lineIndex)).getDomProperty("value");
    }

    private By lineNoteInput(int lineIndex) {
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        return By.xpath(
                "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])["
                        + lineIndex
                        + "]//td[@aria-colindex='6']//input");
    }

    private void setInputValue(By locator, String value) {
        WebElement input = driver.findElement(locator);
        ((JavascriptExecutor) driver)
                .executeScript(
                        "const el=arguments[0]; const setter=Object.getOwnPropertyDescriptor(HTMLInputElement.prototype,'value').set; setter.call(el,arguments[1]); el.dispatchEvent(new Event('input',{bubbles:true})); el.dispatchEvent(new Event('change',{bubbles:true}));",
                        input,
                        value);
        waitMangment
                .fluentWait()
                .until(
                        d -> {
                            WebElement current = d.findElement(locator);
                            String actual = current.getDomProperty("value");
                            if ("number".equals(current.getAttribute("type"))) {
                                try {
                                    return new BigDecimal(value).compareTo(new BigDecimal(actual))
                                            == 0;
                                } catch (Exception ignored) {
                                    return false;
                                }
                            }
                            return value.equals(actual);
                        });
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
        return !driver.findElements(
                        By.xpath(
                                "//form//table[.//th[normalize-space()='مدين']]//tbody/tr/td[@aria-colindex='2']//input[@type='search']"))
                .isEmpty();
    }

    @Step("التحقق من بحث خيارات الحساب داخل البند دون حذف البنود")
    public void assertLineAccountSearch(String matchingCode,String otherCode) {
        By search=By.xpath("(//form//table[.//th[normalize-space()='مدين']]//tbody/tr/td[@aria-colindex='2']//input[@type='search'])[1]");
        WebElement input=waitMangment.fluentWait().until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(search));
        input.click();input.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL,"a"));input.sendKeys(matchingCode);
        waitMangment.fluentWait().until(d->{
            var options=d.findElements(By.cssSelector("li.vs__dropdown-option")).stream().filter(WebElement::isDisplayed).map(WebElement::getText).toList();
            return !options.isEmpty()&&options.stream().allMatch(text->text.contains(matchingCode))
                    &&options.stream().noneMatch(text->text.contains(otherCode));
        });
        input.sendKeys(org.openqa.selenium.Keys.ESCAPE);
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
        if (lineIndex < 1) throw new IllegalArgumentException("lineIndex must be 1 or greater");
        By removeButton =
                By.xpath(
                        "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr[normalize-space(td[1])!=''])["
                                + lineIndex
                                + "]//*[name()='svg' and contains(@class,'feather-trash')]");
        utilites.ClickElement(removeButton);
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
        return readTotal(3);
    }

    @Step("قراءة إجمالي الدائن")
    public BigDecimal getCreditTotal() {
        return readTotal(4);
    }

    private BigDecimal readTotal(int columnIndex) {
        String text =
                driver.findElement(
                                By.xpath(
                                        "//form//table[.//th[normalize-space()='مدين']]/tfoot/tr/th[@aria-colindex='"
                                                + columnIndex
                                                + "']"))
                        .getText()
                        .replaceAll("[^0-9.,-]", "")
                        .replace(",", "");
        return text.isBlank() ? BigDecimal.ZERO : new BigDecimal(text);
    }

    @Step("مطابقة إجمالي المدين {debit} والدائن {credit}")
    public void assertTotals(String debit, String credit) {
        Assert.assertEquals(
                getDebitTotal().compareTo(new BigDecimal(debit)), 0, "إجمالي المدين: المتوقع " + debit + "، الفعلي " + getDebitTotal());
        Assert.assertEquals(
                getCreditTotal().compareTo(new BigDecimal(credit)), 0, "إجمالي الدائن: المتوقع " + credit + "، الفعلي " + getCreditTotal());
    }

    @Step("التحقق من اتزان القيد")
    public boolean isBalanced() {
        return getDebitTotal().compareTo(getCreditTotal()) == 0;
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
        By firstRowSearch =
                By.xpath(
                        "(//form//table[.//th[normalize-space()='مدين']]//tbody/tr/td[@aria-colindex='2']//input[@type='search'])[1]");
        WebElement input = driver.findElement(firstRowSearch);
        input.click();
        waitMangment
                .fluentWait()
                .until(d -> !d.findElements(By.cssSelector("li.vs__dropdown-option")).isEmpty());
    }

    @Step("اختيار الحساب {accountText} للبند الأول")
    public void selectAccountFromFirstRow(String accountText) {
        By option =
                By.xpath(
                        "(//li[contains(@class,'vs__dropdown-option') and contains(normalize-space(),\""
                                + accountText
                                + "\")])[1]");
        utilites.ClickElement(option);
    }

    public boolean hasVoucherMarkerInList(String marker) {
        return driver.findElements(By.xpath("//tr[@role='row' and @data-pk]")).stream()
                .anyMatch(row -> row.getText().contains(marker));
    }

    public boolean hasVoucherMarkerPrefixInList(String markerPrefix) {
        return driver.findElements(By.xpath("//tr[@role='row' and @data-pk]")).stream()
                .anyMatch(row -> row.getText().contains(markerPrefix));
    }

    public List<String> getVoucherRowsContaining(String marker) {
        return driver.findElements(By.xpath("//tr[@role='row' and @data-pk]")).stream()
                .map(WebElement::getText)
                .filter(text -> text.contains(marker))
                .toList();
    }

    @Step("حذف القيود المطابقة للمرجع {markerPrefix}")
    public void deleteVoucherRowsContaining(String markerPrefix) {
        List<WebElement> matches =
                driver.findElements(By.xpath("//tr[@role='row' and @data-pk]")).stream()
                        .filter(row -> row.getText().contains(markerPrefix))
                        .toList();
        for (WebElement match : matches) {
            String pk = match.getAttribute("data-pk");
            utilites.grid()
                    .deleteRowByPk(
                            pk,
                            By.xpath(
                                    "//tr[@data-pk='"
                                            + pk
                                            + "']//button[@data-action-type='delete']"),
                            Clickyestodelet);
            waitMangment
                    .fluentWait()
                    .until(d -> d.findElements(By.xpath("//tr[@data-pk='" + pk + "']")).isEmpty());
        }
    }

    @Step("فتح القيد المحدد للتعديل: {marker}")
    public void editVoucherListRowByMarker(String marker) {
        By editButton =
                By.xpath(
                        "//tr[@role='row' and @aria-rowindex='1' and contains(.,'"
                                + marker
                                + "')]//button[@data-action-type='edit']");
        utilites.ClickElement(editButton);
        waitMangment
                .fluentWait()
                .until(d -> !d.findElements(By.cssSelector("form #textarea")).isEmpty());
        waitMangment
                .fluentWait()
                .until(
                        d ->
                                marker.equals(
                                        d.findElement(By.id("textarea")).getDomProperty("value")));
    }

    @Step("حذف القيد المحدد: {marker}")
    public void deleteVoucherListRowByMarker(String marker) {
        By row =
                By.xpath(
                        "//tr[@role='row' and @aria-rowindex='1' and contains(.,'"
                                + marker
                                + "')]");
        String pk = driver.findElement(row).getAttribute("data-pk");
        utilites.grid()
                .deleteRowByPk(
                        pk,
                        By.xpath("//tr[@data-pk='" + pk + "']//button[@data-action-type='delete']"),
                        Clickyestodelet);
        waitMangment
                .fluentWait()
                .until(d -> d.findElements(By.xpath("//tr[@data-pk='" + pk + "']")).isEmpty());
    }
}
