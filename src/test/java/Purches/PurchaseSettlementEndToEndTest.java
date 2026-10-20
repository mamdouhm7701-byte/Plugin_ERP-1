package Purches;

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

/** فاتورة شراء ثم سندات صرف للمورد نفسه، مع التحقق من التخصيص والمديونية والمخزون. */
public final class PurchaseSettlementEndToEndTest extends PurchaseScenarioTest {
  private final List<FinancialWorkflow> settlements = new ArrayList<>();
  @Override protected PurchaseDocumentKind kind() { return PurchaseDocumentKind.INVOICE; }

  private FinancialWorkflow settlement(PurchaseTestData.Record invoice) {
    var flow = new FinancialWorkflow(driver, true, supplier.id(), supplier.name(), agent.name(),
        invoice.id(), data.read(invoice).get("code"));
    settlements.add(flow); return flow;
  }

  @Test @Story("E2E شراء آجل ثم سداد جزئي ثم استكمال سداد المورد")
  @Description("فاتورة300 ثم صرف100 و200 لنفس الفاتورة؛ صافي رصيد المورد -300 ثم-200 ثم صفر، مع ثبات المخزون بعد السداد.")
  public void creditPurchaseThenPartialAndFinalPayment() {
    prepare("Credit"); var before = data.read(supplier); fill("picker"); var invoice = save(false);
    assertStock("3"); assertSupplierDelta(before, "-300"); var flow = settlement(invoice);
    flow.pay("100"); assertSupplierDelta(before, "-200"); assertStock("3");
    flow.pay("200"); assertSupplierDelta(before, "0"); assertStock("3");
  }

  @Test @Story("E2E شراء ثم تعديل سند صرف المورد ثم حذفه يعكس السداد")
  public void paymentEditAndDeleteRecalculateSupplierDebt() {
    prepare("Credit"); var before = data.read(supplier); fill("picker"); var invoice = save(false);
    assertSupplierDelta(before, "-300"); var flow = settlement(invoice);
    var payment = flow.pay("100"); assertSupplierDelta(before, "-200");
    payment = flow.edit(payment, "150"); assertSupplierDelta(before, "-150");
    flow.delete(payment); assertSupplierDelta(before, "-300"); assertStock("3");
  }

  @Test @Story("E2E سداد فاتورة شراء كاملة ثم حذف السداد يعيد مستحق المورد")
  public void fullPaymentThenDeletionRestoresSupplierDebt() {
    prepare("Credit"); var before = data.read(supplier); fill("picker"); var invoice = save(false);
    assertSupplierDelta(before, "-300"); var flow = settlement(invoice);
    var payment = flow.pay("300"); assertSupplierDelta(before, "0");
    flow.delete(payment); assertSupplierDelta(before, "-300"); assertStock("3");
  }

  @Override @AfterMethod(alwaysRun = true)
  public void finishPurchaseCase(ITestResult result) {
    for (var flow : settlements) flow.capture();
    super.finishPurchaseCase(result);
  }

  @Override @AfterClass(alwaysRun = true)
  public void cleanPurchasePage() {
    WorkflowCleanupOrder.closeChildrenBeforeParents(settlements, () -> super.cleanPurchasePage());
  }
}
