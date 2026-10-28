/* شرح الملف: بناء قيم سجل التجربة من أسماء الحقول وقيمها مع الحفاظ على ترتيبها. */
package TestSupport;

import java.util.*;

public final class FixtureFields {
    private FixtureFields() {}

    public static Map<String, Object> of(Object... pairs) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) values.put((String) pairs[i], pairs[i + 1]);
        return values;
    }
}
