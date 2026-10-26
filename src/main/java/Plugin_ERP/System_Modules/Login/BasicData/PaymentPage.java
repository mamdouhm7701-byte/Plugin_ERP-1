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

public class PaymentPage extends AbstractForDealer {
    private final PaymentRecords records;
    private final By rows = By.cssSelector("table tbody tr[role='row']"),
            headers = By.cssSelector("table thead th"),
            save = By.cssSelector("button[data-action-type='save']");

    public PaymentPage(WebDriver driver) {
        super(driver);
        records = new PaymentRecords(this, driver);
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
                                                                "PaymentPage condition unavailable: "
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
            DismissAlertIfPresent();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "PaymentPage fallback: " + ignored.getClass().getSimpleName());
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
                                                                "PaymentPage condition unavailable: "
                                                                        + ignored.getClass()
                                                                                .getSimpleName());
                                                        return false;
                                                    }
                                                }));
    }

    @Step("إغلاق نموذج الدفع المفتوح إن وجد")
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
            Plugin_ERP.Bots.LogUtils.debug(
                    "PaymentPage fallback: " + ignored.getClass().getSimpleName());
        }
    }

    @Step("التحقق من أعمدة قائمة طرق الدفع")
    public boolean IsListStructureDisplayed() {
        return records.IsListStructureDisplayed();
    }

    @Step("التحقق من عناصر نموذج الدفع")
    public String GetMissingControls() {
        List<String> m = new java.util.ArrayList<>();
        if (!visible(By.cssSelector("input[name='code']"))) m.add("الكود");
        if (!visible(By.cssSelector("input[name='arabicName']"))) m.add("الاسم العربي");
        if (!visible(By.cssSelector("input[name='englishName']"))) m.add("الاسم الإنجليزي");
        if (!visible(
                By.cssSelector(
                        "textarea[label='Notes'],textarea[label-text='Notes'],textarea[name='notes']")))
            m.add("الملاحظات");
        if (!visible(By.cssSelector(".modal-body .vs__dropdown-toggle"))) m.add("اسم البنك");
        if (driver.findElements(By.cssSelector("input[type='checkbox']")).size() < 2)
            m.add("خيارات التحديد");
        if (!visible(save)) m.add("حفظ");
        if (!visible(By.xpath("//button[normalize-space()='إغلاق']"))) m.add("إغلاق");
        return String.join("، ", m);
    }

    @Step("عدد السجلات المطابقة بالاسم الدقيق")
    public int GetExactMatchCount(String n) {
        return records.GetExactMatchCount(n);
    }

    @Step("فتح طريقة الدفع المطابقة للتعديل")
    public void OpenForEditByName(String n) {
        records.OpenForEditByName(n);
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

    public String GetSelectedBankValue() {
        List<WebElement> x = driver.findElements(By.cssSelector(".modal-body .vs__selected"));
        return x.isEmpty() ? "" : x.get(0).getText().trim();
    }

    private WebElement checkbox(String text) {
        return driver.findElement(
                By.xpath(
                        "//label[contains(normalize-space(),'"
                                + text
                                + "')]/following::input[@type='checkbox'][1]"));
    }

    @Step("تغيير خيار طريقة الدفع {text}")
    public void SetPaymentCheckbox(String text, boolean checked) {
        WebElement x = checkbox(text);
        if (x.isSelected() == checked) return;
        String id = x.getAttribute("id");
        WebElement label = driver.findElement(By.cssSelector("label[for='" + id + "']"));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", label);
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(d -> checkbox(text).isSelected() == checked);
    }

    public boolean IsPaymentCheckboxChecked(String text) {
        return checkbox(text).isSelected();
    }

    public boolean IsArabicNameRequiredErrorDisplayed() {
        return visible(By.xpath("//*[contains(normalize-space(),'الاسم العربي مطلوب')]"))
                || visible(By.cssSelector("input[name='arabicName'].is-invalid"));
    }

    public boolean IsBankRequiredErrorDisplayed() {
        return visible(
                        By.xpath(
                                "//*[contains(normalize-space(),'اسم البنك مطلوب') or"
                                        + " contains(normalize-space(),'البنك مطلوب')]"))
                || visible(By.cssSelector(".vs__dropdown-toggle.is-invalid"));
    }

    @Step("طلب حذف طريقة الدفع المطابقة للاسم")
    public void RequestDeleteByName(String n) {
        records.RequestDeleteByName(n);
    }

    public boolean IsDeleteConfirmationDisplayed() {
        return records.IsDeleteConfirmationDisplayed();
    }

    @Step("تأكيد حذف طريقة الدفع المحددة")
    public void ConfirmDelete() {
        records.ConfirmDelete();
    }

    @Step("إلغاء الحذف")
    public void CancelDelete() {
        records.CancelDelete();
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
