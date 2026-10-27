package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Focused UI component; public page entry points and their Allure steps stay on the page. */
final class AreaGridControls {
    private final AreaPage page;
    private final WebDriver driver;
    private final Runnable awaitRows;
    private final GridProviderWait provider;
    private final java.util.function.Function<String, WebElement> rowFinder;
    private static final By areaRows = By.cssSelector("table tbody tr[role='row']");
    private static final By areaHeaders = By.cssSelector("table thead th");

    AreaGridControls(
            AreaPage page,
            WebDriver driver,
            Runnable awaitRows,
            java.util.function.Function<String, WebElement> rowFinder) {
        this.page = page;
        this.driver = driver;
        this.awaitRows = awaitRows;
        this.rowFinder = rowFinder;
        this.provider = new GridProviderWait(driver, "areas");
    }

    private void waitForAreaRows() {
        awaitRows.run();
        provider.awaitReady();
    }

    private WebElement findAreaRow(String name) {
        return rowFinder.apply(name);
    }

    int GetVisibleAreaRowCount() {
        provider.awaitReady();
        return (int)
                driver.findElements(areaRows).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(
                                row -> {
                                    String text = row.getText().trim().toLowerCase();
                                    return !text.contains("لا يوجد بيانات")
                                            && !text.contains(
                                                    "there are no records matching your request")
                                            && !text.contains("no matching records")
                                            && !text.contains("no data");
                                })
                        .count();
    }

    int GetExactAreaMatchCount(String name) {
        return provider.exactMatches(name);
    }

    String GetFirstAreaCellByHeader(String headerName) {
        int index = getColumnIndex(headerName);
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
                        .orElseThrow(() -> new IllegalStateException("No Area row is displayed"));
        return row.findElements(By.cssSelector("td")).get(index).getText().trim();
    }

    String GetAreaCellByName(String name, String headerName) {
        return findAreaRow(name)
                .findElements(By.cssSelector("td"))
                .get(getColumnIndex(headerName))
                .getText()
                .trim();
    }

    java.util.List<String> GetAreaColumnValues(String headerName) {
        int index = getColumnIndex(headerName);
        return driver.findElements(areaRows).stream()
                .filter(WebElement::isDisplayed)
                .filter(
                        row ->
                                !row.findElements(By.cssSelector("button[data-action-type='edit']"))
                                        .isEmpty())
                .map(row -> row.findElements(By.cssSelector("td")).get(index).getText().trim())
                .toList();
    }

    int getColumnIndex(String headerName) {
        provider.awaitReady();
        java.util.List<WebElement> headers = driver.findElements(areaHeaders);
        for (int i = 0; i < headers.size(); i++)
            if (headers.get(i).getText().contains(headerName)) return i;
        throw new IllegalStateException("Area column not found: " + headerName);
    }

    int GetAreaPageSize() {
        return Integer.parseInt(
                new Select(getPageSizeSelect()).getFirstSelectedOption().getText().trim());
    }

    int GetAlternativeAreaPageSize(int current) {
        return new Select(getPageSizeSelect())
                .getOptions().stream()
                        .map(o -> o.getText().trim())
                        .filter(s -> s.matches("\\d+"))
                        .map(Integer::parseInt)
                        .filter(n -> n > current)
                        .findFirst()
                        .orElseThrow(
                                () -> new IllegalStateException("No larger Area page size option"));
    }

    void SelectAreaPageSize(int size) {
        new Select(getPageSizeSelect()).selectByVisibleText(String.valueOf(size));
        waitForAreaRows();
    }

    WebElement getPageSizeSelect() {
        provider.awaitReady();
        return driver.findElements(By.cssSelector("select")).stream()
                .filter(WebElement::isDisplayed)
                .filter(
                        s ->
                                s.findElements(By.cssSelector("option")).stream()
                                        .anyMatch(o -> o.getText().matches("\\d+")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Area page-size selector not found"));
    }

    void GoToNextAreaPage() {
        clickPageButton(true);
    }

    void GoToPreviousAreaPage() {
        clickPageButton(false);
    }

    boolean IsNextAreaPageEnabled() {
        java.util.List<WebElement> buttons = getPageButtons();
        return !buttons.isEmpty() && isPageButtonEnabled(buttons.get(buttons.size() - 1));
    }

    void clickPageButton(boolean next) {
        java.util.List<WebElement> buttons = getPageButtons();
        WebElement button = buttons.get(next ? buttons.size() - 1 : 0);
        if (!isPageButtonEnabled(button))
            throw new IllegalStateException("Requested Area page is not available");
        String firstCode = page.GetFirstAreaCellByHeader("الكود");
        button.click();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !page.GetFirstAreaCellByHeader("الكود").equals(firstCode));
    }

    java.util.List<WebElement> getPageButtons() {
        provider.awaitReady();
        java.util.List<WebElement> buttons =
                driver
                        .findElements(
                                By.cssSelector(
                                        ".pagination .page-item button, .pagination .page-item a, .page-item button,"
                                                + " .page-item a"))
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .toList();
        return buttons.size() < 3 ? java.util.List.of() : buttons;
    }

    boolean isPageButtonEnabled(WebElement button) {
        String classes =
                (button.getAttribute("class")
                                + " "
                                + button.findElement(
                                                By.xpath(
                                                        "ancestor::*[contains(@class,'page-item')][1]"))
                                        .getAttribute("class"))
                        .toLowerCase();
        return button.isEnabled()
                && !classes.contains("disabled")
                && !"true".equals(button.getAttribute("aria-disabled"));
    }

    void ClickAreaColumnHeader(String headerName) {
        driver.findElements(areaHeaders).stream()
                .filter(WebElement::isDisplayed)
                .filter(h -> h.getText().contains(headerName))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("Area header not found: " + headerName))
                .click();
        waitForAreaRows();
    }

    java.util.List<String> SortAreaCodes(java.util.List<String> values, boolean ascending) {
        java.util.List<String> sorted = new java.util.ArrayList<>(values);
        sorted.sort(
                (left, right) -> {
                    int comparison;
                    try {
                        comparison = Long.compare(Long.parseLong(left), Long.parseLong(right));
                    } catch (NumberFormatException ignored) {
                        Plugin_ERP.Bots.LogUtils.debug(
                                "AreaGridControls fallback: " + ignored.getClass().getSimpleName());

                        comparison = left.compareToIgnoreCase(right);
                    }
                    return ascending ? comparison : -comparison;
                });
        return sorted;
    }
}
