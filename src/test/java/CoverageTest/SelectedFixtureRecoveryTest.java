/* شرح الملف: اختبار تنظيف بيانات تجربة سابقة محددة والتأكد من حفظ البيانات وظهور النتيجة المطلوبة.
 */
package CoverageTest;

import Plugin_ERP.Tests.LoginTest.BaseTest;
import TestSupport.BasicDataFixtures;
import org.testng.annotations.Test;

public final class SelectedFixtureRecoveryTest extends BaseTest {
    @Test
    public void recoverChosenDocumentsBeforeTheirParents() throws Exception {
        if (ids("coverage.recover.basic").length
                        + ids("coverage.recover.store").length
                        + ids("coverage.recover.sales").length
                == 0)
            throw new org.testng.SkipException(
                    "Choose explicit journal IDs using coverage.recover.basic/store/sales");
        for (String suffix : ids("coverage.recover.basic"))
            BasicDataFixtures.resume(driver, suffix).close();
        recover("coverage.recover.store", "StoreTest.StoreTestData");
        recover("coverage.recover.sales", "Invoice.SalesTestData");
    }

    private String[] ids(String property) {
        String raw = System.getProperty(property, "").trim();
        return raw.isBlank() ? new String[0] : raw.split(",");
    }

    private void recover(String property, String owner) throws Exception {
        for (String suffix : ids(property)) {
            var method =
                    Class.forName(owner)
                            .getDeclaredMethod(
                                    "resume", org.openqa.selenium.WebDriver.class, String.class);
            method.setAccessible(true);
            try {
                ((AutoCloseable) method.invoke(null, driver, suffix)).close();
            } catch (java.lang.reflect.InvocationTargetException failure) {
                throw new AssertionError("Selected fixture recovery failed", failure.getCause());
            }
        }
    }
}
