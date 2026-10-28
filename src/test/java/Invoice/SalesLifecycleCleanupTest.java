/* شرح الملف: اختبارات تنظيف المبيعات: استقلال بيانات الحالات ومراجعة التنظيف عند نهاية المجموعة.
 */
package Invoice;

import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SalesLifecycleCleanupTest {
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
