/* شرح الملف: اختبارات تنظيف الأصناف: استكمال التنظيف بعد انقطاع أو استجابة غير محسومة.
 */
package ProductsTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsRecoveryCleanupTest {
    @Test
    public void recoversUiItemCommittedBeforeFailedResponse() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record unit = data.unit(1), category = data.category();
        String name = "QA-PR-recover-" + java.util.UUID.randomUUID();
        Map<String, Object> values =
                ProductsTestData.fields(
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
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        ProductsTestData data = new ProductsTestData(api.driver());
        var unit = data.unit(1);
        var category = data.category();
        String marker = "QA-PR-invalid-" + java.util.UUID.randomUUID();
        Map<String, Object> values =
                ProductsTestData.fields(
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
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        ProductsTestData data = new ProductsTestData(api.driver());
        data.store(1);
        api.timeoutBranchUnlinkAfterCommit = true;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(((List<?>) api.branch.get("stores")).size(), 1);
    }

    @Test
    public void retriesUncommittedDeleteBeforeParentAccounts() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.timeOutStore = true;
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record store = data.store(1);
        Object accountId = store.values().get("accountId");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.subList(0, 2), List.of(store.id(), store.id()));
        Assert.assertEquals(api.deletes.get(2), accountId);
    }

    @Test
    public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record store = data.store(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }

    @Test
    public void acceptsEmptyStringReadAfterCommittedDeleteTimeout() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        api.emptyDeletedResponse = true;
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record store = data.store(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }

    @Test
    public void cleansOwnedItemCommittedBeforeCreateFailure() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.failAfterItemInsert = true;
        api.records.put(
                "shared-item",
                new LinkedHashMap<>(Map.of("id", "shared-item", "arabicName", "Existing item")));
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record unit = data.fixture("unit", "Units", ProductsTestData.fields());
        ProductsTestData.Record category =
                data.fixture("category", "ItemCategories", ProductsTestData.fields());
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        data.fixture(
                                "item",
                                "Items",
                                ProductsTestData.fields(
                                        "mainUnitId", unit.id(), "categoryId", category.id())));
        Assert.assertEquals(api.records.size(), 4);
        data.close();
        Assert.assertTrue(api.deletes.get(0).startsWith("Items-"));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-item"));
    }

    @Test
    @io.qameta.allure.Story("استكمال الحذف بعد انقطاع التشغيل من السجلات المتبقية فقط")
    public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        var data = new ProductsTestData(api.driver());
        var first = data.unit(1);
        var last = data.unit(2);
        api.crashAtDelete = 2;
        Assert.expectThrows(Error.class, data::close);
        String suffix = last.name().substring(last.name().length() - 12);
        var rows =
                new org.json.JSONArray(
                        java.nio.file.Files.readString(
                                java.nio.file.Path.of(
                                        "target", "products-fixtures", suffix + ".json")));
        Assert.assertTrue(
                rows.toList().stream().noneMatch(v -> last.id().equals(((Map<?, ?>) v).get("id"))));
        api.crashAtDelete = 0;
        ProductsTestData.resume(api.driver(), suffix).close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(java.util.Collections.frequency(api.deletes, last.id()), 1);
    }

    @Test
    @io.qameta.allure.Story(
            "الاحتفاظ بمحاولة الإنشاء عند فقد الاستجابة واستعادتها بعد إعادة التشغيل دون حذف المتطلبات مبكراً")
    public void resumesUncertainCreateBeforeDeletingItsDependencies() throws Exception {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        var data = new ProductsTestData(api.driver());
        var account = data.account("retained");
        api.failAfterUnitInsert = true;
        Assert.expectThrows(AssertionError.class, () -> data.unit(1));
        api.failPendingLookup = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(account.id()));
        api.failPendingLookup = false;
        api.failAfterUnitInsert = false;
        ProductsTestData.resume(api.driver(), data.journalSuffix()).close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
