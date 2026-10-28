/* شرح الملف: مراجعة اختيار شاشات وحالات كل نشاط وإضافة اختبارات الإضافية عند تفعيلها فقط.
 */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import java.nio.file.*;
import java.util.*;
import org.testng.*;
import org.testng.annotations.Test;
import org.testng.xml.*;

public final class BusinessActivityPlanTest {
    @Test
    public void restaurantBusinessSmokeAddsTaxAcceptanceOnlyWhenEnabled() throws Exception {
        for (var tax : TaxPolicy.values()) {
            for (boolean enabled : List.of(false, true)) {
                var request =
                        new BusinessRunRequest(
                                ActivityType.RESTAURANT,
                                DiscountLevel.TRANSACTION,
                                tax,
                                StocktakingMethod.PERIODIC,
                                enabled);
                var suites =
                        BusinessRunSuites.plan(
                                Path.of(""),
                                request,
                                "Smoke",
                                Files.createTempDirectory("business-smoke-additional-"));
                var names =
                        suites.stream()
                                .flatMap(suite -> suite.getTests().stream())
                                .flatMap(test -> test.getXmlClasses().stream())
                                .map(XmlClass::getName)
                                .toList();
                Assert.assertTrue(
                        names.contains("SpecialSalesTest.RestaurantInvoiceTest"),
                        "Original smoke contract must remain selected");
                Assert.assertEquals(
                        names.contains("SpecialSalesTest.RestaurantMinimumAdditionalTaxTest"),
                        enabled);
                Assert.assertEquals(
                        names.contains("SpecialSalesTest.RestaurantItemBasedAdditionalTaxTest"),
                        enabled && tax == TaxPolicy.ITEM_BASED);
                Assert.assertEquals(
                        names.contains("SpecialSalesTest.RestaurantAdditionalTaxSmokeTest"),
                        enabled && tax != TaxPolicy.ITEM_BASED);
            }
        }
    }

    @Test
    public void activitySelectionUsesNativeOptionIdentityInsteadOfGuessedTranslation() {
        var rows =
                List.of(
                        Map.of("id", "Public", "arabicName", "عام"),
                        Map.of("id", "CarShowroom", "arabicName", "معرض السيارات"));
        Assert.assertEquals(
                BusinessBranchPreparation.activityLabel(rows, "CarShowroom"), "معرض السيارات");
        Assert.expectThrows(
                IllegalStateException.class,
                () -> BusinessBranchPreparation.activityLabel(rows, "Missing"));
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        BusinessBranchPreparation.activityLabel(
                                List.of(rows.getLast(), rows.getLast()), "CarShowroom"));
    }

    @Test
    public void concreteBusinessPlanIncludesQualifiedStockAndBillingJourney() throws Exception {
        var request =
                new BusinessRunRequest(
                        ActivityType.CONCRETE,
                        DiscountLevel.TRANSACTION,
                        TaxPolicy.NET_BASED,
                        StocktakingMethod.PERIODIC,
                        false);
        var suites =
                BusinessRunSuites.plan(
                        Path.of(""),
                        request,
                        "Business checks",
                        Files.createTempDirectory("concrete-business-plan-"));
        var names =
                suites.stream()
                        .flatMap(s -> s.getTests().stream())
                        .flatMap(t -> t.getXmlClasses().stream())
                        .map(XmlClass::getName)
                        .toList();
        Assert.assertTrue(names.contains("Invoice.ConcreteStockJourneyTest"));
        Assert.assertTrue(names.contains("CoverageTest.ConcreteReportRequestTest"));
        Assert.assertTrue(names.contains("Invoice.ConcreteReservationReportTest"));
    }

    @Test
    public void constructionSelectionIncludesItsOwnContractJourney() throws Exception {
        for (var activity : List.of(ActivityType.CONSTRUCTION, ActivityType.CAR_SHOWROOM)) {
            var request =
                    new BusinessRunRequest(
                            activity,
                            DiscountLevel.TRANSACTION,
                            TaxPolicy.NET_BASED,
                            StocktakingMethod.PERIODIC,
                            false);
            var suites =
                    BusinessRunSuites.plan(
                            Path.of(""),
                            request,
                            "Business checks",
                            Files.createTempDirectory("contract-routing-"));
            var names =
                    suites.stream()
                            .flatMap(s -> s.getTests().stream())
                            .flatMap(t -> t.getXmlClasses().stream())
                            .map(XmlClass::getName)
                            .toList();
            Assert.assertEquals(
                    names.contains("Invoice.ConstructionContractTest"),
                    activity == ActivityType.CONSTRUCTION);
        }
    }

    @Test
    public void activitySpecificPlansDoNotClaimUnimplementedJourneys() throws Exception {
        for (var activity : ActivityType.values()) {
            var request =
                    new BusinessRunRequest(
                            activity,
                            DiscountLevel.TRANSACTION,
                            TaxPolicy.NET_BASED,
                            StocktakingMethod.PERIODIC,
                            activity == ActivityType.RESTAURANT);
            Path output = Files.createTempDirectory("business-scope-");
            var suites = BusinessRunSuites.plan(Path.of(""), request, "Business checks", output);
            var classes =
                    suites.stream()
                            .flatMap(s -> s.getTests().stream())
                            .flatMap(t -> t.getXmlClasses().stream())
                            .map(XmlClass::getName)
                            .toList();
            Assert.assertTrue(classes.contains("SuiteSupport.BusinessProfileVerificationTest"));
            Assert.assertEquals(
                    classes.contains("Invoice.SelectedBranchSalesPolicyTest"),
                    activity == ActivityType.PUBLIC || activity == ActivityType.CONSTRUCTION);
            Assert.assertEquals(
                    classes.contains("Invoice.CarShowroomSalesTest"),
                    activity == ActivityType.CAR_SHOWROOM);
            var scope =
                    new org.json.JSONObject(
                            Files.readString(output.resolve("coverage-scope.json")));
            Assert.assertEquals(
                    classes.contains("CoverageTest.ActivityMasterLifecycleTest"),
                    activity == ActivityType.CONCRETE || activity == ActivityType.CAR_SHOWROOM);
            Assert.assertEquals(
                    classes.contains("Invoice.ConstructionContractConversionTest"),
                    activity == ActivityType.CONSTRUCTION);
            Assert.assertEquals(
                    classes.contains("SpecialSalesTest.RestaurantMinimumAdditionalTaxTest"),
                    activity == ActivityType.RESTAURANT);
            Assert.assertEquals(
                    classes.contains("SpecialSalesTest.KitchenOrdersCoverageTest"),
                    activity == ActivityType.RESTAURANT);
            Assert.assertEquals(
                    classes.contains("CoverageTest.TablesCoverageTest"),
                    activity == ActivityType.RESTAURANT);
            if (activity == ActivityType.RESTAURANT) {
                var tableClass =
                        suites.getFirst().getTests().getFirst().getXmlClasses().stream()
                                .filter(c -> c.getName().equals("CoverageTest.TablesCoverageTest"))
                                .findFirst()
                                .orElseThrow();
                Assert.assertEquals(
                        tableClass.getIncludedMethods().stream()
                                .map(org.testng.xml.XmlInclude::getName)
                                .collect(java.util.stream.Collectors.toSet()),
                        Set.of(
                                "createsAndReadsBackExactOwnedRecord",
                                "editsOwnedRecordWithoutCreatingAnother",
                                "cancelAndConfirmDeleteOwnRecord"));
            }
            Assert.assertEquals(
                    classes.contains("Invoice.UnderReviewCoverageTest"),
                    activity == ActivityType.PUBLIC || activity == ActivityType.CONCRETE);
            Assert.assertFalse(scope.getBoolean("completeActivityCoverage"));
        }
    }
}
