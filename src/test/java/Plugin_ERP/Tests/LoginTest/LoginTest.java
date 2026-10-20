package Plugin_ERP.Tests.LoginTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Utiel.PropertyReader;
import Plugin_ERP.Listners.TestNGListeners;

import com.automation.remarks.testng.UniversalVideoListener;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

public class LoginTest extends BaseTest {

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("WEB CLOUD")
    @Feature("LOGIN FEATURE")
    @Story("VALID LOGIN STORY")
    @Description("This test verifies that a user can log in with valid credentials.")
    @Link(name = "Login Test Case", url = "https://binfares.plugin-erp.com/#/login")
    @Test
    public void LoginTest() throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.Login(PropertyReader.getValidUsername(), PropertyReader.getValidPassword());
        loginPage.AssertLoginPageTitle();
        Thread.sleep(1500);
    }

    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("WEB CLOUD")
    @Feature("LOGIN FEATURE")
    @Story("INVALID LOGIN STORY")
    @Description("This test verifies that a user cant log in with valid credentials.")
    @Link(name = "Login Test Case", url = "https://binfares.plugin-erp.com/#/login")
    @Test
    public void InvalidLoginTest() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.Login(
                PropertyReader.getProperty("InvalidUsername"),
                PropertyReader.getProperty("InvalidPassword"));
        // You can add assertions here to verify that the login failed
        loginPage.AssertLoginPageTitle();
    }
}
