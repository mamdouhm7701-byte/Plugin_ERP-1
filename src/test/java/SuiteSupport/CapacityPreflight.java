/* شرح الملف: قراءة الحد المسموح والمستخدم من نفس مصدر شاشة الاشتراك قبل إنشاء فرع البيزنس.
 * فائدته: يمنع بدء تجهيز معروف أنه لن يجد سعة، ويعرض الحدود غير المعلومة دون تخمينها.
 */
package SuiteSupport;

import Plugin_ERP.Util.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.WebDriver;

public final class CapacityPreflight {
    private CapacityPreflight() {}

    static List<Map<String, Object>> quotas(Map<String, ?> data) {
        var rows = new ArrayList<Map<String, Object>>();
        for (String resource :
                List.of(
                        "Branches",
                        "Stores",
                        "Items",
                        "Dealers",
                        "Sales",
                        "Purchase",
                        "FinancialTransaction",
                        "Vouchers",
                        "Users")) {
            Object plan = data.get("plan" + resource), used = data.get("used" + resource);
            var row = new LinkedHashMap<String, Object>();
            row.put("resource", resource);
            boolean known =
                    plan instanceof Number p
                            && p.longValue() > 0
                            && used instanceof Number u
                            && u.longValue() >= 0;
            row.put("status", known ? "REPORTED" : "NOT_ADVERTISED");
            if (known) {
                row.put("limit", plan);
                row.put("used", used);
                row.put(
                        "remaining",
                        Math.max(0L, ((Number) plan).longValue() - ((Number) used).longValue()));
            }
            rows.add(row);
        }
        return rows;
    }

    static Optional<String> exhaustedRequired(List<Map<String, Object>> quotas) {
        return exhaustedRequired(quotas, true);
    }

    static Optional<String> exhaustedRequired(
            List<Map<String, Object>> quotas, boolean needsBranch) {
        return quotas.stream()
                .filter(
                        row ->
                                (needsBranch ? Set.of("Branches", "Stores") : Set.of("Stores"))
                                        .contains(row.get("resource")))
                .filter(row -> "REPORTED".equals(row.get("status")))
                .filter(row -> ((Number) row.get("remaining")).longValue() == 0)
                .map(row -> row.get("resource").toString())
                .findFirst();
    }

    public static void verify(WebDriver driver, Path output) throws java.io.IOException {
        verify(driver, output, true);
    }

    public static void verify(WebDriver driver, Path output, boolean needsBranch)
            throws java.io.IOException {
        Files.createDirectories(output);
        Map<String, ?> result;
        try {
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
            Object raw =
                    VueComponentAccess.executeAsync(
                            driver,
                            """
                const done=arguments[arguments.length-1];
                erpVue.root().$store.dispatch('app/get',{url:'SubscriptionsDashboard',showOverLay:false})
                  .then(value=>{const source=value?.data||value;const data={};
                    for(const resource of ['Branches','Stores','Items','Dealers','Sales','Purchase','FinancialTransaction','Vouchers','Users'])
                      for(const prefix of ['plan','used']){const key=prefix+resource;if(typeof source?.[key]==='number')data[key]=source[key];}
                    done({available:true,data});})
                  .catch(error=>done({available:false,status:error?.response?.status||error?.status||0}));
                """);
            if (!(raw instanceof Map<?, ?> response)
                    || !Boolean.TRUE.equals(response.get("available"))
                    || !(response.get("data") instanceof Map<?, ?> data)) {
                unavailable(
                        output,
                        "Subscription dashboard did not expose capacity; runtime protection remains active");
                return;
            }
            @SuppressWarnings("unchecked")
            var typed = (Map<String, ?>) data;
            result = typed;
        } catch (RuntimeException failure) {
            unavailable(output, "Capacity read unavailable: " + failure.getClass().getSimpleName());
            return;
        }
        var rows = quotas(result);
        var exhausted = exhaustedRequired(rows, needsBranch);
        Files.writeString(
                output.resolve("capacity-preflight.json"),
                org.json.simple.JSONValue.toJSONString(
                        Map.of(
                                "source",
                                "SubscriptionsDashboard",
                                "quotas",
                                rows,
                                "status",
                                exhausted.isPresent() ? "BLOCKED" : "NO_KNOWN_EXHAUSTION",
                                "reservation",
                                false,
                                "requiresNewBranch",
                                needsBranch,
                                "note",
                                "Snapshot only; unadvertised limits remain unknown. In-flight use by others may change headroom.")));
        if (exhausted.isPresent()) {
            RunEnvironmentGate.current().blockCapacity(exhausted.get());
            throw new IllegalStateException(
                    "ENVIRONMENT_BLOCKED: LICENSE_CAPACITY; " + CapacityFailure.explanation());
        }
    }

    static int workerLimit(int requested, Path preparation) throws java.io.IOException {
        Path evidence = preparation.resolve("capacity-preflight.json");
        if (!Files.isRegularFile(evidence)) return requested;
        var snapshot = new org.json.JSONObject(Files.readString(evidence));
        var rows = snapshot.optJSONArray("quotas");
        if (rows == null) return requested;
        for (int i = 0; i < rows.length(); i++) {
            var row = rows.getJSONObject(i);
            if (row.optString("resource").equals("Stores")
                    && row.optString("status").equals("REPORTED")) {
                long remaining = row.getLong("remaining");
                if (remaining <= 0)
                    throw new IllegalStateException(
                            "ENVIRONMENT_BLOCKED: LICENSE_CAPACITY; no store headroom");
                return (int) Math.min(requested, remaining);
            }
        }
        return requested;
    }

    private static void unavailable(Path output, String reason) throws java.io.IOException {
        Files.writeString(
                output.resolve("capacity-preflight.json"),
                org.json.simple.JSONValue.toJSONString(
                        Map.of(
                                "source",
                                "SubscriptionsDashboard",
                                "status",
                                "UNAVAILABLE",
                                "reason",
                                reason)));
        Plugin_ERP.Bots.LogUtils.warn(
                "Capacity preflight unavailable; limits are unknown and runtime protection remains active.");
    }
}
