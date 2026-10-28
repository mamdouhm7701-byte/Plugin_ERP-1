/* شرح الملف: تجهيز سجلات مستقلة للتجربة ومراجعتها وحذفها بعد الانتهاء، ضمن الحركات المالية.
 */
package FinancialMovementsTest;

import FinancialMovementsTest.FinancialFixtureApi.ApiFailure;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

final class FinancialTestData implements AutoCloseable {

    record Record(String endpoint, Object id, String name, Map<String, Object> values)
            implements TestSupport.OwnedFixtureRecord {}

    private final WebDriver driver;
    private final FinancialFixtureApi api;
    private final FinancialFixtureDefinitions definitions = new FinancialFixtureDefinitions(this);
    private final String suffix;
    private final TestSupport.FixtureSession session;
    private final TestSupport.PendingFixtureWrites pendingWrites;
    private final List<Record> created = new ArrayList<>();
    private final Map<String, Record> fixtures = new LinkedHashMap<>();

    FinancialTestData(WebDriver driver) {
        this(driver, UUID.randomUUID().toString().substring(0, 12));
    }

    private FinancialTestData(WebDriver driver, String suffix) {
        this.suffix = suffix;
        this.driver = driver;
        this.session = new TestSupport.FixtureSession(driver);
        this.pendingWrites = new TestSupport.PendingFixtureWrites(session, "financial", suffix);
        this.api = new FinancialFixtureApi(driver, session::verify);
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
        return key.startsWith("QA-FM-")
                        || key.equals("invoice-unit")
                        || key.equals("invoice-category-main")
                        || key.equals("invoice-store")
                        || key.equals("account-main-store")
                        || key.equals("account-sub-store")
                ? key
                : scenarioScope + key;
    }

    @SuppressWarnings("unchecked")
    static FinancialTestData resume(WebDriver driver, String suffix) throws java.io.IOException {
        if (!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))
            throw new IllegalArgumentException("Invalid fixture journal ID");
        var data = new FinancialTestData(driver, suffix);
        var path = java.nio.file.Path.of("target", "financial-fixtures", suffix + ".json");
        if (java.nio.file.Files.exists(path))
            for (Object value :
                    new org.json.JSONArray(TestSupport.FixtureJournals.read(path)).toList()) {
                var row = (Map<String, Object>) value;
                data.session.verify(row);
                String endpoint = (String) row.get("endpoint"), name = (String) row.get("name");
                if (name == null
                        || !name.startsWith("QA-FM-")
                        || !java.util.Set.of(
                                        "Accounts",
                                        "Customers",
                                        "Suppliers",
                                        "Banks",
                                        "Safes",
                                        "PaymentMethods",
                                        "Expenses",
                                        "Incomes",
                                        "DiscountTypes",
                                        "Agents",
                                        "Stores",
                                        "Units",
                                        "ItemCategories",
                                        "Items",
                                        "Purchases",
                                        "ItemTransactions-Sales",
                                        "Collections",
                                        "Payments",
                                        "IncomeTransactions",
                                        "ExpenseTransactions",
                                        "CustomerDiscountTransactions",
                                        "SupplierDiscountTransactions",
                                        "TransfersAndSettlements")
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
                        "Customers",
                        "Suppliers",
                        "Banks",
                        "Safes",
                        "PaymentMethods",
                        "Expenses",
                        "Incomes",
                        "DiscountTypes",
                        "Agents",
                        "Stores",
                        "Units",
                        "ItemCategories",
                        "Items",
                        "Purchases",
                        "ItemTransactions-Sales",
                        "Collections",
                        "Payments",
                        "IncomeTransactions",
                        "ExpenseTransactions",
                        "CustomerDiscountTransactions",
                        "SupplierDiscountTransactions",
                        "TransfersAndSettlements"),
                "QA-FM-");
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

    Record customer(int index) {
        return definitions.customer(index);
    }

    Record supplier(int index) {
        return definitions.supplier(index);
    }

    Record bank(int index) {
        return definitions.bank(index);
    }

    Record safe(int index) {
        return definitions.safe(index);
    }

    Record payment() {
        return definitions.payment();
    }

    Record expense() {
        return definitions.expense();
    }

    Record income() {
        return definitions.income();
    }

    Record discount() {
        return definitions.discount();
    }

    Record agent(boolean purchase) {
        return definitions.agent(purchase);
    }

    Record invoice(boolean purchase, java.math.BigDecimal allocation) {
        return definitions.invoice(purchase, allocation, suffix);
    }

    Record party(String type, int index) {
        return definitions.party(type, index);
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> lookup(String endpoint) {
        return (Map<String, Object>) call("get", endpoint, fields()).get("entity");
    }

    Map<String, Object> read(Record record) {
        return new FinancialFixtureReads(api).read(record);
    }

    Record fixture(String key, String endpoint, Map<String, Object> values) {
        key = fixtureKey(key);
        if (fixtures.containsKey(key)) return fixtures.get(key);
        String name = "QA-FM-" + key + "-" + suffix;
        values.put("arabicName", name);
        values.put("englishName", name);
        values.put(
                "notes",
                endpoint.equals("Purchases") || endpoint.equals("ItemTransactions-Sales")
                        ? name
                        : "Owned Financial test fixture");
        pendingWrites.start(endpoint, name, values);
        Map<String, Object> response;
        try {
            response = call("create", endpoint, values);
        } catch (ApiFailure | org.openqa.selenium.TimeoutException failure) {
            // Items can be committed before a later server-side step fails. Recover its ID
            // for cleanup only; never turn the original failed creation into a passing test.
            if (endpoint.equals("Items"))
                try {
                    List<Map<String, Object>> matches = new ArrayList<>();
                    for (int page = 1; page <= 100; page++) {
                        Object entity =
                                call(
                                                "get",
                                                "Items/Summary?pageNumber="
                                                        + page
                                                        + "&pageSize=500",
                                                fields())
                                        .get("entity");
                        findOwned(entity, name, matches);
                        if (!matches.isEmpty() || !(entity instanceof Map<?, ?> result)) break;
                        Object rows = result.get("pageData"), total = result.get("totalRecords");
                        if (!(rows instanceof List<?> list)
                                || list.isEmpty()
                                || !(total instanceof Number count)
                                || page * (long) list.size() >= count.longValue()) break;
                    }
                    if (matches.size() == 1) {
                        Object recoveredId = matches.get(0).get("id");
                        Record recovered = new Record(endpoint, recoveredId, name, values);
                        Map<String, Object> stored = read(recovered);
                        if (name.equals(stored.get("arabicName"))
                                && values.get("mainUnitId").equals(stored.get("mainUnitId"))
                                && values.get("categoryId").equals(stored.get("categoryId"))) {
                            created.add(recovered);
                            journal();
                        }
                    }
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
        if (note == null || !note.startsWith("QA-FM-"))
            throw new AssertionError("Refusing to track an unowned financial record");
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
                java.nio.file.Path.of("target", "financial-fixtures", suffix + ".json"), rows);
    }

    void preserveTreasuryAuditHistory() {
        session.verify();
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
                java.nio.file.Path.of("target", "treasury-audit-financial", suffix + ".json"),
                rows);
        created.clear();
        fixtures.clear();
        journal();
    }

    private void deleteRecord(Record record) {
        new FinancialFixtureDeletion(this, api).remove(record);
    }

    @SuppressWarnings("unchecked")
    private static void findOwned(Object node, String name, List<Map<String, Object>> matches) {
        if (node instanceof Map<?, ?> row) {
            if (name.equals(row.get("arabicName")) && row.get("id") != null)
                matches.add((Map<String, Object>) row);
            else for (Object value : row.values()) findOwned(value, name, matches);
        } else if (node instanceof List<?> rows)
            for (Object row : rows) findOwned(row, name, matches);
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
