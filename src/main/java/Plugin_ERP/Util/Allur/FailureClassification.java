/* شرح الملف: تحديد نوع المشكلة ومصدر التوقف دون اعتبار كل فشل عيبًا مؤكدًا.
 */
package Plugin_ERP.Util.Allur;

import io.qameta.allure.model.*;
import java.util.*;
import java.util.regex.Pattern;

public final class FailureClassification {
    public record Category(String domain, String origin, String confirmation, String reference) {}

    private static final Pattern KNOWN =
            Pattern.compile("KNOWN_BUG:([A-Z][A-Z0-9]*(?:-[A-Z0-9]+)*-[0-9]+)");

    private FailureClassification() {}

    public static Category classify(TestResult test, Throwable failure) {
        String message = messages(failure);
        if (message.isBlank() && test.getStatusDetails() != null)
            message = Objects.toString(test.getStatusDetails().getMessage(), "");
        String context =
                (Objects.toString(test.getName(), "")
                                + " "
                                + message
                                + " "
                                + steps(test.getSteps()))
                        .toLowerCase(Locale.ROOT);
        var known = KNOWN.matcher(message);
        String reference = known.find() ? known.group(1) : "";
        String domain = domain(context);
        if (Plugin_ERP.Util.CapacityFailure.matches(message)) domain = "سعة وتجهيز البيئة";
        if (test.getStatus() == Status.SKIPPED)
            return new Category(domain, "تجهيز أو اختيار التشغيل", "الحالة لم تُنفذ", reference);
        if (Plugin_ERP.Util.CapacityFailure.matches(message))
            return new Category("سعة وتجهيز البيئة", "حد الترخيص", "لم يكتمل اختبار البيزنس", "");
        if (!reference.isBlank())
            return new Category(domain, "مشكلة موثقة", "مشكلة معروفة", reference);
        if (contains(
                message.toLowerCase(Locale.ROOT),
                "network error",
                "connection refused",
                "connection reset",
                "unknownhost",
                "connectexception",
                "sockettimeout"))
            return new Category(domain, "شبكة أو بيئة التشغيل", "لم يثبت أنه عيب في البرنامج", "");
        if (failure instanceof NullPointerException
                || failure instanceof IllegalArgumentException
                || contains(
                        message,
                        "NoClassDefFoundError",
                        "ClassNotFoundException",
                        "NullPointerException"))
            return new Category(domain, "تنفيذ الاختبار", "لم يثبت أنه عيب في البرنامج", "");
        if (contains(
                context,
                "element click intercepted",
                "stale element",
                "no such element",
                "expected condition failed",
                "timeout"))
            return new Category(domain, "تعذر إكمال خطوة", "السبب يحتاج مراجعة", "");
        if (failure instanceof AssertionError || test.getStatus() == Status.FAILED)
            return new Category(domain, "مقارنة النتيجة", "فشل يحتاج مراجعة", "");
        return new Category(domain, "غير محسوم", "السبب يحتاج مراجعة", "");
    }

    public static void apply(TestResult test, Throwable failure) {
        var result = classify(test, failure);
        for (var entry :
                Map.of(
                                "نوع المشكلة",
                                result.domain(),
                                "مصدر التوقف",
                                result.origin(),
                                "حالة المشكلة",
                                result.confirmation())
                        .entrySet()) {
            test.getLabels().removeIf(l -> l.getName().equals(entry.getKey()));
            test.getLabels().add(new Label().setName(entry.getKey()).setValue(entry.getValue()));
        }
    }

    private static String domain(String text) {
        if (contains(text, "ضريب", "vat", "tax", "rounding", "تقريب")) return "ضرائب وحسابات";
        if (contains(text, "مخزون", "جرد", "stock", "inventory", "warehouse"))
            return "مخزون وكميات";
        if (contains(text, "صلاح", "permission", "unauthorized", "forbidden"))
            return "صلاحيات ودخول";
        if (contains(
                text,
                "قيد",
                "رصيد العميل",
                "رصيد المورد",
                "posting",
                "balance",
                "payment",
                "دفع",
                "سداد")) return "أرصدة وقيود وسداد";
        if (contains(text, "تقرير", "طباعة", "report", "print")) return "تقارير وطباعة";
        if (contains(text, "حفظ", "تعديل", "حذف", "save", "edit", "delete", "persist"))
            return "حفظ وتعديل وحذف";
        if (contains(text, "بحث", "search", "filter", "sort", "pagination"))
            return "بحث وعرض البيانات";
        if (contains(text, "تداخل", "محاذاة", "تنسيق الشاشة", "overlap", "alignment", "layout"))
            return "واجهة وتنسيق";
        return "يحتاج تحديد";
    }

    private static boolean contains(String text, String... words) {
        for (String word : words) if (text.contains(word)) return true;
        return false;
    }

    private static String messages(Throwable error) {
        var out = new StringBuilder();
        var seen = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        while (error != null && seen.add(error)) {
            out.append(error.getClass().getSimpleName())
                    .append(':')
                    .append(error.getMessage())
                    .append('\n');
            error = error.getCause();
        }
        return out.toString();
    }

    private static String steps(List<StepResult> source) {
        var out = new StringBuilder();
        if (source != null)
            for (var s : source)
                if (s != null) {
                    if (s.getStatus() == Status.FAILED || s.getStatus() == Status.BROKEN)
                        out.append(' ').append(s.getName());
                    out.append(steps(s.getSteps()));
                }
        return out.toString();
    }
}
