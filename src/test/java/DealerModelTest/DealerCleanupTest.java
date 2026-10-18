package DealerModelTest;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DealerCleanupTest {
  private static final class Api {
    final Map<String, Map<String, Object>> records = new LinkedHashMap<>();
    final List<String> deletes = new ArrayList<>();
    boolean timeOutStore;
    boolean commitBeforeTimeout;
    boolean rejectStore;
    boolean emptyDeletedResponse;

    WebDriver driver() {
      Object timeouts = Proxy.newProxyInstance(getClass().getClassLoader(),
          new Class<?>[] {WebDriver.Timeouts.class}, (proxy, method, args) -> proxy);
      Object options = Proxy.newProxyInstance(getClass().getClassLoader(),
          new Class<?>[] {WebDriver.Options.class}, (proxy, method, args) -> timeouts);
      return (WebDriver) Proxy.newProxyInstance(getClass().getClassLoader(),
          new Class<?>[] {WebDriver.class, JavascriptExecutor.class}, (proxy, method, args) -> {
            if (method.getName().equals("executeScript")) return true;
            if (method.getName().equals("manage")) return options;
            if (method.getName().equals("executeAsyncScript")) return request((Object[]) args[1]);
            if (method.getName().equals("toString")) return "Fixture cleanup regression driver";
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
        return Map.of("id", id);
      }
      String id = values.get("id").toString();
      if (action.equals("get")) {
        return records.containsKey(id) ? Map.of("entity", records.get(id))
            : emptyDeletedResponse ? Map.of("entity", "")
            : Map.of("error", "Missing owned record", "status", 404);
      }
      deletes.add(id);
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

  @Test public void retriesUncommittedDeleteBeforeParentAccounts() {
    Api api = new Api(); api.timeOutStore = true;
    DealerTestData data = new DealerTestData(api.driver());
    DealerTestData.Record store = data.store();
    Object accountId = store.values().get("accountId");
    data.close();
    Assert.assertTrue(api.records.isEmpty());
    Assert.assertEquals(api.deletes.subList(0, 2), List.of(store.id(), store.id()));
    Assert.assertEquals(api.deletes.get(2), accountId);
  }

  @Test public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted() {
    Api api = new Api(); api.timeOutStore = true; api.commitBeforeTimeout = true;
    DealerTestData data = new DealerTestData(api.driver());
    DealerTestData.Record store = data.store();
    data.close();
    Assert.assertTrue(api.records.isEmpty());
    Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
  }

  @Test public void acceptsEmptyStringReadAfterCommittedDeleteTimeout() {
    Api api = new Api(); api.timeOutStore = true; api.commitBeforeTimeout = true;
    api.emptyDeletedResponse = true;
    DealerTestData data = new DealerTestData(api.driver());
    DealerTestData.Record store = data.store();
    data.close();
    Assert.assertTrue(api.records.isEmpty());
    Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
  }

  @Test public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords() {
    Api api = new Api(); api.rejectStore = true;
    api.records.put("shared-record", new LinkedHashMap<>(Map.of("arabicName", "Existing business data")));
    DealerTestData data = new DealerTestData(api.driver());
    DealerTestData.Record store = data.store();
    Assert.expectThrows(AssertionError.class, data::close);
    Assert.assertEquals(api.deletes, List.of(store.id()));
    Assert.assertEquals(api.records.size(), 4);
    api.rejectStore = false;
    data.close();
    Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-record"));
  }
}
