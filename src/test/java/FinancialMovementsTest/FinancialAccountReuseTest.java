/* شرح الملف: يتأكد أن مشاركة الحساب الرئيسي تقلل التجهيز، مع بقاء العميل وحسابه الفرعي مستقلين وتنظيف الحساب الرئيسي بعد أبنائه فقط. */
package FinancialMovementsTest;

import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FinancialAccountReuseTest {
    @Test
    public void customerLedgersStayIndependentAndTheSharedParentIsDeletedLast() {
        String previous = System.getProperty("fixture.accounts.sharedParents");
        System.setProperty("fixture.accounts.sharedParents", "true");
        var api = new FinancialFixtureApiStub();
        try (var data = new FinancialTestData(api.driver())) {
            data.beginScenario();
            var first = data.customer(1);
            var firstAccount = data.account("customer-1");
            data.beginScenario();
            var second = data.customer(1);
            var secondAccount = data.account("customer-1");
            Assert.assertNotEquals(first.id(), second.id());
            Assert.assertNotEquals(firstAccount.id(), secondAccount.id());
            Assert.assertNotEquals(
                    firstAccount.values().get("code"), secondAccount.values().get("code"));
            Assert.assertEquals(
                    firstAccount.values().get("parentId"), secondAccount.values().get("parentId"));
            Assert.assertEquals(
                    api.records.values().stream()
                            .filter(row -> "Main".equals(row.get("type")))
                            .count(),
                    1L);
            Object parent = firstAccount.values().get("parentId");
            api.records.put("foreign", Map.of("id", "foreign", "arabicName", "Existing customer"));
            data.close();
            Assert.assertTrue(api.deletes.indexOf(parent) > api.deletes.indexOf(firstAccount.id()));
            Assert.assertTrue(
                    api.deletes.indexOf(parent) > api.deletes.indexOf(secondAccount.id()));
            Assert.assertEquals(api.records.keySet(), java.util.Set.of("foreign"));
        } finally {
            if (previous == null) System.clearProperty("fixture.accounts.sharedParents");
            else System.setProperty("fixture.accounts.sharedParents", previous);
        }
    }
}
