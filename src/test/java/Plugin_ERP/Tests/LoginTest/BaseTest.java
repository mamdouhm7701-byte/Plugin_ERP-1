package Plugin_ERP.Tests.LoginTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

@Listeners({AllureTestNg.class, TestNGListeners.class})
public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // ================= SETUP =================
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {

        PropertyReader.loadProperties();
        driver = createDriver();
        try {
            if ("LocalHeadless".equalsIgnoreCase(PropertyReader.getProperty("ExecutionType"))) {
                driver.manage().window().setSize(new Dimension(1920, 1080));
            } else {
                driver.manage().window().maximize();
            }
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            driver.get(PropertyReader.getApplicationUrl().replaceAll("/+$", "") + "/#/login");
            login();
        } catch (Exception | AssertionError failure) {
            if (driver instanceof TakesScreenshot) {
                Plugin_ERP.Utiel.ScreenShotsUtils.takeScreenShot(driver, "SETUP_" + getClass().getSimpleName());
            }
            try {
                tearDown();
            } catch (RuntimeException cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
    }

    protected WebDriver createDriver() {
        return WebDriverFactory.initDriver();
    }

    @Step("تسجيل الدخول بإعدادات البيئة الحالية")
    protected void login() {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.Login(PropertyReader.getValidUsername(), PropertyReader.getValidPassword());
        loginPage.AssertLoginPageTitle();
    }

    // ================= TEARDOWN =================
    @AfterClass(alwaysRun = true)
    public void tearDown() {

        try {

            if (driver != null) WebDriverFactory.quitDriver(driver);
        } finally {
            driver = null;
            wait = null;
        }
    }
}
