/* شرح الملف: حفظ قائمة بيانات الاختبار التي قد تحتاج إلى تنظيف لاحق.
 */
package TestSupport;

import java.io.IOException;
import java.nio.file.*;

public final class FixtureJournals {
    private FixtureJournals() {}

    private static final String PROFILE_METADATA = "_branchProfile";

    /**
     * Old journals keep their schema; profiled journals require their original immutable
     * expectation.
     */
    public static String read(Path path) throws IOException {
        String contents = Files.readString(path);
        var profile = Plugin_ERP.BranchProfiles.BranchProfileScope.currentOrSelected();
        if (profile.isEmpty() && !contents.contains("\"" + PROFILE_METADATA + "\""))
            return contents;
        var rows = new org.json.JSONArray(contents);
        for (Object value : rows.toList()) {
            if (!(value instanceof java.util.Map<?, ?> row))
                throw new IllegalStateException("Invalid recovery journal row");
            Object metadata = row.get(PROFILE_METADATA);
            if (profile.isEmpty())
                throw new IllegalStateException(
                        "Profiled recovery journal requires its branch profile");
            if (!(metadata instanceof java.util.Map<?, ?> stored)
                    || !profile.orElseThrow().fingerprint().equals(stored.get("fingerprint"))) {
                throw new IllegalStateException(
                        "Recovery journal belongs to another or unspecified branch profile; preserve it for recovery");
            }
        }
        return contents;
    }

    private record JournalView(java.util.Collection<?> rows, String previous) {}

    private static JournalView profileRows(Path path, Object rows) throws IOException {
        String previous = Files.isRegularFile(path) ? read(path) : null;
        var profile = Plugin_ERP.BranchProfiles.BranchProfileScope.currentOrSelected();
        var collection = (java.util.Collection<?>) rows;
        if (profile.isEmpty()) return new JournalView(collection, previous);
        var expected = profile.orElseThrow();
        var result = new java.util.ArrayList<java.util.Map<String, Object>>();
        for (Object value : collection) {
            if (!(value instanceof java.util.Map<?, ?> row))
                throw new IllegalArgumentException("Profiled journal rows must be maps");
            var copy = new java.util.LinkedHashMap<String, Object>();
            row.forEach((key, item) -> copy.put(key.toString(), item));
            copy.put(
                    PROFILE_METADATA,
                    java.util.Map.of(
                            "name",
                            expected.name(),
                            "fingerprint",
                            expected.fingerprint(),
                            "expected",
                            expected.expected().fields()));
            result.add(copy);
        }
        return new JournalView(result, previous);
    }

    /** Replace atomically, preserving the prior recovery file if writing is interrupted. */
    public static void write(Path path, Object rows) {
        try {
            var view = profileRows(path, rows);
            String contents = new org.json.JSONArray(view.rows()).toString(2);
            if (contents.equals(view.previous())) return;
            Files.createDirectories(path.getParent());
            Path temporary =
                    Files.createTempFile(path.getParent(), path.getFileName() + "-", ".tmp");
            Files.writeString(temporary, contents);
            for (int attempt = 0; ; attempt++) {
                try {
                    try {
                        Files.move(
                                temporary,
                                path,
                                StandardCopyOption.REPLACE_EXISTING,
                                StandardCopyOption.ATOMIC_MOVE);
                    } catch (AtomicMoveNotSupportedException unsupported) {
                        Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
                    }
                    break;
                } catch (AccessDeniedException locked) {
                    if (attempt == 4) throw locked;
                    try {
                        Thread.sleep(100L * (attempt + 1));
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Journal replacement interrupted", interrupted);
                    }
                }
            }
        } catch (IOException failure) {
            throw new AssertionError("Cannot persist fixture recovery journal", failure);
        }
    }
}
