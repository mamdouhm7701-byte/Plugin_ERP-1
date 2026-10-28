/* شرح الملف: اختبار قراءة سعة المخازن والفروع ورفض البدء عند نفادها، دون اختراع حد غير ظاهر.
 * فائدته: يمنع فحص الجاهزية من إعلان نجاح غير مثبت أو تغيير بيانات الاشتراك.
 */
package SuiteSupport;

import Plugin_ERP.Util.RunEnvironmentGate;
import java.lang.reflect.Proxy;
import java.nio.file.*;
import java.util.*;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class CapacityPreflightTest {
    @Test
    public void consumedBranchSlotDoesNotBlockWorkInsideTheNewBranch() {
        var rows =
                CapacityPreflight.quotas(
                        Map.of(
                                "planBranches",
                                2,
                                "usedBranches",
                                2,
                                "planStores",
                                5,
                                "usedStores",
                                3));
        Assert.assertTrue(CapacityPreflight.exhaustedRequired(rows, false).isEmpty());
        Assert.assertEquals(
                CapacityPreflight.exhaustedRequired(rows, true).orElseThrow(), "Branches");
    }

    @Test
    public void actualStoreHeadroomCapsWorkersWithoutInventingUnknownCapacity() throws Exception {
        Path folder = Files.createTempDirectory("capacity-workers-");
        Path file = folder.resolve("capacity-preflight.json");
        Assert.assertEquals(CapacityPreflight.workerLimit(3, folder), 3);
        Files.writeString(
                file,
                new org.json.JSONObject(
                                Map.of(
                                        "quotas",
                                        CapacityPreflight.quotas(
                                                Map.of("planStores", 5, "usedStores", 4))))
                        .toString());
        Assert.assertEquals(CapacityPreflight.workerLimit(3, folder), 1);
        Files.writeString(file, "{\"status\":\"UNAVAILABLE\"}");
        Assert.assertEquals(CapacityPreflight.workerLimit(3, folder), 3);
    }

    @Test
    public void requiredStoreAndBranchCapacityAreCheckedFromReportedCounts() {
        Assert.assertEquals(
                CapacityPreflight.exhaustedRequired(
                                CapacityPreflight.quotas(Map.of("planStores", 5, "usedStores", 5)))
                        .orElseThrow(),
                "Stores");
        Assert.assertEquals(
                CapacityPreflight.exhaustedRequired(
                                CapacityPreflight.quotas(
                                        Map.of("planBranches", 2, "usedBranches", 3)))
                        .orElseThrow(),
                "Branches");
        Assert.assertTrue(
                CapacityPreflight.exhaustedRequired(
                                CapacityPreflight.quotas(
                                        Map.of(
                                                "planStores",
                                                5,
                                                "usedStores",
                                                3,
                                                "planSales",
                                                10,
                                                "usedSales",
                                                10)))
                        .isEmpty());
    }

    @Test
    public void zeroOrMissingLimitsAreUnknownRatherThanInventedAsUnlimited() {
        var rows = CapacityPreflight.quotas(Map.of("planStores", 0, "usedStores", 5));
        Assert.assertTrue(rows.stream().allMatch(r -> r.get("status").equals("NOT_ADVERTISED")));
        Assert.assertTrue(CapacityPreflight.exhaustedRequired(rows).isEmpty());
    }

    @Test
    public void unavailableDashboardIsRecordedWithoutPreventingOtherwiseValidExecution()
            throws Exception {
        Path output = Files.createTempDirectory("capacity-unavailable-");
        CapacityPreflight.verify(driver(Map.of("available", false, "status", 403)), output);
        Assert.assertTrue(
                Files.readString(output.resolve("capacity-preflight.json"))
                        .contains("UNAVAILABLE"));
    }

    @Test
    public void knownExhaustionStopsBeforeBranchCreationAndHasSnapshotEvidence() throws Exception {
        String previous = System.getProperty("allure.run.id");
        System.setProperty("allure.run.id", "capacity-preflight-" + UUID.randomUUID());
        try {
            Path output = Files.createTempDirectory("capacity-full-");
            var failure =
                    Assert.expectThrows(
                            IllegalStateException.class,
                            () ->
                                    CapacityPreflight.verify(
                                            driver(
                                                    Map.of(
                                                            "available",
                                                            true,
                                                            "data",
                                                            Map.of(
                                                                    "planStores",
                                                                    3,
                                                                    "usedStores",
                                                                    3))),
                                            output));
            Assert.assertTrue(failure.getMessage().contains("LICENSE_CAPACITY"));
            Assert.assertEquals(
                    RunEnvironmentGate.current().block().orElseThrow().source(),
                    "CAPACITY_PREFLIGHT");
            String json = Files.readString(output.resolve("capacity-preflight.json"));
            Assert.assertTrue(json.contains("BLOCKED"));
            Assert.assertFalse(json.contains("password"));
        } finally {
            if (previous == null) System.clearProperty("allure.run.id");
            else System.setProperty("allure.run.id", previous);
        }
    }

    private static WebDriver driver(Map<String, Object> response) {
        Object timeouts =
                Proxy.newProxyInstance(
                        CapacityPreflightTest.class.getClassLoader(),
                        new Class<?>[] {WebDriver.Timeouts.class},
                        (p, m, a) -> p);
        Object options =
                Proxy.newProxyInstance(
                        CapacityPreflightTest.class.getClassLoader(),
                        new Class<?>[] {WebDriver.Options.class},
                        (p, m, a) -> timeouts);
        return (WebDriver)
                Proxy.newProxyInstance(
                        CapacityPreflightTest.class.getClassLoader(),
                        new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                        (proxy, method, args) ->
                                switch (method.getName()) {
                                    case "manage" -> options;
                                    case "executeAsyncScript" -> {
                                        String script = args[0].toString();
                                        Assert.assertTrue(
                                                script.contains("SubscriptionsDashboard"));
                                        Assert.assertFalse(script.contains("app/create"));
                                        Assert.assertFalse(script.contains("app/update"));
                                        yield response;
                                    }
                                    default -> null;
                                });
    }
}
