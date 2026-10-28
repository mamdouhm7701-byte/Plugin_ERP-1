/* شرح الملف: اختبارات إذن تسليم الخرسانة هو الذي يخصم المخزون: إذن تسليم الخرسانة هو الذي يخصم
 * المخزون؛ فاتورة الخرسانة المباشرة لا تخصم المخزون؛ حجز الخرسانة لا يخصم المخزون ويظهر للتسليم.
 */
package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType;
import Plugin_ERP.Business.Calculator.*;
import Plugin_ERP.Models.*;
import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ConcreteStockJourneyTest extends SalesScenarioTest {
    private String endpoint;

    @Test
    public void deliveryConsumesOwnedStockWhenSaved() {
        var profile = prepareOwnedData();
        var reservation = createOwnedReservation(profile);
        sameNumber(data.balance(item, 1), "1000");
        openOwnedForm("concrete-exchange-order/new", "ConcreteDelivery");
        var ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        ready.until(
                        org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                                org.openqa.selenium.By.cssSelector(
                                        ".modal.show tr[data-pk='"
                                                + reservation.id()
                                                + "'] button")))
                .click();
        var row =
                ready.until(
                        d ->
                                d
                                        .findElements(
                                                org.openqa.selenium.By.xpath(
                                                        "//div[contains(@class,'b-sidebar')]//tr[td[contains(.,'"
                                                                + item.name()
                                                                + "')]]"))
                                        .stream()
                                        .filter(org.openqa.selenium.WebElement::isDisplayed)
                                        .findFirst()
                                        .orElse(null));
        Allure.addAttachment(
                "Owned reservation item selection",
                "text/html",
                page.slDiagnosticMarkup(),
                ".html");
        var selection =
                row.findElement(org.openqa.selenium.By.cssSelector("input[type='checkbox']"));
        selection.sendKeys(org.openqa.selenium.Keys.SPACE);
        ready.until(
                d ->
                        d.findElements(org.openqa.selenium.By.cssSelector(".modal.show")).stream()
                                .noneMatch(org.openqa.selenium.WebElement::isDisplayed));
        Assert.assertEquals(page.slDocument().get("storeId"), data.store(1).id());
        Assert.assertEquals(page.slDocument().get("agentId"), agent.id());
        var deliveryQuantity =
                ready.until(
                        org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                                org.openqa.selenium.By.xpath(
                                        "//tr[td[contains(.,'"
                                                + item.name()
                                                + "')]]//input[@type='number' and not(@disabled)]")));
        deliveryQuantity.sendKeys(
                org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"),
                "1",
                org.openqa.selenium.Keys.TAB);
        page.slNotes(marker);
        var delivery = saveActivityDocument();
        var stored = data.read(delivery);
        Assert.assertEquals(stored.get("sourceTransactionId"), reservation.id());
        Assert.assertEquals(stored.get("notes"), marker);
        Assert.assertEquals(stored.get("dealerId"), customer.id());
        Assert.assertEquals(stored.get("storeId"), data.store(1).id());
        var line = firstLine(stored);
        Assert.assertEquals(line.get("itemId"), item.id());
        sameNumber(line.get("quantity"), "1");
        ready.withMessage("Concrete delivery must consume exactly its own quantity")
                .until(d -> number(data.balance(item, 1)).compareTo(new BigDecimal("999")) == 0);
        Allure.addAttachment(
                "Concrete stock after owned delivery",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "reservationId",
                                        reservation.id(),
                                        "delivery",
                                        stored,
                                        "stockBefore",
                                        1000,
                                        "stockAfter",
                                        data.balance(item, 1)))
                        .toString(2),
                ".json");
        page.slDismiss();
        invoiceOwnedDeliveryWithoutSecondStockDeduction(delivery, profile);
    }

    private void invoiceOwnedDeliveryWithoutSecondStockDeduction(
            SalesTestData.Record delivery, BranchProfile profile) {
        openOwnedForm("concrete-sales-invoice/new", "ItemTransactions-ConcreteSales");
        page.slChoose("نوع السداد", "آجل");
        ConcreteInvoiceControls.selectOwnedDelivery(driver, page, delivery, item);
        var expected =
                BranchInvoiceCalculator.calculate(
                        List.of(
                                new PolicyInvoiceLine(
                                        "concrete-delivery",
                                        BigDecimal.ONE,
                                        new BigDecimal("100"),
                                        BigDecimal.ZERO,
                                        DiscountType.VALUE,
                                        false)),
                        profile.expected().taxPolicy(),
                        2,
                        BranchInvoiceCalculator.TaxRounding.INVOICE);
        page.slWaitNet(expected.getNet().toPlainString());
        page.slNotes(marker);
        var invoice = saveActivityDocument();
        var stored = data.read(invoice);
        Assert.assertEquals(
                invoice.values().get("concreteDeliveryOrderIdsList"), List.of(delivery.id()));
        Assert.assertEquals(stored.get("dealerId"), customer.id());
        Assert.assertEquals(stored.get("storeId"), data.store(1).id());
        Assert.assertEquals(stored.get("agentId"), agent.id());
        Assert.assertEquals(stored.get("notes"), marker);
        Assert.assertEquals(firstLine(stored).get("itemId"), item.id());
        Assert.assertEquals(firstLine(stored).get("unitId"), data.unit(1).id());
        sameNumber(firstLine(stored).get("quantity"), "1");
        sameNumber(stored.get("net"), expected.getNet().toPlainString());
        sameNumber(stored.get("totalTaxes"), expected.getTax().toPlainString());
        sameNumber(data.balance(item, 1), "999");
        Allure.addAttachment(
                "Saved delivery billing availability",
                "application/json",
                new org.json.JSONObject(
                                data.lookup(
                                        "ConcreteDelivery/ConcreteDeliveryOrders?pageNumber=1&pageSize=500&transactionType=ConcreteDelivery&dealerId="
                                                + customer.id()))
                        .toString(2),
                ".json");
        Allure.addAttachment(
                "Concrete invoice after delivery keeps stock unchanged",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "deliveryId",
                                        delivery.id(),
                                        "invoice",
                                        stored,
                                        "stockAfterDelivery",
                                        999,
                                        "stockAfterInvoice",
                                        data.balance(item, 1)))
                        .toString(2),
                ".json");
        page.slDismiss();
        page.slEdit("concrete-sales-invoice", invoice.id(), marker, item.name());
        Assert.assertEquals(page.slDocument().get("id"), invoice.id());
        Assert.assertEquals(page.slDocument().get("dealerId"), customer.id());
        var available =
                data.lookup(
                        "ConcreteDelivery/ConcreteDeliveryOrders?pageNumber=1&pageSize=500&transactionType=ConcreteDelivery&dealerId="
                                + customer.id());
        Assert.assertTrue(
                available.get("pageData") instanceof List<?>,
                "Missing server billing-availability rows");
        Assert.assertTrue(
                ((List<?>) available.get("pageData")).isEmpty(),
                "The owned delivery is still available for billing after invoice save");
        sameNumber(available.get("totalRecords"), "0");
        Allure.addAttachment(
                "Reopened invoice and server billing relationship",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "invoiceId",
                                        invoice.id(),
                                        "deliveryId",
                                        delivery.id(),
                                        "sentDeliveryIds",
                                        invoice.values().get("concreteDeliveryOrderIdsList"),
                                        "reopenedInvoice",
                                        page.slDocument(),
                                        "serverAvailableDeliveries",
                                        available))
                        .toString(2),
                ".json");
    }

    @Test
    public void directConcreteInvoiceDoesNotConsumeStock() {
        checkDirectInvoice();
    }

    @Test
    public void reservationLeavesStockUnchangedAndAppearsInDeliveryPicker() {
        var profile = prepareOwnedData();
        var reservation = createOwnedReservation(profile);
        var stored = data.read(reservation);
        sameNumber(data.balance(item, 1), "1000");
        openOwnedForm("concrete-exchange-order/new", "ConcreteDelivery");
        ConcreteReservationEvidence.verify(driver, page, data, item, reservation, stored);
    }

    private BranchProfile prepareOwnedData() {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(profile.expected().activityType(), ActivityType.CONCRETE);
        item =
                data.fixture(
                        "concrete-stock-item",
                        "Items",
                        TestSupport.PolicyItemRecipe.create(data.itemValues(), "100", false));
        customer = data.customer();
        agent = data.agent();
        allowedItems.add(item.id());
        sameNumber(data.balance(item, 1), "1000");
        return profile;
    }

    private void openOwnedForm(String route, String documentEndpoint) {
        endpoint = documentEndpoint;
        page = new SalesModulePage(driver);
        page.slOpen(route);
        driver.navigate().refresh();
        page.slFormReady();
        BranchProfiles.selected()
                .orElseThrow()
                .verify(BranchProfileReader.capture(driver, Duration.ofSeconds(30)));
        page.slCapture(marker, data.store(1).id(), item.id(), endpoint);
        page.slChoose("المخزن", data.store(1).name());
        page.slChoose("العميل", customer.name());
        if (!documentEndpoint.equals("ConcreteDelivery")) {
            page.slChoose("مندوب", agent.name());
            page.slNotes(marker);
        }
    }

    private SalesTestData.Record createOwnedReservation(BranchProfile profile) {
        openOwnedForm("reservations/new", "ItemTransactions-Reservations");
        add(item, "picker");
        page.slQuantity(item.name(), "2");
        var expected =
                BranchInvoiceCalculator.calculate(
                        List.of(
                                new PolicyInvoiceLine(
                                        "reservation",
                                        new BigDecimal("2"),
                                        new BigDecimal("100"),
                                        BigDecimal.ZERO,
                                        DiscountType.VALUE,
                                        false)),
                        profile.expected().taxPolicy(),
                        2,
                        BranchInvoiceCalculator.TaxRounding.INVOICE);
        page.slWaitNet(expected.getNet().toPlainString());
        var record = saveActivityDocument();
        var stored = data.read(record);
        Assert.assertEquals(stored.get("notes"), marker);
        Assert.assertEquals(stored.get("dealerId"), customer.id());
        Assert.assertEquals(stored.get("storeId"), data.store(1).id());
        sameNumber(stored.get("net"), expected.getNet().toPlainString());
        page.slDismiss();
        return record;
    }

    private SalesTestData.Record saveActivityDocument() {
        page.slSave(false);
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .withMessage("Owned concrete document save was not confirmed")
                .until(
                        d -> {
                            data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
                            return !page.slSaves().isEmpty();
                        });
        page.slSettled();
        return capture().getLast();
    }

    private void checkDirectInvoice() {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(profile.expected().activityType(), ActivityType.CONCRETE);
        endpoint = "ItemTransactions-ConcreteSales";
        item =
                data.fixture(
                        "concrete-stock-item",
                        "Items",
                        TestSupport.PolicyItemRecipe.create(data.itemValues(), "100", false));
        customer = data.customer();
        agent = data.agent();
        allowedItems.add(item.id());
        BigDecimal stockBefore = number(data.balance(item, 1));
        sameNumber(stockBefore, "1000");
        var customerBefore = data.read(customer);
        page = new SalesModulePage(driver);
        page.slOpen("concrete-sales-invoice/new");
        driver.navigate().refresh();
        page.slFormReady();
        page.slCapture(marker, data.store(1).id(), item.id(), endpoint);
        page.slChoose("المخزن", data.store(1).name());
        page.slChoose("العميل", customer.name());
        page.slChoose("مندوب", agent.name());
        page.slChoose("نوع السداد", "آجل");
        page.slNotes(marker);
        add(item, "picker");
        page.slQuantity(item.name(), "1");
        var expected =
                BranchInvoiceCalculator.calculate(
                        List.of(
                                new PolicyInvoiceLine(
                                        "concrete",
                                        BigDecimal.ONE,
                                        new BigDecimal("100"),
                                        BigDecimal.ZERO,
                                        DiscountType.VALUE,
                                        false)),
                        profile.expected().taxPolicy(),
                        2,
                        BranchInvoiceCalculator.TaxRounding.INVOICE);
        page.slWaitNet(expected.getNet().toPlainString());
        page.slSave(false);
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .withMessage("Concrete save was not confirmed; attempts retained for recovery")
                .until(
                        d -> {
                            data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
                            return !page.slSaves().isEmpty();
                        });
        page.slSettled();
        var saved = capture().getLast();
        var stored = data.read(saved);
        Assert.assertEquals(stored.get("notes"), marker);
        Assert.assertEquals(stored.get("storeId"), data.store(1).id());
        var lines = (List<?>) stored.get("lines");
        Assert.assertEquals(lines.size(), 1);
        var line = (Map<?, ?>) lines.getFirst();
        Assert.assertEquals(line.get("itemId"), item.id());
        Assert.assertEquals(line.get("unitId"), data.unit(1).id());
        sameNumber(line.get("quantity"), "1");
        {
            Assert.assertEquals(stored.get("dealerId"), customer.id());
            sameNumber(stored.get("netBeforeTaxes"), expected.getBeforeTax().toPlainString());
            sameNumber(stored.get("totalTaxes"), expected.getTax().toPlainString());
            sameNumber(stored.get("net"), expected.getNet().toPlainString());
        }
        BigDecimal expectedStock = stockBefore;
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> number(data.balance(item, 1)).compareTo(expectedStock) == 0);
        Allure.addAttachment(
                "Concrete delivery versus billing stock contract",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "profile",
                                        profile.expected().fields(),
                                        "endpoint",
                                        endpoint,
                                        "stored",
                                        stored,
                                        "stockBefore",
                                        stockBefore,
                                        "expectedStockAfter",
                                        expectedStock,
                                        "actualStockAfter",
                                        data.balance(item, 1),
                                        "customerBefore",
                                        customerBefore,
                                        "customerAfter",
                                        data.read(customer)))
                        .toString(2),
                ".json");
        page.slDismiss();
    }

    @Override
    protected List<SalesTestData.Record> capture() {
        return ActivityDocumentCapture.collect(data, page, marker, endpoint);
    }
}
