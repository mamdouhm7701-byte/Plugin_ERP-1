package Plugin_ERP.System_Modules.Login.Login;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import static org.openqa.selenium.support.locators.RelativeLocator.with;

public class LoginPage {
    //Variables
    private WebDriver driver;
    private ElmentAction utilites;
    private AlertAction alertAction;




    //Locators


   private final By username = By.id("login-email");
   private final By Password = By.cssSelector("input[type='password']");
   private final By LoginButton = with(By.tagName("button")).below(Password);
   private final By WELCOME_MESSAGE = By.xpath("//h4[contains(@class,'content-header-title') and normalize-space(.)='الصفحة الرئيسية']\n");
    private By OpenBasicData = By.xpath("//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");


    //Constructors
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.utilites = new ElmentAction(driver);

    }



    //Methods
    @Step("Login with username: {user} and password: {pass}")
    public void Login(String user, String pass) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB( username, user);
        utilites.TaypeTextB  ( Password, pass);
        utilites.ClickElement( LoginButton);

 }

    @Step("Assert Home page title is visible")
    public void AssertLoginPageTitle() {
        Assert.assertTrue(
                utilites.isDisplayed(WELCOME_MESSAGE),
                "❌ عنوان (الصفحة الرئيسية) لم يظهر بعد تسجيل الدخول"

        );
    }
    @Step("Assert Invalid Login Alert Message is: {expectedMessage}")
    public void AssertInvalidLoginAlertMessage(String expectedMessage) {
        String actualMessage = alertAction.getAlertText();
        Assert.assertEquals(
                actualMessage,
                expectedMessage,
                "❌ رسالة التنبيه عند تسجيل الدخول غير صحيحة"
        );
    }

}
