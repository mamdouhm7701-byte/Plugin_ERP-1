/* شرح الملف: البحث بالاسم الكامل عن سجلات التجربة واكتشاف ارتباط السجلات ببعضها قبل حذفها.
 * فائدته: يمنع اختلاف قواعد البحث بين تنظيف المبيعات والمشتريات؛ لا يمنح إذن الحذف ولا يثبت الملكية وحده.
 */
package TestSupport;

import java.util.*;

public final class FixtureRecordSearch {
    private FixtureRecordSearch() {}

    public static boolean references(Object value, Object id) {
        if (value instanceof Map<?, ?> map)
            return map.values().stream().anyMatch(child -> references(child, id));
        if (value instanceof Iterable<?> list) {
            for (Object child : list) if (references(child, id)) return true;
            return false;
        }
        return id.equals(value);
    }

    @SuppressWarnings("unchecked")
    public static void findOwned(Object node, String name, List<Map<String, Object>> matches) {
        if (node instanceof Map<?, ?> row) {
            if ((name.equals(row.get("arabicName"))
                            || name.equals(row.get("englishName"))
                            || name.equals(row.get("notes")))
                    && row.get("id") != null) matches.add((Map<String, Object>) row);
            else for (Object value : row.values()) findOwned(value, name, matches);
        } else if (node instanceof List<?> rows)
            for (Object row : rows) findOwned(row, name, matches);
    }
}
