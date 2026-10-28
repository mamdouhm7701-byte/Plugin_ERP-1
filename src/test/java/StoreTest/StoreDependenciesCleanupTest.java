/* شرح الملف: اختبارات تنظيف المخازن: ترتيب حذف العلاقات قبل البيانات التي تعتمد عليها.
 */
package StoreTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StoreDependenciesCleanupTest {
    @Test
    @io.qameta.allure.Description(
            "العميل المستخدم في البيانات دائم وخاضع للضريبة ويسمح بالبيع الآجل؛ صلاحية البيع النقدي لا تجعله عميلاً نقدياً.")
    public void normalCustomerIsTaxableAndKeepsItsOwnAccount() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        try (var data = new StoreTestData(api.driver())) {
            var customer = data.customer();
            var stored = data.read(customer);
            Assert.assertEquals(stored.get("isTaxable"), Boolean.TRUE);
            Assert.assertEquals(stored.get("isOnTheFly"), Boolean.FALSE);
            Assert.assertEquals(stored.get("creditSales"), Boolean.TRUE);
            Assert.assertEquals(stored.get("creditNote"), Boolean.TRUE);
            Assert.assertNotNull(stored.get("accountId"));
        }
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
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
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
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
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.rejectStore = true;
        api.records.put(
                "shared-record",
                new LinkedHashMap<>(Map.of("arabicName", "Existing business data")));
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record store = data.store(1);
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertEquals(api.deletes, List.of(store.id()));
        Assert.assertEquals(api.records.size(), 4);
        api.rejectStore = false;
        data.close();
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-record"));
    }

    @Test
    public void updatedRecordDeletesBeforeItsNewReference() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record original = data.store(1);
        StoreTestData.Record movement =
                data.fixture(
                        "movement",
                        "ManufacturingOrders",
                        StoreTestData.fields("storeId", original.id()));
        StoreTestData.Record replacement = data.store(2);
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
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        var data = new StoreTestData(api.driver());
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
        Assert.assertTrue(api.records.containsKey(first.id()));
        Assert.assertTrue(api.deletes.isEmpty());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
