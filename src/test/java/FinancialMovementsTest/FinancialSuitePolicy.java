package FinancialMovementsTest;

import java.util.List;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

/**
 * Retains cleanup failures while allowing independent financial cases to execute in IDE and Maven.
 */
public final class FinancialSuitePolicy implements IAlterSuiteListener {
    @Override
    public void alter(List<XmlSuite> suites) {
        for (XmlSuite suite : suites) {
            if (suite.getTests().stream()
                    .flatMap(test -> test.getXmlClasses().stream())
                    .anyMatch(
                            testClass ->
                                    testClass.getName().startsWith("FinancialMovementsTest."))) {
                suite.setConfigFailurePolicy(XmlSuite.FailurePolicy.CONTINUE);
            }
            alter(suite.getChildSuites());
        }
    }
}
