/* شرح الملف: يراجع اكتمال ظهور شاشة الدخول، ويرفض اعتبار المستخدم داخل النظام قبل تأكيد هويته وفرعه حتى لو لم يُسمح له بعرض الصفحة الرئيسية.
 */
package Plugin_ERP.System_Modules.Login.Login;

import java.lang.reflect.Proxy;
import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class LoginSessionContractTest {
    @Test
    public void redirectWaitsForTheDelayedLoginFormInsteadOfCheckingOnlyTheUrl() {
        var reads = new java.util.concurrent.atomic.AtomicInteger();
        LoginPage page = loginRedirect("login", reads, true);
        page.assertRedirectedToLogin();
        Assert.assertTrue(
                reads.get() >= 3, "Login fields must be allowed to mount after the redirect");
    }

    @Test
    public void loginUrlWithoutTheLoginFormIsNotASuccessfulSecurityCheck() {
        TimeoutException failure =
                Assert.expectThrows(
                        TimeoutException.class,
                        () ->
                                loginRedirect(
                                                "login",
                                                new java.util.concurrent.atomic.AtomicInteger(),
                                                false)
                                        .assertRedirectedToLogin());
        Assert.assertTrue(failure.getMessage().contains("حقل اسم المستخدم"));
    }

    @Test
    public void visibleLoginInputsOnAProtectedRouteDoNotCountAsARedirect() {
        Assert.expectThrows(
                TimeoutException.class,
                () ->
                        loginRedirect(
                                        "users",
                                        new java.util.concurrent.atomic.AtomicInteger(),
                                        true)
                                .assertRedirectedToLogin());
    }

    private LoginPage loginRedirect(
            String route,
            java.util.concurrent.atomic.AtomicInteger reads,
            boolean formEventuallyAppears) {
        var input =
                (org.openqa.selenium.WebElement)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {org.openqa.selenium.WebElement.class},
                                (proxy, method, args) ->
                                        switch (method.getName()) {
                                            case "isDisplayed", "isEnabled" -> true;
                                            default -> null;
                                        });
        var driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class},
                                (proxy, method, args) ->
                                        switch (method.getName()) {
                                            case "getCurrentUrl" ->
                                                    "https://example.invalid/#/" + route;
                                            case "findElement" -> {
                                                if (args[0].equals(
                                                                org.openqa.selenium.By.id(
                                                                        "login-email"))
                                                        && (!formEventuallyAppears
                                                                || reads.incrementAndGet() < 3))
                                                    throw new org.openqa.selenium
                                                            .NoSuchElementException(
                                                            "Login component has not mounted");
                                                yield input;
                                            }
                                            default -> null;
                                        });
        return new LoginPage(driver, Duration.ofMillis(500));
    }

    private LoginPage page(String route, Object branch) {
        return page(route, branch, true, "owned-user");
    }

    private LoginPage page(String route, Object branch, boolean authenticated, String userId) {
        var driver =
                (WebDriver)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                                (proxy, method, args) -> {
                                    return switch (method.getName()) {
                                        case "getCurrentUrl" ->
                                                "https://example.invalid/#/" + route;
                                        case "executeScript" ->
                                                java.util.Map.of(
                                                        "branchId",
                                                        branch,
                                                        "authenticated",
                                                        authenticated,
                                                        "userId",
                                                        userId);
                                        case "toString" -> "Limited-session test driver";
                                        default -> null;
                                    };
                                });
        return new LoginPage(driver, Duration.ofMillis(150));
    }

    @Test
    public void restrictedLandingPageCanHaveAnAuthenticatedBranchSession() {
        page("error-404", "owned-branch").assertAuthenticatedBranch("owned-branch");
    }

    @Test
    public void aDifferentBranchIsNotAcceptedAsSuccessfulPreparation() {
        Assert.expectThrows(
                TimeoutException.class,
                () -> page("error-404", "other-branch").assertAuthenticatedBranch("owned-branch"));
    }

    @Test
    public void loginPageWithRememberedBranchIsNotAnAuthenticatedSession() {
        Assert.expectThrows(
                TimeoutException.class,
                () -> page("login", "owned-branch").assertAuthenticatedBranch("owned-branch"));
    }

    @Test
    public void missingExpectedBranchIsRejected() {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> page("home", "owned-branch").assertAuthenticatedBranch(null));
    }

    @Test
    public void aRememberedBranchWithoutAuthenticationIsRejected() {
        Assert.expectThrows(
                TimeoutException.class,
                () ->
                        page("error-404", "owned-branch", false, "owned-user")
                                .assertAuthenticatedBranch("owned-branch", "owned-user"));
    }

    @Test
    public void aDifferentAuthenticatedUserCannotSatisfyTheFixtureSession() {
        Assert.expectThrows(
                TimeoutException.class,
                () ->
                        page("error-404", "owned-branch", true, "different-user")
                                .assertAuthenticatedBranch("owned-branch", "owned-user"));
    }
}
