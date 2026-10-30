/* شرح الملف: تجميع خطوات إدخال وحفظ ومراجعة المستند، ضمن المشتريات.
 */
package Purches;

import java.util.*;
import org.testng.Assert;

public abstract class PurchaseFormActions extends PurchaseScenarioTest {
    protected void picker() {
        create("picker", "Credit", false);
    }

    protected void barcode() {
        create("barcode", "Credit", false);
    }

    protected void name() {
        create("name", "Credit", false);
    }

    protected void cash() {
        create("picker", "Cash", false);
    }

    protected void print() {
        prepare("Credit");
        var second = ownItem(2);
        add(item, "picker");
        add(second, "picker");
        page.puQuantity(item.name(), "2");
        page.puQuantity(second.name(), "3");
        String column = documentKind() == PurchaseDocumentKind.INVOICE ? "سعر الشراء" : "السعر";
        page.puSetLine(item.name(), column, "100");
        page.puSetLine(second.name(), column, "73.25");
        page.puWaitNet("419.75");
        printContentAssertions =
                stored -> {
                    TestSupport.DecimalAmountAssertions.amount(stored.get("net"), "419.75");
                    Plugin_ERP.Util.PrintedInvoiceAssertions.invoice(
                            driver,
                            stored.get("code").toString(),
                            supplier.name(),
                            java.util.List.of(
                                    new Plugin_ERP.Util.PrintedInvoiceAssertions.Line(
                                            item.name(),
                                            new java.math.BigDecimal("2"),
                                            new java.math.BigDecimal("100")),
                                    new Plugin_ERP.Util.PrintedInvoiceAssertions.Line(
                                            second.name(),
                                            new java.math.BigDecimal("3"),
                                            new java.math.BigDecimal("73.25"))),
                            new java.math.BigDecimal("419.75"));
                };
        save(true);
    }

    protected void empty() {
        prepare("Credit");
        rejectSave();
    }

    protected void invalidQuantity(String quantity) {
        prepare("Credit");
        fill("picker");
        page.puQuantity(item.name(), quantity);
        rejectSave();
    }

    protected void invalidPrice() {
        prepare("Credit");
        fill("picker");
        page.puSetLine(
                item.name(),
                documentKind() == PurchaseDocumentKind.INVOICE ? "سعر الشراء" : "السعر",
                "-1");
        rejectSave();
    }

    protected void missing(String label) {
        prepare("Credit");
        fill("picker");
        if (page.puCanClearChoice(label)) {
            page.puClearChoice(label);
            rejectSave();
        } else {
            Assert.assertFalse(page.puChoiceEmpty(label));
            Assert.assertTrue(page.puSaves().isEmpty());
            assertStock("0");
        }
    }

    protected void fractional() {
        prepare("Credit");
        fill("picker");
        page.puQuantity(item.name(), "1.25");
        page.puWaitNet("125");
        var saved = save(false);
        sameNumber(firstLine(data.read(saved)).get("quantity"), "1.25");
        assertStock("1.25");
    }

    protected void discount(boolean percent) {
        prepare("Credit");
        var balanceBefore = data.read(supplier);
        fill("picker");
        page.puDiscount(percent ? "10" : "15", percent);
        String net = percent ? "270" : "285";
        page.puWaitNet(net);
        var allowed = page.puUserDiscountLimit();
        var requested = number(percent ? "10" : "5");
        var before = data.documentIdentities();
        io.qameta.allure.Allure.addAttachment(
                "Purchase discount permission",
                "application/json",
                new org.json.JSONObject(
                                PurchaseTestData.fields(
                                        "limitExposed",
                                        allowed != null,
                                        "allowedPercentage",
                                        allowed,
                                        "requestedPercentage",
                                        requested,
                                        "screenNet",
                                        net))
                        .toString(),
                ".json");
        page.puSave(false);
        page.puWaitSaveOutcome();
        page.puSettled();
        var captured = capture();
        if (captured.isEmpty()) {
            Assert.assertTrue(
                    page.puSaveErrors()
                            .contains(
                                    "DiscountPercentageGreaterThanAllowedDiscountPercentageForThatUser"),
                    "Discount must either persist or be explicitly rejected by the user's discount policy: "
                            + page.puSaveErrors());
            Assert.assertTrue(
                    allowed == null || allowed.compareTo(requested) < 0,
                    "Backend rejected a discount permitted by the exposed user limit");
            data.verifyNoNewDocuments(before);
            assertStock("0");
            assertSupplierDelta(balanceBefore, "0");
        } else {
            Assert.assertTrue(
                    allowed == null || allowed.compareTo(requested) >= 0,
                    "Backend saved a discount beyond the exposed user limit");
            var stored = data.read(captured.get(captured.size() - 1));
            Assert.assertNotNull(stored);
            io.qameta.allure.Allure.addAttachment(
                    "Saved discounted purchase",
                    "application/json",
                    new org.json.JSONObject(stored).toString(),
                    ".json");
            TestSupport.DecimalAmountAssertions.amount(stored.get("net"), net);
            Assert.assertEquals(stored.get("notes"), marker, "مطابقة الملاحظات");
            Assert.assertEquals(stored.get("dealerId"), supplier.id(), "مطابقة العميل أو المورد");
            Assert.assertEquals(stored.get("storeId"), data.store(1).id(), "مطابقة المخزن");
            Assert.assertEquals(stored.get("agentId"), agent.id(), "مطابقة المندوب");
            Assert.assertEquals(stored.get("paymentType"), "Credit", "مطابقة نوع السداد");
            Assert.assertTrue(
                    documentKind()
                            .type
                            .equalsIgnoreCase(String.valueOf(stored.get("transactionType"))),
                    "التحقق من نوع الحركة");
            Assert.assertEquals(((List<?>) stored.get("lines")).size(), 1, "مطابقة بنود الفاتورة");
            Assert.assertEquals(firstLine(stored).get("itemId"), item.id(), "مطابقة الصنف");
            sameNumber(firstLine(stored).get("quantity"), "3");
            assertStock("3");
            assertSupplierDelta(
                    balanceBefore,
                    documentKind() == PurchaseDocumentKind.INVOICE ? "-" + net : net);
        }
        page.puDismiss();
    }

    protected void notes() {
        prepare("Credit");
        String note = marker + " ملاحظات مشتريات — Purchase 123";
        page.puNotes(note);
        fill("picker");
        var saved = save(false);
        Assert.assertEquals(data.read(saved).get("notes"), note, "مطابقة الملاحظات");
    }

    protected void fresh() {
        prepare("Credit");
        fill("picker");
        page.puNew();
        Assert.assertTrue(
                ((List<?>) page.puDocument().get("lines")).isEmpty(), "التحقق من بنود الفاتورة");
        Assert.assertTrue(page.puSaves().isEmpty());
        assertStock("0");
    }

    protected void remove() {
        prepare("Credit");
        fill("picker");
        var second = ownItem(2);
        add(second, "picker");
        page.puRemoveLine(second.name());
        page.puWaitNet("300");
        var saved = save(false);
        Assert.assertEquals(
                ((List<?>) data.read(saved).get("lines")).size(), 1, "مطابقة بنود الفاتورة");
        Assert.assertEquals(firstLine(data.read(saved)).get("itemId"), item.id(), "مطابقة الصنف");
        assertStock("3");
    }

    protected void multiple() {
        prepare("Credit");
        fill("picker");
        var second = ownItem(2);
        var before = number(data.balance(second, 1));
        add(second, "picker");
        page.puSetLine(
                second.name(),
                documentKind() == PurchaseDocumentKind.INVOICE ? "سعر الشراء" : "السعر",
                "100");
        page.puWaitNet("400");
        var saved = save(false);
        var lines = (List<?>) data.read(saved).get("lines");
        Assert.assertEquals(lines.size(), 2);
        assertStock("3");
        sameNumber(
                data.balance(second, 1),
                before.add(
                                documentKind() == PurchaseDocumentKind.INVOICE
                                        ? number("1")
                                        : documentKind() == PurchaseDocumentKind.RETURN
                                                ? number("-1")
                                                : number("0"))
                        .toPlainString());
    }

    protected void reopen() {
        var saved = create("picker", "Credit", false);
        page.puEdit(documentKind().route, saved.id(), marker, item.name());
        var loaded = page.puDocument();
        Assert.assertEquals(loaded.get("dealerId"), supplier.id(), "مطابقة العميل أو المورد");
        Assert.assertEquals(loaded.get("storeId"), data.store(1).id(), "مطابقة المخزن");
        Assert.assertEquals(firstLine(loaded).get("itemId"), item.id(), "مطابقة الصنف");
        sameNumber(firstLine(loaded).get("quantity"), "3");
    }

    protected void edit() {
        var saved = create("picker", "Credit", false);
        page.puEdit(documentKind().route, saved.id(), marker, item.name());
        page.puCapture(marker, data.store(1).id(), item.id(), documentKind().endpoint);
        page.puQuantity(item.name(), "2");
        page.puWaitNet("200");
        var updated = save(false);
        Assert.assertEquals(updated.id(), saved.id());
        sameNumber(firstLine(data.read(updated)).get("quantity"), "2");
        assertStock("2");
    }

    protected void delete() {
        var saved = create("picker", "Credit", false);
        page.puDeleteOwned(supplier.name(), data.read(saved).get("code").toString());
        Assert.assertNull(data.read(saved));
        assertStock("0");
    }

    protected PurchaseTestData.Record prepareLinkedReturn() {
        convertedKind = PurchaseDocumentKind.INVOICE;
        var invoice = create("picker", "Credit", false);
        String code = data.read(invoice).get("code").toString();
        page.puConvertOwned(supplier.name(), code, invoice.id(), false, item.name());
        convertedKind = PurchaseDocumentKind.RETURN;
        for (int i = 1; i <= 2; i++) initialBalances.put(i, number(data.balance(item, i)));
        sameNumber(firstLine(page.puDocument()).get("quantity"), "3");
        page.puNotes(marker);
        page.puCapture(marker, data.store(1).id(), item.id(), documentKind().endpoint);
        return invoice;
    }

    protected void linkedReturn() {
        var invoice = prepareLinkedReturn();
        var supplierBefore = data.read(supplier);
        page.puQuantity(item.name(), "2");
        page.puWaitNet("200");
        var returned = save(false);
        assertSource(returned, invoice.id());
        sameNumber(firstLine(data.read(returned)).get("quantity"), "2");
        TestSupport.DecimalAmountAssertions.amount(data.read(returned).get("net"), "200");
        assertStock("2");
        assertSupplierDelta(supplierBefore, "200");
        sameNumber(firstLine(data.read(invoice)).get("quantity"), "3");
        TestSupport.DecimalAmountAssertions.amount(data.read(invoice).get("net"), "300");
    }

    protected void convertOrder() {
        convertedKind = PurchaseDocumentKind.ORDER;
        var order = create("picker", "Credit", false);
        String code = data.read(order).get("code").toString();
        page.puConvertOwned(supplier.name(), code, order.id(), true, item.name());
        convertedKind = PurchaseDocumentKind.INVOICE;
        page.puNotes(marker);
        page.puCapture(marker, data.store(1).id(), item.id(), documentKind().endpoint);
        var invoice = save(false);
        assertSource(invoice, order.id());
        assertStock("3");
    }

    protected void assertSource(PurchaseTestData.Record record, Object expected) {
        var stored = data.read(record);
        Object actual = stored.get("sourceTransactionId");
        if (!stored.containsKey("sourceTransactionId")) {
            page.puListScope(supplier.name());
            page.puListText("filteredCode", stored.get("code").toString());
            // Codes are shared by different document kinds; a linked return and its invoice
            // can both match the code filter. Assert identity rather than assuming one row.
            new org.openqa.selenium.support.ui.WebDriverWait(
                            driver, java.time.Duration.ofSeconds(35))
                    .until(
                            d ->
                                    page.puListRows().stream()
                                            .anyMatch(r -> record.id().equals(r.get("id"))));
            var row =
                    page.puListRows().stream()
                            .filter(r -> record.id().equals(r.get("id")))
                            .findFirst()
                            .orElseThrow();
            actual = row.get("sourceTransactionId");
            io.qameta.allure.Allure.addAttachment(
                    "Purchase source relationship from list",
                    "application/json",
                    new org.json.JSONObject(row).toString(),
                    ".json");
        }
        Assert.assertEquals(actual, expected, "Saved purchase must retain its source relationship");
    }

    @SuppressWarnings("unchecked")
    protected static Map<String, Object> firstLine(Map<String, Object> doc) {
        return ((List<Map<String, Object>>) doc.get("lines")).get(0);
    }
}
