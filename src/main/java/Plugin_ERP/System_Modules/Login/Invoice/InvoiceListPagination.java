package Plugin_ERP.System_Modules.Login.Invoice;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Invoice-list page size and navigation; grid refresh policy remains owned by the page. */
final class InvoiceListPagination {
    private final InvoicListPage owner;
    private final WebDriver driver;
    private final Supplier<String> snapshot;
    private final Consumer<String> refresh;
    private static final By PAGINATION_ITEMS = By.cssSelector(".pagination .page-item");

    InvoiceListPagination(
            InvoicListPage owner,
            WebDriver driver,
            Supplier<String> snapshot,
            Consumer<String> refresh) {
        this.owner = owner;
        this.driver = driver;
        this.snapshot = snapshot;
        this.refresh = refresh;
    }

    private String gridSnapshot() {
        return snapshot.get();
    }

    private void waitForGridRefresh(String before) {
        refresh.accept(before);
    }

    private static boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (StaleElementReferenceException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "InvoiceListPagination condition unavailable: "
                            + ignored.getClass().getSimpleName());
            return false;
        }
    }

    void setInvoicePageSize(int pageSize) {
        owner.slAwaitListReady();
        String before = gridSnapshot();
        WebElement select = findPageSizeSelect();
        new Select(select).selectByVisibleText(String.valueOf(pageSize));
        waitForGridRefresh(before);
        owner.slAwaitListReady();
    }

    int getInvoicePageSize() {
        String value = new Select(findPageSizeSelect()).getFirstSelectedOption().getText().trim();
        return Integer.parseInt(value.replaceAll("\\D", ""));
    }

    List<Integer> getInvoicePageSizeOptions() {
        return new Select(findPageSizeSelect())
                .getOptions().stream()
                        .map(WebElement::getText)
                        .map(String::trim)
                        .filter(text -> text.matches("\\d+"))
                        .map(Integer::parseInt)
                        .toList();
    }

    boolean isNextInvoicePageEnabled() {
        WebElement button = findPaginationControl("next");
        return isPaginationEnabled(button);
    }

    boolean isPreviousInvoicePageEnabled() {
        WebElement button = findPaginationControl("previous");
        return isPaginationEnabled(button);
    }

    void goToNextInvoicePage() {
        clickPaginationControl("next");
    }

    void goToPreviousInvoicePage() {
        clickPaginationControl("previous");
    }

    void goToFirstInvoicePage() {
        while (owner.isPreviousInvoicePageEnabled()) {
            clickPaginationControl("previous");
        }
    }

    void goToLastInvoicePage() {
        while (owner.isNextInvoicePageEnabled()) {
            clickPaginationControl("next");
        }
    }

    void goToInvoicePage(int pageNumber) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Invoice list page numbers start from 1");
        }
        WebElement page =
                driver.findElements(PAGINATION_ITEMS).stream()
                        .filter(InvoiceListPagination::isDisplayed)
                        .flatMap(
                                item ->
                                        item
                                                .findElements(
                                                        By.cssSelector(
                                                                "button, a, [role='menuitem']"))
                                                .stream())
                        .filter(InvoiceListPagination::isDisplayed)
                        .filter(
                                button ->
                                        button.getText().trim().equals(String.valueOf(pageNumber)))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Invoice list page number not visible: "
                                                        + pageNumber));
        if (pageNumber != owner.getCurrentInvoicePageNumber()) {
            int previousPage = owner.getCurrentInvoicePageNumber();
            String before = gridSnapshot();
            By locator = By.cssSelector(".pagination [aria-label='Go to page " + pageNumber + "']");
            WebElement control =
                    new WebDriverWait(driver, Duration.ofSeconds(10))
                            .until(ExpectedConditions.presenceOfElementLocated(locator));
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click()", control);
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> owner.getCurrentInvoicePageNumber() == pageNumber);
            waitForGridRefresh(before);
        }
    }

    int getCurrentInvoicePageNumber() {
        List<WebElement> active =
                driver.findElements(By.cssSelector(".pagination .page-item.active"));
        String digits = active.isEmpty() ? "" : active.get(0).getText().replaceAll("\\D", "");
        return digits.isBlank() ? 1 : Integer.parseInt(digits);
    }

    String getInvoicePaginationSummary() {
        return driver
                .findElements(
                        By.cssSelector(".pagination, [class*='pagination'], [class*='page-info']"))
                .stream()
                .filter(InvoiceListPagination::isDisplayed)
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .findFirst()
                .orElse("");
    }

    WebElement findPageSizeSelect() {
        List<WebElement> exact = driver.findElements(By.id("perPageSelect"));
        if (!exact.isEmpty()) return exact.get(0);
        List<WebElement> selects =
                driver.findElements(By.cssSelector("select")).stream()
                        .filter(InvoiceListPagination::isDisplayed)
                        .toList();
        return selects.stream()
                .filter(
                        select ->
                                new Select(select)
                                        .getOptions().stream()
                                                .anyMatch(
                                                        option ->
                                                                option.getText()
                                                                        .trim()
                                                                        .matches("\\d+")))
                .findFirst()
                .orElseGet(
                        () ->
                                selects.stream()
                                        .filter(
                                                select ->
                                                        select.getAttribute("aria-label") != null
                                                                && select.getAttribute("aria-label")
                                                                        .toLowerCase(Locale.ROOT)
                                                                        .contains("page"))
                                        .findFirst()
                                        .orElseThrow(
                                                () ->
                                                        new IllegalStateException(
                                                                "Invoice list page-size selector not found")));
    }

    WebElement findPaginationControl(String kind) {
        String aria =
                switch (kind) {
                    case "next" -> "Go to next page";
                    case "previous" -> "Go to previous page";
                    default -> "";
                };
        if (!aria.isEmpty()) {
            List<WebElement> exact =
                    driver.findElements(By.cssSelector("[aria-label='" + aria + "']"));
            if (!exact.isEmpty()) return exact.get(0);
        }
        String[] tokens =
                switch (kind) {
                    case "next" -> new String[] {"التالي", "next", "›", "❯", "»"};
                    case "previous" -> new String[] {"السابق", "previous", "prev", "‹", "❮", "«"};
                    case "first" -> new String[] {"الأول", "first", "|<", "«"};
                    case "last" -> new String[] {"الأخير", "last", ">|", "»"};
                    default -> new String[0];
                };
        List<WebElement> items =
                driver.findElements(PAGINATION_ITEMS).stream()
                        .filter(InvoiceListPagination::isDisplayed)
                        .toList();
        List<WebElement> candidates =
                items.stream()
                        .flatMap(
                                item ->
                                        item
                                                .findElements(
                                                        By.cssSelector(
                                                                "button, a, [role='menuitem']"))
                                                .stream())
                        .filter(InvoiceListPagination::isDisplayed)
                        .toList();
        WebElement match =
                candidates.stream()
                        .filter(
                                candidate -> {
                                    String text =
                                            (candidate.getText()
                                                            + " "
                                                            + candidate.getAttribute("aria-label")
                                                            + " "
                                                            + candidate.getAttribute("title"))
                                                    .toLowerCase(Locale.ROOT)
                                                    .trim();
                                    return List.of(tokens).stream()
                                            .anyMatch(
                                                    token ->
                                                            text.contains(
                                                                    token.toLowerCase(
                                                                            Locale.ROOT)));
                                })
                        .findFirst()
                        .orElse(null);
        if (match != null) {
            return match;
        }
        if (items.size() < 3) {
            throw new IllegalStateException("Invoice list pagination control not found: " + kind);
        }
        WebElement item =
                switch (kind) {
                    case "next" -> items.get(items.size() - 1);
                    case "previous" -> items.get(0);
                    case "first" -> items.get(0);
                    case "last" -> items.get(items.size() - 1);
                    default ->
                            throw new IllegalArgumentException(
                                    "Unknown pagination control: " + kind);
                };
        return item.findElement(By.cssSelector("button, a"));
    }

    void clickPaginationControl(String kind) {
        String beforeGrid = gridSnapshot();
        WebElement control = findPaginationControl(kind);
        if (!isPaginationEnabled(control)) {
            throw new IllegalStateException("Invoice list pagination control is disabled: " + kind);
        }
        int before = owner.getCurrentInvoicePageNumber();
        By locator = By.cssSelector("[aria-label='Go to " + kind + " page']");
        WebElement clickable =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.presenceOfElementLocated(locator));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click()", clickable);
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(
                        d -> {
                            int after = owner.getCurrentInvoicePageNumber();
                            return kind.equals("next") ? after > before : after < before;
                        });
        waitForGridRefresh(beforeGrid);
    }

    boolean isPaginationEnabled(WebElement button) {
        String parentClass =
                button.findElements(By.xpath("..")).stream()
                        .findFirst()
                        .map(e -> e.getAttribute("class"))
                        .orElse("");
        String classes =
                (button.getAttribute("class") + " " + parentClass).toLowerCase(Locale.ROOT);
        return !"true".equalsIgnoreCase(button.getAttribute("aria-disabled"))
                && !"true"
                        .equalsIgnoreCase(
                                button.findElements(By.xpath("..")).stream()
                                        .findFirst()
                                        .map(e -> e.getAttribute("aria-disabled"))
                                        .orElse("false"))
                && (button.getTagName().equalsIgnoreCase("span") || button.isEnabled())
                && !classes.contains("disabled")
                && !"true".equalsIgnoreCase(button.getAttribute("aria-disabled"));
    }
}
