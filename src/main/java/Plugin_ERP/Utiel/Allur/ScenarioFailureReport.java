package Plugin_ERP.Utiel.Allur;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.math.BigDecimal;
import java.util.Map;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.ITestResult;

/** Adds a readable view of recorded evidence; never changes the outcome or original failure. */
public final class ScenarioFailureReport {
    private static final int MAX_STEPS = 25;
    private static final Pattern ARABIC_VALUES = Pattern.compile(
            "(?:المتوقع|المتوقعة)\\s*[:=]?\\s*(.*?)\\s*[،,؛;]\\s*(?:الفعلي|الفعلية)\\s*[:=]?\\s*(.+)");
    private static final Pattern MESSAGE_VALUES = Pattern.compile(
            "Actual\\s*=\\s*([^,\\r\\n]+),\\s*expected\\s*=\\s*([^\\s,;]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CUSTOMER_BALANCE = Pattern.compile(
            "Owned customer balance did not reach\\s+([-+]?\\d+(?:\\.\\d+)?);\\s*actual DTO=(\\{.*\\})");
    private ScenarioFailureReport() {}

    public static void attach(ITestResult result) {
        var lifecycle = Allure.getLifecycle();
        var current = lifecycle.getCurrentTestCase();
        if (current.isEmpty()) return;
        try {
            AtomicReference<TestResult> captured = new AtomicReference<>();
            lifecycle.updateTestCase(current.get(), test -> captured.set(snapshot(test)));
            TestResult test = captured.get();
            if (test == null) return;
            Throwable failure = result == null ? null : result.getThrowable();
            String message = failure == null ? test.getStatusDetails() == null ? null
                    : test.getStatusDetails().getMessage() : failure.getMessage();
            Allure.addAttachment("شرح مبسط للفشل", "text/html", render(test, message), ".html");
            if (failure != null) {
                StringWriter trace = new StringWriter();
                failure.printStackTrace(new PrintWriter(trace));
                Allure.addAttachment("الخطأ الأصلي للمراجعة الفنية", "text/plain", trace.toString(), ".txt");
            }
        } catch (RuntimeException reportingFailure) {
            // Reporting is supplementary; never replace the test's original exception.
        }
    }

    /** Pure rendering also supports clearly marked previews of existing recorded results. */
    public static String render(TestResult snapshot, String originalFailureMessage) {
        TestResult test = snapshot == null ? new TestResult() : snapshot;
        List<RecordedStep> steps = new ArrayList<>();
        flatten(test.getSteps(), List.of(), steps);
        RecordedStep failed = null;
        for (RecordedStep step : steps) if (isFailure(step.status())) failed = step;
        List<String> context = failed == null ? List.of() : failed.path();
        Values values = null;
        // Prefer the enclosing named business comparison over a nested compareTo(…) result.
        if (!context.isEmpty()) {
            for (int i = context.size() - 2; i >= 0 && values == null; i--) values = values(context.get(i));
            if (values == null) values = values(context.getLast());
        }
        if (values == null) {
            var matcher = MESSAGE_VALUES.matcher(text(originalFailureMessage));
            if (matcher.find()) values = new Values(matcher.group(2), matcher.group(1), "رسالة الخطأ المسجلة");
        }
        if (values == null) values = customerBalanceValues(originalFailureMessage);
        String expected = values == null ? "لم يسجل الاختبار قيمة متوقعة يمكن عرضها بوضوح." : values.expected();
        String actual = values == null ? "لم يسجل الاختبار قيمة فعلية قابلة للمقارنة." : values.actual();
        String explanation = values == null
                ? "لا يكفي نص الخطأ وحده لاستنتاج قيمة أو قاعدة عمل؛ راجع الخطوة والرسالة المسجلتين."
                : "القيم من: " + values.source() + ". هذه قراءة للبيانات المسجلة وليست نتيجة تنفيذ جديد.";
        String error = firstLine(originalFailureMessage);
        StringBuilder html = new StringBuilder("""
                <!doctype html><html lang="ar" dir="rtl"><head><meta charset="UTF-8">
                <meta name="viewport" content="width=device-width,initial-scale=1">
                <title>شرح الفشل المسجل</title><style>
                body{font-family:Segoe UI,Tahoma,Arial,sans-serif;line-height:1.8;background:#f3f5f8;color:#172b4d;margin:0;padding:24px}
                main{max-width:1000px;margin:auto;background:white;padding:24px;border-radius:12px}
                h1{font-size:24px}h2{font-size:19px;margin-top:24px}table{border-collapse:collapse;width:100%;table-layout:fixed}
                th,td{border:1px solid #dce2ea;text-align:right;vertical-align:top;padding:12px;overflow-wrap:anywhere}
                th{background:#edf2f8}li{margin:8px 0}bdi{unicode-bidi:isolate}p{overflow-wrap:anywhere}
                .note{color:#465b75}.failed{color:#a62626}.broken{color:#995500}
                </style></head><body><main><h1>شرح الفشل المسجل</h1>
                """);
        html.append("<p><strong>الشاشة أو المجموعة: </strong>").append(value(screen(test, steps))).append("</p>");
        html.append("<p><strong>الحالة: </strong>").append(value(nonblank(test.getName(), "حالة بدون عنوان مسجل"))).append("</p>");
        html.append("<h2>موضع التوقف</h2><p>")
                .append(context.isEmpty() ? "لا توجد خطوة فاشلة مسجلة داخل هذه الحالة."
                        : value(String.join(" ← ", context)))
                .append("</p><h2>المتوقع وما حدث</h2><table><thead><tr><th>المتوقع</th><th>الفعلي</th></tr></thead><tbody><tr><td>")
                .append(value(expected)).append("</td><td>").append(value(actual)).append("</td></tr></tbody></table>")
                .append("<p class=\"note\">").append(value(explanation)).append("</p>");
        html.append("<h2>رسالة التوقف المسجلة</h2><p>")
                .append(value(error.isBlank() ? "لم يسجل الاختبار رسالة خطأ." : error)).append("</p>");
        html.append("<h2>الخطوات المسجلة</h2><p class=\"note\">هذه خطوات سجلها التقرير بالفعل؛ لا تشمل إجراءات تجهيز لم يسجلها الاختبار.</p>");
        if (steps.isEmpty()) html.append("<p>لا توجد خطوات مسجلة.</p>");
        else {
            html.append("<ol>");
            for (int i = 0; i < Math.min(steps.size(), MAX_STEPS); i++) {
                RecordedStep step = steps.get(i);
                html.append("<li>").append(value(String.join(" ← ", step.path())))
                        .append(" — ").append(value(status(step.status()))).append("</li>");
            }
            html.append("</ol>");
            if (steps.size() > MAX_STEPS) html.append("<p class=\"note\">عُرضت أول ")
                    .append(MAX_STEPS).append(" خطوة من ").append(steps.size())
                    .append(". موضع التوقف أعلاه يعرض آخر خطوة فاشلة؛ بقية الخطوات محفوظة في التقرير الأصلي.</p>");
        }
        return html.append("<p class=\"note\">حالة الاختبار والخطأ الأصلي وتفاصيله الفنية تبقى كما سجلها Allure. هذا الشرح لا يحدد وحده هل السبب في البرنامج أو الاختبار.</p></main></body></html>").toString();
    }

    private record RecordedStep(List<String> path, Status status) {}
    private record Values(String expected, String actual, String source) {}
    private static Values customerBalanceValues(String message) {
        var match = CUSTOMER_BALANCE.matcher(text(message));
        if (!match.find()) return null;
        try {
            if (!(new JSONParser().parse(match.group(2)) instanceof Map<?, ?> row)) return null;
            BigDecimal actual = amount(row, "currentDebit").add(amount(row, "prevDebit"))
                    .subtract(amount(row, "currentCredit")).subtract(amount(row, "prevCredit"));
            return new Values(match.group(1), actual.stripTrailingZeros().toPlainString(),
                    "رسالة التحقق من رصيد العميل والحقول الأربعة المحفوظة لحسابه");
        } catch (ParseException | IllegalArgumentException failure) {
            return null;
        }
    }
    private static BigDecimal amount(Map<?, ?> row, String key) {
        Object raw = row.get(key);
        if (!(raw instanceof Number || raw instanceof String))
            throw new IllegalArgumentException("Missing numeric balance field");
        return new BigDecimal(raw.toString());
    }
    private static Values values(String name) {
        var match = ARABIC_VALUES.matcher(name);
        if (!match.find()) return null;
        String actual = match.group(2).replaceFirst("[،,]\\s*السماحية.*$", "").trim();
        if (match.group(1).isBlank() || actual.isBlank()) return null;
        return new Values(match.group(1).trim(), actual, name);
    }
    private static void flatten(List<StepResult> source, List<String> parent, List<RecordedStep> target) {
        if (source == null) return;
        for (StepResult step : source) {
            if (step == null) continue;
            List<String> path = new ArrayList<>(parent);
            path.add(nonblank(step.getName(), "خطوة بدون عنوان"));
            target.add(new RecordedStep(List.copyOf(path), step.getStatus()));
            flatten(step.getSteps(), path, target);
        }
    }
    private static String screen(TestResult test, List<RecordedStep> steps) {
        for (String key : List.of("screen", "feature", "suite", "subSuite", "epic")) {
            if (test.getLabels() != null) for (Label label : test.getLabels())
                if (label != null && key.equals(label.getName()) && !text(label.getValue()).isBlank()) return label.getValue();
        }
        String found = "";
        for (RecordedStep step : steps) for (String name : step.path())
            if (name.startsWith("فتح شاشة ")) found = name.substring("فتح شاشة ".length());
        return nonblank(found, "اسم الشاشة غير مسجل");
    }
    private static String status(Status value) {
        if (value == null) return "النتيجة غير مسجلة";
        return switch (value) {
            case PASSED -> "نجحت"; case FAILED -> "فشل التحقق"; case BROKEN -> "تعذر إكمال الخطوة";
            case SKIPPED -> "لم تُنفذ"; default -> "النتيجة غير محددة";
        };
    }
    private static boolean isFailure(Status status) { return status == Status.FAILED || status == Status.BROKEN; }
    private static String firstLine(String message) {
        String line = text(message).lines().findFirst().orElse("").trim();
        return line.length() > 500 ? line.substring(0, 500) + "…" : line;
    }
    private static String value(String text) { return "<bdi dir=\"auto\">" + escape(text) + "</bdi>"; }
    private static String escape(String text) {
        return text(text).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
    private static String text(String value) { return value == null ? "" : value; }
    private static String nonblank(String value, String fallback) { return text(value).isBlank() ? fallback : value; }
    private static TestResult snapshot(TestResult source) {
        TestResult copy = new TestResult().setName(source.getName()).setStatus(source.getStatus());
        if (source.getStatusDetails() != null)
            copy.setStatusDetails(new StatusDetails().setMessage(source.getStatusDetails().getMessage()));
        if (source.getLabels() != null) for (Label label : source.getLabels())
            copy.getLabels().add(new Label().setName(label.getName()).setValue(label.getValue()));
        copy.setSteps(copySteps(source.getSteps()));
        return copy;
    }
    private static List<StepResult> copySteps(List<StepResult> source) {
        List<StepResult> result = new ArrayList<>();
        if (source != null) for (StepResult step : source) if (step != null)
            result.add(new StepResult().setName(step.getName()).setStatus(step.getStatus()).setSteps(copySteps(step.getSteps())));
        return result;
    }
}
