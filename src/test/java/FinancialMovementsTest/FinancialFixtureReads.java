/* شرح الملف: قراءة سجل الحركة المالية والتحقق من غياب العميل عبر القائمة الكاملة إذا فشلت صفحة تفاصيله بخطأ500.
 * فائدته: يفصل دليل وجود السجل عن تجهيز البيانات، ولا يعتبر خطأ الخادم وحده دليل حذف.
 */
package FinancialMovementsTest;

import static FinancialMovementsTest.FinancialTestData.fields;

import FinancialMovementsTest.FinancialFixtureApi.ApiFailure;
import FinancialMovementsTest.FinancialTestData.Record;
import java.util.*;

final class FinancialFixtureReads {
    private final FinancialFixtureApi api;

    FinancialFixtureReads(FinancialFixtureApi api) {
        this.api = api;
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> read(Record record) {
        Object entity;
        try {
            entity = api.call("get", record.endpoint(), fields("id", record.id())).get("entity");
        } catch (ApiFailure failure) {
            if (failure.status == 404 || failure.status == 410) return null;
            // This ERP's Customers detail endpoint throws a null-reference error for a
            // deleted ID. Confirm absence through its complete paged list before accepting it.
            if (failure.status == 500
                    && record.endpoint().equals("Customers")
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

    private boolean existsInList(Record record) {
        long seen = 0;
        java.util.Set<Object> ids = new java.util.HashSet<>();
        for (int page = 1; ; page++) {
            Object entity =
                    api.call(
                                    "get",
                                    record.endpoint() + "?pageNumber=" + page + "&pageSize=500",
                                    fields())
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
                throw new AssertionError("Financial cleanup lookup pagination did not advance");
        }
    }
}
