/* شرح الملف: يعيد استخدام الحساب الرئيسي داخل بيانات نفس الكلاس، مع حساب فرعي مستقل لكل حالة ورقم لا يتكرر. لا يشارك أرصدة العملاء أو الموردين بين الحالات. */
package TestSupport;

import java.util.HashMap;
import java.util.Map;

public final class FixtureAccountPool {
    public record Slot(String parentKey, String childSuffix) {}

    private final boolean enabled;
    private final Map<String, Integer> nextChild = new HashMap<>();
    private final Map<String, Slot> assigned = new HashMap<>();

    public FixtureAccountPool() {
        this(Boolean.parseBoolean(System.getProperty("fixture.accounts.sharedParents", "true")));
    }

    FixtureAccountPool(boolean enabled) {
        this.enabled = enabled;
    }

    public Slot account(String role, String scopedChildKey) {
        if (!enabled) return new Slot("account-main-" + role, "01");
        return assigned.computeIfAbsent(
                scopedChildKey,
                ignored -> {
                    int index = nextChild.merge(role, 1, Integer::sum) - 1;
                    // Preserve the existing two-digit child code; start another parent at 100.
                    return new Slot(
                            "account-main-pool-" + role + "-" + index / 99,
                            String.format(java.util.Locale.ROOT, "%02d", index % 99 + 1));
                });
    }
}
