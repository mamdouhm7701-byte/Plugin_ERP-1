package Invoice;

import Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType;
import Plugin_ERP.BranchProfiles.BranchProfiles;
import Plugin_ERP.Business.Calculator.BranchInvoiceCalculator;
import Plugin_ERP.Business.Calculator.PolicyInvoiceLine;
import Plugin_ERP.Models.DiscountType;
import Plugin_ERP.Util.FormInteractions;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** A showroom sale consumes one exact chassis received through an owned purchase. */
public final class CarShowroomSalesTest extends SalesScenarioTest {
    private SalesTestData.Record ownedPurchase;
    private String activeEndpoint = "ItemTransactions-Sales";

    private record ChassisSale(
            SalesTestData.Record invoice,
            SalesTestData.Record car,
            Object colorId,
            String serial) {}

    @DataProvider
    public Object[][] discountModes() {
        return new Object[][] {{false}, {true}};
    }

    @Test(dataProvider = "discountModes")
    public void sellsOwnedChassisAndPreservesItsColorAndStockIdentity(boolean discounted) {
        sellOwnedChassis(discounted);
    }

    private ChassisSale sellOwnedChassis(boolean discounted) {
        activeEndpoint = "ItemTransactions-Sales";
        ownedPurchase = null;
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(profile.expected().activityType(), ActivityType.CAR_SHOWROOM);
        var color = data.fixture("showroom-color", "CarColors", SalesTestData.fields());
        var car =
                data.fixture(
                        "showroom-car",
                        "Items",
                        TestSupport.PolicyItemRecipe.create(data.itemValues(), "100", false));
        String serial =
                "QA" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 15);
        var purchase = CarShowroomReceipt.create(data, car, color, serial);
        ownedPurchase = purchase;
        var received = data.read(purchase);
        Assert.assertEquals(firstLine(received).get("serial"), serial);
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .withMessage("Owned chassis receipt must add exactly one unit")
                .until(d -> number(data.balance(car, 1)).compareTo(new BigDecimal("1001")) == 0);
        prepare("Credit");
        allowedItems.add(car.id());
        page.slCapture(marker, data.store(1).id(), car.id(), "ItemTransactions-Sales");
        add(car, "picker");
        chooseLineValue(car.name(), "رقم الشاسية", serial);
        BigDecimal reduction = discounted ? new BigDecimal("10") : BigDecimal.ZERO;
        if (discounted) {
            if (profile.expected().discountLevel()
                    == Plugin_ERP.BranchProfiles.BranchPolicies.DiscountLevel.ITEMS)
                page.slSetLine(car.name(), "الخصم", reduction.toPlainString());
            else page.slDiscount(reduction.toPlainString(), false);
        }
        var expected =
                BranchInvoiceCalculator.calculate(
                        List.of(
                                new PolicyInvoiceLine(
                                        "owned-chassis",
                                        BigDecimal.ONE,
                                        new BigDecimal("100"),
                                        reduction,
                                        DiscountType.VALUE,
                                        false)),
                        profile.expected().taxPolicy(),
                        2,
                        BranchInvoiceCalculator.TaxRounding.INVOICE);
        page.slWaitNet(expected.getNet().toPlainString());
        var invoice = save(false);
        var stored = data.read(invoice);
        var line = firstLine(stored);
        Assert.assertEquals(line.get("itemId"), car.id());
        Assert.assertEquals(line.get("serial"), serial);
        Assert.assertEquals(line.get("carColorId"), color.id());
        sameNumber(line.get("quantity"), "1");
        sameNumber(stored.get("net"), expected.getNet().toPlainString());
        sameNumber(stored.get("totalTaxes"), expected.getTax().toPlainString());
        sameNumber(data.balance(car, 1), "1000");
        Allure.addAttachment(
                "Owned showroom receipt and chassis sale",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "purchase",
                                        received,
                                        "invoice",
                                        stored,
                                        "serial",
                                        serial,
                                        "colorId",
                                        color.id(),
                                        "stockAfterReceipt",
                                        1001,
                                        "stockAfterSale",
                                        data.balance(car, 1)))
                        .toString(2),
                ".json");
        return new ChassisSale(invoice, car, color.id(), serial);
    }

    @Test
    public void linkedReturnRestoresExactSoldChassisColorAndStock() {
        var sold = sellOwnedChassis(false);
        activeEndpoint = "ItemTransactions-ReturnSales";
        page.slLinkedReturn(sold.invoice().id(), sold.car().name());
        page.slCapture(marker, data.store(1).id(), sold.car().id(), activeEndpoint);
        page.slNotes(marker);
        Assert.assertEquals(page.slDocument().get("sourceTransactionId"), sold.invoice().id());
        var sourceLine = firstLine(page.slDocument());
        Assert.assertEquals(sourceLine.get("serial"), sold.serial());
        Assert.assertEquals(sourceLine.get("carColorId"), sold.colorId());
        var expected =
                BranchInvoiceCalculator.calculate(
                        List.of(
                                new PolicyInvoiceLine(
                                        "returned-chassis",
                                        BigDecimal.ONE,
                                        new BigDecimal("100"),
                                        BigDecimal.ZERO,
                                        DiscountType.VALUE,
                                        false)),
                        BranchProfiles.selected().orElseThrow().expected().taxPolicy(),
                        2,
                        BranchInvoiceCalculator.TaxRounding.INVOICE);
        page.slWaitNet(expected.getNet().toPlainString());
        data.rememberAttempts(
                List.of(
                        SalesTestData.fields(
                                "endpoint", activeEndpoint, "values", page.slDocument())),
                List.of(),
                marker);
        page.slSave(false);
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(
                        d -> {
                            capture();
                            return !page.slSaves().isEmpty();
                        });
        var returned = capture().getLast();
        var stored = data.read(returned);
        var line = firstLine(stored);
        Assert.assertEquals(stored.get("sourceTransactionId"), sold.invoice().id());
        Assert.assertEquals(line.get("itemId"), sold.car().id());
        Assert.assertEquals(line.get("serial"), sold.serial());
        Assert.assertEquals(line.get("carColorId"), sold.colorId());
        sameNumber(line.get("quantity"), "1");
        sameNumber(stored.get("totalTaxes"), expected.getTax().toPlainString());
        sameNumber(stored.get("net"), expected.getNet().toPlainString());
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                number(data.balance(sold.car(), 1))
                                                .compareTo(new BigDecimal("1001"))
                                        == 0);
        Allure.addAttachment(
                "Owned showroom linked chassis return",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "sourceId",
                                        sold.invoice().id(),
                                        "serial",
                                        sold.serial(),
                                        "colorId",
                                        sold.colorId(),
                                        "return",
                                        stored,
                                        "stockAfterReturn",
                                        data.balance(sold.car(), 1)))
                        .toString(2),
                ".json");
        page.slDismiss();
    }

    @Override
    protected List<SalesTestData.Record> capture() {
        if (activeEndpoint.equals("ItemTransactions-ReturnSales"))
            return ActivityDocumentCapture.collect(data, page, marker, activeEndpoint);
        var records = super.capture();
        if (ownedPurchase != null)
            for (var record : records) {
                var dependencies = new java.util.LinkedHashMap<>(record.values());
                dependencies.put("_ownedFixturePurchaseId", ownedPurchase.id());
                data.track(record.endpoint(), record.id(), record.name(), dependencies);
            }
        return records;
    }

    private void chooseLineValue(String name, String heading, String value) {
        var ready = new WebDriverWait(driver, Duration.ofSeconds(35));
        String row = "//tr[td[contains(.,'" + name + "')]]";
        var element =
                ready.until(
                        d ->
                                d.findElements(By.xpath(row)).stream()
                                        .filter(WebElement::isDisplayed)
                                        .findFirst()
                                        .orElse(null));
        var headers =
                element.findElement(By.xpath("ancestor::table[1]"))
                        .findElements(By.cssSelector("thead th"));
        int column = -1;
        for (int index = 0; index < headers.size(); index++)
            if (headers.get(index).getText().trim().equals(heading)) column = index + 1;
        Assert.assertTrue(column > 0, "Showroom line column missing: " + heading);
        var actions =
                new FormInteractions(
                        ready,
                        e ->
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        driver,
                                        "arguments[0].scrollIntoView({block:'center',inline:'nearest'})",
                                        e));
        actions.choose(
                By.xpath(row + "/td[" + column + "]//input[contains(@class,'vs__search')]"),
                value,
                FormInteractions.OptionMatch.CODE_SEGMENT);
    }
}
