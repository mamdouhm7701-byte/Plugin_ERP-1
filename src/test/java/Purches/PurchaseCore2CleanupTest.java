/* شرح الملف: اختبارات تنظيف المشتريات: حذف البيانات المملوكة والتحقق من غيابها.
 */
package Purches;

import io.qameta.allure.Story;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PurchaseCore2CleanupTest {
    @Test
    @Story("قبول نموذج حركة افتراضي بعد التحقق من غياب الحركة")
    public void acceptsVerifiedMissingMovementDefaultDto() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.defaultDeletedDto = true;
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record record =
                data.fixture("transaction", "Purchases", PurchaseTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }

    @Test
    @Story("قبول نموذج بيانات أساسية افتراضي بعد التحقق من غياب السجل")
    public void acceptsVerifiedMissingMasterDefaultDto() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.defaultDeletedMasterDto = true;
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record record = data.fixture("item", "Items", PurchaseTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }
}
