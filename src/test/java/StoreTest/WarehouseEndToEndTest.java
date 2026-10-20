package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.StoreModulePage;
import io.qameta.allure.Allure;
import io.qameta.allure.Story;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** يتتبع نفس الصنف بين المستندات والمخازن؛ المكونات لا تجهز بحركات API. */
public final class WarehouseEndToEndTest extends StoreScenarioTest {
  private StoreMovementKind current = StoreMovementKind.ADDITION;
  private int stageNumber;
  @Override protected StoreMovementKind kind() { return current; }
  @Override protected StoreModulePage movementPage() { return new StoreModulePage(driver); }
  @Override @BeforeMethod(alwaysRun = true)
  public void beginStoreScenario() { current = StoreMovementKind.ADDITION; stageNumber = 0; super.beginStoreScenario(); }

  @SuppressWarnings("unchecked")
  private StoreTestData.Record stage(StoreMovementKind movement, int from, int to, String quantity) {
    current = movement; page = movementPage();
    page.smOpen(current.route + "/new");
    page.smCapture(marker, data.store(from).id(), item.id());
    page.smChoose(current == StoreMovementKind.TRANSFER ? "من مخزن" : "المخزن", data.store(from).name());
    if (current == StoreMovementKind.TRANSFER) page.smChoose("الي مخزن", data.store(to).name());
    Object partyId = null;
    if (current.party()) {
      var party = current == StoreMovementKind.ADDITION ? data.supplier() : data.customer();
      String label = current == StoreMovementKind.ADDITION ? "الموردين" : "العملاء";
      page.smChoose("نوع البند", label); page.smChoose(label, party.name()); partyId = party.id();
    }
    if (current != StoreMovementKind.MANUFACTURING) page.smNotes(marker + "-STEP-" + (++stageNumber) + "-" + current);
    page.smAddPicker(item.name()); page.smQuantity(item.name(), quantity); page.smSave(false);
    new WebDriverWait(driver, Duration.ofSeconds(45)).until(d -> {
      data.rememberAttempts(page.smAttempts(), page.smSaves(), marker);
      return !page.smSaves().isEmpty();
    });
    page.smSettled();
    var saved = page.smSaves().get(page.smSaves().size() - 1);
    Assert.assertEquals(saved.get("endpoint"), current.endpoint);
    var values = (Map<String, Object>) saved.get("values");
    String note = current == StoreMovementKind.MANUFACTURING ? marker : String.valueOf(values.get("notes"));
    data.track(current.endpoint, saved.get("id"), note, values);
    var record = new StoreTestData.Record(current.endpoint, saved.get("id"), note, values);
    var stored = data.read(record);
    Assert.assertEquals(stored.get("storeId"), data.store(from).id());
    if (current == StoreMovementKind.TRANSFER) Assert.assertEquals(stored.get("destinationStoreId"), data.store(to).id());
    if (partyId != null) Assert.assertEquals(stored.get("selectedDestinationId"), partyId);
    var lines = (java.util.List<Map<String, Object>>) stored.get("lines");
    Assert.assertEquals(lines.size(), 1); Assert.assertEquals(lines.get(0).get("itemId"), item.id());
    sameNumber(lines.get(0).get("quantity"), quantity);
    Allure.addAttachment("E2E warehouse stage " + current, "application/json", new org.json.JSONObject(stored).toString(), ".json");
    page.smDismiss(); return record;
  }

  private void balance(int store, java.math.BigDecimal expected) {
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> number(data.balance(item, store)).compareTo(expected) == 0);
  }

  @Test @Story("E2E إذن إضافة ثم تحويل ثم صرف من الوجهة يتتبع نفس الصنف")
  public void receiptThenTransferThenIssueFromDestination() {
    item = data.item(1); var a = number(data.balance(item, 1)); var b = number(data.balance(item, 2));
    stage(StoreMovementKind.ADDITION, 1, 0, "3"); balance(1, a.add(number("3"))); balance(2, b);
    stage(StoreMovementKind.TRANSFER, 1, 2, "3"); balance(1, a); balance(2, b.add(number("3")));
    stage(StoreMovementKind.EXCHANGE, 2, 0, "3"); balance(1, a); balance(2, b);
  }

  @Test @Story("E2E تحويل ثم تحويل عكسي يعيدان أرصدة المخزنين")
  public void transferThenReverseTransferRestoresBothStores() {
    item = data.item(1); var a = number(data.balance(item, 1)); var b = number(data.balance(item, 2));
    stage(StoreMovementKind.TRANSFER, 1, 2, "3"); balance(1, a.subtract(number("3"))); balance(2, b.add(number("3")));
    stage(StoreMovementKind.TRANSFER, 2, 1, "3"); balance(1, a); balance(2, b);
  }

  @Test @Story("E2E إضافة ثم تحويل ثم تالف بالوجهة يمنع تضاعف المخزون")
  public void receiptTransferAndDamagePreserveUnrelatedStock() {
    item = data.item(1); var a = number(data.balance(item, 1)); var b = number(data.balance(item, 2));
    stage(StoreMovementKind.ADDITION, 1, 0, "3"); balance(1, a.add(number("3")));
    stage(StoreMovementKind.TRANSFER, 1, 2, "3"); balance(1, a); balance(2, b.add(number("3")));
    stage(StoreMovementKind.DAMAGE, 2, 0, "3"); balance(1, a); balance(2, b);
  }

  @Test @Story("E2E تصنيع ثم نقل المنتج النهائي وصرفه مع استهلاك المكون مرة واحدة")
  public void manufactureThenTransferThenIssueFinishedProduct() {
    item = data.manufactured(1); var component = data.item(2);
    var componentBefore = number(data.balance(component, 1)); var a = number(data.balance(item, 1)); var b = number(data.balance(item, 2));
    stage(StoreMovementKind.MANUFACTURING, 1, 0, "3"); balance(1, a.add(number("3")));
    sameNumber(data.balance(component, 1), componentBefore.subtract(number("6")).toPlainString());
    stage(StoreMovementKind.TRANSFER, 1, 2, "3"); balance(1, a); balance(2, b.add(number("3")));
    stage(StoreMovementKind.EXCHANGE, 2, 0, "3"); balance(1, a); balance(2, b);
    sameNumber(data.balance(component, 1), componentBefore.subtract(number("6")).toPlainString());
  }

  @Test @Story("E2E نقل كمية كسرية والعودة بها يحافظ على الكسور في المخزنين")
  public void fractionalRoundTripPreservesBothStoreBalances() {
    item = data.item(1); var a = number(data.balance(item, 1)); var b = number(data.balance(item, 2));
    stage(StoreMovementKind.TRANSFER, 1, 2, "1.25"); balance(1, a.subtract(number("1.25"))); balance(2, b.add(number("1.25")));
    stage(StoreMovementKind.TRANSFER, 2, 1, "1.25"); balance(1, a); balance(2, b);
  }
}
