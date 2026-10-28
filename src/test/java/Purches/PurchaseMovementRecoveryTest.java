/* شرح الملف: يراجع استرجاع حركة معلومة المعرف دون البحث في سجلات أخرى، مع بقاء تنظيفها عند مالك التجربة. */
package Purches;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class PurchaseMovementRecoveryTest {
    @Test
    public void identifiedOwnedUpdateRecoversWithoutListingOtherDocuments() {
        PurchaseFixtureApiStub api = new PurchaseFixtureApiStub();
        try (PurchaseTestData data = new PurchaseTestData(api.driver())) {
            var record = data.fixture("known-update", "Purchases", PurchaseTestData.fields());
            var recovery =
                    new PurchaseMovementRecovery(
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
