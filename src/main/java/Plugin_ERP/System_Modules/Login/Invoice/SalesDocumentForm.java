package Plugin_ERP.System_Modules.Login.Invoice;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Sales Document Form: focused operations used by the original page facade. */
final class SalesDocumentForm {
    private final SalesModulePage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;

    SalesDocumentForm(SalesModulePage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    private Object js(String script, Object... args) {
        return owner.js(script, args);
    }

    void slNotes(String note) {
        owner.slType(By.cssSelector("form textarea[label-text='Notes']"), note);
    }

    void slSave(boolean print) {
        owner.slClick(
                print
                        ? By.cssSelector("button[data-action-type='saveAndPrint']")
                        : By.xpath(
                                "//button[@data-action-type='save' and normalize-space()='حفظ']"));
    }

    boolean slSaveDisabled() {
        return driver
                .findElements(
                        By.xpath("//button[@data-action-type='save' and normalize-space()='حفظ']"))
                .stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(e -> !e.isEnabled());
    }

    boolean slHasDocumentSaveAction() {
        return driver
                .findElements(
                        By.xpath(
                                "//button[(@data-action-type='save' and normalize-space()='حفظ') or @data-action-type='saveAndPrint']"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    boolean slHasDocumentDeleteAction() {
        return driver
                .findElements(
                        By.xpath(
                                "//button[not(ancestor::tbody) and (@data-action-type='delete' or normalize-space()='حذف' or @title='حذف')] | //a[@role='menuitem' and normalize-space()='حذف']"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    void slDismiss() {
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

    String slValidation() {
        return ready.until(
                d -> {
                    List<String> out = new ArrayList<>();
                    for (WebElement e :
                            d.findElements(
                                    By.cssSelector(
                                            "small.text-danger,.invalid-feedback,.swal2-title,.swal2-html-container,.b-toast-danger .toast-body,.v-toast__item--error .v-toast__text"))) {
                        if (e.getDomProperty("textContent").isBlank()) continue;
                        owner.js(
                                "arguments[0].scrollIntoView({block:'center',inline:'center'})", e);
                        if (e.isDisplayed() && !e.getText().isBlank()) out.add(e.getText());
                    }
                    return out.isEmpty() ? null : String.join(" | ", new LinkedHashSet<>(out));
                });
    }

    Map<String, Object> slDocument() {
        return (Map<String, Object>)
                owner.js(
                        "const q=[erpVue.root()];while(q.length){const v=q.shift();q.push(...(erpVue.children(v)||[]));if(v.$data?.selectedItem?.lines&&v.$options.methods?.save)return JSON.parse(JSON.stringify(v.$data.selectedItem));}return null");
    }

    void slFormReady() {
        ready.until(
                d -> {
                    var doc = owner.slDocument();
                    return doc != null
                            && doc.get("transactionDate") instanceof String date
                            && !date.isBlank();
                });
    }

    String slDocumentDate() {
        owner.slFormReady();
        return owner.slDocument().get("transactionDate").toString().substring(0, 10);
    }

    void slDiscount(String value, boolean percentage) {
        owner.slType(
                By.cssSelector(
                        percentage
                                ? "fieldset[name='discountPercentage'] input"
                                : "fieldset[name='discountValue'] input"),
                value);
    }

    void slAddOtherPayment(String method, String amount) {
        owner.selectPaymentMode("PaymentMethods");
        owner.slChoose("طريقة الدفع", method);
        owner.slType(By.cssSelector("form input[name='value']:not([disabled])"), amount);
        owner.slClick(
                By.xpath(
                        "//form//*[self::legend or self::label][normalize-space()='طريقة الدفع']/ancestor::div[contains(@class,'row')][1]//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"));
    }

    void slNew() {
        owner.slClick(By.xpath("//button[normalize-space()='جديد']"));
        owner.slDismiss();
        ready.until(d -> ((List<?>) owner.slDocument().get("lines")).isEmpty());
    }

    String slNotesValue() {
        return driver.findElements(By.cssSelector("form textarea[label-text='Notes']")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow()
                .getDomProperty("value");
    }
}
