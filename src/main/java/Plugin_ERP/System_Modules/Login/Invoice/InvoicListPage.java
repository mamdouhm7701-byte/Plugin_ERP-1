package Plugin_ERP.System_Modules.Login.Invoice;

import io.qameta.allure.Step;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InvoicListPage extends SalesModulePage {
    private final InvoiceListGrid grid;
    private final InvoiceListFilters filters;
    private static final Duration PAGE_TIMEOUT = Duration.ofSeconds(20);

    private static final By GRID_HEADERS = By.cssSelector("table thead th");
    private final InvoiceListCalendar calendar;
    private final InvoiceListExport exports;
    private final InvoiceListPagination pagination;

    public InvoicListPage(WebDriver driver) {
        super(driver);
        grid = new InvoiceListGrid(this, driver);
        filters = new InvoiceListFilters(this, driver);
        calendar = new InvoiceListCalendar(driver);
        exports = new InvoiceListExport(driver);
        pagination =
                new InvoiceListPagination(
                        this, driver, this::gridSnapshot, this::waitForGridRefresh);
    }

    @Step("انتظار ظهور قائمة الفواتير")
    public void waitForInvoiceList() {
        WebDriverWait wait = new WebDriverWait(driver, PAGE_TIMEOUT);
        wait.until(
                ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(GRID_HEADERS),
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("input[placeholder*='بحث']")),
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".pagination"))));
    }

    @Step("التحقق من ظهور جدول الفواتير")
    public boolean isInvoiceTableDisplayed() {
        return grid.isInvoiceTableDisplayed();
    }

    @Step("قراءة عناوين أعمدة قائمة الفواتير")
    public List<String> getInvoiceTableHeaders() {
        return grid.getInvoiceTableHeaders();
    }

    @Step("قراءة صفوف قائمة الفواتير الظاهرة")
    public List<List<String>> getVisibleInvoiceRows() {
        return grid.getVisibleInvoiceRows();
    }

    @Step("البحث في فلتر قائمة الفواتير: {label}")
    public void setInvoiceTextFilter(String label, String value) {
        filters.setInvoiceTextFilter(label, value);
    }

    @Step("تطبيق فلاتر قائمة الفواتير")
    public void applyInvoiceFilters() {
        filters.applyInvoiceFilters();
    }

    @Step("تصفير فلاتر قائمة الفواتير")
    public void resetInvoiceFilters() {
        driver.navigate().refresh();
        waitForInvoiceList();
        new WebDriverWait(driver, PAGE_TIMEOUT)
                .until(
                        d -> {
                            try {
                                return isInvoiceTableDisplayed();
                            } catch (org.openqa.selenium.StaleElementReferenceException
                                    | IllegalStateException ignored) {
                                Plugin_ERP.Bots.LogUtils.debug(
                                        "InvoicListPage fallback: "
                                                + ignored.getClass().getSimpleName());

                                // The page can briefly render without a table while Vue restores
                                // the data.
                                return false;
                            }
                        });
    }

    @Step("اختيار قيمة فلتر قائمة الفواتير: {label} = {value}")
    public void selectInvoiceFilter(String label, String value) {
        filters.selectInvoiceFilter(label, value);
    }

    @Step("إرجاع خيارات فلتر قائمة الفواتير: {label}")
    public List<String> getInvoiceFilterOptions(String label) {
        return filters.getInvoiceFilterOptions(label);
    }

    public List<String> getInvoiceFilterOptions(String label, String query) {
        return filters.getInvoiceFilterOptions(label, query);
    }

    @Step("قراءة القيمة الحالية لفلتر قائمة الفواتير: {label}")
    public String getInvoiceFilterValue(String label) {
        return filters.getInvoiceFilterValue(label);
    }

    @Step("التحقق من ظهور السجلات المطابقة للفلتر")
    public boolean areVisibleRowsMatching(String expectedText) {
        return grid.areVisibleRowsMatching(expectedText);
    }

    @Step("قراءة حالة عدم وجود نتائج في قائمة الفواتير")
    public boolean isInvoiceEmptyStateDisplayed() {
        return grid.isInvoiceEmptyStateDisplayed();
    }

    @Step("قراءة أول قيمة ظاهرة في عمود قائمة الفواتير: {columnName}")
    public String getFirstInvoiceCell(String columnName) {
        return grid.getFirstInvoiceCell(columnName);
    }

    @Step("قراءة قيم عمود قائمة الفواتير: {columnName}")
    public List<String> getInvoiceColumnValues(String columnName) {
        return grid.getInvoiceColumnValues(columnName);
    }

    @Step("فرز قائمة الفواتير حسب العمود: {columnName}")
    public void sortInvoiceColumn(String columnName, boolean ascending) {
        grid.sortInvoiceColumn(columnName, ascending);
    }

    @Step("التحقق من ترتيب عمود قائمة الفواتير")
    public boolean isInvoiceColumnSorted(String columnName, boolean ascending) {
        return grid.isInvoiceColumnSorted(columnName, ascending);
    }

    @Step("تغيير حجم صفحة قائمة الفواتير إلى {pageSize}")
    public void setInvoicePageSize(int pageSize) {
        pagination.setInvoicePageSize(pageSize);
    }

    @Step("قراءة حجم الصفحة المحدد لقائمة الفواتير")
    public int getInvoicePageSize() {
        return pagination.getInvoicePageSize();
    }

    @Step("التحقق من أحجام الصفحات المتاحة في قائمة الفواتير")
    public List<Integer> getInvoicePageSizeOptions() {
        return pagination.getInvoicePageSizeOptions();
    }

    @Step("التحقق من إتاحة صفحة تالية لقائمة الفواتير")
    public boolean isNextInvoicePageEnabled() {
        return pagination.isNextInvoicePageEnabled();
    }

    @Step("التحقق من إتاحة صفحة سابقة لقائمة الفواتير")
    public boolean isPreviousInvoicePageEnabled() {
        return pagination.isPreviousInvoicePageEnabled();
    }

    @Step("الانتقال إلى الصفحة التالية لقائمة الفواتير")
    public void goToNextInvoicePage() {
        pagination.goToNextInvoicePage();
    }

    @Step("العودة إلى الصفحة السابقة لقائمة الفواتير")
    public void goToPreviousInvoicePage() {
        pagination.goToPreviousInvoicePage();
    }

    @Step("الانتقال إلى الصفحة الأولى لقائمة الفواتير")
    public void goToFirstInvoicePage() {
        pagination.goToFirstInvoicePage();
    }

    @Step("الانتقال إلى الصفحة الأخيرة لقائمة الفواتير")
    public void goToLastInvoicePage() {
        pagination.goToLastInvoicePage();
    }

    @Step("الانتقال إلى صفحة قائمة الفواتير رقم {pageNumber}")
    public void goToInvoicePage(int pageNumber) {
        pagination.goToInvoicePage(pageNumber);
    }

    @Step("قراءة رقم الصفحة النشطة في قائمة الفواتير")
    public int getCurrentInvoicePageNumber() {
        return pagination.getCurrentInvoicePageNumber();
    }

    @Step("قراءة نص معلومات الصفحات في قائمة الفواتير")
    public String getInvoicePaginationSummary() {
        return pagination.getInvoicePaginationSummary();
    }

    @Step("قراءة رسائل التحقق الظاهرة في قائمة الفواتير")
    public List<String> getInvoiceValidationMessages() {
        return driver
                .findElements(
                        By.cssSelector(
                                "[role='alert'], .invalid-feedback, .text-danger, .is-invalid + small"))
                .stream()
                .filter(InvoicListPage::isDisplayed)
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .toList();
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

    String filterGridSnapshot() {
        return gridSnapshot();
    }

    void awaitFilterGridRefresh(String before) {
        waitForGridRefresh(before);
    }

    private void waitForGridRefresh() {
        grid.waitForGridRefresh();
    }

    private void waitForGridRefresh(String previousGrid) {
        grid.waitForGridRefresh(previousGrid);
    }

    private String gridSnapshot() {
        return grid.gridSnapshot();
    }

    private static boolean isDisplayed(WebElement element) {
        return InvoiceListFilters.isDisplayed(element);
    }

    @Step("تصدير قائمة الفواتير بصيغة {format}")
    public void exportInvoices(String format) {
        exports.exportInvoices(format);
    }

    void setCalendarFilter(WebElement hiddenInput, String value) {
        calendar.setDateFilter(hiddenInput, value);
    }
}
