/* شرح الملف: مراجعة وضوح أسماء الحقول وتصنيف المشاكل وعدم تغيير نتيجة الحالة.
 */
package Plugin_ERP.Util.Allur;

import io.qameta.allure.model.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class SimpleReportingTest {
    @Test
    public void unexplainedExecutionFailureIsNotInventedAsAComparisonOrProductBug() {
        var category =
                FailureClassification.classify(
                        new TestResult().setName("Button overlap"),
                        new RuntimeException("Stopped"));
        Assert.assertEquals(category.domain(), "واجهة وتنسيق");
        Assert.assertEquals(category.origin(), "غير محسوم");
    }

    @Test
    public void missingFieldNameAndKnownInternalReferenceAreHandledWithoutChangingOutcome() {
        Assert.assertEquals(ReadableChecks.field(null), "القيمة");
        var test =
                new TestResult()
                        .setStatus(Status.FAILED)
                        .setFullName("original")
                        .setHistoryId("stable");
        FailureClassification.apply(test, new AssertionError("KNOWN_BUG:REST-RETURN-001 mismatch"));
        Assert.assertEquals(test.getStatus(), Status.FAILED);
        Assert.assertEquals(test.getHistoryId(), "stable");
        Assert.assertEquals(test.getFullName(), "original");
        Assert.assertTrue(
                test.getLabels().stream()
                        .anyMatch(
                                l ->
                                        l.getName().equals("حالة المشكلة")
                                                && l.getValue().equals("مشكلة معروفة")));
    }

    @Test
    public void fieldComparisonShowsBusinessFieldAndActualValues() {
        Assert.assertEquals(
                ReadableChecks.comparison("taxNumber", "310000000000003", "310000000000003"),
                "التحقق من الرقم الضريبي: المتوقع 310000000000003، الفعلي 310000000000003");
        Assert.assertTrue(
                ReadableChecks.comparison("itemArabicName", "صنف الأرز", "صنف الأرز")
                        .contains("اسم الصنف"));
        Assert.assertEquals(ReadableChecks.display(true), "نعم");
        Assert.assertEquals(ReadableChecks.display(false), "لا");
    }

    @Test
    public void networkFailureIsNotAnApplicationBugEvenWhenTaxCaseWasRunning() {
        var category =
                FailureClassification.classify(
                        new TestResult().setName("ضريبة الصنف"),
                        new RuntimeException("Network Error"));
        Assert.assertEquals(category.origin(), "شبكة أو بيئة التشغيل");
        Assert.assertEquals(category.confirmation(), "لم يثبت أنه عيب في البرنامج");
    }

    @Test
    public void assertionWithoutReferenceRemainsUnconfirmedAndKnownRequiresExplicitReference() {
        var test = new TestResult().setName("التحقق من ضريبة فاتورة البيع");
        var result =
                FailureClassification.classify(
                        test, new AssertionError("VAT expected115 actual100"));
        Assert.assertEquals(result.domain(), "ضرائب وحسابات");
        Assert.assertEquals(result.confirmation(), "فشل يحتاج مراجعة");
        result =
                FailureClassification.classify(
                        test, new AssertionError("KNOWN_BUG:ERP-17 mismatch"));
        Assert.assertEquals(result.reference(), "ERP-17");
        Assert.assertEquals(result.confirmation(), "مشكلة معروفة");
    }

    @Test
    public void technicalNullFailureIsTestExecutionAndSkippedIsNotANewBug() {
        Assert.assertEquals(
                FailureClassification.classify(
                                new TestResult(), new NullPointerException("missing page"))
                        .origin(),
                "تنفيذ الاختبار");
        Assert.assertEquals(
                FailureClassification.classify(new TestResult().setStatus(Status.SKIPPED), null)
                        .confirmation(),
                "الحالة لم تُنفذ");
    }
}
