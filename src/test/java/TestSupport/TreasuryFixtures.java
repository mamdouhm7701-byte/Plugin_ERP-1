package TestSupport;

import static TestSupport.BasicDataFixtures.fields;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.*;
import org.openqa.selenium.WebDriver;

/**
 * Durable linkage for handovers, whose API identity is their owned safe and user rather than a
 * name.
 */
public final class TreasuryFixtures implements AutoCloseable {
    private final WebDriver driver;
    private final FixtureSession session;
    private final BasicDataFixturesApi api;
    private final Path journal;
    private final List<Map<String, Object>> intents = new ArrayList<>();

    public TreasuryFixtures(WebDriver driver) {
        this(driver, UUID.randomUUID().toString());
    }

    private TreasuryFixtures(WebDriver driver, String suffix) {
        this.driver = driver;
        session = new FixtureSession(driver);
        api = new BasicDataFixturesApi(driver, session::verify);
        journal = Path.of("target", "treasury-fixtures", suffix + ".json");
    }

    @SuppressWarnings("unchecked")
    public static TreasuryFixtures resume(WebDriver driver, String suffix)
            throws java.io.IOException {
        if (!suffix.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"))
            throw new IllegalArgumentException("Invalid treasury journal suffix");
        var owner = new TreasuryFixtures(driver, suffix);
        for (Object value :
                new org.json.JSONArray(TestSupport.FixtureJournals.read(owner.journal)).toList()) {
            var row = (Map<String, Object>) value;
            owner.session.verify(row);
            if (!(row.get("safeName") instanceof String safe)
                    || !safe.startsWith("QA-BD-")
                    || !(row.get("userName") instanceof String user)
                    || !user.startsWith("QA-BD-")
                    || row.get("safeId") == null
                    || row.get("userId") == null)
                throw new AssertionError("Invalid treasury ownership journal");
            owner.intents.add(row);
        }
        return owner;
    }

    public void expect(BasicDataFixtures.Record safe, BasicDataFixtures.Record user) {
        if (!safe.endpoint().equals("Safes")
                || !user.endpoint().equals("users")
                || !safe.name().startsWith("QA-BD-")
                || !user.name().startsWith("QA-BD-"))
            throw new AssertionError("Treasury prerequisites must belong to the fixture owner");
        if (intents.stream()
                .anyMatch(
                        x -> same(x.get("safeId"), safe.id()) && same(x.get("userId"), user.id())))
            return;
        if (!matching(safe.id(), user.id()).isEmpty())
            throw new AssertionError("Fresh treasury prerequisites already have transactions");
        intents.add(
                fields(
                        "origin",
                        session.origin,
                        "branchId",
                        session.branchId,
                        "safeId",
                        safe.id(),
                        "userId",
                        user.id(),
                        "safeName",
                        safe.name(),
                        "userName",
                        user.name()));
        persist(); // Before both API and UI submissions, including timed-out/failed submissions.
    }

    public Object open(
            BasicDataFixtures.Record safe, BasicDataFixtures.Record user, double balance) {
        expect(safe, user);
        api.call(
                "create",
                "SafeTransactions/open",
                fields(
                        "safeId",
                        safe.id(),
                        "userId",
                        user.id(),
                        "openingBalance",
                        balance,
                        "openDate",
                        OffsetDateTime.now().toString(),
                        "isClosed",
                        false));
        return requireSingle(safe, user).get("id");
    }

    public Map<String, Object> awaitSingle(
            BasicDataFixtures.Record safe, BasicDataFixtures.Record user) {
        return new org.openqa.selenium.support.ui.WebDriverWait(
                        driver, java.time.Duration.ofSeconds(20))
                .until(
                        d -> {
                            var rows = matching(safe.id(), user.id());
                            if (rows.size() > 1)
                                throw new AssertionError("Duplicate owned handovers");
                            return rows.size() == 1 ? rows.get(0) : null;
                        });
    }

    public Map<String, Object> requireSingle(
            BasicDataFixtures.Record safe, BasicDataFixtures.Record user) {
        List<Map<String, Object>> rows = matching(safe.id(), user.id());
        if (rows.size() != 1)
            throw new AssertionError("Expected exactly one owned handover, found " + rows.size());
        return rows.get(0);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> totals(Object id) {
        Object entity =
                api.call("get", "SafeTransactions/safe-totals/" + id, fields()).get("entity");
        if (!(entity instanceof Map<?, ?> map) || !(map.get("value") instanceof Map<?, ?> value))
            throw new AssertionError("Unexpected handover totals response");
        return (Map<String, Object>) value;
    }

    static boolean same(Object a, Object b) {
        return a != null && b != null && a.toString().equals(b.toString());
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> matching(Object safeId, Object userId) {
        List<Map<String, Object>> found = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        for (boolean closed : List.of(false, true)) {
            long seen = 0;
            for (int page = 1; ; page++) {
                Object entity =
                        api.call(
                                        "get",
                                        "SafeTransactions?pageNumber="
                                                + page
                                                + "&pageSize=500&isClosed="
                                                + closed
                                                + "&userId="
                                                + userId,
                                        fields())
                                .get("entity");
                if (!(entity instanceof Map<?, ?> result)
                        || !(result.get("pageData") instanceof List<?> rows)
                        || !(result.get("totalRecords") instanceof Number total))
                    throw new AssertionError("Cannot enumerate treasury ownership");
                for (Object raw : rows) {
                    if (!(raw instanceof Map<?, ?> row) || row.get("id") == null)
                        throw new AssertionError("Invalid treasury list identity");
                    if (same(row.get("safeId"), safeId)
                            && same(row.get("userId"), userId)
                            && seenIds.add(row.get("id").toString()))
                        found.add((Map<String, Object>) row);
                }
                seen += rows.size();
                if (seen >= total.longValue()) break;
                if (rows.isEmpty() || page >= 1000)
                    throw new AssertionError("Incomplete treasury list");
            }
        }
        return found;
    }

    private void persist() {
        FixtureJournals.write(journal, intents);
    }

    @Override
    public void close() {
        session.verify();
        for (var intent : new ArrayList<>(intents)) {
            session.verify(intent);
            // Revalidate parents before closing or reviewing an exact owned transaction.
            verifyParent("Safes", intent.get("safeId"), intent.get("safeName"));
            verifyParent("users", intent.get("userId"), intent.get("userName"));
            for (var row : matching(intent.get("safeId"), intent.get("userId"))) {
                Object id = row.get("id");
                // Closing is the supported terminal lifecycle; treasury history is retained.
                if (!Boolean.TRUE.equals(row.get("isClosed"))) {
                    var expected = TreasuryAmounts.from(totals(id), java.math.BigDecimal.ZERO);
                    api.call(
                            "create",
                            "SafeTransactions/close",
                            fields(
                                    "transactionId",
                                    id,
                                    "actualBalance",
                                    expected.actual(),
                                    "deficit",
                                    expected.deficit(),
                                    "closeDate",
                                    OffsetDateTime.now().toString(),
                                    "notes",
                                    intent.get("safeName")));
                    var remaining = matching(intent.get("safeId"), intent.get("userId"));
                    if (remaining.stream()
                            .anyMatch(
                                    r ->
                                            same(r.get("id"), id)
                                                    && !Boolean.TRUE.equals(r.get("isClosed"))))
                        throw new AssertionError(
                                "Owned handover did not close; retain journal " + journal);
                }
                api.call("update", "safeTransactions/" + id + "/review-status/true", fields());
                var reviewed = matching(intent.get("safeId"), intent.get("userId"));
                if (reviewed.stream()
                        .noneMatch(
                                r ->
                                        same(r.get("id"), id)
                                                && Boolean.TRUE.equals(r.get("isClosed"))
                                                && Boolean.TRUE.equals(r.get("isReviewed"))))
                    throw new AssertionError(
                            "Owned handover was not closed and reviewed; retain journal "
                                    + journal);
                FixtureJournals.write(Path.of("target", "treasury-audit", id + ".json"), reviewed);
            }
            intents.remove(intent);
            persist();
        }
    }

    private void verifyParent(String endpoint, Object id, Object name) {
        Object entity = api.call("get", endpoint, fields("id", id)).get("entity");
        if (!(entity instanceof Map<?, ?> row)
                || !same(row.get("id"), id)
                || !(name.equals(row.get("arabicName"))
                        || name.equals(row.get("englishName"))
                        || name.equals(row.get("fullName"))))
            throw new AssertionError(
                    "Treasury parent ownership changed; retaining journal " + journal);
    }
}
