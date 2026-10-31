/* شرح الملف: شاشة تسجيل الدخول: بندخل اسم المستخدم وكلمة المرور ونراجع الدخول للفرع الصحيح. فائدتها
 * بدء العمل بالحساب المطلوب ومعرفة سبب رفض الدخول.
 */
package Plugin_ERP.System_Modules.Login.Login;

import static org.openqa.selenium.support.locators.RelativeLocator.with;

import Plugin_ERP.Bots.ElementActions;
import io.qameta.allure.Step;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class LoginPage {
    // Variables
    private WebDriver driver;
    private ElementActions utilites;
    private final WebDriverWait wait;

    // Locators

    private final By username = By.id("login-email");
    private final By Password = By.cssSelector("input[type='password']");
    private final By LoginButton = with(By.tagName("button")).below(Password);
    private final By LOGIN_ERROR = By.cssSelector(".swal2-popup.swal2-show .swal2-html-container");
    private final By WELCOME_MESSAGE =
            By.xpath(
                    "//h4[contains(@class,'content-header-title') and normalize-space(.)='الصفحة الرئيسية']\n");

    // Constructors
    public LoginPage(WebDriver driver) {
        this(driver, Duration.ofSeconds(30));
    }

    LoginPage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.utilites = new ElementActions(driver);
        this.wait = new WebDriverWait(driver, timeout);
        this.wait.pollingEvery(Duration.ofMillis(100));
    }

    // Methods
    public void Login(String user, String pass) {
        Plugin_ERP.Util.Allur.ScenarioAccess.login(driver, user, pass);
        io.qameta.allure.Allure.step(
                "تسجيل الدخول",
                () -> {
                    waitForLoginForm();
                    utilites.waits().waitForOverlayToDisappear();
                    utilites.typeTextAndVerify(username, user);
                    utilites.typeTextAndVerify(Password, pass);
                    utilites.ClickElement(LoginButton);
                });
    }

    @Step("Assert Home page title is visible")
    public void AssertLoginPageTitle() {
        wait.withMessage("لم تظهر الصفحة الرئيسية بعد تسجيل الدخول")
                .until(ExpectedConditions.visibilityOfElementLocated(WELCOME_MESSAGE));
        Assert.assertFalse(
                driver.getCurrentUrl().contains("#/login"),
                "يجب مغادرة شاشة الدخول بعد نجاح المصادقة");
    }

    @Step("التحقق من اكتمال الجلسة على الفرع المطلوب دون اشتراط صلاحية الصفحة الرئيسية")
    public void assertAuthenticatedBranch(Object expectedBranchId) {
        assertAuthenticatedBranch(expectedBranchId, null);
    }

    @Step("التحقق من جلسة مستخدم الاختبار على فرعه الصحيح")
    public void assertAuthenticatedBranch(Object expectedBranchId, Object expectedUserId) {
        if (expectedBranchId == null)
            throw new IllegalArgumentException("Expected branch is required");
        var last = new java.util.concurrent.atomic.AtomicReference<Object>();
        try {
            wait.withMessage("لم تكتمل جلسة المستخدم على الفرع المطلوب بعد تسجيل الدخول")
                    .until(
                            d -> {
                                if (d.getCurrentUrl().contains("#/login")) return false;
                                Object actual =
                                        Plugin_ERP.Util.VueComponentAccess.execute(
                                                d,
                                                """
                            const store=erpVue.root()?.$store;
                            return {authenticated:Boolean(store?.state?.auth?.token),
                              branchId:store?.getters['app/currentBranch']?.id??store?.state?.app?.branchId,
                              userId:store?.state?.auth?.profile?.userId};
                            """);
                                last.set(actual);
                                if (!(actual instanceof java.util.Map<?, ?> session)
                                        || !Boolean.TRUE.equals(session.get("authenticated")))
                                    return false;
                                Object branch = session.get("branchId"),
                                        user = session.get("userId");
                                return branch != null
                                        && expectedBranchId.toString().equals(branch.toString())
                                        && (expectedUserId == null
                                                || (user != null
                                                        && expectedUserId
                                                                .toString()
                                                                .equals(user.toString())));
                            });
        } catch (org.openqa.selenium.TimeoutException failure) {
            var context = new java.util.LinkedHashMap<String, Object>();
            context.put("url", driver.getCurrentUrl());
            context.put("expectedBranchId", expectedBranchId);
            context.put("expectedUserId", expectedUserId);
            context.put("session", last.get());
            io.qameta.allure.Allure.addAttachment(
                    "حالة جلسة الدخول دون بيانات سرية",
                    "application/json",
                    org.json.simple.JSONValue.toJSONString(context),
                    ".json");
            throw failure;
        }
    }

    public void waitForLoginForm() {
        wait.withMessage("لم يظهر حقل اسم المستخدم في شاشة الدخول")
                .until(ExpectedConditions.visibilityOfElementLocated(username));
        wait.withMessage("لم يظهر حقل كلمة المرور في شاشة الدخول")
                .until(ExpectedConditions.visibilityOfElementLocated(Password));
        wait.withMessage("زر تسجيل الدخول لم يصبح جاهزًا للضغط")
                .until(ExpectedConditions.elementToBeClickable(LoginButton));
    }

    @Step("التحقق من التحويل إلى الدخول واكتمال ظهور حقول الدخول وزرها")
    public void assertRedirectedToLogin() {
        wait.withMessage("لم يُحوّل الرابط المحمي إلى تسجيل الدخول")
                .until(d -> d.getCurrentUrl().contains("#/login"));
        waitForLoginForm();
        Assert.assertTrue(
                driver.getCurrentUrl().contains("#/login"),
                "غادر المتصفح شاشة الدخول قبل اكتمال التحقق من منع الوصول");
    }

    @Step("التحقق من رفض بيانات الدخول والبقاء خارج النظام")
    public void assertLoginRejected(String expectedMessage) {
        AssertInvalidLoginAlertMessage(expectedMessage);
        Assert.assertTrue(
                driver.getCurrentUrl().contains("#/login"),
                "يجب البقاء في شاشة الدخول بعد رفض البيانات");
        Assert.assertTrue(
                driver.findElement(username).isDisplayed(), "يجب بقاء نموذج الدخول ظاهرًا");
        Assert.assertTrue(
                driver.findElements(WELCOME_MESSAGE).stream().noneMatch(e -> e.isDisplayed()),
                "يجب ألا تظهر الصفحة الرئيسية بعد رفض المصادقة");
    }

    @Step("Assert Invalid Login Alert Message is: {expectedMessage}")
    public void AssertInvalidLoginAlertMessage(String expectedMessage) {
        String actualMessage =
                wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_ERROR))
                        .getText()
                        .trim();
        Assert.assertEquals(
                actualMessage, expectedMessage, "❌ رسالة التنبيه عند تسجيل الدخول غير صحيحة");
    }
}
