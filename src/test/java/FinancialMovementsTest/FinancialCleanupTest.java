/* شرح الملف: مراجعة تنظيف البيانات التي أنشأتها التجربة فقط، ضمن الحركات المالية.
 */
package FinancialMovementsTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FinancialCleanupTest {
    @Test
    public void identifiedCollectionUpdateCanRecoverWithoutDetailBranchMetadata() {
        verifyPendingUpdateRecovery(false, false, true);
    }

    @Test
    public void pendingUpdateRejectsDifferentDealer() {
        verifyPendingUpdateRecovery(true, false, false);
    }

    @Test
    public void pendingUpdateRejectsDifferentBranch() {
        verifyPendingUpdateRecovery(false, true, false);
    }

    @Test
    public void pendingCreateStillRequiresBranchEvidence() {
        verifyPendingUpdateRecovery(false, false, false, null);
    }

    @Test
    public void pendingUpdateRejectsDifferentDocumentIdentity() {
        verifyPendingUpdateRecovery(false, false, false, "receipt-other");
    }

    @Test
    public void pendingUpdateRejectsDifferentIdentityEvenWhenBranchMatches() {
        verifyPendingUpdateRecovery(false, false, false, "receipt-other", true);
    }

    @Test
    public void newFormZeroIdentityCanRecoverWithFullBranchEvidence() {
        verifyPendingUpdateRecovery(false, false, true, "0", true);
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

    @Test
    @io.qameta.allure.Description(
            "العميل المستخدم في البيانات دائم وخاضع للضريبة ويسمح بالبيع الآجل؛ صلاحية البيع النقدي لا تجعله عميلاً نقدياً.")
    public void normalCustomerIsTaxableAndKeepsItsOwnAccount() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        try (var data = new FinancialTestData(api.driver())) {
            var customer = data.customer(1);
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
    public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        api.missingDelete400 = true;
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record bank = data.bank(1);
        api.records.remove(bank.id());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
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
    public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        api.rejectBank = true;
        api.records.put(
                "shared-record",
                new LinkedHashMap<>(Map.of("arabicName", "Existing business data")));
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record bank = data.bank(1);
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertEquals(api.deletes, List.of(bank.id()));
        Assert.assertEquals(api.records.size(), 4);
        api.rejectBank = false;
        data.close();
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-record"));
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
    public void acceptsVerifiedMissingTransactionDefaultDto() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        api.defaultDeletedDto = true;
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record record =
                data.fixture("transaction", "Collections", FinancialTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }

    @Test
    public void acceptsVerifiedMissingMasterDefaultDto() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        api.defaultDeletedMasterDto = true;
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record record = data.fixture("item", "Items", FinancialTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }

    @Test
    public void updatedMovementDeletesBeforeItsNewReference() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record original = data.bank(1);
        FinancialTestData.Record movement =
                data.fixture(
                        "movement",
                        "Collections",
                        FinancialTestData.fields("bankId", original.id()));
        FinancialTestData.Record replacement = data.bank(2);
        Map<String, Object> updated = new LinkedHashMap<>(movement.values());
        updated.put("bankId", replacement.id());
        api.records.put(movement.id().toString(), updated);
        data.track(movement.endpoint(), movement.id(), movement.name(), updated);
        data.close();
        Assert.assertEquals(api.deletes.get(0), movement.id());
        Assert.assertTrue(
                api.deletes.indexOf(movement.id()) < api.deletes.indexOf(replacement.id()));
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void confirmsCustomerAbsenceWithoutHidingExistingRecordErrors() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        api.customerDetail500 = true;
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record customer = data.customer(1);
        Assert.expectThrows(AssertionError.class, () -> data.read(customer));
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.get(0), customer.id());
    }

    @Test
    @io.qameta.allure.Story("مشاركة المراجع الثابتة بالصفحة وعزل بيانات كل حالة دون حذف بينها")
    public void pageScopeSharesOnlyStableReferences() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        var data = new FinancialTestData(api.driver());
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        data.track(
                                "Collections", "business-collection", "Business record", Map.of()));
        data.beginScenario();
        var stable = data.fixture("invoice-unit", "Units", FinancialTestData.fields());
        var first = data.customer(1);
        var bank = data.bank(1);
        var safe = data.safe(1);
        var payment = data.payment();
        data.beginScenario();
        var second = data.customer(1);
        Assert.assertNotEquals(first.id(), second.id());
        Assert.assertNotEquals(first.values().get("accountId"), second.values().get("accountId"));
        Assert.assertNotEquals(bank.id(), data.bank(1).id());
        Assert.assertNotEquals(safe.id(), data.safe(1).id());
        Assert.assertNotEquals(payment.id(), data.payment().id());
        Assert.assertEquals(
                data.fixture("invoice-unit", "Units", FinancialTestData.fields()).id(),
                stable.id());
        Assert.assertTrue(api.records.containsKey(first.id()));
        Assert.assertTrue(api.deletes.isEmpty());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
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

    @Test
    @io.qameta.allure.Story("رفض استرجاع أو حذف سجلات المجموعة من فرع آخر")
    public void refusesRecoveryInAnotherBranch() throws Exception {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        var data = new FinancialTestData(api.driver());
        data.fixture("invoice-unit", "Units", FinancialTestData.fields());
        api.activeBranch = "another-branch";
        Assert.expectThrows(
                AssertionError.class,
                () -> FinancialTestData.resume(api.driver(), data.journalSuffix()));
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

        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        try (var browser = new TestSupport.FixtureBrowser(api.driver())) {
            var marker = new java.util.concurrent.atomic.AtomicReference<String>();
            var broken = new java.util.concurrent.atomic.AtomicBoolean();
            var fixturePage =
                    new Plugin_ERP.System_Modules.Login.FinancialMovements.FinancialMovementPage(
                            browser.driver) {
                        @Override
                        public String captureMarker() {
                            if (broken.get())
                                throw new org.openqa.selenium.WebDriverException(
                                        "Lost browser at page end");
                            return marker.get();
                        }

                        @Override
                        public List<Map<String, Object>> capturedSaves() {
                            return List.of();
                        }

                        @Override
                        public List<Map<String, Object>> capturedAttempts() {
                            return List.of();
                        }

                        @Override
                        public void awaitSavesSettled() {}
                    };
            var harness =
                    new FinancialScenarioTest() {
                        protected Plugin_ERP.System_Modules.Login.FinancialMovements
                                        .FinancialMovementKind
                                kind() {
                            return Plugin_ERP.System_Modules.Login.FinancialMovements
                                    .FinancialMovementKind.RECEIPT;
                        }

                        protected Plugin_ERP.System_Modules.Login.FinancialMovements
                                        .FinancialMovementPage
                                movementPage() {
                            return fixturePage;
                        }
                    };
            browser.bind(harness);
            harness.newFinancialScenario();
            marker.set(harness.marker);
            var data = harness.data;
            var first = data.customer(1);
            browser.addAuxiliaryWindow();
            harness.cleanFinancialScenario();
            harness.newFinancialScenario();
            marker.set(harness.marker);
            Assert.assertSame(harness.data, data);
            var second = data.customer(1);
            browser.addAuxiliaryWindow();
            harness.cleanFinancialScenario();
            Assert.assertTrue(api.records.containsKey(first.id()));
            Assert.assertTrue(api.records.containsKey(second.id()));
            Assert.assertTrue(api.deletes.isEmpty());
            Assert.assertEquals(browser.closed, 2);
            broken.set(true);
            Assert.expectThrows(AssertionError.class, harness::retryFinancialCleanup);
            Assert.assertTrue(api.records.isEmpty());
            Assert.assertNull(harness.data);
        }
    }

    @Test
    @io.qameta.allure.Story(
            "انتظار عودة نفس الفرع بعد إعادة تحميل الشاشة مع استمرار رفض الفرع المختلف")
    public void waitsForBranchHydrationAfterClosingTheForm() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        var data = new FinancialTestData(api.driver());
        var bank = data.bank(1);
        api.branchUnavailableReads = 2;
        Assert.assertEquals(data.read(bank).get("id"), bank.id());
        Assert.assertTrue(api.deletes.isEmpty());
        api.activeBranch = "another-branch";
        Assert.expectThrows(AssertionError.class, () -> data.read(bank));
        Assert.assertTrue(api.deletes.isEmpty());
        api.activeBranch = "branch-test";
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
