package Plugin_ERP.Bots;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BaseAction {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected WaitMangment waitMangment;

    public BaseAction(
            WebDriver driver,
            WebDriverWait wait,
            WaitMangment waitMangment
    ) {
        this.driver = driver;
        this.wait = wait;
        this.waitMangment = waitMangment;
    }

    protected void scrollIntoView(WebElement element) {

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );
    }

    protected void jsClick(WebElement element) {

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                element
        );
    }
    public void clickOnEmptyArea() {
        WebElement body = driver.findElement(By.tagName("body"));

        new Actions(driver)
                .moveToElement(body, 5, 5)
                .click()
                .perform();
    }
}