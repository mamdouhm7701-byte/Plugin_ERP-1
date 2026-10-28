/* شرح الملف: مراجعة إثبات ملكية الحركة قبل تنظيفها، خصوصًا عندما لا تعيد الشاشة ملاحظة التجربة. */
package TestSupport;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureOwnershipTest {
    private record Row(String endpoint, Object id, String name, Map<String, Object> values)
            implements OwnedFixtureRecord {}

    private final Row movement =
            new Row(
                    "Sales",
                    "invoice",
                    "QA-SL-invoice",
                    Map.of(
                            "storeId",
                            "store",
                            "lines",
                            List.of(Map.of("itemId", "item", "unitId", "unit"))));
    private final List<Row> owned =
            List.of(
                    new Row("Stores", "store", "QA-SL-store", Map.of()),
                    new Row("Items", "item", "QA-SL-item", Map.of()));

    private Map<String, Object> stored(String store, String item, String unit) {
        return Map.of("storeId", store, "lines", List.of(Map.of("itemId", item, "unitId", unit)));
    }

    private boolean owns(Row row, Map<String, Object> stored, List<Row> records) {
        return FixtureOwnership.owns("QA-SL-", Set.of("Sales"), row, stored, records);
    }

    @Test
    public void unchangedMovementCanBeVerifiedByOwnedStoreAndExactItemAndUnit() {
        Assert.assertTrue(owns(movement, stored("store", "item", "unit"), owned));
    }

    @Test
    public void differentStoreItemOrUnitNeverPassesFallbackOwnership() {
        Assert.assertFalse(owns(movement, stored("foreign", "item", "unit"), owned));
        Assert.assertFalse(owns(movement, stored("store", "foreign", "unit"), owned));
        Assert.assertFalse(owns(movement, stored("store", "item", "foreign"), owned));
    }

    @Test
    public void fallbackRequiresBothStoreAndItemStillTrackedAsOwned() {
        Assert.assertFalse(owns(movement, stored("store", "item", "unit"), List.of(owned.get(0))));
        Assert.assertFalse(owns(movement, stored("store", "item", "unit"), List.of(owned.get(1))));
        var foreignItem = new Row("Items", "item", "QA-PU-item", Map.of());
        Assert.assertFalse(
                owns(
                        movement,
                        stored("store", "item", "unit"),
                        List.of(owned.get(0), foreignItem)));
    }

    @Test
    public void exactMarkerWorksButPartialMarkerAndOtherFixturePrefixDoNot() {
        Assert.assertTrue(owns(movement, Map.of("notes", "QA-SL-invoice"), List.of()));
        Assert.assertFalse(owns(movement, Map.of("notes", "QA-SL-invoice-extra"), List.of()));
        Assert.assertFalse(
                owns(
                        new Row("Sales", "id", "QA-PU-invoice", Map.of()),
                        Map.of("notes", "QA-PU-invoice"),
                        List.of()));
    }

    @Test
    public void malformedOrEmptyLinesCannotProveOwnership() {
        Assert.assertFalse(owns(movement, Map.of("storeId", "store", "lines", List.of()), owned));
        Assert.assertFalse(
                owns(movement, Map.of("storeId", "store", "lines", List.of("invalid")), owned));
        Assert.assertFalse(
                owns(
                        new Row("Sales", "id", "QA-SL-invalid", Map.of("storeId", "store")),
                        stored("store", "item", "unit"),
                        owned));
    }

    @Test
    public void nonMovementCannotUseMovementFallback() {
        Assert.assertFalse(
                owns(
                        new Row("Items", "id", "QA-SL-item", movement.values()),
                        stored("store", "item", "unit"),
                        owned));
    }
}
