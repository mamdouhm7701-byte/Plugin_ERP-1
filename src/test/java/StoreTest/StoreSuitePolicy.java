package StoreTest;

import java.util.List;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

/**
 * Retains cleanup failures while allowing independent warehouse cases to execute in IDE and Maven.
 */
public final class StoreSuitePolicy implements IAlterSuiteListener {
    @Override
    public void alter(List<XmlSuite> suites) {
        for (XmlSuite suite : suites) {
            if (suite.getTests().stream()
                    .flatMap(test -> test.getXmlClasses().stream())
                    .anyMatch(testClass -> testClass.getName().startsWith("StoreTest."))) {
                suite.setConfigFailurePolicy(XmlSuite.FailurePolicy.CONTINUE);
            }
            alter(suite.getChildSuites());
        }
    }
}
