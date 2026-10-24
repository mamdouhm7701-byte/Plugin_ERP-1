package Plugin_ERP.System_Modules.Login.StoreModel;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/** Warehouse item selection by name, barcode or picker. */
final class StoreItemSelection {
    private final StoreModulePage owner;
    StoreItemSelection(StoreModulePage owner) {
        this.owner = owner;
    }
    private Object js(String script, Object... args) { return owner.js(script, args); }

    void radio(String value) {
        owner.ready.until(
                d -> {
                    for (WebElement input :
                            d.findElements(
                                    By.cssSelector(
                                            "form input[type='radio'][value='" + value + "']"))) {
                        if (!input.isSelected()) {
                            WebElement label = input.findElement(By.xpath("ancestor::label[1]"));
                            owner.js(
                                    "arguments[0].scrollIntoView({block:'center',inline:'center'})",
                                    label);
                            label.click();
                        }
                        return input.isSelected();
                    }
                    return false;
                });
    }

    void smAddName(String name) {
        radio("name");
        owner.smType(By.cssSelector("form input#autosuggest__input"), name, false);
        try {
            owner.smClick(
                    By.xpath(
                            "//div[@id='autosuggest-autosuggest__results']//li[contains(normalize-space(),'"
                                    + name
                                    + "')] | //span[contains(@class,'my-suggestion-item') and contains(normalize-space(),'"
                                    + name
                                    + "')]"));
            owner.ready.until(d -> owner.smLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned item did not appear in name autocomplete. "
                            + smItemCacheDiagnostics(name),
                    e);
        }
    }

    void smAddBarcode(String barcode, String name) {
        radio("barcode");
        owner.smType(
                By.cssSelector("form input[placeholder='بحث ....']:not(#autosuggest__input)"),
                barcode,
                false);
        owner.ready.until(
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
            owner.ready.until(d -> owner.smLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned barcode did not add its item. " + smItemCacheDiagnostics(name), e);
        }
    }

    Object smItemCacheDiagnostics(String name) {
        return owner.js(
                "const raw=sessionStorage.getItem('cachedArray');let rows;try{rows=JSON.parse(raw)}catch(e){return {cacheInvalid:true}}return {cachePresent:raw!==null,cacheCount:Array.isArray(rows)?rows.length:null,ownedItemCached:Array.isArray(rows)&&rows.some(r=>r.arabicName===arguments[0]),lookups:window.__storeQA?.lookups||[]}",
                name);
    }

    void smAddPicker(String name) {
        owner.smButton("إضافة أصناف");
        owner.smType(By.cssSelector(".modal.show input[label-text='arabicName']"), name);
        owner.smClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
        owner.smClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='حفظ']"));
        owner.ready.until(d -> owner.smLineCount(name) > 0);
    }
}
