/* شرح الملف: اختبارات تنظيف المخازن: استكمال التنظيف بعد انقطاع أو استجابة غير محسومة.
 */
package StoreTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StoreRecovery2CleanupTest {
    @Test
    public void recoversUiItemCommittedBeforeFailedResponse() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record unit = data.unit(1), category = data.category();
        String name = "QA-ST-recover-" + java.util.UUID.randomUUID();
        Map<String, Object> values =
                StoreTestData.fields(
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
    public void recoversInvalidItemByUniqueEnglishMarker() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        var unit = data.unit(1);
        var category = data.category();
        String marker = "QA-ST-invalid-" + java.util.UUID.randomUUID();
        Map<String, Object> values =
                StoreTestData.fields(
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
    public void verifiesCommittedBranchUnlinkAfterLostResponse() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        data.store(1);
        api.timeoutBranchUnlinkAfterCommit = true;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(((List<?>) api.branch.get("stores")).size(), 1);
    }

    @Test
    public void retriesUncommittedDeleteBeforeParentAccounts() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.timeOutStore = true;
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record store = data.store(1);
        Object accountId = store.values().get("accountId");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.subList(0, 2), List.of(store.id(), store.id()));
        Assert.assertEquals(api.deletes.get(2), accountId);
    }

    @Test
    public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record store = data.store(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }

    @Test
    public void acceptsEmptyStringReadAfterCommittedDeleteTimeout() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        api.emptyDeletedResponse = true;
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record store = data.store(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }

    @Test
    public void cleansOwnedItemCommittedBeforeCreateFailure() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.failAfterItemInsert = true;
        api.records.put(
                "shared-item",
                new LinkedHashMap<>(Map.of("id", "shared-item", "arabicName", "Existing item")));
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record unit = data.fixture("unit", "Units", StoreTestData.fields());
        StoreTestData.Record category =
                data.fixture("category", "ItemCategories", StoreTestData.fields());
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        data.fixture(
                                "item",
                                "Items",
                                StoreTestData.fields(
                                        "mainUnitId", unit.id(), "categoryId", category.id())));
        Assert.assertEquals(api.records.size(), 4);
        data.close();
        Assert.assertTrue(api.deletes.get(0).startsWith("Items-"));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-item"));
    }

    @Test
    @io.qameta.allure.Story("استكمال الحذف بعد انقطاع التشغيل من السجلات المتبقية فقط")
    public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        var data = new StoreTestData(api.driver());
        var first = data.unit(1);
        var last = data.unit(2);
        api.crashAtDelete = 2;
        Assert.expectThrows(Error.class, data::close);
        String suffix = last.name().substring(last.name().length() - 12);
        var rows =
                new org.json.JSONArray(
                        java.nio.file.Files.readString(
                                java.nio.file.Path.of(
                                        "target", "store-fixtures", suffix + ".json")));
        Assert.assertTrue(
                rows.toList().stream().noneMatch(v -> last.id().equals(((Map<?, ?>) v).get("id"))));
        api.crashAtDelete = 0;
        StoreTestData.resume(api.driver(), suffix).close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(java.util.Collections.frequency(api.deletes, last.id()), 1);
    }
}
