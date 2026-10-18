package ProductsTest;

import java.util.List;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

/** Retains cleanup failures while allowing independent product cases to execute in IDE and Maven. */
public final class ProductsSuitePolicy implements IAlterSuiteListener {
  @Override public void alter(List<XmlSuite> suites) {
    for (XmlSuite suite : suites) {
      if (suite.getTests().stream().flatMap(test -> test.getXmlClasses().stream())
          .anyMatch(testClass -> testClass.getName().startsWith("ProductsTest."))) {
        suite.setConfigFailurePolicy(XmlSuite.FailurePolicy.CONTINUE);
      }
      alter(suite.getChildSuites());
    }
  }
}
