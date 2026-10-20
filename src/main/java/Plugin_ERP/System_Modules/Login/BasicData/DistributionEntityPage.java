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
    private final By rows = By.cssSelector("table tbody tr[role='row']");
    private final By headers = By.cssSelector("table thead th");
    private final By search = By.cssSelector("input[placeholder*='بحث']");
    private final By save = By.cssSelector("button[data-action-type='save']");

    public DistributionEntityPage(WebDriver driver) {
        super(driver);
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
                                                        return false;
                                                    }
                                                }));
    }

    @Override
    public void ClickNew() {
        waitLoading();
        super.ClickNew();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(save));
    }

    @Override
    public void ClickCloseButton() {
        if (visible(By.xpath("//button[normalize-space()='إغلاق']"))) super.ClickCloseButton();
    }

    @Override
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
                                    return false;
                                }
                            });
            waitAlertToDisappear();
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
        waitLoading();
    }

    @Step("إغلاق نموذج جهة الصرف المفتوح إن وجد")
    public void CloseFormIfOpen() {
        ClickCloseButton();
    }

    @Step("البحث في قائمة جهات الصرف")
    public void Search(String text) {
        waitLoading();
        ClickCloseButton();
        waitAlertToDisappear();
        WebElement field =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(search));
        field.clear();
        if (text != null && !text.isBlank()) field.sendKeys(text);
        field.sendKeys(org.openqa.selenium.Keys.ENTER);
        waitLoading();
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

    @Step("التحقق من أعمدة قائمة جهات الصرف وإجراءاتها")
    public boolean IsListStructureDisplayed() {
        List<String> labels =
                driver.findElements(headers).stream()
                        .filter(WebElement::isDisplayed)
                        .map(e -> e.getText().trim())
                        .toList();
        return labels.stream().anyMatch(s -> s.contains("الكود"))
                && labels.stream().anyMatch(s -> s.contains("اسم جهة الصرف"))
                && driver.findElements(rows).stream()
                        .filter(WebElement::isDisplayed)
                        .anyMatch(
                                r ->
                                        !r.findElements(
                                                                By.cssSelector(
                                                                        "button[data-action-type='edit']"))
                                                        .isEmpty()
                                                && !r.findElements(
                                                                By.cssSelector(
                                                                        "button[data-action-type='delete']"))
                                                        .isEmpty());
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
        return (int)
                driver.findElements(rows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                r ->
                                        !r.findElements(
                                                        By.cssSelector(
                                                                "button[data-action-type='edit']"))
                                                .isEmpty())
                        .count();
    }

    private WebElement findRow(String name) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d.findElements(rows).stream()
                                        .filter(WebElement::isDisplayed)
                                        .filter(
                                                r ->
                                                        r
                                                                .findElements(By.cssSelector("td"))
                                                                .stream()
                                                                .anyMatch(
                                                                        c ->
                                                                                c.getText()
                                                                                        .trim()
                                                                                        .equals(
                                                                                                name)))
                                        .findFirst()
                                        .orElse(null));
    }

    @Step("عدد النتائج المطابقة بالاسم الدقيق")
    public int GetExactMatchCount(String name) {
        return (int)
                driver.findElements(rows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                r ->
                                        r.findElements(By.cssSelector("td")).stream()
                                                .anyMatch(c -> c.getText().trim().equals(name)))
                        .count();
    }

    private int columnIndex(String title) {
        List<WebElement> list = driver.findElements(headers);
        for (int i = 0; i < list.size(); i++) if (list.get(i).getText().contains(title)) return i;
        throw new IllegalStateException("Distribution Entity column not found: " + title);
    }

    @Step("قراءة أول خلية في عمود جهة الصرف")
    public String GetFirstCellByHeader(String title) {
        int index = columnIndex(title);
        WebElement row =
                driver.findElements(rows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                r ->
                                        !r.findElements(
                                                        By.cssSelector(
                                                                "button[data-action-type='edit']"))
                                                .isEmpty())
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "No editable Distribution Entity row"));
        return row.findElements(By.cssSelector("td")).get(index).getText().trim();
    }

    private void openEdit(WebElement row) {
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(save));
    }

    @Step("فتح أول جهة صرف للتعديل")
    public void OpenFirstForEdit() {
        WebElement row =
                driver.findElements(rows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                r ->
                                        !r.findElements(
                                                        By.cssSelector(
                                                                "button[data-action-type='edit']"))
                                                .isEmpty())
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "No editable Distribution Entity row"));
        openEdit(row);
    }

    @Step("فتح جهة الصرف المطابقة بالاسم للتعديل")
    public void OpenForEditByName(String name) {
        openEdit(findRow(name));
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

    @Step("طلب حذف جهة الصرف المملوكة للاختبار")
    public void RequestDeleteByName(String name) {
        findRow(name).findElement(By.cssSelector("button[data-action-type='delete']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".swal2-popup")));
    }

    public boolean IsDeleteConfirmationDisplayed() {
        return visible(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"))
                && visible(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"));
    }

    @Step("تأكيد حذف جهة الصرف المملوكة للاختبار")
    public void ConfirmDelete() {
        WebElement confirm =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                ExpectedConditions.elementToBeClickable(
                                        By.cssSelector(
                                                ".swal2-container.swal2-backdrop-show .swal2-confirm")));
        confirm.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show .swal2-cancel"))
                                        .stream()
                                        .noneMatch(
                                                e -> {
                                                    try {
                                                        return e.isDisplayed();
                                                    } catch (Exception ignored) {
                                                        return false;
                                                    }
                                                }));
        waitLoading();
        By success =
                By.xpath(
                        "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحذف')]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(success));
            dismissSuccessIfPresent();
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
    }

    private void dismissSuccessIfPresent() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath(
                                            "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم"
                                                    + " الحذف')]")));
            List<WebElement> buttons =
                    driver.findElements(
                            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"));
            if (!buttons.isEmpty())
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("arguments[0].click();", buttons.get(0));
            waitAlertToDisappear();
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
    }

    public void CancelDelete() {
        driver.findElement(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"))
                .click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.invisibilityOfElementLocated(
                                By.cssSelector(".swal2-container.swal2-backdrop-show")));
    }
}
