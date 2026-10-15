package Plugin_ERP.Drivers;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Utiel.PropertyReader;
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
        String browserType = PropertyReader.getProperty("BrowserType");
        if (browserType == null || browserType.isBlank()) {
            throw new IllegalArgumentException("BrowserType is missing or blank");
        }
        WebDriver driver = ThreadGuard.protect(getDriver(browserType.trim()));
        driverThreadLocal.set(driver);
        LogUtils.info("Initialized WebDriver for browser: " + browserType
                + ", execution type: " + PropertyReader.getProperty("ExecutionType"));
        return driverThreadLocal.get();
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
        }
    }
}
