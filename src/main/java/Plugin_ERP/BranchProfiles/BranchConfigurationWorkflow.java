/* شرح الملف: تنفيذ إعداد فرع التجربة ثم مراجعة القيم المحفوظة.
 */
package Plugin_ERP.BranchProfiles;

import java.util.Map;

public final class BranchConfigurationWorkflow {
    public interface Settings {
        BranchSnapshot current();

        void openForm();

        Map<String, Object> formValues();

        void requireEditable(java.util.Set<String> fields);

        void select(String field, String code);

        void save();

        void reloadSession();
    }

    private BranchConfigurationWorkflow() {}

    public static void apply(BranchConfigurationPlan plan, Settings settings) {
        var before = new BranchProfile("before-settings-change", plan.before());
        before.verify(settings.current());
        settings.openForm();
        var original = BranchConfigurationPlan.snapshotForm(settings.formValues());
        before.verify(BranchConfigurationPlan.fromForm(plan.before().origin(), original));
        if (plan.changes().isEmpty()) {
            plan.target().verify(settings.current());
            return;
        }
        settings.requireEditable(plan.changes().keySet());
        plan.changes().forEach(settings::select);
        var draft = settings.formValues();
        plan.target().verify(BranchConfigurationPlan.fromForm(plan.before().origin(), draft));
        BranchConfigurationPlan.verifyUnrelatedFields(original, draft);
        before.verify(settings.current());
        settings.save();
        settings.reloadSession();
        plan.target().verify(settings.current());
        settings.openForm();
        var persisted = settings.formValues();
        plan.target().verify(BranchConfigurationPlan.fromForm(plan.before().origin(), persisted));
        BranchConfigurationPlan.verifyUnrelatedFields(original, persisted);
    }

    public static void prepareAndRun(
            BranchConfigurationPlan plan, Settings settings, Runnable run) {
        apply(plan, settings);
        run.run();
    }
}
