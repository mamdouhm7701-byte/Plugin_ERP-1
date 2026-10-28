/* شرح الملف: يراجع استرجاع حركة معلومة المعرف دون البحث في سجلات أخرى، مع بقاء تنظيفها عند مالك التجربة. */
package StoreTest;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class StoreMovementRecoveryTest {
    @Test
    public void identifiedOwnedUpdateRecoversWithoutListingOtherDocuments() {
        StoreFixtureApiStub api = new StoreFixtureApiStub();
        try (StoreTestData data = new StoreTestData(api.driver())) {
            var record = data.fixture("known-update", "AdditionOrders", StoreTestData.fields());
            var recovery =
                    new StoreMovementRecovery(
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
            Assert.assertEquals(data.read(record).get("notes"), record.values().get("notes"));
        }
        Assert.assertTrue(api.records.isEmpty());
    }
}
