/* شرح الملف: إضافة وإزالة مخزن التجربة من الفرع دون حذف روابط المخازن الأخرى أو تغيير الافتراضي.
 * فائدته: يجمع حماية ربط المخازن المشتركة بين المبيعات والمشتريات في مكان واحد.
 */
package TestSupport;

import java.util.*;
import java.util.function.*;

public final class OwnedBranchStoreMembership {
    private final Function<Object, Map<String, Object>> load;
    private final Consumer<Map<String, Object>> save;
    private final Consumer<Map<String, Object>> refreshSession;

    public OwnedBranchStoreMembership(
            Function<Object, Map<String, Object>> load,
            Consumer<Map<String, Object>> save,
            Consumer<Map<String, Object>> refreshSession) {
        this.load = load;
        this.save = save;
        this.refreshSession = refreshSession;
    }

    public Map<String, Object> read(Object branchId) {
        Map<String, Object> branch = load.apply(branchId);
        if (branch == null
                || !Objects.equals(branchId, branch.get("id"))
                || !(branch.get("stores") instanceof List<?>))
            throw new AssertionError(
                    "Cannot verify branch stores before changing owned membership");
        return branch;
    }

    public boolean contains(Map<String, Object> branch, Object storeId) {
        return ((List<?>) branch.get("stores"))
                .stream()
                        .anyMatch(
                                value ->
                                        value instanceof Map<?, ?> row
                                                && storeId.equals(row.get("storeId")));
    }

    public void change(Object branchId, Object storeId, boolean add) {
        synchronized (BranchMutationLock.monitor()) {
            Map<String, Object> branch = read(branchId);
            if (contains(branch, storeId) != add) {
                List<Object> stores = new ArrayList<>((List<?>) branch.get("stores"));
                if (add)
                    stores.add(
                            new LinkedHashMap<>(Map.of("branchId", branchId, "storeId", storeId)));
                else
                    stores.removeIf(
                            value ->
                                    value instanceof Map<?, ?> row
                                            && storeId.equals(row.get("storeId")));
                var update = new LinkedHashMap<>(branch);
                update.put("stores", stores);
                save.accept(update);
                Map<String, Object> refreshed = read(branchId);
                if (contains(refreshed, storeId) != add)
                    throw new AssertionError("Owned store membership was not updated");
                for (Object value : (List<?>) branch.get("stores")) {
                    Object previousId = ((Map<?, ?>) value).get("storeId");
                    if (!previousId.equals(storeId) && !contains(refreshed, previousId))
                        throw new AssertionError("Existing branch store was removed");
                }
                if (!Objects.equals(branch.get("defaultStoreId"), refreshed.get("defaultStoreId")))
                    throw new AssertionError("Branch default store changed");
                branch = refreshed;
            }
            refreshSession.accept(branch);
        }
    }
}
