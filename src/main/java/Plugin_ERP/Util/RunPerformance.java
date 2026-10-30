/* شرح الملف: يقيس التجهيز والخطوات والتنظيف وطلبات البيانات وانتظار التوازي دون تغيير تنفيذها أو تسجيل بيانات الدخول. */
package Plugin_ERP.Util;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.testng.ITestResult;

public final class RunPerformance {
    public record Timing(
            String phase,
            String operation,
            String target,
            long calls,
            long failedCalls,
            long totalNanos,
            long maxNanos) {}

    public static final class Collector {
        private final Map<String, Timing> timings = new ConcurrentHashMap<>();

        public void record(
                String phase, String operation, String target, long nanos, boolean failed) {
            String cleanTarget = target.split("\\?", 2)[0];
            String key = phase + "/" + operation + "/" + cleanTarget;
            timings.compute(
                    key,
                    (ignored, old) ->
                            new Timing(
                                    phase,
                                    operation,
                                    cleanTarget,
                                    old == null ? 1 : old.calls() + 1,
                                    (old == null ? 0 : old.failedCalls()) + (failed ? 1 : 0),
                                    (old == null ? 0 : old.totalNanos()) + Math.max(0, nanos),
                                    Math.max(
                                            old == null ? 0 : old.maxNanos(), Math.max(0, nanos))));
        }

        public List<Timing> snapshot() {
            return timings.values().stream()
                    .sorted(Comparator.comparingLong(Timing::totalNanos).reversed())
                    .toList();
        }
    }

    private static final Map<Path, Collector> COLLECTORS = new ConcurrentHashMap<>();
    private static final String START = RunPerformance.class.getName() + ".start";

    private RunPerformance() {}

    public static String phase(org.testng.ITestNGMethod method) {
        if (method.isTest()) return "execution";
        if (method.isAfterMethodConfiguration()
                || method.isAfterClassConfiguration()
                || method.isAfterTestConfiguration()
                || method.isAfterSuiteConfiguration()) return "cleanup";
        return "setup";
    }

    private static boolean protectedResult(ITestResult result) {
        return result != null
                && result.getInstance() instanceof RunEnvironmentGate.ProtectedScenario;
    }

    private static Collector collector() {
        String evidence = System.getProperty("erp.run.evidence.output", "");
        Path output =
                evidence.isBlank()
                        ? Path.of(
                                "target",
                                "performance-" + ProcessHandle.current().pid(),
                                "performance.json")
                        : Path.of(evidence, "performance.json");
        return COLLECTORS.computeIfAbsent(
                output.toAbsolutePath().normalize(), ignored -> new Collector());
    }

    public static void begin(ITestResult result) {
        if (!protectedResult(result)) return;
        result.setAttribute(START, System.nanoTime());
    }

    public static void end(ITestResult result) {
        if (!protectedResult(result) || !(result.getAttribute(START) instanceof Long start)) return;
        result.removeAttribute(START);
        collector()
                .record(
                        phase(result.getMethod()),
                        "invocation",
                        result.getMethod().getQualifiedName(),
                        System.nanoTime() - start,
                        result.getThrowable() != null);
    }

    public static <T> T request(String operation, String endpoint, Supplier<T> task) {
        ITestResult result = org.testng.Reporter.getCurrentTestResult();
        if (!protectedResult(result)) return task.get();
        long start = System.nanoTime();
        boolean success = false;
        try {
            T value = task.get();
            success = true;
            return value;
        } finally {
            collector()
                    .record(
                            phase(result.getMethod()),
                            operation,
                            endpoint,
                            System.nanoTime() - start,
                            !success);
        }
    }

    public static void lockWait(String name, long nanos) {
        collector().record("scheduling", "class-lock-wait", name, nanos, false);
    }

    public static void stage(String phase, String name, long nanos, boolean failed) {
        collector().record(phase, "branch-lifecycle", name, nanos, failed);
    }

    public static synchronized void write() {
        for (var entry : COLLECTORS.entrySet()) write(entry.getKey(), entry.getValue());
    }

    private static void write(Path output, Collector collector) {
        if (collector.snapshot().isEmpty()) return;
        try {
            var rows = new ArrayList<Map<String, Object>>();
            for (Timing t : collector.snapshot())
                rows.add(
                        Map.of(
                                "phase",
                                t.phase(),
                                "operation",
                                t.operation(),
                                "target",
                                t.target(),
                                "calls",
                                t.calls(),
                                "failedCalls",
                                t.failedCalls(),
                                "totalMilliseconds",
                                t.totalNanos() / 1_000_000.0,
                                "maxMilliseconds",
                                t.maxNanos() / 1_000_000.0));
            Files.createDirectories(output.toAbsolutePath().getParent());
            Files.writeString(
                    output,
                    org.json.simple.JSONValue.toJSONString(
                            Map.of(
                                    "measurements",
                                    rows,
                                    "note",
                                    "Invocation times include nested API calls: do not add the two. Totals across parallel browsers are cumulative, not elapsed runtime. Failed waits remain visible; no retries are added.")));
        } catch (Exception failure) {
            Plugin_ERP.Bots.LogUtils.warn(
                    "Performance summary unavailable: " + failure.getClass().getSimpleName());
        }
    }
}
