/* شرح الملف: مساعدة للبحث عن منطقة وتعديلها أو حذفها مع مراجعة التأكيد. فائدتها إدارة المناطق
 * المسجلة بالاسم الصحيح.
 */
package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class AreaRecordActions {
    private final AreaPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;

    AreaRecordActions(AreaPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }

    private final By searchInput = By.cssSelector("input[placeholder='بحث ....']");
    private final By areaRows = By.cssSelector("table tbody tr[role='row']");
    private final By saveButton = By.cssSelector("button[data-action-type='save']");

    private void waitForAreaRows() {
        owner.recordWaitForAreaRows();
    }

    void SearchArea(String name) {
        utilites.waits().waitForOverlayToDisappear();
        WebElement input =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        input.sendKeys(name);
        input.sendKeys(Keys.ENTER);
        waitForAreaRows();
    }

    void EditAreaByName(String name) {
        WebElement row = findAreaRow(name);
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                d.findElements(By.cssSelector("input[name='arabicName']")).stream()
                                        .anyMatch(
                                                e ->
                                                        e.isDisplayed()
                                                                && name.equals(
                                                                        e.getDomProperty(
                                                                                "value"))));
    }

    void DeleteAreaByName(String name) {
        owner.RequestDeleteAreaByName(name);
        owner.ConfirmAreaDelete();
    }

    boolean IsAreaDisplayed(String name) {
        return driver.findElements(areaRows).stream().anyMatch(row -> row.getText().contains(name));
    }

    WebElement findAreaRow(String name) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .ignoring(StaleElementReferenceException.class)
                .until(
                        d ->
                                d.findElements(areaRows).stream()
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
                                                                                                name)))
                                        .findFirst()
                                        .orElse(null));
    }

    void OpenFirstAreaForEdit() {
        WebElement row =
                driver.findElements(areaRows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                candidate ->
                                        !candidate
                                                .findElements(
                                                        By.cssSelector(
                                                                "button[data-action-type='edit']"))
                                                .isEmpty())
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "No editable Area row is displayed"));
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(saveButton));
    }

    void RequestDeleteAreaByName(String name) {
        findAreaRow(name).findElement(By.cssSelector("button[data-action-type='delete']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".swal2-popup")));
    }

    boolean IsAreaDeleteConfirmationDisplayed() {
        return driver
                        .findElements(
                                By.cssSelector(
                                        ".swal2-container.swal2-backdrop-show .swal2-confirm"))
                        .stream()
                        .anyMatch(b -> b.isDisplayed() && b.getText().contains("نعم"))
                && driver
                        .findElements(
                                By.cssSelector(
                                        ".swal2-container.swal2-backdrop-show .swal2-cancel"))
                        .stream()
                        .anyMatch(b -> b.isDisplayed() && b.isEnabled());
    }

    void ConfirmAreaDelete() {
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
                                                .filter(b -> b.getText().contains("نعم"))
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
                                                b ->
                                                        b.isDisplayed()
                                                                && b.getText().contains("نعم")));
        waitForAreaRows();
        dismissDeleteSuccessPopup();
    }

    void CancelAreaDelete() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        ".swal2-container.swal2-backdrop-show .swal2-cancel")))
                .click();
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

    void dismissDeleteSuccessPopup() {
        try {
            WebElement ok =
                    new WebDriverWait(driver, Duration.ofSeconds(3))
                            .until(
                                    ExpectedConditions.elementToBeClickable(
                                            By.cssSelector(
                                                    ".swal2-container.swal2-backdrop-show .swal2-confirm")));
            if (ok.getText().contains("نعم")) return;
            ok.click();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "AreaRecordActions fallback: " + ignored.getClass().getSimpleName());
        }
    }
}
