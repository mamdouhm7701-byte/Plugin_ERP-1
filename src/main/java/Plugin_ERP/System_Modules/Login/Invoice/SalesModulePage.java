package Plugin_ERP.System_Modules.Login.Invoice;

import Plugin_ERP.System_Modules.Login.AbstractForSales;
import Plugin_ERP.Util.FormInteractions;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Sales UI interactions; scenarios and persisted inventory assertions live in tests. */
public class SalesModulePage extends AbstractForSales {
    private final SalesNavigation navigation;
    private final SalesDocumentForm form;
    private final SalesPrintWindows printing;
    private final SalesChoiceControls choices;
    private final SalesSaveCapture saveCapture;
    protected final WebDriverWait ready;
    private final FormInteractions interactions;
    private final SalesItemSelection itemSelection;
    private final SalesDocumentList documentList;
    private final SalesDocumentLines lines;

    public SalesModulePage(WebDriver driver) {
        super(driver);
        choices = new SalesChoiceControls(this, driver);
        saveCapture = new SalesSaveCapture(this);
        ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
        interactions =
                new FormInteractions(
                        ready,
                        element ->
                                js(
                                        "arguments[0].scrollIntoView({block:'center',inline:'center'})",
                                        element));
        documentList = new SalesDocumentList(this, driver, ready);
        itemSelection = new SalesItemSelection(this, driver, ready);
        lines = new SalesDocumentLines(this, driver, ready);
        printing = new SalesPrintWindows(this, driver);
        form = new SalesDocumentForm(this, driver, ready);
        navigation = new SalesNavigation(this, driver, ready);
    }

    public void slAwaitSession() {
        navigation.slAwaitSession();
    }

    protected Object js(String script, Object... args) {
        return Plugin_ERP.Util.VueComponentAccess.execute(driver, script, args);
    }

    protected static String navigationPath(String route) {
        return SalesNavigation.navigationPath(route);
    }

    protected static boolean reusesFormComponent(String current, String destination) {
        return SalesNavigation.reusesFormComponent(current, destination);
    }

    @Step("فتح شاشة المبيعات: {route}")
    public void slOpen(String route) {
        navigation.slOpen(route);
    }

    public void slType(By by, String value) {
        slType(by, value, true);
    }

    void slType(By by, String value, boolean blur) {
        interactions.type(by, value, blur, FormInteractions.InputMode.CLICK_REPLACE);
    }

    protected void slClick(By by) {
        interactions.click(by);
    }

    @Step("اختيار {name} من {label}")
    public void slChoose(String label, String name) {
        choices.slChoose(label, name);
    }

    @Step("مسح الاختيار من {label}")
    public void slClearChoice(String label) {
        choices.slClearChoice(label);
    }

    public boolean slChoiceEmpty(String label) {
        return choices.slChoiceEmpty(label);
    }

    public boolean slCanClearChoice(String label) {
        return choices.slCanClearChoice(label);
    }

    public boolean slHasChoice(String label) {
        return choices.slHasChoice(label);
    }

    @Step("إدخال ملاحظات السند: {note}")
    public void slNotes(String note) {
        form.slNotes(note);
    }

    public String slDiagnosticMarkup() {
        return driver.getPageSource();
    }

    public void utilitesReportReady() {
        utilites.waits().waitForReportReady(60);
    }

    @Step("الضغط على زر {text}")
    public void slButton(String text) {
        slClick(By.xpath("//button[normalize-space()='" + text + "']"));
    }

    @Step("حفظ السند؛ طباعة التقرير: {print}")
    public void slSave(boolean print) {
        form.slSave(print);
    }

    public boolean slSaveDisabled() {
        return form.slSaveDisabled();
    }

    public boolean slHasDocumentSaveAction() {
        return form.slHasDocumentSaveAction();
    }

    public boolean slHasDocumentDeleteAction() {
        return form.slHasDocumentDeleteAction();
    }

    public boolean slLineReadOnly(String name, String column) {
        return lines.slLineReadOnly(name, column);
    }

    public void slDismiss() {
        form.slDismiss();
    }

    @Step("التحقق من رسالة الخطأ")
    public String slValidation() {
        return form.slValidation();
    }

    public int slLineCount(String name) {
        return lines.slLineCount(name);
    }

    public Map<String, String> slLineValues(String name) {
        return lines.slLineValues(name);
    }

    private void radio(String value) {
        itemSelection.radio(value);
    }

    public void slWaitOwnedItemLookup(String name) {
        itemSelection.slWaitOwnedItemLookup(name);
    }

    @Step("إضافة الصنف بالاسم: {name}")
    public void slAddName(String name) {
        itemSelection.slAddName(name);
    }

    @Step("إضافة الصنف بالباركود: {barcode}")
    public void slAddBarcode(String barcode, String name) {
        itemSelection.slAddBarcode(barcode, name);
    }

    @Step("إدخال الباركود {barcode} وإضافة الصنف {name}")
    public void slAddBarcodeImmediately(String barcode, String name) {
        itemSelection.slAddBarcodeImmediately(barcode, name);
    }

    public boolean slOwnedItemCached(String name) {
        return itemSelection.slOwnedItemCached(name);
    }

    @Step("إضافة صنفين موجودين إلى المسودة دون حفظ المستند")
    public List<String> slAddFirstTwoExistingItems() {
        return itemSelection.slAddFirstTwoExistingItems();
    }

    @Step("إضافة الصنف من قائمة الأصناف: {name}")
    public void slAddPicker(String name) {
        itemSelection.slAddPicker(name);
    }

    @Step("البحث في {field} عن {value} والتحقق من الصنف {expectedName}")
    public void slPickerSearch(String field, String value, String expectedName) {
        itemSelection.slPickerSearch(field, value, expectedName);
    }

    @Step("تصفية الأصناف بالفئة {category} والتحقق من الصنف {expectedName}")
    public void slPickerCategory(String category, String expectedName) {
        itemSelection.slPickerCategory(category, expectedName);
    }

    public List<String> slPickerRows() {
        return itemSelection.slPickerRows();
    }

    @Step("إغلاق نافذة اختيار الصنف")
    public void slClosePicker() {
        itemSelection.slClosePicker();
    }

    @Step("تحديد كمية {name}: {quantity}")
    public void slQuantity(String name, String quantity) {
        lines.slQuantity(name, quantity);
    }

    @Step("حذف الصنف {name} من السند")
    public void slRemoveLine(String name) {
        lines.slRemoveLine(name);
    }

    public void slCapture(String marker, Object storeId, Object itemId, String endpoint) {
        saveCapture.slCapture(marker, storeId, itemId, endpoint);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> slSaves() {
        return saveCapture.slSaves();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> slAttempts() {
        return saveCapture.slAttempts();
    }

    public void slSettled() {
        saveCapture.slSettled();
    }

    public String slSaveErrors() {
        return saveCapture.slSaveErrors();
    }

    public String slDiagnostics() {
        return saveCapture.slDiagnostics();
    }

    public String slCaptureMarker() {
        return saveCapture.slCaptureMarker();
    }

    @Step("فتح السند للتعديل")
    public void slEdit(String route, Object id, String marker, String itemName) {
        slOpen(route + "/edit/" + id);
        ready.until(
                d ->
                        d.findElements(By.cssSelector("form textarea[label-text='Notes']")).stream()
                                        .anyMatch(e -> marker.equals(e.getDomProperty("value")))
                                && slLineCount(itemName) > 0);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> slDocument() {
        return form.slDocument();
    }

    public void slFormReady() {
        form.slFormReady();
    }

    public String slDocumentDate() {
        return form.slDocumentDate();
    }

    @Step("تغيير {column} للصنف {name} إلى {value}")
    public void slSetLine(String name, String column, String value) {
        lines.slSetLine(name, column, value);
    }

    public Map<String, String> slTotals() {
        return lines.slTotals();
    }

    @Step("التحقق من صافي السند المتوقع: {value}")
    public void slWaitNet(String value) {
        lines.slWaitNet(value);
    }

    @Step("إدخال خصم {value}، خصم بنسبة: {percentage}")
    public void slDiscount(String value, boolean percentage) {
        form.slDiscount(value, percentage);
    }

    @Step("إضافة طريقة الدفع {method} بمبلغ {amount}")
    public void slAddOtherPayment(String method, String amount) {
        form.slAddOtherPayment(method, amount);
    }

    @Step("فتح نموذج سند جديد")
    public void slNew() {
        form.slNew();
    }

    public String slNotesValue() {
        return form.slNotesValue();
    }

    @Step("إنشاء مرتجع للفاتورة {invoiceId} والصنف {itemName}")
    public void slLinkedReturn(Object invoiceId, String itemName) {
        slOpen(
                "return-sales/new?sourceTransactionId="
                        + invoiceId
                        + "&sourceTransactionType=Sales");
        slFormReady();
        ready.until(d -> slLineCount(itemName) > 0);
    }

    @Step("تصفية القائمة بالمتعامل: {customer}")
    public void slListScope(String customer) {
        documentList.slListScope(customer);
    }

    public void slAwaitListReady() {
        documentList.slAwaitListReady();
    }

    @Step("تطبيق فلاتر القائمة")
    public void slApplyListFilters() {
        documentList.slApplyListFilters();
    }

    public String slListFixtureDate() {
        return documentList.slListFixtureDate();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> slListRows() {
        return documentList.slListRows();
    }

    public void slWaitListIds(Set<Object> expected) {
        documentList.slWaitListIds(expected);
    }

    @Step("إدخال فلتر {name}: {text}")
    public void slListText(String name, String text) {
        documentList.slListText(name, text);
    }

    @Step("اختيار {value} من فلتر {group}")
    public void slListRadio(String group, String value) {
        documentList.slListRadio(group, value);
    }

    public List<String> slHeaders() {
        return documentList.slHeaders();
    }

    @Step("تصدير القائمة بصيغة {format}")
    public void slExport(String format) {
        documentList.slExport(format);
    }

    public String slWindowHandle() {
        return printing.slWindowHandle();
    }

    public void slCloseAuxiliaryWindows(String main) {
        printing.slCloseAuxiliaryWindows(main);
    }

    @Step("التحقق من بيانات الصنف {item} في التقرير المطبوع")
    public void slVerifyPrintedReport(String main, String item) {
        printing.slVerifyPrintedReport(main, item);
    }

    public void slVerifyPrintedReport(String main, String item, Runnable contentAssertions) {
        printing.slVerifyPrintedReport(main, item, contentAssertions);
    }

    FormInteractions formInteractions() {
        return interactions;
    }

    void selectPaymentMode(String value) {
        radio(value);
    }
}
