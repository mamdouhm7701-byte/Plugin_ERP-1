package Plugin_ERP.System_Modules.Login.Login;

import java.lang.reflect.Proxy;
import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

/** A restricted user can authenticate without permission to render the home page. */
public final class LoginSessionContractTest {
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
