/* شرح الملف: اختيار الحالات الملائمة للإعدادات ومنع الاختيارات غير المناسبة، ضمن العملاء والموردين
 * والمناديب.
 */
package DealerModelTest;

import java.util.List;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

public final class DealerSuitePolicy implements IAlterSuiteListener {
    @Override
    public void alter(List<XmlSuite> suites) {
        for (XmlSuite suite : suites) {
            if (suite.getTests().stream()
                    .flatMap(test -> test.getXmlClasses().stream())
                    .anyMatch(testClass -> testClass.getName().startsWith("DealerModelTest."))) {
                suite.setConfigFailurePolicy(XmlSuite.FailurePolicy.CONTINUE);
            }
            alter(suite.getChildSuites());
        }
    }
}
