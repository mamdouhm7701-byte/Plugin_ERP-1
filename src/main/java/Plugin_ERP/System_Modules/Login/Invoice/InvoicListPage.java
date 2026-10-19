package Plugin_ERP.System_Modules.Login.Invoice;

import Plugin_ERP.System_Modules.Login.AbstractForSales;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class InvoicListPage extends SalesModulePage {
    private static final Duration PAGE_TIMEOUT = Duration.ofSeconds(20);
    private static final By GRID_ROWS = By.cssSelector("tbody tr");
    private static final By GRID_HEADERS = By.cssSelector("table thead th");
    private static final By PAGINATION_ITEMS = By.cssSelector(".pagination .page-item");
    private static final String EXPORT_SEARCHABLE_TEXT =
            "translate(concat(@class,' ',@src,' ',@alt,' ',@title,' ',@aria-label,' ',normalize-space(.)),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
    private String pendingFilterGridBaseline;

    public InvoicListPage(WebDriver driver) {
        super(driver);
    }

    @Step("انتظار ظهور قائمة الفواتير")
    public void waitForInvoiceList() {
        WebDriverWait wait = new WebDriverWait(driver, PAGE_TIMEOUT);
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(GRID_HEADERS),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder*='بحث']")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pagination"))
        ));
    }

    @Step("التحقق من ظهور جدول الفواتير")
    public boolean isInvoiceTableDisplayed() {
        return driver.findElements(GRID_HEADERS).stream().anyMatch(InvoicListPage::isDisplayed);
    }

    @Step("قراءة عناوين أعمدة قائمة الفواتير")
    public List<String> getInvoiceTableHeaders() {
        return driver.findElements(GRID_HEADERS).stream()
                .filter(InvoicListPage::isDisplayed)
                .map(WebElement::getText)
                .map(this::cleanHeaderText)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    @Step("قراءة صفوف قائمة الفواتير الظاهرة")
    public List<List<String>> getVisibleInvoiceRows() {
        WebElement anchor = driver.findElements(GRID_HEADERS).stream().filter(InvoicListPage::isDisplayed)
                .findFirst().orElse(null);
        if (anchor == null) return List.of();
        @SuppressWarnings("unchecked")
        List<List<String>> rows = (List<List<String>>) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "const table=arguments[0].closest('table');const headers=[...table.querySelectorAll('thead th')].filter(h=>h.getClientRects().length && h.innerText.trim());"
                        + "const indexes=headers.map(h=>h.getAttribute('aria-colindex'));"
                        + "return [...table.querySelectorAll('tbody tr')].filter(r=>r.getClientRects().length).map(r=>indexes.map(i=>{"
                        + "const c=r.querySelector(`td[aria-colindex='${i}']`);return c?(c.innerText||'').trim():'';}))"
                        + ".filter(r=>r.some(c=>c));", anchor);
        return rows == null ? List.of() : rows;
    }

    @Step("البحث في فلتر قائمة الفواتير: {label}")
    public void setInvoiceTextFilter(String label, String value) {
        slAwaitListReady();
        captureFilterGridBaseline();
        WebElement input = findFilterControl(label);
        if ("hidden".equalsIgnoreCase(input.getAttribute("type"))) {
            setDateFilter(input, value);
            return;
        }
        input.clear();
        if (value != null && !value.isEmpty()) {
            input.sendKeys(value);
        }
        String expectedValue = value == null ? "" : value;
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> {
            try {
                return expectedValue.equals(findFilterControl(label).getAttribute("value"));
            } catch (org.openqa.selenium.StaleElementReferenceException
                     | org.openqa.selenium.NoSuchElementException ignored) {
                return false;
            }
        });
    }

    @Step("تطبيق فلاتر قائمة الفواتير")
    public void applyInvoiceFilters() {
        // Text filters use the screen's Enter handler to refresh the provider.
        slApplyListFilters();
        String baseline = pendingFilterGridBaseline;
        pendingFilterGridBaseline = null;
        waitForGridRefresh(baseline);
    }

    @Step("تصفير فلاتر قائمة الفواتير")
    public void resetInvoiceFilters() {
        driver.navigate().refresh();
        waitForInvoiceList();
        new WebDriverWait(driver, PAGE_TIMEOUT).until(d -> {
            try {
                return isInvoiceTableDisplayed();
            } catch (org.openqa.selenium.StaleElementReferenceException
                     | IllegalStateException ignored) {
                // The page can briefly render without a table while Vue restores the data.
                return false;
            }
        });
    }

    @Step("اختيار قيمة فلتر قائمة الفواتير: {label} = {value}")
    public void selectInvoiceFilter(String label, String value) {
        captureFilterGridBaseline();
        ensureInvoiceFiltersVisible();
        String normalizedLabel = label.toLowerCase(Locale.ROOT);
        if (normalizedLabel.contains("السداد") || normalizedLabel.contains("payment status")) {
            String text = value.equals("مدفوع جزئي") ? "مدفوع جزء" : value;
            clickRadioOption(1, text);
            return;
        }
        if (normalizedLabel.contains("نوع الحركة") || normalizedLabel.contains("movement type")) {
            clickRadioOption(0, value);
            return;
        }
        if (normalizedLabel.contains("مندوب") || normalizedLabel.contains("agent")) {
            slChoose("مندوب", value);
            return;
        }
        if (normalizedLabel.contains("عميل") || normalizedLabel.contains("customer")) {
            slChoose("العميل", value);
            return;
        }
        List<WebElement> matchingChoice = driver.findElements(By.xpath(
                "//fieldset[.//*[self::legend or self::label][contains(normalize-space(.), '" + escapeXPath(label) + "')]]"
                        + "//label[normalize-space(.)='" + escapeXPath(value) + "' or .//*[normalize-space(.)='" + escapeXPath(value) + "']]"
                        + " | //*[@role='radio' or @role='option'][normalize-space(.)='" + escapeXPath(value) + "']"
        )).stream().filter(InvoicListPage::isDisplayed).toList();
        if (!matchingChoice.isEmpty()) {
            matchingChoice.get(0).click();
            return;
        }
        WebElement control = findFilterControl(label);
        if ("select".equalsIgnoreCase(control.getTagName())) {
            new Select(control).selectByVisibleText(value);
        } else {
            control.click();
            control.sendKeys(value);
            By option = By.xpath("//*[(@role='option' or contains(@class,'vs__dropdown-option') or self::li) and normalize-space(.)='" + escapeXPath(value) + "']");
            new WebDriverWait(driver, Duration.ofSeconds(8)).until(ExpectedConditions.elementToBeClickable(option)).click();
        }
    }

    @Step("إرجاع خيارات فلتر قائمة الفواتير: {label}")
    public List<String> getInvoiceFilterOptions(String label) { return getInvoiceFilterOptions(label, ""); }

    public List<String> getInvoiceFilterOptions(String label, String query) {
        ensureInvoiceFiltersVisible();
        String normalizedLabel = label.toLowerCase(Locale.ROOT);
        if (normalizedLabel.contains("السداد") || normalizedLabel.contains("payment status")) return getRadioOptions(1);
        if (normalizedLabel.contains("نوع الحركة") || normalizedLabel.contains("movement type")) return getRadioOptions(0);
        WebElement control = findFilterControl(label);
        if ("select".equalsIgnoreCase(control.getTagName())) {
            return new Select(control).getOptions().stream().map(WebElement::getText).map(String::trim).toList();
        }
        if ("radio".equalsIgnoreCase(control.getAttribute("type"))
                || "checkbox".equalsIgnoreCase(control.getAttribute("type"))) {
            List<WebElement> choices = control.findElements(By.xpath("ancestor::fieldset[1]//label"));
            if (!choices.isEmpty()) {
                return choices.stream().filter(InvoicListPage::isDisplayed).map(WebElement::getText)
                        .map(String::trim).filter(text -> !text.isEmpty()).toList();
            }
        }
        WebElement search = control.findElement(By.cssSelector("input.vs__search"));
        clickVueDropdown(control);
        String listboxId = control.getAttribute("aria-owns");
        By optionsLocator = listboxId == null || listboxId.isBlank()
                ? By.cssSelector(".vs__dropdown-option, [role='option']")
                : By.cssSelector("#" + listboxId + " .vs__dropdown-option, #" + listboxId + " [role='option']");
        boolean hasOptions = driver.findElements(optionsLocator).stream().anyMatch(InvoicListPage::isDisplayed);
        if (!hasOptions && !query.isBlank() && search.isDisplayed() && search.isEnabled()
                && !"true".equalsIgnoreCase(search.getAttribute("readonly"))) {
            try {
                search.sendKeys(query);
            } catch (org.openqa.selenium.InvalidElementStateException ignored) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "const input=arguments[0];input.value=arguments[1];"
                                + "input.dispatchEvent(new Event('input',{bubbles:true}));"
                                + "input.dispatchEvent(new Event('change',{bubbles:true}));", search, query);
            }
        }
        if (!hasOptions) {
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                        d.findElements(optionsLocator).stream().anyMatch(InvoicListPage::isDisplayed));
            } catch (org.openqa.selenium.TimeoutException ignored) {
                return List.of();
            }
        }
        String containerId = control.getAttribute("aria-owns");
        if (containerId != null && !containerId.isBlank()) {
            return driver.findElements(By.cssSelector("#" + containerId + " li, #" + containerId + " [role='option']"))
                    .stream().filter(InvoicListPage::isDisplayed).map(WebElement::getText).map(String::trim).toList();
        }
        return driver.findElements(By.cssSelector(".vs__dropdown-menu li, [role='listbox'] [role='option']"))
                .stream().filter(InvoicListPage::isDisplayed).map(WebElement::getText).map(String::trim).toList();
    }

    @Step("قراءة القيمة الحالية لفلتر قائمة الفواتير: {label}")
    public String getInvoiceFilterValue(String label) {
        String normalizedLabel = label.toLowerCase(Locale.ROOT);
        if (normalizedLabel.contains("السداد") || normalizedLabel.contains("payment status")) return getSelectedRadioValue(1);
        if (normalizedLabel.contains("نوع الحركة") || normalizedLabel.contains("movement type")) return getSelectedRadioValue(0);
        if (normalizedLabel.contains("مندوب") || normalizedLabel.contains("agent")) return selectedVueFilter("مندوب");
        if (normalizedLabel.contains("عميل") || normalizedLabel.contains("customer")) return selectedVueFilter("العميل");
        WebElement control = findFilterControl(label);
        if ("select".equalsIgnoreCase(control.getTagName())) {
            return new Select(control).getFirstSelectedOption().getText().trim();
        }
        String value = control.getAttribute("value");
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        try {
            WebElement selected = control.findElement(By.xpath("ancestor::fieldset[1]//input[@type='radio' and @checked]/following-sibling::*[1]"));
            return selected.getText().trim();
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
            return "";
        }
    }

    @Step("التحقق من ظهور السجلات المطابقة للفلتر")
    public boolean areVisibleRowsMatching(String expectedText) {
        List<List<String>> rows = getVisibleInvoiceRows();
        if (rows.size() == 1 && rows.get(0).stream().anyMatch(this::isEmptyStateText)) {
            return false;
        }
        String expected = expectedText.toLowerCase(Locale.ROOT);
        return !rows.isEmpty() && rows.stream().allMatch(row -> row.stream()
                .anyMatch(cell -> cell.toLowerCase(Locale.ROOT).contains(expected)));
    }

    @Step("قراءة حالة عدم وجود نتائج في قائمة الفواتير")
    public boolean isInvoiceEmptyStateDisplayed() {
        List<List<String>> rows = getVisibleInvoiceRows();
        return rows.stream().anyMatch(row -> row.stream().anyMatch(this::isEmptyStateText))
                || driver.findElements(By.cssSelector(".b-table-empty-row, .dataTables_empty, .empty-state"))
                .stream().anyMatch(InvoicListPage::isDisplayed);
    }

    @Step("قراءة أول قيمة ظاهرة في عمود قائمة الفواتير: {columnName}")
    public String getFirstInvoiceCell(String columnName) {
        return getInvoiceColumnValues(columnName).stream().findFirst().orElse("");
    }

    @Step("قراءة قيم عمود قائمة الفواتير: {columnName}")
    public List<String> getInvoiceColumnValues(String columnName) {
        WebElement header = findColumnHeader(columnName);
        String ariaIndex = header.getAttribute("aria-colindex");
        if (ariaIndex != null && !ariaIndex.isBlank()) {
            @SuppressWarnings("unchecked")
            List<String> values = (List<String>) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "const table=arguments[0].closest('table');const i=arguments[1];return [...table.querySelectorAll('tbody tr')].filter(r=>r.getClientRects().length)"
                            + ".map(r=>r.querySelector(`td[aria-colindex='${i}']`)).filter(c=>c).map(c=>(c.innerText||'').trim());", header, ariaIndex);
            return values == null ? List.of() : values;
        }
        int index = getColumnIndex(columnName);
        return getVisibleInvoiceRows().stream().filter(row -> row.size() > index).map(row -> row.get(index)).toList();
    }

    @Step("فرز قائمة الفواتير حسب العمود: {columnName}")
    public void sortInvoiceColumn(String columnName, boolean ascending) {
        String expected = ascending ? "ascending" : "descending";
        for (int attempt = 0; attempt < 3; attempt++) {
            WebElement header = findColumnHeader(columnName);
            if (expected.equals(header.getDomAttribute("aria-sort"))) return;
            String before = gridSnapshot();
            header.click();
            waitForGridRefresh(before);
        }
        if (!expected.equals(findColumnHeader(columnName).getDomAttribute("aria-sort")))
            throw new AssertionError("Sales column did not reach requested sort order: " + columnName);

    }

    @Step("التحقق من ترتيب عمود قائمة الفواتير")
    public boolean isInvoiceColumnSorted(String columnName, boolean ascending) {
        List<String> values = getInvoiceColumnValues(columnName);
        if (values.size() < 2) {
            return true;
        }
        for (int i = 1; i < values.size(); i++) {
            String column = columnName.toLowerCase(Locale.ROOT);
            int comparison = column.contains("الكود") || column.contains("code") || column.contains("تاريخ") || column.contains("date")
                    ? normalizeComparableText(values.get(i - 1)).compareTo(normalizeComparableText(values.get(i)))
                    : compareCellValues(values.get(i - 1), values.get(i));
            if (ascending ? comparison > 0 : comparison < 0) {
                return false;
            }
        }
        return true;
    }

    @Step("تغيير حجم صفحة قائمة الفواتير إلى {pageSize}")
    public void setInvoicePageSize(int pageSize) {
        slAwaitListReady();
        String before = gridSnapshot();
        WebElement select = findPageSizeSelect();
        new Select(select).selectByVisibleText(String.valueOf(pageSize));
        waitForGridRefresh(before);
        slAwaitListReady();
    }

    @Step("قراءة حجم الصفحة المحدد لقائمة الفواتير")
    public int getInvoicePageSize() {
        String value = new Select(findPageSizeSelect()).getFirstSelectedOption().getText().trim();
        return Integer.parseInt(value.replaceAll("\\D", ""));
    }

    @Step("التحقق من أحجام الصفحات المتاحة في قائمة الفواتير")
    public List<Integer> getInvoicePageSizeOptions() {
        return new Select(findPageSizeSelect()).getOptions().stream()
                .map(WebElement::getText).map(String::trim)
                .filter(text -> text.matches("\\d+"))
                .map(Integer::parseInt).toList();
    }

    @Step("التحقق من إتاحة صفحة تالية لقائمة الفواتير")
    public boolean isNextInvoicePageEnabled() {
        WebElement button = findPaginationControl("next");
        return isPaginationEnabled(button);
    }

    @Step("التحقق من إتاحة صفحة سابقة لقائمة الفواتير")
    public boolean isPreviousInvoicePageEnabled() {
        WebElement button = findPaginationControl("previous");
        return isPaginationEnabled(button);
    }

    @Step("الانتقال إلى الصفحة التالية لقائمة الفواتير")
    public void goToNextInvoicePage() {
        clickPaginationControl("next");
    }

    @Step("العودة إلى الصفحة السابقة لقائمة الفواتير")
    public void goToPreviousInvoicePage() {
        clickPaginationControl("previous");
    }

    @Step("الانتقال إلى الصفحة الأولى لقائمة الفواتير")
    public void goToFirstInvoicePage() {
        while (isPreviousInvoicePageEnabled()) {
            clickPaginationControl("previous");
        }
    }

    @Step("الانتقال إلى الصفحة الأخيرة لقائمة الفواتير")
    public void goToLastInvoicePage() {
        while (isNextInvoicePageEnabled()) {
            clickPaginationControl("next");
        }
    }

    @Step("الانتقال إلى صفحة قائمة الفواتير رقم {pageNumber}")
    public void goToInvoicePage(int pageNumber) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Invoice list page numbers start from 1");
        }
        WebElement page = driver.findElements(PAGINATION_ITEMS).stream()
                .filter(InvoicListPage::isDisplayed)
                .flatMap(item -> item.findElements(By.cssSelector("button, a, [role='menuitem']")).stream())
                .filter(InvoicListPage::isDisplayed)
                .filter(button -> button.getText().trim().equals(String.valueOf(pageNumber)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Invoice list page number not visible: " + pageNumber));
        if (pageNumber != getCurrentInvoicePageNumber()) {
            int previousPage = getCurrentInvoicePageNumber();
            String before = gridSnapshot();
            By locator = By.cssSelector(".pagination [aria-label='Go to page " + pageNumber + "']");
            WebElement control = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click()", control);
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> getCurrentInvoicePageNumber() == pageNumber);
            waitForGridRefresh(before);
        }
    }

    @Step("قراءة رقم الصفحة النشطة في قائمة الفواتير")
    public int getCurrentInvoicePageNumber() {
        List<WebElement> active = driver.findElements(By.cssSelector(".pagination .page-item.active"));
        String digits = active.isEmpty() ? "" : active.get(0).getText().replaceAll("\\D", "");
        return digits.isBlank() ? 1 : Integer.parseInt(digits);
    }

    @Step("قراءة نص معلومات الصفحات في قائمة الفواتير")
    public String getInvoicePaginationSummary() {
        return driver.findElements(By.cssSelector(".pagination, [class*='pagination'], [class*='page-info']"))
                .stream().filter(InvoicListPage::isDisplayed).map(WebElement::getText)
                .map(String::trim).filter(text -> !text.isEmpty()).findFirst().orElse("");
    }

    @Step("قراءة رسائل التحقق الظاهرة في قائمة الفواتير")
    public List<String> getInvoiceValidationMessages() {
        return driver.findElements(By.cssSelector("[role='alert'], .invalid-feedback, .text-danger, .is-invalid + small"))
                .stream().filter(InvoicListPage::isDisplayed).map(WebElement::getText).map(String::trim)
                .filter(text -> !text.isEmpty()).toList();
    }

    @Step("قراءة كل قيم الصفحات الظاهرة في قائمة الفواتير")
    public List<String> getInvoiceRowIdentities(String columnName) {
        return getInvoiceColumnValues(columnName);
    }

    @Step("إعادة تحميل شاشة قائمة الفواتير والانتظار")
    public void refreshInvoiceList() {
        driver.navigate().refresh();
        waitForInvoiceList();
    }

    private WebElement vueFilter(String label) {
        return driver.findElement(By.xpath("//*[self::label or self::legend][normalize-space()='" + label + "']/following::div[@role='combobox'][1]"));
    }
    private String selectedVueFilter(String label) {
        return vueFilter(label).findElements(By.cssSelector(".vs__selected")).stream().map(WebElement::getText).map(String::trim).findFirst().orElse("");
    }

    private WebElement findFilterControl(String label) {
        ensureInvoiceFiltersVisible();
        String key = label == null ? "" : label.toLowerCase(Locale.ROOT);
        if (key.contains("مندوب") || key.contains("agent")) return vueFilter("مندوب");
        if (key.equals("العميل") || key.equals("customer")) return vueFilter("العميل");
        String selector = switch (key) {
            case "رقم الجوال", "الجوال", "جوال", "الهاتف", "phone", "mobile" -> "input[name='phone']";
            case "اسم العميل", "customer name" -> "input[name='onTheFlyDealerName']";
            case "الملاحظات", "ملاحظات", "notes" -> "input[name='notes']";
            case "الكود", "code" -> "input[name='filteredCode']";
            case "رقم الطلب", "order number" -> "input[name='orderNumber']";
            case "من تاريخ", "from date" -> "input[name='fromDate']";
            case "إلى تاريخ", "الي تاريخ", "to date" -> "input[name='toDate']";
            default -> null;
        };
        if (selector != null) {
            List<WebElement> exact = driver.findElements(By.cssSelector(selector));
            if (!exact.isEmpty()) return exact.get(0);
        }
        if (key.contains("عميل") || key.contains("customer")) return vueFilter("العميل");
        String needle = escapeXPath(label.toLowerCase(Locale.ROOT));
        List<WebElement> labelled = driver.findElements(By.xpath(
                "//label[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + needle + "')]/following::input[1] | "
                        + "//label[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + needle + "')]/following::select[1] | "
                        + "//*[self::label or self::legend][contains(normalize-space(.), '" + escapeXPath(label) + "')]/following::*[self::input or self::select][1]"
        ));
        WebElement control = labelled.stream().filter(InvoicListPage::isDisplayed).findFirst().orElse(null);
        if (control != null) {
            return control;
        }

        List<WebElement> byPlaceholder = driver.findElements(By.cssSelector(
                "input[placeholder*='" + label + "'], select[aria-label*='" + label + "'], input[aria-label*='" + label + "']"));
        control = byPlaceholder.stream().filter(InvoicListPage::isDisplayed).findFirst().orElse(null);
        if (control != null) {
            return control;
        }

        String normalized = label.toLowerCase(Locale.ROOT);
        List<WebElement> candidates = driver.findElements(By.cssSelector("input, select"));
        int fallbackIndex = switch (normalized) {
            case "mobile", "phone", "رقم الجوال", "الجوال" -> 0;
            case "customer", "customer name", "العميل", "اسم العميل" -> 1;
            case "agent", "مندوب", "المندوب" -> 2;
            case "notes", "الملاحظات" -> 3;
            case "code", "الكود" -> 4;
            case "order number", "رقم الطلب" -> 5;
            case "from date", "من تاريخ" -> 6;
            case "to date", "إلى تاريخ" -> 7;
            case "payment status", "حالة السداد" -> 8;
            case "movement type", "نوع الحركة" -> 9;
            default -> -1;
        };
        List<WebElement> filterCandidates = candidates.stream().filter(InvoicListPage::isDisplayed).toList();
        if (fallbackIndex >= 0 && fallbackIndex < filterCandidates.size()) {
            return filterCandidates.get(fallbackIndex);
        }
        throw new IllegalStateException("Invoice filter control not found for label: " + label);
    }

    private List<WebElement> findFilterInputs() {
        return driver.findElements(By.cssSelector("input:not([type='hidden']):not([type='checkbox']), select"))
                .stream().filter(InvoicListPage::isDisplayed).collect(Collectors.toCollection(ArrayList::new));
    }

    private WebElement findPageSizeSelect() {
        List<WebElement> exact = driver.findElements(By.id("perPageSelect"));
        if (!exact.isEmpty()) return exact.get(0);
        List<WebElement> selects = driver.findElements(By.cssSelector("select")).stream()
                .filter(InvoicListPage::isDisplayed).toList();
        return selects.stream()
                .filter(select -> new Select(select).getOptions().stream()
                        .anyMatch(option -> option.getText().trim().matches("\\d+")))
                .findFirst()
                .orElseGet(() -> selects.stream()
                        .filter(select -> select.getAttribute("aria-label") != null
                                && select.getAttribute("aria-label").toLowerCase(Locale.ROOT).contains("page"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Invoice list page-size selector not found")));
    }

    private WebElement findPaginationControl(String kind) {
        String aria = switch (kind) {
            case "next" -> "Go to next page";
            case "previous" -> "Go to previous page";
            default -> "";
        };
        if (!aria.isEmpty()) {
            List<WebElement> exact = driver.findElements(By.cssSelector("[aria-label='" + aria + "']"));
            if (!exact.isEmpty()) return exact.get(0);
        }
        String[] tokens = switch (kind) {
            case "next" -> new String[]{"التالي", "next", "›", "❯", "»"};
            case "previous" -> new String[]{"السابق", "previous", "prev", "‹", "❮", "«"};
            case "first" -> new String[]{"الأول", "first", "|<", "«"};
            case "last" -> new String[]{"الأخير", "last", ">|", "»"};
            default -> new String[0];
        };
        List<WebElement> items = driver.findElements(PAGINATION_ITEMS).stream()
                .filter(InvoicListPage::isDisplayed).toList();
        List<WebElement> candidates = items.stream()
                .flatMap(item -> item.findElements(By.cssSelector("button, a, [role='menuitem']")).stream())
                .filter(InvoicListPage::isDisplayed).toList();
        WebElement match = candidates.stream().filter(candidate -> {
            String text = (candidate.getText() + " " + candidate.getAttribute("aria-label") + " "
                    + candidate.getAttribute("title")).toLowerCase(Locale.ROOT).trim();
            return List.of(tokens).stream().anyMatch(token -> text.contains(token.toLowerCase(Locale.ROOT)));
        }).findFirst().orElse(null);
        if (match != null) {
            return match;
        }
        if (items.size() < 3) {
            throw new IllegalStateException("Invoice list pagination control not found: " + kind);
        }
        WebElement item = switch (kind) {
            case "next" -> items.get(items.size() - 1);
            case "previous" -> items.get(0);
            case "first" -> items.get(0);
            case "last" -> items.get(items.size() - 1);
            default -> throw new IllegalArgumentException("Unknown pagination control: " + kind);
        };
        return item.findElement(By.cssSelector("button, a"));
    }

    private void clickPaginationControl(String kind) {
        String beforeGrid = gridSnapshot();
        WebElement control = findPaginationControl(kind);
        if (!isPaginationEnabled(control)) {
            throw new IllegalStateException("Invoice list pagination control is disabled: " + kind);
        }
        int before = getCurrentInvoicePageNumber();
        By locator = By.cssSelector("[aria-label='Go to " + kind + " page']");
        WebElement clickable = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click()", clickable);
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
            int after = getCurrentInvoicePageNumber();
            return kind.equals("next") ? after > before : after < before;
        });
        waitForGridRefresh(beforeGrid);
    }

    private void waitForGridRefresh() {
        waitForGridRefresh(null);
    }

    private void waitForGridRefresh(String previousGrid) {
        long[] quietSince = {0};
        long[] stableGridSince = {0};
        String[] observedGrid = {previousGrid};
        boolean[] changed = {previousGrid == null};
        boolean[] stableWithoutChange = {false};
        long startedAt = System.nanoTime();
        new WebDriverWait(driver, PAGE_TIMEOUT).until(d -> {
            boolean loading = d.findElements(By.cssSelector(
                            ".b-overlay, .position-absolute.bg-light.rounded-lg, .vld-overlay.is-active, .spinner-border, [class*='loading-overlay'], [class*='loader-wrapper']"))
                    .stream().anyMatch(InvoicListPage::isDisplayed);
            String grid = gridSnapshot();
            long now = System.nanoTime();
            if (!grid.isEmpty() && grid.equals(observedGrid[0])) {
                if (stableGridSince[0] == 0) stableGridSince[0] = now;
            } else {
                observedGrid[0] = grid;
                stableGridSince[0] = 0;
            }
            if (previousGrid != null && !grid.isEmpty() && !grid.equals(previousGrid)) changed[0] = true;
            if (loading) {
                quietSince[0] = 0;
                // Some screens leave the overlay mounted after the Vue grid has settled.
                // Require a stable table for 1.5s before accepting that state.
                return stableGridSince[0] != 0
                        && now - stableGridSince[0] >= Duration.ofMillis(1500).toNanos();
            }
            if (quietSince[0] == 0) quietSince[0] = System.nanoTime();
            long quietFor = System.nanoTime() - quietSince[0];
            if (!changed[0] && !grid.isEmpty() && stableGridSince[0] != 0
                    && now - stableGridSince[0] >= Duration.ofMillis(2500).toNanos()
                    && now - startedAt >= Duration.ofMillis(2500).toNanos()) {
                stableWithoutChange[0] = true;
            }
            return (changed[0] || stableWithoutChange[0])
                    && quietFor >= Duration.ofMillis(400).toNanos();
        });
    }

    private String gridSnapshot() {
        try {
            String rows = getVisibleInvoiceRows().stream()
                    .map(row -> String.join("\u001f", row))
                    .collect(Collectors.joining("\u001e"));
            if (!rows.isEmpty()) return rows;
            String emptyState = driver.findElements(By.cssSelector(
                            ".b-table-empty-row, .dataTables_empty, .empty-state, table tbody tr"))
                    .stream().filter(InvoicListPage::isDisplayed)
                    .map(WebElement::getText).map(String::trim)
                    .filter(text -> !text.isEmpty()).collect(Collectors.joining("\u001e"));
            if (!emptyState.isEmpty()) return "EMPTY|" + emptyState;
            return isInvoiceTableDisplayed()
                    ? "EMPTY_TABLE|" + getCurrentInvoicePageNumber() + "|" + String.join("|", getInvoiceTableHeaders())
                    : "";
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            return "";
        }
    }

    private void captureFilterGridBaseline() {
        if (pendingFilterGridBaseline == null) pendingFilterGridBaseline = gridSnapshot();
    }

    private int getColumnIndex(String columnName) {
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

    private int compareCellValues(String left, String right) {
        String normalizedLeft = normalizeComparableText(left);
        String normalizedRight = normalizeComparableText(right);
        try {
            return new java.math.BigDecimal(normalizedLeft.replace(",", "")).compareTo(
                    new java.math.BigDecimal(normalizedRight.replace(",", "")));
        } catch (NumberFormatException ignored) {
            return normalizedLeft.compareTo(normalizedRight);
        }
    }

    private String normalizeComparableText(String value) {
        return value == null ? "" : value.trim()
                .replace("٬", "")
                .replace("٫", ".")
                .replaceAll("\\s+", " ");
    }

    private boolean isEmptyStateText(String value) {
        String text = value.toLowerCase(Locale.ROOT);
        return text.contains("لا يوجد") || text.contains("لا توجد") || text.contains("no data")
                || text.contains("no records") || text.contains("لا توجد بيانات");
    }

    private boolean isPaginationEnabled(WebElement button) {
        String parentClass = button.findElements(By.xpath("..")).stream().findFirst().map(e -> e.getAttribute("class")).orElse("");
        String classes = (button.getAttribute("class") + " " + parentClass).toLowerCase(Locale.ROOT);
        return !"true".equalsIgnoreCase(button.getAttribute("aria-disabled"))
                && !"true".equalsIgnoreCase(button.findElements(By.xpath("..")).stream().findFirst().map(e -> e.getAttribute("aria-disabled")).orElse("false"))
                && (button.getTagName().equalsIgnoreCase("span") || button.isEnabled()) && !classes.contains("disabled")
                && !"true".equalsIgnoreCase(button.getAttribute("aria-disabled"));
    }

    private boolean containsAny(WebElement element, String... tokens) {
        String text = (element.getText() + " " + element.getAttribute("aria-label") + " "
                + element.getAttribute("title") + " " + element.getAttribute("value"))
                .toLowerCase(Locale.ROOT);
        for (String token : tokens) {
            if (text.contains(token.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            return false;
        }
    }

    private static String escapeXPath(String value) {
        return value.replace("'", "&apos;");
    }

    private static boolean isDisplayedNoThrow(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            return false;
        }
    }

    @Step("تصدير قائمة الفواتير بصيغة {format}")
    public void exportInvoices(String format) {
        String normalizedFormat = format == null ? "" : format.trim().toLowerCase();
        if (!normalizedFormat.equals("pdf") && !normalizedFormat.equals("xls")
                && !normalizedFormat.equals("xlsx") && !normalizedFormat.equals("excel")) {
            throw new IllegalArgumentException("Supported invoice export formats are PDF and Excel");
        }

        String exactSelector = normalizedFormat.equals("pdf")
                ? "button:has(img[src*='pdf.'])" : "button:has(img[src*='xls.'])";
        List<WebElement> exact = driver.findElements(By.cssSelector(exactSelector));
        if (exact.stream().anyMatch(InvoicListPage::isDisplayed)) {
            exact.stream().filter(InvoicListPage::isDisplayed).findFirst().orElseThrow().click();
            return;
        }
        String tokenMatch = normalizedFormat.equals("pdf")
                ? "contains(" + EXPORT_SEARCHABLE_TEXT + ", 'pdf')"
                : "(contains(" + EXPORT_SEARCHABLE_TEXT + ", 'xls') or "
                        + "contains(" + EXPORT_SEARCHABLE_TEXT + ", 'excel') or "
                        + "contains(" + EXPORT_SEARCHABLE_TEXT + ", 'spreadsheet'))";
        By exportControl = By.xpath(
                "//*[self::button or self::a][" + tokenMatch + " or .//*[" + tokenMatch + "]]"
        );

        List<WebElement> controls = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    List<WebElement> found = d.findElements(exportControl);
                    return found.stream().anyMatch(WebElement::isDisplayed) ? found : null;
                });
        controls.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Invoice export control not visible: " + format))
                .click();
    }

    private void ensureInvoiceFiltersVisible() {
        new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> {
            try {
                WebElement show = d.findElements(By.cssSelector("button")).stream()
                        .filter(InvoicListPage::isDisplayed)
                        .filter(button -> button.getText().contains("عرض التصفية"))
                        .findFirst().orElse(null);
                if (show != null) {
                    ((org.openqa.selenium.JavascriptExecutor) d).executeScript("arguments[0].click()", show);
                    return true;
                }
                return d.findElements(By.cssSelector("input[name='filteredCode'], input[name='fromDate']"))
                        .stream().anyMatch(InvoicListPage::isDisplayed);
            } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                return false;
            }
        });
    }

    private void selectVueOption(String inputId, String value) {
        WebElement combo = driver.findElement(By.id(inputId));
        WebElement input = combo.findElement(By.cssSelector("input.vs__search"));
        By option = By.xpath("//*[contains(@class,'vs__dropdown-option') and normalize-space(.)='" + escapeXPath(value) + "']");
        if (driver.findElements(option).stream().noneMatch(InvoicListPage::isDisplayed)) {
            clickVueDropdown(combo);
        }
        if (input.isDisplayed() && input.isEnabled() && !"true".equalsIgnoreCase(input.getAttribute("readonly"))) {
            try {
                String current = input.getAttribute("value");
                if (current == null || !current.contains(value)) {
                input.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
                input.sendKeys(value);
                }
            } catch (org.openqa.selenium.InvalidElementStateException ignored) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "const input=arguments[0];input.value=arguments[1];"
                                + "input.dispatchEvent(new Event('input',{bubbles:true}));"
                                + "input.dispatchEvent(new Event('change',{bubbles:true}));", input, value);
            }
        }
        new WebDriverWait(driver, Duration.ofSeconds(8)).until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    private String getVueSelectedValue(String inputId) {
        Object selected = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "const input=document.getElementById(arguments[0]);let root=input;"
                        + "for(let i=0;root&&i<8;i++,root=root.parentElement){const item=root.querySelector('.vs__selected');"
                        + "if(item)return (item.innerText||item.textContent||'').trim();}return '';", inputId);
        return selected == null ? "" : selected.toString().trim();
    }

    private void clickVueDropdown(WebElement input) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "const control=arguments[0],select=control.closest('.v-select');"
                        + "const toggle=(control.matches('.vs__dropdown-toggle')&&control)"
                        + "||(select&&select.querySelector('.vs__dropdown-toggle'));"
                        + "if(toggle){toggle.click();return true;}control.click();return true;", input);
    }

    private void setDateFilter(WebElement hiddenInput, String value) {
        String pickerId = "fromDate".equals(hiddenInput.getAttribute("name")) ? "example-datepicker" : "example-datepicker2";
        WebElement picker = driver.findElement(By.id(pickerId));
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.id(pickerId)));
        picker = driver.findElement(By.id(pickerId));
        picker.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        String[] parts = value.split("-");
        if (parts.length != 3) throw new IllegalArgumentException("Date must use yyyy-MM-dd");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        wait.until(d -> calendarIsVisible(pickerId));
        String shown = calendarCaption(pickerId);
        java.time.YearMonth target = java.time.YearMonth.of(year, month);
        for (int i = 0; i < 240; i++) {
            java.util.regex.Matcher yearMatcher = java.util.regex.Pattern.compile("(\\d{4})").matcher(shown);
            if (!yearMatcher.find()) throw new IllegalStateException("Calendar year not found: " + shown);
            int shownYear = Integer.parseInt(yearMatcher.group(1));
            int shownMonth = parseMonth(shown);
            java.time.YearMonth current = java.time.YearMonth.of(shownYear, shownMonth);
            if (current.equals(target)) break;
            String direction = target.isBefore(current) ? "previous" : "next";
            String previousCaption = shown;
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "const direction=arguments[0],picker=document.getElementById(arguments[1]);"
                            + "const calendars=[...document.querySelectorAll('.b-calendar')];"
                            + "const calendar=calendars.find(c=>c.getClientRects().length&&picker&&c.closest('.b-form-datepicker')===picker.closest('.b-form-datepicker'))"
                            + "||calendars.find(c=>c.getClientRects().length);"
                            + "const buttons=calendar?[...calendar.querySelectorAll('.b-calendar-nav button')]:[];"
                            + "const button=buttons.find(b=>((b.getAttribute('aria-label')||'')+' '+(b.title||'')).toLowerCase().includes(direction))"
                            + "||(buttons.length?buttons[direction==='previous'?0:buttons.length-1]:null);"
                            + "if(button){button.click();return true;}return false;", direction, pickerId);
            shown = new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> {
                try {
                    String currentCaption = calendarCaption(pickerId);
                    return currentCaption.equals(previousCaption) ? null : currentCaption;
                } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                    return null;
                }
            });
        }
        String dayText = String.valueOf(day);
        By dayButtons = By.cssSelector(".b-calendar button, .b-calendar [role='button'], .b-calendar .btn");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> {
                try {
                    List<WebElement> candidates = d.findElements(dayButtons).stream()
                            .filter(InvoicListPage::isDisplayed)
                            .filter(button -> button.getText().trim().replaceAll("\\D", "").equals(dayText))
                            .filter(button -> !button.getAttribute("class").contains("text-muted"))
                            .filter(WebElement::isEnabled)
                            .toList();
                    WebElement targetDay = candidates.stream()
                            .filter(button -> String.valueOf(button.getAttribute("aria-label")).contains(String.valueOf(year)))
                            .findFirst().orElse(candidates.stream().findFirst().orElse(null));
                    if (targetDay == null) return false;
                    targetDay.click();
                    return true;
                } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                    return false;
                }
            });
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new IllegalStateException("Calendar day not found: " + value, e);
        }
    }

    private boolean calendarIsVisible(String pickerId) {
        return Boolean.TRUE.equals(((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "const picker=document.getElementById(arguments[0]),calendars=[...document.querySelectorAll('.b-calendar')];"
                        + "return calendars.some(c=>c.getClientRects().length&&picker&&c.closest('.b-form-datepicker')===picker.closest('.b-form-datepicker'))"
                        + "||calendars.some(c=>c.getClientRects().length);", pickerId));
    }

    private String calendarCaption(String pickerId) {
        Object value = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "const picker=document.getElementById(arguments[0]),calendars=[...document.querySelectorAll('.b-calendar')];"
                        + "const calendar=calendars.find(c=>c.getClientRects().length&&picker&&c.closest('.b-form-datepicker')===picker.closest('.b-form-datepicker'))"
                        + "||calendars.find(c=>c.getClientRects().length);"
                        + "const caption=calendar&&calendar.querySelector('.b-calendar-grid-caption');"
                        + "return caption?(caption.innerText||caption.textContent||'').trim():'';");
        return value == null ? "" : value.toString();
    }

    private int parseMonth(String text) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d{1,2})[/-]").matcher(text);
        if (matcher.find()) return Integer.parseInt(matcher.group(1));
        String normalized = text.replaceAll("\\d", "").trim();
        String[] arabicMonths = {"يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"};
        for (int i = 0; i < arabicMonths.length; i++) if (normalized.contains(arabicMonths[i])) return i + 1;
        return java.time.Month.valueOf(normalized.toUpperCase(Locale.ROOT).substring(0, 3)).getValue();
    }

    private List<Integer> visibleColumnIndexes() {
        return driver.findElements(GRID_HEADERS).stream().filter(InvoicListPage::isDisplayed)
                .filter(header -> !header.getText().trim().isEmpty())
                .map(header -> header.getAttribute("aria-colindex"))
                .map(index -> index == null || index.isBlank() ? "0" : index)
                .map(Integer::parseInt).toList();
    }

    private WebElement findColumnHeader(String columnName) {
        return driver.findElements(GRID_HEADERS).stream().filter(InvoicListPage::isDisplayed)
                .filter(header -> cleanHeaderText(header.getText()).contains(cleanHeaderText(columnName))
                        || cleanHeaderText(columnName).contains(cleanHeaderText(header.getText())))
                .findFirst().orElseThrow(() -> new IllegalStateException("Invoice table column not found: " + columnName));
    }

    private String cleanHeaderText(String text) {
        return text.replaceAll("(?i)\\s*\\(click to sort.*?\\)", "").trim();
    }

    private List<String> getRadioOptions(int groupIndex) {
        List<WebElement> groups = driver.findElements(By.cssSelector(".filter-buttons"));
        if (groups.size() <= groupIndex) return List.of();
        return groups.get(groupIndex).findElements(By.cssSelector("label span")).stream()
                .filter(InvoicListPage::isDisplayed).map(WebElement::getText).map(String::trim).toList();
    }

    private void clickRadioOption(int groupIndex, String value) {
        List<WebElement> groups = driver.findElements(By.cssSelector(".filter-buttons"));
        WebElement group = groups.get(groupIndex);
        WebElement label = group.findElements(By.cssSelector("label")).stream()
                .filter(option -> option.getText().trim().equals(value))
                .findFirst().orElseThrow(() -> new IllegalStateException("Radio option not found: " + value));
        label.click();
        waitForGridRefresh();
    }

    private String getSelectedRadioValue(int groupIndex) {
        List<WebElement> groups = driver.findElements(By.cssSelector(".filter-buttons"));
        if (groups.size() <= groupIndex) return "";
        return groups.get(groupIndex).findElements(By.cssSelector("label.active span")).stream()
                .findFirst().map(WebElement::getText).map(String::trim).orElse("");
    }
}
