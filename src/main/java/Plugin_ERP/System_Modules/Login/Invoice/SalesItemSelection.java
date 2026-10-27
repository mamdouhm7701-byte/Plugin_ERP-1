package Plugin_ERP.System_Modules.Login.Invoice;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Item autocomplete, barcode lookup and picker modal; document line editing stays separate. */
final class SalesItemSelection {
    private final SalesModulePage page;
    private final WebDriver driver;
    private final WebDriverWait ready;

    SalesItemSelection(SalesModulePage page, WebDriver driver, WebDriverWait ready) {
        this.page = page;
        this.driver = driver;
        this.ready = ready;
    }

    private Object js(String script, Object... args) {
        return page.js(script, args);
    }

    void radio(String value) {
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

    void slWaitOwnedItemLookup(String name) {
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                js(
                                        "try{return (window.__salesQA?.lookups||[]).some(l=>(l.items||[]).some(i=>i.arabicName===arguments[0]))||performance.getEntriesByType('resource').some(r=>{try{const u=new URL(r.name);return /\\/items$/i.test(u.pathname)&&u.searchParams.get('storeId')===String(window.__salesQA?.storeId)&&!u.searchParams.has('barcode')}catch(e){return false}})}catch(e){return false}",
                                        name)));
    }

    void slAddName(String name) {
        radio("name");
        page.slType(By.cssSelector("form input#autosuggest__input"), name, false);
        try {
            page.slClick(
                    By.xpath(
                            "//div[@id='autosuggest-autosuggest__results']//li[contains(normalize-space(),'"
                                    + name
                                    + "')] | //span[contains(@class,'my-suggestion-item') and contains(normalize-space(),'"
                                    + name
                                    + "')]"));
            ready.until(d -> page.slLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned item did not appear in name autocomplete. "
                            + slItemCacheDiagnostics(name),
                    e);
        }
    }

    void slAddBarcode(String barcode, String name) {
        page.slAddBarcodeImmediately(barcode, name);
    }

    void slAddBarcodeImmediately(String barcode, String name) {
        radio("barcode");
        page.slType(
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
            ready.until(d -> page.slLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned barcode did not add its item. " + slItemCacheDiagnostics(name), e);
        }
    }

    boolean slOwnedItemCached(String name) {
        return Boolean.TRUE.equals(
                js(
                        "try{return JSON.parse(sessionStorage.getItem('cachedArray')||'[]').some(i=>(i.arabicName||i.itemArabicName)===arguments[0])}catch(e){return false}",
                        name));
    }

    Object slItemCacheDiagnostics(String name) {
        return js(
                "const raw=sessionStorage.getItem('cachedArray');let rows;try{rows=JSON.parse(raw)}catch(e){return {cacheInvalid:true}}return {cachePresent:raw!==null,cacheCount:Array.isArray(rows)?rows.length:null,ownedItemCached:Array.isArray(rows)&&rows.some(r=>r.arabicName===arguments[0]),lookups:window.__salesQA?.lookups||[]}",
                name);
    }

    void slAddPicker(String name) {
        page.slButton("إضافة أصناف");
        page.slType(By.cssSelector(".modal.show input[label-text='arabicName']"), name);
        driver.findElement(By.cssSelector(".modal.show input[label-text='arabicName']"))
                .sendKeys(Keys.ENTER);
        page.slClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
        page.slClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='حفظ']"));
        ready.until(
                d ->
                        page.slLineCount(name) > 0
                                && d.findElements(By.cssSelector(".modal.show")).stream()
                                        .noneMatch(WebElement::isDisplayed));
    }

    void slPickerSearch(String field, String value, String expectedName) {
        page.slButton("إضافة أصناف");
        page.slType(By.cssSelector(".modal.show input[name='" + field + "']"), value);
        ready.until(d -> page.slPickerRows().stream().anyMatch(row -> row.contains(expectedName)));
    }

    void slPickerCategory(String category, String expectedName) {
        page.slButton("إضافة أصناف");
        page.slChoose("الفئة", category);
        ready.until(
                d -> {
                    var rows = page.slPickerRows();
                    return !rows.isEmpty()
                            && rows.stream().allMatch(row -> row.contains(expectedName));
                });
    }

    List<String> slPickerRows() {
        return driver.findElements(By.cssSelector(".modal.show tbody tr")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .filter(t -> !t.isBlank() && !t.contains("لا يوجد بيانات"))
                .toList();
    }

    void slClosePicker() {
        page.slClick(By.cssSelector(".modal.show button.close"));
        ready.until(
                d ->
                        d.findElements(By.cssSelector(".modal.show")).stream()
                                .noneMatch(WebElement::isDisplayed));
    }

    List<String> slAddFirstTwoExistingItems() {
        return Plugin_ERP.System_Modules.Login.DraftItemSelection.selectTwo(
                driver, ready, () -> page.slButton("إضافة أصناف"));
    }
}
