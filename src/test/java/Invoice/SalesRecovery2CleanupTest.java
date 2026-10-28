/* شرح الملف: اختبارات تنظيف المبيعات: استكمال التنظيف بعد فشل أو انقطاع مع منع الحذف المكرر.
 */
package Invoice;

import io.qameta.allure.Story;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SalesRecovery2CleanupTest {
    @Test
    @Story("استعادة المرتجع الثاني دون الخلط بينه وبين المرتجع الأول المعروف")
    public void recoversSecondReturnWithoutConfusingKnownFirstReturn() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        var item = data.item(1);
        var common =
                SalesTestData.fields(
                        "transactionType",
                        "ReturnSales",
                        "storeId",
                        data.store(1).id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        var first =
                data.fixture(
                        "known-return",
                        "ItemTransactions-ReturnSales",
                        new LinkedHashMap<>(common));
        String marker = "QA-SL-second-lost-return";
        var second = new LinkedHashMap<>(common);
        second.put("notes", marker);
        second.put("id", "lost-second-return");
        api.records.put("lost-second-return", second);
        var attempt = new LinkedHashMap<>(second);
        attempt.remove("id");
        data.rememberAttempts(
                List.of(
                        SalesTestData.fields(
                                "endpoint", "ItemTransactions-ReturnSales", "values", attempt)),
                List.of(),
                marker);
        data.close();
        Assert.assertTrue(
                api.deletes.indexOf("lost-second-return") < api.deletes.indexOf(first.id()));
        Assert.assertTrue(api.records.isEmpty());
    }

    @org.testng.annotations.DataProvider
    public Object[][] lostSalesKinds() {
        return new Object[][] {
            {"quotation", "Quotation"}, {"SalesOrder", "SalesOrder"}, {"ReturnSales", "ReturnSales"}
        };
    }

    @Test(dataProvider = "lostSalesKinds")
    @Story("استعادة مستند محفوظ ضاعت استجابته لكل نوع من مستندات المبيعات")
    public void recoversLostResponseForEachSalesDocumentKind(
            String endpointType, String storedType) {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        var item = data.item(1);
        String marker = "QA-SL-lost-" + endpointType;
        var values =
                SalesTestData.fields(
                        "notes",
                        marker,
                        "transactionType",
                        storedType,
                        "storeId",
                        data.store(1).id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        api.records.put("lost-document", new LinkedHashMap<>(values));
        api.records.get("lost-document").put("id", "lost-document");
        data.rememberAttempts(
                List.of(
                        SalesTestData.fields(
                                "endpoint", "ItemTransactions-" + endpointType, "values", values)),
                List.of(),
                marker);
        data.close();
        Assert.assertEquals(api.deletes.get(0), "lost-document");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("رفض استعادة سجل التنظيف في تننت تختلف عن تننت إنشاء البيانات")
    public void refusesJournalRecoveryInAnotherTenant() throws Exception {
        rejectForeignContext(true);
    }

    @Test
    @Story("رفض استعادة سجل التنظيف في فرع مختلف")
    public void refusesJournalRecoveryInAnotherBranch() throws Exception {
        rejectForeignContext(false);
    }

    @Test
    @Story("استعادة مخزن حُفظ قبل فقد استجابة الإنشاء")
    public void recoversStoreCommittedBeforeLostCreateResponse() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.failAfterStoreInsert = true;
        SalesTestData data = new SalesTestData(api.driver());
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        Object storeId =
                api.records.keySet().stream()
                        .filter(k -> k.startsWith("Stores-"))
                        .findFirst()
                        .orElseThrow();
        data.rememberAttempts(List.of(), List.of(), "QA-SL-no-ui-attempts");
        data.close();
        Assert.assertEquals(api.deletes.get(0), storeId);
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("فشل التأكد من المخزن يحتفظ بحساباته")
    public void uncertainStoreLookupFailurePreservesItsAccounts() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.failAfterStoreInsert = true;
        SalesTestData data = new SalesTestData(api.driver());
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        api.failFixtureLookup = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertEquals(api.records.size(), 3);
        api.failFixtureLookup = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("التحقق من استمرار ارتباط المخزن غير المؤكد بحسابه المحدد")
    public void uncertainStoreMustStillReferenceItsOwnedAccount() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.failAfterStoreInsert = true;
        SalesTestData data = new SalesTestData(api.driver());
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        var stored =
                api.records.values().stream()
                        .filter(r -> r.get("id").toString().startsWith("Stores-"))
                        .findFirst()
                        .orElseThrow();
        Object account = stored.put("accountId", "another-account");
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        stored.put("accountId", account);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("استعادة إذن مخزني حُفظ قبل فقد استجابة الإنشاء")
    public void recoversWarehouseCreateCommittedBeforeLostResponse() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        String marker = "QA-SL-lost-create";
        Map<String, Object> values =
                SalesTestData.fields(
                        "notes",
                        marker,
                        "storeId",
                        store.id(),
                        "transactionType",
                        "Sales",
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        Map<String, Object> stored = new LinkedHashMap<>(values);
        stored.put("id", "lost-create");
        api.records.put("lost-create", stored);
        data.rememberAttempts(
                List.of(
                        SalesTestData.fields(
                                "endpoint", "ItemTransactions-Sales", "values", values)),
                List.of(),
                marker);
        data.close();
        Assert.assertEquals(api.deletes.get(0), "lost-create");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("فشل البحث عن إذن مخزني يحتفظ بجميع تبعياته")
    public void uncertainWarehouseLookupFailureRetainsAllParents() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        Map<String, Object> values =
                SalesTestData.fields(
                        "notes",
                        "QA-SL-unresolved",
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        data.rememberAttempts(
                List.of(
                        SalesTestData.fields(
                                "endpoint", "ItemTransactions-Sales", "values", values)),
                List.of(),
                "QA-SL-unresolved");
        api.failMovementLookup = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(item.id()));
        api.failMovementLookup = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("استعادة الملاحظات المعدلة بعد فقد استجابة التعديل")
    public void recoversUpdatedNoteAfterLostUpdateResponse() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        String marker = "QA-SL-lost-update";
        Map<String, Object> original =
                SalesTestData.fields(
                        "id",
                        "lost-update",
                        "notes",
                        marker,
                        "storeId",
                        store.id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        api.records.put("lost-update", new LinkedHashMap<>(original));
        data.track("ItemTransactions-Sales", "lost-update", marker, original);
        Map<String, Object> updated = new LinkedHashMap<>(original);
        updated.put("notes", marker + " EDIT");
        api.records.put("lost-update", updated);
        data.rememberAttempts(
                List.of(
                        SalesTestData.fields(
                                "endpoint", "ItemTransactions-Sales", "values", updated)),
                List.of(),
                marker);
        data.close();
        Assert.assertEquals(api.deletes.get(0), "lost-update");
        Assert.assertTrue(api.records.isEmpty());
    }

    private void rejectForeignContext(boolean tenant) throws Exception {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        var unit = data.unit(1);
        String suffix = unit.name().substring(unit.name().length() - 12);
        if (tenant) api.origin = "https://different-tenant.invalid";
        else api.activeBranch = "different-branch";
        Assert.expectThrows(AssertionError.class, () -> SalesTestData.resume(api.driver(), suffix));
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(unit.id()));
        api.origin = "offline";
        api.activeBranch = "branch-test";
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
