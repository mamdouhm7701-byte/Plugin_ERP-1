/* شرح الملف: تنفيذ رحلة تمر بأكثر من مستند مع مراجعة الربط بينها، ضمن الحركات المالية.
 */
package FinancialMovementsTest;

import Plugin_ERP.System_Modules.Login.FinancialMovements.FinancialMovementKind;
import Plugin_ERP.System_Modules.Login.FinancialMovements.FinancialMovementPage;
import io.qameta.allure.Allure;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public final class FinancialWorkflow implements AutoCloseable {
    public record Movement(Object id, String note, String endpoint, Map<String, Object> values) {}

    private final FinancialTestData data;
    private final FinancialMovementPage page;
    private final WebDriverWait wait;
    private final FinancialMovementKind kind;
    private final String partyName, agentName;
    private final Object partyId, invoiceId;
    private final String invoiceCode;
    private String marker;

    public FinancialWorkflow(
            WebDriver driver,
            boolean purchase,
            Object partyId,
            String partyName,
            String agentName,
            Object invoiceId,
            Object invoiceCode) {
        data = new FinancialTestData(driver);
        data.beginScenario();
        page = new FinancialMovementPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(45));
        kind = purchase ? FinancialMovementKind.PAYMENT : FinancialMovementKind.RECEIPT;
        this.partyId = partyId;
        this.partyName = partyName;
        this.agentName = agentName;
        this.invoiceId = invoiceId;
        this.invoiceCode = String.valueOf(invoiceCode);
    }

    public Movement pay(String amount) {
        capture(); // لا تضيع استجابة سند سابق عند إعادة تحميل القائمة.
        var payment = data.payment();
        page.openList(kind.route);
        page.openNew();
        marker = "QA-FM-E2E-" + UUID.randomUUID().toString().substring(0, 12);
        page.captureSaves(marker);
        page.choose(kind.partyLabel, partyName);
        page.choose("مندوب", agentName);
        page.payment(payment.name());
        page.amount(amount);
        page.notes(marker);
        page.allocate(invoiceCode, amount);
        return save(amount);
    }

    public Movement edit(Movement original, String amount) {
        capture();
        page.openList(kind.route);
        page.identifyMovement(original.note(), read(original).get("code"));
        page.editOwned(original.note());
        // تخصيص السند يقفل قيمته في التطبيق؛ نفك الربط من الواجهة ثم نعيده لنفس الفاتورة.
        page.clearAllocationForEdit();
        marker = original.note();
        page.captureSaves(marker);
        page.amount(amount);
        page.allocate(invoiceCode, amount);
        Movement updated = save(amount);
        Assert.assertEquals(updated.id(), original.id(), "Edit must retain the movement ID");
        return updated;
    }

    public void delete(Movement movement) {
        capture();
        page.openList(kind.route);
        page.identifyMovement(movement.note(), read(movement).get("code"));
        page.requestDeleteOwned(movement.note());
        page.confirmDelete(true);
        page.waitForRemoval(movement.note());
        Assert.assertNull(read(movement), "Deleted settlement still exists");
    }

    public Map<String, Object> read(Movement movement) {
        return data.read(
                new FinancialTestData.Record(
                        movement.endpoint(), movement.id(), movement.note(), movement.values()));
    }

    private Movement save(String amount) {
        // حفظ نافذة التخصيص قد يكون بدأ الحفظ بالفعل؛ لا نرسل حفظاً ثانياً.
        if (!page.hasSaveStarted()) page.saveMovement();
        wait.until(
                d -> {
                    capture();
                    return !page.capturedSaves().isEmpty();
                });
        page.awaitSavesSettled();
        capture();
        var saved = page.capturedSaves().get(page.capturedSaves().size() - 1);
        @SuppressWarnings("unchecked")
        var values = (Map<String, Object>) saved.get("values");
        Movement movement = new Movement(saved.get("id"), marker, kind.endpoint, values);
        Map<String, Object> stored = read(movement);
        Assert.assertNotNull(stored);
        Assert.assertEquals(stored.get("dealerId"), partyId, "مطابقة العميل أو المورد");
        Assert.assertEquals(stored.get("notes"), marker, "مطابقة الملاحظات");
        equalAmount(stored.get("value"), amount);
        Assert.assertEquals(stored.get("paymentMethodId"), data.payment().id());
        Object allocations = stored.get("financialTransactionAllocations");
        Assert.assertTrue(
                allocations instanceof List<?> rows && rows.size() == 1,
                "Settlement must allocate only to its source invoice");
        Map<?, ?> allocation = (Map<?, ?>) ((List<?>) allocations).get(0);
        Assert.assertEquals(allocation.get("itemTransactionId"), invoiceId);
        equalAmount(allocation.get("value"), amount);
        Allure.addAttachment(
                "E2E settlement persisted evidence",
                "application/json",
                new org.json.JSONObject(stored).toString(),
                ".json");
        page.waitForList();
        return movement;
    }

    @SuppressWarnings("unchecked")
    public void capture() {
        if (marker == null || !marker.equals(page.captureMarker())) return;
        data.rememberAttempts(page.capturedAttempts(), marker);
        for (Map<String, Object> saved : page.capturedSaves()) {
            Assert.assertEquals(saved.get("endpoint"), kind.endpoint);
            Map<String, Object> values = (Map<String, Object>) saved.get("values");
            data.track(kind.endpoint, saved.get("id"), marker, values);
        }
    }

    private static void equalAmount(Object actual, String expected) {
        Assert.assertNotNull(actual);
        TestSupport.DecimalAmountAssertions.amount(actual, expected);
    }

    @Override
    public void close() {
        capture();
        data.close();
    }
}
