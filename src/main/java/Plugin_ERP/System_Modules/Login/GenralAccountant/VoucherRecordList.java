package Plugin_ERP.System_Modules.Login.GenralAccountant;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Voucher Record List: focused operations used by the original page facade. */
final class VoucherRecordList {
    private final VouchersPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;
    private final Plugin_ERP.Bots.WaitManagement waitManagement;

    VoucherRecordList(VouchersPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
        this.waitManagement = owner.context().waits();
    }

    boolean hasVoucherMarkerInList(String marker) {
        return driver.findElements(By.xpath("//tr[@role='row' and @data-pk]")).stream()
                .anyMatch(row -> row.getText().contains(marker));
    }

    boolean hasVoucherMarkerPrefixInList(String markerPrefix) {
        return driver.findElements(By.xpath("//tr[@role='row' and @data-pk]")).stream()
                .anyMatch(row -> row.getText().contains(markerPrefix));
    }

    List<String> getVoucherRowsContaining(String marker) {
        return driver.findElements(By.xpath("//tr[@role='row' and @data-pk]")).stream()
                .map(WebElement::getText)
                .filter(text -> text.contains(marker))
                .toList();
    }

    void deleteVoucherRowsContaining(String markerPrefix) {
        List<WebElement> matches =
                driver.findElements(By.xpath("//tr[@role='row' and @data-pk]")).stream()
                        .filter(row -> row.getText().contains(markerPrefix))
                        .toList();
        for (WebElement match : matches) {
            String pk = match.getAttribute("data-pk");
            utilites.grid()
                    .deleteRowByPk(
                            pk,
                            By.xpath(
                                    "//tr[@data-pk='"
                                            + pk
                                            + "']//button[@data-action-type='delete']"),
                            owner.Clickyestodelet);
            waitManagement
                    .fluentWait()
                    .until(d -> d.findElements(By.xpath("//tr[@data-pk='" + pk + "']")).isEmpty());
        }
    }

    void editVoucherListRowByMarker(String marker) {
        By editButton =
                By.xpath(
                        "//tr[@role='row' and @aria-rowindex='1' and contains(.,'"
                                + marker
                                + "')]//button[@data-action-type='edit']");
        utilites.ClickElement(editButton);
        waitManagement
                .fluentWait()
                .until(d -> !d.findElements(By.cssSelector("form #textarea")).isEmpty());
        waitManagement
                .fluentWait()
                .until(
                        d ->
                                marker.equals(
                                        d.findElement(By.id("textarea")).getDomProperty("value")));
    }

    void deleteVoucherListRowByMarker(String marker) {
        By row =
                By.xpath(
                        "//tr[@role='row' and @aria-rowindex='1' and contains(.,'"
                                + marker
                                + "')]");
        String pk = driver.findElement(row).getAttribute("data-pk");
        utilites.grid()
                .deleteRowByPk(
                        pk,
                        By.xpath("//tr[@data-pk='" + pk + "']//button[@data-action-type='delete']"),
                        owner.Clickyestodelet);
        waitManagement
                .fluentWait()
                .until(d -> d.findElements(By.xpath("//tr[@data-pk='" + pk + "']")).isEmpty());
    }
}
