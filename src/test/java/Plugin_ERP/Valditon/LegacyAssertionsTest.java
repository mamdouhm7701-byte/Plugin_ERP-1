/* شرح الملف: مراجعة استمرار عمل المقارنات القديمة بنفس النتائج.
 */
package Plugin_ERP.Valditon;

import java.lang.reflect.Proxy;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class LegacyAssertionsTest {
    @Test
    public void visibleElementPasses() {
        withWait("0", () -> new Hard(browser(() -> element(true))).visible());
    }

    @Test
    public void hiddenElementFailsEvenWhenPresent() {
        withWait(
                "0",
                () -> {
                    AssertionError failure =
                            Assert.expectThrows(
                                    AssertionError.class,
                                    () -> new Hard(browser(() -> element(false))).visible());
                    Assert.assertTrue(failure.getMessage().contains("legacy-target"));
                });
    }

    @Test
    public void waitsForHiddenElementToBecomeVisible() {
        AtomicInteger reads = new AtomicInteger();
        withWait(
                "2", () -> new Hard(browser(() -> element(reads.incrementAndGet() > 1))).visible());
        Assert.assertTrue(reads.get() >= 2, "Presence must not end the visibility wait");
    }

    @Test
    public void missingElementFails() {
        withWait(
                "0",
                () ->
                        Assert.expectThrows(
                                AssertionError.class,
                                () ->
                                        new Hard(
                                                        browser(
                                                                () -> {
                                                                    throw new NoSuchElementException(
                                                                            "missing");
                                                                }))
                                                .visible()));
    }

    @Test
    public void staleElementCanBeReacquired() {
        AtomicInteger reads = new AtomicInteger();
        withWait(
                "2",
                () ->
                        new Hard(
                                        browser(
                                                () -> {
                                                    if (reads.incrementAndGet() == 1)
                                                        throw new StaleElementReferenceException(
                                                                "redraw");
                                                    return element(true);
                                                }))
                                .visible());
        Assert.assertTrue(reads.get() >= 2);
    }

    @Test
    public void browserFailureIsNotReportedAsInvisibility() {
        WebDriverException disconnected = new WebDriverException("Browser disconnected");
        withWait(
                "0",
                () -> {
                    WebDriverException failure =
                            Assert.expectThrows(
                                    WebDriverException.class,
                                    () ->
                                            new Hard(
                                                            browser(
                                                                    () -> {
                                                                        throw disconnected;
                                                                    }))
                                                    .visible());
                    Assert.assertSame(failure, disconnected);
                });
    }

    @Test
    public void hiddenElementIsCollectedBySoftAssertion() {
        withWait(
                "0",
                () -> {
                    Soft soft = new Soft(browser(() -> element(false)));
                    soft.visible();
                    AssertionError failure =
                            Assert.expectThrows(AssertionError.class, Validation::AsserAll);
                    Assert.assertTrue(failure.getMessage().contains("legacy-target"));
                    Validation.AsserAll();
                });
    }

    @Test
    public void softFailuresAreCombinedAndResetAfterFailure() {
        Soft soft = new Soft(browser(() -> element(true)));
        soft.check(false, "first-failure");
        soft.equal("actual", "expected", "second-failure");
        AssertionError failure = Assert.expectThrows(AssertionError.class, Validation::AsserAll);
        Assert.assertTrue(failure.getMessage().contains("first-failure"));
        Assert.assertTrue(failure.getMessage().contains("second-failure"));
        soft.check(true, "next-scenario");
        Validation.AsserAll();
        Validation.AsserAll();
    }

    @Test
    public void workersDoNotConsumeEachOthersSoftFailures() throws Exception {
        var worker = Executors.newSingleThreadExecutor();
        try {
            new Soft(browser(() -> element(true))).check(false, "main-thread-only");
            worker.submit(Validation::AsserAll).get();
            AssertionError mainFailure =
                    Assert.expectThrows(AssertionError.class, Validation::AsserAll);
            Assert.assertTrue(mainFailure.getMessage().contains("main-thread-only"));
            worker.submit(() -> new Soft(browser(() -> element(true))).check(false, "worker-only"))
                    .get();
            Validation.AsserAll();
            worker.submit(
                            () -> {
                                AssertionError failure =
                                        Assert.expectThrows(
                                                AssertionError.class, Validation::AsserAll);
                                Assert.assertTrue(failure.getMessage().contains("worker-only"));
                                Validation.AsserAll();
                            })
                    .get();
        } finally {
            worker.shutdownNow();
            try {
                Validation.AsserAll();
            } catch (AssertionError ignored) {
            }
        }
    }

    @Test
    public void pageAndValueChecksKeepActualExpectedOrder() {
        Hard hard = new Hard(browser(() -> element(true)));
        hard.url("https://fixture.test/home");
        hard.title("Fixture home");
        hard.equal("same", "same", "same-value");
        Assert.expectThrows(AssertionError.class, () -> hard.url("https://other.test"));
        Assert.expectThrows(AssertionError.class, () -> hard.title("Other title"));
        Assert.expectThrows(
                AssertionError.class, () -> hard.equal("actual", "expected", "mismatch"));
    }

    private static void withWait(String seconds, Runnable action) {
        String previous = System.getProperty("defultwait");
        System.setProperty("defultwait", seconds);
        try {
            action.run();
        } finally {
            if (previous == null) System.clearProperty("defultwait");
            else System.setProperty("defultwait", previous);
        }
    }

    private static WebDriver browser(Supplier<WebElement> lookup) {
        return (WebDriver)
                Proxy.newProxyInstance(
                        LegacyAssertionsTest.class.getClassLoader(),
                        new Class<?>[] {WebDriver.class},
                        (proxy, method, args) ->
                                switch (method.getName()) {
                                    case "findElement" -> lookup.get();
                                    case "getCurrentUrl" -> "https://fixture.test/home";
                                    case "getTitle" -> "Fixture home";
                                    default ->
                                            throw new UnsupportedOperationException(
                                                    method.getName());
                                });
    }

    private static WebElement element(boolean displayed) {
        return (WebElement)
                Proxy.newProxyInstance(
                        LegacyAssertionsTest.class.getClassLoader(),
                        new Class<?>[] {WebElement.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("isDisplayed")) return displayed;
                            throw new UnsupportedOperationException(method.getName());
                        });
    }

    private static final class Hard extends BaseAssertion {
        Hard(WebDriver driver) {
            super(driver);
        }

        void visible() {
            Visible(By.id("legacy-target"));
        }

        void url(String expected) {
            PageURL(expected);
        }

        void title(String expected) {
            PageTitle(expected);
        }

        void equal(String actual, String expected, String message) {
            Equals(actual, expected, message);
        }

        protected void assertrue(boolean condition, String message) {
            Assert.assertTrue(condition, message);
        }

        protected void asserfalse(boolean condition, String message) {
            Assert.assertFalse(condition, message);
        }

        protected void assertequals(Object actual, Object expected, String message) {
            Assert.assertEquals(actual, expected, message);
        }
    }

    private static final class Soft extends Validation {
        Soft(WebDriver driver) {
            super(driver);
        }

        void visible() {
            Visible(By.id("legacy-target"));
        }

        void check(boolean condition, String message) {
            assertrue(condition, message);
        }

        void equal(Object actual, Object expected, String message) {
            assertequals(actual, expected, message);
        }
    }
}
