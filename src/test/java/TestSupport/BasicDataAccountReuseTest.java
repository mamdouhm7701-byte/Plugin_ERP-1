/* شرح الملف: يتأكد أن مشاركة حساب البنك الرئيسي تقلل التجهيز، مع بقاء كل بنك وحسابه الفرعي مستقلين وحذف الرئيسي بعد أبنائه فقط. */
package TestSupport;

import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BasicDataAccountReuseTest {
    @Test
    public void bankLedgersStayIndependentAndTheSharedParentIsDeletedLast() {
        String previous = System.getProperty("fixture.accounts.sharedParents");
        System.setProperty("fixture.accounts.sharedParents", "true");
        var api = new BasicDataFixturesApiStub();
        try (var data = new BasicDataFixtures(api.driver())) {
            data.beginScenario();
            var first = data.bank(1);
            var firstAccount = data.account("bank-1");
            data.beginScenario();
            var second = data.bank(1);
            var secondAccount = data.account("bank-1");
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
            api.records.put("foreign", Map.of("id", "foreign", "arabicName", "Existing bank"));
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
