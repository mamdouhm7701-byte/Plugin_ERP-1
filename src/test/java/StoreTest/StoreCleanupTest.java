package StoreTest;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StoreCleanupTest {
    @Test
    public void uncertainCreateStillRejectsUntrackedMovementInOwnedStore() {
        Api api = new Api();
        StoreTestData data = new StoreTestData(api.driver());
        var store = data.store(1);
        var item = data.item(1);
        var unit = data.unit(1);
        api.records.put("untracked", new LinkedHashMap<>(Map.of("id", "untracked",
                "storeId", store.id(), "transactionType", "Manufacturing", "notes", "",
                "lines", List.of(Map.of("itemId", "foreign-item", "unitId", unit.id())))));
        data.rememberAttempts(List.of(StoreTestData.fields("endpoint", "ManufacturingOrders",
                "values", StoreTestData.fields("storeId", store.id(), "notes", "",
                        "lines", List.of(Map.of("itemId", item.id(), "unitId", unit.id()))))),
                List.of(), "QA-ST-unresolved-manufacturing");
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        api.records.remove("untracked");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void uncertainCreateDoesNotConfuseEarlierOwnedMovementInSharedStore() {
        Api api = new Api();
        StoreTestData data = new StoreTestData(api.driver());
        var store = data.store(1);
        var first = data.item(1);
        var second = data.item(2);
        var unit = data.unit(1);
        var previous = data.fixture("previous-manufacturing", "ManufacturingOrders",
                StoreTestData.fields("storeId", store.id(), "transactionType", "Manufacturing",
                        "lines", List.of(Map.of("itemId", first.id(), "unitId", unit.id()))));
        var values = StoreTestData.fields("notes", "", "storeId", store.id(),
                "transactionType", "Manufacturing", "lines",
                List.of(Map.of("itemId", second.id(), "unitId", unit.id())));
        data.rememberAttempts(List.of(StoreTestData.fields("endpoint", "ManufacturingOrders",
                "values", values)), List.of(), "QA-ST-uncommitted-manufacturing");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(java.util.Collections.frequency(api.deletes, previous.id()), 1);
    }

    @Test
    @io.qameta.allure.Description("العميل المستخدم في البيانات دائم وخاضع للضريبة ويسمح بالبيع الآجل؛ صلاحية البيع النقدي لا تجعله عميلاً نقدياً.")
    public void normalCustomerIsTaxableAndKeepsItsOwnAccount() {
        Api api = new Api();
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

    private static final class Api {
        final Map<String, Map<String, Object>> records = new LinkedHashMap<>();
        final List<String> deletes = new ArrayList<>();
        int crashAtDelete;
        boolean timeOutStore;
        boolean commitBeforeTimeout;
        boolean rejectStore;
        boolean emptyDeletedResponse;
        boolean failAfterItemInsert;
        boolean failAfterStoreInsert;
        boolean failFixtureLookup;
        boolean defaultDeletedDto;
        boolean defaultDeletedMasterDto;

        boolean missingDelete400;
        boolean missingDeleteForExisting;
        boolean rejectBranchUnlink;
        boolean timeoutBranchUnlinkAfterCommit;
        boolean rejectMovement;
        boolean failMovementLookup;
        Map<String, Object> branch =
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "branch-test",
                                "defaultStoreId",
                                "existing-store",
                                "phone",
                                "original",
                                "stores",
                                new ArrayList<>(List.of(Map.of("storeId", "existing-store")))));

        WebDriver driver() {
            Object timeouts =
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebDriver.Timeouts.class},
                            (proxy, method, args) -> proxy);
            Object options =
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebDriver.Options.class},
                            (proxy, method, args) -> timeouts);
            return (WebDriver)
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                            (proxy, method, args) -> {
                                if (method.getName().equals("executeScript"))
                                    return ((String) args[0])
                                                    .startsWith("return document.querySelector")
                                            ? "branch-test"
                                            : true;
                                if (method.getName().equals("manage")) return options;
                                if (method.getName().equals("executeAsyncScript"))
                                    return request((Object[]) args[1]);
                                if (method.getName().equals("toString"))
                                    return "Fixture cleanup regression driver";
                                return null;
                            });
        }

        @SuppressWarnings("unchecked")
        Object request(Object[] args) {
            String action = (String) args[0], endpoint = (String) args[1];
            Map<String, Object> values = (Map<String, Object>) args[2];
            if (endpoint.equals("Branches")) {
                if (action.equals("get")) return Map.of("entity", new LinkedHashMap<>(branch));
                if (!action.equals("update"))
                    throw new AssertionError("Never delete an existing branch");
                boolean unlink =
                        ((List<?>) values.get("stores")).size()
                                < ((List<?>) branch.get("stores")).size();
                if (unlink && rejectBranchUnlink)
                    return Map.of("error", "Cannot unlink owned store", "status", 400);
                branch = new LinkedHashMap<>(values);
                if (unlink && timeoutBranchUnlinkAfterCommit) {
                    timeoutBranchUnlinkAfterCommit = false;
                    throw new TimeoutException("Lost unlink response after commit");
                }
                return Map.of("id", "branch-test");
            }
            if (action.equals("create")) {
                String id = endpoint + "-" + (records.size() + 1);
                Map<String, Object> saved = new LinkedHashMap<>(values);
                saved.put("id", id);
                records.put(id, saved);
                if (endpoint.equals("Items") && failAfterItemInsert)
                    return Map.of("error", "Failed after insert", "status", 500);
                if (endpoint.equals("Stores") && failAfterStoreInsert)
                    return Map.of("error", "Lost store create response", "status", 500);
                return Map.of("id", id);
            }
            if (action.equals("get") && endpoint.startsWith("Items/Summary"))
                return Map.of("entity", new ArrayList<>(records.values()));
            if (action.equals("get") && endpoint.startsWith("InventoryTransactions?")) {
                if (failMovementLookup) return Map.of("error", "Lookup unavailable", "status", 503);
                var rows = records.values().stream().filter(r -> r.containsKey("lines")).toList();
                return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
            }
            if (action.equals("get") && endpoint.contains("?")) {
                if (failFixtureLookup)
                    return Map.of("error", "Fixture lookup unavailable", "status", 503);
                var rows =
                        records.values().stream()
                                .filter(
                                        r ->
                                                r.containsKey("id")
                                                        && (r.containsKey("arabicName")
                                                                || r.containsKey("englishName")))
                                .toList();
                return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
            }
            String id = values.get("id").toString();
            if (action.equals("get")) {
                return records.containsKey(id)
                        ? Map.of("entity", records.get(id))
                        : defaultDeletedMasterDto
                                ? Map.of(
                                        "entity",
                                        Map.of("subUnits", List.of(), "type", "RawMaterial"))
                                : defaultDeletedDto
                                        ? Map.of("entity", Map.of("conversionRates", List.of()))
                                        : emptyDeletedResponse
                                                ? Map.of("entity", "")
                                                : Map.of(
                                                        "error",
                                                        "Missing owned record",
                                                        "status",
                                                        404);
            }
            deletes.add(id);
            if (crashAtDelete > 0 && deletes.size() == crashAtDelete)
                throw new Error("Simulated process interruption");
            if (endpoint.equals("ItemTransactions-ExchangeOrder") && rejectMovement)
                return Map.of("error", "Movement is linked", "status", 400);
            if (missingDeleteForExisting || (missingDelete400 && !records.containsKey(id)))
                return Map.of("error", "Common.ObjectNotFound", "status", 400);
            if (endpoint.equals("Stores")) {
                if (rejectStore) return Map.of("error", "Record is in use", "status", 400);
                if (timeOutStore) {
                    timeOutStore = false;
                    if (commitBeforeTimeout) records.remove(id);
                    throw new TimeoutException("Simulated lost DELETE response");
                }
            }
            records.remove(id);
            return Map.of("ok", true);
        }
    }

    @Test
    public void deletesOwnedMovementBeforeItsItemsAndStores() {
        Api api = new Api();
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
    public void recoversStoreCommittedBeforeLostCreateResponse() {
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
    public void resumesPendingStoreCreationFromDurableJournal() throws Exception {
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
        StoreTestData data = new StoreTestData(api.driver());
        Assert.expectThrows(
                AssertionError.class,
                () -> data.track("Items", "business-item", "Business record", Map.of()));
        data.close();
        Assert.assertTrue(api.deletes.isEmpty());
    }

    @Test
    public void checksIdentityBeforeFirstDeletion() {
        Api api = new Api();
        StoreTestData data = new StoreTestData(api.driver());
        var unit = data.unit(1);
        api.records.put(
                unit.id().toString(),
                new LinkedHashMap<>(
                        Map.of("id", unit.id(), "arabicName", "Different business record")));
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        api.records.put(
                unit.id().toString(),
                new LinkedHashMap<>(Map.of("id", unit.id(), "arabicName", unit.name())));
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void cleansManufacturingWithoutNotesUsingOwnedStoreAndItems() {
        Api api = new Api();
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
    public void rejectsManufacturingWithChangedStoreIdentity() {
        manufacturingIdentityMismatch(true);
    }

    @Test
    public void rejectsManufacturingWithChangedItemIdentity() {
        manufacturingIdentityMismatch(false);
    }

    private void manufacturingIdentityMismatch(boolean storeChanged) {
        Api api = new Api();
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
        data.track("ManufacturingOrders", "owned-manufacturing", "QA-ST-manufacturing", values);
        Map<String, Object> changed = new LinkedHashMap<>(values);
        if (storeChanged) changed.put("storeId", "business-store");
        else
            changed.put(
                    "lines",
                    List.of(Map.of("itemId", "business-item", "unitId", data.unit(1).id())));
        api.records.put("owned-manufacturing", changed);
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertFalse(api.deletes.contains("owned-manufacturing"));
        Assert.assertTrue(api.records.containsKey(item.id()));
        api.records.put("owned-manufacturing", values);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void recoversWarehouseCreateCommittedBeforeLostResponse() {
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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

    @Test
    public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence() {
        Api api = new Api();
        api.missingDelete400 = true;
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record store = data.store(1);
        api.records.remove(store.id());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void rejectsMissingDeleteWhenOwnedRecordStillExists() {
        Api api = new Api();
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
    public void recoversUiItemCommittedBeforeFailedResponse() {
        Api api = new Api();
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
        Api api = new Api();
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
    public void removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges() {
        Api api = new Api();
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
        Api api = new Api();
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
    public void verifiesCommittedBranchUnlinkAfterLostResponse() {
        Api api = new Api();
        StoreTestData data = new StoreTestData(api.driver());
        data.store(1);
        api.timeoutBranchUnlinkAfterCommit = true;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(((List<?>) api.branch.get("stores")).size(), 1);
    }

    @Test
    public void retriesUncommittedDeleteBeforeParentAccounts() {
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
    public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords() {
        Api api = new Api();
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
    public void cleansOwnedItemCommittedBeforeCreateFailure() {
        Api api = new Api();
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
    public void acceptsVerifiedMissingMovementDefaultDto() {
        Api api = new Api();
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
        Api api = new Api();
        api.defaultDeletedMasterDto = true;
        StoreTestData data = new StoreTestData(api.driver());
        StoreTestData.Record record = data.fixture("item", "Items", StoreTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }

    @Test
    public void updatedRecordDeletesBeforeItsNewReference() {
        Api api = new Api();
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
        Api api = new Api();
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

    @Test
    @io.qameta.allure.Story("استكمال الحذف بعد انقطاع التشغيل من السجلات المتبقية فقط")
    public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception {
        Api api = new Api();
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

    @Test
    @io.qameta.allure.Story(
            "دورة الصفحة الفعلية تحتفظ ببيانات الحالات وتغلق النوافذ وتنظف المجموعة مع فشل الخطوة الأخيرة")
    public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception {

        Api api = new Api();
        try (var browser = new TestSupport.FixtureBrowser(api.driver())) {
            var marker = new java.util.concurrent.atomic.AtomicReference<String>();
            var broken = new java.util.concurrent.atomic.AtomicBoolean();
            var fixturePage =
                    new Plugin_ERP.System_Modules.Login.StoreModel.StoreModulePage(browser.driver) {
                        @Override
                        public String smCaptureMarker() {
                            return marker.get();
                        }

                        @Override
                        public List<Map<String, Object>> smSaves() {
                            return List.of();
                        }

                        @Override
                        public List<Map<String, Object>> smAttempts() {
                            return List.of();
                        }

                        @Override
                        public void smSettled() {
                            if (broken.get())
                                throw new org.openqa.selenium.WebDriverException(
                                        "Lost browser at page end");
                        }

                        @Override
                        public String smDiagnosticMarkup() {
                            return "<case-failed/>";
                        }

                        @Override
                        public String smDiagnostics() {
                            return "{}";
                        }
                    };
            var harness =
                    new StoreScenarioTest() {
                        protected StoreMovementKind kind() {
                            return StoreMovementKind.TRANSFER;
                        }

                        protected Plugin_ERP.System_Modules.Login.StoreModel.StoreModulePage
                                movementPage() {
                            return fixturePage;
                        }
                    };
            harness.beginStoreScenario();
            marker.set(harness.marker);
            var data = harness.data;
            var first = data.item(1);
            browser.addAuxiliaryWindow();
            harness.cleanStoreScenario(null);
            harness.beginStoreScenario();
            marker.set(harness.marker);
            Assert.assertSame(harness.data, data);
            var second = data.item(1);
            browser.addAuxiliaryWindow();
            var failed =
                    (org.testng.ITestResult)
                            Proxy.newProxyInstance(
                                    getClass().getClassLoader(),
                                    new Class<?>[] {org.testng.ITestResult.class},
                                    (p, m, a) -> m.getName().equals("isSuccess") ? false : null);
            harness.cleanStoreScenario(failed);
            Assert.assertTrue(api.records.containsKey(first.id()));
            Assert.assertTrue(api.records.containsKey(second.id()));
            Assert.assertTrue(api.deletes.isEmpty());
            Assert.assertEquals(browser.closed, 2);
            broken.set(true);
            Assert.expectThrows(AssertionError.class, harness::retryStoreCleanup);
            Assert.assertTrue(api.records.isEmpty());
            Assert.assertNull(harness.data);
        }
    }
}
