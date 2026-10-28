/* شرح الملف: اختبارات تنظيف المشتريات: استكمال التنظيف بعد فشل أو انقطاع مع منع الحذف المكرر.
 */
package Purches;

import io.qameta.allure.Story;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PurchaseRecovery2CleanupTest {
    @Test
    @Story("فشل التأكد من المخزن يحتفظ بحساباته")
    public void uncertainStoreLookupFailurePreservesItsAccounts() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.failAfterStoreInsert = true;
        PurchaseTestData data = new PurchaseTestData(api.driver());
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        api.failFixtureLookup = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertEquals(api.records.size(), 3);
        api.failFixtureLookup = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("التحقق من استمرار ارتباط المخزن غير المؤكد بحسابه المحدد")
    public void uncertainStoreMustStillReferenceItsOwnedAccount() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.failAfterStoreInsert = true;
        PurchaseTestData data = new PurchaseTestData(api.driver());
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        var stored =
                api.records.values().stream()
                        .filter(r -> r.get("id").toString().startsWith("Stores-"))
                        .findFirst()
                        .orElseThrow();
        Object account = stored.put("accountId", "another-account");
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        stored.put("accountId", account);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("استعادة إذن مخزني حُفظ قبل فقد استجابة الإنشاء")
    public void recoversWarehouseCreateCommittedBeforeLostResponse() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        String marker = "QA-PU-lost-create";
        Map<String, Object> values =
                PurchaseTestData.fields(
                        "notes",
                        marker,
                        "storeId",
                        store.id(),
                        "transactionType",
                        "Purchases",
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        Map<String, Object> stored = new LinkedHashMap<>(values);
        stored.put("id", "lost-create");
        api.records.put("lost-create", stored);
        data.rememberAttempts(
                List.of(PurchaseTestData.fields("endpoint", "Purchases", "values", values)),
                List.of(),
                marker);
        data.close();
        Assert.assertEquals(api.deletes.get(0), "lost-create");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("فشل البحث عن إذن مخزني يحتفظ بجميع تبعياته")
    public void uncertainWarehouseLookupFailureRetainsAllParents() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        Map<String, Object> values =
                PurchaseTestData.fields(
                        "notes",
                        "QA-PU-unresolved",
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        data.rememberAttempts(
                List.of(PurchaseTestData.fields("endpoint", "Purchases", "values", values)),
                List.of(),
                "QA-PU-unresolved");
        api.failMovementLookup = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(item.id()));
        api.failMovementLookup = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("استعادة الملاحظات المعدلة بعد فقد استجابة التعديل")
    public void recoversUpdatedNoteAfterLostUpdateResponse() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        String marker = "QA-PU-lost-update";
        Map<String, Object> original =
                PurchaseTestData.fields(
                        "id",
                        "lost-update",
                        "notes",
                        marker,
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        api.records.put("lost-update", new LinkedHashMap<>(original));
        data.track("Purchases", "lost-update", marker, original);
        Map<String, Object> updated = new LinkedHashMap<>(original);
        updated.put("notes", marker + " EDIT");
        api.records.put("lost-update", updated);
        data.rememberAttempts(
                List.of(PurchaseTestData.fields("endpoint", "Purchases", "values", updated)),
                List.of(),
                marker);
        data.close();
        Assert.assertEquals(api.deletes.get(0), "lost-update");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("استعادة صنف أُنشئ من الواجهة رغم فشل الاستجابة")
    public void recoversUiItemCommittedBeforeFailedResponse() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record unit = data.unit(1), category = data.category();
        String name = "QA-PU-recover-" + java.util.UUID.randomUUID();
        Map<String, Object> values =
                PurchaseTestData.fields(
                        "arabicName", name, "mainUnitId", unit.id(), "categoryId", category.id());
        Map<String, Object> saved = new LinkedHashMap<>(values);
        saved.put("id", "Items-ui");
        api.records.put("Items-ui", saved);
        api.records.put(
                "shared-item",
                new LinkedHashMap<>(Map.of("id", "shared-item", "arabicName", "Existing item")));
        data.recoverAttemptedItems(List.of(Map.of("endpoint", "Items", "values", values)));
        data.close();
        Assert.assertEquals(api.deletes.get(0), "Items-ui");
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-item"));
    }

    @Test
    @Story("استعادة صنف غير صالح بعلامة الاختبار الإنجليزية الفريدة")
    public void recoversInvalidItemByUniqueEnglishMarker() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var unit = data.unit(1);
        var category = data.category();
        String marker = "QA-PU-invalid-" + java.util.UUID.randomUUID();
        Map<String, Object> values =
                PurchaseTestData.fields(
                        "arabicName",
                        "",
                        "englishName",
                        marker,
                        "mainUnitId",
                        unit.id(),
                        "categoryId",
                        category.id());
        Map<String, Object> saved = new LinkedHashMap<>(values);
        saved.put("id", "Items-invalid");
        api.records.put("Items-invalid", saved);
        api.records.put(
                "shared-empty",
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "shared-empty",
                                "arabicName",
                                "",
                                "englishName",
                                "Existing item")));
        data.recoverAttemptedItems(List.of(Map.of("endpoint", "Items", "values", values)));
        data.close();
        Assert.assertEquals(api.deletes.get(0), "Items-invalid");
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-empty"));
    }

    @Test
    @Story("تأكيد فك ارتباط الفرع المنفذ بعد فقد الاستجابة")
    public void verifiesCommittedBranchUnlinkAfterLostResponse() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        data.store(1);
        api.timeoutBranchUnlinkAfterCommit = true;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(((List<?>) api.branch.get("stores")).size(), 1);
    }

    @Test
    @Story("إعادة محاولة الحذف غير المنفذ قبل حذف الحسابات التابعة")
    public void retriesUncommittedDeleteBeforeParentAccounts() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.timeOutStore = true;
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record store = data.store(1);
        Object accountId = store.values().get("accountId");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.subList(0, 2), List.of(store.id(), store.id()));
        Assert.assertEquals(api.deletes.get(2), accountId);
    }

    @Test
    @Story("عدم تكرار الحذف عندما نُفذ الطلب الذي انتهت مهلته")
    public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record store = data.store(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }

    @Test
    @Story("قبول قراءة نص فارغ بعد تأكيد تنفيذ الحذف المنتهية مهلته")
    public void acceptsEmptyStringReadAfterCommittedDeleteTimeout() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        api.emptyDeletedResponse = true;
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record store = data.store(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }
}
