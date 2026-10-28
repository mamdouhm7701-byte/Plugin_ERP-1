/* شرح الملف: اختبارات تنظيف المبيعات: استكمال التنظيف بعد فشل أو انقطاع مع منع الحذف المكرر.
 */
package Invoice;

import io.qameta.allure.Story;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SalesRecovery3CleanupTest {
    @Test
    @Story("استعادة صنف أُنشئ من الواجهة رغم فشل الاستجابة")
    public void recoversUiItemCommittedBeforeFailedResponse() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record unit = data.unit(1), category = data.category();
        String name = "QA-SL-recover-" + java.util.UUID.randomUUID();
        Map<String, Object> values =
                SalesTestData.fields(
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
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        var unit = data.unit(1);
        var category = data.category();
        String marker = "QA-SL-invalid-" + java.util.UUID.randomUUID();
        Map<String, Object> values =
                SalesTestData.fields(
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
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        data.store(1);
        api.timeoutBranchUnlinkAfterCommit = true;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(((List<?>) api.branch.get("stores")).size(), 1);
    }

    @Test
    @Story("إعادة محاولة الحذف غير المنفذ قبل حذف الحسابات التابعة")
    public void retriesUncommittedDeleteBeforeParentAccounts() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.timeOutStore = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record store = data.store(1);
        Object accountId = store.values().get("accountId");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.subList(0, 2), List.of(store.id(), store.id()));
        Assert.assertEquals(api.deletes.get(2), accountId);
    }

    @Test
    @Story("عدم تكرار الحذف عندما نُفذ الطلب الذي انتهت مهلته")
    public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record store = data.store(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }

    @Test
    @Story("قبول قراءة نص فارغ بعد تأكيد تنفيذ الحذف المنتهية مهلته")
    public void acceptsEmptyStringReadAfterCommittedDeleteTimeout() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        api.emptyDeletedResponse = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record store = data.store(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }

    @Test
    @Story("تنظيف الصنف الذي حُفظ رغم فشل طلب الإنشاء")
    public void cleansOwnedItemCommittedBeforeCreateFailure() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.failAfterItemInsert = true;
        api.records.put(
                "shared-item",
                new LinkedHashMap<>(Map.of("id", "shared-item", "arabicName", "Existing item")));
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record unit = data.fixture("unit", "Units", SalesTestData.fields());
        SalesTestData.Record category =
                data.fixture("category", "ItemCategories", SalesTestData.fields());
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        data.fixture(
                                "item",
                                "Items",
                                SalesTestData.fields(
                                        "mainUnitId", unit.id(), "categoryId", category.id())));
        Assert.assertEquals(api.records.size(), 4);
        data.close();
        Assert.assertTrue(api.deletes.get(0).startsWith("Items-"));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-item"));
    }

    @Test
    @io.qameta.allure.Story("استكمال الحذف بعد انقطاع التشغيل من السجلات المتبقية فقط")
    public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        var first = data.unit(1);
        var last = data.unit(2);
        api.crashAtDelete = 2;
        Assert.expectThrows(Error.class, data::close);
        String suffix = last.name().substring(last.name().length() - 12);
        var rows =
                new org.json.JSONArray(
                        java.nio.file.Files.readString(
                                java.nio.file.Path.of(
                                        "target", "sales-fixtures", suffix + ".json")));
        Assert.assertTrue(
                rows.toList().stream().noneMatch(v -> last.id().equals(((Map<?, ?>) v).get("id"))));
        api.crashAtDelete = 0;
        SalesTestData.resume(api.driver(), suffix).close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(java.util.Collections.frequency(api.deletes, last.id()), 1);
    }
}
