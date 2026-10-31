/* شرح الملف: اختبارات الصلاحيات: منع مستخدم بلا صلاحية من الطاولات؛ حماية رابط المستخدمين دون جلسة.
 */
package CoverageTest;

import static TestSupport.BasicDataFixtures.fields;

import Plugin_ERP.Drivers.Chrome;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.Util.PropertyReader;
import TestSupport.BasicDataScenarioTest;
import TestSupport.FixtureSession;
import io.qameta.allure.Story;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class PermissionsCoverageTest extends BasicDataScenarioTest {
    @Test
    @Story("المستخدم بلا صلاحية لا يصل لرابط الطاولات أو بياناته من الخادم")
    public void restrictedUserCannotOpenProtectedRouteOrReadApi() {
        String username = "qa_denied_" + UUID.randomUUID().toString().replace("-", ""),
                password = UUID.randomUUID() + "Aa1!";
        var restricted =
                basicData()
                        .fixture(
                                "restricted-permissions",
                                "users",
                                fields(
                                        "userName",
                                        username,
                                        "passwordHash",
                                        password,
                                        "isAdmin",
                                        false,
                                        "isActive",
                                        true,
                                        "maxAllowedDiscount",
                                        0,
                                        "permissions",
                                        List.of(),
                                        "branches",
                                        List.of(
                                                fields(
                                                        "branchId",
                                                        new FixtureSession(driver).branchId)),
                                        "defaultOtherPaymentMethods",
                                        List.of(),
                                        "imageUrl",
                                        "",
                                        "phone",
                                        "",
                                        "email",
                                        "",
                                        "base64Image",
                                        "",
                                        "imageExtension",
                                        ""));
        WebDriver limited = new Chrome().createDriver();
        try {
            limited.get(PropertyReader.getApplicationUrl() + "/#/login");
            LoginPage login = new LoginPage(limited);
            login.Login(
                    Plugin_ERP.Util.LoginUsernames.withinSameCompany(
                            PropertyReader.getValidUsername(), username),
                    password);
            login.assertAuthenticatedBranch(new FixtureSession(driver).branchId, restricted.id());
            limited.get(PropertyReader.getApplicationUrl() + "/#/tables");
            new WebDriverWait(limited, Duration.ofSeconds(20))
                    .until(d -> d.getCurrentUrl().contains("error-404"));
            Assert.assertTrue(
                    limited.findElements(By.cssSelector("button[data-action-type='new']")).stream()
                            .noneMatch(WebElement::isDisplayed));
            Object status =
                    Plugin_ERP.Util.VueComponentAccess.executeAsync(
                            limited,
                            """
                const done=arguments[arguments.length-1];
                erpVue.root().$store.dispatch('app/get',{url:'Tables'})
                  .then(()=>done(200)).catch(e=>done(e?.response?.status||e?.status||e?.data?.status||0));
                """);
            Assert.assertTrue(
                    Set.of("401", "403").contains(String.valueOf(status)),
                    "Backend must deny a user without viewTables; actual HTTP outcome=" + status);
        } catch (RuntimeException | AssertionError failure) {
            captureSessionFailure(limited, "المستخدم المحدود", failure);
            throw failure;
        } finally {
            limited.quit();
        }
    }

    @Test
    @Story("رابط محمي دون جلسة يعيد المستخدم إلى الدخول")
    public void unauthenticatedDirectRouteRequiresLogin() {
        WebDriver isolated = new Chrome().createDriver();
        try {
            isolated.get(PropertyReader.getApplicationUrl() + "/#/users");
            new LoginPage(isolated).assertRedirectedToLogin();
        } catch (RuntimeException | AssertionError failure) {
            captureSessionFailure(isolated, "المتصفح دون جلسة", failure);
            throw failure;
        } finally {
            isolated.quit();
        }
    }

    private void captureSessionFailure(WebDriver session, String label, Throwable failure) {
        try {
            io.qameta.allure.Allure.addAttachment(
                    "رابط " + label + " عند التوقف", session.getCurrentUrl());
            io.qameta.allure.Allure.addAttachment(
                    "صورة " + label + " عند التوقف",
                    "image/png",
                    new java.io.ByteArrayInputStream(
                            ((TakesScreenshot) session).getScreenshotAs(OutputType.BYTES)),
                    ".png");
        } catch (RuntimeException captureFailure) {
            failure.addSuppressed(captureFailure);
        }
    }
}
