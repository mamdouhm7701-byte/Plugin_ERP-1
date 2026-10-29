/* شرح الملف: يجمع فروع التجارب المثبتة وسجلات بياناتها المتبقية لعرضها قبل التنظيف. لا يتصل بالبرنامج ولا يحذف أي شيء. */
package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import java.nio.file.*;
import java.util.*;
import org.json.*;

final class CleanupRunCatalog {
    record Journal(
            String module,
            String suffix,
            List<Path> files,
            Map<String, String> hashes,
            int records,
            List<String> details,
            boolean supported) {}

    record Entry(
            BranchProfile profile,
            String owner,
            Map<String, String> target,
            List<Journal> journals,
            Path registry,
            String registryHash,
            String recordedAt) {
        public String toString() {
            return recordedAt
                    + " — "
                    + owner
                    + " — "
                    + profile.expected().origin()
                    + " — "
                    + records()
                    + " سجل متبقّي";
        }

        int records() {
            return journals.stream().mapToInt(Journal::records).sum();
        }

        String preview() {
            var text =
                    new StringBuilder(
                            "آخر تحديث للسجل بتوقيت القاهرة: "
                                    + recordedAt
                                    + "\nفرع التجربة: "
                                    + owner
                                    + "\nرقم الفرع: "
                                    + profile.expected().branchId()
                                    + "\nالرابط: "
                                    + profile.expected().origin());
            text.append("\nالتننت المسجل: ")
                    .append(
                            target.getOrDefault(
                                    "tenant", "غير متاح؛ حدده من إعدادات الدخول أدناه"));
            text.append(
                    "\n\nهذه سجلات محلية؛ سيتم التحقق من الملكية والوجود داخل البرنامج أثناء التنظيف.\n");
            if (journals.isEmpty())
                text.append("لا توجد بيانات متبقية مسجلة. يمكن محاولة حذف فرع التجربة فقط.\n");
            for (var journal : journals) {
                text.append("\n")
                        .append(label(journal.module()))
                        .append(": ")
                        .append(journal.records())
                        .append(" سجل");
                if (!journal.supported()) text.append(" — يحتاج مراجعة خاصة؛ سيبقى الفرع محفوظًا");
                text.append("\n").append(String.join("\n", journal.details())).append("\n");
            }
            return text.toString();
        }
    }

    record Inventory(List<Entry> entries, List<String> warnings) {}

    private static final Set<String> AUDITS =
            Set.of(
                    "dealer-page-records",
                    "posting-recovery",
                    "treasury-audit",
                    "treasury-audit-masters",
                    "treasury-audit-financial",
                    "inventory-audit-documents");
    private static final List<String> ORDER =
            List.of(
                    "sales",
                    "purchase",
                    "special-sales",
                    "store",
                    "financial",
                    "dealer-page-records",
                    "dealer",
                    "products",
                    "basic-data");

    private CleanupRunCatalog() {}

    static Inventory load(Path project) throws Exception {
        project = project.toAbsolutePath().normalize();
        var entries = new ArrayList<Entry>();
        var warnings = new ArrayList<String>();
        Path registries = project.resolve("config/business-branch-state");
        if (!Files.isDirectory(registries))
            return new Inventory(
                    List.of(), List.of("لا توجد فروع تجارب مسجلة يمكن إثبات ملكيتها."));
        var targets = targets(project, warnings);
        var journalSources = journalSources(project);
        try (var paths = Files.list(registries)) {
            for (var file : paths.filter(p -> p.toString().endsWith(".json")).sorted().toList()) {
                try {
                    within(project, file);
                    var state = new JSONObject(Files.readString(file));
                    if ("DELETED".equals(state.optString("status"))) continue;
                    String name = state.getString("profile");
                    if (!name.matches("business-[a-f0-9]{16}")
                            || !file.getFileName().toString().equals(name.substring(9) + ".json"))
                        throw new IllegalArgumentException("Registry identity");
                    String owner = "QA-BUS-" + name.substring(9);
                    if (!owner.equals(state.getString("owner")))
                        throw new IllegalArgumentException("Registry owner");
                    Path profileFile =
                            project.resolve("config/branch-profiles/" + name + ".properties");
                    within(project, profileFile);
                    var profile = BranchProfiles.load(name, profileFile.getParent()).orElseThrow();
                    if (!profile.expected().branchId().equals(state.getString("branchId"))
                            || !profile.expected().origin().equals(state.getString("origin")))
                        throw new IllegalArgumentException("Profile identity");
                    var target = targets.getOrDefault(name, Map.of());
                    if (!target.isEmpty()
                            && !BusinessRunTarget.normalizeUrl(target.get("url"))
                                    .equals(profile.expected().origin()))
                        throw new IllegalArgumentException("Target mismatch");
                    entries.add(
                            new Entry(
                                    profile,
                                    owner,
                                    target,
                                    journals(project, profile, journalSources),
                                    file,
                                    hash(file),
                                    recordedAt(file)));
                } catch (Exception | AssertionError invalid) {
                    warnings.add(
                            "لن يُستخدم السجل غير المؤكد: "
                                    + file.getFileName()
                                    + " ("
                                    + invalid.getClass().getSimpleName()
                                    + ")");
                }
            }
        }
        return new Inventory(List.copyOf(entries), List.copyOf(warnings));
    }

    private static Map<String, Map<String, String>> targets(Path project, List<String> warnings)
            throws Exception {
        var result = new LinkedHashMap<String, Map<String, String>>();
        Path validation = project.resolve("target/validation");
        if (!Files.isDirectory(validation)) return result;
        try (var paths = Files.list(validation)) {
            for (var dir : paths.filter(Files::isDirectory).sorted().toList()) {
                Path profile = dir.resolve("branch-profile.json"),
                        settings = dir.resolve("run-settings.json");
                if (!Files.isRegularFile(profile) || !Files.isRegularFile(settings)) continue;
                try {
                    within(project, profile);
                    within(project, settings);
                    String name = new JSONObject(Files.readString(profile)).getString("name");
                    var target = new JSONObject(Files.readString(settings)).getJSONObject("target");
                    result.put(
                            name,
                            Map.of(
                                    "environment",
                                    target.getString("environment"),
                                    "tenant",
                                    target.getString("tenant"),
                                    "url",
                                    target.getString("url")));
                } catch (Exception invalid) {
                    warnings.add("تعذر قراءة هدف تشغيل: " + dir.getFileName());
                }
            }
        }
        return result;
    }

    private static Map<Path, String> journalSources(Path project) throws Exception {
        var sources = new LinkedHashMap<Path, String>();
        Path target = project.resolve("target");
        if (!Files.isDirectory(target)) return sources;
        try (var dirs = Files.list(target)) {
            for (var dir : dirs.filter(Files::isDirectory).sorted().toList()) {
                String folder = dir.getFileName().toString();
                if (!folder.endsWith("-fixtures")
                        && !folder.endsWith("-attempts")
                        && !AUDITS.contains(folder)) continue;
                try (var paths = Files.list(dir)) {
                    for (var file :
                            paths.filter(p -> p.toString().endsWith(".json")).sorted().toList()) {
                        // Empty [] journals contain no records; do not reread them for every
                        // branch.
                        if (Files.size(file) <= 2) continue;
                        within(project, file);
                        sources.put(file, Files.readString(file));
                    }
                }
            }
        }
        return sources;
    }

    private static List<Journal> journals(
            Path project, BranchProfile profile, Map<Path, String> sources) throws Exception {
        var groups = new LinkedHashMap<String, List<Path>>();
        var details = new HashMap<String, List<String>>();
        var totals = new HashMap<String, Integer>();
        var supported = new HashMap<String, Boolean>();
        for (var source : sources.entrySet()) {
            Path file = source.getKey();
            String text = source.getValue();
            if (!text.contains(profile.expected().branchId())) continue;
            String folder = file.getParent().getFileName().toString();
            var rows = new JSONArray(text);
            boolean matches = false, mixed = false;
            var description = new ArrayList<String>();
            for (Object value : rows) {
                var row = (JSONObject) value;
                boolean same =
                        profile.expected().origin().equals(row.optString("origin"))
                                && profile.expected().branchId().equals(row.optString("branchId"));
                matches |= same;
                mixed |= !same;
                if (same && description.size() < 12)
                    description.add(
                            endpointLabel(row.optString("endpoint", "سجل"))
                                    + " — "
                                    + row.optString("name", row.optString("id", "محاولة حفظ")));
            }
            if (!matches) continue;
            String module =
                    AUDITS.contains(folder)
                            ? folder
                            : folder.replaceFirst("-(fixtures|attempts)$", "");
            String suffix = file.getFileName().toString().replaceFirst("\\.json$", "");
            String key = module + "/" + suffix;
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(file);
            details.computeIfAbsent(key, k -> new ArrayList<>()).addAll(description);
            totals.merge(key, rows.length(), Integer::sum);
            supported.merge(
                    key,
                    !mixed && ORDER.contains(module) && suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"),
                    (a, b) -> a && b);
        }
        var result = new ArrayList<Journal>();
        for (var group : groups.entrySet()) {
            String[] key = group.getKey().split("/", 2);
            var hashes = new TreeMap<String, String>();
            for (var file : group.getValue())
                hashes.put(project.relativize(file).toString(), hash(file));
            result.add(
                    new Journal(
                            key[0],
                            key[1],
                            List.copyOf(group.getValue()),
                            Map.copyOf(hashes),
                            totals.get(group.getKey()),
                            List.copyOf(details.get(group.getKey())),
                            supported.get(group.getKey())));
        }
        result.sort(
                Comparator.comparingInt(
                        j ->
                                ORDER.contains(j.module())
                                        ? ORDER.indexOf(j.module())
                                        : ORDER.size()));
        return List.copyOf(result);
    }

    static Entry revalidate(Path project, Entry selected) throws Exception {
        var current =
                load(project).entries().stream()
                        .filter(e -> e.profile().name().equals(selected.profile().name()))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "اختيار التنظيف تغير؛ اضغط تحديث القائمة."));
        if (!current.profile().fingerprint().equals(selected.profile().fingerprint())
                || !current.registryHash().equals(selected.registryHash())
                || !current.journals().equals(selected.journals()))
            throw new IllegalStateException(
                    "بيانات التشغيل تغيرت بعد عرضها؛ حدّث القائمة وراجعها قبل التنظيف.");
        return current;
    }

    static void within(Path project, Path path) throws Exception {
        if (!path.toRealPath().startsWith(project.toRealPath()))
            throw new IllegalArgumentException("Recovery evidence is outside the workspace");
    }

    private static String hash(Path file) throws Exception {
        return HexFormat.of()
                .formatHex(
                        java.security.MessageDigest.getInstance("SHA-256")
                                .digest(Files.readAllBytes(file)));
    }

    private static String recordedAt(Path file) throws Exception {
        return java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(java.time.ZoneId.of("Africa/Cairo"))
                .format(Files.getLastModifiedTime(file).toInstant());
    }

    private static String endpointLabel(String endpoint) {
        return switch (endpoint) {
            case "Stores" -> "مخزن";
            case "Items" -> "صنف";
            case "Accounts" -> "حساب";
            case "Units" -> "وحدة";
            case "ItemCategories" -> "تصنيف أصناف";
            case "Customers" -> "عميل";
            case "Suppliers" -> "مورد";
            case "Agents" -> "مندوب";
            case "Banks" -> "بنك";
            case "Safes" -> "خزنة";
            case "PaymentMethods" -> "وسيلة دفع";
            case "Purchases" -> "فاتورة مشتريات";
            case "PurchaseOrders" -> "أمر شراء";
            case "ReturnPurchases" -> "مرتجع مشتريات";
            case "ItemTransactions-Sales" -> "فاتورة مبيعات";
            case "ItemTransactions-Contract" -> "عقد";
            case "ConcreteDelivery" -> "إذن تسليم خرسانة";
            default -> endpoint;
        };
    }

    static String label(String module) {
        return switch (module) {
            case "sales" -> "المبيعات";
            case "purchase" -> "المشتريات";
            case "special-sales" -> "المطعم ونقاط البيع";
            case "store" -> "المخازن";
            case "financial" -> "الحركات المالية";
            case "dealer", "dealer-page-records" -> "العملاء والموردون";
            case "products" -> "الأصناف";
            case "basic-data" -> "البيانات الأساسية";
            default -> "سجل يحتاج مراجعة: " + module;
        };
    }
}
