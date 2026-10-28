/* شرح الملف: اختبارات تقارير السيارة والسائق والمشروع تطابق إذن التسليم: تقارير السيارة والسائق
 * والمشروع تطابق إذن التسليم.
 */
package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.Util.VueComponentAccess;
import TestSupport.BasicDataFixtures;
import TestSupport.ConcreteOperationalReportAssertions;
import TestSupport.ConcreteOperationalReportAssertions.Kind;
import TestSupport.FixtureSession;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

public final class ConcreteOperationalReportTest extends ConcreteOwnedReportScenario {
    private BasicDataFixtures masters;
    private BasicDataFixtures.Record project, mixer, car;

    @Test
    public void ownedDeliveryReportsKeepQuantityIdentityAndCountsAfterBilling() {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(
                profile.expected().activityType(), BranchPolicies.ActivityType.CONCRETE);
        Assert.assertEquals(profile.expected().taxPolicy(), BranchPolicies.TaxPolicy.NET_BASED);
        Assert.assertTrue(
                profile.name().matches("business-[a-f0-9]{16}"),
                "Aggregate report checks require a fresh Business run branch");
        masters = new BasicDataFixtures(driver);
        project = masters.fixture("operational-project", "Projects", Map.of());
        String serial = Long.toString(System.nanoTime());
        car =
                masters.fixture(
                        "operational-mixer",
                        "Cars",
                        Map.of(
                                "carType",
                                "Mixer",
                                "plateNumber",
                                "7" + serial.substring(7, 13),
                                "formExpireDate",
                                LocalDate.now().plusYears(1).toString(),
                                "insuranceExpireDate",
                                LocalDate.now().plusYears(1).toString()));
        mixer =
                masters.fixture(
                        "operational-driver",
                        "Drivers",
                        Map.of(
                                "identityNumber",
                                "2" + serial.substring(4, 13),
                                "type",
                                "MixerDriver",
                                "carId",
                                car.id()));
        var reservation = saveOwnedReservation();
        Assert.assertEquals(data.read(reservation).get("projectId"), project.id());
        sameNumber(data.balance(item, 1), "1000");
        saveOwnedPartialDelivery(reservation);
        var delivery = capture().getLast();
        var stored = data.read(delivery);
        Assert.assertEquals(stored.get("carId"), car.id());
        Assert.assertEquals(stored.get("mixerDriverId"), mixer.id());
        Assert.assertEquals(stored.get("projectId"), project.id());
        saveInvoice(delivery);
        var expected =
                new ConcreteOperationalReportAssertions.Expected(
                        item.name(),
                        customer.name(),
                        project.name(),
                        car.name(),
                        mixer.name(),
                        stored.get("code").toString(),
                        BigDecimal.ONE,
                        BigDecimal.ONE);
        Allure.addAttachment(
                "Independent operational report expectation",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "reservationId",
                                        reservation.id(),
                                        "deliveryId",
                                        delivery.id(),
                                        "deliveryCode",
                                        stored.get("code"),
                                        "reservedQuantity",
                                        2,
                                        "deliveredQuantity",
                                        1,
                                        "deliveryCount",
                                        1,
                                        "item",
                                        item.name(),
                                        "project",
                                        project.name(),
                                        "customer",
                                        customer.name()))
                        .toString(2),
                ".json");
        var failures = new ArrayList<String>();
        for (Kind kind : Kind.values()) {
            try {
                verifyPreview(kind, expected);
            } catch (RuntimeException | AssertionError failure) {
                failures.add(kind + ": " + failure.getMessage());
                Allure.addAttachment("Operational report failure " + kind, failure.toString());
            }
        }
        if (!failures.isEmpty()) throw new AssertionError(String.join("\n", failures));
        sameNumber(data.balance(item, 1), "999");
    }

    @Override
    protected void prepareReservationReferences() {
        var linked = data.read(customer);
        Assert.assertEquals(linked.get("id"), customer.id());
        Assert.assertEquals(linked.get("arabicName"), customer.name());
        linked.put(
                "projects",
                List.of(
                        Map.of(
                                "projectId",
                                project.id(),
                                "arabicName",
                                project.name(),
                                "englishName",
                                project.name())));
        var session = new FixtureSession(driver);
        new SalesFixtureApi(driver, session.origin, session.branchId, session::verify)
                .call("update", "Customers", linked);
    }

    @Override
    protected void selectReservationReferences() {
        page.slChoose(translated("project"), project.name());
    }

    @Override
    protected void selectDeliveryReferences() {
        page.slChoose(translated("mixerDriver"), mixer.name());
    }

    private String translated(String key) {
        return VueComponentAccess.execute(driver, "return erpVue.root().$t(arguments[0])", key)
                .toString()
                .trim();
    }

    private void saveInvoice(SalesTestData.Record delivery) {
        captureEndpoint = "ItemTransactions-ConcreteSales";
        page.slOpen("concrete-sales-invoice/new");
        driver.navigate().refresh();
        page.slFormReady();
        page.slCapture(marker, data.store(1).id(), item.id(), captureEndpoint);
        page.slChoose("المخزن", data.store(1).name());
        page.slChoose("العميل", customer.name());
        page.slChoose("مندوب", agent.name());
        page.slChoose(translated("project"), project.name());
        page.slChoose("نوع السداد", "آجل");
        ConcreteInvoiceControls.selectOwnedDelivery(driver, page, delivery, item);
        page.slNotes(marker);
        page.slWaitNet("115");
        page.slSave(false);
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(
                        d -> {
                            data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
                            return !page.slSaves().isEmpty();
                        });
        page.slSettled();
        var invoice = capture().getLast();
        var stored = data.read(invoice);
        Assert.assertEquals(stored.get("projectId"), project.id());
        Assert.assertEquals(
                invoice.values().get("concreteDeliveryOrderIdsList"), List.of(delivery.id()));
        sameNumber(stored.get("net"), "115");
        sameNumber(data.balance(item, 1), "999");
        page.slDismiss();
    }

    private void verifyPreview(Kind kind, ConcreteOperationalReportAssertions.Expected expected) {
        String report =
                switch (kind) {
                    case CARS -> "carsTransactionReport";
                    case DRIVERS -> "driversTransactionReport";
                    default -> "customersProjectsReport";
                };
        String button =
                kind == Kind.PROJECT_DETAIL
                        ? "استعراض تفصيلي"
                        : kind == Kind.PROJECT_TOTAL ? "استعراض اجمالي" : "استعراض";
        var screen = new ScreenPage(driver);
        screen.open("concrete-reports", "تقارير الخرسانة");
        screen.click(
                By.xpath(
                        "//li[contains(@class,'nav-item-report')][.//span[normalize-space(.)="
                                + ScreenPage.literal(translated(report))
                                + "]]"));
        if (kind == Kind.CARS || kind == Kind.DRIVERS)
            screen.click(By.xpath("//label[normalize-space(.)='إذن تسليم خرسانة']"));
        String previous = driver.getWindowHandle();
        var handles = new HashSet<>(driver.getWindowHandles());
        try {
            screen.click(button);
            String preview =
                    new WebDriverWait(driver, Duration.ofSeconds(30))
                            .until(
                                    d ->
                                            d.getWindowHandles().stream()
                                                    .filter(w -> !handles.contains(w))
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
            var cells = renderedCells();
            ConcreteOperationalReportAssertions.verify(kind, cells, expected);
            Allure.addAttachment(
                    "Operational report accepted " + kind,
                    "Owned row, delivered quantity1 and applicable totals/counts reconciled independently.");
        } finally {
            for (String window : new HashSet<>(driver.getWindowHandles()))
                if (!handles.contains(window)) {
                    driver.switchTo().window(window);
                    driver.close();
                }
            driver.switchTo().window(previous);
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanupOperationalMasters() {
        if (masters != null) {
            if (data != null) data.close();
            masters.close();
        }
    }
}
