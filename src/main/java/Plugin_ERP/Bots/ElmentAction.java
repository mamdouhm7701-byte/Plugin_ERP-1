package Plugin_ERP.Bots;

import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.Duration;
import java.util.NoSuchElementException;

import org.openqa.selenium.*;

public class ElmentAction {
    private WebDriver driver;
    private WebDriverWait wait;
    private WaitMangment waitMangment;
    private ValidationActions validationActions;
    private DropdownActions dropdownActions;
    private WaitActions waitActions;
    private CheckboxActions checkboxActions;
    private GridActions gridActions;
    private PriceListActions priceListActions;
    private UtilityActions utilityActions;
    private AlertAction alertAction;

    public ElmentAction(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.waitMangment = new WaitMangment(driver);
        this.validationActions = new ValidationActions(driver, wait, waitMangment);
        this.dropdownActions = new DropdownActions(driver, wait, waitMangment);
        this.waitActions = new WaitActions(driver, wait, waitMangment);
        this.checkboxActions = new CheckboxActions(driver, wait, waitMangment);
        this.gridActions = new GridActions(driver, wait, waitMangment);
        this.priceListActions = new PriceListActions(driver, wait, waitMangment);
        this.utilityActions=new UtilityActions(driver, wait, waitMangment);
        this.alertAction = new AlertAction(driver, wait, waitMangment);
    }

    //Getter
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

    public UtilityActions Utility(){
        return utilityActions;
    }

    public AlertAction alert() {
        return alertAction;
    }

    //Methods


    //Get Property

    public String getProperty(By by, String property) {
        WebElement element = findElement(by);
        String propertyValue = element.getDomProperty(property);
        LogUtils.info(propertyValue);
        return propertyValue;
    }


    //Gtet Attribute
    public String getAttribute(By by, String attribute) {
        WebElement element = findElement(by);
        String attributeValue = element.getDomAttribute(attribute);
        LogUtils.info(attributeValue);
        return attributeValue;
    }

    private void scrollIntoView(WebElement element) {

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );
    }

    private void jsClick(WebElement element) {

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                element
        );
    }

    public void ClickElement(By locator) {

        int attempts = 0;

        while (attempts < 3) {

            try {

                WebElement element = waitMangment
                        .fluentWait()
                        .until(driver -> {

                            WebElement el = driver.findElement(locator);

                            return (el.isDisplayed() && el.isEnabled())
                                    ? el
                                    : null;
                        });

                scrollIntoView(element);

                try {

                    element.click();

                } catch (Exception e) {

                    jsClick(element);
                }

                return;

            } catch (StaleElementReferenceException e) {

                attempts++;

            } catch (Exception e) {

                try {

                    WebElement fallback = driver.findElement(locator);

                    scrollIntoView(fallback);

                    jsClick(fallback);

                    return;

                } catch (Exception ignored) {
                }

                attempts++;
            }
        }

        throw new RuntimeException(
                "❌ Click failed after retries: " + locator
        );
    }

    public void TaypeText(By locator, String value) {

        int attempts = 0;

        while (attempts < 3) {

            try {

                WebElement element = waitMangment
                        .fluentWait()
                        .until(driver -> {

                            WebElement el = driver.findElement(locator);

                            return (el.isDisplayed() && el.isEnabled())
                                    ? el
                                    : null;
                        });

                scrollIntoView(element);

                wait.until(ExpectedConditions.elementToBeClickable(element));

                element.click();

                element.sendKeys(Keys.CONTROL + "a");
                element.sendKeys(Keys.DELETE);

                wait.until(driver -> {

                    try {

                        String currentValue = element.getAttribute("value");

                        return currentValue == null
                                || currentValue.trim().isEmpty();

                    } catch (Exception e) {

                        return false;
                    }

                });

                element.sendKeys(value);
                LogUtils.info(
                        "Typed Value = " + element.getAttribute("value")
                );

                return;

            } catch (StaleElementReferenceException e) {

                attempts++;

            } catch (Exception e) {

                try {

                    WebElement fallback = driver.findElement(locator);

                    scrollIntoView(fallback);

                    wait.until(ExpectedConditions.elementToBeClickable(fallback));

                    fallback.click();

                    fallback.sendKeys(Keys.CONTROL + "a");
                    fallback.sendKeys(Keys.DELETE);

                    wait.until(driver -> {

                        try {

                            String currentValue = fallback.getAttribute("value");

                            return currentValue == null
                                    || currentValue.trim().isEmpty();

                        } catch (Exception ex) {

                            return false;
                        }

                    });

                    fallback.sendKeys(value);

                    return;

                } catch (Exception ignored) {
                }

                attempts++;
            }
        }

        throw new RuntimeException(
                "❌ Type failed after retries: " + locator
        );
    }

    public WebElement findElement(By locator) {
        return driver.findElement(locator);
    }


    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getText(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
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
            }
        }

        throw new NoSuchElementException(
                "❌ No visible element found: " + locator
        );
    }

    public void clickOnEmptyArea() {
        WebElement body = driver.findElement(By.tagName("body"));

        new Actions(driver)
                .moveToElement(body, 5, 5)
                .click()
                .perform();
    }

    public void TaypeTextB(By locator, String value) {

        String cleanValue = value.trim().replaceAll("\\s+", " ");

        int attempts = 0;

        while (attempts < 3) {

            try {

                clickOnEmptyArea();

                WebElement element = waitMangment
                        .fluentWait()
                        .until(driver -> {

                            try {

                                return getVisibleElement(locator);

                            } catch (Exception e) {

                                return null;
                            }
                        });
                LogUtils.info("Displayed = " + element.isDisplayed());
                LogUtils.info("Enabled = " + element.isEnabled());
                LogUtils.info("Location = " + element.getLocation());
                LogUtils.info("Size = " + element.getSize());
                LogUtils.info("OuterHTML = " + element.getAttribute("outerHTML"));

                Object topElement = ((JavascriptExecutor) driver).executeScript(
                        "return document.elementFromPoint(arguments[0], arguments[1]).outerHTML;",
                        element.getRect().getX() + 5,
                        element.getRect().getY() + 5
                );

                LogUtils.info("Top Element = " + topElement);
                scrollIntoView(element);

                wait.until(ExpectedConditions.elementToBeClickable(element));

                element.click();

                element.sendKeys(Keys.CONTROL + "a");
                element.sendKeys(Keys.DELETE);

                wait.until(driver -> {

                    try {

                        String currentValue = element.getAttribute("value");

                        return currentValue == null
                                || currentValue.trim().isEmpty();

                    } catch (Exception e) {

                        return false;
                    }
                });

                element.sendKeys(cleanValue);

                String typedValue = element.getAttribute("value");

                LogUtils.info("Typed Value = " + typedValue);

                if (typedValue == null || !typedValue.trim().equals(cleanValue)) {

                    throw new RuntimeException(
                            "❌ Typed value mismatch. Expected: [" + cleanValue
                                    + "] Actual: [" + typedValue + "]"
                    );
                }

                return;

            } catch (StaleElementReferenceException e) {

                attempts++;

            } catch (Exception e) {

                e.printStackTrace();

                try {

                    clickOnEmptyArea();

                    WebElement fallback = getVisibleElement(locator);

                    scrollIntoView(fallback);

                    wait.until(ExpectedConditions.elementToBeClickable(fallback));

                    fallback.click();

                    fallback.sendKeys(Keys.CONTROL + "a");
                    fallback.sendKeys(Keys.DELETE);

                    wait.until(driver -> {

                        try {

                            String currentValue = fallback.getAttribute("value");

                            return currentValue == null
                                    || currentValue.trim().isEmpty();

                        } catch (Exception ex) {

                            return false;
                        }
                    });

                    fallback.sendKeys(cleanValue);

                    String typedValue = fallback.getAttribute("value");

                    LogUtils.info("Typed Value (fallback) = " + typedValue);

                    if (typedValue != null && typedValue.trim().equals(cleanValue)) {
                        return;
                    }

                    LogUtils.info(
                            "❌ Fallback typed value mismatch. Expected: [" + cleanValue
                                    + "] Actual: [" + typedValue + "]"
                    );

                } catch (Exception ex) {

                    ex.printStackTrace();
                }

                attempts++;
            }
        }

        throw new RuntimeException(
                "❌ Type failed after retries: " + locator
        );
    }

    public By getLastEditButtonForCustomerReceipt() {

        {

            return By.xpath(
                    "(//tr[contains(@id,'mainTable__row_')]//button[@data-action-type='edit'])[1]"
            );
        }

    }

    public By getLastEditButtonForAdjustDamage() {


        return By.xpath("(//*[@data-action-type='edit'])[1]");
    }



    public void ClickPlusButtonByRow(int numberOfRows, int timesPerRow) {
        // استنى لحد ما كل الصفوف المطلوبة تكون موجودة فعليًا في الجدول
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(d -> {
            List<WebElement> rows = d.findElements(By.xpath("//tbody//tr[@role='row' and @data-pk]"));
            return rows.size() >= numberOfRows;
        });

        for (int rowIndex = 1; rowIndex <= numberOfRows; rowIndex++) {
            for (int i = 0; i < timesPerRow; i++) {
                List<WebElement> rows = driver.findElements(By.xpath("//tbody//tr[@role='row' and @data-pk]"));
                WebElement targetRow = rows.get(rowIndex - 1);
                List<WebElement> buttons = targetRow.findElements(By.tagName("button"));
                WebElement plusButton = buttons.get(1); // الزرار التاني = "+"

                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", plusButton);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                plusButton.click();
                LogUtils.info("✅ صف رقم " + rowIndex + " - ضغطة رقم " + (i + 1));
            }
        }
    }

    public void ClickMinusButtonByRow(int numberOfRows, int timesPerRow) {
        // استنى لحد ما كل الصفوف المطلوبة تكون موجودة فعليًا في الجدول
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(d -> {
            List<WebElement> rows = d.findElements(By.xpath("//tbody//tr[@role='row' and @data-pk]"));
            return rows.size() >= numberOfRows;
        });

        for (int rowIndex = 1; rowIndex <= numberOfRows; rowIndex++) {
            for (int i = 0; i < timesPerRow; i++) {
                List<WebElement> rows = driver.findElements(By.xpath("//tbody//tr[@role='row' and @data-pk]"));
                WebElement targetRow = rows.get(rowIndex - 1);
                List<WebElement> buttons = targetRow.findElements(By.tagName("button"));
                WebElement minusButton = buttons.get(2); // الزرار التالت = "-"

                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", minusButton);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                minusButton.click();
                LogUtils.info("✅ صف رقم " + rowIndex + " - ضغطة Minus رقم " + (i + 1));
            }
        }
    }





}

