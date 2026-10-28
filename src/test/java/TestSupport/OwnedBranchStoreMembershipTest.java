/* شرح الملف: اختبار إضافة مخزن التجربة وإزالته مع بقاء المخازن القديمة والمخزن الافتراضي كما هما.
 * فائدته: يحمي ربط المخازن أثناء نقل المسؤولية المشتركة خارج تجهيز المبيعات والمشتريات.
 */
package TestSupport;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class OwnedBranchStoreMembershipTest {
    @Test
    public void removesOnlyOwnedStoreAndKeepsOtherFieldsAndDefault() {
        var state = new AtomicReference<Map<String, Object>>(branch("original", "owned"));
        var displayed = new AtomicReference<Map<String, Object>>();
        var membership =
                new OwnedBranchStoreMembership(id -> state.get(), state::set, displayed::set);
        membership.change("branch", "owned", false);
        Assert.assertFalse(membership.contains(state.get(), "owned"));
        Assert.assertTrue(membership.contains(state.get(), "original"));
        Assert.assertEquals(state.get().get("defaultStoreId"), "original");
        Assert.assertEquals(state.get().get("phone"), "preserved");
        Assert.assertEquals(displayed.get(), state.get());
    }

    @Test
    public void repeatedAddDoesNotDuplicateMembershipOrWriteAgain() {
        var state = new AtomicReference<Map<String, Object>>(branch("original"));
        var writes = new java.util.concurrent.atomic.AtomicInteger();
        var membership =
                new OwnedBranchStoreMembership(
                        id -> state.get(),
                        value -> {
                            writes.incrementAndGet();
                            state.set(value);
                        },
                        value -> {});
        membership.change("branch", "owned", true);
        membership.change("branch", "owned", true);
        Assert.assertEquals(writes.get(), 1);
        Assert.assertEquals(((List<?>) state.get().get("stores")).size(), 2);
    }

    @Test
    public void changedBranchIdentityOrLostExistingStoreNeverPassesVerification() {
        var wrong =
                new OwnedBranchStoreMembership(
                        id -> Map.of("id", "foreign", "stores", List.of()),
                        value -> {},
                        value -> {});
        Assert.expectThrows(AssertionError.class, () -> wrong.read("branch"));
        var state = new AtomicReference<Map<String, Object>>(branch("original"));
        var unsafe =
                new OwnedBranchStoreMembership(
                        id -> state.get(), value -> state.set(branch("owned")), value -> {});
        Assert.expectThrows(AssertionError.class, () -> unsafe.change("branch", "owned", true));
    }

    private static Map<String, Object> branch(String... stores) {
        var rows = new ArrayList<Map<String, Object>>();
        for (String store : stores) rows.add(Map.of("storeId", store));
        return new LinkedHashMap<>(
                Map.of(
                        "id",
                        "branch",
                        "stores",
                        rows,
                        "defaultStoreId",
                        "original",
                        "phone",
                        "preserved"));
    }
}
