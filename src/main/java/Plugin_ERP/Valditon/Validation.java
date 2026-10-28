/* شرح الملف: يجمع اختلافات المقارنات أثناء الحالة بدل إيقافها عند أول اختلاف، ثم يعرضها معًا عند
 * المراجعة النهائية ويفرغها للحالة التالية.
 */
package Plugin_ERP.Valditon;

import Plugin_ERP.Bots.LogUtils;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

// soft to spell "Validation"
public class Validation extends BaseAssertion {
    private static final ThreadLocal<SoftAssert> assertions = new ThreadLocal<>();

    private static SoftAssert currentAssertions() {
        SoftAssert current = assertions.get();
        if (current == null) {
            current = new Plugin_ERP.Util.Allur.ScenarioSoftAssert();
            assertions.set(current);
        }
        return current;
    }

    public Validation(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void assertrue(boolean condition, String message) {
        currentAssertions().assertTrue(condition, message);
    }

    @Override
    protected void asserfalse(boolean condition, String message) {
        currentAssertions().assertFalse(condition, message);
    }

    @Override
    protected void assertequals(Object actual, Object expected, String message) {
        currentAssertions().assertEquals(actual, expected, message);
    }

    public static void AsserAll() {
        SoftAssert current = assertions.get();
        if (current == null) return;
        try {
            current.assertAll();
        } catch (AssertionError e) {
            LogUtils.error("Assertion failed: " + e.getMessage());
            throw e;
        } finally {
            assertions.remove();
        }
    }
}
