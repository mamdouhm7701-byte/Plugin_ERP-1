package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.*;
import Plugin_ERP.System_Modules.Login.Coverage.ScreenPage;
import Plugin_ERP.Util.PrintedInvoiceAssertions.Cell;
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
public final class ConcreteReservationReportTest extends ConcreteOwnedReportScenario {

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
}
