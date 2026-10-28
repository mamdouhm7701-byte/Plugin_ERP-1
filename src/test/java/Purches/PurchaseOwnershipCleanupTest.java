/* شرح الملف: اختبارات تنظيف المشتريات: رفض حذف بيانات فرع أو تننت أو مالك مختلف.
 */
package Purches;

import io.qameta.allure.Story;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PurchaseOwnershipCleanupTest {
    @Test
    @Story("التحقق من هوية البيانات قبل أول عملية حذف")
    public void checksIdentityBeforeFirstDeletion() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var unit = data.unit(1);
        api.records.put(
                unit.id().toString(),
                new LinkedHashMap<>(
                        Map.of("id", unit.id(), "arabicName", "Different business record")));
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        api.records.put(
                unit.id().toString(),
                new LinkedHashMap<>(Map.of("id", unit.id(), "arabicName", unit.name())));
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("رفض حذف مستند تغيرت هوية مخزنه")
    public void rejectsSalesDocumentWithChangedStoreIdentity() {
        salesDocumentIdentityMismatch(true);
    }

    @Test
    @Story("رفض حذف مستند تغيرت هوية صنفه")
    public void rejectsSalesDocumentWithChangedItemIdentity() {
        salesDocumentIdentityMismatch(false);
    }

    private void salesDocumentIdentityMismatch(boolean storeChanged) {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        Map<String, Object> values =
                PurchaseTestData.fields(
                        "id",
                        "owned-salesDocument",
                        "notes",
                        "",
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        data.track("Purchases", "owned-salesDocument", "QA-PU-salesDocument", values);
        Map<String, Object> changed = new LinkedHashMap<>(values);
        if (storeChanged) changed.put("storeId", "business-store");
        else
            changed.put(
                    "lines",
                    List.of(Map.of("itemId", "business-item", "unitId", data.unit(1).id())));
        api.records.put("owned-salesDocument", changed);
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertFalse(api.deletes.contains("owned-salesDocument"));
        Assert.assertTrue(api.records.containsKey(item.id()));
        api.records.put("owned-salesDocument", values);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
