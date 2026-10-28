/* شرح الملف: جمع سجل بيانات التجربة ومحاولات الحفظ والتتبع في نفس النطاق المملوك دون ربطها بواجهة شاشة معينة.
 * فائدته: يحفظ تقدم الاسترجاع فورًا ويحافظ على علامة التجربة وإعداداتها عند استكمال تشغيل متوقف.
 */
package TestSupport;

import java.util.*;

public final class OwnedFixtureState<R extends OwnedFixtureRecord> {
    private final FixtureRecoveryJournal<R> journal;
    private final FixtureAttemptCapture capture;
    private final List<R> created;
    private final FixtureRecoveryJournal.RecordFactory<R> factory;
    private final String prefix;

    public OwnedFixtureState(
            FixtureRecoveryJournal.Location location,
            List<R> created,
            List<Map<String, Object>> attempts,
            FixtureRecoveryJournal.RecordFactory<R> factory,
            Set<String> movements,
            Set<String> masters,
            FixtureAttemptCapture.Policy policy) {
        this.created = created;
        this.factory = factory;
        prefix = location.prefix();
        journal = new FixtureRecoveryJournal<>(location, created, attempts, factory);
        capture =
                new FixtureAttemptCapture(
                        new FixtureAttemptCapture.Owner(
                                location.origin(), location.branchId(), prefix, movements, masters),
                        created,
                        attempts,
                        journal::persistAttempts,
                        policy);
    }

    public void resume() throws java.io.IOException {
        journal.resume();
    }

    public void persistRecords() {
        journal.persistRecords();
    }

    public void persistAttempts() {
        journal.persistAttempts();
    }

    public FixtureAttemptCapture capture() {
        return capture;
    }

    public void track(
            String rejection, String endpoint, Object id, String note, Map<String, Object> values) {
        FixtureTracking.track(
                created,
                prefix,
                rejection,
                endpoint,
                id,
                note,
                values,
                factory,
                journal::persistRecords);
    }
}
