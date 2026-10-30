/* شرح الملف: يعيد استخدام قراءة الفرع التي تم التحقق منها خلال نفس فحص الطلب فقط، ويمنع انتقالها إلى طلب أو متصفح آخر. */
package Plugin_ERP.BranchProfiles;

import org.openqa.selenium.WebDriver;

public final class VerificationSnapshot {
    public record Context(String origin, Object branchId) {}

    private static final ThreadLocal<State> ACTIVE = new ThreadLocal<>();

    private static final class State {
        final WebDriver driver;
        Context context;

        State(WebDriver driver) {
            this.driver = driver;
        }
    }

    private VerificationSnapshot() {}

    public static void verify(WebDriver driver, Runnable verification) {
        State previous = ACTIVE.get();
        ACTIVE.set(new State(driver));
        try {
            verification.run();
        } finally {
            if (previous == null) ACTIVE.remove();
            else ACTIVE.set(previous);
        }
    }

    static void verified(WebDriver driver, BranchSnapshot snapshot) {
        State state = ACTIVE.get();
        if (state != null
                && state.driver == driver
                && !"false"
                        .equalsIgnoreCase(System.getProperty("fixture.verification.reuse", "true")))
            state.context = new Context(snapshot.origin(), snapshot.branchId());
    }

    public static Context current(WebDriver driver) {
        State state = ACTIVE.get();
        return state != null && state.driver == driver ? state.context : null;
    }
}
