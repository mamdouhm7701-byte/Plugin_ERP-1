package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MainDealer extends AbstractForDealer {
    private static final By TABLE_ROWS = By.cssSelector("table tbody tr[role='row']");
    private static final By TABLE_HEADERS = By.cssSelector("table thead th");
    private static final By SAVE_BUTTON = By.cssSelector("button[data-action-type='save']");
    private static final By DELETE_CONFIRM =
            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm");
    private static final By DELETE_CANCEL =
            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel");

    public MainDealer(WebDriver driver) {
        super(driver);
    }

    @Step("فتح صفحة المتعامل الرئيسي")
    public void OpenMainDealerModel() {
        NavigationHelper(Pages.MAINDEALERS);
        waitForLoadingToFinish();
    }

    @Step("التحقق من بنية قائمة المتعامل الرئيسي")
    public boolean IsListStructureDisplayed() {
        List<String> headers =
                driver.findElements(TABLE_HEADERS).stream()
                        .filter(MainDealer::isDisplayed)
                        .map(element -> element.getText().trim())
                        .toList();

        return headers.stream().anyMatch(text -> text.contains("الكود"))
                && headers.stream().anyMatch(text -> text.contains("اسم المتعامل الرئيسي"))
                && headers.stream().anyMatch(text -> text.contains("النوع"));
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
        int matches = 0;
        for (WebElement row : driver.findElements(TABLE_ROWS)) {
            try {
                if (!row.isDisplayed()) {
                    continue;
                }
                boolean match =
                        row.findElements(By.cssSelector("td")).stream()
                                .anyMatch(cell -> cell.getText().trim().equals(arabicName));
                if (match) {
                    matches++;
                }
            } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                // The table may refresh between reading rows.
            }
        }
        return matches;
    }

    @Step("فتح المتعامل الرئيسي المطابق للاسم للتعديل")
    public void OpenForEditByName(String arabicName) {
        dismissSuccessAlertIfPresent();
        WebElement row = findRowByName(arabicName);
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(SAVE_BUTTON));
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

    @Step("طلب حذف المتعامل الرئيسي المملوك للاختبار")
    public void RequestDeleteByName(String arabicName) {
        dismissSuccessAlertIfPresent();
        WebElement row = findRowByName(arabicName);
        row.findElement(By.cssSelector("button[data-action-type='delete']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DELETE_CONFIRM));
    }

    @Step("تأكيد حذف المتعامل الرئيسي المملوك للاختبار")
    public void ConfirmDeleteByName(String arabicName) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(DELETE_CONFIRM))
                .click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        driver ->
                                driver.findElements(DELETE_CANCEL).stream()
                                        .noneMatch(MainDealer::isDisplayed));
        dismissDeleteSuccessIfPresent();
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(driver -> GetExactMatchCount(arabicName) == 0);
    }

    @Step("إلغاء حذف المتعامل الرئيسي")
    public void CancelDelete() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(DELETE_CANCEL))
                .click();
        waitForAlertToDisappear();
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
        String normalizedFormat = format == null ? "" : format.trim().toLowerCase();
        if (!List.of("pdf", "xls", "xlsx", "excel").contains(normalizedFormat)) {
            throw new IllegalArgumentException("Supported export formats are PDF and Excel");
        }

        String imageName = normalizedFormat.equals("pdf") ? "pdf." : "xls.";
        By exactButton = By.cssSelector("button:has(img[src*='" + imageName + "'])");
        WebElement button =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                d ->
                                        d.findElements(exactButton).stream()
                                                .filter(MainDealer::isDisplayed)
                                                .findFirst()
                                                .orElse(null));
        button.click();
    }

    private WebElement findRowByName(String arabicName) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d.findElements(TABLE_ROWS).stream()
                                        .filter(MainDealer::isDisplayed)
                                        .filter(
                                                row ->
                                                        row
                                                                .findElements(By.cssSelector("td"))
                                                                .stream()
                                                                .anyMatch(
                                                                        cell ->
                                                                                cell.getText()
                                                                                        .trim()
                                                                                        .equals(
                                                                                                arabicName)))
                                        .findFirst()
                                        .orElse(null));
    }

    private boolean isDisplayed(By locator) {
        return driver.findElements(locator).stream().anyMatch(MainDealer::isDisplayed);
    }

    private static boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            return false;
        }
    }

    private void waitForLoadingToFinish() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        "div.b-overlay,div.position-absolute.bg-light.rounded-lg,.loading-bg,.spinner-border"))
                                        .stream()
                                        .noneMatch(MainDealer::isDisplayed));
    }

    private void waitForAlertToDisappear() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show"))
                                        .stream()
                                        .noneMatch(MainDealer::isDisplayed));
    }

    private void dismissDeleteSuccessIfPresent() {
        By success =
                By.xpath(
                        "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحذف')]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(success));
            List<WebElement> buttons =
                    driver.findElements(
                            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"));
            if (!buttons.isEmpty()) {
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].click();", buttons.get(0));
            }
            waitForAlertToDisappear();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // Some versions remove the row without showing a success dialog.
        }
    }

    private void dismissSuccessAlertIfPresent() {
        try {
            WebElement confirm =
                    new WebDriverWait(driver, Duration.ofSeconds(2))
                            .until(ExpectedConditions.visibilityOfElementLocated(DELETE_CONFIRM));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirm);
            waitForAlertToDisappear();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // No success confirmation is shown by some tenant versions.
        }
    }
}
