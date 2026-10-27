package Plugin_ERP.System_Modules.Login.SpecialSales;

import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.Util.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Native controls shared by the two counter screens; independent of the normal invoice POM. */
public abstract class SpecialSalesPage extends Abstract {
    private final CounterPaymentControls recordControls;
    private final CounterDocumentLines lines;
    private final CounterSaveCapture saveCapture;
    protected final WebDriverWait ready;

    protected SpecialSalesPage(WebDriver driver) {
        super(driver);
        lines = new CounterDocumentLines(this, driver);
        saveCapture = new CounterSaveCapture(this);
        ready = new WebDriverWait(driver, Duration.ofSeconds(45));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
        recordControls = new CounterPaymentControls(this, driver, ready);
    }

    public abstract String route();

    public abstract String endpoint();

    public abstract String channel();

    protected Object js(String script, Object... args) {
        return Plugin_ERP.Util.VueComponentAccess.execute(driver, script, args);
    }

    protected String vmScript() {
        return "const queue=[erpVue.root()];let vm;while(queue.length){const v=queue.shift();if(!v)continue;queue.push(...(erpVue.children(v)||[]));if(Array.isArray(v.$data?.selectedItem?.lines)&&v.$options.methods?.save){vm=v;break;}}";
    }

    public void awaitSession() {
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                js(
                                        "return Boolean(erpVue.root()?.$store?.getters['app/currentBranch']?.id)")));
    }

    @Step("فتح شاشة الكاشير")
    public void open() {
        Plugin_ERP.Util.Allur.ScreenNames.opening(route());
        driver.get(PropertyReader.getApplicationUrl() + "/#/" + route());
        driver.navigate().refresh();
        awaitSession();
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                js(
                                        vmScript()
                                                + "return Boolean(vm&&Array.isArray(vm.selectedItem.lines))")));
        dismiss();
    }

    public void click(By locator) {
        ready.until(
                d -> {
                    for (WebElement e : d.findElements(locator)) {
                        if (!e.isEnabled()) continue;
                        js("arguments[0].scrollIntoView({block:'center',inline:'center'})", e);
                        if (e.isDisplayed()) {
                            e.click();
                            return true;
                        }
                    }
                    return false;
                });
    }

    public void text(By locator, String value) {
        ready.until(
                d -> {
                    for (WebElement e : d.findElements(locator))
                        if (e.isDisplayed() && e.isEnabled()) {
                            js("arguments[0].scrollIntoView({block:'center',inline:'center'})", e);
                            e.click();
                            e.sendKeys(Keys.CONTROL + "a");
                            e.sendKeys(value.isEmpty() ? Keys.BACK_SPACE : value);
                            for (var label :
                                    e.findElements(By.xpath("./ancestor::fieldset[1]/legend")))
                                if (label.isDisplayed()) {
                                    label.click();
                                    break;
                                }
                            return true;
                        }
                    return false;
                });
    }

    public void blur() {
        click(By.xpath("//strong[contains(normalize-space(),'الاجمالى')]"));
    }

    public void button(String label) {
        click(By.xpath("//button[normalize-space()='" + label + "']"));
    }

    @Step("اختيار {name} من {label}")
    public void choose(String label, String name) {
        By locator =
                By.xpath(
                        "//*[self::label or self::legend][normalize-space()='"
                                + label
                                + "']/following::input[contains(@class,'vs__search')][1]");
        ready.until(
                d -> {
                    for (WebElement input : d.findElements(locator))
                        if (input.isDisplayed() && input.isEnabled()) {
                            js("arguments[0].scrollIntoView({block:'center'})", input);
                            input.click();
                            input.sendKeys(Keys.CONTROL + "a");
                            input.sendKeys(name);
                            String id = input.getDomAttribute("aria-controls");
                            var options =
                                    d.findElements(
                                            By.xpath(
                                                    "//ul[@id='"
                                                            + id
                                                            + "']//li[not(@aria-disabled='true')]"));
                            for (var option : options)
                                if (option.isDisplayed()
                                        && Arrays.asList(option.getText().trim().split(" - "))
                                                .contains(name)) {
                                    option.click();
                                    return true;
                                }
                        }
                    return false;
                });
    }

    public void dismiss() {
        ready.until(
                d -> {
                    for (var e : d.findElements(By.cssSelector(".swal2-container .swal2-confirm")))
                        if (e.isDisplayed()) {
                            e.click();
                            return false;
                        }
                    return true;
                });
    }

    public String markup() {
        return driver.getPageSource();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> document() {
        return (Map<String, Object>)
                js(vmScript() + "return vm?JSON.parse(JSON.stringify(vm.selectedItem)):{};");
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> diagnostics() {
        return (Map<String, Object>)
                js(
                        vmScript()
                                + "return {document:vm?.selectedItem||{},capture:window.__specialSalesQA||{},text:document.body.innerText};");
    }

    public void capture(String marker) {
        saveCapture.capture(marker);
    }

    public String captureMarker() {
        return saveCapture.captureMarker();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> attempts() {
        return saveCapture.attempts();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> saves() {
        return saveCapture.saves();
    }

    public void settled() {
        saveCapture.settled();
    }

    protected By line(String name) {
        return lines.line(name);
    }

    @Step("تغيير كمية الصنف {name} إلى {value}")
    public void quantity(String name, String value) {
        lines.quantity(name, value);
    }

    @Step("تغيير كمية الصنف {name} بزر الزيادة أو النقصان: {plus}")
    public void changeQuantity(String name, boolean plus) {
        lines.changeQuantity(name, plus);
    }

    @Step("حذف الصنف {name} من الفاتورة")
    public void removeLine(String name) {
        lines.removeLine(name);
    }

    @Step("إدخال الخصم {value}، خصم بنسبة: {percentage}")
    public void discount(String value, boolean percentage) {
        recordControls.discount(value, percentage);
    }

    @Step("إغلاق نافذة السداد")
    public void closePayment() {
        recordControls.closePayment();
    }

    @Step("حفظ الفاتورة وطباعة التقرير")
    public void saveAndPrint() {
        recordControls.saveAndPrint();
    }

    @Step("فتح إجراء {icon} للفاتورة رقم {code}")
    public void invoiceAction(Object id, String code, String icon) {
        for (var label :
                driver.findElements(
                        By.xpath(
                                "//div[contains(@class,'modal') and contains(@class,'show')]//label[normalize-space()='الكل']"))) {
            if (label.isDisplayed()) {
                if (!Arrays.asList(label.getAttribute("class").split("\\s+")).contains("active")) {
                    click(
                            By.xpath(
                                    "//div[contains(@class,'modal') and contains(@class,'show')]//label[normalize-space()='الكل']"));
                }
                break;
            }
        }
        text(By.cssSelector(".modal.show input[name='code']"), code);
        String actionId =
                "invoice-row-" + id + (icon.equals("trash") ? "-delete-icon" : "-preview-icon");
        click(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//button[.//*[local-name()='svg' and @id='"
                                + actionId
                                + "' and contains(@class,'feather-"
                                + icon
                                + "')]]"));
    }

    @Step("إغلاق النافذة المفتوحة")
    public void closeModal() {
        for (var e : driver.findElements(By.cssSelector(".modal.show button.close")))
            if (e.isDisplayed()) {
                e.click();
                break;
            }
    }

    @Step("تأكيد الإجراء أو إلغاؤه: {yes}")
    public void confirm(boolean yes) {
        click(
                By.cssSelector(
                        yes
                                ? ".swal2-container .swal2-confirm"
                                : ".swal2-container .swal2-cancel"));
    }

    public boolean hasDialog() {
        return driver.findElements(By.cssSelector(".swal2-container,.modal.show")).stream()
                .anyMatch(WebElement::isDisplayed);
    }

    @Step("إضافة طريقة الدفع {name} بمبلغ {value}")
    public void addOtherPayment(String name, String value) {
        recordControls.addOtherPayment(name, value);
    }

    @Step("التحقق من وصول صافي الفاتورة إلى {value}")
    public void waitNet(String value) {
        lines.waitNet(value);
    }

    @Step("التحقق من وصول إجمالي الفاتورة إلى {value}")
    public void waitTotal(String value) {
        lines.waitTotal(value);
    }

    public void awaitTotals() {
        lines.awaitTotals();
    }

    public void awaitSave() {
        saveCapture.awaitSave();
    }

    public void awaitSaveCount(int count) {
        saveCapture.awaitSaveCount(count);
    }

    public void awaitInvoice(Object id) {
        ready.until(d -> id.equals(document().get("id")));
        awaitTotals();
    }

    @Step("إدخال الملاحظات: {value}")
    public void notes(String value) {
        text(
                By.cssSelector(
                        "textarea[label='Notes'],textarea[label-text='Notes'],textarea[name='notes']"),
                value);
    }

    public void closeExtraWindows(String main) {
        try {
            for (String handle : new ArrayList<>(driver.getWindowHandles()))
                if (!main.equals(handle)) {
                    driver.switchTo().window(handle);
                    driver.close();
                }
        } finally {
            driver.switchTo().window(main);
        }
    }

    public abstract void addItem(String name, String barcode, String category);

    public abstract void save();
}
