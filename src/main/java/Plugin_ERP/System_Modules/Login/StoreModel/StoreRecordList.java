/* شرح الملف: مساعدة للبحث عن مستند مخزني وقراءة بياناته وحذفه وتصدير القائمة. فائدتها متابعة حركات
 * المخزون المسجلة والوصول للمستند المطلوب.
 */
package Plugin_ERP.System_Modules.Login.StoreModel;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class StoreRecordList {
    private final StoreModulePage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;

    StoreRecordList(StoreModulePage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    private Object ownedId;
    private String ownedMarker;
    private By listSearch;

    private Object js(String script, Object... args) {
        return owner.js(script, args);
    }

    void smFind(String route, String marker, Object id, Object code) {
        if (!marker.startsWith("QA-ST-") || id == null)
            throw new AssertionError("Refuse unrelated warehouse row");
        ownedId = id;
        ownedMarker = marker;
        owner.smOpen(route);
        if (route.equals("damage-adjustment")) {
            // This screen has no search/filter control. Locate the owned identity on its pages.
            listSearch = null;
            // Bootstrap initially renders a 1000-row pagination placeholder. Do not click its
            // last page until real records (with identities) have reached the DOM.
            ready.until(
                    d ->
                            d
                                    .findElements(
                                            By.cssSelector(
                                                    "tbody tr[data-pk],tbody tr[id*='__row_'],tbody tr:has([id*='invoice-row-'])"))
                                    .stream()
                                    .anyMatch(WebElement::isDisplayed));
            while (!smHasDocument(marker)) {
                var previous =
                        driver.findElements(By.cssSelector("tbody tr")).stream()
                                .map(WebElement::getText)
                                .toList();
                ready.until(
                        d -> {
                            if (smHasDocument(marker)) return true;
                            var next =
                                    d
                                            .findElements(
                                                    By.cssSelector(
                                                            ".pagination .page-item:last-child button"))
                                            .stream()
                                            .filter(WebElement::isDisplayed)
                                            .filter(WebElement::isEnabled)
                                            .findFirst()
                                            .orElseThrow(
                                                    () ->
                                                            new AssertionError(
                                                                    "Owned damage document not found: "
                                                                            + id));
                            next.click();
                            return true;
                        });
                if (smHasDocument(marker)) break;
                ready.until(
                        d ->
                                !d.findElements(By.cssSelector("tbody tr")).stream()
                                        .map(WebElement::getText)
                                        .toList()
                                        .equals(previous));
            }
            return;
        }
        By search =
                By.cssSelector(
                        route.equals("damage-adjustment")
                                ? "input[placeholder*='بحث']"
                                : "input#code,input[name='fCode']");
        listSearch = search;
        ready.until(
                d -> {
                    if (d.findElements(search).stream().anyMatch(WebElement::isDisplayed))
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
        owner.smType(search, route.equals("damage-adjustment") ? marker : code.toString());
        ready.until(d -> owner.smHasDocument(marker));
    }

    void smSearch(String value) {
        if (listSearch == null)
            throw new IllegalStateException("This warehouse list has no search control");
        owner.smType(listSearch, value);
    }

    boolean smHasSearch() {
        return listSearch != null;
    }

    By ownedRow() {
        return By.cssSelector(
                "tbody tr[data-pk='"
                        + ownedId
                        + "'],tbody tr[id$='__row_"
                        + ownedId
                        + "'],tbody tr:has([id*='invoice-row-"
                        + ownedId
                        + "-'])");
    }

    boolean smHasDocument(String marker) {
        return marker.equals(ownedMarker)
                && Plugin_ERP.Util.StableDomRead.read(
                        driver,
                        () ->
                                driver.findElements(ownedRow()).stream()
                                        .anyMatch(WebElement::isDisplayed));
    }

    void smDeleteOwn(String marker, boolean confirm) {
        if (!marker.startsWith("QA-ST-") || !marker.equals(ownedMarker) || ownedId == null)
            throw new AssertionError("Refuse unrelated document deletion");
        ready.until(
                d -> {
                    for (WebElement row : d.findElements(ownedRow()))
                        for (WebElement b :
                                row.findElements(
                                        By.cssSelector(
                                                "[data-action-type='delete'],[id*='-delete-']"))) {
                            owner.js(
                                    "arguments[0].scrollIntoView({block:'center',inline:'center'})",
                                    b);
                            if (b.isDisplayed() && b.isEnabled()) {
                                b.click();
                                return true;
                            }
                        }
                    return false;
                });
        owner.smClick(By.cssSelector(confirm ? "button.swal2-confirm" : "button.swal2-cancel"));
        ready.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".swal2-container")));
    }

    List<String> smHeaders() {
        return driver.findElements(By.cssSelector("thead th")).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .toList();
    }

    void smExport(String format) {
        owner.smClick(
                By.cssSelector(
                        "button:has(img[src*='"
                                + (format.equals("pdf") ? "pdf." : "xls.")
                                + "'])"));
    }
}
