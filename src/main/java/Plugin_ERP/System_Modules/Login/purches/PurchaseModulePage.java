package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.System_Modules.Login.AbstractForPurchases;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import Plugin_ERP.Utiel.FormInteractions;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Purchase UI interactions; scenarios and persisted inventory assertions live in tests. */
public class PurchaseModulePage extends AbstractForPurchases {
    private final PurchaseDocumentTransitions transitions;
    private final PurchaseDocumentForm form;
    private final PurchasePrintWindows printing;
    private final PurchaseChoiceControls choices;
    private final PurchaseSaveCapture saveCapture;
    protected final WebDriverWait ready;
    private final FormInteractions interactions;
    private final PurchaseItemSelection itemSelection;
    private final PurchaseDocumentList documentList;
    private final PurchaseDocumentLines lines;
    private final PurchaseCalendar calendar;

    public PurchaseModulePage(WebDriver driver) {
        super(driver);
        choices = new PurchaseChoiceControls(this, driver);
        saveCapture = new PurchaseSaveCapture(this);
        calendar = new PurchaseCalendar(driver);
        ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
        interactions = new FormInteractions(ready,
                element -> js("arguments[0].scrollIntoView({block:'center',inline:'center'})", element));
        documentList = new PurchaseDocumentList(this, driver, ready);
        itemSelection = new PurchaseItemSelection(this, driver, ready);
        lines = new PurchaseDocumentLines(this, driver, ready);
        printing = new PurchasePrintWindows(this, driver);
        form = new PurchaseDocumentForm(this, driver, ready);
        transitions = new PurchaseDocumentTransitions(this, driver, ready);
    }

    public void puAwaitSession() {
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(
                        d ->
                                !d.getCurrentUrl().contains("/login")
                                        && Boolean.TRUE.equals(
                                                js(
                                                        "return Boolean(document.querySelector('#app')?.__vue__?.$store?.getters['app/currentBranch']?.id)")));
    }

    protected Object js(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    @Step("فتح شاشة المشتريات: {route}")
    public void puOpen(String route) {
        Plugin_ERP.Utiel.Allur.ScreenNames.opening(route);
        driver.get(PropertyReader.getApplicationUrl() + "/#/" + route);
        driver.navigate().refresh();
        puAwaitSession();
        ready.until(
                d ->
                        d
                                .findElements(
                                        By.cssSelector(
                                                "form button[data-action-type='save'],button[data-action-type='new'],table thead th"))
                                .stream()
                                .anyMatch(WebElement::isDisplayed));
        puDismiss();
    }

    public void puType(By by, String value) {
        puType(by, value, true);
    }

    void puType(By by, String value, boolean blur) {
        interactions.type(by, value, blur, FormInteractions.InputMode.CLICK_REPLACE);
    }

    protected void puClick(By by) {
        interactions.click(by);
    }

    @Step("اختيار {name} من {label}")
    public void puChoose(String label, String name) {
        choices.puChoose(label, name);
    }

    @Step("مسح الاختيار من {label}")
    public void puClearChoice(String label) {
        choices.puClearChoice(label);
    }

    public boolean puChoiceEmpty(String label) {
        return choices.puChoiceEmpty(label);
    }

    public boolean puCanClearChoice(String label) {
        return choices.puCanClearChoice(label);
    }

    public boolean puHasChoice(String label) {
        return choices.puHasChoice(label);
    }

    @Step("إدخال ملاحظات السند: {note}")
    public void puNotes(String note) {
        form.puNotes(note);
    }

    public String puDiagnosticMarkup() {
        return driver.getPageSource();
    }

    public void utilitesReportReady() {
        utilites.waits().waitForReportReady(60);
    }

    @Step("الضغط على زر {text}")
    public void puButton(String text) {
        puClick(By.xpath("//button[normalize-space()='" + text + "']"));
    }

    @Step("حفظ السند؛ طباعة التقرير: {print}")
    public void puSave(boolean print) {
        form.puSave(print);
    }

    public boolean puSaveDisabled() {
        return form.puSaveDisabled();
    }

    public boolean puHasDocumentSaveAction() {
        return form.puHasDocumentSaveAction();
    }

    public boolean puHasDocumentDeleteAction() {
        return form.puHasDocumentDeleteAction();
    }

    public boolean puLineReadOnly(String name, String column) {
        return lines.puLineReadOnly(name, column);
    }

    public void puDismiss() {
        form.puDismiss();
    }

    @Step("التحقق من رسالة الخطأ")
    public String puValidation() {
        return form.puValidation();
    }

    public int puLineCount(String name) {
        return lines.puLineCount(name);
    }

    public Map<String, String> puLineValues(String name) {
        return lines.puLineValues(name);
    }

    private void radio(String value) {
        itemSelection.radio(value);
    }

    public void puWaitOwnedItemLookup(String name) {
        itemSelection.puWaitOwnedItemLookup(name);
    }

    @Step("إضافة الصنف بالاسم: {name}")
    public void puAddName(String name) {
        itemSelection.puAddName(name);
    }

    @Step("إضافة الصنف بالباركود: {barcode}")
    public void puAddBarcode(String barcode, String name) {
        itemSelection.puAddBarcode(barcode, name);
    }

    @Step("إدخال الباركود {barcode} وإضافة الصنف {name}")
    public void puAddBarcodeImmediately(String barcode, String name) {
        itemSelection.puAddBarcodeImmediately(barcode, name);
    }

    public boolean puOwnedItemCached(String name) {
        return itemSelection.puOwnedItemCached(name);
    }

    @Step("إضافة الصنف من قائمة الأصناف: {name}")
    public void puAddPicker(String name) {
        itemSelection.puAddPicker(name);
    }

    @Step("البحث في {field} عن {value} والتحقق من الصنف {expectedName}")
    public void puPickerSearch(String field, String value, String expectedName) {
        itemSelection.puPickerSearch(field, value, expectedName);
    }

    @Step("تصفية الأصناف بالفئة {category} والتحقق من الصنف {expectedName}")
    public void puPickerCategory(String category, String expectedName) {
        itemSelection.puPickerCategory(category, expectedName);
    }

    public List<String> puPickerRows() {
        return itemSelection.puPickerRows();
    }

    @Step("إغلاق نافذة اختيار الصنف")
    public void puClosePicker() {
        itemSelection.puClosePicker();
    }

    @Step("تحديد كمية {name}: {quantity}")
    public void puQuantity(String name, String quantity) {
        lines.puQuantity(name, quantity);
    }

    @Step("حذف الصنف {name} من السند")
    public void puRemoveLine(String name) {
        lines.puRemoveLine(name);
    }

    public void puCapture(String marker, Object storeId, Object itemId, String endpoint) {
        saveCapture.puCapture(marker, storeId, itemId, endpoint);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> puSaves() {
        return saveCapture.puSaves();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> puAttempts() {
        return saveCapture.puAttempts();
    }

    public void puSettled() {
        saveCapture.puSettled();
    }

    public String puSaveErrors() {
        return saveCapture.puSaveErrors();
    }

    public String puDiagnostics() {
        return saveCapture.puDiagnostics();
    }

    public void puWaitSaveOutcome() {
        saveCapture.puWaitSaveOutcome();
    }

    public String puCaptureMarker() {
        return saveCapture.puCaptureMarker();
    }

    @Step("فتح السند للتعديل")
    public void puEdit(String route, Object id, String marker, String itemName) {
        puOpen(route + "/edit/" + id);
        ready.until(
                d ->
                        d.findElements(By.cssSelector("form textarea[label-text='Notes']")).stream()
                                        .anyMatch(e -> marker.equals(e.getDomProperty("value")))
                                && puLineCount(itemName) > 0);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> puDocument() {
        return form.puDocument();
    }

    public void puFormReady() {
        form.puFormReady();
    }

    public void puAwaitOwnedLookups(Object store, Object supplier, Object agent) {
        form.puAwaitOwnedLookups(store, supplier, agent);
    }

    public String puDocumentDate() {
        return form.puDocumentDate();
    }

    @Step("تغيير {column} للصنف {name} إلى {value}")
    public void puSetLine(String name, String column, String value) {
        lines.puSetLine(name, column, value);
    }

    public Map<String, String> puTotals() {
        return lines.puTotals();
    }

    @Step("التحقق من صافي السند المتوقع: {value}")
    public void puWaitNet(String value) {
        lines.puWaitNet(value);
    }

    @Step("إدخال خصم {value}، خصم بنسبة: {percentage}")
    public void puDiscount(String value, boolean percentage) {
        form.puDiscount(value, percentage);
    }

    public java.math.BigDecimal puUserDiscountLimit() {
        return form.puUserDiscountLimit();
    }

    @Step("إضافة طريقة الدفع {method} بمبلغ {amount}")
    public void puAddOtherPayment(String method, String amount) {
        form.puAddOtherPayment(method, amount);
    }

    @Step("فتح نموذج سند جديد")
    public void puNew() {
        form.puNew();
    }

    public String puNotesValue() {
        return form.puNotesValue();
    }

    @Step("إنشاء مرتجع للفاتورة {invoiceId} والصنف {itemName}")
    public void puLinkedReturn(Object invoiceId, String itemName) {
        transitions.puLinkedReturn(invoiceId, itemName);
    }

    @Step("تحويل مستند الاختبار المحدد بالكود: {code}")
    public void puConvertOwned(String supplier, String code, boolean toInvoice, String itemName) {
        transitions.puConvertOwned(supplier, code, toInvoice, itemName);
    }

    public void puConvertOwned(String supplier, String code, Object sourceId, boolean toInvoice, String itemName) {
        transitions.puConvertOwned(supplier, code, sourceId, toInvoice, itemName);
    }

    @Step("حذف مستند الاختبار من القائمة: {code}")
    public void puDeleteOwned(String supplier, String code) {
        transitions.puDeleteOwned(supplier, code);
    }

    @Step("تصفية القائمة بالمتعامل: {customer}")
    public void puListScope(String customer) {
        documentList.puListScope(customer);
    }

    public void puAwaitListReady() {
        documentList.puAwaitListReady();
    }

    @Step("تطبيق فلاتر القائمة")
    public void puApplyListFilters() {
        documentList.puApplyListFilters();
    }

    public String puListFixtureDate() {
        return documentList.puListFixtureDate();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> puListRows() {
        return documentList.puListRows();
    }

    public void puWaitListIds(Set<Object> expected) {
        documentList.puWaitListIds(expected);
    }

    @Step("إدخال فلتر {name}: {text}")
    public void puListText(String name, String text) {
        documentList.puListText(name, text);
    }

    @Step("اختيار {value} من فلتر {group}")
    public void puListRadio(String group, String value) {
        documentList.puListRadio(group, value);
    }

    public List<String> puHeaders() {
        return documentList.puHeaders();
    }

    @Step("عرض {size} سجل في الصفحة")
    public void puPageSize(int size) {
        documentList.puPageSize(size);
    }

    @Step("الانتقال للصفحة التالية")
    public void puNextPage() {
        documentList.puNextPage();
    }

    @Step("العودة للصفحة الأولى")
    public void puFirstPage() {
        documentList.puFirstPage();
    }

    @Step("ترتيب القائمة حسب {label}")
    public void puSort(String label) {
        documentList.puSort(label);
    }

    @Step("تصدير القائمة بصيغة {format}")
    public void puExport(String format) {
        documentList.puExport(format);
    }

    public String puWindowHandle() {
        return printing.puWindowHandle();
    }

    public void puCloseAuxiliaryWindows(String main) {
        printing.puCloseAuxiliaryWindows(main);
    }

    @Step("التحقق من بيانات الصنف {item} في التقرير المطبوع")
    public void puVerifyPrintedReport(String main, String item) {
        printing.puVerifyPrintedReport(main, item);
    }
    public void puVerifyPrintedReport(String main, String item, Runnable contentAssertions) {
        printing.puVerifyPrintedReport(main, item, contentAssertions);
    }

    @Step("اختيار تاريخ فلتر المشتريات من التقويم: {value}")
    public void puDateFilter(String name, String value) {
        puAwaitListReady();
        WebElement hidden = driver.findElement(By.cssSelector("input[name='" + name + "']"));
        if (value.equals(hidden.getDomProperty("value"))) return;
        setDateFilter(hidden, value);
        ready.until(
                d ->
                        value.equals(
                                d.findElement(By.cssSelector("input[name='" + name + "']"))
                                        .getDomProperty("value")));
        puAwaitListReady();
    }

    private void setDateFilter(WebElement hiddenInput, String value) {
        calendar.selectDate(hiddenInput, value);
    }

    FormInteractions formInteractions() { return interactions; }
    void selectPaymentMode(String value) { radio(value); }
}
