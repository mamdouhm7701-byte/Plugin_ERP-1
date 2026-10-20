package Plugin_ERP.System_Modules.Login.Invoice;

import Plugin_ERP.System_Modules.Login.AbstractForSales;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Sales UI interactions; scenarios and persisted inventory assertions live in tests. */
public class SalesModulePage extends AbstractForSales {
    protected final WebDriverWait ready;

    public SalesModulePage(WebDriver driver) {
        super(driver);
        ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
    }

    public void slAwaitSession() {
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

    @Step("فتح شاشة المبيعات: {route}")
    public void slOpen(String route) {
        driver.get(PropertyReader.getApplicationUrl() + "/#/" + route);
        driver.navigate().refresh();
        slAwaitSession();
        ready.until(
                d ->
                        d
                                .findElements(
                                        By.cssSelector(
                                                "form button[data-action-type='save'],button[data-action-type='new'],table thead th"))
                                .stream()
                                .anyMatch(WebElement::isDisplayed));
        slDismiss();
    }

    public void slType(By by, String value) {
        slType(by, value, true);
    }

    private void slType(By by, String value, boolean blur) {
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

    protected void slClick(By by) {
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
    public void slChoose(String label, String name) {
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

    public void slClearChoice(String label) {
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
        ready.until(d -> slChoiceEmpty(label));
    }

    public boolean slChoiceEmpty(String label) {
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

    public boolean slCanClearChoice(String label) {
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

    public boolean slHasChoice(String label) {
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

    public void slNotes(String note) {
        slType(By.cssSelector("form textarea[label-text='Notes']"), note);
    }

    public String slDiagnosticMarkup() {
        return driver.getPageSource();
    }

    public void utilitesReportReady() {
        utilites.waits().waitForReportReady(60);
    }

    public void slButton(String text) {
        slClick(By.xpath("//button[normalize-space()='" + text + "']"));
    }

    public void slSave(boolean print) {
        slClick(
                print
                        ? By.cssSelector("button[data-action-type='saveAndPrint']")
                        : By.xpath(
                                "//button[@data-action-type='save' and normalize-space()='حفظ']"));
    }

    public boolean slSaveDisabled() {
        return driver
                .findElements(
                        By.xpath("//button[@data-action-type='save' and normalize-space()='حفظ']"))
                .stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(e -> !e.isEnabled());
    }

    public boolean slHasDocumentSaveAction() {
        return driver
                .findElements(
                        By.xpath(
                                "//button[(@data-action-type='save' and normalize-space()='حفظ') or @data-action-type='saveAndPrint']"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public boolean slHasDocumentDeleteAction() {
        return driver
                .findElements(
                        By.xpath(
                                "//button[not(ancestor::tbody) and (@data-action-type='delete' or normalize-space()='حذف' or @title='حذف')] | //a[@role='menuitem' and normalize-space()='حذف']"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public boolean slLineReadOnly(String name, String column) {
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

    public void slDismiss() {
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
    public String slValidation() {
        return ready.until(
                d -> {
                    List<String> out = new ArrayList<>();
                    for (WebElement e :
                            d.findElements(
                                    By.cssSelector(
                                            "small.text-danger,.invalid-feedback,.swal2-title,.swal2-html-container,.b-toast-danger .toast-body,.v-toast__item--error .v-toast__text"))) {
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

    public int slLineCount(String name) {
        return (int)
                driver.findElements(line(name)).stream().filter(WebElement::isDisplayed).count();
    }

    public Map<String, String> slLineValues(String name) {
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

    public void slWaitOwnedItemLookup(String name) {
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                js(
                                        "try{return (window.__salesQA?.lookups||[]).some(l=>(l.items||[]).some(i=>i.arabicName===arguments[0]))||performance.getEntriesByType('resource').some(r=>{try{const u=new URL(r.name);return /\\/items$/i.test(u.pathname)&&u.searchParams.get('storeId')===String(window.__salesQA?.storeId)&&!u.searchParams.has('barcode')}catch(e){return false}})}catch(e){return false}",
                                        name)));
    }

    @Step("إضافة صنف الاختبار بالاسم: {name}")
    public void slAddName(String name) {
        radio("name");
        slType(By.cssSelector("form input#autosuggest__input"), name, false);
        try {
            slClick(
                    By.xpath(
                            "//div[@id='autosuggest-autosuggest__results']//li[contains(normalize-space(),'"
                                    + name
                                    + "')] | //span[contains(@class,'my-suggestion-item') and contains(normalize-space(),'"
                                    + name
                                    + "')]"));
            ready.until(d -> slLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned item did not appear in name autocomplete. "
                            + slItemCacheDiagnostics(name),
                    e);
        }
    }

    @Step("إضافة صنف الاختبار بالباركود: {barcode}")
    public void slAddBarcode(String barcode, String name) {
        slAddBarcodeImmediately(barcode, name);
    }

    public void slAddBarcodeImmediately(String barcode, String name) {
        radio("barcode");
        slType(
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
            ready.until(d -> slLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned barcode did not add its item. " + slItemCacheDiagnostics(name), e);
        }
    }

    public boolean slOwnedItemCached(String name) {
        return Boolean.TRUE.equals(
                js(
                        "try{return JSON.parse(sessionStorage.getItem('cachedArray')||'[]').some(i=>(i.arabicName||i.itemArabicName)===arguments[0])}catch(e){return false}",
                        name));
    }

    private Object slItemCacheDiagnostics(String name) {
        return js(
                "const raw=sessionStorage.getItem('cachedArray');let rows;try{rows=JSON.parse(raw)}catch(e){return {cacheInvalid:true}}return {cachePresent:raw!==null,cacheCount:Array.isArray(rows)?rows.length:null,ownedItemCached:Array.isArray(rows)&&rows.some(r=>r.arabicName===arguments[0]),lookups:window.__salesQA?.lookups||[]}",
                name);
    }

    @Step("إضافة صنف الاختبار من قائمة الأصناف: {name}")
    public void slAddPicker(String name) {
        slButton("إضافة أصناف");
        slType(By.cssSelector(".modal.show input[label-text='arabicName']"), name);
        slClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
        slClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='حفظ']"));
        ready.until(
                d ->
                        slLineCount(name) > 0
                                && d.findElements(By.cssSelector(".modal.show")).stream()
                                        .noneMatch(WebElement::isDisplayed));
    }

    public void slPickerSearch(String field, String value, String expectedName) {
        slButton("إضافة أصناف");
        slType(By.cssSelector(".modal.show input[name='" + field + "']"), value);
        ready.until(d -> slPickerRows().stream().anyMatch(row -> row.contains(expectedName)));
    }

    public void slPickerCategory(String category, String expectedName) {
        slButton("إضافة أصناف");
        slChoose("الفئة", category);
        ready.until(
                d -> {
                    var rows = slPickerRows();
                    return !rows.isEmpty()
                            && rows.stream().allMatch(row -> row.contains(expectedName));
                });
    }

    public List<String> slPickerRows() {
        return driver.findElements(By.cssSelector(".modal.show tbody tr")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .filter(t -> !t.isBlank() && !t.contains("لا يوجد بيانات"))
                .toList();
    }

    public void slClosePicker() {
        slClick(By.cssSelector(".modal.show button.close"));
        ready.until(
                d ->
                        d.findElements(By.cssSelector(".modal.show")).stream()
                                .noneMatch(WebElement::isDisplayed));
    }

    @Step("تحديد كمية {name}: {quantity}")
    public void slQuantity(String name, String quantity) {
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

    public void slRemoveLine(String name) {
        slClick(
                By.xpath(
                        "//form//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-trash')]]"));
        ready.until(d -> slLineCount(name) == 0);
    }

    @Step("تسجيل طلبات حفظ سند الاختبار")
    public void slCapture(String marker, Object storeId, Object itemId, String endpoint) {
        js(
                """
    const store=document.querySelector('#app').__vue__.$store;
    window.__salesQA={marker:arguments[0],storeId:arguments[1],itemId:arguments[2],endpoint:arguments[3],saves:[],attempts:[],pending:0,errors:[],lookups:[],uiErrors:[]};
    if(!window.__salesQAErrorsInstalled){window.__salesQAErrorsInstalled=true;
      addEventListener('error',e=>window.__salesQA?.uiErrors.push(e.message));
      addEventListener('unhandledrejection',e=>window.__salesQA?.uiErrors.push(String(e.reason?.stack||e.reason)));}
    if(store.__salesQACaptureInstalled)return;store.__salesQACaptureInstalled=true;const dispatch=store.dispatch.bind(store);
    store.dispatch=function(action,payload,...rest){const cap=window.__salesQA;
      const lookup=cap&&action==='app/get'&&/^items[?]/i.test(payload?.url||'')&&String(payload.url).includes('storeId='+cap.storeId);
      const owned=cap&&['app/create','app/update'].includes(action)&&payload?.url===cap.endpoint&&
        ((typeof payload?.data?.notes==='string'&&payload.data.notes.startsWith(cap.marker))||
        (payload.data?.storeId===cap.storeId&&payload.data.lines?.length>0&&payload.data.lines.every(l=>l.itemId===cap.itemId)));
      const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;if(owned){cap.pending++;cap.attempts.push({endpoint:payload.url,values:sent});}
      return dispatch(action,payload,...rest).then(result=>{if(lookup){const data=result?.data||result;cap.lookups.push({url:payload.url,total:data?.totalRecords,items:data?.pageData?.map(i=>({id:i.id,arabicName:i.arabicName}))});}
        if(owned){cap.pending--;const entity=result?.data||result;
        cap.saves.push({endpoint:payload.url,id:sent.id||entity?.id||(typeof entity==='string'||typeof entity==='number'?entity:null),values:sent});}return result;})
        .catch(error=>{if(lookup)cap.lookups.push({url:payload.url,error:String(error),status:error?.response?.status});if(owned){cap.pending--;cap.errors.push(String(error));}throw error;});};
    """,
                marker,
                storeId,
                itemId,
                endpoint);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> slSaves() {
        return (List<Map<String, Object>>) js("return window.__salesQA?.saves||[]");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> slAttempts() {
        return (List<Map<String, Object>>) js("return window.__salesQA?.attempts||[]");
    }

    public void slSettled() {
        ready.until(d -> !Boolean.TRUE.equals(js("return (window.__salesQA?.pending||0)>0")));
    }

    public String slSaveErrors() {
        return String.valueOf(js("return window.__salesQA?.errors||[]"));
    }

    public String slDiagnostics() {
        return String.valueOf(
                js(
                        "return JSON.stringify({lookups:window.__salesQA?.lookups||[],errors:window.__salesQA?.uiErrors||[]})"));
    }

    public String slCaptureMarker() {
        return (String) js("return window.__salesQA?.marker||null");
    }

    @Step("فتح سند الاختبار للتعديل")
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
        return (Map<String, Object>)
                js(
                        "const q=[document.querySelector('#app').__vue__];while(q.length){const v=q.shift();q.push(...(v.$children||[]));if(v.$data?.selectedItem?.lines&&v.$options.methods?.save)return JSON.parse(JSON.stringify(v.$data.selectedItem));}return null");
    }

    public void slFormReady() {
        ready.until(
                d -> {
                    var doc = slDocument();
                    return doc != null
                            && doc.get("transactionDate") instanceof String date
                            && !date.isBlank();
                });
    }

    public String slDocumentDate() {
        slFormReady();
        return slDocument().get("transactionDate").toString().substring(0, 10);
    }

    public void slSetLine(String name, String column, String value) {
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

    public Map<String, String> slTotals() {
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
        return result;
    }

    public void slWaitNet(String value) {
        ready.until(
                d -> {
                    String actual = slTotals().get("الصافي");
                    return actual != null
                            && new java.math.BigDecimal(actual.replace(",", ""))
                                            .compareTo(new java.math.BigDecimal(value))
                                    == 0;
                });
    }

    public void slDiscount(String value, boolean percentage) {
        slType(
                By.cssSelector(
                        percentage
                                ? "fieldset[name='discountPercentage'] input"
                                : "fieldset[name='discountValue'] input"),
                value);
    }

    public void slAddOtherPayment(String method, String amount) {
        radio("PaymentMethods");
        slChoose("طريقة الدفع", method);
        slType(By.cssSelector("form input[name='value']:not([disabled])"), amount);
        slClick(
                By.xpath(
                        "//form//*[self::legend or self::label][normalize-space()='طريقة الدفع']/ancestor::div[contains(@class,'row')][1]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
    }

    public void slNew() {
        slClick(By.xpath("//button[normalize-space()='جديد']"));
        slDismiss();
        ready.until(d -> ((List<?>) slDocument().get("lines")).isEmpty());
    }

    public String slNotesValue() {
        return driver.findElements(By.cssSelector("form textarea[label-text='Notes']")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow()
                .getDomProperty("value");
    }

    public void slLinkedReturn(Object invoiceId, String itemName) {
        slOpen(
                "return-sales/new?sourceTransactionId="
                        + invoiceId
                        + "&sourceTransactionType=Sales");
        slFormReady();
        ready.until(d -> slLineCount(itemName) > 0);
    }

    public void slListScope(String customer) {
        slOpen("invoice-list");
        slAwaitListReady();
        slButton("عرض التصفية");
        slChoose("العميل", customer);
        slApplyListFilters();
    }

    public void slAwaitListReady() {
        ready.until(d -> slListReady());
    }

    private boolean slListReady() {
        return Boolean.TRUE.equals(
                js(
                        "const q=[document.querySelector('#app').__vue__];let table,owner;while(q.length){const v=q.shift();q.push(...(v.$children||[]));if(v.$options.methods?.invoiceProvider)owner=v;if(v.$options.name==='BTable'&&v.$el?.getClientRects().length&&v.$el.closest('.ps-list-page'))table=v;}return Boolean(table&&owner&&!table.computedBusy&&!owner.isTableBusy&&performance.getEntriesByType('resource').some(r=>{try{return /\\/SalesTransactions$/i.test(new URL(r.name).pathname)}catch(e){return false}}))"));
    }

    public void slApplyListFilters() {
        slAwaitListReady();
        ready.until(
                d -> {
                    for (WebElement input :
                            d.findElements(By.cssSelector("input[name='filteredCode']")))
                        if (input.isDisplayed() && input.isEnabled()) {
                            input.sendKeys(Keys.ENTER);
                            return true;
                        }
                    return false;
                });
        slAwaitListReady();
    }

    public String slListFixtureDate() {
        slOpen("invoice-list");
        slButton("عرض التصفية");
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
        if (to.isBefore(from)) throw new AssertionError("Default sales list date range is invalid");
        var today = java.time.LocalDate.now();
        return (today.isBefore(from) ? from : today.isAfter(to) ? to : today).toString();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> slListRows() {
        return (List<Map<String, Object>>)
                js(
                        "const q=[document.querySelector('#app').__vue__];while(q.length){const v=q.shift();q.push(...(v.$children||[]));if(v.$options.name==='BTable'&&v.$el?.getClientRects().length&&v.$el.closest('.ps-list-page')&&Array.isArray(v.$data.localItems))return JSON.parse(JSON.stringify(v.$data.localItems));}return []");
    }

    public void slWaitListIds(Set<Object> expected) {
        ready.until(
                d -> {
                    if (!slListReady()) return false;
                    var actual = new HashSet<Object>();
                    for (var row : slListRows()) actual.add(row.get("id"));
                    return actual.equals(expected);
                });
    }

    public void slListText(String name, String text) {
        slAwaitListReady();
        slType(By.cssSelector("input[name='" + name + "']"), text, false);
        slApplyListFilters();
    }

    public void slListRadio(String group, String value) {
        String anchor = group.equals("status") ? "2" : "sales";
        slClick(
                By.xpath(
                        "//div[@role='radiogroup' and .//input[@value='"
                                + anchor
                                + "']]//input[@value='"
                                + value
                                + "']/ancestor::label[1]"));
    }

    public List<String> slHeaders() {
        return driver.findElements(By.cssSelector("table thead th")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .toList();
    }

    public void slExport(String format) {
        slClick(
                By.cssSelector(
                        "button:has(img[src*='"
                                + (format.equals("pdf") ? "pdf." : "xls.")
                                + "'])"));
    }

    public String slWindowHandle() {
        return driver.getWindowHandle();
    }

    public void slCloseAuxiliaryWindows(String main) {
        for (String window : new ArrayList<>(driver.getWindowHandles()))
            if (!window.equals(main)) {
                driver.switchTo().window(window);
                driver.close();
            }
        driver.switchTo().window(main);
    }

    public void slVerifyPrintedReport(String main, String item) {
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
            slDismiss();
        }
    }
}
