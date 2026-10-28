/* شرح الملف: اختبار سلامة تجهيز الخطوات المشتركة للحالات ثم تنظيف بياناتها، ضمن تجهيز بيانات
 * التجربة.
 */
package TestSupport;

import Plugin_ERP.Tests.LoginTest.BaseTest;
import java.util.ArrayList;
import org.testng.annotations.*;

public abstract class BasicDataScenarioTest extends BaseTest {
    private BasicDataFixtures basicData;
    private String fixtureWindow;

    protected final BasicDataFixtures basicData() {
        if (basicData == null) basicData = new BasicDataFixtures(driver);
        return basicData;
    }

    protected final String ownedName(String endpoint, String label) {
        String name = basicData().reserveName(endpoint, label);
        basicData().installCapture();
        return name;
    }

    protected final long recordCount(String endpoint) {
        Object result = basicData().query(endpoint);
        if (result instanceof java.util.Map<?, ?> dto
                && dto.get("totalRecords") instanceof Number total) return total.longValue();
        if (result instanceof java.util.List<?> rows) return rows.size();
        throw new AssertionError("Cannot verify persisted record count for " + endpoint);
    }

    @BeforeMethod(alwaysRun = true)
    public void beginBasicDataScenario() {
        requirePreparedSession();
        fixtureWindow = driver.getWindowHandle();
        basicData().beginScenario();
        basicData().installCapture();
    }

    @AfterMethod(alwaysRun = true)
    public void collectBasicDataScenario() {
        if (driver == null) return;
        try {
            // Saves belong to the application window, even when printing changed the active tab.
            if (fixtureWindow != null && driver.getWindowHandles().contains(fixtureWindow))
                driver.switchTo().window(fixtureWindow);
            if (basicData != null) {
                basicData.awaitUiSettled();
                basicData.collectUi();
            }
        } finally {
            if (fixtureWindow != null && driver.getWindowHandles().contains(fixtureWindow)) {
                for (String handle : new ArrayList<>(driver.getWindowHandles()))
                    if (!handle.equals(fixtureWindow)) {
                        driver.switchTo().window(handle);
                        driver.close();
                    }
                driver.switchTo().window(fixtureWindow);
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanBasicDataFixtures() {
        AssertionError failure = null;
        try {
            collectBasicDataScenario();
        } catch (RuntimeException | AssertionError e) {
            failure = new AssertionError("Collecting basic data saves failed", e);
        }
        try {
            if (basicData != null) basicData.close();
        } catch (RuntimeException | AssertionError e) {
            if (failure == null) failure = new AssertionError("Basic data cleanup failed", e);
            else failure.addSuppressed(e);
        }
        if (failure != null) throw failure;
    }
}
