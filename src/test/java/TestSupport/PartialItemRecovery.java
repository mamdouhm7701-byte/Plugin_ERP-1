/* شرح الملف: استرجاع صنف تم حفظه جزئيًا بعد فشل الاستجابة، بعد مطابقة علامته ووحدته ومجموعته.
 * فائدته: يبقي الصنف المملوك قابلًا للتنظيف دون تبني صنف مشابه أو مكرر الاسم.
 */
package TestSupport;

import java.util.*;
import java.util.function.*;

public final class PartialItemRecovery {
    private PartialItemRecovery() {}

    @FunctionalInterface
    public interface RecordFactory<R> {
        R create(String endpoint, Object id, String name, Map<String, Object> values);
    }

    public record Access<R extends OwnedFixtureRecord>(
            String prefix,
            Collection<R> created,
            BiFunction<String, Map<String, Object>, Map<String, Object>> get,
            RecordFactory<R> records,
            Function<R, Map<String, Object>> read,
            Consumer<R> remember) {}

    @SuppressWarnings("unchecked")
    public static <R extends OwnedFixtureRecord> void recover(
            List<Map<String, Object>> attempts, Access<R> access) {

        for (Map<String, Object> attempt : attempts) {
            if (!"Items".equals(attempt.get("endpoint"))) continue;
            Map<String, Object> values = (Map<String, Object>) attempt.get("values");
            String name =
                    java.util.stream.Stream.of("arabicName", "englishName", "notes")
                            .map(values::get)
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .filter(v -> v.startsWith(access.prefix()))
                            .findFirst()
                            .orElse(null);
            if (name == null
                    || !name.startsWith(access.prefix())
                    || access.created().stream().anyMatch(r -> r.name().equals(name))) continue;
            List<Map<String, Object>> matches = new ArrayList<>();
            java.util.Set<Object> seen = new java.util.HashSet<>();
            for (int page = 1; ; page++) {
                Object entity =
                        access.get()
                                .apply(
                                        "Items/Summary?pageNumber=" + page + "&pageSize=500",
                                        Map.of())
                                .get("entity");
                TestSupport.FixtureRecordSearch.findOwned(entity, name, matches);
                if (!matches.isEmpty() || entity instanceof List<?>) break;
                if (!(entity instanceof Map<?, ?> result)
                        || !(result.get("pageData") instanceof List<?> rows)
                        || !(result.get("totalRecords") instanceof Number total))
                    throw new AssertionError("Cannot verify partial item through its summary list");
                int added = 0;
                for (Object value : rows) {
                    if (!(value instanceof Map<?, ?> row) || row.get("id") == null)
                        throw new AssertionError("Invalid summary identity");
                    if (seen.add(row.get("id"))) added++;
                }
                if (seen.size() >= total.longValue()) break;
                if (added == 0)
                    throw new AssertionError("Partial item summary pagination did not advance");
            }
            if (matches.size() > 1)
                throw new AssertionError("Ambiguous partially saved test item: " + name);
            if (matches.size() == 1) {
                R record = access.records().create("Items", matches.get(0).get("id"), name, values);
                Map<String, Object> stored = access.read().apply(record);
                if (stored == null
                        || (!name.equals(stored.get("arabicName"))
                                && !name.equals(stored.get("englishName"))
                                && !name.equals(stored.get("notes")))
                        || !java.util.Objects.equals(
                                values.get("mainUnitId"), stored.get("mainUnitId"))
                        || !java.util.Objects.equals(
                                values.get("categoryId"), stored.get("categoryId")))
                    throw new AssertionError("Partial item ownership cannot be verified");
                access.remember().accept(record);
            }
        }
    }
}
