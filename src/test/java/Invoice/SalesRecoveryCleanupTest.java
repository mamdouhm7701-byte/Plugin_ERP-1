/* شرح الملف: اختبارات تنظيف المبيعات: استكمال التنظيف بعد فشل أو انقطاع مع منع الحذف المكرر.
 */
package Invoice;

import io.qameta.allure.Story;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SalesRecoveryCleanupTest {
    @Test
    public void uncertainShowroomPurchaseIsRecoveredThroughItsNativeList() {
        var api = new SalesFixtureApiStub();
        api.omitMovementListNotes = true;
        api.omitMovementListStore = true;
        var data = new SalesTestData(api.driver());
        var item = data.item(1);
        var values =
                SalesTestData.fields(
                        "notes",
                        "QA-SL-uncertain-showroom-receipt",
                        "storeId",
                        data.store(1).id(),
                        "transactionType",
                        "Purch",
                        "lines",
                        List.of(Map.of("itemId", item.id())));
        var stored = new LinkedHashMap<>(values);
        stored.put("id", "owned-showroom-purchase");
        api.records.put("owned-showroom-purchase", stored);
        api.records.put(
                "foreign-showroom-receipt",
                SalesTestData.fields(
                        "id",
                        "foreign-showroom-receipt",
                        "notes",
                        "Historical purchase",
                        "storeId",
                        "foreign-store",
                        "transactionType",
                        "Purch",
                        "lines",
                        List.of()));
        data.rememberAttempts(
                List.of(Map.of("endpoint", "Purchases", "values", values)),
                List.of(),
                "QA-SL-uncertain-showroom-receipt");
        data.close();
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("foreign-showroom-receipt"));
        Assert.assertTrue(
                api.deletes.indexOf("owned-showroom-purchase") < api.deletes.indexOf(item.id()));
    }

    @Test
    public void concreteSalesAliasRecoversThroughSalesListWithoutSummaryNotes() {
        var api = new SalesFixtureApiStub();
        api.omitMovementListNotes = true;
        var data = new SalesTestData(api.driver());
        String marker = "QA-SL-concrete-sales-recovery";
        var item = data.item(1);
        var values =
                SalesTestData.fields(
                        "notes",
                        marker,
                        "storeId",
                        data.store(1).id(),
                        "transactionType",
                        "Sales",
                        "isConcrete",
                        true,
                        "lines",
                        List.of(
                                SalesTestData.fields(
                                        "itemId",
                                        item.id(),
                                        "unitId",
                                        data.unit(1).id(),
                                        "quantity",
                                        1)));
        var stored = new LinkedHashMap<>(values);
        stored.put("id", "concrete-invoice-ui");
        api.records.put("concrete-invoice-ui", stored);
        data.rememberAttempts(
                List.of(
                        SalesTestData.fields(
                                "endpoint", "ItemTransactions-ConcreteSales", "values", values)),
                List.of(),
                marker);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void nativeConcreteDeliveryUncertainCreateIsRecoveredBeforeParentsAreRemoved() {
        var api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        String marker = "QA-SL-concrete-native-recovery";
        var item = data.item(1);
        var values =
                SalesTestData.fields(
                        "notes",
                        marker,
                        "storeId",
                        data.store(1).id(),
                        "transactionType",
                        "ConcreteDelivery",
                        "lines",
                        List.of(
                                SalesTestData.fields(
                                        "itemId",
                                        item.id(),
                                        "unitId",
                                        data.unit(1).id(),
                                        "quantity",
                                        1)));
        var stored = new LinkedHashMap<>(values);
        stored.put("id", "delivery-ui");
        api.records.put("delivery-ui", stored);
        api.records.put(
                "foreign-delivery",
                SalesTestData.fields(
                        "id",
                        "foreign-delivery",
                        "notes",
                        "Existing delivery",
                        "transactionType",
                        "ConcreteDelivery",
                        "storeId",
                        "foreign-store",
                        "lines",
                        List.of()));
        data.rememberAttempts(
                List.of(SalesTestData.fields("endpoint", "ConcreteDelivery", "values", values)),
                List.of(),
                marker);
        data.close();
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("foreign-delivery"));
    }

    @Test
    @Story("احتفاظ التنظيف بمحاولة الإنشاء المتأخرة وتبعياتها حتى ظهور المستند")
    public void delayedSeedCreateRetainsParentsUntilItsDocumentCanBeRecovered() throws Exception {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        api.delayMovementCreate = true;
        Assert.expectThrows(
                org.openqa.selenium.TimeoutException.class,
                () -> data.seedInvoice(1, "Sales", "2026-01-01", "1", "40"));
        var failure = Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(failure.getMessage().contains("still unresolved"));
        Assert.assertTrue(
                api.deletes.isEmpty(), "A pending create can still reference its parents");
        String marker = api.delayedMovement.get("notes").toString();
        String suffix = marker.substring(marker.length() - 12);
        SalesTestData resumed = SalesTestData.resume(api.driver(), suffix);
        Assert.expectThrows(AssertionError.class, resumed::close);
        Assert.assertTrue(
                api.deletes.isEmpty(), "The persisted timeout must also block recovery cleanup");
        String delayedId = api.delayedMovement.get("id").toString();
        api.records.put(delayedId, api.delayedMovement);
        resumed.close();
        Assert.assertEquals(api.deletes.get(0), delayedId);
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("استعادة حساب ضاعت استجابته من شجرة الحسابات دون حذف حسابات أخرى")
    public void recoversLostAccountFromItsTreeWithoutDeletingExistingAccounts() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        String name = "QA-SL-lost-account";
        var values =
                SalesTestData.fields(
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
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        data.beginScenario();
        var item = data.item(1);
        String marker = "QA-SL-case-a-lost";
        var values =
                SalesTestData.fields(
                        "notes",
                        marker,
                        "transactionType",
                        "Sales",
                        "storeId",
                        data.store(1).id(),
                        "lines",
                        List.of(Map.of("itemId", item.id(), "unitId", data.unit(1).id())));
        var stored = new LinkedHashMap<>(values);
        stored.put("id", "previous-lost");
        api.records.put("previous-lost", stored);
        data.rememberAttempts(
                List.of(
                        SalesTestData.fields(
                                "endpoint", "ItemTransactions-Sales", "values", values)),
                List.of(),
                marker);
        data.rememberAttempts(List.of(), List.of(), "QA-SL-different-case");
        data.beginScenario();
        Assert.assertTrue(
                data.documentIdentities().contains("ItemTransactions-Sales/previous-lost"));
        Assert.assertTrue(api.deletes.isEmpty());
        data.item(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertTrue(api.deletes.indexOf("previous-lost") < api.deletes.indexOf(item.id()));
    }
}
