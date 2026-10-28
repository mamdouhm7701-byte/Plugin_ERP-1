/* شرح الملف: اختيار الحالات الملائمة للإعدادات ومنع الاختيارات غير المناسبة، ضمن المبيعات.
 */
package Invoice;

import java.util.List;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

public final class SalesSuitePolicy implements IAlterSuiteListener {
    @Override
    public void alter(List<XmlSuite> suites) {
        for (XmlSuite suite : suites) {
            if (suite.getTests().stream()
                    .flatMap(t -> t.getXmlClasses().stream())
                    .anyMatch(c -> c.getName().startsWith("Invoice.")))
                suite.setConfigFailurePolicy(XmlSuite.FailurePolicy.CONTINUE);
            alter(suite.getChildSuites());
        }
    }
}
