/* شرح الملف: مراجعة ترتيب تنظيف البيانات، وحفظ السجل الأب إذا تعذّر حذف سجل تابع له، ثم استكمال المحاولة لاحقًا. */
package TestSupport;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureCleanupTest {
    private record Row(String endpoint, Object id, String name, Map<String, Object> values)
            implements OwnedFixtureRecord {}

    @Test
    public void removesOwnedRowsInProvidedOrderAndPersistsProgress() {
        var parent = new Row("Items", "parent", "QA-parent", Map.of());
        var child = new Row("Sales", "child", "QA-child", Map.of("itemId", "parent"));
        var records = new ArrayList<>(List.of(parent, child));
        var fixtures = new LinkedHashMap<>(Map.of("parent", parent, "child", child));
        var removed = new ArrayList<Object>();
        FixtureCleanup.run(
                records, fixtures, List.of(child, parent), r -> removed.add(r.id()), () -> {});
        Assert.assertEquals(removed, List.of("child", "parent"));
        Assert.assertTrue(records.isEmpty());
        Assert.assertTrue(fixtures.isEmpty());
    }

    @Test
    public void failedChildKeepsItsParentUntilRetryAndNeverDeletesOtherRowsTwice() {
        var parent = new Row("Items", "parent", "QA-parent", Map.of());
        var child = new Row("Sales", "child", "QA-child", Map.of("itemId", "parent"));
        var independent = new Row("Units", "unit", "QA-unit", Map.of());
        var records = new ArrayList<>(List.of(parent, child, independent));
        var fixtures =
                new LinkedHashMap<>(Map.of("parent", parent, "child", child, "unit", independent));
        var removed = new ArrayList<Object>();
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureCleanup.run(
                                records,
                                fixtures,
                                List.of(independent, child, parent),
                                r -> {
                                    if (r.equals(child)) throw new AssertionError("blocked");
                                    removed.add(r.id());
                                },
                                () -> {}));
        Assert.assertEquals(removed, List.of("unit"));
        Assert.assertEquals(records, List.of(parent, child));
        Assert.assertFalse(fixtures.containsKey("unit"));
        FixtureCleanup.run(
                records, fixtures, List.of(child, parent), r -> removed.add(r.id()), () -> {});
        Assert.assertEquals(removed, List.of("unit", "child", "parent"));
        Assert.assertTrue(records.isEmpty());
    }

    @Test
    public void cyclicDependenciesRemainRecordedWhenNeitherCanBeRemoved() {
        var first = new Row("Items", "first", "QA-first", Map.of("otherId", "second"));
        var second = new Row("Items", "second", "QA-second", Map.of("otherId", "first"));
        var records = new ArrayList<>(List.of(first, second));
        var fixtures = new LinkedHashMap<>(Map.of("first", first, "second", second));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureCleanup.run(
                                records,
                                fixtures,
                                List.of(first, second),
                                r -> {
                                    throw new AssertionError("blocked");
                                },
                                () -> {}));
        Assert.assertEquals(records.size(), 2);
        Assert.assertEquals(fixtures.size(), 2);
    }
}
