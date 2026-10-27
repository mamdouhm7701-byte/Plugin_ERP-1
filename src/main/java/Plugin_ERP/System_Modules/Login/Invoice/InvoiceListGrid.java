package Plugin_ERP.System_Modules.Login.Invoice;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Visible invoice rows, columns, sorting and grid refresh synchronization. */
final class InvoiceListGrid {
    private final InvoicListPage owner;
    private final WebDriver driver;

    InvoiceListGrid(InvoicListPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    private static final Duration PAGE_TIMEOUT = Duration.ofSeconds(20);
    private static final By GRID_HEADERS = By.cssSelector("table thead th");

    private static boolean isDisplayed(WebElement element) {
        return InvoiceListFilters.isDisplayed(element);
    }

    boolean isInvoiceTableDisplayed() {
        return driver.findElements(GRID_HEADERS).stream().anyMatch(InvoiceListGrid::isDisplayed);
    }

    List<String> getInvoiceTableHeaders() {
        return driver.findElements(GRID_HEADERS).stream()
                .filter(InvoiceListGrid::isDisplayed)
                .map(WebElement::getText)
                .map(this::cleanHeaderText)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    List<List<String>> getVisibleInvoiceRows() {
        WebElement anchor =
                driver.findElements(GRID_HEADERS).stream()
                        .filter(InvoiceListGrid::isDisplayed)
                        .findFirst()
                        .orElse(null);
        if (anchor == null) return List.of();
        @SuppressWarnings("unchecked")
        List<List<String>> rows =
                (List<List<String>>)
                        ((org.openqa.selenium.JavascriptExecutor) driver)
                                .executeScript(
                                        "const table=arguments[0].closest('table');const headers=[...table.querySelectorAll('thead th')].filter(h=>h.getClientRects().length && h.innerText.trim());"
                                                + "const indexes=headers.map(h=>h.getAttribute('aria-colindex'));"
                                                + "return [...table.querySelectorAll('tbody tr')].filter(r=>r.getClientRects().length).map(r=>indexes.map(i=>{"
                                                + "const c=r.querySelector(`td[aria-colindex='${i}']`);return c?(c.innerText||'').trim():'';}))"
                                                + ".filter(r=>r.some(c=>c));",
                                        anchor);
        return rows == null ? List.of() : rows;
    }

    boolean areVisibleRowsMatching(String expectedText) {
        List<List<String>> rows = owner.getVisibleInvoiceRows();
        if (rows.size() == 1 && rows.get(0).stream().anyMatch(this::isEmptyStateText)) {
            return false;
        }
        String expected = expectedText.toLowerCase(Locale.ROOT);
        return !rows.isEmpty()
                && rows.stream()
                        .allMatch(
                                row ->
                                        row.stream()
                                                .anyMatch(
                                                        cell ->
                                                                cell.toLowerCase(Locale.ROOT)
                                                                        .contains(expected)));
    }

    boolean isInvoiceEmptyStateDisplayed() {
        List<List<String>> rows = owner.getVisibleInvoiceRows();
        return rows.stream().anyMatch(row -> row.stream().anyMatch(this::isEmptyStateText))
                || driver
                        .findElements(
                                By.cssSelector(
                                        ".b-table-empty-row, .dataTables_empty, .empty-state"))
                        .stream()
                        .anyMatch(InvoiceListGrid::isDisplayed);
    }

    String getFirstInvoiceCell(String columnName) {
        return owner.getInvoiceColumnValues(columnName).stream().findFirst().orElse("");
    }

    List<String> getInvoiceColumnValues(String columnName) {
        WebElement header = findColumnHeader(columnName);
        String ariaIndex = header.getAttribute("aria-colindex");
        if (ariaIndex != null && !ariaIndex.isBlank()) {
            @SuppressWarnings("unchecked")
            List<String> values =
                    (List<String>)
                            ((org.openqa.selenium.JavascriptExecutor) driver)
                                    .executeScript(
                                            "const table=arguments[0].closest('table');const i=arguments[1];return [...table.querySelectorAll('tbody tr')].filter(r=>r.getClientRects().length)"
                                                    + ".map(r=>r.querySelector(`td[aria-colindex='${i}']`)).filter(c=>c).map(c=>(c.innerText||'').trim());",
                                            header,
                                            ariaIndex);
            return values == null ? List.of() : values;
        }
        int index = getColumnIndex(columnName);
        return owner.getVisibleInvoiceRows().stream()
                .filter(row -> row.size() > index)
                .map(row -> row.get(index))
                .toList();
    }

    void sortInvoiceColumn(String columnName, boolean ascending) {
        String expected = ascending ? "ascending" : "descending";
        for (int attempt = 0; attempt < 3; attempt++) {
            WebElement header = findColumnHeader(columnName);
            if (expected.equals(header.getDomAttribute("aria-sort"))) return;
            String before = gridSnapshot();
            header.click();
            waitForGridRefresh(before);
        }
        if (!expected.equals(findColumnHeader(columnName).getDomAttribute("aria-sort")))
            throw new AssertionError(
                    "Sales column did not reach requested sort order: " + columnName);
    }

    boolean isInvoiceColumnSorted(String columnName, boolean ascending) {
        List<String> values = owner.getInvoiceColumnValues(columnName);
        if (values.size() < 2) {
            return true;
        }
        for (int i = 1; i < values.size(); i++) {
            String column = columnName.toLowerCase(Locale.ROOT);
            int comparison =
                    column.contains("الكود")
                                    || column.contains("code")
                                    || column.contains("تاريخ")
                                    || column.contains("date")
                            ? normalizeComparableText(values.get(i - 1))
                                    .compareTo(normalizeComparableText(values.get(i)))
                            : compareCellValues(values.get(i - 1), values.get(i));
            if (ascending ? comparison > 0 : comparison < 0) {
                return false;
            }
        }
        return true;
    }

    void waitForGridRefresh() {
        waitForGridRefresh(null);
    }

    void waitForGridRefresh(String previousGrid) {
        long[] quietSince = {0};
        long[] stableGridSince = {0};
        String[] observedGrid = {previousGrid};
        boolean[] changed = {previousGrid == null};
        boolean[] stableWithoutChange = {false};
        long startedAt = System.nanoTime();
        new WebDriverWait(driver, PAGE_TIMEOUT)
                .until(
                        d -> {
                            boolean loading =
                                    d
                                            .findElements(
                                                    By.cssSelector(
                                                            ".b-overlay, .position-absolute.bg-light.rounded-lg, .vld-overlay.is-active, .spinner-border, [class*='loading-overlay'], [class*='loader-wrapper']"))
                                            .stream()
                                            .anyMatch(InvoiceListGrid::isDisplayed);
                            String grid = gridSnapshot();
                            long now = System.nanoTime();
                            if (!grid.isEmpty() && grid.equals(observedGrid[0])) {
                                if (stableGridSince[0] == 0) stableGridSince[0] = now;
                            } else {
                                observedGrid[0] = grid;
                                stableGridSince[0] = 0;
                            }
                            if (previousGrid != null
                                    && !grid.isEmpty()
                                    && !grid.equals(previousGrid)) changed[0] = true;
                            if (loading) {
                                quietSince[0] = 0;
                                // Some screens leave the overlay mounted after the Vue grid has
                                // settled.
                                // Require a stable table for 1.5s before accepting that state.
                                return stableGridSince[0] != 0
                                        && now - stableGridSince[0]
                                                >= Duration.ofMillis(1500).toNanos();
                            }
                            if (quietSince[0] == 0) quietSince[0] = System.nanoTime();
                            long quietFor = System.nanoTime() - quietSince[0];
                            if (!changed[0]
                                    && !grid.isEmpty()
                                    && stableGridSince[0] != 0
                                    && now - stableGridSince[0] >= Duration.ofMillis(2500).toNanos()
                                    && now - startedAt >= Duration.ofMillis(2500).toNanos()) {
                                stableWithoutChange[0] = true;
                            }
                            return (changed[0] || stableWithoutChange[0])
                                    && quietFor >= Duration.ofMillis(400).toNanos();
                        });
    }

    String gridSnapshot() {
        try {
            String rows =
                    owner.getVisibleInvoiceRows().stream()
                            .map(row -> String.join("\u001f", row))
                            .collect(Collectors.joining("\u001e"));
            if (!rows.isEmpty()) return rows;
            String emptyState =
                    driver
                            .findElements(
                                    By.cssSelector(
                                            ".b-table-empty-row, .dataTables_empty, .empty-state, table tbody tr"))
                            .stream()
                            .filter(InvoiceListGrid::isDisplayed)
                            .map(WebElement::getText)
                            .map(String::trim)
                            .filter(text -> !text.isEmpty())
                            .collect(Collectors.joining("\u001e"));
            if (!emptyState.isEmpty()) return "EMPTY|" + emptyState;
            return owner.isInvoiceTableDisplayed()
                    ? "EMPTY_TABLE|"
                            + owner.getCurrentInvoicePageNumber()
                            + "|"
                            + String.join("|", owner.getInvoiceTableHeaders())
                    : "";
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "InvoiceListGrid condition unavailable: " + ignored.getClass().getSimpleName());
            return "";
        }
    }

    int getColumnIndex(String columnName) {
        List<WebElement> headers = driver.findElements(GRID_HEADERS);
        int visibleIndex = 0;
        for (int i = 0; i < headers.size(); i++) {
            if (!isDisplayed(headers.get(i)) || headers.get(i).getText().trim().isEmpty()) continue;
            String headerText = cleanHeaderText(headers.get(i).getText());
            if (headerText.equalsIgnoreCase(columnName)
                    || headerText.contains(columnName)
                    || columnName.contains(headerText)) {
                return visibleIndex;
            }
            visibleIndex++;
        }
        throw new IllegalStateException("Invoice table column not found: " + columnName);
    }

    int compareCellValues(String left, String right) {
        String normalizedLeft = normalizeComparableText(left);
        String normalizedRight = normalizeComparableText(right);
        try {
            return new java.math.BigDecimal(normalizedLeft.replace(",", ""))
                    .compareTo(new java.math.BigDecimal(normalizedRight.replace(",", "")));
        } catch (NumberFormatException ignored) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "InvoiceListGrid fallback: " + ignored.getClass().getSimpleName());

            return normalizedLeft.compareTo(normalizedRight);
        }
    }

    String normalizeComparableText(String value) {
        return value == null
                ? ""
                : value.trim().replace("٬", "").replace("٫", ".").replaceAll("\\s+", " ");
    }

    boolean isEmptyStateText(String value) {
        String text = value.toLowerCase(Locale.ROOT);
        return text.contains("لا يوجد")
                || text.contains("لا توجد")
                || text.contains("no data")
                || text.contains("no records")
                || text.contains("لا توجد بيانات");
    }

    List<Integer> visibleColumnIndexes() {
        return driver.findElements(GRID_HEADERS).stream()
                .filter(InvoiceListGrid::isDisplayed)
                .filter(header -> !header.getText().trim().isEmpty())
                .map(header -> header.getAttribute("aria-colindex"))
                .map(index -> index == null || index.isBlank() ? "0" : index)
                .map(Integer::parseInt)
                .toList();
    }

    WebElement findColumnHeader(String columnName) {
        return driver.findElements(GRID_HEADERS).stream()
                .filter(InvoiceListGrid::isDisplayed)
                .filter(
                        header ->
                                cleanHeaderText(header.getText())
                                                .contains(cleanHeaderText(columnName))
                                        || cleanHeaderText(columnName)
                                                .contains(cleanHeaderText(header.getText())))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Invoice table column not found: " + columnName));
    }

    String cleanHeaderText(String text) {
        return text.replaceAll("(?i)\\s*\\(click to sort.*?\\)", "").trim();
    }
}
