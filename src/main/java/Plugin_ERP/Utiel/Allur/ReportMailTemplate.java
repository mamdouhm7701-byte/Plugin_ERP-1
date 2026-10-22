package Plugin_ERP.Utiel.Allur;

import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import Plugin_ERP.Utiel.PropertyReader;

/** Self-contained email presentation: inline styles, table layout, no external assets. */
public final class ReportMailTemplate {
    private ReportMailTemplate() {}
    private static final class JSONObject {
        private final Map<?, ?> values;
        JSONObject(String json) throws Exception { this((Map<?, ?>) new org.json.simple.parser.JSONParser().parse(json)); }
        JSONObject(Map<?, ?> values) { this.values = values; }
        JSONObject getJSONObject(String key) { return new JSONObject((Map<?, ?>) values.get(key)); }
        JSONObject optJSONObject(String key) { return values.get(key) instanceof Map<?, ?> map ? new JSONObject(map) : null; }
        JSONArray optJSONArray(String key) { return values.get(key) instanceof List<?> list ? new JSONArray(list) : null; }
        String optString(String key) { return optString(key, ""); }
        String optString(String key, String fallback) { return values.get(key) instanceof String s ? s : fallback; }
        int optInt(String key) { return (int) optLong(key); }
        long optLong(String key) { return values.get(key) instanceof Number n ? n.longValue() : 0; }
    }
    private record JSONArray(List<?> values) {
        int length() { return values.size(); }
        JSONObject getJSONObject(int index) { return new JSONObject((Map<?, ?>) values.get(index)); }
    }
    public record Failure(String name, String message) {}
    public record Run(String suite, String tenant, String id, int total, int passed, int failed,
            int broken, int skipped, long start, long duration, List<Failure> failures) {}

    public static Run read(Path report, Path results) throws Exception {
        JSONObject summary = new JSONObject(Files.readString(report.resolve("widgets/summary.json")));
        JSONObject stats = summary.getJSONObject("statistic"), time = summary.optJSONObject("time");
        Set<String> suites = new LinkedHashSet<>();
        List<Failure> failures = new ArrayList<>();
        try (var paths = Files.list(results)) {
            for (Path path : paths.filter(p -> p.getFileName().toString().endsWith("-result.json")).sorted().toList()) {
                JSONObject result = new JSONObject(Files.readString(path));
                JSONArray labels = result.optJSONArray("labels");
                if (labels != null) for (int i = 0; i < labels.length(); i++) {
                    JSONObject label = labels.getJSONObject(i);
                    if ("parentSuite".equals(label.optString("name"))) suites.add(label.optString("value"));
                }
                if (Set.of("failed", "broken").contains(result.optString("status"))) {
                    JSONObject detail = result.optJSONObject("statusDetails");
                    failures.add(new Failure(result.optString("fullName", result.optString("name")),
                            detail == null ? "لا توجد تفاصيل إضافية" : detail.optString("message")));
                }
            }
        }
        String tenant = PropertyReader.getProperty("Environment");
        if (!"Test".equalsIgnoreCase(tenant)) tenant = PropertyReader.getProperty("Tenant");
        return new Run(suites.isEmpty() ? "Test run" : String.join(" · ", suites), tenant,
                System.getProperty("allure.run.id", "local"), stats.optInt("total"), stats.optInt("passed"),
                stats.optInt("failed"), stats.optInt("broken"), stats.optInt("skipped"),
                time == null ? 0 : time.optLong("start"), time == null ? 0 : time.optLong("duration"), failures);
    }

    private static String status(Run r) {
        if (r.total() == 0) return "لا توجد نتائج";
        if (r.failed() + r.broken() > 0) return "التشغيل يحتاج مراجعة";
        if (r.skipped() > 0 || r.passed() != r.total()) return "اكتمل التشغيل مع حالات غير مكتملة";
        return "نجح التشغيل بالكامل";
    }
    public static String subject(Run r) {
        String result = r.failed() + r.broken() > 0 ? "مراجعة " + (r.failed() + r.broken()) + " حالات" : status(r);
        return ("[" + r.suite() + "] " + result + " — " + r.tenant() + " — Plugin ERP").replaceAll("[\\r\\n]", " ");
    }
    private static String escape(String text) {
        return Objects.toString(text, "—").replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
    private static String date(Run r) {
        return r.start() <= 0 ? "—" : DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm", Locale.ENGLISH)
                .withZone(ZoneId.of("Africa/Cairo")).format(Instant.ofEpochMilli(r.start()));
    }
    private static String duration(Run r) { return (r.duration() / 60000) + "m " + (r.duration() / 1000 % 60) + "s"; }
    public static String plain(Run r) {
        StringBuilder text = new StringBuilder("Plugin ERP | " + status(r) + "\n" + r.suite()
                + "\nTenant: " + r.tenant() + "\nRun: " + r.id() + "\nCairo: " + date(r)
                + "\nDuration: " + duration(r) + "\nTotal: " + r.total() + " | Passed: " + r.passed()
                + " | Failed: " + r.failed() + " | Broken: " + r.broken() + " | Skipped: " + r.skipped());
        r.failures().stream().limit(5).forEach(f -> text.append("\n").append(f.name()).append(": ").append(shortMessage(f.message())));
        return text + "\n\nExtract allure-report.zip and open index.html. Extract testng-report.zip and open emailable-report.html.";
    }
    private static String shortMessage(String message) {
        String normalized = Objects.toString(message, "").replaceAll("\\s+", " ").trim();
        return normalized.length() > 260 ? normalized.substring(0, 260) + "…" : normalized;
    }
    private static String metric(String label, int count, String color) {
        return "<td width=\"25%\" style=\"padding:16px 4px;text-align:center;border:1px solid #e5eaf1;border-radius:12px;background:#ffffff\">"
                + "<div style=\"font-size:28px;font-weight:700;color:" + color + "\">" + count
                + "</div><div style=\"font-size:12px;color:#64748b;margin-top:6px\">" + label + "</div></td>";
    }
    public static String html(Run r) {
        int percent = r.total() == 0 ? 0 : (int) Math.round(100.0 * r.passed() / r.total());
        boolean issue = r.failed() + r.broken() > 0;
        String color = issue ? "#b42318" : r.passed() == r.total() && r.total() > 0 ? "#087f5b" : "#946200";
        StringBuilder html = new StringBuilder("""
                <!doctype html><html lang="ar" dir="rtl"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"></head>
                <body style="margin:0;padding:0;background:#f0f3f8;font-family:Tahoma,Arial,sans-serif;color:#172b4d">
                <div style="display:none;max-height:0;overflow:hidden;mso-hide:all">%s · %s اختبار · التفاصيل والمرفقات بالداخل</div>
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f0f3f8"><tr><td align="center" style="padding:24px 12px">
                <table role="presentation" width="640" cellspacing="0" cellpadding="0" style="width:100%%;max-width:640px;background:#ffffff;border-radius:20px;overflow:hidden">
                <tr><td style="background:#101e38;padding:30px 28px;border-top:5px solid #37d4b0">
                <div dir="ltr" style="color:#ffffff;font-size:22px;font-weight:700;letter-spacing:1px;text-align:right">PLUGIN <span style="color:#37d4b0">ERP</span></div>
                <div style="color:#b9c8df;font-size:12px;margin-top:8px">تقرير جودة الاختبارات الآلية</div>
                <div dir="auto" style="color:#ffffff;font-size:15px;margin-top:22px;line-height:1.7">%s</div></td></tr>
                <tr><td style="padding:28px">
                <div style="font-size:12px;font-weight:bold;color:%s">نتيجة التشغيل</div>
                <h1 style="font-size:25px;line-height:1.6;margin:8px 0;color:#101e38">%s</h1>
                <p style="font-size:14px;line-height:1.9;color:#64748b;margin:0 0 22px">%s</p>
                <table role="presentation" width="100%%" cellspacing="4" cellpadding="0"><tr>%s%s%s%s</tr></table>
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-top:22px"><tr><td style="font-size:13px;color:#475569">نسبة النجاح</td><td align="left" style="font-size:15px;font-weight:bold;color:#087f5b">%d%%</td></tr></table>
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-top:10px;background:#e9eef5;border-radius:6px"><tr><td height="8" style="height:8px;font-size:0;background:#11a780;border-radius:6px" width="%d%%"></td><td height="8" style="height:8px;font-size:0" width="%d%%"></td></tr></table>
                <div style="font-size:12px;color:#64748b;margin-top:10px">إجمالي الاختبارات: <b style="color:#172b4d">%d</b> · الفشل يشمل أخطاء التنفيذ</div>
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-top:26px;background:#f7f9fc;border:1px solid #e5eaf1;border-radius:12px"><tr><td style="padding:18px;font-size:13px;line-height:2;color:#475569">
                التينانت: <b dir="auto">%s</b><br>توقيت القاهرة: <span dir="ltr">%s</span><br>مدة التشغيل: <span dir="ltr">%s</span><br>معرّف التشغيل: <span dir="ltr" style="word-break:break-all">%s</span></td></tr></table>
                """.formatted(escape(status(r)), r.total(), escape(r.suite()), color, escape(status(r)),
                issue ? "راجع الحالات التالية لتحديد السبب، ثم افتح التقرير الكامل للتفاصيل." : "ملخص التشغيل جاهز، والتقريران التفصيليان مرفقان مع الرسالة.",
                metric("ناجح", r.passed(), "#087f5b"), metric("فاشل", r.failed()+r.broken(), "#b42318"),
                metric("متخطّي", r.skipped(), "#946200"), metric("الإجمالي", r.total(), "#172b4d"),
                percent, percent, 100-percent, r.total(), escape(r.tenant()), escape(date(r)), duration(r), escape(r.id())));
        if (!r.failures().isEmpty()) {
            html.append("<h2 style=\"font-size:17px;margin:28px 0 12px\">الحالات التي تحتاج مراجعة</h2>");
            r.failures().stream().limit(5).forEach(f -> html.append("<div style=\"border:1px solid #f5d4cf;border-right:4px solid #d94a38;border-radius:10px;padding:15px;margin-bottom:10px;background:#fffafa\"><div dir=\"ltr\" style=\"font-size:12px;font-weight:bold;line-height:1.8;overflow-wrap:anywhere;word-break:break-word;text-align:left\">")
                    .append(escape(f.name())).append("</div><div dir=\"auto\" style=\"font-size:12px;color:#7a4844;line-height:1.9;margin-top:8px;word-break:break-word\">")
                    .append(escape(shortMessage(f.message()))).append("</div></div>"));
            if (r.failures().size() > 5) html.append("<p style=\"color:#64748b;font-size:12px\">باقي الحالات متاحة في المرفقات.</p>");
        }
        return html.append("""
                <h2 style="font-size:17px;margin:28px 0 12px">تقاريرك مرفقة بالرسالة</h2>
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="border:1px solid #e5eaf1;border-radius:12px"><tr><td style="padding:18px;font-size:13px;line-height:1.9">
                <b dir="ltr">Allure Report</b><br><span style="color:#64748b">فك ضغط <span dir="ltr">allure-report.zip</span> وافتح <span dir="ltr">index.html</span> في المتصفح.</span>
                <hr style="border:0;border-top:1px solid #e5eaf1;margin:14px 0"><b dir="ltr">TestNG Report</b><br><span style="color:#64748b">فك ضغط <span dir="ltr">testng-report.zip</span> وافتح <span dir="ltr">emailable-report.html</span>.</span>
                </td></tr></table></td></tr>
                <tr><td style="padding:20px 28px;background:#f7f9fc;border-top:1px solid #e5eaf1;color:#64748b;font-size:11px;line-height:1.8">Plugin ERP · إشعار آلي بنتائج الاختبارات<br>الأرقام تعكس نتيجة التشغيل، ولا تعني خلو النظام من العيوب.</td></tr>
                </table></td></tr></table></body></html>
                """).toString();
    }
}
