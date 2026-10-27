package Plugin_ERP.System_Modules.Login.Products;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.Util.FormInteractions;
import Plugin_ERP.Util.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Product-module interactions shared by the four existing Page Objects. */
public class ProductsModulePage extends Abstract {
    private final ProductValidationFeedback validationFeedback;
    private final ProductRecordList records;
    private final ProductSaveCapture saveCapture;
    protected final WebDriverWait ready;
    private final FormInteractions choices;
    private final FormInteractions inputs;
    private String listUrl;

    public ProductsModulePage(WebDriver driver) {
        super(driver);
        ready = new WebDriverWait(driver, Duration.ofSeconds(30));
        validationFeedback = new ProductValidationFeedback(driver, ready);
        ready.ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
        choices =
                new FormInteractions(
                        ready,
                        e ->
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        driver,
                                        "arguments[0].scrollIntoView({block:'center'})",
                                        e));
        inputs =
                new FormInteractions(
                        ready,
                        e -> {
                            if (e.isEnabled())
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        driver,
                                        "arguments[0].scrollIntoView({block:'center',inline:'center'})",
                                        e);
                        });
        saveCapture = new ProductSaveCapture(this, driver, ready);
        records = new ProductRecordList(this, driver, ready);
    }

    public void prAwaitSession() {
        java.util.function.Function<WebDriver, Boolean> authenticated =
                d ->
                        !d.getCurrentUrl().contains("/login")
                                && Boolean.TRUE.equals(
                                        Plugin_ERP.Util.VueComponentAccess.execute(
                                                d,
                                                "return Boolean(erpVue.root()?.$store?.getters['app/currentBranch']?.id)"));
        try {
            new WebDriverWait(driver, Duration.ofSeconds(45)).until(authenticated);
        } catch (TimeoutException first) {
            if (!driver.getCurrentUrl().contains("/login")) throw first;
            // The shared login helper can click before Vue attaches the submit handler.
            new WebDriverWait(driver, Duration.ofSeconds(60))
                    .ignoring(ElementClickInterceptedException.class)
                    .ignoring(StaleElementReferenceException.class)
                    .until(
                            d -> {
                                if (authenticated.apply(d)) return true;
                                for (WebElement b :
                                        d.findElements(
                                                By.xpath(
                                                        "//button[contains(normalize-space(),'تسجيل الدخول')]")))
                                    if (b.isDisplayed() && b.isEnabled()) {
                                        b.click();
                                        return true;
                                    }
                                return false;
                            });
            new WebDriverWait(driver, Duration.ofSeconds(45)).until(authenticated);
        }
    }

    @Step("فتح قائمة موديول الأصناف: {route}")
    public void prOpen(Pages route) {
        Plugin_ERP.Util.Allur.ScreenNames.opening(route.getRoute());
        listUrl = PropertyReader.getApplicationUrl() + route.getRoute();
        driver.get(listUrl);
        driver.navigate().refresh();
        ready.until(
                d ->
                        d.findElements(By.cssSelector("button[data-action-type='new']")).stream()
                                .anyMatch(WebElement::isDisplayed));
        prDismiss();
    }

    @Step("فتح سجل جديد")
    public void prNew() {
        String before = driver.getCurrentUrl();
        ready.until(
                d -> {
                    if (prFormVisible()) return true;
                    if (!before.equals(d.getCurrentUrl())) return false;
                    List<WebElement> buttons =
                            d.findElements(
                                    By.xpath(
                                            "//button[@data-action-type='new' and normalize-space()='جديد']"));
                    for (WebElement b : buttons)
                        if (b.isDisplayed()) {
                            b.click();
                            break;
                        }
                    return false;
                });
    }

    public boolean prFormVisible() {
        return prNameInputs().stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean prFormPresent() {
        return !prNameInputs().isEmpty();
    }

    private List<WebElement> prNameInputs() {
        return driver.findElements(
                By.cssSelector(
                        "form input[name='arabicName'],form [data-title='الاسم العربي'] input"));
    }

    protected WebElement visible(By locator) {
        return ready.until(
                d ->
                        d.findElements(locator).stream()
                                .filter(WebElement::isDisplayed)
                                .findFirst()
                                .orElse(null));
    }

    public void prType(By locator, String value) {
        inputs.type(locator, value, true, FormInteractions.InputMode.CLEAR_REPLACE);
    }

    protected void prClick(By locator) {
        inputs.click(locator);
    }

    @Step("إدخال الاسم العربي: {name}")
    public void prArabic(String name) {
        prType(
                By.cssSelector(
                        "form input[name='arabicName'],form [data-title='الاسم العربي'] input"),
                name);
    }

    @Step("إدخال الاسم الإنجليزي: {name}")
    public void prEnglish(String name) {
        prType(
                By.cssSelector(
                        "form input[name='englishName'],form [data-title='الاسم الإنجليزي'] input"),
                name);
    }

    public String prEnglishValue() {
        return visible(
                        By.cssSelector(
                                "form input[name='englishName'],form [data-title='الاسم الإنجليزي'] input"))
                .getDomProperty("value");
    }

    @Step("إدخال الملاحظات: {notes}")
    public void prNotes(String notes) {
        prType(By.cssSelector("form textarea"), notes);
    }

    @Step("إدخال {name}: {value}")
    public void prField(String name, String value) {
        prType(By.cssSelector("form input[name='" + name + "']"), value);
    }

    public String prFieldValue(String name) {
        return driver.findElements(By.cssSelector("form input[name='" + name + "']")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Visible product field missing: " + name))
                .getDomProperty("value");
    }

    /** Snapshot of visible price-field feedback; success dialogs are not validation messages. */
    public Map<String, String> prPriceValidationMessages() {
        return validationFeedback.prPriceValidationMessages();
    }

    @Step("إدخال {title}: {value}")
    public void prTitledField(String title, String value) {
        prType(By.cssSelector("form [data-title='" + title + "'] input"), value);
    }

    @Step("اختيار {name} من {label}")
    public void prChoose(String label, String name) {
        By locator =
                By.xpath(
                        "//*[self::label or self::legend][normalize-space()='"
                                + label
                                + "']/following::input[contains(@class,'vs__search')][1]");
        choices.choose(
                locator, name, FormInteractions.OptionMatch.EXACT, "Ambiguous test-owned option: ");
    }

    @Step("تغيير اختيار {label}: {selected}")
    public void prCheck(String label, boolean selected) {
        By locator =
                By.xpath(
                        "//label[normalize-space()='"
                                + label
                                + "']/preceding-sibling::input[@type='checkbox'] | //label[normalize-space()='"
                                + label
                                + "']/following::input[@type='checkbox'][1]");
        WebElement input =
                driver.findElements(locator).stream()
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Checkbox missing: " + label));
        if (input.isSelected() != selected)
            Plugin_ERP.Util.VueComponentAccess.execute(driver, "arguments[0].click()", input);
        ready.until(d -> input.isSelected() == selected);
    }

    @Step("فتح تبويب: {text}")
    public void prTab(String text) {
        prClick(By.xpath("//*[@role='tab' and normalize-space()='" + text + "']"));
    }

    @Step("الضغط على زر {text}")
    public void prButton(String text) {
        prClick(By.xpath("//button[normalize-space()='" + text + "']"));
    }

    @Step("حفظ السجل")
    public void prSave() {
        prClick(By.xpath("//form//button[@data-action-type='save' or normalize-space()='حفظ']"));
    }

    public boolean prSaveDisabled() {
        return validationFeedback.prSaveDisabled();
    }

    @Step("حفظ البيانات وطباعة التقرير")
    public void prSavePrint() {
        prButton("حفظ و طباعة");
    }

    public void prDismiss() {
        ready.until(
                d -> {
                    List<WebElement> overlays = d.findElements(By.cssSelector(".swal2-container"));
                    if (overlays.stream().noneMatch(WebElement::isDisplayed)) return true;
                    for (WebElement b : d.findElements(By.cssSelector("button.swal2-confirm")))
                        if (b.isDisplayed()) b.click();
                    return false;
                });
    }

    public void prWaitList() {
        ready.until(d -> !prFormVisible());
        prDismiss();
    }

    @Step("إغلاق النموذج دون حفظ")
    public void prDiscard() {
        prDismiss();
        driver.get(listUrl);
        driver.navigate().refresh();
        ready.until(d -> !prFormVisible());
    }

    public String prValidation() {
        return validationFeedback.prValidation();
    }

    @Step("البحث عن سجل الاختبار: {name}")
    public void prSearch(String name) {
        records.prSearch(name);
    }

    public boolean prHasRow(String name) {
        return records.prHasRow(name);
    }

    public void prWaitRow(String name) {
        records.prWaitRow(name);
    }

    @Step("تعديل سجل مملوك للاختبار: {name}")
    public void prEdit(String name) {
        records.prEdit(name);
    }

    @Step("طلب حذف سجل مملوك للاختبار: {name}")
    public void prDelete(String name) {
        records.prDelete(name);
    }

    private void prRowAction(String name, String action) {
        records.prRowAction(name, action);
    }

    @Step("تأكيد الإجراء أو إلغاؤه: {confirm}")
    public void prConfirm(boolean confirm) {
        prClick(By.cssSelector(confirm ? "button.swal2-confirm" : "button.swal2-cancel"));
        ready.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".swal2-container")));
    }

    @Step("تصدير القائمة بصيغة {format}")
    public void prExport(String format) {
        prClick(
                By.cssSelector(
                        "button:has(img[src*='"
                                + (format.equals("pdf") ? "pdf." : "xls.")
                                + "'])"));
    }

    public List<String> prHeaders() {
        return driver.findElements(By.cssSelector("thead th")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .toList();
    }

    public void prCapture(String marker) {
        saveCapture.prCapture(marker);
    }

    public String prCaptureMarker() {
        return saveCapture.prCaptureMarker();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> prSaves() {
        return saveCapture.prSaves();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> prAttempts() {
        return saveCapture.prAttempts();
    }

    public void prSettled() {
        saveCapture.prSettled();
    }

    public String prSaveErrors() {
        return saveCapture.prSaveErrors();
    }

    public void prAwaitReport() {
        utilites.waits().waitForReportReady(60);
    }

    @Step("اختيار الصنف من النافذة: {name}")
    public void prPickItem(String name) {
        prType(
                By.cssSelector(
                        ".modal.show input#arabicName,.modal.show input[placeholder*='بحث']"),
                name);
        prClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//tr[.//td[normalize-space()='"
                                + name
                                + "']]//button[contains(@class,'btn-outline-primary')]"));
    }

    @Step("إغلاق نافذة اختيار الصنف")
    public void prClosePicker() {
        prClick(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='إغلاق']"));
        ready.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal.show")));
    }

    public int prFormRowsContaining(String name) {
        return (int)
                driver.findElements(By.cssSelector("form tbody tr")).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(r -> r.getText().contains(name))
                        .count();
    }

    void clickRecordControl(By locator) {
        prClick(locator);
    }

    List<WebElement> recordNameInputs() {
        return prNameInputs();
    }
}
