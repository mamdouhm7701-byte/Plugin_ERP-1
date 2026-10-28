/* شرح الملف: اختبارات تنظيف الأصناف: حذف بيانات التجربة والتحقق من غيابها.
 */
package ProductsTest;

import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsCleanupTest {
    @Test
    public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.missingDelete400 = true;
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record store = data.store(1);
        api.records.remove(store.id());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void rejectsMissingDeleteWhenOwnedRecordStillExists() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record unit = data.unit(1);
        api.missingDeleteForExisting = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.records.containsKey(unit.id()));
        api.missingDeleteForExisting = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    public void acceptsVerifiedMissingProductDefaultDto() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.defaultDeletedDto = true;
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record record =
                data.fixture("transaction", "PriceLists", ProductsTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }

    @Test
    public void acceptsVerifiedMissingMasterDefaultDto() {
        ProductsFixtureApiStub api = new ProductsFixtureApiStub();
        api.defaultDeletedMasterDto = true;
        ProductsTestData data = new ProductsTestData(api.driver());
        ProductsTestData.Record record = data.fixture("item", "Items", ProductsTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }
}
