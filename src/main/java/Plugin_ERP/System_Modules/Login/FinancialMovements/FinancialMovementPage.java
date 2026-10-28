/* شرح الملف: مساعدة لتسجيل الحركات المالية وحفظها وتوزيع مبالغها على المستندات ومراجعة قائمتها.
 * فائدتها متابعة القبض والصرف والخصومات والتحويلات في خطوات مشتركة.
 */
package Plugin_ERP.System_Modules.Login.FinancialMovements;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.Util.FormInteractions;
import Plugin_ERP.Util.PropertyReader;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public class FinancialMovementPage extends Abstract {
    private final FinancialChoiceControls choiceControls;
    private final FinancialFieldValues fieldValues;
    private final FinancialRecordList records;
    private final FinancialSaveCapture saveCapture;
    private final WebDriverWait ready;
    private final FormInteractions choices;
    private String listUrl;

    public FinancialMovementPage(WebDriver driver) {
        super(driver);
        ready = new WebDriverWait(driver, Duration.ofSeconds(25));
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
        saveCapture = new FinancialSaveCapture(this, driver, ready);
        records = new FinancialRecordList(this, driver, ready);
        choiceControls = new FinancialChoiceControls(this, driver, ready, choices);
        fieldValues = new FinancialFieldValues(this, driver, ready);
    }

    @Step("فتح قائمة الحركة المالية: {route}")
    public void openList(Pages route) {
        Plugin_ERP.Util.Allur.ScreenNames.opening(route.getRoute());
        listUrl = PropertyReader.getApplicationUrl() + route.getRoute();
        driver.get(listUrl);
        driver.navigate().refresh();
        ready.until(
                d ->
                        d.findElements(By.cssSelector("button[data-action-type='new']")).stream()
                                .anyMatch(WebElement::isDisplayed));
        dismissPopup();
    }

    @Step("فتح نموذج حركة مالية جديدة")
    public void openNew() {
        String before = driver.getCurrentUrl();
        ready.until(
                d -> {
                    if (isFormVisible()) return true;
                    if (!d.getCurrentUrl().equals(before)) return false;
                    WebElement b =
                            d
                                    .findElements(
                                            By.xpath(
                                                    "//button[@data-action-type='new' and normalize-space()='جديد']"))
                                    .stream()
                                    .filter(WebElement::isDisplayed)
                                    .findFirst()
                                    .orElse(null);
                    if (b == null) return false;
                    b.click();
                    return false;
                });
        ready.until(
                d ->
                        d.findElements(By.cssSelector("input[name='value']")).stream()
                                .anyMatch(WebElement::isDisplayed));
    }

    @Step("إدخال قيمة الحركة: {amount}")
    public void amount(String amount) {
        type(By.cssSelector("input[name='value']"), amount);
    }

    @Step("إدخال ملاحظات الحركة المحددة: {note}")
    public void notes(String note) {
        type(By.cssSelector("textarea[label-text='Notes'],textarea"), note);
    }

    public String notesValue() {
        return driver.findElement(By.cssSelector("textarea")).getDomProperty("value");
    }

    @Step("اختيار {name} من {label}")
    public void choose(String label, String name) {
        choiceControls.choose(label, name);
    }

    private void chooseInput(By locator, String name) {
        choiceControls.chooseInput(locator, name);
    }

    @Step("اختيار طريقة الدفع المسجلة للاختبار")
    public void payment(String methodName) {
        choiceControls.payment(methodName);
    }

    @Step("اختيار طرف التحويل {side}: {type} / {name}")
    public void transferParty(int side, String type, String name) {
        choiceControls.transferParty(side, type, name);
    }

    @Step("اختيار نوع الطرف {side}: {type}")
    public void selectTransferType(int side, String type) {
        choiceControls.selectTransferType(side, type);
    }

    @Step("اختيار نوع الحركة: {value}")
    public void movementType(String value) {
        choiceControls.movementType(value);
    }

    @Step("مسح الحقل {label} في الموضع {index}")
    public void clearField(String label, int index) {
        choiceControls.clearField(label, index);
    }

    @Step("مسح وجهة التحويل")
    public void clearTransferDestination() {
        choiceControls.clearTransferDestination();
    }

    @Step("إدخال تاريخ خارج السنة المالية")
    public void outsideFiscalYear() {
        fieldValues.outsideFiscalYear();
    }

    public BigDecimal[] taxValues() {
        return fieldValues.taxValues();
    }

    public boolean validationFor(String field, String label) {
        return fieldValues.validationFor(field, label);
    }

    @Step("تحديد تطبيق الضريبة: {value}")
    public void taxable(boolean value) {
        fieldValues.taxable(value);
    }

    @Step("حفظ الحركة المالية")
    public void saveMovement() {
        click(By.cssSelector("button[data-action-type='save']"));
    }

    @Step("حفظ وطباعة الحركة المالية")
    public void saveAndPrintMovement() {
        click(By.cssSelector("button[data-action-type='saveAndPrint']"));
    }

    public void awaitReport() {
        utilites.waits().waitForReportReady(60);
        utilites.waits().waitForOverlayToDisappear();
    }

    public void attachReportEvidence() {
        io.qameta.allure.Allure.addAttachment(
                "Financial report failure",
                "image/png",
                new java.io.ByteArrayInputStream(
                        ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)),
                "png");
        io.qameta.allure.Allure.addAttachment(
                "Financial report text", driver.findElement(By.tagName("body")).getText());
    }

    @Step("تخصيص المبلغ لفاتورة الاختبار فقط: {invoiceCode}")
    public void allocate(String invoiceCode, String amount) {
        click(By.xpath("//button[translate(normalize-space(.),' ','')='حفظوتخصيص']"));
        WebElement row =
                ready.until(
                        d ->
                                d.findElements(By.cssSelector(".modal.show tbody tr")).stream()
                                        .filter(WebElement::isDisplayed)
                                        .filter(
                                                r ->
                                                        r
                                                                .findElements(By.cssSelector("td"))
                                                                .stream()
                                                                .anyMatch(
                                                                        c ->
                                                                                c.getText()
                                                                                        .trim()
                                                                                        .equals(
                                                                                                invoiceCode)))
                                        .findFirst()
                                        .orElse(null));
        WebElement value = row.findElement(By.cssSelector("input[type='number']"));
        ready.until(
                d -> {
                    value.click();
                    value.sendKeys(Keys.CONTROL + "a");
                    value.sendKeys(amount);
                    value.sendKeys(Keys.END);
                    value.sendKeys(Keys.TAB);
                    try {
                        return new java.math.BigDecimal(value.getDomProperty("value"))
                                        .compareTo(new java.math.BigDecimal(amount))
                                == 0;
                    } catch (NumberFormatException invalid) {
                        Plugin_ERP.Bots.LogUtils.debug(
                                "FinancialMovementPage condition unavailable: "
                                        + invalid.getClass().getSimpleName());
                        return false;
                    }
                });
        // Allocation is committed by the modal's own Save button.
        click(
                By.xpath(
                        "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='حفظ']"));
    }

    public List<String> validationMessages() {
        return fieldValues.validationMessages();
    }

    public boolean isFormVisible() {
        return driver.findElements(By.cssSelector("input[name='value']")).stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public boolean isFormPresent() {
        return !driver.findElements(By.cssSelector("input[name='value']")).isEmpty();
    }

    public void identifyMovement(String note, Object code) {
        records.identifyMovement(note, code);
    }

    @Step("إغلاق الحركة دون حفظ")
    public void closeWithoutSaving() {
        dismissPopup();
        driver.get(listUrl);
        driver.navigate().refresh();
        ready.until(d -> !isFormPresent());
    }

    public void dismissPopup() {
        ready.until(
                d -> {
                    if (d.findElements(By.cssSelector(".swal2-container")).stream()
                            .noneMatch(WebElement::isDisplayed)) return true;
                    for (WebElement b : d.findElements(By.cssSelector("button.swal2-confirm")))
                        if (b.isDisplayed()) b.click();
                    return false;
                });
    }

    private WebElement row(String note) {
        return records.row(note);
    }

    public boolean hasRow(String note) {
        return records.hasRow(note);
    }

    public void waitForRow(String note) {
        records.waitForRow(note);
    }

    @Step("تعديل الحركة المحددة بملاحظاتها: {note}")
    public void editOwned(String note) {
        records.editOwned(note);
    }

    @Step("إلغاء التخصيص السابق للسند المملوك قبل تعديل قيمته وإعادة تخصيصه")
    public void clearAllocationForEdit() {
        By amount = By.cssSelector("input[name='value']");
        if (driver.findElement(amount).isEnabled()
                && driver.findElement(amount).getDomAttribute("readonly") == null) return;
        WebElement previousDocument = driver.findElement(By.tagName("html"));
        String previousNote = notesValue();
        click(By.xpath("//button[normalize-space()='إلغاء التخصيص']"));
        // التطبيق يحفظ إلغاء التخصيص ثم يعيد تحميل الصفحة، فلا نعدل النموذج القديم.
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.stalenessOf(previousDocument));
        ready.until(d -> isFormVisible());
        ready.until(d -> previousNote.equals(notesValue()));
        ready.until(
                d -> {
                    for (WebElement button :
                            d.findElements(By.cssSelector(".swal2-popup button.swal2-confirm")))
                        if (button.isDisplayed()) {
                            button.click();
                            return false;
                        }
                    return d.findElement(amount).isEnabled()
                            && d.findElement(amount).getDomAttribute("readonly") == null;
                });
    }

    @Step("طلب حذف الحركة المحددة فقط: {note}")
    public void requestDeleteOwned(String note) {
        records.requestDeleteOwned(note);
    }

    @Step("تأكيد الحذف أو إلغاؤه: {confirm}")
    public void confirmDelete(boolean confirm) {
        records.confirmDelete(confirm);
    }

    public void waitForRemoval(String note) {
        records.waitForRemoval(note);
    }

    public boolean hasListHeaders() {
        return records.hasListHeaders();
    }

    public void waitForList() {
        records.waitForList();
    }

    @Step("تصدير القائمة بصيغة {format}")
    public void export(String format) {
        records.export(format);
    }

    public void type(By locator, String value) {
        fieldValues.type(locator, value);
    }

    void clickControl(By by) {
        click(by);
    }

    private void click(By by) {
        ready.until(
                d -> {
                    WebElement e =
                            d.findElements(by).stream()
                                    .filter(WebElement::isDisplayed)
                                    .findFirst()
                                    .orElse(null);
                    if (e == null || !e.isEnabled()) return false;
                    Plugin_ERP.Util.VueComponentAccess.execute(
                            d, "arguments[0].scrollIntoView({block:'center'})", e);
                    e.click();
                    return true;
                });
    }

    public void captureSaves(String marker) {
        saveCapture.captureSaves(marker);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> capturedSaves() {
        return saveCapture.capturedSaves();
    }

    public String captureMarker() {
        return saveCapture.captureMarker();
    }

    public List<Map<String, Object>> capturedAttempts() {
        return saveCapture.capturedAttempts();
    }

    public boolean hasSaveStarted() {
        return saveCapture.hasSaveStarted();
    }

    public void awaitSavesSettled() {
        saveCapture.awaitSavesSettled();
    }

    void clickFinancialControl(By locator) {
        click(locator);
    }
}
