/* شرح الملف: مساعدة لعرض قائمة الخزائن وترتيبها والتنقل بينها وتصديرها. فائدتها الوصول إلى بيانات
 * الخزائن ومراجعتها خارج القائمة.
 */
package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

final class CashListControls {
    private final CashPage page;
    private final WebDriver driver;
    private final Runnable awaitLoading;
    private final GridProviderWait provider;
    private static final By SafeRows = By.xpath("//tbody//tr[@role='row']");
    private static final By SafeTableHeaders = By.cssSelector("table thead th");

    CashListControls(CashPage page, WebDriver driver, Runnable awaitLoading) {
        this.page = page;
        this.driver = driver;
        this.awaitLoading = awaitLoading;
        this.provider = new GridProviderWait(driver, "safes");
    }

    private void waitForCashPageLoading() {
        awaitLoading.run();
        awaitReady();
    }

    void awaitReady() {
        provider.awaitReady();
    }

    int getVisibleSafeRowCount() {
        awaitReady();
        return (int) driver.findElements(SafeRows).stream().filter(WebElement::isDisplayed).count();
    }

    int getSelectedSafePageSize() {
        WebElement select = findSafePageSizeSelect();
        return Integer.parseInt(
                new org.openqa.selenium.support.ui.Select(select)
                        .getFirstSelectedOption()
                        .getText()
                        .trim());
    }

    int getAlternativeSafePageSize(int currentValue) {
        return new org.openqa.selenium.support.ui.Select(findSafePageSizeSelect())
                .getOptions().stream()
                        .map(option -> option.getText().trim())
                        .filter(text -> text.matches("\\d+"))
                        .map(Integer::parseInt)
                        .filter(value -> value > currentValue)
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "No larger safe page-size option is available"));
    }

    void selectSafePageSize(int pageSize) {
        new org.openqa.selenium.support.ui.Select(findSafePageSizeSelect())
                .selectByVisibleText(String.valueOf(pageSize));
        waitForCashPageLoading();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        d ->
                                getSelectedSafePageSize() == pageSize
                                        && page.getVisibleSafeRowCount() <= pageSize);
    }

    private WebElement findSafePageSizeSelect() {
        awaitReady();
        return driver.findElements(By.cssSelector("select")).stream()
                .filter(WebElement::isDisplayed)
                .filter(
                        select ->
                                select.findElements(By.cssSelector("option")).stream()
                                        .anyMatch(
                                                option -> option.getText().trim().matches("\\d+")))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("Safe list page-size selector not found"));
    }

    int getSafeTotalRowCount() {
        awaitReady();
        List<WebElement> rows = driver.findElements(SafeRows);
        if (rows.size() == 1 && rows.get(0).getText().toLowerCase().contains("no data")) {
            return 0;
        }
        return rows.size();
    }

    boolean isSafeNextPageEnabled() {
        List<WebElement> pageButtons = getPaginationButtons();
        if (pageButtons.isEmpty()) {
            return false;
        }
        WebElement next = pageButtons.get(pageButtons.size() - 1);
        return isPaginationButtonEnabled(next);
    }

    void goToNextSafePage() {
        waitForCashPageLoading();
        List<WebElement> pageButtons = getPaginationButtons();
        WebElement next = pageButtons.get(pageButtons.size() - 1);
        if (!isPaginationButtonEnabled(next)) {
            throw new IllegalStateException("Safe list has no next page");
        }
        String firstCode = page.getFirstSafeCellByHeader("الكود");
        next.click();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !page.getFirstSafeCellByHeader("الكود").equals(firstCode));
    }

    void goToPreviousSafePage() {
        waitForCashPageLoading();
        List<WebElement> pageButtons = getPaginationButtons();
        WebElement previous = pageButtons.get(0);
        if (!isPaginationButtonEnabled(previous)) {
            throw new IllegalStateException("Safe list has no previous page");
        }
        String firstCode = page.getFirstSafeCellByHeader("الكود");
        previous.click();
        waitForCashPageLoading();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !page.getFirstSafeCellByHeader("الكود").equals(firstCode));
    }

    private List<WebElement> getPaginationButtons() {
        List<WebElement> buttons =
                driver
                        .findElements(
                                By.cssSelector(
                                        ".pagination .page-item button, .pagination .page-item a, .page-item button, .page-item a"))
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .toList();
        if (buttons.size() < 3) {
            return List.of();
        }
        return buttons;
    }

    private boolean isPaginationButtonEnabled(WebElement button) {
        WebElement pageItem =
                button.findElement(
                        By.xpath(
                                "ancestor::*[contains(concat(' ',normalize-space(@class),' '),' page-item ')][1]"));
        String className =
                (pageItem.getAttribute("class") + " " + button.getAttribute("class")).toLowerCase();
        return button.isEnabled()
                && !className.contains("disabled")
                && !"true".equalsIgnoreCase(button.getAttribute("aria-disabled"));
    }

    String getFirstSafeCellByHeader(String headerName) {
        awaitReady();
        List<WebElement> headers = driver.findElements(SafeTableHeaders);
        int columnIndex = -1;
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().contains(headerName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex < 0) {
            throw new IllegalStateException("Safe table column not found: " + headerName);
        }

        WebElement row =
                new WebDriverWait(driver, Duration.ofSeconds(15))
                        .ignoring(StaleElementReferenceException.class)
                        .withMessage("No editable safe row is visible after provider completion")
                        .until(
                                d ->
                                        d.findElements(SafeRows).stream()
                                                .filter(WebElement::isDisplayed)
                                                .filter(
                                                        candidate ->
                                                                !candidate
                                                                        .findElements(
                                                                                By.cssSelector(
                                                                                        "button[data-action-type='edit']"))
                                                                        .isEmpty())
                                                .findFirst()
                                                .orElse(null));
        List<WebElement> cells = row.findElements(By.cssSelector("td"));
        if (columnIndex >= cells.size()) {
            throw new IllegalStateException(
                    "Safe table row does not contain column: " + headerName);
        }
        return cells.get(columnIndex).getText().trim();
    }

    List<String> getSafeColumnValues(String headerName) {
        awaitReady();
        List<WebElement> headers = driver.findElements(SafeTableHeaders);
        int columnIndex = -1;
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().contains(headerName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex < 0) {
            throw new IllegalStateException("Safe table column not found: " + headerName);
        }

        final int valueColumn = columnIndex;
        return driver.findElements(SafeRows).stream()
                .filter(WebElement::isDisplayed)
                .filter(
                        row ->
                                !row.findElements(By.cssSelector("button[data-action-type='edit']"))
                                        .isEmpty())
                .map(
                        row -> {
                            List<WebElement> cells = row.findElements(By.cssSelector("td"));
                            if (valueColumn >= cells.size()) {
                                throw new IllegalStateException(
                                        "Safe table row does not contain column: " + headerName);
                            }
                            return cells.get(valueColumn).getText().trim();
                        })
                .toList();
    }

    List<String> sortCodes(List<String> codes, boolean ascending) {
        List<String> sorted = new java.util.ArrayList<>(codes);
        sorted.sort(
                (left, right) -> {
                    try {
                        int comparison = Long.compare(Long.parseLong(left), Long.parseLong(right));
                        return ascending ? comparison : -comparison;
                    } catch (NumberFormatException ignored) {
                        Plugin_ERP.Bots.LogUtils.debug(
                                "CashListControls fallback: " + ignored.getClass().getSimpleName());

                        int comparison = left.compareToIgnoreCase(right);
                        return ascending ? comparison : -comparison;
                    }
                });
        return sorted;
    }

    void clickSafeColumnHeader(String headerName) {
        waitForCashPageLoading();
        WebElement header =
                driver.findElements(SafeTableHeaders).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(element -> element.getText().trim().contains(headerName))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Safe table column not found: " + headerName));
        header.click();
        waitForCashPageLoading();
    }

    void exportSafes(String format) {
        String normalizedFormat = format.trim().toLowerCase();
        if (!normalizedFormat.equals("pdf") && !normalizedFormat.equals("xls")) {
            throw new IllegalArgumentException("Supported safe export formats are PDF and XLS");
        }

        String lower =
                "translate(concat(@class,' ',@src,' ',@alt,' ',@title,' ',@aria-label,' ',normalize-space(.)),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
        String tokenMatch =
                normalizedFormat.equals("pdf")
                        ? "contains(" + lower + ", 'pdf')"
                        : "(contains("
                                + lower
                                + ", 'xls') or contains("
                                + lower
                                + ", 'excel') or "
                                + "contains("
                                + lower
                                + ", 'spreadsheet'))";
        By exportControl =
                By.xpath(
                        "//*[self::button or self::a]["
                                + tokenMatch
                                + " or .//*["
                                + tokenMatch
                                + "]]");

        List<WebElement> controls =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                d -> {
                                    List<WebElement> found = d.findElements(exportControl);
                                    return found.stream().anyMatch(WebElement::isDisplayed)
                                            ? found
                                            : null;
                                });
        WebElement control =
                controls.stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Export control not visible: " + format));
        control.click();
    }
}
