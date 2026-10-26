package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Distribution Records: focused operations used by the original page facade. */
final class DistributionRecords {
    private final DistributionEntityPage owner;
    private final WebDriver driver;

    DistributionRecords(DistributionEntityPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    private final By rows = By.cssSelector("table tbody tr[role='row']");
    private final By headers = By.cssSelector("table thead th");
    private final By search = By.cssSelector("input[placeholder*='بحث']");
    private final By save = By.cssSelector("button[data-action-type='save']");

    private boolean visible(By locator) {
        return owner.recordVisible(locator);
    }

    private void waitLoading() {
        owner.recordWaitLoading();
    }

    private void waitAlertToDisappear() {
        owner.recordWaitAlertToDisappear();
    }

    void Search(String text) {
        waitLoading();
        owner.ClickCloseButton();
        waitAlertToDisappear();
        WebElement field =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(search));
        field.clear();
        if (text != null && !text.isBlank()) field.sendKeys(text);
        field.sendKeys(org.openqa.selenium.Keys.ENTER);
        waitLoading();
    }

    boolean IsListStructureDisplayed() {
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

    int GetEditableRowCount() {
        new GridProviderWait(driver, "ExchangeDestinations").awaitReady();
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

    WebElement findRow(String name) {
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

    int GetExactMatchCount(String name) {
        return (int)
                driver.findElements(rows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                r ->
                                        r.findElements(By.cssSelector("td")).stream()
                                                .anyMatch(c -> c.getText().trim().equals(name)))
                        .count();
    }

    int columnIndex(String title) {
        List<WebElement> list = driver.findElements(headers);
        for (int i = 0; i < list.size(); i++) if (list.get(i).getText().contains(title)) return i;
        throw new IllegalStateException("Distribution Entity column not found: " + title);
    }

    String GetFirstCellByHeader(String title) {
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

    void openEdit(WebElement row) {
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(save));
    }

    void OpenFirstForEdit() {
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

    void OpenForEditByName(String name) {
        openEdit(findRow(name));
    }

    void RequestDeleteByName(String name) {
        findRow(name).findElement(By.cssSelector("button[data-action-type='delete']")).click();
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
                                                        Plugin_ERP.Bots.LogUtils.debug(
                                                                "DistributionRecords condition unavailable: "
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
            dismissSuccessIfPresent();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "DistributionRecords fallback: " + ignored.getClass().getSimpleName());
        }
    }

    void dismissSuccessIfPresent() {
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
            Plugin_ERP.Bots.LogUtils.debug(
                    "DistributionRecords fallback: " + ignored.getClass().getSimpleName());
        }
    }

    void CancelDelete() {
        driver.findElement(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"))
                .click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.invisibilityOfElementLocated(
                                By.cssSelector(".swal2-container.swal2-backdrop-show")));
    }
}
