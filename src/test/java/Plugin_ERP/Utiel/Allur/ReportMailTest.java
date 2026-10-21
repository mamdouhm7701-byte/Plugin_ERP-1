package Plugin_ERP.Utiel.Allur;

import java.nio.file.Files;
import java.util.zip.ZipFile;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportMailTest {
    @Test public void acceptsMultipleRecipientsAndRejectsMalformedAddresses() throws Exception {
        Assert.assertEquals(ReportMail.recipients("one@gmail.com, two@gmail.com").length, 2);
        Assert.expectThrows(Exception.class, () -> ReportMail.recipients(""));
        Assert.expectThrows(Exception.class, () -> ReportMail.recipients("invalid@@gmail.com"));
    }

    @Test public void zipPreservesNestedReportAssets() throws Exception {
        var root = Files.createTempDirectory("mail-archive-test-");
        try {
            var report = Files.createDirectories(root.resolve("report/assets"));
            Files.writeString(report.resolve("test.txt"), "current run");
            Files.writeString(root.resolve("report/index.html"), "report");
            var archive = ReportMail.archive(root.resolve("report"), root.resolve("report.zip"));
            try (var zip = new ZipFile(archive.toFile())) {
                Assert.assertEquals(zip.size(), 2);
                Assert.assertNotNull(zip.getEntry("index.html"));
                Assert.assertEquals(new String(zip.getInputStream(zip.getEntry("assets/test.txt")).readAllBytes()), "current run");
            }
        } finally {
            try (var paths = Files.walk(root)) {
                for (var path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        }
    }
}
