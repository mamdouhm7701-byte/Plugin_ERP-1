/* شرح الملف: مراجعة بقاء كل تجربة على إعدادات الفرع الذي اختارته.
 */
package Plugin_ERP.BranchProfiles;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class BranchProfileRuntimeTest {
    private BranchProfile profile(String policy) {
        return new BranchProfile(
                "reference",
                BranchSnapshot.from(
                        Map.of(
                                "origin",
                                "https://erp.example",
                                "branchId",
                                "owned",
                                "activityType",
                                "Public",
                                "discountLevel",
                                "Items",
                                "taxPolicy",
                                policy,
                                "stocktakingMethod",
                                "Periodic")));
    }

    @Test
    public void profileFingerprintChangesOnlyWithItsNameOrExpectedConfiguration() {
        Assert.assertEquals(profile("NetBased").fingerprint(), profile("NetBased").fingerprint());
        Assert.assertNotEquals(
                profile("NetBased").fingerprint(), profile("ItemBased").fingerprint());
        Assert.assertEquals(profile("NetBased").fingerprint().length(), 64);
    }

    @Test
    public void nestedScopesRestoreThePreviousProfileAndDoNotLeakToAnotherThread()
            throws Exception {
        Assert.assertTrue(BranchProfileScope.current().isEmpty());
        var first = profile("NetBased");
        try (var outer = BranchProfileScope.bind(first)) {
            try (var inner = BranchProfileScope.bind(profile("ItemBased"))) {
                Assert.assertEquals(
                        BranchProfileScope.current().orElseThrow().expected().taxPolicy(),
                        BranchPolicies.TaxPolicy.ITEM_BASED);
            }
            Assert.assertSame(BranchProfileScope.current().orElseThrow(), first);
            var isolated = new java.util.concurrent.atomic.AtomicBoolean();
            Thread other = new Thread(() -> isolated.set(BranchProfileScope.current().isEmpty()));
            other.start();
            other.join();
            Assert.assertTrue(isolated.get());
        }
        Assert.assertTrue(BranchProfileScope.current().isEmpty());
    }

    @Test
    public void disabledGuardDoesNotAccessBrowser() {
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (proxy, method, args) -> {
                                    throw new AssertionError(
                                            "Legacy run must not read branch policies");
                                });
        BranchProfileGuard.capture().verify(driver);
    }

    @DataProvider
    public Object[][] fixtureApis() {
        return new Object[][] {
            {"Invoice.SalesFixtureApi"},
            {"Purches.PurchaseFixtureApi"},
            {"StoreTest.StoreFixtureApi"},
            {"ProductsTest.ProductsFixtureApi"},
            {"DealerModelTest.DealerFixtureApi"},
            {"FinancialMovementsTest.FinancialFixtureApi"},
            {"SpecialSalesTest.SpecialSalesFixtureApi"},
            {"TestSupport.BasicDataFixturesApi"}
        };
    }

    @Test(dataProvider = "fixtureApis")
    public void policyDriftStopsEveryFixtureTransportBeforeDispatch(String className)
            throws Exception {
        AtomicInteger writes = new AtomicInteger();
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (proxy, method, args) -> {
                                    if (method.getName().equals("executeScript"))
                                        return profile("ItemBased").expected().fields();
                                    if (method.getName().equals("executeAsyncScript")) {
                                        writes.incrementAndGet();
                                        throw new AssertionError(
                                                "Write dispatched after policy drift");
                                    }
                                    throw new AssertionError(
                                            "Unexpected operation before guard: "
                                                    + method.getName());
                                });
        try (var scope = BranchProfileScope.bind(profile("NetBased"))) {
            Class<?> type = Class.forName(className);
            var constructor = type.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            Object[] arguments =
                    java.util.Arrays.stream(constructor.getParameterTypes())
                            .map(
                                    parameter -> {
                                        if (parameter == WebDriver.class) return driver;
                                        if (parameter == Runnable.class) return (Runnable) () -> {};
                                        if (parameter == String.class) return "https://erp.example";
                                        if (parameter == Object.class) return "owned";
                                        throw new AssertionError(parameter);
                                    })
                            .toArray();
            Object api = constructor.newInstance(arguments);
            var call = type.getDeclaredMethod("call", String.class, String.class, Map.class);
            call.setAccessible(true);
            var failure =
                    Assert.expectThrows(
                            java.lang.reflect.InvocationTargetException.class,
                            () -> call.invoke(api, "create", "Accounts", Map.of()));
            Assert.assertTrue(
                    failure.getCause() instanceof IllegalStateException,
                    String.valueOf(failure.getCause()));
            Assert.assertTrue(failure.getCause().getMessage().contains("taxPolicy"));
            Assert.assertEquals(writes.get(), 0);
        }
    }

    @Test
    public void policyAndAdditionalTaxAreCheckedTogetherAndNeverCachedAcrossCalls() {
        var selected =
                new BranchProfile(
                        "combined",
                        BranchSnapshot.from(
                                Map.of(
                                        "origin",
                                        "https://erp.example",
                                        "branchId",
                                        "owned",
                                        "activityType",
                                        "Restaurant",
                                        "discountLevel",
                                        "Items",
                                        "taxPolicy",
                                        "NetBased",
                                        "stocktakingMethod",
                                        "Periodic")));
        var values = new java.util.LinkedHashMap<String, Object>(selected.expected().fields());
        values.put("additionalEnabled", true);
        values.put("additionalRate", 100);
        var reads = new AtomicInteger();
        WebDriver driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (p, m, args) -> {
                                    if (m.getName().equals("executeScript")) {
                                        reads.incrementAndGet();
                                        return values;
                                    }
                                    throw new AssertionError(m.getName());
                                });
        String previous = System.getProperty("business.additionalTax");
        System.setProperty("business.additionalTax", "true");
        try (var scope = BranchProfileScope.bind(selected)) {
            var guard = BranchProfileGuard.capture();
            guard.verify(driver);
            Assert.assertEquals(reads.get(), 1);
            values.put("additionalRate", 50);
            Assert.expectThrows(IllegalStateException.class, () -> guard.verify(driver));
            Assert.assertEquals(reads.get(), 2);
        } finally {
            if (previous == null) System.clearProperty("business.additionalTax");
            else System.setProperty("business.additionalTax", previous);
        }
    }
}
