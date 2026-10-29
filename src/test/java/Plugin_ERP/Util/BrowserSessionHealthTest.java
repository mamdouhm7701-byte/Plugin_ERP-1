/* شرح الملف: يتأكد أن تعليق المتصفح يوقف الأوامر التالية فورًا، وأن انتهاء انتظار عنصر عادي لا يلغي الجلسة ولا يمنع إغلاقها. */
package Plugin_ERP.Util;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BrowserSessionHealthTest {
    private WebDriver fake(AtomicInteger commands, AtomicInteger quits, boolean terminal) {
        return (WebDriver)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("hashCode"))
                                return System.identityHashCode(proxy);
                            if (method.getName().equals("equals")) return proxy == args[0];
                            if (method.getName().equals("toString")) return "fake-browser";
                            if (method.getName().equals("quit")) {
                                quits.incrementAndGet();
                                return null;
                            }
                            commands.incrementAndGet();
                            if (method.getName().equals("getTitle"))
                                throw new TimeoutException(
                                        terminal
                                                ? "timeout: Timed out receiving message from renderer: 60.000"
                                                : "waiting for an element");
                            return "ready";
                        });
    }

    @Test
    public void failedRendererBlocksCommandsButAllowsQuitAndRetainsOriginalFailure() {
        var commands = new AtomicInteger();
        var quits = new AtomicInteger();
        var driver = BrowserSessionHealth.decorate(fake(commands, quits, true));
        try {
            var first = Assert.expectThrows(TimeoutException.class, driver::getTitle);
            Assert.assertTrue(first.getMessage().contains("renderer"));
            Assert.assertFalse(BrowserSessionHealth.available(driver));
            Assert.expectThrows(WebDriverException.class, driver::getCurrentUrl);
            Assert.expectThrows(
                    WebDriverException.class,
                    () -> ((JavascriptExecutor) driver).executeScript("return 1"));
            Assert.assertEquals(commands.get(), 1);
            Assert.expectThrows(
                    org.testng.SkipException.class, () -> BrowserSessionHealth.require(driver));
            driver.quit();
            Assert.assertEquals(quits.get(), 1);
        } finally {
            BrowserSessionHealth.release(driver);
        }
    }

    @Test
    public void ordinaryWaitTimeoutDoesNotMarkTheBrowserAsDead() {
        var commands = new AtomicInteger();
        var driver = BrowserSessionHealth.decorate(fake(commands, new AtomicInteger(), false));
        try {
            Assert.expectThrows(TimeoutException.class, driver::getTitle);
            Assert.assertTrue(BrowserSessionHealth.available(driver));
            Assert.assertEquals(driver.getCurrentUrl(), "ready");
            Assert.assertEquals(commands.get(), 2);
        } finally {
            BrowserSessionHealth.release(driver);
        }
    }

    @Test
    public void knownTerminalMessageIsClassifiedAsEnvironment() {
        var failure = new TimeoutException("Timed out receiving message from renderer: 60.000");
        var test =
                new io.qameta.allure.model.TestResult()
                        .setStatus(io.qameta.allure.model.Status.BROKEN);
        Assert.assertEquals(
                Plugin_ERP.Util.Allur.FailureClassification.classify(test, failure).origin(),
                "شبكة أو بيئة التشغيل");
        Assert.assertEquals(
                Plugin_ERP.Util.Allur.OutcomeClassification.classify(
                                "broken", failure.getMessage(), "", "")
                        .kind(),
                Plugin_ERP.Util.Allur.OutcomeClassification.Kind.ENVIRONMENT);
        Assert.assertTrue(
                Plugin_ERP.Util.Allur.ReadableFailure.describe(failure.getMessage(), "حفظ")
                        .contains("المتصفح توقف"));
    }

    @Test
    public void videoAndHealthWrappersUnwrapToTheOriginalDriverInBoundedSteps() {
        String previous = System.getProperty("video.enabled");
        System.setProperty("video.enabled", "true");
        WebDriver raw =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, TakesScreenshot.class},
                                (proxy, method, args) -> {
                                    if (method.getName().equals("hashCode"))
                                        return System.identityHashCode(proxy);
                                    if (method.getName().equals("equals")) return proxy == args[0];
                                    if (method.getName().equals("toString"))
                                        return "original-driver";
                                    return null;
                                });
        var guarded = BrowserSessionHealth.decorate(raw);
        try {
            WebDriver wrapped = FailureVideoRecorder.decorate(guarded);
            int depth = 0;
            while (wrapped instanceof WrapsDriver driver && depth < 8) {
                wrapped = driver.getWrappedDriver();
                depth++;
            }
            Assert.assertSame(
                    wrapped,
                    raw,
                    "Unwrapping must reach the original driver, not create an endless wrapper chain");
        } finally {
            FailureVideoRecorder.release();
            BrowserSessionHealth.release(guarded);
            if (previous == null) System.clearProperty("video.enabled");
            else System.setProperty("video.enabled", previous);
        }
    }

    @Test
    public void timeoutWithVideoAndThreadProtectionReturnsInsteadOfLooping() throws Exception {
        var worker =
                java.util.concurrent.Executors.newSingleThreadExecutor(
                        r -> {
                            var t = new Thread(r, "timeout-regression");
                            t.setDaemon(true);
                            return t;
                        });
        String previous = System.getProperty("video.enabled");
        System.setProperty("video.enabled", "true");
        try {
            var result =
                    worker.submit(
                            () -> {
                                WebDriver raw =
                                        (WebDriver)
                                                Proxy.newProxyInstance(
                                                        getClass().getClassLoader(),
                                                        new Class<?>[] {
                                                            WebDriver.class,
                                                            TakesScreenshot.class,
                                                            HasCapabilities.class
                                                        },
                                                        (proxy, method, args) -> {
                                                            if (method.getName()
                                                                    .equals("getCapabilities"))
                                                                return new ImmutableCapabilities(
                                                                        "browserName",
                                                                        "local-probe");
                                                            if (method.getName().equals("toString"))
                                                                return "timeout-probe";
                                                            return null;
                                                        });
                                var health = BrowserSessionHealth.decorate(raw);
                                var browser =
                                        org.openqa.selenium.support.ThreadGuard.protect(
                                                FailureVideoRecorder.decorate(health));
                                try {
                                    return Assert.expectThrows(
                                                    TimeoutException.class,
                                                    () ->
                                                            new org.openqa.selenium.support.ui
                                                                            .WebDriverWait(
                                                                            browser,
                                                                            java.time.Duration.ZERO)
                                                                    .until(d -> false))
                                            .getMessage();
                                } finally {
                                    FailureVideoRecorder.release();
                                    BrowserSessionHealth.release(health);
                                }
                            });
            Assert.assertTrue(
                    result.get(5, java.util.concurrent.TimeUnit.SECONDS)
                            .contains("Expected condition failed"));
        } finally {
            worker.shutdownNow();
            if (previous == null) System.clearProperty("video.enabled");
            else System.setProperty("video.enabled", previous);
        }
    }
}
