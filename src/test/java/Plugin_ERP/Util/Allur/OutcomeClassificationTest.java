/* شرح الملف: مراجعة تصنيف نتائج التشغيل والتأكد من عدم اتهام التطبيق بلا دليل.
 * فائدته: يميز التخطي ومشاكل البيئة والكود عن العيوب المؤكدة والنتائج غير المحسومة.
 */
package Plugin_ERP.Util.Allur;

import Plugin_ERP.Util.Allur.OutcomeClassification.Kind;
import Plugin_ERP.Util.Allur.ReviewJson.*;
import java.nio.file.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class OutcomeClassificationTest {
    @Test
    public void assertionsAndTimeoutsAloneDoNotProveAnApplicationDefect() {
        Assert.assertEquals(
                classify("failed", "VAT expected115 actual100", "", ""), Kind.NEEDS_REVIEW);
        Assert.assertEquals(
                classify("broken", "Expected condition failed", "", ""), Kind.NEEDS_REVIEW);
        Assert.assertEquals(classify("failed", "KNOWN_BUG:UNKNOWN-1", "", ""), Kind.NEEDS_REVIEW);
        Assert.assertEquals(
                classify("broken", "API returned NullPointerException", "", ""), Kind.NEEDS_REVIEW);
    }

    @Test
    public void skippedAndPassedDoNotInheritAnOldApplicationDefect() {
        Assert.assertEquals(
                classify("skipped", "VAT mismatch", "", "APPLICATION_DEFECT"), Kind.NOT_EXECUTED);
        Assert.assertEquals(classify("passed", "", "", "APPLICATION_DEFECT"), Kind.PASSED);
    }

    @Test
    public void environmentAndTestCodeHaveSeparateEvidence() {
        Assert.assertEquals(classify("broken", "Network Error", "", ""), Kind.ENVIRONMENT);
        Assert.assertEquals(
                classify("broken", "SessionNotCreatedException", "", ""), Kind.ENVIRONMENT);
        Assert.assertEquals(
                classify("broken", "InvalidSelectorException", "", ""), Kind.TEST_DEFECT);
        Assert.assertEquals(
                classify(
                        "broken",
                        "Cannot invoke method",
                        "java.lang.NullPointerException\n\tat Plugin_ERP.Bots.ElementActions.click(ElementActions.java:5)",
                        ""),
                Kind.TEST_DEFECT);
        Assert.assertEquals(
                classify("failed", "VAT mismatch", "", "APPLICATION_DEFECT"),
                Kind.APPLICATION_DEFECT);
        Assert.assertEquals(
                classify("broken", "Network Error", "", "APPLICATION_DEFECT"), Kind.ENVIRONMENT);
    }

    @Test
    public void summaryCountsAndRowsUseTheSameClassificationWithoutChangingStatuses()
            throws Exception {
        Path root = Files.createTempDirectory("outcome-review-");
        Path results = Files.createDirectory(root.resolve("results"));
        String[] statuses = {"failed", "broken", "broken", "skipped", "failed", "passed"};
        String[] messages = {
            "confirmed VAT mismatch",
            "Network Error",
            "InvalidSelectorException",
            "setup failed",
            "Expected condition failed",
            ""
        };
        for (int i = 0; i < statuses.length; i++)
            Files.writeString(
                    results.resolve(i + "-result.json"),
                    new JSONObject()
                            .put("fullName", "Test.case" + i)
                            .put("status", statuses[i])
                            .put("statusDetails", new JSONObject().put("message", messages[i]))
                            .toString());
        JSONArray issues =
                new JSONArray()
                        .put(
                                new JSONObject()
                                        .put("id", "ERP-1")
                                        .put("method", "Test.case0")
                                        .put(
                                                "failureContains",
                                                new JSONArray().put("confirmed VAT mismatch"))
                                        .put("kind", "APPLICATION_DEFECT")
                                        .put("confirmed", true));
        Path output = root.resolve("review");
        RunFailureOverview.write(results, output, "one", issues);
        JSONObject summary = new JSONObject(Files.readString(output.resolve("summary.json")));
        for (Kind kind : Kind.values())
            Assert.assertEquals(summary.getJSONObject("outcomeCounts").getInt(kind.label()), 1);
        JSONArray rows = summary.getJSONArray("cases");
        Assert.assertEquals(rows.length(), 5);
        Assert.assertEquals(rows.getJSONObject(0).getString("status"), "failed");
        Assert.assertEquals(
                rows.getJSONObject(0).getString("outcome"), Kind.APPLICATION_DEFECT.label());
        Assert.assertTrue(Files.readString(output.resolve("summary.html")).contains("سبب التصنيف"));
        JSONArray unconfirmed =
                new JSONArray()
                        .put(
                                new JSONObject()
                                        .put("method", "Test.case0")
                                        .put("id", "ERP-1")
                                        .put(
                                                "failureContains",
                                                new JSONArray().put("confirmed VAT mismatch"))
                                        .put("kind", "APPLICATION_DEFECT")
                                        .put("confirmed", false));
        RunFailureOverview.write(results, output, "two", unconfirmed);
        summary = new JSONObject(Files.readString(output.resolve("summary.json")));
        Assert.assertEquals(
                summary.getJSONObject("outcomeCounts").getInt(Kind.APPLICATION_DEFECT.label()), 0);
    }

    private static Kind classify(String status, String message, String trace, String confirmed) {
        return OutcomeClassification.classify(status, message, trace, confirmed).kind();
    }
}
