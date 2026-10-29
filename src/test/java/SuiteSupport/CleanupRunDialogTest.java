/* شرح الملف: يثبت دون الاتصال بالبرنامج أن التنظيف يقتصر على الفرع المسجل، ويحافظ على السجلات عند الفشل ويرفض تشغيلًا متداخلًا أو معاينة قديمة. */
package SuiteSupport;

import java.nio.file.*;
import java.util.*;
import org.json.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class CleanupRunDialogTest {
    private static final String PROFILE = "business-0123456789abcdef",
            BRANCH = "owned-branch",
            ORIGIN = "https://cleanup.example";

    private static Path project() throws Exception {
        Path root = Files.createTempDirectory("cleanup-ui-test");
        Path profiles = Files.createDirectories(root.resolve("config/branch-profiles"));
        Files.writeString(
                profiles.resolve(PROFILE + ".properties"),
                "origin="
                        + ORIGIN
                        + "\nbranchId="
                        + BRANCH
                        + "\nactivityType=Restaurant\ndiscountLevel=Transaction\ntaxPolicy=NetBased\nstocktakingMethod=Periodic\n");
        Path registry = Files.createDirectories(root.resolve("config/business-branch-state"));
        Files.writeString(
                registry.resolve("0123456789abcdef.json"),
                new JSONObject(
                                Map.of(
                                        "profile",
                                        PROFILE,
                                        "owner",
                                        "QA-BUS-0123456789abcdef",
                                        "branchId",
                                        BRANCH,
                                        "origin",
                                        ORIGIN,
                                        "status",
                                        "VERIFIED"))
                        .toString());
        return root;
    }

    private static Path journal(Path root, String folder, String branch) throws Exception {
        Path dir = Files.createDirectories(root.resolve("target/" + folder));
        Path file = dir.resolve("12345678-abc.json");
        Files.writeString(
                file,
                new JSONArray(
                                List.of(
                                        Map.of(
                                                "origin",
                                                ORIGIN,
                                                "branchId",
                                                branch,
                                                "endpoint",
                                                "Stores",
                                                "id",
                                                "owned-store",
                                                "name",
                                                "QA-PU-store",
                                                "values",
                                                Map.of("arabicName", "QA-PU-store"))))
                        .toString());
        return file;
    }

    private static BusinessRunTarget target(String origin) {
        return new BusinessRunTarget(
                "Cloud",
                "CleanupProbe",
                origin,
                "probe-user",
                "dummy-cleanup-password".toCharArray());
    }

    private static final class FakeExecution extends CleanupRunExecution {
        final List<String> actions = new ArrayList<>();
        boolean fail, busy;

        @Override
        NativeInteractiveSuiteListener gate() {
            return new NativeInteractiveSuiteListener() {
                @Override
                void acquire(Path project) {
                    if (busy)
                        throw new IllegalStateException(
                                "Another ERP run is active. Wait for it to finish.");
                }
            };
        }

        @Override
        void recover(CleanupRunCatalog.Journal journal) throws Exception {
            actions.add("recover:" + journal.module());
            if (fail) throw new IllegalStateException("simulated ERP deletion refusal");
            for (var file : journal.files()) Files.writeString(file, "[]");
        }

        @Override
        void deleteBranch(Path root, Plugin_ERP.BranchProfiles.BranchProfile profile, Path output)
                throws Exception {
            Assert.assertFalse(BusinessBranchCleanup.pendingRecovery(root, BRANCH));
            actions.add("delete:" + profile.expected().branchId());
        }
    }

    @Test
    public void previewUsesOnlyProvenOwnedBranchAndMatchingJournals() throws Exception {
        Path root = project();
        journal(root, "purchase-fixtures", BRANCH);
        journal(root, "sales-fixtures", "other-branch");
        var entries = CleanupRunCatalog.load(root).entries();
        Assert.assertEquals(entries.size(), 1);
        Assert.assertEquals(entries.getFirst().records(), 1);
        Assert.assertEquals(entries.getFirst().journals().getFirst().module(), "purchase");
        Assert.assertTrue(entries.getFirst().preview().contains("المشتريات"));
        Path state = entries.getFirst().registry();
        var modified = new JSONObject(Files.readString(state)).put("owner", "real-user-branch");
        Files.writeString(state, modified.toString());
        var refused = CleanupRunCatalog.load(root);
        Assert.assertTrue(refused.entries().isEmpty());
        Assert.assertFalse(refused.warnings().isEmpty());
    }

    @Test
    public void cleanupPreservesJournalsAndDeletesBranchOnlyAfterData() throws Exception {
        Path root = project();
        Path file = journal(root, "purchase-fixtures", BRANCH);
        var selected = CleanupRunCatalog.load(root).entries().getFirst();
        var execution = new FakeExecution();
        String previous = System.getProperty("ExecutionType");
        var result = execution.execute(root, selected, target(ORIGIN), true, text -> {});
        Assert.assertTrue(result.dataCleaned());
        Assert.assertTrue(result.branchDeleted());
        Assert.assertEquals(execution.actions, List.of("recover:purchase", "delete:" + BRANCH));
        Assert.assertEquals(Files.readString(file), "[]");
        Assert.assertTrue(
                Files.exists(
                        result.output()
                                .resolve("before/target/purchase-fixtures/12345678-abc.json")));
        Assert.assertFalse(
                Files.readString(result.output().resolve("summary.json"))
                        .contains("dummy-cleanup-password"));
        Assert.assertEquals(System.getProperty("ExecutionType"), previous);
    }

    @Test
    public void changedPreviewOrWrongOriginCannotStartDeletion() throws Exception {
        Path root = project();
        Path file = journal(root, "purchase-fixtures", BRANCH);
        var selected = CleanupRunCatalog.load(root).entries().getFirst();
        var execution = new FakeExecution();
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        execution.execute(
                                root,
                                selected,
                                target("https://another.example"),
                                true,
                                text -> {}));
        Files.writeString(file, "[]");
        Assert.expectThrows(
                IllegalStateException.class,
                () -> execution.execute(root, selected, target(ORIGIN), true, text -> {}));
        Assert.assertTrue(execution.actions.isEmpty());
    }

    @Test
    public void activeRunOrFailedDeleteKeepsRecoveryEvidenceAndBranch() throws Exception {
        Path root = project();
        Path file = journal(root, "purchase-fixtures", BRANCH);
        var selected = CleanupRunCatalog.load(root).entries().getFirst();
        var execution = new FakeExecution();
        execution.busy = true;
        Assert.expectThrows(
                IllegalStateException.class,
                () -> execution.execute(root, selected, target(ORIGIN), true, text -> {}));
        Assert.assertTrue(execution.actions.isEmpty());
        execution.busy = false;
        execution.fail = true;
        var result = execution.execute(root, selected, target(ORIGIN), true, text -> {});
        Assert.assertFalse(result.dataCleaned());
        Assert.assertFalse(result.branchDeleted());
        Assert.assertFalse(new JSONArray(Files.readString(file)).isEmpty());
        Assert.assertEquals(execution.actions, List.of("recover:purchase"));
        Assert.assertFalse(result.remaining().isEmpty());
    }

    @Test
    public void unsupportedAuditBlocksBranchDeletionAndDataOnlyOptionKeepsBranch()
            throws Exception {
        Path root = project();
        journal(root, "treasury-audit", BRANCH);
        var execution = new FakeExecution();
        var result =
                execution.execute(
                        root,
                        CleanupRunCatalog.load(root).entries().getFirst(),
                        target(ORIGIN),
                        true,
                        text -> {});
        Assert.assertFalse(result.dataCleaned());
        Assert.assertTrue(execution.actions.isEmpty());
        Path other = project();
        journal(other, "purchase-fixtures", BRANCH);
        var dataOnly = new FakeExecution();
        var cleaned =
                dataOnly.execute(
                        other,
                        CleanupRunCatalog.load(other).entries().getFirst(),
                        target(ORIGIN),
                        false,
                        text -> {});
        Assert.assertTrue(cleaned.dataCleaned());
        Assert.assertFalse(cleaned.branchDeleted());
        Assert.assertEquals(dataOnly.actions, List.of("recover:purchase"));
    }

    @Test
    public void fixtureAndAttemptPairAreRecoveredOnceAndTargetComesFromOriginalRun()
            throws Exception {
        Path root = project();
        journal(root, "purchase-fixtures", BRANCH);
        journal(root, "purchase-attempts", BRANCH);
        Path run = Files.createDirectories(root.resolve("target/validation/business-probe"));
        Files.writeString(
                run.resolve("branch-profile.json"),
                new JSONObject(Map.of("name", PROFILE)).toString());
        Files.writeString(
                run.resolve("run-settings.json"),
                new JSONObject(
                                Map.of(
                                        "target",
                                        Map.of(
                                                "environment",
                                                "Cloud",
                                                "tenant",
                                                "OriginalTenant",
                                                "url",
                                                ORIGIN)))
                        .toString());
        var selected = CleanupRunCatalog.load(root).entries().getFirst();
        Assert.assertEquals(selected.journals().size(), 1);
        Assert.assertEquals(selected.records(), 2);
        Assert.assertEquals(selected.target().get("tenant"), "OriginalTenant");
        var execution = new FakeExecution();
        execution.execute(root, selected, target(ORIGIN), true, text -> {});
        Assert.assertEquals(execution.actions, List.of("recover:purchase", "delete:" + BRANCH));
    }
}
