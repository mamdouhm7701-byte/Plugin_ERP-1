package SuiteSupport;

import java.util.*;
import org.testng.xml.*;

/** Uses the existing reviewed isolation policy; methods and data-provider rows stay serial. */
final class BusinessSuiteScheduling {
    private BusinessSuiteScheduling() {}

    static List<XmlSuite> partition(List<XmlSuite> suites, int workers) {
        if (workers < 1 || workers > 6) throw new IllegalArgumentException("Choose 1–6 browsers");
        if (workers == 1) return suites;
        var result = new ArrayList<XmlSuite>();
        for (var suite : suites) {
            var verification = new ArrayList<XmlClass>();
            var independent = new ArrayList<XmlClass>();
            var exclusive = new ArrayList<XmlClass>();
            for (var test : suite.getTests())
                for (var type : test.getXmlClasses()) {
                    if (type.getName().equals("SuiteSupport.BusinessProfileVerificationTest"))
                        verification.add(type);
                    else if (BranchIsolationListener.independent(type.getName()))
                        independent.add(type);
                    else exclusive.add(type);
                }
            add(result, suite.getName() + " / verify", verification, 1);
            add(result, suite.getName() + " / owned parallel", independent, workers);
            add(result, suite.getName() + " / exclusive", exclusive, 1);
        }
        return result;
    }

    private static void add(
            List<XmlSuite> result, String name, List<XmlClass> classes, int workers) {
        if (classes.isEmpty()) return;
        var suite = new XmlSuite();
        suite.setName(name);
        suite.setParallel(workers > 1 ? XmlSuite.ParallelMode.CLASSES : XmlSuite.ParallelMode.NONE);
        suite.setThreadCount(workers);
        suite.setDataProviderThreadCount(1);
        suite.setConfigFailurePolicy(XmlSuite.FailurePolicy.CONTINUE);
        suite.setListeners(List.of(BranchIsolationListener.class.getName()));
        var test = new XmlTest(suite);
        test.setName(name);
        test.setPreserveOrder(true);
        test.setParallel(suite.getParallel());
        test.setXmlClasses(classes);
        result.add(suite);
    }
}
