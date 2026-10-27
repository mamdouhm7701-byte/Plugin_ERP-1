package TestSupport;

import Plugin_ERP.BranchProfiles.*;
import java.nio.file.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ProfiledFixtureJournalsTest {
    private BranchProfile profile(String policy) {
        return new BranchProfile(
                "reference",
                BranchSnapshot.from(
                        Map.of(
                                "origin",
                                "https://erp.example",
                                "branchId",
                                "owned",
                                "activityType",
                                "Public",
                                "discountLevel",
                                "Items",
                                "taxPolicy",
                                policy,
                                "stocktakingMethod",
                                "Periodic")));
    }

    @Test
    public void journalPinsProfileWithoutChangingTheOwnersInMemoryRows() throws Exception {
        Path folder = Files.createTempDirectory("profile-journal-");
        Path file = folder.resolve("owned.json");
        var row =
                new LinkedHashMap<String, Object>(
                        Map.of(
                                "id",
                                "owned-record",
                                "origin",
                                "https://erp.example",
                                "branchId",
                                "owned"));
        try {
            try (var scope = BranchProfileScope.bind(profile("NetBased"))) {
                FixtureJournals.write(file, List.of(row));
                Assert.assertFalse(row.containsKey("_branchProfile"));
                Assert.assertTrue(
                        FixtureJournals.read(file).contains(profile("NetBased").fingerprint()));
            }
            try (var scope = BranchProfileScope.bind(profile("ItemBased"))) {
                Assert.expectThrows(IllegalStateException.class, () -> FixtureJournals.read(file));
                String previous = Files.readString(file);
                Assert.expectThrows(
                        IllegalStateException.class,
                        () -> FixtureJournals.write(file, List.of(row)));
                Assert.assertEquals(
                        Files.readString(file),
                        previous,
                        "Wrong profile must not replace the recovery journal");
            }
            Assert.expectThrows(IllegalStateException.class, () -> FixtureJournals.read(file));
        } finally {
            Files.deleteIfExists(file);
            Files.delete(folder);
        }
    }

    @Test
    public void oldJournalWorksWithoutProfileButCannotBeRelabelledAsProfiled() throws Exception {
        Path folder = Files.createTempDirectory("legacy-profile-journal-");
        Path file = folder.resolve("old.json");
        try {
            FixtureJournals.write(file, List.of(Map.of("id", "old-owned-record")));
            Assert.assertTrue(FixtureJournals.read(file).contains("old-owned-record"));
            try (var scope = BranchProfileScope.bind(profile("NetBased"))) {
                Assert.expectThrows(IllegalStateException.class, () -> FixtureJournals.read(file));
            }
        } finally {
            Files.deleteIfExists(file);
            Files.delete(folder);
        }
    }
}
