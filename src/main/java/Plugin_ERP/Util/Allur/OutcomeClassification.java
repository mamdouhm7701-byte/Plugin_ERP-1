/* شرح الملف: تحديد هل النتيجة عيب تطبيق أو اختبار أو مشكلة بيئة أو حالة لم تنفذ.
 * فائدته: يوضح أساس التصنيف ويترك الفشل غير المثبت للمراجعة دون تغيير نتيجة الاختبار.
 */
package Plugin_ERP.Util.Allur;

import java.util.Locale;

public final class OutcomeClassification {
    public enum Kind {
        APPLICATION_DEFECT("عيب تطبيق"),
        TEST_DEFECT("عيب اختبار"),
        ENVIRONMENT("مشكلة بيئة"),
        NOT_EXECUTED("حالة لم تُنفذ"),
        NEEDS_REVIEW("يحتاج مراجعة"),
        PASSED("ناجح");
        private final String label;

        Kind(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    public record Decision(Kind kind, String reason) {}

    private OutcomeClassification() {}

    public static Decision classify(
            String status, String message, String trace, String confirmedKind) {
        if ("skipped".equals(status))
            return new Decision(
                    Kind.NOT_EXECUTED,
                    Plugin_ERP.Util.CapacityFailure.matches(message)
                            ? "لم تنفذ الحالة لأن عائق سعة الترخيص أوقف بدء حالات جديدة؛ التنظيف مسموح."
                            : "لم تنفذ الحالة؛ راجع سبب التخطي والتجهيز ولا تعتبرها عيب تطبيق.");
        if ("passed".equals(status))
            return new Decision(
                    Kind.PASSED,
                    "نجحت الحالة في هذا التشغيل؛ إغلاق العيب السابق يحتاج مراجعة مستقلة.");
        String text =
                ((message == null ? "" : message) + "\n" + (trace == null ? "" : trace))
                        .toLowerCase(Locale.ROOT);
        if (text.contains("deadlocked") && text.contains("victim"))
            return new Decision(
                    Kind.ENVIRONMENT,
                    "قاعدة البيانات ألغت عملية بسبب تعارض أقفال؛ هذا عائق تنفيذ ولا يثبت فشل قاعدة العمل المختبرة.");
        if (Plugin_ERP.Util.CapacityFailure.matches(text))
            return new Decision(Kind.ENVIRONMENT, Plugin_ERP.Util.CapacityFailure.explanation());
        if (text.contains("transient failure")
                && text.contains("enableretryonfailure")
                && text.contains("usesqlserver"))
            return new Decision(
                    Kind.ENVIRONMENT,
                    "السيرفر أبلغ عن عطل SQL مؤقت أثناء التنفيذ أو التنظيف. احتفظ بسجل الاسترجاع؛ التصنيف لا يعني نجاح الحذف ولا يعيد العملية تلقائيًا.");
        if (contains(
                text,
                "network error",
                "connection refused",
                "connection reset",
                "unknownhostexception",
                "connectexception",
                "sockettimeoutexception",
                "sessionnotcreatedexception",
                "only supports chrome version",
                "browser_session_unavailable",
                "timed out receiving message from renderer",
                "tab crashed",
                "chrome not reachable",
                "invalid session id"))
            return new Decision(
                    Kind.ENVIRONMENT,
                    "دليل الخطأ يشير إلى اتصال أو توافق تشغيل المتصفح؛ لا يثبت خللًا في حسابات التطبيق.");
        String stack = trace == null ? "" : trace.stripLeading().toLowerCase(Locale.ROOT);
        if (text.contains("scenario_not_applicable:"))
            return new Decision(
                    Kind.TEST_DEFECT,
                    "خطوات الاختبار تطلب تغيير قيمة ثابتة في الشاشة؛ راجع سيناريو النشاط. لم تتخط الحالة ولم يتغير الحقل بالقوة.");
        boolean testNullFailure =
                stack.startsWith("java.lang.nullpointerexception")
                        && contains(
                                stack,
                                "at plugin_erp.",
                                "at suitesupport.",
                                "at invoice.",
                                "at purches.",
                                "at storetest.",
                                "at specialsalestest.",
                                "at testsupport.");
        if (contains(
                        text,
                        "invalidselectorexception",
                        "classcastexception",
                        "erpvue is not defined",
                        "noclassdeffounderror",
                        "classnotfoundexception",
                        "nosuchmethoderror")
                || testNullFailure)
            return new Decision(
                    Kind.TEST_DEFECT,
                    "التنفيذ توقف بخطأ في محدد العنصر أو تحميل الكود أو التعامل مع قيمة غير موجودة؛ راجع كود الاختبار.");
        if ("APPLICATION_DEFECT".equals(confirmedKind))
            return new Decision(
                    Kind.APPLICATION_DEFECT,
                    "الفشل طابق طريقة ورسالة عيب تطبيق مؤكد في سجل المتابعة.");
        if ("TEST_DEFECT".equals(confirmedKind))
            return new Decision(Kind.TEST_DEFECT, "الفشل طابق عيب اختبار مؤكد في سجل المتابعة.");
        if ("ENVIRONMENT".equals(confirmedKind))
            return new Decision(Kind.ENVIRONMENT, "الفشل طابق مشكلة بيئة مؤكدة في سجل المتابعة.");
        return new Decision(
                Kind.NEEDS_REVIEW,
                "اختلاف القيم أو انتهاء الانتظار أو تعذر الضغط وحده لا يحسم هل الخلل في التطبيق أم الاختبار أم البيئة.");
    }

    private static boolean contains(String text, String... markers) {
        for (String marker : markers) if (text.contains(marker)) return true;
        return false;
    }
}
