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
    public void explicitDatabaseTimeoutIsDifferentFromAnUnexplainedUiTimeout() {
        String serverTimeout =
                "Execution Timeout Expired. The timeout period elapsed prior to completion of the operation or the server is not responding.";
        Assert.assertEquals(
                FailureClassification.classify(
                                new io.qameta.allure.model.TestResult()
                                        .setStatus(io.qameta.allure.model.Status.FAILED),
                                new AssertionError(serverTimeout))
                        .origin(),
                "شبكة أو بيئة التشغيل");
        Assert.assertEquals(
                classify(
                        "failed",
                        "Owned barcode did not add its item. status=500 Execution Timeout Expired. The timeout period elapsed prior to completion of the operation or the server is not responding.",
                        "",
                        ""),
                Kind.ENVIRONMENT);
        Assert.assertEquals(classify("broken", "script timeout", "", ""), Kind.NEEDS_REVIEW);
        Assert.assertEquals(
                classify("broken", "Expected condition failed after 35 seconds", "", ""),
                Kind.NEEDS_REVIEW);
    }

    @Test
    public void explicitSqlTransientFailureDoesNotLookLikeABusinessAssertion() {
        Assert.assertEquals(
                classify(
                        "broken",
                        "Fixture delete Accounts: An exception has been raised that is likely due to a transient failure. Consider enabling transient error resiliency by adding 'EnableRetryOnFailure' to the 'UseSqlServer' call.",
                        "",
                        ""),
                Kind.ENVIRONMENT);
        Assert.assertEquals(
                classify("broken", "Fixture delete Accounts: record is used", "", ""),
                Kind.NEEDS_REVIEW);
    }

    @Test
    public void explicitIncompatibleScenarioIsATestDefectAndDoesNotBecomeSkipped() {
        Assert.assertEquals(
                classify(
                        "broken",
                        "SCENARIO_NOT_APPLICABLE: fixed quantity differs from requested quantity",
                        "",
                        ""),
                Kind.TEST_DEFECT);
        Assert.assertEquals(
                classify("failed", "quantity expected 3 actual 1", "", ""), Kind.NEEDS_REVIEW);
    }

    @Test
    public void knownCashOrderAndReturnFailuresMatchOnlyTheirEvidence() throws Exception {
        Path root = Files.createTempDirectory("known-business-failures-");
        Path results = Files.createDirectory(root.resolve("results"));
        String order = "Invoice.SalesOrderTest.cashOrderRecordsCustomerAdvanceWithoutStockMovement";
        String returned =
                "SpecialSalesTest.RestaurantAdditionalTaxReturnTest.linkedReturnRefundsOriginalAdditionalTaxAndVat";
        String[] methods = {
            order,
            returned,
            returned,
            order,
            "Invoice.SalesFormTest.cashDocumentUsesOwnedPayment",
            "SpecialSalesTest.RestaurantInvoiceTest.reopeningHoldKeepsNotes",
            "SpecialSalesTest.RestaurantInvoiceTest.reopeningHoldKeepsNotes"
        };
        String[] messages = {
            "Expected customer balance delta -345.00, actual 0",
            "صافي السند غير مطابق: المتوقع 40.25، الفعلي 23",
            "صافي السند غير مطابق: المتوقع 120.75، الفعلي 69",
            "Expected condition failed",
            "Expected customer balance delta -345.00, actual 0",
            "Held restaurant notes must survive reopening expected [owned-marker] but found []",
            "Held restaurant notes must be persisted before reopening"
        };
        for (int i = 0; i < methods.length; i++)
            Files.writeString(
                    results.resolve(i + "-result.json"),
                    new JSONObject()
                            .put("fullName", methods[i])
                            .put("historyId", "case-" + i)
                            .put("status", "failed")
                            .put("statusDetails", new JSONObject().put("message", messages[i]))
                            .toString());
        JSONArray issues;
        try (var stream = getClass().getResourceAsStream("/issue-register.json")) {
            Assert.assertNotNull(stream);
            issues =
                    new JSONArray(
                            new String(
                                    stream.readAllBytes(),
                                    java.nio.charset.StandardCharsets.UTF_8));
        }
        Path output = root.resolve("review");
        RunFailureOverview.write(results, output, "known-business", issues);
        var summary = new JSONObject(Files.readString(output.resolve("summary.json")));
        Assert.assertEquals(
                summary.getJSONObject("outcomeCounts").getInt(Kind.APPLICATION_DEFECT.label()), 4);
        Assert.assertEquals(
                summary.getJSONObject("outcomeCounts").getInt(Kind.NEEDS_REVIEW.label()), 3);
    }

    @Test
    public void databaseVictimAndFixtureScriptErrorsHaveExplicitOrigins() {
        Assert.assertEquals(
                classify("failed", "Transaction deadlocked; chosen as deadlock victim", "", ""),
                Kind.ENVIRONMENT);
        Assert.assertEquals(
                classify("broken", "javascript error: erpVue is not defined", "", ""),
                Kind.TEST_DEFECT);
        Assert.assertEquals(
                classify(
                        "broken",
                        "ClassCastException: proxy cannot be cast to JavascriptExecutor",
                        "",
                        ""),
                Kind.TEST_DEFECT);
    }

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
