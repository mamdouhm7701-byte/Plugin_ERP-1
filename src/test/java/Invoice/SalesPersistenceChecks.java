/* شرح الملف: خطوات حفظ مستند البيع والتحقق من العلاقات والمدفوعات.
 */
package Invoice;

import Plugin_ERP.System_Modules.Login.Invoice.*;
import io.qameta.allure.Allure;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

final class SalesPersistenceChecks {
    private final SalesScenarioTest owner;

    SalesPersistenceChecks(SalesScenarioTest owner) {
        this.owner = owner;
    }

    SalesTestData.Record save(boolean print) {
        Map<String, Object> before = owner.page.slDocument();
        if (owner.paymentType.equals("Cash")
                && owner.kind() != SalesDocumentKind.QUOTATION
                && ((List<?>) before.get("payments")).isEmpty())
            owner.page.slAddOtherPayment(owner.payment.name(), owner.page.slTotals().get("الصافي"));
        int count = owner.page.slSaves().size();
        owner.page.slSave(print);
        try {
            new WebDriverWait(owner.caseDriver(), Duration.ofSeconds(45))
                    .until(
                            d -> {
                                owner.data.rememberAttempts(
                                        owner.page.slAttempts(),
                                        owner.page.slSaves(),
                                        owner.marker);
                                return owner.page.slSaves().size() > count;
                            });
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Sales document was not saved: " + owner.page.slSaveErrors(), e);
        }
        owner.page.slSettled();
        var records = owner.capture();
        var record = records.get(records.size() - 1);
        Assert.assertEquals(
                record.endpoint(),
                owner.kind().endpoint,
                "Saved document must match the current journey step");
        Map<String, Object> stored = owner.data.read(record);
        Allure.addAttachment(
                "Saved sales document",
                "application/json",
                new org.json.JSONObject(
                                SalesTestData.fields("sent", record.values(), "stored", stored))
                        .toString(),
                ".json");
        Assert.assertNotNull(stored);
        Assert.assertEquals(stored.get("notes"), record.name(), "مطابقة الملاحظات");
        Assert.assertEquals(stored.get("storeId"), owner.data.store(1).id(), "مطابقة المخزن");
        Assert.assertEquals(stored.get("dealerId"), owner.customer.id(), "مطابقة العميل أو المورد");
        Assert.assertEquals(stored.get("agentId"), owner.agent.id(), "مطابقة المندوب");
        Assert.assertTrue(
                owner.kind().type.equalsIgnoreCase(stored.get("transactionType").toString()),
                "التحقق من نوع الحركة");
        List<Map<String, Object>> lines = (List<Map<String, Object>>) stored.get("lines");
        Assert.assertFalse(lines.isEmpty());
        Assert.assertTrue(
                lines.stream().allMatch(l -> owner.allowedItems.contains(l.get("itemId"))),
                "Saved document contains an item outside this test");
        if (owner.paymentType.equals("Cash") && owner.kind() != SalesDocumentKind.QUOTATION) {
            var payments = (List<Map<String, Object>>) stored.get("payments");
            Assert.assertFalse(payments.isEmpty());
            Assert.assertTrue(
                    payments.stream()
                            .allMatch(
                                    p -> owner.payment.id().equals(p.get("otherPaymentMethodId"))),
                    "Unexpected payment method on owned document");
            Assert.assertEquals(
                    payments.stream()
                            .map(p -> owner.number(p.get("value")))
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .compareTo(owner.number(stored.get("net"))),
                    0,
                    "مطابقة الصافي");
        }
        if (print)
            owner.page.slVerifyPrintedReport(
                    owner.mainWindow,
                    owner.item.name(),
                    () -> owner.printContentAssertions.accept(stored));
        else owner.page.slDismiss();
        return record;
    }
}
