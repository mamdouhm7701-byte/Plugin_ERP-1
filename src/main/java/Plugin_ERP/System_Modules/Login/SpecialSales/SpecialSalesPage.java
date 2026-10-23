package Plugin_ERP.System_Modules.Login.SpecialSales;

import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.Utiel.PropertyReader;

import io.qameta.allure.Step;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

/** Native controls shared by the two counter screens; independent of the normal invoice POM. */
public abstract class SpecialSalesPage extends Abstract {
    protected final WebDriverWait ready;

    protected SpecialSalesPage(WebDriver driver) {
        super(driver);
        ready = new WebDriverWait(driver, Duration.ofSeconds(45));
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
    }

    public abstract String route();

    public abstract String endpoint();

    public abstract String channel();

    protected Object js(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    protected String vmScript() {
        return "const queue=[document.querySelector('#app')?.__vue__];let"
                   + " vm;while(queue.length){const"
                   + " v=queue.shift();if(!v)continue;queue.push(...(v.$children||[]));if(Array.isArray(v.$data?.selectedItem?.lines)&&v.$options.methods?.save){vm=v;break;}}";
    }

    public void awaitSession() {
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                js(
                                        "return"
                                            + " Boolean(document.querySelector('#app')?.__vue__?.$store?.getters['app/currentBranch']?.id)")));
    }

    @Step("فتح شاشة الكاشير")
    public void open() {
        Plugin_ERP.Utiel.Allur.ScreenNames.opening(route());
        driver.get(PropertyReader.getApplicationUrl() + "/#/" + route());
        driver.navigate().refresh();
        awaitSession();
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                js(
                                        vmScript()
                                                + "return"
                                                + " Boolean(vm&&Array.isArray(vm.selectedItem.lines))")));
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
                                + "return"
                                + " {document:vm?.selectedItem||{},capture:window.__specialSalesQA||{},text:document.body.innerText};");
    }

    public void capture(String marker) {
        js(
                """
                const root=document.querySelector('#app').__vue__,store=root.$store;
                window.__specialSalesQA={marker:arguments[0],endpoint:arguments[1],attempts:[],saves:[],errors:[],pending:0};
                if(store.__specialSalesCaptureInstalled)return;store.__specialSalesCaptureInstalled=true;const dispatch=store.dispatch.bind(store);
                store.dispatch=function(action,payload,...rest){const c=window.__specialSalesQA;
                  const owned=c&&payload?.url===c.endpoint&&['app/create','app/update'].includes(action)&&payload?.data?.notes?.startsWith(c.marker);
                  const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;
                  if(owned){c.pending++;c.attempts.push({endpoint:payload.url,values:sent});}
                  return dispatch(action,payload,...rest).then(result=>{if(owned){c.pending--;const entity=result?.data||result;c.saves.push({endpoint:payload.url,id:sent.id||entity?.id||(typeof entity==='string'?entity:null),values:sent});}return result;})
                    .catch(error=>{if(owned){c.pending--;c.errors.push(JSON.stringify(error?.response?.data||error?.data||{message:String(error)}));}throw error;});};
                """,
                marker,
                endpoint());
    }

    public String captureMarker() {
        return (String) js("return window.__specialSalesQA?.marker||null");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> attempts() {
        return (List<Map<String, Object>>) js("return window.__specialSalesQA?.attempts||[]");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> saves() {
        return (List<Map<String, Object>>) js("return window.__specialSalesQA?.saves||[]");
    }

    public void settled() {
        ready.until(
                d -> !Boolean.TRUE.equals(js("return (window.__specialSalesQA?.pending||0)>0")));
    }

    protected By line(String name) {
        return By.xpath(
                "//section[@id='lines']//div[contains(concat(' ',normalize-space(@class),' '),'"
                        + " ecommerce-card ')][.//*[normalize-space(text())='"
                        + name
                        + "']]");
    }

    @Step("تغيير كمية الصنف {name} إلى {value}")
    public void quantity(String name, String value) {
        var card =
                ready.until(
                        d ->
                                d.findElements(line(name)).stream()
                                        .filter(WebElement::isDisplayed)
                                        .findFirst()
                                        .orElse(null));
        var input = card.findElement(By.cssSelector("input[placeholder='الكمية']"));
        input.click();
        input.sendKeys(Keys.CONTROL + "a");
        input.sendKeys(value);
        blur();
    }

    @Step("تغيير كمية الصنف {name} بزر الزيادة أو النقصان: {plus}")
    public void changeQuantity(String name, boolean plus) {
        var card = driver.findElement(line(name));
        card.findElement(
                        By.xpath(
                                ".//div[contains(@class,'quantity-input')]//button[.//*[local-name()='svg'"
                                    + " and contains(@class,'feather-"
                                        + (plus ? "plus" : "minus")
                                        + "')]]"))
                .click();
    }

    @Step("حذف الصنف {name} من الفاتورة")
    public void removeLine(String name) {
        var card = driver.findElement(line(name));
        card.findElement(
                        By.xpath(
                                ".//button[.//*[local-name()='svg' and"
                                        + " contains(@class,'feather-x')]]"))
                .click();
    }

    @Step("إدخال الخصم {value}، خصم بنسبة: {percentage}")
    public void discount(String value, boolean percentage) {
        text(
                percentage
                        ? By.cssSelector("input[placeholder='النسبة']")
                        : By.cssSelector("input[name='discountValue']"),
                value);
    }

    @Step("إغلاق نافذة السداد")
    public void closePayment() {
        for (var e : driver.findElements(By.cssSelector(".b-sidebar-backdrop")))
            if (e.isDisplayed()) {
                e.click();
                break;
            }
    }

    @Step("حفظ الفاتورة وطباعة التقرير")
    public void saveAndPrint() {
        By sidebar =
                By.xpath(
                        "//div[@id='sidebar-invoice-add-payment']//button[normalize-space()='حفظ و"
                                + " طباعة']");
        if (driver.findElements(sidebar).stream().anyMatch(WebElement::isDisplayed)) click(sidebar);
        else button("حفظ و طباعة");
    }

    @Step("فتح إجراء {icon} للفاتورة رقم {code}")
    public void invoiceAction(Object id, String code, String icon) {
        for (var label :
                driver.findElements(
                        By.xpath(
                                "//div[contains(@class,'modal') and"
                                    + " contains(@class,'show')]//label[normalize-space()='الكل']"))) {
            if (label.isDisplayed()) {
                if (!Arrays.asList(label.getAttribute("class").split("\\s+")).contains("active")) {
                    click(
                            By.xpath(
                                    "//div[contains(@class,'modal') and"
                                        + " contains(@class,'show')]//label[normalize-space()='الكل']"));
                }
                break;
            }
        }
        text(By.cssSelector(".modal.show input[name='code']"), code);
        String actionId =
                "invoice-row-" + id + (icon.equals("trash") ? "-delete-icon" : "-preview-icon");
        click(
                By.xpath(
                        "//div[contains(@class,'modal') and"
                            + " contains(@class,'show')]//button[.//*[local-name()='svg' and @id='"
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
        int count = ((List<?>) document().get("payments")).size();
        click(By.xpath("//label[normalize-space()='آخرى']"));
        choose("طريقة الدفع", name);
        text(By.cssSelector("input[name='value']:not([disabled])"), value);
        click(
                By.xpath(
                        "//fieldset[@label-text='paymentWay']/ancestor::div[contains(concat('"
                                + " ',normalize-space(@class),' '),' row"
                                + " ')][1]//button[.//*[local-name()='svg' and"
                                + " contains(@class,'feather-plus')]]"));
        ready.until(d -> ((List<?>) document().get("payments")).size() == count + 1);
    }

    @Step("التحقق من وصول صافي الفاتورة إلى {value}")
    public void waitNet(String value) {
        try {
            ready.until(d -> new java.math.BigDecimal(document().get("net").toString())
                    .compareTo(new java.math.BigDecimal(value)) == 0);
        } catch (TimeoutException failure) {
            throw new AssertionError("صافي الفاتورة غير مطابق: المتوقع " + value
                    + "، الفعلي " + document().get("net"), failure);
        }
    }

    @Step("التحقق من وصول إجمالي الفاتورة إلى {value}")
    public void waitTotal(String value) {
        try {
            ready.until(d -> new java.math.BigDecimal(document().get("total").toString())
                    .compareTo(new java.math.BigDecimal(value)) == 0);
        } catch (TimeoutException failure) {
            throw new AssertionError("إجمالي الفاتورة غير مطابق: المتوقع " + value
                    + "، الفعلي " + document().get("total"), failure);
        }
    }

    public void awaitTotals() {
        try {
            ready.until(
                    d ->
                            Boolean.TRUE.equals(
                                    js(
                                            vmScript()
                                                    + "return"
                                                    + " Math.abs(Number(vm.selectedItem.total)-(vm.selectedItem.lines||[]).reduce((sum,line)=>sum+Number(line.total||0),0))<0.00001;")));
        } catch (TimeoutException failure) {
            var value = document();
            var sum =
                    ((List<?>) value.get("lines"))
                            .stream()
                                    .map(
                                            row ->
                                                    new java.math.BigDecimal(
                                                            ((Map<?, ?>) row)
                                                                    .get("total")
                                                                    .toString()))
                                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            throw new AssertionError(
                    "إجمالي الفاتورة لا يطابق مجموع البنود: المتوقع " + sum + "، الفعلي " + value.get("total"),
                    failure);
        }
    }

    public void awaitSave() {
        ready.until(d -> !saves().isEmpty());
        settled();
    }

    public void awaitSaveCount(int count) {
        ready.until(d -> saves().size() >= count);
        settled();
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
