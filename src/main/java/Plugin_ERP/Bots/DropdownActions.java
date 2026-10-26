package Plugin_ERP.Bots;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DropdownActions extends BaseAction {

    public DropdownActions(WebDriver driver, WebDriverWait wait, WaitMangment waitMangment) {
        super(driver, wait, waitMangment);
    }

    public void selectUsersByCode(int... codes) {
        for (int code : codes) {
            By add = By.xpath("//div[contains(@class,'modal') and contains(@class,'show')]"
                    + "//td[normalize-space()='" + code
                    + "']/following-sibling::td//button[@data-action-type='save']");
            wait.ignoring(StaleElementReferenceException.class)
                    .ignoring(ElementClickInterceptedException.class).until(d -> {
                        for (WebElement button : d.findElements(add)) {
                            if (!button.isDisplayed() || !button.isEnabled()) continue;
                            scrollIntoView(button);
                            button.click();
                            return true;
                        }
                        return false;
                    });
        }
    }

    public void typeAndVerify(WebElement input, String expectedText) {

        String cleanExpected = expectedText.trim().replaceAll("\\s+", " ");

        for (int i = 1; i <= 3; i++) {

            input.sendKeys(Keys.CONTROL + "a");
            input.sendKeys(Keys.DELETE);

            wait.until(ExpectedConditions.attributeToBe(input, "value", ""));

            input.sendKeys(cleanExpected);

            try {
                wait.until(ExpectedConditions.attributeToBe(input, "value", cleanExpected));
            } catch (TimeoutException retry) {
                // Keep the bounded typing retry for inputs that replace their value.
            }

            String actualValue = input.getAttribute("value");

            if (actualValue != null && actualValue.trim().equals(cleanExpected)) {
                return; // القيمة مطابقة، اتأكدنا
            }

            System.out.println(
                    "Verify Failed (try "
                            + i
                            + "/3) - Expected: ["
                            + cleanExpected
                            + "] Actual: ["
                            + actualValue
                            + "]");
        }

        throw new RuntimeException(
                "Typed value does not match expected text after verification retries: ["
                        + cleanExpected
                        + "]");
    }

    public void selectVueOptionboth(String labelText, String optionText) {

        String cleanOptionText = optionText.trim().replaceAll("\\s+", " ");

        String base = "//*[self::legend or self::label][normalize-space()='" + labelText + "']";

        By searchInputBy = By.xpath(base + "/following::input[contains(@class,'vs__search')][1]");

        By optionBy =
                By.xpath(
                        "//ul[contains(@id,'__listbox')]//li[normalize-space()='"
                                + cleanOptionText
                                + "']");

        Exception lastException = null;

        for (int attempt = 1; attempt <= 5; attempt++) {

            System.out.println("Attempt " + attempt + "/5 : Selecting [" + cleanOptionText + "]");

            try {

                clickOnEmptyArea();

                WebElement searchInput =
                        wait.until(ExpectedConditions.elementToBeClickable(searchInputBy));

                ((JavascriptExecutor) driver)
                        .executeScript(
                                "arguments[0].scrollIntoView({block:'center'});", searchInput);

                searchInput.click();

                typeAndVerify(searchInput, cleanOptionText);

                WebDriverWait retryWait = new WebDriverWait(driver, Duration.ofSeconds(10));

                WebElement option =
                        retryWait.until(ExpectedConditions.elementToBeClickable(optionBy));

                try {
                    option.click();
                } catch (ElementClickInterceptedException ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
                }

                System.out.println("Selected Successfully : " + cleanOptionText);

                return;

            } catch (Exception e) {

                lastException = e;

                System.out.println(
                        "Attempt "
                                + attempt
                                + " Failed : "
                                + e.getClass().getSimpleName()
                                + " - "
                                + e.getMessage());

                try {
                    clickOnEmptyArea();
                    WebElement searchInput = driver.findElement(searchInputBy);
                    searchInput.sendKeys(Keys.CONTROL + "a");
                    searchInput.sendKeys(Keys.DELETE);
                } catch (Exception ignored) {
                }

                if (attempt < 5) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Dropdown selection retry interrupted", ex);
                    }
                }
            }
        }

        throw new RuntimeException(
                "Couldn't select option [" + cleanOptionText + "] after 5 attempts.",
                lastException);
    }

    public void selectVueOptionByDataTitle(String title, String optionText) {

        String cleanOptionText = optionText.trim().replaceAll("\\s+", " ");

        By inputBy =
                By.xpath(
                        "//div[@data-title='"
                                + title
                                + "']//input[contains(@class,'vs__search') and not(@disabled)]");

        By optionBy =
                By.xpath(
                        "//ul[contains(@id,'__listbox')]//li[normalize-space()='"
                                + cleanOptionText
                                + "']");

        Exception lastException = null;

        for (int attempt = 1; attempt <= 5; attempt++) {

            System.out.println("Attempt " + attempt + "/5 : Selecting [" + cleanOptionText + "]");

            try {

                clickOnEmptyArea();

                WebElement input = wait.until(ExpectedConditions.elementToBeClickable(inputBy));

                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block:'center'});", input);

                input.click();

                typeAndVerify(input, cleanOptionText);

                WebDriverWait retryWait = new WebDriverWait(driver, Duration.ofSeconds(10));

                WebElement option =
                        retryWait.until(ExpectedConditions.elementToBeClickable(optionBy));

                try {
                    option.click();
                } catch (ElementClickInterceptedException ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
                }

                System.out.println("Selected Successfully : " + cleanOptionText);

                return;

            } catch (Exception e) {

                lastException = e;

                System.out.println(
                        "Attempt "
                                + attempt
                                + " Failed : "
                                + e.getClass().getSimpleName()
                                + " - "
                                + e.getMessage());

                try {
                    clickOnEmptyArea();
                    WebElement input = driver.findElement(inputBy);
                    input.sendKeys(Keys.CONTROL + "a");
                    input.sendKeys(Keys.DELETE);
                } catch (Exception ignored) {
                }

                if (attempt < 5) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Dropdown selection retry interrupted", ex);
                    }
                }
            }
        }

        throw new RuntimeException(
                "Couldn't select option [" + cleanOptionText + "] after 5 attempts.",
                lastException);
    }

    public void selectVueMultiOption(String label, String[] values) {

        By toggleLocator =
                By.xpath(
                        "//label[normalize-space()='"
                                + label
                                + "']"
                                + "/following::div[contains(@class,'vs__dropdown-toggle')][1]");

        clickOnEmptyArea();

        WebElement toggle = wait.until(ExpectedConditions.elementToBeClickable(toggleLocator));
        toggle.click();

        By inputLocator =
                By.xpath(
                        "//label[normalize-space()='"
                                + label
                                + "']"
                                + "/following::input[contains(@class,'vs__search')][1]");

        for (String rawValue : values) {

            String optionValue = rawValue.trim().replaceAll("\\s+", " ");

            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(inputLocator));

            typeAndVerify(input, optionValue);

            By optionLocator =
                    By.xpath(
                            "//li[contains(@class,'vs__dropdown-option')][normalize-space()='"
                                    + optionValue
                                    + "']");

            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            option.click();

            By selectedValueLocator =
                    By.xpath(
                            "//label[normalize-space()='"
                                    + label
                                    + "']"
                                    + "/following::div[contains(@class,'vs__selected')]"
                                    + "//*[normalize-space()='"
                                    + optionValue
                                    + "']");

            wait.until(ExpectedConditions.visibilityOfElementLocated(selectedValueLocator));
        }
    }

    public void selectTreasuryHandoverDropdown(String label, String value) {

        String cleanValue = value.trim().replaceAll("\\s+", " ");

        try {

            LogUtils.info("========== START DROPDOWN SELECTION ==========");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            clickOnEmptyArea();

            String dropdownXpath = "(//div[@data-title='" + label + "'])[last()]";

            WebElement dropdown =
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dropdownXpath)));

            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView(true);", dropdown);

            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dropdownXpath)));

            try {
                dropdown.click();
            } catch (Exception e) {

                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
            }

            LogUtils.info("Dropdown Opened Successfully");

            WebElement input =
                    wait.until(
                            ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath(
                                            "(//div[@data-title='"
                                                    + label
                                                    + "']//input)[last()]")));

            typeAndVerify(input, cleanValue);

            LogUtils.info("Value Typed And Verified : " + cleanValue);

            wait.until(d -> dropdown.findElements(By.cssSelector("[role='option'], .vs__dropdown-option"))
                    .stream().anyMatch(option -> option.isDisplayed()
                            && option.getText().trim().replaceAll("\\s+", " ").equals(cleanValue)));

            input.sendKeys(Keys.ENTER);

            LogUtils.info("OPTION SELECTED SUCCESSFULLY");

        } catch (Exception e) {

            LogUtils.error("========== DROPDOWN FAILED ==========");
            LogUtils.error("Failure Reason : " + e.getMessage());

            throw new RuntimeException("Unable to select value from dropdown: " + label);
        }
    }

    public void selectVueOptionLeft(String labelText, String optionText) {

        String cleanOptionText = optionText.trim().replaceAll("\\s+", " ");

        By radioLabelBy =
                By.xpath(
                        "(//div[@role='radiogroup'])[2]//span[normalize-space()='"
                                + labelText
                                + "']");

        By searchInputBy =
                By.xpath(
                        "(//div[@role='radiogroup'])[2]/following::input[contains(@class,'vs__search')][1]");

        By optionBy = By.xpath("//li[normalize-space()='" + cleanOptionText + "']");

        Exception lastException = null;

        for (int attempt = 1; attempt <= 5; attempt++) {

            System.out.println("Attempt " + attempt + "/5 : Selecting [" + cleanOptionText + "]");

            try {

                clickOnEmptyArea();

                WebElement radioLabel =
                        wait.until(ExpectedConditions.elementToBeClickable(radioLabelBy));

                ((JavascriptExecutor) driver)
                        .executeScript(
                                "arguments[0].scrollIntoView({block:'center'});", radioLabel);

                radioLabel.click();

                WebElement searchInput =
                        wait.until(ExpectedConditions.elementToBeClickable(searchInputBy));

                searchInput.click();

                typeAndVerify(searchInput, cleanOptionText);

                WebDriverWait retryWait = new WebDriverWait(driver, Duration.ofSeconds(10));

                WebElement option =
                        retryWait.until(ExpectedConditions.elementToBeClickable(optionBy));

                try {
                    option.click();
                } catch (ElementClickInterceptedException ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
                }

                System.out.println("Selected Successfully : " + cleanOptionText);

                return;

            } catch (Exception e) {

                lastException = e;

                System.out.println(
                        "Attempt "
                                + attempt
                                + " Failed : "
                                + e.getClass().getSimpleName()
                                + " - "
                                + e.getMessage());

                try {
                    clickOnEmptyArea();
                    WebElement searchInput = driver.findElement(searchInputBy);
                    searchInput.sendKeys(Keys.CONTROL + "a");
                    searchInput.sendKeys(Keys.DELETE);
                } catch (Exception ignored) {
                }

                if (attempt < 5) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Dropdown selection retry interrupted", ex);
                    }
                }
            }
        }

        throw new RuntimeException(
                "Couldn't select option [" + cleanOptionText + "] after 5 attempts.",
                lastException);
    }

    public void selectProductByName(String productName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector("div.position-absolute.bg-light.rounded-lg")));

        WebElement plusButton =
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                ExpectedConditions.elementToBeClickable(
                                        By.xpath(
                                                "//tr[.//td[normalize-space()='"
                                                        + productName
                                                        + "']]//button[contains(@class,'btn-outline-primary')]")));

        plusButton.click();
    }

    public void selectFirstProducts(int count) {

        int startRow = 3;

        for (int i = startRow; i < startRow + count; i++) {

            By addButton =
                    By.xpath(
                            "(//tbody/tr)["
                                    + i
                                    + "]//button[contains(@class,'btn-outline-primary')]");

            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(addButton));

            button.click();
        }
    }
}
