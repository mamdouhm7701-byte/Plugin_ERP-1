/* شرح الملف: اختبارات حذف فرع التجربة فقط بعد التأكد من انتهاء تنظيف بياناته.
 */
package SuiteSupport;

import java.nio.file.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BusinessBranchCleanupTest {
    @Test
    public void retainedFinancialAndAuditHistoryPreventsBranchDeletion() throws Exception {
        for (String name :
                java.util.List.of(
                        "dealer-page-records",
                        "posting-recovery",
                        "treasury-audit",
                        "treasury-audit-masters",
                        "treasury-audit-financial",
                        "inventory-audit-documents")) {
            Path project = Files.createTempDirectory("retained-branch-");
            Path directory = project.resolve("target").resolve(name);
            Files.createDirectories(directory);
            Files.writeString(directory.resolve("retained.json"), "[{\"branchId\":\"owned\"}]");
            Assert.assertTrue(BusinessBranchCleanup.pendingRecovery(project, "owned"), name);
            Assert.assertFalse(BusinessBranchCleanup.pendingRecovery(project, "other"), name);
        }
    }

    @Test
    public void pendingFixtureOrLostCreateRetainsOnlyItsBranch() throws Exception {
        Path project = Files.createTempDirectory("branch-cleanup-");
        Path fixtures = project.resolve("target/sales-fixtures");
        Path attempts = project.resolve("target/sales-attempts");
        Files.createDirectories(fixtures);
        Files.createDirectories(attempts);
        Files.writeString(fixtures.resolve("foreign.json"), "[{\"branchId\":\"foreign\"}]");
        Assert.assertFalse(BusinessBranchCleanup.pendingRecovery(project, "owned"));
        Files.writeString(
                attempts.resolve("pending.json"),
                "[{\"_branchProfile\":{\"expected\":{\"branchId\":\"owned\"}}}]");
        Assert.assertTrue(BusinessBranchCleanup.pendingRecovery(project, "owned"));
        Files.writeString(attempts.resolve("pending.json"), "[]");
        Assert.assertFalse(BusinessBranchCleanup.pendingRecovery(project, "owned"));
        Files.writeString(fixtures.resolve("owned.json"), "[{\"branchId\":\"owned\"}]");
        Assert.assertTrue(BusinessBranchCleanup.pendingRecovery(project, "owned"));
    }

    @Test
    public void malformedJournalCannotAuthorizeBranchDeletion() throws Exception {
        Path project = Files.createTempDirectory("branch-cleanup-corrupt-");
        Path directory = project.resolve("target/sales-fixtures");
        Files.createDirectories(directory);
        Files.writeString(directory.resolve("broken.json"), "not-json");
        Assert.expectThrows(
                org.json.JSONException.class,
                () -> BusinessBranchCleanup.pendingRecovery(project, "owned"));
    }
}
