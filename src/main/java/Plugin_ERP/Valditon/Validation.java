package Plugin_ERP.Valditon;

import Plugin_ERP.Bots.LogUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

// soft to spell "Validation"
public class Validation extends BaseAssertion {
    private static SoftAssert softAssert = new SoftAssert();
    private static boolean used = false;

    public Validation(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void assertrue(boolean condition, String message) {
        used = true;
        softAssert.assertTrue(condition, message);
    }

    @Override
    protected void asserfalse(boolean condition, String message) {
        used = true;
        softAssert.assertFalse(condition, message);
    }

    @Override
    protected void assertequals(Object actual, Object expected, String message) {
        used = true;
        softAssert.assertEquals(actual, expected, message);
    }

    public static void AsserAll() {
        if (!used) return;
        try {
            softAssert.assertAll();
        } catch (AssertionError e) {
            LogUtils.error("Assertion failed: " + e.getMessage());
            throw e;
        } finally {
            softAssert = new SoftAssert();
        }
    }
}
