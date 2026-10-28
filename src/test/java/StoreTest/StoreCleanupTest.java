/* شرح الملف: اختبارات تنظيف المخازن: حذف بيانات التجربة والتحقق من غيابها.
 */
package StoreTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StoreCleanupTest {
    @Test
    public void fixtureCallWaitsForOriginalBranchToHydrateAfterNavigation() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        try (StoreTestData data = new StoreTestData(api.driver())) {
            api.missingBranchReads = 2;
            Assert.assertNotNull(data.store(1).id());
            Assert.assertEquals(api.missingBranchReads, 0);
        }
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void fixtureCallStillRejectsAnActualBranchChangeBeforeMutation() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        api.activeBranch = "another-branch";
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        Assert.assertTrue(api.records.isEmpty());
        api.activeBranch = "branch-test";
        data.close();
    }

    @Test
    public void fixtureCallStillRejectsAnActualOriginChangeBeforeMutation() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        api.activeOrigin = "https://another-tenant.invalid";
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        Assert.assertTrue(api.records.isEmpty());
        api.activeOrigin = "offline";
        data.close();
    }

    @Test
    public void postedInventoryFixtureCreatesOnlyTheStoreItsAuditNeeds() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        try (StoreTestData data = new StoreTestData(api.driver())) {
            var item = data.inventoryItem(1);
            var stores = (List<?>) item.values().get("stores");
            Assert.assertEquals(
                    stores.size(), 1, "Inventory audit must not retain an unused second store");
            var stock = (Map<?, ?>) stores.getFirst();
            Assert.assertEquals(stock.get("storeId"), data.store(1).id());
            Assert.assertEquals(stock.get("openingBalance"), 1000);
            Assert.assertEquals(
                    api.records.values().stream()
                            .filter(
                                    row ->
                                            String.valueOf(row.get("arabicName"))
                                                    .contains("store-2-"))
                            .count(),
                    0L);
        }
        Assert.assertTrue(api.records.isEmpty(), "Unposted fixture must remain fully cleanable");
    }

    @Test
    public void deletesOwnedMovementBeforeItsItemsAndStores() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        var movement =
                data.fixture(
                        "movement",
                        "ItemTransactions-ExchangeOrder",
                        StoreTestData.fields(
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
    public void resumesPendingStoreCreationFromDurableJournal() throws Exception {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.failAfterStoreInsert = true;
        WebDriver driver = api.driver();
        StoreTestData data = new StoreTestData(driver);
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        String name =
                api.records.values().stream()
                        .filter(r -> r.get("id").toString().startsWith("Stores-"))
                        .map(r -> r.get("arabicName").toString())
                        .findFirst()
                        .orElseThrow();
        StoreTestData.resume(driver, name.substring(name.length() - 12)).close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void retainsStockDependenciesWhenMovementDeletionFails() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        data.fixture(
                "movement",
                "ItemTransactions-ExchangeOrder",
                StoreTestData.fields(
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
    public void refusesTrackingUnownedRecord() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        Assert.expectThrows(
                AssertionError.class,
                () -> data.track("Items", "business-item", "Business record", Map.of()));
        data.close();
        Assert.assertTrue(api.deletes.isEmpty());
    }

    @Test
    public void cleansManufacturingWithoutNotesUsingOwnedStoreAndItems() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        Map<String, Object> values =
                StoreTestData.fields(
                        "id",
                        "owned-manufacturing",
                        "notes",
                        "",
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        api.records.put("owned-manufacturing", new LinkedHashMap<>(values));
        data.track("ManufacturingOrders", "owned-manufacturing", "QA-ST-manufacturing", values);
        data.close();
        Assert.assertEquals(api.deletes.get(0), "owned-manufacturing");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.missingDelete400 = true;
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record store = data.store(1);
        api.records.remove(store.id());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void rejectsMissingDeleteWhenOwnedRecordStillExists() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record unit = data.unit(1);
        api.missingDeleteForExisting = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.records.containsKey(unit.id()));
        api.missingDeleteForExisting = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void acceptsVerifiedMissingMovementDefaultDto() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.defaultDeletedDto = true;
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record record =
                data.fixture("transaction", "ManufacturingOrders", StoreTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }

    @Test
    public void acceptsVerifiedMissingMasterDefaultDto() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        api.defaultDeletedMasterDto = true;
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record record = data.fixture("item", "Items", StoreTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }
}
