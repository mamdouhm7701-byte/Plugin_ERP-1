package ProductsTest;

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
final class ProductsTestData implements AutoCloseable {
  private static final class ApiFailure extends AssertionError {
    private final int status;
    ApiFailure(int status, String message) { super(message); this.status = status; }
  }
  record Record(String endpoint, Object id, String name, Map<String, Object> values) {}
  private final WebDriver driver;
  private final String origin;
  private final String suffix;
  private final TestSupport.FixtureSession session;
  private final TestSupport.PendingFixtureWrites pendingWrites;
  private final List<Record> created = new ArrayList<>();
  private final Map<String, Record> fixtures = new LinkedHashMap<>();
  private static final String BRANCH_STORE_LINK="ProductBranchStoreMembership";

  ProductsTestData(WebDriver driver) {this(driver,UUID.randomUUID().toString().substring(0,12));}
  private ProductsTestData(WebDriver driver,String suffix) {
    this.suffix=suffix;
    this.driver = driver;
    this.session=new TestSupport.FixtureSession(driver);
    this.pendingWrites=new TestSupport.PendingFixtureWrites(session,"products",suffix);
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(d ->
        ((JavascriptExecutor) d).executeScript("return Boolean(document.querySelector('#app')"
            + "?.__vue__?.$store?.getters['app/currentBranch']?.id)"));
    Object currentOrigin=((JavascriptExecutor)driver).executeScript("return location.origin");
    origin=currentOrigin instanceof String value?value:"offline";
  }

  private int scenarioSequence;
  private String scenarioScope="";
  /** Stable references belong to this page; mutable/searchable records belong to the case. */
  void beginScenario(){session.verify();recoverPendingFixtures();scenarioScope="case-"+(++scenarioSequence)+"-";}
  private String fixtureKey(String key){return key.startsWith("QA-PR-")||key.startsWith("unit-")||key.equals("category-main")||key.startsWith("store-")||key.startsWith("account-main-store-")||key.startsWith("account-sub-store-")?key:scenarioScope+key;}


  @SuppressWarnings("unchecked") static ProductsTestData resume(WebDriver driver,String suffix)throws java.io.IOException{
    if(!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))throw new IllegalArgumentException("Invalid fixture journal ID");
    var data=new ProductsTestData(driver,suffix);var path=java.nio.file.Path.of("target","products-fixtures",suffix+".json");
    if(java.nio.file.Files.exists(path))for(Object value:new org.json.JSONArray(java.nio.file.Files.readString(path)).toList()){
      var row=(Map<String,Object>)value;data.session.verify(row);
      String endpoint=(String)row.get("endpoint"),name=(String)row.get("name");
      if(name==null||!name.startsWith("QA-PR-")||!java.util.Set.of("Accounts","Units","ItemCategories","Stores","Items","PriceLists","ProductBranchStoreMembership").contains(endpoint)||row.get("id")==null||!(row.get("values") instanceof Map<?,?>))throw new AssertionError("Invalid owned fixture journal record");
      data.created.add(new Record(endpoint,row.get("id"),name,(Map<String,Object>)row.get("values")));
    }data.pendingWrites.resume(java.util.Set.of("Accounts","Units","ItemCategories","Stores","Items","PriceLists","ProductBranchStoreMembership"),"QA-PR-");return data;
  }

  static Map<String, Object> fields(Object... pairs) {
    Map<String, Object> values = new LinkedHashMap<>();
    for (int i = 0; i < pairs.length; i += 2) values.put((String) pairs[i], pairs[i + 1]);
    return values;
  }

  Record account(String key) {
    Record parent = fixture("account-main-"+key, "Accounts", fields("type", "Main",
        "code", Integer.toString(java.util.concurrent.ThreadLocalRandom.current().nextInt(10000000, 99999999)),
        "nature", "Debit", "finalAccountType", "Budget", "level", 1,
        "isDistributable", false, "isSharedBetweenBranches", false,
        "prevDebit", 0, "prevCredit", 0, "previousBalances", List.of()));
    return fixture("account-sub-"+key, "Accounts", fields("type", "Sub", "nature", "Debit",
        "code", parent.values().get("code") + "01", "finalAccountType", "Budget",
        "parentId", parent.id(), "level", 2, "isDistributable", false,
        "isSharedBetweenBranches", false, "prevDebit", 0, "prevCredit", 0,
        "previousBalances", List.of()));
  }
  Record unit(int index) {return fixture("unit-"+index,"Units",fields("conversionRates",List.of()));}
  Record mainCategory(){return fixture("category-main","ItemCategories",fields("isMain",true));}
  Record category(){return fixture("category","ItemCategories",fields("isMain",false,"parentCategoryId",mainCategory().id()));}
  Record store(int index){
    Record store=fixture("store-"+index,"Stores",fields("accountId",account("store-"+index).id()));
    String key="branch-store-"+index;
    if(!fixtures.containsKey(key)){
      Object branchId=((JavascriptExecutor)driver).executeScript("return document.querySelector('#app').__vue__.$store.getters['app/currentBranch'].id");
      if(!(branchId instanceof String branch)||branch.isBlank())throw new AssertionError("Current branch identity is unavailable");
      Record link=new Record(BRANCH_STORE_LINK,store.id(),store.name(),fields("branchId",branchId,"storeId",store.id()));
      // Journal before updating: a lost response must still leave a recoverable owned link.
      created.add(link);fixtures.put(key,link);journal();changeBranchStore(link,true);
    }
    return store;
  }
  @SuppressWarnings("unchecked") private Map<String,Object> branchFor(Record link){
    Object entity=call("get","Branches",fields("id",link.values().get("branchId"))).get("entity");
    if(!(entity instanceof Map<?,?> branch)||!link.values().get("branchId").equals(branch.get("id"))||!(branch.get("stores") instanceof List<?>))
      throw new AssertionError("Cannot verify branch stores before changing owned membership");
    return (Map<String,Object>)entity;
  }
  private boolean containsStore(Map<String,Object> branch,Object storeId){
    return ((List<?>)branch.get("stores")).stream().anyMatch(value->value instanceof Map<?,?> row&&storeId.equals(row.get("storeId")));
  }
  @io.qameta.allure.Step("تحديث ربط مخزن الاختبار بالفرع: {add}")
  private void changeBranchStore(Record link,boolean add){
    Map<String,Object> branch=branchFor(link);Object storeId=link.values().get("storeId");
    if(containsStore(branch,storeId)!=add){
      List<Object> stores=new ArrayList<>((List<?>)branch.get("stores"));
      if(add)stores.add(fields("branchId",branch.get("id"),"storeId",storeId));
      else stores.removeIf(value->value instanceof Map<?,?> row&&storeId.equals(row.get("storeId")));
      Map<String,Object> update=new LinkedHashMap<>(branch);update.put("stores",stores);
      call("update","Branches",update);
      Map<String,Object> refreshed=branchFor(link);
      if(containsStore(refreshed,storeId)!=add)throw new AssertionError("Owned store membership was not updated");
      for(Object value:(List<?>)branch.get("stores")){
        Object previousId=((Map<?,?>)value).get("storeId");
        if(!previousId.equals(storeId)&&!containsStore(refreshed,previousId))throw new AssertionError("Existing branch store was removed");
      }
      if(!java.util.Objects.equals(branch.get("defaultStoreId"),refreshed.get("defaultStoreId")))throw new AssertionError("Branch default store changed");
      branch=refreshed;
    }
    ((JavascriptExecutor)driver).executeScript("const root=document.querySelector('#app').__vue__;if(root.$store.getters['app/currentBranch']?.id===arguments[0].id)root.$store.commit('app/setCurrentBranch',arguments[0])",branch);
  }
  Map<String,Object> itemValues(){
    return fields("categoryId",category().id(),"mainUnitId",unit(1).id(),
        "mainUnitBarcode","PR"+UUID.randomUUID().toString().replace("-",""),"type","RawMaterial","isStoreItem",true,
        "isTaxable",false,"itemTax",0,"salesPrice",100,"minSalesPrice",90,"maxSalesPrice",110,
        "sales",true,"purch",true,"returnSales",true,"returnPurch",true,"quotation",true,
        "salesOrder",true,"purchOrder",true,"storageTransfer",true,"additionOrder",true,
        "exchangeOrder",true,"damageAdjustment",true,"isScalesItem",false,
        "subUnits",List.of(),"components",List.of(),"additions",List.of(),"offers",List.of(),"discounts",List.of(),
        "stores",List.of(fields("storeId",store(1).id(),"itemPlace","QA","openingBalance",0,"currentBalance",0,
            "minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true)));
  }
  Record item(int index){return fixture("item-"+index,"Items",itemValues());}
  Record priceList(){Record item=item(1);return fixture("price-list","PriceLists",fields("pricelistDate",java.time.LocalDate.now().toString(),
      "items",List.of(fields("itemId",item.id(),"unitId",unit(1).id(),"salesPrice",125,"minSalesPrice",100,"maxSalesPrice",150))));}
  @SuppressWarnings("unchecked") Map<String,Object> lookup(String endpoint){return (Map<String,Object>)call("get",endpoint,fields()).get("entity");}
  @SuppressWarnings("unchecked") void recoverAttemptedItems(List<Map<String,Object>> attempts){
    for(Map<String,Object> attempt:attempts){
      if(!"Items".equals(attempt.get("endpoint")))continue;
      Map<String,Object> values=(Map<String,Object>)attempt.get("values");String name=java.util.stream.Stream.of("arabicName","englishName","notes")
          .map(values::get).filter(String.class::isInstance).map(String.class::cast).filter(v->v.startsWith("QA-PR-")).findFirst().orElse(null);
      if(name==null||!name.startsWith("QA-PR-")||created.stream().anyMatch(r->r.name().equals(name)))continue;
      List<Map<String,Object>> matches=new ArrayList<>();
      java.util.Set<Object> seen=new java.util.HashSet<>();
      for(int page=1;;page++){
        Object entity=call("get","Items/Summary?pageNumber="+page+"&pageSize=500",fields()).get("entity");findOwned(entity,name,matches);
        if(!matches.isEmpty()||entity instanceof List<?>)break;
        if(!(entity instanceof Map<?,?> result)||!(result.get("pageData") instanceof List<?> rows)||!(result.get("totalRecords") instanceof Number total))
          throw new AssertionError("Cannot verify partial item through its summary list");
        int added=0;for(Object value:rows){if(!(value instanceof Map<?,?> row)||row.get("id")==null)throw new AssertionError("Invalid summary identity");if(seen.add(row.get("id")))added++;}
        if(seen.size()>=total.longValue())break;
        if(added==0)throw new AssertionError("Partial item summary pagination did not advance");
      }
      if(matches.size()>1)throw new AssertionError("Ambiguous partially saved test item: "+name);
      if(matches.size()==1){Record record=new Record("Items",matches.get(0).get("id"),name,values);Map<String,Object> stored=read(record);
        if(stored==null||(!name.equals(stored.get("arabicName"))&&!name.equals(stored.get("englishName"))&&!name.equals(stored.get("notes")))||!java.util.Objects.equals(values.get("mainUnitId"),stored.get("mainUnitId"))
            ||!java.util.Objects.equals(values.get("categoryId"),stored.get("categoryId")))throw new AssertionError("Partial item ownership cannot be verified");
        created.add(record);journal();
      }
    }
  }

  @SuppressWarnings("unchecked")
  Map<String, Object> read(Record record) {
    if(record.endpoint().equals(BRANCH_STORE_LINK)){
      if(!containsStore(branchFor(record),record.values().get("storeId")))return null;
      Map<String,Object> store=read(new Record("Stores",record.values().get("storeId"),record.name(),fields()));
      if(store==null||(!record.name().equals(store.get("arabicName"))&&!record.name().equals(store.get("englishName"))))
        throw new AssertionError("Cannot verify owned store membership identity");
      return store;
    }
    Object entity;
    try {entity = call("get", record.endpoint(), fields("id", record.id())).get("entity");}
    catch(ApiFailure failure){
      if(failure.status==404||failure.status==410)return null;
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
  @io.qameta.allure.Step("إنشاء بيانات مساعدة مستقلة: {key}")
  Record fixture(String key, String endpoint, Map<String, Object> values) {
    key=fixtureKey(key);
    if (fixtures.containsKey(key)) return fixtures.get(key);
    String name = key.startsWith("QA-PR-") ? key : "QA-PR-" + key + "-" + suffix;
    values.put("arabicName", name);
    values.put("englishName", name);
    values.put("notes","Owned Products test fixture");
    pendingWrites.start(endpoint,name,values);
    Map<String, Object> response;
    try {response = call("create", endpoint, values);}
    catch(ApiFailure|org.openqa.selenium.TimeoutException failure){
      // Retain a partial commit for cleanup; the original creation still fails.
      if(endpoint.equals("Items"))try{recoverAttemptedItems(List.of(fields("endpoint",endpoint,"values",values)));}
      catch(RuntimeException|AssertionError recovery){failure.addSuppressed(recovery);}
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
        const args = {url:endpoint};
        if (action === 'create') {
          if (!branchId) { done({error:'No current branch selected'}); return; }
          args.data = {...values, branchId};
        } else if(action === 'update') {args.data=values;} else { args.id = values.id; }
        root.$store.dispatch('app/' + action, args).then(result => {
          if (action === 'delete') { done({ok:true}); return; }
          const entity = result?.data || result;
          done({id: entity?.id || (typeof entity === 'string' || typeof entity === 'number' ? entity : null),
            code: entity?.code || null,
            entity: action === 'get' ? JSON.parse(JSON.stringify(entity)) : null});
        }).catch(error => done({status: error?.response?.status || error?.status || error?.data?.status || 0,
          error: JSON.stringify(error?.response?.data || error?.data || {status:error?.status, message:String(error)})}));
        """, action, endpoint, values);
    Map<String, Object> response = (Map<String, Object>) result;
    if (response.get("error") != null) {
      int status = response.get("status") instanceof Number number ? number.intValue() : 0;
      throw new ApiFailure(status, "Fixture " + action + " " + endpoint + ": " + response.get("error"));
    }
    return response;
  }

  void track(String endpoint, Object id, String note, Map<String,Object> values) {
    if(note==null||!note.startsWith("QA-PR-"))throw new AssertionError("Refusing to track an unowned product record");
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
    TestSupport.FixtureJournals.write(java.nio.file.Path.of("target","products-fixtures",suffix+".json"),rows);
  }

  private static boolean retryable(ApiFailure failure) {
    return failure.status == 0 || failure.status == 408 || failure.status == 429 || failure.status >= 500;
  }

  private void deleteRecord(Record record) {
    AssertionError lastFailure = null;
    for (int attempt = 1; attempt <= 3; attempt++) {
      try {
        if(record.endpoint().equals(BRANCH_STORE_LINK))changeBranchStore(record,false);
        else call("delete", record.endpoint(), fields("id", record.id()));
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
        if (!record.name().equals(existing.get("arabicName")) && !record.name().equals(existing.get("notes")) && !record.name().equals(existing.get("englishName"))) {
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
  @SuppressWarnings("unchecked") private static void findOwned(Object node,String name,List<Map<String,Object>> matches){
    if(node instanceof Map<?,?> row){
      if((name.equals(row.get("arabicName"))||name.equals(row.get("englishName"))||name.equals(row.get("notes")))&&row.get("id")!=null)matches.add((Map<String,Object>)row);
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
