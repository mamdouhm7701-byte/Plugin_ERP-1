/* شرح الملف: يسجل وقت كل سجل أثناء التنظيف لتمييز بطء الحذف الحقيقي عن وقت الاختبارات، دون تغيير ترتيب الحذف أو التعامل مع الفشل. */
package TestSupport;

import java.nio.file.*;
import java.util.*;

public final class FixtureCleanupTiming {
    private FixtureCleanupTiming() {}

    public static void record(OwnedFixtureRecord record, long started, boolean removed) {
        String folder = System.getProperty("erp.run.evidence.output", "");
        if (folder.isBlank()) return;
        try {
            Path project = Path.of("").toAbsolutePath().normalize();
            Path output = Path.of(folder).toAbsolutePath().normalize();
            if (!output.startsWith(project.resolve("target")))
                throw new IllegalArgumentException("Cleanup timing must stay inside target");
            Files.createDirectories(output);
            var row =
                    new org.json.JSONObject(
                            Map.of(
                                    "endpoint",
                                    record.endpoint(),
                                    "id",
                                    record.id().toString(),
                                    "name",
                                    record.name(),
                                    "removed",
                                    removed,
                                    "milliseconds",
                                    (System.nanoTime() - started) / 1_000_000));
            synchronized (FixtureCleanupTiming.class) {
                Files.writeString(
                        output.resolve("cleanup-record-times.jsonl"),
                        row + System.lineSeparator(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            }
        } catch (Exception failure) {
            Plugin_ERP.Bots.LogUtils.warn(
                    "Cleanup timing could not be saved; cleanup outcome is unchanged.");
        }
    }
}
