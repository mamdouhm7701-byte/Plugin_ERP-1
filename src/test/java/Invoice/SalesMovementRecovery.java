/* شرح الملف: استرجاع حركات المبيعات التي ضاعت استجابة حفظها أو تعديلها، مع رفض الهوية الملتبسة.
 * فائدته: يفصل البحث والتحقق عن حذف البيانات؛ مالك التجربة يحتفظ بسجل الاسترجاع حتى نجاح هذه المراجعة.
 */
package Invoice;

import static Invoice.SalesTestData.fields;

import Invoice.SalesTestData.Record;
import java.util.*;
import java.util.function.BiFunction;

final class SalesMovementRecovery {
    private final SalesTestData data;
    private final List<Record> created;
    private final BiFunction<String, Map<String, Object>, Map<String, Object>> get;

    SalesMovementRecovery(
            SalesTestData data,
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
            String list =
                    endpoint.equals("ConcreteDelivery")
                            ? "ConcreteDelivery"
                            : endpoint.equals("Purchases") ? "PurchaseLists" : "SalesTransactions";
            String type =
                    endpoint.equals("ConcreteDelivery")
                            ? "ConcreteDelivery"
                            : endpoint.equals("Purchases")
                                    ? "Purch"
                                    : endpoint.equals("ItemTransactions-ConcreteSales")
                                            ? "Sales"
                                            : endpoint.substring("ItemTransactions-".length());
            java.util.Set<Object> seen = new java.util.HashSet<>();
            for (int page = 1; ; page++) {
                String query = list + "?pageNumber=" + page + "&pageSize=500";
                if (endpoint.equals("Purchases")) {
                    query += "&transactionType=Purch";
                    if (values.get("dealerId") != null)
                        query += "&dealerId=" + values.get("dealerId");
                }
                Object entity = get.apply(query, fields()).get("entity");
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
                    boolean summaryOnly = !row.containsKey("notes") && !row.containsKey("storeId");
                    if (summaryOnly && !endpoint.equals("Purchases"))
                        throw new AssertionError(
                                "Sales list omits fields needed to verify an uncertain create");
                    if (created.stream()
                            .anyMatch(
                                    r ->
                                            r.endpoint().equals(endpoint)
                                                    && java.util.Objects.equals(
                                                            r.id(), row.get("id")))) continue;
                    if ((summaryOnly && endpoint.equals("Purchases"))
                            || name.equals(row.get("notes"))
                            || (java.util.Objects.equals(values.get("storeId"), row.get("storeId"))
                                    && type.equalsIgnoreCase(
                                            String.valueOf(row.get("transactionType"))))) {
                        Record candidate = new Record(endpoint, row.get("id"), name, values);
                        Map<String, Object> stored = data.read(candidate);
                        if (stored != null) {
                            if (!data.owns(candidate, stored)) {
                                if (endpoint.equals("Purchases") && summaryOnly) continue;
                                throw new AssertionError(
                                        "Cannot verify uncertain sales candidate ownership");
                            }
                            matches.add(candidate);
                        }
                    }
                }
                if (seen.size() >= total.longValue()) break;
                if (added == 0)
                    throw new AssertionError("Uncertain sales lookup pagination did not advance");
            }
        }
        if (matches.size() > 1)
            throw new AssertionError("Ambiguous uncertain sales create: " + name);
        if (matches.isEmpty() && Boolean.TRUE.equals(attempt.get("creationTimedOut")))
            throw new AssertionError(
                    "Timed-out sales create is still unresolved; retaining its journal and parent fixtures: "
                            + name);
        if (matches.size() == 1) {
            Record found = matches.get(0);
            Map<String, Object> stored = data.read(found);
            if (stored == null)
                throw new AssertionError("Recovered sales document disappeared before tracking");
            String actualName =
                    stored.get("notes") instanceof String note && note.startsWith("QA-SL-")
                            ? note
                            : name;
            data.track(endpoint, found.id(), actualName, stored);
        }
    }
}
