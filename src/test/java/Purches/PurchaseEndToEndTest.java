/* شرح الملف: اختبارات رحلة مشتريات بين شاشات متعددة: تعديل فاتورة شراء ثم مرتجع مرتبط يستخدم الكمية
 * والقيمة المعدلتين؛ شراء بكمية كسرية ثم مرتجع مرتبط يعيد الرصيد دون فقد كسور؛ أمر شراء ثم فاتورة
 * ثم مرتجع كامل يعيد المخزون ورصيد المورد.
 */
package Purches;

import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class PurchaseEndToEndTest extends PurchaseFormActions {
    @Override
    protected PurchaseDocumentKind kind() {
        return PurchaseDocumentKind.INVOICE;
    }

    private PurchaseTestData.Record convert(PurchaseTestData.Record source, boolean toInvoice) {
        page.puConvertOwned(
                supplier.name(),
                data.read(source).get("code").toString(),
                source.id(),
                toInvoice,
                item.name());
        convertedKind = toInvoice ? PurchaseDocumentKind.INVOICE : PurchaseDocumentKind.RETURN;
        for (int i = 1; i <= 2; i++) initialBalances.put(i, number(data.balance(item, i)));
        page.puNotes(marker + "-" + convertedKind);
        page.puCapture(marker, data.store(1).id(), item.id(), documentKind().endpoint);
        var saved = save(false);
        assertSource(saved, source.id());
        return saved;
    }

    @Test
    @Story("E2E أمر شراء ثم فاتورة ثم مرتجع كامل يعيد المخزون ورصيد المورد")
    public void orderThenInvoiceThenReturnRestoresSupplierAndStock() {
        convertedKind = PurchaseDocumentKind.ORDER;
        prepare("Credit");
        var before = data.read(supplier);
        var stock = initialBalances.get(1);
        fill("picker");
        var order = save(false);
        assertStock("3");
        assertSupplierDelta(before, "0");
        var invoice = convert(order, true);
        assertStock("3");
        assertSupplierDelta(before, "-300");
        var returned = convert(invoice, false);
        assertStock("3");
        assertSupplierDelta(before, "0");
        sameNumber(firstLine(data.read(returned)).get("quantity"), "3");
        sameNumber(data.balance(item, 1), stock.toPlainString());
    }

    @Test
    @Story("E2E تعديل فاتورة شراء ثم مرتجع مرتبط يستخدم الكمية والقيمة المعدلتين")
    public void editedInvoiceThenReturnUsesUpdatedQuantityAndValue() {
        prepare("Credit");
        var before = data.read(supplier);
        var stock = initialBalances.get(1);
        fill("picker");
        var invoice = save(false);
        assertStock("3");
        assertSupplierDelta(before, "-300");
        page.puEdit(documentKind().route, invoice.id(), invoice.name(), item.name());
        page.puCapture(marker, data.store(1).id(), item.id(), documentKind().endpoint);
        page.puQuantity(item.name(), "2");
        page.puWaitNet("200");
        var updated = save(false);
        Assert.assertEquals(updated.id(), invoice.id());
        assertStock("2");
        assertSupplierDelta(before, "-200");
        var returned = convert(updated, false);
        assertStock("2");
        assertSupplierDelta(before, "0");
        sameNumber(firstLine(data.read(returned)).get("quantity"), "2");
        TestSupport.DecimalAmountAssertions.amount(data.read(returned).get("net"), "200");
        sameNumber(data.balance(item, 1), stock.toPlainString());
    }

    @Test
    @Story("E2E شراء بكمية كسرية ثم مرتجع مرتبط يعيد الرصيد دون فقد كسور")
    public void fractionalPurchaseThenLinkedReturnRestoresBalances() {
        prepare("Credit");
        var before = data.read(supplier);
        var stock = initialBalances.get(1);
        fill("picker");
        page.puQuantity(item.name(), "1.25");
        page.puWaitNet("125");
        var invoice = save(false);
        assertStock("1.25");
        assertSupplierDelta(before, "-125");
        var returned = convert(invoice, false);
        assertStock("1.25");
        assertSupplierDelta(before, "0");
        sameNumber(firstLine(data.read(returned)).get("quantity"), "1.25");
        sameNumber(data.balance(item, 1), stock.toPlainString());
    }
}
