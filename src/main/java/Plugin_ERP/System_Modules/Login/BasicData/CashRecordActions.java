package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Safe record search, editing and confirmed or cancelled deletion. */
final class CashRecordActions {
    private final CashPage owner;
    private final WebDriver driver;

    CashRecordActions(CashPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    private final By GridSearch = By.cssSelector("input[placeholder*='بحث']");
    private final By SafeRows = By.xpath("//tbody//tr[@role='row']");

    private void waitForCashPageLoading() {
        owner.awaitCashLoading();
    }

    private void waitForCashAlertToDisappear() {
        owner.awaitCashAlert();
    }

    void searchSafes(String searchText) {
        waitForCashPageLoading();
        if (owner.isCashCreationFormDisplayed()) owner.ClickCloseButton();
        waitForCashAlertToDisappear();
        WebElement search =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.visibilityOfElementLocated(GridSearch));
        search.clear();
        if (searchText != null && !searchText.isEmpty()) {
            search.sendKeys(searchText);
        }
        search.sendKeys(org.openqa.selenium.Keys.ENTER);
        waitForCashPageLoading();
        new GridProviderWait(driver, "safes").awaitReady();
    }

    boolean isSafeVisibleInSearchResults(String safeText) {
        new GridProviderWait(driver, "safes").awaitReady();
        return Boolean.TRUE.equals(
                ((JavascriptExecutor) driver)
                        .executeScript(
                                """
                return [...document.querySelectorAll('#mainTable tbody tr[role="row"]')]
                    .some(r=>r.getClientRects().length&&r.innerText.includes(arguments[0]));
                """,
                                safeText));
    }

    int getExactSafeMatchCount(String safeName) {
        return new GridProviderWait(driver, "safes").exactMatches(safeName);
    }

    void openSafeForEditByName(String safeName) {
        waitForCashPageLoading();
        WebElement row = findSafeRowByName(safeName);
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d.findElements(By.cssSelector("input[name='arabicName']")).stream()
                                        .anyMatch(
                                                e ->
                                                        e.isDisplayed()
                                                                && safeName.equals(
                                                                        e.getDomProperty(
                                                                                "value"))));
    }

    void requestDeleteSafeByName(String safeName) {
        waitForCashPageLoading();
        findSafeRowByName(safeName)
                .findElement(By.cssSelector("button[data-action-type='delete']"))
                .click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".swal2-popup")));
    }

    boolean isSafeDeleteConfirmationDisplayed() {
        boolean yesButtonVisible =
                driver
                        .findElements(
                                By.cssSelector(
                                        ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .anyMatch(button -> button.getText().trim().contains("نعم"));
        boolean cancelButtonInteractive =
                driver
                        .findElements(
                                By.cssSelector(
                                        ".swal2-container.swal2-backdrop-show .swal2-cancel"))
                        .stream()
                        .anyMatch(button -> button.isDisplayed() && button.isEnabled());
        return yesButtonVisible && cancelButtonInteractive;
    }

    void confirmSafeDelete() {
        WebElement confirm =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                d ->
                                        d
                                                .findElements(
                                                        By.cssSelector(
                                                                ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                                                .stream()
                                                .filter(WebElement::isDisplayed)
                                                .filter(
                                                        button ->
                                                                button.getText()
                                                                        .trim()
                                                                        .contains("نعم"))
                                                .findFirst()
                                                .orElse(null));
        confirm.click();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                                        .stream()
                                        .noneMatch(
                                                button ->
                                                        button.isDisplayed()
                                                                && button.getText()
                                                                        .trim()
                                                                        .contains("نعم")));
        waitForCashPageLoading();
        dismissCashDeleteSuccessPopupIfShown();
        waitForCashPageLoading();
    }

    void dismissCashDeleteSuccessPopupIfShown() {
        By successPopup =
                By.xpath(
                        "//div[contains(@class,'swal2-popup') and (contains(normalize-space(.),'تم الحذف بنجاح') or contains(normalize-space(.),'تم الحذف'))]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(successPopup));
            WebElement confirm =
                    new WebDriverWait(driver, Duration.ofSeconds(5))
                            .until(
                                    ExpectedConditions.elementToBeClickable(
                                            By.cssSelector(
                                                    ".swal2-container.swal2-backdrop-show .swal2-confirm")));
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", confirm);
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(
                            ExpectedConditions.invisibilityOfElementLocated(
                                    By.cssSelector(".swal2-container.swal2-backdrop-show")));
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "CashRecordActions fallback: " + ignored.getClass().getSimpleName());

            // Some application versions close the confirmation without a second popup.
        }
    }

    void cancelSafeDelete() {
        WebElement cancel =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                ExpectedConditions.elementToBeClickable(
                                        By.cssSelector(
                                                ".swal2-container.swal2-backdrop-show .swal2-cancel")));
        cancel.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show"))
                                        .stream()
                                        .noneMatch(WebElement::isDisplayed));
    }

    WebElement findSafeRowByName(String safeName) {
        return driver.findElements(SafeRows).stream()
                .filter(WebElement::isDisplayed)
                .filter(
                        row ->
                                row.findElements(By.cssSelector("td")).stream()
                                        .anyMatch(cell -> cell.getText().trim().equals(safeName)))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Test safe not found by exact name: " + safeName));
    }
}
