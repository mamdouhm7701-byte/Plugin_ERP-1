package Purches;

import java.util.*;
import org.testng.Assert;

/** Reusable scenarios; UI operations are implemented by PurchaseModulePage. */
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
        create("picker", "Credit", true);
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
            sameNumber(stored.get("net"), net);
            Assert.assertEquals(stored.get("notes"), marker);
            Assert.assertEquals(stored.get("dealerId"), supplier.id());
            Assert.assertEquals(stored.get("storeId"), data.store(1).id());
            Assert.assertEquals(stored.get("agentId"), agent.id());
            Assert.assertEquals(stored.get("paymentType"), "Credit");
            Assert.assertTrue(
                    documentKind()
                            .type
                            .equalsIgnoreCase(String.valueOf(stored.get("transactionType"))));
            Assert.assertEquals(((List<?>) stored.get("lines")).size(), 1);
            Assert.assertEquals(firstLine(stored).get("itemId"), item.id());
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
        Assert.assertEquals(data.read(saved).get("notes"), note);
    }

    protected void fresh() {
        prepare("Credit");
        fill("picker");
        page.puNew();
        Assert.assertTrue(((List<?>) page.puDocument().get("lines")).isEmpty());
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
        Assert.assertEquals(((List<?>) data.read(saved).get("lines")).size(), 1);
        Assert.assertEquals(firstLine(data.read(saved)).get("itemId"), item.id());
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
        Assert.assertEquals(loaded.get("dealerId"), supplier.id());
        Assert.assertEquals(loaded.get("storeId"), data.store(1).id());
        Assert.assertEquals(firstLine(loaded).get("itemId"), item.id());
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

    protected void linkedReturn() {
        convertedKind = PurchaseDocumentKind.INVOICE;
        var invoice = create("picker", "Credit", false);
        String code = data.read(invoice).get("code").toString();
        page.puConvertOwned(supplier.name(), code, false, item.name());
        convertedKind = PurchaseDocumentKind.RETURN;
        for (int i = 1; i <= 2; i++) initialBalances.put(i, number(data.balance(item, i)));
        sameNumber(firstLine(page.puDocument()).get("quantity"), "3");
        page.puNotes(marker);
        page.puCapture(marker, data.store(1).id(), item.id(), documentKind().endpoint);
        if (!page.puLineReadOnly(item.name(), "الكمية")) {
            page.puQuantity(item.name(), "2");
            var quantity = number(firstLine(page.puDocument()).get("quantity"));
            if (quantity.compareTo(number("3")) != 0) {
                sameNumber(quantity, "2");
                page.puWaitNet("200");
                var before = data.documentIdentities();
                if (!page.puSaveDisabled()) {
                    page.puSave(false);
                    page.puWaitSaveOutcome();
                    page.puSettled();
                    var captured = capture();
                    if (!captured.isEmpty()) {
                        var normalized = data.read(captured.get(captured.size() - 1));
                        io.qameta.allure.Allure.addAttachment(
                                "Linked purchase return quantity evidence",
                                "application/json",
                                new org.json.JSONObject(
                                                PurchaseTestData.fields(
                                                        "requestedQuantity",
                                                        2,
                                                        "originalQuantity",
                                                        3,
                                                        "stored",
                                                        normalized))
                                        .toString(),
                                ".json");
                        sameNumber(firstLine(normalized).get("quantity"), "3");
                        sameNumber(normalized.get("net"), "300");
                        page.puDismiss();
                        assertSource(captured.get(captured.size() - 1), invoice.id());
                        assertStock("3");
                        return;
                    }
                }
                data.verifyNoNewDocuments(before);
                assertStock("0");
                page.puDismiss();
                page.puQuantity(item.name(), "3");
                page.puWaitNet("300");
                page.puCapture(marker, data.store(1).id(), item.id(), documentKind().endpoint);
            }
        }
        var returned = save(false);
        assertSource(returned, invoice.id());
        sameNumber(firstLine(data.read(returned)).get("quantity"), "3");
        assertStock("3");
    }

    protected void convertOrder() {
        convertedKind = PurchaseDocumentKind.ORDER;
        var order = create("picker", "Credit", false);
        String code = data.read(order).get("code").toString();
        page.puConvertOwned(supplier.name(), code, true, item.name());
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
            page.puWaitListIds(Set.of(record.id()));
            var row = page.puListRows().get(0);
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
