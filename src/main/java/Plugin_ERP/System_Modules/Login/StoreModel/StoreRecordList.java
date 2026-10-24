package Plugin_ERP.System_Modules.Login.StoreModel;

import Plugin_ERP.System_Modules.Login.AbstractForStore;
import Plugin_ERP.Utiel.FormInteractions;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Store Record List: focused operations used by the original page facade. */
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
    private Object js(String script, Object... args) { return owner.js(script, args); }

    void smFind(String route, String marker, Object id, Object code) {
        if (!marker.startsWith("QA-ST-") || id == null)
            throw new AssertionError("Refuse unrelated warehouse row");
        ownedId = id;
        ownedMarker = marker;
        owner.smOpen(route);
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
        owner.smType(listSearch, value);
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
                && driver.findElements(ownedRow()).stream().anyMatch(WebElement::isDisplayed);
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
                            owner.js("arguments[0].scrollIntoView({block:'center',inline:'center'})", b);
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
