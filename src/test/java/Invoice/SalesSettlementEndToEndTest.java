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

/** فاتورة بيع وسندات قبض فعلية بنفس العميل والفاتورة؛ التنظيف المالي يسبق مصدر البيع. */
public final class SalesSettlementEndToEndTest extends SalesScenarioTest {
  private final List<FinancialWorkflow> settlements = new ArrayList<>();

  private FinancialWorkflow settlement(SalesTestData.Record invoice) {
    var flow = new FinancialWorkflow(driver, false, customer.id(), customer.name(), agent.name(),
        invoice.id(), data.read(invoice).get("code"));
    settlements.add(flow);
    return flow;
  }

  @Test @Story("E2E بيع آجل ثم قبض جزئي ثم استكمال التحصيل")
  @Description("فاتورة 300 ثم سندا قبض 100 و200 مخصصان لنفس الفاتورة؛ المديونية 300 ثم200 ثم صفر والمخزون لا يتغير بالقبض.")
  public void creditSaleThenPartialAndFinalCollection() {
    prepare("Credit"); var before = data.read(customer); fill("picker"); var invoice = save(false);
    assertStock("3"); assertCustomerDelta(before, "300");
    var flow = settlement(invoice); flow.pay("100");
    assertCustomerDelta(before, "200"); assertStock("3");
    flow.pay("200"); assertCustomerDelta(before, "0"); assertStock("3");
  }

  @Test @Story("E2E بيع آجل ثم تعديل سند القبض وعكسه بالحذف")
  @Description("يحصل 100 ثم يعدل نفس السند إلى150 ثم يحذفه؛ المديونية 200 ثم150 ثم300، مع ثبات حركة مخزون الفاتورة.")
  public void collectionEditAndDeleteRecalculateCustomerDebt() {
    prepare("Credit"); var before = data.read(customer); fill("picker"); var invoice = save(false);
    assertCustomerDelta(before, "300"); var flow = settlement(invoice);
    var receipt = flow.pay("100"); assertCustomerDelta(before, "200");
    receipt = flow.edit(receipt, "150"); assertCustomerDelta(before, "150");
    flow.delete(receipt); assertCustomerDelta(before, "300"); assertStock("3");
  }

  @Test @Story("E2E بيع آجل ثم تحصيل كامل ثم حذف التحصيل يعيد المديونية")
  public void fullCollectionThenDeletionRestoresOutstandingDebt() {
    prepare("Credit"); var before = data.read(customer); fill("picker"); var invoice = save(false);
    assertCustomerDelta(before, "300"); var flow = settlement(invoice);
    var receipt = flow.pay("300"); assertCustomerDelta(before, "0");
    flow.delete(receipt); assertCustomerDelta(before, "300"); assertStock("3");
  }

  @Override @AfterMethod(alwaysRun = true)
  public void cleanSalesScenario(ITestResult result) {
    for (var flow : settlements) flow.capture();
    super.cleanSalesScenario(result);
  }

  @Override @AfterClass(alwaysRun = true)
  public void retrySalesCleanup() {
    WorkflowCleanupOrder.closeChildrenBeforeParents(settlements, () -> super.retrySalesCleanup());
  }
}
