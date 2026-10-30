/* شرح الملف: يتأكد دون فتح البرنامج أن اختيار XML يحافظ على الفلاتر والإعدادات ويمنع الملفات الداخلية والخروج من مسار المشروع. */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.BranchPolicies.TaxPolicy;
import java.nio.file.*;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class XmlSuiteCatalogTest {
    @Test
    public void parallelInvoiceReviewKeepsPoliciesAndExclusiveCashierStagesSeparated()
            throws Exception {
        String previous = System.getProperty("xml.review.parallel");
        try {
            System.setProperty("xml.review.parallel", "true");
            var suites =
                    XmlSuiteCatalog.plan(
                            Path.of(""),
                            XmlSuiteCatalog.INVOICE_REVIEW,
                            6,
                            Files.createTempDirectory("parallel-invoice-review-"));
            Assert.assertEquals(suites.size(), 3);
            Assert.assertEquals(suites.get(1).getThreadCount(), 2);
            var names =
                    suites.get(1).getTests().getFirst().getXmlClasses().stream()
                            .map(org.testng.xml.XmlClass::getName)
                            .collect(java.util.stream.Collectors.toSet());
            Assert.assertEquals(
                    names,
                    java.util.Set.of(
                            "Invoice.SalesInvoiceTaxReviewTest",
                            "Purches.PurchaseInvoiceTaxReviewTest"));
            Assert.assertEquals(
                    suites.get(2).getParallel(), org.testng.xml.XmlSuite.ParallelMode.NONE);
            XmlReviewSuiteListener.limitWorkers(suites.get(1), 1);
            Assert.assertEquals(
                    suites.get(1).getParallel(), org.testng.xml.XmlSuite.ParallelMode.NONE);
        } finally {
            if (previous == null) System.clearProperty("xml.review.parallel");
            else System.setProperty("xml.review.parallel", previous);
        }
    }

    @Test
    public void catalogContainsInvoiceReviewAndExplainsSalesRestrictions() {
        var entries = XmlSuiteCatalog.load(Path.of(""));
        var review =
                entries.stream()
                        .filter(e -> e.path().equals(XmlSuiteCatalog.INVOICE_REVIEW))
                        .findFirst()
                        .orElseThrow();
        Assert.assertTrue(review.description().contains("منع التعديل والحذف"));
        Assert.assertTrue(review.description().contains("السياسات الثلاث"));
        Assert.assertFalse(
                entries.stream()
                        .anyMatch(
                                e ->
                                        e.path().contains("interactive")
                                                || e.path().equals("ci-testng.xml")
                                                || e.path().contains("/internal/")
                                                || e.path().contains("/legacy/")));
    }

    @Test
    public void chosenXmlPreservesMethodsParametersAndListeners() throws Exception {
        Path root = Files.createTempDirectory("xml-selection");
        Path resources = Files.createDirectories(root.resolve("src/test/resources"));
        Files.writeString(
                resources.resolve("sample-testng.xml"),
                """
            <suite name="Selected" parallel="classes"><parameter name="recipe" value="tax"/>
            <listeners><listener class-name="SuiteSupport.BranchIsolationListener"/></listeners>
            <test name="Only selected"><classes><class name="Invoice.SalesInvoiceTaxReviewTest"><methods><include name="reviewSalesInvoice"/></methods></class></classes></test></suite>
            """);
        var suites = XmlSuiteCatalog.plan(root, "sample-testng.xml", 4, root.resolve("output"));
        Assert.assertEquals(suites.getFirst().getParameter("recipe"), "tax");
        Assert.assertEquals(
                suites.getFirst().getListeners(), List.of("SuiteSupport.BranchIsolationListener"));
        Assert.assertEquals(
                suites.getFirst()
                        .getTests()
                        .getFirst()
                        .getXmlClasses()
                        .getFirst()
                        .getIncludedMethods()
                        .getFirst()
                        .getName(),
                "reviewSalesInvoice");
        Assert.assertEquals(suites.getFirst().getThreadCount(), 1);
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> XmlSuiteCatalog.plan(root, "../ci-testng.xml", 1, root.resolve("bad")));
    }

    @Test
    public void matrixRunsEveryPolicyOnceAndOrdinaryXmlUsesSelectedPolicyOnly() {
        Assert.assertEquals(
                XmlReviewPolicies.policies(true, TaxPolicy.ITEM_BASED),
                List.of(TaxPolicy.NET_BASED, TaxPolicy.ITEM_BASED, TaxPolicy.PRICE_INCLUDED));
        Assert.assertEquals(
                XmlReviewPolicies.policies(false, TaxPolicy.ITEM_BASED),
                List.of(TaxPolicy.ITEM_BASED));
    }

    @Test
    public void invoiceSuiteHasFourIndependentWorkflowClasses() throws Exception {
        var suites =
                XmlSuiteCatalog.plan(
                        Path.of(""),
                        XmlSuiteCatalog.INVOICE_REVIEW,
                        1,
                        Files.createTempDirectory("invoice-plan"));
        var types = suites.getFirst().getTests().getFirst().getXmlClasses();
        Assert.assertEquals(
                types.stream().map(org.testng.xml.XmlClass::getName).toList(),
                List.of(
                        "SuiteSupport.BusinessProfileVerificationTest",
                        "Invoice.SalesInvoiceTaxReviewTest",
                        "SpecialSalesTest.PosInvoiceTaxReviewTest",
                        "SpecialSalesTest.RestaurantInvoiceTaxReviewTest",
                        "Purches.PurchaseInvoiceTaxReviewTest"));
        for (var type : types) Assert.assertFalse(BusinessLevelContracts.methods(type).isEmpty());
    }

    @Test
    public void blockedStartupExplainsActiveRunInsteadOfSuggestingMissingTests() {
        var message =
                XmlReviewSuiteListener.startupMessage(
                        new IllegalStateException(
                                "Another ERP run is active. Wait for it to finish."));
        Assert.assertTrue(message.contains("تشغيل ERP آخر"));
        Assert.assertTrue(message.contains("انتظر"));
        Assert.assertTrue(
                XmlReviewSuiteListener.startupMessage(new IllegalArgumentException("Missing XML"))
                        .contains("Missing XML"));
    }
}
