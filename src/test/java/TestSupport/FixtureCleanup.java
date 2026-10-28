/* شرح الملف: تنظيف السجلات بترتيب محدد، مع الاحتفاظ بالسجلات التابعة المتعذرة والبيانات التي تعتمد عليها للمحاولة التالية.
 * فائدته: يمنع حذف بيانات أساسية ما زالت مطلوبة لحركة لم ينجح تنظيفها، ويسجل تقدم الحذف أولًا بأول.
 */
package TestSupport;

import java.util.*;
import java.util.function.Consumer;

public final class FixtureCleanup {
    private FixtureCleanup() {}

    public static <R extends OwnedFixtureRecord> void run(
            List<R> records,
            Map<String, R> fixtures,
            List<R> ordered,
            Consumer<R> remove,
            Runnable persist) {
        List<String> failures = new ArrayList<>();
        List<R> pending = new ArrayList<>();
        for (R record : ordered) {
            if (pending.stream()
                    .anyMatch(
                            child -> FixtureRecordSearch.references(child.values(), record.id()))) {
                pending.add(record);
                failures.add(
                        record.name()
                                + " ["
                                + record.endpoint()
                                + "/"
                                + record.id()
                                + "]: waiting for dependent fixture cleanup");
                continue;
            }
            try {
                remove.accept(record);
                records.remove(record);
                fixtures.entrySet().removeIf(entry -> entry.getValue().equals(record));
                persist.run();
            } catch (RuntimeException | AssertionError failure) {
                pending.add(record);
                failures.add(
                        record.name()
                                + " ["
                                + record.endpoint()
                                + "/"
                                + record.id()
                                + "]: "
                                + String.valueOf(failure.getMessage()).split("\\R", 2)[0]);
            }
        }
        records.removeIf(record -> !pending.contains(record));
        fixtures.entrySet().removeIf(entry -> !pending.contains(entry.getValue()));
        persist.run();
        if (!failures.isEmpty())
            throw new AssertionError("Fixture cleanup failed: " + String.join("; ", failures));
    }
}
