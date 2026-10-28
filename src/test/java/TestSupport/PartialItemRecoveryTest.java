/* شرح الملف: يراجع استرجاع صنف حُفظ جزئيًا دون تبني صنف آخر أو إخفاء تغير الوحدة أو المجموعة. */
package TestSupport;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class PartialItemRecoveryTest {
    private record Row(String endpoint, Object id, String name, Map<String, Object> values)
            implements OwnedFixtureRecord {}

    private final Map<String, Object> values =
            Map.of("arabicName", "QA-owned", "mainUnitId", "unit", "categoryId", "category");

    private PartialItemRecovery.Access<Row> access(
            Object entity, Map<String, Object> stored, List<Row> tracked) {
        return new PartialItemRecovery.Access<>(
                "QA-",
                tracked,
                (url, query) -> Map.of("entity", entity),
                Row::new,
                row -> stored,
                tracked::add);
    }

    @Test
    public void exactPartialItemIsTrackedOnceWithItsOriginalDependencies() {
        var tracked = new ArrayList<Row>();
        var context =
                access(List.of(Map.of("id", "item", "arabicName", "QA-owned")), values, tracked);
        PartialItemRecovery.recover(
                List.of(Map.of("endpoint", "Items", "values", values)), context);
        PartialItemRecovery.recover(
                List.of(Map.of("endpoint", "Items", "values", values)), context);
        Assert.assertEquals(tracked.size(), 1);
        Assert.assertEquals(tracked.getFirst().id(), "item");
    }

    @Test
    public void changedUnitOrCategoryNeverRegistersItemAsOwned() {
        for (String field : List.of("mainUnitId", "categoryId")) {
            var changed = new LinkedHashMap<String, Object>(values);
            changed.put(field, "foreign");
            var tracked = new ArrayList<Row>();
            Assert.expectThrows(
                    AssertionError.class,
                    () ->
                            PartialItemRecovery.recover(
                                    List.of(Map.of("endpoint", "Items", "values", values)),
                                    access(
                                            List.of(Map.of("id", "item", "arabicName", "QA-owned")),
                                            changed,
                                            tracked)));
            Assert.assertTrue(tracked.isEmpty());
        }
    }

    @Test
    public void duplicateCandidatesOrMalformedSummaryDoNotPassRecovery() {
        var tracked = new ArrayList<Row>();
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        PartialItemRecovery.recover(
                                List.of(Map.of("endpoint", "Items", "values", values)),
                                access(
                                        List.of(
                                                Map.of("id", "a", "arabicName", "QA-owned"),
                                                Map.of("id", "b", "arabicName", "QA-owned")),
                                        values,
                                        tracked)));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        PartialItemRecovery.recover(
                                List.of(Map.of("endpoint", "Items", "values", values)),
                                access(Map.of("unexpected", true), values, tracked)));
        Assert.assertTrue(tracked.isEmpty());
    }

    @Test
    public void unrelatedNamesAndOtherEndpointsAreIgnored() {
        var tracked = new ArrayList<Row>();
        PartialItemRecovery.recover(
                List.of(
                        Map.of("endpoint", "Other"),
                        Map.of("endpoint", "Items", "values", Map.of("arabicName", "foreign"))),
                new PartialItemRecovery.Access<Row>(
                        "QA-",
                        tracked,
                        (u, v) -> {
                            throw new AssertionError("No application reads expected");
                        },
                        Row::new,
                        r -> Map.of(),
                        tracked::add));
        Assert.assertTrue(tracked.isEmpty());
    }
}
