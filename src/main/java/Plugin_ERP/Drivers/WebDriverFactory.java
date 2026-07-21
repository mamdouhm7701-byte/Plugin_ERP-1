package Plugin_ERP.Drivers;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Utiel.PropertyReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ThreadGuard;

public class WebDriverFactory {

    private static String browserType;

    private static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private WebDriverFactory() {}

    private static WebDriver getDriver(String driverType) {
        Browser browser = Browser.valueOf(driverType.toUpperCase());
        AbstractDriver abstractDriver = browser.getDriverFactory();
        return abstractDriver.createDriver();
    }

    public static WebDriver initDriver() {
        if (browserType == null) {
            browserType = PropertyReader.getProperty("BrowserType");
        }
        WebDriver driver = ThreadGuard.protect(getDriver(browserType));
        driverThreadLocal.set(driver);
        LogUtils.info("Initialized WebDriver for browser: " + browserType);
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