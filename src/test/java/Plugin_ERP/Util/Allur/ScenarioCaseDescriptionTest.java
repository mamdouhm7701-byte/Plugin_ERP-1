package Plugin_ERP.Util.Allur;

import io.qameta.allure.model.TestResult;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ScenarioCaseDescriptionTest {
    @Test
    public void malformedDescriptionNamesResourceCaseAndInvalidField() {
        String json =
                """
                {"example.Screen#save":{"title":"Save","objective":"Check saving",
                "preconditions":"Open form","data":"One record","steps":["Save"],"expected":42}}
                """;
        var error =
                Assert.expectThrows(
                        IllegalStateException.class,
                        () ->
                                ScenarioCaseDescriptions.parse(
                                        "broken-cases.json", new java.io.StringReader(json)));
        Assert.assertTrue(error.getMessage().contains("broken-cases.json [example.Screen#save]"));
        Assert.assertTrue(error.getMessage().contains("expected must be nonblank text"));
    }

    @Test
    public void invalidJsonPreservesParseCauseAndResourceName() {
        var error =
                Assert.expectThrows(
                        IllegalStateException.class,
                        () ->
                                ScenarioCaseDescriptions.parse(
                                        "invalid.json", new java.io.StringReader("{")));
        Assert.assertTrue(error.getMessage().contains("invalid.json"));
        Assert.assertNotNull(error.getCause());
    }

    @Test
    public void blankStepsAreRejectedInsteadOfRenderingAnEmptyInstruction() {
        String json =
                """
                {"example.Screen#save":{"steps":[" "]}}
                """;
        var error =
                Assert.expectThrows(
                        IllegalStateException.class,
                        () ->
                                ScenarioCaseDescriptions.parse(
                                        "blank-step.json", new java.io.StringReader(json)));
        Assert.assertTrue(error.getMessage().contains("each step must be nonblank text"));
    }

    @Test
    public void holdPaymentExampleShowsStockBeforeAfterHoldAndAfterPayment() throws Exception {
        var type = Class.forName("SpecialSalesTest.RestaurantInvoiceTest");
        var result = new TestResult();
        ScenarioReportCatalog.apply(
                result, type.getName(), type.getMethod("payingHoldDoesNotDeductAgain"));
        Assert.assertEquals(result.getName(), "دفع طلب المطعم المعلّق لا يخصم الصنف مرة ثانية");
        String html = result.getDescriptionHtml();
        Assert.assertTrue(html.contains("1000"));
        Assert.assertTrue(html.contains("999"));
        Assert.assertTrue(html.contains("998"));
        Assert.assertTrue(html.contains("نفس الطلب"));
        Assert.assertTrue(html.contains("كامل الصافي"));
        Assert.assertFalse(html.contains("isOnHold"));
        Assert.assertFalse(html.contains("data.balance"));
    }

    @Test
    public void barcodeCaseExplainsPurposeStepsAndExpectedInPlainArabic() throws Exception {
        var type = Class.forName("Invoice.SalesInvoiceCoverageTest");
        var result = new TestResult().setFullName("original.name").setHistoryId("original-history");
        ScenarioReportCatalog.apply(
                result, type.getName(), type.getMethod("savesOwnedItemByBarcode"));
        String html = result.getDescriptionHtml();
        Assert.assertNotNull(html, "Case details must be provided, not just a translated title");
        Assert.assertTrue(html.contains("بنختبر إيه؟"));
        Assert.assertTrue(html.contains("خطوات التجربة"));
        Assert.assertTrue(html.contains("المتوقع"));
        Assert.assertTrue(html.contains("باركود"));
        Assert.assertFalse(html.contains("DTO"));
        Assert.assertFalse(html.contains("fixture"));
        Assert.assertEquals(result.getFullName(), "original.name");
        Assert.assertEquals(result.getHistoryId(), "original-history");
    }

    @Test
    public void everyLiveCaseHasStepsAndExpectedWithoutTechnicalPlaceholders() throws Exception {
        Assert.assertEquals(ScenarioCaseDescriptions.size(), 895);
        for (String key : ScenarioCaseDescriptions.references()) {
            String[] parts = key.split("#", 2);
            var type = Class.forName(parts[0]);
            Assert.assertTrue(
                    java.util.Arrays.stream(type.getMethods())
                            .anyMatch(
                                    method ->
                                            method.getName().equals(parts[1])
                                                    && method.getAnnotation(Test.class) != null),
                    key);
            String html = ScenarioCaseDescriptions.html(parts[0], parts[1]);
            Assert.assertNotNull(html, key);
            Assert.assertTrue(html.contains("<ol>") && html.contains("<li>"), key);
            Assert.assertTrue(html.contains("المتوقع"), key);
            for (String technical :
                    new String[] {
                        "DTO", "fixture", "captured", "assertEquals", "راجع النتيجة أدناه"
                    }) Assert.assertFalse(html.contains(technical), key + ": " + technical);
        }
    }
}
