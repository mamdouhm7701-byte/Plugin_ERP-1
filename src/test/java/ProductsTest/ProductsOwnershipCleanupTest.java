/* شرح الملف: اختبارات تنظيف الأصناف: حماية هوية الفرع وملكية السجلات قبل الحذف.
 */
package ProductsTest;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsOwnershipCleanupTest {
    @Test
    @io.qameta.allure.Story("رفض استرجاع أو حذف سجلات المجموعة من فرع آخر")
    public void refusesRecoveryInAnotherBranch() throws Exception {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        var data = new ProductsTestData(api.driver());
        data.unit(1);
        api.activeBranch = "another-branch";
        Assert.expectThrows(
                AssertionError.class,
                () -> ProductsTestData.resume(api.driver(), data.journalSuffix()));
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.deletes.isEmpty());
        api.activeBranch = "branch-test";
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }
}
