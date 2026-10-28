/* شرح الملف: إظهار المقارنات التي نفذتها الحالة مع المتوقع والفعلي.
 */
package Plugin_ERP.Util.Allur;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/** Reports assertions where they execute, preserving the original return value and failure. */
@Aspect
public class ScenarioAssertions {
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<java.util.Deque<String>> FRAMES =
            ThreadLocal.withInitial(java.util.ArrayDeque::new);

    public static boolean isRecording() {
        return ACTIVE.get() && Allure.getLifecycle().getCurrentTestCase().isPresent();
    }

    public static void begin() {
        ACTIVE.set(true);
    }

    public static void end() {
        ACTIVE.remove();
    }

    public static void recordComparison(
            Object actual, Object expected, String message, AssertionError failure) {
        if (!ACTIVE.get() || Allure.getLifecycle().getCurrentTestCase().isEmpty()) return;
        String id = UUID.randomUUID().toString();
        String name = ReadableChecks.comparison(message, expected, actual);
        Allure.getLifecycle().startStep(id, new StepResult().setName(name));
        Allure.getLifecycle()
                .updateStep(
                        id,
                        step -> {
                            step.setStatus(failure == null ? Status.PASSED : Status.FAILED);
                            if (failure != null)
                                step.setStatusDetails(
                                        new StatusDetails().setMessage(failure.getMessage()));
                        });
        if (failure != null) {
            var driver = Plugin_ERP.Drivers.WebDriverFactory.getDriver();
            if (driver != null)
                Plugin_ERP.Util.ScreenShotsUtils.takeScreenShot(driver, "موضع فشل المقارنة");
        }
        Allure.getLifecycle().stopStep(id);
    }

    public static String title(String method, Object[] args) {
        if (args.length == 0) return "التحقق من نتيجة الحالة";
        String actual = display(args[0]);
        return switch (method) {
            case "assertTrue" -> "التحقق من الشرط: المتوقع نعم، الفعلي " + actual;
            case "assertFalse" -> "التحقق من الشرط: المتوقع لا، الفعلي " + actual;
            case "assertNull" -> "التحقق من خلو القيمة: الفعلي " + actual;
            case "assertNotNull" -> "التحقق من وجود قيمة: الفعلي " + actual;
            case "assertEquals", "assertEqualsNoOrder", "assertSame" ->
                    "التحقق من تطابق القيم: المتوقع "
                            + (args.length > 1 ? display(args[1]) : "")
                            + "، الفعلي "
                            + actual;
            case "assertNotEquals", "assertNotSame" ->
                    "التحقق من اختلاف القيم: القيمة التي يجب اختلافها "
                            + (args.length > 1 ? display(args[1]) : "")
                            + "، الفعلي "
                            + actual;
            default -> "التحقق " + method + ": " + actual;
        };
    }

    private static String display(Object value) {
        return ReadableChecks.display(value);
    }

    @Pointcut(
            "call(public static void org.testng.Assert.assert*(..)) && (within(Plugin_ERP..*) || within(Invoice..*) || within(Purches..*) || within(ProductsTest..*) || within(DealerModelTest..*) || within(StoreTest..*) || within(FinancialMovementsTest..*) || within(SpecialSalesTest..*) || within(GenralAccountantTest..*) || within(TestSupport..*)) && !within(Plugin_ERP.Util.Allur.ScenarioAssertions)")
    public void assertionCall() {}

    @Before("assertionCall()")
    public void start(JoinPoint call) {
        if (!ACTIVE.get() || Allure.getLifecycle().getCurrentTestCase().isEmpty()) {
            FRAMES.get().push("");
            return;
        }
        String id = UUID.randomUUID().toString();
        FRAMES.get().push(id);
        String name = title(call.getSignature().getName(), call.getArgs());
        Object[] values = call.getArgs();
        int messageIndex =
                switch (call.getSignature().getName()) {
                    case "assertTrue", "assertFalse", "assertNull", "assertNotNull" -> 1;
                    default -> 2;
                };
        if (values.length > messageIndex && values[values.length - 1] instanceof String message)
            name = message + " — " + name;
        if (values.length > 2 && values[2] instanceof Number tolerance)
            name += "، السماحية " + display(tolerance);
        Allure.getLifecycle().startStep(id, new StepResult().setName(name));
    }

    @AfterReturning("assertionCall()")
    public void passed() {
        finish(null);
    }

    @AfterThrowing(pointcut = "assertionCall()", throwing = "failure")
    public void failed(Throwable failure) {
        finish(failure);
    }

    private void finish(Throwable failure) {
        var frames = FRAMES.get();
        if (frames.isEmpty()) {
            FRAMES.remove();
            return;
        }
        String id = frames.pop();
        if (frames.isEmpty()) FRAMES.remove();
        if (id.isEmpty()) return;
        try {
            if (failure == null) {
                Allure.getLifecycle().updateStep(id, step -> step.setStatus(Status.PASSED));
                return;
            }
            var trace = new java.io.StringWriter();
            failure.printStackTrace(new java.io.PrintWriter(trace));
            Allure.getLifecycle()
                    .updateStep(
                            id,
                            step ->
                                    step.setStatus(
                                                    failure instanceof AssertionError
                                                            ? Status.FAILED
                                                            : Status.BROKEN)
                                            .setStatusDetails(
                                                    new StatusDetails()
                                                            .setMessage(failure.getMessage())
                                                            .setTrace(trace.toString())));
            var driver = Plugin_ERP.Drivers.WebDriverFactory.getDriver();
            if (driver != null)
                Plugin_ERP.Util.ScreenShotsUtils.takeScreenShot(driver, "موضع فشل التحقق");
        } finally {
            Allure.getLifecycle().stopStep(id);
        }
    }
}
