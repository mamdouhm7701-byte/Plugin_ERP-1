package Plugin_ERP.Util.Allur;

import Plugin_ERP.Listeners.TestNGListeners;
import java.lang.reflect.Proxy;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ReportingContractsTest {
    @Test
    public void knownCategoryRequiresExplicitReferenceEvenForMultilineFailures() throws Exception {
        try (var input = getClass().getResourceAsStream("/allure-categories.json")) {
            Assert.assertNotNull(input);
            var categories =
                    new org.json.JSONArray(
                            new String(
                                    input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
            String known = categories.getJSONObject(0).getString("messageRegex");
            String unknown = categories.getJSONObject(1).getString("messageRegex");
            Assert.assertTrue(
                    java.util.regex.Pattern.matches(known, "Failure\nKNOWN_BUG:ERP-17\nEvidence"));
            Assert.assertFalse(
                    java.util.regex.Pattern.matches(
                            known, "Unexpected failure in a test with a known history"));
            Assert.assertTrue(
                    java.util.regex.Pattern.matches(unknown, "Unexpected failure\nDetails"));
            Assert.assertFalse(
                    java.util.regex.Pattern.matches(
                            unknown, "Failure\nKNOWN_BUG:ERP-17\nEvidence"));
        }
    }

    @Test
    public void capabilitiesAreReadThroughInterfaceRatherThanConcreteDriverType() {
        var capabilities = new org.openqa.selenium.remote.DesiredCapabilities();
        capabilities.setBrowserName("chrome");
        capabilities.setVersion("130.1");
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, HasCapabilities.class},
                                (proxy, method, args) -> {
                                    if (method.getName().equals("getCapabilities"))
                                        return capabilities;
                                    throw new AssertionError(method.getName());
                                });
        Assert.assertEquals(AllureEnvironment.browserDetails(driver).get("Browser"), "chrome");
        Assert.assertEquals(
                AllureEnvironment.browserDetails(driver).get("Browser Version"), "130.1");
    }

    @Test
    public void successAndSkipCallbacksDoNotAccessBrowserForScreenshots() throws Exception {
        var field = Plugin_ERP.Drivers.WebDriverFactory.class.getDeclaredField("driverThreadLocal");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        var current = (ThreadLocal<WebDriver>) field.get(null);
        WebDriver previous = current.get();
        current.set(
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class},
                                (proxy, method, args) -> {
                                    throw new AssertionError(
                                            "Unexpected browser access: " + method.getName());
                                }));
        try {
            new TestNGListeners().onTestSuccess(null);
            new TestNGListeners().onTestSkipped(null);
        } finally {
            if (previous == null) current.remove();
            else current.set(previous);
        }
    }
}
