package Invoice;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Invoice.InvoicListPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.Owner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class InvoiceListTestCases extends BaseTest {

    private static final Set<String> PDF_EXTENSIONS = Set.of(".pdf");
    private static final Set<String> EXCEL_EXTENSIONS = Set.of(".xls", ".xlsx");

    private InvoicListPage openInvoiceList() {
        InvoicListPage invoiceListPage = new InvoicListPage(driver);
        invoiceListPage.NavigationHelper(Pages.INVOICELIST);
        invoiceListPage.waitForInvoiceList();
        invoiceListPage.refreshInvoiceList();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> !invoiceListPage.getVisibleInvoiceRows().isEmpty());
        return invoiceListPage;
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("عرض قائمة الفواتير بالنتائج الافتراضية")
    @Description("يتحقق من ظهور جدول قائمة الفواتير وعناوين الأعمدة عند فتح الشاشة.")
    @Test(priority = 1)
    public void BrowseInvoiceListShowsDefaultRows() {
        InvoicListPage page = openInvoiceList();
        Assert.assertTrue(page.isInvoiceTableDisplayed(), "TC-FLT-001: يجب ظهور جدول قائمة الفواتير");
        Assert.assertFalse(page.getInvoiceTableHeaders().isEmpty(), "TC-FLT-001: يجب ظهور عناوين الجدول");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("التحقق من أعمدة الجدول وتناسق الصفوف")
    @Description("يتحقق من وجود أعمدة في الجدول وتطابق عدد خلايا كل صف مع عدد العناوين.")
    @Test(priority = 2)
    public void InvoiceListTableShowsColumnsAndAlignedRows() {
        InvoicListPage page = openInvoiceList();
        List<String> headers = page.getInvoiceTableHeaders();
        Assert.assertTrue(headers.size() >= 2, "TC-TBL-001: يجب أن يظهر عمودان على الأقل");
        page.getVisibleInvoiceRows().forEach(row ->
                Assert.assertEquals(row.size(), headers.size(), "TC-TBL-002: يجب تطابق عدد خلايا الصف مع الأعمدة"));
    }

    @DataProvider(name = "notFoundInvoiceTextFilters")
    public Object[][] notFoundInvoiceTextFilters() {
        return new Object[][]{
                {"رقم الجوال", "TC-FLT-003"},
                {"الملاحظات", "TC-FLT-010"}, {"الكود", "TC-FLT-012"},
                {"رقم الطلب", "TC-FLT-014"}
        };
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("التحقق من الفلاتر النصية دون نتائج")
    @Description("يتحقق من عدم ظهور سجلات عند البحث بقيمة غير موجودة في الحقل المحدد.")
    @Test(priority = 3, dataProvider = "notFoundInvoiceTextFilters")
    public void NotFoundInvoiceTextFiltersShowEmptyState(String filter, String testCaseId) {
        InvoicListPage page = openInvoiceList();
        page.setInvoiceTextFilter(filter, "QA_NO_MATCH_" + System.nanoTime());
        page.applyInvoiceFilters();
        Assert.assertTrue(page.isInvoiceEmptyStateDisplayed() || page.getVisibleInvoiceRows().isEmpty(),
                testCaseId + ": لا يجب أن تظهر سجلات عند عدم وجود تطابق في " + filter);
    }

    @DataProvider(name = "existingInvoiceTextFilters")
    public Object[][] existingInvoiceTextFilters() {
        return new Object[][]{
                {"الكود", new String[]{"الكود", "كود"}, "TC-FLT-011"}
        };
    }



    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("البحث الجزئي بكود الفاتورة")
    @Description("يتحقق من أن نتائج البحث الجزئي بكود الفاتورة لا تتضمن سجلات غير مطابقة.")
    @Test(priority = 5)
    public void PartialCodeFilterReturnsOnlyMatchingRows() {
        InvoicListPage page = openInvoiceList();
        String code = firstNonBlankCell(page, new String[]{"الكود", "كود"});
        Assert.assertFalse(code.isBlank(), "TC-FLT-004: يلزم كود مرجعي من عمود ظاهر");
        String fragment = code.substring(0, Math.min(4, code.length()));
        page.setInvoiceTextFilter("الكود", fragment);
        page.applyInvoiceFilters();
        List<List<String>> rows = page.getVisibleInvoiceRows();
        Assert.assertTrue(rows.isEmpty() || rows.stream().allMatch(row -> row.stream()
                        .anyMatch(cell -> cell.contains(fragment))),
                "TC-FLT-004: يجب أن تتبع النتائج سلوك المطابقة الجزئية أو الكاملة دون نتائج غير مطابقة");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("إعادة ضبط الفلاتر")
    @Description("يتحقق من عودة قائمة الفواتير إلى النتائج الافتراضية بعد إعادة ضبط الفلاتر.")
    @Test(priority = 6)
    public void ResetInvoiceFiltersReturnsToDefaultResults() {
        InvoicListPage page = openInvoiceList();
        List<List<String>> defaultRows = page.getVisibleInvoiceRows();
        page.setInvoiceTextFilter("الكود", "QA_NO_MATCH_" + System.nanoTime());
        page.applyInvoiceFilters();
        page.resetInvoiceFilters();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> page.isInvoiceTableDisplayed() && !page.getVisibleInvoiceRows().isEmpty());
        Assert.assertEquals(page.getVisibleInvoiceRows(), defaultRows, "TC-FLT-028: يجب الرجوع إلى النتائج الافتراضية");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("تحديث النتائج عند تغيير الفلتر")
    @Description("يتحقق من تحديث النتائج وإزالة النتائج السابقة بعد تغيير قيمة الفلتر.")
    @Test(priority = 7)
    public void ChangingAppliedFilterRefreshesResults() {
        InvoicListPage page = openInvoiceList();
        String code = firstNonBlankCell(page, new String[]{"الكود", "كود"});
        page.setInvoiceTextFilter("الكود", code);
        page.applyInvoiceFilters();
        List<List<String>> firstResults = page.getVisibleInvoiceRows();
        page.setInvoiceTextFilter("الكود", "QA_NO_MATCH_" + System.nanoTime());
        page.applyInvoiceFilters();
        Assert.assertTrue(page.getVisibleInvoiceRows().isEmpty() || page.isInvoiceEmptyStateDisplayed(),
                "TC-FLT-029/TC-EDGE-004: يجب إزالة النتائج السابقة بعد تغيير الفلتر");
        Assert.assertNotEquals(page.getVisibleInvoiceRows(), firstResults,
                "TC-FLT-029/TC-EDGE-004: يجب تحديث النتائج عند تغيير قيمة الفلتر");
    }

    @DataProvider(name = "invoicePageSizes")
    public Object[][] invoicePageSizes() {
        return new Object[][]{{25}, {50}, {100}, {500}};
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("تغيير حجم صفحة الفواتير")
    @Description("يتحقق من تطبيق حجم الصفحة المختار وعدم تجاوز عدد الصفوف للحد المحدد.")
    @Test(priority = 8, dataProvider = "invoicePageSizes")
    public void InvoicePageSizeCanBeSelected(int pageSize) {
        InvoicListPage page = openInvoiceList();
        List<Integer> available = page.getInvoicePageSizeOptions();
        Assert.assertTrue(available.contains(pageSize), "TC-SIZE: حجم الصفحة غير موجود: " + pageSize);
        page.setInvoicePageSize(pageSize);
        Assert.assertEquals(page.getInvoicePageSize(), pageSize, "TC-SIZE: يجب تطبيق حجم الصفحة المحدد");
        Assert.assertTrue(page.getVisibleInvoiceRows().size() <= pageSize,
                "TC-SIZE: عدد الصفوف الظاهرة لا يتجاوز حجم الصفحة");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("التحقق من خيارات حجم الصفحة")
    @Description("يتحقق من ظهور أحجام الصفحات المتوقعة 25 و50 و100 و500.")
    @Test(priority = 9)
    public void InvoicePageSizeOptionsAreExpected() {
        InvoicListPage page = openInvoiceList();
        Assert.assertTrue(page.getInvoicePageSizeOptions().containsAll(List.of(25, 50, 100, 500)),
                "TC-SIZE-008: يجب عرض الخيارات 25 و50 و100 و500");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("التنقل بين الصفحات التالية والسابقة")
    @Description("يتحقق من الانتقال إلى الصفحة التالية ثم الرجوع إلى الصفحة السابقة.")
    @Test(priority = 10)
    public void PaginationMovesForwardAndBack() {
        InvoicListPage page = openInvoiceList();
        Assert.assertTrue(page.isNextInvoicePageEnabled(), "TC-PAG-001: يلزم وجود صفحة تالية لبيانات الاختبار");
        int firstPage = page.getCurrentInvoicePageNumber();
        page.goToNextInvoicePage();
        Assert.assertEquals(page.getCurrentInvoicePageNumber(), firstPage + 1, "TC-PAG-001: يجب فتح الصفحة التالية");
        page.goToPreviousInvoicePage();
        Assert.assertEquals(page.getCurrentInvoicePageNumber(), firstPage, "TC-PAG-002: يجب العودة للصفحة السابقة");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("فتح صفحة برقم محدد")
    @Description("يتحقق من فتح الصفحة المطلوبة ثم العودة إلى الصفحة الأولى.")
    @Test(priority = 11)
    public void PaginationCanOpenAnExplicitPageNumber() {
        InvoicListPage page = openInvoiceList();
        Assert.assertTrue(page.isNextInvoicePageEnabled(), "TC-PAG-003: يلزم وجود صفحتين على الأقل");
        page.goToInvoicePage(2);
        Assert.assertEquals(page.getCurrentInvoicePageNumber(), 2, "TC-PAG-003: يجب فتح رقم الصفحة المحدد");
        page.goToFirstInvoicePage();
        Assert.assertEquals(page.getCurrentInvoicePageNumber(), 1, "TC-PAG-003: يجب الرجوع للصفحة الأولى");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("تعطيل السابق في الصفحة الأولى")
    @Description("يتحقق من تعطيل زر الصفحة السابقة عند الوقوف على الصفحة الأولى.")
    @Test(priority = 12)
    public void PreviousIsDisabledOnFirstInvoicePage() {
        InvoicListPage page = openInvoiceList();
        page.goToFirstInvoicePage();
        Assert.assertFalse(page.isPreviousInvoicePageEnabled(), "TC-PAG-004: يجب تعطيل السابق في الصفحة الأولى");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("تعطيل التالي في آخر صفحة")
    @Description("يتحقق من الوصول إلى آخر صفحة وتعطيل زر الانتقال إلى الصفحة التالية.")
    @Test(priority = 13)
    public void LastInvoicePageDisablesNext() {
        InvoicListPage page = openInvoiceList();
        Assert.assertTrue(page.isNextInvoicePageEnabled(), "TC-PAG-005: يلزم وجود صفحات متعددة لبيانات الاختبار");
        page.goToLastInvoicePage();
        Assert.assertFalse(page.isNextInvoicePageEnabled(), "TC-PAG-005/006: يجب الوصول لآخر صفحة وتعطيل التالي");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("تطبيق فلتر من صفحة متقدمة")
    @Description("يتحقق من إعادة ضبط رقم الصفحة بصورة صحيحة عند تطبيق فلتر من صفحة متقدمة.")
    @Test(priority = 14)
    public void FilterCanBeAppliedFromAdvancedPage() {
        InvoicListPage page = openInvoiceList();
        Assert.assertTrue(page.isNextInvoicePageEnabled(), "TC-PAG-009: يلزم وجود صفحة متقدمة لبيانات الاختبار");
        page.goToNextInvoicePage();
        page.setInvoiceTextFilter("الكود", "QA_NO_MATCH_" + System.nanoTime());
        page.applyInvoiceFilters();
        Assert.assertTrue(page.getCurrentInvoicePageNumber() == 1 || page.getCurrentInvoicePageNumber() == 0,
                "TC-PAG-009: يجب إعادة ترقيم النتائج بعد تضييق الفلتر");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("الفلاتر الفارغة وتحديث القائمة")
    @Description("يتحقق من سلامة عرض جدول الفواتير عند ترك الفلاتر فارغة وبعد تحديث القائمة.")
    @Test(priority = 15)
    public void EmptyFiltersAndRefreshKeepInvoiceListHealthy() {
        InvoicListPage page = openInvoiceList();
        page.resetInvoiceFilters();
        Assert.assertTrue(page.isInvoiceTableDisplayed(), "TC-EDGE-001: يجب أن تعمل الفلاتر الفارغة");
        page.refreshInvoiceList();
        Assert.assertTrue(page.isInvoiceTableDisplayed(), "TC-EDGE-003: يجب ظهور الجدول بعد التحديث");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("تحديث القائمة بعد تطبيق فلتر")
    @Description("يتحقق من بقاء الشاشة في حالة متسقة بعد تحديث القائمة عقب تطبيق فلتر.")
    @Test(priority = 16)
    public void RefreshAfterApplyingFilterKeepsAConsistentState() {
        InvoicListPage page = openInvoiceList();
        String missingValue = "QA_REFRESH_NO_MATCH_" + System.nanoTime();
        page.setInvoiceTextFilter("الكود", missingValue);
        page.applyInvoiceFilters();
        page.refreshInvoiceList();
        Assert.assertTrue(page.isInvoiceTableDisplayed(), "TC-EDGE-003: يجب أن تبقى الشاشة صالحة بعد التحديث");
    }


    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("عدم تكرار الفواتير بين الصفحات")
    @Description("يتحقق من عدم تكرار معرفات الفواتير بين صفحتين متتاليتين.")
    @Test(priority = 18)
    public void InvoiceRowsDoNotRepeatAcrossAdjacentPages() {
        InvoicListPage page = openInvoiceList();
        Assert.assertTrue(page.isNextInvoicePageEnabled(), "TC-EDGE-005: يلزم وجود صفحتين لبيانات الاختبار");
        String identityHeader = firstAvailableHeader(page, new String[]{"الكود", "رقم الفاتورة", "رقم الطلب"});
        Set<String> firstPage = new HashSet<>(page.getInvoiceRowIdentities(identityHeader));
        page.goToNextInvoicePage();
        List<String> nextPage = page.getInvoiceRowIdentities(identityHeader);
        Assert.assertTrue(nextPage.stream().noneMatch(firstPage::contains),
                "TC-EDGE-005: يجب عدم تكرار معرفات الفواتير بين الصفحات");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("فرز كود الفاتورة تصاعديًا وتنازليًا")
    @Description("يتحقق من عمل الفرز التصاعدي والتنازلي لعمود كود الفاتورة.")
    @Test(priority = 19)
    public void SortingInvoiceCodeAscendingAndDescending() {
        InvoicListPage page = openInvoiceList();
        String codeHeader = firstAvailableHeader(page, new String[]{"الكود", "رقم الفاتورة", "رقم الطلب"});
        page.sortInvoiceColumn(codeHeader, true);
        Assert.assertTrue(page.isInvoiceColumnSorted(codeHeader, true), "TC-SRT-001: يجب فرز الكود تصاعديًا");
        page.sortInvoiceColumn(codeHeader, false);
        Assert.assertTrue(page.isInvoiceColumnSorted(codeHeader, false), "TC-SRT-002: يجب فرز الكود تنازليًا");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("فرز الفواتير حسب التاريخ")
    @Description("يتحقق من ترتيب الفواتير حسب التاريخ تصاعديًا وتنازليًا.")
    @Test(priority = 20)
    public void SortingByDateColumn() {
        InvoicListPage page = openInvoiceList();
        String dateHeader = firstAvailableHeader(page, new String[]{"تاريخ الحركة", "التاريخ", "تاريخ الفاتورة"});
        page.sortInvoiceColumn(dateHeader, true);
        Assert.assertTrue(page.isInvoiceColumnSorted(dateHeader, true), "TC-SRT-004: يجب ترتيب التاريخ تصاعديًا");
        page.sortInvoiceColumn(dateHeader, false);
        Assert.assertTrue(page.isInvoiceColumnSorted(dateHeader, false), "TC-SRT-004: يجب ترتيب التاريخ تنازليًا");
    }

    @DataProvider(name = "invoiceDropdownFilters")
    public Object[][] invoiceDropdownFilters() {
        return new Object[][]{
                {"مندوب", "مندوب", new String[]{"مندوب", "اسم المندوب"}, "TC-FLT-007"},
                {"العميل", "العميل", new String[]{"المتعامل", "العميل", "اسم العميل"}, "TC-FLT-008"}
        };
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("تصفية الفواتير باستخدام قائمة منسدلة")
    @Description("يتحقق من اختيار قيمة موجودة من الفلتر المنسدل وعرض النتائج المطابقة.")
    @Test(priority = 21, dataProvider = "invoiceDropdownFilters")
    public void InvoiceDropdownFilterCanSelectAnExistingOption(String filterLabel, String column,
                                                               String[] headerAliases, String testCaseId) {
        InvoicListPage page = openInvoiceList();
        boolean agentFilter = filterLabel.contains("مندوب");
        String expected = agentFilter ? "مندوب تست" : "عميل عام";
        Assert.assertFalse(expected.isBlank(), testCaseId + ": يلزم سجل مرجعي لـ" + filterLabel);
        page.selectInvoiceFilter(filterLabel, expected);
        page.applyInvoiceFilters();
        if (agentFilter) {
            Assert.assertTrue(page.getInvoiceFilterValue(filterLabel).contains(expected),
                    testCaseId + ": يجب الاحتفاظ بالمندوب المختار");
            Assert.assertTrue(page.isInvoiceTableDisplayed(), testCaseId + ": يجب بقاء الجدول ظاهرًا");
        } else {
            Assert.assertTrue(page.areVisibleRowsMatching(expected), testCaseId + ": يجب أن تطابق النتائج " + filterLabel);
        }
    }

    @DataProvider(name = "invoicePaymentStatuses")
    public Object[][] invoicePaymentStatuses() {
        return new Object[][]{{"مدفوع", "TC-FLT-019"}, {"غير مدفوع", "TC-FLT-020"},
                {"مدفوع جزئي", "TC-FLT-021"}};
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("التصفية حسب حالة السداد")
    @Description("يتحقق من أن جميع النتائج تتوافق مع حالة السداد المختارة.")
    @Test(priority = 22, dataProvider = "invoicePaymentStatuses")
    public void PaymentStatusFilterShowsOnlySelectedStatus(String status, String testCaseId) {
        InvoicListPage page = openInvoiceList();
        List<String> statuses = page.getInvoiceFilterOptions("حالة السداد");
        String displayedStatus = status.equals("مدفوع جزئي") ? "مدفوع جزء" : status;
        Assert.assertTrue(statuses.stream().anyMatch(option -> option.contains(displayedStatus)),
                testCaseId + ": خيار حالة السداد غير متوفر: " + status);
        page.selectInvoiceFilter("حالة السداد", status);
        page.applyInvoiceFilters();
        Assert.assertEquals(page.getInvoiceFilterValue("حالة السداد"), status.equals("مدفوع جزئي") ? "مدفوع جزء" : status,
                testCaseId + ": يجب تمييز حالة السداد المختارة");
        Assert.assertTrue(page.isInvoiceTableDisplayed(), testCaseId + ": يجب بقاء قائمة الفواتير ظاهرة");
        assertPaymentAmountsMatchStatus(page, status, testCaseId);
    }

    private void assertPaymentAmountsMatchStatus(InvoicListPage page, String status, String testCaseId) {
        String paidHeader = firstAvailableHeader(page, new String[]{"المدفوع", "مبلغ المدفوع", "المدفوع فعلي"});
        String remainingHeader = firstAvailableHeader(page, new String[]{"الباقي", "المتبقي", "المتبقى", "المستحق"});
        List<String> paidValues = page.getInvoiceColumnValues(paidHeader);
        List<String> remainingValues = page.getInvoiceColumnValues(remainingHeader);
        Assert.assertEquals(paidValues.size(), remainingValues.size(),
                testCaseId + ": يجب توافر قيمة المدفوع والباقي لكل صف");

        for (int i = 0; i < paidValues.size(); i++) {
            java.math.BigDecimal paid = parseInvoiceAmount(paidValues.get(i));
            java.math.BigDecimal remaining = parseInvoiceAmount(remainingValues.get(i));
            switch (status) {
                case "مدفوع" -> Assert.assertEquals(remaining.compareTo(java.math.BigDecimal.ZERO), 0,
                        testCaseId + ": الباقي يجب أن يكون صفرًا للفاتورة المدفوعة، الصف " + (i + 1));
                case "غير مدفوع" -> Assert.assertEquals(paid.compareTo(java.math.BigDecimal.ZERO), 0,
                        testCaseId + ": المدفوع يجب أن يكون صفرًا للفاتورة غير المدفوعة، الصف " + (i + 1));
                case "مدفوع جزئي" -> {
                    Assert.assertTrue(paid.compareTo(java.math.BigDecimal.ZERO) > 0,
                            testCaseId + ": المدفوع يجب أن يكون أكبر من صفر للمدفوع جزئيًا، الصف " + (i + 1));
                    Assert.assertTrue(remaining.compareTo(java.math.BigDecimal.ZERO) > 0,
                            testCaseId + ": الباقي يجب أن يكون أكبر من صفر للمدفوع جزئيًا، الصف " + (i + 1));
                }
                default -> throw new IllegalArgumentException("حالة سداد غير مدعومة: " + status);
            }
        }
    }

    private java.math.BigDecimal parseInvoiceAmount(String value) {
        String normalized = value == null ? "" : value
                .replace('٠', '0').replace('١', '1').replace('٢', '2').replace('٣', '3')
                .replace('٤', '4').replace('٥', '5').replace('٦', '6').replace('٧', '7')
                .replace('٨', '8').replace('٩', '9')
                .replace('۰', '0').replace('۱', '1').replace('۲', '2').replace('۳', '3')
                .replace('۴', '4').replace('۵', '5').replace('۶', '6').replace('۷', '7')
                .replace('۸', '8').replace('۹', '9')
                .replace("٬", "").replace("٫", ".").replace(",", "").trim();
        java.util.regex.Matcher amount = java.util.regex.Pattern.compile("-?\\d+(?:\\.\\d+)?").matcher(normalized);
        if (!amount.find()) {
            throw new AssertionError("تعذر قراءة مبلغ الفاتورة: " + value);
        }
        return new java.math.BigDecimal(amount.group());
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("عرض جميع حالات السداد")
    @Description("يتحقق من عرض قائمة الفواتير عند اختيار جميع حالات السداد.")
    @Test(priority = 23)
    public void PaymentStatusAllRestoresUnrestrictedResults() {
        InvoicListPage page = openInvoiceList();
        List<String> options = page.getInvoiceFilterOptions("حالة السداد");
        String all = options.stream().filter(value -> value.equalsIgnoreCase("الكل")
                || value.toLowerCase(Locale.ROOT).contains("all")).findFirst().orElse("الكل");
        page.selectInvoiceFilter("حالة السداد", all);
        page.applyInvoiceFilters();
        Assert.assertTrue(page.isInvoiceTableDisplayed(), "TC-FLT-022: يجب عرض القائمة عند اختيار الكل");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("التصفية حسب نوع الحركة وإعادة الضبط")
    @Description("يتحقق من تطبيق فلتر نوع الحركة وإمكانية العودة إلى عرض جميع الأنواع.")
    @Test(priority = 24)
    public void InvoiceMovementTypeCanBeFilteredAndResetToAll() {
        InvoicListPage page = openInvoiceList();
        List<String> options = page.getInvoiceFilterOptions("نوع الحركة");
        Assert.assertFalse(options.isEmpty(), "TC-FLT-023/024: يجب عرض خيارات نوع الحركة");
        String movement = options.stream().filter(value -> !value.equalsIgnoreCase("الكل")
                && !value.toLowerCase(Locale.ROOT).contains("all")).findFirst().orElseThrow();
        page.selectInvoiceFilter("نوع الحركة", movement);
        page.applyInvoiceFilters();
        Assert.assertTrue(page.getVisibleInvoiceRows().stream().allMatch(row -> row.stream()
                        .anyMatch(cell -> cell.contains(movement))),
                "TC-FLT-023: النتائج يجب أن تطابق نوع الحركة المختار");
        String all = options.stream().filter(value -> value.equalsIgnoreCase("الكل")
                || value.toLowerCase(Locale.ROOT).contains("all")).findFirst().orElse(null);
        if (all != null) {
            page.selectInvoiceFilter("نوع الحركة", all);
            page.applyInvoiceFilters();
            Assert.assertTrue(page.isInvoiceTableDisplayed(), "TC-FLT-024: يجب الرجوع للقائمة غير المقيدة");
        }
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("قبول نطاق تاريخ صحيح ليوم واحد")
    @Description("يتحقق من قبول نطاق تاريخ صحيح مكوّن من يوم واحد.")
    @Test(priority = 25)
    public void ValidAndSingleDayInvoiceDateRangesAreAccepted() {
        InvoicListPage page = openInvoiceList();
        String date = firstNonBlankCell(page, new String[]{"تاريخ الحركة", "التاريخ", "تاريخ الفاتورة"});
        Assert.assertFalse(date.isBlank(), "TC-FLT-015/016: يلزم تاريخ مرجعي من سجل موجود");
        String isoDate = normalizeDateForInput(date);
        page.setInvoiceTextFilter("من تاريخ", isoDate);
        page.setInvoiceTextFilter("إلى تاريخ", isoDate);
        page.applyInvoiceFilters();
        String dateHeader = firstAvailableHeader(page, new String[]{"تاريخ الحركة", "التاريخ", "تاريخ الفاتورة"});
        Assert.assertTrue(page.getInvoiceColumnValues(dateHeader).isEmpty() || page.getInvoiceColumnValues(dateHeader).stream()
                        .allMatch(cell -> isoDate.equals(normalizeDateForInput(cell))),
                "TC-FLT-016: نتائج البحث بتاريخ يوم واحد يجب أن تكون داخل التاريخ المحدد");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("قبول نطاق تاريخ واسع وصحيح")
    @Description("يتحقق من قبول نطاق تاريخ صالح مع استمرار عرض جدول الفواتير.")
    @Test(priority = 26)
    public void BroadValidInvoiceDateRangeKeepsTheInvoiceListAvailable() {
        InvoicListPage page = openInvoiceList();
        page.setInvoiceTextFilter("من تاريخ", "2026-01-01");
        page.setInvoiceTextFilter("إلى تاريخ", "2027-12-31");
        page.applyInvoiceFilters();
        Assert.assertTrue(page.isInvoiceTableDisplayed(), "TC-FLT-015: يجب قبول نطاق تاريخ صالح");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("رفض نطاق تاريخ غير منطقي")
    @Description("يتحقق من عدم عرض نتائج مضللة عند إدخال تاريخ بداية بعد تاريخ النهاية.")
    @Test(priority = 27)
    public void InvalidInvoiceDateRangeDoesNotReturnMisleadingRows() {
        InvoicListPage page = openInvoiceList();
        page.setInvoiceTextFilter("من تاريخ", "2027-12-31");
        page.setInvoiceTextFilter("إلى تاريخ", "2026-01-01");
        page.applyInvoiceFilters();
        Assert.assertTrue(page.isInvoiceEmptyStateDisplayed() || page.getVisibleInvoiceRows().isEmpty()
                        || !page.getInvoiceValidationMessages().isEmpty(),
                "TC-FLT-017: يجب رفض نطاق التاريخ غير المنطقي أو إبراز حالته بوضوح");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("عدم عرض سجلات خارج نطاق التاريخ")
    @Description("يتحقق من عدم بقاء سجلات قديمة عند البحث في نطاق تاريخ لا يحتوي على نتائج.")
    @Test(priority = 28)
    public void DateRangeWithoutRecordsShowsNoStaleRows() {
        InvoicListPage page = openInvoiceList();
        page.setInvoiceTextFilter("من تاريخ", "2025-01-01");
        page.setInvoiceTextFilter("إلى تاريخ", "2025-01-02");
        page.applyInvoiceFilters();
        Assert.assertTrue(page.getVisibleInvoiceRows().isEmpty() || page.isInvoiceEmptyStateDisplayed(),
                "TC-FLT-018: يجب عدم إبقاء صفوف خارج نطاق التاريخ");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("الجمع بين فلتر العميل والمندوب والفرز")
    @Description("يتحقق من تطبيق شرطي العميل والمندوب معًا واستمرار تأثيرهما بعد الفرز.")
    @Test(priority = 29)
    public void CombinedInvoiceFiltersAndSortingKeepBothConditions() {
        InvoicListPage page = openInvoiceList();
        String customer = "عميل عام";
        String agent = "مندوب تست";
        Assert.assertFalse(customer.isBlank(), "TC-FLT-025: يلزم عميل مرجعي");
        Assert.assertFalse(agent.isBlank(), "TC-FLT-025: يلزم مندوب مرجعي");
        page.selectInvoiceFilter("العميل", customer);
        page.selectInvoiceFilter("مندوب", agent);
        page.applyInvoiceFilters();
        Assert.assertTrue(page.getInvoiceFilterValue("مندوب").contains(agent),
                "TC-FLT-025: يجب الاحتفاظ باختيار المندوب");
        Assert.assertTrue(page.getVisibleInvoiceRows().stream().allMatch(row ->
                        row.stream().anyMatch(cell -> cell.contains(customer))),
                "TC-FLT-025: يجب بقاء نتائج العميل المختار");

        String codeHeader = firstAvailableHeader(page, new String[]{"الكود", "رقم الفاتورة", "رقم الطلب"});
        page.sortInvoiceColumn(codeHeader, true);
        Assert.assertTrue(page.getVisibleInvoiceRows().stream().allMatch(row ->
                        row.stream().anyMatch(cell -> cell.contains(customer))),
                "TC-SRT-003: الفرز يجب ألا يلغي الفلاتر");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("الجمع بين فلتر الكود والتاريخ")
    @Description("يتحقق من أن النتائج تتوافق مع كود الفاتورة ونطاق التاريخ المحدد.")
    @Test(priority = 30)
    public void CombinedCodeAndDateFilterReturnsOnlyMatchingCode() {
        InvoicListPage page = openInvoiceList();
        String code = firstNonBlankCell(page, new String[]{"الكود", "رقم الفاتورة", "رقم الطلب"});
        String date = firstNonBlankCell(page, new String[]{"تاريخ الحركة", "التاريخ", "تاريخ الفاتورة"});
        Assert.assertFalse(code.isBlank(), "TC-FLT-026: يلزم كود مرجعي");
        Assert.assertFalse(date.isBlank(), "TC-FLT-026: يلزم تاريخ مرجعي");
        page.setInvoiceTextFilter("الكود", code);
        page.setInvoiceTextFilter("من تاريخ", normalizeDateForInput(date));
        page.setInvoiceTextFilter("إلى تاريخ", normalizeDateForInput(date));
        page.applyInvoiceFilters();
        Assert.assertTrue(page.areVisibleRowsMatching(code), "TC-FLT-026: النتائج يجب أن تطابق الكود والتاريخ");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("الفلاتر المتعارضة تعرض حالة فارغة")
    @Description("يتحقق من عدم عرض سجلات عند استخدام قيم بحث متعارضة وغير موجودة.")
    @Test(priority = 31)
    public void ConflictingInvoiceFiltersShowNoResults() {
        InvoicListPage page = openInvoiceList();
        page.selectInvoiceFilter("العميل", "عميل عام");
        page.setInvoiceTextFilter("الكود", "QA_CODE_B_" + System.nanoTime());
        page.applyInvoiceFilters();
        Assert.assertTrue(page.getVisibleInvoiceRows().isEmpty() || page.isInvoiceEmptyStateDisplayed(),
                "TC-FLT-027: الفلاتر المتعارضة يجب أن تعرض حالة فارغة");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("الحفاظ على الفلتر والفرز أثناء التنقل")
    @Description("يتحقق من استمرار الفلتر والفرز أثناء التنقل وإعادة الصفحة عند تضييق النتائج.")
    @Test(priority = 32)
    public void PaginationPreservesFilterAndSortAndResetsPageForNewFilter() {
        InvoicListPage page = openInvoiceList();
        String customer = "عميل عام";
        String codeHeader = firstAvailableHeader(page, new String[]{"الكود", "رقم الفاتورة", "رقم الطلب"});
        page.selectInvoiceFilter("العميل", customer);
        page.applyInvoiceFilters();
        page.sortInvoiceColumn(codeHeader, true);
        Assert.assertTrue(page.isNextInvoicePageEnabled(), "TC-PAG-007/008: يلزم وجود صفحتين لبيانات الاختبار");
        page.goToNextInvoicePage();
        Assert.assertTrue(page.getVisibleInvoiceRows().stream().allMatch(row ->
                        row.stream().anyMatch(cell -> cell.contains(customer))),
                "TC-PAG-007: يجب بقاء الفلتر فعالًا بين الصفحات");
        Assert.assertTrue(page.isInvoiceColumnSorted(codeHeader, true),
                "TC-PAG-008: يجب استمرار الفرز عبر الصفحات");
        page.setInvoiceTextFilter("الكود", "QA_NO_MATCH_" + System.nanoTime());
        page.applyInvoiceFilters();
        Assert.assertTrue(page.getCurrentInvoicePageNumber() <= 1,
                "TC-PAG-009: يجب إعادة الصفحة الحالية عند تقليل النتائج");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("تغيير حجم الصفحة من صفحة متقدمة")
    @Description("يتحقق من إمكانية تغيير حجم الصفحة من صفحة متقدمة مع بقاء رقم الصفحة صالحًا.")
    @Test(priority = 33)
    public void SwitchingPageNumberAndPageSizeFromLaterPageWorks() {
        InvoicListPage page = openInvoiceList();
        Assert.assertTrue(page.isNextInvoicePageEnabled(), "TC-SIZE-006: يلزم وجود صفحة متقدمة لبيانات الاختبار");
        page.goToNextInvoicePage();
        page.setInvoicePageSize(50);
        Assert.assertEquals(page.getInvoicePageSize(), 50, "TC-PAG-003/TC-SIZE-006: يجب تطبيق رقم الصفحة وحجمها");
        Assert.assertTrue(page.getCurrentInvoicePageNumber() >= 1,
                "TC-PAG-003/TC-SIZE-006: يجب أن يبقى رقم الصفحة صالحًا");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("حجم الصفحة مع الفلتر والفرز")
    @Description("يتحقق من احترام حجم الصفحة مع مجموعة نتائج صغيرة ووجود فلتر وفرز.")
    @Test(priority = 34)
    public void PageSizeWorksWithFilterAndSortAndSmallResultSet() {
        InvoicListPage page = openInvoiceList();
        String code = firstNonBlankCell(page, new String[]{"الكود", "رقم الفاتورة", "رقم الطلب"});
        String codeHeader = firstAvailableHeader(page, new String[]{"الكود", "رقم الفاتورة", "رقم الطلب"});
        page.setInvoiceTextFilter("الكود", code);
        page.applyInvoiceFilters();
        page.sortInvoiceColumn(codeHeader, true);
        page.setInvoicePageSize(25);
        Assert.assertTrue(page.getVisibleInvoiceRows().size() <= 25,
                "TC-SIZE-005/007: يجب احترام حجم الصفحة مع النتائج القليلة والفرز والفلتر");
        Assert.assertTrue(page.isInvoiceColumnSorted(codeHeader, true), "TC-SIZE-007: يجب بقاء الفرز فعالًا");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("اختلاف سجلات الصفحات والعودة للأولى")
    @Description("يتحقق من اختلاف السجلات بين صفحتين والعودة إلى بيانات الصفحة الأولى.")
    @Test(priority = 35)
    public void AdjacentInvoicePagesHaveDistinctRowsAndReturnToFirst() {
        InvoicListPage page = openInvoiceList();
        String identityHeader = firstAvailableHeader(page, new String[]{"الكود", "رقم الفاتورة", "رقم الطلب"});
        List<String> firstPage = page.getInvoiceRowIdentities(identityHeader);
        Assert.assertTrue(page.isNextInvoicePageEnabled(), "TC-EDGE-005: يلزم وجود صفحتين لبيانات الاختبار");
        page.goToNextInvoicePage();
        List<String> secondPage = page.getInvoiceRowIdentities(identityHeader);
        Set<String> firstPageSet = new HashSet<>(firstPage);
        Assert.assertTrue(secondPage.stream().noneMatch(firstPageSet::contains),
                "TC-EDGE-005: لا يجب تكرار السجلات بين الصفحات");
        page.goToPreviousInvoicePage();
        Assert.assertEquals(page.getInvoiceRowIdentities(identityHeader), firstPage,
                "TC-PAG-002: يجب الرجوع لنفس بيانات الصفحة السابقة");
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("حجم صفحة أكبر من عدد النتائج")
    @Description("يتحقق من عرض مجموعة نتائج صغيرة دون تجاوز حجم الصفحة المحدد.")
    @Test(priority = 36)
    public void InvoiceTablePageSizeCanExceedSmallResultSet() {
        InvoicListPage page = openInvoiceList();
        String code = firstNonBlankCell(page, new String[]{"الكود", "رقم الفاتورة", "رقم الطلب"});
        page.setInvoiceTextFilter("الكود", code);
        page.applyInvoiceFilters();
        page.setInvoicePageSize(500);
        Assert.assertTrue(page.getVisibleInvoiceRows().size() <= 500,
                "TC-SIZE-005: النتائج الأقل من حجم الصفحة تعرض دون تجاوز الحد");
    }

    private String firstNonBlankCell(InvoicListPage page, String[] headerAliases) {
        for (String alias : headerAliases) {
            try {
                return page.getInvoiceColumnValues(alias).stream()
                        .filter(value -> value != null && !value.isBlank())
                        .findFirst().orElse("");
            } catch (IllegalStateException ignored) {
                // Try the next localized column alias.
            }
        }
        return "";
    }

    private String firstAvailableHeader(InvoicListPage page, String[] aliases) {
        List<String> headers = page.getInvoiceTableHeaders();
        return headers.stream().filter(header -> List.of(aliases).stream().anyMatch(header::contains))
                .findFirst().orElseThrow(() -> new AssertionError("لم يتم العثور على عمود من: " + List.of(aliases)));
    }

    private String normalizeDateForInput(String value) {
        value = value.replace("\u200f", "").replace("\u200e", "").trim();
        java.util.regex.Matcher iso = java.util.regex.Pattern.compile("(\\d{4})[-/](\\d{1,2})[-/](\\d{1,2})").matcher(value);
        if (iso.find()) {
            return String.format("%s-%02d-%02d", iso.group(1), Integer.parseInt(iso.group(2)), Integer.parseInt(iso.group(3)));
        }
        java.util.regex.Matcher dayFirst = java.util.regex.Pattern.compile("(\\d{1,2})[-/](\\d{1,2})[-/](\\d{4})").matcher(value);
        if (dayFirst.find()) {
            return String.format("%s-%02d-%02d", dayFirst.group(3), Integer.parseInt(dayFirst.group(2)), Integer.parseInt(dayFirst.group(1)));
        }
        return value;
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("تصدير قائمة الفواتير إلى PDF")
    @Description("يتحقق من تنزيل ملف PDF والتأكد من أن ترويسة الملف تدل على صيغة PDF.")
    @Test(priority = 37)
    public void ExportInvoiceListToPdf() throws IOException {
        InvoicListPage invoiceList = openInvoiceList();
        Path downloadDirectory = prepareExportDirectory();
        Set<String> existingFiles = listFileNames(downloadDirectory);

        invoiceList.exportInvoices("pdf");
        Path downloadedFile = waitForNewExport(downloadDirectory, existingFiles, PDF_EXTENSIONS);

        Assert.assertNotNull(downloadedFile, "لم يتم تنزيل ملف PDF لقائمة الفواتير");
        byte[] header = Files.readAllBytes(downloadedFile);
        Assert.assertTrue(
                header.length > 4 && new String(header, 0, 4).equals("%PDF"),
                "الملف الناتج ليس PDF صالحًا"
        );
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("استعراض الفواتير")
    @Story("تصدير قائمة الفواتير إلى Excel")
    @Description("يتحقق من تنزيل ملف Excel والتأكد من أن الملف الناتج غير فارغ.")
    @Test(priority = 38)
    public void ExportInvoiceListToExcel() throws IOException {
        InvoicListPage invoiceList = openInvoiceList();
        Path downloadDirectory = prepareExportDirectory();
        Set<String> existingFiles = listFileNames(downloadDirectory);

        invoiceList.exportInvoices("excel");
        Path downloadedFile = waitForNewExport(downloadDirectory, existingFiles, EXCEL_EXTENSIONS);

        Assert.assertNotNull(downloadedFile, "لم يتم تنزيل ملف Excel لقائمة الفواتير");
        Assert.assertTrue(Files.size(downloadedFile) > 0, "ملف Excel الناتج فارغ");
    }

    private Path prepareExportDirectory() throws IOException {
        Path directory = Path.of(
                System.getProperty("user.dir"),
                "target",
                "cash-export-downloads"
        ).toAbsolutePath();
        Files.createDirectories(directory);
        return directory;
    }

    private Set<String> listFileNames(Path directory) throws IOException {
        try (var files = Files.list(directory)) {
            Set<String> names = new HashSet<>();
            files.forEach(file -> names.add(file.getFileName().toString()));
            return names;
        }
    }

    private Path waitForNewExport(Path directory, Set<String> existingFiles, Set<String> extensions) {
        return new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
            try (var files = Files.list(directory)) {
                return files
                        .filter(Files::isRegularFile)
                        .filter(file -> !existingFiles.contains(file.getFileName().toString()))
                        .filter(file -> !file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".crdownload"))
                        .filter(file -> isNonEmptyFile(file))
                        .filter(file -> matchesExportType(file, extensions))
                        .findFirst()
                        .orElse(null);
            } catch (IOException e) {
                return null;
            }
        });
    }

    private boolean isNonEmptyFile(Path file) {
        try {
            return Files.size(file) > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean matchesExportType(Path file, Set<String> extensions) {
        try {
            byte[] header = Files.readAllBytes(file);
            if (extensions.contains(".pdf")) return header.length >= 4 && new String(header, 0, 4).equals("%PDF");
            return header.length >= 4 && ((header[0] == 'P' && header[1] == 'K')
                    || (header[0] == (byte) 0xD0 && header[1] == (byte) 0xCF));
        } catch (IOException e) {
            return false;
        }
    }
}
