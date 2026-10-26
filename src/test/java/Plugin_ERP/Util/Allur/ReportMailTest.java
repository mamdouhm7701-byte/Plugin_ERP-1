package Plugin_ERP.Util.Allur;

import java.nio.file.Files;
import java.util.zip.ZipFile;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportMailTest {
    @Test
    public void acceptsMultipleRecipientsAndRejectsMalformedAddresses() throws Exception {
        Assert.assertEquals(ReportMail.recipients("one@gmail.com, two@gmail.com").length, 2);
        Assert.expectThrows(Exception.class, () -> ReportMail.recipients(""));
        Assert.expectThrows(Exception.class, () -> ReportMail.recipients("invalid@@gmail.com"));
    }

    @Test
    public void automaticMailRequiresOptInAndRecipients() {
        var config = new java.util.Properties();
        Assert.assertFalse(ReportMail.enabled(config));
        config.setProperty("enabled", "true");
        Assert.assertFalse(ReportMail.enabled(config));
        config.setProperty("to", "   ");
        Assert.assertFalse(ReportMail.enabled(config));
        config.setProperty("to", "recipient@example.com");
        Assert.assertTrue(ReportMail.enabled(config));
        config.setProperty("enabled", "false");
        Assert.assertFalse(ReportMail.enabled(config));
    }

    @Test
    public void missingRecipientFailsBeforeCredentialsOrReportPackaging() {
        var config = new java.util.Properties();
        var failure =
                Assert.expectThrows(
                        IllegalArgumentException.class,
                        () -> ReportMail.validateConfiguration(config));
        Assert.assertEquals(failure.getMessage(), "Report recipient required");
        config.setProperty("to", "   ");
        Assert.expectThrows(
                IllegalArgumentException.class, () -> ReportMail.validateConfiguration(config));
    }

    @Test
    public void completeConfigurationIsValidatedWithoutSendingMail() throws Exception {
        var config = new java.util.Properties();
        config.setProperty("to", "one@example.com,two@example.com");
        Assert.expectThrows(
                IllegalStateException.class, () -> ReportMail.validateConfiguration(config));
        config.setProperty("username", "sender@example.com");
        config.setProperty("password", "synthetic-test-value");
        ReportMail.validateConfiguration(config);
    }

    @Test
    public void zipPreservesNestedReportAssets() throws Exception {
        var root = Files.createTempDirectory("mail-archive-test-");
        try {
            var report = Files.createDirectories(root.resolve("report/assets"));
            Files.writeString(report.resolve("test.txt"), "current run");
            Files.writeString(root.resolve("report/index.html"), "report");
            var archive = ReportMail.archive(root.resolve("report"), root.resolve("report.zip"));
            try (var zip = new ZipFile(archive.toFile())) {
                Assert.assertEquals(zip.size(), 2);
                Assert.assertNotNull(zip.getEntry("index.html"));
                Assert.assertEquals(
                        new String(
                                zip.getInputStream(zip.getEntry("assets/test.txt")).readAllBytes()),
                        "current run");
            }
        } finally {
            try (var paths = Files.walk(root)) {
                for (var path : paths.sorted(java.util.Comparator.reverseOrder()).toList())
                    Files.deleteIfExists(path);
            }
        }
    }
}
