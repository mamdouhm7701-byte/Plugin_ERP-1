package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RoutLine extends AbstractForDealer {
    private final RouteRecords recordControls;

    private final By save = By.cssSelector("button[data-action-type='save']");

    private boolean visible(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    private void waitLoading() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        "div.b-overlay,div.position-absolute.bg-light.rounded-lg,.loading-bg,.spinner-border"))
                                        .stream()
                                        .noneMatch(
                                                e -> {
                                                    try {
                                                        return e.isDisplayed();
                                                    } catch (Exception ignored) {
                                                        return false;
                                                    }
                                                }));
    }

    @Override
    @Step("فتح نموذج جديد")
    public void ClickNew() {
        waitLoading();
        super.ClickNew();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(save));
    }

    @Override
    @Step("إغلاق النموذج دون حفظ")
    public void ClickCloseButton() {
        if (visible(By.xpath("//button[normalize-space()='إغلاق']"))) super.ClickCloseButton();
    }

    @Override
    @Step("حفظ البيانات المدخلة")
    public void ClickSaveButton() {
        super.ClickSaveButton();
        By success =
                By.xpath(
                        "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحفظ')]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(success));
            DismissAlertIfPresent();
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
        waitLoading();
    }

    private void waitAlert() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show"))
                                        .stream()
                                        .noneMatch(
                                                e -> {
                                                    try {
                                                        return e.isDisplayed();
                                                    } catch (Exception ignored) {
                                                        return false;
                                                    }
                                                }));
    }

    @Step("إغلاق النموذج المفتوح")
    public void CloseFormIfOpen() {
        ClickCloseButton();
    }

    @Step("إغلاق التنبيه الظاهر")
    public void DismissAlertIfPresent() {
        try {
            List<WebElement> b =
                    driver.findElements(
                            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"));
            if (!b.isEmpty())
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("arguments[0].click();", b.get(0));
            waitAlert();
        } catch (Exception ignored) {
        }
    }

    @Step("التحقق من بنية قائمة حالات خط السير")
    public boolean IsListStructureDisplayed() {
        return recordControls.IsListStructureDisplayed();
    }

    @Step("التحقق من عناصر نموذج حالة خط السير")
    public String GetMissingControls() {
        List<String> m = new java.util.ArrayList<>();
        if (!visible(By.cssSelector("input[name='code']"))) m.add("الكود");
        if (!visible(By.cssSelector("input[name='arabicName']"))) m.add("الاسم العربي");
        if (!visible(By.cssSelector("input[name='englishName']"))) m.add("الاسم الإنجليزي");
        if (driver.findElements(By.cssSelector("input[type='radio']")).size() < 2)
            m.add("نوع المستند");
        if (!visible(
                By.cssSelector(
                        "textarea[label='Notes'],textarea[label-text='Notes'],textarea[name='notes']")))
            m.add("الملاحظات");
        if (driver.findElements(By.cssSelector("input[type='checkbox']")).isEmpty())
            m.add("تفعيل موقع العميل");
        if (!visible(save)) m.add("حفظ");
        if (!visible(By.xpath("//button[normalize-space()='إغلاق']"))) m.add("إغلاق");
        return String.join("، ", m);
    }

    @Step("عدد حالات خط السير المطابقة بالاسم")
    public int GetExactMatchCount(String n) {
        return recordControls.GetExactMatchCount(n);
    }

    @Step("فتح حالة خط السير المطابقة للتعديل")
    public void OpenForEditByName(String n) {
        recordControls.OpenForEditByName(n);
    }

    private String value(By b) {
        return driver.findElement(b).getAttribute("value").trim();
    }

    public String GetArabicNameValue() {
        return value(By.cssSelector("input[name='arabicName']"));
    }

    public String GetEnglishNameValue() {
        return value(By.cssSelector("input[name='englishName']"));
    }

    public String GetCodeValue() {
        return value(By.cssSelector("input[name='code']"));
    }

    public String GetDocumentTypeValue() {
        List<WebElement> x =
                driver.findElements(By.cssSelector(".modal-body .btn-group label.btn.active span"));
        if (!x.isEmpty()) return x.get(0).getText().trim();
        List<WebElement> checked =
                driver.findElements(By.cssSelector(".modal-body input[type='radio']:checked"));
        if (checked.isEmpty()) return "";
        String id = checked.get(0).getAttribute("id");
        List<WebElement> labels =
                driver.findElements(By.cssSelector(".modal-body label[for='" + id + "']"));
        return labels.isEmpty() ? "" : labels.get(0).getText().trim();
    }

    public boolean IsRouteLocationChecked() {
        return driver.findElements(By.cssSelector(".modal-body input[type='checkbox']")).stream()
                .anyMatch(WebElement::isSelected);
    }

    @Step("تعيين خيار تفعيل موقع العميل")
    public void SetRouteLocation(boolean checked) {
        List<WebElement> x =
                driver.findElements(By.cssSelector(".modal-body input[type='checkbox']"));
        if (x.isEmpty()) throw new IllegalStateException("Route location checkbox not found");
        WebElement input = x.get(0);
        if (input.isSelected() == checked) return;
        String id = input.getAttribute("id");
        WebElement label =
                driver.findElement(By.cssSelector(".modal-body label[for='" + id + "']"));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", label);
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(
                        d ->
                                driver
                                        .findElements(
                                                By.cssSelector(
                                                        ".modal-body input[type='checkbox']"))
                                        .stream()
                                        .anyMatch(e -> e.isSelected() == checked));
    }

    public boolean IsArabicNameRequiredDisplayed() {
        return visible(By.xpath("//*[contains(normalize-space(),'الاسم العربي مطلوب')]"))
                || visible(By.cssSelector("input[name='arabicName'].is-invalid"));
    }

    @Step("طلب حذف حالة خط السير المطابقة")
    public void RequestDeleteByName(String n) {
        recordControls.RequestDeleteByName(n);
    }

    public boolean IsDeleteConfirmationDisplayed() {
        return recordControls.IsDeleteConfirmationDisplayed();
    }

    @Step("تأكيد حذف حالة الاختبار")
    public void ConfirmDelete() {
        recordControls.ConfirmDelete();
    }

    @Step("إلغاء الحذف")
    public void CancelDelete() {
        recordControls.CancelDelete();
    }

    // Constructors
    public RoutLine(WebDriver driver) {
        super(driver);
        recordControls = new RouteRecords(this, driver);
    }

    // Locators
    private By OpenBasicData =
            By.xpath(
                    "//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");
    private By OpenRoutLine =
            By.xpath("//span[contains(@class,'menu-title') and contains(text(),'خط السير')]");

    // Methods
    @Step("Open Rout Line Page")
    public void OpenRoutLinePage() {
        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenRoutLine);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();
    }

    @Step("اختيار راديو بوتوم:{RadiBouton}")
    public void SelectRadiBouton(String RadiBouton) {
        utilites.checkbox().selectRadioButton(RadiBouton);
    }
    boolean recordVisible(By locator) { return visible(locator); }
    void recordWaitLoading() { waitLoading(); }
    void recordWaitAlert() { waitAlert(); }
}
