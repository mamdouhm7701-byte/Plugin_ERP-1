package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AreaPage extends AbstractForDealer {

    private final By searchInput = By.cssSelector("input[placeholder='بحث ....']");
    private final By areaRows = By.cssSelector("table tbody tr[role='row']");
    private final By saveButton = By.cssSelector("button[data-action-type='save']");
    private final By areaHeaders = By.cssSelector("table thead th");

    public AreaPage(WebDriver driver) {
        super(driver);
    }

    // locators
    private By OpenBasicData =
            By.xpath(
                    "//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");
    private By OpenArea =
            By.xpath("//span[contains(@class,'menu-title') and contains(text(),'المناطق')]");

    // Methods
    @Step("فتح قائمه المناطق")
    public void OpenAreaPage() {
        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(OpenArea);
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();
    }

    @Step("البحث عن منطقة: {name}")
    public void SearchArea(String name) {
        utilites.waits().waitForOverlayToDisappear();
        WebElement input =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        input.sendKeys(name);
        input.sendKeys(Keys.ENTER);
        waitForAreaRows();
    }

    @Override
    public void ClickSaveButton() {
        super.ClickSaveButton();
        waitForAreaSaveSuccessPopupToDisappear();
    }

    private void waitForAreaSaveSuccessPopupToDisappear() {
        By successPopup =
                By.xpath(
                        "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحفظ"
                                + " بنجاح')]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(successPopup));
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(
                            ExpectedConditions.invisibilityOfElementLocated(
                                    By.cssSelector(".swal2-container.swal2-backdrop-show")));
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // Validation failures do not show the save confirmation popup.
        }
    }

    @Step("فتح تعديل المنطقة المحددة بالاسم: {name}")
    public void EditAreaByName(String name) {
        WebElement row = findAreaRow(name);
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(saveButton));
    }

    @Step("حذف المنطقة المحددة بالاسم: {name}")
    public void DeleteAreaByName(String name) {
        RequestDeleteAreaByName(name);
        ConfirmAreaDelete();
    }

    @Step("التحقق من وجود المنطقة: {name}")
    public boolean IsAreaDisplayed(String name) {
        return driver.findElements(areaRows).stream().anyMatch(row -> row.getText().contains(name));
    }

    @Step("التحقق من الاسم العربي الإلزامي")
    public boolean IsAreaArabicNameRequiredDisplayed() {
        return driver
                .findElements(By.xpath("//*[contains(normalize-space(),'الاسم العربي مطلوب')]"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    private WebElement findAreaRow(String name) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d.findElements(areaRows).stream()
                                        .filter(row -> row.getText().contains(name))
                                        .findFirst()
                                        .orElse(null));
    }

    @Step("التحقق من أعمدة وإجراءات قائمة المناطق")
    public boolean IsAreaListStructureDisplayed() {
        boolean code =
                driver.findElements(areaHeaders).stream()
                        .anyMatch(h -> h.getText().contains("الكود"));
        boolean name =
                driver.findElements(areaHeaders).stream()
                        .anyMatch(h -> h.getText().contains("اسم المنطقة"));
        boolean rowActions =
                driver.findElements(areaRows).stream()
                        .filter(WebElement::isDisplayed)
                        .anyMatch(
                                row ->
                                        row.findElements(
                                                                        By.cssSelector(
                                                                                "button[data-action-type='edit']"))
                                                                .size()
                                                        == 1
                                                && row.findElements(
                                                                        By.cssSelector(
                                                                                "button[data-action-type='delete']"))
                                                                .size()
                                                        == 1);
        return code && name && rowActions;
    }

    @Step("التحقق من ظهور عناصر نموذج المنطقة")
    public String GetMissingAreaFormControls() {
        java.util.List<String> missing = new java.util.ArrayList<>();
        if (driver.findElements(By.cssSelector("input[name='arabicName']")).stream()
                .noneMatch(WebElement::isDisplayed)) missing.add("الاسم العربي");
        if (driver.findElements(By.cssSelector("input[name='englishName']")).stream()
                .noneMatch(WebElement::isDisplayed)) missing.add("الاسم الإنجليزي");
        if (driver.findElements(By.xpath("//textarea[@label='Notes']")).stream()
                .noneMatch(WebElement::isDisplayed)) missing.add("الملاحظات");
        if (driver.findElements(saveButton).stream().noneMatch(WebElement::isDisplayed))
            missing.add("حفظ");
        if (driver.findElements(By.xpath("//button[normalize-space()='إغلاق']")).stream()
                .noneMatch(WebElement::isDisplayed)) missing.add("إغلاق");
        if (driver
                .findElements(By.xpath("//*[normalize-space()='الكود']/following::input[1]"))
                .stream()
                .noneMatch(WebElement::isDisplayed)) missing.add("الكود");
        return String.join(", ", missing);
    }

    @Step("التحقق من ظهور نموذج إضافة منطقة")
    public boolean IsAreaCreationFormDisplayed() {
        return driver.findElements(By.cssSelector("input[name='arabicName']")).stream()
                .anyMatch(WebElement::isDisplayed);
    }

    @Step("قراءة عدد صفوف المناطق الظاهرة")
    public int GetVisibleAreaRowCount() {
        return (int)
                driver.findElements(areaRows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                row -> {
                                    String text = row.getText().trim().toLowerCase();
                                    return !text.contains("لا يوجد بيانات")
                                            && !text.contains(
                                                    "there are no records matching your request")
                                            && !text.contains("no matching records")
                                            && !text.contains("no data");
                                })
                        .count();
    }

    @Step("قراءة عدد السجلات المطابقة للاسم الدقيق: {name}")
    public int GetExactAreaMatchCount(String name) {
        return (int)
                driver.findElements(areaRows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                row ->
                                        row.findElements(By.cssSelector("td")).stream()
                                                .anyMatch(
                                                        cell -> cell.getText().trim().equals(name)))
                        .count();
    }

    @Step("قراءة قيمة أول صف في عمود: {headerName}")
    public String GetFirstAreaCellByHeader(String headerName) {
        int index = getColumnIndex(headerName);
        WebElement row =
                driver.findElements(areaRows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                candidate ->
                                        !candidate
                                                .findElements(
                                                        By.cssSelector(
                                                                "button[data-action-type='edit']"))
                                                .isEmpty())
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No Area row is displayed"));
        return row.findElements(By.cssSelector("td")).get(index).getText().trim();
    }

    @Step("قراءة قيم عمود المناطق: {headerName}")
    public java.util.List<String> GetAreaColumnValues(String headerName) {
        int index = getColumnIndex(headerName);
        return driver.findElements(areaRows).stream()
                .filter(WebElement::isDisplayed)
                .filter(
                        row ->
                                !row.findElements(By.cssSelector("button[data-action-type='edit']"))
                                        .isEmpty())
                .map(row -> row.findElements(By.cssSelector("td")).get(index).getText().trim())
                .toList();
    }

    private int getColumnIndex(String headerName) {
        java.util.List<WebElement> headers = driver.findElements(areaHeaders);
        for (int i = 0; i < headers.size(); i++)
            if (headers.get(i).getText().contains(headerName)) return i;
        throw new IllegalStateException("Area column not found: " + headerName);
    }

    @Step("فتح أول منطقة للتعديل")
    public void OpenFirstAreaForEdit() {
        WebElement row =
                driver.findElements(areaRows).stream()
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
                                () ->
                                        new IllegalStateException(
                                                "No editable Area row is displayed"));
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(saveButton));
    }

    @Step("قراءة كود المنطقة من النموذج")
    public String GetAreaCodeInputValue() {
        return driver.findElement(By.cssSelector("input[name='code']"))
                .getAttribute("value")
                .trim();
    }

    @Step("قراءة الاسم العربي من نموذج المنطقة")
    public String GetArabicNameInputValue() {
        return driver.findElement(By.cssSelector("input[name='arabicName']"))
                .getAttribute("value")
                .trim();
    }

    @Step("قراءة الاسم الإنجليزي من نموذج المنطقة")
    public String GetEnglishNameInputValue() {
        return driver.findElement(By.cssSelector("input[name='englishName']"))
                .getAttribute("value")
                .trim();
    }

    @Step("قراءة الملاحظات من نموذج المنطقة")
    public String GetAreaNotesValue() {
        return driver.findElement(By.xpath("//textarea[@label='Notes']")).getAttribute("value");
    }

    @Step("قراءة حجم الصفحة المحدد")
    public int GetAreaPageSize() {
        return Integer.parseInt(
                new Select(getPageSizeSelect()).getFirstSelectedOption().getText().trim());
    }

    @Step("قراءة حجم صفحة بديل أكبر من {current}")
    public int GetAlternativeAreaPageSize(int current) {
        return new Select(getPageSizeSelect())
                .getOptions().stream()
                        .map(o -> o.getText().trim())
                        .filter(s -> s.matches("\\d+"))
                        .map(Integer::parseInt)
                        .filter(n -> n > current)
                        .findFirst()
                        .orElseThrow(
                                () -> new IllegalStateException("No larger Area page size option"));
    }

    @Step("اختيار حجم الصفحة: {size}")
    public void SelectAreaPageSize(int size) {
        new Select(getPageSizeSelect()).selectByVisibleText(String.valueOf(size));
        waitForAreaRows();
    }

    private WebElement getPageSizeSelect() {
        return driver.findElements(By.cssSelector("select")).stream()
                .filter(WebElement::isDisplayed)
                .filter(
                        s ->
                                s.findElements(By.cssSelector("option")).stream()
                                        .anyMatch(o -> o.getText().matches("\\d+")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Area page-size selector not found"));
    }

    @Step("فتح صفحة المنطقة التالية")
    public void GoToNextAreaPage() {
        clickPageButton(true);
    }

    @Step("العودة إلى صفحة المناطق السابقة")
    public void GoToPreviousAreaPage() {
        clickPageButton(false);
    }

    @Step("التحقق من إمكانية الانتقال إلى الصفحة التالية")
    public boolean IsNextAreaPageEnabled() {
        java.util.List<WebElement> buttons = getPageButtons();
        return !buttons.isEmpty() && isPageButtonEnabled(buttons.get(buttons.size() - 1));
    }

    private void clickPageButton(boolean next) {
        java.util.List<WebElement> buttons = getPageButtons();
        WebElement button = buttons.get(next ? buttons.size() - 1 : 0);
        if (!isPageButtonEnabled(button))
            throw new IllegalStateException("Requested Area page is not available");
        String firstCode = GetFirstAreaCellByHeader("الكود");
        button.click();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !GetFirstAreaCellByHeader("الكود").equals(firstCode));
    }

    private java.util.List<WebElement> getPageButtons() {
        java.util.List<WebElement> buttons =
                driver
                        .findElements(
                                By.cssSelector(
                                        ".pagination .page-item button, .pagination .page-item a, .page-item button,"
                                                + " .page-item a"))
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .toList();
        return buttons.size() < 3 ? java.util.List.of() : buttons;
    }

    private boolean isPageButtonEnabled(WebElement button) {
        String classes =
                (button.getAttribute("class")
                                + " "
                                + button.findElement(
                                                By.xpath(
                                                        "ancestor::*[contains(@class,'page-item')][1]"))
                                        .getAttribute("class"))
                        .toLowerCase();
        return button.isEnabled()
                && !classes.contains("disabled")
                && !"true".equals(button.getAttribute("aria-disabled"));
    }

    @Step("فرز قائمة المناطق حسب العمود: {headerName}")
    public void ClickAreaColumnHeader(String headerName) {
        driver.findElements(areaHeaders).stream()
                .filter(WebElement::isDisplayed)
                .filter(h -> h.getText().contains(headerName))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("Area header not found: " + headerName))
                .click();
        waitForAreaRows();
    }

    @Step("ترتيب أكواد المناطق تصاعديًا أو تنازليًا")
    public java.util.List<String> SortAreaCodes(java.util.List<String> values, boolean ascending) {
        java.util.List<String> sorted = new java.util.ArrayList<>(values);
        sorted.sort(
                (left, right) -> {
                    int comparison;
                    try {
                        comparison = Long.compare(Long.parseLong(left), Long.parseLong(right));
                    } catch (NumberFormatException ignored) {
                        comparison = left.compareToIgnoreCase(right);
                    }
                    return ascending ? comparison : -comparison;
                });
        return sorted;
    }

    @Step("طلب حذف المنطقة المملوكة للاختبار: {name}")
    public void RequestDeleteAreaByName(String name) {
        findAreaRow(name).findElement(By.cssSelector("button[data-action-type='delete']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".swal2-popup")));
    }

    @Step("التحقق من نافذة تأكيد حذف المنطقة")
    public boolean IsAreaDeleteConfirmationDisplayed() {
        return driver
                        .findElements(
                                By.cssSelector(
                                        ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                        .stream()
                        .anyMatch(b -> b.isDisplayed() && b.getText().contains("نعم"))
                && driver
                        .findElements(
                                By.cssSelector(
                                        ".swal2-container.swal2-backdrop-show .swal2-cancel"))
                        .stream()
                        .anyMatch(b -> b.isDisplayed() && b.isEnabled());
    }

    @Step("تأكيد حذف سجل المنطقة المملوك للاختبار")
    public void ConfirmAreaDelete() {
        WebElement confirm =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                d ->
                                        d
                                                .findElements(
                                                        By.cssSelector(
                                                                ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                                                .stream()
                                                .filter(WebElement::isDisplayed)
                                                .filter(b -> b.getText().contains("نعم"))
                                                .findFirst()
                                                .orElse(null));
        confirm.click();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                                        .stream()
                                        .noneMatch(
                                                b ->
                                                        b.isDisplayed()
                                                                && b.getText().contains("نعم")));
        waitForAreaRows();
        dismissDeleteSuccessPopup();
    }

    @Step("إلغاء حذف سجل المنطقة")
    public void CancelAreaDelete() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        ".swal2-container.swal2-backdrop-show .swal2-cancel")))
                .click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show"))
                                        .stream()
                                        .noneMatch(WebElement::isDisplayed));
    }

    private void dismissDeleteSuccessPopup() {
        try {
            WebElement ok =
                    new WebDriverWait(driver, Duration.ofSeconds(3))
                            .until(
                                    ExpectedConditions.elementToBeClickable(
                                            By.cssSelector(
                                                    ".swal2-container.swal2-backdrop-show .swal2-confirm")));
            if (ok.getText().contains("نعم")) return;
            ok.click();
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
    }

    @Step("حفظ المنطقة مرتين بسرعة")
    public void ClickSaveTwiceRapidly() {
        waitForAreaRows();
        WebElement button =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(saveButton));
        new Actions(driver).doubleClick(button).perform();
        waitForAreaSaveSuccessPopupToDisappear();
    }

    private void waitForAreaRows() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(".loading-bg, .spinner-border"))
                                        .stream()
                                        .noneMatch(WebElement::isDisplayed));
    }
}
