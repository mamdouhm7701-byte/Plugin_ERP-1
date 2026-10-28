package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import java.nio.file.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.xml.*;
import org.testng.xml.internal.Parser;

public final class BusinessLevelContractsTest {
    @DataProvider
    public Object[][] restaurantLevels() {
        var rows = new ArrayList<Object[]>();
        for (String level : List.of("Smoke", "Sanity", "E2E", "Regression", "Qualification"))
            for (var tax : TaxPolicy.values())
                for (var discount : DiscountLevel.values())
                    for (boolean additional : List.of(false, true))
                        rows.add(new Object[] {level, tax, discount, additional});
        return rows.toArray(Object[][]::new);
    }

    @Test(dataProvider = "restaurantLevels")
    public void eachLevelSelectsApplicableTaxContractsAndE2eOmitsDraftChecks(
            String level, TaxPolicy tax, DiscountLevel discount, boolean additional) {
        var request =
                new BusinessRunRequest(
                        ActivityType.RESTAURANT,
                        discount,
                        tax,
                        StocktakingMethod.PERIODIC,
                        additional);
        var cases = cases(BusinessLevelContracts.missing(request, level, List.of()));
        Assert.assertEquals(
                cases.contains(
                        "SpecialSalesTest.RestaurantMinimumAdditionalTaxTest#savesMinimumAdditionalTaxPerUnitBeforeQuantity"),
                additional);
        Assert.assertEquals(
                cases.contains(
                        "SpecialSalesTest.RestaurantItemBasedAdditionalTaxTest#additionalSubjectPaysVatWhileOtherExemptItemRemainsExempt"),
                additional && tax == TaxPolicy.ITEM_BASED);
        Assert.assertEquals(
                cases.contains(
                        "SpecialSalesTest.RestaurantAdditionalTaxSmokeTest#subjectItemAdditionalTaxMatchesIndependentOracle"),
                additional && tax != TaxPolicy.ITEM_BASED);
        Assert.assertEquals(
                cases.contains(
                        "SpecialSalesTest.RestaurantAdditionalTaxSmokeTest#mixedItemsExcludeNonSubjectItemFromAdditionalTax"),
                additional && tax != TaxPolicy.ITEM_BASED && !level.equals("E2E"));
        Assert.assertEquals(
                cases.contains(
                        "SpecialSalesTest.RestaurantAdditionalTaxSmokeTest#discountPrecedesAdditionalTax"),
                additional
                        && tax != TaxPolicy.ITEM_BASED
                        && discount == DiscountLevel.TRANSACTION
                        && !level.equals("E2E"));
    }

    @Test
    public void originalXmlSelectionsArePreservedAndBusinessAdditionsAreNotDuplicated()
            throws Exception {
        for (String level : List.of("Smoke", "Sanity", "E2E", "Regression")) {
            var originals =
                    new Parser(
                                    "src/test/resources/"
                                            + level.toLowerCase(Locale.ROOT)
                                            + "-testng.xml")
                            .parseToList();
            var before = selected(originals);
            var request =
                    new BusinessRunRequest(
                            ActivityType.RESTAURANT,
                            DiscountLevel.TRANSACTION,
                            TaxPolicy.NET_BASED,
                            StocktakingMethod.PERIODIC,
                            true);
            var additions = BusinessLevelContracts.missing(request, level, originals);
            Assert.assertEquals(selected(originals), before, "Original level changed: " + level);
            Assert.assertTrue(
                    Collections.disjoint(before, cases(additions)),
                    "Repeated business contract: " + level);
            var suite = new XmlSuite();
            new XmlTest(suite).setXmlClasses(additions);
            var combined = new ArrayList<>(originals);
            combined.add(suite);
            Assert.assertTrue(BusinessLevelContracts.missing(request, level, combined).isEmpty());
        }
    }

    @Test
    public void directXmlPolicyLeavesRunsWithoutAnExplicitBusinessProfileUnchanged()
            throws Exception {
        var suites =
                new ArrayList<>(new Parser("src/test/resources/smoke-testng.xml").parseToList());
        var before = selected(suites);
        new BusinessLevelSuitePolicy() {
            @Override
            Optional<BusinessRunRequest> selectedBusiness() {
                return Optional.empty();
            }
        }.alter(suites);
        Assert.assertEquals(selected(suites), before);
        Assert.assertEquals(suites.size(), 1);
    }

    @Test
    public void directXmlPolicyAddsSelectedContractsOnce() throws Exception {
        for (String level : List.of("smoke", "sanity", "e2e", "regression")) {
            var suites =
                    new ArrayList<>(
                            new Parser("src/test/resources/" + level + "-testng.xml")
                                    .parseToList());
            var policy =
                    new BusinessLevelSuitePolicy() {
                        @Override
                        Optional<BusinessRunRequest> selectedBusiness() {
                            return Optional.of(
                                    new BusinessRunRequest(
                                            ActivityType.RESTAURANT,
                                            DiscountLevel.TRANSACTION,
                                            TaxPolicy.NET_BASED,
                                            StocktakingMethod.PERIODIC,
                                            true));
                        }
                    };
            policy.alter(suites);
            var once = selected(suites);
            int count = suites.size();
            policy.alter(suites);
            Assert.assertEquals(suites.size(), count);
            Assert.assertEquals(selected(suites), once);
            Assert.assertTrue(
                    once.contains(
                            "SpecialSalesTest.RestaurantMinimumAdditionalTaxTest#savesMinimumAdditionalTaxPerUnitBeforeQuantity"));
        }
    }

    @Test
    public void concreteRenderedValuesAreSelectedOnlyWithQualifiedTaxPolicy() {
        for (var tax : TaxPolicy.values()) {
            var request =
                    new BusinessRunRequest(
                            ActivityType.CONCRETE,
                            DiscountLevel.TRANSACTION,
                            tax,
                            StocktakingMethod.PERIODIC,
                            false);
            var cases = cases(BusinessLevelContracts.missing(request, "Regression", List.of()));
            Assert.assertEquals(
                    cases.stream()
                            .anyMatch(c -> c.startsWith("Invoice.ConcreteReservationReportTest#")),
                    tax == TaxPolicy.NET_BASED);
            Assert.assertTrue(cases.stream().noneMatch(c -> c.contains("AdditionalTax")));
        }
    }

    private static Set<String> selected(List<XmlSuite> suites) {
        var result = new TreeSet<String>();
        for (var suite : suites) {
            for (var test : suite.getTests()) result.addAll(cases(test.getXmlClasses()));
            result.addAll(selected(suite.getChildSuites()));
        }
        return result;
    }

    private static Set<String> cases(List<XmlClass> classes) {
        var result = new TreeSet<String>();
        for (var type : classes)
            for (String method : BusinessLevelContracts.methods(type))
                Assert.assertTrue(
                        result.add(type.getName() + "#" + method), "Repeated added contract");
        return result;
    }
}
