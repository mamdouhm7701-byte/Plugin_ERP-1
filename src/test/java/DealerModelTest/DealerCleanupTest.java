package DealerModelTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DealerCleanupTest {


    @Test
    public void retriesUncommittedDeleteBeforeParentAccounts() {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        api.timeOutStore = true;
        DealerTestData data = new DealerTestData(api.driver());
        DealerTestData.Record store = data.store();
        Object accountId = store.values().get("accountId");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.subList(0, 2), List.of(store.id(), store.id()));
        Assert.assertEquals(api.deletes.get(2), accountId);
    }

    @Test
    public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted() {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        DealerTestData data = new DealerTestData(api.driver());
        DealerTestData.Record store = data.store();
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }

    @Test
    public void acceptsEmptyStringReadAfterCommittedDeleteTimeout() {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        api.emptyDeletedResponse = true;
        DealerTestData data = new DealerTestData(api.driver());
        DealerTestData.Record store = data.store();
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }

    @Test
    public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords() {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        api.rejectStore = true;
        api.records.put(
                "shared-record",
                new LinkedHashMap<>(Map.of("arabicName", "Existing business data")));
        DealerTestData data = new DealerTestData(api.driver());
        DealerTestData.Record store = data.store();
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertEquals(api.deletes, List.of(store.id()));
        Assert.assertEquals(api.records.size(), 4);
        api.rejectStore = false;
        data.close();
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-record"));
    }

    @Test
    @io.qameta.allure.Story("مشاركة المراجع الثابتة بالصفحة وعزل بيانات كل حالة دون حذف بينها")
    public void pageScopeSharesOnlyStableReferences() {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        var data = new DealerTestData(api.driver());
        data.beginScenario();
        var stable = data.area();
        var first = data.item(1);
        var account = data.account();
        data.beginScenario();
        var second = data.item(1);
        Assert.assertNotEquals(first.id(), second.id());
        Assert.assertNotEquals(
                first.values().get("mainUnitBarcode"), second.values().get("mainUnitBarcode"));
        Assert.assertNotEquals(account.id(), data.account().id());
        Assert.assertEquals(data.area().id(), stable.id());
        Assert.assertTrue(api.records.containsKey(first.id()));
        Assert.assertTrue(api.deletes.isEmpty());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Story("استكمال الحذف بعد انقطاع التشغيل من السجلات المتبقية فقط")
    public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        var data = new DealerTestData(api.driver());
        var first = data.area();
        var last = data.unit();
        api.crashAtDelete = 2;
        Assert.expectThrows(Error.class, data::close);
        String suffix = last.name().substring(last.name().length() - 12);
        var rows =
                new org.json.JSONArray(
                        java.nio.file.Files.readString(
                                java.nio.file.Path.of(
                                        "target", "dealer-fixtures", suffix + ".json")));
        Assert.assertTrue(
                rows.toList().stream().noneMatch(v -> last.id().equals(((Map<?, ?>) v).get("id"))));
        api.crashAtDelete = 0;
        DealerTestData.resume(api.driver(), suffix).close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(java.util.Collections.frequency(api.deletes, last.id()), 1);
    }

    @Test
    @io.qameta.allure.Story(
            "الاحتفاظ بمحاولة الإنشاء عند فقد الاستجابة واستعادتها بعد إعادة التشغيل دون حذف المتطلبات مبكراً")
    public void resumesUncertainCreateBeforeDeletingItsDependencies() throws Exception {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        var data = new DealerTestData(api.driver());
        var account = data.account();
        api.failAfterUnitInsert = true;
        Assert.expectThrows(AssertionError.class, () -> data.unit());
        api.failPendingLookup = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(account.id()));
        api.failPendingLookup = false;
        api.failAfterUnitInsert = false;
        DealerTestData.resume(api.driver(), data.journalSuffix()).close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Story("رفض استرجاع أو حذف سجلات المجموعة من فرع آخر")
    public void refusesRecoveryInAnotherBranch() throws Exception {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        var data = new DealerTestData(api.driver());
        data.unit();
        api.activeBranch = "another-branch";
        Assert.expectThrows(
                AssertionError.class,
                () -> DealerTestData.resume(api.driver(), data.journalSuffix()));
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
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        try (var browser = new TestSupport.FixtureBrowser(api.driver())) {
            var page = new Plugin_ERP.System_Modules.Login.AbstractForDealer(browser.driver);
            var harness = new DealerScenarioTest();
            browser.bind(harness);
            harness.beginDealerCase();
            var data = harness.data();
            var first = data.account();
            String one = harness.ownedName(page, Plugin_ERP.Enums.Pages.CUSTOMERS, "FIRST");
            api.records.put(
                    "Customers-owned-1",
                    new LinkedHashMap<>(
                            Map.of(
                                    "id",
                                    "Customers-owned-1",
                                    "arabicName",
                                    one,
                                    "accountId",
                                    first.id())));
            browser.addAuxiliaryWindow();
            harness.cleanScenarioRecords();
            harness.beginDealerCase();
            Assert.assertSame(harness.data(), data);
            var second = data.account();
            String two = harness.ownedName(page, Plugin_ERP.Enums.Pages.CUSTOMERS, "SECOND");
            api.records.put(
                    "Customers-owned-2",
                    new LinkedHashMap<>(
                            Map.of(
                                    "id",
                                    "Customers-owned-2",
                                    "arabicName",
                                    two,
                                    "accountId",
                                    second.id())));
            browser.addAuxiliaryWindow();
            harness.cleanScenarioRecords();
            Assert.assertTrue(api.records.containsKey(first.id()));
            Assert.assertTrue(api.records.containsKey(second.id()));
            Assert.assertTrue(api.deletes.isEmpty());
            Assert.assertEquals(browser.closed, 2);
            api.rejectDealer = true;
            Assert.expectThrows(AssertionError.class, harness::retryRemainingScenarioCleanup);
            Assert.assertTrue(api.deletes.stream().noneMatch(id -> id.startsWith("Accounts-")));
            Assert.assertTrue(api.records.containsKey(first.id()));
            api.rejectDealer = false;
            harness.retryRemainingScenarioCleanup();
            Assert.assertTrue(api.records.isEmpty());
            Assert.assertTrue(
                    api.deletes.indexOf(first.id()) > api.deletes.indexOf("Customers-owned-1"));
        }
    }

    @Test
    @io.qameta.allure.Story("تنظيف المتعامل المملوك من الصفحات التالية مع حماية سجلات المستخدم")
    public void cleanupFindsOwnedDealerBeyondFirstListPage() {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        var data = new DealerTestData(api.driver());
        api.records.put(
                "Customers-user-1",
                new LinkedHashMap<>(Map.of("id", "Customers-user-1", "arabicName", "User one")));
        api.records.put(
                "Customers-user-2",
                new LinkedHashMap<>(Map.of("id", "Customers-user-2", "arabicName", "User two")));
        api.records.put(
                "Customers-owned",
                new LinkedHashMap<>(
                        Map.of("id", "Customers-owned", "arabicName", "QA-hidden-page")));
        data.deleteOwnedDealer(Plugin_ERP.Enums.Pages.CUSTOMERS, "QA-hidden-page");
        data.close();
        Assert.assertEquals(api.deletes, List.of("Customers-owned"));
        Assert.assertEquals(api.records.size(), 2);
    }

    @Test
    @io.qameta.allure.Story("رفض حذف المتعامل عند تكرار علامة الملكية بدلاً من اختيار أول صف")
    public void ambiguousDealerNameStopsCleanup() {
        DealerFixtureApiStub api = new DealerFixtureApiStub();
        var data = new DealerTestData(api.driver());
        api.records.put(
                "Customers-owned-1",
                new LinkedHashMap<>(
                        Map.of("id", "Customers-owned-1", "arabicName", "QA-duplicate-owned")));
        api.records.put(
                "Customers-owned-2",
                new LinkedHashMap<>(
                        Map.of("id", "Customers-owned-2", "arabicName", "QA-duplicate-owned")));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        data.deleteOwnedDealer(
                                Plugin_ERP.Enums.Pages.CUSTOMERS, "QA-duplicate-owned"));
        Assert.assertTrue(api.deletes.isEmpty());
        data.close();
        Assert.assertEquals(api.records.size(), 2);
    }
}
