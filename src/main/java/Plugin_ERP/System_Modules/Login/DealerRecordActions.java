package Plugin_ERP.System_Modules.Login;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Dealer edit and delete actions, including their confirmation dialogs. */
final class DealerRecordActions {
    private final AbstractForDealer owner;
    private final WebDriver driver;

    DealerRecordActions(AbstractForDealer owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    private static final By DEALER_TABLE_ROWS = By.cssSelector("table tbody tr[role='row']");
    private static final By DEALER_SAVE_BUTTON = By.cssSelector("button[data-action-type='save']");
    private static final By DEALER_DELETE_CONFIRM =
            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm");
    private static final By DEALER_DELETE_CANCEL =
            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel");

    private boolean isElementDisplayed(WebElement element) {
        return owner.dealerElementDisplayed(element);
    }

    private boolean isVisible(By locator) {
        return owner.dealerControlVisible(locator);
    }

    private void showDealerBasicDataIfPresent() {
        owner.showDealerBasicData();
    }

    void openFirstDealerForEdit() {
        owner.rememberDealerListLocation();
        WebElement row =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                d ->
                                        d.findElements(DEALER_TABLE_ROWS).stream()
                                                .filter(this::isElementDisplayed)
                                                .filter(
                                                        candidate ->
                                                                candidate
                                                                        .findElements(
                                                                                By.cssSelector(
                                                                                        "button[data-action-type='edit']"))
                                                                        .stream()
                                                                        .anyMatch(
                                                                                this
                                                                                        ::isElementDisplayed))
                                                .findFirst()
                                                .orElse(null));
        clickDealerRowAction(row, "edit");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DEALER_SAVE_BUTTON));
        waitForDealerEditData();
    }

    void waitForDealerEditData() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                .until(d -> !owner.getDealerArabicNameValue().isBlank());
        showDealerBasicDataIfPresent();
    }

    void requestDeleteFirstDealer() {
        WebElement row =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                d ->
                                        d.findElements(DEALER_TABLE_ROWS).stream()
                                                .filter(this::isElementDisplayed)
                                                .filter(
                                                        candidate ->
                                                                candidate
                                                                        .findElements(
                                                                                By.cssSelector(
                                                                                        "button[data-action-type='delete']"))
                                                                        .stream()
                                                                        .anyMatch(
                                                                                this
                                                                                        ::isElementDisplayed))
                                                .findFirst()
                                                .orElse(null));
        clickDealerRowAction(row, "delete");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DEALER_DELETE_CANCEL));
    }

    void cancelDealerDelete() {
        WebElement cancel =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.visibilityOfElementLocated(DEALER_DELETE_CANCEL));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", cancel);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(DEALER_DELETE_CANCEL));
    }

    boolean isDealerDeleteConfirmationDisplayed() {
        return isVisible(DEALER_DELETE_CONFIRM) && isVisible(DEALER_DELETE_CANCEL);
    }

    void dismissDealerSaveSuccessIfPresent() {
        By successPopup = By.cssSelector(".swal2-container.swal2-backdrop-show");
        try {
            WebElement confirm =
                    new WebDriverWait(driver, Duration.ofSeconds(2))
                            .until(
                                    d ->
                                            d
                                                    .findElements(
                                                            By.cssSelector(
                                                                    ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                                                    .stream()
                                                    .filter(this::isElementDisplayed)
                                                    .findFirst()
                                                    .orElse(null));
            confirm.click();
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOfElementLocated(successPopup));
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "DealerRecordActions fallback: " + ignored.getClass().getSimpleName());

            // Successful saves may use a toast or close the dialog automatically.
        }
    }

    void openDealerForEditByName(String arabicName) {
        owner.rememberDealerListLocation();
        WebElement row =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                d ->
                                        d.findElements(DEALER_TABLE_ROWS).stream()
                                                .filter(this::isElementDisplayed)
                                                .filter(
                                                        candidate ->
                                                                candidate
                                                                        .findElements(
                                                                                By.cssSelector(
                                                                                        "td"))
                                                                        .stream()
                                                                        .anyMatch(
                                                                                cell ->
                                                                                        cell.getText()
                                                                                                .trim()
                                                                                                .equals(
                                                                                                        arabicName)))
                                                .findFirst()
                                                .orElse(null));
        clickDealerRowAction(row, "edit");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DEALER_SAVE_BUTTON));
        waitForDealerEditData();
    }

    void requestDealerDeleteByName(String arabicName) {
        WebElement row =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                d ->
                                        d.findElements(DEALER_TABLE_ROWS).stream()
                                                .filter(this::isElementDisplayed)
                                                .filter(
                                                        candidate ->
                                                                candidate
                                                                        .findElements(
                                                                                By.cssSelector(
                                                                                        "td"))
                                                                        .stream()
                                                                        .anyMatch(
                                                                                cell ->
                                                                                        cell.getText()
                                                                                                .trim()
                                                                                                .equals(
                                                                                                        arabicName)))
                                                .findFirst()
                                                .orElse(null));
        clickDealerRowAction(row, "delete");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DEALER_DELETE_CONFIRM));
    }

    void confirmDealerDeleteByName(String arabicName) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(DEALER_DELETE_CONFIRM))
                .click();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> owner.getExactDealerNameMatchCount(arabicName) == 0);
        By successPopup =
                By.xpath(
                        "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحذف')]");
        try {
            WebElement successButton =
                    new WebDriverWait(driver, Duration.ofSeconds(3))
                            .until(
                                    d ->
                                            d
                                                    .findElements(
                                                            By.cssSelector(
                                                                    ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                                                    .stream()
                                                    .filter(this::isElementDisplayed)
                                                    .filter(
                                                            button ->
                                                                    driver
                                                                            .findElements(
                                                                                    successPopup)
                                                                            .stream()
                                                                            .anyMatch(
                                                                                    this
                                                                                            ::isElementDisplayed))
                                                    .findFirst()
                                                    .orElse(null));
            successButton.click();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "DealerRecordActions fallback: " + ignored.getClass().getSimpleName());

            // Some deployments close the confirmation without a separate success dialog.
        }
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show"))
                                        .stream()
                                        .noneMatch(this::isElementDisplayed));
    }

    void clickDealerRowAction(WebElement row, String action) {
        WebElement button =
                row.findElement(By.cssSelector("button[data-action-type='" + action + "']"));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", button);
        try {
            button.click();
        } catch (org.openqa.selenium.ElementNotInteractableException exception) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "DealerRecordActions fallback: " + exception.getClass().getSimpleName());

            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", button);
        }
    }
}
