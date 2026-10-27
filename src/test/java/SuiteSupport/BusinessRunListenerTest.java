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
    public void concreteBusinessPlanIncludesQualifiedStockAndBillingJourney() throws Exception {
        var request = new BusinessRunRequest(ActivityType.CONCRETE, DiscountLevel.TRANSACTION,
                TaxPolicy.NET_BASED, StocktakingMethod.PERIODIC, false);
        var suites = BusinessRunSuites.plan(Path.of(""), request, "Business checks", Files.createTempDirectory("concrete-business-plan-"));
        var names = suites.stream().flatMap(s -> s.getTests().stream()).flatMap(t -> t.getXmlClasses().stream())
                .map(XmlClass::getName).toList();
        Assert.assertTrue(names.contains("Invoice.ConcreteStockJourneyTest"));
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
                true);
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
        String key = BusinessBranchPreparation.creationKey(request,"https://erp.example", output.resolve("run-a"));
        Assert.assertEquals(BusinessBranchPreparation.creationKey(request,"https://erp.example",output.resolve("temporary/../run-a")),key);
        Assert.assertNotEquals(BusinessBranchPreparation.creationKey(request,"https://other.example",output.resolve("run-a")),key);
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
                    activity != ActivityType.CONCRETE && activity != ActivityType.RESTAURANT);
            var scope =
                    new org.json.JSONObject(
                            Files.readString(output.resolve("coverage-scope.json")));
            Assert.assertEquals(
                    classes.contains("CoverageTest.ActivityMasterLifecycleTest"),
                    activity == ActivityType.CONCRETE || activity == ActivityType.CAR_SHOWROOM);
            Assert.assertFalse(scope.getBoolean("completeActivityCoverage"));
        }
    }
}
