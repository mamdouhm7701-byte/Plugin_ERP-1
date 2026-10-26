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

    private Object js(String script, Object... args) {
        return owner.js(script, args);
    }

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
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned item did not appear in name autocomplete. "
                            + smItemCacheDiagnostics(name),
                    e);
        }
        try {
            owner.ready.until(d -> owner.smLineCount(name) > 0);
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Owned item suggestion was clicked but its document line did not appear. "
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
                "const raw=sessionStorage.getItem('cachedArray');let rows,cacheInvalid=false;"
                        + "try{rows=JSON.parse(raw)}catch(e){cacheInvalid=true}"
                        + "const shown=e=>Boolean(e&&e.getClientRects().length&&getComputedStyle(e).visibility!=='hidden');"
                        + "const input=Array.from(document.querySelectorAll('form input#autosuggest__input')).find(shown);"
                        + "const labels=Array.from(document.querySelectorAll('form label,form legend'));"
                        + "const label=labels.find(e=>shown(e)&&['المخزن','من مخزن'].includes(e.textContent.trim()));"
                        + "let selectedStoreLabel=null;"
                        + "if(label){const dropdown=document.evaluate(\"following::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]\",label,null,XPathResult.FIRST_ORDERED_NODE_TYPE,null).singleNodeValue;"
                        + "selectedStoreLabel=dropdown?.querySelector('.vs__selected')?.textContent?.trim()||null}"
                        + "const root=document.querySelector('#app')?.__vue__;const queue=root?[root]:[];const seen=new Set();let vmStoreId=null;"
                        + "while(queue.length&&seen.size<2000){const vm=queue.shift();if(!vm||seen.has(vm))continue;seen.add(vm);"
                        + "const el=vm.$el;const ownsInput=input&&el&&typeof el.contains==='function'&&el.contains(input);"
                        + "if(ownsInput&&vm.$data?.inputProps?.id==='autosuggest__input'&&vm.$props&&Object.prototype.hasOwnProperty.call(vm.$props,'storeId')){vmStoreId=vm.$props.storeId;break}"
                        + "queue.push(...(vm.$children||[]))}"
                        + "return {cachePresent:raw!==null,cacheCount:Array.isArray(rows)?rows.length:null,"
                        + "ownedItemCached:Array.isArray(rows)&&rows.some(r=>r.arabicName===arguments[0]),"
                        + "lookups:window.__storeQA?.lookups||[],...(cacheInvalid?{cacheInvalid:true}:{}),"
                        + "typedName:input?.value||null,defaultStoreId:root?.$store?.getters?.['app/currentBranch']?.defaultStoreId||null,"
                        + "selectedStoreLabel,vmStoreId}",
                name);
    }

    void smAddPicker(String name) {
        owner.smButton("إضافة أصناف");
        owner.smType(By.cssSelector(".modal.show input[label-text='arabicName']"), name);
        owner.ready.until(
                d -> {
                    d.findElement(By.cssSelector(".modal.show input[label-text='arabicName']"))
                            .sendKeys(Keys.ENTER);
                    return true;
                });
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
