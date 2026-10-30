/* شرح الملف: تجهيز حجز وتسليم الخرسانة المشترك بين اختبارات التقارير.
 */
package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import Plugin_ERP.Util.PrintedInvoiceAssertions.Cell;
import Plugin_ERP.Util.VueComponentAccess;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

abstract class ConcreteOwnedReportScenario extends SalesScenarioTest {
    protected static final String ENDPOINT = "ItemTransactions-Reservations";
    protected String captureEndpoint = ENDPOINT;

    protected void prepareReservationReferences() {}

    protected void selectReservationReferences() {}

    protected void selectDeliveryReferences() {}

    protected void saveOwnedPartialDelivery(SalesTestData.Record reservation) {
        captureEndpoint = "ConcreteDelivery";
        page.slOpen("concrete-exchange-order/new");
        driver.navigate().refresh();
        page.slFormReady();
        page.slCapture(marker, data.store(1).id(), item.id(), captureEndpoint);
        page.slChoose("المخزن", data.store(1).name());
        page.slChoose("العميل", customer.name());
        var ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        ready.until(
                        org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        ".modal.show tr[data-pk='"
                                                + reservation.id()
                                                + "'] button")))
                .click();
        var row =
                ready.until(
                        d ->
                                d
                                        .findElements(
                                                By.xpath(
                                                        "//div[contains(@class,'b-sidebar')]//tr[td[contains(.,'"
                                                                + item.name()
                                                                + "')]]"))
                                        .stream()
                                        .filter(WebElement::isDisplayed)
                                        .findFirst()
                                        .orElse(null));
        row.findElement(By.cssSelector("input[type='checkbox']")).sendKeys(Keys.SPACE);
        ready.until(
                d ->
                        d.findElements(By.cssSelector(".modal.show")).stream()
                                .noneMatch(WebElement::isDisplayed));
        ready.until(
                        org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                                By.xpath(
                                        "//tr[td[contains(.,'"
                                                + item.name()
                                                + "')]]//input[@type='number' and not(@disabled)]")))
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), "1", Keys.TAB);
        selectDeliveryReferences();
        page.slNotes(marker);
        page.slSave(false);
        ready.until(
                d -> {
                    data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
                    return !page.slSaves().isEmpty();
                });
        page.slSettled();
        var stored = data.read(capture().getLast());
        Assert.assertEquals(stored.get("sourceTransactionId"), reservation.id());
        Assert.assertEquals(firstLine(stored).get("itemId"), item.id());
        sameNumber(firstLine(stored).get("quantity"), "1");
        ready.until(d -> number(data.balance(item, 1)).compareTo(new BigDecimal("999")) == 0);
        page.slDismiss();
    }

    protected SalesTestData.Record saveOwnedReservation() {
        captureEndpoint = ENDPOINT;
        item =
                data.fixture(
                        "concrete-report-item",
                        "Items",
                        TestSupport.PolicyItemRecipe.create(data.itemValues(), "100", false));
        customer = data.customer();
        agent = data.agent();
        prepareReservationReferences();
        allowedItems.add(item.id());
        page = new SalesModulePage(driver);
        page.slOpen("reservations/new");
        driver.navigate().refresh();
        page.slFormReady();
        BranchProfiles.selected()
                .orElseThrow()
                .verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        page.slCapture(marker, data.store(1).id(), item.id(), ENDPOINT);
        page.slChoose("المخزن", data.store(1).name());
        page.slChoose("العميل", customer.name());
        page.slChoose("مندوب", agent.name());
        selectReservationReferences();
        page.slNotes(marker);
        add(item, "picker");
        page.slQuantity(item.name(), "2");
        page.slWaitNet("230");
        page.slSave(false);
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(
                        d -> {
                            data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
                            return !page.slSaves().isEmpty();
                        });
        page.slSettled();
        var saved = capture().getLast();
        var stored = data.read(saved);
        Assert.assertEquals(stored.get("dealerId"), customer.id());
        Assert.assertEquals(firstLine(stored).get("itemId"), item.id());
        sameNumber(firstLine(stored).get("quantity"), "2");
        TestSupport.DecimalAmountAssertions.amount(stored.get("net"), "230");
        page.slDismiss();
        return saved;
    }

    @SuppressWarnings("unchecked")
    protected List<Cell> renderedCells() {
        var raw =
                (List<Map<String, Object>>)
                        VueComponentAccess.execute(
                                driver,
                                """
            return [...document.querySelectorAll('.e-reportviewer-paragrap,.e-reportviewer-paragrap *')]
              .filter(e=>!e.children.length&&e.getClientRects().length&&e.innerText.trim())
              .map(e=>{let r=e.getBoundingClientRect();return {text:e.innerText.trim(),x:r.x,y:r.y,
                width:r.width,height:r.height,page:e.closest('[id*="_pageDiv"],[id*="_pageContainer"]')?.id||'document'};});
            """);
        Allure.addAttachment(
                "Rendered owned reservation report cells",
                "application/json",
                org.json.simple.JSONValue.toJSONString(raw),
                ".json");
        return raw.stream()
                .map(
                        c ->
                                new Cell(
                                        c.get("text").toString(),
                                        coordinate(c, "x"),
                                        coordinate(c, "y"),
                                        coordinate(c, "width"),
                                        coordinate(c, "height"),
                                        c.get("page").toString()))
                .toList();
    }

    private static double coordinate(Map<String, Object> cell, String key) {
        return ((Number) cell.get(key)).doubleValue();
    }

    @Override
    protected List<SalesTestData.Record> capture() {
        return ActivityDocumentCapture.collect(data, page, marker, captureEndpoint);
    }
}
