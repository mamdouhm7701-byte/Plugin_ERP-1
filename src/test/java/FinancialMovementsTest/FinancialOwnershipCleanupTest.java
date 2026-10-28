/* شرح الملف: اختبارات تنظيف الحركات المالية: حماية هوية الفرع وملكية السجلات قبل الحذف.
 */
package FinancialMovementsTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FinancialOwnershipCleanupTest {
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
