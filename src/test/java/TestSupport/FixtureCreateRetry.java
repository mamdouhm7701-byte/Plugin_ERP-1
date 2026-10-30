/* شرح الملف: يعيد تجهيز السجل فقط عندما تؤكد قاعدة البيانات إلغاء العملية بسبب تعارض أقفال. لا يكرر حفظًا مجهول النتيجة. */
package TestSupport;

import java.util.Locale;
import java.util.function.Supplier;

public final class FixtureCreateRetry {
    private FixtureCreateRetry() {}

    public static <T> T run(String action, String endpoint, Supplier<T> operation) {
        return run(
                action, () -> Plugin_ERP.Util.RunPerformance.request(action, endpoint, operation));
    }

    public static <T> T run(String action, Supplier<T> operation) {
        for (int attempt = 1; ; attempt++) {
            try {
                return operation.get();
            } catch (FixtureApiFailure failure) {
                String message = String.valueOf(failure.getMessage()).toLowerCase(Locale.ROOT);
                if (!action.equals("create")
                        || attempt >= 3
                        || failure.status != 500
                        || !message.contains("deadlocked")
                        || !message.contains("victim")) throw failure;
                Plugin_ERP.Bots.LogUtils.warn(
                        "Fixture create rolled back after a database deadlock; bounded retry "
                                + attempt
                                + "/2");
            }
        }
    }
}
