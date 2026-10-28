/* شرح الملف: اختبارات تنظيف المبيعات: حذف البيانات المملوكة والتحقق من غيابها.
 */
package Invoice;

import io.qameta.allure.Story;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SalesCleanupTest {
    @Test
    public void failedChassisSaleCleanupRetainsItsOwnedReceiptAndColor() {
        var api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        var color = data.fixture("receipt-color", "CarColors", SalesTestData.fields());
        var purchase =
                data.fixture(
                        "chassis-receipt",
                        "Purchases",
                        SalesTestData.fields(
                                "storeId",
                                data.store(1).id(),
                                "transactionType",
                                "Purch",
                                "lines",
                                List.of(Map.of("carColorId", color.id()))));
        var sale =
                data.fixture(
                        "chassis-sale",
                        "ItemTransactions-Sales",
                        SalesTestData.fields(
                                "storeId", data.store(1).id(),
                                "lines", List.of(Map.of("carColorId", color.id()))));
        var dependencies = new LinkedHashMap<>(sale.values());
        dependencies.put("_ownedFixturePurchaseId", purchase.id());
        data.track(sale.endpoint(), sale.id(), sale.name(), dependencies);
        api.rejectMovement = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(
                api.records.containsKey(purchase.id()),
                "The chassis receipt must survive while its sale could not be removed");
        Assert.assertTrue(api.records.containsKey(color.id()));
        api.rejectMovement = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void ownedShowroomPurchaseAndColorAreCleanedAfterTheirSale() {
        var api = new SalesFixtureApiStub();
        api.records.put("foreign-showroom-purchase", Map.of("notes", "Historical purchase"));
        var data = new SalesTestData(api.driver());
        var color = data.fixture("showroom-color", "CarColors", SalesTestData.fields());
        var purchase =
                data.fixture(
                        "showroom-purchase",
                        "Purchases",
                        SalesTestData.fields(
                                "storeId",
                                data.store(1).id(),
                                "transactionType",
                                "Purch",
                                "lines",
                                List.of(Map.of("carColorId", color.id()))));
        Assert.assertEquals(
                purchase.values().get("notes"),
                purchase.name(),
                "Purchase must retain its owned movement recovery marker");
        var values =
                SalesTestData.fields(
                        "notes",
                        "QA-SL-showroom-sale",
                        "storeId",
                        data.store(1).id(),
                        "sourceTransactionId",
                        purchase.id(),
                        "lines",
                        List.of(Map.of("carColorId", color.id())));
        api.records.put("owned-showroom-sale", values);
        data.track("ItemTransactions-Sales", "owned-showroom-sale", "QA-SL-showroom-sale", values);
        data.close();
        Assert.assertTrue(
                api.deletes.indexOf("owned-showroom-sale") < api.deletes.indexOf(purchase.id()));
        Assert.assertTrue(api.deletes.indexOf(purchase.id()) < api.deletes.indexOf(color.id()));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("foreign-showroom-purchase"));
    }

    @Test
    @Story("انتظار تحميل الفرع الأصلي قبل الوصول إلى البيانات المدخلة")
    public void waitsForOriginalBranchHydrationBeforeFixtureAccess() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        api.branchUnavailableReads = 2;
        var unit = data.unit(1);
        Assert.assertTrue(api.records.containsKey(unit.id()));
        Assert.assertEquals(api.branchUnavailableReads, 0);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("اكتشاف حفظ فعلي خلف استجابة فاشلة في اختبار رفض الحفظ")
    public void negativeSaveDetectsACommitBehindFailedResponse() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        var item = data.item(1);
        var before = data.documentIdentities();
        String marker = "QA-SL-invalid-partial-commit";
        var values =
                SalesTestData.fields(
                        "id",
                        "partial-invalid",
                        "notes",
                        marker,
                        "transactionType",
                        "Sales",
                        "storeId",
                        data.store(1).id(),
                        "lines",
                        List.of(
                                Map.of(
                                        "itemId",
                                        item.id(),
                                        "unitId",
                                        data.unit(1).id(),
                                        "quantity",
                                        0)));
        api.records.put("partial-invalid", new LinkedHashMap<>(values));
        var attempted = new LinkedHashMap<>(values);
        attempted.remove("id");
        data.rememberAttempts(
                List.of(
                        SalesTestData.fields(
                                "endpoint", "ItemTransactions-Sales", "values", attempted)),
                List.of(),
                marker);
        Assert.assertTrue(
                Assert.expectThrows(AssertionError.class, () -> data.verifyNoNewDocuments(before))
                        .getMessage()
                        .contains("Invalid sales document committed"));
        data.close();
        Assert.assertEquals(api.deletes.get(0), "partial-invalid");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("حذف الحركة المحددة قبل أصنافها ومخازنها")
    public void deletesOwnedMovementBeforeItsItemsAndStores() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        var movement =
                data.fixture(
                        "movement",
                        "ItemTransactions-Sales",
                        SalesTestData.fields(
                                "storeId",
                                store.id(),
                                "lines",
                                List.of(Map.of("itemId", item.id()))));
        api.records.put(
                "existing-movement",
                new LinkedHashMap<>(Map.of("id", "existing-movement", "notes", "Business record")));
        data.close();
        Assert.assertTrue(api.deletes.indexOf(movement.id()) < api.deletes.indexOf(item.id()));
        Assert.assertTrue(api.deletes.indexOf(item.id()) < api.deletes.indexOf(store.id()));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("existing-movement"));
    }

    @Test
    @Story("استئناف إنشاء مخزن معلق من سجل التنظيف المحفوظ")
    public void resumesPendingStoreCreationFromDurableJournal() throws Exception {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.failAfterStoreInsert = true;
        WebDriver driver = api.driver();
        SalesTestData data = new SalesTestData(driver);
        Assert.expectThrows(AssertionError.class, () -> data.store(1));
        String name =
                api.records.values().stream()
                        .filter(r -> r.get("id").toString().startsWith("Stores-"))
                        .map(r -> r.get("arabicName").toString())
                        .findFirst()
                        .orElseThrow();
        SalesTestData.resume(driver, name.substring(name.length() - 12)).close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("الاحتفاظ بتبعيات المخزون عند فشل حذف الحركة")
    public void retainsStockDependenciesWhenMovementDeletionFails() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        var item = data.item(1);
        var store = data.store(1);
        data.fixture(
                "movement",
                "ItemTransactions-Sales",
                SalesTestData.fields(
                        "storeId", store.id(), "lines", List.of(Map.of("itemId", item.id()))));
        api.rejectMovement = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.records.containsKey(item.id()));
        Assert.assertTrue(api.records.containsKey(store.id()));
        api.rejectMovement = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("رفض تسجيل سجل غير مملوك للاختبار ضمن بيانات التنظيف")
    public void refusesTrackingUnownedRecord() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        Assert.expectThrows(
                AssertionError.class,
                () -> data.track("Items", "business-item", "Business record", Map.of()));
        data.close();
        Assert.assertTrue(api.deletes.isEmpty());
    }

    @Test
    @Story("تنظيف مستند بلا ملاحظات بعد التحقق من ملكية مخزنه وأصنافه")
    public void cleansSalesDocumentWithoutNotesUsingOwnedStoreAndItems() {
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
        api.records.put("owned-salesDocument", new LinkedHashMap<>(values));
        data.track("ItemTransactions-Sales", "owned-salesDocument", "QA-SL-salesDocument", values);
        data.close();
        Assert.assertEquals(api.deletes.get(0), "owned-salesDocument");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("قبول استجابة عدم وجود السجل عند الحذف بعد تأكيد غيابه بالقراءة")
    public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.missingDelete400 = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record store = data.store(1);
        api.records.remove(store.id());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
