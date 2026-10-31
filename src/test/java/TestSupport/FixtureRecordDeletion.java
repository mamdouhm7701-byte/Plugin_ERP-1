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
        Map<String, Object> identity = readConfirmed(record, read);
        if (identity == null) return;
        if (!owns.test(record, identity))
            throw new AssertionError(
                    "Refusing cleanup: fixture ownership changed for " + record.id());
        AssertionError lastFailure = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            boolean notFoundResponse = false;
            try {
                remove.accept(record);
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
                    notFoundResponse = true;
                } else if (!retryable(failure)) {
                    throw failure;
                }
                lastFailure = failure;
            } catch (org.openqa.selenium.TimeoutException failure) {
                lastFailure =
                        new AssertionError(
                                "Delete response timed out for " + record.endpoint(), failure);
            }
            // A timed-out DELETE may have committed. Read back before sending another DELETE.
            Map<String, Object> existing;
            try {
                existing = readConfirmed(record, read);
            } catch (RuntimeException | AssertionError failure) {
                var uncertain =
                        new AssertionError(
                                "تعذر تأكيد نتيجة حذف "
                                        + record.endpoint()
                                        + "/"
                                        + record.id()
                                        + "؛ لم نرسل حذفًا آخر. سجل الاسترجاع محفوظ.",
                                failure);
                if (lastFailure != null) uncertain.addSuppressed(lastFailure);
                throw uncertain;
            }
            if (existing == null) return;
            if (!owns.test(record, existing))
                throw new AssertionError(
                        "Refusing cleanup: fixture ownership changed for " + record.id());
            if (notFoundResponse)
                throw new AssertionError(
                        "Delete endpoint did not remove existing owned record: "
                                + record.endpoint()
                                + "/"
                                + record.id(),
                        lastFailure);
        }
        throw lastFailure;
    }

    private static <R extends OwnedFixtureRecord> Map<String, Object> readConfirmed(
            R record, Function<R, Map<String, Object>> read) {
        for (int attempt = 1; ; attempt++) {
            try {
                return read.apply(record);
            } catch (FixtureApiFailure failure) {
                if (failure.status == 404 || failure.status == 410) return null;
                if (attempt >= 3 || !retryable(failure)) throw failure;
            } catch (org.openqa.selenium.TimeoutException failure) {
                if (attempt >= 3) throw failure;
            }
        }
    }

    private static boolean retryable(FixtureApiFailure failure) {
        return failure.status == 0
                || failure.status == 408
                || failure.status == 429
                || failure.status >= 500;
    }
}
