package StoreTest;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

/** In-memory ERP transport used by cleanup regressions. */
final class StoreFixtureApiStub {
    final Map<String, Map<String, Object>> records = new LinkedHashMap<>();
    final List<String> deletes = new ArrayList<>();
    int crashAtDelete;
    boolean timeOutStore;
    boolean commitBeforeTimeout;
    boolean rejectStore;
    boolean emptyDeletedResponse;
    boolean failAfterItemInsert;
    boolean failAfterStoreInsert;
    boolean failFixtureLookup;
    boolean defaultDeletedDto;
    boolean defaultDeletedMasterDto;

    boolean missingDelete400;
    boolean missingDeleteForExisting;
    String activeOrigin = "offline";
    String activeBranch = "branch-test";
    int missingBranchReads;
    boolean rejectBranchUnlink;
    boolean timeoutBranchUnlinkAfterCommit;
    boolean rejectMovement;
    boolean failMovementLookup;
    Map<String, Object> branch =
            new LinkedHashMap<>(
                    Map.of(
                            "id",
                            "branch-test",
                            "defaultStoreId",
                            "existing-store",
                            "phone",
                            "original",
                            "stores",
                            new ArrayList<>(List.of(Map.of("storeId", "existing-store")))));

    WebDriver driver() {
        Object timeouts =
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebDriver.Timeouts.class},
                        (proxy, method, args) -> proxy);
        Object options =
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebDriver.Options.class},
                        (proxy, method, args) -> timeouts);
        return (WebDriver)
                Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {WebDriver.class, JavascriptExecutor.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("executeScript")) {
                                String script = (String) args[0];
                                if (script.startsWith("return location.origin")) return activeOrigin;
                                if (script.startsWith("return document.querySelector")) {
                                    if (missingBranchReads > 0) { missingBranchReads--; return null; }
                                    return activeBranch;
                                }
                                return true;
                            }
                            if (method.getName().equals("manage")) return options;
                            if (method.getName().equals("executeAsyncScript"))
                                return request((Object[]) args[1]);
                            if (method.getName().equals("toString"))
                                return "Fixture cleanup regression driver";
                            return null;
                        });
    }

    @SuppressWarnings("unchecked")
    Object request(Object[] args) {
        if (missingBranchReads > 0 || !java.util.Objects.equals(args[3], activeOrigin)
                || !java.util.Objects.equals(args[4], activeBranch))
            return Map.of("error", "ERP origin or branch changed; refusing fixture access");
        String action = (String) args[0], endpoint = (String) args[1];
        Map<String, Object> values = (Map<String, Object>) args[2];
        if (endpoint.equals("Branches")) {
            if (action.equals("get")) return Map.of("entity", new LinkedHashMap<>(branch));
            if (!action.equals("update"))
                throw new AssertionError("Never delete an existing branch");
            boolean unlink =
                    ((List<?>) values.get("stores")).size()
                            < ((List<?>) branch.get("stores")).size();
            if (unlink && rejectBranchUnlink)
                return Map.of("error", "Cannot unlink owned store", "status", 400);
            branch = new LinkedHashMap<>(values);
            if (unlink && timeoutBranchUnlinkAfterCommit) {
                timeoutBranchUnlinkAfterCommit = false;
                throw new TimeoutException("Lost unlink response after commit");
            }
            return Map.of("id", "branch-test");
        }
        if (action.equals("create")) {
            String id = endpoint + "-" + (records.size() + 1);
            Map<String, Object> saved = new LinkedHashMap<>(values);
            saved.put("id", id);
            records.put(id, saved);
            if (endpoint.equals("Items") && failAfterItemInsert)
                return Map.of("error", "Failed after insert", "status", 500);
            if (endpoint.equals("Stores") && failAfterStoreInsert)
                return Map.of("error", "Lost store create response", "status", 500);
            return Map.of("id", id);
        }
        if (action.equals("get") && endpoint.startsWith("Items/Summary"))
            return Map.of("entity", new ArrayList<>(records.values()));
        if (action.equals("get") && endpoint.startsWith("InventoryTransactions?")) {
            if (failMovementLookup) return Map.of("error", "Lookup unavailable", "status", 503);
            var rows = records.values().stream().filter(r -> r.containsKey("lines")).toList();
            return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
        }
        if (action.equals("get") && endpoint.contains("?")) {
            if (failFixtureLookup)
                return Map.of("error", "Fixture lookup unavailable", "status", 503);
            var rows =
                    records.values().stream()
                            .filter(
                                    r ->
                                            r.containsKey("id")
                                                    && (r.containsKey("arabicName")
                                                            || r.containsKey("englishName")))
                            .toList();
            return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
        }
        String id = values.get("id").toString();
        if (action.equals("get")) {
            return records.containsKey(id)
                    ? Map.of("entity", records.get(id))
                    : defaultDeletedMasterDto
                            ? Map.of(
                                    "entity",
                                    Map.of("subUnits", List.of(), "type", "RawMaterial"))
                            : defaultDeletedDto
                                    ? Map.of("entity", Map.of("conversionRates", List.of()))
                                    : emptyDeletedResponse
                                            ? Map.of("entity", "")
                                            : Map.of(
                                                    "error",
                                                    "Missing owned record",
                                                    "status",
                                                    404);
        }
        deletes.add(id);
        if (crashAtDelete > 0 && deletes.size() == crashAtDelete)
            throw new Error("Simulated process interruption");
        if (endpoint.equals("ItemTransactions-ExchangeOrder") && rejectMovement)
            return Map.of("error", "Movement is linked", "status", 400);
        if (missingDeleteForExisting || (missingDelete400 && !records.containsKey(id)))
            return Map.of("error", "Common.ObjectNotFound", "status", 400);
        if (endpoint.equals("Stores")) {
            if (rejectStore) return Map.of("error", "Record is in use", "status", 400);
            if (timeOutStore) {
                timeOutStore = false;
                if (commitBeforeTimeout) records.remove(id);
                throw new TimeoutException("Simulated lost DELETE response");
            }
        }
        records.remove(id);
        return Map.of("ok", true);
    }
}
