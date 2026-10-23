package SuiteSupport;

import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Tests.LoginTest.LoginTest;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.xml.internal.Parser;
import org.testng.xml.XmlSuite;

/** Reject accidental inclusion of the existing live browser tests in the automatic CI gate. */
public final class CiSuiteContractTest {
    @Test
    public void ciDoesNotSelectLiveErpSessions() throws Exception {
        for (XmlSuite suite : new Parser("src/test/resources/ci-testng.xml").parseToList()) {
            Assert.assertTrue(suite.getSuiteFiles().isEmpty(), "CI must not include live module suites");
            Assert.assertEquals(suite.getParallel(), XmlSuite.ParallelMode.NONE);
            for (var test : suite.getTests()) {
                for (var entry : test.getXmlClasses()) {
                    Class<?> type = Class.forName(entry.getName());
                    Assert.assertFalse(BaseTest.class.isAssignableFrom(type),
                            "Live ERP class selected by CI: " + type.getName());
                    Assert.assertFalse(LoginTest.class.isAssignableFrom(type),
                            "Live authentication must remain an explicit separate run");
                }
            }
        }
    }

    @Test
    public void ciRetainsLocalCleanupAndRuntimeChecks() throws Exception {
        Set<String> classes = new HashSet<>();
        for (XmlSuite suite : new Parser("src/test/resources/ci-testng.xml").parseToList())
            for (var test : suite.getTests())
                for (var entry : test.getXmlClasses()) classes.add(entry.getName());
        for (XmlSuite suite : new Parser(Path.of("src/test/resources/additional/standalone/fixture-lifecycle-testng.xml")
                .toString()).parseToList())
            for (var test : suite.getTests())
                for (var entry : test.getXmlClasses())
                    Assert.assertTrue(classes.contains(entry.getName()), "Missing cleanup contract: " + entry.getName());
        Assert.assertTrue(classes.containsAll(Set.of("Purches.PurchaseCleanupTest",
                "SuiteSupport.RuntimeCompatibilityTest", "SuiteSupport.BrowserLifecycleTest",
                "Plugin_ERP.Utiel.ConfigurationPropertiesTest",
                "Plugin_ERP.Utiel.Allur.AllureReportPolicyTest")));
        Assert.assertTrue(classes.containsAll(Set.of("Plugin_ERP.Utiel.Allur.ScenarioAccessTest",
                "Plugin_ERP.Utiel.InteractionAccuracyTest")), "CI must retain access metadata and assertion accuracy checks");
    }
}
