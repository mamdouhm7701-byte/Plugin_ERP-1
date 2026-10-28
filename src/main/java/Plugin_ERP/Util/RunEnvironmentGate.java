/* شرح الملف: إيقاف تجهيز حالات جديدة بعد ثبوت نفاد سعة الترخيص في نفس التشغيل.
 * فائدته: يبقي أول فشل ودليله، ويوضح أن الحالات التالية لم تنفذ، ويسمح بقراءة وتنظيف البيانات.
 */
package Plugin_ERP.Util;

import Plugin_ERP.Bots.LogUtils;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.testng.SkipException;

public final class RunEnvironmentGate {
    public interface ProtectedScenario {}

    public record Block(String code, String resource, String detectedUtc, String source) {}

    private static final Map<String, Gate> RUNS = new ConcurrentHashMap<>();

    private RunEnvironmentGate() {}

    public static Gate current() {
        String run =
                System.getProperty("allure.run.id", "process-" + ProcessHandle.current().pid());
        return RUNS.computeIfAbsent(run, id -> new Gate(block -> persist(id, block)));
    }

    public static void beforeCreate(String action) {
        current().beforeCreate(action);
    }

    public static void observe(String action, String resource, Object error) {
        current().observe(action, resource, Objects.toString(error, ""));
    }

    private static void persist(String run, Block block) {
        try {
            String key =
                    HexFormat.of()
                            .formatHex(
                                    java.security.MessageDigest.getInstance("SHA-256")
                                            .digest(
                                                    run.getBytes(
                                                            java.nio.charset.StandardCharsets
                                                                    .UTF_8)));
            Path folder = Plugin_ERP.Util.Allur.AllureConstants.ALLURE_RESULTS_DIR.getParent();
            if (folder.equals(Plugin_ERP.Util.Allur.AllureConstants.PROJECT_DIR))
                folder = folder.resolve("target");
            Path file = folder.resolve("readiness").resolve(key + ".json");
            Files.createDirectories(file.getParent());
            Files.writeString(
                    file,
                    org.json.simple.JSONValue.toJSONString(
                            Map.of(
                                    "run",
                                    run,
                                    "code",
                                    block.code(),
                                    "resource",
                                    block.resource(),
                                    "source",
                                    block.source(),
                                    "detectedUtc",
                                    block.detectedUtc(),
                                    "cleanupAllowed",
                                    true,
                                    "reason",
                                    CapacityFailure.explanation())),
                    StandardOpenOption.CREATE_NEW);
            LogUtils.warn(
                    "Environment capacity blocked new scenarios; readiness evidence: " + file);
        } catch (Exception reportingFailure) {
            LogUtils.warn(
                    "Could not save readiness evidence ("
                            + reportingFailure.getClass().getSimpleName()
                            + "); capacity protection remains active in memory.");
        }
    }

    public static final class Gate {
        private final AtomicReference<Block> blocked = new AtomicReference<>();
        private final Consumer<Block> evidence;

        public Gate(Consumer<Block> evidence) {
            this.evidence = Objects.requireNonNull(evidence);
        }

        public boolean observe(String action, String resource, String error) {
            if (!action.equals("create") || !CapacityFailure.matches(error)) return false;
            block(resource, "API_RESPONSE");
            return true;
        }

        public void blockCapacity(String resource) {
            block(resource, "CAPACITY_PREFLIGHT");
        }

        private void block(String resource, String source) {
            var block =
                    new Block(
                            "LICENSE_CAPACITY",
                            resource,
                            java.time.Instant.now().toString(),
                            source);
            if (blocked.compareAndSet(null, block)) evidence.accept(block);
        }

        public void beforeCreate(String action) {
            if (action.equals("create")) beforeScenario();
        }

        public void beforeScenario() {
            Block block = blocked.get();
            if (block != null)
                throw new SkipException(
                        "ENVIRONMENT_BLOCKED: LICENSE_CAPACITY; "
                                + CapacityFailure.explanation()
                                + " المورد: "
                                + block.resource());
        }

        public Optional<Block> block() {
            return Optional.ofNullable(blocked.get());
        }
    }
}
