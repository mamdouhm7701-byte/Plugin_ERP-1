package Plugin_ERP.System_Modules.Login.purches;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Purchase Document Transitions: focused operations used by the original page facade. */
final class PurchaseDocumentTransitions {
    private final PurchaseModulePage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;
    private final WebDriverWait ready;

    PurchaseDocumentTransitions(PurchaseModulePage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
        this.ready = ready;
    }

    void puLinkedReturn(Object invoiceId, String itemName) {
        owner.puOpen(
                "return-purch/new?sourceTransactionId="
                        + invoiceId
                        + "&sourceTransactionType=Purch");
        owner.puFormReady();
        ready.until(d -> owner.puLineCount(itemName) > 0);
    }

    void puConvertOwned(String supplier, String code, boolean toInvoice, String itemName) {
        puConvertOwned(supplier, code, null, toInvoice, itemName);
    }

    void puConvertOwned(
            String supplier,
            String code,
            Object expectedSourceId,
            boolean toInvoice,
            String itemName) {
        owner.puListScope(supplier);
        owner.puListText("filteredCode", code);
        var source =
                ready.until(
                        d -> {
                            var rows =
                                    owner.puListRows().stream()
                                            .filter(r -> code.equals(String.valueOf(r.get("code"))))
                                            .filter(
                                                    r ->
                                                            expectedSourceId == null
                                                                    || expectedSourceId.equals(
                                                                            r.get("id")))
                                            .toList();
                            if (rows.isEmpty()) return null;
                            if (rows.size() != 1)
                                throw new AssertionError(
                                        "Purchase conversion requires a unique source identity");
                            return rows.get(0);
                        });
        Object sourceId = source.get("id");
        String icon = toInvoice ? "repeat" : "refresh-cw";
        owner.puClick(
                By.cssSelector(
                        "tbody tr[data-pk='"
                                + sourceId
                                + "'] button:has([class*='"
                                + icon
                                + "']),tbody tr[id$='__row_"
                                + sourceId
                                + "'] button:has([class*='"
                                + icon
                                + "'])"));
        ready.until(d -> d.getCurrentUrl().contains(toInvoice ? "purch-invoice" : "return-purch"));
        owner.puFormReady();
        ready.until(
                d ->
                        sourceId.equals(owner.puDocument().get("sourceTransactionId"))
                                && owner.puLineCount(itemName) > 0);
        ready.until(
                d ->
                        d.findElements(By.cssSelector("#loading-bg,div.b-overlay")).stream()
                                .noneMatch(WebElement::isDisplayed));
        utilites.waits().waitForOverlayToDisappear();
    }

    void puDeleteOwned(String supplier, String code) {
        owner.puListScope(supplier);
        owner.puListText("filteredCode", code);
        ready.until(
                d ->
                        owner.puListRows().size() == 1
                                && code.equals(
                                        String.valueOf(owner.puListRows().get(0).get("code"))));
        owner.puClick(
                By.xpath(
                        "//table//tbody/tr[.//td[normalize-space()='"
                                + code
                                + "']]//button[@data-action-type='delete']"));
        owner.puClick(By.cssSelector("button.swal2-confirm"));
        owner.puDismiss();
        owner.puWaitListIds(Set.of());
    }

    void puEdit(String route, Object id, String marker, String itemName) {
        owner.puOpen(route + "/edit/" + id);
        ready.until(
                d ->
                        d.findElements(By.cssSelector("form textarea[label-text='Notes']")).stream()
                                        .anyMatch(e -> marker.equals(e.getDomProperty("value")))
                                && owner.puLineCount(itemName) > 0);
    }
}
