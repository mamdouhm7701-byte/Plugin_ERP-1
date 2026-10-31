/* شرح الملف: تجهيز بيانات أساسية مستقلة مثل البنك والمخزن والحساب ثم تنظيفها.
 */
package TestSupport;

import TestSupport.BasicDataFixturesApi.ApiFailure;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class BasicDataFixtures implements AutoCloseable {

    public record Record(String endpoint, Object id, String name, Map<String, Object> values)
            implements OwnedFixtureRecord {
        public Record {
            values = java.util.Collections.unmodifiableMap(new LinkedHashMap<>(values));
        }
    }

    public static final java.util.Set<String> ALLOWED = BasicDataFixtureEndpoints.ALLOWED;
    private final java.util.Set<String> reservedNames = new java.util.HashSet<>();
    private final WebDriver driver;
    private final BasicDataFixturesApi api;
    final TestSupport.FixtureAccountPool accounts = new TestSupport.FixtureAccountPool();
    private final BasicDataFixturesDefinitions definitions = new BasicDataFixturesDefinitions(this);
    private final String suffix;
    private final TestSupport.FixtureSession session;
    private final TestSupport.PendingFixtureWrites pendingWrites;
    private final List<Record> created = new ArrayList<>();
    private final Map<String, Record> fixtures = new LinkedHashMap<>();

    public BasicDataFixtures(WebDriver driver) {
        this(driver, UUID.randomUUID().toString().substring(0, 12));
    }

    private BasicDataFixtures(WebDriver driver, String suffix) {
        this.suffix = suffix;
        this.driver = driver;
        this.session = new TestSupport.FixtureSession(driver);
        this.pendingWrites = new TestSupport.PendingFixtureWrites(session, "basic-data", suffix);
        this.api = new BasicDataFixturesApi(driver, session::verify);
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        d,
                                        "return Boolean(erpVue.root()?.$store?.getters['app/currentBranch']?.id)"));
    }

    /** Closed and reviewed treasury history keeps its linked master records for audit. */
    public void preserveAuditHistory() {
        preserveAuditHistory("treasury-audit-masters");
    }

    public void preserveAuditHistory(String folder) {
        if (!java.util.Set.of("treasury-audit-masters", "inventory-audit-documents")
                .contains(folder)) throw new IllegalArgumentException("Unsupported audit folder");
        session.verify();
        awaitUiSettled();
        collectUi();
        recoverPendingFixtures();
        var rows =
                created.stream()
                        .map(
                                r ->
                                        fields(
                                                "origin",
                                                session.origin,
                                                "branchId",
                                                session.branchId,
                                                "endpoint",
                                                r.endpoint(),
                                                "id",
                                                r.id(),
                                                "name",
                                                r.name(),
                                                "values",
                                                r.values()))
                        .toList();
        TestSupport.FixtureJournals.write(
                java.nio.file.Path.of("target", folder, suffix + ".json"), rows);
        created.clear();
        fixtures.clear();
        retainedForRecovery = null;
        journal();
    }

    private String retainedForRecovery;

    /** A separately managed child failed cleanup; leave every prerequisite journaled. */
    public void retainForRecovery(String reason) {
        retainedForRecovery = java.util.Objects.requireNonNull(reason);
        journal();
    }

    private int scenarioSequence;
    private String scenarioScope = "";

    /** Every method receives independent mutable records. */
    public void beginScenario() {
        session.verify();
        awaitUiSettled();
        collectUi();
        recoverPendingFixtures();
        scenarioScope = "case-" + (++scenarioSequence) + "-";
    }

    String fixtureKey(String key) {
        return key.startsWith("account-main-pool-") ? key : scenarioScope + key;
    }

    @SuppressWarnings("unchecked")
    public static BasicDataFixtures resume(WebDriver driver, String suffix)
            throws java.io.IOException {
        if (!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))
            throw new IllegalArgumentException("Invalid fixture journal ID");
        var data = new BasicDataFixtures(driver, suffix);
        var path = java.nio.file.Path.of("target", "basic-data-fixtures", suffix + ".json");
        if (java.nio.file.Files.exists(path))
            for (Object value :
                    new org.json.JSONArray(TestSupport.FixtureJournals.read(path)).toList()) {
                var row = (Map<String, Object>) value;
                data.session.verify(row);
                String endpoint = (String) row.get("endpoint"), name = (String) row.get("name");
                if (name == null
                        || (!name.startsWith("QA-BD-") || !name.endsWith("-" + suffix))
                        || !ALLOWED.contains(endpoint)
                        || row.get("id") == null
                        || !(row.get("values") instanceof Map<?, ?>))
                    throw new AssertionError("Invalid owned fixture journal record");
                data.reservedNames.add(name);
                data.created.add(
                        new Record(
                                endpoint,
                                row.get("id"),
                                name,
                                (Map<String, Object>) row.get("values")));
            }
        var attempts = java.nio.file.Path.of("target", "basic-data-attempts", suffix + ".json");
        if (java.nio.file.Files.exists(attempts))
            for (Object value :
                    new org.json.JSONArray(TestSupport.FixtureJournals.read(attempts)).toList()) {
                if (!(value instanceof Map<?, ?> row)
                        || !(row.get("name") instanceof String name)
                        || !name.endsWith("-" + suffix))
                    throw new AssertionError("Pending name belongs to another fixture run");
            }
        data.pendingWrites.resume(ALLOWED, "QA-BD-");
        return data;
    }

    public static Map<String, Object> fields(Object... pairs) {
        if (pairs.length % 2 != 0) throw new IllegalArgumentException("Expected field/value pairs");
        Map<String, Object> values = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) values.put((String) pairs[i], pairs[i + 1]);
        return values;
    }

    public Record account(String key) {
        return definitions.account(key);
    }

    public Record bank(int index) {
        return definitions.bank(index);
    }

    public Record safe(int index) {
        return definitions.safe(index);
    }

    public Record user(String key) {
        return definitions.user(key, session.branchId);
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> lookup(String endpoint) {
        return (Map<String, Object>) call("get", endpoint, fields()).get("entity");
    }

    /** Read-only access for persisted screen assertions, still bound to this origin/branch. */
    public Object query(String endpoint) {
        return call("get", endpoint, fields()).get("entity");
    }

    public Record captured(String endpoint, String name) {
        collectUi();
        var matches =
                created.stream()
                        .filter(r -> r.endpoint().equals(endpoint) && r.name().equals(name))
                        .toList();
        if (matches.size() > 1) throw new AssertionError("Ambiguous owned screen save: " + name);
        return matches.isEmpty() ? null : matches.get(0);
    }

    public Record awaitSaved(String endpoint, String name) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(35))
                    .withMessage("Owned UI save was not captured: " + name)
                    .until(
                            d -> {
                                installCapture();
                                return captured(endpoint, name);
                            });
        } catch (org.openqa.selenium.TimeoutException failure) {
            try {
                Object diagnostics =
                        Plugin_ERP.Util.VueComponentAccess.execute(
                                driver,
                                "return {attempts:window.__basicDataQA?.attempts||[],errors:window.__basicDataQA?.errors||[],pending:window.__basicDataQA?.pending}");
                io.qameta.allure.Allure.addAttachment(
                        "Owned basic data save diagnostics",
                        "application/json",
                        new org.json.JSONObject((Map<?, ?>) diagnostics).toString(),
                        ".json");
            } catch (RuntimeException diagnosticFailure) {
                failure.addSuppressed(diagnosticFailure);
            }
            throw failure;
        }
    }

    public Map<String, Object> read(Record record) {
        return new BasicDataFixtureReads(api).read(record);
    }

    public Record fixture(String key, String endpoint, Map<String, Object> values) {
        requireEndpoint(endpoint);
        values = new LinkedHashMap<>(values);
        key = fixtureKey(key);
        if (fixtures.containsKey(key)) return fixtures.get(key);
        String name = "QA-BD-" + key + "-" + suffix;
        reservedNames.add(name);
        values.put("arabicName", name);
        values.put("englishName", name);
        values.put("notes", name);
        if (endpoint.equals("users")) values.put("fullName", name);
        Map<String, Object> persisted = new LinkedHashMap<>(values);
        persisted.remove("passwordHash");
        pendingWrites.start(endpoint, name, persisted);
        Map<String, Object> response;
        try {
            response = call("create", endpoint, values);
        } catch (ApiFailure | org.openqa.selenium.TimeoutException failure) {
            // Preserve the pending journal even if the server committed before losing the response.
            throw failure;
        }
        Object id = response.get("id");
        if (id == null) throw new AssertionError("Fixture API did not return an ID: " + endpoint);
        Record record = new Record(endpoint, id, name, persisted);
        created.add(record);
        journal();
        fixtures.put(key, record);
        pendingWrites.committed(endpoint, name);
        return record;
    }

    private Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
        return api.call(action, endpoint, values);
    }

    public void track(String endpoint, Object id, String note, Map<String, Object> values) {
        requireEndpoint(endpoint);
        values = new LinkedHashMap<>(values);
        values.remove("passwordHash");
        if (note == null || !reservedNames.contains(note))
            throw new AssertionError("Refusing to track an unowned basic-data record");
        if (id == null) throw new AssertionError("Saved movement did not return an ID");
        for (int i = 0; i < created.size(); i++)
            if (created.get(i).endpoint().equals(endpoint) && created.get(i).id().equals(id)) {
                created.remove(i);
                created.add(new Record(endpoint, id, note, new LinkedHashMap<>(values)));
                journal();
                pendingWrites.committed(endpoint, note);
                return;
            }
        created.add(new Record(endpoint, id, note, new LinkedHashMap<>(values)));
        journal();
        pendingWrites.committed(endpoint, note);
    }

    private void recoverPendingFixtures() {
        pendingWrites.recover(
                (endpoint, values) -> call("get", endpoint, values),
                (endpoint, id, name, values) -> {
                    reservedNames.add(name);
                    var full = new LinkedHashMap<String, Object>(values);
                    Object stored = call("get", endpoint, fields("id", id)).get("entity");
                    if (stored instanceof Map<?, ?> dto)
                        for (var entry : dto.entrySet())
                            full.put((String) entry.getKey(), entry.getValue());
                    full.remove("passwordHash");
                    track(endpoint, id, name, full);
                });
    }

    public String journalSuffix() {
        return suffix;
    }

    private void journal() {
        var rows =
                created.stream()
                        .map(
                                r ->
                                        fields(
                                                "origin",
                                                session.origin,
                                                "branchId",
                                                session.branchId,
                                                "endpoint",
                                                r.endpoint(),
                                                "id",
                                                r.id(),
                                                "name",
                                                r.name(),
                                                "values",
                                                r.values()))
                        .toList();
        TestSupport.FixtureJournals.write(
                java.nio.file.Path.of("target", "basic-data-fixtures", suffix + ".json"), rows);
    }

    private static void requireEndpoint(String endpoint) {
        if (!ALLOWED.contains(endpoint))
            throw new IllegalArgumentException("Unsupported owned fixture endpoint: " + endpoint);
    }

    public String reserveName(String endpoint, String label) {
        requireEndpoint(endpoint);
        String name =
                "QA-BD-"
                        + label.replaceAll("[^a-zA-Z0-9-]", "-")
                        + "-"
                        + UUID.randomUUID().toString().substring(0, 12)
                        + "-"
                        + suffix;
        reservedNames.add(name);
        pendingWrites.start(endpoint, name, fields());
        return name;
    }

    public void collectUi() {
        new BasicDataUiCapture(this, driver, suffix, reservedNames).collectUi();
    }

    public void installCapture() {
        new BasicDataUiCapture(this, driver, suffix, reservedNames).installCapture();
    }

    /** Reloaded pages retain unresolved request counters; never delete their prerequisites. */
    public void awaitUiSettled() {
        new BasicDataUiCapture(this, driver, suffix, reservedNames).awaitUiSettled();
    }

    private void deleteRecord(Record record) {
        new BasicDataFixtureDeletion(this, api).remove(record);
    }

    @Override
    public void close() {
        session.verify();
        if (retainedForRecovery != null)
            throw new AssertionError(
                    "Prerequisites retained for child recovery: " + retainedForRecovery);
        awaitUiSettled();
        collectUi();
        recoverPendingFixtures();
        FixtureCleanup.run(
                created,
                fixtures,
                new ArrayList<>(created.reversed()),
                this::deleteRecord,
                this::journal);
    }
}
