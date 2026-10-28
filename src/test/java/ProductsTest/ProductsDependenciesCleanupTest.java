/* شرح الملف: اختبارات تنظيف الأصناف: ترتيب حذف العلاقات قبل البيانات التي تعتمد عليها.
 */
package ProductsTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsDependenciesCleanupTest {
    @Test
    public void removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        ProductsTestData data = new ProductsTestData(api.driver());
        var store = data.store(1);
        Assert.assertTrue(
                ((List<?>) api.branch.get("stores"))
                        .stream().anyMatch(v -> store.id().equals(((Map<?, ?>) v).get("storeId"))));
        List<Object> concurrent = new ArrayList<>((List<?>) api.branch.get("stores"));
        concurrent.add(Map.of("storeId", "another-user-store"));
        api.branch.put("stores", concurrent);
        api.branch.put("phone", "changed-by-user");
        data.close();
        Assert.assertEquals(api.branch.get("phone"), "changed-by-user");
        Assert.assertEquals(api.branch.get("defaultStoreId"), "existing-store");
        Assert.assertEquals(
                ((List<?>) api.branch.get("stores"))
                        .stream()
                                .map(v -> ((Map<?, ?>) v).get("storeId"))
                                .collect(java.util.stream.Collectors.toSet()),
                java.util.Set.of("existing-store", "another-user-store"));
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void retainsStoreAndAccountsWhenOwnedMembershipCannotBeRemoved() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        ProductsTestData data = new ProductsTestData(api.driver());
        var store = data.store(1);
        api.rejectBranchUnlink = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(store.id()));
        api.rejectBranchUnlink = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.rejectStore = true;
        api.records.put(
                "shared-record",
                new LinkedHashMap<>(Map.of("arabicName", "Existing business data")));
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record store = data.store(1);
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertEquals(api.deletes, List.of(store.id()));
        Assert.assertEquals(api.records.size(), 4);
        api.rejectStore = false;
        data.close();
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-record"));
    }

    @Test
    public void updatedRecordDeletesBeforeItsNewReference() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record original = data.store(1);
        ProductsTestData.Record movement =
                data.fixture(
                        "movement",
                        "PriceLists",
                        ProductsTestData.fields("storeId", original.id()));
        ProductsTestData.Record replacement = data.store(2);
        Map<String, Object> updated = new LinkedHashMap<>(movement.values());
        updated.put("storeId", replacement.id());
        api.records.put(movement.id().toString(), updated);
        data.track(movement.endpoint(), movement.id(), movement.name(), updated);
        data.close();
        Assert.assertEquals(api.deletes.get(0), movement.id());
        Assert.assertTrue(
                api.deletes.indexOf(movement.id()) < api.deletes.indexOf(replacement.id()));
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Story("مشاركة المراجع الثابتة بالصفحة وعزل بيانات كل حالة دون حذف بينها")
    public void pageScopeSharesOnlyStableReferences() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        var data = new ProductsTestData(api.driver());
        Assert.expectThrows(
                AssertionError.class,
                () -> data.track("Items", "business-item", "Business record", Map.of()));
        data.beginScenario();
        var stable = data.unit(1);
        var first = data.item(1);
        var firstStore = data.store(1);
        data.beginScenario();
        var second = data.item(1);
        Assert.assertNotEquals(first.id(), second.id());
        Assert.assertNotEquals(
                first.values().get("mainUnitBarcode"), second.values().get("mainUnitBarcode"));
        Assert.assertEquals(data.unit(1).id(), stable.id());
        Assert.assertEquals(data.store(1).id(), firstStore.id());
        var marker = "QA-PR-explicit-" + java.util.UUID.randomUUID();
        Assert.assertEquals(
                data.fixture(marker, "Units", ProductsTestData.fields()).name(), marker);
        Assert.assertTrue(api.records.containsKey(first.id()));
        Assert.assertTrue(api.deletes.isEmpty());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
