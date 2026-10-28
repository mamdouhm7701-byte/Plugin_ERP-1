/* شرح الملف: اختبارات تنظيف المشتريات: حذف البيانات المملوكة والتحقق من غيابها.
 */
package Purches;

import io.qameta.allure.Story;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PurchaseCleanupTest {
    @Test
    @Story("التحقق من مورد سند القبض عبر تفاصيله عندما تخفي القائمة هوية المتعامل دون تعديل أي سند")
    public void receiptLookupUsesDetailsAndPreservesUnrelatedReceipts() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        var supplier = data.supplier();
        api.records.put(
                "own-receipt",
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "own-receipt",
                                "dealerId",
                                supplier.id(),
                                "value",
                                300,
                                "receipt",
                                true)));
        api.records.put(
                "existing-receipt",
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "existing-receipt",
                                "dealerId",
                                "existing-supplier",
                                "value",
                                600,
                                "receipt",
                                true)));
        var found = data.ownedSupplierReceipts(supplier.id());
        Assert.assertEquals(found.size(), 1);
        Assert.assertEquals(found.get(0).get("id"), "own-receipt");
        Assert.assertTrue(api.deletes.isEmpty());
        data.close();
        Assert.assertEquals(
                api.records.keySet(), java.util.Set.of("own-receipt", "existing-receipt"));
    }

    @Test
    @Story(
            "إعادة محاولة إنشاء البيانات المدخلة بعد إلغاء المعاملة بسبب تعارض أقفال دون تكرار السجل")
    public void retriesRolledBackDeadlockWithoutDuplicateFixture() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.deadlockCreates = 1;
        var data = new PurchaseTestData(api.driver());
        var unit = data.unit(1);
        Assert.assertEquals(api.createCalls, 2);
        Assert.assertEquals(api.records.size(), 1);
        Assert.assertTrue(api.records.containsKey(unit.id()));
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("تحديد إعادة محاولة تعارض الأقفال بثلاث محاولات مع إبقاء بيانات الاستعادة")
    public void boundsRolledBackDeadlockRetries() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.deadlockCreates = 4;
        var data = new PurchaseTestData(api.driver());
        Assert.expectThrows(AssertionError.class, () -> data.unit(1));
        Assert.assertEquals(api.createCalls, 3);
        Assert.assertTrue(api.records.isEmpty());
        data.close();
    }

    @Test
    @Story("انتظار تحميل الفرع الأصلي قبل الوصول إلى البيانات المدخلة")
    public void waitsForOriginalBranchHydrationBeforeFixtureAccess() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        api.branchUnavailableReads = 2;
        var unit = data.unit(1);
        Assert.assertTrue(api.records.containsKey(unit.id()));
        Assert.assertEquals(api.branchUnavailableReads, 0);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("اكتشاف حفظ فعلي خلف استجابة فاشلة في اختبار رفض الحفظ")
    public void negativeSaveDetectsACommitBehindFailedResponse() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        var before = data.documentIdentities();
        String marker = "QA-PU-invalid-partial-commit";
        var values =
                PurchaseTestData.fields(
                        "id",
                        "partial-invalid",
                        "notes",
                        marker,
                        "transactionType",
                        "Purchases",
                        "storeId",
                        data.store(1).id(),
                        "lines",
                        List.of(
                                Map.of(
                                        "itemId",
                                        item.id(),
                                        "unitId",
                                        data.unit(1).id(),
                                        "quantity",
                                        0)));
        api.records.put("partial-invalid", new LinkedHashMap<>(values));
        var attempted = new LinkedHashMap<>(values);
        attempted.remove("id");
        data.rememberAttempts(
                List.of(PurchaseTestData.fields("endpoint", "Purchases", "values", attempted)),
                List.of(),
                marker);
        Assert.assertTrue(
                Assert.expectThrows(AssertionError.class, () -> data.verifyNoNewDocuments(before))
                        .getMessage()
                        .contains("Invalid sales document committed"));
        data.close();
        Assert.assertEquals(api.deletes.get(0), "partial-invalid");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("حذف الحركة المحددة قبل أصنافها ومخازنها")
    public void deletesOwnedMovementBeforeItsItemsAndStores() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        var movement =
                data.fixture(
                        "movement",
                        "Purchases",
                        PurchaseTestData.fields(
                                "storeId",
                                store.id(),
                                "lines",
                                List.of(Map.of("itemId", item.id()))));
        api.records.put(
                "existing-movement",
                new LinkedHashMap<>(Map.of("id", "existing-movement", "notes", "Business record")));
        data.close();
        Assert.assertTrue(api.deletes.indexOf(movement.id()) < api.deletes.indexOf(item.id()));
        Assert.assertTrue(api.deletes.indexOf(item.id()) < api.deletes.indexOf(store.id()));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("existing-movement"));
    }

    @Test
    @Story("استئناف إنشاء مخزن معلق من سجل التنظيف المحفوظ")
    public void resumesPendingStoreCreationFromDurableJournal() throws Exception {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.failAfterStoreInsert = true;
        WebDriver driver = api.driver();
        PurchaseTestData data = new PurchaseTestData(driver);
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        String name =
                api.records.values().stream()
                        .filter(r -> r.get("id").toString().startsWith("Stores-"))
                        .map(r -> r.get("arabicName").toString())
                        .findFirst()
                        .orElseThrow();
        PurchaseTestData.resume(driver, name.substring(name.length() - 12)).close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("الاحتفاظ بتبعيات المخزون عند فشل حذف الحركة")
    public void retainsStockDependenciesWhenMovementDeletionFails() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        data.fixture(
                "movement",
                "Purchases",
                PurchaseTestData.fields(
                        "storeId", store.id(), "lines", List.of(Map.of("itemId", item.id()))));
        api.rejectMovement = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.records.containsKey(item.id()));
        Assert.assertTrue(api.records.containsKey(store.id()));
        api.rejectMovement = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("رفض تسجيل سجل غير مملوك للاختبار ضمن بيانات التنظيف")
    public void refusesTrackingUnownedRecord() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        Assert.expectThrows(
                AssertionError.class,
                () -> data.track("Items", "business-item", "Business record", Map.of()));
        data.close();
        Assert.assertTrue(api.deletes.isEmpty());
    }

    @Test
    @Story("تنظيف مستند بلا ملاحظات بعد التحقق من ملكية مخزنه وأصنافه")
    public void cleansSalesDocumentWithoutNotesUsingOwnedStoreAndItems() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        Map<String, Object> values =
                PurchaseTestData.fields(
                        "id",
                        "owned-salesDocument",
                        "notes",
                        "",
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        api.records.put("owned-salesDocument", new LinkedHashMap<>(values));
        data.track("Purchases", "owned-salesDocument", "QA-PU-salesDocument", values);
        data.close();
        Assert.assertEquals(api.deletes.get(0), "owned-salesDocument");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("قبول استجابة عدم وجود السجل عند الحذف بعد تأكيد غيابه بالقراءة")
    public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.missingDelete400 = true;
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record store = data.store(1);
        api.records.remove(store.id());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("رفض اعتبار السجل محذوفاً عندما تؤكد القراءة وجوده")
    public void rejectsMissingDeleteWhenOwnedRecordStillExists() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record unit = data.unit(1);
        api.missingDeleteForExisting = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.records.containsKey(unit.id()));
        api.missingDeleteForExisting = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
