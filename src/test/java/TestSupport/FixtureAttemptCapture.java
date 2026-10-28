/* شرح الملف: تسجيل طلبات الحفظ التي تحتاج مراجعة نتيجتها، مع فصل محاولات الحالات وطلبات تجهيز البيانات.
 * فائدته: يبقي الحركات غير المحسومة قابلة للاسترجاع دون تكرار تسجيل الحركات التي تم تتبعها بالفعل.
 */
package TestSupport;

import java.util.*;

public final class FixtureAttemptCapture {
    public enum Policy {
        SCENARIO_MOVEMENTS,
        WAREHOUSE_MOVEMENTS
    }

    public record Owner(
            String origin,
            Object branchId,
            String prefix,
            Set<String> movements,
            Set<String> masters) {}

    private final Owner owner;
    private final Collection<? extends OwnedFixtureRecord> created;
    private final List<Map<String, Object>> pending;
    private final Runnable persist;
    private final Policy policy;

    public FixtureAttemptCapture(
            Owner owner,
            Collection<? extends OwnedFixtureRecord> created,
            List<Map<String, Object>> pending,
            Runnable persist,
            Policy policy) {
        this.owner = owner;
        this.created = created;
        this.pending = pending;
        this.persist = persist;
        this.policy = policy;
    }

    private static Map<String, Object> fields(Object... pairs) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) values.put((String) pairs[i], pairs[i + 1]);
        return values;
    }

    public Map<String, Object> rememberSeed(
            String endpoint, String name, Map<String, Object> values) {
        Map<String, Object> attempt =
                fields(
                        "origin",
                        owner.origin(),
                        "branchId",
                        owner.branchId(),
                        "fixture",
                        true,
                        "endpoint",
                        endpoint,
                        "name",
                        name,
                        "values",
                        new LinkedHashMap<>(values));
        pending.add(attempt);
        persist.run();
        return attempt;
    }

    @SuppressWarnings("unchecked")
    public void remember(
            List<Map<String, Object>> attempts, List<Map<String, Object>> saves, String marker) {
        if (!marker.startsWith(owner.prefix()))
            throw new AssertionError("Refuse unrelated sales attempt");
        List<Map<String, Object>> available = new ArrayList<>(saves);
        pending.removeIf(
                row ->
                        policy == Policy.WAREHOUSE_MOVEMENTS
                                ? !Boolean.TRUE.equals(row.get("factory"))
                                : !Boolean.TRUE.equals(row.get("factory"))
                                        && !Boolean.TRUE.equals(row.get("fixture"))
                                        && row.get("name") instanceof String name
                                        && name.startsWith(marker));
        for (var attempt : attempts) {
            String endpoint = (String) attempt.get("endpoint");
            if (!owner.movements().contains(endpoint))
                throw new AssertionError("Unexpected sales attempt endpoint");
            Map<String, Object> values = (Map<String, Object>) attempt.get("values");
            Object id = values.get("id");
            boolean successful = false;
            for (var saved : new ArrayList<>(available))
                if (endpoint.equals(saved.get("endpoint")) && values.equals(saved.get("values"))) {
                    id = saved.get("id");
                    successful = true;
                    available.remove(saved);
                    break;
                }
            final Object knownId = id;
            if (successful
                    && id != null
                    && created.stream()
                            .anyMatch(r -> r.endpoint().equals(endpoint) && r.id().equals(knownId)))
                continue;
            String name =
                    values.get("notes") instanceof String note && note.startsWith(marker)
                            ? note
                            : marker;
            pending.add(
                    fields(
                            "origin",
                            owner.origin(),
                            "branchId",
                            owner.branchId(),
                            "endpoint",
                            endpoint,
                            "id",
                            id,
                            "name",
                            name,
                            "values",
                            new LinkedHashMap<>(values)));
        }
        persist.run();
    }

    public Map<String, Object> rememberMaster(
            String endpoint, String name, Map<String, Object> values) {
        if (!owner.masters().contains(endpoint) || !name.startsWith(owner.prefix()))
            throw new AssertionError("Refuse unrelated fixture recovery");
        Map<String, Object> attempt =
                fields(
                        "origin",
                        owner.origin(),
                        "branchId",
                        owner.branchId(),
                        "factory",
                        true,
                        "fixture",
                        true,
                        "endpoint",
                        endpoint,
                        "name",
                        name,
                        "values",
                        new LinkedHashMap<>(values));
        if (policy == Policy.WAREHOUSE_MOVEMENTS) attempt.remove("fixture");
        pending.add(attempt);
        persist.run();
        return attempt;
    }
}
