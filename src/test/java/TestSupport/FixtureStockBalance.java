/* شرح الملف: قراءة رصيد الصنف في مخزن التجربة المحدد، والفشل إذا لم تُعد البيانات هذا المخزن بدل افتراض رصيد صفر. */
package TestSupport;

import java.util.*;
import java.util.function.Supplier;

public final class FixtureStockBalance {
    private FixtureStockBalance() {}

    public static Object read(Map<String, Object> item, Supplier<Object> storeId) {
        for (Object value : (List<?>) item.get("stores")) {
            Map<?, ?> row = (Map<?, ?>) value;
            if (storeId.get().equals(row.get("storeId"))) return row.get("currentBalance");
        }
        throw new AssertionError("Owned store balance missing");
    }
}
