/* شرح الملف: مساعدة لقراءة قائمة المتعاملين الرئيسيين وفتح السجل أو طلب حذفه. فائدتها تحديد
 * المتعامل المقصود ومراجعة بياناته المسجلة.
 */
package Plugin_ERP.System_Modules.Login.DealerModel;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class MainDealerRecords {
    private final MainDealer owner;
    private final WebDriver driver;

    MainDealerRecords(MainDealer owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    private static final By TABLE_ROWS = By.cssSelector("table tbody tr[role='row']");
    private static final By TABLE_HEADERS = By.cssSelector("table thead th");
    private static final By SAVE_BUTTON = By.cssSelector("button[data-action-type='save']");
    private static final By DELETE_CONFIRM =
            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm");
    private static final By DELETE_CANCEL =
            By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel");

    boolean IsListStructureDisplayed() {
        List<String> headers =
                driver.findElements(TABLE_HEADERS).stream()
                        .filter(MainDealerRecords::isDisplayed)
                        .map(element -> element.getText().trim())
                        .toList();

        return headers.stream().anyMatch(text -> text.contains("الكود"))
                && headers.stream().anyMatch(text -> text.contains("اسم المتعامل الرئيسي"))
                && headers.stream().anyMatch(text -> text.contains("النوع"));
    }

    int GetExactMatchCount(String arabicName) {
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
                Plugin_ERP.Bots.LogUtils.debug(
                        "MainDealerRecords fallback: " + ignored.getClass().getSimpleName());

                // The table may refresh between reading rows.
            }
        }
        return matches;
    }

    void OpenForEditByName(String arabicName) {
        dismissSuccessAlertIfPresent();
        WebElement row = findRowByName(arabicName);
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(SAVE_BUTTON));
    }

    void RequestDeleteByName(String arabicName) {
        dismissSuccessAlertIfPresent();
        WebElement row = findRowByName(arabicName);
        row.findElement(By.cssSelector("button[data-action-type='delete']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(DELETE_CONFIRM));
    }

    void ConfirmDeleteByName(String arabicName) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(DELETE_CONFIRM))
                .click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        driver ->
                                driver.findElements(DELETE_CANCEL).stream()
                                        .noneMatch(MainDealerRecords::isDisplayed));
        dismissDeleteSuccessIfPresent();
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(driver -> owner.GetExactMatchCount(arabicName) == 0);
    }

    void CancelDelete() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(DELETE_CANCEL))
                .click();
        waitForAlertToDisappear();
    }

    void ExportList(String format) {
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
                                                .filter(MainDealerRecords::isDisplayed)
                                                .findFirst()
                                                .orElse(null));
        button.click();
    }

    WebElement findRowByName(String arabicName) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d.findElements(TABLE_ROWS).stream()
                                        .filter(MainDealerRecords::isDisplayed)
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

    boolean isDisplayed(By locator) {
        return driver.findElements(locator).stream().anyMatch(MainDealerRecords::isDisplayed);
    }

    static boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "MainDealerRecords condition unavailable: "
                            + ignored.getClass().getSimpleName());
            return false;
        }
    }

    void waitForLoadingToFinish() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        "div.b-overlay,div.position-absolute.bg-light.rounded-lg,.loading-bg,.spinner-border"))
                                        .stream()
                                        .noneMatch(MainDealerRecords::isDisplayed));
    }

    void waitForAlertToDisappear() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show"))
                                        .stream()
                                        .noneMatch(MainDealerRecords::isDisplayed));
    }

    void dismissDeleteSuccessIfPresent() {
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
            Plugin_ERP.Bots.LogUtils.debug(
                    "MainDealerRecords fallback: " + ignored.getClass().getSimpleName());

            // Some versions remove the row without showing a success dialog.
        }
    }

    void dismissSuccessAlertIfPresent() {
        try {
            WebElement confirm =
                    new WebDriverWait(driver, Duration.ofSeconds(2))
                            .until(ExpectedConditions.visibilityOfElementLocated(DELETE_CONFIRM));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirm);
            waitForAlertToDisappear();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "MainDealerRecords fallback: " + ignored.getClass().getSimpleName());

            // No success confirmation is shown by some tenant versions.
        }
    }
}
