/* شرح الملف: اختبارات تنظيف الحركات المالية: استكمال التنظيف بعد انقطاع أو استجابة غير محسومة.
 */
package FinancialMovementsTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FinancialRecoveryCleanupTest {
    @Test
    public void identifiedCollectionUpdateCanRecoverWithoutDetailBranchMetadata() {
        verifyPendingUpdateRecovery(false, false, true);
    }

    @Test
    public void retriesUncommittedDeleteBeforeParentAccounts() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        api.timeOutBank = true;
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record bank = data.bank(1);
        Object accountId = bank.values().get("accountId");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.subList(0, 2), List.of(bank.id(), bank.id()));
        Assert.assertEquals(api.deletes.get(2), accountId);
    }

    @Test
    public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        api.timeOutBank = true;
        api.commitBeforeTimeout = true;
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record bank = data.bank(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(bank.id()::equals).count(), 1L);
    }

    @Test
    public void acceptsEmptyStringReadAfterCommittedDeleteTimeout() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        api.timeOutBank = true;
        api.commitBeforeTimeout = true;
        api.emptyDeletedResponse = true;
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record bank = data.bank(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(bank.id()::equals).count(), 1L);
    }

    @Test
    public void cleansOwnedItemCommittedBeforeCreateFailure() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        api.failAfterItemInsert = true;
        api.records.put(
                "shared-item",
                new LinkedHashMap<>(Map.of("id", "shared-item", "arabicName", "Existing item")));
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record unit = data.fixture("unit", "Units", FinancialTestData.fields());
        FinancialTestData.Record category =
                data.fixture("category", "ItemCategories", FinancialTestData.fields());
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        data.fixture(
                                "item",
                                "Items",
                                FinancialTestData.fields(
                                        "mainUnitId", unit.id(), "categoryId", category.id())));
        Assert.assertEquals(api.records.size(), 4);
        data.close();
        Assert.assertTrue(api.deletes.get(0).startsWith("Items-"));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-item"));
    }

    @Test
    @io.qameta.allure.Story("استكمال الحذف بعد انقطاع التشغيل من السجلات المتبقية فقط")
    public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        var data = new FinancialTestData(api.driver());
        var first = data.bank(1);
        var last = data.safe(2);
        api.crashAtDelete = 2;
        Assert.expectThrows(Error.class, data::close);
        String suffix = last.name().substring(last.name().length() - 12);
        var rows =
                new org.json.JSONArray(
                        java.nio.file.Files.readString(
                                java.nio.file.Path.of(
                                        "target", "financial-fixtures", suffix + ".json")));
        Assert.assertTrue(
                rows.toList().stream().noneMatch(v -> last.id().equals(((Map<?, ?>) v).get("id"))));
        api.crashAtDelete = 0;
        FinancialTestData.resume(api.driver(), suffix).close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(java.util.Collections.frequency(api.deletes, last.id()), 1);
    }

    @Test
    @io.qameta.allure.Story(
            "الاحتفاظ بمحاولة الإنشاء عند فقد الاستجابة واستعادتها بعد إعادة التشغيل دون حذف المتطلبات مبكراً")
    public void resumesUncertainCreateBeforeDeletingItsDependencies() throws Exception {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        var data = new FinancialTestData(api.driver());
        var account = data.account("retained");
        api.failAfterUnitInsert = true;
        Assert.expectThrows(
                AssertionError.class,
                () -> data.fixture("invoice-unit", "Units", FinancialTestData.fields()));
        api.failPendingLookup = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(account.id()));
        api.failPendingLookup = false;
        api.failAfterUnitInsert = false;
        FinancialTestData.resume(api.driver(), data.journalSuffix()).close();
        Assert.assertTrue(api.records.isEmpty());
    }

    private void verifyPendingUpdateRecovery(
            boolean changedDealer, boolean changedBranch, boolean allowed) {
        verifyPendingUpdateRecovery(changedDealer, changedBranch, allowed, "receipt-owned");
    }

    private void verifyPendingUpdateRecovery(
            boolean changedDealer, boolean changedBranch, boolean allowed, String intendedId) {
        verifyPendingUpdateRecovery(changedDealer, changedBranch, allowed, intendedId, false);
    }

    private void verifyPendingUpdateRecovery(
            boolean changedDealer,
            boolean changedBranch,
            boolean allowed,
            String intendedId,
            boolean includeBranch) {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        String name = "QA-FM-recovery-check";
        var pending =
                new TestSupport.PendingFixtureWrites(
                        new TestSupport.FixtureSession(api.driver()),
                        "financial",
                        "local-" + java.util.UUID.randomUUID());
        var values =
                new LinkedHashMap<String, Object>(
                        Map.of(
                                "branchId",
                                "branch-test",
                                "dealerId",
                                "customer-owned",
                                "notes",
                                name));
        if (intendedId != null) values.put("id", intendedId);
        var stored = new LinkedHashMap<String, Object>(values);
        stored.put("id", "receipt-owned");
        stored.remove("branchId");
        if (includeBranch) stored.put("branchId", "branch-test");
        if (changedDealer) stored.put("dealerId", "customer-other");
        if (changedBranch) stored.put("branchId", "branch-other");
        pending.start("Collections", name, values);
        var recovered = new ArrayList<Object>();
        Runnable action =
                () ->
                        pending.recover(
                                (endpoint, query) ->
                                        Map.of(
                                                "entity",
                                                endpoint.contains("?") ? List.of(stored) : stored),
                                (endpoint, id, marker, fields) -> recovered.add(id));
        try {
            if (allowed) {
                action.run();
                Assert.assertEquals(recovered, List.of("receipt-owned"));
            } else {
                Assert.expectThrows(AssertionError.class, action::run);
                Assert.assertTrue(recovered.isEmpty());
            }
        } finally {
            pending.committed("Collections", name);
        }
    }
}
