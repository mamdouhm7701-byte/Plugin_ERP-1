/* شرح الملف: يجعل فحص جاهزية الحقول أقرب، مع إبقاء شرط النجاح ومهلة الانتظار كما هما. يمكن الرجوع للفاصل السابق من إعداد التشغيل. */
package Plugin_ERP.Util;

import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class UiPolling {
    private UiPolling() {}

    public static WebDriverWait configure(WebDriverWait wait) {
        wait.pollingEvery(interval(System.getProperty("ui.wait.poll.millis", "200")));
        return wait;
    }

    static Duration interval(String configured) {
        try {
            long millis = Long.parseLong(configured);
            if (millis >= 100 && millis <= 1000) return Duration.ofMillis(millis);
        } catch (NumberFormatException invalid) {
            throw new IllegalArgumentException(
                    "ui.wait.poll.millis must be between 100 and 1000", invalid);
        }
        throw new IllegalArgumentException("ui.wait.poll.millis must be between 100 and 1000");
    }
}
