/* شرح الملف: مراجعة تنظيف البيانات التي أنشأتها التجربة فقط، ضمن المطعم ونقاط البيع.
 */
package SpecialSalesTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

@io.qameta.allure.Story("عزل البيانات والتنظيف الآمن")
public class SpecialSalesCleanupTest {
    @Test
    public void refreshedRestaurantSourceIsDeletedAfterItsOwnedReturn() {
        var source =
                new SpecialSalesTestData.Record(
                        "ItemTransactions-RestaurantSales",
                        "source",
                        "QA-SP-source",
                        Map.of("notes", "QA-SP-source"));
        var returned =
                new SpecialSalesTestData.Record(
                        "ItemTransactions-ReturnSales",
                        "returned",
                        "QA-SP-return",
                        Map.of("notes", "QA-SP-return", "sourceTransactionId", "source"));
        Assert.assertEquals(
                SpecialSalesCleanupOrder.records(List.of(returned, source)),
                List.of(returned, source));
        var foreign =
                new SpecialSalesTestData.Record("Customers", "other", "QA-SP-other", Map.of());
        Assert.assertEquals(
                SpecialSalesCleanupOrder.records(List.of(source, foreign, returned)),
                List.of(returned, foreign, source));
    }

    @Test
    @io.qameta.allure.Description(
            "العميل المستخدم في البيانات دائم وخاضع للضريبة ويسمح بالبيع الآجل؛ صلاحية البيع النقدي لا تجعله عميلاً نقدياً.")
    public void normalCustomerIsTaxableAndKeepsItsOwnAccount() {
        SpecialSalesFixtureApiStub api = new SpecialSalesFixtureApiStub();
        try (var data = new SpecialSalesTestData(api.driver())) {
            var customer = data.customer();
            var stored = data.read(customer);
            Assert.assertEquals(stored.get("isTaxable"), Boolean.TRUE);
            Assert.assertEquals(stored.get("isOnTheFly"), Boolean.FALSE);
            Assert.assertEquals(stored.get("creditSales"), Boolean.TRUE);
            Assert.assertEquals(stored.get("creditNote"), Boolean.TRUE);
            Assert.assertNotNull(stored.get("accountId"));
            var registered = data.registeredCustomer();
            Assert.assertEquals(data.read(registered).get("isTaxable"), Boolean.TRUE);
            Assert.assertEquals(data.read(registered).get("isOnTheFly"), Boolean.FALSE);
        }
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Description(
            "مشاركة الوحدات والمخازن وعزل الأصناف والعملاء بين الحالات حتى تنظيف الصفحة.")
    public void sharesReferencesAndIsolatesCases() {
        SpecialSalesFixtureApiStub api = new SpecialSalesFixtureApiStub();
        try (var data = new SpecialSalesTestData(api.driver())) {
            data.beginScenario();
            var unit = data.unit(1);
            var store = data.store(1);
            var category = data.category();
            var item = data.item(1);
            var customer = data.customer();
            Assert.assertNotEquals(data.registeredCustomer().id(), customer.id());
            data.beginScenario();
            Assert.assertEquals(data.unit(1).id(), unit.id());
            Assert.assertEquals(data.store(1).id(), store.id());
            Assert.assertEquals(data.category().id(), category.id());
            Assert.assertNotEquals(data.item(1).id(), item.id());
            Assert.assertNotEquals(data.customer().id(), customer.id());
            Assert.assertTrue(api.deletes.isEmpty());
        }
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Description(
            "استرجاع افتراضيات الفرع قبل حذف العميل والمخزن مع الحفاظ على بقية إعداداته.")
    public void restoresDefaultsBeforeDeletingReferences() {
        SpecialSalesFixtureApiStub api = new SpecialSalesFixtureApiStub();
        api.branch.put("defaultCustomerId", "existing-customer");
        try (var data = new SpecialSalesTestData(api.driver())) {
            data.beginScenario();
            var customer = data.customer();
            var defaults = data.configureOwnedDefaults(customer);
            Assert.assertTrue(
                    ((List<?>) api.branch.get("stores"))
                            .stream()
                                    .anyMatch(
                                            value ->
                                                    value instanceof Map<?, ?> store
                                                            && defaults.values()
                                                                    .get("storeId")
                                                                    .equals(store.get("storeId"))),
                    "Configuring restaurant defaults must retain the newly owned branch store");
            Assert.assertEquals(api.branch.get("defaultCustomerId"), customer.id());
            data.restoreDefaults(defaults);
            Assert.assertEquals(api.branch.get("defaultCustomerId"), "existing-customer");
        }
        Assert.assertEquals(api.branch.get("defaultStoreId"), "existing-store");
        Assert.assertEquals(api.branch.get("phone"), "original");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Description(
            "رفض حذف سجل تغيرت ملكيته والاحتفاظ بسجل الاسترجاع حتى حل التعارض.")
    public void refusesChangedOwnership() {
        SpecialSalesFixtureApiStub api = new SpecialSalesFixtureApiStub();
        var data = new SpecialSalesTestData(api.driver());
        var unit = data.unit(1);
        var original = new LinkedHashMap<>(api.records.get(unit.id()));
        api.records.put(
                unit.id().toString(),
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                unit.id(),
                                "arabicName",
                                "external",
                                "englishName",
                                "external")));
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        api.records.put(unit.id().toString(), original);
        data.close();
    }

    @Test
    @io.qameta.allure.Description(
            "الحفاظ على تغيير خارجي في افتراضيات الفرع وعدم الكتابة فوقه أثناء التنظيف.")
    public void preservesExternalDefaultsChange() {
        SpecialSalesFixtureApiStub api = new SpecialSalesFixtureApiStub();
        api.branch.put("defaultCustomerId", "existing-customer");
        var data = new SpecialSalesTestData(api.driver());
        data.beginScenario();
        var customer = data.customer();
        var defaults = data.configureOwnedDefaults(customer);
        api.branch.put("defaultCustomerId", "external-customer");
        Assert.expectThrows(AssertionError.class, () -> data.restoreDefaults(defaults));
        Assert.assertEquals(api.branch.get("defaultCustomerId"), "external-customer");
        Assert.assertTrue(api.deletes.isEmpty());
        api.branch.put("defaultCustomerId", customer.id());
        data.restoreDefaults(defaults);
        data.close();
    }

    @Test
    @io.qameta.allure.Description(
            "حذف فواتير القناتين قبل الصنف المرتبط بها مع قصر التنظيف على سجلات الاختبار.")
    public void deletesBothChannelsBeforeItem() {
        SpecialSalesFixtureApiStub api = new SpecialSalesFixtureApiStub();
        String itemId;
        try (var data = new SpecialSalesTestData(api.driver())) {
            data.beginScenario();
            var item = data.item(1);
            itemId = item.id().toString();
            data.fixture(
                    "pos",
                    "ItemTransactions-PointOfSales",
                    SpecialSalesTestData.fields("lines", List.of(Map.of("itemId", item.id()))));
            data.fixture(
                    "restaurant",
                    "ItemTransactions-RestaurantSales",
                    SpecialSalesTestData.fields("lines", List.of(Map.of("itemId", item.id()))));
        }
        Assert.assertTrue(api.deletes.get(0).startsWith("ItemTransactions-RestaurantSales"));
        Assert.assertTrue(api.deletes.get(1).startsWith("ItemTransactions-PointOfSales"));
        Assert.assertTrue(api.deletes.indexOf(itemId) > 1);
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Description(
            "استرجاع تشغيل منقطع من الجورنال واستعادة افتراضيات الفرع وتنظيف بياناته فقط.")
    public void interruptedDefaultsCanBeRecovered() throws Exception {
        SpecialSalesFixtureApiStub api = new SpecialSalesFixtureApiStub();
        api.branch.put("defaultCustomerId", "existing-customer");
        var interrupted = new SpecialSalesTestData(api.driver());
        interrupted.beginScenario();
        interrupted.configureOwnedDefaults(interrupted.customer());
        try (var resumed =
                SpecialSalesTestData.resume(api.driver(), interrupted.journalSuffix())) {}
        Assert.assertEquals(api.branch.get("defaultCustomerId"), "existing-customer");
        Assert.assertEquals(api.branch.get("defaultStoreId"), "existing-store");
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Description(
            "طاولات المطعم لا تدعم أسماء المتعاملين؛ يتم إثبات ملكيتها بملاحظة فريدة وتنظيفها بعد"
                    + " التحقق من الملكية.")
    public void tableOwnershipSurvivesDtoWithoutNames() {
        SpecialSalesFixtureApiStub api = new SpecialSalesFixtureApiStub();
        try (var data = new SpecialSalesTestData(api.driver())) {
            data.beginScenario();
            var table = data.table();
            var stored = data.read(table);
            Assert.assertFalse(stored.containsKey("arabicName"));
            Assert.assertEquals(stored.get("notes"), table.name());
        }
        Assert.assertTrue(api.records.isEmpty());
    }
}
