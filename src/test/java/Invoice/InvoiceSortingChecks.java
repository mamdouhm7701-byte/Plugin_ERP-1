/* شرح الملف: مراجعة ترتيب فواتير عميل التجربة والتواريخ غير الصحيحة دون فقد نطاق بياناته. */
package Invoice;

import java.util.*;
import org.testng.Assert;

final class InvoiceSortingChecks {
    private final InvoiceListTestCases owner;

    InvoiceSortingChecks(InvoiceListTestCases owner) {
        this.owner = owner;
    }

    void SortingByDateColumn() {
        var a = owner.seed(1);
        String other =
                java.time.LocalDate.parse(owner.seedDate)
                        .plusDays(
                                java.time.LocalDate.parse(owner.seedDate).getDayOfMonth() == 1
                                        ? 1
                                        : -1)
                        .toString();
        var b = owner.data.seedInvoice(2, "Sales", other, "1", "0");
        owner.page.slListScope(owner.data.customer().name());
        owner.list()
                .setInvoiceTextFilter(
                        "من تاريخ",
                        java.time.LocalDate.parse(owner.seedDate)
                                        .isBefore(java.time.LocalDate.parse(other))
                                ? owner.seedDate
                                : other);
        owner.list()
                .setInvoiceTextFilter(
                        "الي تاريخ",
                        java.time.LocalDate.parse(owner.seedDate)
                                        .isAfter(java.time.LocalDate.parse(other))
                                ? owner.seedDate
                                : other);
        owner.page.slWaitListIds(Set.of(a.id(), b.id()));
        owner.list().sortInvoiceColumn("التاريخ", true);
        Assert.assertTrue(owner.list().isInvoiceColumnSorted("التاريخ", true));
        owner.list().sortInvoiceColumn("التاريخ", false);
        Assert.assertTrue(owner.list().isInvoiceColumnSorted("التاريخ", false));
        owner.page.slWaitListIds(Set.of(a.id(), b.id()));
    }

    void InvalidInvoiceDateRangeDoesNotReturnMisleadingRows() {
        var a = owner.seed(1);
        owner.scoped(a);
        owner.list()
                .setInvoiceTextFilter(
                        "من تاريخ",
                        java.time.LocalDate.parse(owner.seedDate).plusDays(1).toString());
        owner.list().setInvoiceTextFilter("الي تاريخ", owner.seedDate);
        new org.openqa.selenium.support.ui.WebDriverWait(
                        owner.caseDriver(), java.time.Duration.ofSeconds(35))
                .until(
                        d ->
                                owner.page.slListRows().isEmpty()
                                        || !owner.list().getInvoiceValidationMessages().isEmpty());
    }

    void SortingInvoiceCodeAscendingAndDescending() {
        owner.sortingCodeKeepsOwnedRecords();
    }
}
