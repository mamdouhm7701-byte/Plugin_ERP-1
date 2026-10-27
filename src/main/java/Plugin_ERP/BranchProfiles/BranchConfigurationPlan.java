package Plugin_ERP.BranchProfiles;

import java.util.*;

/**
 * A deliberate change to four policies on the same origin and branch, never an implicit branch
 * switch.
 */
public record BranchConfigurationPlan(BranchSnapshot before, BranchProfile target) {
    public static final List<String> EDITABLE =
            List.of("activityType", "discountLevel", "taxPolicy", "stocktakingMethod");

    public BranchConfigurationPlan {
        Objects.requireNonNull(before);
        Objects.requireNonNull(target);
        if (!before.origin().equals(target.expected().origin())
                || !before.branchId().equals(target.expected().branchId()))
            throw new IllegalArgumentException(
                    "Settings changes require the same origin and branch identity");
    }

    public Map<String, String> changes() {
        var changes = new LinkedHashMap<String, String>();
        for (String field : EDITABLE) {
            String desired = target.expected().fields().get(field);
            if (!before.fields().get(field).equals(desired)) changes.put(field, desired);
        }
        return Collections.unmodifiableMap(changes);
    }

    public static BranchSnapshot fromForm(String origin, Map<String, Object> form) {
        var fields = new HashMap<String, Object>(form);
        fields.put("origin", origin);
        fields.put("branchId", form.get("id"));
        return BranchSnapshot.from(fields);
    }

    public static Map<String, Object> snapshotForm(Map<String, Object> form) {
        var result = new LinkedHashMap<String, Object>();
        form.forEach((key, value) -> result.put(key, detached(value)));
        return Collections.unmodifiableMap(result);
    }

    private static Object detached(Object value) {
        if (value instanceof Map<?, ?> map) {
            var copy = new LinkedHashMap<Object, Object>();
            map.forEach((key, item) -> copy.put(key, detached(item)));
            return Collections.unmodifiableMap(copy);
        }
        if (value instanceof List<?> list) {
            var copy = new ArrayList<Object>();
            list.forEach(item -> copy.add(detached(item)));
            return Collections.unmodifiableList(copy);
        }
        return value;
    }

    public static void verifyUnrelatedFields(
            Map<String, Object> before, Map<String, Object> after) {
        var original = new HashMap<>(before);
        var edited = new HashMap<>(after);
        EDITABLE.forEach(
                field -> {
                    original.remove(field);
                    edited.remove(field);
                });
        if (!original.equals(edited))
            throw new IllegalStateException(
                    "Branch form changed unrelated settings; refusing to continue");
    }
}
