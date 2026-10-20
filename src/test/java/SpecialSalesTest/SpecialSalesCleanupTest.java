package SpecialSalesTest;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@io.qameta.allure.Story("عزل البيانات والتنظيف الآمن")
public class SpecialSalesCleanupTest {
    @Test
    @io.qameta.allure.Description("العميل المستخدم في البيانات دائم وخاضع للضريبة ويسمح بالبيع الآجل؛ صلاحية البيع النقدي لا تجعله عميلاً نقدياً.")
    public void normalCustomerIsTaxableAndKeepsItsOwnAccount() {
        Api api = new Api();
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

    private static final class Api {
        final Map<String, Map<String, Object>> records = new LinkedHashMap<>();
        final List<String> deletes = new ArrayList<>();
        int crashAtDelete;
        boolean failAfterUnitInsert, failPendingLookup;
        String activeBranch = "branch-test";
        boolean timeOutStore;
        boolean commitBeforeTimeout;
        boolean rejectStore;
        boolean emptyDeletedResponse;
        boolean failAfterItemInsert;
        boolean defaultDeletedDto;
        boolean defaultDeletedMasterDto;

        boolean missingDelete400;
        boolean missingDeleteForExisting;
        boolean rejectBranchUnlink;
        boolean timeoutBranchUnlinkAfterCommit;
        Map<String, Object> branch =
                new LinkedHashMap<>(
                        Map.of(
                                "id",
                                "branch-test",
                                "defaultStoreId",
                                "existing-store",
                                "phone",
                                "original",
                                "stores",
                                new ArrayList<>(List.of(Map.of("storeId", "existing-store")))));

        WebDriver driver() {
            Object timeouts =
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebDriver.Timeouts.class},
                            (proxy, method, args) -> proxy);
            Object options =
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebDriver.Options.class},
                            (proxy, method, args) -> timeouts);
            return (WebDriver)
                    Proxy.newProxyInstance(
                            getClass().getClassLoader(),
                            new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                            (proxy, method, args) -> {
                                if (method.getName().equals("executeScript")) {
                                    String code = (String) args[0];
                                    if (code.equals("return location.origin")) return "offline";
                                    return code.startsWith("return document.querySelector")
                                            ? activeBranch
                                            : true;
                                }
                                if (method.getName().equals("manage")) return options;
                                if (method.getName().equals("executeAsyncScript"))
                                    return request((Object[]) args[1]);
                                if (method.getName().equals("toString"))
                                    return "Fixture cleanup regression driver";
                                return null;
                            });
        }

        @SuppressWarnings("unchecked")
        Object request(Object[] args) {
            String action = (String) args[0], endpoint = (String) args[1];
            Map<String, Object> values = (Map<String, Object>) args[2];
            if (endpoint.equals("Branches")) {
                if (action.equals("get")) return Map.of("entity", new LinkedHashMap<>(branch));
                if (!action.equals("update"))
                    throw new AssertionError("Never delete an existing branch");
                boolean unlink =
                        ((List<?>) values.get("stores")).size()
                                < ((List<?>) branch.get("stores")).size();
                if (unlink && rejectBranchUnlink)
                    return Map.of("error", "Cannot unlink owned store", "status", 400);
                branch = new LinkedHashMap<>(values);
                if (unlink && timeoutBranchUnlinkAfterCommit) {
                    timeoutBranchUnlinkAfterCommit = false;
                    throw new TimeoutException("Lost unlink response after commit");
                }
                return Map.of("id", "branch-test");
            }
            if (action.equals("create")) {
                String id = endpoint + "-" + (records.size() + 1);
                Map<String, Object> saved = new LinkedHashMap<>(values);
                if (endpoint.equals("Tables")) {
                    if (!(values.get("chairsCount") instanceof String))
                        return Map.of("error", "Invalid chairsCount type", "status", 400);
                    saved.remove("arabicName");
                    saved.remove("englishName");
                }
                saved.put("id", id);
                records.put(id, saved);
                if (endpoint.equals("Units") && failAfterUnitInsert)
                    return Map.of("error", "Lost unit create response", "status", 500);
                if (endpoint.equals("Items") && failAfterItemInsert)
                    return Map.of("error", "Failed after insert", "status", 500);
                return Map.of("id", id);
            }
            if (action.equals("get") && endpoint.startsWith("Items/Summary"))
                return Map.of("entity", new ArrayList<>(records.values()));
            if (action.equals("get") && endpoint.startsWith("Units?")) {
                if (failPendingLookup)
                    return Map.of("error", "Recovery lookup unavailable", "status", 503);
                var rows =
                        records.values().stream()
                                .filter(r -> r.get("id").toString().startsWith("Units-"))
                                .toList();
                return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
            }
            String id = values.get("id").toString();
            if (action.equals("get")) {
                return records.containsKey(id)
                        ? Map.of("entity", records.get(id))
                        : defaultDeletedMasterDto
                                ? Map.of(
                                        "entity",
                                        Map.of("subUnits", List.of(), "type", "RawMaterial"))
                                : defaultDeletedDto
                                        ? Map.of("entity", Map.of("conversionRates", List.of()))
                                        : emptyDeletedResponse
                                                ? Map.of("entity", "")
                                                : Map.of(
                                                        "error",
                                                        "Missing owned record",
                                                        "status",
                                                        404);
            }
            deletes.add(id);
            if (crashAtDelete > 0 && deletes.size() == crashAtDelete)
                throw new Error("Simulated process interruption");
            if (missingDeleteForExisting || (missingDelete400 && !records.containsKey(id)))
                return Map.of("error", "Common.ObjectNotFound", "status", 400);
            if (endpoint.equals("Stores")) {
                if (rejectStore) return Map.of("error", "Record is in use", "status", 400);
                if (timeOutStore) {
                    timeOutStore = false;
                    if (commitBeforeTimeout) records.remove(id);
                    throw new TimeoutException("Simulated lost DELETE response");
                }
            }
            records.remove(id);
            return Map.of("ok", true);
        }
    }

    @Test
    @io.qameta.allure.Description(
            "مشاركة الوحدات والمخازن وعزل الأصناف والعملاء بين الحالات حتى تنظيف الصفحة.")
    public void sharesReferencesAndIsolatesCases() {
        Api api = new Api();
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
        Api api = new Api();
        api.branch.put("defaultCustomerId", "existing-customer");
        try (var data = new SpecialSalesTestData(api.driver())) {
            data.beginScenario();
            var customer = data.customer();
            var defaults = data.configureOwnedDefaults(customer);
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
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
        Api api = new Api();
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
