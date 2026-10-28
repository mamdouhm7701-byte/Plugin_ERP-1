/* شرح الملف: اختبارات تنظيف الحركات المالية: ترتيب حذف العلاقات قبل البيانات التي تعتمد عليها.
 */
package FinancialMovementsTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FinancialDependenciesCleanupTest {
    @Test
    @io.qameta.allure.Description(
            "العميل المستخدم في البيانات دائم وخاضع للضريبة ويسمح بالبيع الآجل؛ صلاحية البيع النقدي لا تجعله عميلاً نقدياً.")
    public void normalCustomerIsTaxableAndKeepsItsOwnAccount() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        try (var data = new FinancialTestData(api.driver())) {
            var customer = data.customer(1);
            var stored = data.read(customer);
            Assert.assertEquals(stored.get("isTaxable"), Boolean.TRUE);
            Assert.assertEquals(stored.get("isOnTheFly"), Boolean.FALSE);
            Assert.assertEquals(stored.get("creditSales"), Boolean.TRUE);
            Assert.assertEquals(stored.get("creditNote"), Boolean.TRUE);
            Assert.assertNotNull(stored.get("accountId"));
        }
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        api.rejectBank = true;
        api.records.put(
                "shared-record",
                new LinkedHashMap<>(Map.of("arabicName", "Existing business data")));
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record bank = data.bank(1);
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertEquals(api.deletes, List.of(bank.id()));
        Assert.assertEquals(api.records.size(), 4);
        api.rejectBank = false;
        data.close();
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-record"));
    }

    @Test
    public void updatedMovementDeletesBeforeItsNewReference() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        FinancialTestData data = new FinancialTestData(api.driver());
        FinancialTestData.Record original = data.bank(1);
        FinancialTestData.Record movement =
                data.fixture(
                        "movement",
                        "Collections",
                        FinancialTestData.fields("bankId", original.id()));
        FinancialTestData.Record replacement = data.bank(2);
        Map<String, Object> updated = new LinkedHashMap<>(movement.values());
        updated.put("bankId", replacement.id());
        api.records.put(movement.id().toString(), updated);
        data.track(movement.endpoint(), movement.id(), movement.name(), updated);
        data.close();
        Assert.assertEquals(api.deletes.get(0), movement.id());
        Assert.assertTrue(
                api.deletes.indexOf(movement.id()) < api.deletes.indexOf(replacement.id()));
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Story("مشاركة المراجع الثابتة بالصفحة وعزل بيانات كل حالة دون حذف بينها")
    public void pageScopeSharesOnlyStableReferences() {
        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        var data = new FinancialTestData(api.driver());
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        data.track(
                                "Collections", "business-collection", "Business record", Map.of()));
        data.beginScenario();
        var stable = data.fixture("invoice-unit", "Units", FinancialTestData.fields());
        var first = data.customer(1);
        var bank = data.bank(1);
        var safe = data.safe(1);
        var payment = data.payment();
        data.beginScenario();
        var second = data.customer(1);
        Assert.assertNotEquals(first.id(), second.id());
        Assert.assertNotEquals(first.values().get("accountId"), second.values().get("accountId"));
        Assert.assertNotEquals(bank.id(), data.bank(1).id());
        Assert.assertNotEquals(safe.id(), data.safe(1).id());
        Assert.assertNotEquals(payment.id(), data.payment().id());
        Assert.assertEquals(
                data.fixture("invoice-unit", "Units", FinancialTestData.fields()).id(),
                stable.id());
        Assert.assertTrue(api.records.containsKey(first.id()));
        Assert.assertTrue(api.deletes.isEmpty());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
