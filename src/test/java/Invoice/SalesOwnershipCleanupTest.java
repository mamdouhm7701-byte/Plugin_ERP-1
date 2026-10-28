/* شرح الملف: اختبارات تنظيف المبيعات: رفض حذف بيانات فرع أو تننت أو مالك مختلف.
 */
package Invoice;

import io.qameta.allure.Story;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SalesOwnershipCleanupTest {
    @Test
    public void ownedConcreteReservationKeepsMarkerAndRecoveryIdentity() {
        var api = new SalesFixtureApiStub();
        try (var data = new SalesTestData(api.driver())) {
            String marker = "QA-SL-owned-concrete-reservation";
            var record =
                    data.fixture(
                            marker,
                            "ItemTransactions-Reservations",
                            SalesTestData.fields(
                                    "storeId",
                                    data.store(1).id(),
                                    "transactionType",
                                    "Reservations",
                                    "lines",
                                    List.of()));
            Assert.assertEquals(data.read(record).get("notes"), marker);
            Assert.assertTrue(
                    data.documentIdentities().contains(record.endpoint() + "/" + record.id()));
        }
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void ownedConstructionContractKeepsItsMarkerAndRecoveryIdentity() {
        var api = new SalesFixtureApiStub();
        try (var data = new SalesTestData(api.driver())) {
            String marker = "QA-SL-owned-contract-recovery";
            var contract =
                    data.fixture(
                            marker,
                            "ItemTransactions-Contract",
                            SalesTestData.fields(
                                    "storeId", data.store(1).id(), "lines", List.of()));
            Assert.assertEquals(data.read(contract).get("notes"), marker);
            Assert.assertTrue(
                    data.documentIdentities()
                            .contains("ItemTransactions-Contract/" + contract.id()));
            data.rememberAttempts(
                    List.of(
                            SalesTestData.fields(
                                    "endpoint", contract.endpoint(), "values", contract.values())),
                    List.of(
                            SalesTestData.fields(
                                    "endpoint",
                                    contract.endpoint(),
                                    "id",
                                    contract.id(),
                                    "values",
                                    contract.values())),
                    marker);
        }
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("التحقق من هوية البيانات قبل أول عملية حذف")
    public void checksIdentityBeforeFirstDeletion() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
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
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        Map<String, Object> values =
                SalesTestData.fields(
                        "id",
                        "owned-salesDocument",
                        "notes",
                        "",
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        data.track("ItemTransactions-Sales", "owned-salesDocument", "QA-SL-salesDocument", values);
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
