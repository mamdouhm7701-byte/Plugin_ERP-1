/* شرح الملف: اختبارات حفظ العقد لا يخصم المخزون ولا يغير رصيد العميل: حفظ العقد لا يخصم المخزون ولا
 * يغير رصيد العميل.
 */
package Invoice;

import Plugin_ERP.BranchProfiles.*;
import Plugin_ERP.BranchProfiles.BranchPolicies.ActivityType;
import Plugin_ERP.Business.Calculator.*;
import Plugin_ERP.Models.*;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ConstructionContractTest extends SalesScenarioTest {
    private static final String ENDPOINT = "ItemTransactions-Contract";

    @Test
    public void savesOwnedContractAndReadsBackExactRecipe() {
        var profile = BranchProfiles.selected().orElseThrow();
        Assert.assertEquals(profile.expected().activityType(), ActivityType.CONSTRUCTION);
        item =
                data.fixture(
                        "contract-taxable-item",
                        "Items",
                        TestSupport.PolicyItemRecipe.create(data.itemValues(), "100", false));
        customer = data.customer();
        agent = data.agent();
        allowedItems.add(item.id());
        var stockBefore = data.balance(item, 1);
        var customerBefore = data.read(customer);
        page.slOpen("contracts/new");
        page.slFormReady();
        page.slCapture(marker, data.store(1).id(), item.id(), ENDPOINT);
        page.slChoose("المخزن", data.store(1).name());
        page.slChoose("العميل", customer.name());
        page.slChoose("مندوب", agent.name());
        page.slChoose("نوع العقد", "عقود");
        page.slNotes(marker);
        add(item, "picker");
        var expected =
                BranchInvoiceCalculator.calculate(
                        List.of(
                                new PolicyInvoiceLine(
                                        "contract",
                                        BigDecimal.ONE,
                                        new BigDecimal("100"),
                                        BigDecimal.ZERO,
                                        DiscountType.VALUE,
                                        false)),
                        profile.expected().taxPolicy(),
                        2,
                        BranchInvoiceCalculator.TaxRounding.INVOICE);
        page.slWaitNet(expected.getNet().toPlainString());
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> !page.slSaveDisabled());
        page.slSave(false);
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .withMessage(
                        "Contract save was not confirmed: captured API and UI evidence retained")
                .until(
                        d -> {
                            data.rememberAttempts(page.slAttempts(), page.slSaves(), marker);
                            return !page.slSaves().isEmpty();
                        });
        page.slSettled();
        var records = capture();
        var contract = records.getLast();
        var stored = data.read(contract);
        Assert.assertEquals(stored.get("transactionType"), "Contract");
        Assert.assertEquals(stored.get("notes"), marker);
        Assert.assertEquals(stored.get("storeId"), data.store(1).id());
        Assert.assertEquals(stored.get("dealerId"), customer.id());
        Assert.assertEquals(stored.get("agentId"), agent.id());
        Assert.assertEquals(((List<?>) stored.get("lines")).size(), 1);
        var line = (Map<?, ?>) ((List<?>) stored.get("lines")).getFirst();
        Assert.assertEquals(line.get("itemId"), item.id());
        sameNumber(line.get("quantity"), "1");
        sameNumber(line.get("price"), "100");
        sameNumber(stored.get("netBeforeTaxes"), expected.getBeforeTax().toPlainString());
        sameNumber(stored.get("totalTaxes"), expected.getTax().toPlainString());
        sameNumber(stored.get("net"), expected.getNet().toPlainString());
        sameNumber(data.balance(item, 1), stockBefore.toString());
        var customerAfter = data.read(customer);
        Assert.assertEquals(
                customerBalance(customerAfter).compareTo(customerBalance(customerBefore)),
                0,
                "Creating a contract must not change its owned customer balance");
        Allure.addAttachment(
                "Owned contract save and observed balances",
                "application/json",
                new org.json.JSONObject(
                                Map.of(
                                        "stored",
                                        stored,
                                        "stockBefore",
                                        stockBefore,
                                        "stockAfter",
                                        data.balance(item, 1),
                                        "customerBefore",
                                        customerBefore,
                                        "customerAfter",
                                        customerAfter,
                                        "scope",
                                        "Create/read/amounts; contract creation must not affect stock or customer balance. Settlement remains separate."))
                        .toString(2),
                ".json");
        page.slDismiss();
    }

    @Override
    protected List<SalesTestData.Record> capture() {
        return ActivityDocumentCapture.collect(data, page, marker, ENDPOINT);
    }
}
