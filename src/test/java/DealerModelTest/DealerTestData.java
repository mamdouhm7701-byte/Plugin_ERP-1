/* شرح الملف: تجهيز سجلات مستقلة للتجربة ومراجعتها وحذفها بعد الانتهاء، ضمن العملاء والموردين
 * والمناديب.
 */
package DealerModelTest;

import DealerModelTest.DealerFixtureApi.ApiFailure;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

final class DealerTestData implements AutoCloseable {

    record Record(String endpoint, Object id, String name, Map<String, Object> values)
            implements TestSupport.OwnedFixtureRecord {}

    private final WebDriver driver;
    private final DealerFixtureApi api;
    private final DealerFixtureDefinitions definitions = new DealerFixtureDefinitions(this);
    private final String suffix;
    private final TestSupport.FixtureSession session;
    private final TestSupport.PendingFixtureWrites pendingWrites;
    private final List<Record> created = new ArrayList<>();
    private final Map<String, Record> fixtures = new LinkedHashMap<>();

    DealerTestData(WebDriver driver) {
        this(driver, UUID.randomUUID().toString().substring(0, 12));
    }

    private DealerTestData(WebDriver driver, String suffix) {
        this.suffix = suffix;
        this.driver = driver;
        this.session = new TestSupport.FixtureSession(driver);
        this.pendingWrites = new TestSupport.PendingFixtureWrites(session, "dealer", suffix);
        this.api = new DealerFixtureApi(driver, session::verify);
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        d,
                                        "return Boolean(erpVue.root()?.$store?.getters['app/currentBranch']?.id)"));
    }

    private int scenarioSequence;
    private String scenarioScope = "";

    /** Stable references belong to this page; mutable/searchable records belong to the case. */
    void beginScenario() {
        session.verify();
        recoverPendingFixtures();
        scenarioScope = "case-" + (++scenarioSequence) + "-";
    }

    private String fixtureKey(String key) {
        return key.startsWith("QA-")
                        || key.equals("area")
                        || key.startsWith("class-")
                        || key.startsWith("main-")
                        || key.equals("unit")
                        || key.equals("category-main")
                ? key
                : scenarioScope + key;
    }

    @SuppressWarnings("unchecked")
    static DealerTestData resume(WebDriver driver, String suffix) throws java.io.IOException {
        if (!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))
            throw new IllegalArgumentException("Invalid fixture journal ID");
        var data = new DealerTestData(driver, suffix);
        var path = java.nio.file.Path.of("target", "dealer-fixtures", suffix + ".json");
        if (java.nio.file.Files.exists(path))
            for (Object value :
                    new org.json.JSONArray(TestSupport.FixtureJournals.read(path)).toList()) {
                var row = (Map<String, Object>) value;
                data.session.verify(row);
                String endpoint = (String) row.get("endpoint"), name = (String) row.get("name");
                if (name == null
                        || !name.startsWith("QA-")
                        || !java.util.Set.of(
                                        "Areas",
                                        "DealerClassifications",
                                        "MainDealers",
                                        "Customers",
                                        "AgentItinerariesStatus",
                                        "Agents",
                                        "Accounts",
                                        "Stores",
                                        "Units",
                                        "ItemCategories",
                                        "Items",
                                        "PriceLists")
                                .contains(endpoint)
                        || row.get("id") == null
                        || !(row.get("values") instanceof Map<?, ?>))
                    throw new AssertionError("Invalid owned fixture journal record");
                data.created.add(
                        new Record(
                                endpoint,
                                row.get("id"),
                                name,
                                (Map<String, Object>) row.get("values")));
            }
        data.pendingWrites.resume(
                java.util.Set.of(
                        "Areas",
                        "DealerClassifications",
                        "MainDealers",
                        "Customers",
                        "AgentItinerariesStatus",
                        "Agents",
                        "Accounts",
                        "Stores",
                        "Units",
                        "ItemCategories",
                        "Items",
                        "PriceLists"),
                "QA-");
        return data;
    }

    static Map<String, Object> fields(Object... pairs) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) values.put((String) pairs[i], pairs[i + 1]);
        return values;
    }

    Record area() {
        return definitions.area();
    }

    Record classification(String type) {
        return definitions.classification(type);
    }

    Record mainDealer(String type) {
        return definitions.mainDealer(type);
    }

    Record agent(String nature) {
        return definitions.agent(nature);
    }

    Record account() {
        return definitions.account();
    }

    Record store() {
        return definitions.store();
    }

    Record unit() {
        return definitions.unit();
    }

    Record category() {
        return definitions.category();
    }

    Record item(int index) {
        return definitions.item(index);
    }

    Record priceList() {
        return definitions.priceList();
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> read(Record record) {
        Object entity = call("get", record.endpoint(), fields("id", record.id())).get("entity");
        // The ERP returns HTTP 200 with an empty string for some deleted records.
        if (entity instanceof String text && text.isBlank()) return null;
        if (!(entity instanceof Map<?, ?>)) {
            throw new AssertionError(
                    "Unexpected fixture read response for "
                            + record.endpoint()
                            + "/"
                            + record.id());
        }
        return (Map<String, Object>) entity;
    }

    Record fixture(String key, String endpoint, Map<String, Object> values) {
        key = fixtureKey(key);
        if (fixtures.containsKey(key)) return fixtures.get(key);
        String name = "QA-" + key + "-" + suffix;
        values.put("arabicName", name);
        values.put("englishName", name);
        values.put("notes", "Owned Dealer test fixture");
        pendingWrites.start(endpoint, name, values);
        Map<String, Object> response = call("create", endpoint, values);
        Object id = response.get("id");
        if (id == null) throw new AssertionError("Fixture API did not return an ID: " + endpoint);
        Record record = new Record(endpoint, id, name, values);
        created.add(record);
        journal();
        fixtures.put(key, record);
        pendingWrites.committed(endpoint, name);
        if (endpoint.equals("Items")) {
            values.put("code", call("get", endpoint, fields("id", id)).get("code"));
        }
        return record;
    }

    private Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
        return api.call(action, endpoint, values);
    }

    private void recoverPendingFixtures() {
        pendingWrites.recover(
                (endpoint, values) -> call("get", endpoint, values),
                (endpoint, id, name, values) -> {
                    if (created.stream()
                            .noneMatch(r -> r.endpoint().equals(endpoint) && r.id().equals(id))) {
                        created.add(new Record(endpoint, id, name, new LinkedHashMap<>(values)));
                        journal();
                    }
                });
    }

    String journalSuffix() {
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
                java.nio.file.Path.of("target", "dealer-fixtures", suffix + ".json"), rows);
    }

    /** Page cleanup must not infer absence from the currently visible table page. */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> dealerRows(String endpoint) {
        List<Map<String, Object>> records = new ArrayList<>();
        java.util.Set<Object> ids = new java.util.HashSet<>();
        for (int page = 1; ; page++) {
            Object entity =
                    call(
                                    "get",
                                    endpoint
                                            + "?pageNumber="
                                            + page
                                            + "&pageSize=500&branchId="
                                            + session.branchId,
                                    fields())
                            .get("entity");
            List<?> rows;
            long total;
            if (endpoint.equals("AgentItineraries")
                    && entity instanceof Map<?, ?> result
                    && result.get("result") instanceof List<?> list) {
                rows = list;
                total = list.size();
            } else if (entity instanceof List<?> list) {
                rows = list;
                total = list.size();
            } else if (entity instanceof Map<?, ?> result
                    && result.get("pageData") instanceof List<?> list
                    && result.get("totalRecords") instanceof Number count) {
                rows = list;
                total = count.longValue();
            } else
                throw new AssertionError(
                        "Cannot establish owned dealer absence through complete list");
            int added = 0;
            for (Object value : rows) {
                if (!(value instanceof Map<?, ?> row) || row.get("id") == null)
                    throw new AssertionError("Dealer list identity is unavailable");
                if (ids.add(row.get("id"))) {
                    records.add((Map<String, Object>) row);
                    added++;
                }
            }
            if (ids.size() >= total) return records;
            if (added == 0) throw new AssertionError("Dealer cleanup pagination did not advance");
        }
    }

    private static boolean ownsDealer(Map<?, ?> row, String name) {
        return java.util.stream.Stream.of("arabicName", "englishName", "notes")
                        .anyMatch(field -> name.equals(row.get(field)))
                || row.get("visit") instanceof Map<?, ?> visit && name.equals(visit.get("title"));
    }

    void deleteOwnedDealer(Plugin_ERP.Enums.Pages route, String name) {
        if (name == null || !name.startsWith("QA-"))
            throw new AssertionError("Refusing unowned dealer cleanup");
        String endpoint =
                switch (route) {
                    case AGENTS -> "Agents";
                    case CUSTOMERS -> "Customers";
                    case SUPPLIERS -> "Suppliers";
                    case MAINDEALERS -> "MainDealers";
                    case CUSTOMERCLASS -> "DealerClassifications";
                    case DEALERROUTELINE -> "AgentItineraries";
                    default -> throw new AssertionError("Unsupported dealer cleanup route");
                };
        var matches = dealerRows(endpoint).stream().filter(row -> ownsDealer(row, name)).toList();
        if (matches.isEmpty()) return;
        if (matches.size() != 1 && !endpoint.equals("AgentItineraries"))
            throw new AssertionError("Ambiguous owned dealer name; retaining recovery journal");
        for (Map<String, Object> row : matches) deleteExactDealer(endpoint, row.get("id"), name);
    }

    private void deleteExactDealer(String endpoint, Object id, String name) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            Object raw =
                    endpoint.equals("AgentItineraries")
                            ? dealerRows(endpoint).stream()
                                    .filter(row -> id.equals(row.get("id")))
                                    .findFirst()
                                    .orElse(null)
                            : call("get", endpoint, fields("id", id)).get("entity");
            if (!(raw instanceof Map<?, ?> current)
                    || !id.equals(current.get("id"))
                    || !ownsDealer(current, name))
                throw new AssertionError("Dealer ownership changed before cleanup");
            ApiFailure rejected = null;
            try {
                call("delete", endpoint, fields("id", id));
            } catch (ApiFailure failure) {
                rejected = failure;
            } catch (org.openqa.selenium.TimeoutException unknown) {
                /* Verify whether the exact delete committed. */
            }
            var remaining =
                    dealerRows(endpoint).stream().filter(row -> id.equals(row.get("id"))).toList();
            if (remaining.isEmpty()) return;
            if (remaining.stream().anyMatch(row -> !ownsDealer(row, name)))
                throw new AssertionError("Dealer ownership changed during cleanup");
            if (rejected != null && !DealerFixtureDeletion.retryable(rejected)) throw rejected;
        }
        throw new AssertionError("Owned dealer still exists after cleanup: " + name);
    }

    private void deleteRecord(Record record) {
        new DealerFixtureDeletion(this, api).remove(record);
    }

    @Override
    public void close() {
        recoverPendingFixtures();
        TestSupport.FixtureCleanup.run(
                created,
                fixtures,
                new ArrayList<>(created.reversed()),
                this::deleteRecord,
                this::journal);
    }
}
