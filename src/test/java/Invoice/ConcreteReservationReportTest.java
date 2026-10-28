package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import Plugin_ERP.Util.PrintedInvoiceAssertions.Cell;
import Plugin_ERP.Util.VueComponentAccess;
import TestSupport.ConcreteReservationReportAssertions;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

/** Reconciles the rendered reservation row and grand total against an owned native transaction. */
public final class ConcreteReservationReportTest extends SalesScenarioTest {
    private static final String ENDPOINT = "ItemTransactions-Reservations";
    private String captureEndpoint = ENDPOINT;

    @DataProvider
    public Object[][] previews() {
        return new Object[][] {{"استعراض تفصيلي"}, {"استعراض اجمالي"}};
    }

    @Test(dataProvider = "previews")
    public void renderedReservationMatchesOwnedQuantityAndValue(String previewButton) {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(profile.expected().activityType(), ActivityType.CONCRETE);
        Assert.assertEquals(profile.expected().taxPolicy(), TaxPolicy.NET_BASED);
        var reservation = saveOwnedReservation();
        verifyPreview(reservation, previewButton, 0);
        sameNumber(data.balance(item, 1), "1000");
        saveOwnedPartialDelivery(reservation);
        verifyPreview(reservation, previewButton, 1);
        sameNumber(data.balance(item, 1), "999");
    }

    private void verifyPreview(
            SalesTestData.Record reservation, String previewButton, int consumed) {
        var screen = new ScreenPage(driver);
        screen.open("concrete-reports", "تقارير الخرسانة");
        screen.click(
                By.xpath(
                        "//li[contains(@class,'nav-item-report')][.//span[normalize-space(.)='تقرير الحجوزات']]"));
        screen.choose("العميل", customer.name());
        Set<String> previous = new HashSet<>(driver.getWindowHandles());
        String original = driver.getWindowHandle();
        try {
            screen.click(previewButton);
            String preview =
                    new WebDriverWait(driver, Duration.ofSeconds(30))
                            .until(
                                    d ->
                                            d.getWindowHandles().stream()
                                                    .filter(w -> !previous.contains(w))
                                                    .findFirst()
                                                    .orElse(null));
            driver.switchTo().window(preview);
            new WebDriverWait(driver, Duration.ofSeconds(60))
                    .until(
                            d ->
                                    d
                                            .findElements(
                                                    By.cssSelector(".e-reportviewer-paragrap"))
                                            .stream()
                                            .anyMatch(e -> e.getText().contains(item.name())));
            List<Cell> cells = renderedCells();
            Allure.addAttachment(
                    "Owned concrete reservation report expectation",
                    "application/json",
                    new org.json.JSONObject(
                                    Map.of(
                                            "reservationId",
                                            reservation.id(),
                                            "item",
                                            item.name(),
                                            "customer",
                                            customer.name(),
                                            "quantity",
                                            2,
                                            "price",
                                            100,
                                            "valueBeforeVat",
                                            200,
                                            "consumedQuantity",
                                            consumed,
                                            "remainingQuantity",
                                            2 - consumed,
                                            "preview",
                                            previewButton))
                            .toString(2),
                    ".json");
            Assert.assertTrue(
                    cells.stream().anyMatch(c -> c.text().equals(customer.name())),
                    "Rendered report must retain the selected owned customer");
            ConcreteReservationReportAssertions.verify(
                    cells,
                    item.name(),
                    new BigDecimal("2"),
                    new BigDecimal("100"),
                    BigDecimal.valueOf(consumed));
        } finally {
            for (String window : new HashSet<>(driver.getWindowHandles())) {
                if (!previous.contains(window)) {
                    driver.switchTo().window(window);
                    driver.close();
                }
            }
            driver.switchTo().window(original);
        }
    }

    private void saveOwnedPartialDelivery(SalesTestData.Record reservation) {
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

    private SalesTestData.Record saveOwnedReservation() {
        captureEndpoint = ENDPOINT;
        item =
                data.fixture(
                        "concrete-report-item",
                        "Items",
                        TestSupport.PolicyItemRecipe.create(data.itemValues(), "100", false));
        customer = data.customer();
        agent = data.agent();
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
        sameNumber(stored.get("net"), "230");
        page.slDismiss();
        return saved;
    }

    @SuppressWarnings("unchecked")
    private List<Cell> renderedCells() {
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
