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
    //Constructors
    public CashPage
    (WebDriver driver) {
        super(driver);
    }

    @Override
    @Step("الانتقال إلى شاشة الخزن بانتظار عناصر الشاشة")
    public void NavigationHelper(Pages route) {
        if (route != Pages.SAFES) {
            super.NavigationHelper(route);
            return;
        }
        driver.navigate().to(PropertyReader.getApplicationUrl() + route.getRoute());
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(d ->
                "complete".equals(((org.openqa.selenium.JavascriptExecutor) d)
                        .executeScript("return document.readyState")));
        waitForCashPageLoading();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(GridSearch));
    }

    //Locators
    private By OpenBasicData = By.xpath("//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");

    private By BasicData = By.xpath("//span[contains(text(),'البيانات الأساسية')]");
    private By Enteringthetreasury = By.xpath("//span[contains(text(),'البيانات الأساسية')]/following::span[contains(text(),'الخزن')]\n");
    private final By GridSearch = By.cssSelector("input[placeholder*='بحث']");
    private final By SafeRows = By.xpath("//tbody//tr[@role='row']");
    private final By SafeTableHeaders = By.cssSelector("table thead th");
    private final By SafeRowsWithActions = By.xpath(
            "//tbody//tr[@role='row'][.//button[@data-action-type='edit'] and .//button[@data-action-type='delete']]"
    );
    private final By SafePageSizeSelect = By.cssSelector("select");

    //Methods
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
        return isVisible(By.cssSelector("input[name='arabicName']"));
    }

    @Override
    @Step("فتح نموذج إضافة خزنة")
    public void ClickNew() {
        waitForCashPageLoading();
        super.ClickNew();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> isCashCreationFormDisplayed()
                        && isVisible(By.cssSelector("button[data-action-type='save']"))
                        && isVisible(By.xpath("//button[normalize-space()='إغلاق']")));
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
        By successPopup = By.xpath(
                "//div[contains(@class,'swal2-popup') and (contains(normalize-space(.),'تم الحفظ بنجاح') or contains(normalize-space(.),'تم الحفظ'))]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(successPopup));
            WebElement confirm = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm")));
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", confirm);
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.invisibilityOfElementLocated(
                            By.cssSelector(".swal2-container.swal2-backdrop-show")));
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // Negative validation submits do not show a save-success popup.
        }
    }

    @Step("النقر مرتين بسرعة على حفظ سجل الاختبار")
    public void clickSaveTwiceRapidly() {
        waitForCashPageLoading();
        WebElement saveButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[data-action-type='save']")));
        new org.openqa.selenium.interactions.Actions(driver).doubleClick(saveButton).perform();
        waitForCashPageLoading();
        dismissCashSaveSuccessPopupIfShown();
        waitForCashPageLoading();
    }

    @Step("التحقق من حالة خيار عام")
    public boolean isSafeGeneralChecked() {
        String generalText = "\u0639\u0627\u0645";
        String[] locators = {
                "//legend[contains(normalize-space(),'" + generalText + "')]/following-sibling::div//input[@type='checkbox'][1]",
                "//label[contains(normalize-space(),'" + generalText + "')]/following::input[@type='checkbox'][1]",
                "(//*[self::div or self::span or self::p][contains(normalize-space(),'" + generalText + "')]/following::input[@type='checkbox'])[1]",
                "(//input[@type='checkbox'][preceding::*[contains(normalize-space(),'" + generalText + "')]])[1]",
                "(//input[@type='checkbox'][following::*[contains(normalize-space(),'" + generalText + "')]])[1]",
                "(//*[contains(normalize-space(),'" + generalText + "')]/descendant::input[@type='checkbox'])[1]"
        };
        for (String locator : locators) {
            List<WebElement> inputs = driver.findElements(By.xpath(locator));
            if (!inputs.isEmpty()) return inputs.get(0).isSelected();
        }
        throw new IllegalStateException("Safe general checkbox input was not found");
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
        List<String> headers = driver.findElements(SafeTableHeaders).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .map(String::trim)
                .toList();
        boolean expectedHeadersPresent = headers.stream().anyMatch(text -> text.contains("الكود"))
                && headers.stream().anyMatch(text -> text.contains("اسم الخزنة"))
                && headers.stream().anyMatch(text -> text.contains("رقم الحساب"));
        boolean rowActionsPresent = driver.findElements(SafeRowsWithActions).stream()
                .anyMatch(WebElement::isDisplayed);
        return expectedHeadersPresent && rowActionsPresent;
    }

    @Step("التحقق من حقول وأزرار نافذة إضافة الخزنة")
    public String getMissingCashCreationFormControls() {
        java.util.List<String> missing = new java.util.ArrayList<>();
        if (!isVisible(By.xpath("//*[self::label or self::legend][normalize-space()='الكود']/following::input[1]"))) missing.add("الكود");
        if (!isVisible(By.cssSelector("input[name='arabicName']"))) missing.add("الاسم العربي");
        if (!isVisible(By.cssSelector("input[name='englishName']"))) missing.add("الاسم الإنجليزي");
        if (!isVisible(By.xpath("//label[normalize-space()='رقم الحساب']/following::div[contains(@class,'vs__dropdown-toggle')][1]"))) missing.add("رقم الحساب");
        By generalLabel = By.xpath("//label[contains(normalize-space(),'عام')]");
        By generalCheckbox = By.xpath("//label[contains(normalize-space(),'عام')]/following::input[@type='checkbox'][1]");
        if (!isVisible(generalLabel) || driver.findElements(generalCheckbox).isEmpty()) missing.add("عام");
        if (!isVisible(By.xpath("//textarea[contains(@class,'form-control') and @label='Notes']"))) missing.add("الملاحظات");
        if (!isVisible(By.cssSelector("button[data-action-type='save']"))) missing.add("حفظ");
        if (!isVisible(By.xpath("//button[normalize-space()='إغلاق']"))) missing.add("إغلاق");
        return String.join("، ", missing);
    }

    private boolean isVisible(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    @Step("انتظار اكتمال تحميل شاشة الخزن")
    private void waitForCashPageLoading() {
        By loadingOverlays = By.cssSelector(
                "div.b-overlay, div.position-absolute.bg-light.rounded-lg");
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> d.findElements(loadingOverlays).stream().noneMatch(element -> {
                    try {
                        return element.isDisplayed();
                    } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                        return false;
                    }
                }));
    }

    @Step("انتظار اختفاء تنبيه الخزنة")
    private void waitForCashAlertToDisappear() {
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(d -> d.findElements(By.cssSelector(".swal2-container.swal2-backdrop-show"))
                        .stream().noneMatch(WebElement::isDisplayed));
    }

    @Step("البحث في قائمة الخزن عن: {searchText}")
    public void searchSafes(String searchText) {
        waitForCashPageLoading();
        waitForCashAlertToDisappear();
        WebElement search = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(GridSearch));
        search.clear();
        if (searchText != null && !searchText.isEmpty()) {
            search.sendKeys(searchText);
        }
        waitForCashPageLoading();
    }

    @Step("التحقق من ظهور خزنة في نتائج البحث: {safeText}")
    public boolean isSafeVisibleInSearchResults(String safeText) {
        return driver.findElements(SafeRows).stream()
                .anyMatch(row -> row.getText().contains(safeText));
    }

    @Step("عدد الخزن المطابقة للاسم الدقيق: {safeName}")
    public int getExactSafeMatchCount(String safeName) {
        return (int) driver.findElements(SafeRows).stream()
                .filter(WebElement::isDisplayed)
                .filter(row -> row.getText().contains(safeName))
                .count();
    }

    @Step("فتح الخزنة المطابقة للاسم للتعديل: {safeName}")
    public void openSafeForEditByName(String safeName) {
        waitForCashPageLoading();
        WebElement row = findSafeRowByName(safeName);
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='arabicName']")));
    }

    @Step("طلب حذف الخزنة التي أنشأها الاختبار: {safeName}")
    public void requestDeleteSafeByName(String safeName) {
        waitForCashPageLoading();
        findSafeRowByName(safeName).findElement(By.cssSelector("button[data-action-type='delete']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".swal2-popup")));
    }

    @Step("التحقق من ظهور تأكيد حذف الخزنة")
    public boolean isSafeDeleteConfirmationDisplayed() {
        boolean yesButtonVisible = driver.findElements(By.cssSelector(
                        ".swal2-container.swal2-backdrop-show .swal2-confirm")).stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(button -> button.getText().trim().contains("نعم"));
        boolean cancelButtonInteractive = driver.findElements(By.cssSelector(
                        ".swal2-container.swal2-backdrop-show .swal2-cancel")).stream()
                .anyMatch(button -> button.isDisplayed() && button.isEnabled());
        return yesButtonVisible && cancelButtonInteractive;
    }

    @Step("تأكيد حذف سجل الاختبار فقط")
    public void confirmSafeDelete() {
        WebElement confirm = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"))
                        .stream().filter(WebElement::isDisplayed)
                        .filter(button -> button.getText().trim().contains("نعم"))
                        .findFirst().orElse(null));
        confirm.click();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> d.findElements(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"))
                        .stream().noneMatch(button -> button.isDisplayed()
                                && button.getText().trim().contains("نعم")));
        waitForCashPageLoading();
        dismissCashDeleteSuccessPopupIfShown();
        waitForCashPageLoading();
    }

    private void dismissCashDeleteSuccessPopupIfShown() {
        By successPopup = By.xpath(
                "//div[contains(@class,'swal2-popup') and (contains(normalize-space(.),'تم الحذف بنجاح') or contains(normalize-space(.),'تم الحذف'))]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(successPopup));
            WebElement confirm = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm")));
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", confirm);
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.invisibilityOfElementLocated(
                            By.cssSelector(".swal2-container.swal2-backdrop-show")));
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // Some application versions close the confirmation without a second popup.
        }
    }

    @Step("إلغاء حذف سجل الاختبار")
    public void cancelSafeDelete() {
        WebElement cancel = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector(
                        ".swal2-container.swal2-backdrop-show .swal2-cancel")));
        cancel.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(By.cssSelector(".swal2-container.swal2-backdrop-show")).stream()
                        .noneMatch(WebElement::isDisplayed));
    }

    private WebElement findSafeRowByName(String safeName) {
        return driver.findElements(SafeRows).stream()
                .filter(WebElement::isDisplayed)
                .filter(row -> row.findElements(By.cssSelector("td")).stream()
                        .anyMatch(cell -> cell.getText().trim().equals(safeName)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Test safe not found by exact name: " + safeName));
    }

    @Step("الحصول على عدد صفوف قائمة الخزن")
    public int getVisibleSafeRowCount() {
        return (int) driver.findElements(SafeRows).stream()
                .filter(WebElement::isDisplayed)
                .count();
    }

    @Step("قراءة حجم صفحة قائمة الخزن المحدد")
    public int getSelectedSafePageSize() {
        WebElement select = findSafePageSizeSelect();
        return Integer.parseInt(new org.openqa.selenium.support.ui.Select(select)
                .getFirstSelectedOption().getText().trim());
    }

    @Step("قراءة خيار بديل لحجم صفحة الخزن")
    public int getAlternativeSafePageSize(int currentValue) {
        return new org.openqa.selenium.support.ui.Select(findSafePageSizeSelect()).getOptions().stream()
                .map(option -> option.getText().trim())
                .filter(text -> text.matches("\\d+"))
                .map(Integer::parseInt)
                .filter(value -> value > currentValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No larger safe page-size option is available"));
    }

    @Step("تغيير حجم صفحة قائمة الخزن إلى {pageSize}")
    public void selectSafePageSize(int pageSize) {
        int currentRowCount = getVisibleSafeRowCount();
        new org.openqa.selenium.support.ui.Select(findSafePageSizeSelect()).selectByVisibleText(String.valueOf(pageSize));
        waitForCashPageLoading();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> getVisibleSafeRowCount() == Math.min(currentRowCount, pageSize));
    }

    private WebElement findSafePageSizeSelect() {
        return driver.findElements(By.cssSelector("select")).stream()
                .filter(WebElement::isDisplayed)
                .filter(select -> select.findElements(By.cssSelector("option")).stream()
                        .anyMatch(option -> option.getText().trim().matches("\\d+")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Safe list page-size selector not found"));
    }

    @Step("قراءة العدد الإجمالي لسجلات الخزن")
    public int getSafeTotalRowCount() {
        List<WebElement> rows = driver.findElements(SafeRows);
        if (rows.size() == 1 && rows.get(0).getText().toLowerCase().contains("no data")) {
            return 0;
        }
        return rows.size();
    }

    @Step("التحقق من إتاحة الانتقال للصفحة التالية")
    public boolean isSafeNextPageEnabled() {
        List<WebElement> pageButtons = getPaginationButtons();
        if (pageButtons.isEmpty()) {
            return false;
        }
        WebElement next = pageButtons.get(pageButtons.size() - 1);
        return isPaginationButtonEnabled(next);
    }

    @Step("الانتقال إلى الصفحة التالية لقائمة الخزن")
    public void goToNextSafePage() {
        waitForCashPageLoading();
        List<WebElement> pageButtons = getPaginationButtons();
        WebElement next = pageButtons.get(pageButtons.size() - 1);
        if (!isPaginationButtonEnabled(next)) {
            throw new IllegalStateException("Safe list has no next page");
        }
        String firstCode = getFirstSafeCellByHeader("الكود");
        next.click();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !getFirstSafeCellByHeader("الكود").equals(firstCode));
    }

    @Step("العودة إلى الصفحة السابقة لقائمة الخزن")
    public void goToPreviousSafePage() {
        waitForCashPageLoading();
        List<WebElement> pageButtons = getPaginationButtons();
        WebElement previous = pageButtons.get(0);
        if (!isPaginationButtonEnabled(previous)) {
            throw new IllegalStateException("Safe list has no previous page");
        }
        previous.click();
        waitForCashPageLoading();
    }

    private List<WebElement> getPaginationButtons() {
        List<WebElement> buttons = driver.findElements(By.cssSelector(
                        ".pagination .page-item button, .pagination .page-item a, .page-item button, .page-item a"))
                .stream().filter(WebElement::isDisplayed).toList();
        if (buttons.size() < 3) {
            return List.of();
        }
        return buttons;
    }

    private boolean isPaginationButtonEnabled(WebElement button) {
        WebElement pageItem = button.findElement(By.xpath("ancestor::*[contains(concat(' ',normalize-space(@class),' '),' page-item ')][1]"));
        String className = (pageItem.getAttribute("class") + " " + button.getAttribute("class")).toLowerCase();
        return button.isEnabled()
                && !className.contains("disabled")
                && !"true".equalsIgnoreCase(button.getAttribute("aria-disabled"));
    }

    private void waitForSafeRowsToRefresh() {
        waitForCashPageLoading();
    }

    @Step("قراءة قيمة الخزنة من أول صف حسب اسم العمود: {headerName}")
    public String getFirstSafeCellByHeader(String headerName) {
        List<WebElement> headers = driver.findElements(SafeTableHeaders);
        int columnIndex = -1;
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().contains(headerName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex < 0) {
            throw new IllegalStateException("Safe table column not found: " + headerName);
        }

        WebElement row = driver.findElements(SafeRows).stream()
                .filter(WebElement::isDisplayed)
                .filter(candidate -> !candidate.findElements(By.cssSelector("button[data-action-type='edit']")).isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No editable safe row is visible"));
        List<WebElement> cells = row.findElements(By.cssSelector("td"));
        if (columnIndex >= cells.size()) {
            throw new IllegalStateException("Safe table row does not contain column: " + headerName);
        }
        return cells.get(columnIndex).getText().trim();
    }

    @Step("قراءة قيم عمود الخزن: {headerName}")
    public List<String> getSafeColumnValues(String headerName) {
        List<WebElement> headers = driver.findElements(SafeTableHeaders);
        int columnIndex = -1;
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().contains(headerName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex < 0) {
            throw new IllegalStateException("Safe table column not found: " + headerName);
        }

        final int valueColumn = columnIndex;
        return driver.findElements(SafeRows).stream()
                .filter(WebElement::isDisplayed)
                .filter(row -> !row.findElements(By.cssSelector("button[data-action-type='edit']")).isEmpty())
                .map(row -> {
                    List<WebElement> cells = row.findElements(By.cssSelector("td"));
                    if (valueColumn >= cells.size()) {
                        throw new IllegalStateException("Safe table row does not contain column: " + headerName);
                    }
                    return cells.get(valueColumn).getText().trim();
                })
                .toList();
    }

    public List<String> sortCodes(List<String> codes, boolean ascending) {
        List<String> sorted = new java.util.ArrayList<>(codes);
        sorted.sort((left, right) -> {
            try {
                int comparison = Long.compare(Long.parseLong(left), Long.parseLong(right));
                return ascending ? comparison : -comparison;
            } catch (NumberFormatException ignored) {
                int comparison = left.compareToIgnoreCase(right);
                return ascending ? comparison : -comparison;
            }
        });
        return sorted;
    }

    @Step("اختيار ترتيب عمود قائمة الخزن: {headerName}")
    public void clickSafeColumnHeader(String headerName) {
        waitForCashPageLoading();
        WebElement header = driver.findElements(SafeTableHeaders).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().trim().contains(headerName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Safe table column not found: " + headerName));
        header.click();
        waitForCashPageLoading();
    }

    @Step("فتح أول خزنة ظاهرة للتعديل دون حفظ")
    public void openFirstSafeForEdit() {
        WebElement row = driver.findElements(SafeRows).stream()
                .filter(WebElement::isDisplayed)
                .filter(candidate -> !candidate.findElements(By.cssSelector("button[data-action-type='edit']")).isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No editable safe row is visible"));
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='arabicName']")));
    }

    @Step("قراءة قيمة الاسم العربي الحالية من النموذج")
    public String getArabicNameInputValue() {
        return driver.findElement(By.cssSelector("input[name='arabicName']")).getAttribute("value");
    }

    @Step("قراءة قيمة الاسم الإنجليزي الحالية من النموذج")
    public String getEnglishNameInputValue() {
        return driver.findElement(By.cssSelector("input[name='englishName']")).getAttribute("value");
    }

    @Step("قراءة قيمة الملاحظات الحالية")
    public String getNotesValue() {
        return findNotesField().getAttribute("value");
    }

    @Step("قراءة الحد الأقصى المعلن لحقل الملاحظات")
    public int getNotesMaxLength() {
        List<WebElement> fields = driver.findElements(By.xpath(
                "//textarea[contains(@class,'form-control') and @label='Notes']"));
        if (fields.isEmpty()) {
            return -1;
        }
        String maxLength = fields.get(0).getAttribute("maxlength");
        if (maxLength == null || maxLength.isBlank()) {
            return -1;
        }
        return Integer.parseInt(maxLength);
    }

    private WebElement findNotesField() {
        return driver.findElements(By.xpath(
                        "//textarea[contains(@class,'form-control') and @label='Notes']"))
                .stream().filter(WebElement::isDisplayed).findFirst()
                .orElseThrow(() -> new IllegalStateException("Cash notes field is not visible"));
    }

    @Step("قراءة كود الخزنة الحالية من النموذج")
    public String getSafeCodeInputValue() {
        return driver.findElement(By.xpath("//*[self::label or self::legend][normalize-space()='الكود']/following::input[1]"))
                .getAttribute("value").trim();
    }

    @Step("قراءة رقم الحساب الحالي من النموذج")
    public String getSafeAccountValue() {
        List<WebElement> selectedValues = driver.findElements(By.xpath(
                "//label[normalize-space()='رقم الحساب']/following::div[contains(@class,'vs__dropdown-toggle')][1]//div[contains(@class,'vs__selected')]"
        ));
        return selectedValues.isEmpty() ? "" : selectedValues.get(0).getText().trim();
    }

    @Step("تصدير قائمة الخزن بصيغة {format}")
    public void exportSafes(String format) {
        String normalizedFormat = format.trim().toLowerCase();
        if (!normalizedFormat.equals("pdf") && !normalizedFormat.equals("xls")) {
            throw new IllegalArgumentException("Supported safe export formats are PDF and XLS");
        }

        String lower = "translate(concat(@class,' ',@src,' ',@alt,' ',@title,' ',@aria-label,' ',normalize-space(.)),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
        String tokenMatch = normalizedFormat.equals("pdf")
                ? "contains(" + lower + ", 'pdf')"
                : "(contains(" + lower + ", 'xls') or contains(" + lower + ", 'excel') or " +
                        "contains(" + lower + ", 'spreadsheet'))";
        By exportControl = By.xpath(
                "//*[self::button or self::a][" + tokenMatch + " or .//*[" + tokenMatch + "]]"
        );

        List<WebElement> controls = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    List<WebElement> found = d.findElements(exportControl);
                    return found.stream().anyMatch(WebElement::isDisplayed) ? found : null;
                });
        WebElement control = controls.stream().filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Export control not visible: " + format));
        control.click();
    }
    

}
