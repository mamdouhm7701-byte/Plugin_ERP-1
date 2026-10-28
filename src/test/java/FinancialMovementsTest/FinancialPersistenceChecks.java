/* شرح الملف: خطوات حفظ الحركة المالية ومطابقة المتعامل والحساب وطباعتها.
 */
package FinancialMovementsTest;

import Plugin_ERP.System_Modules.Login.FinancialMovements.*;
import io.qameta.allure.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

final class FinancialPersistenceChecks {
    private final FinancialScenarioTest owner;

    FinancialPersistenceChecks(FinancialScenarioTest owner) {
        this.owner = owner;
    }

    FinancialTestData.Record persist(boolean print, String amount) {
        if (!owner.page.hasSaveStarted()) {
            if (print) owner.page.saveAndPrintMovement();
            else owner.page.saveMovement();
        }
        try {
            new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(30))
                    .until(
                            d -> {
                                owner.data.rememberAttempts(
                                        owner.page.capturedAttempts(), owner.marker);
                                return !owner.page.capturedSaves().isEmpty();
                            });
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Movement save did not complete. Validation: "
                            + owner.page.validationMessages(),
                    e);
        }
        List<FinancialTestData.Record> saves = owner.capture();
        FinancialTestData.Record record = saves.get(saves.size() - 1);
        Map<String, Object> stored = owner.data.read(record);
        Assert.assertNotNull(stored, "Movement was not persisted");
        owner.page.identifyMovement(owner.marker, stored.get("code"));
        Assert.assertEquals(stored.get("notes"), owner.marker, "مطابقة الملاحظات");
        Assert.assertEquals(
                new BigDecimal(stored.get("value").toString()).compareTo(new BigDecimal(amount)),
                0,
                "Persisted amount differs");
        owner.assertOwnedRelationships(stored);
        if (owner.allocationInvoice != null) {
            Object allocations =
                    stored.get(
                            owner.kind().discount()
                                    ? "discountTransactionAllocations"
                                    : "financialTransactionAllocations");
            Assert.assertTrue(
                    allocations instanceof List<?> rows && rows.size() == 1,
                    "Expected one owned invoice allocation");
            Map<?, ?> allocated = (Map<?, ?>) ((List<?>) allocations).get(0);
            Assert.assertEquals(allocated.get("itemTransactionId"), owner.allocationInvoice.id());
            Object allocatedAmount =
                    allocated.get(owner.kind().discount() ? "allocatedValue" : "value");
            Assert.assertNotNull(
                    allocatedAmount, "Persisted allocation amount missing: " + allocated);
            Assert.assertEquals(
                    new BigDecimal(allocatedAmount.toString()).compareTo(new BigDecimal(amount)),
                    0);
        }
        if (print) {
            new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(30))
                    .until(d -> d.getWindowHandles().size() > 1);
            String report =
                    owner.caseDriver().getWindowHandles().stream()
                            .filter(h -> !h.equals(owner.mainWindow))
                            .findFirst()
                            .orElseThrow();
            owner.caseDriver().switchTo().window(report);
            try {
                owner.page.awaitReport();
                owner.page.AssertTextInReport(owner.marker);
                owner.page.AssertNumberInReport(amount);
                owner.page.AssertDateInReport(
                        java.time.LocalDate.parse(
                                        stored.get("transactionDate").toString().substring(0, 10))
                                .format(
                                        java.time.format.DateTimeFormatter.ofPattern(
                                                "dd/MM/yyyy")));
                if (owner.party != null) owner.page.AssertTextInReport(owner.party.name());
                if (owner.kind() == FinancialMovementKind.TRANSFER) {
                    owner.page.AssertTextInReport(owner.data.party(owner.fromType, 1).name());
                    owner.page.AssertTextInReport(owner.data.party(owner.toType, 2).name());
                } else if (!owner.kind().discount()) {
                    owner.page.AssertTextInReport("اخري");
                    owner.page.AssertTextInReport(owner.data.bank(1).name());
                }
            } catch (RuntimeException | AssertionError failure) {
                owner.page.attachReportEvidence();
                throw failure;
            } finally {
                owner.caseDriver().close();
                owner.caseDriver().switchTo().window(owner.mainWindow);
            }
        }
        owner.page.waitForList();
        return record;
    }

    void assertOwnedRelationships(Map<String, Object> stored) {
        if (owner.kind() == FinancialMovementKind.TRANSFER) {
            Assert.assertEquals(stored.get("fromType"), owner.fromType);
            Assert.assertEquals(stored.get("toType"), owner.toType);
            Assert.assertEquals(
                    stored.get("from" + owner.fromType + "Id"),
                    owner.data.party(owner.fromType, 1).id());
            Assert.assertEquals(
                    stored.get("to" + owner.toType + "Id"), owner.data.party(owner.toType, 2).id());
        } else if (owner.kind() == FinancialMovementKind.EXPENSE
                || owner.kind() == FinancialMovementKind.INCOME) {
            String key =
                    owner.directAccountMode
                            ? "accountId"
                            : owner.kind() == FinancialMovementKind.EXPENSE
                                    ? "expenseId"
                                    : "incomeId";
            Assert.assertEquals(
                    stored.get(key), owner.party.id(), "Movement must use its own account/type");
        } else {
            Assert.assertEquals(
                    stored.get("dealerId"), owner.party.id(), "Movement must use its own dealer");
            Assert.assertEquals(
                    stored.get("agentId"),
                    owner.data
                            .agent(
                                    owner.kind() == FinancialMovementKind.PAYMENT
                                            || owner.kind()
                                                    == FinancialMovementKind.SUPPLIER_DISCOUNT)
                            .id(),
                    "مطابقة المندوب");
        }
        if (!owner.kind().discount() && owner.kind() != FinancialMovementKind.TRANSFER) {
            Assert.assertEquals(stored.get("paymentMethodId"), owner.data.payment().id());
            Assert.assertEquals(stored.get("bankId"), owner.data.bank(1).id(), "مطابقة البنك");
        }
    }
}
