/* شرح الملف: وصف سجل أنشأته التجربة: نوعه ومعرفه وعلامته والبيانات التي حفظتها؛ يستخدم لمراجعة الملكية فقط. */
package TestSupport;

import java.util.Map;

public interface OwnedFixtureRecord {
    String endpoint();

    Object id();

    String name();

    Map<String, Object> values();
}
