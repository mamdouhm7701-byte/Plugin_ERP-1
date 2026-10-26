package SuiteSupport;

import java.nio.file.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.*;

public final class ParallelSuitePlannerTest {
    private Set<String> caseIds(Map<String, List<String>> methodsByClass) {
        Set<String> result = new HashSet<>();
        methodsByClass.forEach(
                (className, methods) ->
                        methods.forEach(methodName -> result.add(className + "#" + methodName)));
        return result;
    }

    private void deleteTemporaryPlan(Path planDirectory) throws Exception {
        for (String file : List.of("parallel.xml", "exclusive.xml", "plan.json"))
            Files.deleteIfExists(planDirectory.resolve(file));
        Files.delete(planDirectory);
    }

    @DataProvider
    public Object[][] levels() {
        return new Object[][] {{"smoke"}, {"sanity"}, {"e2e"}, {"regression"}};
    }

    @Test(dataProvider = "levels")
    public void partitionPreservesEveryOriginalCaseOnce(String level) throws Exception {
        Path source = Path.of("src/test/resources/" + level + "-testng.xml"),
                planDirectory = Files.createTempDirectory("parallel-plan-");
        try {
            ParallelSuitePlanner.plan(source, planDirectory, 3, Set.of());
            var parallel =
                    caseIds(ParallelSuitePlanner.selected(planDirectory.resolve("parallel.xml")));
            var exclusive =
                    caseIds(ParallelSuitePlanner.selected(planDirectory.resolve("exclusive.xml")));
            Assert.assertTrue(Collections.disjoint(parallel, exclusive));
            var combinedCases = new HashSet<>(parallel);
            combinedCases.addAll(exclusive);
            Assert.assertEquals(combinedCases, caseIds(ParallelSuitePlanner.selected(source)));
        } finally {
            deleteTemporaryPlan(planDirectory);
        }
    }

    @Test
    public void checkpointExcludesOnlyExplicitlyCompletedCases() throws Exception {
        Path source = Path.of("src/test/resources/e2e-testng.xml"),
                planDirectory = Files.createTempDirectory("parallel-plan-");
        try {
            var expected = caseIds(ParallelSuitePlanner.selected(source));
            String completed = expected.iterator().next();
            ParallelSuitePlanner.plan(source, planDirectory, 6, Set.of(completed));
            expected.remove(completed);
            var actual =
                    caseIds(ParallelSuitePlanner.selected(planDirectory.resolve("parallel.xml")));
            actual.addAll(
                    caseIds(ParallelSuitePlanner.selected(planDirectory.resolve("exclusive.xml"))));
            Assert.assertEquals(actual, expected);
        } finally {
            deleteTemporaryPlan(planDirectory);
        }
    }

    @Test
    public void unknownCheckpointCannotSilentlyRemoveCoverage() throws Exception {
        Path planDirectory = Files.createTempDirectory("parallel-plan-");
        try {
            Assert.expectThrows(
                    IllegalArgumentException.class,
                    () ->
                            ParallelSuitePlanner.plan(
                                    Path.of("src/test/resources/e2e-testng.xml"),
                                    planDirectory,
                                    3,
                                    Set.of("Unknown#case")));
        } finally {
            deleteTemporaryPlan(planDirectory);
        }
    }

    @Test
    public void regressionKeepsPriceDefectsAndBranchDefaultExceptionsInTheirCorrectStages()
            throws Exception {
        Path planDirectory = Files.createTempDirectory("regression-plan-");
        try {
            ParallelSuitePlanner.plan(
                    Path.of("src/test/resources/regression-testng.xml"),
                    planDirectory,
                    3,
                    Set.of());
            var parallel =
                    caseIds(ParallelSuitePlanner.selected(planDirectory.resolve("parallel.xml")));
            var exclusive =
                    caseIds(ParallelSuitePlanner.selected(planDirectory.resolve("exclusive.xml")));
            for (String method :
                    List.of(
                            "PR_AllPricesZero",
                            "PR_AllPricesEqual",
                            "PR_SalePriceEqualsMinimum",
                            "PR_SalePriceEqualsMaximum",
                            "PR_NegativeSalePrice",
                            "PR_MinimumAboveSalePrice",
                            "PR_MaximumBelowSalePrice",
                            "PR_MinimumAboveMaximum",
                            "PR_MaximumBelowMinimum",
                            "PR_SalePriceBelowMinimum",
                            "PR_SalePriceAboveMaximum")) {
                Assert.assertTrue(
                        parallel.contains("ProductsTest.AddProductsTest#" + method), method);
            }
            Assert.assertTrue(
                    exclusive.contains(
                            "SpecialSalesTest.PointOfSaleTest#savesCreditAndDeductsStock"));
            Assert.assertTrue(
                    exclusive.contains(
                            "SpecialSalesTest.RestaurantInvoiceTest#payingHoldDoesNotDeductAgain"));
            Assert.assertTrue(
                    exclusive.contains(
                            "Invoice.PostingCoverageTest#postsAndUnpostsOnlyCapturedOwnedInvoice"));
            for (String caseId : parallel) {
                Assert.assertTrue(
                        BranchIsolationListener.independent(
                                caseId.substring(0, caseId.indexOf('#'))),
                        caseId);
            }
            for (String caseId : exclusive) {
                Assert.assertFalse(
                        BranchIsolationListener.independent(
                                caseId.substring(0, caseId.indexOf('#'))),
                        caseId);
            }
        } finally {
            deleteTemporaryPlan(planDirectory);
        }
    }
}
