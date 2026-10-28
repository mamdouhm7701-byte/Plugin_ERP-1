/* شرح الملف: اختيار الحالات الملائمة للإعدادات ومنع الاختيارات غير المناسبة، ضمن المخازن.
 */
package StoreTest;

import java.util.List;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

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
