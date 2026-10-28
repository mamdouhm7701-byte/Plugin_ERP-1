/* شرح الملف: مراجعة صفحات قائمة الفواتير وحجم الصفحة وترتيبها مع بقاء فلتر عميل التجربة والنتائج المملوكة.
 * فائدته: يجمع تحقق التنقل بين الصفحات دون تغيير أسماء الحالات في حزم التشغيل.
 */
package Invoice;

import java.util.*;
import org.testng.Assert;

final class InvoicePaginationChecks {
    private final InvoiceListTestCases owner;

    InvoicePaginationChecks(InvoiceListTestCases owner) {
        this.owner = owner;
    }

    void PaginationCanOpenAnExplicitPageNumber() {
        owner.paged();
        owner.list().goToInvoicePage(2);
        owner.page.slWaitListIds(owner.remaining);
        Assert.assertEquals(owner.list().getCurrentInvoicePageNumber(), 2);
        owner.list().goToInvoicePage(1);
        owner.page.slWaitListIds(owner.first);
    }

    void SwitchingPageNumberAndPageSizeFromLaterPageWorks() {
        owner.paged();
        owner.list().goToInvoicePage(2);
        owner.page.slWaitListIds(owner.remaining);
        owner.list().setInvoicePageSize(50);
        owner.page.slWaitListIds(owner.all);
        Assert.assertEquals(owner.list().getCurrentInvoicePageNumber(), 1);
    }

    void PreviousIsDisabledOnFirstInvoicePage() {
        var a = owner.seed(1);
        owner.scoped(a);
        Assert.assertEquals(owner.list().getCurrentInvoicePageNumber(), 1);
        Assert.assertFalse(owner.list().isPreviousInvoicePageEnabled());
    }

    void PaginationPreservesFilterAndSortAndResetsPageForNewFilter() {
        owner.paged();
        owner.list().sortInvoiceColumn("الكود", false);
        owner.first = owner.ids();
        owner.remaining = new HashSet<>(owner.all);
        owner.remaining.removeAll(owner.first);
        owner.list().goToNextInvoicePage();
        owner.page.slWaitListIds(owner.remaining);
        Assert.assertTrue(owner.list().isInvoiceColumnSorted("الكود", false));
        Object id = owner.first.iterator().next();
        owner.page.slListText("filteredCode", owner.codeFor(id));
        owner.page.slWaitListIds(Set.of(id));
        Assert.assertEquals(owner.list().getCurrentInvoicePageNumber(), 1);
    }

    void PaginationMovesForwardAndBack() {
        owner.paged();
        owner.list().goToNextInvoicePage();
        owner.page.slWaitListIds(owner.remaining);
        owner.list().goToPreviousInvoicePage();
        owner.page.slWaitListIds(owner.first);
    }

    void LastInvoicePageDisablesNext() {
        owner.paged();
        owner.list().goToLastInvoicePage();
        owner.page.slWaitListIds(owner.remaining);
        Assert.assertFalse(owner.list().isNextInvoicePageEnabled());
    }

    void PageSizeWorksWithFilterAndSortAndSmallResultSet() {
        var a = owner.seed(1);
        var b = owner.seed(2);
        owner.scoped(a, b);
        owner.list().sortInvoiceColumn("الكود", true);
        owner.list().setInvoicePageSize(100);
        owner.page.slWaitListIds(Set.of(a.id(), b.id()));
        Assert.assertTrue(owner.list().isInvoiceColumnSorted("الكود", true));
    }

    void FilterCanBeAppliedFromAdvancedPage() {
        owner.paged();
        owner.list().goToNextInvoicePage();
        owner.page.slWaitListIds(owner.remaining);
        Object id = owner.first.iterator().next();
        String code = owner.codeFor(id);
        owner.page.slListText("filteredCode", code);
        owner.page.slWaitListIds(Set.of(id));
        Assert.assertEquals(owner.list().getCurrentInvoicePageNumber(), 1);
    }
}
