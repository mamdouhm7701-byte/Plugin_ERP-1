package Plugin_ERP.Bots;

import Plugin_ERP.Utiel.PropertyReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class WaitActions extends BaseAction {

    public WaitActions(WebDriver driver) {
        this(driver, new WebDriverWait(driver, Duration.ofSeconds(10)), new WaitMangment(driver));
    }

    public WaitActions(WebDriver driver, WebDriverWait wait, WaitMangment waitMangment) {
        super(driver, wait, waitMangment);
    }

    public void waitForUrlContains(String text) {
        waitMangment.fluentWait().until(ExpectedConditions.urlContains(text));
    }

    public void waitForElementVisible(By locator) {
        waitMangment.fluentWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void WaitForElementVisible(By locator) {
        new WebDriverWait(
                        driver,
                        Duration.ofSeconds(
                                Long.parseLong(PropertyReader.getProperty("defultwait"))))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitForReportReady(int timeoutSec) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

        // 1. Page ready
        wait.until(
                webDriver ->
                        ((JavascriptExecutor) webDriver)
                                .executeScript("return document.readyState")
                                .equals("complete"));

        // 2. Spinner يختفي
        List<By> loaders =
                List.of(
                        By.cssSelector(".e-reportviewer-progressbar"),
                        By.cssSelector(".e-reportviewer-loadingindicator"),
                        By.cssSelector("[class*='loading']"),
                        By.cssSelector("[class*='spinner']"));
        for (By loader : loaders) {
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(loader));
            } catch (Exception ignored) {
            }
        }

        // 3. استنى أي عنصر فيه بيانات في التقرير
        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(
                                "div.e-reportviewer-paragrap div[style*='white-space: pre-wrap']")));

        // 4. استنى إن في عنصر فيه نص فعلي مش فاضي
        wait.until(
                webDriver -> {
                    try {
                        List<WebElement> elements =
                                webDriver.findElements(
                                        By.cssSelector(
                                                "div.e-reportviewer-paragrap div[style*='white-space: pre-wrap']"));
                        return elements.stream().anyMatch(el -> !el.getText().trim().isEmpty());
                    } catch (Exception e) {
                        return false;
                    }
                });

    }

    public void switchToNewTab() {
        switchToNewTab(Set.of(driver.getWindowHandle()));
    }

    public void switchToNewTab(Set<String> originalTabs) {
        Set<String> baseline = Set.copyOf(originalTabs);

        // استنى لحد ما يظهر tab جديد — زودنا الوقت لـ 30 ثانية
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        String created = wait.until(d -> {
            var candidates = d.getWindowHandles().stream().filter(h -> !baseline.contains(h)).toList();
            if (candidates.size() > 1) throw new IllegalStateException("Ambiguous new report tabs: " + candidates);
            return candidates.size() == 1 ? candidates.get(0) : null;
        });

        // انتقل للـ tab الجديد
        driver.switchTo().window(created);

        // استنى الـ URL يحتوي على ReportViewer
        wait.until(ExpectedConditions.urlContains("ReportViewer"));
    }

    public void waitForOverlayToDisappear() {
        By overlay = By.cssSelector("div.b-overlay");
        final long[] hiddenSince = {-1};
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(200))
                .until(d -> {
                    try {
                        boolean visible = d.findElements(overlay).stream().anyMatch(WebElement::isDisplayed);
                        if (visible) { hiddenSince[0] = -1; return false; }
                        long now = System.nanoTime();
                        if (hiddenSince[0] < 0) hiddenSince[0] = now;
                        return now - hiddenSince[0] >= Duration.ofSeconds(1).toNanos();
                    } catch (StaleElementReferenceException stale) {
                        hiddenSince[0] = -1;
                        return false;
                    }
                });
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(d ->
                "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    public void waitForScreenReady(By locator) {

        waitForOverlayToDisappear();

        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

        wait.until(ExpectedConditions.elementToBeClickable(locator));

        waitForOverlayToDisappear();
    }

    // الانتظار هنا للتعديل على الفواتير لانه بينتظر الحفظ والطباعه والصفوف ترجع
    public void waitForExchangeEditPageForEditBills(By locator1, By locator2) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator1));
        wait.until(d -> !d.findElements(locator2).isEmpty());
        waitForOverlayToDisappear();
    }

    // الانتظار هنا عشان التعديل لكل البرنامج بيستنى الحفظ
    public void waitForExchangeEditPage(By locator1) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator1));
        waitForOverlayToDisappear();
    }

    private final By EditableRows =
            By.xpath("//tbody//tr[@role='row' and .//input[not(@type='hidden')]]");

    private final By QuantityHeader =
            By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الكمية']]");

    public WebElement waitForQuantityInputByRow(int rowNumber) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return wait.until(
                d -> {
                    String quantityColIndex =
                            d.findElement(QuantityHeader).getAttribute("aria-colindex");

                    List<WebElement> rows = d.findElements(EditableRows);
                    if (rows.size() < rowNumber) {
                        return null;
                    }

                    WebElement targetRow = rows.get(rowNumber - 1);

                    List<WebElement> quantityInputs =
                            targetRow.findElements(
                                    By.xpath(
                                            ".//td[@aria-colindex='"
                                                    + quantityColIndex
                                                    + "']//input[not(@type='hidden')]"));

                    if (quantityInputs.isEmpty()) {
                        quantityInputs =
                                targetRow.findElements(
                                        By.xpath(
                                                ".//input[@type='number' and not(@type='hidden')]"));
                    }

                    return quantityInputs.isEmpty() || !quantityInputs.get(0).isDisplayed()
                            ? null
                            : quantityInputs.get(0);
                });
    }

    public WebElement waitForInputByRow(String columnName, int rowNumber) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return wait.until(
                d -> {
                    String columnIndex =
                            d.findElement(
                                            By.xpath(
                                                    "//th[@role='columnheader'][.//div[normalize-space()='"
                                                            + columnName
                                                            + "']]"))
                                    .getAttribute("aria-colindex");

                    List<WebElement> rows = d.findElements(EditableRows);

                    if (rows.size() < rowNumber) {
                        return null;
                    }

                    WebElement targetRow = rows.get(rowNumber - 1);

                    List<WebElement> inputs =
                            targetRow.findElements(
                                    By.xpath(
                                            ".//td[@aria-colindex='"
                                                    + columnIndex
                                                    + "']//input[not(@type='hidden')]"));

                    return inputs.isEmpty() || !inputs.get(0).isDisplayed() ? null : inputs.get(0);
                });
    }
}
