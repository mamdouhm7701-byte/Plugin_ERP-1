/* شرح الملف: يراجع حفظ سجلات الاسترجاع وفتحها في نفس الفرع فقط، مع منع خلط سجل تجربة أخرى. */
package TestSupport;

import java.nio.file.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureRecoveryJournalTest {
    private record Row(String endpoint, Object id, String name, Map<String, Object> values)
            implements OwnedFixtureRecord {}

    private FixtureRecoveryJournal<Row> journal(
            Path root, Object branch, List<Row> records, List<Map<String, Object>> attempts) {
        return new FixtureRecoveryJournal<>(
                new FixtureRecoveryJournal.Location(
                        root, "sales", "abcdef12-abc", "offline", branch, "QA-SL-"),
                records,
                attempts,
                Row::new);
    }

    @Test
    public void resumesOwnedRowsAndPendingAttemptsWithoutChangingTheirBusinessValues()
            throws Exception {
        Path root = Files.createTempDirectory("fixture-journal-");
        var records =
                new ArrayList<>(
                        List.of(
                                new Row(
                                        "Items",
                                        "item",
                                        "QA-SL-item",
                                        Map.of("mainUnitId", "unit"))));
        var attempts =
                new ArrayList<Map<String, Object>>(
                        List.of(
                                new LinkedHashMap<>(
                                        Map.of(
                                                "origin",
                                                "offline",
                                                "branchId",
                                                "branch",
                                                "endpoint",
                                                "Items",
                                                "name",
                                                "QA-SL-pending",
                                                "values",
                                                Map.of("mainUnitId", "unit")))));
        var journal = journal(root, "branch", records, attempts);
        journal.persistRecords();
        journal.persistAttempts();
        var loaded = new ArrayList<Row>();
        var pending = new ArrayList<Map<String, Object>>();
        journal(root, "branch", loaded, pending).resume();
        Assert.assertEquals(loaded, records);
        Assert.assertEquals(pending, attempts);
        journal.persistAttempts();
        Assert.assertTrue(Files.isRegularFile(root.resolve("sales-attempts/abcdef12-abc.json")));
    }

    @Test
    public void anotherBranchCannotResumeOrMutateOriginalJournal() throws Exception {
        Path root = Files.createTempDirectory("fixture-journal-branch-");
        journal(
                        root,
                        "branch",
                        new ArrayList<>(List.of(new Row("Items", "id", "QA-SL-item", Map.of()))),
                        new ArrayList<>())
                .persistRecords();
        Path path = root.resolve("sales-fixtures/abcdef12-abc.json");
        String before = Files.readString(path);
        Assert.expectThrows(
                AssertionError.class,
                () -> journal(root, "foreign", new ArrayList<>(), new ArrayList<>()).resume());
        Assert.assertEquals(Files.readString(path), before);
    }

    @Test
    public void unrelatedNamesAreNotAdoptedFromRecoveryFile() throws Exception {
        Path root = Files.createTempDirectory("fixture-journal-name-");
        FixtureJournals.write(
                root.resolve("sales-fixtures/abcdef12-abc.json"),
                List.of(
                        Map.of(
                                "origin",
                                "offline",
                                "branchId",
                                "branch",
                                "endpoint",
                                "Items",
                                "name",
                                "foreign",
                                "values",
                                Map.of())));
        Assert.expectThrows(
                AssertionError.class,
                () -> journal(root, "branch", new ArrayList<>(), new ArrayList<>()).resume());
    }
}
