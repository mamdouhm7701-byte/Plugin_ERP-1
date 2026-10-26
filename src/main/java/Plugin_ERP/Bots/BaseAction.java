package Plugin_ERP.Bots;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BaseAction {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected WaitMangment waitMangment;

    public BaseAction(WebDriver driver, WebDriverWait wait, WaitMangment waitMangment) {
        this.driver = driver;
        this.wait = wait;
        this.waitMangment = waitMangment;
    }

    /** Re-read the row on every poll because provider refreshes replace DOM nodes. */
    protected String waitForRowPk(By rowLocator) {
        return new WebDriverWait(driver, java.time.Duration.ofSeconds(20))
                .ignoring(StaleElementReferenceException.class)
                .until(d -> {
                    var rows = d.findElements(rowLocator);
                    if (rows.isEmpty()) return null;
                    String pk = rows.get(0).getAttribute("data-pk");
                    return pk == null || pk.isBlank() || "null".equals(pk.trim()) ? null : pk.trim();
                });
    }

    protected void scrollIntoView(WebElement element) {

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    protected void jsClick(WebElement element) {

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void clickOnEmptyArea() {
        new Actions(driver).moveToLocation(5, 5).click().perform();
    }
}
