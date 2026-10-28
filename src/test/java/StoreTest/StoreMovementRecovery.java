/* شرح الملف: استرجاع حركات المخازن التي ضاعت استجابة حفظها أو تعديلها، مع رفض الهوية الملتبسة.
 * فائدته: يفصل البحث والتحقق عن حذف البيانات؛ مالك التجربة يحتفظ بسجل الاسترجاع حتى نجاح هذه المراجعة.
 */
package StoreTest;

import static StoreTest.StoreTestData.fields;

import StoreTest.StoreTestData.Record;
import java.util.*;
import java.util.function.BiFunction;

final class StoreMovementRecovery {
    private final StoreTestData data;
    private final List<Record> created;
    private final BiFunction<String, Map<String, Object>, Map<String, Object>> get;

    StoreMovementRecovery(
            StoreTestData data,
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
                    throw new AssertionError("Cannot verify uncertain warehouse update ownership");
                matches.add(candidate);
            }
        } else {
            String list =
                    endpoint.equals("DamageAdjustments")
                            ? "DamageAdjustments"
                            : endpoint.equals("ItemTransactions-storageTransfer")
                                    ? "ItemTransactions-StorageTransfer"
                                    : "InventoryTransactions";
            String type =
                    switch (endpoint) {
                        case "AdditionOrders" -> "AdditionOrder";
                        case "ManufacturingOrders" -> "Manufacturing";
                        case "ItemTransactions-ExchangeOrder" -> "ExchangeOrder";
                        case "DamageAdjustments" -> "DamageAdjustment";
                        default -> "StorageTransfer";
                    };
            java.util.Set<Object> seen = new java.util.HashSet<>();
            for (int page = 1; ; page++) {
                Object entity =
                        get.apply(list + "?pageNumber=" + page + "&pageSize=500", fields())
                                .get("entity");
                if (!(entity instanceof Map<?, ?> result)
                        || !(result.get("pageData") instanceof List<?> rows)
                        || !(result.get("totalRecords") instanceof Number total))
                    throw new AssertionError(
                            "Cannot verify uncertain warehouse create through its list");
                int added = 0;
                for (Object value : rows) {
                    if (!(value instanceof Map<?, ?> row) || row.get("id") == null)
                        throw new AssertionError("Warehouse list has no record identity");
                    if (!seen.add(row.get("id"))) continue;
                    added++;
                    if (!row.containsKey("notes") && !row.containsKey("storeId"))
                        throw new AssertionError(
                                "Warehouse list omits fields needed to verify an uncertain create");
                    if (name.equals(row.get("notes"))
                            || (java.util.Objects.equals(values.get("storeId"), row.get("storeId"))
                                    && type.equalsIgnoreCase(
                                            String.valueOf(row.get("transactionType"))))) {
                        Record candidate = new Record(endpoint, row.get("id"), name, values);
                        Map<String, Object> stored = data.read(candidate);
                        if (stored != null) {
                            if (!data.owns(candidate, stored)) {
                                // Shared page stores also contain earlier, already journaled
                                // cases.
                                // They remain tracked for cleanup but are not this uncertain
                                // create.
                                boolean trackedElsewhere =
                                        created.stream()
                                                .anyMatch(
                                                        r ->
                                                                r.endpoint().equals(endpoint)
                                                                        && r.id().equals(
                                                                                        candidate
                                                                                                .id())
                                                                        && data.owns(r, stored));
                                if (trackedElsewhere) continue;
                                throw new AssertionError(
                                        "Cannot verify uncertain warehouse candidate ownership");
                            }
                            matches.add(candidate);
                        }
                    }
                }
                if (seen.size() >= total.longValue()) break;
                if (added == 0)
                    throw new AssertionError(
                            "Uncertain warehouse lookup pagination did not advance");
            }
        }
        if (matches.size() > 1)
            throw new AssertionError("Ambiguous uncertain warehouse create: " + name);
        if (matches.size() == 1) {
            Record found = matches.get(0);
            Map<String, Object> stored = data.read(found);
            if (stored == null)
                throw new AssertionError(
                        "Recovered warehouse document disappeared before tracking");
            String actualName =
                    stored.get("notes") instanceof String note && note.startsWith("QA-ST-")
                            ? note
                            : name;
            data.track(endpoint, found.id(), actualName, stored);
        }
    }
}
