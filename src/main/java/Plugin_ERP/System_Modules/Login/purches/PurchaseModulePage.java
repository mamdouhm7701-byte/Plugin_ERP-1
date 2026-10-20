package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.System_Modules.Login.AbstractForPurchases;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Purchase UI interactions; scenarios and persisted inventory assertions live in tests. */
public class PurchaseModulePage extends AbstractForPurchases {
    protected final WebDriverWait ready;

    public PurchaseModulePage(WebDriver driver) {
        super(driver);
        ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
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

    private void puType(By by, String value, boolean blur) {
        ready.until(
                d -> {
                    for (WebElement e : d.findElements(by)) {
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", e);
                        if (!e.isDisplayed() || !e.isEnabled()) continue;
                        e.click();
                        e.getDomProperty("value");
                        if (value.isEmpty()) e.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
                        else e.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
                        if (blur) e.sendKeys(Keys.TAB);
                        return true;
                    }
                    return false;
                });
    }

    protected void puClick(By by) {
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
    public void puChoose(String label, String name) {
        By by =
                By.xpath(
                        "//*[self::label or self::legend][(normalize-space()='"
                                + label
                                + "' or normalize-space(text())='"
                                + label
                                + "')]/following::input[contains(@class,'vs__search')][1]");
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
                                                            || Arrays.asList(text.split(" - "))
                                                                    .contains(name);
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

    public void puClearChoice(String label) {
        ready.until(
                d -> {
                    for (WebElement dropdown :
                            d.findElements(
                                    By.xpath(
                                            "//*[self::label or self::legend][(normalize-space()='"
                                                    + label
                                                    + "' or normalize-space(text())='"
                                                    + label
                                                    + "')]/following::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]"))) {
                        js(
                                "arguments[0].scrollIntoView({block:'center',inline:'center'})",
                                dropdown);
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
        ready.until(d -> puChoiceEmpty(label));
    }

    public boolean puChoiceEmpty(String label) {
        List<WebElement> dropdowns =
                driver.findElements(
                        By.xpath(
                                "//*[self::label or self::legend][(normalize-space()='"
                                        + label
                                        + "' or normalize-space(text())='"
                                        + label
                                        + "')]/following::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]"));
        return !dropdowns.isEmpty()
                && dropdowns.get(0).findElements(By.cssSelector(".vs__selected")).isEmpty();
    }

    public boolean puCanClearChoice(String label) {
        return driver
                .findElements(
                        By.xpath(
                                "//*[self::label or self::legend][(normalize-space()='"
                                        + label
                                        + "' or normalize-space(text())='"
                                        + label
                                        + "')]/following::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]//button[contains(@class,'vs__clear')]"))
                .stream()
                .anyMatch(e -> e.isDisplayed() && e.isEnabled());
    }

    public boolean puHasChoice(String label) {
        return driver
                .findElements(
                        By.xpath(
                                "//*[self::label or self::legend][(normalize-space()='"
                                        + label
                                        + "' or normalize-space(text())='"
                                        + label
                                        + "')]"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public void puNotes(String note) {
        puType(By.cssSelector("form textarea[label-text='Notes']"), note);
    }

    public String puDiagnosticMarkup() {
        return driver.getPageSource();
    }

    public void utilitesReportReady() {
        utilites.waits().waitForReportReady(60);
    }

    public void puButton(String text) {
        puClick(By.xpath("//button[normalize-space()='" + text + "']"));
    }

    public void puSave(boolean print) {
        puClick(
                print
                        ? By.cssSelector("button[data-action-type='saveAndPrint']")
                        : By.xpath(
                                "//button[@data-action-type='save' and normalize-space()='حفظ']"));
    }

    public boolean puSaveDisabled() {
        return driver
                .findElements(
                        By.xpath("//button[@data-action-type='save' and normalize-space()='حفظ']"))
                .stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(e -> !e.isEnabled());
    }

    public boolean puHasDocumentSaveAction() {
        return driver
                .findElements(
                        By.xpath(
                                "//button[(@data-action-type='save' and normalize-space()='حفظ') or @data-action-type='saveAndPrint']"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public boolean puHasDocumentDeleteAction() {
        return driver
                .findElements(
                        By.xpath(
                                "//button[not(ancestor::tbody) and (@data-action-type='delete' or normalize-space()='حذف' or @title='حذف')] | //a[@role='menuitem' and normalize-space()='حذف']"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public boolean puLineReadOnly(String name, String column) {
        WebElement row =
                ready.until(
                        d ->
                                d.findElements(line(name)).stream()
                                        .filter(WebElement::isDisplayed)
                                        .findFirst()
                                        .orElse(null));
        String index =
                row
                        .findElement(By.xpath("ancestor::table[1]"))
                        .findElements(By.cssSelector("thead th"))
                        .stream()
                        .filter(e -> column.equals(e.getDomProperty("textContent").trim()))
                        .map(e -> e.getDomAttribute("aria-colindex"))
                        .findFirst()
                        .orElseThrow();
        var inputs =
                row.findElements(
                        By.cssSelector(
                                "td[aria-colindex='" + index + "'] input:not([type='hidden'])"));
        return !inputs.isEmpty()
                && inputs.stream()
                        .allMatch(e -> !e.isEnabled() || e.getDomAttribute("readonly") != null);
    }

    public void puDismiss() {
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
    public String puValidation() {
        return ready.until(
                d -> {
                    List<String> out = new ArrayList<>();
                    for (WebElement e :
                            d.findElements(
                                    By.cssSelector(
                                            ".text-danger,.invalid-feedback,.swal2-title,.swal2-html-container,.b-toast-danger .toast-body,.v-toast__item--error .v-toast__text"))) {
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

    public int puLineCount(String name) {
        return (int)
                driver.findElements(line(name)).stream().filter(WebElement::isDisplayed).count();
    }

    public Map<String, String> puLineValues(String name) {
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

    public void puWaitOwnedItemLookup(String name) {
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                js(
                                        "try{return (window.__purchaseQA?.lookups||[]).some(l=>(l.items||[]).some(i=>i.arabicName===arguments[0]))||performance.getEntriesByType('resource').some(r=>{try{const u=new URL(r.name);return /\\/items$/i.test(u.pathname)&&u.searchParams.get('storeId')===String(window.__purchaseQA?.storeId)&&!u.searchParams.has('barcode')}catch(e){return false}})}catch(e){return false}",
                                        name)));
    }

    @Step("إضافة صنف الاختبار بالاسم: {name}")
    public void puAddName(String name) {
        radio("name");
        puType(By.cssSelector("form input#autosuggest__input"), name, false);
        try {
            puClick(
                    By.xpath(
                            "//div[@id='autosuggest-autosuggest__results']//li[contains(normalize-space(),'"
                                    + name
                                    + "')] | //span[contains(@class,'my-suggestion-item') and contains(normalize-space(),'"
                                    + name
                                    + "')]"));
            ready.until(d -> puLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned item did not appear in name autocomplete. "
                            + puItemCacheDiagnostics(name),
                    e);
        }
    }

    @Step("إضافة صنف الاختبار بالباركود: {barcode}")
    public void puAddBarcode(String barcode, String name) {
        puAddBarcodeImmediately(barcode, name);
    }

    public void puAddBarcodeImmediately(String barcode, String name) {
        radio("barcode");
        puType(
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
            ready.until(d -> puLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned barcode did not add its item. " + puItemCacheDiagnostics(name), e);
        }
    }

    public boolean puOwnedItemCached(String name) {
        return Boolean.TRUE.equals(
                js(
                        "try{return JSON.parse(sessionStorage.getItem('cachedArray')||'[]').some(i=>(i.arabicName||i.itemArabicName)===arguments[0])}catch(e){return false}",
                        name));
    }

    private Object puItemCacheDiagnostics(String name) {
        return js(
                "const raw=sessionStorage.getItem('cachedArray');let rows;try{rows=JSON.parse(raw)}catch(e){return {cacheInvalid:true}}return {cachePresent:raw!==null,cacheCount:Array.isArray(rows)?rows.length:null,ownedItemCached:Array.isArray(rows)&&rows.some(r=>r.arabicName===arguments[0]),lookups:window.__purchaseQA?.lookups||[]}",
                name);
    }

    @Step("إضافة صنف الاختبار من قائمة الأصناف: {name}")
    public void puAddPicker(String name) {
        puButton("إضافة أصناف");
        puType(By.cssSelector(".modal.show input[label-text='arabicName']"), name);
        puClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
        puClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='حفظ']"));
        ready.until(
                d ->
                        puLineCount(name) > 0
                                && d.findElements(By.cssSelector(".modal.show")).stream()
                                        .noneMatch(WebElement::isDisplayed));
    }

    public void puPickerSearch(String field, String value, String expectedName) {
        puButton("إضافة أصناف");
        puType(By.cssSelector(".modal.show input[name='" + field + "']"), value);
        ready.until(d -> puPickerRows().stream().anyMatch(row -> row.contains(expectedName)));
    }

    public void puPickerCategory(String category, String expectedName) {
        puButton("إضافة أصناف");
        puChoose("الفئة", category);
        ready.until(
                d -> {
                    var rows = puPickerRows();
                    return !rows.isEmpty()
                            && rows.stream().allMatch(row -> row.contains(expectedName));
                });
    }

    public List<String> puPickerRows() {
        return driver.findElements(By.cssSelector(".modal.show tbody tr")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .filter(t -> !t.isBlank() && !t.contains("لا يوجد بيانات"))
                .toList();
    }

    public void puClosePicker() {
        puClick(By.cssSelector(".modal.show button.close"));
        ready.until(
                d ->
                        d.findElements(By.cssSelector(".modal.show")).stream()
                                .noneMatch(WebElement::isDisplayed));
    }

    @Step("تحديد كمية {name}: {quantity}")
    public void puQuantity(String name, String quantity) {
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
                                    .filter(
                                            e ->
                                                    e.getDomProperty("textContent")
                                                            .trim()
                                                            .equals("الكمية"))
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
                        e.click();
                        e.getDomProperty("value");
                        e.sendKeys(Keys.chord(Keys.CONTROL, "a"), quantity, Keys.TAB);
                        return true;
                    }
                    return false;
                });
    }

    public void puRemoveLine(String name) {
        puClick(
                By.xpath(
                        "//form//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-trash')]]"));
        ready.until(d -> puLineCount(name) == 0);
    }

    @Step("تسجيل طلبات حفظ سند الاختبار")
    public void puCapture(String marker, Object storeId, Object itemId, String endpoint) {
        js(
                """
    const store=document.querySelector('#app').__vue__.$store;
    window.__purchaseQA={marker:arguments[0],storeId:arguments[1],itemId:arguments[2],endpoint:arguments[3],saves:[],attempts:[],pending:0,errors:[],lookups:[],uiErrors:[]};
    if(!window.__purchaseQAErrorsInstalled){window.__purchaseQAErrorsInstalled=true;
      addEventListener('error',e=>window.__purchaseQA?.uiErrors.push(e.message));
      addEventListener('unhandledrejection',e=>window.__purchaseQA?.uiErrors.push(String(e.reason?.stack||e.reason)));}
    if(store.__purchaseQACaptureInstalled)return;store.__purchaseQACaptureInstalled=true;const dispatch=store.dispatch.bind(store);
    store.dispatch=function(action,payload,...rest){const cap=window.__purchaseQA;
      const lookup=cap&&action==='app/get'&&/^items[?]/i.test(payload?.url||'')&&String(payload.url).includes('storeId='+cap.storeId);
      const owned=cap&&['app/create','app/update'].includes(action)&&payload?.url===cap.endpoint&&
        ((typeof payload?.data?.notes==='string'&&payload.data.notes.startsWith(cap.marker))||
        (payload.data?.storeId===cap.storeId&&payload.data.lines?.length>0&&payload.data.lines.every(l=>l.itemId===cap.itemId)));
      const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;if(owned){cap.pending++;cap.attempts.push({endpoint:payload.url,values:sent});}
      return dispatch(action,payload,...rest).then(result=>{if(lookup){const data=result?.data||result;cap.lookups.push({url:payload.url,total:data?.totalRecords,items:data?.pageData?.map(i=>({id:i.id,arabicName:i.arabicName}))});}
        if(owned){cap.pending--;const entity=result?.data||result;
        cap.saves.push({endpoint:payload.url,id:sent.id||entity?.id||(typeof entity==='string'||typeof entity==='number'?entity:null),values:sent});}return result;})
        .catch(error=>{if(lookup)cap.lookups.push({url:payload.url,error:String(error),status:error?.response?.status});if(owned){cap.pending--;cap.errors.push(JSON.stringify(error?.response?.data||error?.data||{message:String(error),status:error?.status}));}throw error;});};
    """,
                marker,
                storeId,
                itemId,
                endpoint);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> puSaves() {
        return (List<Map<String, Object>>) js("return window.__purchaseQA?.saves||[]");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> puAttempts() {
        return (List<Map<String, Object>>) js("return window.__purchaseQA?.attempts||[]");
    }

    public void puSettled() {
        ready.until(d -> !Boolean.TRUE.equals(js("return (window.__purchaseQA?.pending||0)>0")));
    }

    public String puSaveErrors() {
        return String.valueOf(js("return window.__purchaseQA?.errors||[]"));
    }

    public String puDiagnostics() {
        return String.valueOf(
                js(
                        "return JSON.stringify({lookups:window.__purchaseQA?.lookups||[],errors:window.__purchaseQA?.uiErrors||[],saveErrors:window.__purchaseQA?.errors||[],pending:window.__purchaseQA?.pending,attempts:window.__purchaseQA?.attempts,saves:window.__purchaseQA?.saves})"));
    }

    public void puWaitSaveOutcome() {
        ready.until(
                d ->
                        !puSaves().isEmpty()
                                || Boolean.TRUE.equals(
                                        js("return (window.__purchaseQA?.errors||[]).length>0"))
                                || !d.findElements(
                                                By.cssSelector(
                                                        "form [aria-invalid='true'],form input:invalid,form select:invalid,form textarea:invalid"))
                                        .isEmpty()
                                || d
                                        .findElements(
                                                By.cssSelector(
                                                        ".text-danger,.invalid-feedback,.swal2-html-container,.swal2-title"))
                                        .stream()
                                        .anyMatch(e -> e.isDisplayed() && !e.getText().isBlank()));
    }

    public String puCaptureMarker() {
        return (String) js("return window.__purchaseQA?.marker||null");
    }

    @Step("فتح سند الاختبار للتعديل")
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
        return (Map<String, Object>)
                js(
                        "const q=[document.querySelector('#app').__vue__];while(q.length){const v=q.shift();q.push(...(v.$children||[]));if(v.$data?.selectedItem?.lines&&v.$options.methods?.save)return JSON.parse(JSON.stringify(v.$data.selectedItem));}return null");
    }

    public void puFormReady() {
        ready.until(
                d -> {
                    var doc = puDocument();
                    return doc != null
                            && doc.get("transactionDate") instanceof String date
                            && !date.isBlank();
                });
    }

    public void puAwaitOwnedLookups(Object store, Object supplier, Object agent) {
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(
                        d ->
                                Boolean.TRUE.equals(
                                        js(
                                                "const q=[document.querySelector('#app').__vue__];while(q.length){const v=q.shift();q.push(...(v.$children||[]));if(v.$data?.selectedItem?.lines&&v.$options.methods?.save){const l=v.lookups;return Boolean(l?.stores?.some(x=>x.id===arguments[0])&&l?.suppliers?.some(x=>x.id===arguments[1])&&l?.agents?.some(x=>x.id===arguments[2]));}}return false",
                                                store,
                                                supplier,
                                                agent)));
    }

    public String puDocumentDate() {
        puFormReady();
        return puDocument().get("transactionDate").toString().substring(0, 10);
    }

    public void puSetLine(String name, String column, String value) {
        ready.until(
                d -> {
                    WebElement row =
                            d.findElements(line(name)).stream()
                                    .filter(WebElement::isDisplayed)
                                    .findFirst()
                                    .orElse(null);
                    if (row == null) return false;
                    WebElement table = row.findElement(By.xpath("ancestor::table[1]"));
                    String index =
                            table.findElements(By.cssSelector("thead th")).stream()
                                    .filter(
                                            e ->
                                                    e.getDomProperty("textContent")
                                                            .trim()
                                                            .equals(column))
                                    .map(e -> e.getDomAttribute("aria-colindex"))
                                    .findFirst()
                                    .orElseThrow(
                                            () ->
                                                    new AssertionError(
                                                            "Missing line column: " + column));
                    for (WebElement input :
                            row.findElements(
                                    By.cssSelector(
                                            "td[aria-colindex='"
                                                    + index
                                                    + "'] input:not([type='hidden'])"))) {
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", input);
                        if (input.isDisplayed() && input.isEnabled()) {
                            input.click();
                            input.getDomProperty("value");
                            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), value, Keys.TAB);
                            return true;
                        }
                    }
                    return false;
                });
    }

    public Map<String, String> puTotals() {
        Map<String, String> result = new LinkedHashMap<>();
        for (String label : List.of("الاجمالى", "خصم", "قبل الضريبة", "الضريبة", "الصافي")) {
            for (WebElement input :
                    driver.findElements(
                            By.xpath(
                                    "//span[contains(@class,'font-weight-bolder') and normalize-space()='"
                                            + label
                                            + "']/parent::div/following-sibling::div[1]//input"))) {
                if (input.isDisplayed()) {
                    result.put(label, input.getDomProperty("value"));
                    break;
                }
            }
        }
        if (!result.containsKey("الصافي"))
            for (WebElement input : driver.findElements(By.cssSelector("input[name='Net']")))
                if (input.isDisplayed()) result.put("الصافي", input.getDomProperty("value"));
        return result;
    }

    public void puWaitNet(String value) {
        ready.until(
                d -> {
                    String actual = puTotals().get("الصافي");
                    return actual != null
                            && new java.math.BigDecimal(actual.replace(",", ""))
                                            .compareTo(new java.math.BigDecimal(value))
                                    == 0;
                });
    }

    public void puDiscount(String value, boolean percentage) {
        puType(
                By.cssSelector(
                        percentage
                                ? "fieldset[name='discountPercentage'] input"
                                : "fieldset[name='discountValue'] input"),
                value);
    }

    public java.math.BigDecimal puUserDiscountLimit() {
        Object value =
                js(
                        "const q=[document.querySelector('#app').__vue__];while(q.length){const v=q.shift();q.push(...(v.$children||[]));const user=v.lookups?.CurrentUser||v.lookups?.currentUser;if(user?.maxAllowedDiscount!=null)return user.maxAllowedDiscount;}return null");
        return value == null ? null : new java.math.BigDecimal(value.toString());
    }

    public void puAddOtherPayment(String method, String amount) {
        radio("PaymentMethods");
        puChoose("طريقة الدفع", method);
        puType(By.cssSelector("form input[name='value']:not([disabled])"), amount);
        puClick(
                By.xpath(
                        "//form//*[self::legend or self::label][normalize-space()='طريقة الدفع']/ancestor::div[contains(@class,'row')][1]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
    }

    public void puNew() {
        puClick(By.xpath("//button[normalize-space()='جديد']"));
        puDismiss();
        ready.until(d -> ((List<?>) puDocument().get("lines")).isEmpty());
    }

    public String puNotesValue() {
        return driver.findElements(By.cssSelector("form textarea[label-text='Notes']")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow()
                .getDomProperty("value");
    }

    public void puLinkedReturn(Object invoiceId, String itemName) {
        puOpen(
                "return-purch/new?sourceTransactionId="
                        + invoiceId
                        + "&sourceTransactionType=Purch");
        puFormReady();
        ready.until(d -> puLineCount(itemName) > 0);
    }

    @Step("تحويل مستند الاختبار المحدد بالكود: {code}")
    public void puConvertOwned(String supplier, String code, boolean toInvoice, String itemName) {
        puListScope(supplier);
        puListText("filteredCode", code);
        ready.until(
                d ->
                        puListRows().size() == 1
                                && code.equals(String.valueOf(puListRows().get(0).get("code"))));
        Object sourceId = puListRows().get(0).get("id");
        String icon = toInvoice ? "repeat" : "refresh-cw";
        puClick(
                By.xpath(
                        "//table//tbody/tr[.//td[normalize-space()='"
                                + code
                                + "']]//button[.//*[contains(@class,'"
                                + icon
                                + "')]]"));
        ready.until(d -> d.getCurrentUrl().contains(toInvoice ? "purch-invoice" : "return-purch"));
        puFormReady();
        ready.until(
                d ->
                        sourceId.equals(puDocument().get("sourceTransactionId"))
                                && puLineCount(itemName) > 0);
        ready.until(
                d ->
                        d.findElements(By.cssSelector("#loading-bg,div.b-overlay")).stream()
                                .noneMatch(WebElement::isDisplayed));
        utilites.waits().waitForOverlayToDisappear();
    }

    @Step("حذف مستند الاختبار من القائمة: {code}")
    public void puDeleteOwned(String supplier, String code) {
        puListScope(supplier);
        puListText("filteredCode", code);
        ready.until(
                d ->
                        puListRows().size() == 1
                                && code.equals(String.valueOf(puListRows().get(0).get("code"))));
        puClick(
                By.xpath(
                        "//table//tbody/tr[.//td[normalize-space()='"
                                + code
                                + "']]//button[@data-action-type='delete']"));
        puClick(By.cssSelector("button.swal2-confirm"));
        puDismiss();
        puWaitListIds(Set.of());
    }

    public void puListScope(String customer) {
        puOpen("purch-invoice-list");
        puAwaitListReady();
        puButton("عرض التصفية");
        puChoose("المورد", customer);
        puApplyListFilters();
    }

    public void puAwaitListReady() {
        ready.until(d -> puListReady());
    }

    private boolean puListReady() {
        return Boolean.TRUE.equals(
                js(
                        "const q=[document.querySelector('#app').__vue__];let table,owner;while(q.length){const v=q.shift();q.push(...(v.$children||[]));if(v.$options.methods?.invoiceProvider)owner=v;if(v.$options.name==='BTable'&&v.$el?.getClientRects().length&&v.$el.closest('.ps-list-page'))table=v;}return Boolean(table&&owner&&!table.computedBusy&&!owner.isTableBusy&&performance.getEntriesByType('resource').some(r=>{try{return /\\/PurchaseLists$/i.test(new URL(r.name).pathname)}catch(e){return false}}))"));
    }

    public void puApplyListFilters() {
        puAwaitListReady();
        ready.until(
                d -> {
                    for (WebElement input :
                            d.findElements(By.cssSelector("input[name='codeFilter']")))
                        if (input.isDisplayed() && input.isEnabled()) {
                            input.sendKeys(Keys.ENTER);
                            return true;
                        }
                    return false;
                });
        puAwaitListReady();
    }

    public String puListFixtureDate() {
        puOpen("purch-invoice-list");
        puAwaitListReady();
        puButton("عرض التصفية");
        ready.until(
                d ->
                        !d.findElements(By.cssSelector("input[name='fromDate']")).isEmpty()
                                && !d.findElements(By.cssSelector("input[name='toDate']"))
                                        .isEmpty());
        var from =
                java.time.LocalDate.parse(
                        driver.findElement(By.cssSelector("input[name='fromDate']"))
                                .getDomProperty("value")
                                .substring(0, 10));
        var to =
                java.time.LocalDate.parse(
                        driver.findElement(By.cssSelector("input[name='toDate']"))
                                .getDomProperty("value")
                                .substring(0, 10));
        if (to.isBefore(from))
            throw new AssertionError("Default purchase list date range is invalid");
        var today = java.time.LocalDate.now();
        return (today.isBefore(from) ? from : today.isAfter(to) ? to : today).toString();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> puListRows() {
        return (List<Map<String, Object>>)
                js(
                        "const q=[document.querySelector('#app').__vue__];while(q.length){const v=q.shift();q.push(...(v.$children||[]));if(v.$options.name==='BTable'&&v.$el?.getClientRects().length&&v.$el.closest('.ps-list-page')&&Array.isArray(v.$data.localItems))return JSON.parse(JSON.stringify(v.$data.localItems));}return []");
    }

    public void puWaitListIds(Set<Object> expected) {
        ready.until(
                d -> {
                    if (!puListReady()) return false;
                    var actual = new HashSet<Object>();
                    for (var row : puListRows()) actual.add(row.get("id"));
                    return actual.equals(expected);
                });
    }

    public void puListText(String name, String text) {
        puAwaitListReady();
        if (name.equals("fromDate") || name.equals("toDate")) {
            puDateFilter(name, text);
            return;
        }
        puType(
                By.cssSelector(
                        "input[name='"
                                + (name.equals("filteredCode") ? "codeFilter" : name)
                                + "']"),
                text,
                false);
        puApplyListFilters();
    }

    public void puListRadio(String group, String value) {
        String anchor = group.equals("status") ? "2" : "sales";
        puClick(
                By.xpath(
                        "//div[@role='radiogroup' and .//input[@value='"
                                + anchor
                                + "']]//input[@value='"
                                + value
                                + "']/ancestor::label[1]"));
    }

    public List<String> puHeaders() {
        return driver.findElements(By.cssSelector("table thead th")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .toList();
    }

    public void puPageSize(int size) {
        ready.until(
                d -> {
                    for (WebElement select : d.findElements(By.tagName("select")))
                        if (select.isDisplayed()
                                && new Select(select)
                                        .getOptions().stream()
                                                .anyMatch(
                                                        o ->
                                                                o.getDomProperty("value")
                                                                        .equals(
                                                                                Integer.toString(
                                                                                        size)))) {
                            new Select(select).selectByValue(Integer.toString(size));
                            return true;
                        }
                    return false;
                });
        puAwaitListReady();
    }

    public void puNextPage() {
        puClick(
                By.cssSelector(
                        "button.page-link[aria-label='Go to next page'],a.page-link[aria-label='Go to next page']"));
    }

    public void puFirstPage() {
        puClick(
                By.cssSelector(
                        "button.page-link[aria-label='Go to first page'],a.page-link[aria-label='Go to first page']"));
    }

    public void puSort(String label) {
        puClick(
                By.xpath(
                        "//table//th[.//*[normalize-space()='"
                                + label
                                + "'] or normalize-space()='"
                                + label
                                + "']"));
        puAwaitListReady();
    }

    public void puExport(String format) {
        puClick(
                By.cssSelector(
                        "button:has(img[src*='"
                                + (format.equals("pdf") ? "pdf." : "xls.")
                                + "'])"));
    }

    public String puWindowHandle() {
        return driver.getWindowHandle();
    }

    public void puCloseAuxiliaryWindows(String main) {
        for (String window : new ArrayList<>(driver.getWindowHandles()))
            if (!window.equals(main)) {
                driver.switchTo().window(window);
                driver.close();
            }
        driver.switchTo().window(main);
    }

    public void puVerifyPrintedReport(String main, String item) {
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(d -> d.getWindowHandles().size() > 1);
        driver.switchTo()
                .window(
                        driver.getWindowHandles().stream()
                                .filter(w -> !w.equals(main))
                                .findFirst()
                                .orElseThrow());
        try {
            utilitesReportReady();
            AssertTextInReport(item);
        } finally {
            driver.close();
            driver.switchTo().window(main);
            puDismiss();
        }
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
        String pickerId =
                "fromDate".equals(hiddenInput.getAttribute("name"))
                        ? "example-datepicker"
                        : "example-datepicker2";
        WebElement picker = driver.findElement(By.id(pickerId));
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id(pickerId)));
        picker = driver.findElement(By.id(pickerId));
        picker.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        String[] parts = value.split("-");
        if (parts.length != 3) throw new IllegalArgumentException("Date must use yyyy-MM-dd");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        wait.until(d -> calendarIsVisible(pickerId));
        String shown = calendarCaption(pickerId);
        java.time.YearMonth target = java.time.YearMonth.of(year, month);
        for (int i = 0; i < 240; i++) {
            java.util.regex.Matcher yearMatcher =
                    java.util.regex.Pattern.compile("(\\d{4})").matcher(shown);
            if (!yearMatcher.find())
                throw new IllegalStateException("Calendar year not found: " + shown);
            int shownYear = Integer.parseInt(yearMatcher.group(1));
            int shownMonth = parseMonth(shown);
            java.time.YearMonth current = java.time.YearMonth.of(shownYear, shownMonth);
            if (current.equals(target)) break;
            String direction = target.isBefore(current) ? "previous" : "next";
            String previousCaption = shown;
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript(
                            "const direction=arguments[0],picker=document.getElementById(arguments[1]);"
                                    + "const calendars=[...document.querySelectorAll('.b-calendar')];"
                                    + "const calendar=calendars.find(c=>c.getClientRects().length&&picker&&c.closest('.b-form-datepicker')===picker.closest('.b-form-datepicker'))"
                                    + "||calendars.find(c=>c.getClientRects().length);"
                                    + "const buttons=calendar?[...calendar.querySelectorAll('.b-calendar-nav button')]:[];"
                                    + "const button=buttons.find(b=>((b.getAttribute('aria-label')||'')+' '+(b.title||'')).toLowerCase().includes(direction))"
                                    + "||(buttons.length?buttons[direction==='previous'?0:buttons.length-1]:null);"
                                    + "if(button){button.click();return true;}return false;",
                            direction,
                            pickerId);
            shown =
                    new WebDriverWait(driver, Duration.ofSeconds(5))
                            .until(
                                    d -> {
                                        try {
                                            String currentCaption = calendarCaption(pickerId);
                                            return currentCaption.equals(previousCaption)
                                                    ? null
                                                    : currentCaption;
                                        } catch (
                                                org.openqa.selenium.StaleElementReferenceException
                                                        ignored) {
                                            return null;
                                        }
                                    });
        }
        String dayText = String.valueOf(day);
        By dayButtons =
                By.cssSelector(".b-calendar button, .b-calendar [role='button'], .b-calendar .btn");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(
                            d -> {
                                try {
                                    List<WebElement> candidates =
                                            d.findElements(dayButtons).stream()
                                                    .filter(WebElement::isDisplayed)
                                                    .filter(
                                                            button ->
                                                                    button.getText()
                                                                            .trim()
                                                                            .replaceAll("\\D", "")
                                                                            .equals(dayText))
                                                    .filter(
                                                            button ->
                                                                    !button.getAttribute("class")
                                                                            .contains("text-muted"))
                                                    .filter(WebElement::isEnabled)
                                                    .toList();
                                    WebElement targetDay =
                                            candidates.stream()
                                                    .filter(
                                                            button ->
                                                                    String.valueOf(
                                                                                    button
                                                                                            .getAttribute(
                                                                                                    "aria-label"))
                                                                            .contains(
                                                                                    String.valueOf(
                                                                                            year)))
                                                    .findFirst()
                                                    .orElse(
                                                            candidates.stream()
                                                                    .findFirst()
                                                                    .orElse(null));
                                    if (targetDay == null) return false;
                                    targetDay.click();
                                    return true;
                                } catch (
                                        org.openqa.selenium.StaleElementReferenceException
                                                ignored) {
                                    return false;
                                }
                            });
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new IllegalStateException("Calendar day not found: " + value, e);
        }
    }

    private boolean calendarIsVisible(String pickerId) {
        return Boolean.TRUE.equals(
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript(
                                "const picker=document.getElementById(arguments[0]),calendars=[...document.querySelectorAll('.b-calendar')];"
                                        + "return calendars.some(c=>c.getClientRects().length&&picker&&c.closest('.b-form-datepicker')===picker.closest('.b-form-datepicker'))"
                                        + "||calendars.some(c=>c.getClientRects().length);",
                                pickerId));
    }

    private String calendarCaption(String pickerId) {
        Object value =
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript(
                                "const picker=document.getElementById(arguments[0]),calendars=[...document.querySelectorAll('.b-calendar')];"
                                        + "const calendar=calendars.find(c=>c.getClientRects().length&&picker&&c.closest('.b-form-datepicker')===picker.closest('.b-form-datepicker'))"
                                        + "||calendars.find(c=>c.getClientRects().length);"
                                        + "const caption=calendar&&calendar.querySelector('.b-calendar-grid-caption');"
                                        + "return caption?(caption.innerText||caption.textContent||'').trim():'';");
        return value == null ? "" : value.toString();
    }

    private int parseMonth(String text) {
        java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile("(\\d{1,2})[/-]").matcher(text);
        if (matcher.find()) return Integer.parseInt(matcher.group(1));
        String normalized = text.replaceAll("\\d", "").trim();
        String[] arabicMonths = {
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر",
            "أكتوبر", "نوفمبر", "ديسمبر"
        };
        for (int i = 0; i < arabicMonths.length; i++)
            if (normalized.contains(arabicMonths[i])) return i + 1;
        return java.time.Month.valueOf(normalized.toUpperCase(Locale.ROOT).substring(0, 3))
                .getValue();
    }
}
