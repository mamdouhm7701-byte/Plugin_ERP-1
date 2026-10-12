package Plugin_ERP.Tests.LoginTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Listners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.time.Duration;

@Listeners({
        AllureTestNg.class,
        TestNGListeners.class
})
public class BaseTest {

    protected static WebDriver driver;
    protected static WebDriverWait wait;

    // ================= SETUP =================
    @BeforeSuite(alwaysRun = true)
    public void setup() throws Exception {

        driver = WebDriverFactory.initDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(PropertyReader.getApplicationUrl() + "/#/login");

        Thread.sleep(5000);

        System.out.println("Current URL = " + driver.getCurrentUrl());
        System.out.println("Title = " + driver.getTitle());
        System.out.println("ReadyState = " + ((JavascriptExecutor) driver)
                .executeScript("return document.readyState"));

        System.out.println("login-email exists = "
                + driver.getPageSource().contains("login-email"));


        login();
     //  Thread.sleep(5000);

      //  login();
    }


    @Step("Login with username: {user} and password: {pass}")

    private void login() {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.Login(
                PropertyReader.getValidUsername(),
                PropertyReader.getValidPassword()
        );
    }

    // ================= BEFORE TEST =================


    // ================= AFTER TEST =================


    // ================= TEARDOWN =================
    @AfterSuite(alwaysRun = true)
    public void tearDown() {

        try {

            if (driver != null) {
                driver.quit();
                driver = null;
            }

        } catch (Exception ignored) {}

        generateAllureReport();
    }


    // ================= ALLURE =================
    private void generateAllureReport() {

        try {

            Process generate = new ProcessBuilder(
                    "cmd",
                    "/c",
                    "allure generate allure-results --clean -o allure-report"
            )
                    .directory(new File(System.getProperty("user.dir")))
                    .start();

            generate.waitFor();

            new ProcessBuilder(
                    "cmd",
                    "/c",
                    "start allure-report\\index.html"
            )
                    .directory(new File(System.getProperty("user.dir")))
                    .start();

            System.out.println("✅ Allure Report Generated & Opened");

        } catch (Exception e) {
            System.out.println("❌ Allure Error: " + e.getMessage());
        }
    }
}