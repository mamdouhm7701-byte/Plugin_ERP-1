/* شرح الملف: قراءة البيانات الأساسية وتأكيد غياب الفاتورة تحت الإجراء من قائمتها إذا فشلت تفاصيلها بخطأ500.
 * فائدته: يحافظ على اختلاف قواعد البيانات الأساسية، بما فيها الاسم الكامل، دون اعتبار رد غير مفهوم دليلًا على الحذف.
 */
package TestSupport;

import static TestSupport.BasicDataFixtures.fields;

import TestSupport.BasicDataFixtures.Record;
import TestSupport.BasicDataFixturesApi.ApiFailure;
import java.util.*;

final class BasicDataFixtureReads {
    private final BasicDataFixturesApi api;

    BasicDataFixtureReads(BasicDataFixturesApi api) {
        this.api = api;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> read(Record record) {
        Object entity;
        try {
            entity = api.call("get", record.endpoint(), fields("id", record.id())).get("entity");
        } catch (ApiFailure failure) {
            if (failure.status == 404 || failure.status == 410) return null;
            if (failure.status == 500
                    && record.endpoint().equals("UnderReviews")
                    && !underReviewExistsInList(record.id())) return null;
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
                && dto.get("fullName") == null
                && !dto.containsKey("pageData")
                && !dto.containsKey("totalRecords")
                && !dto.containsKey("error")
                && !dto.containsKey("message")) return null;
        return (Map<String, Object>) entity;
    }

    private boolean underReviewExistsInList(Object id) {
        java.util.Set<Object> seen = new java.util.HashSet<>();
        for (int page = 1; ; page++) {
            Object entity =
                    api.call(
                                    "get",
                                    "SalesTransactions?transactionType=UnderReviewTransaction&pageNumber="
                                            + page
                                            + "&pageSize=500",
                                    fields())
                            .get("entity");
            if (!(entity instanceof Map<?, ?> result)
                    || !(result.get("pageData") instanceof List<?> rows)
                    || !(result.get("totalRecords") instanceof Number total))
                throw new AssertionError(
                        "Cannot confirm missing under-review document through its list");
            int added = 0;
            for (Object value : rows) {
                if (!(value instanceof Map<?, ?> row) || row.get("id") == null)
                    throw new AssertionError("Missing under-review list identity");
                if (id.equals(row.get("id"))) return true;
                if (seen.add(row.get("id"))) added++;
            }
            if (seen.size() >= total.longValue()) return false;
            if (added == 0) throw new AssertionError("Under-review pagination did not advance");
        }
    }
}
