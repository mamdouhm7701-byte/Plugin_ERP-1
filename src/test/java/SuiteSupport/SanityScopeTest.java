/* شرح الملف: يثبت أن Sanity المركز يشغل القسم المختار مع التحقق من الفرع، دون تعديل الملف الأصلي. */
package SuiteSupport;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.xml.*;

public class SanityScopeTest {
    @Test
    public void selectedModulePreservesItsMethodsAndProfileCheck() {
        var suite = new XmlSuite();
        var test = new XmlTest(suite);
        var sales = new XmlClass("Invoice.SalesInvoiceCoverageTest");
        sales.setIncludedMethods(List.of(new XmlInclude("savesOwnedItemFromPicker")));
        test.setXmlClasses(
                new ArrayList<>(
                        List.of(
                                sales,
                                new XmlClass("Purches.PurchaseInvoiceCoverageTest"),
                                new XmlClass("SuiteSupport.BusinessProfileVerificationTest"))));
        SanityScope.filter(List.of(suite), "Sales");
        Assert.assertEquals(test.getXmlClasses().size(), 2);
        Assert.assertSame(test.getXmlClasses().getFirst(), sales);
        Assert.assertEquals(
                sales.getIncludedMethods().getFirst().getName(), "savesOwnedItemFromPicker");
    }

    @Test
    public void invalidScopeFailsAndAllPreservesSelection() {
        var suite = new XmlSuite();
        var test = new XmlTest(suite);
        test.setXmlClasses(
                new ArrayList<>(List.of(new XmlClass("SpecialSalesTest.RestaurantInvoiceTest"))));
        SanityScope.filter(List.of(suite), "All");
        Assert.assertEquals(test.getXmlClasses().size(), 1);
        Assert.expectThrows(
                IllegalArgumentException.class, () -> SanityScope.filter(List.of(suite), "Saless"));
    }
}
