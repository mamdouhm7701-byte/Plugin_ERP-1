/* شرح الملف: حفظ سجلات بيانات التجربة ومحاولات الحفظ غير المحسومة، ثم استعادتها في نفس الفرع والبيئة.
 * فائدته: يوحد الكتابة الآمنة لسجلات البيع والشراء والمخازن ويحافظ على بيانات الاسترجاع بعد توقف التشغيل.
 */
package TestSupport;

import java.nio.file.*;
import java.util.*;

public final class FixtureRecoveryJournal<R extends OwnedFixtureRecord> {
    public record Location(
            Path root,
            String folder,
            String suffix,
            String origin,
            Object branchId,
            String prefix) {
        public Location {
            if (!folder.matches("[a-z][a-z-]*") || !suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))
                throw new IllegalArgumentException("Invalid fixture journal location");
        }

        private Path path(String kind) {
            return root.resolve(folder + "-" + kind).resolve(suffix + ".json");
        }
    }

    @FunctionalInterface
    public interface RecordFactory<R> {
        R create(String endpoint, Object id, String name, Map<String, Object> values);
    }

    private final Location location;
    private final List<R> records;
    private final List<Map<String, Object>> attempts;
    private final RecordFactory<R> factory;
    private String lastAttempts = "";

    public FixtureRecoveryJournal(
            Location location,
            List<R> records,
            List<Map<String, Object>> attempts,
            RecordFactory<R> factory) {
        this.location = location;
        this.records = records;
        this.attempts = attempts;
        this.factory = factory;
    }

    public void persistRecords() {
        var rows = new ArrayList<Map<String, Object>>();
        for (R record : records) {
            var row = new LinkedHashMap<String, Object>();
            row.put("origin", location.origin());
            row.put("branchId", location.branchId());
            row.put("endpoint", record.endpoint());
            row.put("id", record.id());
            row.put("name", record.name());
            row.put("values", record.values());
            rows.add(row);
        }
        FixtureJournals.write(location.path("fixtures"), rows);
    }

    public void persistAttempts() {
        String json = new org.json.JSONArray(attempts).toString(2);
        if (json.equals(lastAttempts)) return;
        FixtureJournals.write(location.path("attempts"), attempts);
        lastAttempts = json;
    }

    @SuppressWarnings("unchecked")
    public void resume() throws java.io.IOException {
        for (String kind : List.of("fixtures", "attempts")) {
            Path path = location.path(kind);
            if (!Files.exists(path)) continue;
            for (Object value : new org.json.JSONArray(FixtureJournals.read(path)).toList()) {
                Map<String, Object> row = (Map<String, Object>) value;
                if (!location.origin().equals(row.get("origin"))
                        || !Objects.equals(location.branchId(), row.get("branchId")))
                    throw new AssertionError(
                            "Recovery journal belongs to another origin or branch: " + path);
                String endpoint = (String) row.get("endpoint"), name = (String) row.get("name");
                if (name == null
                        || !name.startsWith(location.prefix())
                        || endpoint == null
                        || !(row.get("values") instanceof Map<?, ?>))
                    throw new AssertionError("Invalid fixture recovery journal record: " + path);
                if (kind.equals("fixtures"))
                    records.add(
                            factory.create(
                                    endpoint,
                                    row.get("id"),
                                    name,
                                    (Map<String, Object>) row.get("values")));
                else attempts.add(row);
            }
        }
    }
}
