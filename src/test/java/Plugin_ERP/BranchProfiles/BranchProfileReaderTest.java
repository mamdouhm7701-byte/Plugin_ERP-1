/* شرح الملف: اختبارات قراءة إعدادات الفرع كما هي محفوظة في البرنامج.
 */
package Plugin_ERP.BranchProfiles;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BranchProfileReaderTest {
    @Test
    public void configurationIsReadOnlyAndReacquiredAfterBranchHydration() {
        AtomicInteger calls = new AtomicInteger();
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (proxy, method, args) -> {
                                    Assert.assertEquals(
                                            method.getName(),
                                            "executeScript",
                                            "Profile guard must not write or navigate");
                                    String script = (String) args[0];
                                    Assert.assertTrue(
                                            script.contains("required.some"),
                                            "Wait for all policy fields, not branch id alone");
                                    Assert.assertFalse(script.contains("dispatch("));
                                    Assert.assertFalse(script.contains("commit("));
                                    return calls.incrementAndGet() == 1
                                            ? null
                                            : Map.of(
                                                    "origin",
                                                    "https://erp.example",
                                                    "branchId",
                                                    "owned",
                                                    "activityType",
                                                    "Restaurant",
                                                    "discountLevel",
                                                    "Transaction",
                                                    "taxPolicy",
                                                    "PriceIncluded",
                                                    "stocktakingMethod",
                                                    "Continuous");
                                });
        var actual = BranchProfileReader.capture(driver, Duration.ofSeconds(2));
        Assert.assertEquals(actual.activityType(), BranchPolicies.ActivityType.RESTAURANT);
        Assert.assertEquals(actual.discountLevel(), BranchPolicies.DiscountLevel.TRANSACTION);
        Assert.assertEquals(calls.get(), 2);
    }

    @Test
    public void explicitProfileWithInvalidNameStopsBeforeCreatingBrowser() {
        String previous = System.getProperty("branch.profile");
        System.setProperty("branch.profile", "../outside");
        var base =
                new Plugin_ERP.Tests.LoginTest.BaseTest() {
                    @Override
                    protected WebDriver createDriver() {
                        throw new AssertionError("Browser must not start for invalid profile");
                    }
                };
        try {
            Assert.expectThrows(IllegalArgumentException.class, base::setup);
        } finally {
            base.tearDown();
            if (previous == null) System.clearProperty("branch.profile");
            else System.setProperty("branch.profile", previous);
        }
    }

    @Test
    public void branchSelectionNeverChangesOriginOrAdaptsPolicyMismatch() {
        var fields =
                Map.<String, Object>of(
                        "origin",
                        "https://erp.example",
                        "branchId",
                        "owned",
                        "activityType",
                        "Public",
                        "discountLevel",
                        "Transaction",
                        "taxPolicy",
                        "NetBased",
                        "stocktakingMethod",
                        "Periodic");
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (proxy, method, args) -> {
                                    if (method.getName().equals("executeScript")) return fields;
                                    throw new AssertionError(
                                            "Unexpected selection action: " + method.getName());
                                });
        var actual = BranchSnapshot.from(fields);
        BranchSessionSelector.select(driver, new BranchProfile("same", actual));
        var wrongOrigin = new java.util.HashMap<String, Object>(fields);
        wrongOrigin.put("origin", "https://other.example");
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        BranchSessionSelector.select(
                                driver,
                                new BranchProfile("other", BranchSnapshot.from(wrongOrigin))));
        var wrongPolicy = new java.util.HashMap<String, Object>(fields);
        wrongPolicy.put("taxPolicy", "ItemBased");
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        BranchSessionSelector.select(
                                driver,
                                new BranchProfile(
                                        "wrong-policy", BranchSnapshot.from(wrongPolicy))));
    }
}
