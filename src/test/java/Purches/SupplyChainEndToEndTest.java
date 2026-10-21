package Purches;

import FinancialMovementsTest.FinancialWorkflow;
import Invoice.SalesWorkflow;
import StoreTest.TransferWorkflow;
import TestSupport.WorkflowCleanupOrder;
import TestSupport.WorkflowEntity;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/** شراء ثم نقل ثم بيع وتحميل نفس الفواتير بالسداد أو إعادتها؛ لا يوجد تجهيز مستندات عبر API. */
public final class SupplyChainEndToEndTest extends PurchaseFormActions {
  private final List<FinancialWorkflow> finances = new ArrayList<>();
  private final List<SalesWorkflow> sales = new ArrayList<>();
  private final List<TransferWorkflow> transfers = new ArrayList<>();
  private final List<TransferWorkflow> reverseTransfers = new ArrayList<>();
  private final List<AutoCloseable> finalPurchaseReturns = new ArrayList<>();
  private PurchaseTestData.Record buyer, salesAgent;
  private Map<String, Object> buyerBefore, supplierBefore;
  private BigDecimal sourceBefore, destinationBefore;
  @Override protected PurchaseDocumentKind kind() { return PurchaseDocumentKind.INVOICE; }

  private static WorkflowEntity ref(PurchaseTestData.Record record) { return new WorkflowEntity(record.id(), record.name()); }

  private PurchaseTestData.Record buy(String quantity) {
    prepare("Credit"); buyer = data.customer(); salesAgent = data.agent(false);
    buyerBefore = data.read(buyer); supplierBefore = data.read(supplier);
    org.testng.Assert.assertEquals(buyerBefore.get("isTaxable"), Boolean.TRUE);
    org.testng.Assert.assertEquals(buyerBefore.get("isOnTheFly"), Boolean.FALSE);
    sourceBefore = initialBalances.get(1); destinationBefore = initialBalances.get(2);
    fill("picker"); page.puQuantity(item.name(), quantity); page.puWaitNet(number(quantity).multiply(number("100")).toPlainString());
    var invoice = save(false); assertStock(quantity); assertSupplierDelta(supplierBefore, number(quantity).multiply(number("-100")).toPlainString());
    return invoice;
  }

  private TransferWorkflow transfer(String quantity) {
    var flow = new TransferWorkflow(driver, ref(item)); transfers.add(flow);
    flow.transfer(ref(data.store(1)), ref(data.store(2)), quantity); return flow;
  }

  private SalesWorkflow selling() {
    var flow = new SalesWorkflow(driver, ref(item), ref(data.store(2)), ref(buyer), ref(salesAgent));
    sales.add(flow); return flow;
  }

  private void stock(BigDecimal source, BigDecimal destination) {
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> number(data.balance(item, 1)).compareTo(source) == 0
        && number(data.balance(item, 2)).compareTo(destination) == 0);
  }

  private void customerDebt(String delta) {
    var expected = signedBalance(buyerBefore).add(number(sales.get(sales.size()-1).net(delta)));
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> signedBalance(data.read(buyer)).compareTo(expected) == 0);
  }

  @Test @Story("E2E شراء ثم تحويل ثم بيع من الوجهة بنفس الصنف والمخازن")
  @Description("شراء3 ثم نقل3 ثم بيع3؛ أرصدة المخزنين تعود لخط الأساس، والمورد مستحق300 والعميل مدين300 مع تحقق هويات كل مستند.")
  public void purchaseTransferAndSaleKeepStockAndPartyLedgersConsistent() {
    buy("3"); transfer("3"); stock(sourceBefore, destinationBefore.add(number("3")));
    selling().sell("3"); stock(sourceBefore, destinationBefore); customerDebt("300"); assertSupplierDelta(supplierBefore, "-300");
  }

  @Test @Story("E2E شراء ونقل وبيع ثم تحصيل العميل وسداد المورد لنفس الفواتير")
  public void supplyChainThenCollectionAndSupplierPaymentClearBothDebts() {
    var purchase = buy("3"); transfer("3"); stock(sourceBefore, destinationBefore.add(number("3")));
    var sale = selling().sell("3"); stock(sourceBefore, destinationBefore); customerDebt("300");
    var receipt = new FinancialWorkflow(driver, false, buyer.id(), buyer.name(), salesAgent.name(), sale.id(), sale.code()); finances.add(receipt);
    receipt.pay(sales.get(sales.size()-1).net("300")); customerDebt("0"); assertSupplierDelta(supplierBefore, "-300");
    var payment = new FinancialWorkflow(driver, true, supplier.id(), supplier.name(), agent.name(), purchase.id(), data.read(purchase).get("code")); finances.add(payment);
    payment.pay("300"); assertSupplierDelta(supplierBefore, "0"); customerDebt("0"); stock(sourceBefore, destinationBefore);
  }

  @Test @Story("E2E شراء ونقل وبيع ثم عكس الرحلة بمرتجع بيع ونقل عكسي ومرتجع شراء")
  public void bothReturnsAndReverseTransferRestoreTheWholeChain() {
    var purchase = buy("3"); transfer("3"); stock(sourceBefore, destinationBefore.add(number("3")));
    var seller = selling(); var sale = seller.sell("3"); stock(sourceBefore, destinationBefore); customerDebt("300");
    seller.returnPart(sale, "3"); stock(sourceBefore, destinationBefore.add(number("3"))); customerDebt("0");
    var reverse = new TransferWorkflow(driver, ref(item)); reverseTransfers.add(reverse);
    reverse.transfer(ref(data.store(2)), ref(data.store(1)), "3"); stock(sourceBefore.add(number("3")), destinationBefore);
    page.puConvertOwned(supplier.name(), data.read(purchase).get("code").toString(), false, item.name());
    convertedKind = PurchaseDocumentKind.RETURN;
    for (int i = 1; i <= 2; i++) initialBalances.put(i, number(data.balance(item, i)));
    page.puNotes(marker + "-PURCHASE-RETURN"); page.puCapture(marker, data.store(1).id(), item.id(), documentKind().endpoint);
    // آخر حركة يجب عكسها أولاً كيلا يطلب تنظيف المرتجع إنقاص مخزون سبق إرجاعه للمورد.
    var returnId = new java.util.concurrent.atomic.AtomicReference<PurchaseTestData.Record>();
    var owner = data;
    finalPurchaseReturns.add(() -> {
      if (returnId.get() == null) throw new AssertionError("Unresolved final purchase return; retain all earlier workflow stages and recover its purchase journal first");
      owner.deleteOwned(returnId.get());
    });
    var returned = save(false); returnId.set(returned); assertSource(returned, purchase.id()); assertStock("3");
    stock(sourceBefore, destinationBefore); customerDebt("0"); assertSupplierDelta(supplierBefore, "0");
  }

  @Test @Story("E2E شراء ونقل وبيع بكمية كسرية يثبت الدقة بين الموديولات")
  public void fractionalSupplyChainPreservesQuantitiesAndAmounts() {
    buy("1.25"); transfer("1.25"); stock(sourceBefore, destinationBefore.add(number("1.25")));
    selling().sell("1.25"); stock(sourceBefore, destinationBefore); customerDebt("125"); assertSupplierDelta(supplierBefore, "-125");
  }

  @Test @Story("E2E شراء3 ونقل2 وبيع1 يحافظ على المخزون المتبقي في المخزنين")
  public void partialTransferAndSaleRetainUnsoldStockInBothStores() {
    buy("3"); transfer("2"); stock(sourceBefore.add(number("1")), destinationBefore.add(number("2")));
    selling().sell("1"); stock(sourceBefore.add(number("1")), destinationBefore.add(number("1")));
    customerDebt("100"); assertSupplierDelta(supplierBefore, "-300");
  }

  @Test @Story("E2E بيع جزئي من الكمية المنقولة ثم مرتجع جزئي يصحح مخزون الوجهة والمديونية")
  public void partialSaleReturnKeepsRemainingDestinationStockAndDebt() {
    buy("3"); transfer("3"); stock(sourceBefore, destinationBefore.add(number("3")));
    var seller = selling(); var sale = seller.sell("2"); stock(sourceBefore, destinationBefore.add(number("1"))); customerDebt("200");
    seller.returnPart(sale, "1"); stock(sourceBefore, destinationBefore.add(number("2"))); customerDebt("100");
    assertSupplierDelta(supplierBefore, "-300");
  }

  private void captureChildren() {
    for (var flow : finances) flow.capture();
    for (var flow : sales) flow.capture();
    for (var flow : transfers) flow.capture();
    for (var flow : reverseTransfers) flow.capture();
  }

  @Override @AfterMethod(alwaysRun = true)
  public void finishPurchaseCase(ITestResult result) { captureChildren(); super.finishPurchaseCase(result); }

  @Override @AfterClass(alwaysRun = true)
  public void cleanPurchasePage() {
    WorkflowCleanupOrder.closeChildrenBeforeParents(finances, () ->
        WorkflowCleanupOrder.closeChildrenBeforeParents(finalPurchaseReturns, () ->
            WorkflowCleanupOrder.closeChildrenBeforeParents(reverseTransfers, () ->
                WorkflowCleanupOrder.closeChildrenBeforeParents(sales, () ->
                    WorkflowCleanupOrder.closeChildrenBeforeParents(transfers, () -> super.cleanPurchasePage())))));
  }
}
