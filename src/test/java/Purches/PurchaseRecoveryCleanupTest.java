/* شرح الملف: اختبارات تنظيف المشتريات: استكمال التنظيف بعد فشل أو انقطاع مع منع الحذف المكرر.
 */
package Purches;

import io.qameta.allure.Story;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PurchaseRecoveryCleanupTest {
    @Test
    @Story("تحديث سجل الاسترجاع بعد كل حذف ناجح حتى لا يعيد الانقطاع سجلات تم تنظيفها")
    public void interruptedCleanupJournalsOnlyRemainingOwnedRecords() throws Exception {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        var first = data.unit(1);
        var second = data.unit(2);
        api.crashAtDelete = 2;
        Assert.expectThrows(Error.class, data::close);
        String suffix = first.name().substring("QA-PU-unit-1-".length());
        var rows =
                new org.json.JSONArray(
                        java.nio.file.Files.readString(
                                java.nio.file.Path.of(
                                        "target", "purchase-fixtures", suffix + ".json")));
        Assert.assertEquals(rows.length(), 1);
        Assert.assertEquals(rows.getJSONObject(0).getString("id"), first.id());
        api.crashAtDelete = 0;
        PurchaseTestData.resume(api.driver(), suffix).close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(java.util.Collections.frequency(api.deletes, second.id()), 1);
    }

    @Test
    @Story(
            "استعادة مستند المشتريات من قائمة مختصرة لا تعرض الملاحظات أو المخزن مع حماية المستندات الأخرى")
    public void recoversSparsePurchaseListThroughVerifiedDetails() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.sparseMovementList = true;
        var data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        var supplier = data.supplier();
        String marker = "QA-PU-sparse-lost";
        var values =
                PurchaseTestData.fields(
                        "notes",
                        marker,
                        "storeId",
                        data.store(1).id(),
                        "dealerId",
                        supplier.id(),
                        "transactionType",
                        "Purch",
                        "lines",
                        List.of(
                                PurchaseTestData.fields(
                                        "itemId", item.id(), "unitId", data.unit(1).id())));
        var stored = new LinkedHashMap<>(values);
        stored.put("id", "owned-lost-purchase");
        api.records.put("owned-lost-purchase", stored);
        api.records.put(
                "existing-purchase",
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "existing-purchase",
                                "notes",
                                "Existing purchase",
                                "storeId",
                                "existing-store",
                                "lines",
                                List.of(Map.of("itemId", "existing-item")))));
        data.rememberAttempts(
                List.of(PurchaseTestData.fields("endpoint", "Purchases", "values", values)),
                List.of(),
                marker);
        data.close();
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("existing-purchase"));
        Assert.assertTrue(api.deletes.contains("owned-lost-purchase"));
        Assert.assertFalse(api.deletes.contains("existing-purchase"));
    }

    @Test
    @Story("استعادة حساب ضاعت استجابته من شجرة الحسابات دون حذف حسابات أخرى")
    public void recoversLostAccountFromItsTreeWithoutDeletingExistingAccounts() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        String name = "QA-PU-lost-account";
        var values =
                PurchaseTestData.fields(
                        "arabicName",
                        name,
                        "englishName",
                        name,
                        "code",
                        "90012345",
                        "type",
                        "Main");
        var stored = new LinkedHashMap<>(values);
        stored.put("id", "lost-account");
        api.records.put("lost-account", stored);
        api.records.put(
                "existing-account",
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "existing-account",
                                "arabicName",
                                "Existing business account")));
        data.rememberFixtureAttempt("Accounts", name, values);
        data.close();
        Assert.assertTrue(api.usedAccountTree);
        Assert.assertEquals(api.deletes, List.of("lost-account"));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("existing-account"));
    }

    @Test
    @Story("الانتقال للحالة التالية يحتفظ بمحاولة الحفظ الفاشلة السابقة ويستعيدها")
    public void pageTransitionRetainsAndRecoversThePreviousFailedSave() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        data.beginScenario();
        var item = data.item(1);
        String marker = "QA-PU-case-a-lost";
        var values =
                PurchaseTestData.fields(
                        "notes",
                        marker,
                        "transactionType",
                        "Purchases",
                        "storeId",
                        data.store(1).id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        var stored = new LinkedHashMap<>(values);
        stored.put("id", "previous-lost");
        api.records.put("previous-lost", stored);
        data.rememberAttempts(
                List.of(PurchaseTestData.fields("endpoint", "Purchases", "values", values)),
                List.of(),
                marker);
        data.rememberAttempts(List.of(), List.of(), "QA-PU-different-case");
        data.beginScenario();
        Assert.assertTrue(data.documentIdentities().contains("Purchases/previous-lost"));
        Assert.assertTrue(api.deletes.isEmpty());
        data.item(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertTrue(api.deletes.indexOf("previous-lost") < api.deletes.indexOf(item.id()));
    }

    @Test
    @Story("استعادة المرتجع الثاني دون الخلط بينه وبين المرتجع الأول المعروف")
    public void recoversSecondReturnWithoutConfusingKnownFirstReturn() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        var common =
                PurchaseTestData.fields(
                        "transactionType",
                        "ReturnPurch",
                        "storeId",
                        data.store(1).id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        var first = data.fixture("known-return", "ReturnPurchases", new LinkedHashMap<>(common));
        String marker = "QA-PU-second-lost-return";
        var second = new LinkedHashMap<>(common);
        second.put("notes", marker);
        second.put("id", "lost-second-return");
        api.records.put("lost-second-return", second);
        var attempt = new LinkedHashMap<>(second);
        attempt.remove("id");
        data.rememberAttempts(
                List.of(PurchaseTestData.fields("endpoint", "ReturnPurchases", "values", attempt)),
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
            {"Purchases", "Purch"},
            {"PurchaseOrders", "PurchOrder"},
            {"ReturnPurchases", "ReturnPurch"}
        };
    }

    @Test(dataProvider = "lostSalesKinds")
    @Story("استعادة مستند محفوظ ضاعت استجابته لكل نوع من مستندات المشتريات")
    public void recoversLostResponseForEachSalesDocumentKind(
            String endpointType, String storedType) {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        var item = data.item(1);
        String marker = "QA-PU-lost-" + endpointType;
        var values =
                PurchaseTestData.fields(
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
                List.of(PurchaseTestData.fields("endpoint", endpointType, "values", values)),
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
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.failAfterStoreInsert = true;
        PurchaseTestData data = new PurchaseTestData(api.driver());
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        Object storeId =
                api.records.keySet().stream()
                        .filter(k -> k.startsWith("Stores-"))
                        .findFirst()
                        .orElseThrow();
        data.rememberAttempts(List.of(), List.of(), "QA-PU-no-ui-attempts");
        data.close();
        Assert.assertEquals(api.deletes.get(0), storeId);
        Assert.assertTrue(api.records.isEmpty());
    }

    private void rejectForeignContext(boolean tenant) throws Exception {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        var unit = data.unit(1);
        String suffix = unit.name().substring(unit.name().length() - 12);
        if (tenant) api.origin = "https://different-tenant.invalid";
        else api.activeBranch = "different-branch";
        Assert.expectThrows(
                AssertionError.class, () -> PurchaseTestData.resume(api.driver(), suffix));
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(unit.id()));
        api.origin = "offline";
        api.activeBranch = "branch-test";
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
