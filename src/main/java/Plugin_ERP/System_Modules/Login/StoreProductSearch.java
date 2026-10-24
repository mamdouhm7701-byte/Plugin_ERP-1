package Plugin_ERP.System_Modules.Login;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Product autocomplete and the legacy store and invoice selection waits. */
final class StoreProductSearch {
    private final DocumentPage owner;
    private final WebDriver driver;
    StoreProductSearch(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }
    private final By SearchInput = By.cssSelector("input[placeholder='بحث ....']");

    By getSuggestionItem(String name) {
        return By.xpath("//span[@class='my-suggestion-item' and contains(text(), '" + name + "')]");
    }

    void SearchAndSelectProduct(String productName) {
        owner.utilites.waits().waitForOverlayToDisappear();

        int maxRetries = 3;
        boolean found = false;

        for (int attempt = 1; attempt <= maxRetries && !found; attempt++) {
            try {
                // امسح الحقل الأول قبل أي محاولة (حتى الأولى، احتياطًا)
                SearchInputElement().clear();

                owner.utilites.TaypeTextB(SearchInput, productName);
                owner.utilites.ClickElement(getSuggestionItem(productName));
                owner.utilites.waits().waitForOverlayToDisappear();

                found = waitForProductToAppearInTable(productName, 5);

            } catch (Exception e) {
                System.out.println("⚠️ المحاولة " + attempt + " فشلت بسبب: " + e.getMessage());
                found = false;
            }

            if (!found) {
                System.out.println("🔁 إعادة المحاولة رقم " + (attempt + 1) + " للصنف: " + productName);
            }
        }

        if (!found) {
            throw new RuntimeException("❌ فشل إضافة الصنف بعد " + maxRetries + " محاولات: " + productName);
        }
    }

    void searchAndSelectInvoiceProduct(String productName) {
        owner.utilites.waits().waitForOverlayToDisappear();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(SearchInput)
        );
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        searchInput.sendKeys(Keys.BACK_SPACE);
        searchInput.sendKeys(productName);

        By suggestion = getSuggestionItem(productName);
        WebElement suggestionElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(suggestion)
        );
        suggestionElement.click();

        // Wait until the clicked suggestion is consumed, rather than clicking
        // it again when the grid's row markup differs from other screens.
        wait.until(ExpectedConditions.stalenessOf(suggestionElement));
        wait.until(d -> {
            String value = d.findElement(SearchInput).getAttribute("value");
            return value == null || value.isBlank();
        });
    }

    WebElement SearchInputElement() {
        return driver.findElement(SearchInput);
    }

    boolean waitForProductToAppearInTable(String productName, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(d -> {
                List<WebElement> matches = d.findElements(
                        By.xpath("//tbody//tr[@role='row']//td[contains(text(), '" + productName + "')]")
                );
                return !matches.isEmpty();
            });
            System.out.println("✅ تم التأكد من إضافة الصنف: " + productName);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
