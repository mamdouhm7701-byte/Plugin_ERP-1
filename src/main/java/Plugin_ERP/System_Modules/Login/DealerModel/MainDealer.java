package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MainDealer extends AbstractForDealer {
    private final MainDealerRecords records;
    private static final By TABLE_ROWS = By.cssSelector("table tbody tr[role='row']");
    private static final By TABLE_HEADERS = By.cssSelector("table thead th");
    private static final By SAVE_BUTTON = By.cssSelector("button[data-action-type='save']");
    private static final By DELETE_CONFIRM =
            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm");
    private static final By DELETE_CANCEL =
            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel");

    public MainDealer(WebDriver driver) {
        super(driver);
        records = new MainDealerRecords(this, driver);
    }

    @Step("فتح صفحة المتعامل الرئيسي")
    public void OpenMainDealerModel() {
        NavigationHelper(Pages.MAINDEALERS);
        waitForLoadingToFinish();
    }

    @Step("التحقق من بنية قائمة المتعامل الرئيسي")
    public boolean IsListStructureDisplayed() {
        return records.IsListStructureDisplayed();
    }

    @Step("الحقول والأزرار الناقصة من نموذج المتعامل الرئيسي")
    public String GetMissingFormControls() {
        List<String> missing = new java.util.ArrayList<>();
        addMissing(missing, "الكود", By.cssSelector("input[name='code']"));
        addMissing(missing, "الاسم العربي", By.cssSelector("input[name='arabicName']"));
        addMissing(missing, "الاسم الإنجليزي", By.cssSelector("input[name='englishName']"));
        addMissing(missing, "النوع", By.cssSelector("[name='type'] .vs__dropdown-toggle"));
        addMissing(missing, "الملاحظات", By.cssSelector("textarea"));
        addMissing(missing, "حفظ", SAVE_BUTTON);
        return String.join("، ", missing);
    }

    private void addMissing(List<String> missing, String label, By locator) {
        if (driver.findElements(locator).stream().noneMatch(MainDealer::isDisplayed)) {
            missing.add(label);
        }
    }

    @Step("عدد النتائج المطابقة للاسم العربي بدقة")
    public int GetExactMatchCount(String arabicName) {
        return records.GetExactMatchCount(arabicName);
    }

    @Step("فتح المتعامل الرئيسي المطابق للاسم للتعديل")
    public void OpenForEditByName(String arabicName) {
        records.OpenForEditByName(arabicName);
    }

    @Step("قراءة الاسم العربي من نموذج المتعامل الرئيسي")
    public String GetArabicNameValue() {
        return driver.findElement(By.cssSelector("input[name='arabicName']"))
                .getAttribute("value")
                .trim();
    }

    @Step("قراءة الاسم الإنجليزي من نموذج المتعامل الرئيسي")
    public String GetEnglishNameValue() {
        return driver.findElement(By.cssSelector("input[name='englishName']"))
                .getAttribute("value")
                .trim();
    }

    @Step("قراءة نوع المتعامل المحدد")
    public String GetSelectedKindValue() {
        List<WebElement> selected =
                driver.findElements(By.cssSelector("[name='type'] .vs__selected"));
        return selected.isEmpty() ? "" : selected.get(0).getText().trim();
    }

    @Step("طلب حذف المتعامل الرئيسي المحدد")
    public void RequestDeleteByName(String arabicName) {
        records.RequestDeleteByName(arabicName);
    }

    @Step("تأكيد حذف المتعامل الرئيسي المحدد")
    public void ConfirmDeleteByName(String arabicName) {
        records.ConfirmDeleteByName(arabicName);
    }

    @Step("إلغاء حذف المتعامل الرئيسي")
    public void CancelDelete() {
        records.CancelDelete();
    }

    @Step("التحقق من رسالة إلزامية الاسم العربي")
    public boolean IsArabicNameRequiredDisplayed() {
        return isDisplayed(By.xpath("//*[contains(normalize-space(),'الاسم العربي مطلوب')]"))
                || isDisplayed(By.cssSelector("input[name='arabicName'].is-invalid"));
    }

    @Step("التحقق من رسالة إلزامية نوع المتعامل")
    public boolean IsKindRequiredDisplayed() {
        return isDisplayed(By.xpath("//*[contains(normalize-space(),'النوع مطلوب')]"))
                || isDisplayed(By.cssSelector(".modal-body .vs__dropdown-toggle.is-invalid"));
    }

    @Step("تصدير قائمة المتعامل الرئيسي بصيغة {format}")
    public void ExportList(String format) {
        records.ExportList(format);
    }

    private WebElement findRowByName(String arabicName) {
        return records.findRowByName(arabicName);
    }

    private boolean isDisplayed(By locator) {
        return records.isDisplayed(locator);
    }

    private static boolean isDisplayed(WebElement element) {
        return MainDealerRecords.isDisplayed(element);
    }

    private void waitForLoadingToFinish() {
        records.waitForLoadingToFinish();
    }

    private void waitForAlertToDisappear() {
        records.waitForAlertToDisappear();
    }

    private void dismissDeleteSuccessIfPresent() {
        records.dismissDeleteSuccessIfPresent();
    }

    private void dismissSuccessAlertIfPresent() {
        records.dismissSuccessAlertIfPresent();
    }
}
