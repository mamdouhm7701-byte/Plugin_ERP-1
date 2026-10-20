package Invoice;

import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import TestSupport.WorkflowEntity;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

/**
 * بيع أو مرتجع عبر الواجهة على صنف ومخزن أنشأتهما رحلة شراء أو تصنيع.
 * يملك مستنداته فقط. يجب تنظيفه قبل تحويل المخزون ثم صاحب المراجع الأصلية.
 */
public final class SalesWorkflow implements AutoCloseable {
  public record Document(Object id, String code, String note, String endpoint, Map<String, Object> values) {}
  private final SalesTestData data;
  private final SalesModulePage page;
  private final WebDriverWait wait;
  private final WorkflowEntity item, store, customer, agent;
  private String marker;
  private final WebDriver driver;
  private TestSupport.SalesTaxProfile salesTax;
  public String net(String beforeTax) { return salesTax.net(beforeTax); }
  private SalesDocumentKind kind = SalesDocumentKind.INVOICE;

  public SalesWorkflow(WebDriver driver, WorkflowEntity item, WorkflowEntity store,
      WorkflowEntity customer, WorkflowEntity agent) {
    this.driver = driver; this.item = item; this.store = store; this.customer = customer; this.agent = agent;
    data = new SalesTestData(driver); data.beginScenario();
    page = new SalesModulePage(driver); wait = new WebDriverWait(driver, Duration.ofSeconds(45));
  }

  public Document sell(String quantity) {
    capture(); kind = SalesDocumentKind.INVOICE;
    page.slOpen("invoice/new"); page.slFormReady(); beginCapture();
    page.slChoose("المخزن", store.name()); page.slChoose("نوع السداد", "آجل");
    page.slChoose("العميل", customer.name()); page.slChoose("مندوب", agent.name());
    if (page.slHasChoice("قوائم الأسعار") && !page.slChoiceEmpty("قوائم الأسعار")) page.slClearChoice("قوائم الأسعار");
    salesTax = TestSupport.SalesTaxProfile.capture(driver);
    page.slNotes(marker); page.slAddPicker(item.name()); page.slQuantity(item.name(), quantity);
    page.slSetLine(item.name(), "السعر", "100");
    page.slWaitNet(net(new BigDecimal(quantity).multiply(new BigDecimal("100")).toPlainString()));
    return save(quantity, null);
  }

  public Document returnPart(Document source, String quantity) {
    capture(); kind = SalesDocumentKind.RETURN;
    page.slLinkedReturn(source.id(), item.name()); beginCapture(); page.slNotes(marker);
    Assert.assertEquals(page.slDocument().get("sourceTransactionId"), source.id());
    page.slQuantity(item.name(), quantity);
    page.slWaitNet(net(new BigDecimal(quantity).multiply(new BigDecimal("100")).toPlainString()));
    return save(quantity, source.id());
  }

  private void beginCapture() {
    marker = "QA-SL-E2E-" + UUID.randomUUID().toString().substring(0, 12);
    page.slCapture(marker, store.id(), item.id(), kind.endpoint);
  }

  @SuppressWarnings("unchecked")
  private Document save(String quantity, Object sourceId) {
    page.slSave(false);
    wait.until(d -> { capture(); return !page.slSaves().isEmpty(); });
    page.slSettled(); capture();
    var saved = page.slSaves().get(page.slSaves().size() - 1);
    var values = (Map<String, Object>) saved.get("values");
    var record = new SalesTestData.Record(kind.endpoint, saved.get("id"), marker, values);
    var stored = data.read(record);
    Assert.assertNotNull(stored); Assert.assertEquals(stored.get("dealerId"), customer.id());
    Assert.assertEquals(stored.get("agentId"), agent.id()); Assert.assertEquals(stored.get("storeId"), store.id());
    Assert.assertEquals(stored.get("notes"), marker);
    var lines = (List<Map<String, Object>>) stored.get("lines"); Assert.assertEquals(lines.size(), 1);
    Assert.assertEquals(lines.get(0).get("itemId"), item.id());
    same(lines.get(0).get("quantity"), new BigDecimal(quantity));
    same(stored.get("net"), new BigDecimal(net(new BigDecimal(quantity).multiply(new BigDecimal("100")).toPlainString())));
    if (sourceId != null) Assert.assertEquals(stored.get("sourceTransactionId"), sourceId);
    Allure.addAttachment("Supply chain sales document", "application/json", new org.json.JSONObject(stored).toString(), ".json");
    page.slDismiss();
    return new Document(record.id(), String.valueOf(stored.get("code")), marker, kind.endpoint, values);
  }

  @SuppressWarnings("unchecked")
  public void capture() {
    if (marker == null || !marker.equals(page.slCaptureMarker())) return;
    data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
    for (var saved : page.slSaves()) {
      Assert.assertEquals(saved.get("endpoint"), kind.endpoint);
      data.track(kind.endpoint, saved.get("id"), marker, (Map<String, Object>) saved.get("values"));
    }
  }

  private static void same(Object actual, BigDecimal expected) {
    Assert.assertNotNull(actual); Assert.assertEquals(new BigDecimal(actual.toString()).compareTo(expected), 0);
  }

  @Override public void close() { capture(); data.close(); }
}
