/* شرح الملف: اختبارات إظهار شرح مبسط لمكان الفشل والقيمة المتوقعة والفعلية.
 */
package Plugin_ERP.Util.Allur;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.AllureResultsWriter;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.model.TestResultContainer;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.Test;

public final class ScenarioFailureReportTest {
    @Test
    public void customerDebtFailureShowsExpectedAndPersistedBalance() {
        String message =
                "Owned customer balance did not reach 115; actual DTO="
                        + "{\"currentDebit\":0,\"prevDebit\":0,\"currentCredit\":0,\"prevCredit\":0}";
        String html = ScenarioFailureReport.render(result(), message);
        Assert.assertTrue(html.contains("<td><bdi dir=\"auto\">115</bdi></td>"));
        Assert.assertTrue(html.contains("<td><bdi dir=\"auto\">0</bdi></td>"));
        Assert.assertTrue(html.contains("الحقول الأربعة المحفوظة لحسابه"));
    }

    @Test
    public void recordedCustomerBalanceIncludesOpeningAndCreditAmounts() {
        String message =
                "Owned customer balance did not reach 115; actual DTO="
                        + "{\"currentDebit\":\"120.50\",\"prevDebit\":10,\"currentCredit\":3,\"prevCredit\":\"2.25\"}";
        String html = ScenarioFailureReport.render(result(), message);
        Assert.assertTrue(html.contains("<td><bdi dir=\"auto\">125.25</bdi></td>"));
    }

    @Test
    public void incompleteOrInvalidCustomerBalancesAreNotTreatedAsZero() {
        for (String json :
                List.of(
                        "{\"currentDebit\":0}",
                        "{broken}",
                        "{\"currentDebit\":\"unknown\",\"prevDebit\":0,\"currentCredit\":0,\"prevCredit\":0}")) {
            String html =
                    ScenarioFailureReport.render(
                            result(),
                            "Owned customer balance did not reach 115; actual DTO=" + json);
            Assert.assertTrue(html.contains("لم يسجل الاختبار قيمة فعلية"));
        }
    }

    @Test
    public void parentComparisonShowsBusinessValuesRatherThanNestedCompareToNumbers() {
        StepResult comparison = step("مطابقة القيمة: المتوقع 999، الفعلي 1000", Status.FAILED);
        comparison
                .getSteps()
                .add(step("التحقق من تطابق القيم: المتوقع 0، الفعلي 1", Status.FAILED));
        TestResult test =
                result().setSteps(List.of(step("إضافة الصنف", Status.PASSED), comparison));
        String html =
                ScenarioFailureReport.render(
                        test, "Actual=1000, expected=999 expected [0] but found [1]");
        Assert.assertTrue(html.contains("<td><bdi dir=\"auto\">999</bdi></td>"));
        Assert.assertTrue(html.contains("<td><bdi dir=\"auto\">1000</bdi></td>"));
        Assert.assertFalse(html.contains("<td><bdi dir=\"auto\">0</bdi></td>"));
        Assert.assertTrue(html.contains("مطابقة القيمة: المتوقع 999، الفعلي 1000 ← التحقق"));
        Assert.assertTrue(html.contains("الخطوات المسجلة"));
    }

    @Test
    public void valuesAndLabelsAreEscapedAndIsolatedInArabicLayout() {
        TestResult test = result().setName("فاتورة <script>alert(1)</script>");
        test.getLabels().add(new Label().setName("screen").setValue("عملاء <img src=x> & موردين"));
        test.getSteps().add(step("مطابقة الاسم: المتوقع <A>، الفعلي B & C", Status.FAILED));
        test.getParameters()
                .add(new Parameter().setName("كلمة المرور").setValue("never-copy-this-parameter"));
        String html =
                ScenarioFailureReport.render(
                        test, "error <script>unsafe()</script>\nprivate multiline details");
        Assert.assertTrue(html.contains("lang=\"ar\" dir=\"rtl\""));
        Assert.assertTrue(html.contains("<bdi dir=\"auto\">&lt;A&gt;</bdi>"));
        Assert.assertTrue(html.contains("B &amp; C"));
        Assert.assertFalse(html.contains("<script>"));
        Assert.assertFalse(html.contains("<img"));
        Assert.assertFalse(html.contains("never-copy-this-parameter"));
        Assert.assertFalse(html.contains("private multiline details"));
    }

    @Test
    public void missingRecordedValuesAreExplicitAndDoNotInventABusinessExpectation() {
        String html =
                ScenarioFailureReport.render(result(), "TimeoutException: waiting for element");
        Assert.assertTrue(html.contains("لا توجد خطوة فاشلة مسجلة"));
        Assert.assertTrue(html.contains("لم يسجل الاختبار قيمة متوقعة"));
        Assert.assertTrue(html.contains("لم يسجل الاختبار قيمة فعلية"));
        Assert.assertTrue(html.contains("لا توجد خطوات مسجلة"));
        Assert.assertFalse(html.contains("المتوقع 999"));
    }

    @Test
    public void catalogScreenSuiteTakesPriorityOverGenericSubSuiteWithoutAFeature() {
        TestResult test = new TestResult().setName("حفظ سجل");
        test.getLabels().add(new Label().setName("suite").setValue("قائمة الأصناف"));
        test.getLabels().add(new Label().setName("subSuite").setValue("حالات الاختبار"));
        String html = ScenarioFailureReport.render(test, "A recorded failure");
        Assert.assertTrue(
                html.contains(
                        "<strong>الشاشة أو المجموعة: </strong><bdi dir=\"auto\">قائمة الأصناف</bdi>"));
    }

    @Test
    public void explicitOriginalActualAndExpectedAreUsedWhenNoStepValuesExist() {
        TestResult test = result().setSteps(List.of(step("قراءة المخزون", Status.FAILED)));
        String html =
                ScenarioFailureReport.render(
                        test, "Actual=1000, expected=999 expected [0] but found [1]");
        Assert.assertTrue(html.contains("<td><bdi dir=\"auto\">999</bdi></td>"));
        Assert.assertTrue(html.contains("<td><bdi dir=\"auto\">1000</bdi></td>"));
        Assert.assertTrue(html.contains("رسالة الخطأ المسجلة"));
    }

    @Test
    public void limitsLongRecordedJourneysButStillShowsTheFinalFailedContext() {
        TestResult test = result();
        for (int i = 1; i <= 30; i++) test.getSteps().add(step("إجراء " + i, Status.PASSED));
        test.getSteps().add(step("آخر خطوة فاشلة", Status.BROKEN));
        String html = ScenarioFailureReport.render(test, "A recorded failure");
        Assert.assertEquals(html.split("<li>", -1).length - 1, 25);
        Assert.assertTrue(html.contains("خطوة من 31"));
        Assert.assertTrue(html.contains("آخر خطوة فاشلة"));
        Assert.assertTrue(html.contains("بقية الخطوات محفوظة"));
    }

    @Test
    public void attachKeepsOutcomeIdentityAndOriginalExceptionAndUsesNoDriver() {
        AllureLifecycle previous = Allure.getLifecycle();
        MemoryWriter writer = new MemoryWriter();
        AllureLifecycle lifecycle = new AllureLifecycle(writer);
        Allure.setLifecycle(lifecycle);
        String id = UUID.randomUUID().toString();
        AssertionError failure = new AssertionError("Actual=1000, expected=999");
        StatusDetails details =
                new StatusDetails()
                        .setMessage("Original TestNG message")
                        .setTrace("original-trace");
        TestResult original =
                result().setUuid(id)
                        .setFullName("Original.Class.method")
                        .setHistoryId("original-history")
                        .setStatus(Status.FAILED)
                        .setStatusDetails(details);
        original.getSteps().add(step("مطابقة القيمة: المتوقع 999، الفعلي 1000", Status.FAILED));
        ITestResult testResult = throwableOnly(failure);
        try {
            lifecycle.scheduleTestCase(original);
            lifecycle.startTestCase(id);
            ScenarioFailureReport.attach(testResult);
            Assert.assertSame(testResult.getThrowable(), failure);
            Assert.assertEquals(original.getStatus(), Status.FAILED);
            Assert.assertSame(original.getStatusDetails(), details);
            Assert.assertEquals(
                    original.getStatusDetails().getMessage(), "Original TestNG message");
            Assert.assertEquals(original.getStatusDetails().getTrace(), "original-trace");
            Assert.assertEquals(original.getFullName(), "Original.Class.method");
            Assert.assertEquals(original.getHistoryId(), "original-history");
            Assert.assertEquals(original.getName(), "حفظ فاتورة نقدية");
            Assert.assertEquals(original.getSteps().getFirst().getStatus(), Status.FAILED);
            Assert.assertEquals(original.getAttachments().size(), 2);
            var htmlAttachment =
                    original.getAttachments().stream()
                            .filter(a -> "text/html".equals(a.getType()))
                            .findFirst()
                            .orElseThrow();
            var technical =
                    original.getAttachments().stream()
                            .filter(a -> "text/plain".equals(a.getType()))
                            .findFirst()
                            .orElseThrow();
            Assert.assertTrue(
                    writer.attachments
                            .get(htmlAttachment.getSource())
                            .contains("شرح الفشل المسجل"));
            Assert.assertTrue(
                    writer.attachments
                            .get(technical.getSource())
                            .contains("java.lang.AssertionError: Actual=1000, expected=999"));
        } finally {
            lifecycle.stopTestCase(id);
            Allure.setLifecycle(previous);
        }
    }

    @Test
    public void noCurrentTestDoesNotCreateAttachmentsOrReadTheTestResult() {
        AllureLifecycle previous = Allure.getLifecycle();
        MemoryWriter writer = new MemoryWriter();
        Allure.setLifecycle(new AllureLifecycle(writer));
        ITestResult unreadable =
                (ITestResult)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {ITestResult.class},
                                (proxy, method, args) -> {
                                    throw new AssertionError("No result access expected");
                                });
        try {
            ScenarioFailureReport.attach(unreadable);
            Assert.assertTrue(writer.attachments.isEmpty());
        } finally {
            Allure.setLifecycle(previous);
        }
    }

    private TestResult result() {
        TestResult result = new TestResult().setName("حفظ فاتورة نقدية").setStatus(Status.FAILED);
        result.getLabels().add(new Label().setName("feature").setValue("نقاط البيع"));
        return result;
    }

    private StepResult step(String name, Status status) {
        return new StepResult().setName(name).setStatus(status);
    }

    private ITestResult throwableOnly(Throwable failure) {
        return (ITestResult)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {ITestResult.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("getThrowable")) return failure;
                            throw new AssertionError(
                                    "Reporting must not change or inspect unrelated test state: "
                                            + method.getName());
                        });
    }

    private static final class MemoryWriter implements AllureResultsWriter {
        final Map<String, String> attachments = new LinkedHashMap<>();

        @Override
        public void write(TestResult test) {}

        @Override
        public void write(TestResultContainer container) {}

        @Override
        public void write(String source, InputStream attachment) {
            try (attachment) {
                attachments.put(
                        source, new String(attachment.readAllBytes(), StandardCharsets.UTF_8));
            } catch (IOException failure) {
                throw new IllegalStateException(failure);
            }
        }
    }
}
