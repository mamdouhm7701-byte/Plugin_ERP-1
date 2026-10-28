/* شرح الملف: مساعدة للبحث في سجلات الأصناف وفتح السجل للتعديل أو الحذف. فائدتها الوصول للصنف
 * المقصود ومراجعة وجوده بعد التسجيل.
 */
package Plugin_ERP.System_Modules.Login.Products;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class ProductRecordList {
    private final ProductsModulePage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;

    ProductRecordList(ProductsModulePage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    private void prClick(By locator) {
        owner.clickRecordControl(locator);
    }

    private List<WebElement> prNameInputs() {
        return owner.recordNameInputs();
    }

    void prSearch(String name) {
        By locator =
                By.xpath(
                        "//input[@id='arabicName' or @name='arabicNamePart' or contains(@placeholder,'بحث') or (@type='search' and not(contains(@class,'vs__search')))] | //*[self::label or self::legend][normalize-space()='الاسم العربي']/following::input[1]");
        ready.until(
                d -> {
                    if (d.findElements(locator).stream().anyMatch(WebElement::isDisplayed))
                        return true;
                    for (WebElement b :
                            d.findElements(
                                    By.xpath(
                                            "//button[contains(normalize-space(),'عرض التصفية')]")))
                        if (b.isDisplayed()) {
                            b.click();
                            break;
                        }
                    return false;
                });
        owner.prType(locator, name);
    }

    boolean prHasRow(String name) {
        return driver.findElements(By.cssSelector("tbody tr")).stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(
                        r ->
                                r.findElements(By.cssSelector("td")).stream()
                                        .anyMatch(c -> c.getText().trim().equals(name)));
    }

    void prWaitRow(String name) {
        ready.until(d -> owner.prHasRow(name));
    }

    void prEdit(String name) {
        prRowAction(name, "edit");
        // The form mounts before its API response; typing earlier is overwritten by hydration.
        ready.until(
                d ->
                        prNameInputs().stream()
                                .anyMatch(
                                        e ->
                                                e.isDisplayed()
                                                        && e.isEnabled()
                                                        && name.equals(e.getDomProperty("value"))));
    }

    void prDelete(String name) {
        prRowAction(name, "delete");
        owner.visible(By.cssSelector("button.swal2-confirm"));
    }

    void prRowAction(String name, String action) {
        ready.until(
                d -> {
                    WebElement row =
                            d.findElements(By.cssSelector("tbody tr")).stream()
                                    .filter(WebElement::isDisplayed)
                                    .filter(
                                            r ->
                                                    r.findElements(By.cssSelector("td")).stream()
                                                            .anyMatch(
                                                                    c ->
                                                                            c.getText()
                                                                                    .trim()
                                                                                    .equals(name)))
                                    .findFirst()
                                    .orElse(null);
                    if (row == null) return false;
                    List<WebElement> buttons =
                            row.findElements(
                                    By.cssSelector(
                                            "[id*='"
                                                    + action
                                                    + "'],[data-action-type='"
                                                    + action
                                                    + "']"
                                                    + (action.equals("edit")
                                                            ? ",[id$='-preview-icon']"
                                                            : "")));
                    for (WebElement b : buttons) {
                        ((JavascriptExecutor) d)
                                .executeScript(
                                        "arguments[0].scrollIntoView({block:'center',inline:'center'})",
                                        b);
                        if (b.isDisplayed()) {
                            b.click();
                            return true;
                        }
                    }
                    return false;
                });
    }
}
