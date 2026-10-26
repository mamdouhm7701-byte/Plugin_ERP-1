package Invoice;

import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class LinkedSalesReturnTest extends SalesScenarioTest {
    private SalesTestData.Record source;

    @Override
    protected SalesDocumentKind kind() {
        return SalesDocumentKind.RETURN;
    }

    private void linked() {
        // The return customer picker lists customers with source invoices. Seed the source
        // before opening its linked return instead of searching for an unfunded new customer.
        prepare("Credit", false, false, false);
        var beforeSource = number(data.balance(item, 1));
        source = data.seedInvoice(101, "Sales", page.slDocumentDate(), "3", "0");
        awaitOwnedStock(
                beforeSource.subtract(new java.math.BigDecimal("3")),
                "Source invoice stock effect");
        initialBalances.put(1, number(data.balance(item, 1)));
        page.slLinkedReturn(source.id(), item.name());
        page.slCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
        page.slNotes(marker);
        Assert.assertEquals(
                page.slDocument().get("agentId"),
                agent.id(),
                "Linked return must retain the owned source agent");
        Assert.assertEquals(page.slDocument().get("sourceTransactionId"), source.id());
        Assert.assertEquals(
                page.slDocument().get("dealerId"), customer.id(), "مطابقة العميل أو المورد");
    }

    @Test
    @Story("مرتجع جزئي يعيد للمخزون الكمية المرتجعة فقط")
    public void partialLinkedReturnRestoresOnlyReturnedQuantity() {
        linked();
        page.slQuantity(item.name(), "1");
        page.slWaitNet(salesNet("100"));
        var saved = save(false);
        sameNumber(firstLine(data.read(saved)).get("quantity"), "1");
        assertStock("1");
    }

    @Test
    @Story("منع مرتجع مرتبط يتجاوز الكمية المباعة بالفاتورة الأصلية")
    public void linkedReturnCannotExceedOriginalQuantity() {
        linked();
        page.slQuantity(item.name(), "4");
        rejectSave();
    }

    @Test
    @Story("مرتجع كامل يعيد للمخزون كامل الكمية المباعة")
    public void fullLinkedReturnRestoresSoldQuantity() {
        linked();
        page.slQuantity(item.name(), "3");
        page.slWaitNet(salesNet("300"));
        save(false);
        assertStock("3");
    }

    @Test
    @Story("منع مجموع المرتجعات المرتبطة من تجاوز الكمية المباعة")
    public void cumulativeLinkedReturnsCannotExceedSoldQuantity() {
        linked();
        page.slQuantity(item.name(), "2");
        page.slWaitNet(salesNet("200"));
        save(false);
        assertStock("2");
        initialBalances.put(1, number(data.balance(item, 1)));
        page.slLinkedReturn(source.id(), item.name());
        page.slCapture(marker, data.store(1).id(), item.id(), kind().endpoint);
        page.slNotes(marker + "-second");
        Assert.assertEquals(page.slDocument().get("agentId"), agent.id(), "مطابقة المندوب");
        page.slQuantity(item.name(), "2");
        rejectSave();
    }
}
