/* شرح الملف: اختبارات تنظيف المخازن: عزل الحالات والتنظيف في نهاية المجموعة.
 */
package StoreTest;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StoreLifecycleCleanupTest {
    @Test
    @io.qameta.allure.Story(
            "دورة الصفحة الفعلية تحتفظ ببيانات الحالات وتغلق النوافذ وتنظف المجموعة مع فشل الخطوة الأخيرة")
    public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception {

        StoreFixtureApiStub api = new StoreFixtureApiStub();
        try (var browser = new TestSupport.FixtureBrowser(api.driver())) {
            var marker = new java.util.concurrent.atomic.AtomicReference<String>();
            var broken = new java.util.concurrent.atomic.AtomicBoolean();
            var fixturePage =
                    new Plugin_ERP.System_Modules.Login.StoreModel.StoreModulePage(browser.driver) {
                        @Override
                        public String smCaptureMarker() {
                            return marker.get();
                        }

                        @Override
                        public List<Map<String, Object>> smSaves() {
                            return List.of();
                        }

                        @Override
                        public List<Map<String, Object>> smAttempts() {
                            return List.of();
                        }

                        @Override
                        public void smSettled() {
                            if (broken.get())
                                throw new org.openqa.selenium.WebDriverException(
                                        "Lost browser at page end");
                        }

                        @Override
                        public String smDiagnosticMarkup() {
                            return "<case-failed/>";
                        }

                        @Override
                        public String smDiagnostics() {
                            return "{}";
                        }
                    };
            var harness =
                    new StoreScenarioTest() {
                        protected StoreMovementKind kind() {
                            return StoreMovementKind.TRANSFER;
                        }

                        protected Plugin_ERP.System_Modules.Login.StoreModel.StoreModulePage
                                movementPage() {
                            return fixturePage;
                        }
                    };
            browser.bind(harness);
            harness.beginStoreScenario();
            marker.set(harness.marker);
            var data = harness.data;
            var first = data.item(1);
            browser.addAuxiliaryWindow();
            harness.cleanStoreScenario(null);
            harness.beginStoreScenario();
            marker.set(harness.marker);
            Assert.assertSame(harness.data, data);
            var second = data.item(1);
            browser.addAuxiliaryWindow();
            var failed =
                    (org.testng.ITestResult)
                            Proxy.newProxyInstance(
                                    getClass().getClassLoader(),
                                    new Class<?>[] {org.testng.ITestResult.class},
                                    (p, m, a) -> m.getName().equals("isSuccess") ? false : null);
            harness.cleanStoreScenario(failed);
            Assert.assertTrue(api.records.containsKey(first.id()));
            Assert.assertTrue(api.records.containsKey(second.id()));
            Assert.assertTrue(api.deletes.isEmpty());
            Assert.assertEquals(browser.closed, 2);
            broken.set(true);
            Assert.expectThrows(AssertionError.class, harness::retryStoreCleanup);
            Assert.assertTrue(api.records.isEmpty());
            Assert.assertNull(harness.data);
        }
    }
}
