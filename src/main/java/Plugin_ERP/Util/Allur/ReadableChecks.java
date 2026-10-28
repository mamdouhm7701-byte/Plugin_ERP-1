/* شرح الملف: عرض أسماء الحقول والمتوقع والفعلي بكلام مفهوم.
 */
package Plugin_ERP.Util.Allur;

import java.util.*;

public final class ReadableChecks {
    private ReadableChecks() {}

    private static final Map<String, String> FIELDS =
            Map.ofEntries(
                    Map.entry("taxNumber", "الرقم الضريبي"),
                    Map.entry("taxPercentage", "نسبة الضريبة"),
                    Map.entry("totalTaxes", "قيمة الضريبة"),
                    Map.entry("totalAdditionalTaxes", "قيمة الضريبة الإضافية"),
                    Map.entry("netBeforeTaxes", "القيمة قبل الضريبة"),
                    Map.entry("net", "صافي الفاتورة"),
                    Map.entry("itemArabicName", "اسم الصنف"),
                    Map.entry("itemEnglishName", "اسم الصنف بالإنجليزي"),
                    Map.entry("arabicName", "الاسم العربي"),
                    Map.entry("englishName", "الاسم الإنجليزي"),
                    Map.entry("quantity", "الكمية"),
                    Map.entry("price", "السعر"),
                    Map.entry("salesPrice", "سعر البيع"),
                    Map.entry("currentBalance", "رصيد المخزون"),
                    Map.entry("discountValue", "قيمة الخصم"),
                    Map.entry("discountPercentage", "نسبة الخصم"),
                    Map.entry("code", "الكود"),
                    Map.entry("barcode", "الباركود"),
                    Map.entry("notes", "الملاحظات"),
                    Map.entry("accountNumber", "رقم الحساب"),
                    Map.entry("email", "البريد الإلكتروني"),
                    Map.entry("isTaxable", "تطبيق الضريبة"),
                    Map.entry("isOnHold", "تعليق الطلب"),
                    Map.entry("serial", "رقم الشاسيه"),
                    Map.entry("carColorId", "لون السيارة"),
                    Map.entry("isActive", "تفعيل السجل"));

    public static boolean businessField(String name) {
        return name != null && FIELDS.containsKey(name);
    }

    public static String field(String name) {
        return name == null || name.isBlank() ? "القيمة" : FIELDS.getOrDefault(name, name);
    }

    public static String comparison(String name, Object expected, Object actual) {
        String title =
                name == null || name.isBlank()
                        ? "التحقق من القيمة"
                        : businessField(name) ? "التحقق من " + field(name) : name;
        return title + ": المتوقع " + display(expected) + "، الفعلي " + display(actual);
    }

    public static String display(Object value) {
        if (value == null) return "فارغ";
        if (value instanceof Boolean b) return b ? "نعم" : "لا";
        String text =
                value.getClass().isArray()
                        ? Arrays.deepToString(new Object[] {value})
                        : String.valueOf(value);
        return text.length() > 300 ? text.substring(0, 300) + "…" : text;
    }
}
