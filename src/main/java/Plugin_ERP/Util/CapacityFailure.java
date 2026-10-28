/* شرح الملف: التعرف على رفض التجهيز بسبب الحد المسموح في الترخيص من كود البرنامج الفعلي.
 * فائدته: يميز عائق السعة عن فشل حسابات الفاتورة دون الاعتماد على كلمة ترخيص عامة.
 */
package Plugin_ERP.Util;

import java.util.Locale;

public final class CapacityFailure {
    private CapacityFailure() {}

    public static boolean matches(String message) {
        String text = message == null ? "" : message.toLowerCase(Locale.ROOT);
        return text.contains("common.licenselimitationexceedederrormessage")
                || text.contains("license limitation exceeded")
                || text.contains("environment_blocked: license_capacity");
    }

    public static String explanation() {
        return "تعذر تجهيز بيانات التجربة لأن الحد المسموح في الترخيص لا يسمح بإضافة بيانات أخرى. "
                + "راجع السعة والبيانات المملوكة المتبقية؛ لم يكتمل اختبار البيزنس في هذه الحالة.";
    }
}
