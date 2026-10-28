/* شرح الملف: اختيار الحالات المناسبة للنشاط والإعدادات المطلوبة.
 */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import java.nio.file.*;
import java.util.*;
import org.testng.xml.*;

final class BusinessRunSuites {
    private BusinessRunSuites() {}

    static List<XmlSuite> plan(Path project, BusinessRunRequest request, String level, Path output)
            throws Exception {
        return plan(project, request, level, 1, output);
    }

    static List<XmlSuite> plan(
            Path project, BusinessRunRequest request, String level, int workers, Path output)
            throws Exception {
        if (workers < 1 || workers > 6) throw new IllegalArgumentException("Choose 1–6 browsers");
        List<XmlSuite> result;
        if (!level.equals("Business checks")) {
            result =
                    new ArrayList<>(
                            NativeInteractiveSuiteListener.buildSuites(
                                    project, level, workers, output));
            var additions = BusinessLevelContracts.missing(request, level, result);
            if (!additions.isEmpty()) {
                var business = new XmlSuite();
                business.setName("Selected " + request.activity().code() + " business / " + level);
                business.setParallel(XmlSuite.ParallelMode.NONE);
                var contracts = new XmlTest(business);
                contracts.setName("Selected activity, policies and additional tax");
                contracts.setXmlClasses(additions);
                result.addAll(BusinessSuiteScheduling.partition(List.of(business), workers));
            }
        } else {
            var suite = new XmlSuite();
            suite.setName("Selected business contracts");
            suite.setParallel(XmlSuite.ParallelMode.NONE);
            var test = new XmlTest(suite);
            test.setName("Selected activity and policies");
            var classes = businessContracts(request);
            test.setXmlClasses(classes);
            result = new ArrayList<>(BusinessSuiteScheduling.partition(List.of(suite), workers));
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
                                                "Selected workflows include owned chassis sales, concrete stock, restaurant tables/kitchen and contract billing; accepted evidence distinguishes passing cases from confirmed product failures",
                                                "Periodic/continuous accounting entries are not inferred from stock quantity checks",
                                                "Mixed-item movement discount allocation remains outside the confirmed contracts; final-only rounding checks retain observed product failures",
                                                "Contract remaining quantities currently fail the confirmed business rule; those checks remain red",
                                                "Concrete under-review save passes; native edit displays empty UI despite loaded data and remains a failing contract",
                                                "Rendered reservation values and cars/drivers/customer-project delivery quantities and counts are verified for NetBased tax; other policies retain report-request checks"),
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

    static List<XmlClass> businessContracts(BusinessRunRequest request) {
        var classes = new ArrayList<XmlClass>();
        classes.add(new XmlClass("SuiteSupport.BusinessProfileVerificationTest"));
        switch (request.activity()) {
            case RESTAURANT -> {
                classes.add(new XmlClass("SpecialSalesTest.RestaurantScreenAccessTest"));
                classes.add(new XmlClass("SpecialSalesTest.SelectedRestaurantPolicyTest"));
                var tables = new XmlClass("CoverageTest.TablesCoverageTest");
                tables.setIncludedMethods(
                        new ArrayList<>(
                                List.of(
                                        new XmlInclude("createsAndReadsBackExactOwnedRecord"),
                                        new XmlInclude("editsOwnedRecordWithoutCreatingAnother"),
                                        new XmlInclude("cancelAndConfirmDeleteOwnRecord"))));
                classes.add(tables);
                classes.add(new XmlClass("SpecialSalesTest.KitchenOrdersCoverageTest"));
                if (request.additionalTax()) classes.addAll(additionalTaxContracts(request));
            }
            case PUBLIC -> {
                classes.add(new XmlClass("Invoice.StocktakingAccountingTest"));
                classes.add(new XmlClass("Invoice.SalesFinalRoundingTest"));
                classes.add(new XmlClass("Invoice.SelectedBranchSalesPolicyTest"));
                classes.add(underReviewContracts());
            }
            case CONSTRUCTION -> {
                classes.add(new XmlClass("Invoice.SalesFinalRoundingTest"));
                classes.add(new XmlClass("CoverageTest.ActivitySpecificScreenAccessTest"));
                classes.add(new XmlClass("Invoice.SelectedBranchSalesPolicyTest"));
                classes.add(new XmlClass("Invoice.ConstructionContractTest"));
                classes.add(new XmlClass("Invoice.ConstructionContractConversionTest"));
            }
            case CAR_SHOWROOM -> {
                classes.add(new XmlClass("CoverageTest.ActivitySpecificScreenAccessTest"));
                classes.add(new XmlClass("CoverageTest.ActivityMasterLifecycleTest"));
                classes.add(new XmlClass("Invoice.CarShowroomSalesTest"));
            }
            case CONCRETE -> {
                classes.add(new XmlClass("CoverageTest.ActivitySpecificScreenAccessTest"));
                classes.add(new XmlClass("CoverageTest.ActivityMasterLifecycleTest"));
                classes.add(new XmlClass("Invoice.ConcreteStockJourneyTest"));
                classes.add(new XmlClass("CoverageTest.ConcreteReportRequestTest"));
                if (request.tax() == TaxPolicy.NET_BASED) {
                    classes.add(new XmlClass("Invoice.ConcreteReservationReportTest"));
                    classes.add(new XmlClass("Invoice.ConcreteOperationalReportTest"));
                }
                classes.add(underReviewContracts());
            }
        }
        return classes;
    }

    static List<XmlClass> additionalTaxContracts(BusinessRunRequest request) {
        var classes = new ArrayList<XmlClass>();

        classes.add(new XmlClass("SpecialSalesTest.RestaurantMinimumAdditionalTaxTest"));
        classes.add(new XmlClass("SpecialSalesTest.RestaurantAdditionalTaxBoundaryTest"));
        classes.add(new XmlClass("SpecialSalesTest.RestaurantAdditionalTaxOrderTest"));
        classes.add(new XmlClass("SpecialSalesTest.RestaurantAdditionalTaxReturnTest"));
        if (request.tax() == TaxPolicy.ITEM_BASED)
            classes.add(new XmlClass("SpecialSalesTest.RestaurantItemBasedAdditionalTaxTest"));
        else {
            var additional = new XmlClass("SpecialSalesTest.RestaurantAdditionalTaxSmokeTest");
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

        return classes;
    }

    private static XmlClass underReviewContracts() {
        var underReview = new XmlClass("Invoice.UnderReviewCoverageTest");
        underReview.setIncludedMethods(
                new ArrayList<>(
                        List.of(
                                new XmlInclude("savesUnderReviewWithoutPostingInventory"),
                                new XmlInclude("reopensSavedDocumentByCapturedId"))));
        return underReview;
    }
}
