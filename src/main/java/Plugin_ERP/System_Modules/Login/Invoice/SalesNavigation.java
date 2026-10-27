package Plugin_ERP.System_Modules.Login.Invoice;

import Plugin_ERP.Util.PropertyReader;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Focused SalesNavigation operations extracted from SalesModulePage. */
final class SalesNavigation {
    private final SalesModulePage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;

    SalesNavigation(SalesModulePage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    void slAwaitSession() {
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(
                        d ->
                                !d.getCurrentUrl().contains("/login")
                                        && Boolean.TRUE.equals(
                                                js(
                                                        "return Boolean(erpVue.root()?.$store?.getters['app/currentBranch']?.id)")));
    }

    void slOpen(String route) {
        Plugin_ERP.Util.Allur.ScreenNames.opening(route);
        String destination =
                PropertyReader.getApplicationUrl().replaceAll("/+$", "") + "/#/" + route;
        boolean reused = reusesFormComponent(driver.getCurrentUrl(), destination);
        if (!driver.getCurrentUrl().equals(destination)) driver.get(destination);
        // A query-only route change reuses Vue's mounted form and does not load its source.
        if (reused) driver.navigate().refresh();
        slAwaitSession();
        ready.until(
                d ->
                        Boolean.TRUE.equals(
                                js(
                                        """
            const route=erpVue.root()?.$router?.currentRoute;
            if(!route||route.path.replace(/\\/+$/g,'')!==arguments[0])return false;
            const view=route.matched[route.matched.length-1]?.instances?.default;
            return !!(view?._isMounted&&!view._isDestroyed&&view.$el?.isConnected&&view.$el.getClientRects?.().length);
            """,
                                        navigationPath(route))));
        ready.until(
                d ->
                        d
                                .findElements(
                                        By.cssSelector(
                                                "form button[data-action-type='save'],button[data-action-type='new'],table thead th"))
                                .stream()
                                .anyMatch(WebElement::isDisplayed));
        slDismiss();
    }

    static String navigationPath(String route) {
        return "/" + route.split("[?#]", 2)[0].replaceAll("^/+|/+$", "");
    }

    static boolean reusesFormComponent(String current, String destination) {
        return current.split("\\?", 2)[0].equals(destination.split("\\?", 2)[0]);
    }

    private Object js(String script, Object... args) {
        return owner.js(script, args);
    }

    private void slDismiss() {
        owner.slDismiss();
    }
}
