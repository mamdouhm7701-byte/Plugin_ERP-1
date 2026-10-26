package Invoice;

import io.qameta.allure.Story;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class SalesListCoverageTest extends SalesListActions {
    @DataProvider
    @Override
    public Object[][] pageSizes() {
        return super.pageSizes();
    }

    @Test
    @Story("فلتر العميل يعرض مستندات عميل الاختبار فقط")
    @Override
    public void ownCustomerFilterReturnsOnlyOwnDocuments() {
        super.ownCustomerFilterReturnsOnlyOwnDocuments();
    }

    @Test
    @Story("البحث بالكود الكامل يعرض مستند الاختبار المطابق")
    @Override
    public void exactCodeFilterFindsOwnedDocument() {
        super.exactCodeFilterFindsOwnedDocument();
    }

    @Test
    @Story("البحث بالملاحظات يعرض مستند الاختبار المطابق")
    @Override
    public void notesFilterFindsOwnedDocument() {
        super.notesFilterFindsOwnedDocument();
    }

    @Test
    @Story("البحث بكود غير موجود يعرض قائمة فارغة")
    @Override
    public void unmatchedCodeShowsNoOwnDocuments() {
        super.unmatchedCodeShowsNoOwnDocuments();
    }

    @Test(dataProvider = "pageSizes")
    @Story("تغيير حجم الصفحة يحافظ على نتائج الاختبار لكل حجم مدعوم")
    @Override
    public void pageSizeKeepsOwnedResults(int size) {
        super.pageSizeKeepsOwnedResults(size);
    }

    @Test
    @Story("التنقل بين الصفحات دون تكرار السجلات والعودة للصفحة الأولى")
    @Override
    public void paginationHasNoDuplicatesAndReturnsToFirst() {
        super.paginationHasNoDuplicatesAndReturnsToFirst();
    }

    @Test
    @Story("ترتيب الكود يحافظ على مستندات الاختبار")
    @Override
    public void sortingCodeKeepsOwnedRecords() {
        super.sortingCodeKeepsOwnedRecords();
    }

    @Test
    @Story("مسح فلتر الكود يعيد جميع مستندات عميل الاختبار")
    @Override
    public void clearingCodeRestoresOwnCustomerScope() {
        super.clearingCodeRestoresOwnCustomerScope();
    }

    @Test
    @Story("تصدير النتائج المفلترة إلى ملف Excel فعلي")
    @Override
    public void exportsOwnFilteredListToExcel() {
        super.exportsOwnFilteredListToExcel();
    }

    @Test
    @Story("تصدير النتائج المفلترة إلى ملف PDF فعلي")
    @Override
    public void exportsOwnFilteredListToPdf() {
        super.exportsOwnFilteredListToPdf();
    }

    @Test
    @Story("فلتر المندوب يعرض مستندات مندوب الاختبار فقط")
    @Override
    public void ownedAgentFilterKeepsOnlyOwnDocuments() {
        super.ownedAgentFilterKeepsOnlyOwnDocuments();
    }

    @Test
    @Story("فلاتر حالة الدفع تطابق المستندات المدفوعة والجزئية وغير المدفوعة")
    @Override
    public void paymentStatusesMatchOwnedAmounts() {
        super.paymentStatusesMatchOwnedAmounts();
    }

    @Test
    @Story("فلتر نوع المستند يفصل أنواع مستندات المبيعات")
    @Override
    public void documentTypeFilterSeparatesOwnedSalesDocuments() {
        super.documentTypeFilterSeparatesOwnedSalesDocuments();
    }

    @Test
    @Story("فلتر تاريخ يوم واحد يعرض مستندات الاختبار في هذا اليوم")
    @Override
    public void singleDayDateRangeKeepsOwnDocuments() {
        super.singleDayDateRangeKeepsOwnDocuments();
    }

    @Test
    @Story("نطاق تاريخ بلا مستندات يمسح النتائج السابقة")
    @Override
    public void dateRangeWithoutOwnRecordsClearsStaleRows() {
        super.dateRangeWithoutOwnRecordsClearsStaleRows();
    }
}
