/* شرح الملف: اختيار المخزن والعميل الافتراضيين لتجربة المطعم ثم استعادة القيم السابقة إذا لم يغيرها طرف آخر.
 * فائدته: يحتفظ بدليل القيم الأصلية ويرفض الكتابة فوق تغيير خارجي أثناء التنظيف.
 */
package SpecialSalesTest;

import static SpecialSalesTest.SpecialSalesTestData.fields;

import SpecialSalesTest.SpecialSalesTestData.Record;
import java.util.*;

final class SpecialSalesBranchDefaults {
    private static final String DEFAULTS = "SpecialSalesBranchDefaults";
    private final SpecialSalesTestData data;
    private final SpecialSalesFixtureApi api;
    private final TestSupport.FixtureSession session;
    private final List<Record> created;
    private final String suffix;
    private final Runnable persist;

    SpecialSalesBranchDefaults(
            SpecialSalesTestData data,
            SpecialSalesFixtureApi api,
            TestSupport.FixtureSession session,
            List<Record> created,
            String suffix,
            Runnable persist) {
        this.data = data;
        this.api = api;
        this.session = session;
        this.created = created;
        this.suffix = suffix;
        this.persist = persist;
    }

    Record configureOwnedDefaults(Record customer) {
        synchronized (TestSupport.BranchMutationLock.monitor()) {
            Object branchId = session.branchId;
            var link =
                    new Record(
                            DEFAULTS,
                            branchId,
                            "QA-SP-defaults-" + suffix,
                            fields("branchId", branchId));
            // Store creation links it to the branch. Read the branch only after that
            // change so the default update cannot erase its newly owned membership.
            var ownedStore = data.store(1);
            var branch = data.branchFor(link);
            var record =
                    new Record(
                            DEFAULTS,
                            branchId,
                            link.name(),
                            fields(
                                    "branchId",
                                    branchId,
                                    "storeId",
                                    ownedStore.id(),
                                    "customerId",
                                    customer.id(),
                                    "previousStoreId",
                                    branch.get("defaultStoreId"),
                                    "previousCustomerId",
                                    branch.get("defaultCustomerId")));
            created.add(record);
            persist.run();
            var update = new LinkedHashMap<>(branch);
            update.put("defaultStoreId", ownedStore.id());
            update.put("defaultCustomerId", customer.id());
            api.call("update", "Branches", update);
            data.syncBranch(data.branchFor(record));
            return record;
        }
    }

    void restoreDefaults(Record record) {
        synchronized (TestSupport.BranchMutationLock.monitor()) {
            var branch = data.branchFor(record);
            var update = new LinkedHashMap<>(branch);
            for (String field : List.of("defaultStoreId", "defaultCustomerId")) {
                Object
                        owned =
                                record.values()
                                        .get(
                                                field.equals("defaultStoreId")
                                                        ? "storeId"
                                                        : "customerId"),
                        previous =
                                record.values()
                                        .get(
                                                field.equals("defaultStoreId")
                                                        ? "previousStoreId"
                                                        : "previousCustomerId");
                if (java.util.Objects.equals(branch.get(field), owned)) update.put(field, previous);
                else if (!java.util.Objects.equals(branch.get(field), previous))
                    throw new AssertionError(
                            "Branch defaults changed externally; retaining owned data instead of overwriting");
            }
            if (!update.equals(branch)) api.call("update", "Branches", update);
            var refreshed = data.branchFor(record);
            if (!java.util.Objects.equals(
                            refreshed.get("defaultStoreId"), record.values().get("previousStoreId"))
                    || !java.util.Objects.equals(
                            refreshed.get("defaultCustomerId"),
                            record.values().get("previousCustomerId")))
                throw new AssertionError("Branch default restoration failed");
            data.syncBranch(refreshed);
        }
    }
}
