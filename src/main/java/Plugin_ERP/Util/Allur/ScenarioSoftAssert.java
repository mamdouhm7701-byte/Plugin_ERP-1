/* شرح الملف: تجميع أكثر من اختلاف داخل نفس الحالة قبل عرض النتيجة.
 */
package Plugin_ERP.Util.Allur;

import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;

public final class ScenarioSoftAssert extends SoftAssert {
    @Override
    public void onAssertSuccess(IAssert<?> assertion) {
        ScenarioAssertions.recordComparison(
                assertion.getActual(), assertion.getExpected(), assertion.getMessage(), null);
    }

    @Override
    public void onAssertFailure(IAssert<?> assertion, AssertionError error) {
        ScenarioAssertions.recordComparison(
                assertion.getActual(), assertion.getExpected(), assertion.getMessage(), error);
    }
}
