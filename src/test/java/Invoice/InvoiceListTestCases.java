package Invoice;

import io.qameta.allure.Story;

import Plugin_ERP.System_Modules.Login.Invoice.InvoicListPage;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.*;

/** Original entry points retained; assertions are scoped to newly created owned documents. */
public class InvoiceListTestCases extends SalesListActions {
    private InvoicListPage list() {
        return new InvoicListPage(driver);
    }

    private Set<Object> first, remaining, all;

    private void scoped(SalesTestData.Record... records) {
        page.slListScope(data.customer().name());
        Set<Object> expected = new HashSet<>();
        for (var r : records) expected.add(r.id());
        page.slWaitListIds(expected);
    }

    private void paged() {
        all = new HashSet<>();
        for (int i = 0; i < 26; i++) all.add(seed(i).id());
        page.slListScope(data.customer().name());
        list().setInvoicePageSize(25);
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(35))
                .until(
                        d ->
                                page.slListRows().size() == 25
                                        && page.slListRows().stream()
                                                .allMatch(r -> all.contains(r.get("id"))));
        first = ids();
        remaining = new HashSet<>(all);
        remaining.removeAll(first);
        Assert.assertEquals(remaining.size(), 1);
    }

    @Test
    @Story("فتح القائمة المفلترة بمستندات عميل الاختبار")
    public void BrowseInvoiceListShowsDefaultRows() {
        ownCustomerFilterReturnsOnlyOwnDocuments();
    }

    @Test
    @Story("التحقق من أعمدة جدول الفواتير وتطابق عدد خلايا الصفوف معها")
    public void InvoiceListTableShowsColumnsAndAlignedRows() {
        var a = seed(1);
        scoped(a);
        var rows = list().getVisibleInvoiceRows();
        Assert.assertFalse(rows.isEmpty());
        Assert.assertFalse(list().getInvoiceTableHeaders().isEmpty());
        Assert.assertTrue(
                rows.stream().allMatch(r -> r.size() == list().getInvoiceTableHeaders().size()));
    }

    @DataProvider
    public Object[][] notFoundInvoiceTextFilters() {
        return new Object[][] {
            {"رقم الجوال", "TC-FLT-003"},
            {"الملاحظات", "TC-FLT-010"},
            {"الكود", "TC-FLT-012"},
            {"رقم الطلب", "TC-FLT-014"}
        };
    }

    @Test(dataProvider = "notFoundInvoiceTextFilters")
    @Story("فلاتر النص غير المطابقة تعرض قائمة فارغة لكل حقل")
    public void NotFoundInvoiceTextFiltersShowEmptyState(String filter, String testCaseId) {
        var a = seed(1);
        scoped(a);
        String field =
                switch (filter) {
                    case "رقم الجوال" -> "phone";
                    case "الملاحظات" -> "notes";
                    case "رقم الطلب" -> "orderNumber";
                    default -> "filteredCode";
                };
        page.slListText(field, "QA-SL-NO-MATCH-" + UUID.randomUUID());
        page.slWaitListIds(Set.of());
        Assert.assertTrue(page.slListRows().isEmpty(), testCaseId);
    }

    @DataProvider
    public Object[][] existingInvoiceTextFilters() {
        return new Object[][] {{"الكود", new String[] {"الكود", "كود"}, "TC-FLT-011"}};
    }

    @Test
    @Story("البحث بجزء من الكود يعرض السجلات المطابقة فقط")
    public void PartialCodeFilterReturnsOnlyMatchingRows() {
        var a = seed(1);
        var b = seed(2);
        scoped(a, b);
        String code = data.read(a).get("code").toString();
        String part = code.length() > 1 ? code.substring(0, code.length() - 1) : code;
        Set<Object> expected = new HashSet<>();
        for (var r : List.of(a, b))
            if (data.read(r).get("code").toString().contains(part)) expected.add(r.id());
        Assert.assertFalse(expected.isEmpty());
        page.slListText("filteredCode", part);
        page.slWaitListIds(expected);
    }

    @Test
    @Story("مسح الفلاتر يعيد مستندات عميل الاختبار")
    public void ResetInvoiceFiltersReturnsToDefaultResults() {
        clearingCodeRestoresOwnCustomerScope();
    }

    @Test
    @Story("تغيير فلتر مطبق يحدث نتائج القائمة")
    public void ChangingAppliedFilterRefreshesResults() {
        var a = seed(1);
        var b = seed(2);
        scoped(a, b);
        page.slListText("filteredCode", data.read(a).get("code").toString());
        page.slWaitListIds(Set.of(a.id()));
        page.slListText("filteredCode", data.read(b).get("code").toString());
        page.slWaitListIds(Set.of(b.id()));
    }

    @DataProvider
    public Object[][] invoicePageSizes() {
        return pageSizes();
    }

    @Test(dataProvider = "invoicePageSizes")
    @Story("اختيار كل حجم صفحة مدعوم يحافظ على النتائج")
    public void InvoicePageSizeCanBeSelected(int pageSize) {
        pageSizeKeepsOwnedResults(pageSize);
    }

    @Test
    @Story("خيارات حجم الصفحة تساوي خمسة وعشرين وخمسين ومائة وخمسمائة")
    public void InvoicePageSizeOptionsAreExpected() {
        var a = seed(1);
        scoped(a);
        Assert.assertEquals(
                new HashSet<>(list().getInvoicePageSizeOptions()), Set.of(25, 50, 100, 500));
    }

    @Test
    @Story("الانتقال للصفحة التالية ثم السابقة يعيد نفس السجلات")
    public void PaginationMovesForwardAndBack() {
        paged();
        list().goToNextInvoicePage();
        page.slWaitListIds(remaining);
        list().goToPreviousInvoicePage();
        page.slWaitListIds(first);
    }

    @Test
    @Story("فتح صفحة برقمها ثم العودة للصفحة الأولى")
    public void PaginationCanOpenAnExplicitPageNumber() {
        paged();
        list().goToInvoicePage(2);
        page.slWaitListIds(remaining);
        Assert.assertEquals(list().getCurrentInvoicePageNumber(), 2);
        list().goToInvoicePage(1);
        page.slWaitListIds(first);
    }

    @Test
    @Story("تعطيل زر السابق في أول صفحة")
    public void PreviousIsDisabledOnFirstInvoicePage() {
        var a = seed(1);
        scoped(a);
        Assert.assertEquals(list().getCurrentInvoicePageNumber(), 1);
        Assert.assertFalse(list().isPreviousInvoicePageEnabled());
    }

    @Test
    @Story("تعطيل زر التالي في آخر صفحة")
    public void LastInvoicePageDisablesNext() {
        paged();
        list().goToLastInvoicePage();
        page.slWaitListIds(remaining);
        Assert.assertFalse(list().isNextInvoicePageEnabled());
    }

    @Test
    @Story("تطبيق فلتر من صفحة متقدمة يعيد النتائج للصفحة الأولى")
    public void FilterCanBeAppliedFromAdvancedPage() {
        paged();
        list().goToNextInvoicePage();
        page.slWaitListIds(remaining);
        Object id = first.iterator().next();
        String code = codeFor(id);
        page.slListText("filteredCode", code);
        page.slWaitListIds(Set.of(id));
        Assert.assertEquals(list().getCurrentInvoicePageNumber(), 1);
    }

    private String codeFor(Object id) {
        return data.read(
                        new SalesTestData.Record(
                                "ItemTransactions-Sales", id, "QA-SL-reference", Map.of()))
                .get("code")
                .toString();
    }

    @Test
    @Story("مسح فلاتر النص وإعادة فتح القائمة يحافظان على النتائج")
    public void EmptyFiltersAndRefreshKeepInvoiceListHealthy() {
        var a = seed(1);
        var b = seed(2);
        scoped(a, b);
        page.slListText("notes", "");
        page.slListText("filteredCode", "");
        page.slWaitListIds(Set.of(a.id(), b.id()));
        scoped(a, b);
    }

    @Test
    @Story("إعادة تطبيق الفلتر بعد فتح القائمة تعطي نفس النتائج")
    public void RefreshAfterApplyingFilterKeepsAConsistentState() {
        var a = seed(1);
        var b = seed(2);
        scoped(a, b);
        String code = data.read(a).get("code").toString();
        page.slListText("filteredCode", code);
        page.slWaitListIds(Set.of(a.id()));
        scoped(a, b);
        page.slListText("filteredCode", code);
        page.slWaitListIds(Set.of(a.id()));
    }

    @Test
    @Story("عدم تكرار الفواتير بين صفحتين متجاورتين")
    public void InvoiceRowsDoNotRepeatAcrossAdjacentPages() {
        paginationHasNoDuplicatesAndReturnsToFirst();
    }

    @Test
    @Story("ترتيب كود الفاتورة تصاعدياً وتنازلياً")
    public void SortingInvoiceCodeAscendingAndDescending() {
        sortingCodeKeepsOwnedRecords();
    }

    @Test
    @Story("ترتيب تاريخ الفاتورة تصاعدياً وتنازلياً")
    public void SortingByDateColumn() {
        var a = seed(1);
        String other =
                java.time.LocalDate.parse(seedDate)
                        .plusDays(java.time.LocalDate.parse(seedDate).getDayOfMonth() == 1 ? 1 : -1)
                        .toString();
        var b = data.seedInvoice(2, "Sales", other, "1", "0");
        page.slListScope(data.customer().name());
        list().setInvoiceTextFilter(
                        "من تاريخ",
                        java.time.LocalDate.parse(seedDate)
                                        .isBefore(java.time.LocalDate.parse(other))
                                ? seedDate
                                : other);
        list().setInvoiceTextFilter(
                        "الي تاريخ",
                        java.time.LocalDate.parse(seedDate)
                                        .isAfter(java.time.LocalDate.parse(other))
                                ? seedDate
                                : other);
        page.slWaitListIds(Set.of(a.id(), b.id()));
        list().sortInvoiceColumn("التاريخ", true);
        Assert.assertTrue(list().isInvoiceColumnSorted("التاريخ", true));
        list().sortInvoiceColumn("التاريخ", false);
        Assert.assertTrue(list().isInvoiceColumnSorted("التاريخ", false));
        page.slWaitListIds(Set.of(a.id(), b.id()));
    }

    @DataProvider
    public Object[][] invoiceDropdownFilters() {
        return new Object[][] {
            {"مندوب", "مندوب", new String[] {"مندوب", "اسم المندوب"}, "TC-FLT-007"},
            {"العميل", "العميل", new String[] {"المتعامل", "العميل", "اسم العميل"}, "TC-FLT-008"}
        };
    }

    @Test(dataProvider = "invoiceDropdownFilters")
    @Story("اختيار عميل أو مندوب الاختبار من فلاتر القائمة")
    public void InvoiceDropdownFilterCanSelectAnExistingOption(
            String filterLabel, String column, String[] headerAliases, String testCaseId) {
        var a = seed(1);
        scoped(a);
        page.slChoose(
                filterLabel,
                filterLabel.equals("مندوب") ? data.agent().name() : data.customer().name());
        page.slWaitListIds(Set.of(a.id()));
        Assert.assertEquals(page.slListRows().size(), 1, testCaseId);
    }

    @DataProvider
    public Object[][] invoicePaymentStatuses() {
        return new Object[][] {
            {"مدفوع", "TC-FLT-019"}, {"غير مدفوع", "TC-FLT-020"}, {"مدفوع جزئي", "TC-FLT-021"}
        };
    }

    @Test(dataProvider = "invoicePaymentStatuses")
    @Story("فلتر حالة الدفع يعرض المستند المطابق للمبلغ المدفوع")
    public void PaymentStatusFilterShowsOnlySelectedStatus(String status, String testCaseId) {
        var unpaid = seed(1);
        var paid = data.seedInvoice(2, "Sales", seedDate, "1", "100");
        var partial = data.seedInvoice(3, "Sales", seedDate, "1", "40");
        scoped(unpaid, paid, partial);
        String value = status.equals("مدفوع") ? "2" : status.equals("غير مدفوع") ? "0" : "1";
        page.slListRadio("status", value);
        page.slWaitListIds(
                Set.of(
                        value.equals("2")
                                ? paid.id()
                                : value.equals("0") ? unpaid.id() : partial.id()));
        Assert.assertEquals(page.slListRows().size(), 1, testCaseId);
    }

    @Test
    @Story("إعادة حالة الدفع إلى الكل تسترجع جميع الحالات")
    public void PaymentStatusAllRestoresUnrestrictedResults() {
        paymentStatusesMatchOwnedAmounts();
    }

    @Test
    @Story("فلترة نوع الحركة ثم استعادة جميع الأنواع")
    public void InvoiceMovementTypeCanBeFilteredAndResetToAll() {
        documentTypeFilterSeparatesOwnedSalesDocuments();
    }

    @Test
    @Story("قبول نطاق تاريخ صحيح يشمل يوماً واحداً")
    public void ValidAndSingleDayInvoiceDateRangesAreAccepted() {
        singleDayDateRangeKeepsOwnDocuments();
    }

    @Test
    @Story("نطاق تاريخ السنة يعرض فاتورة الاختبار")
    public void BroadValidInvoiceDateRangeKeepsTheInvoiceListAvailable() {
        var a = seed(1);
        scoped(a);
        list().setInvoiceTextFilter("من تاريخ", seedDate.substring(0, 4) + "-01-01");
        list().setInvoiceTextFilter("الي تاريخ", seedDate.substring(0, 4) + "-12-31");
        page.slWaitListIds(Set.of(a.id()));
    }

    @Test
    @Story("نطاق تاريخ معكوس يعرض تحققاً أو قائمة فارغة دون نتائج مضللة")
    public void InvalidInvoiceDateRangeDoesNotReturnMisleadingRows() {
        var a = seed(1);
        scoped(a);
        list().setInvoiceTextFilter(
                        "من تاريخ", java.time.LocalDate.parse(seedDate).plusDays(1).toString());
        list().setInvoiceTextFilter("الي تاريخ", seedDate);
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(35))
                .until(
                        d ->
                                page.slListRows().isEmpty()
                                        || !list().getInvoiceValidationMessages().isEmpty());
    }

    @Test
    @Story("نطاق تاريخ بلا سجلات يمسح الصفوف القديمة")
    public void DateRangeWithoutRecordsShowsNoStaleRows() {
        dateRangeWithoutOwnRecordsClearsStaleRows();
    }

    @Test
    @Story("دمج فلتر الملاحظات والترتيب يحافظ على شرط الفلترة")
    public void CombinedInvoiceFiltersAndSortingKeepBothConditions() {
        var a = seed(1);
        var b = seed(2);
        scoped(a, b);
        page.slListText("notes", a.name());
        page.slWaitListIds(Set.of(a.id()));
        list().sortInvoiceColumn("الكود", false);
        page.slWaitListIds(Set.of(a.id()));
    }

    @Test
    @Story("دمج الكود والتاريخ يعرض الفاتورة المطابقة فقط")
    public void CombinedCodeAndDateFilterReturnsOnlyMatchingCode() {
        var a = seed(1);
        var b = seed(2);
        scoped(a, b);
        list().setInvoiceTextFilter("من تاريخ", seedDate);
        list().setInvoiceTextFilter("الي تاريخ", seedDate);
        page.slListText("filteredCode", data.read(a).get("code").toString());
        page.slWaitListIds(Set.of(a.id()));
    }

    @Test
    @Story("فلاتر متعارضة تعرض قائمة فارغة")
    public void ConflictingInvoiceFiltersShowNoResults() {
        var a = seed(1);
        var b = seed(2);
        scoped(a, b);
        page.slListText("filteredCode", data.read(a).get("code").toString());
        page.slListText("notes", b.name());
        page.slWaitListIds(Set.of());
    }

    @Test
    @Story("التنقل يحافظ على الفلتر والترتيب والفلتر الجديد يعيد الصفحة الأولى")
    public void PaginationPreservesFilterAndSortAndResetsPageForNewFilter() {
        paged();
        list().sortInvoiceColumn("الكود", false);
        first = ids();
        remaining = new HashSet<>(all);
        remaining.removeAll(first);
        list().goToNextInvoicePage();
        page.slWaitListIds(remaining);
        Assert.assertTrue(list().isInvoiceColumnSorted("الكود", false));
        Object id = first.iterator().next();
        page.slListText("filteredCode", codeFor(id));
        page.slWaitListIds(Set.of(id));
        Assert.assertEquals(list().getCurrentInvoicePageNumber(), 1);
    }

    @Test
    @Story("تغيير حجم الصفحة من صفحة متقدمة يعيد الصفحة الأولى")
    public void SwitchingPageNumberAndPageSizeFromLaterPageWorks() {
        paged();
        list().goToInvoicePage(2);
        page.slWaitListIds(remaining);
        list().setInvoicePageSize(50);
        page.slWaitListIds(all);
        Assert.assertEquals(list().getCurrentInvoicePageNumber(), 1);
    }

    @Test
    @Story("حجم صفحة كبير يحافظ على الفلتر والترتيب والنتائج الصغيرة")
    public void PageSizeWorksWithFilterAndSortAndSmallResultSet() {
        var a = seed(1);
        var b = seed(2);
        scoped(a, b);
        list().sortInvoiceColumn("الكود", true);
        list().setInvoicePageSize(100);
        page.slWaitListIds(Set.of(a.id(), b.id()));
        Assert.assertTrue(list().isInvoiceColumnSorted("الكود", true));
    }

    @Test
    @Story("الصفحات المتجاورة بلا تكرار والعودة للأولى تعيد سجلاتها")
    public void AdjacentInvoicePagesHaveDistinctRowsAndReturnToFirst() {
        paginationHasNoDuplicatesAndReturnsToFirst();
    }

    @Test
    @Story("حجم صفحة أكبر من عدد النتائج يعرض جميع سجلات الاختبار")
    public void InvoiceTablePageSizeCanExceedSmallResultSet() {
        pageSizeKeepsOwnedResults(500);
    }

    @Test
    @Story("تصدير فواتير الاختبار إلى ملف PDF فعلي")
    public void ExportInvoiceListToPdf() {
        export("pdf");
    }

    @Test
    @Story("تصدير فواتير الاختبار إلى ملف Excel فعلي")
    public void ExportInvoiceListToExcel() {
        export("excel");
    }
}
