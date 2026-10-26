package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Bank Records: focused operations used by the original page facade. */
final class BankRecords {
    private final BankPage owner;
    private final WebDriver driver;

    BankRecords(BankPage owner, WebDriver driver) {
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

    private void waitRows() {
        owner.recordWaitRows();
    }

    private void waitAlertToDisappear() {
        owner.recordWaitAlertToDisappear();
    }

    private void dismissSaveSuccessIfPresent() {
        owner.recordDismissSaveSuccessIfPresent();
    }

    void SearchBank(String value) {
        waitRows();
        By closeForm = By.xpath("//button[normalize-space()='إغلاق']");
        if (visible(closeForm)) owner.ClickCloseButton();
        waitAlertToDisappear();
        WebElement input =
                new WebDriverWait(driver, Duration.ofSeconds(15))
                        .until(ExpectedConditions.elementToBeClickable(search));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", input);
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(input).click().perform();
        input.clear();
        if (value != null && !value.isBlank()) input.sendKeys(value);
        input.sendKeys(Keys.ENTER);
        waitRows();
    }

    boolean IsBankListStructureDisplayed() {
        List<String> text =
                driver.findElements(headers).stream()
                        .filter(WebElement::isDisplayed)
                        .map(e -> e.getText().trim())
                        .toList();
        return text.stream().anyMatch(v -> v.contains("الكود"))
                && text.stream().anyMatch(v -> v.contains("اسم البنك"))
                && text.stream().anyMatch(v -> v.contains("رقم الحساب"))
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

    int GetEditableBankCount() {
        new GridProviderWait(driver, "banks").awaitReady();
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
                                                                                        .equals(
                                                                                                name)))
                                        .findFirst()
                                        .orElse(null));
    }

    int GetExactBankMatchCount(String name) {
        return new GridProviderWait(driver, "banks").exactMatches(name);
    }

    int columnIndex(String label) {
        List<WebElement> list = driver.findElements(headers);
        for (int i = 0; i < list.size(); i++) if (list.get(i).getText().contains(label)) return i;
        throw new IllegalStateException("Bank column not found: " + label);
    }

    String GetFirstBankCellByHeader(String label) {
        int i = columnIndex(label);
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
                        .orElseThrow(() -> new IllegalStateException("No editable Bank row"));
        return row.findElements(By.cssSelector("td")).get(i).getText().trim();
    }

    void OpenFirstBankForEdit() {
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
                        .orElseThrow(() -> new IllegalStateException("No editable Bank row"));
        openEdit(row);
    }

    void OpenBankForEditByName(String name) {
        openEdit(findRow(name));
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

    void openEdit(WebElement row) {
        row.findElement(By.cssSelector("button[data-action-type='edit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(save));
    }

    void RequestDeleteBankByName(String name) {
        findRow(name).findElement(By.cssSelector("button[data-action-type='delete']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".swal2-popup")));
    }

    boolean IsBankDeleteConfirmationDisplayed() {
        return visible(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-confirm"))
                && visible(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"));
    }

    void ConfirmBankDelete() {
        WebElement confirm =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                ExpectedConditions.elementToBeClickable(
                                        By.cssSelector(
                                                ".swal2-container.swal2-backdrop-show .swal2-confirm")));
        confirm.click();
        waitRows();
        By success =
                By.xpath(
                        "//div[contains(@class,'swal2-popup') and contains(normalize-space(.),'تم الحذف')]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(success));
            dismissSaveSuccessIfPresent();
        } catch (org.openqa.selenium.TimeoutException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "BankRecords fallback: " + ignored.getClass().getSimpleName());
        }
    }

    void CancelBankDelete() {
        driver.findElement(By.cssSelector(".swal2-container.swal2-backdrop-show .swal2-cancel"))
                .click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.invisibilityOfElementLocated(
                                By.cssSelector(".swal2-container.swal2-backdrop-show")));
    }
}
