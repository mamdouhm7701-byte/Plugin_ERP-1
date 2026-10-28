/* شرح الملف: تجهيز سجلات مستقلة للتجربة ومراجعتها وحذفها بعد الانتهاء، ضمن المطعم ونقاط البيع.
 */
package SpecialSalesTest;

import SpecialSalesTest.SpecialSalesFixtureApi.ApiFailure;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

final class SpecialSalesTestData implements AutoCloseable {

    record Record(String endpoint, Object id, String name, Map<String, Object> values)
            implements TestSupport.OwnedFixtureRecord {}

    private final WebDriver driver;
    private final SpecialSalesFixtureApi api;
    private final SpecialSalesFixtureDefinitions definitions =
            new SpecialSalesFixtureDefinitions(this);
    private final String origin;
    private final String suffix;
    private final TestSupport.FixtureSession session;
    private final TestSupport.PendingFixtureWrites pendingWrites;
    private final List<Record> created = new ArrayList<>();
    private final Map<String, Record> fixtures = new LinkedHashMap<>();
    private static final String BRANCH_STORE_LINK = "SpecialSalesBranchStoreMembership";
    private static final String DEFAULTS = "SpecialSalesBranchDefaults";

    SpecialSalesTestData(WebDriver driver) {
        this(driver, UUID.randomUUID().toString().substring(0, 12));
    }

    private SpecialSalesTestData(WebDriver driver, String suffix) {
        this.suffix = suffix;
        this.driver = driver;
        this.session = new TestSupport.FixtureSession(driver);
        this.pendingWrites = new TestSupport.PendingFixtureWrites(session, "special-sales", suffix);
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        d,
                                        "return Boolean(erpVue.root()?.$store?.getters['app/currentBranch']?.id)"));
        Object currentOrigin =
                Plugin_ERP.Util.VueComponentAccess.execute(driver, "return location.origin");
        origin = currentOrigin instanceof String value ? value : "offline";
        api = new SpecialSalesFixtureApi(driver, session::verify);
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
        return key.startsWith("QA-SP-")
                        || key.startsWith("unit-")
                        || key.equals("category-main")
                        || key.equals("category")
                        || key.startsWith("store-")
                        || key.startsWith("account-main-store-")
                        || key.startsWith("account-sub-store-")
                ? key
                : scenarioScope + key;
    }

    @SuppressWarnings("unchecked")
    static SpecialSalesTestData resume(WebDriver driver, String suffix) throws java.io.IOException {
        if (!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))
            throw new IllegalArgumentException("Invalid fixture journal ID");
        var data = new SpecialSalesTestData(driver, suffix);
        SpecialSalesRecoveryState.load(suffix, data.session, data.created, data.pendingWrites);
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
        return branchStores().store(index);
    }

    private SpecialSalesBranchDefaults branchDefaults() {
        return new SpecialSalesBranchDefaults(this, api, session, created, suffix, this::journal);
    }

    Record configureOwnedDefaults(Record customer) {
        return branchDefaults().configureOwnedDefaults(customer);
    }

    void restoreDefaults(Record record) {
        branchDefaults().restoreDefaults(record);
    }

    void syncBranch(Map<String, Object> branch) {
        Plugin_ERP.Util.VueComponentAccess.execute(
                driver,
                "const root=erpVue.root();if(root.$store.getters['app/currentBranch']?.id===arguments[0].id)root.$store.commit('app/setCurrentBranch',arguments[0])",
                branch);
    }

    private SpecialSalesBranchStores branchStores() {
        return new SpecialSalesBranchStores(this, driver, created, fixtures, this::journal);
    }

    Map<String, Object> branchFor(Record link) {
        return branchStores().branchFor(link);
    }

    private boolean containsStore(Map<String, Object> branch, Object storeId) {
        return branchStores().containsStore(branch, storeId);
    }

    private void changeBranchStore(Record link, boolean add) {
        branchStores().changeBranchStore(link, add);
    }

    Map<String, Object> itemValues() {
        return definitions.itemValues();
    }

    Record item(int index) {
        return definitions.item(index);
    }

    Record customer() {
        return definitions.customer();
    }

    Record registeredCustomer() {
        return definitions.registeredCustomer();
    }

    private Record customer(String key) {
        return definitions.customer(key);
    }

    Record bank() {
        return definitions.bank();
    }

    Record payment() {
        return definitions.payment();
    }

    Record safe() {
        return definitions.safe();
    }

    Record table() {
        return definitions.table();
    }

    Object balance(Record item, int index) {
        for (Object value : (List<?>) read(item).get("stores")) {
            var row = (Map<?, ?>) value;
            if (store(index).id().equals(row.get("storeId"))) return row.get("currentBalance");
        }
        throw new AssertionError("Owned stock balance missing");
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
                        "QA-SP-",
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
        if (record.endpoint().equals(DEFAULTS)) return branchFor(record);
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
        return new SpecialSalesFixtureReads(api).read(record);
    }

    Record fixture(String key, String endpoint, Map<String, Object> values) {
        key = fixtureKey(key);
        if (fixtures.containsKey(key)) return fixtures.get(key);
        String name = key.startsWith("QA-SP-") ? key : "QA-SP-" + key + "-" + suffix;
        values.put("arabicName", name);
        values.put("englishName", name);
        values.put(
                "notes",
                (endpoint.startsWith("ItemTransactions-") || endpoint.equals("Tables"))
                        ? name
                        : "Owned POS/restaurant fixture");
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

    Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
        return api.call(action, endpoint, values);
    }

    void track(String endpoint, Object id, String note, Map<String, Object> values) {
        if (note == null || !note.startsWith("QA-SP-"))
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
                java.nio.file.Path.of("target", "special-sales-fixtures", suffix + ".json"), rows);
    }

    private void deleteRecord(Record record) {
        new SpecialSalesFixtureDeletion(this, api, owned -> changeBranchStore(owned, false))
                .remove(record);
    }

    @Override
    public void close() {
        recoverPendingFixtures();
        TestSupport.FixtureCleanup.run(
                created,
                fixtures,
                SpecialSalesCleanupOrder.records(created),
                record -> {
                    if (record.endpoint().equals(DEFAULTS)) restoreDefaults(record);
                    else deleteRecord(record);
                },
                this::journal);
    }
}
