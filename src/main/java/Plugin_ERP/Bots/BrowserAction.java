package Plugin_ERP.Bots;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Util.PropertyReader;
import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class BrowserAction {

    private final WebDriver driver;

    public BrowserAction(WebDriver driver) {
        this.driver = driver;
    }

    // maximize browser window
    public void maximizeWindow() {
        driver.manage().window().maximize();
    }

    // refresh browser
    public void refreshPage() {
        driver.navigate().refresh();
        LogUtils.info("PageRefresh");
    }

    // Get current URL
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        LogUtils.info("Current URL: " + url);
        return url;
    }

    public void switchToMainTab() {

        String mainWindow = driver.getWindowHandles().iterator().next();

        driver.switchTo().window(mainWindow);
    }

    // navigate to URL
    public void navigateToUrl(String url) {
        driver.navigate().to(url);
        LogUtils.info("Navigated to URL: " + url);
    }

    // closr current tab
    public void closeCurrentTab() {
        driver.close();
    }

    // open New tab
    public void openNewTab() {
        driver.switchTo().newWindow(org.openqa.selenium.WindowType.TAB);
    }

    public void navigateTo(String navigateAction) {
        switch (navigateAction) {
            case "back":
                driver.navigate().back();
                break;
            case "forward":
                driver.navigate().forward();
                break;
            case "refresh":
                driver.navigate().refresh();
                break;
            default:
                driver.navigate().to(navigateAction);
                break;
        }
    }

    public void clearBrowserCache() {
        try {
            ((JavascriptExecutor) driver)
                    .executeScript(
                            "window.localStorage.clear();" + "window.sessionStorage.clear();");

            // Clear cookies
            driver.manage().deleteAllCookies();

            // Hard reload (Ctrl+Shift+R equivalent)
            ((JavascriptExecutor) driver).executeScript("location.reload(true);");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(
                    ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));

            System.out.println("✅ Cache cleared and page reloaded");

        } catch (Exception e) {
            System.out.println("⚠️ Cache clear failed: " + e.getMessage());
        }
    }

    public void navigateWithRetry(String url) {

        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            try {

                driver.navigate().to(url);

                new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(
                                webDriver ->
                                        ((JavascriptExecutor) webDriver)
                                                .executeScript("return document.readyState")
                                                .equals("complete"));

                new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(ExpectedConditions.urlToBe(url));

                return;

            } catch (Exception e) {

                LogUtils.info("Navigation failed. Attempt " + attempt + "/" + maxAttempts);

                if (attempt == maxAttempts) {
                    throw new RuntimeException("Failed to navigate to: " + url, e);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Navigation retry interrupted", ex);
                }
            }
        }
    }

    public void waitForOverlayToDisappear() {
        new WaitActions(driver).waitForOverlayToDisappear();
    }

    public void OpenNavigationHelper(Pages page) {
        waitForOverlayToDisappear();
        String url = PropertyReader.getApplicationUrl() + page.getRoute();
        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                LogUtils.info("Opening page: " + page.name());
                driver.navigate().to(url);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

                // انتظار اكتمال تحميل الصفحة الأساسي
                wait.until(
                        webDriver ->
                                ((JavascriptExecutor) webDriver)
                                        .executeScript("return document.readyState")
                                        .equals("complete"));

                // التأكد من الوصول للرابط المطلوب
                wait.until(ExpectedConditions.urlToBe(url));

                // استنى اختفاء أي أوفرلاي/سبينر لحد ما الصفحة تخلص تحميل فعليًا
                waitForOverlayToDisappear();

                // تأكيد نهائي إننا لسه في نفس الصفحة المطلوبة بعد كل الانتظارات
                Assert.assertEquals(
                        driver.getCurrentUrl(),
                        url,
                        "لم يتم الوصول إلى الصفحة المطلوبة: " + page.name());

                LogUtils.info("Successfully opened page: " + page.name());
                return;

            } catch (Exception e) {
                LogUtils.info(
                        "Failed to open page: "
                                + page.name()
                                + " | Attempt "
                                + attempt
                                + "/"
                                + maxAttempts);
                if (attempt == maxAttempts) {
                    throw new RuntimeException("Failed to open page: " + page.name(), e);
                }

                // انتظار بسيط قبل إعادة المحاولة
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Navigation retry interrupted", ex);
                }
            }
        }
    }
}
