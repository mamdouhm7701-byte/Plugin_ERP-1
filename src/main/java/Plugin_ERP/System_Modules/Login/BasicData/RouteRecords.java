package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Route Records: focused operations used by the original page facade. */
final class RouteRecords {
    private final RoutLine owner;
    private final WebDriver driver;

    RouteRecords(RoutLine owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    private final By rows = By.cssSelector("table tbody tr[role='row']");
    private final By headers = By.cssSelector("table thead th");
    private final By save = By.cssSelector("button[data-action-type='save']");

    private boolean visible(By locator) {
        return owner.recordVisible(locator);
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
                h.stream().anyMatch(s -> s.contains("الكود"))
                        && h.stream().anyMatch(s -> s.contains("حالة خط السير"))
                        && h.stream().anyMatch(s -> s.contains("نوع المستند"));
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

    int GetExactMatchCount(String n) {
        return new GridProviderWait(driver, "AgentItinerariesStatus").exactMatches(n);
    }

    WebElement row(String n) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .ignoring(StaleElementReferenceException.class)
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

    void OpenForEditByName(String n) {
        row(n).findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d.findElements(By.cssSelector("input[name='arabicName']")).stream()
                                        .anyMatch(
                                                e ->
                                                        e.isDisplayed()
                                                                && n.equals(
                                                                        e.getDomProperty(
                                                                                "value"))));
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
                                                                "RouteRecords condition unavailable: "
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
                    "RouteRecords fallback: " + ignored.getClass().getSimpleName());
        }
    }

    void CancelDelete() {
        driver.findElement(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"))
                .click();
        waitAlert();
    }
}
