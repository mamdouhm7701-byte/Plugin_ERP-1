package FinancialMovementsTest;

import FinancialMovementsTest.FinancialFixtureApi.ApiFailure;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Per-scenario fixtures. Uses the logged-in application's API client and current branch. */
final class FinancialTestData implements AutoCloseable {

  record Record(String endpoint, Object id, String name, Map<String, Object> values) {}
  private final WebDriver driver;
  private final FinancialFixtureApi api;
  private final FinancialFixtureDefinitions definitions = new FinancialFixtureDefinitions(this);
  private final String suffix;
  private final TestSupport.FixtureSession session;
  private final TestSupport.PendingFixtureWrites pendingWrites;
  private final List<Record> created = new ArrayList<>();
  private final Map<String, Record> fixtures = new LinkedHashMap<>();

  FinancialTestData(WebDriver driver) {this(driver,UUID.randomUUID().toString().substring(0,12));}
  private FinancialTestData(WebDriver driver,String suffix) {
    this.suffix=suffix;
    this.driver = driver;
    this.session=new TestSupport.FixtureSession(driver);
    this.pendingWrites=new TestSupport.PendingFixtureWrites(session,"financial",suffix);
    this.api=new FinancialFixtureApi(driver,session::verify);
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(d ->
        ((JavascriptExecutor) d).executeScript("return Boolean(document.querySelector('#app')"
            + "?.__vue__?.$store?.getters['app/currentBranch']?.id)"));
  }

  private int scenarioSequence;
  private String scenarioScope="";
  /** Stable references belong to this page; mutable/searchable records belong to the case. */
  void beginScenario(){session.verify();recoverPendingFixtures();scenarioScope="case-"+(++scenarioSequence)+"-";}
  private String fixtureKey(String key){return key.startsWith("QA-FM-")||key.equals("invoice-unit")||key.equals("invoice-category-main")||key.equals("invoice-store")||key.equals("account-main-store")||key.equals("account-sub-store")?key:scenarioScope+key;}


  @SuppressWarnings("unchecked") static FinancialTestData resume(WebDriver driver,String suffix)throws java.io.IOException{
    if(!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))throw new IllegalArgumentException("Invalid fixture journal ID");
    var data=new FinancialTestData(driver,suffix);var path=java.nio.file.Path.of("target","financial-fixtures",suffix+".json");
    if(java.nio.file.Files.exists(path))for(Object value:new org.json.JSONArray(java.nio.file.Files.readString(path)).toList()){
      var row=(Map<String,Object>)value;data.session.verify(row);
      String endpoint=(String)row.get("endpoint"),name=(String)row.get("name");
      if(name==null||!name.startsWith("QA-FM-")||!java.util.Set.of("Accounts","Customers","Suppliers","Banks","Safes","PaymentMethods","Expenses","Incomes","DiscountTypes","Agents","Stores","Units","ItemCategories","Items","Purchases","ItemTransactions-Sales","Collections","Payments","IncomeTransactions","ExpenseTransactions","CustomerDiscountTransactions","SupplierDiscountTransactions","TransfersAndSettlements").contains(endpoint)||row.get("id")==null||!(row.get("values") instanceof Map<?,?>))throw new AssertionError("Invalid owned fixture journal record");
      data.created.add(new Record(endpoint,row.get("id"),name,(Map<String,Object>)row.get("values")));
    }data.pendingWrites.resume(java.util.Set.of("Accounts","Customers","Suppliers","Banks","Safes","PaymentMethods","Expenses","Incomes","DiscountTypes","Agents","Stores","Units","ItemCategories","Items","Purchases","ItemTransactions-Sales","Collections","Payments","IncomeTransactions","ExpenseTransactions","CustomerDiscountTransactions","SupplierDiscountTransactions","TransfersAndSettlements"),"QA-FM-");return data;
  }

  static Map<String, Object> fields(Object... pairs) {
    Map<String, Object> values = new LinkedHashMap<>();
    for (int i = 0; i < pairs.length; i += 2) values.put((String) pairs[i], pairs[i + 1]);
    return values;
  }

  Record account(String key) {
    return definitions.account(key);
  }
  Record customer(int index) {
    return definitions.customer(index);
  }
  Record supplier(int index) {
    return definitions.supplier(index);
  }
  Record bank(int index) {
    return definitions.bank(index);
  }
  Record safe(int index) {
    return definitions.safe(index);
  }
  Record payment() {
    return definitions.payment();
  }
  Record expense() {
    return definitions.expense();
  }
  Record income() {
    return definitions.income();
  }
  Record discount() {
    return definitions.discount();
  }
  Record agent(boolean purchase) {
    return definitions.agent(purchase);
  }
  Record invoice(boolean purchase,java.math.BigDecimal allocation) {
    return definitions.invoice(purchase, allocation, suffix);
  }
  Record party(String type,int index) {
    return definitions.party(type, index);
  }
  @SuppressWarnings("unchecked") Map<String,Object> lookup(String endpoint){return (Map<String,Object>)call("get",endpoint,fields()).get("entity");}

  @SuppressWarnings("unchecked")
  Map<String, Object> read(Record record) {
    Object entity;
    try {entity = call("get", record.endpoint(), fields("id", record.id())).get("entity");}
    catch(ApiFailure failure){
      if(failure.status==404||failure.status==410)return null;
      // This ERP's Customers detail endpoint throws a null-reference error for a
      // deleted ID. Confirm absence through its complete paged list before accepting it.
      if(failure.status==500&&record.endpoint().equals("Customers")&&!existsInList(record))return null;
      throw failure;
    }
    // The ERP returns HTTP 200 with an empty string for some deleted records.
    if (entity instanceof String text && text.isBlank()) return null;
    if (!(entity instanceof Map<?, ?>)) {
      throw new AssertionError("Unexpected fixture read response for " + record.endpoint() + "/" + record.id());
    }
    Map<?,?> dto=(Map<?,?>)entity;
    if(dto.get("id")==null&&dto.get("arabicName")==null&&dto.get("englishName")==null&&dto.get("notes")==null
        &&!dto.containsKey("pageData")&&!dto.containsKey("totalRecords")
        &&!dto.containsKey("error")&&!dto.containsKey("message"))return null;
    return (Map<String, Object>) entity;
  }
  private boolean existsInList(Record record){
    long seen=0;java.util.Set<Object> ids=new java.util.HashSet<>();
    for(int page=1;;page++){
      Object entity=call("get",record.endpoint()+"?pageNumber="+page+"&pageSize=500",fields()).get("entity");
      if(!(entity instanceof Map<?,?> result)||!(result.get("pageData") instanceof List<?> rows)
          ||!(result.get("totalRecords") instanceof Number total))
        throw new AssertionError("Cannot verify missing owned record through its list: "+record.endpoint());
      int added=0;
      for(Object value:rows){
        if(!(value instanceof Map<?,?> row)||row.get("id")==null)throw new AssertionError("Invalid lookup identity");
        if(record.id().equals(row.get("id")))return true;
        if(ids.add(row.get("id")))added++;
      }
      seen+=added;if(seen>=total.longValue())return false;
      if(added==0)throw new AssertionError("Financial cleanup lookup pagination did not advance");
    }
  }

  @io.qameta.allure.Step("إنشاء بيانات مساعدة مستقلة: {key}")
  Record fixture(String key, String endpoint, Map<String, Object> values) {
    key=fixtureKey(key);
    if (fixtures.containsKey(key)) return fixtures.get(key);
    String name = "QA-FM-" + key + "-" + suffix;
    values.put("arabicName", name);
    values.put("englishName", name);
    values.put("notes", endpoint.equals("Purchases")||endpoint.equals("ItemTransactions-Sales")
        ?name:"Owned Financial test fixture");
    pendingWrites.start(endpoint,name,values);
    Map<String, Object> response;
    try {response = call("create", endpoint, values);}
    catch(ApiFailure|org.openqa.selenium.TimeoutException failure){
      // Items can be committed before a later server-side step fails. Recover its ID
      // for cleanup only; never turn the original failed creation into a passing test.
      if(endpoint.equals("Items"))try{
        List<Map<String,Object>> matches=new ArrayList<>();
        for(int page=1;page<=100;page++){
          Object entity=call("get","Items/Summary?pageNumber="+page+"&pageSize=500",fields()).get("entity");
          findOwned(entity,name,matches);
          if(!matches.isEmpty()||!(entity instanceof Map<?,?> result))break;
          Object rows=result.get("pageData"),total=result.get("totalRecords");
          if(!(rows instanceof List<?> list)||list.isEmpty()||!(total instanceof Number count)
              ||page*(long)list.size()>=count.longValue())break;
        }
        if(matches.size()==1){
          Object recoveredId=matches.get(0).get("id");
          Record recovered=new Record(endpoint,recoveredId,name,values);
          Map<String,Object> stored=read(recovered);
          if(name.equals(stored.get("arabicName"))&&values.get("mainUnitId").equals(stored.get("mainUnitId"))
              &&values.get("categoryId").equals(stored.get("categoryId"))){created.add(recovered);journal();}
        }
      }catch(RuntimeException|AssertionError recovery){failure.addSuppressed(recovery);}
      throw failure;
    }
    Object id = response.get("id");
    if (id == null) throw new AssertionError("Fixture API did not return an ID: " + endpoint);
    Record record = new Record(endpoint, id, name, values);
    created.add(record);
    journal();
    fixtures.put(key, record);pendingWrites.committed(endpoint,name);
    return record;
  }

  private Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
    return api.call(action, endpoint, values);
  }

  void track(String endpoint, Object id, String note, Map<String,Object> values) {
    if(note==null||!note.startsWith("QA-FM-"))throw new AssertionError("Refusing to track an unowned financial record");
    if (id == null) throw new AssertionError("Saved movement did not return an ID");
    for(int i=0;i<created.size();i++)if(created.get(i).endpoint().equals(endpoint)&&created.get(i).id().equals(id)){
      created.remove(i);created.add(new Record(endpoint,id,note,new LinkedHashMap<>(values)));journal();pendingWrites.committed(endpoint,note);return;
    }
    created.add(new Record(endpoint,id,note,new LinkedHashMap<>(values)));
    journal();pendingWrites.committed(endpoint,note);
  }
  @SuppressWarnings("unchecked") void rememberAttempts(List<Map<String,Object>> attempts,String marker){
    for(var attempt:attempts){
      var values=(Map<String,Object>)attempt.get("values");
      String name=java.util.stream.Stream.of("arabicName","englishName","notes").map(values::get).filter(String.class::isInstance).map(String.class::cast).filter(v->v.startsWith(marker)).findFirst().orElse(null);
      if(name==null)throw new AssertionError("Unowned captured request");
      pendingWrites.start((String)attempt.get("endpoint"),name,values);
    }
  }

  private void recoverPendingFixtures(){pendingWrites.recover((endpoint,values)->call("get",endpoint,values),(endpoint,id,name,values)->{track(endpoint,id,name,values);});}
  String journalSuffix(){return suffix;}
  private void journal(){
    var rows=created.stream().map(r->fields("origin",session.origin,"branchId",session.branchId,"endpoint",r.endpoint(),"id",r.id(),"name",r.name(),"values",r.values())).toList();
    TestSupport.FixtureJournals.write(java.nio.file.Path.of("target","financial-fixtures",suffix+".json"),rows);
  }

  private static boolean retryable(ApiFailure failure) {
    return failure.status == 0 || failure.status == 408 || failure.status == 429 || failure.status >= 500;
  }

  private void deleteRecord(Record record) {
    AssertionError lastFailure = null;
    for (int attempt = 1; attempt <= 3; attempt++) {
      try {
        Map<String,Object> owned=cleanupIdentity(record);
        if(owned==null)return;
        if(!record.name().equals(owned.get("arabicName"))&&!record.name().equals(owned.get("englishName"))&&!record.name().equals(owned.get("notes")))throw new AssertionError("Refusing cleanup: fixture ownership changed for "+record.id());
        call("delete", record.endpoint(), fields("id", record.id()));
        if(read(record)==null)return;
        lastFailure=new AssertionError("Delete did not remove owned record: "+record.endpoint()+"/"+record.id());
      } catch (ApiFailure failure) {
        if (failure.status == 404 || failure.status == 410
            || (failure.status == 400 && failure.getMessage().contains("Common.ObjectNotFound"))) {
          if(read(record)==null)return;
          throw new AssertionError("Delete endpoint did not remove existing owned record: "+record.endpoint()+"/"+record.id(),failure);
        }
        if (!retryable(failure)) throw failure;
        lastFailure = failure;
      } catch (org.openqa.selenium.TimeoutException failure) {
        lastFailure = new AssertionError("Delete response timed out for " + record.endpoint(), failure);
      }
      // A timed-out DELETE may have committed. Read back before sending another DELETE.
      try {
        Map<String, Object> existing = read(record);
        if (existing == null) {
          return;
        }
        if (!record.name().equals(existing.get("arabicName")) && !record.name().equals(existing.get("notes"))) {
          throw new AssertionError("Refusing cleanup: fixture ownership changed for " + record.id());
        }
      } catch (ApiFailure failure) {
        if (failure.status == 404 || failure.status == 410) return;
        if (!retryable(failure)) throw failure;
        lastFailure = failure;
      } catch (org.openqa.selenium.TimeoutException failure) {
        lastFailure = new AssertionError("Could not verify timed-out deletion of " + record.endpoint(), failure);
      }
    }
    throw lastFailure;
  }

  @SuppressWarnings("unchecked") private Map<String,Object> cleanupIdentity(Record record){
    try{return read(record);}catch(ApiFailure failure){
      if(failure.status!=500||!record.endpoint().equals("Customers"))throw failure;
      java.util.Set<Object> seen=new java.util.HashSet<>();
      for(int page=1;;page++){
        Object entity=call("get","Customers?pageNumber="+page+"&pageSize=500",fields()).get("entity");
        if(!(entity instanceof Map<?,?> result)||!(result.get("pageData") instanceof List<?> rows)||!(result.get("totalRecords") instanceof Number total))throw failure;
        int added=0;for(Object value:rows){
          if(!(value instanceof Map<?,?> row)||row.get("id")==null)throw failure;
          if(record.id().equals(row.get("id")))return (Map<String,Object>)row;
          if(seen.add(row.get("id")))added++;
        }
        if(seen.size()>=total.longValue())return null;
        if(added==0)throw failure;
      }
    }
  }

  private static boolean references(Object value, Object id) {
    if (value instanceof Map<?, ?> map) return map.values().stream().anyMatch(child -> references(child, id));
    if (value instanceof Iterable<?> list) {
      for (Object child : list) if (references(child, id)) return true;
      return false;
    }
    return id.equals(value);
  }
  @SuppressWarnings("unchecked") private static void findOwned(Object node,String name,List<Map<String,Object>> matches){
    if(node instanceof Map<?,?> row){
      if(name.equals(row.get("arabicName"))&&row.get("id")!=null)matches.add((Map<String,Object>)row);
      else for(Object value:row.values())findOwned(value,name,matches);
    }else if(node instanceof List<?> rows)for(Object row:rows)findOwned(row,name,matches);
  }

  @io.qameta.allure.Step("تنظيف سجلات الاختبار بترتيب العلاقات")
  @Override public void close() {
    recoverPendingFixtures();
    List<String> failures = new ArrayList<>();
    List<Record> pending = new ArrayList<>();
    for (int index = created.size() - 1; index >= 0; index--) {
      Record record = created.get(index);
      if (pending.stream().anyMatch(child -> references(child.values(), record.id()))) {
        pending.add(record);
        failures.add(record.name() + " [" + record.endpoint() + "/" + record.id()
            + "]: waiting for dependent fixture cleanup");
        continue;
      }
      try { deleteRecord(record); created.remove(record); fixtures.entrySet().removeIf(entry->entry.getValue().equals(record)); journal(); }
      catch (RuntimeException | AssertionError failure) {
        pending.add(record);
        failures.add(record.name() + " [" + record.endpoint() + "/" + record.id() + "]: "
            + String.valueOf(failure.getMessage()).split("\\R", 2)[0]);
      }
    }
    created.removeIf(record -> !pending.contains(record));
    fixtures.entrySet().removeIf(entry -> !pending.contains(entry.getValue()));
    journal();
    if (!failures.isEmpty()) throw new AssertionError("Fixture cleanup failed: " + String.join("; ", failures));
  }
}
