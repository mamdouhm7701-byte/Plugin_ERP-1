package TestSupport;

/** هوية مرجع موجود تملكه الرحلة الأصلية؛ تمريره لا ينشئ نسخة جديدة أو ينقل ملكية حذفه. */
public record WorkflowEntity(Object id, String name) {
    public WorkflowEntity {
        if (id == null || name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Workflow reference must have a persisted ID and name");
        }
    }
}
