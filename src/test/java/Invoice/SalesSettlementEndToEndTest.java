/* شرح الملف: اختبارات تعديل سند القبض وحذفه يعيدان حساب مديونية العميل: تعديل سند القبض وحذفه
 * يعيدان حساب مديونية العميل؛ بيع آجل ثم تحصيل كامل ثم حذف التحصيل يعيد المديونية؛ تحصيل جزئي ثم
 * كامل لفاتورة بيع آجلة.
 */
package Invoice;

import FinancialMovementsTest.FinancialWorkflow;
import TestSupport.WorkflowCleanupOrder;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import java.util.ArrayList;
import java.util.List;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public final class SalesSettlementEndToEndTest extends SalesScenarioTest {
    private final List<FinancialWorkflow> settlements = new ArrayList<>();

    private FinancialWorkflow settlement(SalesTestData.Record invoice) {
        var flow =
                new FinancialWorkflow(
                        driver,
                        false,
                        customer.id(),
                        customer.name(),
                        agent.name(),
                        invoice.id(),
                        data.read(invoice).get("code"));
        settlements.add(flow);
        return flow;
    }

    @Test
    @Story("E2E بيع آجل ثم قبض جزئي ثم استكمال التحصيل")
    @Description(
            "فاتورة أساسها قبل الضريبة 300 ثم سندا قبض أساسهما 100 و200، وتضاف ضريبة الفرع إلى المبالغ مخصصان لنفس الفاتورة؛ المديونية 300 ثم200 ثم صفر والمخزون لا يتغير بالقبض.")
    public void creditSaleThenPartialAndFinalCollection() {
        prepare("Credit");
        var before = data.read(customer);
        fill("picker");
        var invoice = save(false);
        assertStock("3");
        assertCustomerDelta(before, "300");
        var flow = settlement(invoice);
        flow.pay(salesNet("100"));
        assertCustomerDelta(before, "200");
        assertStock("3");
        flow.pay(salesNet("200"));
        assertCustomerDelta(before, "0");
        assertStock("3");
    }

    @Test
    @Story("E2E بيع آجل ثم تعديل سند القبض وعكسه بالحذف")
    @Description(
            "يحصل أساس100 ثم يعدل أساس نفس السند إلى150 ثم يحذفه، مع احتساب ضريبة الفرع في المبالغ؛ المديونية 200 ثم150 ثم300، مع ثبات حركة مخزون الفاتورة.")
    public void collectionEditAndDeleteRecalculateCustomerDebt() {
        prepare("Credit");
        var before = data.read(customer);
        fill("picker");
        var invoice = save(false);
        assertCustomerDelta(before, "300");
        var flow = settlement(invoice);
        var receipt = flow.pay(salesNet("100"));
        assertCustomerDelta(before, "200");
        receipt = flow.edit(receipt, salesNet("150"));
        assertCustomerDelta(before, "150");
        flow.delete(receipt);
        assertCustomerDelta(before, "300");
        assertStock("3");
    }

    @Test
    @Story("E2E بيع آجل ثم تحصيل كامل ثم حذف التحصيل يعيد المديونية")
    public void fullCollectionThenDeletionRestoresOutstandingDebt() {
        prepare("Credit");
        var before = data.read(customer);
        fill("picker");
        var invoice = save(false);
        assertCustomerDelta(before, "300");
        var flow = settlement(invoice);
        var receipt = flow.pay(salesNet("300"));
        assertCustomerDelta(before, "0");
        flow.delete(receipt);
        assertCustomerDelta(before, "300");
        assertStock("3");
    }

    @Override
    @AfterMethod(alwaysRun = true)
    public void cleanSalesScenario(ITestResult result) {
        for (var flow : settlements) flow.capture();
        super.cleanSalesScenario(result);
    }

    @Override
    @AfterClass(alwaysRun = true)
    public void retrySalesCleanup() {
        WorkflowCleanupOrder.closeChildrenBeforeParents(
                settlements, () -> super.retrySalesCleanup());
    }
}
