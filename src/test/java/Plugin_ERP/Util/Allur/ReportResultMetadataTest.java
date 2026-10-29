/* شرح الملف: يثبت أن سياسات الضريبة تظهر منفصلة، وأن التقرير لا يحوّل الانتظار أو اختلاف القيم إلى عيب تطبيق بلا دليل. */
package Plugin_ERP.Util.Allur;

import io.qameta.allure.model.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportResultMetadataTest {
    private TestResult result(String policy) {
        return new TestResult()
                .setHistoryId("same-method-and-arguments")
                .setFullName("SpecialSalesTest.SpecialInvoiceTaxReview.reviewSpecialInvoice")
                .setStatus(Status.PASSED)
                .setLabels(
                        new ArrayList<>(
                                List.of(
                                        new Label()
                                                .setName("review.scope")
                                                .setValue(
                                                        "taxPolicy=" + policy + ";additional=true"),
                                        new Label()
                                                .setName("testClass")
                                                .setValue(
                                                        "SpecialSalesTest.RestaurantInvoiceTaxReviewTest"))))
                .setParameters(
                        new ArrayList<>(
                                List.of(
                                        new Parameter().setName("arg0").setValue("تعديل"),
                                        new Parameter().setName("arg1").setValue("false"),
                                        new Parameter().setName("arg2").setValue("true"))));
    }

    @Test
    public void listenerIsAutomaticallyRegistered() {
        Assert.assertTrue(
                java.util.ServiceLoader.load(io.qameta.allure.listener.TestLifecycleListener.class)
                        .stream()
                        .anyMatch(p -> p.type() == ReportResultMetadata.class));
    }

    @Test
    public void separatesPoliciesButKeepsRealRetriesTogether() {
        var listener = new ReportResultMetadata();
        var net = result("NetBased");
        var item = result("ItemBased");
        var included = result("PriceIncluded");
        for (var r : List.of(net, item, included)) listener.beforeTestWrite(r);
        Assert.assertEquals(
                Set.of(net.getHistoryId(), item.getHistoryId(), included.getHistoryId()).size(), 3);
        var retry = result("NetBased");
        listener.beforeTestWrite(retry);
        Assert.assertEquals(net.getHistoryId(), retry.getHistoryId());
        listener.beforeTestWrite(net);
        Assert.assertEquals(net.getHistoryId(), retry.getHistoryId());
        Assert.assertTrue(net.getName().contains("ضريبة على الصافي"));
        Assert.assertTrue(net.getName().contains("سعر منخفض"));
        Assert.assertTrue(net.getDescription().contains("40.25"));
    }

    @Test
    public void knownTestExpectationDefectRequiresBothMethodAndSignature() {
        var test =
                result("ItemBased")
                        .setStatus(Status.FAILED)
                        .setStatusDetails(
                                new StatusDetails()
                                        .setMessage(
                                                "صافي الفاتورة غير مطابق: المتوقع 230.00، الفعلي 200"));
        new ReportResultMetadata().beforeTestWrite(test);
        Assert.assertTrue(
                test.getStatusDetails()
                        .getMessage()
                        .startsWith("KNOWN_BUG:REST-ADDITIONAL-EXEMPT-003"));
        Assert.assertTrue(
                test.getLabels().stream().anyMatch(l -> l.getValue().equals("عيب اختبار")));
        Assert.assertEquals(test.getStatus(), Status.FAILED);
        Assert.assertNull(
                ReportResultMetadata.knownIssue(
                        "other.method", "صافي الفاتورة غير مطابق: المتوقع 230.00، الفعلي 200"));
    }

    @Test
    public void timeoutStaysUnresolvedAndKeepsItsOriginalEvidence() {
        var test =
                result("PriceIncluded")
                        .setStatus(Status.BROKEN)
                        .setStatusDetails(
                                new StatusDetails()
                                        .setMessage("Expected condition failed: timeout")
                                        .setTrace("original trace"));
        new ReportResultMetadata().beforeTestWrite(test);
        Assert.assertTrue(
                test.getLabels().stream().anyMatch(l -> l.getValue().equals("يحتاج مراجعة")));
        Assert.assertEquals(test.getStatusDetails().getTrace(), "original trace");
        Assert.assertFalse(test.getStatusDetails().getMessage().contains("KNOWN_BUG"));
    }
}
