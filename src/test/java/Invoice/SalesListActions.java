package Invoice;

import Plugin_ERP.System_Modules.Login.Invoice.InvoicListPage;
import java.util.*;
import org.testng.Assert;

/** Every list expectation is scoped to documents created by this scenario. */
public abstract class SalesListActions extends SalesScenarioTest {
    protected String seedDate;

    protected void ownCustomerFilterReturnsOnlyOwnDocuments() {
        var a = seed(1);
        var b = seed(2);
        page.slListScope(data.customer().name());
        page.slWaitListIds(Set.of(a.id(), b.id()));
        Assert.assertEquals(page.slListRows().size(), 2);
    }

    protected void exactCodeFilterFindsOwnedDocument() {
        var a = seed(1);
        seed(2);
        var stored = data.read(a);
        page.slListScope(data.customer().name());
        page.slListText("filteredCode", stored.get("code").toString());
        page.slWaitListIds(Set.of(a.id()));
    }

    protected void notesFilterFindsOwnedDocument() {
        var a = seed(1);
        seed(2);
        page.slListScope(data.customer().name());
        page.slListText("notes", a.name());
        page.slWaitListIds(Set.of(a.id()));
    }

    protected void unmatchedCodeShowsNoOwnDocuments() {
        var a = seed(1);
        page.slListScope(data.customer().name());
        page.slWaitListIds(Set.of(a.id()));
        page.slListText("filteredCode", "QA-SL-NO-MATCH-" + UUID.randomUUID());
        page.slWaitListIds(Set.of());
    }

    public Object[][] pageSizes() {
        return new Object[][] {{25}, {50}, {100}, {500}};
    }

    protected void pageSizeKeepsOwnedResults(int size) {
        var a = seed(1);
        var b = seed(2);
        page.slListScope(data.customer().name());
        page.slWaitListIds(Set.of(a.id(), b.id()));
        var list = new InvoicListPage(driver);
        list.setInvoicePageSize(size);
        page.slWaitListIds(Set.of(a.id(), b.id()));
        Assert.assertEquals(list.getInvoicePageSize(), size);
    }

    protected void paginationHasNoDuplicatesAndReturnsToFirst() {
        Set<Object> expected = new HashSet<>();
        for (int i = 0; i < 26; i++) expected.add(seed(i).id());
        page.slListScope(data.customer().name());
        var list = new InvoicListPage(driver);
        list.setInvoicePageSize(25);
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(35))
                .until(
                        d ->
                                page.slListRows().size() == 25
                                        && page.slListRows().stream()
                                                .allMatch(r -> expected.contains(r.get("id"))));
        Set<Object> first = ids();
        Assert.assertFalse(list.isPreviousInvoicePageEnabled());
        list.goToNextInvoicePage();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(35))
                .until(d -> page.slListRows().size() == 1);
        Set<Object> second = ids();
        Assert.assertTrue(Collections.disjoint(first, second));
        var union = new HashSet<>(first);
        union.addAll(second);
        Assert.assertEquals(union, expected);
        Assert.assertFalse(list.isNextInvoicePageEnabled());
        list.goToFirstInvoicePage();
        page.slWaitListIds(first);
    }

    protected void sortingCodeKeepsOwnedRecords() {
        var a = seed(1);
        var b = seed(2);
        page.slListScope(data.customer().name());
        page.slWaitListIds(Set.of(a.id(), b.id()));
        var list = new InvoicListPage(driver);
        list.sortInvoiceColumn("الكود", true);
        Assert.assertTrue(list.isInvoiceColumnSorted("الكود", true));
        page.slWaitListIds(Set.of(a.id(), b.id()));
        list.sortInvoiceColumn("الكود", false);
        Assert.assertTrue(list.isInvoiceColumnSorted("الكود", false));
        page.slWaitListIds(Set.of(a.id(), b.id()));
    }

    protected void clearingCodeRestoresOwnCustomerScope() {
        var a = seed(1);
        var b = seed(2);
        page.slListScope(data.customer().name());
        page.slWaitListIds(Set.of(a.id(), b.id()));
        page.slListText("filteredCode", data.read(a).get("code").toString());
        page.slWaitListIds(Set.of(a.id()));
        page.slListText("filteredCode", "");
        page.slWaitListIds(Set.of(a.id(), b.id()));
    }

    protected void exportsOwnFilteredListToExcel() {
        export("excel");
    }

    protected void exportsOwnFilteredListToPdf() {
        export("pdf");
    }

    protected void ownedAgentFilterKeepsOnlyOwnDocuments() {
        var a = seed(1);
        var b = data.seedInvoice(2, "Sales", seedDate, "1", "0", data.otherSalesAgent());
        page.slListScope(data.customer().name());
        page.slWaitListIds(Set.of(a.id(), b.id()));
        page.slChoose("مندوب", data.agent().name());
        page.slWaitListIds(Set.of(a.id()));
    }

    protected void paymentStatusesMatchOwnedAmounts() {
        var unpaid = seed(1);
        String fullAmount = data.read(unpaid).get("net").toString();
        var paid = data.seedInvoice(2, "Sales", seedDate, "1", fullAmount);
        var partial = data.seedInvoice(3, "Sales", seedDate, "1", "40");
        var unpaidStored = data.read(unpaid);
        var paidStored = data.read(paid);
        var partialStored = data.read(partial);
        io.qameta.allure.Allure.addAttachment(
                "Owned payment status fixtures",
                "application/json",
                new org.json.JSONObject(
                                SalesTestData.fields(
                                        "unpaid",
                                        unpaidStored,
                                        "paid",
                                        paidStored,
                                        "partial",
                                        partialStored))
                        .toString(),
                ".json");
        sameNumber(unpaidStored.get("paid"), "0");
        sameNumber(paidStored.get("paid"), paidStored.get("net").toString());
        Assert.assertTrue(
                number(partialStored.get("paid")).signum() > 0
                        && number(partialStored.get("paid"))
                                        .compareTo(number(partialStored.get("net")))
                                < 0,
                "Partial-payment fixture must actually be partially paid");
        page.slListScope(data.customer().name());
        var all = Set.of(unpaid.id(), paid.id(), partial.id());
        page.slWaitListIds(all);
        page.slListRadio("status", "2");
        page.slWaitListIds(Set.of(paid.id()));
        page.slListRadio("status", "0");
        page.slWaitListIds(Set.of(unpaid.id()));
        page.slListRadio("status", "1");
        page.slWaitListIds(Set.of(partial.id()));
        page.slListRadio("status", "");
        page.slWaitListIds(all);
    }

    protected void documentTypeFilterSeparatesOwnedSalesDocuments() {
        var invoice = seed(1);
        var order = data.seedInvoice(2, "SalesOrder", seedDate, "1", "0");
        page.slListScope(data.customer().name());
        var all = Set.of(invoice.id(), order.id());
        page.slWaitListIds(all);
        page.slListRadio("type", "salesOrder");
        page.slWaitListIds(Set.of(order.id()));
        page.slListRadio("type", "sales");
        page.slWaitListIds(Set.of(invoice.id()));
        page.slListRadio("type", "");
        page.slWaitListIds(all);
    }

    protected void singleDayDateRangeKeepsOwnDocuments() {
        var a = seed(1);
        page.slListScope(data.customer().name());
        page.slWaitListIds(Set.of(a.id()));
        var list = new InvoicListPage(driver);
        list.setInvoiceTextFilter("من تاريخ", seedDate);
        list.setInvoiceTextFilter("الي تاريخ", seedDate);
        page.slWaitListIds(Set.of(a.id()));
    }

    protected void dateRangeWithoutOwnRecordsClearsStaleRows() {
        var a = seed(1);
        page.slListScope(data.customer().name());
        page.slWaitListIds(Set.of(a.id()));
        String previous = java.time.LocalDate.parse(seedDate).minusDays(1).toString();
        var list = new InvoicListPage(driver);
        list.setInvoiceTextFilter("من تاريخ", previous);
        list.setInvoiceTextFilter("الي تاريخ", previous);
        page.slWaitListIds(Set.of());
    }

    protected void export(String format) {
        var a = seed(1);
        page.slListScope(data.customer().name());
        page.slWaitListIds(Set.of(a.id()));
        SalesExportAssertions.assertExportDownloaded(driver, () -> page.slExport(format), format);
    }

    protected Set<Object> ids() {
        Set<Object> ids = new HashSet<>();
        for (var row : page.slListRows())
            Assert.assertTrue(ids.add(row.get("id")), "Repeated document on the same page");
        return ids;
    }

    protected SalesTestData.Record seed(int index) {
        if (seedDate == null) seedDate = page.slListFixtureDate();
        return data.seedInvoice(index, "Sales", seedDate, "1", "0");
    }
}
