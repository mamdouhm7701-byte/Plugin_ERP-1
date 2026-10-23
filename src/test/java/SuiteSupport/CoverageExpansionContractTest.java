package SuiteSupport;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.xml.internal.Parser;

/** Keeps the requested screen expansion explicit and prevents scope drift. */
public final class CoverageExpansionContractTest {
    @Test public void expansionSelectsEveryRequestedScreenAndOnlyThoseScreens() throws Exception {
        Set<String> actual = new HashSet<>();
        for (var suite : new Parser("src/test/resources/coverage-expansion-testng.xml").parseToList())
            for (var test : suite.getTests()) for (var entry : test.getXmlClasses())
                Assert.assertTrue(actual.add(entry.getName()), "Duplicate screen class: " + entry.getName());
        Assert.assertEquals(actual, Set.of(
                "CoverageTest.TablesCoverageTest", "CoverageTest.AccountGroupsCoverageTest",
                "CoverageTest.CostCentersCoverageTest", "CoverageTest.ChartOfAccountsCoverageTest",
                "StoreTest.StocktakingCoverageTest",
                "StoreTest.ShortageAdjustmentCoverageTest", "StoreTest.OverflowAdjustmentCoverageTest",
                "Invoice.UnderReviewCoverageTest", "SpecialSalesTest.KitchenOrdersCoverageTest",
                "Invoice.PostingCoverageTest", "Invoice.ActionsCoverageTest", "CoverageTest.UsersCoverageTest",
                "CoverageTest.PermissionsCoverageTest", "CoverageTest.AuditLogCoverageTest",
                "CoverageTest.ZatcaConfigurationCoverageTest"));
        String regression = java.nio.file.Files.readString(java.nio.file.Path.of("src/test/resources/regression-testng.xml"));
        Assert.assertTrue(regression.contains("coverage-expansion-testng.xml"));
        boolean existingPriceLists=false;
        for(var suite:new Parser("src/test/resources/products-testng.xml").parseToList())
            for(var test:suite.getTests())for(var entry:test.getXmlClasses())
                if(entry.getName().equals("ProductsTest.ProductListTest"))existingPriceLists=true;
        Assert.assertTrue(existingPriceLists,"Existing price-list coverage must remain in regression");
    }
}
