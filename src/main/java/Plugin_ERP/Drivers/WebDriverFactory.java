/* شرح الملف: فتح المتصفح المختار وحماية كل تجربة من التداخل مع متصفح آخر.
 */
package Plugin_ERP.Drivers;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Util.PropertyReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ThreadGuard;

public class WebDriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private WebDriverFactory() {}

    private static WebDriver getDriver(String driverType) {
        Browser browser = Browser.valueOf(driverType.toUpperCase());
        AbstractDriver abstractDriver = browser.getDriverFactory();
        return abstractDriver.createDriver();
    }

    public static WebDriver initDriver() {
        if (driverThreadLocal.get() != null) {
            throw new IllegalStateException(
                    "A browser is already active on this thread; close its owning class first");
        }
        String browserType = PropertyReader.getProperty("BrowserType");
        if (browserType == null || browserType.isBlank()) {
            throw new IllegalArgumentException("BrowserType is missing or blank");
        }
        WebDriver rawDriver = getDriver(browserType.trim());
        WebDriver driver;
        WebDriver session = null;
        try {
            // Preparation and cleanup create browsers outside BaseTest as well.
            rawDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(60));
            session = Plugin_ERP.Util.BrowserSessionHealth.decorate(rawDriver);
            driver = ThreadGuard.protect(Plugin_ERP.Util.FailureVideoRecorder.decorate(session));
            Plugin_ERP.Util.BrowserSessionHealth.alias(session, driver);
        } catch (RuntimeException failure) {
            Plugin_ERP.Util.BrowserSessionHealth.release(session);
            rawDriver.quit();
            throw failure;
        }
        driverThreadLocal.set(driver);
        LogUtils.info(
                "Initialized WebDriver for browser: "
                        + browserType
                        + ", execution type: "
                        + PropertyReader.getProperty("ExecutionType"));
        return driverThreadLocal.get();
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        quitDriver(driverThreadLocal.get());
    }

    /** Close the caller's browser without removing another owner's thread-local session. */
    public static void quitDriver(WebDriver ownedDriver) {
        try {
            if (ownedDriver != null) ownedDriver.quit();
        } finally {
            Plugin_ERP.Util.BrowserSessionHealth.release(ownedDriver);
            if (driverThreadLocal.get() == ownedDriver) {
                Plugin_ERP.Util.FailureVideoRecorder.release();
                driverThreadLocal.remove();
            }
        }
    }
}
