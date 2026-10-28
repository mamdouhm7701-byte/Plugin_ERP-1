package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import java.nio.file.*;
import java.util.*;
import org.testng.*;
import org.testng.annotations.Test;
import org.testng.xml.*;

public final class BusinessRunListenerTest {
    @Test
    public void selectedCloudOrTestTargetAppliesBeforePreparationAndRestoresAllOverrides()
            throws Exception {
        String shared = Files.readString(Path.of("src/main/resources/WebData.properties"));
        for (String environment : List.of("Cloud", "Test")) {
            var previous = new HashMap<String, String>();
            for (String key :
                    List.of(
                            "Environment",
                            "Tenant",
                            "ExecutionType",
                            "TestURL",
                            "Test.Username",
                            "Test.Password",
                            "RunTenant",
                            "RunTenant.Username",
                            "RunTenant.Password")) previous.put(key, System.getProperty(key));
            var listener =
                    new BusinessRunListener() {
                        @Override
                        BusinessRunDialog.Selection selectBusiness() {
                            return new BusinessRunDialog.Selection(
                                    selection().business(),
                                    "Smoke",
                                    "LocalHeadless",
                                    new BusinessRunTarget(
                                            environment,
                                            "RunTenant",
                                            "https://chosen.example",
                                            "chosen-user",
                                            "chosen-secret".toCharArray()),
                                    3);
                        }

                        @Override
                        void acquire(Path project) {}

                        @Override
                        BranchProfile prepare(
                                BusinessRunRequest request, Path project, Path output) {
                            Assert.assertEquals(
                                    Plugin_ERP.Util.PropertyReader.getProperty("Environment"),
                                    environment);
                            Assert.assertEquals(
                                    Plugin_ERP.Util.PropertyReader.getProperty("Tenant"),
                                    "RunTenant");
                            Assert.assertEquals(
                                    Plugin_ERP.Util.PropertyReader.getApplicationUrl(),
                                    "https://chosen.example");
                            Assert.assertEquals(
                                    Plugin_ERP.Util.PropertyReader.getValidUsername(),
                                    "chosen-user");
                            Assert.assertEquals(
                                    Plugin_ERP.Util.PropertyReader.getValidPassword(),
                                    "chosen-secret");
                            throw new IllegalStateException("expected target verification stop");
                        }
                    };
            var failure =
                    Assert.expectThrows(
                            IllegalStateException.class, () -> listener.alter(new ArrayList<>()));
            Assert.assertTrue(failure.getMessage().contains("expected target verification stop"));
            previous.forEach(
                    (key, value) ->
                            Assert.assertEquals(
                                    System.getProperty(key), value, "Leaked run override: " + key));
        }
        Assert.assertEquals(
                Files.readString(Path.of("src/main/resources/WebData.properties")), shared);
    }

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
    public void preparationAndCleanupFailuresAreBothPreserved() throws Exception {
        var profile =
                BranchProfiles.load("demo-concrete-reference", Path.of("config/branch-profiles"))
                        .orElseThrow();
        var listener =
                new BusinessRunListener() {
                    @Override
                    BusinessRunDialog.Selection selectBusiness() {
                        return selection();
                    }

                    @Override
                    void acquire(Path project) {}

                    @Override
                    BranchProfile prepare(BusinessRunRequest request, Path project, Path output) {
                        confirmedBranchCreated(profile);
                        throw new IllegalStateException("original readback failure");
                    }

                    @Override
                    void cleanupOwnedBranch(BranchProfile owned, Path output) {
                        throw new IllegalStateException("cleanup transport failure");
                    }
                };
        var failure =
                Assert.expectThrows(
                        IllegalStateException.class, () -> listener.alter(new ArrayList<>()));
        Assert.assertTrue(failure.getMessage().contains("original readback failure"));
        Assert.assertEquals(failure.getCause().getSuppressed().length, 1);
        Assert.assertTrue(failure.getCause().getSuppressed()[0].getMessage().contains("cleanup"));
    }

    @Test
    public void confirmedBranchIsCleanedWhenLaterPreparationFails() throws Exception {
        var expected =
                BranchProfiles.load("demo-concrete-reference", Path.of("config/branch-profiles"))
                        .orElseThrow();
        var events = new ArrayList<String>();
        var listener =
                new BusinessRunListener() {
                    @Override
                    BusinessRunDialog.Selection selectBusiness() {
                        return selection();
                    }

                    @Override
                    void acquire(Path project) {}

                    @Override
                    BranchProfile prepare(BusinessRunRequest request, Path project, Path output) {
                        confirmedBranchCreated(expected);
                        throw new IllegalStateException(
                                "Session verification failed after branch creation");
                    }

                    @Override
                    void cleanupOwnedBranch(BranchProfile profile, Path output) {
                        Assert.assertEquals(profile, expected);
                        Assert.assertNotNull(output);
                        events.add("cleaned");
                    }
                };
        Assert.expectThrows(IllegalStateException.class, () -> listener.alter(new ArrayList<>()));
        Assert.assertEquals(events, List.of("cleaned"));
        listener.onExecutionFinish();
        Assert.assertEquals(events, List.of("cleaned"), "Cleanup must not execute twice");
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
    public void identicalBusinessChoicesCreateDifferentBranchesForDifferentRuns() throws Exception {
        var request = selection().business();
        Path output = Files.createTempDirectory("business-run-identity-");
        String first =
                BusinessBranchPreparation.creationKey(
                        request, "https://erp.example", output.resolve("run-a"));
        String second =
                BusinessBranchPreparation.creationKey(
                        request, "https://erp.example", output.resolve("run-b"));
        Assert.assertNotEquals(
                first, second, "A new run must not reuse the previous policy branch");
        Assert.assertEquals(
                BusinessBranchPreparation.creationKey(
                        request, "https://erp.example", output.resolve("run-a")),
                first,
                "An uncertain retry of the same run must keep its creation identity");
        Assert.assertTrue(first.matches("[a-f0-9]{16}"));
    }

    private BusinessRunDialog.Selection selection() {
        return new BusinessRunDialog.Selection(
                new BusinessRunRequest(
                        ActivityType.RESTAURANT,
                        DiscountLevel.TRANSACTION,
                        TaxPolicy.PRICE_INCLUDED,
                        StocktakingMethod.PERIODIC,
                        true),
                "Business checks",
                "LocalHeadless",
                new BusinessRunTarget(
                        "Cloud",
                        "Demo",
                        "https://example.test",
                        "contract-user",
                        "contract-secret".toCharArray()),
                1);
    }

    @Test
    public void pendingBranchIntentSurvivesBuildOutputCleanup() throws Exception {
        Path project = Files.createTempDirectory("durable-branch-intent-");
        String key = "0123456789abcdef";
        Path legacy = project.resolve("target/business-branch-registry/" + key + ".json");
        Files.createDirectories(legacy.getParent());
        String pending = "{\"status\":\"CREATE_PENDING\"}";
        Files.writeString(legacy, pending);
        Path durable = BusinessBranchPreparation.registryPath(project, key);
        Assert.assertEquals(
                durable, project.resolve("config/business-branch-state/" + key + ".json"));
        Assert.assertEquals(Files.readString(durable), pending);
        Files.delete(legacy);
        Assert.assertEquals(
                Files.readString(BusinessBranchPreparation.registryPath(project, key)), pending);
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> BusinessBranchPreparation.registryPath(project, "../outside"));
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
    public void runIdentityUsesCanonicalOutputAndIsolatesOrigins() throws Exception {
        Path output = Files.createTempDirectory("business-canonical-run-");
        var request = selection().business();
        String key =
                BusinessBranchPreparation.creationKey(
                        request, "https://erp.example", output.resolve("run-a"));
        Assert.assertEquals(
                BusinessBranchPreparation.creationKey(
                        request, "https://erp.example", output.resolve("temporary/../run-a")),
                key);
        Assert.assertNotEquals(
                BusinessBranchPreparation.creationKey(
                        request, "https://other.example", output.resolve("run-a")),
                key);
    }

    @Test
    public void nativeCasesStartOnlyAfterVerifiedPreparationAndPropertiesAreRestored()
            throws Exception {
        var original = new HashMap<String, String>();
        for (String key :
                List.of(
                        "Environment",
                        "Tenant",
                        "ExecutionType",
                        "branch.profile",
                        "branch.profile.fingerprint",
                        "branch.profile.select",
                        "business.additionalTax")) original.put(key, System.getProperty(key));
        var events = new ArrayList<String>();
        var listener =
                new BusinessRunListener() {
                    @Override
                    BusinessRunDialog.Selection selectBusiness() {
                        return selection();
                    }

                    @Override
                    void acquire(Path project) {}

                    @Override
                    BranchProfile prepare(BusinessRunRequest request, Path project, Path output)
                            throws Exception {
                        Assert.assertEquals(System.getProperty("Tenant"), "Demo");
                        events.add("prepared");
                        return BranchProfiles.load(
                                        "demo-restaurant-inclusive",
                                        project.resolve("config/branch-profiles"))
                                .orElseThrow();
                    }

                    @Override
                    List<XmlSuite> plan(
                            Path project, BusinessRunDialog.Selection choice, Path output) {
                        Assert.assertEquals(events, List.of("prepared"));
                        Assert.assertEquals(System.getProperty("branch.profile.select"), "true");
                        var suite = new XmlSuite();
                        suite.setName("Business local native events");
                        new XmlTest(suite)
                                .setXmlClasses(List.of(new XmlClass(NativeRunnerEventProbe.class)));
                        return List.of(suite);
                    }
                };
        var runner = new TestNG();
        runner.setUseDefaultListeners(false);
        runner.setXmlSuites(new ArrayList<>(List.of(new XmlSuite())));
        runner.addListener(listener);
        runner.addListener(
                new ITestListener() {
                    @Override
                    public void onTestStart(ITestResult result) {
                        Assert.assertEquals(events.get(0), "prepared");
                        events.add(result.getName());
                    }
                });
        try {
            runner.run();
            Assert.assertFalse(runner.hasFailure());
            Assert.assertEquals(
                    new HashSet<>(events), Set.of("prepared", "firstCase", "secondCase"));
        } finally {
            listener.onExecutionFinish();
        }
        original.forEach((key, value) -> Assert.assertEquals(System.getProperty(key), value, key));
    }

    @Test
    public void failedPreparationCannotStartAnySuiteOrLeakEnvironment() {
        String environment = System.getProperty("Environment"),
                tenant = System.getProperty("Tenant");
        var listener =
                new BusinessRunListener() {
                    @Override
                    BusinessRunDialog.Selection selectBusiness() {
                        return selection();
                    }

                    @Override
                    void acquire(Path project) {}

                    @Override
                    BranchProfile prepare(BusinessRunRequest request, Path project, Path output) {
                        throw new IllegalStateException("Save response unknown");
                    }

                    @Override
                    List<XmlSuite> plan(
                            Path project, BusinessRunDialog.Selection choice, Path output) {
                        throw new AssertionError("Planning must never happen before verification");
                    }
                };
        Assert.expectThrows(IllegalStateException.class, () -> listener.alter(new ArrayList<>()));
        Assert.assertEquals(System.getProperty("Environment"), environment);
        Assert.assertEquals(System.getProperty("Tenant"), tenant);
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
