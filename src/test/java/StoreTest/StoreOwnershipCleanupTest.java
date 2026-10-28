/* شرح الملف: اختبارات تنظيف المخازن: حماية هوية الفرع وملكية السجلات قبل الحذف.
 */
package StoreTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StoreOwnershipCleanupTest {
    @Test
    public void checksIdentityBeforeFirstDeletion() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
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
    public void rejectsManufacturingWithChangedStoreIdentity() {
        manufacturingIdentityMismatch(true);
    }

    @Test
    public void rejectsManufacturingWithChangedItemIdentity() {
        manufacturingIdentityMismatch(false);
    }

    private void manufacturingIdentityMismatch(boolean storeChanged) {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        StoreTestData data = new StoreTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        Map<String, Object> values =
                StoreTestData.fields(
                        "id",
                        "owned-manufacturing",
                        "notes",
                        "",
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        data.track("ManufacturingOrders", "owned-manufacturing", "QA-ST-manufacturing", values);
        Map<String, Object> changed = new LinkedHashMap<>(values);
        if (storeChanged) changed.put("storeId", "business-store");
        else
            changed.put(
                    "lines",
                    List.of(Map.of("itemId", "business-item", "unitId", data.unit(1).id())));
        api.records.put("owned-manufacturing", changed);
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertFalse(api.deletes.contains("owned-manufacturing"));
        Assert.assertTrue(api.records.containsKey(item.id()));
        api.records.put("owned-manufacturing", values);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
