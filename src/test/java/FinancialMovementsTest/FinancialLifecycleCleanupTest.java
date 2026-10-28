/* شرح الملف: اختبارات تنظيف الحركات المالية: عزل الحالات والتنظيف في نهاية المجموعة.
 */
package FinancialMovementsTest;

import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FinancialLifecycleCleanupTest {
    @Test
    @io.qameta.allure.Story(
            "دورة الصفحة الفعلية تحتفظ ببيانات الحالات وتغلق النوافذ وتنظف المجموعة مع فشل الخطوة الأخيرة")
    public void actualCallbacksKeepCasesUntilPageEndEvenAfterFailure() throws Exception {

        FinancialFixtureApiStub api = new FinancialFixtureApiStub();
        try (var browser = new TestSupport.FixtureBrowser(api.driver())) {
            var marker = new java.util.concurrent.atomic.AtomicReference<String>();
            var broken = new java.util.concurrent.atomic.AtomicBoolean();
            var fixturePage =
                    new Plugin_ERP.System_Modules.Login.FinancialMovements.FinancialMovementPage(
                            browser.driver) {
                        @Override
                        public String captureMarker() {
                            if (broken.get())
                                throw new org.openqa.selenium.WebDriverException(
                                        "Lost browser at page end");
                            return marker.get();
                        }

                        @Override
                        public List<Map<String, Object>> capturedSaves() {
                            return List.of();
                        }

                        @Override
                        public List<Map<String, Object>> capturedAttempts() {
                            return List.of();
                        }

                        @Override
                        public void awaitSavesSettled() {}
                    };
            var harness =
                    new FinancialScenarioTest() {
                        protected Plugin_ERP.System_Modules.Login.FinancialMovements
                                        .FinancialMovementKind
                                kind() {
                            return Plugin_ERP.System_Modules.Login.FinancialMovements
                                    .FinancialMovementKind.RECEIPT;
                        }

                        protected Plugin_ERP.System_Modules.Login.FinancialMovements
                                        .FinancialMovementPage
                                movementPage() {
                            return fixturePage;
                        }
                    };
            browser.bind(harness);
            harness.newFinancialScenario();
            marker.set(harness.marker);
            var data = harness.data;
            var first = data.customer(1);
            browser.addAuxiliaryWindow();
            harness.cleanFinancialScenario();
            harness.newFinancialScenario();
            marker.set(harness.marker);
            Assert.assertSame(harness.data, data);
            var second = data.customer(1);
            browser.addAuxiliaryWindow();
            harness.cleanFinancialScenario();
            Assert.assertTrue(api.records.containsKey(first.id()));
            Assert.assertTrue(api.records.containsKey(second.id()));
            Assert.assertTrue(api.deletes.isEmpty());
            Assert.assertEquals(browser.closed, 2);
            broken.set(true);
            Assert.expectThrows(AssertionError.class, harness::retryFinancialCleanup);
            Assert.assertTrue(api.records.isEmpty());
            Assert.assertNull(harness.data);
        }
    }
}
