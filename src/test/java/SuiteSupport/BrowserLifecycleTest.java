/* شرح الملف: مراجعة فتح وإغلاق المتصفح دون تداخل.
 */
package SuiteSupport;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BrowserLifecycleTest {
    @Test
    @SuppressWarnings("unchecked")
    public void threeWorkerThreadsKeepSeparateBrowsersAndReleaseOnlyTheirOwn() throws Exception {
        var field = WebDriverFactory.class.getDeclaredField("driverThreadLocal");
        field.setAccessible(true);
        ThreadLocal<WebDriver> current = (ThreadLocal<WebDriver>) field.get(null);
        var pool = java.util.concurrent.Executors.newFixedThreadPool(3);
        var ready = new java.util.concurrent.CyclicBarrier(3);
        var browsers = java.util.concurrent.ConcurrentHashMap.<Probe>newKeySet();
        try {
            var tasks = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for (int i = 0; i < 3; i++)
                tasks.add(
                        pool.submit(
                                () -> {
                                    Probe probe = new Probe(false, false);
                                    current.set(probe.fake);
                                    try {
                                        browsers.add(probe);
                                        ready.await(10, java.util.concurrent.TimeUnit.SECONDS);
                                        Assert.assertSame(WebDriverFactory.getDriver(), probe.fake);
                                        WebDriverFactory.quitDriver();
                                        Assert.assertEquals(probe.quits.get(), 1);
                                        Assert.assertNull(WebDriverFactory.getDriver());
                                    } catch (Exception e) {
                                        throw new IllegalStateException(e);
                                    } finally {
                                        current.remove();
                                    }
                                }));
            for (var task : tasks) task.get(15, java.util.concurrent.TimeUnit.SECONDS);
            Assert.assertEquals(browsers.size(), 3);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    public void headlessSetupKeepsTheDesktopViewportInsteadOfMaximizing() throws Exception {
        String previous = System.getProperty("ExecutionType");
        Probe probe = new Probe(false, false);
        System.setProperty("ExecutionType", "LocalHeadless");
        try {
            probe.setup();
            Assert.assertEquals(probe.viewport, new Dimension(1920, 1080));
            Assert.assertFalse(probe.maximized);
        } finally {
            probe.tearDown();
            if (previous == null) System.clearProperty("ExecutionType");
            else System.setProperty("ExecutionType", previous);
        }
    }

    @Test
    public void closingOneClassDoesNotCloseAnotherClassBrowser() throws Exception {
        Probe first = new Probe(false, false);
        Probe second = new Probe(false, false);
        try {
            first.setup();
            second.setup();
            Assert.assertNotSame(first.browser(), second.browser());
            first.tearDown();
            Assert.assertEquals(first.quits.get(), 1);
            Assert.assertEquals(second.quits.get(), 0);
            Assert.assertNotNull(second.browser());
        } finally {
            first.tearDown();
            second.tearDown();
        }
        Assert.assertEquals(second.quits.get(), 1);
    }

    @Test
    public void loginFailureClosesItsBrowserAndKeepsTheOriginalFailure() {
        Probe probe = new Probe(true, true);
        AssertionError failure = Assert.expectThrows(AssertionError.class, probe::setup);
        Assert.assertEquals(failure.getMessage(), "Login failed");
        Assert.assertEquals(failure.getSuppressed().length, 1);
        Assert.assertEquals(probe.quits.get(), 1);
        Assert.assertNull(probe.browser());
        var skipped = Assert.expectThrows(org.testng.SkipException.class, probe::requireReady);
        Assert.assertSame(
                skipped.getCause(),
                failure,
                "Dependent preparation must retain the original setup failure");
        probe.tearDown();
        Assert.assertEquals(probe.quits.get(), 1);
    }

    @Test
    public void missingLoginClassClosesBrowserAndSkipsDependentPreparationWithOriginalCause() {
        Probe probe = new Probe(false, false);
        var original = new NoClassDefFoundError("Plugin_ERP/System_Modules/Login/Login/LoginPage");
        probe.linkageFailure = original;
        var failure = Assert.expectThrows(NoClassDefFoundError.class, probe::setup);
        Assert.assertSame(failure, original);
        Assert.assertEquals(probe.quits.get(), 1);
        Assert.assertNull(probe.browser());
        var skipped = Assert.expectThrows(org.testng.SkipException.class, probe::requireReady);
        Assert.assertSame(skipped.getCause(), original);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void quitFailureReleasesFactoryAndDuplicateInitCannotReplaceAnActiveBrowser()
            throws Exception {
        var field = WebDriverFactory.class.getDeclaredField("driverThreadLocal");
        field.setAccessible(true);
        ThreadLocal<WebDriver> current = (ThreadLocal<WebDriver>) field.get(null);
        WebDriver previous = current.get();
        Probe active = new Probe(false, true);
        current.set(active.fake);
        try {
            Assert.expectThrows(IllegalStateException.class, WebDriverFactory::initDriver);
            Assert.assertSame(WebDriverFactory.getDriver(), active.fake);
            Assert.expectThrows(WebDriverException.class, WebDriverFactory::quitDriver);
            Assert.assertNull(WebDriverFactory.getDriver());
        } finally {
            if (previous == null) current.remove();
            else current.set(previous);
        }
    }

    private static final class Probe extends BaseTest {
        void requireReady() {
            requirePreparedSession();
        }

        final AtomicInteger quits = new AtomicInteger();
        final WebDriver fake;
        private final boolean failLogin;
        private LinkageError linkageFailure;
        Dimension viewport;
        boolean maximized;

        Probe(boolean failLogin, boolean failQuit) {
            this.failLogin = failLogin;
            fake =
                    (WebDriver)
                            Proxy.newProxyInstance(
                                    getClass().getClassLoader(),
                                    new Class<?>[] {WebDriver.class},
                                    (proxy, method, args) -> {
                                        if (method.getName().equals("quit")) {
                                            quits.incrementAndGet();
                                            if (failQuit)
                                                throw new WebDriverException("Quit failed");
                                        }
                                        if (method.getName().equals("manage")) return options();
                                        return null;
                                    });
        }

        private WebDriver.Options options() {
            return (WebDriver.Options)
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebDriver.Options.class},
                            (proxy, method, args) -> {
                                if (method.getName().equals("window"))
                                    return Proxy.newProxyInstance(
                                            getClass().getClassLoader(),
                                            new Class<?>[] {WebDriver.Window.class},
                                            (p, m, a) -> {
                                                if (m.getName().equals("setSize"))
                                                    viewport = (Dimension) a[0];
                                                if (m.getName().equals("maximize"))
                                                    maximized = true;
                                                return null;
                                            });
                                if (method.getName().equals("timeouts"))
                                    return Proxy.newProxyInstance(
                                            getClass().getClassLoader(),
                                            new Class<?>[] {WebDriver.Timeouts.class},
                                            (p, m, a) -> p);
                                return null;
                            });
        }

        @Override
        protected WebDriver createDriver() {
            return fake;
        }

        @Override
        protected void login() {
            if (linkageFailure != null) throw linkageFailure;
            if (failLogin) throw new AssertionError("Login failed");
        }

        WebDriver browser() {
            return driver;
        }
    }
}
