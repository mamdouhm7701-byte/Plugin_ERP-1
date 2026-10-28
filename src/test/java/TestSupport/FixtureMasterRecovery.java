/* شرح الملف: العثور على سجل أساسي محفوظ بعد ضياع استجابة إنشائه، مع التحقق من الاسم والملكية ومراجع الحساب والصنف.
 * فائدته: يمنع اختيار سجل عشوائي عند تكرار الاسم أو اعتبار قائمة ناقصة دليلًا على غياب السجل؛ لا يحذف أي بيانات.
 */
package TestSupport;

import java.util.*;
import java.util.function.*;

public final class FixtureMasterRecovery {
    private FixtureMasterRecovery() {}

    @FunctionalInterface
    public interface RecordFactory<R> {
        R create(String endpoint, Object id, String name, Map<String, Object> values);
    }

    public record Access<R extends OwnedFixtureRecord>(
            String prefix,
            Set<String> masters,
            Object branchId,
            BiFunction<String, Map<String, Object>, Map<String, Object>> get,
            RecordFactory<R> records,
            Function<R, Map<String, Object>> read,
            BiPredicate<R, Map<String, Object>> owns,
            boolean accountTree) {}

    @SuppressWarnings("unchecked")
    public static <R extends OwnedFixtureRecord> Optional<R> find(
            Map<String, Object> attempt, Access<R> access) {

        String endpoint = (String) attempt.get("endpoint"), name = (String) attempt.get("name");
        Map<String, Object> values = (Map<String, Object>) attempt.get("values");
        if (!access.masters().contains(endpoint) || !name.startsWith(access.prefix()))
            throw new AssertionError("Invalid fixture recovery endpoint or name");
        List<R> matches = new ArrayList<>();
        java.util.Set<Object> seen = new java.util.HashSet<>();
        for (int page = 1; ; page++) {
            Object entity =
                    access.get()
                            .apply(
                                    (endpoint.equals("Accounts") && access.accountTree())
                                            ? "Aggregates/AccountTreeViewModel?showBalance=false&type=Main&branchId="
                                                    + access.branchId()
                                            : endpoint + "?pageNumber=" + page + "&pageSize=500",
                                    Map.of())
                            .get("entity");
            List<?> rows;
            long total;
            if ((endpoint.equals("Accounts") && access.accountTree())) {
                if (!(entity instanceof Map<?, ?> tree)
                        || (!(tree.get("Accounts") instanceof List<?>)
                                && !(tree.get("AccountsTree") instanceof List<?>)))
                    throw new AssertionError("Cannot verify uncertain account through its tree");
                List<Map<String, Object>> found = new ArrayList<>();
                TestSupport.FixtureRecordSearch.findOwned(entity, name, found);
                Map<Object, Map<String, Object>> unique = new LinkedHashMap<>();
                for (var row : found) unique.put(row.get("id"), row);
                rows = new ArrayList<>(unique.values());
                total = rows.size();
            } else if (entity instanceof List<?> list) {
                rows = list;
                total = list.size();
            } else if (entity instanceof Map<?, ?> result
                    && result.get("pageData") instanceof List<?> list
                    && result.get("totalRecords") instanceof Number count) {
                rows = list;
                total = count.longValue();
            } else
                throw new AssertionError(
                        "Cannot verify uncertain fixture through its list: " + endpoint);
            int added = 0;
            for (Object value : rows) {
                if (!(value instanceof Map<?, ?> row)
                        || row.get("id") == null
                        || (!row.containsKey("arabicName") && !row.containsKey("englishName")))
                    throw new AssertionError("Fixture list omits ownership fields: " + endpoint);
                if (!seen.add(row.get("id"))) continue;
                added++;
                if (!name.equals(row.get("arabicName")) && !name.equals(row.get("englishName")))
                    continue;
                R candidate = access.records().create(endpoint, row.get("id"), name, values);
                Map<String, Object> stored = access.read().apply(candidate);
                if (stored == null) continue;
                if (!access.owns().test(candidate, stored))
                    throw new AssertionError("Uncertain fixture ownership changed");
                for (String reference :
                        List.of(
                                "accountId",
                                "parentId",
                                "parentCategoryId",
                                "categoryId",
                                "mainUnitId"))
                    if (values.containsKey(reference)
                            && !java.util.Objects.equals(
                                    values.get(reference), stored.get(reference)))
                        throw new AssertionError(
                                "Uncertain fixture reference changed: " + reference);
                matches.add(candidate);
            }
            if (seen.size() >= total) break;
            if (added == 0)
                throw new AssertionError("Uncertain fixture pagination did not advance");
        }
        if (matches.size() > 1) throw new AssertionError("Ambiguous uncertain fixture: " + name);
        return matches.isEmpty() ? Optional.empty() : Optional.of(matches.getFirst());
    }
}
