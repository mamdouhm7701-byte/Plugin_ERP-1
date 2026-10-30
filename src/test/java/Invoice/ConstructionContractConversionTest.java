/* شرح الملف: اختبارات تحويل العقد لفاتورة يعرض الكمية المتبقية الصحيحة: تحويل العقد لفاتورة يعرض
 * الكمية المتبقية الصحيحة؛ البنود الإضافية تظهر عند تحويل العقد إلى فاتورة.
 */
package Invoice;

import Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType;
import Plugin_ERP.BranchProfiles.BranchProfiles;
import Plugin_ERP.Business.Calculator.*;
import Plugin_ERP.Models.DiscountType;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class ConstructionContractConversionTest extends SalesScenarioTest {
    private static final String CONTRACT = "ItemTransactions-Contract";
    private static final String SALES = "ItemTransactions-Sales";
    private String endpoint;

    @DataProvider
    public Object[][] billingQuantities() {
        return new Object[][] {{"1"}, {"2"}};
    }

    @Test(dataProvider = "billingQuantities")
    public void billsOwnedContractPartiallyOrInFullWithoutDuplicatingQuantities(
            String firstQuantity) {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(profile.expected().activityType(), ActivityType.CONSTRUCTION);
        item =
                data.fixture(
                        "contract-conversion-item",
                        "Items",
                        TestSupport.PolicyItemRecipe.create(data.itemValues(), "100", false));
        customer = data.customer();
        agent = data.agent();
        allowedItems.add(item.id());
        var customerBefore = customerBalance(data.read(customer));
        sameNumber(data.balance(item, 1), "1000");

        openContract();
        add(item, "picker");
        page.slQuantity(item.name(), "2");
        var contract = saveOwned(expected("2"));
        sameNumber(data.balance(item, 1), "1000");
        Assert.assertEquals(
                customerBalance(data.read(customer)).compareTo(customerBefore),
                0,
                "An unbilled contract must not change its customer balance");

        var invoices = new ArrayList<SalesTestData.Record>();
        var first = bill(contract, "2", firstQuantity, "first");
        invoices.add(first);
        assertRemaining(
                contract,
                new BigDecimal("2").subtract(new BigDecimal(firstQuantity)).toPlainString());
        sameNumber(
                data.balance(item, 1),
                new BigDecimal("1000").subtract(new BigDecimal(firstQuantity)).toPlainString());
        if (firstQuantity.equals("1")) {
            var second = bill(contract, "1", "1", "remainder");
            Assert.assertNotEquals(first.id(), second.id());
            invoices.add(second);
        }
        assertRemaining(contract, "0");
        sameNumber(data.balance(item, 1), "998");
        var billedTotal = expected("2").getNet();
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                customerBalance(data.read(customer))
                                                .subtract(customerBefore)
                                                .compareTo(billedTotal)
                                        == 0);
        Allure.addAttachment(
                "Owned contract partial and remaining billing",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "contract",
                                        data.read(contract),
                                        "invoices",
                                        invoices.stream().map(data::read).toList(),
                                        "stockAfter",
                                        data.balance(item, 1),
                                        "customerDelta",
                                        customerBalance(data.read(customer))
                                                .subtract(customerBefore)))
                        .toString(2),
                ".json");
    }

    @Test
    public void additionalTermsEditUnbilledContractAndCarryIntoItsInvoice() {
        Assert.assertEquals(
                BranchProfiles.selected().orElseThrow().expected().activityType(),
                ActivityType.CONSTRUCTION);
        item =
                data.fixture(
                        "contract-original-item",
                        "Items",
                        TestSupport.PolicyItemRecipe.create(data.itemValues(), "100", false));
        var extra =
                data.fixture(
                        "contract-extra-item",
                        "Items",
                        TestSupport.PolicyItemRecipe.create(data.itemValues(), "120", false));
        customer = data.customer();
        agent = data.agent();
        allowedItems.add(item.id());
        allowedItems.add(extra.id());
        var customerBefore = customerBalance(data.read(customer));
        openContract();
        add(item, "picker");
        page.slQuantity(item.name(), "2");
        var contract = saveOwned(expected("2"));
        Object originalCode = data.read(contract).get("code");
        page.slOpen("contracts/edit/" + contract.id());
        page.slFormReady();
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> contract.id().equals(page.slDocument().get("id")));
        page.slCapture(marker, data.store(1).id(), item.id(), CONTRACT);
        page.slChoose("نوع العقد", "بنود إضافية");
        add(extra, "picker");
        var updatedExpected =
                expected(
                        List.of(
                                invoiceLine("original", "2", "100"),
                                invoiceLine("addition", "1", "120")));
        var amended = saveOwned(updatedExpected);
        Assert.assertEquals(amended.id(), contract.id());
        var amendedStored = data.read(amended);
        Assert.assertEquals(amendedStored.get("code"), originalCode);
        Assert.assertEquals(amendedStored.get("contractType"), "AdditionalItems");
        assertLine(amendedStored, item, "2");
        assertLine(amendedStored, extra, "1");
        sameNumber(data.balance(item, 1), "1000");
        sameNumber(data.balance(extra, 1), "1000");
        Assert.assertEquals(
                customerBalance(data.read(customer)).compareTo(customerBefore),
                0,
                "Amending an unbilled contract must not create a customer debt");

        marker = "QA-SL-amended-billing-" + UUID.randomUUID().toString().substring(0, 12);
        endpoint = SALES;
        page.slOpen(
                "invoice/new?sourceTransactionId="
                        + contract.id()
                        + "&sourceTransactionType=Contract");
        page.slFormReady();
        new WebDriverWait(driver, Duration.ofSeconds(35))
                .until(
                        d ->
                                contract.id().equals(page.slDocument().get("sourceTransactionId"))
                                        && page.slDocument().get("lines") instanceof List<?> lines
                                        && lines.size() == 2);
        page.slCapture(marker, data.store(1).id(), item.id(), endpoint);
        page.slNotes(marker);
        assertLine(page.slDocument(), item, "2");
        assertLine(page.slDocument(), extra, "1");
        var invoice = saveOwned(updatedExpected);
        var stored = data.read(invoice);
        Assert.assertEquals(stored.get("sourceTransactionId"), contract.id());
        Assert.assertEquals(stored.get("sourceTransactionType"), "Contract");
        Assert.assertEquals(stored.get("dealerId"), customer.id());
        Assert.assertEquals(((List<?>) stored.get("lines")).size(), 2);
        assertLine(stored, item, "2");
        assertLine(stored, extra, "1");
        sameNumber(data.balance(item, 1), "998");
        sameNumber(data.balance(extra, 1), "999");
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                customerBalance(data.read(customer))
                                                .subtract(customerBefore)
                                                .compareTo(updatedExpected.getNet())
                                        == 0);
        Allure.addAttachment(
                "Owned additional contract terms billed",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "amendedContract",
                                        amendedStored,
                                        "invoice",
                                        stored,
                                        "originalStock",
                                        data.balance(item, 1),
                                        "additionalStock",
                                        data.balance(extra, 1)))
                        .toString(2),
                ".json");
    }

    private static void assertLine(
            Map<String, Object> document, SalesTestData.Record record, String quantity) {
        var matches =
                ((List<?>) document.get("lines"))
                        .stream()
                                .map(value -> (Map<?, ?>) value)
                                .filter(line -> record.id().equals(line.get("itemId")))
                                .toList();
        Assert.assertEquals(matches.size(), 1, "Exactly one owned line must survive conversion");
        sameNumber(matches.getFirst().get("quantity"), quantity);
    }

    private void openContract() {
        endpoint = CONTRACT;
        page.slOpen("contracts/new");
        page.slFormReady();
        page.slCapture(marker, data.store(1).id(), item.id(), endpoint);
        page.slChoose("المخزن", data.store(1).name());
        page.slChoose("العميل", customer.name());
        page.slChoose("مندوب", agent.name());
        page.slChoose("نوع العقد", "عقود");
        page.slNotes(marker);
    }

    private SalesTestData.Record bill(
            SalesTestData.Record contract, String available, String quantity, String stage) {
        marker = "QA-SL-" + stage + "-" + UUID.randomUUID().toString().substring(0, 12);
        endpoint = SALES;
        page.slOpen(
                "invoice/new?sourceTransactionId="
                        + contract.id()
                        + "&sourceTransactionType=Contract");
        page.slFormReady();
        new WebDriverWait(driver, Duration.ofSeconds(35))
                .until(
                        d ->
                                contract.id().equals(page.slDocument().get("sourceTransactionId"))
                                        && page.slDocument().get("lines") instanceof List<?> lines
                                        && lines.size() == 1);
        var loaded = page.slDocument();
        Assert.assertEquals(loaded.get("dealerId"), customer.id());
        Assert.assertEquals(loaded.get("storeId"), data.store(1).id());
        Assert.assertEquals(loaded.get("agentId"), agent.id());
        Assert.assertEquals(loaded.get("paymentType"), "Credit");
        var line = (Map<?, ?>) ((List<?>) loaded.get("lines")).getFirst();
        Assert.assertEquals(line.get("itemId"), item.id());
        sameNumber(line.get("quantity"), available);
        page.slCapture(marker, data.store(1).id(), item.id(), endpoint);
        page.slNotes(marker);
        page.slQuantity(item.name(), quantity);
        var invoice = saveOwned(expected(quantity));
        var stored = data.read(invoice);
        Assert.assertEquals(stored.get("sourceTransactionId"), contract.id());
        Assert.assertEquals(stored.get("sourceTransactionType"), "Contract");
        Assert.assertEquals(stored.get("dealerId"), customer.id());
        Assert.assertEquals(stored.get("storeId"), data.store(1).id());
        var savedLine = (Map<?, ?>) ((List<?>) stored.get("lines")).getFirst();
        Assert.assertEquals(savedLine.get("itemId"), item.id());
        sameNumber(savedLine.get("quantity"), quantity);
        return invoice;
    }

    private void assertRemaining(SalesTestData.Record contract, String quantity) {
        page.slOpen(
                "invoice/new?sourceTransactionId="
                        + contract.id()
                        + "&sourceTransactionType=Contract");
        page.slFormReady();
        new WebDriverWait(driver, Duration.ofSeconds(35))
                .until(
                        d ->
                                contract.id().equals(page.slDocument().get("sourceTransactionId"))
                                        && customer.id().equals(page.slDocument().get("dealerId"))
                                        && agent.id().equals(page.slDocument().get("agentId")));
        var source = page.slDocument();
        Allure.addAttachment(
                "Owned contract native remaining-quantity view",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "contract",
                                        data.read(contract),
                                        "sourceForm",
                                        source,
                                        "expectedRemaining",
                                        quantity))
                        .toString(2),
                ".json");
        var lines =
                ((List<?>) source.get("lines"))
                        .stream()
                                .map(value -> (Map<?, ?>) value)
                                .filter(line -> item.id().equals(line.get("itemId")))
                                .toList();
        if (new BigDecimal(quantity).signum() == 0)
            Assert.assertTrue(
                    lines.isEmpty(), "A fully billed owned line must not remain available");
        else {
            Assert.assertEquals(lines.size(), 1);
            sameNumber(lines.getFirst().get("quantity"), quantity);
        }
    }

    private Plugin_ERP.Models.InvoiceTotals expected(String quantity) {
        return expected(List.of(invoiceLine("owned-contract", quantity, "100")));
    }

    private static PolicyInvoiceLine invoiceLine(String key, String quantity, String price) {
        return new PolicyInvoiceLine(
                key,
                new BigDecimal(quantity),
                new BigDecimal(price),
                BigDecimal.ZERO,
                DiscountType.VALUE,
                false);
    }

    private Plugin_ERP.Models.InvoiceTotals expected(List<PolicyInvoiceLine> lines) {
        return BranchInvoiceCalculator.calculate(
                lines,
                BranchProfiles.selected().orElseThrow().expected().taxPolicy(),
                2,
                BranchInvoiceCalculator.TaxRounding.INVOICE);
    }

    private SalesTestData.Record saveOwned(Plugin_ERP.Models.InvoiceTotals expected) {
        page.slWaitNet(expected.getNet().toPlainString());
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> !page.slSaveDisabled());
        page.slSave(false);
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(
                        d -> {
                            data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
                            return !page.slSaves().isEmpty();
                        });
        page.slSettled();
        var record = capture().getLast();
        var stored = data.read(record);
        TestSupport.DecimalAmountAssertions.amount(
                stored.get("netBeforeTaxes"), expected.getBeforeTax().toPlainString());
        TestSupport.DecimalAmountAssertions.amount(
                stored.get("totalTaxes"), expected.getTax().toPlainString());
        TestSupport.DecimalAmountAssertions.amount(
                stored.get("net"), expected.getNet().toPlainString());
        Allure.addAttachment(
                "Owned activity saved document",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "endpoint",
                                        endpoint,
                                        "sent",
                                        record.values(),
                                        "stored",
                                        stored))
                        .toString(2),
                ".json");
        page.slDismiss();
        return record;
    }

    @Override
    protected List<SalesTestData.Record> capture() {
        return ActivityDocumentCapture.collect(data, page, marker, endpoint);
    }
}
