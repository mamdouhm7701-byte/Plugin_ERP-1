package Plugin_ERP.System_Modules.Login.Login;

import Plugin_ERP.Bots.ElmentAction;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.openqa.selenium.support.locators.RelativeLocator.with;

public class LoginPage {
    // Variables
    private WebDriver driver;
    private ElmentAction utilites;
    private final WebDriverWait wait;

    // Locators

    private final By username = By.id("login-email");
    private final By Password = By.cssSelector("input[type='password']");
    private final By LoginButton = with(By.tagName("button")).below(Password);
    private final By LOGIN_ERROR = By.cssSelector(".swal2-popup.swal2-show .swal2-html-container");
    private final By WELCOME_MESSAGE =
            By.xpath(
                    "//h4[contains(@class,'content-header-title') and normalize-space(.)='الصفحة الرئيسية']\n");
    private By OpenBasicData =
            By.xpath(
                    "//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");

    // Constructors
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.utilites = new ElmentAction(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.wait.pollingEvery(Duration.ofMillis(100));
    }

    // Methods
    public void Login(String user, String pass) {
        io.qameta.allure.Allure.step("تسجيل الدخول", () -> {
            waitForLoginForm();
            utilites.waits().waitForOverlayToDisappear();
            utilites.TaypeTextB(username, user);
            utilites.TaypeTextB(Password, pass);
            utilites.ClickElement(LoginButton);
        });
    }

    @Step("Assert Home page title is visible")
    public void AssertLoginPageTitle() {
        wait.withMessage("لم تظهر الصفحة الرئيسية بعد تسجيل الدخول")
                .until(ExpectedConditions.visibilityOfElementLocated(WELCOME_MESSAGE));
        Assert.assertFalse(driver.getCurrentUrl().contains("#/login"),
                "يجب مغادرة شاشة الدخول بعد نجاح المصادقة");
    }

    public void waitForLoginForm() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(username));
        wait.until(ExpectedConditions.visibilityOfElementLocated(Password));
        wait.until(ExpectedConditions.elementToBeClickable(LoginButton));
    }

    @Step("التحقق من رفض بيانات الدخول والبقاء خارج النظام")
    public void assertLoginRejected(String expectedMessage) {
        AssertInvalidLoginAlertMessage(expectedMessage);
        Assert.assertTrue(driver.getCurrentUrl().contains("#/login"),
                "يجب البقاء في شاشة الدخول بعد رفض البيانات");
        Assert.assertTrue(driver.findElement(username).isDisplayed(), "يجب بقاء نموذج الدخول ظاهرًا");
        Assert.assertTrue(driver.findElements(WELCOME_MESSAGE).stream().noneMatch(e -> e.isDisplayed()),
                "يجب ألا تظهر الصفحة الرئيسية بعد رفض المصادقة");
    }

    @Step("Assert Invalid Login Alert Message is: {expectedMessage}")
    public void AssertInvalidLoginAlertMessage(String expectedMessage) {
        String actualMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_ERROR))
                .getText().trim();
        Assert.assertEquals(
                actualMessage, expectedMessage, "❌ رسالة التنبيه عند تسجيل الدخول غير صحيحة");
    }
}
