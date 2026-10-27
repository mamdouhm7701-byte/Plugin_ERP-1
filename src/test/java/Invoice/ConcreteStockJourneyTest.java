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

/** Concrete stock moves on delivery; billing alone must not consume stock. */
public final class ConcreteStockJourneyTest extends SalesScenarioTest {
    private String endpoint;

    @Test
    public void deliveryConsumesOwnedStockWhenSaved() {
        check(true);
    }

    @Test
    public void directConcreteInvoiceDoesNotConsumeStock() {
        check(false);
    }

    @Test
    public void reservationLeavesStockUnchangedAndAppearsInDeliveryPicker() {
        var profile = prepareOwnedData();
        var reservation = createOwnedReservation(profile);
        var stored = data.read(reservation);
        sameNumber(data.balance(item, 1), "1000");
        openOwnedForm("concrete-exchange-order/new", "ConcreteDelivery");
        page.slButton("الحجوزات");
        new WebDriverWait(driver, Duration.ofSeconds(35))
                .until(
                        d ->
                                d
                                        .findElements(
                                                org.openqa.selenium.By.cssSelector(".modal.show"))
                                        .stream()
                                        .anyMatch(org.openqa.selenium.WebElement::isDisplayed));
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                Boolean.TRUE.equals(
                                        Plugin_ERP.Util.VueComponentAccess.execute(
                                                d,
                                                """
                    const r=erpVue.root().$router.currentRoute,vm=r.matched.at(-1).instances.default;
                    return Array.isArray(vm.allData)&&vm.allData.some(row=>String(row.id)===String(arguments[0]));
                    """,
                                                reservation.id())));
        Allure.addAttachment(
                "Owned reservation in delivery picker",
                "text/html",
                page.slDiagnosticMarkup(),
                ".html");
        Object source =
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver,
                        """
            const r=erpVue.root().$router.currentRoute,vm=r.matched.at(-1).instances.default;
            return {rows:vm.allData?.filter(row=>String(row.id)===String(arguments[0])),keys:Object.keys(vm.$data||{}),
              methods:Object.fromEntries(Object.entries(vm.$options.methods||{}).filter(([key])=>/res|row|line|import|add/i.test(key)).map(([key,value])=>[key,String(value)]))};
            """,
                        reservation.id());
        Allure.addAttachment(
                "Owned reservation source identity",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "storedReservation",
                                        stored,
                                        "picker",
                                        source,
                                        "stockBefore",
                                        1000,
                                        "stockAfterReservation",
                                        data.balance(item, 1)))
                        .toString(2),
                ".json");
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
        page.slChoose("مندوب", agent.name());
        page.slNotes(marker);
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

    private void check(boolean delivery) {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(profile.expected().activityType(), ActivityType.CONCRETE);
        endpoint = delivery ? "ConcreteDelivery" : "ItemTransactions-ConcreteSales";
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
        page.slOpen(delivery ? "concrete-exchange-order/new" : "concrete-sales-invoice/new");
        driver.navigate().refresh();
        page.slFormReady();
        page.slCapture(marker, data.store(1).id(), item.id(), endpoint);
        page.slChoose("المخزن", data.store(1).name());
        page.slChoose("العميل", customer.name());
        page.slChoose("مندوب", agent.name());
        if (!delivery) page.slChoose("نوع السداد", "آجل");
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
        if (!delivery) page.slWaitNet(expected.getNet().toPlainString());
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
        if (!delivery) {
            Assert.assertEquals(stored.get("dealerId"), customer.id());
            sameNumber(stored.get("netBeforeTaxes"), expected.getBeforeTax().toPlainString());
            sameNumber(stored.get("totalTaxes"), expected.getTax().toPlainString());
            sameNumber(stored.get("net"), expected.getNet().toPlainString());
        }
        BigDecimal expectedStock = delivery ? stockBefore.subtract(BigDecimal.ONE) : stockBefore;
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
