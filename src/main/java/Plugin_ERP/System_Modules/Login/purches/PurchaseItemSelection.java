/* شرح الملف: مساعدة لاختيار أصناف الشراء بالاسم أو الباركود أو نافذة الأصناف. فائدتها إضافة الصنف
 * المطلوب للمستند ومراجعة نتائج البحث.
 */
package Plugin_ERP.System_Modules.Login.purches;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class PurchaseItemSelection {
    private final PurchaseModulePage page;
    private final WebDriver driver;
    private final WebDriverWait ready;

    PurchaseItemSelection(PurchaseModulePage page, WebDriver driver, WebDriverWait ready) {
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

    void puWaitOwnedItemLookup(String name) {
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                js(
                                        "try{return (window.__purchaseQA?.lookups||[]).some(l=>(l.items||[]).some(i=>i.arabicName===arguments[0]))||performance.getEntriesByType('resource').some(r=>{try{const u=new URL(r.name);return /\\/items$/i.test(u.pathname)&&u.searchParams.get('storeId')===String(window.__purchaseQA?.storeId)&&!u.searchParams.has('barcode')}catch(e){return false}})}catch(e){return false}",
                                        name)));
    }

    void puAddName(String name) {
        radio("name");
        page.puType(By.cssSelector("form input#autosuggest__input"), name, false);
        try {
            page.puClick(
                    By.xpath(
                            "//div[@id='autosuggest-autosuggest__results']//li[contains(normalize-space(),'"
                                    + name
                                    + "')] | //span[contains(@class,'my-suggestion-item') and contains(normalize-space(),'"
                                    + name
                                    + "')]"));
            ready.until(d -> page.puLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned item did not appear in name autocomplete. "
                            + puItemCacheDiagnostics(name),
                    e);
        }
    }

    void puAddBarcode(String barcode, String name) {
        page.puAddBarcodeImmediately(barcode, name);
    }

    void puAddBarcodeImmediately(String barcode, String name) {
        radio("barcode");
        page.puType(
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
            ready.until(d -> page.puLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned barcode did not add its item. " + puItemCacheDiagnostics(name), e);
        }
    }

    boolean puOwnedItemCached(String name) {
        return Boolean.TRUE.equals(
                js(
                        "try{return JSON.parse(sessionStorage.getItem('cachedArray')||'[]').some(i=>(i.arabicName||i.itemArabicName)===arguments[0])}catch(e){return false}",
                        name));
    }

    Object puItemCacheDiagnostics(String name) {
        return js(
                "const raw=sessionStorage.getItem('cachedArray');let rows;try{rows=JSON.parse(raw)}catch(e){return {cacheInvalid:true}}return {cachePresent:raw!==null,cacheCount:Array.isArray(rows)?rows.length:null,ownedItemCached:Array.isArray(rows)&&rows.some(r=>r.arabicName===arguments[0]),lookups:window.__purchaseQA?.lookups||[]}",
                name);
    }

    void puAddPicker(String name) {
        page.puButton("إضافة أصناف");
        page.puType(By.cssSelector(".modal.show input[label-text='arabicName']"), name);
        page.puClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
        page.puClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='حفظ']"));
        ready.until(
                d ->
                        page.puLineCount(name) > 0
                                && d.findElements(By.cssSelector(".modal.show")).stream()
                                        .noneMatch(WebElement::isDisplayed));
    }

    void puPickerSearch(String field, String value, String expectedName) {
        page.puButton("إضافة أصناف");
        page.puType(By.cssSelector(".modal.show input[name='" + field + "']"), value);
        ready.until(d -> page.puPickerRows().stream().anyMatch(row -> row.contains(expectedName)));
    }

    void puPickerCategory(String category, String expectedName) {
        page.puButton("إضافة أصناف");
        page.puChoose("الفئة", category);
        ready.until(
                d -> {
                    var rows = page.puPickerRows();
                    return !rows.isEmpty()
                            && rows.stream().allMatch(row -> row.contains(expectedName));
                });
    }

    List<String> puPickerRows() {
        return driver.findElements(By.cssSelector(".modal.show tbody tr")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .filter(t -> !t.isBlank() && !t.contains("لا يوجد بيانات"))
                .toList();
    }

    void puClosePicker() {
        page.puClick(By.cssSelector(".modal.show button.close"));
        ready.until(
                d ->
                        d.findElements(By.cssSelector(".modal.show")).stream()
                                .noneMatch(WebElement::isDisplayed));
    }
}
