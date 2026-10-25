package Invoice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Story;

public class SalesCleanupTest {
    @Test
    @Story("احتفاظ التنظيف بمحاولة الإنشاء المتأخرة وتبعياتها حتى ظهور المستند")
    public void delayedSeedCreateRetainsParentsUntilItsDocumentCanBeRecovered() throws Exception {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        api.delayMovementCreate = true;
        Assert.expectThrows(org.openqa.selenium.TimeoutException.class,
                () -> data.seedInvoice(1, "Sales", "2026-01-01", "1", "40"));
        var failure = Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(failure.getMessage().contains("still unresolved"));
        Assert.assertTrue(api.deletes.isEmpty(), "A pending create can still reference its parents");
        String marker = api.delayedMovement.get("notes").toString();
        String suffix = marker.substring(marker.length() - 12);
        SalesTestData resumed = SalesTestData.resume(api.driver(), suffix);
        Assert.expectThrows(AssertionError.class, resumed::close);
        Assert.assertTrue(api.deletes.isEmpty(), "The persisted timeout must also block recovery cleanup");
        String delayedId = api.delayedMovement.get("id").toString();
        api.records.put(delayedId, api.delayedMovement);
        resumed.close();
        Assert.assertEquals(api.deletes.get(0), delayedId);
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Description("العميل المستخدم في البيانات دائم وخاضع للضريبة ويسمح بالبيع الآجل؛ صلاحية البيع النقدي لا تجعله عميلاً نقدياً.")
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
    @Story("مشاركة بيانات الصفحة الثابتة مع عزل الأصناف والعملاء والأرصدة بين الحالات")
    public void pageFixturesAreSharedButCaseBalancesStayIsolated() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        data.beginScenario();
        var first = data.seedInvoice(1, "Sales", "2026-01-01", "3", "0");
        var firstItem = data.item(1);
        var firstCustomer = data.customer();
        var sharedStore = data.store(1);
        var sharedUnit = data.unit(1);
        data.beginScenario();
        var second = data.seedInvoice(1, "Sales", "2026-01-01", "1", "0");
        var secondItem = data.item(1);
        var secondCustomer = data.customer();
        Assert.assertNotEquals(first.id(), second.id());
        Assert.assertNotEquals(firstItem.id(), secondItem.id());
        Assert.assertNotEquals(firstCustomer.id(), secondCustomer.id());
        Assert.assertNotEquals(
                firstCustomer.values().get("accountId"), secondCustomer.values().get("accountId"));
        Assert.assertEquals(data.store(1).id(), sharedStore.id());
        Assert.assertEquals(data.unit(1).id(), sharedUnit.id());
        Assert.assertTrue(api.deletes.isEmpty(), "No cleanup is allowed between cases on the page");
        Assert.assertTrue(api.records.containsKey(first.id()));
        data.close();
        Assert.assertTrue(api.deletes.indexOf(first.id()) < api.deletes.indexOf(sharedStore.id()));
        Assert.assertTrue(api.deletes.indexOf(second.id()) < api.deletes.indexOf(sharedStore.id()));
        Assert.assertTrue(api.records.isEmpty());
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

    @Test
    @Story("رفض اعتبار السجل محذوفاً عندما تؤكد القراءة وجوده")
    public void rejectsMissingDeleteWhenOwnedRecordStillExists() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record unit = data.unit(1);
        api.missingDeleteForExisting = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.records.containsKey(unit.id()));
        api.missingDeleteForExisting = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("استعادة صنف أُنشئ من الواجهة رغم فشل الاستجابة")
    public void recoversUiItemCommittedBeforeFailedResponse() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record unit = data.unit(1), category = data.category();
        String name = "QA-SL-recover-" + java.util.UUID.randomUUID();
        Map<String, Object> values =
                SalesTestData.fields(
                        "arabicName", name, "mainUnitId", unit.id(), "categoryId", category.id());
        Map<String, Object> saved = new LinkedHashMap<>(values);
        saved.put("id", "Items-ui");
        api.records.put("Items-ui", saved);
        api.records.put(
                "shared-item",
                new LinkedHashMap<>(Map.of("id", "shared-item", "arabicName", "Existing item")));
        data.recoverAttemptedItems(List.of(Map.of("endpoint", "Items", "values", values)));
        data.close();
        Assert.assertEquals(api.deletes.get(0), "Items-ui");
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-item"));
    }

    @Test
    @Story("استعادة صنف غير صالح بعلامة الاختبار الإنجليزية الفريدة")
    public void recoversInvalidItemByUniqueEnglishMarker() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        var unit = data.unit(1);
        var category = data.category();
        String marker = "QA-SL-invalid-" + java.util.UUID.randomUUID();
        Map<String, Object> values =
                SalesTestData.fields(
                        "arabicName",
                        "",
                        "englishName",
                        marker,
                        "mainUnitId",
                        unit.id(),
                        "categoryId",
                        category.id());
        Map<String, Object> saved = new LinkedHashMap<>(values);
        saved.put("id", "Items-invalid");
        api.records.put("Items-invalid", saved);
        api.records.put(
                "shared-empty",
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "shared-empty",
                                "arabicName",
                                "",
                                "englishName",
                                "Existing item")));
        data.recoverAttemptedItems(List.of(Map.of("endpoint", "Items", "values", values)));
        data.close();
        Assert.assertEquals(api.deletes.get(0), "Items-invalid");
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-empty"));
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
    @Story("تأكيد فك ارتباط الفرع المنفذ بعد فقد الاستجابة")
    public void verifiesCommittedBranchUnlinkAfterLostResponse() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        data.store(1);
        api.timeoutBranchUnlinkAfterCommit = true;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(((List<?>) api.branch.get("stores")).size(), 1);
    }

    @Test
    @Story("إعادة محاولة الحذف غير المنفذ قبل حذف الحسابات التابعة")
    public void retriesUncommittedDeleteBeforeParentAccounts() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.timeOutStore = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record store = data.store(1);
        Object accountId = store.values().get("accountId");
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.subList(0, 2), List.of(store.id(), store.id()));
        Assert.assertEquals(api.deletes.get(2), accountId);
    }

    @Test
    @Story("عدم تكرار الحذف عندما نُفذ الطلب الذي انتهت مهلته")
    public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record store = data.store(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
    }

    @Test
    @Story("قبول قراءة نص فارغ بعد تأكيد تنفيذ الحذف المنتهية مهلته")
    public void acceptsEmptyStringReadAfterCommittedDeleteTimeout() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.timeOutStore = true;
        api.commitBeforeTimeout = true;
        api.emptyDeletedResponse = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record store = data.store(1);
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
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
    @Story("تنظيف الصنف الذي حُفظ رغم فشل طلب الإنشاء")
    public void cleansOwnedItemCommittedBeforeCreateFailure() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.failAfterItemInsert = true;
        api.records.put(
                "shared-item",
                new LinkedHashMap<>(Map.of("id", "shared-item", "arabicName", "Existing item")));
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record unit = data.fixture("unit", "Units", SalesTestData.fields());
        SalesTestData.Record category =
                data.fixture("category", "ItemCategories", SalesTestData.fields());
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        data.fixture(
                                "item",
                                "Items",
                                SalesTestData.fields(
                                        "mainUnitId", unit.id(), "categoryId", category.id())));
        Assert.assertEquals(api.records.size(), 4);
        data.close();
        Assert.assertTrue(api.deletes.get(0).startsWith("Items-"));
        Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-item"));
    }

    @Test
    @Story("قبول نموذج حركة افتراضي بعد التحقق من غياب الحركة")
    public void acceptsVerifiedMissingMovementDefaultDto() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.defaultDeletedDto = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record record =
                data.fixture("transaction", "ItemTransactions-Sales", SalesTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }

    @Test
    @Story("قبول نموذج بيانات أساسية افتراضي بعد التحقق من غياب السجل")
    public void acceptsVerifiedMissingMasterDefaultDto() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.defaultDeletedMasterDto = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record record = data.fixture("item", "Items", SalesTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
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

    @Test
    @io.qameta.allure.Story("مشاركة المراجع الثابتة بالصفحة وعزل بيانات كل حالة دون حذف بينها")
    public void pageScopeSharesOnlyStableReferences() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        data.beginScenario();
        var stable = data.unit(1);
        var first = data.item(1);
        var bank = data.bank();
        var safe = data.safe();
        var payment = data.payment();
        data.beginScenario();
        var second = data.item(1);
        Assert.assertNotEquals(first.id(), second.id());
        Assert.assertNotEquals(bank.id(), data.bank().id());
        Assert.assertNotEquals(
                bank.values().get("accountId"), data.bank().values().get("accountId"));
        Assert.assertNotEquals(safe.id(), data.safe().id());
        Assert.assertNotEquals(payment.id(), data.payment().id());
        Assert.assertEquals(data.unit(1).id(), stable.id());
        Assert.assertTrue(api.records.containsKey(first.id()));
        Assert.assertTrue(api.deletes.isEmpty());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @io.qameta.allure.Story("استكمال الحذف بعد انقطاع التشغيل من السجلات المتبقية فقط")
    public void interruptedPageCleanupResumesOnlyRemainingOwnedRecords() throws Exception {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        var data = new SalesTestData(api.driver());
        var first = data.unit(1);
        var last = data.unit(2);
        api.crashAtDelete = 2;
        Assert.expectThrows(Error.class, data::close);
        String suffix = last.name().substring(last.name().length() - 12);
        var rows =
                new org.json.JSONArray(
                        java.nio.file.Files.readString(
                                java.nio.file.Path.of(
                                        "target", "sales-fixtures", suffix + ".json")));
        Assert.assertTrue(
                rows.toList().stream().noneMatch(v -> last.id().equals(((Map<?, ?>) v).get("id"))));
        api.crashAtDelete = 0;
        SalesTestData.resume(api.driver(), suffix).close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(java.util.Collections.frequency(api.deletes, last.id()), 1);
    }

    @Test
    @io.qameta.allure.Story(
            "دورة الصفحة الفعلية تحتفظ ببيانات الحالات وتغلق النوافذ وتنظف المجموعة مع فشل الخطوة الأخيرة")
    public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception {

        SalesFixtureApiStub api = new SalesFixtureApiStub();
        try (var browser = new TestSupport.FixtureBrowser(api.driver())) {
            var broken = new java.util.concurrent.atomic.AtomicBoolean();
            var page =
                    new Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage(browser.driver) {
                        @Override
                        public String slCaptureMarker() {
                            return null;
                        }

                        @Override
                        public void slSettled() {
                            if (broken.get())
                                throw new org.openqa.selenium.WebDriverException(
                                        "Lost browser at page end");
                        }
                    };
            var harness = new SalesScenarioTest() {};
            browser.bind(harness);
            harness.beginSalesScenario();
            harness.page = page;
            var data = harness.data;
            var first = data.customer();
            browser.addAuxiliaryWindow();
            harness.cleanSalesScenario(null);
            harness.beginSalesScenario();
            harness.page = page;
            Assert.assertSame(harness.data, data);
            var second = data.customer();
            browser.addAuxiliaryWindow();
            harness.cleanSalesScenario(null);
            Assert.assertTrue(api.records.containsKey(first.id()));
            Assert.assertTrue(api.records.containsKey(second.id()));
            Assert.assertTrue(api.deletes.isEmpty());
            Assert.assertEquals(browser.closed, 2);
            broken.set(true);
            Assert.expectThrows(AssertionError.class, harness::retrySalesCleanup);
            Assert.assertTrue(api.records.isEmpty());
        }
    }
}
