/* شرح الملف: اختبارات تنظيف المخازن: استكمال التنظيف بعد انقطاع أو استجابة غير محسومة.
 */
package StoreTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StoreRecoveryCleanupTest {
    @Test
    public void uncertainCreateStillRejectsUntrackedMovementInOwnedStore() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        var store = data.store(1);
        var item = data.item(1);
        var unit = data.unit(1);
        api.records.put(
                "untracked",
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "untracked",
                                "storeId",
                                store.id(),
                                "transactionType",
                                "Manufacturing",
                                "notes",
                                "",
                                "lines",
                                List.of(Map.of("itemId", "foreign-item", "unitId", unit.id())))));
        data.rememberAttempts(
                List.of(
                        StoreTestData.fields(
                                "endpoint",
                                "ManufacturingOrders",
                                "values",
                                StoreTestData.fields(
                                        "storeId",
                                        store.id(),
                                        "notes",
                                        "",
                                        "lines",
                                        List.of(
                                                Map.of(
                                                        "itemId", item.id(), "unitId",
                                                        unit.id()))))),
                List.of(),
                "QA-ST-unresolved-manufacturing");
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        api.records.remove("untracked");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void uncertainCreateDoesNotConfuseEarlierOwnedMovementInSharedStore() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        var store = data.store(1);
        var first = data.item(1);
        var second = data.item(2);
        var unit = data.unit(1);
        var previous =
                data.fixture(
                        "previous-manufacturing",
                        "ManufacturingOrders",
                        StoreTestData.fields(
                                "storeId",
                                store.id(),
                                "transactionType",
                                "Manufacturing",
                                "lines",
                                List.of(Map.of("itemId", first.id(), "unitId", unit.id()))));
        var values =
                StoreTestData.fields(
                        "notes",
                        "",
                        "storeId",
                        store.id(),
                        "transactionType",
                        "Manufacturing",
                        "lines",
                        List.of(Map.of("itemId", second.id(), "unitId", unit.id())));
        data.rememberAttempts(
                List.of(StoreTestData.fields("endpoint", "ManufacturingOrders", "values", values)),
                List.of(),
                "QA-ST-uncommitted-manufacturing");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(java.util.Collections.frequency(api.deletes, previous.id()), 1);
    }

    @Test
    public void recoversStoreCommittedBeforeLostCreateResponse() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.failAfterStoreInsert = true;
        StoreTestData data = new StoreTestData(api.driver());
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        Object storeId =
                api.records.keySet().stream()
                        .filter(k -> k.startsWith("Stores-"))
                        .findFirst()
                        .orElseThrow();
        data.rememberAttempts(List.of(), List.of(), "QA-ST-no-ui-attempts");
        data.close();
        Assert.assertEquals(api.deletes.get(0), storeId);
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void uncertainStoreLookupFailurePreservesItsAccounts() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.failAfterStoreInsert = true;
        StoreTestData data = new StoreTestData(api.driver());
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
    public void uncertainStoreMustStillReferenceItsOwnedAccount() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.failAfterStoreInsert = true;
        StoreTestData data = new StoreTestData(api.driver());
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
    public void recoversWarehouseCreateCommittedBeforeLostResponse() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        String marker = "QA-ST-lost-create";
        Map<String, Object> values =
                StoreTestData.fields(
                        "notes",
                        marker,
                        "storeId",
                        store.id(),
                        "transactionType",
                        "ExchangeOrder",
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        Map<String, Object> stored = new LinkedHashMap<>(values);
        stored.put("id", "lost-create");
        api.records.put("lost-create", stored);
        data.rememberAttempts(
                List.of(
                        StoreTestData.fields(
                                "endpoint", "ItemTransactions-ExchangeOrder", "values", values)),
                List.of(),
                marker);
        data.close();
        Assert.assertEquals(api.deletes.get(0), "lost-create");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void uncertainWarehouseLookupFailureRetainsAllParents() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        Map<String, Object> values =
                StoreTestData.fields(
                        "notes",
                        "QA-ST-unresolved",
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        data.rememberAttempts(
                List.of(
                        StoreTestData.fields(
                                "endpoint", "ItemTransactions-ExchangeOrder", "values", values)),
                List.of(),
                "QA-ST-unresolved");
        api.failMovementLookup = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(item.id()));
        api.failMovementLookup = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void recoversUpdatedNoteAfterLostUpdateResponse() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        String marker = "QA-ST-lost-update";
        Map<String, Object> original =
                StoreTestData.fields(
                        "id",
                        "lost-update",
                        "notes",
                        marker,
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        api.records.put("lost-update", new LinkedHashMap<>(original));
        data.track("ItemTransactions-ExchangeOrder", "lost-update", marker, original);
        Map<String, Object> updated = new LinkedHashMap<>(original);
        updated.put("notes", marker + " EDIT");
        api.records.put("lost-update", updated);
        data.rememberAttempts(
                List.of(
                        StoreTestData.fields(
                                "endpoint", "ItemTransactions-ExchangeOrder", "values", updated)),
                List.of(),
                marker);
        data.close();
        Assert.assertEquals(api.deletes.get(0), "lost-update");
        Assert.assertTrue(api.records.isEmpty());
    }
}
