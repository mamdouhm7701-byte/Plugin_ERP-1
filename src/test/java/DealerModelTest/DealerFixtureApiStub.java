/* شرح الملف: تجربة تجهيز وقراءة البيانات دون الاتصال بالبرنامج الحقيقي، ضمن العملاء والموردين
 * والمناديب.
 */
package DealerModelTest;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

final class DealerFixtureApiStub {
    final Map<String, Map<String, Object>> records = new LinkedHashMap<>();
    final List<String> deletes = new ArrayList<>();
    int crashAtDelete;
    boolean failAfterUnitInsert, failPendingLookup;
    boolean rejectDealer;
    String activeBranch = "branch-test";
    boolean timeOutStore;
    boolean commitBeforeTimeout;
    boolean rejectStore;
    boolean emptyDeletedResponse;

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
                                return code.contains("return erpVue.root()") ? activeBranch : true;
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
            return Map.of("id", id);
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
        if (action.equals("get") && endpoint.startsWith("AgentItineraries?")) {
            return Map.of(
                    "entity",
                    Map.of(
                            "result",
                            records.values().stream()
                                    .filter(
                                            row ->
                                                    row.get("id")
                                                            .toString()
                                                            .startsWith("AgentItineraries-"))
                                    .toList()));
        }
        String listEndpoint = endpoint.split("\\?")[0];
        if (action.equals("get")
                && endpoint.contains("?")
                && java.util.Set.of(
                                "Agents",
                                "Customers",
                                "Suppliers",
                                "MainDealers",
                                "DealerClassifications")
                        .contains(listEndpoint)) {
            var rows =
                    records.values().stream()
                            .filter(r -> r.get("id").toString().startsWith(listEndpoint + "-"))
                            .toList();
            var match = java.util.regex.Pattern.compile("pageNumber=(\\d+)").matcher(endpoint);
            int page = match.find() ? Integer.parseInt(match.group(1)) : 1;
            return Map.of(
                    "entity",
                    Map.of(
                            "pageData",
                            page <= rows.size() ? List.of(rows.get(page - 1)) : List.of(),
                            "totalRecords",
                            rows.size()));
        }
        String id = values.get("id").toString();
        if (action.equals("get")) {
            return records.containsKey(id)
                    ? Map.of("entity", records.get(id))
                    : emptyDeletedResponse
                            ? Map.of("entity", "")
                            : Map.of("error", "Missing owned record", "status", 404);
        }
        deletes.add(id);
        if (crashAtDelete > 0 && deletes.size() == crashAtDelete)
            throw new Error("Simulated process interruption");
        if (endpoint.equals("Customers") && rejectDealer)
            return Map.of("error", "Owned dealer still in use", "status", 400);
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
