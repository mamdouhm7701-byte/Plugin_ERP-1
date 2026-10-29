/* شرح الملف: حذف سجل أساسي بعد مطابقة معرفه وعلامة التجربة، والتحقق من غيابه عند فقد استجابة الحذف.
 * فائدته: يبقي السجل محفوظًا للاسترجاع إذا تغيرت هويته أو فشل الحذف بدل إخفاء الخطأ.
 */
package TestSupport;

import static TestSupport.BasicDataFixtures.fields;

import TestSupport.BasicDataFixtures.Record;
import TestSupport.BasicDataFixturesApi.ApiFailure;
import java.util.*;

final class BasicDataFixtureDeletion {
    private final BasicDataFixtures data;
    private final BasicDataFixturesApi api;

    BasicDataFixtureDeletion(BasicDataFixtures data, BasicDataFixturesApi api) {
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
            Map<String, Object> verifiedRecord = null;
            boolean readCompleted = false;
            try {
                Map<String, Object> owned = cleanupIdentity(record);
                if (owned == null) return;
                if (!record.name().equals(owned.get("arabicName"))
                        && !record.name().equals(owned.get("englishName"))
                        && !record.name().equals(owned.get("notes"))
                        && !record.name().equals(owned.get("fullName")))
                    throw new AssertionError(
                            "Refusing cleanup: fixture ownership changed for " + record.id());
                api.call("delete", record.endpoint(), fields("id", record.id()));
                verifiedRecord = data.read(record);
                if (verifiedRecord == null) return;
                readCompleted = true;
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
                Map<String, Object> existing = readCompleted ? verifiedRecord : data.read(record);
                if (existing == null) {
                    return;
                }
                if (!record.name().equals(existing.get("arabicName"))
                        && !record.name().equals(existing.get("englishName"))
                        && !record.name().equals(existing.get("notes"))
                        && !record.name().equals(existing.get("fullName"))) {
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

    private Map<String, Object> cleanupIdentity(Record record) {
        Map<String, Object> stored = data.read(record);
        if (stored != null && !java.util.Objects.equals(record.id(), stored.get("id")))
            throw new AssertionError("Owned fixture identity changed");
        return stored;
    }
}
