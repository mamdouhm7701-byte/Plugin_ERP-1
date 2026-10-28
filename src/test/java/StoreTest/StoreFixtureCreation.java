/* شرح الملف: إنشاء سجل تجهيز واحد مع تسجيل محاولته قبل الإرسال، والاحتفاظ به للاسترجاع إذا ضاعت النتيجة.
 * فائدته: يفصل شكل إنشاء البيانات عن إدارة الحالة وتنظيفها، دون إعادة طلب قد يكون حُفظ بالفعل.
 */
package StoreTest;

import static StoreTest.StoreFixtureEndpoints.*;
import static StoreTest.StoreTestData.fields;

import StoreTest.StoreFixtureApi.ApiFailure;
import StoreTest.StoreTestData.Record;
import java.util.*;

final class StoreFixtureCreation {
    private final StoreTestData data;
    private final List<Record> created;
    private final Map<String, Record> fixtures;
    private final List<Map<String, Object>> uncertainDocuments;
    private final String suffix;

    StoreFixtureCreation(
            StoreTestData data,
            List<Record> created,
            Map<String, Record> fixtures,
            List<Map<String, Object>> uncertainDocuments,
            String suffix) {
        this.data = data;
        this.created = created;
        this.fixtures = fixtures;
        this.uncertainDocuments = uncertainDocuments;
        this.suffix = suffix;
    }

    Record create(String key, String endpoint, Map<String, Object> values) {

        if (fixtures.containsKey(key)) return fixtures.get(key);
        String name = key.startsWith("QA-ST-") ? key : "QA-ST-" + key + "-" + suffix;
        values.put("arabicName", name);
        values.put("englishName", name);
        values.put("notes", "Owned Store test fixture");
        Map<String, Object> attempt =
                MASTERS.contains(endpoint)
                        ? data.rememberFixtureAttempt(endpoint, name, values)
                        : null;
        Map<String, Object> response;
        try {
            response = data.call("create", endpoint, values);
        } catch (ApiFailure | org.openqa.selenium.TimeoutException failure) {
            // Retain a partial commit for cleanup; the original creation still fails.
            if (endpoint.equals("Items"))
                try {
                    data.recoverAttemptedItems(
                            List.of(fields("endpoint", endpoint, "values", values)));
                } catch (RuntimeException | AssertionError recovery) {
                    failure.addSuppressed(recovery);
                }
            throw failure;
        }
        Object id = response.get("id");
        if (id == null) throw new AssertionError("Fixture API did not return an ID: " + endpoint);
        Record record = new Record(endpoint, id, name, values);
        created.add(record);
        data.journal();
        if (attempt != null) {
            uncertainDocuments.remove(attempt);
            data.journalAttempts();
        }
        fixtures.put(key, record);
        return record;
    }
}
