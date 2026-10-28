/* شرح الملف: اختبارات تنظيف المبيعات: حذف البيانات المملوكة والتحقق من غيابها.
 */
package Invoice;

import io.qameta.allure.Story;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SalesCore2CleanupTest {
    @Test
    @Story("رفض اعتبار السجل محذوفاً عندما تؤكد القراءة وجوده")
    public void rejectsMissingDeleteWhenOwnedRecordStillExists() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record unit = data.unit(1);
        api.missingDeleteForExisting = true;
        Assert.expectThrows(AssertionError.class, data::close);
        Assert.assertTrue(api.records.containsKey(unit.id()));
        api.missingDeleteForExisting = false;
        data.close();
        Assert.assertTrue(api.records.isEmpty());
    }

    @Test
    @Story("قبول نموذج حركة افتراضي بعد التحقق من غياب الحركة")
    public void acceptsVerifiedMissingMovementDefaultDto() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.defaultDeletedDto = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record record =
                data.fixture("transaction", "ItemTransactions-Sales", SalesTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }

    @Test
    @Story("قبول نموذج بيانات أساسية افتراضي بعد التحقق من غياب السجل")
    public void acceptsVerifiedMissingMasterDefaultDto() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        api.defaultDeletedMasterDto = true;
        SalesTestData data = new SalesTestData(api.driver());
        SalesTestData.Record record = data.fixture("item", "Items", SalesTestData.fields());
        data.close();
        Assert.assertTrue(api.records.isEmpty());
        Assert.assertEquals(api.deletes, List.of(record.id()));
    }
}
