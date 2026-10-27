package Plugin_ERP.System_Modules.Login.purches;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Purchase Document Form: focused operations used by the original page facade. */
final class PurchaseDocumentForm {
    private final PurchaseModulePage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;

    PurchaseDocumentForm(PurchaseModulePage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    private Object js(String script, Object... args) {
        return owner.js(script, args);
    }

    void puNotes(String note) {
        owner.puType(By.cssSelector("form textarea[label-text='Notes']"), note);
    }

    void puSave(boolean print) {
        owner.puClick(
                print
                        ? By.cssSelector("button[data-action-type='saveAndPrint']")
                        : By.xpath(
                                "//button[@data-action-type='save' and normalize-space()='حفظ']"));
    }

    boolean puSaveDisabled() {
        return driver
                .findElements(
                        By.xpath("//button[@data-action-type='save' and normalize-space()='حفظ']"))
                .stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(e -> !e.isEnabled());
    }

    boolean puHasDocumentSaveAction() {
        return driver
                .findElements(
                        By.xpath(
                                "//button[(@data-action-type='save' and normalize-space()='حفظ') or @data-action-type='saveAndPrint']"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    boolean puHasDocumentDeleteAction() {
        return driver
                .findElements(
                        By.xpath(
                                "//button[not(ancestor::tbody) and (@data-action-type='delete' or normalize-space()='حذف' or @title='حذف')] | //a[@role='menuitem' and normalize-space()='حذف']"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    void puDismiss() {
        new WebDriverWait(driver, Duration.ofSeconds(90))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class)
                .until(
                        d -> {
                            if (d.findElements(By.cssSelector(".swal2-container")).stream()
                                    .noneMatch(WebElement::isDisplayed)) return true;
                            for (WebElement e :
                                    d.findElements(By.cssSelector("button.swal2-confirm")))
                                if (e.isDisplayed()) e.click();
                            return false;
                        });
    }

    String puValidation() {
        return ready.until(
                d -> {
                    List<String> out = new ArrayList<>();
                    for (WebElement e :
                            d.findElements(
                                    By.cssSelector(
                                            ".text-danger,.invalid-feedback,.swal2-title,.swal2-html-container,.b-toast-danger .toast-body,.v-toast__item--error .v-toast__text"))) {
                        if (e.getDomProperty("textContent").isBlank()) continue;
                        owner.js(
                                "arguments[0].scrollIntoView({block:'center',inline:'center'})", e);
                        if (e.isDisplayed() && !e.getText().isBlank()) out.add(e.getText());
                    }
                    return out.isEmpty() ? null : String.join(" | ", new LinkedHashSet<>(out));
                });
    }

    Map<String, Object> puDocument() {
        return (Map<String, Object>)
                owner.js(
                        "const q=[erpVue.root()];while(q.length){const v=q.shift();q.push(...(erpVue.children(v)||[]));if(v.$data?.selectedItem?.lines&&v.$options.methods?.save)return JSON.parse(JSON.stringify(v.$data.selectedItem));}return null");
    }

    void puFormReady() {
        ready.until(
                d -> {
                    var doc = owner.puDocument();
                    return doc != null
                            && doc.get("transactionDate") instanceof String date
                            && !date.isBlank();
                });
    }

    void puAwaitOwnedLookups(Object store, Object supplier, Object agent) {
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(
                        d ->
                                Boolean.TRUE.equals(
                                        owner.js(
                                                "const q=[erpVue.root()];while(q.length){const v=q.shift();q.push(...(erpVue.children(v)||[]));if(v.$data?.selectedItem?.lines&&v.$options.methods?.save){const l=v.lookups;return Boolean(l?.stores?.some(x=>x.id===arguments[0])&&l?.suppliers?.some(x=>x.id===arguments[1])&&l?.agents?.some(x=>x.id===arguments[2]));}}return false",
                                                store,
                                                supplier,
                                                agent)));
    }

    String puDocumentDate() {
        owner.puFormReady();
        return owner.puDocument().get("transactionDate").toString().substring(0, 10);
    }

    void puDiscount(String value, boolean percentage) {
        owner.puType(
                By.cssSelector(
                        percentage
                                ? "fieldset[name='discountPercentage'] input"
                                : "fieldset[name='discountValue'] input"),
                value);
    }

    java.math.BigDecimal puUserDiscountLimit() {
        Object value =
                owner.js(
                        "const q=[erpVue.root()];while(q.length){const v=q.shift();q.push(...(erpVue.children(v)||[]));const user=v.lookups?.CurrentUser||v.lookups?.currentUser;if(user?.maxAllowedDiscount!=null)return user.maxAllowedDiscount;}return null");
        return value == null ? null : new java.math.BigDecimal(value.toString());
    }

    void puAddOtherPayment(String method, String amount) {
        owner.selectPaymentMode("PaymentMethods");
        owner.puChoose("طريقة الدفع", method);
        owner.puType(By.cssSelector("form input[name='value']:not([disabled])"), amount);
        owner.puClick(
                By.xpath(
                        "//form//*[self::legend or self::label][normalize-space()='طريقة الدفع']/ancestor::div[contains(@class,'row')][1]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
    }

    void puNew() {
        owner.puClick(By.xpath("//button[normalize-space()='جديد']"));
        owner.puDismiss();
        ready.until(d -> ((List<?>) owner.puDocument().get("lines")).isEmpty());
    }

    String puNotesValue() {
        return driver.findElements(By.cssSelector("form textarea[label-text='Notes']")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow()
                .getDomProperty("value");
    }
}
