/* شرح الملف: اختبارات تنظيف المشتريات: استقلال بيانات الحالات ومراجعة التنظيف عند نهاية المجموعة.
 */
package Purches;

import io.qameta.allure.Story;
import java.lang.reflect.Proxy;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PurchaseLifecycleCleanupTest {
    @Test
    @Story(
            "دورة الصفحة تحتفظ ببيانات الحالتين الناجحة والفاشلة وتغلق النوافذ ثم تنظف المجموعة مرة واحدة")
    public void actualPageLifecycleDefersCleanupUntilGroupEndEvenAfterFailure() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        var closes = new java.util.concurrent.atomic.AtomicInteger();
        var brokenBrowser = new java.util.concurrent.atomic.AtomicBoolean();
        var harness =
                new PurchaseScenarioTest() {
                    protected PurchaseDocumentKind kind() {
                        return PurchaseDocumentKind.INVOICE;
                    }
                };
        harness.data = data;
        harness.marker = "QA-PU-lifecycle";
        harness.mainWindow = "main";
        harness.page =
                new Plugin_ERP.System_Modules.Login.purches.PurchaseModulePage(api.driver()) {
                    @Override
                    public void puCloseAuxiliaryWindows(String main) {
                        closes.incrementAndGet();
                    }

                    @Override
                    public void puSettled() {
                        if (brokenBrowser.get())
                            throw new org.openqa.selenium.WebDriverException(
                                    "Browser unavailable at group end");
                    }

                    @Override
                    public String puCaptureMarker() {
                        return null;
                    }

                    @Override
                    public String puDiagnosticMarkup() {
                        return "<fixture-failure/>";
                    }

                    @Override
                    public String puDiagnostics() {
                        return "{}";
                    }
                };
        data.beginScenario();
        var first = data.item(1);
        var store = data.store(1);
        harness.finishPurchaseCase(null);
        Assert.assertTrue(api.records.containsKey(first.id()));
        Assert.assertTrue(api.deletes.isEmpty());
        data.beginScenario();
        var second = data.item(1);
        var failed =
                (org.testng.ITestResult)
                        Proxy.newProxyInstance(
                                getClass().getClassLoader(),
                                new Class<?>[] {org.testng.ITestResult.class},
                                (proxy, method, args) ->
                                        method.getName().equals("isSuccess") ? false : null);
        harness.finishPurchaseCase(failed);
        Assert.assertTrue(api.records.containsKey(first.id()));
        Assert.assertTrue(api.records.containsKey(second.id()));
        Assert.assertEquals(data.store(1).id(), store.id());
        Assert.assertTrue(api.deletes.isEmpty());
        brokenBrowser.set(true);
        Assert.expectThrows(AssertionError.class, harness::cleanPurchasePage);
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertNull(harness.data);
        Assert.assertEquals(closes.get(), 3);
        int deletes = api.deletes.size();
        harness.cleanPurchasePage();
        Assert.assertEquals(api.deletes.size(), deletes);
    }

    @Test
    @Story("مشاركة بيانات الصفحة الثابتة مع عزل الأصناف والموردين والأرصدة بين الحالات")
    public void pageFixturesAreSharedButCaseBalancesStayIsolated() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        var data = new PurchaseTestData(api.driver());
        data.beginScenario();
        var first = data.seedPurchase(1, "Purchases", "2026-01-01", "3", "0");
        var firstItem = data.item(1);
        var firstCustomer = data.supplier();
        var sharedStore = data.store(1);
        var sharedUnit = data.unit(1);
        var firstBank = data.bank();
        var firstPayment = data.payment();
        var firstSafe = data.safe();
        data.beginScenario();
        var second = data.seedPurchase(1, "Purchases", "2026-01-01", "1", "0");
        var secondItem = data.item(1);
        var secondCustomer = data.supplier();
        Assert.assertNotEquals(first.id(), second.id());
        Assert.assertNotEquals(firstItem.id(), secondItem.id());
        Assert.assertNotEquals(firstCustomer.id(), secondCustomer.id());
        Assert.assertNotEquals(
                firstCustomer.values().get("accountId"), secondCustomer.values().get("accountId"));
        Assert.assertNotEquals(data.bank().id(), firstBank.id());
        Assert.assertNotEquals(
                data.bank().values().get("accountId"), firstBank.values().get("accountId"));
        Assert.assertNotEquals(data.payment().id(), firstPayment.id());
        Assert.assertNotEquals(data.safe().id(), firstSafe.id());
        Assert.assertNotEquals(
                data.safe().values().get("accountId"), firstSafe.values().get("accountId"));
        Assert.assertEquals(data.store(1).id(), sharedStore.id());
        Assert.assertEquals(data.unit(1).id(), sharedUnit.id());
        Assert.assertTrue(api.deletes.isEmpty(), "No cleanup is allowed between cases on the page");
        Assert.assertTrue(api.records.containsKey(first.id()));
        data.close();
        Assert.assertTrue(api.deletes.indexOf(first.id()) < api.deletes.indexOf(sharedStore.id()));
        Assert.assertTrue(api.deletes.indexOf(second.id()) < api.deletes.indexOf(sharedStore.id()));
        Assert.assertTrue(api.records.isEmpty());
    }
}
