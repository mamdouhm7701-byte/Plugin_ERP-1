/* شرح الملف: يراجع العثور على سجل التجربة بالاسم الكامل، واكتشاف ارتباطه بسجلات أخرى قبل التنظيف. */
package TestSupport;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureRecordSearchTest {
    @Test
    public void detectsReferencesInsideNestedMapsAndListsWithoutMatchingPartialIds() {
        Object data = Map.of("rows", List.of(Map.of("itemId", "owned-id"), Map.of("other", 42)));
        Assert.assertTrue(FixtureRecordSearch.references(data, "owned-id"));
        Assert.assertFalse(FixtureRecordSearch.references(data, "owned"));
        Assert.assertFalse(FixtureRecordSearch.references(data, "42"));
        Assert.assertFalse(FixtureRecordSearch.references(null, "owned-id"));
    }

    @Test
    public void findsExactOwnedNamesWithIdsAndPreservesDuplicateCandidates() {
        var found = new ArrayList<Map<String, Object>>();
        FixtureRecordSearch.findOwned(
                List.of(
                        Map.of("id", "a", "arabicName", "QA-owned"),
                        Map.of("id", "b", "notes", "QA-owned"),
                        Map.of("id", "c", "englishName", "QA-owned-other"),
                        Map.of("arabicName", "QA-owned")),
                "QA-owned",
                found);
        Assert.assertEquals(found.size(), 2);
        Assert.assertEquals(found.get(0).get("id"), "a");
        Assert.assertEquals(found.get(1).get("id"), "b");
    }

    @Test
    public void matchingParentIsNotCountedAgainThroughItsNestedChildren() {
        var found = new ArrayList<Map<String, Object>>();
        FixtureRecordSearch.findOwned(
                Map.of(
                        "id",
                        "parent",
                        "englishName",
                        "QA-owned",
                        "rows",
                        List.of(Map.of("id", "child", "notes", "QA-owned"))),
                "QA-owned",
                found);
        Assert.assertEquals(found.size(), 1);
        Assert.assertEquals(found.get(0).get("id"), "parent");
    }
}
