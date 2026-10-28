/* شرح الملف: مساعدة مشتركة لحركات المخزون، تشمل التاريخ والمخزن والأصناف والكميات والحفظ والقائمة.
 * فائدتها تنظيم تسجيل الإضافة والصرف والتحويل والتصنيع والتالف.
 */
package Plugin_ERP.System_Modules.Login.StoreModel;

import Plugin_ERP.System_Modules.Login.AbstractForStore;
import Plugin_ERP.Util.FormInteractions;
import Plugin_ERP.Util.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public class StoreModulePage extends AbstractForStore {
    private final StoreRecordList records;
    private final StoreSaveCapture saveCapture;
    private final StoreItemSelection itemSelection;
    protected final WebDriverWait ready;
    private final FormInteractions interactions;
    private final StoreDocumentLines lines;

    public StoreModulePage(WebDriver driver) {
        super(driver);
        saveCapture = new StoreSaveCapture(this);
        itemSelection = new StoreItemSelection(this);
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
        lines = new StoreDocumentLines(this, driver, ready);
        records = new StoreRecordList(this, driver, ready);
    }

    public void smAwaitSession() {
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(
                        d ->
                                !d.getCurrentUrl().contains("/login")
                                        && Boolean.TRUE.equals(
                                                js(
                                                        "return Boolean(erpVue.root()?.$store?.getters['app/currentBranch']?.id)")));
    }

    protected Object js(String script, Object... args) {
        return Plugin_ERP.Util.VueComponentAccess.execute(driver, script, args);
    }

    @Step("فتح شاشة المخازن: {route}")
    public void smOpen(String route) {
        Plugin_ERP.Util.Allur.ScreenNames.opening(route);
        driver.get(PropertyReader.getApplicationUrl() + "/#/" + route);
        driver.navigate().refresh();
        ready.until(
                d ->
                        d
                                .findElements(
                                        By.cssSelector(
                                                "form button[data-action-type='save'],button[data-action-type='new'],table thead th"))
                                .stream()
                                .anyMatch(WebElement::isDisplayed));
        smDismiss();
    }

    public void smType(By by, String value) {
        smType(by, value, true);
    }

    void smType(By by, String value, boolean blur) {
        interactions.type(by, value, blur, FormInteractions.InputMode.CLEAR_REPLACE);
    }

    protected void smClick(By by) {
        interactions.click(by);
    }

    @Step("اختيار {name} من {label}")
    public void smChoose(String label, String name) {
        By by =
                By.xpath(
                        "//*[self::label or self::legend][normalize-space()='"
                                + label
                                + "']/following::input[contains(@class,'vs__search')][1]");
        interactions.choose(by, name, FormInteractions.OptionMatch.NAME_AT_EDGE);
    }

    @Step("مسح الاختيار من {label}")
    public void smClearChoice(String label) {
        ready.until(
                d -> {
                    for (WebElement dropdown :
                            d.findElements(
                                    By.xpath(
                                            "//*[self::label or self::legend][normalize-space()='"
                                                    + label
                                                    + "']/following::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]"))) {
                        if (dropdown.findElements(By.cssSelector(".vs__selected")).isEmpty())
                            return true;
                        for (WebElement button :
                                dropdown.findElements(By.cssSelector("button.vs__clear")))
                            if (button.isDisplayed() && button.isEnabled()) {
                                button.click();
                                return true;
                            }
                    }
                    return false;
                });
        ready.until(d -> smChoiceEmpty(label));
    }

    public boolean smChoiceEmpty(String label) {
        List<WebElement> dropdowns =
                driver.findElements(
                        By.xpath(
                                "//*[self::label or self::legend][normalize-space()='"
                                        + label
                                        + "']/following::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]"));
        return !dropdowns.isEmpty()
                && dropdowns.get(0).findElements(By.cssSelector(".vs__selected")).isEmpty();
    }

    @Step("إدخال ملاحظات السند: {note}")
    public void smNotes(String note) {
        smType(By.cssSelector("form textarea"), note);
    }

    public java.time.LocalDate smCurrentDate() {
        return Plugin_ERP.Util.DocumentDates.parse(
                driver.findElement(By.cssSelector("form input[placeholder='YYYY-MM-DD']"))
                        .getDomProperty("value")
                        .replace('/', '-'));
    }

    public String smDiagnosticMarkup() {
        return driver.getPageSource();
    }

    public void utilitesReportReady() {
        utilites.waits().waitForReportReady(60);
    }

    @Step("إدخال تاريخ السند: {date}")
    public void smDate(String date) {
        smType(By.cssSelector("form input[placeholder='YYYY-MM-DD']"), date);
    }

    @Step("الضغط على زر {text}")
    public void smButton(String text) {
        smClick(By.xpath("//button[normalize-space()='" + text + "']"));
    }

    @Step("حفظ السند؛ طباعة التقرير: {print}")
    public void smSave(boolean print) {
        smClick(
                By.cssSelector(
                        "form button[data-action-type='"
                                + (print ? "saveAndPrint" : "save")
                                + "']"));
    }

    public boolean smSaveDisabled() {
        return driver.findElements(By.cssSelector("form button[data-action-type='save']")).stream()
                .anyMatch(e -> !e.isEnabled());
    }

    public void smDismiss() {
        ready.until(
                d -> {
                    if (d.findElements(By.cssSelector(".swal2-container")).stream()
                            .noneMatch(WebElement::isDisplayed)) return true;
                    for (WebElement e : d.findElements(By.cssSelector("button.swal2-confirm")))
                        if (e.isDisplayed()) e.click();
                    return false;
                });
    }

    @Step("التحقق من رسالة الخطأ")
    public String smValidation() {
        return ready.until(
                d -> {
                    List<String> out = new ArrayList<>();
                    for (WebElement input :
                            d.findElements(
                                    By.cssSelector(
                                            "form input.is-invalid,form input[aria-invalid='true']"))) {
                        if (!input.isDisplayed()) continue;
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", input);
                        new org.openqa.selenium.interactions.Actions(d)
                                .moveToElement(input)
                                .perform();
                        Object field =
                                js(
                                        """
                            const e=arguments[0],cell=e.closest('td'),table=e.closest('table');
                            const col=cell?.getAttribute('aria-colindex');
                            return (col&&table?.querySelector('th[aria-colindex="'+col+'"]')?.innerText)
                                ||e.getAttribute('placeholder')||e.name||'input';
                            """,
                                        input);
                        for (WebElement tip : d.findElements(By.cssSelector(".tooltip-inner")))
                            if (tip.isDisplayed() && !tip.getText().isBlank())
                                out.add(field + ": " + tip.getText());
                    }
                    for (WebElement input : d.findElements(By.cssSelector("form input:invalid"))) {
                        if (!input.isDisplayed()) continue;
                        String nativeMessage = input.getDomProperty("validationMessage");
                        if (nativeMessage != null && !nativeMessage.isBlank()) {
                            Object label =
                                    js(
                                            """
                                const e=arguments[0],cell=e.closest('td'),table=e.closest('table');
                                const col=cell?.getAttribute('aria-colindex');
                                const header=col&&table?.querySelector('th[aria-colindex="'+col+'"]');
                                return header?.innerText||e.getAttribute('placeholder')||e.name||'input';
                                """,
                                            input);
                            out.add(label + ": " + nativeMessage);
                        }
                    }
                    for (WebElement e :
                            d.findElements(
                                    By.cssSelector(
                                            "small.text-danger,.invalid-feedback,.swal2-title,.swal2-html-container"))) {
                        if (e.getDomProperty("textContent").isBlank()) continue;
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", e);
                        if (e.isDisplayed() && !e.getText().isBlank()) out.add(e.getText());
                    }
                    return out.isEmpty() ? null : String.join(" | ", new LinkedHashSet<>(out));
                });
    }

    public int smLineCount(String name) {
        return lines.smLineCount(name);
    }

    public Map<String, String> smAwaitInvalidQuantity(String name) {
        return lines.smAwaitInvalidQuantity(name);
    }

    public Map<String, String> smLineValues(String name) {
        return lines.smLineValues(name);
    }

    @Step("إضافة الصنف بالاسم: {name}")
    public void smAddName(String name) {
        itemSelection.smAddName(name);
    }

    @Step("إضافة الصنف بالباركود: {barcode}")
    public void smAddBarcode(String barcode, String name) {
        itemSelection.smAddBarcode(barcode, name);
    }

    @Step("إضافة الصنف من قائمة الأصناف: {name}")
    public void smAddPicker(String name) {
        itemSelection.smAddPicker(name);
    }

    @Step("تحديد كمية {name}: {quantity}")
    public void smQuantity(String name, String quantity) {
        lines.smQuantity(name, quantity);
    }

    @Step("حذف الصنف {name} من السند")
    public void smRemoveLine(String name) {
        lines.smRemoveLine(name);
    }

    public void smCapture(String marker, Object storeId, Object itemId) {
        saveCapture.smCapture(marker, storeId, itemId);
    }

    public String smCaptureMarker() {
        return saveCapture.smCaptureMarker();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> smSaves() {
        return saveCapture.smSaves();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> smAttempts() {
        return saveCapture.smAttempts();
    }

    public void smSettled() {
        saveCapture.smSettled();
    }

    public String smSaveErrors() {
        return saveCapture.smSaveErrors();
    }

    public String smDiagnostics() {
        return saveCapture.smDiagnostics();
    }

    @Step("فتح السند للتعديل")
    public void smEdit(String route, Object id, String marker, String itemName) {
        smOpen(route + "/edit/" + id);
        ready.until(
                d ->
                        route.equals("manufacturing-order")
                                ? smLineCount(itemName) > 0
                                : d.findElements(By.cssSelector("form textarea")).stream()
                                        .anyMatch(e -> marker.equals(e.getDomProperty("value"))));
    }

    @Step("البحث عن السند فقط: {marker}")
    public void smFind(String route, String marker, Object id, Object code) {
        records.smFind(route, marker, id, code);
    }

    @Step("البحث عن السند: {value}")
    public void smSearch(String value) {
        records.smSearch(value);
    }

    public boolean smHasListSearch() {
        return records.smHasSearch();
    }

    public boolean smHasDocument(String marker) {
        return records.smHasDocument(marker);
    }

    @Step("حذف السند فقط: {marker}")
    public void smDeleteOwn(String marker, boolean confirm) {
        records.smDeleteOwn(marker, confirm);
    }

    public List<String> smHeaders() {
        return records.smHeaders();
    }

    @Step("تصدير القائمة بصيغة {format}")
    public void smExport(String format) {
        records.smExport(format);
    }
}
