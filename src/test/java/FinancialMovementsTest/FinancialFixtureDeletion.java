/* شرح الملف: حذف سجلات الحركات المالية المملوكة، والتحقق من نتيجة الحذف بعد الاستجابة الضائعة قبل إعادة المحاولة.
 * فائدته: يفصل قواعد التنظيف عن إنشاء البيانات، مع إبقاء فشل الملكية أو الصلاحيات ظاهرًا.
 */
package FinancialMovementsTest;

import static FinancialMovementsTest.FinancialTestData.fields;

import FinancialMovementsTest.FinancialFixtureApi.ApiFailure;
import FinancialMovementsTest.FinancialTestData.Record;
import java.util.*;

final class FinancialFixtureDeletion {
    private final FinancialTestData data;
    private final FinancialFixtureApi api;

    FinancialFixtureDeletion(FinancialTestData data, FinancialFixtureApi api) {
        this.data = data;
        this.api = api;
    }

    private static boolean retryable(ApiFailure failure) {
        return failure.status == 0
                || failure.status == 408
                || failure.status == 429
                || failure.status >= 500;
    }

    void remove(Record record) {
        AssertionError lastFailure = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                Map<String, Object> owned = cleanupIdentity(record);
                if (owned == null) return;
                if (!record.name().equals(owned.get("arabicName"))
                        && !record.name().equals(owned.get("englishName"))
                        && !record.name().equals(owned.get("notes")))
                    throw new AssertionError(
                            "Refusing cleanup: fixture ownership changed for " + record.id());
                api.call("delete", record.endpoint(), fields("id", record.id()));
                if (data.read(record) == null) return;
                lastFailure =
                        new AssertionError(
                                "Delete did not remove owned record: "
                                        + record.endpoint()
                                        + "/"
                                        + record.id());
            } catch (ApiFailure failure) {
                if (failure.status == 404
                        || failure.status == 410
                        || (failure.status == 400
                                && failure.getMessage().contains("Common.ObjectNotFound"))) {
                    if (data.read(record) == null) return;
                    throw new AssertionError(
                            "Delete endpoint did not remove existing owned record: "
                                    + record.endpoint()
                                    + "/"
                                    + record.id(),
                            failure);
                }
                if (!retryable(failure)) throw failure;
                lastFailure = failure;
            } catch (org.openqa.selenium.TimeoutException failure) {
                lastFailure =
                        new AssertionError(
                                "Delete response timed out for " + record.endpoint(), failure);
            }
            // A timed-out DELETE may have committed. Read back before sending another DELETE.
            try {
                Map<String, Object> existing = data.read(record);
                if (existing == null) {
                    return;
                }
                if (!record.name().equals(existing.get("arabicName"))
                        && !record.name().equals(existing.get("notes"))) {
                    throw new AssertionError(
                            "Refusing cleanup: fixture ownership changed for " + record.id());
                }
            } catch (ApiFailure failure) {
                if (failure.status == 404 || failure.status == 410) return;
                if (!retryable(failure)) throw failure;
                lastFailure = failure;
            } catch (org.openqa.selenium.TimeoutException failure) {
                lastFailure =
                        new AssertionError(
                                "Could not verify timed-out deletion of " + record.endpoint(),
                                failure);
            }
        }
        throw lastFailure;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> cleanupIdentity(Record record) {
        try {
            return data.read(record);
        } catch (ApiFailure failure) {
            if (failure.status != 500 || !record.endpoint().equals("Customers")) throw failure;
            java.util.Set<Object> seen = new java.util.HashSet<>();
            for (int page = 1; ; page++) {
                Object entity =
                        api.call("get", "Customers?pageNumber=" + page + "&pageSize=500", fields())
                                .get("entity");
                if (!(entity instanceof Map<?, ?> result)
                        || !(result.get("pageData") instanceof List<?> rows)
                        || !(result.get("totalRecords") instanceof Number total)) throw failure;
                int added = 0;
                for (Object value : rows) {
                    if (!(value instanceof Map<?, ?> row) || row.get("id") == null) throw failure;
                    if (record.id().equals(row.get("id"))) return (Map<String, Object>) row;
                    if (seen.add(row.get("id"))) added++;
                }
                if (seen.size() >= total.longValue()) return null;
                if (added == 0) throw failure;
            }
        }
    }
}
