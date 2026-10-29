/* شرح الملف: يثبت أن حفظ نفس سجل الاسترجاع لا يعيد الكتابة، مع بقاء رفض تغيير ملف فرع آخر. */
package TestSupport;

import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureJournalEfficiencyTest {
    @Test
    public void unchangedJournalIsNotReplacedButChangedProgressIsPersisted() throws Exception {
        Path path = Files.createTempDirectory("journal-efficiency").resolve("records.json");
        FixtureJournals.write(path, List.of());
        var known = FileTime.fromMillis(1_000_000);
        Files.setLastModifiedTime(path, known);
        FixtureJournals.write(path, List.of());
        Assert.assertEquals(Files.getLastModifiedTime(path), known);
        FixtureJournals.write(
                path,
                List.of(
                        Map.of(
                                "origin",
                                "https://erp.example",
                                "branchId",
                                "owned",
                                "id",
                                "record")));
        Assert.assertNotEquals(Files.getLastModifiedTime(path), known);
        Assert.assertTrue(Files.readString(path).contains("record"));
    }
}
