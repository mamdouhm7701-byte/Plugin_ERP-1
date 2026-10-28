/* شرح الملف: اختبارات تنظيف المبيعات: ترتيب حذف المستندات والعلاقات قبل البيانات التي تعتمد عليها.
 */
package Invoice;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SalesDependencies2CleanupTest {
    @Test
    @io.qameta.allure.Story("مشاركة المراجع الثابتة بالصفحة وعزل بيانات كل حالة دون حذف بينها")
    public void pageScopeSharesOnlyStableReferences() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        data.beginScenario();
        var stable = data.unit(1);
        var first = data.item(1);
        var bank = data.bank();
        var safe = data.safe();
        var payment = data.payment();
        data.beginScenario();
        var second = data.item(1);
        Assert.assertNotEquals(first.id(), second.id());
        Assert.assertNotEquals(bank.id(), data.bank().id());
        Assert.assertNotEquals(
                bank.values().get("accountId"), data.bank().values().get("accountId"));
        Assert.assertNotEquals(safe.id(), data.safe().id());
        Assert.assertNotEquals(payment.id(), data.payment().id());
        Assert.assertEquals(data.unit(1).id(), stable.id());
        Assert.assertTrue(api.records.containsKey(first.id()));
        Assert.assertTrue(api.deletes.isEmpty());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
