/* شرح الملف: مراجعة بيانات فواتير التجهيز: الكمية والقيمة والمدفوع وطريقة السداد، ثم التأكد من تنظيف سجلاتها. */
package Purches;

import java.math.BigDecimal;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class PurchaseSeedInvoicesTest {
    @Test
    public void preparesIndependentCreditAndPaidDocumentsWithExactFixtureValues() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        try (PurchaseTestData data = new PurchaseTestData(api.driver())) {
            var factory = new PurchaseSeedInvoices(data);
            var credit =
                    factory.create(
                            1,
                            "PurchaseOrders",
                            "2026-01-01",
                            "2.5",
                            "0",
                            data.agent(),
                            "REF-owned");
            var paid =
                    factory.create(
                            2,
                            "PurchaseOrders",
                            "2026-01-02",
                            "1",
                            "50",
                            data.agent(),
                            "REF-owned");
            Assert.assertNotEquals(credit.id(), paid.id());
            Assert.assertEquals(credit.values().get("paymentType"), "Credit");
            Assert.assertEquals(credit.values().get("payments"), List.of());
            Assert.assertEquals(
                    new BigDecimal(credit.values().get("net").toString())
                            .compareTo(new BigDecimal("250")),
                    0);
            Assert.assertEquals(paid.values().get("paymentType"), "Cash");
            Assert.assertEquals(paid.values().get("transactionDate"), "2026-01-02");
            var payment = (Map<?, ?>) ((List<?>) paid.values().get("payments")).getFirst();
            Assert.assertEquals(
                    new BigDecimal(payment.get("value").toString()).compareTo(new BigDecimal("50")),
                    0);
            var line = (Map<?, ?>) ((List<?>) credit.values().get("lines")).getFirst();
            Assert.assertEquals(line.get("itemId"), data.item(1).id());
            Assert.assertEquals(line.get("unitId"), data.unit(1).id());
            Assert.assertEquals(
                    new BigDecimal(line.get("quantity").toString())
                            .compareTo(new BigDecimal("2.5")),
                    0);
            Assert.assertEquals(paid.values().get("referenceNumber"), "REF-owned");
        }
        Assert.assertTrue(api.records.isEmpty(), "All owned invoice fixtures must be removed");
    }
}
