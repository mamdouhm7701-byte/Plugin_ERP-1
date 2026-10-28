/* شرح الملف: التأكد من وضوح سبب التوقف وحفظ الدليل وحساب التكرار وعدم إغلاق العيب بسبب تخطي الحالة.
 * فائدته: يمنع ملخصًا مضللًا أو فقدان دليل الرن السابق دون تشغيل البرنامج الحقيقي.
 */
package Plugin_ERP.Util.Allur;

import Plugin_ERP.Util.Allur.ReviewJson.*;
import java.nio.file.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FailureReviewTest {
    @Test
    public void missingAllureContextDuringATestIsNotHiddenAsSetupNoise() {
        var original = io.qameta.allure.Allure.getLifecycle();
        Assert.assertNotNull(org.testng.Reporter.getCurrentTestResult());
        try {
            io.qameta.allure.Allure.setLifecycle(new io.qameta.allure.AllureLifecycle());
            var event =
                    org.apache.logging.log4j.core.impl.Log4jLogEvent.newBuilder()
                            .setMessage(
                                    new org.apache.logging.log4j.message.SimpleMessage(
                                            "Could not start step: no test case running"))
                            .build();
            Assert.assertEquals(
                    new AllureContextNoiseFilter().filter(event),
                    org.apache.logging.log4j.core.Filter.Result.NEUTRAL);
        } finally {
            io.qameta.allure.Allure.setLifecycle(original);
        }
    }

    @Test
    public void activeTestLifecycleErrorsRemainVisibleToLog4j() {
        Assert.assertTrue(
                io.qameta.allure.Allure.getLifecycle().getCurrentTestCaseOrStep().isPresent());
        var event =
                org.apache.logging.log4j.core.impl.Log4jLogEvent.newBuilder()
                        .setMessage(
                                new org.apache.logging.log4j.message.SimpleMessage(
                                        "Could not start step: no test case running"))
                        .build();
        Assert.assertEquals(
                new AllureContextNoiseFilter().filter(event),
                org.apache.logging.log4j.core.Filter.Result.NEUTRAL);
    }

    @Test
    public void configurationFailureAppearsEvenWithoutAnyTestResult() throws Exception {
        Path root = Files.createTempDirectory("failure-review-setup-");
        Path results = Files.createDirectory(root.resolve("results"));
        JSONObject container =
                new JSONObject()
                        .put("name", "RestaurantTest")
                        .put(
                                "befores",
                                new JSONArray()
                                        .put(
                                                new JSONObject()
                                                        .put("name", "setup")
                                                        .put("status", "broken")
                                                        .put(
                                                                "statusDetails",
                                                                new JSONObject()
                                                                        .put(
                                                                                "message",
                                                                                "Network Error"))));
        Files.writeString(results.resolve("setup-container.json"), container.toString());
        RunFailureOverview.write(results, root.resolve("review"), "one", new JSONArray());
        JSONObject summary = summary(root.resolve("review"));
        Assert.assertEquals(summary.getInt("configurationFailures"), 1);
        Assert.assertEquals(summary.getInt("failedOrBroken"), 0);
        Assert.assertTrue(row(root.resolve("review")).getString("title").contains("تعذر التجهيز"));
        Assert.assertTrue(row(root.resolve("review")).getString("reason").contains("تعذر الاتصال"));
    }

    @Test
    public void evidenceCannotReadAttachmentsOutsideResultsDirectory() throws Exception {
        Path root = Files.createTempDirectory("failure-review-path-");
        Path results = Files.createDirectory(root.resolve("results"));
        Files.writeString(root.resolve("private.txt"), "outside-data");
        JSONObject value =
                test("failed", "mismatch", "NetBased")
                        .put(
                                "attachments",
                                new JSONArray()
                                        .put(
                                                new JSONObject()
                                                        .put("source", "../private.txt")
                                                        .put("name", "external")));
        put(results, value);
        Path output = root.resolve("review");
        RunFailureOverview.write(results, output, "one", new JSONArray());
        Assert.assertEquals(row(output).getJSONArray("attachments").length(), 0);
        Assert.assertTrue(Files.exists(root.resolve("private.txt")));
    }

    @Test
    public void timeoutExplainsTheMissingActionWithoutExposingLambdaOrInventingACause() {
        String readable =
                ReadableFailure.describe(
                        "Expected condition failed: waiting for Page$$Lambda/0x123 (tried for 45 seconds)",
                        "إضافة صنف الأرز");
        Assert.assertTrue(readable.contains("إضافة صنف الأرز"));
        Assert.assertTrue(readable.contains("لا يحدد السبب"));
        Assert.assertFalse(readable.contains("Lambda"));
        Assert.assertEquals(
                ReadableFailure.describe("المتوقع 10، الفعلي 12", ""), "المتوقع 10، الفعلي 12");
    }

    @Test
    public void onlyExpectedLifecycleNoiseOutsideAnyContextIsFiltered() {
        String message = "Could not start step: no test case running";
        Assert.assertTrue(AllureContextNoiseFilter.expectedOutsideContext(message, false));
        Assert.assertFalse(AllureContextNoiseFilter.expectedOutsideContext(message, true));
        Assert.assertFalse(
                AllureContextNoiseFilter.expectedOutsideContext(
                        "Could not write test result", false));
        Assert.assertFalse(AllureContextNoiseFilter.expectedOutsideContext("Network Error", false));
    }

    @Test
    public void attachmentNoiseIsFilteredOnlyWhenNoCaseOrStepExists() {
        String message = "Could not add attachment: no test is running";
        Assert.assertTrue(AllureContextNoiseFilter.expectedOutsideContext(message, false));
        Assert.assertFalse(AllureContextNoiseFilter.expectedOutsideContext(message, true));
        Assert.assertFalse(
                AllureContextNoiseFilter.expectedOutsideContext(
                        "Could not add attachment: write failed", false));
    }

    @Test
    public void repeatedExportDoesNotCountTwiceAndSkippedDoesNotEraseFailure() throws Exception {
        Path root = Files.createTempDirectory("failure-review-history-");
        Path results = Files.createDirectory(root.resolve("results"));
        Path output = root.resolve("review");
        put(results, test("failed", "VAT mismatch", "NetBased"));
        RunFailureOverview.write(results, output, "one", new JSONArray());
        RunFailureOverview.write(results, output, "one", new JSONArray());
        Assert.assertEquals(row(output).getInt("failedRuns"), 1);
        put(results, test("skipped", "setup failed", "NetBased"));
        RunFailureOverview.write(results, output, "two", new JSONArray());
        put(results, test("passed", "", "NetBased"));
        RunFailureOverview.write(results, output, "three", new JSONArray());
        Assert.assertTrue(row(output).getBoolean("retest"));
        Assert.assertTrue(row(output).getString("tracking").contains("تأكيد الإغلاق"));
    }

    @Test
    public void anotherPolicyOrDataSetDoesNotConfirmAFix() throws Exception {
        Path root = Files.createTempDirectory("failure-review-scope-");
        Path results = Files.createDirectory(root.resolve("results"));
        Path output = root.resolve("review");
        put(results, test("failed", "VAT mismatch", "NetBased"));
        RunFailureOverview.write(results, output, "one", new JSONArray());
        put(results, test("passed", "", "PriceIncluded"));
        RunFailureOverview.write(results, output, "two", new JSONArray());
        Assert.assertEquals(summary(output).getJSONArray("cases").length(), 0);
        JSONObject changedData =
                test("passed", "", "NetBased")
                        .put(
                                "parameters",
                                new JSONArray()
                                        .put(
                                                new JSONObject()
                                                        .put("name", "quantity")
                                                        .put("value", "3")));
        put(results, changedData);
        RunFailureOverview.write(results, output, "three", new JSONArray());
        Assert.assertEquals(summary(output).getJSONArray("cases").length(), 0);
    }

    @Test
    public void knownIssueRequiresMatchingMethodAndFailureNotJustClass() throws Exception {
        Path root = Files.createTempDirectory("failure-review-known-");
        Path results = Files.createDirectory(root.resolve("results"));
        Path output = root.resolve("review");
        JSONArray register =
                new JSONArray()
                        .put(
                                new JSONObject()
                                        .put("id", "ERP-17")
                                        .put("method", "Sales.check")
                                        .put("failureContains", new JSONArray().put("VAT mismatch"))
                                        .put("rule", "VAT15%")
                                        .put("state", "FIX_READY")
                                        .put("ticket", ""));
        put(results, test("failed", "VAT mismatch", "NetBased"));
        RunFailureOverview.write(results, output, "one", register);
        Assert.assertEquals(row(output).getString("issue"), "ERP-17");
        Assert.assertTrue(row(output).getString("tracking").contains("فشل مجددًا"));
        put(results, test("broken", "Expected condition failed: waiting for catalog", "NetBased"));
        RunFailureOverview.write(results, output, "two", register);
        Assert.assertEquals(row(output).getString("issue"), "");
        Assert.assertEquals(row(output).getInt("failedRuns"), 2);
        Assert.assertEquals(register.getJSONObject(0).getString("state"), "FIX_READY");
    }

    @Test
    public void archivedEvidenceSurvivesNextRunAndHtmlEscapesUntrustedContent() throws Exception {
        Path root = Files.createTempDirectory("failure-review-evidence-");
        Path results = Files.createDirectory(root.resolve("results"));
        Path output = root.resolve("review");
        Files.writeString(results.resolve("photo.txt"), "evidence");
        JSONObject value =
                test("failed", "<script>alert(1)</script>", "NetBased")
                        .put(
                                "parameters",
                                new JSONArray()
                                        .put(
                                                new JSONObject()
                                                        .put("name", "password")
                                                        .put("value", "not-for-review")))
                        .put(
                                "attachments",
                                new JSONArray()
                                        .put(
                                                new JSONObject()
                                                        .put("name", "الصورة")
                                                        .put("source", "photo.txt")));
        put(results, value);
        RunFailureOverview.write(results, output, "one", new JSONArray());
        Path evidence = Path.of(java.net.URI.create(row(output).getString("evidence")));
        Files.delete(results.resolve("photo.txt"));
        Files.delete(results.resolve("case-result.json"));
        Assert.assertEquals(
                Files.readString(evidence.getParent().resolve("photo.txt")), "evidence");
        Assert.assertFalse(Files.readString(evidence).contains("not-for-review"));
        String html = Files.readString(output.resolve("summary.html"));
        Assert.assertFalse(html.contains("<script>"));
        Assert.assertTrue(html.contains("&lt;script&gt;"));
    }

    private static JSONObject test(String status, String message, String policy) {
        return new JSONObject()
                .put("fullName", "Sales.check")
                .put("status", status)
                .put("statusDetails", new JSONObject().put("message", message))
                .put(
                        "labels",
                        new JSONArray()
                                .put(
                                        new JSONObject()
                                                .put("name", "review.scope")
                                                .put("value", policy)));
    }

    private static void put(Path results, JSONObject test) throws Exception {
        Files.writeString(results.resolve("case-result.json"), test.toString());
    }

    private static JSONObject summary(Path output) throws Exception {
        return new JSONObject(Files.readString(output.resolve("summary.json")));
    }

    private static JSONObject row(Path output) throws Exception {
        return summary(output).getJSONArray("cases").getJSONObject(0);
    }
}
