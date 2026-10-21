package ProductsTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsCleanupTest {


    @Test
    public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.missingDelete400 = true;
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record store = data.store(1);
        api.records.remove(store.id());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void rejectsMissingDeleteWhenOwnedRecordStillExists() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record unit = data.unit(1);
        api.missingDeleteForExisting = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.records.containsKey(unit.id()));
        api.missingDeleteForExisting = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

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
    public void acceptsVerifiedMissingProductDefaultDto() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.defaultDeletedDto = true;
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record record =
                data.fixture("transaction", "PriceLists", ProductsTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }

    @Test
    public void acceptsVerifiedMissingMasterDefaultDto() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.defaultDeletedMasterDto = true;
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record record = data.fixture("item", "Items", ProductsTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
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

    @Test
    @io.qameta.allure.Story("رفض استرجاع أو حذف سجلات المجموعة من فرع آخر")
    public void refusesRecoveryInAnotherBranch() throws Exception {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        var data = new ProductsTestData(api.driver());
        data.unit(1);
        api.activeBranch = "another-branch";
        Assert.expectThrows(
                AssertionError.class,
                () -> ProductsTestData.resume(api.driver(), data.journalSuffix()));
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        api.activeBranch = "branch-test";
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Story(
            "دورة الصفحة الفعلية تحتفظ ببيانات الحالات وتغلق النوافذ وتنظف المجموعة مع فشل الخطوة الأخيرة")
    public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception {

        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        try (var browser = new TestSupport.FixtureBrowser(api.driver())) {
            var marker = new java.util.concurrent.atomic.AtomicReference<String>();
            var broken = new java.util.concurrent.atomic.AtomicBoolean();
            var fixturePage =
                    new Plugin_ERP.System_Modules.Login.Products.ProductsModulePage(
                            browser.driver) {
                        @Override
                        public String prCaptureMarker() {
                            return marker.get();
                        }

                        @Override
                        public List<Map<String, Object>> prSaves() {
                            return List.of();
                        }

                        @Override
                        public List<Map<String, Object>> prAttempts() {
                            return List.of();
                        }

                        @Override
                        public void prSettled() {
                            if (broken.get())
                                throw new org.openqa.selenium.WebDriverException(
                                        "Lost browser at page end");
                        }
                    };
            var harness =
                    new ProductScenarioTest() {
                        protected Kind kind() {
                            return Kind.ITEM;
                        }

                        protected Plugin_ERP.System_Modules.Login.Products.ProductsModulePage
                                modulePage() {
                            return fixturePage;
                        }
                    };
            browser.bind(harness);
            harness.newProductScenario();
            marker.set(harness.marker);
            var data = harness.data;
            var first = data.item(1);
            browser.addAuxiliaryWindow();
            harness.cleanProductScenario();
            harness.newProductScenario();
            marker.set(harness.marker);
            Assert.assertSame(harness.data, data);
            var second = data.item(1);
            browser.addAuxiliaryWindow();
            harness.cleanProductScenario();
            Assert.assertTrue(api.records.containsKey(first.id()));
            Assert.assertTrue(api.records.containsKey(second.id()));
            Assert.assertTrue(api.deletes.isEmpty());
            Assert.assertEquals(browser.closed, 2);
            broken.set(true);
            Assert.expectThrows(AssertionError.class, harness::retryProductCleanup);
            Assert.assertTrue(api.records.isEmpty());
            Assert.assertNull(harness.data);
        }
    }
}
