package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.StoreModulePage;
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

/** ينقل نفس الصنف والمخزنين التابعين لمالك خارجي؛ لا يجهز صنفاً بديلاً ولا يحذف المراجع. */
public final class TransferWorkflow implements AutoCloseable {
  private static final String ENDPOINT = "ItemTransactions-storageTransfer";
  private final StoreTestData data;
  private final StoreModulePage page;
  private final WebDriverWait wait;
  private final WorkflowEntity item;
  private String marker;

  public TransferWorkflow(WebDriver driver, WorkflowEntity item) {
    this.item = item; data = new StoreTestData(driver); data.beginScenario();
    page = new StoreModulePage(driver); wait = new WebDriverWait(driver, Duration.ofSeconds(45));
  }

  @SuppressWarnings("unchecked")
  public void transfer(WorkflowEntity from, WorkflowEntity to, String quantity) {
    capture(); page.smOpen("storage-transfer/new");
    marker = "QA-ST-E2E-" + UUID.randomUUID().toString().substring(0, 12);
    page.smCapture(marker, from.id(), item.id());
    page.smChoose("من مخزن", from.name()); page.smChoose("الي مخزن", to.name());
    page.smNotes(marker); page.smAddPicker(item.name()); page.smQuantity(item.name(), quantity); page.smSave(false);
    wait.until(d -> { capture(); return !page.smSaves().isEmpty(); });
    page.smSettled(); capture();
    var saved = page.smSaves().get(page.smSaves().size() - 1);
    var values = (Map<String, Object>) saved.get("values");
    var stored = data.read(new StoreTestData.Record(ENDPOINT, saved.get("id"), marker, values));
    Assert.assertEquals(stored.get("storeId"), from.id()); Assert.assertEquals(stored.get("destinationStoreId"), to.id());
    Assert.assertEquals(stored.get("notes"), marker);
    var lines = (List<Map<String, Object>>) stored.get("lines"); Assert.assertEquals(lines.size(), 1);
    Assert.assertEquals(lines.get(0).get("itemId"), item.id());
    Assert.assertEquals(new BigDecimal(lines.get(0).get("quantity").toString()).compareTo(new BigDecimal(quantity)), 0);
    Allure.addAttachment("Supply chain transfer", "application/json", new org.json.JSONObject(stored).toString(), ".json");
    page.smDismiss();
  }

  @SuppressWarnings("unchecked")
  public void capture() {
    if (marker == null || !marker.equals(page.smCaptureMarker())) return;
    data.rememberAttempts(page.smAttempts(), page.smSaves(), marker);
    for (var saved : page.smSaves()) {
      Assert.assertEquals(saved.get("endpoint"), ENDPOINT);
      data.track(ENDPOINT, saved.get("id"), marker, (Map<String, Object>) saved.get("values"));
    }
  }

  @Override public void close() { capture(); data.close(); }
}
