package Invoice;

import java.util.*;
import org.testng.Assert;

/** Shared contract exercised through each concrete sales form, with fresh owned fixtures. */
public abstract class SalesFormActions extends SalesScenarioTest {
    protected void savesOwnedItemFromPicker() {
        create("picker", "Credit", false);
    }

    protected void savesOwnedItemByBarcode() {
        create("barcode", "Credit", false);
    }

    protected void savesOwnedItemByName() {
        create("name", "Credit", false);
    }

    protected void cashDocumentUsesOwnedPayment() {
        var saved = create("picker", "Cash", false);
        if (kind() == SalesDocumentKind.QUOTATION) {
            Assert.assertFalse(page.slHasChoice("نوع السداد"));
            Object payments = data.read(saved).get("payments");
            Assert.assertTrue(payments == null || ((List<?>) payments).isEmpty());
        }
    }

    protected void fractionalQuantityPersists() {
        prepare("Credit");
        add(item, "picker");
        page.slQuantity(item.name(), "1.25");
        page.slWaitNet(salesNet("125"));
        var saved = save(false);
        sameNumber(firstLine(data.read(saved)).get("quantity"), "1.25");
        assertStock("1.25");
    }

    protected void twoOwnedItemsPersist() {
        prepare("Credit");
        var second = ownItem(2);
        var secondBefore = number(data.balance(second, 1));
        add(item, "picker");
        add(second, "picker");
        page.slWaitNet(salesNet("200"));
        var saved = save(false);
        Assert.assertEquals(
                ((List<?>) data.read(saved).get("lines")).size(), 2, "مطابقة بنود الفاتورة");
        assertStock("1");
        sameNumber(
                data.balance(second, 1),
                secondBefore
                        .add(
                                kind() == SalesDocumentKind.INVOICE
                                        ? java.math.BigDecimal.ONE.negate()
                                        : kind() == SalesDocumentKind.RETURN
                                                ? java.math.BigDecimal.ONE
                                                : java.math.BigDecimal.ZERO)
                        .toPlainString());
    }

    protected void removedItemIsNotSaved() {
        prepare("Credit");
        var second = ownItem(2);
        add(item, "picker");
        add(second, "picker");
        page.slRemoveLine(second.name());
        page.slWaitNet(salesNet("100"));
        var saved = save(false);
        Assert.assertEquals(
                ((List<?>) data.read(saved).get("lines")).size(), 1, "مطابقة بنود الفاتورة");
        Assert.assertEquals(firstLine(data.read(saved)).get("itemId"), item.id(), "مطابقة الصنف");
        assertStock("1");
    }

    protected void percentageDiscountRecalculates() {
        data.customer(10);
        prepare("Credit");
        add(item, "picker");
        page.slQuantity(item.name(), "3");
        page.slDiscount("0", true);
        page.slWaitNet(salesNet("300"));
        page.slDiscount("10", true);
        page.slWaitNet(salesNet("270"));
        var saved = save(false);
        sameNumber(data.read(saved).get("net"), salesNet("270"));
        assertStock("3");
    }

    protected void fixedDiscountRecalculates() {
        data.customer(10);
        prepare("Credit");
        add(item, "picker");
        page.slQuantity(item.name(), "3");
        page.slDiscount("0", true);
        page.slWaitNet(salesNet("300"));
        page.slDiscount("15", false);
        page.slWaitNet(salesNet("285"));
        var saved = save(false);
        sameNumber(data.read(saved).get("net"), salesNet("285"));
        assertStock("3");
    }

    protected void ownedPriceListIsApplied() {
        prepare("Credit", true, false);
        fill("picker");
        var saved = save(false);
        sameNumber(data.read(saved).get("net"), salesNet("375"));
        assertStock("3");
    }

    protected void unicodeNotesPersist() {
        prepare("Credit");
        String note = marker + " مبيعات اختبار — Sales 123";
        page.slNotes(note);
        fill("picker");
        var saved = save(false);
        Assert.assertEquals(data.read(saved).get("notes"), note, "مطابقة الملاحظات");
    }

    protected void emptyDocumentCannotSave() {
        prepare("Credit");
        rejectSave();
    }

    protected void zeroQuantityCannotSave() {
        prepare("Credit");
        add(item, "picker");
        page.slQuantity(item.name(), "0");
        rejectSave();
    }

    protected void negativeQuantityCannotSave() {
        prepare("Credit");
        add(item, "picker");
        page.slQuantity(item.name(), "-1");
        rejectSave();
    }

    protected void missingCustomerCannotSave() {
        prepare("Credit");
        if (!page.slCanClearChoice("العميل")) {
            Assert.assertEquals(
                    page.slDocument().get("dealerId"), customer.id(), "مطابقة العميل أو المورد");
            Assert.assertFalse(page.slChoiceEmpty("العميل"));
            Assert.assertTrue(page.slSaves().isEmpty());
            return;
        }
        page.slClearChoice("العميل");
        Assert.assertTrue(page.slChoiceEmpty("العميل"));
        add(item, "picker");
        page.slQuantity(item.name(), "3");
        rejectSave();
    }

    protected void missingStoreCannotSave() {
        prepare("Credit");
        fill("picker");
        if (!page.slCanClearChoice("المخزن")) {
            Assert.assertEquals(
                    page.slDocument().get("storeId"), data.store(1).id(), "مطابقة المخزن");
            Assert.assertFalse(page.slChoiceEmpty("المخزن"));
            Assert.assertTrue(page.slSaves().isEmpty());
            assertStock("0");
            return;
        }
        page.slClearChoice("المخزن");
        Assert.assertTrue(page.slChoiceEmpty("المخزن"));
        rejectSave();
    }

    protected void negativePriceCannotSave() {
        prepare("Credit");
        add(item, "picker");
        page.slSetLine(item.name(), "السعر", "-1");
        rejectSave();
    }

    protected void notesRespectMaximumLength() {
        prepare("Credit");
        page.slNotes(marker + "x".repeat(1100));
        String expected = (marker + "x".repeat(1100)).substring(0, 1000);
        Assert.assertEquals(page.slNotesValue(), expected);
        fill("picker");
        var saved = save(false);
        Assert.assertEquals(data.read(saved).get("notes"), expected, "مطابقة الملاحظات");
    }

    protected void newClearsUnsavedLines() {
        prepare("Credit");
        fill("picker");
        page.slNew();
        Assert.assertTrue(
                ((List<?>) page.slDocument().get("lines")).isEmpty(), "التحقق من بنود الفاتورة");
        Assert.assertTrue(page.slSaves().isEmpty());
        assertStock("0");
    }

    protected void savedDocumentDeletePolicy() {
        var saved = create("picker", "Credit", false);
        if (kind() == SalesDocumentKind.INVOICE) {
            page.slEdit(kind().route, saved.id(), saved.name(), item.name());
            Assert.assertFalse(page.slHasDocumentDeleteAction());
            assertStock("3");
            sameNumber(firstLine(data.read(saved)).get("quantity"), "3");
            return;
        }
        var before = data.read(customer);
        data.deleteOwned(saved);
        assertStock("0");
        assertCustomerDelta(
                before,
                kind() == SalesDocumentKind.INVOICE
                        ? "-300"
                        : kind() == SalesDocumentKind.RETURN ? "300" : "0");
    }

    protected void savedDocumentReopensWithOwnedValues() {
        var saved = create("picker", "Credit", false);
        page.slEdit(kind().route, saved.id(), saved.name(), item.name());
        var loaded = page.slDocument();
        Assert.assertEquals(loaded.get("dealerId"), customer.id(), "مطابقة العميل أو المورد");
        Assert.assertEquals(loaded.get("storeId"), data.store(1).id(), "مطابقة المخزن");
        Assert.assertEquals(firstLine(loaded).get("itemId"), item.id(), "مطابقة الصنف");
        sameNumber(firstLine(loaded).get("quantity"), "3");
    }

    protected void savedDocumentEditPolicy() {
        var saved = create("picker", "Credit", false);
        var before = data.read(customer);
        page.slEdit(kind().route, saved.id(), saved.name(), item.name());
        if (kind() == SalesDocumentKind.INVOICE) {
            Assert.assertTrue(page.slLineReadOnly(item.name(), "الكمية"));
            Assert.assertTrue(page.slLineReadOnly(item.name(), "السعر"));
            Assert.assertFalse(page.slHasDocumentSaveAction());
            sameNumber(firstLine(data.read(saved)).get("quantity"), "3");
            return;
        }
        page.slCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
        page.slQuantity(item.name(), "2");
        page.slWaitNet(salesNet("200"));
        var edited = save(false);
        Assert.assertEquals(edited.id(), saved.id());
        sameNumber(firstLine(data.read(edited)).get("quantity"), "2");
        assertStock("2");
        assertCustomerDelta(
                before,
                kind() == SalesDocumentKind.INVOICE
                        ? "-100"
                        : kind() == SalesDocumentKind.RETURN ? "100" : "0");
    }
}
