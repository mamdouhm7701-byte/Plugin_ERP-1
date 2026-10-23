package TestSupport;

import java.nio.file.*;
import java.util.*;
import java.util.function.BiFunction;

/** Journals a fixture create before dispatch, so a lost response cannot orphan its parents. */
public final class PendingFixtureWrites {
    public interface Recovered {
        void accept(String endpoint, Object id, String name, Map<String, Object> values);
    }

    private final FixtureSession session;
    private final Path path;
    private final List<Map<String, Object>> pending = new ArrayList<>();

    public PendingFixtureWrites(FixtureSession session, String folder, String suffix) {
        this.session = session;
        path = Path.of("target", folder + "-attempts", suffix + ".json");
    }

    private void persist() {
        FixtureJournals.write(path, pending);
    }

    public void start(String endpoint, String name, Map<String, Object> values) {
        session.verify();
        var row = new LinkedHashMap<String, Object>();
        row.put("origin", session.origin);
        row.put("branchId", session.branchId);
        row.put("endpoint", endpoint);
        row.put("name", name);
        row.put("values", new LinkedHashMap<>(values));
        if (pending.contains(row)) return;
        pending.add(row);
        persist();
    }

    public void committed(String endpoint, String name) {
        if (pending.removeIf(
                row -> endpoint.equals(row.get("endpoint")) && name.equals(row.get("name"))))
            persist();
    }

    @SuppressWarnings("unchecked")
    public void resume(Set<String> allowed, String prefix) throws java.io.IOException {
        if (!Files.exists(path)) return;
        for (Object value : new org.json.JSONArray(Files.readString(path)).toList()) {
            var row = (Map<String, Object>) value;
            session.verify(row);
            if (!(row.get("name") instanceof String name)
                    || !name.startsWith(prefix)
                    || !allowed.contains(row.get("endpoint"))
                    || !(row.get("values") instanceof Map<?, ?>))
                throw new AssertionError("Invalid pending fixture write");
            pending.add(row);
        }
    }

    @SuppressWarnings("unchecked")
    public void recover(
            BiFunction<String, Map<String, Object>, Map<String, Object>> api, Recovered recovered) {
        session.verify();
        for (var attempt : new ArrayList<>(pending)) {
            String endpoint = (String) attempt.get("endpoint"), name = (String) attempt.get("name");
            var values = (Map<String, Object>) attempt.get("values");
            List<Map<String, Object>> matches = new ArrayList<>();
            Set<Object> seen = new HashSet<>();
            for (int page = 1; ; page++) {
                String url =
                        endpoint.equals("Accounts")
                                ? "Aggregates/AccountTreeViewModel?showBalance=false&type=Main&branchId="
                                        + session.branchId
                                : (endpoint.equals("Items") ? "Items/Summary" : endpoint)
                                        + "?pageNumber="
                                        + page
                                        + "&pageSize=500";
                Object entity = api.apply(url, Map.of()).get("entity");
                find(entity, name, matches);
                if (entity instanceof List<?> || endpoint.equals("Accounts")) break;
                if (!(entity instanceof Map<?, ?> result)
                        || !(result.get("pageData") instanceof List<?> rows)
                        || !(result.get("totalRecords") instanceof Number total))
                    throw new AssertionError("Cannot recover uncertain fixture from " + endpoint);
                int added = 0;
                for (Object row : rows) {
                    if (!(row instanceof Map<?, ?> record) || record.get("id") == null)
                        throw new AssertionError("Invalid fixture list identity");
                    if (seen.add(record.get("id"))) added++;
                }
                if (seen.size() >= total.longValue()) break;
                if (added == 0)
                    throw new AssertionError("Fixture recovery pagination did not advance");
            }
            Map<Object, Map<String, Object>> unique = new LinkedHashMap<>();
            for (var match : matches) unique.put(match.get("id"), match);
            if (unique.size() > 1)
                throw new AssertionError("Ambiguous owned fixture recovery: " + name);
            if (!unique.isEmpty()) {
                Object id = unique.keySet().iterator().next();
                Object intendedId = values.get("id");
                // Some new-form DTOs use zero/empty IDs; those are creates, not updates.
                boolean identifiedAttempt = intendedId != null
                        && !intendedId.toString().isBlank() && !intendedId.toString().equals("0");
                if (identifiedAttempt && !Objects.equals(intendedId, id))
                    throw new AssertionError("Recovered fixture document identity changed");
                Object raw = api.apply(endpoint, Map.of("id", id)).get("entity");
                if (!(raw instanceof Map<?, ?> stored) || !matchesName(stored, name))
                    throw new AssertionError("Cannot verify recovered fixture ownership");
                // Financial edit DTOs omit branchId. Only a previously identified update
                // may use the verified session branch; creates still require full evidence.
                boolean identifiedFinancialUpdate =
                        Set.of("Collections", "Payments").contains(endpoint)
                                && Objects.equals(values.get("id"), id)
                                && Objects.equals(stored.get("id"), id)
                                && Objects.equals(values.get("branchId"), session.branchId);
                for (var field : values.entrySet())
                    if (field.getKey().endsWith("Id")
                            && field.getValue() != null
                            && !(field.getKey().equals("branchId")
                                    && stored.get("branchId") == null
                                    && identifiedFinancialUpdate)
                            && !Objects.equals(field.getValue(), stored.get(field.getKey())))
                        throw new AssertionError(
                                "Recovered fixture dependency changed: " + field.getKey());
                recovered.accept(endpoint, id, name, values);
            }
            pending.remove(attempt);
            persist();
        }
    }

    private static boolean matchesName(Map<?, ?> row, String name) {
        return name.equals(row.get("arabicName"))
                || name.equals(row.get("englishName"))
                || name.equals(row.get("notes"))
                || name.equals(row.get("fullName"));
    }

    @SuppressWarnings("unchecked")
    private static void find(Object node, String name, List<Map<String, Object>> matches) {
        if (node instanceof Map<?, ?> row) {
            if (row.get("id") != null && matchesName(row, name))
                matches.add((Map<String, Object>) row);
            for (Object child : row.values())
                if (child instanceof Map<?, ?> || child instanceof List<?>)
                    find(child, name, matches);
        } else if (node instanceof List<?> rows) for (Object row : rows) find(row, name, matches);
    }
}
