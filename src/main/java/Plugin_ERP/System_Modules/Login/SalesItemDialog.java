/* شرح الملف: مساعدة للبحث في نافذة إضافة أصناف البيع وعرض الأصناف المختارة. فائدتها الوصول للأصناف
 * المطلوبة ومراجعتها قبل إضافتها.
 */
package Plugin_ERP.System_Modules.Login;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

final class SalesItemDialog {
    private final AbstractForSales owner;
    private final WebDriver driver;

    SalesItemDialog(AbstractForSales owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    void searchInAddItemsDialog(String fieldLabel, String query) {
        By radioOption =
                By.cssSelector(
                        ".modal-content input[type='radio'][value='"
                                + (fieldLabel.equals("الباركود") ? "barcode" : "name")
                                + "']");
        By searchField = By.cssSelector(".modal-content input[placeholder='بحث ....']");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(radioOption)).click();
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));
        input.click();
        input.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        input.sendKeys(org.openqa.selenium.Keys.BACK_SPACE);
        input.sendKeys(query);
        owner.utilites.waits().waitForOverlayToDisappear();

        By updatedResult =
                By.xpath(
                        "//div[contains(@class,'modal-content')]//tbody//tr["
                                + "contains(normalize-space(.), '"
                                + query
                                + "') or "
                                + "contains(normalize-space(.), 'لا يوجد بيانات')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(updatedResult));
    }

    List<String> getAddItemsDialogRows() {
        By rowsLocator = By.cssSelector(".modal-content tbody tr");
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(
                                d -> {
                                    List<WebElement> rows =
                                            d.findElements(rowsLocator).stream()
                                                    .filter(WebElement::isDisplayed)
                                                    .collect(Collectors.toList());
                                    return rows.isEmpty() ? null : rows;
                                })
                        .stream()
                        .map(WebElement::getText)
                        .map(String::trim)
                        .filter(text -> !text.isEmpty() && !text.contains("لا يوجد بيانات"))
                        .collect(Collectors.toList());
    }

    void setShowSelectedItems(boolean shouldBeChecked) {
        owner.utilites
                .checkbox()
                .stableFastSetCheckboxByText("عرض الأصناف المختارة", shouldBeChecked);
    }
}
