/* شرح الملف: جمع أسباب الفشل وأدلتها وتكرارها ونتائج إعادة الاختبار في ملخص واحد بعد التشغيل.
 * فائدته: يوضح الحالات التي تحتاج مراجعة دون تغيير نجاحها أو فشلها أو إغلاق العيوب تلقائيًا.
 */
package Plugin_ERP.Util.Allur;

import Plugin_ERP.Util.Allur.ReviewJson.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

public final class RunFailureOverview {
    private RunFailureOverview() {}

    public static void generate() {
        try {
            Path results = AllureConstants.ALLURE_RESULTS_DIR;
            Path parent =
                    results.getParent().equals(AllureConstants.PROJECT_DIR)
                            ? AllureConstants.PROJECT_DIR.resolve("target")
                            : results.getParent();
            Path output = parent.resolve("failure-review");
            write(
                    AllureConstants.ALLURE_RESULTS_DIR,
                    output,
                    System.getProperty("allure.run.id", "unidentified-run"),
                    registry());
            System.out.println("ملخص الفشل وإعادة الاختبار: " + output.resolve("summary.html"));
        } catch (Exception failure) {
            Plugin_ERP.Bots.LogUtils.warn(
                    "Failure overview unavailable ("
                            + failure.getClass().getSimpleName()
                            + "); original results retained.");
        }
    }

    private static JSONArray registry() throws java.io.IOException {
        try (var stream = RunFailureOverview.class.getResourceAsStream("/issue-register.json")) {
            if (stream == null) throw new java.io.IOException("Issue register missing");
            return new JSONArray(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    static synchronized void write(Path results, Path output, String run, JSONArray issues)
            throws Exception {
        Files.createDirectories(output);
        // Separate processes must not overwrite one another's recurrence history.
        try (var channel =
                        FileChannel.open(
                                output.resolve("history.lock"),
                                StandardOpenOption.CREATE,
                                StandardOpenOption.WRITE);
                var lock = channel.tryLock()) {
            if (lock == null) throw new java.io.IOException("Failure history is busy");
            Path historyPath = output.resolve("history.json");
            JSONObject history =
                    Files.exists(historyPath)
                            ? new JSONObject(Files.readString(historyPath))
                            : new JSONObject();
            Path runOutput = output.resolve("runs").resolve(hash(run));
            Files.createDirectories(runOutput);
            JSONArray rows = new JSONArray();
            JSONObject outcomeCounts = new JSONObject();
            for (var kind : OutcomeClassification.Kind.values()) outcomeCounts.put(kind.label(), 0);
            int passed = 0, failed = 0, skipped = 0, configurations = 0;
            for (var entry : FailureReviewResults.read(results)) {
                Path file = entry.file();
                JSONObject test = entry.test();
                String status = test.optString("status");
                if (entry.configuration()) configurations++;
                else if (status.equals("passed")) passed++;
                else if (status.equals("skipped")) skipped++;
                else if (Set.of("failed", "broken").contains(status)) failed++;
                else continue;
                String key = identity(test);
                JSONObject before = history.optJSONObject(key);
                String name = test.optString("fullName", test.optString("name"));
                String message =
                        test.optJSONObject("statusDetails") == null
                                ? ""
                                : test.getJSONObject("statusDetails").optString("message");
                String step = lastFailedStep(test.optJSONArray("steps"));
                JSONObject issue = match(issues, name, message);
                String trace =
                        test.optJSONObject("statusDetails") == null
                                ? ""
                                : test.getJSONObject("statusDetails").optString("trace");
                var classification =
                        OutcomeClassification.classify(
                                status,
                                message,
                                trace,
                                issue != null && issue.optBoolean("confirmed")
                                        ? issue.optString("kind")
                                        : "");
                String outcome = classification.kind().label();
                outcomeCounts.put(outcome, outcomeCounts.getInt(outcome) + 1);
                boolean bad = Set.of("failed", "broken").contains(status);
                int count = before == null ? 0 : before.optInt("failedRuns");
                boolean sameRun = before != null && run.equals(before.optString("run"));
                if (bad && !sameRun) count++;
                boolean retest =
                        status.equals("passed")
                                && before != null
                                && (Set.of("failed", "broken").contains(before.optString("status"))
                                        || before.optBoolean("retest"));
                String issueId =
                        issue == null
                                ? bad || before == null ? "" : before.optString("issue")
                                : issue.getString("id");
                if (!bad && !issueId.isBlank())
                    for (int i = 0; i < issues.length(); i++)
                        if (issueId.equals(issues.getJSONObject(i).optString("id")))
                            issue = issues.getJSONObject(i);
                JSONObject row =
                        new JSONObject()
                                .put("case", name)
                                .put("title", test.optString("name", name))
                                .put("status", status)
                                .put("outcome", outcome)
                                .put("classificationReason", classification.reason())
                                .put("step", step)
                                .put(
                                        "reason",
                                        status.equals("passed")
                                                ? "اكتملت الحالة بنجاح في هذا التشغيل؛ راجع الدليل قبل إغلاق العيب."
                                                : ReadableFailure.describe(message, step))
                                .put("issue", issueId)
                                .put("failedRuns", count)
                                .put("retest", retest)
                                .put("evidence", file.toAbsolutePath().toUri().toString())
                                .put(
                                        "rule",
                                        issue == null
                                                ? "لا توجد قاعدة مرتبطة بهذا الفشل؛ يحتاج مراجعة"
                                                : issue.optString("rule"))
                                .put("ticket", issue == null ? "" : issue.optString("ticket"))
                                .put("fixedIn", issue == null ? "" : issue.optString("fixedIn"))
                                .put(
                                        "verifiedIn",
                                        issue == null ? "" : issue.optString("verifiedIn"))
                                .put("tracking", tracking(issue, retest, bad));
                if (bad || status.equals("skipped") || retest) {
                    Path evidence =
                            FailureEvidenceArchive.save(
                                    results, file, runOutput.resolve("evidence"), test);
                    row.put("evidence", evidence.toAbsolutePath().toUri().toString());
                    row.put(
                            "attachments",
                            FailureEvidenceArchive.links(test, evidence.getParent()));
                    rows.put(row);
                }
                // A skipped case is not evidence of a fix and must not erase the previous
                // outcome.
                if (!status.equals("skipped"))
                    history.put(
                            key,
                            new JSONObject()
                                    .put("run", run)
                                    .put("status", status)
                                    .put("failedRuns", count)
                                    .put("issue", issueId)
                                    .put("retest", retest));
            }
            JSONObject summary =
                    new JSONObject()
                            .put("run", run)
                            .put("passed", passed)
                            .put("failedOrBroken", failed)
                            .put("configurationFailures", configurations)
                            .put("outcomeCounts", outcomeCounts)
                            .put("skipped", skipped)
                            .put("cases", rows)
                            .put(
                                    "note",
                                    "التكرار عدد تشغيلات الحالة الفاشلة المسجلة محليًا، وليس إثبات تكرار نفس العيب. النجاح لا يغلق العيب تلقائيًا.");
            Files.writeString(runOutput.resolve("summary.json"), summary.toString());
            Files.writeString(runOutput.resolve("summary.html"), html(summary));
            Files.writeString(output.resolve("summary.json"), summary.toString());
            Files.writeString(output.resolve("summary.html"), html(summary));
            Path temporary = Files.createTempFile(output, "history-", ".tmp");
            Files.writeString(temporary, history.toString());
            try {
                Files.move(
                        temporary,
                        historyPath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Plugin_ERP.Bots.LogUtils.debug(
                        "Atomic history replacement unavailable; using same-directory replacement");
                Files.move(temporary, historyPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static JSONObject match(JSONArray issues, String name, String message) {
        for (int i = 0; i < issues.length(); i++) {
            JSONObject issue = issues.getJSONObject(i);
            if (!name.equals(issue.optString("method"))) continue;
            JSONArray signatures = issue.optJSONArray("failureContains");
            if (signatures != null)
                for (int n = 0; n < signatures.length(); n++)
                    if (!signatures.getString(n).isBlank()
                            && message.contains(signatures.getString(n))) return issue;
        }
        return null;
    }

    private static String tracking(JSONObject issue, boolean retest, boolean bad) {
        if (issue == null)
            return retest ? "نجح إعادة الاختبار؛ يحتاج تأكيد الإغلاق" : "يحتاج مراجعة";
        String state = issue.optString("state", "OPEN");
        if (bad && Set.of("VERIFIED", "FIX_READY").contains(state))
            return "فشل مجددًا؛ راجع الإصلاح أو إعادة فتح العيب";
        if (state.equals("VERIFIED")
                && (issue.optString("fixedIn").isBlank()
                        || issue.optString("verifiedIn").isBlank()))
            return "إغلاق غير مكتمل؛ سجل إصدار الإصلاح ودليل إعادة الاختبار";
        if (retest)
            return state.equals("VERIFIED")
                    ? "تم تأكيد الإصلاح يدويًا في سجل المتابعة"
                    : "نجح إعادة الاختبار؛ يحتاج تأكيد الإغلاق";
        return switch (state) {
            case "OPEN" -> "مفتوح";
            case "REOPENED" -> "أعيد فتحه";
            case "FIX_READY" -> "الإصلاح جاهز لإعادة الاختبار";
            case "VERIFIED" -> "تم تأكيد الإصلاح يدويًا";
            default -> "حالة متابعة غير معروفة؛ راجع السجل";
        };
    }

    private static String identity(JSONObject test) throws Exception {
        List<String> parameters = new ArrayList<>();
        JSONArray values = test.optJSONArray("parameters");
        if (values != null)
            for (int i = 0; i < values.length(); i++) {
                JSONObject parameter = values.getJSONObject(i);
                String name = parameter.optString("name");
                if (parameter.optBoolean("excluded")
                        || name.toLowerCase(Locale.ROOT)
                                .matches(".*(password|secret|token|كلمة المرور).*")) continue;
                parameters.add(name + "=" + parameter.optString("value"));
            }
        Collections.sort(parameters);
        List<String> scope = new ArrayList<>();
        JSONArray labels = test.optJSONArray("labels");
        if (labels != null)
            for (int i = 0; i < labels.length(); i++) {
                JSONObject label = labels.getJSONObject(i);
                if (label.optString("name").equals("review.scope"))
                    scope.add(label.optString("value"));
            }
        return hash(test.optString("fullName", test.optString("name")) + parameters + scope);
    }

    private static String hash(String value) throws Exception {
        return HexFormat.of()
                .formatHex(
                        MessageDigest.getInstance("SHA-256")
                                .digest(value.getBytes(StandardCharsets.UTF_8)));
    }

    private static String lastFailedStep(JSONArray steps) {
        String last = "";
        if (steps != null)
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                if (Set.of("failed", "broken").contains(step.optString("status")))
                    last = step.optString("name");
                String child = lastFailedStep(step.optJSONArray("steps"));
                if (!child.isBlank()) last = child;
            }
        return last;
    }

    private static String html(JSONObject summary) {
        StringBuilder out =
                new StringBuilder(
                        "<!doctype html><html lang='ar' dir='rtl'>"
                                + "<meta charset='UTF-8'><title>ملخص الفشل وإعادة الاختبار</title>"
                                + "<style>body{font-family:Tahoma,Arial;margin:32px;line-height:1.9}"
                                + "article{border:1px solid #ccc;padding:20px;margin:20px 0;overflow-wrap:anywhere}</style>"
                                + "<h1>ملخص الفشل وإعادة الاختبار</h1>");
        out.append("<p>ناجح: ")
                .append(summary.getInt("passed"))
                .append(" | فشل أو توقف: ")
                .append(summary.getInt("failedOrBroken"))
                .append(" | لم ينفذ: ")
                .append(summary.getInt("skipped"))
                .append(" | أخطاء التجهيز أو التنظيف: ")
                .append(summary.getInt("configurationFailures"))
                .append("</p><p>")
                .append(escape(summary.getString("note")))
                .append("</p>");
        out.append("<p>معرف التشغيل: ").append(escape(summary.getString("run"))).append("</p>");
        if (summary.getInt("passed") + summary.getInt("failedOrBroken") + summary.getInt("skipped")
                == 0) out.append("<p>لم تسجل أي نتيجة حالة؛ لا يعد هذا تشغيلًا ناجحًا.</p>");
        JSONArray rows = summary.getJSONArray("cases");
        out.append(
                "<h2>تصنيف النتائج</h2><p>يشمل الحالات وأخطاء التجهيز والتنظيف المسجلة. التصنيف لا يغيّر نتيجة التست.</p><ul>");
        for (var kind : OutcomeClassification.Kind.values())
            out.append("<li>")
                    .append(escape(kind.label()))
                    .append(": ")
                    .append(summary.getJSONObject("outcomeCounts").getInt(kind.label()))
                    .append("</li>");
        out.append("</ul>");
        if (rows.isEmpty())
            out.append("<p>لا توجد حالات فاشلة أو متخطاة أو إعادة اختبار تحتاج متابعة.</p>");
        for (int i = 0; i < rows.length(); i++) {
            JSONObject row = rows.getJSONObject(i);
            out.append("<article><h2>").append(escape(row.getString("title"))).append("</h2>");
            for (String field :
                    List.of(
                            "outcome",
                            "classificationReason",
                            "reason",
                            "step",
                            "rule",
                            "issue",
                            "tracking",
                            "ticket",
                            "fixedIn",
                            "verifiedIn"))
                out.append("<p>")
                        .append(escape(label(field)))
                        .append(": ")
                        .append(
                                escape(
                                        row.optString(field).isBlank()
                                                ? "غير مسجل"
                                                : row.getString(field)))
                        .append("</p>");
            out.append("<p>مرات فشل الحالة: ")
                    .append(row.getInt("failedRuns"))
                    .append("</p><a href=\"")
                    .append(escape(row.getString("evidence")))
                    .append("\">نسخة النتيجة للمراجعة</a>");
            JSONArray attachments = row.optJSONArray("attachments");
            if (attachments != null)
                for (int n = 0; n < attachments.length(); n++) {
                    JSONObject attachment = attachments.getJSONObject(n);
                    out.append("<p><a href=\"")
                            .append(escape(attachment.getString("url")))
                            .append("\">")
                            .append(escape(attachment.getString("name")))
                            .append("</a></p>");
                }
            out.append("</article>");
        }
        return out.append("</html>").toString();
    }

    private static String label(String field) {
        return switch (field) {
            case "outcome" -> "تصنيف النتيجة";
            case "classificationReason" -> "سبب التصنيف";
            case "reason" -> "ما حدث";
            case "step" -> "موضع التوقف";
            case "rule" -> "القاعدة المطلوبة";
            case "issue" -> "معرف المتابعة";
            case "tracking" -> "المتابعة";
            case "fixedIn" -> "إصدار الإصلاح";
            case "verifiedIn" -> "تشغيل تأكيد الإصلاح";
            default -> "التذكرة الخارجية";
        };
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
