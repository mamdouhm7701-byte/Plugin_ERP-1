/* شرح الملف: اختبارات تنظيف الأصناف: عزل الحالات والتنظيف في نهاية المجموعة.
 */
package ProductsTest;

import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsLifecycleCleanupTest {
    @Test
    @io.qameta.allure.Story(
            "دورة الصفحة الفعلية تحتفظ ببيانات الحالات وتغلق النوافذ وتنظف المجموعة مع فشل الخطوة الأخيرة")
    public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception {

        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        try (var browser = new TestSupport.FixtureBrowser(api.driver())) {
            var marker = new java.util.concurrent.atomic.AtomicReference<String>();
            var broken = new java.util.concurrent.atomic.AtomicBoolean();
            var fixturePage =
                    new Plugin_ERP.System_Modules.Login.Products.ProductsModulePage(
                            browser.driver) {
                        @Override
                        public String prCaptureMarker() {
                            return marker.get();
                        }

                        @Override
                        public List<Map<String, Object>> prSaves() {
                            return List.of();
                        }

                        @Override
                        public List<Map<String, Object>> prAttempts() {
                            return List.of();
                        }

                        @Override
                        public void prSettled() {
                            if (broken.get())
                                throw new org.openqa.selenium.WebDriverException(
                                        "Lost browser at page end");
                        }
                    };
            var harness =
                    new ProductScenarioTest() {
                        protected Kind kind() {
                            return Kind.ITEM;
                        }

                        protected Plugin_ERP.System_Modules.Login.Products.ProductsModulePage
                                modulePage() {
                            return fixturePage;
                        }
                    };
            browser.bind(harness);
            harness.newProductScenario();
            marker.set(harness.marker);
            var data = harness.data;
            var first = data.item(1);
            browser.addAuxiliaryWindow();
            harness.cleanProductScenario();
            harness.newProductScenario();
            marker.set(harness.marker);
            Assert.assertSame(harness.data, data);
            var second = data.item(1);
            browser.addAuxiliaryWindow();
            harness.cleanProductScenario();
            Assert.assertTrue(api.records.containsKey(first.id()));
            Assert.assertTrue(api.records.containsKey(second.id()));
            Assert.assertTrue(api.deletes.isEmpty());
            Assert.assertEquals(browser.closed, 2);
            broken.set(true);
            Assert.expectThrows(AssertionError.class, harness::retryProductCleanup);
            Assert.assertTrue(api.records.isEmpty());
            Assert.assertNull(harness.data);
        }
    }
}
