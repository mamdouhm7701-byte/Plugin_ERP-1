package Plugin_ERP.BranchProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

/** Explicit selection only: files outside the regular shared resource settings are never merged. */
public final class BranchProfiles {
    private static final Set<String> FIELDS =
            Set.of(
                    "origin",
                    "branchId",
                    "activityType",
                    "discountLevel",
                    "taxPolicy",
                    "stocktakingMethod");

    private BranchProfiles() {}

    public static Optional<BranchProfile> selected() {
        return selected(
                Plugin_ERP.Util.PropertyReader.getProperty("branch.profile"),
                Path.of("config", "branch-profiles"),
                Plugin_ERP.Util.PropertyReader.getProperty("branch.profile.fingerprint"));
    }

    public static Optional<BranchProfile> selected(
            String name, Path directory, String fingerprint) {
        var profile = load(name, directory);
        if (fingerprint != null && !fingerprint.isBlank()) {
            if (profile.isEmpty() || !profile.orElseThrow().fingerprint().equals(fingerprint)) {
                throw new IllegalStateException(
                        "Branch profile fingerprint changed; refusing to continue this run");
            }
        }
        return profile;
    }

    public static Optional<BranchProfile> load(String name, Path directory) {
        if (name == null || name.isBlank()) return Optional.empty();
        if (!name.matches("[a-z0-9][a-z0-9_-]{0,63}"))
            throw new IllegalArgumentException("Invalid branch profile name");
        Path file = directory.resolve(name + ".properties");
        Properties settings = new Properties();
        try (var reader = Files.newBufferedReader(file, java.nio.charset.StandardCharsets.UTF_8)) {
            settings.load(reader);
        } catch (java.io.IOException failure) {
            throw new IllegalStateException(
                    "Cannot read selected branch profile '" + name + "'", failure);
        }
        if (!settings.stringPropertyNames().equals(FIELDS)) {
            throw new IllegalArgumentException(
                    "Branch profile must define exactly: "
                            + String.join(", ", new java.util.TreeSet<>(FIELDS)));
        }
        Map<String, Object> values = new java.util.HashMap<>();
        settings.forEach((key, value) -> values.put(key.toString(), value));
        return Optional.of(new BranchProfile(name, BranchSnapshot.from(values)));
    }
}
