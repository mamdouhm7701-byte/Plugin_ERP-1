package Plugin_ERP.System_Modules.Login.StoreModel;

import Plugin_ERP.System_Modules.Login.AbstractForStore;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Warehouse UI interactions; scenarios and persisted inventory assertions live in tests. */
public class StoreModulePage extends AbstractForStore {
    protected final WebDriverWait ready;
    private Object ownedId;
    private String ownedMarker;
    private By listSearch;

    public StoreModulePage(WebDriver driver) {
        super(driver);
        ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
    }

    public void smAwaitSession() {
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

    @Step("فتح شاشة المخازن: {route}")
    public void smOpen(String route) {
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

    private void smType(By by, String value, boolean blur) {
        ready.until(
                d -> {
                    for (WebElement e : d.findElements(by)) {
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", e);
                        if (!e.isDisplayed() || !e.isEnabled()) continue;
                        e.sendKeys(Keys.CONTROL + "a");
                        e.sendKeys(Keys.DELETE);
                        if (!value.isEmpty()) e.sendKeys(value);
                        if (blur) e.sendKeys(Keys.TAB);
                        return true;
                    }
                    return false;
                });
    }

    protected void smClick(By by) {
        ready.until(
                d -> {
                    for (WebElement e : d.findElements(by)) {
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", e);
                        if (e.isDisplayed() && e.isEnabled()) {
                            e.click();
                            return true;
                        }
                    }
                    return false;
                });
    }

    @Step("اختيار {name} من {label}")
    public void smChoose(String label, String name) {
        By by =
                By.xpath(
                        "//*[self::label or self::legend][normalize-space()='"
                                + label
                                + "']/following::input[contains(@class,'vs__search')][1]");
        ready.until(
                d -> {
                    for (WebElement input : d.findElements(by)) {
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", input);
                        if (!input.isDisplayed() || !input.isEnabled()) continue;
                        if (!name.equals(input.getDomProperty("value"))) {
                            input.click();
                            input.sendKeys(Keys.CONTROL + "a");
                            input.sendKeys(name);
                        }
                        String id = input.getDomAttribute("aria-controls");
                        List<WebElement> options =
                                d
                                        .findElements(
                                                By.xpath(
                                                        "//ul[@id='"
                                                                + id
                                                                + "']//li[not(@aria-disabled='true')]"))
                                        .stream()
                                        .filter(WebElement::isDisplayed)
                                        .filter(
                                                e -> {
                                                    String text =
                                                            e.getText()
                                                                    .trim()
                                                                    .replaceAll("\\s+", " ");
                                                    return text.equals(name)
                                                            || text.startsWith(name + " - ")
                                                            || text.endsWith(" - " + name);
                                                })
                                        .toList();
                        if (options.isEmpty()) return false;
                        if (options.size() != 1)
                            throw new AssertionError("Ambiguous owned option: " + name);
                        options.get(0).click();
                        return true;
                    }
                    return false;
                });
    }

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

    public void smNotes(String note) {
        smType(By.cssSelector("form textarea"), note);
    }

    public java.time.LocalDate smCurrentDate() {
        return java.time.LocalDate.parse(
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

    public void smDate(String date) {
        smType(By.cssSelector("form input[placeholder='YYYY-MM-DD']"), date);
    }

    public void smButton(String text) {
        smClick(By.xpath("//button[normalize-space()='" + text + "']"));
    }

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

    private By line(String name) {
        return By.xpath("//form//tbody/tr[.//td[contains(normalize-space(),'" + name + "')]]");
    }

    public int smLineCount(String name) {
        return (int)
                driver.findElements(line(name)).stream().filter(WebElement::isDisplayed).count();
    }

    public Map<String, String> smLineValues(String name) {
        return ready.until(
                d -> {
                    WebElement row =
                            d.findElements(line(name)).stream()
                                    .filter(WebElement::isDisplayed)
                                    .findFirst()
                                    .orElse(null);
                    if (row == null) return null;
                    Map<String, String> values = new LinkedHashMap<>();
                    for (WebElement header :
                            row.findElement(By.xpath("ancestor::table[1]"))
                                    .findElements(By.cssSelector("thead th"))) {
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", header);
                        String label = header.getText().trim();
                        if (label.isBlank()) continue;
                        List<WebElement> cells =
                                row.findElements(
                                        By.cssSelector(
                                                "td[aria-colindex='"
                                                        + header.getDomAttribute("aria-colindex")
                                                        + "']"));
                        if (cells.isEmpty()) continue;
                        WebElement cell = cells.get(0);
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", cell);
                        List<WebElement> inputs =
                                cell.findElements(By.cssSelector("input:not([type='hidden'])"));
                        values.put(
                                label,
                                inputs.isEmpty()
                                        ? cell.getText().trim()
                                        : inputs.get(0).getDomProperty("value"));
                    }
                    return values;
                });
    }

    private void radio(String value) {
        ready.until(
                d -> {
                    for (WebElement input :
                            d.findElements(
                                    By.cssSelector(
                                            "form input[type='radio'][value='" + value + "']"))) {
                        if (!input.isSelected()) {
                            WebElement label = input.findElement(By.xpath("ancestor::label[1]"));
                            js(
                                    "arguments[0].scrollIntoView({block:'center',inline:'center'})",
                                    label);
                            label.click();
                        }
                        return input.isSelected();
                    }
                    return false;
                });
    }

    @Step("إضافة صنف الاختبار بالاسم: {name}")
    public void smAddName(String name) {
        radio("name");
        smType(By.cssSelector("form input#autosuggest__input"), name, false);
        try {
            smClick(
                    By.xpath(
                            "//div[@id='autosuggest-autosuggest__results']//li[contains(normalize-space(),'"
                                    + name
                                    + "')] | //span[contains(@class,'my-suggestion-item') and contains(normalize-space(),'"
                                    + name
                                    + "')]"));
            ready.until(d -> smLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned item did not appear in name autocomplete. "
                            + smItemCacheDiagnostics(name),
                    e);
        }
    }

    @Step("إضافة صنف الاختبار بالباركود: {barcode}")
    public void smAddBarcode(String barcode, String name) {
        radio("barcode");
        smType(
                By.cssSelector("form input[placeholder='بحث ....']:not(#autosuggest__input)"),
                barcode,
                false);
        ready.until(
                d -> {
                    for (WebElement input :
                            d.findElements(
                                    By.cssSelector(
                                            "form input[placeholder='بحث ....']:not(#autosuggest__input)")))
                        if (input.isDisplayed()) {
                            input.sendKeys(Keys.ENTER);
                            return true;
                        }
                    return false;
                });
        try {
            ready.until(d -> smLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned barcode did not add its item. " + smItemCacheDiagnostics(name), e);
        }
    }

    private Object smItemCacheDiagnostics(String name) {
        return js(
                "const raw=sessionStorage.getItem('cachedArray');let rows;try{rows=JSON.parse(raw)}catch(e){return {cacheInvalid:true}}return {cachePresent:raw!==null,cacheCount:Array.isArray(rows)?rows.length:null,ownedItemCached:Array.isArray(rows)&&rows.some(r=>r.arabicName===arguments[0]),lookups:window.__storeQA?.lookups||[]}",
                name);
    }

    @Step("إضافة صنف الاختبار من قائمة الأصناف: {name}")
    public void smAddPicker(String name) {
        smButton("إضافة أصناف");
        smType(By.cssSelector(".modal.show input[label-text='arabicName']"), name);
        smClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
        smClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='حفظ']"));
        ready.until(d -> smLineCount(name) > 0);
    }

    @Step("تحديد كمية {name}: {quantity}")
    public void smQuantity(String name, String quantity) {
        ready.until(
                d -> {
                    WebElement row =
                            d.findElements(line(name)).stream()
                                    .filter(WebElement::isDisplayed)
                                    .findFirst()
                                    .orElse(null);
                    if (row == null) return false;
                    WebElement table = row.findElement(By.xpath("ancestor::table[1]"));
                    String col =
                            table.findElements(By.cssSelector("thead th")).stream()
                                    .filter(e -> e.getText().trim().equals("الكمية"))
                                    .map(e -> e.getDomAttribute("aria-colindex"))
                                    .findFirst()
                                    .orElseThrow(
                                            () -> new AssertionError("Quantity column missing"));
                    List<WebElement> inputs =
                            row.findElements(
                                    By.cssSelector(
                                            "td[aria-colindex='"
                                                    + col
                                                    + "'] input:not([type='hidden'])"));
                    for (WebElement e : inputs) {
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", e);
                        if (!e.isDisplayed() || !e.isEnabled()) continue;
                        e.sendKeys(Keys.CONTROL + "a");
                        e.sendKeys(Keys.DELETE);
                        e.sendKeys(quantity);
                        e.sendKeys(Keys.TAB);
                        return true;
                    }
                    return false;
                });
    }

    public void smRemoveLine(String name) {
        smClick(
                By.xpath(
                        "//form//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-trash')]]"));
        ready.until(d -> smLineCount(name) == 0);
    }

    @Step("تسجيل طلبات حفظ سند الاختبار")
    public void smCapture(String marker, Object storeId, Object itemId) {
        js(
                """
    const store=document.querySelector('#app').__vue__.$store;
    window.__storeQA={marker:arguments[0],storeId:arguments[1],itemId:arguments[2],saves:[],attempts:[],pending:0,errors:[],lookups:[],uiErrors:[]};
    if(!window.__storeQAErrorsInstalled){window.__storeQAErrorsInstalled=true;
      addEventListener('error',e=>window.__storeQA?.uiErrors.push(e.message));
      addEventListener('unhandledrejection',e=>window.__storeQA?.uiErrors.push(String(e.reason?.stack||e.reason)));}
    if(store.__storeQACaptureInstalled)return;store.__storeQACaptureInstalled=true;const dispatch=store.dispatch.bind(store);
    store.dispatch=function(action,payload,...rest){const cap=window.__storeQA;
      const lookup=cap&&action==='app/get'&&/^items[?]/i.test(payload?.url||'')&&String(payload.url).includes('storeId='+cap.storeId);
      const owned=cap&&['app/create','app/update'].includes(action)&&
        ((typeof payload?.data?.notes==='string'&&payload.data.notes.startsWith(cap.marker))||
        (payload?.url==='ManufacturingOrders'&&payload.data?.storeId===cap.storeId&&payload.data.lines?.length>0&&payload.data.lines.every(l=>l.itemId===cap.itemId)));
      const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;if(owned){cap.pending++;cap.attempts.push({endpoint:payload.url,values:sent});}
      return dispatch(action,payload,...rest).then(result=>{if(lookup){const data=result?.data||result;cap.lookups.push({url:payload.url,total:data?.totalRecords,items:data?.pageData?.map(i=>({id:i.id,arabicName:i.arabicName}))});}
        if(owned){cap.pending--;const entity=result?.data||result;
        cap.saves.push({endpoint:payload.url,id:sent.id||entity?.id||(typeof entity==='string'||typeof entity==='number'?entity:null),values:sent});}return result;})
        .catch(error=>{if(lookup)cap.lookups.push({url:payload.url,error:String(error),status:error?.response?.status});if(owned){cap.pending--;cap.errors.push(String(error));}throw error;});};
    """,
                marker,
                storeId,
                itemId);
    }

    public String smCaptureMarker() {
        return (String) js("return window.__storeQA?.marker||null");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> smSaves() {
        return (List<Map<String, Object>>) js("return window.__storeQA?.saves||[]");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> smAttempts() {
        return (List<Map<String, Object>>) js("return window.__storeQA?.attempts||[]");
    }

    public void smSettled() {
        ready.until(d -> !Boolean.TRUE.equals(js("return (window.__storeQA?.pending||0)>0")));
    }

    public String smSaveErrors() {
        return String.valueOf(js("return window.__storeQA?.errors||[]"));
    }

    public String smDiagnostics() {
        return String.valueOf(
                js(
                        "return JSON.stringify({lookups:window.__storeQA?.lookups||[],errors:window.__storeQA?.uiErrors||[]})"));
    }

    @Step("فتح سند الاختبار للتعديل")
    public void smEdit(String route, Object id, String marker, String itemName) {
        smOpen(route + "/edit/" + id);
        ready.until(
                d ->
                        route.equals("manufacturing-order")
                                ? smLineCount(itemName) > 0
                                : d.findElements(By.cssSelector("form textarea")).stream()
                                        .anyMatch(e -> marker.equals(e.getDomProperty("value"))));
    }

    @Step("البحث عن سند الاختبار فقط: {marker}")
    public void smFind(String route, String marker, Object id, Object code) {
        if (!marker.startsWith("QA-ST-") || id == null)
            throw new AssertionError("Refuse unrelated warehouse row");
        ownedId = id;
        ownedMarker = marker;
        smOpen(route);
        By search =
                By.cssSelector(
                        route.equals("damage-adjustment")
                                ? "input[placeholder*='بحث']"
                                : "input#code,input[name='fCode']");
        listSearch = search;
        ready.until(
                d -> {
                    if (d.findElements(search).stream().anyMatch(WebElement::isDisplayed))
                        return true;
                    for (WebElement b :
                            d.findElements(
                                    By.xpath(
                                            "//button[contains(normalize-space(),'عرض التصفية')]")))
                        if (b.isDisplayed()) {
                            b.click();
                            break;
                        }
                    return false;
                });
        smType(search, route.equals("damage-adjustment") ? marker : code.toString());
        ready.until(d -> smHasDocument(marker));
    }

    public void smSearch(String value) {
        smType(listSearch, value);
    }

    private By ownedRow() {
        return By.cssSelector(
                "tbody tr[data-pk='"
                        + ownedId
                        + "'],tbody tr[id$='__row_"
                        + ownedId
                        + "'],tbody tr:has([id*='invoice-row-"
                        + ownedId
                        + "-'])");
    }

    public boolean smHasDocument(String marker) {
        return marker.equals(ownedMarker)
                && driver.findElements(ownedRow()).stream().anyMatch(WebElement::isDisplayed);
    }

    @Step("حذف سند الاختبار فقط: {marker}")
    public void smDeleteOwn(String marker, boolean confirm) {
        if (!marker.startsWith("QA-ST-") || !marker.equals(ownedMarker) || ownedId == null)
            throw new AssertionError("Refuse unrelated document deletion");
        ready.until(
                d -> {
                    for (WebElement row : d.findElements(ownedRow()))
                        for (WebElement b :
                                row.findElements(
                                        By.cssSelector(
                                                "[data-action-type='delete'],[id*='-delete-']"))) {
                            js("arguments[0].scrollIntoView({block:'center',inline:'center'})", b);
                            if (b.isDisplayed() && b.isEnabled()) {
                                b.click();
                                return true;
                            }
                        }
                    return false;
                });
        smClick(By.cssSelector(confirm ? "button.swal2-confirm" : "button.swal2-cancel"));
        ready.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".swal2-container")));
    }

    public List<String> smHeaders() {
        return driver.findElements(By.cssSelector("thead th")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .toList();
    }

    public void smExport(String format) {
        smClick(
                By.cssSelector(
                        "button:has(img[src*='"
                                + (format.equals("pdf") ? "pdf." : "xls.")
                                + "'])"));
    }
}
