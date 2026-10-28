/* شرح الملف: محاكاة رفض إنشاء مخزن بسبب الترخيص ثم التأكد من منع الإنشاء التالي والسماح بالتنظيف.
 * فائدته: يثبت أن حماية السعة تعمل عند طلب التجهيز الفعلي مع الاحتفاظ بأول خطأ من البرنامج.
 */
package Invoice;

import java.util.*;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public final class CapacityFixtureBoundaryTest {
    @Test
    public void firstCapacityFailureRemainsOriginalAndNextCreateNeverReachesTheApi() {
        String previous = System.getProperty("allure.run.id");
        System.setProperty("allure.run.id", "capacity-api-" + UUID.randomUUID());
        try {
            var stub = new SalesFixtureApiStub();
            stub.capacityExceeded = true;
            stub.records.put("owned-store", new LinkedHashMap<>(Map.of("id", "owned-store")));
            var api = new SalesFixtureApi(stub.driver(), "offline", "branch-test", () -> {});
            var original =
                    Assert.expectThrows(
                            SalesFixtureApi.ApiFailure.class,
                            () -> api.call("create", "Stores", Map.of()));
            Assert.assertEquals(original.status, 400);
            Assert.assertTrue(
                    original.getMessage().contains("LicenseLimitationExceededErrorMessage"));
            Assert.expectThrows(SkipException.class, () -> api.call("create", "Stores", Map.of()));
            Assert.assertEquals(stub.createAttempts, 1);
            api.call("get", "Branches", Map.of("id", "branch-test"));
            api.call("delete", "Stores", Map.of("id", "owned-store"));
            Assert.assertFalse(stub.records.containsKey("owned-store"));
        } finally {
            if (previous == null) System.clearProperty("allure.run.id");
            else System.setProperty("allure.run.id", previous);
        }
    }
}
