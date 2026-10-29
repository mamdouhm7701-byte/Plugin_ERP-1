/* شرح الملف: تحويل سبب توقف الشاشة إلى جملة مفهومة مع إبقاء الخطأ الأصلي للمراجعة.
 * فائدته: يوضح ما لم يكتمل دون افتراض أن السبب عيب في البرنامج.
 */
package Plugin_ERP.Util.Allur;

import java.util.Locale;

public final class ReadableFailure {
    private ReadableFailure() {}

    public static String describe(String message, String step) {
        String raw = message == null ? "" : message;
        String text = raw.toLowerCase(Locale.ROOT);
        String action = step == null || step.isBlank() ? "الخطوة المطلوبة" : step;
        if (text.contains("browser_session_unavailable")
                || text.contains("timed out receiving message from renderer"))
            return "المتصفح توقف عن الاستجابة؛ لم تكتمل الخطوة. احتفظ بسجلات البيانات للتنظيف ولا تعتبرها نتيجة بيزنس.";
        if (Plugin_ERP.Util.CapacityFailure.matches(raw))
            return Plugin_ERP.Util.CapacityFailure.explanation();
        if (text.contains("element click intercepted"))
            return "تعذر الضغط أثناء «" + action + "»: يوجد عنصر آخر يغطي مكان الضغط.";
        if (text.contains("stale element"))
            return "تغير محتوى الشاشة أثناء «" + action + "» قبل اكتمال التعامل معه.";
        if (text.contains("no such element"))
            return "لم يظهر العنصر المطلوب لإكمال «" + action + "».";
        if (text.contains("expected condition failed") || text.contains("timeoutexception"))
            return "انتهت مهلة الانتظار ولم يكتمل «"
                    + action
                    + "». راجع صورة الشاشة والطلب المسجل؛ انتهاء الانتظار وحده لا يحدد السبب.";
        if (text.contains("network error")
                || text.contains("connection refused")
                || text.contains("connection reset"))
            return "تعذر الاتصال بالخدمة أثناء «" + action + "».";
        String first = raw.lines().findFirst().orElse("لم يسجل سبب التوقف.");
        return first.length() > 500 ? first.substring(0, 500) + "…" : first;
    }
}
