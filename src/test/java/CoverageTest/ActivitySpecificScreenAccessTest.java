package CoverageTest;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Util.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

/** Activity-specific route access only. It never submits a form or claims business coverage. */
public final class ActivitySpecificScreenAccessTest extends BaseTest {
    @DataProvider
    public Object[][] screens() {
        var activity = BranchProfiles.selected().orElseThrow().expected().activityType();
        List<String> routes =
                switch (activity) {
                    case CONSTRUCTION -> List.of("contracts/new");
                    case CAR_SHOWROOM -> List.of("car-colors");
                    case CONCRETE ->
                            List.of(
                                    "cars/",
                                    "castingTypes",
                                    "cementTypes",
                                    "concrete-exchange-order/new",
                                    "concrete-reports",
                                    "concrete-sales-invoice/new",
                                    "concrete-under-review/new",
                                    "drivers/",
                                    "maxTemperature",
                                    "projects",
                                    "reservations/new");
                    default ->
                            throw new IllegalArgumentException(
                                    "This suite targets Construction, Concrete or CarShowroom");
                };
        return routes.stream().map(route -> new Object[] {route}).toArray(Object[][]::new);
    }

    @Test(dataProvider = "screens")
    public void specificRouteMountsWithoutSubmitting(String route) throws Exception {
        requirePreparedSession();
        var expected = BranchProfiles.selected().orElseThrow();
        expected.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        driver.get(expected.expected().origin() + "/#/" + route);
        new WebDriverWait(driver, Duration.ofSeconds(35))
                .until(
                        d ->
                                Boolean.TRUE.equals(
                                        VueComponentAccess.execute(
                                                d,
                                                """
            const r=erpVue.root()?.$router?.currentRoute;
            const normalize=s=>{while(s.endsWith('/'))s=s.slice(0,-1);return s.toLowerCase();};
            const vm=r?.matched?.at(-1)?.instances?.default;
            return normalize(r?.path||'')===normalize('/'+arguments[0])&&!!vm?._isMounted&&
              !!vm.$el?.isConnected&&!!vm.$el?.getClientRects?.().length;
            """,
                                                route)));
        var headings =
                driver.findElements(By.cssSelector("h4.content-header-title")).stream()
                        .filter(WebElement::isDisplayed)
                        .map(WebElement::getText)
                        .filter(text -> !text.isBlank())
                        .toList();
        Assert.assertFalse(headings.isEmpty(), "Activity screen has no visible heading");
        expected.verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        var evidence =
                Map.of(
                        "activity",
                        expected.expected().activityType().code(),
                        "route",
                        "#/" + route,
                        "directAccess",
                        "PASSED",
                        "businessValidation",
                        "NOT_RUN",
                        "headings",
                        headings,
                        "capturedUtc",
                        java.time.Instant.now().toString());
        Path path =
                Path.of(
                        "target/activity-screen-access",
                        expected.expected().activityType().code(),
                        route.replace('/', '_') + ".json");
        Files.createDirectories(path.getParent());
        Files.writeString(path, new org.json.JSONObject(evidence).toString(2));
        io.qameta.allure.Allure.addAttachment(
                "Activity screen access",
                "application/json",
                new org.json.JSONObject(evidence).toString(2),
                ".json");
    }
}
