/* شرح الملف: اختبار سلامة تجهيز الدخول والمتصفح قبل الحالات ثم إغلاق المتصفح بعد الانتهاء.
 */
package Plugin_ERP.Tests.LoginTest;

import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.Listeners.TestNGListeners;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Util.PropertyReader;
import io.qameta.allure.Step;
import io.qameta.allure.testng.AllureTestNg;
import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

@Listeners({AllureTestNg.class, TestNGListeners.class})
public class BaseTest implements Plugin_ERP.Util.RunEnvironmentGate.ProtectedScenario {

    protected WebDriver driver;
    protected WebDriverWait wait;
    private Throwable setupFailure;
    private Plugin_ERP.BranchProfiles.BranchProfile branchProfile;
    private Plugin_ERP.BranchProfiles.BranchProfileScope profileScope;

    protected final void requirePreparedSession() {
        if (setupFailure != null
                && Plugin_ERP.Util.CapacityFailure.matches(setupFailure.getMessage()))
            throw new org.testng.SkipException(
                    "ENVIRONMENT_BLOCKED: LICENSE_CAPACITY; "
                            + Plugin_ERP.Util.CapacityFailure.explanation(),
                    setupFailure);
        if (setupFailure != null)
            throw new org.testng.SkipException(
                    "Scenario preparation skipped because base login/setup failed; see the setup result",
                    setupFailure);
        if (driver == null)
            throw new IllegalStateException(
                    "Scenario preparation requires a successfully created browser");
    }

    // ================= SETUP =================
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        setupFailure = null;
        branchProfile = null;
        try {
            Plugin_ERP.Util.RunEnvironmentGate.current().beforeScenario();
            PropertyReader.loadProperties();
            branchProfile = Plugin_ERP.BranchProfiles.BranchProfiles.selected().orElse(null);
            if (branchProfile != null)
                profileScope = Plugin_ERP.BranchProfiles.BranchProfileScope.bind(branchProfile);
            driver = createDriver();
            if ("LocalHeadless".equalsIgnoreCase(PropertyReader.getProperty("ExecutionType"))) {
                driver.manage().window().setSize(new Dimension(1920, 1080));
            } else {
                driver.manage().window().maximize();
            }
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            driver.get(PropertyReader.getApplicationUrl().replaceAll("/+$", "") + "/#/login");
            login();
            if (branchProfile != null) {
                if (Boolean.parseBoolean(PropertyReader.getProperty("branch.profile.select")))
                    Plugin_ERP.BranchProfiles.BranchSessionSelector.select(driver, branchProfile);
                verifySelectedBranchProfile();
            }
        } catch (Exception | AssertionError | LinkageError failure) {
            setupFailure = failure;
            if (driver instanceof TakesScreenshot) {
                Plugin_ERP.Util.ScreenShotsUtils.takeScreenShot(
                        driver, "SETUP_" + getClass().getSimpleName());
            }
            try {
                tearDown();
            } catch (RuntimeException cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
    }

    @BeforeMethod(alwaysRun = true)
    public final void verifyProfileBeforeScenario() {
        Plugin_ERP.Util.RunEnvironmentGate.current().beforeScenario();
        if (branchProfile != null) {
            requirePreparedSession();
            verifySelectedBranchProfile();
        }
    }

    @AfterMethod(alwaysRun = true)
    public final void verifyProfileAfterScenario() {
        if (branchProfile != null && setupFailure == null && driver != null) {
            verifySelectedBranchProfile();
        }
    }

    @Step("التحقق من إعداد الفرع المختار دون تغييره")
    private void verifySelectedBranchProfile() {
        if (branchProfile == null) return;
        var actual =
                Plugin_ERP.BranchProfiles.BranchProfileReader.capture(
                        driver, Duration.ofSeconds(30));
        var evidence = new org.json.JSONObject();
        evidence.put("profile", branchProfile.name());
        evidence.put("fingerprint", branchProfile.fingerprint());
        evidence.put("expected", branchProfile.expected().fields());
        evidence.put("actual", actual.fields());
        io.qameta.allure.Allure.addAttachment(
                "Branch profile preflight", "application/json", evidence.toString(2), ".json");
        branchProfile.verify(actual);
        Plugin_ERP.BranchProfiles.AdditionalTaxExpectation.capture().verify(driver);
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
            if (profileScope != null) {
                profileScope.close();
                profileScope = null;
            }
        }
    }
}
