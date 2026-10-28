/* شرح الملف: اختيار قيمة محددة من قائمة الشاشة مع التأكد من عدم اختيار اسم آخر.
 */
package Plugin_ERP.Bots;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

final class VueDropdownSelection {
    private final DropdownActions owner;
    private final WebDriver driver;
    private final WebDriverWait wait;

    VueDropdownSelection(DropdownActions owner, WebDriver driver, WebDriverWait wait) {
        this.owner = owner;
        this.driver = driver;
        this.wait = wait;
    }

    void selectVueOptionboth(String labelText, String optionText) {

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
                    Plugin_ERP.Bots.LogUtils.debug(
                            "VueDropdownSelection fallback: " + ex.getClass().getSimpleName());

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
                    Plugin_ERP.Bots.LogUtils.debug(
                            "DropdownActions fallback: " + ignored.getClass().getSimpleName());
                }

                if (attempt < 5) {
                    wait.until(ExpectedConditions.elementToBeClickable(searchInputBy));
                }
            }
        }

        throw new RuntimeException(
                "Couldn't select option [" + cleanOptionText + "] after 5 attempts.",
                lastException);
    }

    void selectVueOptionByDataTitle(String title, String optionText) {

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
                    Plugin_ERP.Bots.LogUtils.debug(
                            "VueDropdownSelection fallback: " + ex.getClass().getSimpleName());

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
                    Plugin_ERP.Bots.LogUtils.debug(
                            "DropdownActions fallback: " + ignored.getClass().getSimpleName());
                }

                if (attempt < 5) {
                    wait.until(ExpectedConditions.elementToBeClickable(inputBy));
                }
            }
        }

        throw new RuntimeException(
                "Couldn't select option [" + cleanOptionText + "] after 5 attempts.",
                lastException);
    }

    void selectVueOptionLeft(String labelText, String optionText) {

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
                    Plugin_ERP.Bots.LogUtils.debug(
                            "VueDropdownSelection fallback: " + ex.getClass().getSimpleName());

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
                    Plugin_ERP.Bots.LogUtils.debug(
                            "DropdownActions fallback: " + ignored.getClass().getSimpleName());
                }

                if (attempt < 5) {
                    wait.until(ExpectedConditions.elementToBeClickable(searchInputBy));
                }
            }
        }

        throw new RuntimeException(
                "Couldn't select option [" + cleanOptionText + "] after 5 attempts.",
                lastException);
    }

    private void clickOnEmptyArea() {
        owner.clickOnEmptyArea();
    }

    private void typeAndVerify(WebElement input, String value) {
        owner.typeAndVerify(input, value);
    }
}
