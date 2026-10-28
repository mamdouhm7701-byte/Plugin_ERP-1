/* شرح الملف: مساعدة لفتح شاشات المشتريات والانتقال بينها وإغلاق الرسائل العارضة. فائدتها الوصول إلى
 * مستند الشراء المطلوب.
 */
package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.Util.PropertyReader;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class PurchaseNavigation {
    private final PurchaseModulePage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;

    PurchaseNavigation(PurchaseModulePage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    void puAwaitSession() {
        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(
                        d ->
                                !d.getCurrentUrl().contains("/login")
                                        && Boolean.TRUE.equals(
                                                js(
                                                        "return Boolean(erpVue.root()?.$store?.getters['app/currentBranch']?.id)")));
    }

    void puOpen(String route) {
        Plugin_ERP.Util.Allur.ScreenNames.opening(route);
        driver.get(PropertyReader.getApplicationUrl() + "/#/" + route);
        driver.navigate().refresh();
        puAwaitSession();
        ready.until(
                d ->
                        d
                                .findElements(
                                        By.cssSelector(
                                                "form button[data-action-type='save'],button[data-action-type='new'],table thead th"))
                                .stream()
                                .anyMatch(WebElement::isDisplayed));
        puDismiss();
    }

    private Object js(String script, Object... args) {
        return owner.js(script, args);
    }

    private void puDismiss() {
        owner.puDismiss();
    }
}
