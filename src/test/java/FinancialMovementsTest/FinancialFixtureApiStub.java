package FinancialMovementsTest;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

/** In-memory ERP transport used by local cleanup regressions. */
final class FinancialFixtureApiStub {
    final Map<String, Map<String, Object>> records = new LinkedHashMap<>();
    final List<String> deletes = new ArrayList<>();
    int crashAtDelete;
    boolean failAfterUnitInsert, failPendingLookup;
    String activeBranch = "branch-test";
    int branchUnavailableReads;
    boolean timeOutBank;
    boolean commitBeforeTimeout;
    boolean rejectBank;
    boolean emptyDeletedResponse;
    boolean failAfterItemInsert;
    boolean defaultDeletedDto;
    boolean defaultDeletedMasterDto;
    boolean customerDetail500;
    boolean missingDelete400;

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
                                String code = (String) args[0];
                                if (code.equals("return location.origin")) return "offline";
                                if (code.contains("return erpVue.root()")) {
                                    if (branchUnavailableReads > 0) {
                                        branchUnavailableReads--;
                                        return null;
                                    }
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
        String action = (String) args[0], endpoint = (String) args[1];
        Map<String, Object> values = (Map<String, Object>) args[2];
        if (action.equals("create")) {
            String id = endpoint + "-" + (records.size() + 1);
            Map<String, Object> saved = new LinkedHashMap<>(values);
            saved.put("id", id);
            records.put(id, saved);
            if (endpoint.equals("Units") && failAfterUnitInsert)
                return Map.of("error", "Lost unit create response", "status", 500);
            if (endpoint.equals("Items") && failAfterItemInsert)
                return Map.of("error", "Failed after insert", "status", 500);
            return Map.of("id", id);
        }
        if (action.equals("get") && endpoint.startsWith("Items/Summary"))
            return Map.of("entity", new ArrayList<>(records.values()));
        if (action.equals("get") && endpoint.startsWith("Customers?")) {
            List<Map<String, Object>> rows =
                    records.entrySet().stream()
                            .filter(e -> e.getKey().startsWith("Customers-"))
                            .map(Map.Entry::getValue)
                            .toList();
            return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
        }
        if (action.equals("get") && endpoint.startsWith("Units?")) {
            if (failPendingLookup)
                return Map.of("error", "Recovery lookup unavailable", "status", 503);
            var rows =
                    records.values().stream()
                            .filter(r -> r.get("id").toString().startsWith("Units-"))
                            .toList();
            return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
        }
        String id = values.get("id").toString();
        if (action.equals("get")) {
            if (customerDetail500 && endpoint.equals("Customers"))
                return Map.of("error", "Null reference", "status", 500);
            return records.containsKey(id)
                    ? Map.of("entity", records.get(id))
                    : defaultDeletedMasterDto
                            ? Map.of("entity", Map.of("subUnits", List.of(), "type", "RawMaterial"))
                            : defaultDeletedDto
                                    ? Map.of(
                                            "entity",
                                            Map.of(
                                                    "transactionDate",
                                                    "0001-01-01T00:00:00",
                                                    "value",
                                                    0))
                                    : emptyDeletedResponse
                                            ? Map.of("entity", "")
                                            : Map.of(
                                                    "error", "Missing owned record", "status", 404);
        }
        deletes.add(id);
        if (crashAtDelete > 0 && deletes.size() == crashAtDelete)
            throw new Error("Simulated process interruption");
        if (missingDelete400 && !records.containsKey(id))
            return Map.of("error", "Common.ObjectNotFound", "status", 400);
        if (endpoint.equals("Banks")) {
            if (rejectBank) return Map.of("error", "Record is in use", "status", 400);
            if (timeOutBank) {
                timeOutBank = false;
                if (commitBeforeTimeout) records.remove(id);
                throw new TimeoutException("Simulated lost DELETE response");
            }
        }
        records.remove(id);
        return Map.of("ok", true);
    }
}
