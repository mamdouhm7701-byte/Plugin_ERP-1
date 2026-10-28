/* شرح الملف: انتظار وصول رصيد المخزون إلى الكمية المتوقعة.
 */
package SpecialSalesTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.function.Supplier;
import org.openqa.selenium.support.ui.FluentWait;

final class StockBalanceWait {
    private StockBalanceWait() {}

    static void await(Supplier<Object> balance, String expected, Duration timeout) {
        BigDecimal target = new BigDecimal(expected);
        Object[] last = {null};
        new FluentWait<>(balance)
                .withTimeout(timeout)
                .pollingEvery(Duration.ofMillis(250))
                .withMessage(
                        () ->
                                "Stock balance did not reach expected="
                                        + expected
                                        + "; last actual="
                                        + last[0])
                .until(
                        read -> {
                            last[0] = read.get();
                            return new BigDecimal(last[0].toString()).compareTo(target) == 0;
                        });
    }
}
