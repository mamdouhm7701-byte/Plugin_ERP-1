/* شرح الملف: تسجيل السجل المملوك بعد الحفظ أو تحديثه ونقله لآخر ترتيب التنظيف، مع حفظ التقدم فورًا.
 * فائدته: لا يضيف نفس المعرف مرتين ولا يغيّر بيانات السجلات الأخرى.
 */
package TestSupport;

import java.util.*;

public final class FixtureTracking {
    private FixtureTracking() {}

    public static <R extends OwnedFixtureRecord> void track(
            List<R> created,
            String prefix,
            String rejection,
            String endpoint,
            Object id,
            String note,
            Map<String, Object> values,
            FixtureRecoveryJournal.RecordFactory<R> records,
            Runnable persist) {
        if (note == null || !note.startsWith(prefix)) throw new AssertionError(rejection);
        if (id == null) throw new AssertionError("Saved movement did not return an ID");
        for (int i = 0; i < created.size(); i++)
            if (created.get(i).endpoint().equals(endpoint) && created.get(i).id().equals(id)) {
                created.remove(i);
                created.add(records.create(endpoint, id, note, new LinkedHashMap<>(values)));
                persist.run();
                return;
            }
        created.add(records.create(endpoint, id, note, new LinkedHashMap<>(values)));
        persist.run();
    }
}
