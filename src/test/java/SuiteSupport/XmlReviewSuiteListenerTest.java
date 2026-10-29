/* شرح الملف: يتحقق دون ERP أن الحالات تظهر في TestNG، وكل سياسة تجهز قبل حالاتها وتنظف قبل التالية، وأن تعذر التنظيف يمنع تشغيل الفرع التالي. */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import java.nio.file.*;
import java.util.*;
import org.testng.*;
import org.testng.annotations.Test;
import org.testng.xml.*;

public final class XmlReviewSuiteListenerTest {
    static final List<String> events = new ArrayList<>();

    public static final class VisibleCase {
        @Test
        public void visibleInvoiceCase(ITestContext context) {
            if (context.getSuite().getXmlSuite().getParentSuite() == null) return;
            events.add("test:" + context.getSuite().getXmlSuite().getParentSuite().getName());
        }
    }

    private static final class Probe extends XmlReviewSuiteListener {
        final Path project;
        final boolean failCleanup;

        Probe(Path project, boolean failCleanup) {
            this.project = project;
            this.failCleanup = failCleanup;
        }

        @Override
        void acquire(Path root) {}

        @Override
        Path output(Path root) {
            return project.resolve("output");
        }

        @Override
        BusinessRunDialog.Selection selection() {
            return new BusinessRunDialog.Selection(
                    new BusinessRunRequest(
                            ActivityType.RESTAURANT,
                            DiscountLevel.TRANSACTION,
                            TaxPolicy.NET_BASED,
                            StocktakingMethod.PERIODIC,
                            false),
                    "XML:" + XmlSuiteCatalog.INVOICE_REVIEW,
                    "Local",
                    new BusinessRunTarget(
                            "Cloud",
                            "Probe",
                            "https://probe.example",
                            "user",
                            "secret".toCharArray()),
                    1);
        }

        @Override
        List<XmlSuite> plan(Path root, BusinessRunDialog.Selection choice, Path output)
                throws Exception {
            return super.plan(project, choice, output);
        }

        @Override
        BusinessRunListener preparer(BusinessRunDialog.Selection choice, XmlSuite suite) {
            return new BusinessRunListener() {
                @Override
                public void alter(List<XmlSuite> suites) {
                    events.add(
                            "prepare:"
                                    + (suite.getParentSuite() == null
                                                    ? suite
                                                    : suite.getParentSuite())
                                            .getName());
                }

                @Override
                public void onExecutionFinish() {
                    events.add("cleanup:" + choice.business().tax().code());
                    if (failCleanup) throw new IllegalStateException("cleanup probe");
                }
            };
        }
    }

    private static Path project() throws Exception {
        Path root = Files.createTempDirectory("native-xml-review");
        Path resources = Files.createDirectories(root.resolve("src/test/resources"));
        Files.writeString(
                resources.resolve(XmlSuiteCatalog.INVOICE_REVIEW),
                """
            <!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
            <suite name="Visible invoice"><test name="Invoice case"><classes><class name="SuiteSupport.XmlReviewSuiteListenerTest$VisibleCase"/></classes></test></suite>
            """);
        return root;
    }

    @Test
    public void everyPolicyIsVisibleAndCleanupPrecedesNextPreparation() throws Exception {
        events.clear();
        var engine = BusinessStabilityChild.engine();
        engine.setUseDefaultListeners(false);
        engine.addListener(new Probe(project(), false));
        engine.run();
        Assert.assertEquals(engine.getStatus(), 0);
        Assert.assertEquals(events.size(), 9);
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(events.get(i * 3).startsWith("prepare:"));
            Assert.assertTrue(events.get(i * 3 + 1).startsWith("test:"));
            Assert.assertTrue(events.get(i * 3 + 2).startsWith("cleanup:"));
        }
    }

    @Test
    public void cleanupFailurePreventsAnotherPolicyFromCreatingData() throws Exception {
        events.clear();
        var engine = BusinessStabilityChild.engine();
        engine.setUseDefaultListeners(false);
        engine.addListener(new Probe(project(), true));
        try {
            engine.run();
        } catch (IllegalStateException expected) {
            Assert.assertTrue(expected.getMessage().contains("blocked"));
        }
        Assert.assertEquals(events.stream().filter(e -> e.startsWith("prepare:")).count(), 1L);
        Assert.assertEquals(events.stream().filter(e -> e.startsWith("test:")).count(), 1L);
        Assert.assertEquals(events.stream().filter(e -> e.startsWith("cleanup:")).count(), 1L);
    }
}
