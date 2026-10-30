/* شرح الملف: إدارة بيانات تجربة الشراء: ربط المورد والفاتورة بسجلات التجربة، وتتبع المحاولات غير المحسومة، ثم حذف البيانات المملوكة بعد الحركات التابعة.
 */
package Purches;

import static Purches.PurchaseFixtureEndpoints.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

final class PurchaseTestData implements AutoCloseable {
    record Record(String endpoint, Object id, String name, Map<String, Object> values)
            implements TestSupport.OwnedFixtureRecord {}

    private final WebDriver driver;
    private final PurchaseFixtureApi api;
    final TestSupport.FixtureAccountPool accounts = new TestSupport.FixtureAccountPool();
    private final PurchaseFixtureDefinitions definitions = new PurchaseFixtureDefinitions(this);
    private final String origin;
    private final Object sessionBranchId;
    private final String suffix;
    private final List<Record> created = new ArrayList<>();
    private final Map<String, Record> fixtures = new LinkedHashMap<>();
    private static final String BRANCH_STORE_LINK = "PurchaseBranchStoreMembership";
    private final List<Map<String, Object>> uncertainDocuments = new ArrayList<>();
    private final TestSupport.OwnedFixtureState<Record> state;
    private int scenarioSequence;
    private String scenarioScope = "";

    /** Share page prerequisites, while every case keeps independent business entities. */
    void beginScenario() {
        awaitContext();
        recoverUncertainDocuments();
        scenarioScope = "case-" + (++scenarioSequence) + "-";
    }

    private void awaitContext() {
        new TestSupport.FixtureContextGuard(driver, origin, sessionBranchId).verify();
    }

    String fixtureKey(String key) {
        boolean shared =
                key.startsWith("store-")
                        || key.startsWith("unit-")
                        || key.equals("category-main")
                        || key.startsWith("agent-")
                        || key.startsWith("account-main-pool-")
                        || key.startsWith("account-main-store-")
                        || key.startsWith("account-sub-store-");
        return shared ? key : scenarioScope + key;
    }

    PurchaseTestData(WebDriver driver) {
        this(driver, UUID.randomUUID().toString().substring(0, 12));
    }

    private PurchaseTestData(WebDriver driver, String suffix) {
        this.suffix = suffix;
        this.driver = driver;
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                Plugin_ERP.Util.VueComponentAccess.execute(
                                        d,
                                        "return Boolean(erpVue.root()?.$store?.getters['app/currentBranch']?.id)"));
        Object currentOrigin =
                Plugin_ERP.Util.VueComponentAccess.execute(driver, "return location.origin");
        origin = currentOrigin instanceof String value ? value : "offline";
        sessionBranchId =
                Plugin_ERP.Util.VueComponentAccess.execute(
                        driver, "return erpVue.root().$store.getters['app/currentBranch'].id");
        api = new PurchaseFixtureApi(driver, origin, sessionBranchId, this::awaitContext);
        state =
                new TestSupport.OwnedFixtureState<>(
                        new TestSupport.FixtureRecoveryJournal.Location(
                                java.nio.file.Path.of("target"),
                                "purchase",
                                suffix,
                                origin,
                                sessionBranchId,
                                "QA-PU-"),
                        created,
                        uncertainDocuments,
                        Record::new,
                        MOVEMENTS,
                        MASTERS,
                        TestSupport.FixtureAttemptCapture.Policy.SCENARIO_MOVEMENTS);
    }

    /** Resume only an explicitly chosen journal, in its original tenant and branch. */
    @SuppressWarnings("unchecked")
    static PurchaseTestData resume(WebDriver driver, String suffix) throws java.io.IOException {
        if (!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))
            throw new IllegalArgumentException("Invalid sales journal ID");
        PurchaseTestData data = new PurchaseTestData(driver, suffix);
        data.state.resume();
        return data;
    }

    static Map<String, Object> fields(Object... pairs) {
        return TestSupport.FixtureFields.of(pairs);
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

    private PurchaseBranchStores branchStores() {
        return new PurchaseBranchStores(this, driver, created, fixtures, this::journal);
    }

    Record store(int index) {
        return branchStores().store(index);
    }

    private Map<String, Object> branchFor(Record link) {
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

    Record agent() {
        return definitions.agent();
    }

    Record otherSalesAgent() {
        return definitions.otherSalesAgent();
    }

    Record agent(boolean purchase) {
        return definitions.agent(purchase);
    }

    Record destination() {
        return definitions.destination();
    }

    Record supplier() {
        return definitions.supplier();
    }

    Record customer() {
        return definitions.customer();
    }

    Record customer(int allowedDiscount) {
        return definitions.customer(allowedDiscount);
    }

    Object balance(Record item, int index) {
        return TestSupport.FixtureStockBalance.read(read(item), () -> store(index).id());
    }

    Map<String, Object> rememberSeedAttempt(
            String endpoint, String name, Map<String, Object> values) {
        return state.capture().rememberSeed(endpoint, name, values);
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

    Record discountType() {
        return definitions.discountType();
    }

    Record priceList() {
        return definitions.priceList();
    }

    Record seedPurchase(int index, String type, String date, String quantity, String paid) {
        return seedPurchase(index, type, date, quantity, paid, agent());
    }

    Record seedPurchase(
            int index, String type, String date, String quantity, String paid, Record salesAgent) {
        return new PurchaseSeedInvoices(this)
                .create(
                        index,
                        type,
                        date,
                        quantity,
                        paid,
                        salesAgent,
                        "REF-" + scenarioScope + index + "-" + suffix);
    }

    void deleteOwned(Record record) {
        deleteRecord(record);
    }

    List<Map<String, Object>> ownedSupplierReceipts(Object supplierId) {
        return new PurchaseSupplierReceipts(this, created, (url, query) -> call("get", url, query))
                .find(supplierId);
    }

    java.util.Set<String> documentIdentities() {
        return created.stream()
                .filter(r -> MOVEMENTS.contains(r.endpoint()))
                .map(r -> r.endpoint() + "/" + r.id())
                .collect(java.util.stream.Collectors.toSet());
    }

    void verifyNoNewDocuments(java.util.Set<String> before) {
        recoverUncertainDocuments();
        var added = new java.util.HashSet<>(documentIdentities());
        added.removeAll(before);
        if (!added.isEmpty())
            throw new AssertionError("Invalid sales document committed: " + added);
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> lookup(String endpoint) {
        return (Map<String, Object>) call("get", endpoint, fields()).get("entity");
    }

    void recoverAttemptedItems(List<Map<String, Object>> attempts) {
        TestSupport.PartialItemRecovery.recover(
                attempts,
                new TestSupport.PartialItemRecovery.Access<Record>(
                        "QA-PU-",
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
        if (record.endpoint().equals(BRANCH_STORE_LINK))
            return branchStores().readMembership(record);
        return new TestSupport.FixtureRecordReader(
                        (url, query) -> call("get", url, query),
                        java.util.Set.of("Customers", "Suppliers"))
                .read(record);
    }

    Record fixture(String key, String endpoint, Map<String, Object> values) {
        return new PurchaseFixtureCreation(this, created, fixtures, uncertainDocuments, suffix)
                .create(fixtureKey(key), endpoint, values);
    }

    Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
        return api.call(action, endpoint, values);
    }

    void track(String endpoint, Object id, String note, Map<String, Object> values) {
        state.track("Refusing unrelated sales record", endpoint, id, note, values);
    }

    void journal() {
        state.persistRecords();
    }

    boolean owns(Record record, Map<String, Object> stored) {
        return TestSupport.FixtureOwnership.owns("QA-PU-", MOVEMENTS, record, stored, created);
    }

    @SuppressWarnings("unchecked")
    void rememberAttempts(
            List<Map<String, Object>> attempts, List<Map<String, Object>> saves, String marker) {
        state.capture().remember(attempts, saves, marker);
    }

    void journalAttempts() {
        state.persistAttempts();
    }

    Map<String, Object> rememberFixtureAttempt(
            String endpoint, String name, Map<String, Object> values) {
        return state.capture().rememberMaster(endpoint, name, values);
    }

    @SuppressWarnings("unchecked")
    private void recoverFixtureAttempt(Map<String, Object> attempt) {
        String endpoint = (String) attempt.get("endpoint"), name = (String) attempt.get("name");
        Map<String, Object> values = (Map<String, Object>) attempt.get("values");
        var recovered =
                TestSupport.FixtureMasterRecovery.find(
                        attempt,
                        new TestSupport.FixtureMasterRecovery.Access<Record>(
                                "QA-PU-",
                                MASTERS,
                                sessionBranchId,
                                (url, query) -> call("get", url, query),
                                Record::new,
                                this::read,
                                this::owns,
                                true));
        if (recovered.isPresent()) {
            Record found = recovered.orElseThrow();
            track(endpoint, found.id(), name, values);
            if (endpoint.equals("Stores")) {
                Record link =
                        new Record(
                                BRANCH_STORE_LINK,
                                found.id(),
                                name,
                                fields("branchId", sessionBranchId, "storeId", found.id()));
                if (containsStore(branchFor(link), found.id()))
                    track(BRANCH_STORE_LINK, found.id(), name, link.values());
            }
        }
        uncertainDocuments.remove(attempt);
        journalAttempts();
    }

    private void recoverUncertainDocuments() {
        var movements =
                new PurchaseMovementRecovery(
                        this, created, (url, values) -> call("get", url, values));
        for (var attempt : new ArrayList<>(uncertainDocuments)) {
            if (Boolean.TRUE.equals(attempt.get("factory"))) {
                recoverFixtureAttempt(attempt);
                continue;
            }
            movements.recover(attempt);
            uncertainDocuments.remove(attempt);
            journalAttempts();
        }
    }

    private void deleteRecord(Record record) {
        if (record.endpoint().equals(BRANCH_STORE_LINK)
                && read(
                                new Record(
                                        "Stores",
                                        record.values().get("storeId"),
                                        record.name(),
                                        fields()))
                        == null) {
            // The owned store may already be gone; remove only its journaled membership ID.
            changeBranchStore(record, false);
            return;
        }
        TestSupport.FixtureRecordDeletion.delete(
                record,
                this::read,
                this::owns,
                owned -> {
                    if (owned.endpoint().equals(BRANCH_STORE_LINK)) changeBranchStore(owned, false);
                    else call("delete", owned.endpoint(), fields("id", owned.id()));
                });
    }

    @Override
    public void close() {
        recoverUncertainDocuments();
        TestSupport.FixtureCleanup.run(
                created,
                fixtures,
                new ArrayList<>(created.reversed()),
                this::deleteRecord,
                this::journal);
    }
}
