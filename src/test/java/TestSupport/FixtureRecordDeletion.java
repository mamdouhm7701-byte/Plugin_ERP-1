/* شرح الملف: حذف سجل مؤكد الملكية، والتحقق بالقراءة بعد كل محاولة أو استجابة ضائعة قبل تكرار الحذف.
 * فائدته: يبقي فشل الصلاحيات أو تغيّر الملكية ظاهرًا، ولا يخفيه كتنظيف ناجح.
 */
package TestSupport;

import java.util.*;
import java.util.function.*;

public final class FixtureRecordDeletion {
    private FixtureRecordDeletion() {}

    public static <R extends OwnedFixtureRecord> void delete(
            R record,
            Function<R, Map<String, Object>> read,
            BiPredicate<R, Map<String, Object>> owns,
            Consumer<R> remove) {
        Map<String, Object> identity = read.apply(record);
        if (identity == null) return;
        if (!owns.test(record, identity))
            throw new AssertionError(
                    "Refusing cleanup: fixture ownership changed for " + record.id());
        AssertionError lastFailure = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            Map<String, Object> verifiedRecord = null;
            boolean readCompleted = false;
            try {
                remove.accept(record);
                verifiedRecord = read.apply(record);
                if (verifiedRecord == null) return;
                readCompleted = true;
                lastFailure =
                        new AssertionError(
                                "Delete did not remove owned record: "
                                        + record.endpoint()
                                        + "/"
                                        + record.id());
            } catch (FixtureApiFailure failure) {
                if (failure.status == 404
                        || failure.status == 410
                        || (failure.status == 400
                                && failure.getMessage().contains("Common.ObjectNotFound"))) {
                    if (read.apply(record) == null) return;
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
                Map<String, Object> existing = readCompleted ? verifiedRecord : read.apply(record);
                if (existing == null) {
                    return;
                }
                if (!owns.test(record, existing)) {
                    throw new AssertionError(
                            "Refusing cleanup: fixture ownership changed for " + record.id());
                }
            } catch (FixtureApiFailure failure) {
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

    private static boolean retryable(FixtureApiFailure failure) {
        return failure.status == 0
                || failure.status == 408
                || failure.status == 429
                || failure.status >= 500;
    }
}
