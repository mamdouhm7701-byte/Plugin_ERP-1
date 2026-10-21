package Plugin_ERP.Tests.LoginTest;

import Plugin_ERP.Drivers.Browser;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Utiel.PropertyReader;
import Plugin_ERP.Utiel.ScreenShotsUtils;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/** Authentication owns a fresh browser per method, separate from the ERP suite session. */
@Listeners(AllureTestNg.class)
@Epic("WEB CLOUD")
@Feature("LOGIN FEATURE")
@Owner("MAMDOUH")
public class LoginTest {
    private final ThreadLocal<WebDriver> browsers = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void openLogin() {
        PropertyReader.loadProperties();
        WebDriver driver = Browser.valueOf(PropertyReader.getProperty("BrowserType")
                .trim().toUpperCase(Locale.ROOT)).getDriverFactory().createDriver();
        browsers.set(driver);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.get(PropertyReader.getApplicationUrl().replaceAll("/+$", "") + "/#/login");
        new LoginPage(driver).waitForLoginForm();
    }

    @AfterMethod(alwaysRun = true)
    public void closeLogin(ITestResult result) {
        WebDriver driver = browsers.get();
        try {
            if (driver != null) {
                if (!result.isSuccess()) ScreenShotsUtils.takeScreenShot(driver, "FAIL_" + result.getName());
                driver.quit();
            }
        } finally {
            browsers.remove();
        }
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("VALID LOGIN STORY")
    @Description("Fresh unauthenticated session accepts valid credentials and reaches home.")
    public void LoginTest() {
        String username = PropertyReader.getValidUsername();
        String password = PropertyReader.getValidPassword();
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException("Login credentials are missing; see config/Login.local.properties or ERP environment variables.");
        }
        LoginPage page = new LoginPage(browsers.get());
        page.Login(username, password);
        page.AssertLoginPageTitle();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("INVALID LOGIN STORY")
    @Description("Unknown generated account is rejected with a credential error and stays on login.")
    public void InvalidLoginTest() {
        LoginPage page = new LoginPage(browsers.get());
        // Avoid failed-password attempts against a real account or saved invalid data.
        page.Login("qa-login-" + UUID.randomUUID() + "@example.invalid", UUID.randomUUID().toString());
        page.assertLoginRejected(System.getProperty("Login.UnknownUserMessage", "User.UserNotFound"));
    }
}
