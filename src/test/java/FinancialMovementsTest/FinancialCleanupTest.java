/* شرح الملف: اختبارات تنظيف الحركات المالية: حذف بيانات التجربة والتحقق من غيابها.
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
