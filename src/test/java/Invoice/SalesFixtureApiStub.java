package Invoice;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

/** In-memory fixture API with explicit fault injection; never connects to ERP. */
final class SalesFixtureApiStub {
    String origin = "offline", activeBranch = "branch-test";
    int branchUnavailableReads;
    boolean usedAccountTree;
    boolean omitMovementListNotes;
    boolean rejectConcreteInvoiceWhileDeliveryExists;
    boolean customerGetAlways500, customerGet500WhenMissing;
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
    boolean rejectBranchUnlink;
    boolean timeoutBranchUnlinkAfterCommit;
    boolean rejectMovement;
    boolean failMovementLookup;
    boolean delayMovementCreate;
    Map<String, Object> delayedMovement;
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
                                if (script.equals("return location.origin")) return origin;
                                if (script.contains("return erpVue.root()")) {
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
            if (endpoint.startsWith("ItemTransactions-") && delayMovementCreate) {
                delayedMovement = saved;
                throw new TimeoutException("Movement create is still pending");
            }
            records.put(id, saved);
            if (endpoint.equals("Items") && failAfterItemInsert)
                return Map.of("error", "Failed after insert", "status", 500);
            if (endpoint.equals("Stores") && failAfterStoreInsert)
                return Map.of("error", "Lost store create response", "status", 500);
            return Map.of("id", id);
        }
        if (action.equals("get") && endpoint.startsWith("Items/Summary"))
            return Map.of("entity", new ArrayList<>(records.values()));
        if (action.equals("get") && endpoint.startsWith("Accounts?"))
            return Map.of("error", "Unsupported account list", "status", 405);
        if (action.equals("get") && endpoint.startsWith("Aggregates/AccountTreeViewModel?")) {
            usedAccountTree = true;
            return Map.of(
                    "entity",
                    Map.of(
                            "Accounts",
                            new ArrayList<>(records.values()),
                            "AccountsTree",
                            new ArrayList<>(records.values())));
        }
        if (action.equals("get")
                && (endpoint.startsWith("SalesTransactions?")
                        || endpoint.startsWith("ConcreteDelivery?"))) {
            if (failMovementLookup) return Map.of("error", "Lookup unavailable", "status", 503);
            var rows =
                    records.values().stream()
                            .filter(r -> r.containsKey("lines"))
                            .filter(
                                    r ->
                                            !endpoint.startsWith("ConcreteDelivery?")
                                                    || "ConcreteDelivery"
                                                            .equals(r.get("transactionType")))
                            .map(
                                    r -> {
                                        var summary = new LinkedHashMap<>(r);
                                        if (omitMovementListNotes) summary.remove("notes");
                                        return summary;
                                    })
                            .toList();
            return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
        }
        if (action.equals("get") && endpoint.contains("?")) {
            if (failFixtureLookup)
                return Map.of("error", "Fixture lookup unavailable", "status", 503);
            String namePart =
                    endpoint.contains("arabicNamePart=")
                            ? java.net.URLDecoder.decode(
                                    endpoint.split("arabicNamePart=", 2)[1].split("&", 2)[0],
                                    java.nio.charset.StandardCharsets.UTF_8)
                            : null;
            var rows =
                    records.values().stream()
                            .filter(
                                    r ->
                                            r.containsKey("id")
                                                    && (r.containsKey("arabicName")
                                                            || r.containsKey("englishName")))
                            .filter(
                                    r ->
                                            namePart == null
                                                    || String.valueOf(r.get("arabicName"))
                                                            .contains(namePart))
                            .toList();
            return Map.of("entity", Map.of("pageData", rows, "totalRecords", rows.size()));
        }
        String id = values.get("id").toString();
        if (action.equals("get")) {
            if (endpoint.equals("Customers")
                    && (customerGetAlways500
                            || (customerGet500WhenMissing && !records.containsKey(id))))
                return Map.of("error", "Customer lookup failed", "status", 500);
            return records.containsKey(id)
                    ? Map.of("entity", records.get(id))
                    : defaultDeletedMasterDto
                            ? Map.of("entity", Map.of("subUnits", List.of(), "type", "RawMaterial"))
                            : defaultDeletedDto
                                    ? Map.of("entity", Map.of("conversionRates", List.of()))
                                    : emptyDeletedResponse
                                            ? Map.of("entity", "")
                                            : Map.of(
                                                    "error", "Missing owned record", "status", 404);
        }
        deletes.add(id);
        if (rejectConcreteInvoiceWhileDeliveryExists
                && endpoint.equals("ItemTransactions-ConcreteSales")
                && records.get(id).get("concreteDeliveryOrderIdsList") instanceof List<?> links
                && links.stream().anyMatch(link -> records.containsKey(String.valueOf(link)))) {
            return Map.of("error", "Concrete invoice is used by its delivery", "status", 400);
        }
        if (crashAtDelete > 0 && deletes.size() == crashAtDelete)
            throw new Error("Simulated process interruption");
        if (endpoint.equals("ItemTransactions-Sales") && rejectMovement)
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
