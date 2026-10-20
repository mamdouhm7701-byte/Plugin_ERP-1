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

public class ExpensesPage extends AbstractForDealer {
    private final By rows = By.cssSelector("table tbody tr[role='row']"),
            headers = By.cssSelector("table thead th"),
            save = By.cssSelector("button[data-action-type='save']");

    public ExpensesPage(WebDriver driver) {
        super(driver);
    }

    private boolean visible(By by) {
        return driver.findElements(by).stream().anyMatch(WebElement::isDisplayed);
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
            dismissSuccess();
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
        waitLoading();
    }

    private void dismissSuccess() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(
                            d -> {
                                List<WebElement> b =
                                        d.findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show .swal2-confirm"));
                                if (b.isEmpty()) return false;
                                try {
                                    ((org.openqa.selenium.JavascriptExecutor) d)
                                            .executeScript("arguments[0].click();", b.get(0));
                                    return true;
                                } catch (Exception e) {
                                    return false;
                                }
                            });
            waitAlert();
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
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

    @Step("إغلاق النموذج المفتوح إن وجد")
    public void CloseFormIfOpen() {
        ClickCloseButton();
    }

    @Step("إغلاق إشعار ظاهر")
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

    @Step("التحقق من بنية قائمة المصروفات")
    public boolean IsListStructureDisplayed() {
        List<String> h =
                driver.findElements(headers).stream()
                        .filter(WebElement::isDisplayed)
                        .map(e -> e.getText().trim())
                        .toList();
        boolean headings =
                h.stream().anyMatch(x -> x.contains("الكود"))
                        && h.stream().anyMatch(x -> x.contains("اسم المصروف"))
                        && h.stream().anyMatch(x -> x.contains("رقم الحساب"));
        List<WebElement> editable =
                driver.findElements(rows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                r ->
                                        !r.findElements(
                                                        By.cssSelector(
                                                                "button[data-action-type='edit']"))
                                                .isEmpty())
                        .toList();
        return headings
                && (editable.isEmpty()
                        || editable.stream()
                                .allMatch(
                                        r ->
                                                !r.findElements(
                                                                By.cssSelector(
                                                                        "button[data-action-type='delete']"))
                                                        .isEmpty()));
    }

    @Step("عناصر نموذج المصروف غير الظاهرة")
    public String GetMissingControls() {
        List<String> m = new java.util.ArrayList<>();
        if (!visible(By.cssSelector("input[name='code']"))) m.add("الكود");
        if (!visible(By.cssSelector("input[name='arabicName']"))) m.add("الاسم العربي");
        if (!visible(By.cssSelector("input[name='englishName']"))) m.add("الاسم الإنجليزي");
        if (!visible(By.cssSelector(".modal-body .vs__dropdown-toggle"))) m.add("رقم الحساب");
        if (driver.findElements(By.cssSelector("input[name='IsSharedBetweenBranches']")).isEmpty()
                || !visible(By.xpath("//label[normalize-space()='عام']"))) m.add("عام");
        if (!visible(save)) m.add("حفظ");
        if (!visible(By.xpath("//button[normalize-space()='إغلاق']"))) m.add("إغلاق");
        return String.join("، ", m);
    }

    @Step("عدد صفوف المصروفات القابلة للتعديل")
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

    private WebElement row(String n) {
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
                                                                                        .equals(n)))
                                        .findFirst()
                                        .orElse(null));
    }

    @Step("عدد السجلات المطابقة بالاسم الدقيق")
    public int GetExactMatchCount(String n) {
        int count = 0;
        for (WebElement r : driver.findElements(rows)) {
            try {
                if (!r.isDisplayed()) continue;
                for (WebElement c : r.findElements(By.cssSelector("td")))
                    if (c.getText().trim().equals(n)) {
                        count++;
                        break;
                    }
            } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            }
        }
        return count;
    }

    private int col(String title) {
        List<WebElement> h = driver.findElements(headers);
        for (int i = 0; i < h.size(); i++) if (h.get(i).getText().contains(title)) return i;
        throw new IllegalStateException("Expense column missing: " + title);
    }

    @Step("قراءة أول خلية في عمود المصروف")
    public String GetFirstCellByHeader(String title) {
        int i = col(title);
        WebElement r =
                driver.findElements(rows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                x ->
                                        !x.findElements(
                                                        By.cssSelector(
                                                                "button[data-action-type='edit']"))
                                                .isEmpty())
                        .findFirst()
                        .orElseThrow();
        return r.findElements(By.cssSelector("td")).get(i).getText().trim();
    }

    private void openEdit(WebElement r) {
        r.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(save));
    }

    @Step("فتح المصروف بالاسم للتعديل")
    public void OpenForEditByName(String n) {
        openEdit(row(n));
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

    public String GetSelectedAccountValue() {
        List<WebElement> x = driver.findElements(By.cssSelector(".modal-body .vs__selected"));
        return x.isEmpty() ? "" : x.get(0).getText().trim();
    }

    public boolean IsSharedChecked() {
        return driver.findElement(By.cssSelector("input[name='IsSharedBetweenBranches']"))
                .isSelected();
    }

    @Step("ضبط خيار عام")
    public void SetExpenseShared(boolean checked) {
        WebElement input =
                driver.findElement(By.cssSelector("input[name='IsSharedBetweenBranches']"));
        if (input.isSelected() != checked) {
            String id = input.getAttribute("id");
            WebElement label = driver.findElement(By.cssSelector("label[for='" + id + "']"));
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", label);
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(d -> input.isSelected() == checked);
        }
    }

    public boolean IsArabicNameRequiredDisplayed() {
        return visible(By.xpath("//*[contains(normalize-space(),'الاسم العربي مطلوب')]"))
                || visible(By.cssSelector("input[name='arabicName'].is-invalid"));
    }

    @Step("طلب حذف المصروف المملوك للاختبار")
    public void RequestDeleteByName(String n) {
        row(n).findElement(By.cssSelector("button[data-action-type='delete']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".swal2-popup")));
    }

    public boolean IsDeleteConfirmationDisplayed() {
        return visible(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"))
                && visible(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"));
    }

    @Step("تأكيد حذف المصروف المملوك للاختبار")
    public void ConfirmDelete() {
        WebElement yes =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                ExpectedConditions.elementToBeClickable(
                                        By.cssSelector(
                                                ".swal2-container.swal2-backdrop-show .swal2-confirm")));
        yes.click();
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
        dismissDeleteSuccess();
    }

    private void dismissDeleteSuccess() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath(
                                            "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم"
                                                    + " الحذف')]")));
            DismissAlertIfPresent();
        } catch (org.openqa.selenium.TimeoutException ignored) {
        }
    }

    public void CancelDelete() {
        driver.findElement(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"))
                .click();
        waitAlert();
    }
}
