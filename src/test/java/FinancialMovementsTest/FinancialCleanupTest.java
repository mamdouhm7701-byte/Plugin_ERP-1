package FinancialMovementsTest;

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

    private void verifyPendingUpdateRecovery(boolean changedDealer, boolean changedBranch, boolean allowed) {
        verifyPendingUpdateRecovery(changedDealer, changedBranch, allowed, "receipt-owned");
    }

    private void verifyPendingUpdateRecovery(boolean changedDealer, boolean changedBranch, boolean allowed, String intendedId) {
        verifyPendingUpdateRecovery(changedDealer, changedBranch, allowed, intendedId, false);
    }

    private void verifyPendingUpdateRecovery(boolean changedDealer, boolean changedBranch, boolean allowed, String intendedId, boolean includeBranch) {
        Api api = new Api();
        String name = "QA-FM-recovery-check";
        var pending = new TestSupport.PendingFixtureWrites(
                new TestSupport.FixtureSession(api.driver()), "financial", "local-" + java.util.UUID.randomUUID());
        var values = new LinkedHashMap<String, Object>(Map.of("branchId", "branch-test",
                "dealerId", "customer-owned", "notes", name));
        if (intendedId != null) values.put("id", intendedId);
        var stored = new LinkedHashMap<String, Object>(values);
        stored.put("id", "receipt-owned");
        stored.remove("branchId");
        if (includeBranch) stored.put("branchId", "branch-test");
        if (changedDealer) stored.put("dealerId", "customer-other");
        if (changedBranch) stored.put("branchId", "branch-other");
        pending.start("Collections", name, values);
        var recovered = new ArrayList<Object>();
        Runnable action = () -> pending.recover((endpoint, query) ->
                Map.of("entity", endpoint.contains("?") ? List.of(stored) : stored),
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
    @io.qameta.allure.Description("العميل المستخدم في البيانات دائم وخاضع للضريبة ويسمح بالبيع الآجل؛ صلاحية البيع النقدي لا تجعله عميلاً نقدياً.")
    public void normalCustomerIsTaxableAndKeepsItsOwnAccount() {
        Api api = new Api();
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

    private static final class Api {
        final Map<String, Map<String, Object>> records = new LinkedHashMap<>();
        final List<String> deletes = new ArrayList<>();
        int crashAtDelete;
        boolean failAfterUnitInsert, failPendingLookup;
        String activeBranch = "branch-test";
        int branchUnavailableReads;
        boolean timeOutBank;
        boolean commitBeforeTimeout;
        boolean rejectBank;
        boolean emptyDeletedResponse;
        boolean failAfterItemInsert;
        boolean defaultDeletedDto;
        boolean defaultDeletedMasterDto;
        boolean customerDetail500;
        boolean missingDelete400;

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
                                if (method.getName().equals("executeScript")) {
                                    String code = (String) args[0];
                                    if (code.equals("return location.origin")) return "offline";
                                    if (code.startsWith("return document.querySelector")) {
                                        if (branchUnavailableReads > 0) {
                                            branchUnavailableReads--;
                                            return null;
                                        }
                                        return activeBranch;
                                    }
                                    return true;
                                }
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
            if (action.equals("create")) {
                String id = endpoint + "-" + (records.size() + 1);
                Map<String, Object> saved = new LinkedHashMap<>(values);
                saved.put("id", id);
                records.put(id, saved);
                if (endpoint.equals("Units") && failAfterUnitInsert)
                    return Map.of("error", "Lost unit create response", "status", 500);
                if (endpoint.equals("Items") && failAfterItemInsert)
                    return Map.of("error", "Failed after insert", "status", 500);
                return Map.of("id", id);
            }
            if (action.equals("get") && endpoint.startsWith("Items/Summary"))
                return Map.of("entity", new ArrayList<>(records.values()));
            if (action.equals("get") && endpoint.startsWith("Customers?")) {
                List<Map<String, Object>> rows =
                        records.entrySet().stream()
                                .filter(e -> e.getKey().startsWith("Customers-"))
                                .map(Map.Entry::getValue)
                                .toList();
                return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
            }
            if (action.equals("get") && endpoint.startsWith("Units?")) {
                if (failPendingLookup)
                    return Map.of("error", "Recovery lookup unavailable", "status", 503);
                var rows =
                        records.values().stream()
                                .filter(r -> r.get("id").toString().startsWith("Units-"))
                                .toList();
                return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
            }
            String id = values.get("id").toString();
            if (action.equals("get")) {
                if (customerDetail500 && endpoint.equals("Customers"))
                    return Map.of("error", "Null reference", "status", 500);
                return records.containsKey(id)
                        ? Map.of("entity", records.get(id))
                        : defaultDeletedMasterDto
                                ? Map.of(
                                        "entity",
                                        Map.of("subUnits", List.of(), "type", "RawMaterial"))
                                : defaultDeletedDto
                                        ? Map.of(
                                                "entity",
                                                Map.of(
                                                        "transactionDate",
                                                        "0001-01-01T00:00:00",
                                                        "value",
                                                        0))
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
            if (missingDelete400 && !records.containsKey(id))
                return Map.of("error", "Common.ObjectNotFound", "status", 400);
            if (endpoint.equals("Banks")) {
                if (rejectBank) return Map.of("error", "Record is in use", "status", 400);
                if (timeOutBank) {
                    timeOutBank = false;
                    if (commitBeforeTimeout) records.remove(id);
                    throw new TimeoutException("Simulated lost DELETE response");
                }
            }
            records.remove(id);
            return Map.of("ok", true);
        }
    }

    @Test
    public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence() {
        Api api = new Api();
        api.missingDelete400 = true;
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record bank = data.bank(1);
        api.records.remove(bank.id());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void retriesUncommittedDeleteBeforeParentAccounts() {
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
        api.defaultDeletedMasterDto = true;
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record record = data.fixture("item", "Items", FinancialTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }

    @Test
    public void updatedMovementDeletesBeforeItsNewReference() {
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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

        Api api = new Api();
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
        Api api = new Api();
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
