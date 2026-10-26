package Plugin_ERP.Bots;

import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ElementActions {
    private final TextEntryActions textEntry;
    private WebDriver driver;
    private WebDriverWait wait;
    private WaitManagement waitManagement;
    private ValidationActions validationActions;
    private DropdownActions dropdownActions;
    private WaitActions waitActions;
    private CheckboxActions checkboxActions;
    private GridActions gridActions;
    private PriceListActions priceListActions;
    private UtilityActions utilityActions;
    private AlertAction alertAction;

    public ElementActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.waitManagement = new WaitManagement(driver);
        this.textEntry = new TextEntryActions(driver, waitManagement);
        this.validationActions = new ValidationActions(driver, wait, waitManagement);
        this.dropdownActions = new DropdownActions(driver, wait, waitManagement);
        this.waitActions = new WaitActions(driver, wait, waitManagement);
        this.checkboxActions = new CheckboxActions(driver, wait, waitManagement);
        this.gridActions = new GridActions(driver, wait, waitManagement);
        this.priceListActions = new PriceListActions(driver, wait, waitManagement);
        this.utilityActions = new UtilityActions(driver, wait, waitManagement);
        this.alertAction = new AlertAction(driver, wait, waitManagement);
    }

    // Getter
    public DropdownActions dropdown() {
        return dropdownActions;
    }

    public ValidationActions validation() {
        return validationActions;
    }

    public WaitActions waits() {
        return waitActions;
    }

    public CheckboxActions checkbox() {
        return checkboxActions;
    }

    public GridActions grid() {
        return gridActions;
    }

    public PriceListActions PriceList() {
        return priceListActions;
    }

    public UtilityActions Utility() {
        return utilityActions;
    }

    public AlertAction alert() {
        return alertAction;
    }

    // Methods

    // Get Property

    public String getProperty(By by, String property) {
        WebElement element = findElement(by);
        String propertyValue = element.getDomProperty(property);
        LogUtils.info(propertyValue);
        return propertyValue;
    }

    // Gtet Attribute
    public String getAttribute(By by, String attribute) {
        WebElement element = findElement(by);
        String attributeValue = element.getDomAttribute(attribute);
        LogUtils.info(attributeValue);
        return attributeValue;
    }

    private void scrollIntoView(WebElement element) {

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    private void jsClick(WebElement element) {

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void ClickElement(By locator) {
        waitManagement
                .fluentWait()
                .withMessage("Native click unavailable: " + locator)
                .until(
                        d -> {
                            WebElement element = d.findElement(locator);
                            if (!element.isDisplayed() || !element.isEnabled()) return false;
                            scrollIntoView(element);
                            element.click();
                            return true;
                        });
    }

    public void typeText(By locator, String value) {
        textEntry.enter(locator, value, false);
    }

    public WebElement findElement(By locator) {
        return driver.findElement(locator);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getText(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return element.getText();
    }

    public boolean isDisplayed(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return findElement(locator).isDisplayed();
        } catch (Exception e) {
            Plugin_ERP.Bots.LogUtils.debug(
                    "ElementActions condition unavailable: " + e.getClass().getSimpleName());
            return false;
        }
    }

    public WebElement getVisibleElement(By locator) {

        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {

            try {

                if (element.isDisplayed() && element.isEnabled()) {
                    LogUtils.info("VISIBLE ELEMENT FOUND");
                    return element;
                }

            } catch (Exception ignored) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "ElementActions fallback: " + ignored.getClass().getSimpleName());
            }
        }

        throw new NoSuchElementException("❌ No visible element found: " + locator);
    }

    public void clickOnEmptyArea() {
        new Actions(driver).moveToLocation(5, 5).click().perform();
    }

    public void typeTextAndVerify(By locator, String value) {
        textEntry.enter(locator, value, true);
    }

    public By getLastEditButtonForCustomerReceipt() {

        {
            return By.xpath(
                    "(//tr[contains(@id,'mainTable__row_')]//button[@data-action-type='edit'])[1]");
        }
    }

    public By getLastEditButtonForAdjustDamage() {

        return By.xpath("(//*[@data-action-type='edit'])[1]");
    }

    public void ClickPlusButtonByRow(int numberOfRows, int timesPerRow) {
        // استنى لحد ما كل الصفوف المطلوبة تكون موجودة فعليًا في الجدول
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(
                d -> {
                    List<WebElement> rows =
                            d.findElements(By.xpath("//tbody//tr[@role='row' and @data-pk]"));
                    return rows.size() >= numberOfRows;
                });

        for (int rowIndex = 1; rowIndex <= numberOfRows; rowIndex++) {
            for (int i = 0; i < timesPerRow; i++) {
                List<WebElement> rows =
                        driver.findElements(By.xpath("//tbody//tr[@role='row' and @data-pk]"));
                WebElement targetRow = rows.get(rowIndex - 1);
                List<WebElement> buttons = targetRow.findElements(By.tagName("button"));
                WebElement plusButton = buttons.get(1); // الزرار التاني = "+"

                ((JavascriptExecutor) driver)
                        .executeScript(
                                "arguments[0].scrollIntoView({block: 'center'});", plusButton);

                wait.until(
                        org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                                plusButton));

                plusButton.click();
                LogUtils.info("✅ صف رقم " + rowIndex + " - ضغطة رقم " + (i + 1));
            }
        }
    }

    public void ClickMinusButtonByRow(int numberOfRows, int timesPerRow) {
        // استنى لحد ما كل الصفوف المطلوبة تكون موجودة فعليًا في الجدول
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(
                d -> {
                    List<WebElement> rows =
                            d.findElements(By.xpath("//tbody//tr[@role='row' and @data-pk]"));
                    return rows.size() >= numberOfRows;
                });

        for (int rowIndex = 1; rowIndex <= numberOfRows; rowIndex++) {
            for (int i = 0; i < timesPerRow; i++) {
                List<WebElement> rows =
                        driver.findElements(By.xpath("//tbody//tr[@role='row' and @data-pk]"));
                WebElement targetRow = rows.get(rowIndex - 1);
                List<WebElement> buttons = targetRow.findElements(By.tagName("button"));
                WebElement minusButton = buttons.get(2); // الزرار التالت = "-"

                ((JavascriptExecutor) driver)
                        .executeScript(
                                "arguments[0].scrollIntoView({block: 'center'});", minusButton);

                wait.until(
                        org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                                minusButton));

                minusButton.click();
                LogUtils.info("✅ صف رقم " + rowIndex + " - ضغطة Minus رقم " + (i + 1));
            }
        }
    }
}
