/* شرح الملف: قراءة سجل التجربة وتأكيد غيابه، مع مراجعة القائمة عند خطأ تفاصيل العميل أو المورد المسموح به فقط.
 * فائدته: لا يعتبر خطأ الخادم أو الرد غير المفهوم دليلًا على نجاح الحذف.
 */
package TestSupport;

import java.util.*;
import java.util.function.BiFunction;

public final class FixtureRecordReader {
    private final BiFunction<String, Map<String, Object>, Map<String, Object>> get;
    private final Set<String> serverErrorFallback;

    public FixtureRecordReader(
            BiFunction<String, Map<String, Object>, Map<String, Object>> get,
            Set<String> serverErrorFallback) {
        this.get = get;
        this.serverErrorFallback = Set.copyOf(serverErrorFallback);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> read(OwnedFixtureRecord record) {
        Object entity;
        try {
            entity = get.apply(record.endpoint(), Map.of("id", record.id())).get("entity");
        } catch (FixtureApiFailure failure) {
            if (failure.status == 404 || failure.status == 410) return null;
            if (failure.status == 500
                    && serverErrorFallback.contains(record.endpoint())
                    && !existsInList(record)) return null;
            throw failure;
        }
        // The ERP returns HTTP 200 with an empty string for some deleted records.
        if (entity instanceof String text && text.isBlank()) return null;
        if (!(entity instanceof Map<?, ?>)) {
            throw new AssertionError(
                    "Unexpected fixture read response for "
                            + record.endpoint()
                            + "/"
                            + record.id());
        }
        Map<?, ?> dto = (Map<?, ?>) entity;
        if (dto.get("id") == null
                && dto.get("arabicName") == null
                && dto.get("englishName") == null
                && dto.get("notes") == null
                && !dto.containsKey("pageData")
                && !dto.containsKey("totalRecords")
                && !dto.containsKey("error")
                && !dto.containsKey("message")) return null;
        return (Map<String, Object>) entity;
    }

    private boolean existsInList(OwnedFixtureRecord record) {
        long seen = 0;
        java.util.Set<Object> ids = new java.util.HashSet<>();
        for (int page = 1; ; page++) {
            Object entity =
                    get.apply(
                                    record.endpoint()
                                            + "?pageNumber="
                                            + page
                                            + "&pageSize=500&arabicNamePart="
                                            + java.net.URLEncoder.encode(
                                                    record.name(),
                                                    java.nio.charset.StandardCharsets.UTF_8),
                                    Map.of())
                            .get("entity");
            if (!(entity instanceof Map<?, ?> result)
                    || !(result.get("pageData") instanceof List<?> rows)
                    || !(result.get("totalRecords") instanceof Number total))
                throw new AssertionError(
                        "Cannot verify missing owned record through its list: "
                                + record.endpoint());
            int added = 0;
            for (Object value : rows) {
                if (!(value instanceof Map<?, ?> row) || row.get("id") == null)
                    throw new AssertionError("Invalid lookup identity");
                if (record.id().equals(row.get("id"))) return true;
                if (ids.add(row.get("id"))) added++;
            }
            seen += added;
            if (seen >= total.longValue()) return false;
            if (added == 0)
                throw new AssertionError("Store cleanup lookup pagination did not advance");
        }
    }
}
