/* شرح الملف: مساعدة لفتح أقسام النظام وشاشاته. فائدتها الوصول إلى العمل المطلوب من القوائم.
 */
package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.*;
import Plugin_ERP.Enums.Pages;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class PageNavigation {
    private final Abstract owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;
    private final Plugin_ERP.Bots.BrowserAction browserAction;

    PageNavigation(Abstract owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
        this.browserAction = owner.context().browser();
    }

    void NavigateTo(String url) {
        browserAction.navigateWithRetry(url);
        LogUtils.info("تم الانتقال الى: " + url);
    }

    void NavigationHelper(Pages route) {
        Plugin_ERP.Util.Allur.ScreenNames.opening(route.getRoute());
        browserAction.OpenNavigationHelper(Pages.valueOf(route.name()));
        driver.navigate().refresh();
        utilites.waits().waitForOverlayToDisappear();
    }
}
