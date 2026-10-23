package Plugin_ERP.System_Modules.Login.Invoice;

import Plugin_ERP.System_Modules.Login.AbstractForSales;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import Plugin_ERP.Utiel.FormInteractions;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Sales UI interactions; scenarios and persisted inventory assertions live in tests. */
public class SalesModulePage extends AbstractForSales {
    protected final WebDriverWait ready;
    private final FormInteractions interactions;
    private final SalesDocumentList documentList;

    public SalesModulePage(WebDriver driver) {
        super(driver);
        ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
        interactions = new FormInteractions(ready,
                element -> js("arguments[0].scrollIntoView({block:'center',inline:'center'})", element));
        documentList = new SalesDocumentList(this, driver, ready);
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
        Plugin_ERP.Utiel.Allur.ScreenNames.opening(route);
        String destination=PropertyReader.getApplicationUrl().replaceAll("/+$","") + "/#/" + route;
        if(driver.getCurrentUrl().equals(destination)) driver.navigate().refresh();
        else driver.get(destination);
        slAwaitSession();
        ready.until(d -> Boolean.TRUE.equals(js("""
            const route=document.querySelector('#app')?.__vue__?.$router?.currentRoute;
            if(!route||route.path.replace(/\\/+$/g,'')!=='/'+arguments[0].replace(/^\\/+|\\/+$/g,''))return false;
            const view=route.matched[route.matched.length-1]?.instances?.default;
            return !!(view?._isMounted&&!view._isDestroyed&&view.$el?.isConnected&&view.$el.getClientRects?.().length);
            """,route)));
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

    void slType(By by, String value, boolean blur) {
        interactions.type(by, value, blur, FormInteractions.InputMode.CLICK_REPLACE);
    }

    protected void slClick(By by) {
        interactions.click(by);
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
        interactions.choose(by, name, FormInteractions.OptionMatch.CODE_SEGMENT);
    }

    @Step("مسح الاختيار من {label}")
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

    @Step("إدخال ملاحظات السند: {note}")
    public void slNotes(String note) {
        slType(By.cssSelector("form textarea[label-text='Notes']"), note);
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
        new WebDriverWait(driver, Duration.ofSeconds(90))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class).until(
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

    @Step("إضافة الصنف بالاسم: {name}")
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

    @Step("إضافة الصنف بالباركود: {barcode}")
    public void slAddBarcode(String barcode, String name) {
        slAddBarcodeImmediately(barcode, name);
    }

    @Step("إدخال الباركود {barcode} وإضافة الصنف {name}")
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

    @Step("إضافة الصنف من قائمة الأصناف: {name}")
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

    @Step("البحث في {field} عن {value} والتحقق من الصنف {expectedName}")
    public void slPickerSearch(String field, String value, String expectedName) {
        slButton("إضافة أصناف");
        slType(By.cssSelector(".modal.show input[name='" + field + "']"), value);
        ready.until(d -> slPickerRows().stream().anyMatch(row -> row.contains(expectedName)));
    }

    @Step("تصفية الأصناف بالفئة {category} والتحقق من الصنف {expectedName}")
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

    @Step("إغلاق نافذة اختيار الصنف")
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

    @Step("حذف الصنف {name} من السند")
    public void slRemoveLine(String name) {
        slClick(
                By.xpath(
                        "//form//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-trash')]]"));
        ready.until(d -> slLineCount(name) == 0);
    }

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

    @Step("تغيير {column} للصنف {name} إلى {value}")
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

    @Step("التحقق من صافي السند المتوقع: {value}")
    public void slWaitNet(String value) {
        try {
            ready.until(
                d -> {
                    String actual = slTotals().get("الصافي");
                    return actual != null
                            && new java.math.BigDecimal(actual.replace(",", ""))
                                            .compareTo(new java.math.BigDecimal(value))
                                    == 0;
                });
        } catch (org.openqa.selenium.TimeoutException failure) {
            throw new AssertionError("صافي السند غير مطابق: المتوقع " + value
                    + "، الفعلي " + slTotals().get("الصافي"), failure);
        }

    }

    @Step("إدخال خصم {value}، خصم بنسبة: {percentage}")
    public void slDiscount(String value, boolean percentage) {
        slType(
                By.cssSelector(
                        percentage
                                ? "fieldset[name='discountPercentage'] input"
                                : "fieldset[name='discountValue'] input"),
                value);
    }

    @Step("إضافة طريقة الدفع {method} بمبلغ {amount}")
    public void slAddOtherPayment(String method, String amount) {
        radio("PaymentMethods");
        slChoose("طريقة الدفع", method);
        slType(By.cssSelector("form input[name='value']:not([disabled])"), amount);
        slClick(
                By.xpath(
                        "//form//*[self::legend or self::label][normalize-space()='طريقة الدفع']/ancestor::div[contains(@class,'row')][1]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
    }

    @Step("فتح نموذج سند جديد")
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

    @Step("التحقق من بيانات الصنف {item} في التقرير المطبوع")
    public void slVerifyPrintedReport(String main, String item) {
        slVerifyPrintedReport(main,item,()->{});
    }
    public void slVerifyPrintedReport(String main, String item, Runnable contentAssertions) {
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
            contentAssertions.run();
        } finally {
            driver.close();
            driver.switchTo().window(main);
            slDismiss();
        }
    }
}
