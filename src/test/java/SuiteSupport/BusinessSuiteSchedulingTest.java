/* شرح الملف: اختبارات توزيع الحالات التي يمكن تشغيلها معًا وإبقاء الحالات المشتركة منفردة.
 */
package SuiteSupport;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.xml.*;

public final class BusinessSuiteSchedulingTest {
    @Test
    public void businessPlanKeepsEveryCaseWhenBrowserLimitChanges() throws Exception {
        for (var activity :
                List.of(
                        Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType.RESTAURANT,
                        Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType.CONCRETE)) {
            var request =
                    new Plugin_ERP.BranchProfiles.BusinessRunRequest(
                            activity,
                            Plugin_ERP.BranchProfiles.BranchPolicies.DiscountLevel.TRANSACTION,
                            Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy.NET_BASED,
                            Plugin_ERP.BranchProfiles.BranchPolicies.StocktakingMethod.PERIODIC,
                            activity
                                    == Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType
                                            .RESTAURANT);
            for (String level : List.of("Business checks", "Smoke")) {
                var serial =
                        BusinessRunSuites.plan(
                                java.nio.file.Path.of(""),
                                request,
                                level,
                                1,
                                java.nio.file.Files.createTempDirectory("business-serial-plan-"));
                var parallel =
                        BusinessRunSuites.plan(
                                java.nio.file.Path.of(""),
                                request,
                                level,
                                3,
                                java.nio.file.Files.createTempDirectory("business-parallel-plan-"));
                Assert.assertEquals(
                        cases(parallel), cases(serial), "Cases changed with browser count");
                for (var suite : parallel) {
                    Assert.assertTrue(suite.getThreadCount() <= 3);
                    if (suite.getParallel() == XmlSuite.ParallelMode.CLASSES)
                        for (var test : suite.getTests())
                            for (var type : test.getXmlClasses())
                                Assert.assertTrue(
                                        BranchIsolationListener.independent(type.getName()),
                                        "Unreviewed class became parallel");
                }
            }
        }
    }

    private static List<String> cases(List<XmlSuite> suites) {
        return suites.stream()
                .flatMap(s -> s.getTests().stream())
                .flatMap(t -> t.getXmlClasses().stream())
                .flatMap(
                        c ->
                                BusinessLevelContracts.methods(c).stream()
                                        .map(m -> c.getName() + "#" + m))
                .sorted()
                .toList();
    }

    private List<XmlSuite> source() {
        var suite = new XmlSuite();
        suite.setName("Business scheduling");
        var test = new XmlTest(suite);
        var tables = new XmlClass("CoverageTest.TablesCoverageTest");
        tables.setIncludedMethods(
                new ArrayList<>(List.of(new XmlInclude("createsAndReadsBackExactOwnedRecord"))));
        test.setXmlClasses(
                new ArrayList<>(
                        List.of(
                                new XmlClass("SuiteSupport.BusinessProfileVerificationTest"),
                                tables,
                                new XmlClass("Invoice.UnderReviewCoverageTest"),
                                new XmlClass(
                                        "SpecialSalesTest.RestaurantMinimumAdditionalTaxTest"))));
        return List.of(suite);
    }

    @Test
    public void parallelStageContainsOnlyReviewedClassesAndRetainsMethodSelection() {
        var result = BusinessSuiteScheduling.partition(source(), 3);
        Assert.assertEquals(result.size(), 3);
        Assert.assertEquals(
                result.getFirst().getTests().getFirst().getXmlClasses().getFirst().getName(),
                "SuiteSupport.BusinessProfileVerificationTest");
        var parallel = result.get(1);
        Assert.assertEquals(parallel.getParallel(), XmlSuite.ParallelMode.CLASSES);
        Assert.assertEquals(parallel.getThreadCount(), 3);
        Assert.assertEquals(parallel.getDataProviderThreadCount(), 1);
        Assert.assertEquals(parallel.getTests().getFirst().getXmlClasses().size(), 2);
        Assert.assertEquals(
                parallel.getTests()
                        .getFirst()
                        .getXmlClasses()
                        .getFirst()
                        .getIncludedMethods()
                        .getFirst()
                        .getName(),
                "createsAndReadsBackExactOwnedRecord");
        var exclusive = result.getLast();
        Assert.assertEquals(exclusive.getThreadCount(), 1);
        Assert.assertEquals(exclusive.getParallel(), XmlSuite.ParallelMode.NONE);
        Assert.assertEquals(
                exclusive.getTests().getFirst().getXmlClasses().getFirst().getName(),
                "SpecialSalesTest.RestaurantMinimumAdditionalTaxTest");
        Assert.assertEquals(
                result.stream()
                        .flatMap(s -> s.getTests().stream())
                        .flatMap(t -> t.getXmlClasses().stream())
                        .count(),
                4L);
    }

    @Test
    public void oneWorkerKeepsOriginalOrderAndUnsupportedCountsStop() {
        var source = source();
        Assert.assertSame(BusinessSuiteScheduling.partition(source, 1), source);
        Assert.expectThrows(
                IllegalArgumentException.class, () -> BusinessSuiteScheduling.partition(source, 0));
        Assert.expectThrows(
                IllegalArgumentException.class, () -> BusinessSuiteScheduling.partition(source, 7));
    }
}
