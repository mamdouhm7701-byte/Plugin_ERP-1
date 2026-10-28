/* شرح الملف: اختبارات تنفيذ إعداد فرع التجربة ثم مراجعة القيم المحفوظة.
 */
package Plugin_ERP.BranchProfiles;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class BranchConfigurationWorkflowTest {
    private BranchSnapshot snapshot(String policy) {
        return BranchSnapshot.from(
                Map.of(
                        "origin",
                        "https://erp.example",
                        "branchId",
                        "owned",
                        "activityType",
                        "Public",
                        "discountLevel",
                        "Items",
                        "taxPolicy",
                        policy,
                        "stocktakingMethod",
                        "Periodic"));
    }

    private final class Settings implements BranchConfigurationWorkflow.Settings {
        BranchSnapshot session = snapshot("NetBased");
        Map<String, Object> form = new HashMap<>(session.fields());
        int saves, runs;
        boolean alterUnrelated, rejectSave;
        boolean locked;
        int selections;

        public void requireEditable(Set<String> fields) {
            if (locked)
                throw new IllegalStateException("Branch policies are locked after transactions");
        }

        Settings() {
            form.remove("origin");
            form.remove("branchId");
            form.put("id", "owned");
            form.put("storeId", "preserved-store");
        }

        public BranchSnapshot current() {
            return session;
        }

        public void openForm() {}

        public Map<String, Object> formValues() {
            return new HashMap<>(form);
        }

        public void select(String field, String code) {
            selections++;
            form.put(field, code);
            if (alterUnrelated) form.put("storeId", "changed");
        }

        public void save() {
            saves++;
            if (!rejectSave) session = BranchConfigurationPlan.fromForm(session.origin(), form);
        }

        public void reloadSession() {}
    }

    private BranchConfigurationPlan plan(String policy) {
        return new BranchConfigurationPlan(
                snapshot("NetBased"), new BranchProfile("requested", snapshot(policy)));
    }

    @Test
    public void verifiesPersistenceBeforeStartingRun() {
        var ui = new Settings();
        BranchConfigurationWorkflow.prepareAndRun(plan("PriceIncluded"), ui, () -> ui.runs++);
        Assert.assertEquals(ui.saves, 1);
        Assert.assertEquals(ui.runs, 1);
        Assert.assertEquals(ui.form.get("storeId"), "preserved-store");
    }

    @Test
    public void noChangeNeverSaves() {
        var ui = new Settings();
        BranchConfigurationWorkflow.prepareAndRun(plan("NetBased"), ui, () -> ui.runs++);
        Assert.assertEquals(ui.saves, 0);
        Assert.assertEquals(ui.runs, 1);
    }

    @Test
    public void unrelatedDraftChangeStopsBeforeSave() {
        var ui = new Settings();
        ui.alterUnrelated = true;
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        BranchConfigurationWorkflow.prepareAndRun(
                                plan("ItemBased"), ui, () -> ui.runs++));
        Assert.assertEquals(ui.saves, 0);
        Assert.assertEquals(ui.runs, 0);
    }

    @Test
    public void rejectedSaveDoesNotStartRun() {
        var ui = new Settings();
        ui.rejectSave = true;
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        BranchConfigurationWorkflow.prepareAndRun(
                                plan("ItemBased"), ui, () -> ui.runs++));
        Assert.assertEquals(ui.runs, 0);
    }

    @Test
    public void externalPolicyChangeStopsBeforeEditing() {
        var ui = new Settings();
        ui.session = snapshot("PriceIncluded");
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        BranchConfigurationWorkflow.prepareAndRun(
                                plan("ItemBased"), ui, () -> ui.runs++));
        Assert.assertEquals(ui.saves, 0);
        Assert.assertEquals(ui.runs, 0);
        Assert.assertEquals(ui.form.get("taxPolicy"), "NetBased");
    }

    @Test
    public void anotherBranchCannotBeSelectedBySettingsChange() {
        var other = new HashMap<String, Object>(snapshot("NetBased").fields());
        other.put("branchId", "other");
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        new BranchConfigurationPlan(
                                snapshot("NetBased"),
                                new BranchProfile("other", BranchSnapshot.from(other))));
    }

    @Test
    public void nestedOriginalSnapshotCannotBeChangedByEditingTheForm() {
        var nested = new HashMap<String, Object>();
        nested.put("account", "original");
        var form = new HashMap<String, Object>();
        form.put("options", nested);
        var before = BranchConfigurationPlan.snapshotForm(form);
        nested.put("account", "changed");
        Assert.expectThrows(
                IllegalStateException.class,
                () -> BranchConfigurationPlan.verifyUnrelatedFields(before, form));
    }

    @Test
    public void transactionLockedBranchStopsBeforeAnyDraftEditOrRun() {
        var ui = new Settings();
        ui.locked = true;
        Assert.expectThrows(
                IllegalStateException.class,
                () ->
                        BranchConfigurationWorkflow.prepareAndRun(
                                plan("ItemBased"), ui, () -> ui.runs++));
        Assert.assertEquals(ui.selections, 0);
        Assert.assertEquals(ui.saves, 0);
        Assert.assertEquals(ui.runs, 0);
    }
}
