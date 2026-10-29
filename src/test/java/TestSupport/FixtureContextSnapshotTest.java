/* شرح الملف: يثبت أن فحص الرابط والفرع يتم بأمر واحد كل مرة، ويرفض التغيير فورًا دون الاعتماد على نتيجة قديمة. */
package TestSupport;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureContextSnapshotTest {
    @Test
    public void everyContextCheckIsOneFreshReadAndChangedBranchIsRejected() {
        var reads = new AtomicInteger();
        var current =
                new AtomicReference<Map<String, Object>>(
                        Map.of("origin", "offline", "branchId", "owned"));
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (p, m, args) -> {
                                    if (m.getName().equals("executeScript")) {
                                        reads.incrementAndGet();
                                        Assert.assertTrue(
                                                ((String) args[0])
                                                        .contains("fixture-context-snapshot"));
                                        return current.get();
                                    }
                                    throw new AssertionError("Unexpected command");
                                });
        var session = new FixtureSession(driver);
        Assert.assertEquals(reads.get(), 1);
        session.verify();
        Assert.assertEquals(reads.get(), 2);
        current.set(Map.of("origin", "offline", "branchId", "another"));
        Assert.expectThrows(AssertionError.class, session::verify);
        Assert.assertEquals(reads.get(), 3);
        current.set(Map.of("origin", "another-origin", "branchId", "owned"));
        Assert.expectThrows(
                AssertionError.class,
                () -> new FixtureContextGuard(driver, "offline", "owned").verify());
        Assert.assertEquals(reads.get(), 4);
    }
}
