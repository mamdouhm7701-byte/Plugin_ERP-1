package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.*;
import Plugin_ERP.Enums.Pages;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

/** Page Navigation: focused operations used by the original page facade. */
final class PageNavigation {
    private final Abstract owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElmentAction utilites;
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
        Plugin_ERP.Utiel.Allur.ScreenNames.opening(route.getRoute());
        utilites.waits().waitForOverlayToDisappear();

        browserAction.OpenNavigationHelper(Pages.valueOf(route.name()));
        utilites.waits().waitForOverlayToDisappear();
        driver.navigate().refresh();
        utilites.waits().waitForOverlayToDisappear();


    }
}
