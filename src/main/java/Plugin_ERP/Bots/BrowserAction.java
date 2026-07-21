package Plugin_ERP.Bots;

import Plugin_ERP.Utiel.PropertyReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import Plugin_ERP.Enums.Pages;
import org.testng.Assert;

public class BrowserAction {

    private final WebDriver driver;

    public BrowserAction(WebDriver driver) {
        this.driver = driver;
    }


    //maximize browser window
    public void maximizeWindow() {
        driver.manage().window().maximize();
    }

    //refresh browser
    public void refreshPage() {
        driver.navigate().refresh();
        LogUtils.info("PageRefresh");
    }

    //Get current URL
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        LogUtils.info("Current URL: " + url);
        return url;
    }


    public void switchToMainTab() {

        String mainWindow = driver.getWindowHandles().iterator().next();

        driver.switchTo().window(mainWindow);
    }

    //navigate to URL
    public void navigateToUrl(String url) {
        driver.navigate().to(url);
        LogUtils.info("Navigated to URL: " + url);
    }

    //closr current tab
    public void closeCurrentTab() {
        driver.close();
    }

    //open New tab
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
            ((JavascriptExecutor) driver).executeScript(
                    "window.localStorage.clear();" +
                            "window.sessionStorage.clear();"
            );

            // Clear cookies
            driver.manage().deleteAllCookies();

            // Hard reload (Ctrl+Shift+R equivalent)
            ((JavascriptExecutor) driver).executeScript(
                    "location.reload(true);"
            );

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.jsReturnsValue(
                    "return document.readyState === 'complete'"
            ));

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
                        .until(webDriver ->
                                ((JavascriptExecutor) webDriver)
                                        .executeScript("return document.readyState")
                                        .equals("complete"));

                new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(ExpectedConditions.urlToBe(url));

                return;

            } catch (Exception e) {

                LogUtils.info("Navigation failed. Attempt "
                        + attempt + "/" + maxAttempts);

                if (attempt == maxAttempts) {
                    throw new RuntimeException(
                            "Failed to navigate to: " + url, e);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void waitForOverlayToDisappear() {
        By overlay = By.cssSelector("div.b-overlay");
        long timeoutMillis = 30000;
        long startTime = System.currentTimeMillis();

        try {
            long hiddenSince = -1;

            while (true) {
                if (System.currentTimeMillis() - startTime > timeoutMillis) {
                    throw new RuntimeException("Overlay did not stabilize within timeout.");
                }

                boolean overlayVisible;
                try {
                    List<WebElement> overlays = driver.findElements(overlay);
                    overlayVisible = overlays.stream().anyMatch(el -> {
                        try {
                            return el.isDisplayed();
                        } catch (StaleElementReferenceException e) {
                            return false;
                        }
                    });
                } catch (StaleElementReferenceException e) {
                    overlayVisible = false;
                }

                if (overlayVisible) {
                    hiddenSince = -1;
                } else {
                    if (hiddenSince == -1) {
                        hiddenSince = System.currentTimeMillis();
                    }
                    if (System.currentTimeMillis() - hiddenSince >= 1000) {
                        break;
                    }
                }

                Thread.sleep(200);
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(d ->
                    "complete".equals(
                            ((JavascriptExecutor) d).executeScript("return document.readyState")));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for overlay.", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Overlay did not stabilize within timeout.", e);
        }
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
                wait.until(webDriver ->
                        ((JavascriptExecutor) webDriver)
                                .executeScript("return document.readyState")
                                .equals("complete"));

                // التأكد من الوصول للرابط المطلوب
                wait.until(ExpectedConditions.urlToBe(url));

                // استنى اختفاء أي أوفرلاي/سبينر لحد ما الصفحة تخلص تحميل فعليًا
                waitForOverlayToDisappear();

                // تأكيد نهائي إننا لسه في نفس الصفحة المطلوبة بعد كل الانتظارات
                Assert.assertEquals(driver.getCurrentUrl(), url,
                        "لم يتم الوصول إلى الصفحة المطلوبة: " + page.name());

                LogUtils.info("Successfully opened page: " + page.name());
                return;

            } catch (Exception e) {
                LogUtils.info("Failed to open page: "
                        + page.name()
                        + " | Attempt "
                        + attempt + "/" + maxAttempts);
                if (attempt == maxAttempts) {
                    throw new RuntimeException(
                            "Failed to open page: " + page.name(), e);
                }

                // انتظار بسيط قبل إعادة المحاولة
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    }

