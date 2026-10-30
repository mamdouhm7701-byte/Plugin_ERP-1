/* شرح الملف: يثبت أن مشاركة قراءة الفرع محصورة في الفحص الجاري ولا تتسرب بين الطلبات أو المتصفحات أو بعد الاستثناء. */
package Plugin_ERP.BranchProfiles;

import java.lang.reflect.Proxy;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class VerificationSnapshotTest {
    private WebDriver driver() {
        return (WebDriver)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebDriver.class},
                        (p, m, a) -> null);
    }

    private BranchSnapshot snapshot() {
        return BranchSnapshot.from(
                Map.of(
                        "origin",
                        "https://erp.example",
                        "branchId",
                        "owned",
                        "activityType",
                        "Restaurant",
                        "discountLevel",
                        "Transaction",
                        "taxPolicy",
                        "NetBased",
                        "stocktakingMethod",
                        "Periodic"));
    }

    @Test
    public void verifiedReadIsReusedOnlyDuringItsOwnGuard() {
        var driver = driver();
        var another = driver();
        VerificationSnapshot.verify(
                driver,
                () -> {
                    Assert.assertNull(VerificationSnapshot.current(driver));
                    VerificationSnapshot.verified(driver, snapshot());
                    Assert.assertEquals(
                            TestSupport.FixtureContextSnapshot.read(driver).branchId(), "owned");
                    Assert.assertNull(VerificationSnapshot.current(another));
                });
        Assert.assertNull(VerificationSnapshot.current(driver));
        VerificationSnapshot.verify(
                driver, () -> Assert.assertNull(VerificationSnapshot.current(driver)));
    }

    @Test
    public void exceptionAndNestedVerificationCannotLeaveAStaleRead() {
        var driver = driver();
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        VerificationSnapshot.verify(
                                driver,
                                () -> {
                                    VerificationSnapshot.verified(driver, snapshot());
                                    VerificationSnapshot.verify(
                                            driver,
                                            () ->
                                                    Assert.assertNull(
                                                            VerificationSnapshot.current(driver)));
                                    Assert.assertNotNull(VerificationSnapshot.current(driver));
                                    throw new AssertionError("verification failed");
                                }));
        Assert.assertNull(VerificationSnapshot.current(driver));
    }
}
