/* شرح الملف: اختيار الحالات الملائمة للإعدادات ومنع الاختيارات غير المناسبة، ضمن الحركات المالية.
 */
package FinancialMovementsTest;

import java.util.List;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

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
