/* شرح الملف: إنشاء مخزن التجربة وربطه بالفرع ثم إزالة ارتباطه المملوك، مع الحفاظ على مخازن الفرع الأخرى والافتراضي.
 * فائدته: يجمع خطوات ربط المخزن وسجل الاسترجاع الخاص بها خارج تجهيز بقية بيانات القسم.
 */
package StoreTest;

import static StoreTest.StoreTestData.fields;

import StoreTest.StoreTestData.Record;
import java.util.*;
import org.openqa.selenium.WebDriver;

final class StoreBranchStores {
    private static final String BRANCH_STORE_LINK = "StoreBranchStoreMembership";
    private final StoreTestData data;
    private final WebDriver driver;
    private final List<Record> created;
    private final Map<String, Record> fixtures;
    private final Runnable persist;

    StoreBranchStores(
            StoreTestData data,
            WebDriver driver,
            List<Record> created,
            Map<String, Record> fixtures,
            Runnable persist) {
        this.data = data;
        this.driver = driver;
        this.created = created;
        this.fixtures = fixtures;
        this.persist = persist;
    }

    Record store(int index) {
        synchronized (TestSupport.BranchMutationLock.monitor()) {
            Record store =
                    data.fixture(
                            "store-" + index,
                            "Stores",
                            fields("accountId", data.account("store-" + index).id()));
            String key = "branch-store-" + index;
            if (!fixtures.containsKey(key)) {
                Object branchId =
                        Plugin_ERP.Util.VueComponentAccess.execute(
                                driver,
                                "return erpVue.root().$store.getters['app/currentBranch'].id");
                if (!(branchId instanceof String branch) || branch.isBlank())
                    throw new AssertionError("Current branch identity is unavailable");
                Record link =
                        new Record(
                                BRANCH_STORE_LINK,
                                store.id(),
                                store.name(),
                                fields("branchId", branchId, "storeId", store.id()));
                // Journal before updating: a lost response must still leave a recoverable owned
                // link.
                created.add(link);
                fixtures.put(key, link);
                persist.run();
                changeBranchStore(link, true);
            }
            return store;
        }
    }

    @SuppressWarnings("unchecked")
    private TestSupport.OwnedBranchStoreMembership branchMembership() {
        return new TestSupport.OwnedBranchStoreMembership(
                id -> {
                    Object entity = data.call("get", "Branches", fields("id", id)).get("entity");
                    return entity instanceof Map<?, ?> ? (Map<String, Object>) entity : null;
                },
                update -> data.call("update", "Branches", update),
                branch ->
                        Plugin_ERP.Util.VueComponentAccess.execute(
                                driver,
                                "const root=erpVue.root();if(root.$store.getters['app/currentBranch']?.id===arguments[0].id)root.$store.commit('app/setCurrentBranch',arguments[0])",
                                branch));
    }

    Map<String, Object> branchFor(Record link) {
        return branchMembership().read(link.values().get("branchId"));
    }

    boolean containsStore(Map<String, Object> branch, Object storeId) {
        return branchMembership().contains(branch, storeId);
    }

    void changeBranchStore(Record link, boolean add) {
        branchMembership().change(link.values().get("branchId"), link.values().get("storeId"), add);
    }
}
