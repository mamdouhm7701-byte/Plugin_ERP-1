/* شرح الملف: مساعدة لاختيار صنفين من الأصناف الموجودة أثناء تجهيز مستند. فائدتها تكوين بنود المستند
 * من الخيارات المتاحة.
 */
package Plugin_ERP.System_Modules.Login;

import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class DraftItemSelection {
    private DraftItemSelection() {}

    public static List<String> selectTwo(
            WebDriver driver, WebDriverWait ready, Runnable openPicker) {
        openPicker.run();
        var buttons =
                ready.until(
                        d -> {
                            var found =
                                    d
                                            .findElements(
                                                    By.xpath(
                                                            "//div[contains(@class,'modal') and contains(@class,'show')]//tbody//button[.//*[local-name()='svg' and contains(@class,'feather-plus')]]"))
                                            .stream()
                                            .filter(WebElement::isDisplayed)
                                            .toList();
                            return found.size() >= 2 ? found : null;
                        });
        buttons.get(0).click();
        buttons.get(1).click();
        // This is the picker confirmation, not the document save button.
        driver
                .findElements(
                        By.xpath(
                                "//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='حفظ']"))
                .stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow()
                .click();
        ready.until(
                d ->
                        d.findElements(By.cssSelector(".modal.show")).stream()
                                .noneMatch(WebElement::isDisplayed));
        String index =
                driver.findElements(By.cssSelector("form thead th")).stream()
                        .filter(e -> e.getDomProperty("textContent").trim().equals("الاسم"))
                        .findFirst()
                        .orElseThrow()
                        .getDomAttribute("aria-colindex");
        var names =
                driver
                        .findElements(
                                By.cssSelector("form tbody tr td[aria-colindex='" + index + "']"))
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .map(e -> e.getText().trim())
                        .filter(name -> !name.isEmpty())
                        .distinct()
                        .toList();
        if (names.size() != 2)
            throw new IllegalStateException("Draft requires two distinct existing items");
        return names;
    }
}
