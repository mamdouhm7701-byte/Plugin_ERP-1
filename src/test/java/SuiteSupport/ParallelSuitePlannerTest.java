package SuiteSupport;

import java.nio.file.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.*;

public final class ParallelSuitePlannerTest {
    private Set<String> cases(Map<String,List<String>> classes) {
        Set<String> result=new HashSet<>();classes.forEach((type,methods)->methods.forEach(m->result.add(type+"#"+m)));return result;
    }
    private void removePlan(Path dir) throws Exception {
        for(String file:List.of("parallel.xml","exclusive.xml","plan.json"))Files.deleteIfExists(dir.resolve(file));
        Files.delete(dir);
    }
    @DataProvider public Object[][] levels(){return new Object[][]{{"smoke"},{"sanity"},{"e2e"},{"regression"}};}
    @Test(dataProvider="levels") public void partitionPreservesEveryOriginalCaseOnce(String level) throws Exception {
        Path source=Path.of("src/test/resources/"+level+"-testng.xml"),dir=Files.createTempDirectory("parallel-plan-");
        try {
            ParallelSuitePlanner.plan(source,dir,3,Set.of());
            var parallel=cases(ParallelSuitePlanner.selected(dir.resolve("parallel.xml")));
            var exclusive=cases(ParallelSuitePlanner.selected(dir.resolve("exclusive.xml")));
            Assert.assertTrue(Collections.disjoint(parallel,exclusive));
            var union=new HashSet<>(parallel);union.addAll(exclusive);
            Assert.assertEquals(union,cases(ParallelSuitePlanner.selected(source)));
        } finally {removePlan(dir);}
    }
    @Test public void checkpointExcludesOnlyExplicitlyCompletedCases() throws Exception {
        Path source=Path.of("src/test/resources/e2e-testng.xml"),dir=Files.createTempDirectory("parallel-plan-");
        try {
            var expected=cases(ParallelSuitePlanner.selected(source));String completed=expected.iterator().next();
            ParallelSuitePlanner.plan(source,dir,6,Set.of(completed));expected.remove(completed);
            var actual=cases(ParallelSuitePlanner.selected(dir.resolve("parallel.xml")));
            actual.addAll(cases(ParallelSuitePlanner.selected(dir.resolve("exclusive.xml"))));
            Assert.assertEquals(actual,expected);
        } finally {removePlan(dir);}
    }
    @Test public void unknownCheckpointCannotSilentlyRemoveCoverage() throws Exception {
        Path dir=Files.createTempDirectory("parallel-plan-");
        try {
            Assert.expectThrows(IllegalArgumentException.class,()->ParallelSuitePlanner.plan(
                    Path.of("src/test/resources/e2e-testng.xml"),dir,3,Set.of("Unknown#case")));
        } finally {removePlan(dir);}
    }

    @Test public void regressionKeepsPriceDefectsAndBranchDefaultExceptionsInTheirCorrectStages() throws Exception {
        Path dir=Files.createTempDirectory("regression-plan-");
        try {
            ParallelSuitePlanner.plan(Path.of("src/test/resources/regression-testng.xml"),dir,3,Set.of());
            var parallel=cases(ParallelSuitePlanner.selected(dir.resolve("parallel.xml")));
            var exclusive=cases(ParallelSuitePlanner.selected(dir.resolve("exclusive.xml")));
            for(String method:List.of("PR_AllPricesZero","PR_AllPricesEqual","PR_SalePriceEqualsMinimum",
                    "PR_SalePriceEqualsMaximum","PR_NegativeSalePrice","PR_MinimumAboveSalePrice",
                    "PR_MaximumBelowSalePrice","PR_MinimumAboveMaximum","PR_MaximumBelowMinimum",
                    "PR_SalePriceBelowMinimum","PR_SalePriceAboveMaximum"))
                Assert.assertTrue(parallel.contains("ProductsTest.AddProductsTest#"+method),method);
            Assert.assertTrue(exclusive.contains("SpecialSalesTest.PointOfSaleTest#savesCreditAndDeductsStock"));
            Assert.assertTrue(exclusive.contains("SpecialSalesTest.RestaurantInvoiceTest#payingHoldDoesNotDeductAgain"));
            Assert.assertTrue(exclusive.contains("Invoice.PostingCoverageTest#postsAndUnpostsOnlyCapturedOwnedInvoice"));
            for(String key:parallel)Assert.assertTrue(BranchIsolationListener.independent(key.substring(0,key.indexOf('#'))),key);
            for(String key:exclusive)Assert.assertFalse(BranchIsolationListener.independent(key.substring(0,key.indexOf('#'))),key);
        } finally {removePlan(dir);}
    }
}
