/* شرح الملف: يتأكد أن التعارض الملغى يمكن تكراره بحد أقصى، وأن أخطاء الحفظ المجهولة لا تتكرر فتنتج بيانات مضاعفة. */
package TestSupport;

import java.util.concurrent.atomic.AtomicInteger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FixtureCreateRetryTest {
    @Test
    public void retriesOnlyExplicitlyRolledBackCreate() {
        var calls = new AtomicInteger();
        String value =
                FixtureCreateRetry.run(
                        "create",
                        () -> {
                            if (calls.incrementAndGet() < 3)
                                throw new FixtureApiFailure(
                                        500,
                                        "Transaction was deadlocked and chosen as the deadlock victim");
                            return "owned-id";
                        });
        Assert.assertEquals(value, "owned-id");
        Assert.assertEquals(calls.get(), 3);
    }

    @Test
    public void ambiguousFailureAndNonCreateAreNeverRetried() {
        for (String action : java.util.List.of("create", "delete", "update")) {
            var calls = new AtomicInteger();
            var failure =
                    new FixtureApiFailure(
                            500,
                            action.equals("create")
                                    ? "Lost response; commit unknown"
                                    : "Transaction deadlocked, deadlock victim");
            var thrown =
                    Assert.expectThrows(
                            FixtureApiFailure.class,
                            () ->
                                    FixtureCreateRetry.run(
                                            action,
                                            () -> {
                                                calls.incrementAndGet();
                                                throw failure;
                                            }));
            Assert.assertSame(thrown, failure);
            Assert.assertEquals(calls.get(), 1);
        }
    }

    @Test
    public void threeFailuresStayFailedAndKeepTheirOriginalError() {
        var calls = new AtomicInteger();
        var failure = new FixtureApiFailure(500, "Transaction deadlocked; deadlock victim");
        Assert.assertSame(
                Assert.expectThrows(
                        FixtureApiFailure.class,
                        () ->
                                FixtureCreateRetry.run(
                                        "create",
                                        () -> {
                                            calls.incrementAndGet();
                                            throw failure;
                                        })),
                failure);
        Assert.assertEquals(calls.get(), 3);
    }
}
