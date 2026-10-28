/* شرح الملف: اختبارات تنظيف المشتريات: استكمال التنظيف بعد فشل أو انقطاع مع منع الحذف المكرر.
 */
package Purches;

import io.qameta.allure.Story;
import java.util.LinkedHashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PurchaseRecovery3CleanupTest {
    @Test
    @Story("تنظيف الصنف الذي حُفظ رغم فشل طلب الإنشاء")
    public void cleansOwnedItemCommittedBeforeCreateFailure() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.failAfterItemInsert = true;
        api.records.put(
                "shared-item",
                new LinkedHashMap<>(Map.of("id", "shared-item", "arabicName", "Existing item")));
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record unit = data.fixture("unit", "Units", PurchaseTestData.fields());
        PurchaseTestData.Record category =
                data.fixture("category", "ItemCategories", PurchaseTestData.fields());
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        data.fixture(
                                "item",
                                "Items",
                                PurchaseTestData.fields(
                                        "mainUnitId", unit.id(), "categoryId", category.id())));
        Assert.assertEquals(api.records.size(), 4);
        data.close();
        Assert.assertTrue(api.deletes.get(0).startsWith("Items-"));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-item"));
    }
}
