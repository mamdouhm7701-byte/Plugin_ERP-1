/* شرح الملف: يراجع استرجاع بيانات أساسية بعد ضياع استجابة الحفظ، ورفض الاسم المكرر أو المرجع المتغير. */
package TestSupport;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureMasterRecoveryTest {
    private record Row(String endpoint, Object id, String name, Map<String, Object> values)
            implements OwnedFixtureRecord {}

    private final Map<String, Object> attempt =
            Map.of(
                    "endpoint",
                    "Items",
                    "name",
                    "QA-SL-owned",
                    "values",
                    Map.of("mainUnitId", "unit"));

    private FixtureMasterRecovery.Access<Row> access(Object entity, Map<String, Object> detail) {
        return new FixtureMasterRecovery.Access<>(
                "QA-SL-",
                Set.of("Items", "Accounts"),
                "branch",
                (url, query) -> Map.of("entity", entity),
                Row::new,
                row -> detail,
                (row, stored) -> row.name().equals(stored.get("arabicName")),
                true);
    }

    private Map<String, Object> row(String id) {
        return Map.of("id", id, "arabicName", "QA-SL-owned", "mainUnitId", "unit");
    }

    @Test
    public void recoversOnlyExactOwnedCandidateWithUnchangedReferences() {
        var found =
                FixtureMasterRecovery.find(attempt, access(List.of(row("item")), row("item")))
                        .orElseThrow();
        Assert.assertEquals(found.id(), "item");
        Assert.assertEquals(found.values(), attempt.get("values"));
    }

    @Test
    public void duplicateNamesNeverChooseAnArbitraryCandidate() {
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureMasterRecovery.find(
                                attempt, access(List.of(row("a"), row("b")), row("a"))));
    }

    @Test
    public void changedDependencyOrOwnershipRemainsUnresolved() {
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureMasterRecovery.find(
                                attempt,
                                access(
                                        List.of(row("a")),
                                        Map.of(
                                                "id",
                                                "a",
                                                "arabicName",
                                                "QA-SL-owned",
                                                "mainUnitId",
                                                "foreign"))));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureMasterRecovery.find(
                                attempt,
                                access(
                                        List.of(row("a")),
                                        Map.of(
                                                "id",
                                                "a",
                                                "arabicName",
                                                "foreign",
                                                "mainUnitId",
                                                "unit"))));
    }

    @Test
    public void repeatedPageCannotMasqueradeAsCompleteLookup() {
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureMasterRecovery.find(
                                attempt,
                                access(
                                        Map.of("pageData", List.of(row("a")), "totalRecords", 2),
                                        row("a"))));
    }

    @Test
    public void malformedTreeOrListDoesNotCountAsAbsence() {
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureMasterRecovery.find(
                                attempt, access(Map.of("unexpected", List.of()), row("a"))));
        var account =
                Map.<String, Object>of(
                        "endpoint", "Accounts", "name", "QA-SL-owned", "values", Map.of());
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureMasterRecovery.find(
                                account, access(Map.of("unrelated", List.of()), row("a"))));
        Assert.assertTrue(
                FixtureMasterRecovery.find(attempt, access(List.of(), Map.of())).isEmpty());
    }

    @Test
    public void unapprovedEndpointOrMarkerCannotReadAnyApplicationData() {
        var reads = new AtomicInteger();
        var context =
                new FixtureMasterRecovery.Access<Row>(
                        "QA-SL-",
                        Set.of("Items"),
                        "branch",
                        (url, query) -> {
                            reads.incrementAndGet();
                            return Map.of();
                        },
                        Row::new,
                        r -> Map.of(),
                        (r, s) -> true,
                        true);
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureMasterRecovery.find(
                                Map.of(
                                        "endpoint",
                                        "Foreign",
                                        "name",
                                        "QA-SL-owned",
                                        "values",
                                        Map.of()),
                                context));
        Assert.expectThrows(
                AssertionError.class,
                () ->
                        FixtureMasterRecovery.find(
                                Map.of(
                                        "endpoint",
                                        "Items",
                                        "name",
                                        "QA-PU-owned",
                                        "values",
                                        Map.of()),
                                context));
        Assert.assertEquals(reads.get(), 0);
    }

    @Test
    public void accountListStrategyPreservesWarehouseLookupWithoutRequiringTree() {
        var context =
                new FixtureMasterRecovery.Access<Row>(
                        "QA-ST-",
                        Set.of("Accounts"),
                        "branch",
                        (url, query) -> {
                            Assert.assertEquals(url, "Accounts?pageNumber=1&pageSize=500");
                            return Map.of(
                                    "entity",
                                    List.of(
                                            Map.of(
                                                    "id",
                                                    "account",
                                                    "arabicName",
                                                    "QA-ST-account")));
                        },
                        Row::new,
                        r -> Map.of("id", "account", "arabicName", "QA-ST-account"),
                        (r, stored) -> true,
                        false);
        var found =
                FixtureMasterRecovery.find(
                                Map.of(
                                        "endpoint",
                                        "Accounts",
                                        "name",
                                        "QA-ST-account",
                                        "values",
                                        Map.of()),
                                context)
                        .orElseThrow();
        Assert.assertEquals(found.id(), "account");
    }
}
