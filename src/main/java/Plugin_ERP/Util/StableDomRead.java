/* شرح الملف: يعيد قراءة عناصر الشاشة إذا استبدلتها الواجهة أثناء القراءة، من غير تكرار ضغط أو حفظ أو حذف. */
package Plugin_ERP.Util;

import java.time.Duration;
import java.util.function.Supplier;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class StableDomRead {
    private StableDomRead() {}

    private record Value<T>(T value) {}

    public static <T> T read(WebDriver driver, Supplier<T> reader) {
        return new WebDriverWait(driver, Duration.ofSeconds(2))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(StaleElementReferenceException.class)
                .until(d -> new Value<>(reader.get()))
                .value();
    }
}
