/* شرح الملف: يثبت جمع أوقات الطلبات والفشل دون حفظ بيانات البحث أو اعتبار زمن المتصفحات المتوازي زمنًا فعليًا واحدًا. */
package Plugin_ERP.Util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RunPerformanceTest {
    @Test
    public void policyRoundsKeepTheirTimingSummariesInSeparateFolders() throws Exception {
        var root = java.nio.file.Files.createTempDirectory("policy-performance-");
        String key = "erp.run.evidence.output", previous = System.getProperty(key);
        try {
            System.setProperty(key, root.resolve("NET_BASED").toString());
            RunPerformance.stage("setup", "first-policy", 10, false);
            System.setProperty(key, root.resolve("ITEM_BASED").toString());
            RunPerformance.stage("setup", "second-policy", 20, false);
            RunPerformance.write();
            var first = java.nio.file.Files.readString(root.resolve("NET_BASED/performance.json"));
            var second =
                    java.nio.file.Files.readString(root.resolve("ITEM_BASED/performance.json"));
            Assert.assertTrue(first.contains("first-policy"));
            Assert.assertFalse(first.contains("second-policy"));
            Assert.assertTrue(second.contains("second-policy"));
            Assert.assertFalse(second.contains("first-policy"));
        } finally {
            if (previous == null) System.clearProperty(key);
            else System.setProperty(key, previous);
        }
    }

    @Test
    public void aggregatesAttemptsAndSeparatesPhasesWithoutQueryData() {
        var collector = new RunPerformance.Collector();
        collector.record("setup", "get", "Items?barcode=private", 10, false);
        collector.record("setup", "get", "Items?name=private", 20, true);
        collector.record("cleanup", "delete", "Items", 5, false);
        var rows = collector.snapshot();
        Assert.assertEquals(rows.size(), 2);
        Assert.assertEquals(rows.getFirst().calls(), 2L);
        Assert.assertEquals(rows.getFirst().totalNanos(), 30L);
        Assert.assertEquals(rows.getFirst().maxNanos(), 20L);
        Assert.assertEquals(rows.getFirst().failedCalls(), 1L);
        Assert.assertEquals(rows.getFirst().target(), "Items");
    }
}
