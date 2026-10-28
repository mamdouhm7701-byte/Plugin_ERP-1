/* شرح الملف: اختبارات تنظيف المبيعات: ترتيب حذف المستندات والعلاقات قبل البيانات التي تعتمد عليها.
 */
package Invoice;

import io.qameta.allure.Story;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SalesDependenciesCleanupTest {
    @Test
    public void contractAmendedAfterPartialBillingIsRemovedAfterAllItsInvoices() {
        var api = new SalesFixtureApiStub();
        api.rejectContractWhileInvoiceExists = true;
        var data = new SalesTestData(api.driver());
        var item = data.item(1);
        var contract =
                data.fixture(
                        "contract",
                        "ItemTransactions-Contract",
                        SalesTestData.fields(
                                "storeId",
                                data.store(1).id(),
                                "transactionType",
                                "Contract",
                                "lines",
                                List.of(Map.of("itemId", item.id()))));
        var invoiceValues =
                SalesTestData.fields(
                        "storeId",
                        data.store(1).id(),
                        "sourceTransactionId",
                        contract.id(),
                        "sourceTransactionType",
                        "Contract",
                        "lines",
                        List.of(Map.of("itemId", item.id())));
        var first = data.fixture("partial-invoice", "ItemTransactions-Sales", invoiceValues);
        // Updating the parent moves its latest recovery record after the first child.
        data.track(contract.endpoint(), contract.id(), contract.name(), contract.values());
        var remaining = data.fixture("remaining-invoice", "ItemTransactions-Sales", invoiceValues);
        data.close();
        Assert.assertTrue(api.deletes.indexOf(first.id()) < api.deletes.indexOf(contract.id()));
        Assert.assertTrue(api.deletes.indexOf(remaining.id()) < api.deletes.indexOf(contract.id()));
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void concreteDeliveryIsRemovedBeforeItsLinkedInvoiceAndReservation() {
        var api = new SalesFixtureApiStub();
        api.rejectConcreteInvoiceWhileDeliveryExists = true;
        var data = new SalesTestData(api.driver());
        var item = data.item(1);
        var reservation =
                SalesTestData.fields(
                        "notes",
                        "QA-SL-owned-reservation",
                        "storeId",
                        data.store(1).id(),
                        "lines",
                        List.of(Map.of("itemId", item.id())));
        var delivery =
                SalesTestData.fields(
                        "notes",
                        "QA-SL-owned-delivery",
                        "storeId",
                        data.store(1).id(),
                        "sourceTransactionId",
                        "owned-reservation",
                        "lines",
                        List.of(Map.of("itemId", item.id())));
        var invoice =
                SalesTestData.fields(
                        "notes",
                        "QA-SL-owned-concrete-invoice",
                        "storeId",
                        data.store(1).id(),
                        "concreteDeliveryOrderIdsList",
                        List.of("owned-delivery"),
                        "lines",
                        List.of(Map.of("itemId", item.id())));
        api.records.put("owned-reservation", reservation);
        data.track(
                "ItemTransactions-Reservations",
                "owned-reservation",
                "QA-SL-owned-reservation",
                reservation);
        api.records.put("owned-delivery", delivery);
        data.track("ConcreteDelivery", "owned-delivery", "QA-SL-owned-delivery", delivery);
        api.records.put("owned-concrete-invoice", invoice);
        data.track(
                "ItemTransactions-ConcreteSales",
                "owned-concrete-invoice",
                "QA-SL-owned-concrete-invoice",
                invoice);
        api.records.put("foreign-delivery", Map.of("notes", "Historical delivery"));
        data.close();
        Assert.assertTrue(
                api.deletes.indexOf("owned-delivery")
                        < api.deletes.indexOf("owned-concrete-invoice"));
        Assert.assertTrue(
                api.deletes.indexOf("owned-concrete-invoice")
                        < api.deletes.indexOf("owned-reservation"));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("foreign-delivery"));
    }

    @Test
    @io.qameta.allure.Description(
            "العميل المستخدم في البيانات دائم وخاضع للضريبة ويسمح بالبيع الآجل؛ صلاحية البيع النقدي لا تجعله عميلاً نقدياً.")
    public void normalCustomerIsTaxableAndKeepsItsOwnAccount() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        try (var data = new SalesTestData(api.driver())) {
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
    @Story("فشل قراءة العميل لا يبرر حذف عميل موجود أو حساباته")
    public void customerLookupErrorCannotDeleteAnExistingCustomerOrItsAccounts() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        var customer = data.customer();
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
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        var source = data.seedInvoice(1, "Sales", "2026-01-01", "3", "0");
        var values =
                SalesTestData.fields(
                        "notes",
                        "QA-SL-linked-return",
                        "sourceTransactionId",
                        source.id(),
                        "storeId",
                        data.store(1).id(),
                        "lines",
                        source.values().get("lines"));
        api.records.put("owned-return", new LinkedHashMap<>(values));
        data.track("ItemTransactions-ReturnSales", "owned-return", "QA-SL-linked-return", values);
        data.close();
        Assert.assertTrue(api.deletes.indexOf("owned-return") < api.deletes.indexOf(source.id()));
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("حذف الفاتورة المدفوعة قبل وسيلة الدفع والبنك")
    public void paidInvoiceIsDeletedBeforePaymentMethodAndBank() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        var source = data.seedInvoice(1, "Sales", "2026-01-01", "3", "300");
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
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
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
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
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
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.rejectStore = true;
        api.records.put(
                "shared-record",
                new LinkedHashMap<>(Map.of("arabicName", "Existing business data")));
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record store = data.store(1);
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
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record original = data.store(1);
        SalesTestData.Record movement =
                data.fixture(
                        "movement",
                        "ItemTransactions-Sales",
                        SalesTestData.fields("storeId", original.id()));
        SalesTestData.Record replacement = data.store(2);
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
