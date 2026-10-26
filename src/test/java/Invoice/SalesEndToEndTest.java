package Invoice;

import Plugin_ERP.System_Modules.Login.Invoice.SalesReturnPage;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * E2E: فاتورة من الواجهة ثم مرتجع مرتبط بنفس الفاتورة والعميل والصنف. فائدته كشف أخطاء الربط
 * والأرصدة بين الشاشات التي لا يكشفها اختبار كل شاشة منفردة. API يجهز المراجع ويقرأ النتيجة
 * وينظفها؛ إنشاء مستندي العمل وحفظهما يتم من الواجهة.
 */
public final class SalesEndToEndTest extends SalesScenarioTest {
    private SalesDocumentKind currentKind = SalesDocumentKind.INVOICE;

    @Override
    protected SalesDocumentKind kind() {
        return currentKind;
    }

    @Override
    @BeforeMethod(alwaysRun = true)
    public void beginSalesScenario() {
        currentKind = SalesDocumentKind.INVOICE;
        super.beginSalesScenario();
    }

    @Test
    @Story("E2E إنشاء فاتورة بيع ثم مرتجع كامل مرتبط يعيد المخزون ورصيد العميل")
    @Description(
            "يحفظ فاتورة آجلة بكمية 3 من الواجهة، ثم يحفظ مرتجعاً مرتبطاً لنفس الفاتورة من"
                    + " الواجهة، ويتحقق من هوية المصدر والطرف والصنف وصافي 300 وعودة المخزون ومديونية"
                    + " العميل. التنظيف بعد مجموعة الرحلة يحذف المرتجع قبل الفاتورة.")
    public void invoiceThenLinkedReturnRestoresStockAndCustomerBalance() {
        prepare("Credit");
        var customerBeforeSale = data.read(customer);
        var stockBeforeSale = initialBalances.get(1);
        fill("picker");
        var invoice = save(false);
        assertStock("3");
        assertCustomerDelta(customerBeforeSale, "300");

        // نحتفظ بنفس بيانات الحالة؛ لا ننشئ عميلاً أو صنفاً بديلاً عند الانتقال للمرتجع.
        currentKind = SalesDocumentKind.RETURN;
        page = new SalesReturnPage(driver);
        initialBalances.put(1, number(data.balance(item, 1)));
        page.slLinkedReturn(invoice.id(), item.name());
        page.slCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
        page.slNotes(marker + "-RETURN");
        Assert.assertEquals(page.slDocument().get("sourceTransactionId"), invoice.id());
        Assert.assertEquals(
                page.slDocument().get("dealerId"), customer.id(), "مطابقة العميل أو المورد");
        Assert.assertEquals(page.slDocument().get("agentId"), agent.id(), "مطابقة المندوب");
        page.slQuantity(item.name(), "3");
        page.slWaitNet(salesNet("300"));

        var returned = save(false);
        var storedReturn = data.read(returned);
        Assert.assertEquals(storedReturn.get("sourceTransactionId"), invoice.id());
        Assert.assertEquals(firstLine(storedReturn).get("itemId"), item.id(), "مطابقة الصنف");
        sameNumber(firstLine(storedReturn).get("quantity"), "3");
        sameNumber(storedReturn.get("net"), salesNet("300"));
        assertStock("3");
        awaitOwnedStock(stockBeforeSale, "Full linked return restores the pre-sale stock");
        assertCustomerDelta(customerBeforeSale, "0");
    }

    private SalesTestData.Record returnPart(
            SalesTestData.Record invoice, String quantity, String net) {
        currentKind = SalesDocumentKind.RETURN;
        page = new SalesReturnPage(driver);
        initialBalances.put(1, number(data.balance(item, 1)));
        page.slLinkedReturn(invoice.id(), item.name());
        page.slCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
        page.slNotes(marker + "-RETURN-" + java.util.UUID.randomUUID().toString().substring(0, 6));
        Assert.assertEquals(page.slDocument().get("sourceTransactionId"), invoice.id());
        page.slQuantity(item.name(), quantity);
        page.slWaitNet(salesNet(net));
        var returned = save(false);
        Assert.assertEquals(data.read(returned).get("sourceTransactionId"), invoice.id());
        sameNumber(firstLine(data.read(returned)).get("quantity"), quantity);
        assertStock(quantity);
        return returned;
    }

    @Test
    @Story("E2E بيع ثم مرتجع جزئي ثم باقي الكمية يعيدان الرصيد بالكامل")
    public void saleThenTwoPartialReturnsRestoreOriginalBalances() {
        prepare("Credit");
        var before = data.read(customer);
        var stock = initialBalances.get(1);
        fill("picker");
        var invoice = save(false);
        assertStock("3");
        assertCustomerDelta(before, "300");
        returnPart(invoice, "1", "100");
        assertCustomerDelta(before, "200");
        returnPart(invoice, "2", "200");
        assertCustomerDelta(before, "0");
        awaitOwnedStock(stock, "Two partial returns must restore the initial stock");
    }

    @Test
    @Story("E2E بيع ثم مرتجع جزئي ثم رفض تجاوز المتبقي مع ثبات الأرصدة")
    public void partialReturnThenOverReturnIsRejectedWithoutSideEffects() {
        prepare("Credit");
        var before = data.read(customer);
        fill("picker");
        var invoice = save(false);
        assertStock("3");
        assertCustomerDelta(before, "300");
        returnPart(invoice, "2", "200");
        assertCustomerDelta(before, "100");
        initialBalances.put(1, number(data.balance(item, 1)));
        page.slLinkedReturn(invoice.id(), item.name());
        page.slCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
        page.slNotes(marker + "-OVER-RETURN");
        page.slQuantity(item.name(), "2");
        rejectSave();
        assertCustomerDelta(before, "100");
        assertStock("0");
    }

    @Test
    @Story("E2E بيع بكمية كسرية ثم مرتجع كامل يحافظ على الكسور والأرصدة")
    public void fractionalSaleThenReturnRestoresExactBalances() {
        prepare("Credit");
        var before = data.read(customer);
        var stock = initialBalances.get(1);
        add(item, "picker");
        page.slQuantity(item.name(), "1.25");
        page.slWaitNet(salesNet("125"));
        var invoice = save(false);
        assertStock("1.25");
        assertCustomerDelta(before, "125");
        returnPart(invoice, "1.25", "125");
        assertCustomerDelta(before, "0");
        awaitOwnedStock(stock, "Fractional return must restore the exact opening balance");
    }

    @Test
    @Story("E2E بيع ثم إلغاء مسودة المرتجع يبقي أثر البيع ثم يحفظ المرتجع الصحيح")
    public void cancelledReturnDoesNotPostBeforeTheConfirmedReturn() {
        prepare("Credit");
        var before = data.read(customer);
        var stock = initialBalances.get(1);
        fill("picker");
        var invoice = save(false);
        assertStock("3");
        assertCustomerDelta(before, "300");
        currentKind = SalesDocumentKind.RETURN;
        page = new SalesReturnPage(driver);
        var identities = data.documentIdentities();
        page.slLinkedReturn(invoice.id(), item.name());
        page.slCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
        page.slNotes(marker + "-UNSAVED");
        page.slQuantity(item.name(), "1");
        page.slOpen("invoice-list");
        data.verifyNoNewDocuments(identities);
        awaitOwnedStock(stock.subtract(number("3")), "Cancelled return must not restore stock");
        assertCustomerDelta(before, "300");
        returnPart(invoice, "3", "300");
        assertCustomerDelta(before, "0");
        awaitOwnedStock(stock, "Confirmed return restores original stock");
    }
}
