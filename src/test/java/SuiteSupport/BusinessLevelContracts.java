package SuiteSupport;

import Plugin_ERP.BranchProfiles.BusinessRunRequest;
import java.util.*;
import org.testng.annotations.Test;
import org.testng.xml.*;

/**
 * Adds selected business contracts once while preserving each original level's method selection.
 */
final class BusinessLevelContracts {
    private BusinessLevelContracts() {}

    private static final Set<String> SMOKE_CLASSES =
            Set.of(
                    "SuiteSupport.BusinessProfileVerificationTest",
                    "Invoice.SelectedBranchSalesPolicyTest",
                    "SpecialSalesTest.SelectedRestaurantPolicyTest",
                    "Invoice.ConstructionContractTest",
                    "Invoice.CarShowroomSalesTest",
                    "SpecialSalesTest.RestaurantMinimumAdditionalTaxTest",
                    "SpecialSalesTest.RestaurantAdditionalTaxSmokeTest",
                    "SpecialSalesTest.RestaurantItemBasedAdditionalTaxTest");

    private static final Set<String> E2E_CLASSES =
            Set.of(
                    "SuiteSupport.BusinessProfileVerificationTest",
                    "Invoice.SelectedBranchSalesPolicyTest",
                    "SpecialSalesTest.SelectedRestaurantPolicyTest",
                    "Invoice.ConstructionContractConversionTest",
                    "Invoice.CarShowroomSalesTest",
                    "Invoice.ConcreteStockJourneyTest",
                    "Invoice.ConcreteReservationReportTest",
                    "SpecialSalesTest.KitchenOrdersCoverageTest",
                    "SpecialSalesTest.RestaurantMinimumAdditionalTaxTest",
                    "SpecialSalesTest.RestaurantAdditionalTaxSmokeTest",
                    "SpecialSalesTest.RestaurantItemBasedAdditionalTaxTest");

    static List<XmlClass> missing(
            BusinessRunRequest request, String level, List<XmlSuite> originals) {
        var selected = new HashMap<String, Set<String>>();
        for (var suite : originals) collect(suite, selected);
        var additions = new ArrayList<XmlClass>();
        for (var contract : BusinessRunSuites.businessContracts(request)) {
            var desired = new LinkedHashSet<>(methods(contract));
            if (level.equals("Smoke")) {
                if (contract.getName().equals("Invoice.ConcreteStockJourneyTest"))
                    desired.retainAll(
                            Set.of(
                                    "directConcreteInvoiceDoesNotConsumeStock",
                                    "reservationLeavesStockUnchangedAndAppearsInDeliveryPicker"));
                else if (!SMOKE_CLASSES.contains(contract.getName())) continue;
            } else if (level.equals("E2E")) {
                if (!E2E_CLASSES.contains(contract.getName())) continue;
                if (contract.getName().equals("SpecialSalesTest.RestaurantAdditionalTaxSmokeTest"))
                    desired.retainAll(Set.of("subjectItemAdditionalTaxMatchesIndependentOracle"));
            } else if (!Set.of("Sanity", "Regression", "Qualification").contains(level)) {
                throw new IllegalArgumentException("Unsupported business level: " + level);
            }
            desired.removeAll(selected.getOrDefault(contract.getName(), Set.of()));
            if (desired.isEmpty()) continue;
            var addition = new XmlClass(contract.getName());
            addition.setIncludedMethods(
                    new ArrayList<>(desired.stream().sorted().map(XmlInclude::new).toList()));
            additions.add(addition);
            selected.computeIfAbsent(contract.getName(), key -> new HashSet<>()).addAll(desired);
        }
        return additions;
    }

    private static void collect(XmlSuite suite, Map<String, Set<String>> selected) {
        for (var test : suite.getTests())
            for (var type : test.getXmlClasses())
                selected.computeIfAbsent(type.getName(), key -> new HashSet<>())
                        .addAll(methods(type));
        for (var child : suite.getChildSuites()) collect(child, selected);
    }

    static Set<String> methods(XmlClass type) {
        try {
            var result = new TreeSet<String>();
            for (var method : Class.forName(type.getName()).getMethods()) {
                var annotation = method.getAnnotation(Test.class);
                if (annotation == null || !annotation.enabled()) continue;
                String name = method.getName();
                boolean included =
                        type.getIncludedMethods().isEmpty()
                                || type.getIncludedMethods().stream()
                                        .anyMatch(i -> name.matches(i.getName()));
                boolean excluded = type.getExcludedMethods().stream().anyMatch(name::matches);
                if (included && !excluded) result.add(name);
            }
            return result;
        } catch (ClassNotFoundException failure) {
            throw new IllegalStateException(
                    "Unknown business contract class: " + type.getName(), failure);
        }
    }
}
