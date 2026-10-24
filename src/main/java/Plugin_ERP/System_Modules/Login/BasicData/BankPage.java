package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BankPage extends AbstractForDealer {
    private final BankRecords records;

    private final By search = By.cssSelector("input[placeholder*='بحث']");
    private final By save = By.cssSelector("button[data-action-type='save']");

    public BankPage(WebDriver driver) {
        super(driver);
        records = new BankRecords(this, driver);
    }

    private boolean visible(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    private void waitRows() {
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
                                                        return false;
                                                    }
                                                }));
    }

    @Override
    @Step("إغلاق النموذج دون حفظ")
    public void ClickCloseButton() {
        if (visible(By.xpath("//button[normalize-space()='إغلاق']"))) super.ClickCloseButton();
    }

    @Override
    @Step("فتح نموذج جديد")
    public void ClickNew() {
        waitRows();
        super.ClickNew();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(save));
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
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(
                            ExpectedConditions.invisibilityOfElementLocated(
                                    By.cssSelector(".swal2-container.swal2-backdrop-show")));
        } catch (org.openqa.selenium.TimeoutException ignored) {
            /* Validation errors do not show success. */
        }
        waitRows();
        if (!visible(By.xpath("//button[normalize-space()='إغلاق']"))) {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(search));
            waitRows();
        }
    }

    @Step("البحث في قائمة البنوك")
    public void SearchBank(String value) {
        records.SearchBank(value);
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
                                                        return false;
                                                    }
                                                }));
    }

    private void dismissSaveSuccessIfPresent() {
        By popup =
                By.xpath(
                        "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحفظ')]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(popup));
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
                                    return false;
                                }
                            });
            waitAlertToDisappear();
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
    }

    @Step("التحقق من بنية قائمة البنوك")
    public boolean IsBankListStructureDisplayed() {
        return records.IsBankListStructureDisplayed();
    }

    @Step("عناصر نموذج البنك غير الظاهرة")
    public String GetMissingBankFormControls() {
        List<String> missing = new java.util.ArrayList<>();
        if (!visible(By.cssSelector("input[name='code']"))
                && !visible(By.xpath("//*[normalize-space()='الكود']/following::input[1]")))
            missing.add("الكود");
        if (!visible(By.cssSelector("input[name='arabicName']"))) missing.add("الاسم العربي");
        if (!visible(By.cssSelector("input[name='englishName']"))) missing.add("الاسم الإنجليزي");
        if (!visible(By.cssSelector("input#adress"))) missing.add("العنوان");
        if (!visible(By.cssSelector("input#phone"))) missing.add("الهاتف");
        if (!visible(By.cssSelector("input[name='email']"))) missing.add("البريد الإلكتروني");
        if (!visible(By.cssSelector("input[name='accountNumber']"))) missing.add("رقم الحساب");
        if (!visible(By.cssSelector("input[name='iban']"))) missing.add("IBAN");
        if (!visible(By.xpath("//textarea[@label='Notes']"))) missing.add("الملاحظات");
        if (!visible(save)) missing.add("حفظ");
        if (!visible(By.xpath("//button[normalize-space()='إغلاق']"))) missing.add("إغلاق");
        return String.join("، ", missing);
    }

    @Step("عدد صفوف البنوك القابلة للتعديل")
    public int GetEditableBankCount() {
        return records.GetEditableBankCount();
    }

    @Step("عدد البنوك المطابقة بالاسم الدقيق")
    public int GetExactBankMatchCount(String name) {
        return records.GetExactBankMatchCount(name);
    }

    @Step("قراءة أول خلية من عمود البنك")
    public String GetFirstBankCellByHeader(String label) {
        return records.GetFirstBankCellByHeader(label);
    }

    @Step("فتح صف البنك المعروض للتعديل")
    public void OpenFirstBankForEdit() {
        records.OpenFirstBankForEdit();
    }

    @Step("فتح البنك المطابق بالاسم للتعديل")
    public void OpenBankForEditByName(String name) {
        records.OpenBankForEditByName(name);
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

    public String GetEmailValue() {
        return value(By.cssSelector("input[name='email']"));
    }

    public String GetAddressValue() {
        return value(By.cssSelector("input#adress"));
    }

    public String GetBankAccountValue() {
        return value(By.cssSelector("input[name='accountNumber']"));
    }

    public String GetIbanValue() {
        return value(By.cssSelector("input[name='iban']"));
    }

    public String GetBankCodeValue() {
        return value(By.cssSelector("input[name='code']"));
    }

    public String GetNotesValue() {
        return driver.findElement(By.xpath("//textarea[@label='Notes']")).getAttribute("value");
    }

    public boolean IsBankArabicNameRequiredDisplayed() {
        return visible(By.xpath("//*[contains(normalize-space(),'الاسم العربي مطلوب')]"))
                || driver
                        .findElements(By.cssSelector("input[name='arabicName'].is-invalid"))
                        .stream()
                        .anyMatch(WebElement::isDisplayed);
    }

    public boolean IsInvalidBankEmailDisplayed() {
        return visible(
                        By.xpath(
                                "//*[contains(normalize-space(),'البريد الالكتروني') and (contains(.,'صحيح') or"
                                        + " contains(.,'صالح'))]"))
                || driver.findElements(By.cssSelector("input[name='email'].is-invalid")).stream()
                        .anyMatch(WebElement::isDisplayed);
    }

    @Step("طلب حذف البنك المحدد بالاسم المطابق")
    public void RequestDeleteBankByName(String name) {
        records.RequestDeleteBankByName(name);
    }

    public boolean IsBankDeleteConfirmationDisplayed() {
        return records.IsBankDeleteConfirmationDisplayed();
    }

    @Step("تأكيد حذف بنك الاختبار")
    public void ConfirmBankDelete() {
        records.ConfirmBankDelete();
    }

    @Step("إلغاء حذف البنك")
    public void CancelBankDelete() {
        records.CancelBankDelete();
    }
    boolean recordVisible(By locator) { return visible(locator); }
    void recordWaitRows() { waitRows(); }
    void recordWaitAlertToDisappear() { waitAlertToDisappear(); }
    void recordDismissSaveSuccessIfPresent() { dismissSaveSuccessIfPresent(); }
}
