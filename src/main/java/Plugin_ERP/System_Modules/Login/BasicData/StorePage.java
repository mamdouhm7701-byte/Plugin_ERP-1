/* شرح الملف: شاشة المخازن: بنسجل اسم المخزن والحساب المرتبط به ونراجع بياناته. فائدتها تجهيز أماكن
 * حفظ الأصناف واستخدامها في حركات المخزون.
 */
package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StorePage extends AbstractForDealer {
    private final By save = By.cssSelector("button[data-action-type='save']");
    private final WarehouseRecords recordControls;

    public StorePage(WebDriver driver) {
        super(driver);
        recordControls = new WarehouseRecords(this, driver);
    }

    private boolean visible(By b) {
        return driver.findElements(b).stream().anyMatch(WebElement::isDisplayed);
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
                                                        Plugin_ERP.Bots.LogUtils.debug(
                                                                "StorePage condition unavailable: "
                                                                        + ignored.getClass()
                                                                                .getSimpleName());
                                                        return false;
                                                    }
                                                }));
    }

    @Step("فتح شاشة المخازن")
    public void OpenStorePage() {
        NavigationHelper(Pages.WAREHOUSES);
        waitLoading();
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
            Plugin_ERP.Bots.LogUtils.debug(
                    "StorePage fallback: " + ignored.getClass().getSimpleName());
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
                                                        Plugin_ERP.Bots.LogUtils.debug(
                                                                "StorePage condition unavailable: "
                                                                        + ignored.getClass()
                                                                                .getSimpleName());
                                                        return false;
                                                    }
                                                }));
    }

    @Step("إغلاق تنبيه المخزن")
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
            Plugin_ERP.Bots.LogUtils.debug(
                    "StorePage fallback: " + ignored.getClass().getSimpleName());
        }
    }

    @Step("عناصر نموذج المخزن غير الظاهرة")
    public String GetMissingControls() {
        List<String> m = new java.util.ArrayList<>();
        if (!visible(By.cssSelector("input[name='code']"))) m.add("الكود");
        if (!visible(By.cssSelector("input[name='arabicName']"))) m.add("الاسم العربي");
        if (!visible(By.cssSelector("input[name='englishName']"))) m.add("الاسم الإنجليزي");
        if (!visible(By.cssSelector(".modal-body .vs__dropdown-toggle"))) m.add("رقم الحساب");
        if (!visible(By.cssSelector(".modal-body textarea"))) m.add("الملاحظات");
        if (!visible(save)) m.add("حفظ");
        if (!visible(By.xpath("//button[normalize-space()='إغلاق']"))) m.add("إغلاق");
        return String.join("، ", m);
    }

    @Step("التحقق من بنية قائمة المخازن")
    public boolean IsListStructureDisplayed() {
        return recordControls.IsListStructureDisplayed();
    }

    @Step("عدد السجلات المطابقة بالاسم الدقيق")
    public int GetExactMatchCount(String n) {
        return recordControls.GetExactMatchCount(n);
    }

    @Step("فتح المخزن المحدد للاختبار")
    public void OpenForEditByName(String n) {
        recordControls.OpenForEditByName(n);
    }

    public String GetArabicNameValue() {
        return driver.findElement(By.cssSelector("input[name='arabicName']"))
                .getAttribute("value")
                .trim();
    }

    public String GetEnglishNameValue() {
        return driver.findElement(By.cssSelector("input[name='englishName']"))
                .getAttribute("value")
                .trim();
    }

    public String GetCodeValue() {
        return driver.findElement(By.cssSelector("input[name='code']"))
                .getAttribute("value")
                .trim();
    }

    public String GetSelectedAccountValue() {
        List<WebElement> x = driver.findElements(By.cssSelector(".modal-body .vs__selected"));
        return x.isEmpty() ? "" : x.get(0).getText().trim();
    }

    @Step("التحقق من الاسم العربي الإلزامي")
    public boolean IsArabicNameRequiredDisplayed() {
        return visible(By.xpath("//*[contains(normalize-space(),'الاسم العربي مطلوب')]"))
                || visible(By.cssSelector("input[name='arabicName'].is-invalid"));
    }

    @Step("طلب حذف المخزن المحدد")
    public void RequestDeleteByName(String n) {
        recordControls.RequestDeleteByName(n);
    }

    public boolean IsDeleteConfirmationDisplayed() {
        return recordControls.IsDeleteConfirmationDisplayed();
    }

    @Step("تأكيد حذف مخزن الاختبار")
    public void ConfirmDelete(String name) {
        recordControls.ConfirmDelete(name);
    }

    @Step("إلغاء حذف المخزن")
    public void CancelDelete() {
        recordControls.CancelDelete();
    }

    @Step("إغلاق النموذج إن كان مفتوحا")
    public void CloseFormIfOpen() {
        ClickCloseButton();
    }

    boolean recordVisible(By b) {
        return visible(b);
    }

    void recordWaitLoading() {
        waitLoading();
    }

    void recordWaitAlert() {
        waitAlert();
    }
}
