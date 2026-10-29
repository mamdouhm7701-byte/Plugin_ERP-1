/* شرح الملف: يفصل نتائج سياسات الفرع في التقرير، ويعرض وصف مراجعة الفاتورة ونوع المشكلة دون تغيير نتيجة التنفيذ. */
package Plugin_ERP.Util.Allur;

import Plugin_ERP.Util.Allur.ReviewJson.*;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class ReportResultMetadata implements TestLifecycleListener {
    @Override
    public void beforeTestWrite(TestResult test) {
        String scope = label(test, "review.scope");
        if (!scope.isBlank() && !scope.startsWith("legacy:") && !scope.startsWith("unverified:")) {
            String original = label(test, "report.originalHistoryId");
            if (original.isBlank()) {
                original = Objects.toString(test.getHistoryId(), test.getFullName());
                setLabel(test, "report.originalHistoryId", original);
            }
            test.setHistoryId(scopedIdentity(original, scope));
        }
        invoice(test, scope);
        String message =
                test.getStatusDetails() == null
                        ? ""
                        : Objects.toString(test.getStatusDetails().getMessage(), "");
        JSONObject issue = knownIssue(test.getFullName(), message);
        String kind = issue == null ? "" : issue.optString("kind");
        if (issue != null
                && test.getStatus() != Status.PASSED
                && test.getStatus() != Status.SKIPPED) {
            String marker = "KNOWN_BUG:" + issue.optString("id");
            if (!message.contains(marker))
                test.getStatusDetails().setMessage(marker + " — " + message);
            setLabel(test, "مرجع المشكلة", issue.optString("id"));
        }
        String trace = test.getStatusDetails() == null ? "" : test.getStatusDetails().getTrace();
        var decision =
                OutcomeClassification.classify(
                        test.getStatus() == null ? "" : test.getStatus().value(),
                        message,
                        trace,
                        kind);
        setLabel(test, "تصنيف النتيجة", decision.kind().label());
        setLabel(test, "سبب التصنيف", decision.reason());
        if (test.getStatus() == Status.FAILED || test.getStatus() == Status.BROKEN) {
            String marker = "OUTCOME:" + decision.kind().name();
            if (test.getStatusDetails() != null
                    && !Objects.toString(test.getStatusDetails().getMessage(), "").contains(marker))
                test.getStatusDetails()
                        .setMessage(test.getStatusDetails().getMessage() + "\n" + marker);
            setLabel(test, "مصدر التوقف", decision.kind().label());
            setLabel(test, "حالة المشكلة", issue == null ? decision.reason() : "مشكلة معروفة");
        }
    }

    public static String scopedIdentity(String original, String scope) {
        return UUID.nameUUIDFromBytes((original + "\n" + scope).getBytes(StandardCharsets.UTF_8))
                .toString();
    }

    static JSONObject knownIssue(String method, String message) {
        try (var input = ReportResultMetadata.class.getResourceAsStream("/issue-register.json")) {
            if (input == null) return null;
            var issues = new JSONArray(new String(input.readAllBytes(), StandardCharsets.UTF_8));
            for (int i = 0; i < issues.length(); i++) {
                var issue = issues.getJSONObject(i);
                if (!issue.optBoolean("confirmed")
                        || !Objects.equals(method, issue.optString("method"))) continue;
                var signatures = issue.optJSONArray("failureContains");
                if (signatures != null)
                    for (int n = 0; n < signatures.length(); n++) {
                        String signature = signatures.getString(n);
                        if (!signature.isBlank() && message.contains(signature)) return issue;
                    }
            }
        } catch (Exception failure) {
            Plugin_ERP.Bots.LogUtils.warn(
                    "Issue classification unavailable; original result retained.");
        }
        return null;
    }

    private static void invoice(TestResult test, String scope) {
        String type = label(test, "testClass");
        String screen =
                type.endsWith("RestaurantInvoiceTaxReviewTest")
                        ? "فاتورة المطعم"
                        : type.endsWith("PosInvoiceTaxReviewTest")
                                ? "فاتورة نقاط البيع"
                                : type.endsWith("SalesInvoiceTaxReviewTest")
                                        ? "فاتورة المبيعات"
                                        : type.endsWith("PurchaseInvoiceTaxReviewTest")
                                                ? "فاتورة المشتريات"
                                                : "";
        if (screen.isBlank()) return;
        String policy =
                scope.contains("taxPolicy=NetBased")
                        ? "ضريبة على الصافي"
                        : scope.contains("taxPolicy=ItemBased")
                                ? "ضريبة على الأصناف"
                                : scope.contains("taxPolicy=PriceIncluded")
                                        ? "السعر شامل الضريبة"
                                        : "سياسة الفرع";
        String action = parameter(test, "arg0");
        boolean exempt =
                scope.contains("taxPolicy=ItemBased") && parameter(test, "arg1").equals("true");
        boolean low = parameter(test, "arg2").equals("true");
        test.setName(
                screen
                        + " — "
                        + action
                        + " — "
                        + policy
                        + (exempt ? " — صنف معفى" : " — صنف خاضع")
                        + (low ? " — سعر منخفض" : " — سعر عادي"));
        setLabel(test, "سياسة الضريبة", policy);
        setLabel(test, "feature", screen);
        if (test.getDescription() == null || !test.getDescription().contains("الصنف المستخدم:")) {
            boolean additional =
                    screen.equals("فاتورة المطعم") && scope.contains("additional=true");
            String base = low ? "10" : "100";
            String vat = exempt ? "0" : additional ? (low ? "5.25" : "30") : low ? "1.50" : "15";
            String net =
                    exempt
                            ? (additional ? (low ? "35" : "200") : base)
                            : additional ? (low ? "40.25" : "230") : low ? "11.50" : "115";
            test.setDescription(
                    "هنفتح "
                            + screen
                            + " ونضيف صنف التجربة. سعر الوحدة قبل الضريبة "
                            + base
                            + ". "
                            + (additional ? "الإضافية للوحدة " + (low ? "25" : "100") + ". " : "")
                            + "نتحقق أن الضريبة "
                            + vat
                            + " والصافي "
                            + net
                            + " عند كمية واحدة. بعدها نراجع "
                            + action
                            + ". اسم الصنف الفعلي موجود في خطوة إضافة الصنف. لو توقف التنفيذ قبلها، العملية لم تكتمل.");
        }
        String operation =
                switch (action) {
                    case "تعديل" ->
                            screen.equals("فاتورة المبيعات")
                                    ? "نحاول تعديل الفاتورة المحفوظة ونتأكد أن البرنامج يمنع التعديل وأن أرقامها ومخزونها ثابتان."
                                    : "نفتح الفاتورة المحفوظة ونغير الكمية إلى "
                                            + (screen.equals("فاتورة المشتريات") ? "2" : "3")
                                            + "، ثم نراجع الأرقام الجديدة والمخزون.";
                    case "حذف" ->
                            screen.equals("فاتورة المبيعات")
                                    ? "نحاول حذف الفاتورة المحفوظة ونتأكد أن البرنامج يمنع الحذف وأن الفاتورة والمخزون ثابتان."
                                    : "نحذف الفاتورة المحفوظة ونتأكد أنها اختفت وأن المخزون رجع إلى رصيده قبل الفاتورة.";
                    case "طباعة" ->
                            "نفتح طباعة الفاتورة ونتأكد أن اسم الصنف ورقم الفاتورة والصافي مطابقون، وأن الطباعة لا تغير الفاتورة.";
                    default -> "نحفظ الفاتورة ثم نراجع قيمها المحفوظة وتأثيرها على المخزون.";
                };
        String description =
                test.getDescription()
                        + "\nالخطوات: 1. فتح الشاشة وإضافة صنف التجربة. 2. مقارنة قبل الضريبة والضريبة والصافي بالقيم المذكورة. 3. حفظ الفاتورة ومقارنة الأرقام مرة أخرى. 4. "
                        + operation
                        + "\nتنبيه: هذه خطوات الحالة المطلوبة؛ الفشل في خطوة مبكرة لا يعني أن الخطوات التالية اتنفذت.";
        test.setDescriptionHtml(
                "<div dir=\"rtl\"><h3>هنراجع إيه؟</h3><p>"
                        + escape(description).replace("\n", "</p><p>")
                        + "</p></div>");
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String parameter(TestResult test, String name) {
        return test.getParameters().stream()
                .filter(p -> name.equals(p.getName()))
                .map(Parameter::getValue)
                .findFirst()
                .orElse("");
    }

    private static String label(TestResult test, String name) {
        return test.getLabels().stream()
                .filter(l -> name.equals(l.getName()))
                .map(Label::getValue)
                .findFirst()
                .orElse("");
    }

    private static void setLabel(TestResult test, String name, String value) {
        test.getLabels().removeIf(l -> name.equals(l.getName()));
        test.getLabels().add(new Label().setName(name).setValue(value));
    }
}
