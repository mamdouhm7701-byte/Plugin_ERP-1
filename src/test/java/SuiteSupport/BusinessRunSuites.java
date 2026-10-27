package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import java.nio.file.*;
import java.util.*;
import org.testng.xml.*;

/** Explicit business checks; original level definitions remain untouched. */
final class BusinessRunSuites {
    private BusinessRunSuites() {}

    static List<XmlSuite> plan(Path project, BusinessRunRequest request, String level, Path output)
            throws Exception {
        List<XmlSuite> result;
        if (!level.equals("Business checks"))
            result =
                    new ArrayList<>(
                            NativeInteractiveSuiteListener.buildSuites(project, level, 1, output));
        else {
            var suite = new XmlSuite();
            suite.setName("Selected business contracts");
            suite.setParallel(XmlSuite.ParallelMode.NONE);
            var test = new XmlTest(suite);
            test.setName("Selected activity and policies");
            var classes = new ArrayList<XmlClass>();
            classes.add(new XmlClass("SuiteSupport.BusinessProfileVerificationTest"));
            switch (request.activity()) {
                case RESTAURANT -> {
                    classes.add(new XmlClass("SpecialSalesTest.RestaurantScreenAccessTest"));
                    classes.add(new XmlClass("SpecialSalesTest.SelectedRestaurantPolicyTest"));
                    if (request.additionalTax()) {
                        if (request.tax() == TaxPolicy.ITEM_BASED)
                            classes.add(
                                    new XmlClass(
                                            "SpecialSalesTest.RestaurantItemBasedAdditionalTaxTest"));
                        else {
                            var additional =
                                    new XmlClass(
                                            "SpecialSalesTest.RestaurantAdditionalTaxSmokeTest");
                            additional.setIncludedMethods(
                                    new ArrayList<>(
                                            List.of(
                                                    new XmlInclude(
                                                            "subjectItemAdditionalTaxMatchesIndependentOracle"),
                                                    new XmlInclude(
                                                            "mixedItemsExcludeNonSubjectItemFromAdditionalTax"))));
                            if (request.discount() == DiscountLevel.TRANSACTION)
                                additional
                                        .getIncludedMethods()
                                        .add(new XmlInclude("discountPrecedesAdditionalTax"));
                            classes.add(additional);
                        }
                    }
                }
                case PUBLIC -> classes.add(new XmlClass("Invoice.SelectedBranchSalesPolicyTest"));
                case CONSTRUCTION -> {
                    classes.add(new XmlClass("CoverageTest.ActivitySpecificScreenAccessTest"));
                    classes.add(new XmlClass("Invoice.SelectedBranchSalesPolicyTest"));
                    classes.add(new XmlClass("Invoice.ConstructionContractTest"));
                }
                case CAR_SHOWROOM -> {
                    classes.add(new XmlClass("CoverageTest.ActivitySpecificScreenAccessTest"));
                    classes.add(new XmlClass("CoverageTest.ActivityMasterLifecycleTest"));
                    classes.add(new XmlClass("Invoice.SelectedBranchSalesPolicyTest"));
                }
                case CONCRETE -> {
                    classes.add(new XmlClass("CoverageTest.ActivitySpecificScreenAccessTest"));
                    classes.add(new XmlClass("CoverageTest.ActivityMasterLifecycleTest"));
                    classes.add(new XmlClass("Invoice.ConcreteStockJourneyTest"));
                }
            }
            test.setXmlClasses(classes);
            result = new ArrayList<>(List.of(suite));
        }
        Files.createDirectories(output);
        Files.writeString(
                output.resolve("coverage-scope.json"),
                new org.json.JSONObject(
                                Map.of(
                                        "business",
                                        request.fields(),
                                        "level",
                                        level,
                                        "completeActivityCoverage",
                                        false,
                                        "limits",
                                        List.of(
                                                "Contract creation and concrete reservation/delivery/billing stock journeys are covered; contract settlement and car-sale journeys are not yet qualified",
                                                "Periodic/continuous accounting entries are not inferred from stock quantity checks",
                                                "Mixed-item movement discount allocation and unconfirmed rounding remain outside the confirmed contracts"),
                                        "classes",
                                        result.stream()
                                                .flatMap(s -> s.getTests().stream())
                                                .flatMap(t -> t.getXmlClasses().stream())
                                                .map(XmlClass::getName)
                                                .distinct()
                                                .toList()))
                        .toString(2),
                StandardOpenOption.CREATE_NEW);
        return result;
    }
}
