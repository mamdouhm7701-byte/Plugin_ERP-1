package DealerModelTest;

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
final class DealerTestData implements AutoCloseable {
  private static final class ApiFailure extends AssertionError {
    private final int status;
    ApiFailure(int status, String message) { super(message); this.status = status; }
  }
  record Record(String endpoint, Object id, String name, Map<String, Object> values) {}
  private final WebDriver driver;
  private final String suffix;
  private final TestSupport.FixtureSession session;
  private final TestSupport.PendingFixtureWrites pendingWrites;
  private final List<Record> created = new ArrayList<>();
  private final Map<String, Record> fixtures = new LinkedHashMap<>();

  DealerTestData(WebDriver driver) {this(driver,UUID.randomUUID().toString().substring(0,12));}
  private DealerTestData(WebDriver driver,String suffix) {
    this.suffix=suffix;
    this.driver = driver;
    this.session=new TestSupport.FixtureSession(driver);
    this.pendingWrites=new TestSupport.PendingFixtureWrites(session,"dealer",suffix);
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(d ->
        ((JavascriptExecutor) d).executeScript("return Boolean(document.querySelector('#app')"
            + "?.__vue__?.$store?.getters['app/currentBranch']?.id)"));
  }

  private int scenarioSequence;
  private String scenarioScope="";
  /** Stable references belong to this page; mutable/searchable records belong to the case. */
  void beginScenario(){session.verify();recoverPendingFixtures();scenarioScope="case-"+(++scenarioSequence)+"-";}
  private String fixtureKey(String key){return key.startsWith("QA-")||key.equals("area")||key.startsWith("class-")||key.startsWith("main-")||key.equals("unit")||key.equals("category-main")?key:scenarioScope+key;}


  @SuppressWarnings("unchecked") static DealerTestData resume(WebDriver driver,String suffix)throws java.io.IOException{
    if(!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))throw new IllegalArgumentException("Invalid fixture journal ID");
    var data=new DealerTestData(driver,suffix);var path=java.nio.file.Path.of("target","dealer-fixtures",suffix+".json");
    if(java.nio.file.Files.exists(path))for(Object value:new org.json.JSONArray(java.nio.file.Files.readString(path)).toList()){
      var row=(Map<String,Object>)value;data.session.verify(row);
      String endpoint=(String)row.get("endpoint"),name=(String)row.get("name");
      if(name==null||!name.startsWith("QA-")||!java.util.Set.of("Areas","DealerClassifications","MainDealers","Agents","Accounts","Stores","Units","ItemCategories","Items","PriceLists").contains(endpoint)||row.get("id")==null||!(row.get("values") instanceof Map<?,?>))throw new AssertionError("Invalid owned fixture journal record");
      data.created.add(new Record(endpoint,row.get("id"),name,(Map<String,Object>)row.get("values")));
    }data.pendingWrites.resume(java.util.Set.of("Areas","DealerClassifications","MainDealers","Agents","Accounts","Stores","Units","ItemCategories","Items","PriceLists"),"QA-");return data;
  }

  static Map<String, Object> fields(Object... pairs) {
    Map<String, Object> values = new LinkedHashMap<>();
    for (int i = 0; i < pairs.length; i += 2) values.put((String) pairs[i], pairs[i + 1]);
    return values;
  }

  Record area() { return fixture("area", "Areas", fields()); }
  Record classification(String type) {
    return fixture("class-" + type, "DealerClassifications", fields("classificationType", type));
  }
  Record mainDealer(String type) {
    return fixture("main-" + type, "MainDealers", fields("type", type));
  }
  Record agent(String nature) {
    return fixture("agent-" + nature, "Agents", fields("nature", nature,
        "commissionSlices", List.of(), "itemCommissions", List.of(),
        "isActive", true, "areaId", area().id(),
        "dealersClassificationId", classification("Agents").id()));
  }
  Record account() {
    Record parent = fixture("account-main", "Accounts", fields("type", "Main",
        "code", Integer.toString(java.util.concurrent.ThreadLocalRandom.current().nextInt(10000000, 99999999)),
        "nature", "Debit", "finalAccountType", "Budget", "level", 1,
        "isDistributable", false, "isSharedBetweenBranches", false,
        "prevDebit", 0, "prevCredit", 0, "previousBalances", List.of()));
    return fixture("account-sub", "Accounts", fields("type", "Sub", "nature", "Debit",
        "code", parent.values().get("code") + "01",
        "finalAccountType", "Budget", "parentId", parent.id(), "level", 2,
        "isDistributable", false, "isSharedBetweenBranches", false,
        "prevDebit", 0, "prevCredit", 0, "previousBalances", List.of()));
  }
  Record store() {
    return fixture("store", "Stores", fields("accountId", account().id()));
  }
  Record unit() {
    return fixture("unit", "Units", fields("conversionRates", List.of()));
  }
  Record category() {
    Record parent = fixture("category-main", "ItemCategories", fields("isMain", true));
    return fixture("category-sub", "ItemCategories", fields("isMain", false,
        "parentCategoryId", parent.id()));
  }
  Record item(int index) {
    String barcode = "QA" + UUID.randomUUID().toString().replace("-", "");
    return fixture("item-" + index, "Items", fields(
        "categoryId", category().id(), "mainUnitId", unit().id(),
        "mainUnitBarcode", barcode, "salesPrice", 100, "minSalesPrice", 90,
        "maxSalesPrice", 110, "isScalesItem", false, "isTaxable", false,
        "isStoreItem", true, "type", "RawMaterial", "itemTax", 0,
        "subUnits", List.of(), "components", List.of(), "additions", List.of(),
        "offers", List.of(), "discounts", List.of(), "sales", true, "purch", true,
        "returnSales", true, "returnPurch", true, "quotation", true,
        "salesOrder", true, "purchOrder", true, "storageTransfer", true,
        "additionOrder", true, "exchangeOrder", true, "damageAdjustment", true,
        "stores", List.of(fields("storeId", store().id(), "itemPlace", "QA",
            "openingBalance", 0, "currentBalance", 0, "minimumLimit", 0,
            "reorderLimit", 0, "costPrice", 80, "isActive", true))));
  }
  Record priceList() {
    return fixture("price-list", "PriceLists", fields("items", List.of(),
        "pricelistDate", java.time.LocalDate.now().toString(),
        "code", Integer.toString(java.util.concurrent.ThreadLocalRandom.current().nextInt(10000000, 99999999))));
  }

  @SuppressWarnings("unchecked")
  Map<String, Object> read(Record record) {
    Object entity = call("get", record.endpoint(), fields("id", record.id())).get("entity");
    // The ERP returns HTTP 200 with an empty string for some deleted records.
    if (entity instanceof String text && text.isBlank()) return null;
    if (!(entity instanceof Map<?, ?>)) {
      throw new AssertionError("Unexpected fixture read response for " + record.endpoint() + "/" + record.id());
    }
    return (Map<String, Object>) entity;
  }

  private Record fixture(String key, String endpoint, Map<String, Object> values) {
    key=fixtureKey(key);
    if (fixtures.containsKey(key)) return fixtures.get(key);
    String name = "QA-" + key + "-" + suffix;
    values.put("arabicName", name);
    values.put("englishName", name);
    values.put("notes", "Owned Dealer test fixture");
    pendingWrites.start(endpoint,name,values);
    Map<String, Object> response = call("create", endpoint, values);
    Object id = response.get("id");
    if (id == null) throw new AssertionError("Fixture API did not return an ID: " + endpoint);
    Record record = new Record(endpoint, id, name, values);
    created.add(record);journal();
    fixtures.put(key, record);pendingWrites.committed(endpoint,name);
    if (endpoint.equals("Items")) {
      values.put("code", call("get", endpoint, fields("id", id)).get("code"));
    }
    return record;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
    session.verify();
    driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(action.equals("delete") ? 60 : 30));
    Object result = ((JavascriptExecutor) driver).executeAsyncScript("""
        const [action, endpoint, values, done] = arguments;
        const root = document.querySelector('#app')?.__vue__;
        if (!root?.$store) { done({error:'Authenticated ERP session is not ready'}); return; }
        const queue = [root]; let branchId = null;
        while (queue.length) {
          const vm = queue.shift();
          if (vm.currentBranch?.id) { branchId = vm.currentBranch.id; break; }
          queue.push(...(vm.$children || []));
        }
        const args = {url: endpoint};
        if (action === 'create') {
          if (!branchId) { done({error:'No current branch selected'}); return; }
          args.data = {...values, branchId};
        } else { args.id = values.id; }
        root.$store.dispatch('app/' + action, args).then(result => {
          if (action === 'delete') { done({ok:true}); return; }
          const entity = result?.data || result;
          done({id: entity?.id || (typeof entity === 'string' || typeof entity === 'number' ? entity : null),
            code: entity?.code || null,
            entity: action === 'get' ? JSON.parse(JSON.stringify(entity)) : null});
        }).catch(error => done({status: error?.status || 0,
          error: JSON.stringify(error?.data || {status:error?.status, message:String(error)})}));
        """, action, endpoint, values);
    Map<String, Object> response = (Map<String, Object>) result;
    if (response.get("error") != null) {
      int status = response.get("status") instanceof Number number ? number.intValue() : 0;
      throw new ApiFailure(status, "Fixture " + action + " " + endpoint + ": " + response.get("error"));
    }
    return response;
  }

  private void recoverPendingFixtures(){pendingWrites.recover((endpoint,values)->call("get",endpoint,values),(endpoint,id,name,values)->{if(created.stream().noneMatch(r->r.endpoint().equals(endpoint)&&r.id().equals(id))){created.add(new Record(endpoint,id,name,new LinkedHashMap<>(values)));journal();}});}
  String journalSuffix(){return suffix;}
  private void journal(){
    var rows=created.stream().map(r->fields("origin",session.origin,"branchId",session.branchId,"endpoint",r.endpoint(),"id",r.id(),"name",r.name(),"values",r.values())).toList();
    TestSupport.FixtureJournals.write(java.nio.file.Path.of("target","dealer-fixtures",suffix+".json"),rows);
  }

  private static boolean retryable(ApiFailure failure) {
    return failure.status == 0 || failure.status == 408 || failure.status == 429 || failure.status >= 500;
  }

  /** Page cleanup must not infer absence from the currently visible table page. */
  @SuppressWarnings("unchecked") private List<Map<String,Object>> dealerRows(String endpoint){
    List<Map<String,Object>> records=new ArrayList<>();java.util.Set<Object> ids=new java.util.HashSet<>();
    for(int page=1;;page++){
      Object entity=call("get",endpoint+"?pageNumber="+page+"&pageSize=500&branchId="+session.branchId,fields()).get("entity");
      List<?> rows;long total;
      if(entity instanceof List<?> list){rows=list;total=list.size();}
      else if(entity instanceof Map<?,?> result&&result.get("pageData") instanceof List<?> list&&result.get("totalRecords") instanceof Number count){rows=list;total=count.longValue();}
      else throw new AssertionError("Cannot establish owned dealer absence through complete list");
      int added=0;for(Object value:rows){
        if(!(value instanceof Map<?,?> row)||row.get("id")==null)throw new AssertionError("Dealer list identity is unavailable");
        if(ids.add(row.get("id"))){records.add((Map<String,Object>)row);added++;}
      }
      if(ids.size()>=total)return records;
      if(added==0)throw new AssertionError("Dealer cleanup pagination did not advance");
    }
  }

  void deleteOwnedDealer(Plugin_ERP.Enums.Pages route,String name){
    if(name==null||!name.startsWith("QA-"))throw new AssertionError("Refusing unowned dealer cleanup");
    String endpoint=switch(route){case AGENTS->"Agents";case CUSTOMERS->"Customers";case SUPPLIERS->"Suppliers";case MAINDEALERS->"MainDealers";case CUSTOMERCLASS->"DealerClassifications";default->throw new AssertionError("Unsupported dealer cleanup route");};
    var matches=dealerRows(endpoint).stream().filter(row->name.equals(row.get("arabicName"))).toList();
    if(matches.isEmpty())return;
    if(matches.size()!=1)throw new AssertionError("Ambiguous owned dealer name; retaining recovery journal");
    Object id=matches.get(0).get("id");
    for(int attempt=1;attempt<=3;attempt++){
      ApiFailure rejected=null;
      try{call("delete",endpoint,fields("id",id));}
      catch(ApiFailure failure){rejected=failure;}
      catch(org.openqa.selenium.TimeoutException unknown){/* Verify whether the exact delete committed. */}
      var remaining=dealerRows(endpoint).stream().filter(row->id.equals(row.get("id"))).toList();
      if(remaining.isEmpty())return;
      if(remaining.stream().anyMatch(row->!name.equals(row.get("arabicName"))))throw new AssertionError("Dealer ownership changed during cleanup");
      if(rejected!=null&&!retryable(rejected))throw rejected;
    }
    throw new AssertionError("Owned dealer still exists after cleanup: "+name);
  }

  private void deleteRecord(Record record) {
    AssertionError lastFailure = null;
    for (int attempt = 1; attempt <= 3; attempt++) {
      try {
        Map<String,Object> owned=read(record);
        if(owned==null)return;
        if(!record.name().equals(owned.get("arabicName"))&&!record.name().equals(owned.get("englishName"))&&!record.name().equals(owned.get("notes")))throw new AssertionError("Refusing cleanup: fixture ownership changed for "+record.id());
        call("delete", record.endpoint(), fields("id", record.id()));
        return;
      } catch (ApiFailure failure) {
        if (failure.status == 404 || failure.status == 410) return;
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
        if (!record.name().equals(existing.get("arabicName"))) {
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

  private static boolean references(Object value, Object id) {
    if (value instanceof Map<?, ?> map) return map.values().stream().anyMatch(child -> references(child, id));
    if (value instanceof Iterable<?> list) {
      for (Object child : list) if (references(child, id)) return true;
      return false;
    }
    return id.equals(value);
  }

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
