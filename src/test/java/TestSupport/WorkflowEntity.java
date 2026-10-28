/* شرح الملف: وصف السجل الذي أنشأته رحلة الاختبار ويحتاج إلى تنظيف.
 */
package TestSupport;

public record WorkflowEntity(Object id, String name) {
    public WorkflowEntity {
        if (id == null || name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Workflow reference must have a persisted ID and name");
        }
    }
}
