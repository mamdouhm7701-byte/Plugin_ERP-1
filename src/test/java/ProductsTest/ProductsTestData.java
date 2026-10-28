/* شرح الملف: تجهيز سجلات مستقلة للتجربة ومراجعتها وحذفها بعد الانتهاء، ضمن الأصناف.
 */
package ProductsTest;

import ProductsTest.ProductsFixtureApi.ApiFailure;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

final class ProductsTestData implements AutoCloseable {

    record Record(String endpoint, Object id, String name, Map<String, Object> values)
            implements TestSupport.OwnedFixtureRecord {}

    private final WebDriver driver;
    private final ProductsFixtureApi api;
    private final ProductsFixtureDefinitions definitions = new ProductsFixtureDefinitions(this);
    private final String origin;
    private final String suffix;
    private final TestSupport.FixtureSession session;
    private final TestSupport.PendingFixtureWrites pendingWrites;
    private final List<Record> created = new ArrayList<>();
    private final Map<String, Record> fixtures = new LinkedHashMap<>();
    private static final String BRANCH_STORE_LINK = "ProductBranchStoreMembership";

    ProductsTestData(WebDriver driver) {
        this(driver, UUID.randomUUID().toString().substring(0, 12));
    }

    private ProductsTestData(WebDriver driver, String suffix) {
        this.suffix = suffix;
        this.driver = driver;
        this.session = new TestSupport.FixtureSession(driver);
        this.pendingWrites = new TestSupport.PendingFixtureWrites(session, "products", suffix);
        this.api = new ProductsFixtureApi(driver, session::verify);
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        d,
                                        "return Boolean(erpVue.root()?.$store?.getters['app/currentBranch']?.id)"));
        Object currentOrigin =
                Plugin_ERP.Util.VueComponentAccess.execute(driver, "return location.origin");
        origin = currentOrigin instanceof String value ? value : "offline";
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
        return key.startsWith("QA-PR-")
                        || key.startsWith("unit-")
                        || key.equals("category-main")
                        || key.startsWith("store-")
                        || key.startsWith("account-main-store-")
                        || key.startsWith("account-sub-store-")
                ? key
                : scenarioScope + key;
    }

    @SuppressWarnings("unchecked")
    static ProductsTestData resume(WebDriver driver, String suffix) throws java.io.IOException {
        if (!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))
            throw new IllegalArgumentException("Invalid fixture journal ID");
        var data = new ProductsTestData(driver, suffix);
        var path = java.nio.file.Path.of("target", "products-fixtures", suffix + ".json");
        if (java.nio.file.Files.exists(path))
            for (Object value :
                    new org.json.JSONArray(TestSupport.FixtureJournals.read(path)).toList()) {
                var row = (Map<String, Object>) value;
                data.session.verify(row);
                String endpoint = (String) row.get("endpoint"), name = (String) row.get("name");
                if (name == null
                        || !name.startsWith("QA-PR-")
                        || !java.util.Set.of(
                                        "Accounts",
                                        "Units",
                                        "ItemCategories",
                                        "Stores",
                                        "Items",
                                        "PriceLists",
                                        "ProductBranchStoreMembership")
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
                        "Accounts",
                        "Units",
                        "ItemCategories",
                        "Stores",
                        "Items",
                        "PriceLists",
                        "ProductBranchStoreMembership"),
                "QA-PR-");
        return data;
    }

    static Map<String, Object> fields(Object... pairs) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) values.put((String) pairs[i], pairs[i + 1]);
        return values;
    }

    Record account(String key) {
        return definitions.account(key);
    }

    Record unit(int index) {
        return definitions.unit(index);
    }

    Record mainCategory() {
        return definitions.mainCategory();
    }

    Record category() {
        return definitions.category();
    }

    Record store(int index) {
        synchronized (TestSupport.BranchMutationLock.monitor()) {
            Record store =
                    fixture(
                            "store-" + index,
                            "Stores",
                            fields("accountId", account("store-" + index).id()));
            String key = "branch-store-" + index;
            if (!fixtures.containsKey(key)) {
                Object branchId =
                        Plugin_ERP.Util.VueComponentAccess.execute(
                                driver,
                                "return erpVue.root().$store.getters['app/currentBranch'].id");
                if (!(branchId instanceof String branch) || branch.isBlank())
                    throw new AssertionError("Current branch identity is unavailable");
                Record link =
                        new Record(
                                BRANCH_STORE_LINK,
                                store.id(),
                                store.name(),
                                fields("branchId", branchId, "storeId", store.id()));
                // Journal before updating: a lost response must still leave a recoverable owned
                // link.
                created.add(link);
                fixtures.put(key, link);
                journal();
                changeBranchStore(link, true);
            }
            return store;
        }
    }

    @SuppressWarnings("unchecked")
    private TestSupport.OwnedBranchStoreMembership branchMembership() {
        return new TestSupport.OwnedBranchStoreMembership(
                id -> {
                    Object entity = call("get", "Branches", fields("id", id)).get("entity");
                    return entity instanceof Map<?, ?> ? (Map<String, Object>) entity : null;
                },
                update -> call("update", "Branches", update),
                branch ->
                        Plugin_ERP.Util.VueComponentAccess.execute(
                                driver,
                                "const root=erpVue.root();if(root.$store.getters['app/currentBranch']?.id===arguments[0].id)root.$store.commit('app/setCurrentBranch',arguments[0])",
                                branch));
    }

    private Map<String, Object> branchFor(Record link) {
        return branchMembership().read(link.values().get("branchId"));
    }

    private boolean containsStore(Map<String, Object> branch, Object storeId) {
        return branchMembership().contains(branch, storeId);
    }

    private void changeBranchStore(Record link, boolean add) {
        branchMembership().change(link.values().get("branchId"), link.values().get("storeId"), add);
    }

    Map<String, Object> itemValues() {
        return definitions.itemValues();
    }

    Record item(int index) {
        return definitions.item(index);
    }

    Record priceList() {
        return definitions.priceList();
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> lookup(String endpoint) {
        return (Map<String, Object>) call("get", endpoint, fields()).get("entity");
    }

    void recoverAttemptedItems(List<Map<String, Object>> attempts) {
        TestSupport.PartialItemRecovery.recover(
                attempts,
                new TestSupport.PartialItemRecovery.Access<Record>(
                        "QA-PR-",
                        created,
                        (url, query) -> call("get", url, query),
                        Record::new,
                        this::read,
                        record -> {
                            created.add(record);
                            journal();
                        }));
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> read(Record record) {
        if (record.endpoint().equals(BRANCH_STORE_LINK)) {
            if (!containsStore(branchFor(record), record.values().get("storeId"))) return null;
            Map<String, Object> store =
                    read(
                            new Record(
                                    "Stores",
                                    record.values().get("storeId"),
                                    record.name(),
                                    fields()));
            if (store == null
                    || (!record.name().equals(store.get("arabicName"))
                            && !record.name().equals(store.get("englishName"))))
                throw new AssertionError("Cannot verify owned store membership identity");
            return store;
        }
        Object entity;
        try {
            entity = call("get", record.endpoint(), fields("id", record.id())).get("entity");
        } catch (ApiFailure failure) {
            if (failure.status == 404 || failure.status == 410) return null;
            throw failure;
        }
        // The ERP returns HTTP 200 with an empty string for some deleted records.
        if (entity instanceof String text && text.isBlank()) return null;
        if (!(entity instanceof Map<?, ?>)) {
            throw new AssertionError(
                    "Unexpected fixture read response for "
                            + record.endpoint()
                            + "/"
                            + record.id());
        }
        Map<?, ?> dto = (Map<?, ?>) entity;
        if (dto.get("id") == null
                && dto.get("arabicName") == null
                && dto.get("englishName") == null
                && dto.get("notes") == null
                && !dto.containsKey("pageData")
                && !dto.containsKey("totalRecords")
                && !dto.containsKey("error")
                && !dto.containsKey("message")) return null;
        return (Map<String, Object>) entity;
    }

    Record fixture(String key, String endpoint, Map<String, Object> values) {
        key = fixtureKey(key);
        if (fixtures.containsKey(key)) return fixtures.get(key);
        String name = key.startsWith("QA-PR-") ? key : "QA-PR-" + key + "-" + suffix;
        values.put("arabicName", name);
        values.put("englishName", name);
        values.put("notes", "Owned Products test fixture");
        pendingWrites.start(endpoint, name, values);
        Map<String, Object> response;
        try {
            response = call("create", endpoint, values);
        } catch (ApiFailure | org.openqa.selenium.TimeoutException failure) {
            // Retain a partial commit for cleanup; the original creation still fails.
            if (endpoint.equals("Items"))
                try {
                    recoverAttemptedItems(List.of(fields("endpoint", endpoint, "values", values)));
                } catch (RuntimeException | AssertionError recovery) {
                    failure.addSuppressed(recovery);
                }
            throw failure;
        }
        Object id = response.get("id");
        if (id == null) throw new AssertionError("Fixture API did not return an ID: " + endpoint);
        Record record = new Record(endpoint, id, name, values);
        created.add(record);
        journal();
        fixtures.put(key, record);
        pendingWrites.committed(endpoint, name);
        return record;
    }

    private Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
        return api.call(action, endpoint, values);
    }

    void track(String endpoint, Object id, String note, Map<String, Object> values) {
        if (note == null || !note.startsWith("QA-PR-"))
            throw new AssertionError("Refusing to track an unowned product record");
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

    @SuppressWarnings("unchecked")
    void rememberAttempts(List<Map<String, Object>> attempts, String marker) {
        for (var attempt : attempts) {
            var values = (Map<String, Object>) attempt.get("values");
            String name =
                    java.util.stream.Stream.of("arabicName", "englishName", "notes")
                            .map(values::get)
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .filter(v -> v.startsWith(marker))
                            .findFirst()
                            .orElse(null);
            if (name == null) throw new AssertionError("Unowned captured request");
            pendingWrites.start((String) attempt.get("endpoint"), name, values);
        }
    }

    private void recoverPendingFixtures() {
        pendingWrites.recover(
                (endpoint, values) -> call("get", endpoint, values),
                (endpoint, id, name, values) -> {
                    track(endpoint, id, name, values);
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
                java.nio.file.Path.of("target", "products-fixtures", suffix + ".json"), rows);
    }

    private void deleteRecord(Record record) {
        new ProductsFixtureDeletion(this, api, owned -> changeBranchStore(owned, false))
                .remove(record);
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
