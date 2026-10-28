/* شرح الملف: يراجع استرجاع حركة معلومة المعرف دون البحث في سجلات أخرى، مع بقاء تنظيفها عند مالك التجربة. */
package Invoice;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class SalesMovementRecoveryTest {
    @Test
    public void identifiedOwnedUpdateRecoversWithoutListingOtherDocuments() {
        SalesFixtureApiStub api = new SalesFixtureApiStub();
        try (SalesTestData data = new SalesTestData(api.driver())) {
            var record =
                    data.fixture("known-update", "ItemTransactions-Sales", SalesTestData.fields());
            var recovery =
                    new SalesMovementRecovery(
                            data,
                            List.of(record),
                            (url, values) -> {
                                throw new AssertionError(
                                        "Identified updates must not list unrelated documents");
                            });
            recovery.recover(
                    Map.of(
                            "endpoint",
                            record.endpoint(),
                            "id",
                            record.id(),
                            "name",
                            record.name(),
                            "values",
                            record.values()));
            Assert.assertEquals(data.read(record).get("notes"), record.name());
        }
        Assert.assertTrue(api.records.isEmpty());
    }
}
