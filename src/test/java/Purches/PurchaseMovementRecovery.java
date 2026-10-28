/* شرح الملف: استرجاع حركات المشتريات التي ضاعت استجابة حفظها أو تعديلها، مع رفض الهوية الملتبسة.
 * فائدته: يفصل البحث والتحقق عن حذف البيانات؛ مالك التجربة يحتفظ بسجل الاسترجاع حتى نجاح هذه المراجعة.
 */
package Purches;

import static Purches.PurchaseTestData.fields;

import Purches.PurchaseTestData.Record;
import java.util.*;
import java.util.function.BiFunction;

final class PurchaseMovementRecovery {
    private final PurchaseTestData data;
    private final List<Record> created;
    private final BiFunction<String, Map<String, Object>, Map<String, Object>> get;

    PurchaseMovementRecovery(
            PurchaseTestData data,
            List<Record> created,
            BiFunction<String, Map<String, Object>, Map<String, Object>> get) {
        this.data = data;
        this.created = created;
        this.get = get;
    }

    @SuppressWarnings("unchecked")
    void recover(Map<String, Object> attempt) {
        String endpoint = (String) attempt.get("endpoint"), name = (String) attempt.get("name");
        Map<String, Object> values = (Map<String, Object>) attempt.get("values");
        List<Record> matches = new ArrayList<>();
        Object id = attempt.get("id");
        if (id != null) {
            Record candidate = new Record(endpoint, id, name, values);
            Map<String, Object> stored = data.read(candidate);
            if (stored != null) {
                boolean verified =
                        data.owns(candidate, stored)
                                || created.stream()
                                        .anyMatch(
                                                r ->
                                                        r.endpoint().equals(endpoint)
                                                                && r.id().equals(id)
                                                                && data.owns(r, stored));
                if (!verified)
                    throw new AssertionError("Cannot verify uncertain sales update ownership");
                matches.add(candidate);
            }
        } else {
            String list = "PurchaseLists";
            String type =
                    switch (endpoint) {
                        case "Purchases" -> "Purch";
                        case "PurchaseOrders" -> "PurchOrder";
                        case "ReturnPurchases" -> "ReturnPurch";
                        default -> throw new AssertionError("Unknown purchase endpoint");
                    };
            java.util.Set<Object> seen = new java.util.HashSet<>();
            for (int page = 1; ; page++) {
                Object entity =
                        get.apply(
                                        list
                                                + "?pageNumber="
                                                + page
                                                + "&pageSize=500&transactionType="
                                                + type
                                                + "&dealerId="
                                                + values.get("dealerId"),
                                        fields())
                                .get("entity");
                if (!(entity instanceof Map<?, ?> result)
                        || !(result.get("pageData") instanceof List<?> rows)
                        || !(result.get("totalRecords") instanceof Number total))
                    throw new AssertionError(
                            "Cannot verify uncertain sales create through its list");
                int added = 0;
                for (Object value : rows) {
                    if (!(value instanceof Map<?, ?> row) || row.get("id") == null)
                        throw new AssertionError("Sales list has no record identity");
                    if (!seen.add(row.get("id"))) continue;
                    added++;
                    if (created.stream()
                            .anyMatch(
                                    r ->
                                            r.endpoint().equals(endpoint)
                                                    && java.util.Objects.equals(
                                                            r.id(), row.get("id")))) continue;
                    if ((!row.containsKey("notes") && !row.containsKey("storeId"))
                            || name.equals(row.get("notes"))
                            || (java.util.Objects.equals(values.get("storeId"), row.get("storeId"))
                                    && type.equalsIgnoreCase(
                                            String.valueOf(row.get("transactionType"))))) {
                        Record candidate = new Record(endpoint, row.get("id"), name, values);
                        Map<String, Object> stored = data.read(candidate);
                        if (stored != null && data.owns(candidate, stored)) matches.add(candidate);
                    }
                }
                if (seen.size() >= total.longValue()) break;
                if (added == 0)
                    throw new AssertionError("Uncertain sales lookup pagination did not advance");
            }
        }
        if (matches.size() > 1)
            throw new AssertionError("Ambiguous uncertain sales create: " + name);
        if (matches.size() == 1) {
            Record found = matches.get(0);
            Map<String, Object> stored = data.read(found);
            if (stored == null)
                throw new AssertionError("Recovered sales document disappeared before tracking");
            String actualName =
                    stored.get("notes") instanceof String note && note.startsWith("QA-PU-")
                            ? note
                            : name;
            data.track(endpoint, found.id(), actualName, stored);
        }
    }
}
