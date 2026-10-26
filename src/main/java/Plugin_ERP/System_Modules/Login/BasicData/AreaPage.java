package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AreaPage extends AbstractForDealer {
    private final AreaRecordActions recordControls;
    private final AreaGridControls grid;

    private final By areaRows = By.cssSelector("table tbody tr[role='row']");
    private final By saveButton = By.cssSelector("button[data-action-type='save']");
    private final By areaHeaders = By.cssSelector("table thead th");

    public AreaPage(WebDriver driver) {
        super(driver);
        grid = new AreaGridControls(this, driver, this::waitForAreaRows, this::findAreaRow);
        recordControls = new AreaRecordActions(this, driver);
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
        recordControls.SearchArea(name);
    }

    @Override
    @Step("حفظ البيانات المدخلة")
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
        recordControls.EditAreaByName(name);
    }

    @Step("حذف المنطقة المحددة بالاسم: {name}")
    public void DeleteAreaByName(String name) {
        recordControls.DeleteAreaByName(name);
    }

    @Step("التحقق من وجود المنطقة: {name}")
    public boolean IsAreaDisplayed(String name) {
        return recordControls.IsAreaDisplayed(name);
    }

    @Step("التحقق من الاسم العربي الإلزامي")
    public boolean IsAreaArabicNameRequiredDisplayed() {
        return driver
                .findElements(By.xpath("//*[contains(normalize-space(),'الاسم العربي مطلوب')]"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    private WebElement findAreaRow(String name) {
        return recordControls.findAreaRow(name);
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
        return grid.GetVisibleAreaRowCount();
    }

    @Step("قراءة عدد السجلات المطابقة للاسم الدقيق: {name}")
    public int GetExactAreaMatchCount(String name) {
        return grid.GetExactAreaMatchCount(name);
    }

    @Step("قراءة قيمة أول صف في عمود: {headerName}")
    public String GetFirstAreaCellByHeader(String headerName) {
        return grid.GetFirstAreaCellByHeader(headerName);
    }

    public String GetAreaCellByName(String name, String headerName) {
        return grid.GetAreaCellByName(name, headerName);
    }

    @Step("قراءة قيم عمود المناطق: {headerName}")
    public java.util.List<String> GetAreaColumnValues(String headerName) {
        return grid.GetAreaColumnValues(headerName);
    }

    @Step("فتح أول منطقة للتعديل")
    public void OpenFirstAreaForEdit() {
        recordControls.OpenFirstAreaForEdit();
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
        return grid.GetAreaPageSize();
    }

    @Step("قراءة حجم صفحة بديل أكبر من {current}")
    public int GetAlternativeAreaPageSize(int current) {
        return grid.GetAlternativeAreaPageSize(current);
    }

    @Step("اختيار حجم الصفحة: {size}")
    public void SelectAreaPageSize(int size) {
        grid.SelectAreaPageSize(size);
    }

    @Step("فتح صفحة المنطقة التالية")
    public void GoToNextAreaPage() {
        grid.GoToNextAreaPage();
    }

    @Step("العودة إلى صفحة المناطق السابقة")
    public void GoToPreviousAreaPage() {
        grid.GoToPreviousAreaPage();
    }

    @Step("التحقق من إمكانية الانتقال إلى الصفحة التالية")
    public boolean IsNextAreaPageEnabled() {
        return grid.IsNextAreaPageEnabled();
    }

    @Step("فرز قائمة المناطق حسب العمود: {headerName}")
    public void ClickAreaColumnHeader(String headerName) {
        grid.ClickAreaColumnHeader(headerName);
    }

    @Step("ترتيب أكواد المناطق تصاعديًا أو تنازليًا")
    public java.util.List<String> SortAreaCodes(java.util.List<String> values, boolean ascending) {
        return grid.SortAreaCodes(values, ascending);
    }

    @Step("طلب حذف المنطقة المحددة: {name}")
    public void RequestDeleteAreaByName(String name) {
        recordControls.RequestDeleteAreaByName(name);
    }

    @Step("التحقق من نافذة تأكيد حذف المنطقة")
    public boolean IsAreaDeleteConfirmationDisplayed() {
        return recordControls.IsAreaDeleteConfirmationDisplayed();
    }

    @Step("تأكيد حذف سجل المنطقة المحدد")
    public void ConfirmAreaDelete() {
        recordControls.ConfirmAreaDelete();
    }

    @Step("إلغاء حذف سجل المنطقة")
    public void CancelAreaDelete() {
        recordControls.CancelAreaDelete();
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

    void recordWaitForAreaSaveSuccessPopupToDisappear() {
        waitForAreaSaveSuccessPopupToDisappear();
    }

    void recordWaitForAreaRows() {
        waitForAreaRows();
    }
}
