package Plugin_ERP.Bots;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PriceListActions extends BaseAction {

    public PriceListActions(WebDriver driver, WebDriverWait wait, WaitMangment waitMangment) {
        super(driver, wait, waitMangment);
    }

    private List<String> selectedProducts = new ArrayList<>();
    private List<String> enteredPrices = new ArrayList<>();

    public List<String> getSelectedProducts() {
        return selectedProducts;
    }

    public List<String> getEnteredPrices() {
        return enteredPrices;
    }

    public void enterPriceByRowIndex(Object... rowsAndPrices) {

        enteredPrices.clear();

        for (int i = 0; i < rowsAndPrices.length; i += 2) {

            int rowIndex = (int) rowsAndPrices[i];
            String price = rowsAndPrices[i + 1].toString();

            enteredPrices.add(price);

            WebElement priceInput =
                    driver.findElement(
                            By.xpath(
                                    "(//td[@aria-colindex='4']//input[@type='number'])["
                                            + rowIndex
                                            + "]"));

            priceInput.click();
            priceInput.sendKeys(Keys.CONTROL + "a");
            priceInput.sendKeys(price);
        }
    }

    public void selectUsersByCodeFprPriceList(int... codes) {

        selectedProducts.clear();

        for (int code : codes) {

            By productNameLocator =
                    By.xpath("//tr[@aria-rowindex='" + code + "']/td[@aria-colindex='2']");

            WebElement productCell =
                    wait.until(ExpectedConditions.visibilityOfElementLocated(productNameLocator));

            String productName = productCell.getText().trim();

            selectedProducts.add(productName);

            By buttonLocator =
                    By.xpath("//tr[@aria-rowindex='" + code + "']//td[@aria-colindex='5']//button");

            wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));

            driver.findElement(buttonLocator).click();
        }
    }

    public By getLastEditButtonForPriceList() {
        return By.id("invoice-row-" + getLastRowPk() + "-preview-icon");
    }

    public void deleteRowForPriceList() {

        int attempts = 0;
        String deletedPk = getLastRowPk();
        ;

        while (attempts < 3) {

            try {
                By deletedRow = By.xpath("//tr[@data-pk='" + deletedPk + "']");
                String deleteIconId = "invoice-row-" + deletedPk + "-delete-icon";

                System.out.println("🗑️ Start deleting row with pk: " + deletedPk);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

                // ✅ انتظر الصف يكون موجود في الـ DOM
                wait.until(ExpectedConditions.presenceOfElementLocated(By.id(deleteIconId)));

                // ✅ اضغط بـ JavaScript لأن SVG صغير مش بيتعرف عليه Selenium
                WebElement deleteIcon = driver.findElement(By.id(deleteIconId));
                ((JavascriptExecutor) driver)
                        .executeScript(
                                "arguments[0].dispatchEvent(new MouseEvent('click', {bubbles: true}));",
                                deleteIcon);

                // ✅ انتظر زرار التأكيد "نعم"
                By confirmBtn = By.xpath("//button[contains(.,'نعم')]");
                wait.until(ExpectedConditions.elementToBeClickable(confirmBtn));
                ClickElement(confirmBtn);

                // ✅ انتظر الصف يختفي
                long startTime = System.currentTimeMillis();
                wait.until(ExpectedConditions.numberOfElementsToBe(deletedRow, 0));
                long endTime = System.currentTimeMillis();

                System.out.println("✅ Row deleted successfully");
                System.out.println(
                        "⏱️ Delete wait time: " + ((endTime - startTime) / 1000.0) + " seconds");

                Assert.assertTrue(
                        driver.findElements(deletedRow).isEmpty(),
                        "❌ الصف لم يُحذف! data-pk: " + deletedPk);

                return;

            } catch (StaleElementReferenceException e) {
                attempts++;
                System.out.println("⚠️ Stale element - Retry: " + attempts);

            } catch (Exception e) {
                attempts++;
                System.out.println("⚠️ Delete failed - Retry: " + attempts);
                System.out.println("❗ Exception: " + e.getMessage());

                if (attempts == 3) {
                    throw new RuntimeException("❌ Failed to delete row after retries", e);
                }
            }
        }
    }

    public String getLastRowPk() {
        return waitForRowPk(By.xpath("//tr[@role='row' and @aria-rowindex='1']"));
    }

    public void ClickElement(By locator) {

        int attempts = 0;

        while (attempts < 3) {

            try {

                WebElement element =
                        waitMangment
                                .fluentWait()
                                .until(
                                        driver -> {
                                            WebElement el = driver.findElement(locator);

                                            return (el.isDisplayed() && el.isEnabled()) ? el : null;
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

        throw new RuntimeException("❌ Click failed after retries: " + locator);
    }
}
