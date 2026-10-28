/* شرح الملف: اختبارات انتظار وصول رصيد المخزون إلى الكمية المتوقعة.
 */
package SpecialSalesTest;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class StockBalanceWaitTest {
    @Test
    public void delayedStockEventuallyMatches() {
        AtomicInteger reads = new AtomicInteger();
        StockBalanceWait.await(
                () -> reads.incrementAndGet() == 1 ? "1000" : "999.00",
                "999",
                Duration.ofSeconds(2));
        Assert.assertEquals(reads.get(), 2);
    }

    @Test
    public void unchangedStockFailsWithLastActualAndExpected() {
        TimeoutException failure =
                Assert.expectThrows(
                        TimeoutException.class,
                        () -> StockBalanceWait.await(() -> "1000", "999", Duration.ZERO));
        Assert.assertTrue(failure.getMessage().contains("expected=999"));
        Assert.assertTrue(failure.getMessage().contains("last actual=1000"));
    }

    @Test
    public void balanceReadFailurePropagatesWithoutRetry() {
        AtomicInteger reads = new AtomicInteger();
        IllegalStateException cause = new IllegalStateException("Stock API failed");
        IllegalStateException failure =
                Assert.expectThrows(
                        IllegalStateException.class,
                        () ->
                                StockBalanceWait.await(
                                        () -> {
                                            reads.incrementAndGet();
                                            throw cause;
                                        },
                                        "999",
                                        Duration.ofSeconds(2)));
        Assert.assertSame(failure, cause);
        Assert.assertEquals(reads.get(), 1);
    }
}
