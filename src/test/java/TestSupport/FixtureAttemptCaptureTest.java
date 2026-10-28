/* شرح الملف: مراجعة تسجيل محاولات الحفظ مع عزل الحالات وعدم إعادة تسجيل حركة نجح تتبعها بالفعل. */
package TestSupport;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class FixtureAttemptCaptureTest {
    private record Row(String endpoint, Object id, String name, Map<String, Object> values)
            implements OwnedFixtureRecord {}

    private FixtureAttemptCapture capture(
            List<Map<String, Object>> pending,
            List<Row> records,
            FixtureAttemptCapture.Policy policy,
            Runnable persist) {
        return new FixtureAttemptCapture(
                new FixtureAttemptCapture.Owner(
                        "offline", "branch", "QA-SL-", Set.of("Sales"), Set.of("Items")),
                records,
                pending,
                persist,
                policy);
    }

    @Test
    public void recordingOneCasePreservesOtherCasesAndFactoryAttempts() {
        var pending = new ArrayList<Map<String, Object>>();
        var calls = new AtomicInteger();
        var capture =
                capture(
                        pending,
                        List.of(),
                        FixtureAttemptCapture.Policy.SCENARIO_MOVEMENTS,
                        calls::incrementAndGet);
        capture.rememberMaster("Items", "QA-SL-master", Map.of());
        capture.rememberSeed("Sales", "QA-SL-seed", Map.of());
        capture.remember(
                List.of(Map.of("endpoint", "Sales", "values", Map.of("notes", "QA-SL-first"))),
                List.of(),
                "QA-SL-first");
        capture.remember(
                List.of(Map.of("endpoint", "Sales", "values", Map.of("notes", "QA-SL-second"))),
                List.of(),
                "QA-SL-second");
        Assert.assertEquals(pending.size(), 4);
        Assert.assertEquals(calls.get(), 4);
        Assert.assertTrue(pending.stream().anyMatch(row -> "QA-SL-first".equals(row.get("name"))));
    }

    @Test
    public void trackedSuccessfulSaveIsNotAddedAgainAndInputValuesAreCopied() {
        var pending = new ArrayList<Map<String, Object>>();
        var capture =
                capture(
                        pending,
                        List.of(new Row("Sales", "saved", "QA-SL-case", Map.of())),
                        FixtureAttemptCapture.Policy.SCENARIO_MOVEMENTS,
                        () -> {});
        var values = new LinkedHashMap<String, Object>(Map.of("notes", "QA-SL-case"));
        capture.remember(
                List.of(Map.of("endpoint", "Sales", "values", values)),
                List.of(Map.of("endpoint", "Sales", "values", values, "id", "saved")),
                "QA-SL-case");
        Assert.assertTrue(pending.isEmpty());
        capture.remember(
                List.of(Map.of("endpoint", "Sales", "values", values)), List.of(), "QA-SL-case");
        values.put("notes", "changed");
        Assert.assertEquals(
                ((Map<?, ?>) pending.getFirst().get("values")).get("notes"), "QA-SL-case");
    }

    @Test
    public void warehousePolicyReplacesOnlyNonFactoryAttempts() {
        var pending = new ArrayList<Map<String, Object>>();
        var capture =
                capture(
                        pending,
                        List.of(),
                        FixtureAttemptCapture.Policy.WAREHOUSE_MOVEMENTS,
                        () -> {});
        capture.rememberMaster("Items", "QA-SL-master", Map.of());
        capture.remember(
                List.of(Map.of("endpoint", "Sales", "values", Map.of())), List.of(), "QA-SL-first");
        capture.remember(
                List.of(Map.of("endpoint", "Sales", "values", Map.of())),
                List.of(),
                "QA-SL-second");
        Assert.assertEquals(pending.size(), 2);
        Assert.assertFalse(pending.getFirst().containsKey("fixture"));
        Assert.assertEquals(pending.getLast().get("name"), "QA-SL-second");
    }

    @Test
    public void unrelatedMarkerAndMasterEndpointAreRejectedBeforePersistence() {
        var pending = new ArrayList<Map<String, Object>>();
        var calls = new AtomicInteger();
        var capture =
                capture(
                        pending,
                        List.of(),
                        FixtureAttemptCapture.Policy.SCENARIO_MOVEMENTS,
                        calls::incrementAndGet);
        Assert.expectThrows(
                AssertionError.class, () -> capture.remember(List.of(), List.of(), "foreign"));
        Assert.expectThrows(
                AssertionError.class,
                () -> capture.rememberMaster("Foreign", "QA-SL-marker", Map.of()));
        Assert.assertEquals(calls.get(), 0);
        Assert.assertTrue(pending.isEmpty());
    }
}
