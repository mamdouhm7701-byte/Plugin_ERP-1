package Plugin_ERP.Util.Allur;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import java.util.List;
import java.util.UUID;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Exercises the woven report, not just annotation strings or a title formatter. */
public final class ScenarioJourneyReportTest {
    @Step("فتح شاشة الفاتورة")
    public void openScreen() {}

    @Step("إضافة الصنف الأول")
    public void firstItem() {}

    @Step("تعليق الفاتورة")
    public void hold() {}

    @Step("استرجاع الفاتورة المعلقة")
    public void restore() {}

    @Step("إضافة الصنف الثاني")
    public void secondItem() {}

    private List<io.qameta.allure.model.StepResult> report(Runnable scenario, boolean reporting) {
        var lifecycle = Allure.getLifecycle();
        String id = UUID.randomUUID().toString();
        var recorded =
                new java.util.concurrent.atomic.AtomicReference<
                        List<io.qameta.allure.model.StepResult>>();
        lifecycle.startStep(
                id, new io.qameta.allure.model.StepResult().setName("فحص تسجيل رحلة الحالة"));
        try {
            if (reporting) ScenarioAssertions.begin();
            scenario.run();
            lifecycle.updateStep(id, step -> recorded.set(List.copyOf(step.getSteps())));
            return recorded.get();
        } finally {
            ScenarioAssertions.end();
            lifecycle.updateStep(id, step -> step.setStatus(io.qameta.allure.model.Status.PASSED));
            lifecycle.stopStep(id);
        }
    }

    @Test
    public void recordsActualJourneyAndFailureAtItsPosition() throws Exception {
        var result =
                report(
                        () -> {
                            openScreen();
                            firstItem();
                            hold();
                            restore();
                            secondItem();
                            try {
                                Assert.assertEquals(1, 2, "عدد أصناف الفاتورة بعد الاسترجاع");
                            } catch (AssertionError expected) {
                                /* Inspect the original failure in the generated step. */
                            }
                        },
                        true);
        var steps = result;
        Assert.assertEquals(steps.size(), 6);
        var expected =
                List.of(
                        "فتح شاشة الفاتورة",
                        "إضافة الصنف الأول",
                        "تعليق الفاتورة",
                        "استرجاع الفاتورة المعلقة",
                        "إضافة الصنف الثاني");
        for (int index = 0; index < expected.size(); index++)
            Assert.assertEquals(steps.get(index).getName(), expected.get(index));
        var failed = steps.get(5);
        Assert.assertEquals(failed.getStatus(), io.qameta.allure.model.Status.FAILED);
        Assert.assertTrue(failed.getName().contains("المتوقع 2، الفعلي 1"));
        Assert.assertTrue(failed.getStatusDetails().getMessage().contains("عدد أصناف الفاتورة"));
        Assert.assertTrue(failed.getStatusDetails().getTrace().contains("AssertionError"));
    }

    @Test
    public void preservesFailuresAndDoesNotRecordBackgroundAssertions() throws Exception {
        AssertionError[] original = new AssertionError[1];
        var result =
                report(
                        () -> {
                            try {
                                Assert.assertTrue(false, "الصنف الثاني غير موجود");
                            } catch (AssertionError error) {
                                original[0] = error;
                            }
                        },
                        true);
        Assert.assertNotNull(original[0]);
        Assert.assertTrue(original[0].getMessage().contains("الصنف الثاني غير موجود"));
        Assert.assertEquals(result.size(), 1);
        var background = report(() -> Assert.assertEquals(1, 1), false);
        Assert.assertEquals(background.size(), 0);
    }

    @Test
    public void deferredFailureAppearsBeforeTheNextScreenAction() throws Exception {
        boolean[] failedAtEnd = {false};
        var result =
                report(
                        () -> {
                            var checks = new ScenarioSoftAssert();
                            firstItem();
                            checks.assertEquals(1, 2, "عدد الأصناف");
                            secondItem();
                            try {
                                checks.assertAll();
                            } catch (AssertionError error) {
                                failedAtEnd[0] = true;
                            }
                        },
                        true);
        Assert.assertTrue(failedAtEnd[0]);
        var steps = result;
        Assert.assertEquals(steps.size(), 3);
        Assert.assertEquals(steps.get(1).getStatus(), io.qameta.allure.model.Status.FAILED);
        Assert.assertTrue(steps.get(1).getName().contains("المتوقع 2، الفعلي 1"));
        Assert.assertEquals(steps.get(2).getName(), "إضافة الصنف الثاني");
    }

    @Test
    public void numericToleranceIsExplicitInTheRecordedComparison() throws Exception {
        var steps = report(() -> Assert.assertEquals(1.01, 1.0, 0.1), true);
        Assert.assertEquals(steps.size(), 1);
        Assert.assertEquals(steps.get(0).getStatus(), io.qameta.allure.model.Status.PASSED);
        Assert.assertTrue(steps.get(0).getName().contains("السماحية 0.1"));
    }
}
