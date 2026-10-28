package SpecialSalesTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.System_Modules.Login.SpecialSales.RestaurantInvoicePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import java.nio.file.*;
import java.time.Duration;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.*;

/** Explicit activity smoke: mounts restaurant-specific screens without creating transactions. */
public final class RestaurantScreenAccessTest extends BaseTest {
    private SpecialSalesTestData prerequisites;

    @AfterClass(alwaysRun = true)
    public void restoreOwnedScreenPrerequisites() {
        if (prerequisites != null) prerequisites.close();
    }

    @DataProvider
    public Object[][] screens() {
        return new Object[][] {
            {"tables", "الطاولات"},
            {"restaurant-orders", "طلبات المطبخ"},
            {"restaurant-menu", "فاتورة المطعم"}
        };
    }

    @Test(dataProvider = "screens")
    public void restaurantSpecificScreenMountsForCurrentActivity(String route, String title)
            throws Exception {
        requirePreparedSession();
        var baseline = BranchProfileReader.capture(driver, Duration.ofSeconds(30));
        Assert.assertEquals(
                baseline.activityType(),
                ActivityType.RESTAURANT,
                "Activity access probe must not change branch settings");
        if (route.equals("restaurant-menu")) {
            prerequisites = new SpecialSalesTestData(driver);
            prerequisites.beginScenario();
            var customer = prerequisites.customer();
            prerequisites.configureOwnedDefaults(customer);
            new RestaurantInvoicePage(driver).open();
            io.qameta.allure.Allure.addAttachment(
                    "Owned restaurant navigation prerequisites",
                    "application/json",
                    new org.json.JSONObject(
                                    Map.of(
                                            "customer",
                                            customer.name(),
                                            "store",
                                            prerequisites.store(1).name(),
                                            "branchId",
                                            baseline.branchId()))
                            .toString(2),
                    ".json");
        } else new ScreenPage(driver).open(route, title);
        new BranchProfile("restaurant-screen-access", baseline)
                .verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        Path evidence = Path.of("target/activity-screen-access/Restaurant", route + ".json");
        Files.createDirectories(evidence.getParent());
        Files.writeString(
                evidence,
                new org.json.JSONObject(
                                Map.of(
                                        "activity",
                                        "Restaurant",
                                        "route",
                                        "#/" + route,
                                        "directAccess",
                                        "PASSED",
                                        "businessValidation",
                                        "NOT_RUN",
                                        "capturedUtc",
                                        java.time.Instant.now().toString()))
                        .toString(2));
    }
}
