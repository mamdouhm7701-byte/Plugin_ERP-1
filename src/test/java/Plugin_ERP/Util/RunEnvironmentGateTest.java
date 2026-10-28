/* شرح الملف: اختبار توقف التجهيز بعد حد الترخيص مع بقاء التنظيف مسموحًا وعزل التشغيلات.
 * فائدته: يمنع تكرار الإنشاء الفاشل أو إخفاء أول خطأ أو منع حذف بيانات التجربة.
 */
package Plugin_ERP.Util;

import Plugin_ERP.Listeners.TestNGListeners;
import Plugin_ERP.Util.Allur.*;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.*;
import org.testng.annotations.Test;

public final class RunEnvironmentGateTest {
    private static final String LIMIT = "Common.LicenseLimitationExceededErrorMessage";

    @Test
    public void unrelatedBusinessErrorsDoNotStopOtherScenarios() {
        var gate = new RunEnvironmentGate.Gate(block -> Assert.fail("Unexpected capacity block"));
        Assert.assertFalse(gate.observe("create", "Stores", "Record is in use"));
        Assert.assertFalse(gate.observe("get", "Stores", LIMIT));
        Assert.assertFalse(CapacityFailure.matches("VAT license comparison failed"));
        gate.beforeCreate("create");
    }

    @Test
    public void capacityStopsNewWorkButPermitsEveryCleanupOperation() {
        var evidence = new ArrayList<RunEnvironmentGate.Block>();
        var gate = new RunEnvironmentGate.Gate(evidence::add);
        Assert.assertTrue(gate.observe("create", "Stores", LIMIT));
        Assert.expectThrows(SkipException.class, gate::beforeScenario);
        Assert.expectThrows(SkipException.class, () -> gate.beforeCreate("create"));
        for (String action : List.of("get", "update", "delete", "getAll"))
            gate.beforeCreate(action);
        Assert.assertEquals(evidence.size(), 1);
        Assert.assertEquals(evidence.getFirst().source(), "API_RESPONSE");
        var nextRun = new RunEnvironmentGate.Gate(block -> {});
        nextRun.beforeScenario();
    }

    @Test
    public void parallelFailuresKeepOnePrimaryCapacityRecord() throws Exception {
        var written = new AtomicInteger();
        var gate = new RunEnvironmentGate.Gate(block -> written.incrementAndGet());
        var pool = Executors.newFixedThreadPool(4);
        try {
            var tasks = new ArrayList<Callable<Boolean>>();
            for (int i = 0; i < 20; i++) tasks.add(() -> gate.observe("create", "Stores", LIMIT));
            for (var future : pool.invokeAll(tasks))
                Assert.assertTrue(future.get(5, TimeUnit.SECONDS));
            Assert.assertEquals(written.get(), 1);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    public void capacityClassificationKeepsTheFirstFailureAndExplainsSubsequentSkips() {
        Assert.assertEquals(
                OutcomeClassification.classify("failed", LIMIT, "", "APPLICATION_DEFECT").kind(),
                OutcomeClassification.Kind.ENVIRONMENT);
        var skipped =
                OutcomeClassification.classify(
                        "skipped", "ENVIRONMENT_BLOCKED: LICENSE_CAPACITY", "", "");
        Assert.assertEquals(skipped.kind(), OutcomeClassification.Kind.NOT_EXECUTED);
        Assert.assertTrue(skipped.reason().contains("سعة"));
        Assert.assertTrue(ReadableFailure.describe(LIMIT, "").contains("لم يكتمل اختبار البيزنس"));
        var test =
                new io.qameta.allure.model.TestResult()
                        .setStatus(io.qameta.allure.model.Status.FAILED);
        FailureClassification.apply(test, new AssertionError(LIMIT));
        Assert.assertEquals(test.getStatus(), io.qameta.allure.model.Status.FAILED);
        Assert.assertTrue(
                test.getLabels().stream().anyMatch(l -> l.getValue().equals("حد الترخيص")));
    }

    @Test
    public void listenerBlocksSetupAndTestBodiesButNeverAfterCleanup() {
        String previous = System.getProperty("allure.run.id");
        System.setProperty("allure.run.id", "capacity-listener-" + UUID.randomUUID());
        try {
            RunEnvironmentGate.current().observe("create", "Stores", LIMIT);
            var listener = new TestNGListeners();
            ITestResult protectedResult = result(new Protected());
            Assert.expectThrows(
                    SkipException.class,
                    () -> listener.beforeInvocation(invocation("test"), protectedResult));
            Assert.expectThrows(
                    SkipException.class,
                    () -> listener.beforeInvocation(invocation("beforeClass"), protectedResult));
            Assert.expectThrows(
                    SkipException.class,
                    () -> listener.beforeInvocation(invocation("beforeMethod"), protectedResult));
            listener.beforeInvocation(invocation("afterMethod"), protectedResult);
            listener.beforeInvocation(invocation("afterClass"), protectedResult);
            listener.beforeInvocation(invocation("test"), result(new Object()));
        } finally {
            if (previous == null) System.clearProperty("allure.run.id");
            else System.setProperty("allure.run.id", previous);
        }
    }

    private static final class Protected implements RunEnvironmentGate.ProtectedScenario {}

    private static ITestResult result(Object instance) {
        return (ITestResult)
                Proxy.newProxyInstance(
                        RunEnvironmentGateTest.class.getClassLoader(),
                        new Class<?>[] {ITestResult.class},
                        (proxy, method, args) ->
                                method.getName().equals("getInstance") ? instance : null);
    }

    private static IInvokedMethod invocation(String phase) {
        ITestNGMethod method =
                (ITestNGMethod)
                        Proxy.newProxyInstance(
                                RunEnvironmentGateTest.class.getClassLoader(),
                                new Class<?>[] {ITestNGMethod.class},
                                (proxy, called, args) ->
                                        switch (called.getName()) {
                                            case "isBeforeClassConfiguration" ->
                                                    phase.equals("beforeClass");
                                            case "isBeforeMethodConfiguration" ->
                                                    phase.equals("beforeMethod");
                                            default ->
                                                    called.getReturnType() == boolean.class
                                                            ? false
                                                            : null;
                                        });
        return (IInvokedMethod)
                Proxy.newProxyInstance(
                        RunEnvironmentGateTest.class.getClassLoader(),
                        new Class<?>[] {IInvokedMethod.class},
                        (proxy, called, args) ->
                                switch (called.getName()) {
                                    case "getTestMethod" -> method;
                                    case "isTestMethod" -> phase.equals("test");
                                    default ->
                                            called.getReturnType() == boolean.class ? false : null;
                                });
    }
}
