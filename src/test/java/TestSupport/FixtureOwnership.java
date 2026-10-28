/* شرح الملف: مراجعة علامة التجربة، أو المخزن والصنف والوحدة المملوكة، قبل السماح بتنظيف سجل بيع أو شراء أو مخزن.
 * فائدته: يوحد قواعد إثبات الملكية؛ فحص الفرع والتننت وترتيب الحذف يظلان عند مالك البيانات.
 */
package TestSupport;

import java.util.*;

public final class FixtureOwnership {
    private FixtureOwnership() {}

    public static boolean owns(
            String prefix,
            java.util.Set<String> movements,
            OwnedFixtureRecord record,
            Map<String, Object> stored,
            java.util.Collection<? extends OwnedFixtureRecord> created) {
        if (!record.name().startsWith(prefix)) return false;
        if (record.name().equals(stored.get("arabicName"))
                || record.name().equals(stored.get("englishName"))
                || record.name().equals(stored.get("notes"))) return true;
        if (!movements.contains(record.endpoint())
                || !java.util.Objects.equals(record.values().get("storeId"), stored.get("storeId")))
            return false;
        if (created.stream()
                .noneMatch(
                        r ->
                                r.endpoint().equals("Stores")
                                        && r.id().equals(stored.get("storeId"))
                                        && r.name().startsWith(prefix))) return false;
        if (!(stored.get("lines") instanceof List<?> lines)
                || lines.isEmpty()
                || !(record.values().get("lines") instanceof List<?> expected)) return false;
        for (Object value : lines) {
            if (!(value instanceof Map<?, ?> line)) return false;
            if (expected.stream()
                    .noneMatch(
                            e ->
                                    e instanceof Map<?, ?> row
                                            && java.util.Objects.equals(
                                                    row.get("itemId"), line.get("itemId"))
                                            && java.util.Objects.equals(
                                                    row.get("unitId"), line.get("unitId"))))
                return false;
            if (created.stream()
                    .noneMatch(
                            r ->
                                    r.endpoint().equals("Items")
                                            && r.id().equals(line.get("itemId"))
                                            && r.name().startsWith(prefix))) return false;
        }
        return true;
    }
}
