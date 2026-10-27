package Plugin_ERP.BranchProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BranchProfileTest {
    private Map<String, Object> actual() {
        return Map.of(
                "origin",
                "https://erp.example",
                "branchId",
                "branch-owned",
                "activityType",
                "Public",
                "discountLevel",
                "Items",
                "taxPolicy",
                "NetBased",
                "stocktakingMethod",
                "Periodic");
    }

    private String configuration() {
        return "origin=https://erp.example\nbranchId=branch-owned\nactivityType=Public\ndiscountLevel=Items\ntaxPolicy=NetBased\nstocktakingMethod=Periodic\n";
    }

    @Test
    public void runManifestRejectsChangedConfigurationWithoutReplacingOriginalEvidence()
            throws Exception {
        Path directory = Files.createTempDirectory("profile-pin-");
        Path file = directory.resolve("reference.properties");
        Path manifest = directory.resolve("run.json");
        try {
            Files.writeString(file, configuration());
            BranchProfileCommand.write("reference", directory, manifest, null);
            String evidence = Files.readString(manifest);
            String fingerprint = new org.json.JSONObject(evidence).getString("fingerprint");
            BranchProfileCommand.write("reference", directory, manifest, fingerprint);
            Files.writeString(file, configuration().replace("NetBased", "ItemBased"));
            Assert.expectThrows(
                    IllegalStateException.class,
                    () -> BranchProfiles.selected("reference", directory, fingerprint));
            Assert.expectThrows(
                    IllegalStateException.class,
                    () -> BranchProfileCommand.write("reference", directory, manifest, null));
            Assert.assertEquals(Files.readString(manifest), evidence);
            Assert.expectThrows(
                    IllegalStateException.class,
                    () -> BranchProfiles.selected(null, directory, fingerprint));
        } finally {
            Files.deleteIfExists(file);
            Files.deleteIfExists(manifest);
            Files.deleteIfExists(directory);
        }
    }

    @Test
    public void noSelectionDoesNotReadFilesOrChangeLegacyRun() throws Exception {
        Assert.assertTrue(BranchProfiles.load(null, Path.of("missing-profile-folder")).isEmpty());
        Assert.assertTrue(BranchProfiles.load(" ", Path.of("missing-profile-folder")).isEmpty());
    }

    @Test
    public void matchingProfileAcceptsActualBranchWithoutAdaptingItsExpectations()
            throws Exception {
        withProfile(configuration(), profile -> profile.verify(BranchSnapshot.from(actual())));
    }

    @Test
    public void everyProfileAxisMustMatchAndMismatchNamesOnlyChangedFields() throws Exception {
        withProfile(
                configuration(),
                profile -> {
                    for (String field : actual().keySet()) {
                        var changed = new java.util.HashMap<>(actual());
                        changed.put(
                                field,
                                switch (field) {
                                    case "origin" -> "https://other.example";
                                    case "branchId" -> "foreign-branch";
                                    case "activityType" -> "Restaurant";
                                    case "discountLevel" -> "Transaction";
                                    case "taxPolicy" -> "ItemBased";
                                    default -> "Continuous";
                                });
                        var failure =
                                Assert.expectThrows(
                                        IllegalStateException.class,
                                        () -> profile.verify(BranchSnapshot.from(changed)));
                        Assert.assertTrue(
                                failure.getMessage().contains(field), failure.getMessage());
                        profile.verify(BranchSnapshot.from(actual()));
                    }
                });
    }

    @Test
    public void missingAndUnknownPolicyCannotFallBackToDefault() throws Exception {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> BranchSnapshot.from(Map.of("origin", "https://erp.example")));
        var unknown = new java.util.HashMap<>(actual());
        unknown.put("taxPolicy", "UnknownPolicy");
        Assert.expectThrows(IllegalArgumentException.class, () -> BranchSnapshot.from(unknown));
        withInvalidProfile(configuration().replace("taxPolicy=NetBased\n", ""));
        withInvalidProfile(
                configuration().replace("activityType=Public", "activityType=UnknownActivity"));
    }

    @Test
    public void profileNameCannotEscapeDirectoryAndExplicitMissingProfileFails() {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> BranchProfiles.load("../outside", Path.of("config/branch-profiles")));
        Assert.expectThrows(
                IllegalStateException.class,
                () -> BranchProfiles.load("missing-profile", Path.of("missing-profile-folder")));
    }

    @Test
    public void unexpectedPropertyTypoAndNonOriginUrlAreRejected() throws Exception {
        withInvalidProfile(configuration() + "taxPolcy=NetBased\n");
        withInvalidProfile(
                configuration().replace("https://erp.example", "https://erp.example/#/login"));
        withInvalidProfile(
                configuration().replace("https://erp.example", "https://user:pass@erp.example"));
    }

    private void withInvalidProfile(String settings) throws Exception {
        Path folder = Files.createTempDirectory("branch-profile-invalid-");
        try {
            Files.writeString(folder.resolve("reference.properties"), settings);
            Assert.expectThrows(
                    IllegalArgumentException.class, () -> BranchProfiles.load("reference", folder));
        } finally {
            Files.deleteIfExists(folder.resolve("reference.properties"));
            Files.delete(folder);
        }
    }

    private interface Check {
        void run(BranchProfile profile) throws Exception;
    }

    private void withProfile(String settings, Check check) throws Exception {
        Path folder = Files.createTempDirectory("branch-profile-test-");
        try {
            Files.writeString(folder.resolve("reference.properties"), settings);
            check.run(BranchProfiles.load("reference", folder).orElseThrow());
        } finally {
            Files.delete(folder.resolve("reference.properties"));
            Files.delete(folder);
        }
    }
}
