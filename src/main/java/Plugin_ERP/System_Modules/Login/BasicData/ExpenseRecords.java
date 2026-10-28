/* شرح الملف: مساعدة لقراءة أنواع المصروفات وفتح النوع المطلوب للتعديل أو الحذف. فائدتها متابعة
 * بيانات المصروفات المسجلة وحساباتها.
 */
package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class ExpenseRecords {
    private final ExpensesPage owner;
    private final WebDriver driver;

    ExpenseRecords(ExpensesPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    private final By rows = By.cssSelector("table tbody tr[role='row']"),
            headers = By.cssSelector("table thead th"),
            save = By.cssSelector("button[data-action-type='save']");

    private boolean visible(By by) {
        return owner.recordVisible(by);
    }

    private void waitLoading() {
        owner.recordWaitLoading();
    }

    private void waitAlert() {
        owner.recordWaitAlert();
    }

    boolean IsListStructureDisplayed() {
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

    int GetEditableRowCount() {
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

    WebElement row(String n) {
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

    int GetExactMatchCount(String n) {
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
                Plugin_ERP.Bots.LogUtils.debug(
                        "ExpenseRecords fallback: " + ignored.getClass().getSimpleName());
            }
        }
        return count;
    }

    int col(String title) {
        List<WebElement> h = driver.findElements(headers);
        for (int i = 0; i < h.size(); i++) if (h.get(i).getText().contains(title)) return i;
        throw new IllegalStateException("Expense column missing: " + title);
    }

    String GetFirstCellByHeader(String title) {
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

    void openEdit(WebElement r) {
        r.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(save));
    }

    void OpenForEditByName(String n) {
        openEdit(row(n));
    }

    void RequestDeleteByName(String n) {
        row(n).findElement(By.cssSelector("button[data-action-type='delete']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".swal2-popup")));
    }

    boolean IsDeleteConfirmationDisplayed() {
        return visible(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"))
                && visible(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"));
    }

    void ConfirmDelete() {
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
                                                        Plugin_ERP.Bots.LogUtils.debug(
                                                                "ExpenseRecords condition unavailable: "
                                                                        + ignored.getClass()
                                                                                .getSimpleName());
                                                        return false;
                                                    }
                                                }));
        waitLoading();
        dismissDeleteSuccess();
    }

    void dismissDeleteSuccess() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath(
                                            "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم"
                                                    + " الحذف')]")));
            owner.DismissAlertIfPresent();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "ExpenseRecords fallback: " + ignored.getClass().getSimpleName());
        }
    }

    void CancelDelete() {
        driver.findElement(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"))
                .click();
        waitAlert();
    }
}
