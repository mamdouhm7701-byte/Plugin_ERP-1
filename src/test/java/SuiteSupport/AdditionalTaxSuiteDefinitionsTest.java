/* شرح الملف: مراجعة بقاء حالات الإضافية والحد الأدنى والصنف المعفى ضمن حزم المطاعم المحددة.
 */
package SuiteSupport;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.xml.internal.Parser;

public final class AdditionalTaxSuiteDefinitionsTest {
    private static final Path RESOURCES = Path.of("src", "test", "resources");

    @Test
    public void restaurantAdditionalSmokeRetainsSubjectMixedAndDiscountContracts()
            throws Exception {
        var suites =
                new Parser(
                                RESOURCES
                                        .resolve(
                                                "additional/standalone/smoke-restaurant-additional-testng.xml")
                                        .toString())
                        .parseToList();
        var classes = new HashSet<String>();
        for (var suite : suites)
            for (var test : suite.getTests())
                for (var entry : test.getXmlClasses())
                    Assert.assertTrue(
                            classes.add(entry.getName()), "Duplicate additional-tax smoke class");
        Assert.assertEquals(
                classes,
                Set.of(
                        "SpecialSalesTest.RestaurantAdditionalTaxSmokeTest",
                        "SpecialSalesTest.RestaurantMinimumAdditionalTaxTest"));
        var cases = new HashSet<String>();
        for (var method :
                Class.forName("SpecialSalesTest.RestaurantAdditionalTaxSmokeTest")
                        .getDeclaredMethods())
            if (method.isAnnotationPresent(Test.class)) cases.add(method.getName());
        Assert.assertEquals(
                cases,
                Set.of(
                        "subjectItemAdditionalTaxMatchesIndependentOracle",
                        "mixedItemsExcludeNonSubjectItemFromAdditionalTax",
                        "discountPrecedesAdditionalTax"));
    }

    @Test
    public void itemBasedAdditionalSmokeIncludesExemptMixedContract() throws Exception {
        var suites =
                new Parser(
                                RESOURCES
                                        .resolve(
                                                "additional/standalone/smoke-restaurant-item-tax-testng.xml")
                                        .toString())
                        .parseToList();
        var classes = new HashSet<String>();
        for (var suite : suites)
            for (var test : suite.getTests())
                for (var entry : test.getXmlClasses())
                    Assert.assertTrue(
                            classes.add(entry.getName()), "Duplicate ItemBased smoke class");
        Assert.assertEquals(
                classes,
                Set.of(
                        "SpecialSalesTest.RestaurantItemBasedAdditionalTaxTest",
                        "SpecialSalesTest.RestaurantMinimumAdditionalTaxTest"));
        var cases = new HashSet<String>();
        for (var method :
                Class.forName("SpecialSalesTest.RestaurantItemBasedAdditionalTaxTest")
                        .getDeclaredMethods())
            if (method.isAnnotationPresent(Test.class)) cases.add(method.getName());
        Assert.assertEquals(cases, Set.of("exemptItemsRemainVatExemptWithAdditionalTax"));
    }
}
