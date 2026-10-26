package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Warehouse Records: focused operations used by the original page facade. */
final class WarehouseRecords {
    private final StorePage owner;
    private final WebDriver driver;

    WarehouseRecords(StorePage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    private final By rows = By.cssSelector("table tbody tr[role='row']"),
            headers = By.cssSelector("table thead th"),
            save = By.cssSelector("button[data-action-type='save']");

    private boolean visible(By b) {
        return owner.recordVisible(b);
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
        return h.stream().anyMatch(x -> x.contains("الكود"))
                && h.stream().anyMatch(x -> x.contains("اسم المخزن"))
                && h.stream().anyMatch(x -> x.contains("رقم الحساب"));
    }

    int GetExactMatchCount(String n) {
        int c = 0;
        for (WebElement r : driver.findElements(rows))
            try {
                if (!r.isDisplayed()) continue;
                for (WebElement td : r.findElements(By.cssSelector("td")))
                    if (td.getText().trim().equals(n)) {
                        c++;
                        break;
                    }
            } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "WarehouseRecords fallback: " + ignored.getClass().getSimpleName());
            }
        return c;
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
                                                                        td ->
                                                                                td.getText()
                                                                                        .trim()
                                                                                        .equals(n)))
                                        .findFirst()
                                        .orElse(null));
    }

    void OpenForEditByName(String n) {
        row(n).findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(save));
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

    void ConfirmDelete(String name) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        ".swal2-container.swal2-backdrop-show .swal2-confirm")))
                .click();
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
                                                                "WarehouseRecords condition unavailable: "
                                                                        + ignored.getClass()
                                                                                .getSimpleName());
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
            owner.DismissAlertIfPresent();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "WarehouseRecords fallback: " + ignored.getClass().getSimpleName());
        }
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> owner.GetExactMatchCount(name) == 0);
    }

    void CancelDelete() {
        driver.findElement(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"))
                .click();
        waitAlert();
    }
}
