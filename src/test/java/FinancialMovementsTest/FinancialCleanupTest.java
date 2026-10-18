package FinancialMovementsTest;

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

public class FinancialCleanupTest {
  private static final class Api {
    final Map<String, Map<String, Object>> records = new LinkedHashMap<>();
    final List<String> deletes = new ArrayList<>();
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
        if(endpoint.equals("Items")&&failAfterItemInsert)return Map.of("error","Failed after insert","status",500);
        return Map.of("id", id);
      }
      if(action.equals("get")&&endpoint.startsWith("Items/Summary"))return Map.of("entity",new ArrayList<>(records.values()));
      if(action.equals("get")&&endpoint.startsWith("Customers?")){
        List<Map<String,Object>> rows=records.entrySet().stream().filter(e->e.getKey().startsWith("Customers-"))
            .map(Map.Entry::getValue).toList();
        return Map.of("entity",Map.of("pageData",rows,"totalRecords",rows.size()));
      }
      String id = values.get("id").toString();
      if (action.equals("get")) {
        if(customerDetail500&&endpoint.equals("Customers"))return Map.of("error","Null reference","status",500);
        return records.containsKey(id) ? Map.of("entity", records.get(id))
            : defaultDeletedMasterDto ? Map.of("entity",Map.of("subUnits",List.of(),"type","RawMaterial"))
            : defaultDeletedDto ? Map.of("entity",Map.of("transactionDate","0001-01-01T00:00:00","value",0))
            : emptyDeletedResponse ? Map.of("entity", "")
            : Map.of("error", "Missing owned record", "status", 404);
      }
      deletes.add(id);
      if(missingDelete400&&!records.containsKey(id))return Map.of("error","Common.ObjectNotFound","status",400);
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

  @Test public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence() {
    Api api=new Api();api.missingDelete400=true;
    FinancialTestData data=new FinancialTestData(api.driver());
    FinancialTestData.Record bank=data.bank(1);api.records.remove(bank.id());
    data.close();Assert.assertTrue(api.records.isEmpty());
  }

  @Test public void retriesUncommittedDeleteBeforeParentAccounts() {
    Api api = new Api(); api.timeOutBank = true;
    FinancialTestData data = new FinancialTestData(api.driver());
    FinancialTestData.Record bank = data.bank(1);
    Object accountId = bank.values().get("accountId");
    data.close();
    Assert.assertTrue(api.records.isEmpty());
    Assert.assertEquals(api.deletes.subList(0, 2), List.of(bank.id(), bank.id()));
    Assert.assertEquals(api.deletes.get(2), accountId);
  }

  @Test public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted() {
    Api api = new Api(); api.timeOutBank = true; api.commitBeforeTimeout = true;
    FinancialTestData data = new FinancialTestData(api.driver());
    FinancialTestData.Record bank = data.bank(1);
    data.close();
    Assert.assertTrue(api.records.isEmpty());
    Assert.assertEquals(api.deletes.stream().filter(bank.id()::equals).count(), 1L);
  }

  @Test public void acceptsEmptyStringReadAfterCommittedDeleteTimeout() {
    Api api = new Api(); api.timeOutBank = true; api.commitBeforeTimeout = true;
    api.emptyDeletedResponse = true;
    FinancialTestData data = new FinancialTestData(api.driver());
    FinancialTestData.Record bank = data.bank(1);
    data.close();
    Assert.assertTrue(api.records.isEmpty());
    Assert.assertEquals(api.deletes.stream().filter(bank.id()::equals).count(), 1L);
  }

  @Test public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords() {
    Api api = new Api(); api.rejectBank = true;
    api.records.put("shared-record", new LinkedHashMap<>(Map.of("arabicName", "Existing business data")));
    FinancialTestData data = new FinancialTestData(api.driver());
    FinancialTestData.Record bank = data.bank(1);
    Assert.expectThrows(AssertionError.class, data::close);
    Assert.assertEquals(api.deletes, List.of(bank.id()));
    Assert.assertEquals(api.records.size(), 4);
    api.rejectBank = false;
    data.close();
    Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-record"));
  }
  @Test public void cleansOwnedItemCommittedBeforeCreateFailure(){
    Api api=new Api();api.failAfterItemInsert=true;
    api.records.put("shared-item",new LinkedHashMap<>(Map.of("id","shared-item","arabicName","Existing item")));
    FinancialTestData data=new FinancialTestData(api.driver());
    FinancialTestData.Record unit=data.fixture("unit","Units",FinancialTestData.fields());
    FinancialTestData.Record category=data.fixture("category","ItemCategories",FinancialTestData.fields());
    Assert.expectThrows(AssertionError.class,()->data.fixture("item","Items",FinancialTestData.fields("mainUnitId",unit.id(),"categoryId",category.id())));
    Assert.assertEquals(api.records.size(),4);
    data.close();
    Assert.assertTrue(api.deletes.get(0).startsWith("Items-"));
    Assert.assertEquals(api.records.keySet(),java.util.Set.of("shared-item"));
  }
  @Test public void acceptsVerifiedMissingTransactionDefaultDto(){
    Api api=new Api();api.defaultDeletedDto=true;
    FinancialTestData data=new FinancialTestData(api.driver());
    FinancialTestData.Record record=data.fixture("transaction","Collections",FinancialTestData.fields());
    data.close();
    Assert.assertTrue(api.records.isEmpty());Assert.assertEquals(api.deletes,List.of(record.id()));
  }
  @Test public void acceptsVerifiedMissingMasterDefaultDto(){
    Api api=new Api();api.defaultDeletedMasterDto=true;
    FinancialTestData data=new FinancialTestData(api.driver());
    FinancialTestData.Record record=data.fixture("item","Items",FinancialTestData.fields());
    data.close();
    Assert.assertTrue(api.records.isEmpty());Assert.assertEquals(api.deletes,List.of(record.id()));
  }
  @Test public void updatedMovementDeletesBeforeItsNewReference(){
    Api api=new Api();FinancialTestData data=new FinancialTestData(api.driver());
    FinancialTestData.Record original=data.bank(1);
    FinancialTestData.Record movement=data.fixture("movement","Collections",FinancialTestData.fields("bankId",original.id()));
    FinancialTestData.Record replacement=data.bank(2);
    Map<String,Object> updated=new LinkedHashMap<>(movement.values());updated.put("bankId",replacement.id());
    api.records.put(movement.id().toString(),updated);data.track(movement.endpoint(),movement.id(),movement.name(),updated);
    data.close();
    Assert.assertEquals(api.deletes.get(0),movement.id());
    Assert.assertTrue(api.deletes.indexOf(movement.id())<api.deletes.indexOf(replacement.id()));
    Assert.assertTrue(api.records.isEmpty());
  }
  @Test public void confirmsCustomerAbsenceWithoutHidingExistingRecordErrors(){
    Api api=new Api();api.customerDetail500=true;
    FinancialTestData data=new FinancialTestData(api.driver());FinancialTestData.Record customer=data.customer(1);
    Assert.expectThrows(AssertionError.class,()->data.read(customer));
    data.close();
    Assert.assertTrue(api.records.isEmpty());Assert.assertEquals(api.deletes.get(0),customer.id());
  }
}
