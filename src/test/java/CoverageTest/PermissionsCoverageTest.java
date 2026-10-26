package CoverageTest;

import Plugin_ERP.Drivers.Chrome;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Utiel.PropertyReader;
import TestSupport.BasicDataScenarioTest;
import TestSupport.FixtureSession;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Story;
import static TestSupport.BasicDataFixtures.fields;

/** Direct-route and backend authorization use a separately authenticated disposable limited user. */
public final class PermissionsCoverageTest extends BasicDataScenarioTest {
    @Test @Story("المستخدم بلا صلاحية لا يصل لرابط الطاولات أو بياناته من الخادم")
    public void restrictedUserCannotOpenProtectedRouteOrReadApi() {
        String username="qa_denied_"+UUID.randomUUID().toString().replace("-", ""), password=UUID.randomUUID()+"Aa1!";
        var restricted=basicData().fixture("restricted-permissions","users",fields("userName",username,"passwordHash",password,
                "isAdmin",false,"isActive",true,"maxAllowedDiscount",0,"permissions",List.of(),
                "branches",List.of(fields("branchId",new FixtureSession(driver).branchId)),"defaultOtherPaymentMethods",List.of(),
                "imageUrl","","phone","","email","","base64Image","","imageExtension",""));
        WebDriver limited=new Chrome().createDriver();
        try {
            limited.get(PropertyReader.getApplicationUrl()+"/#/login");
            LoginPage login=new LoginPage(limited);
            login.Login(Plugin_ERP.Utiel.LoginUsernames.withinSameCompany(PropertyReader.getValidUsername(),username),password);
            login.assertAuthenticatedBranch(new FixtureSession(driver).branchId,restricted.id());
            limited.get(PropertyReader.getApplicationUrl()+"/#/tables");
            new WebDriverWait(limited,Duration.ofSeconds(20)).until(d->d.getCurrentUrl().contains("error-404"));
            Assert.assertTrue(limited.findElements(By.cssSelector("button[data-action-type='new']")).stream().noneMatch(WebElement::isDisplayed));
            Object status=((JavascriptExecutor)limited).executeAsyncScript("""
                const done=arguments[arguments.length-1];
                document.querySelector('#app').__vue__.$store.dispatch('app/get',{url:'Tables'})
                  .then(()=>done(200)).catch(e=>done(e?.response?.status||e?.status||e?.data?.status||0));
                """);
            Assert.assertTrue(Set.of("401","403").contains(String.valueOf(status)),
                    "Backend must deny a user without viewTables; actual HTTP outcome="+status);
        } catch (RuntimeException | AssertionError failure) {
            try {
                io.qameta.allure.Allure.addAttachment("رابط جلسة المستخدم المحدود عند التوقف", limited.getCurrentUrl());
                io.qameta.allure.Allure.addAttachment("صورة جلسة المستخدم المحدود", "image/png",
                        new java.io.ByteArrayInputStream(((TakesScreenshot)limited).getScreenshotAs(OutputType.BYTES)), ".png");
            } catch (RuntimeException captureFailure) { failure.addSuppressed(captureFailure); }
            throw failure;
        } finally { limited.quit(); }
    }
    @Test @Story("رابط محمي دون جلسة يعيد المستخدم إلى الدخول")
    public void unauthenticatedDirectRouteRequiresLogin() {
        WebDriver isolated=new Chrome().createDriver();
        try {
            isolated.get(PropertyReader.getApplicationUrl()+"/#/users");
            new WebDriverWait(isolated,Duration.ofSeconds(20)).until(d->d.getCurrentUrl().contains("/login"));
            Assert.assertFalse(isolated.findElements(By.cssSelector("#login-email")).isEmpty());
        } finally { isolated.quit(); }
    }
}
