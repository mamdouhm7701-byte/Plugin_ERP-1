package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.text.Utilities;
import java.time.Duration;
import java.util.List;

public class CashPage extends AbstractForDealer {
    private final CashFormReader formReader;
    private final CashRecordActions records;
    private final CashListControls listControls;
    // Constructors
    public CashPage(WebDriver driver) {
        super(driver);
        records = new CashRecordActions(this, driver);
        listControls = new CashListControls(this, driver, this::waitForCashPageLoading);
        formReader = new CashFormReader(this, driver);
    }

    @Override
    @Step("الانتقال إلى شاشة الخزن بانتظار عناصر الشاشة")
    public void NavigationHelper(Pages route) {
        if (route != Pages.SAFES) {
            super.NavigationHelper(route);
            return;
        }
        driver.navigate().to(PropertyReader.getApplicationUrl() + route.getRoute());
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(
                        d ->
                                "complete"
                                        .equals(
                                                ((org.openqa.selenium.JavascriptExecutor) d)
                                                        .executeScript(
                                                                "return document.readyState")));
        waitForCashPageLoading();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(GridSearch));
        listControls.awaitReady();
    }

    // Locators
    private By OpenBasicData =
            By.xpath(
                    "//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");

    private By BasicData = By.xpath("//span[contains(text(),'البيانات الأساسية')]");
    private By Enteringthetreasury =
            By.xpath(
                    "//span[contains(text(),'البيانات الأساسية')]/following::span[contains(text(),'الخزن')]\n");
    private final By GridSearch = By.cssSelector("input[placeholder*='بحث']");
    private final By SafeRows = By.xpath("//tbody//tr[@role='row']");
    private final By SafeTableHeaders = By.cssSelector("table thead th");
    private final By SafeRowsWithActions =
            By.xpath(
                    "//tbody//tr[@role='row'][.//button[@data-action-type='edit'] and .//button[@data-action-type='delete']]");

    // Methods
    @Step("فتح قائمه الخزن")
    public void OpenCashPage() {
        utilites.ClickElement(BasicData);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(Enteringthetreasury);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenBasicData);
    }

    @Step("التحقق من ظهور نموذج إضافة خزنة")
    public boolean isCashCreationFormDisplayed() {
        return formReader.isCashCreationFormDisplayed();
    }

    @Override
    @Step("فتح نموذج إضافة خزنة")
    public void ClickNew() {
        waitForCashPageLoading();
        super.ClickNew();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(
                        d ->
                                isCashCreationFormDisplayed()
                                        && isVisible(
                                                By.cssSelector("button[data-action-type='save']"))
                                        && isVisible(
                                                By.xpath("//button[normalize-space()='إغلاق']")));
    }

    @Override
    @Step("حفظ نموذج الخزنة وانتظار استجابة الصفحة")
    public void ClickSaveButton() {
        waitForCashPageLoading();
        super.ClickSaveButton();
        waitForCashPageLoading();
        dismissCashSaveSuccessPopupIfShown();
        waitForCashPageLoading();
    }

    private void dismissCashSaveSuccessPopupIfShown() {
        By successPopup =
                By.xpath(
                        "//div[contains(@class,'swal2-popup') and (contains(normalize-space(.),'تم الحفظ بنجاح') or contains(normalize-space(.),'تم الحفظ'))]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(successPopup));
            WebElement confirm =
                    new WebDriverWait(driver, Duration.ofSeconds(5))
                            .until(
                                    ExpectedConditions.elementToBeClickable(
                                            By.cssSelector(
                                                    ".swal2-container.swal2-backdrop-show .swal2-confirm")));
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", confirm);
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(
                            ExpectedConditions.invisibilityOfElementLocated(
                                    By.cssSelector(".swal2-container.swal2-backdrop-show")));
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // Negative validation submits do not show a save-success popup.
        }
    }

    @Step("النقر مرتين بسرعة على حفظ سجل الاختبار")
    public void clickSaveTwiceRapidly() {
        waitForCashPageLoading();
        WebElement saveButton =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                ExpectedConditions.elementToBeClickable(
                                        By.cssSelector("button[data-action-type='save']")));
        new org.openqa.selenium.interactions.Actions(driver).doubleClick(saveButton).perform();
        waitForCashPageLoading();
        dismissCashSaveSuccessPopupIfShown();
        waitForCashPageLoading();
    }

    @Step("التحقق من حالة خيار عام")
    public boolean isSafeGeneralChecked() {
        return formReader.isSafeGeneralChecked();
    }

    @Override
    @Step("إغلاق نموذج الخزنة")
    public void ClickCloseButton() {
        waitForCashPageLoading();
        if (isCashCreationFormDisplayed()) {
            super.ClickCloseButton();
            waitForCashPageLoading();
        }
    }

    @Step("التحقق من أعمدة وجدول الخزن وأزرار الصفوف")
    public boolean isSafeListStructureDisplayed() {
        List<String> headers =
                driver.findElements(SafeTableHeaders).stream()
                        .filter(WebElement::isDisplayed)
                        .map(WebElement::getText)
                        .map(String::trim)
                        .toList();
        boolean expectedHeadersPresent =
                headers.stream().anyMatch(text -> text.contains("الكود"))
                        && headers.stream().anyMatch(text -> text.contains("اسم الخزنة"))
                        && headers.stream().anyMatch(text -> text.contains("رقم الحساب"));
        boolean rowActionsPresent =
                driver.findElements(SafeRowsWithActions).stream().anyMatch(WebElement::isDisplayed);
        return expectedHeadersPresent && rowActionsPresent;
    }

    @Step("التحقق من حقول وأزرار نافذة إضافة الخزنة")
    public String getMissingCashCreationFormControls() {
        return formReader.getMissingCashCreationFormControls();
    }

    private boolean isVisible(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    @Step("انتظار اكتمال تحميل شاشة الخزن")
    private void waitForCashPageLoading() {
        By loadingOverlays =
                By.cssSelector("div.b-overlay, div.position-absolute.bg-light.rounded-lg");
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                d.findElements(loadingOverlays).stream()
                                        .noneMatch(
                                                element -> {
                                                    try {
                                                        return element.isDisplayed();
                                                    } catch (
                                                            org.openqa.selenium
                                                                            .StaleElementReferenceException
                                                                    ignored) {
                                                        return false;
                                                    }
                                                }));
    }

    @Step("انتظار اختفاء تنبيه الخزنة")
    private void waitForCashAlertToDisappear() {
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show"))
                                        .stream()
                                        .noneMatch(WebElement::isDisplayed));
    }

    @Step("البحث في قائمة الخزن عن: {searchText}")
    public void searchSafes(String searchText) {
        records.searchSafes(searchText);
    }

    @Step("التحقق من ظهور خزنة في نتائج البحث: {safeText}")
    public boolean isSafeVisibleInSearchResults(String safeText) {
        return records.isSafeVisibleInSearchResults(safeText);
    }

    @Step("عدد الخزن المطابقة للاسم الدقيق: {safeName}")
    public int getExactSafeMatchCount(String safeName) {
        return records.getExactSafeMatchCount(safeName);
    }

    @Step("فتح الخزنة المطابقة للاسم للتعديل: {safeName}")
    public void openSafeForEditByName(String safeName) {
        records.openSafeForEditByName(safeName);
    }

    @Step("طلب حذف الخزنة التي أنشأها الاختبار: {safeName}")
    public void requestDeleteSafeByName(String safeName) {
        records.requestDeleteSafeByName(safeName);
    }

    @Step("التحقق من ظهور تأكيد حذف الخزنة")
    public boolean isSafeDeleteConfirmationDisplayed() {
        return records.isSafeDeleteConfirmationDisplayed();
    }

    @Step("تأكيد حذف سجل الاختبار فقط")
    public void confirmSafeDelete() {
        records.confirmSafeDelete();
    }

    @Step("إلغاء حذف سجل الاختبار")
    public void cancelSafeDelete() {
        records.cancelSafeDelete();
    }

    @Step("الحصول على عدد صفوف قائمة الخزن")
    public int getVisibleSafeRowCount() {
        return listControls.getVisibleSafeRowCount();
    }

    @Step("قراءة حجم صفحة قائمة الخزن المحدد")
    public int getSelectedSafePageSize() {
        return listControls.getSelectedSafePageSize();
    }

    @Step("قراءة خيار بديل لحجم صفحة الخزن")
    public int getAlternativeSafePageSize(int currentValue) {
        return listControls.getAlternativeSafePageSize(currentValue);
    }

    @Step("تغيير حجم صفحة قائمة الخزن إلى {pageSize}")
    public void selectSafePageSize(int pageSize) {
        listControls.selectSafePageSize(pageSize);
    }

    @Step("قراءة العدد الإجمالي لسجلات الخزن")
    public int getSafeTotalRowCount() {
        return listControls.getSafeTotalRowCount();
    }

    @Step("التحقق من إتاحة الانتقال للصفحة التالية")
    public boolean isSafeNextPageEnabled() {
        return listControls.isSafeNextPageEnabled();
    }

    @Step("الانتقال إلى الصفحة التالية لقائمة الخزن")
    public void goToNextSafePage() {
        listControls.goToNextSafePage();
    }

    @Step("العودة إلى الصفحة السابقة لقائمة الخزن")
    public void goToPreviousSafePage() {
        listControls.goToPreviousSafePage();
    }

    @Step("قراءة قيمة الخزنة من أول صف حسب اسم العمود: {headerName}")
    public String getFirstSafeCellByHeader(String headerName) {
        return listControls.getFirstSafeCellByHeader(headerName);
    }

    @Step("قراءة قيم عمود الخزن: {headerName}")
    public List<String> getSafeColumnValues(String headerName) {
        return listControls.getSafeColumnValues(headerName);
    }

    public List<String> sortCodes(List<String> codes, boolean ascending) {
        return listControls.sortCodes(codes, ascending);
    }

    @Step("اختيار ترتيب عمود قائمة الخزن: {headerName}")
    public void clickSafeColumnHeader(String headerName) {
        listControls.clickSafeColumnHeader(headerName);
    }

    @Step("فتح أول خزنة ظاهرة للتعديل دون حفظ")
    public void openFirstSafeForEdit() {
        WebElement row =
                driver.findElements(SafeRows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                candidate ->
                                        !candidate
                                                .findElements(
                                                        By.cssSelector(
                                                                "button[data-action-type='edit']"))
                                                .isEmpty())
                        .findFirst()
                        .orElseThrow(
                                () -> new IllegalStateException("No editable safe row is visible"));
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("input[name='arabicName']")));
    }

    @Step("قراءة قيمة الاسم العربي الحالية من النموذج")
    public String getArabicNameInputValue() {
        return formReader.getArabicNameInputValue();
    }

    @Step("قراءة قيمة الاسم الإنجليزي الحالية من النموذج")
    public String getEnglishNameInputValue() {
        return formReader.getEnglishNameInputValue();
    }

    @Step("قراءة قيمة الملاحظات الحالية")
    public String getNotesValue() {
        return formReader.getNotesValue();
    }

    @Step("قراءة الحد الأقصى المعلن لحقل الملاحظات")
    public int getNotesMaxLength() {
        return formReader.getNotesMaxLength();
    }

    @Step("قراءة كود الخزنة الحالية من النموذج")
    public String getSafeCodeInputValue() {
        return formReader.getSafeCodeInputValue();
    }

    @Step("قراءة رقم الحساب الحالي من النموذج")
    public String getSafeAccountValue() {
        return formReader.getSafeAccountValue();
    }

    @Step("تصدير قائمة الخزن بصيغة {format}")
    public void exportSafes(String format) {
        listControls.exportSafes(format);
    }
    void awaitCashLoading() { waitForCashPageLoading(); }
    void awaitCashAlert() { waitForCashAlertToDisappear(); }
    boolean recordIsVisible(By locator) { return isVisible(locator); }
    void recordWaitForCashPageLoading() { waitForCashPageLoading(); }
    void recordWaitForCashAlertToDisappear() { waitForCashAlertToDisappear(); }
}
