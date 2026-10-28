/* شرح الملف: حذف سجلات العملاء والموردين والمناديب المملوكة، والتحقق من نتيجة الحذف بعد الاستجابة الضائعة قبل إعادة المحاولة.
 * فائدته: يفصل قواعد التنظيف عن إنشاء البيانات، مع إبقاء فشل الملكية أو الصلاحيات ظاهرًا.
 */
package DealerModelTest;

import static DealerModelTest.DealerTestData.fields;

import DealerModelTest.DealerFixtureApi.ApiFailure;
import DealerModelTest.DealerTestData.Record;
import java.util.*;

final class DealerFixtureDeletion {
    private final DealerTestData data;
    private final DealerFixtureApi api;

    DealerFixtureDeletion(DealerTestData data, DealerFixtureApi api) {
        this.data = data;
        this.api = api;
    }

    static boolean retryable(ApiFailure failure) {
        return failure.status == 0
                || failure.status == 408
                || failure.status == 429
                || failure.status >= 500;
    }

    void remove(Record record) {
        AssertionError lastFailure = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                Map<String, Object> owned = data.read(record);
                if (owned == null) return;
                if (!record.name().equals(owned.get("arabicName"))
                        && !record.name().equals(owned.get("englishName"))
                        && !record.name().equals(owned.get("notes")))
                    throw new AssertionError(
                            "Refusing cleanup: fixture ownership changed for " + record.id());
                api.call("delete", record.endpoint(), fields("id", record.id()));
                return;
            } catch (ApiFailure failure) {
                if (failure.status == 404 || failure.status == 410) return;
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
                if (!record.name().equals(existing.get("arabicName"))) {
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
}
