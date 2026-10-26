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

public class DistributionEntityPage extends AbstractForDealer {
    private final DistributionRecords records;

    private final By save = By.cssSelector("button[data-action-type='save']");

    public DistributionEntityPage(WebDriver driver) {
        super(driver);
        records = new DistributionRecords(this, driver);
    }

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
                                                        "div.b-overlay, div.position-absolute.bg-light.rounded-lg, .loading-bg,"
                                                                + " .spinner-border"))
                                        .stream()
                                        .noneMatch(
                                                e -> {
                                                    try {
                                                        return e.isDisplayed();
                                                    } catch (Exception ignored) {
                                                        Plugin_ERP.Bots.LogUtils.debug(
                                                                "DistributionEntityPage condition unavailable: "
                                                                        + ignored.getClass()
                                                                                .getSimpleName());
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
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(
                            d -> {
                                List<WebElement> buttons =
                                        d.findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show .swal2-confirm"));
                                if (buttons.isEmpty()) return false;
                                try {
                                    ((org.openqa.selenium.JavascriptExecutor) d)
                                            .executeScript("arguments[0].click();", buttons.get(0));
                                    return true;
                                } catch (Exception ignored) {
                                    Plugin_ERP.Bots.LogUtils.debug(
                                            "DistributionEntityPage condition unavailable: "
                                                    + ignored.getClass().getSimpleName());
                                    return false;
                                }
                            });
            waitAlertToDisappear();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "DistributionEntityPage fallback: " + ignored.getClass().getSimpleName());
        }
        waitLoading();
    }

    @Step("إغلاق نموذج جهة الصرف المفتوح إن وجد")
    public void CloseFormIfOpen() {
        ClickCloseButton();
    }

    @Step("البحث في قائمة جهات الصرف")
    public void Search(String text) {
        records.Search(text);
    }

    private void waitAlertToDisappear() {
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
                                                                "DistributionEntityPage condition unavailable: "
                                                                        + ignored.getClass()
                                                                                .getSimpleName());
                                                        return false;
                                                    }
                                                }));
    }

    @Step("التحقق من أعمدة قائمة جهات الصرف وإجراءاتها")
    public boolean IsListStructureDisplayed() {
        return records.IsListStructureDisplayed();
    }

    @Step("عناصر نموذج جهة الصرف غير الظاهرة")
    public String GetMissingFormControls() {
        List<String> missing = new java.util.ArrayList<>();
        if (!visible(By.cssSelector("input[name='code']"))) missing.add("الكود");
        if (!visible(By.cssSelector("input[name='arabicName']"))) missing.add("الاسم العربي");
        if (!visible(By.cssSelector("input[name='englishName']"))) missing.add("الاسم الإنجليزي");
        if (!visible(By.cssSelector(".modal-body .vs__dropdown-toggle"))) missing.add("رقم الحساب");
        if (!visible(save)) missing.add("حفظ");
        if (!visible(By.xpath("//button[normalize-space()='إغلاق']"))) missing.add("إغلاق");
        return String.join("، ", missing);
    }

    @Step("عدد صفوف جهات الصرف القابلة للتعديل")
    public int GetEditableRowCount() {
        return records.GetEditableRowCount();
    }

    @Step("عدد النتائج المطابقة بالاسم الدقيق")
    public int GetExactMatchCount(String name) {
        return records.GetExactMatchCount(name);
    }

    @Step("قراءة أول خلية في عمود جهة الصرف")
    public String GetFirstCellByHeader(String title) {
        return records.GetFirstCellByHeader(title);
    }

    @Step("فتح أول جهة صرف للتعديل")
    public void OpenFirstForEdit() {
        records.OpenFirstForEdit();
    }

    @Step("فتح جهة الصرف المطابقة بالاسم للتعديل")
    public void OpenForEditByName(String name) {
        records.OpenForEditByName(name);
    }

    private String value(By locator) {
        return driver.findElement(locator).getAttribute("value").trim();
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

    public String GetSelectedAccountValue() {
        List<WebElement> selected =
                driver.findElements(By.cssSelector(".modal-body .vs__selected"));
        return selected.isEmpty() ? "" : selected.get(0).getText().trim();
    }

    public boolean IsArabicNameRequiredDisplayed() {
        return visible(By.xpath("//*[contains(normalize-space(),'الاسم العربي مطلوب')]"))
                || driver
                        .findElements(By.cssSelector("input[name='arabicName'].is-invalid"))
                        .stream()
                        .anyMatch(WebElement::isDisplayed);
    }

    @Step("طلب حذف جهة الصرف المحددة")
    public void RequestDeleteByName(String name) {
        records.RequestDeleteByName(name);
    }

    public boolean IsDeleteConfirmationDisplayed() {
        return records.IsDeleteConfirmationDisplayed();
    }

    @Step("تأكيد حذف جهة الصرف المحددة")
    public void ConfirmDelete() {
        records.ConfirmDelete();
    }

    @Step("إلغاء الحذف")
    public void CancelDelete() {
        records.CancelDelete();
    }

    boolean recordVisible(By locator) {
        return visible(locator);
    }

    void recordWaitLoading() {
        waitLoading();
    }

    void recordWaitAlertToDisappear() {
        waitAlertToDisappear();
    }
}
