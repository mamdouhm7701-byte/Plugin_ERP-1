/* شرح الملف: اختبارات تنظيف المشتريات: ترتيب حذف المستندات والعلاقات قبل البيانات التي تعتمد عليها.
 */
package Purches;

import io.qameta.allure.Story;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PurchaseDependenciesCleanupTest {
    @Test
    @io.qameta.allure.Description(
            "العميل المستخدم في البيانات دائم وخاضع للضريبة ويسمح بالبيع الآجل؛ صلاحية البيع النقدي لا تجعله عميلاً نقدياً.")
    public void normalCustomerIsTaxableAndKeepsItsOwnAccount() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        try (var data = new PurchaseTestData(api.driver())) {
            var customer = data.customer();
            var stored = data.read(customer);
            Assert.assertEquals(stored.get("isTaxable"), Boolean.TRUE);
            Assert.assertEquals(stored.get("isOnTheFly"), Boolean.FALSE);
            Assert.assertEquals(stored.get("creditSales"), Boolean.TRUE);
            Assert.assertEquals(stored.get("creditNote"), Boolean.TRUE);
            Assert.assertNotNull(stored.get("accountId"));
        }
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("فشل قراءة المورد لا يبرر حذف عميل موجود أو حساباته")
    public void customerLookupErrorCannotDeleteAnExistingCustomerOrItsAccounts() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        var customer = data.supplier();
        api.records.put(
                "existing-customer",
                new LinkedHashMap<>(
                        Map.of("id", "existing-customer", "arabicName", "Unrelated customer")));
        api.customerGetAlways500 = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(customer.id()));
        api.customerGetAlways500 = false;
        api.customerGet500WhenMissing = true;
        data.close();
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("existing-customer"));
        Assert.assertTrue(api.deletes.contains(customer.id()));
    }

    @Test
    @Story("حذف المرتجع المرتبط قبل الفاتورة الأصلية")
    public void linkedReturnIsDeletedBeforeSourceInvoice() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        var source = data.seedPurchase(1, "Purchases", "2026-01-01", "3", "0");
        var values =
                PurchaseTestData.fields(
                        "notes",
                        "QA-PU-linked-return",
                        "sourceTransactionId",
                        source.id(),
                        "storeId",
                        data.store(1).id(),
                        "lines",
                        source.values().get("lines"));
        api.records.put("owned-return", new LinkedHashMap<>(values));
        data.track("ReturnPurchases", "owned-return", "QA-PU-linked-return", values);
        data.close();
        Assert.assertTrue(api.deletes.indexOf("owned-return") < api.deletes.indexOf(source.id()));
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("حذف الفاتورة المدفوعة قبل وسيلة الدفع والبنك")
    public void paidInvoiceIsDeletedBeforePaymentMethodAndBank() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        var source = data.seedPurchase(1, "Purchases", "2026-01-01", "3", "300");
        var method = data.payment();
        var bank = data.bank();
        data.close();
        Assert.assertTrue(api.deletes.indexOf(source.id()) < api.deletes.indexOf(method.id()));
        Assert.assertTrue(api.deletes.indexOf(method.id()) < api.deletes.indexOf(bank.id()));
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("إزالة ارتباط الفرع الخاص بالاختبار فقط مع حفظ التغييرات المتزامنة")
    public void removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var store = data.store(1);
        Assert.assertTrue(
                ((List<?>) api.branch.get("stores"))
                        .stream().anyMatch(v -> store.id().equals(((Map<?, ?>) v).get("storeId"))));
        List<Object> concurrent = new ArrayList<>((List<?>) api.branch.get("stores"));
        concurrent.add(Map.of("storeId", "another-user-store"));
        api.branch.put("stores", concurrent);
        api.branch.put("phone", "changed-by-user");
        data.close();
        Assert.assertEquals(api.branch.get("phone"), "changed-by-user");
        Assert.assertEquals(api.branch.get("defaultStoreId"), "existing-store");
        Assert.assertEquals(
                ((List<?>) api.branch.get("stores"))
                        .stream()
                                .map(v -> ((Map<?, ?>) v).get("storeId"))
                                .collect(java.util.stream.Collectors.toSet()),
                java.util.Set.of("existing-store", "another-user-store"));
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("الاحتفاظ بالمخزن وحساباته عند تعذر إزالة ارتباطه المحدد")
    public void retainsStoreAndAccountsWhenOwnedMembershipCannotBeRemoved() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        var store = data.store(1);
        api.rejectBranchUnlink = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        Assert.assertTrue(api.records.containsKey(store.id()));
        api.rejectBranchUnlink = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("الاحتفاظ بالتبعيات المعطلة وإعادة محاولة تنظيف سجلات الاختبار فقط")
    public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        api.rejectStore = true;
        api.records.put(
                "shared-record",
                new LinkedHashMap<>(Map.of("arabicName", "Existing business data")));
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record store = data.store(1);
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertEquals(api.deletes, List.of(store.id()));
        Assert.assertEquals(api.records.size(), 4);
        api.rejectStore = false;
        data.close();
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-record"));
    }

    @Test
    @Story("حذف السجل المعدل قبل المرجع الجديد الذي أصبح مرتبطاً به")
    public void updatedRecordDeletesBeforeItsNewReference() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        PurchaseTestData data = new PurchaseTestData(api.driver());
        PurchaseTestData.Record original = data.store(1);
        PurchaseTestData.Record movement =
                data.fixture(
                        "movement", "Purchases", PurchaseTestData.fields("storeId", original.id()));
        PurchaseTestData.Record replacement = data.store(2);
        Map<String, Object> updated = new LinkedHashMap<>(movement.values());
        updated.put("storeId", replacement.id());
        api.records.put(movement.id().toString(), updated);
        data.track(movement.endpoint(), movement.id(), movement.name(), updated);
        data.close();
        Assert.assertEquals(api.deletes.get(0), movement.id());
        Assert.assertTrue(
                api.deletes.indexOf(movement.id()) < api.deletes.indexOf(replacement.id()));
        Assert.assertTrue(api.records.isEmpty());
    }
}
