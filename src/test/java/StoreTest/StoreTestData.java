package StoreTest;

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
final class StoreTestData implements AutoCloseable {
  private static final class ApiFailure extends AssertionError {
    private final int status;
    ApiFailure(int status, String message) { super(message); this.status = status; }
  }
  record Record(String endpoint, Object id, String name, Map<String, Object> values) {}
  private final WebDriver driver;
  private final String origin;
  private final Object sessionBranchId;
  private final String suffix;
  private final List<Record> created = new ArrayList<>();
  private final Map<String, Record> fixtures = new LinkedHashMap<>();
  private static final String BRANCH_STORE_LINK="StoreBranchStoreMembership";
  private static final java.util.Set<String> MOVEMENTS=java.util.Set.of("ItemTransactions-ExchangeOrder","AdditionOrders","ManufacturingOrders","ItemTransactions-storageTransfer","DamageAdjustments");
  private static final java.util.Set<String> MASTERS=java.util.Set.of("Accounts","Units","ItemCategories","Stores","Items","Agents","Customers","Suppliers","ExchangeDestinations");
  private final List<Map<String,Object>> uncertainDocuments=new ArrayList<>();
  private String lastAttemptJournal="";

  StoreTestData(WebDriver driver) {this(driver,UUID.randomUUID().toString().substring(0,12));}
  private StoreTestData(WebDriver driver,String suffix) {
    this.suffix=suffix;
    this.driver = driver;
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(d ->
        ((JavascriptExecutor) d).executeScript("return Boolean(document.querySelector('#app')"
            + "?.__vue__?.$store?.getters['app/currentBranch']?.id)"));
    Object currentOrigin=((JavascriptExecutor)driver).executeScript("return location.origin");
    origin=currentOrigin instanceof String value?value:"offline";
    sessionBranchId=((JavascriptExecutor)driver).executeScript("return document.querySelector('#app').__vue__.$store.getters['app/currentBranch'].id");
  }

  /** Resume only an explicitly chosen journal, in its original tenant and branch. */
  @SuppressWarnings("unchecked") static StoreTestData resume(WebDriver driver,String suffix)throws java.io.IOException{
    if(!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))throw new IllegalArgumentException("Invalid warehouse journal ID");
    StoreTestData data=new StoreTestData(driver,suffix);
    for(String folder:List.of("store-fixtures","store-attempts")){
      var path=java.nio.file.Path.of("target",folder,suffix+".json");if(!java.nio.file.Files.exists(path))continue;
      for(Object value:new org.json.JSONArray(java.nio.file.Files.readString(path)).toList()){
        Map<String,Object> row=(Map<String,Object>)value;
        if(!data.origin.equals(row.get("origin"))||!java.util.Objects.equals(data.sessionBranchId,row.get("branchId")))
          throw new AssertionError("Warehouse journal belongs to another origin or branch");
        String endpoint=(String)row.get("endpoint"),name=(String)row.get("name");
        if(name==null||!name.startsWith("QA-ST-")||endpoint==null||!(row.get("values") instanceof Map<?,?>))throw new AssertionError("Invalid warehouse journal record");
        if(folder.equals("store-fixtures"))data.created.add(new Record(endpoint,row.get("id"),name,(Map<String,Object>)row.get("values")));
        else data.uncertainDocuments.add(row);
      }
    }
    return data;
  }

  private int scenarioSequence;
  private String scenarioScope="";
  /** Stable references belong to this page; mutable/searchable records belong to the case. */
  void beginScenario(){recoverUncertainDocuments();scenarioScope="case-"+(++scenarioSequence)+"-";}
  private String fixtureKey(String key){return key.startsWith("unit-")||key.equals("category-main")||key.startsWith("store-")||key.startsWith("account-main-store-")||key.startsWith("account-sub-store-")?key:scenarioScope+key;}

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
        "mainUnitBarcode","ST"+UUID.randomUUID().toString().replace("-",""),"type","RawMaterial","isStoreItem",true,
        "isTaxable",false,"itemTax",0,"salesPrice",100,"minSalesPrice",90,"maxSalesPrice",110,
        "sales",true,"purch",true,"returnSales",true,"returnPurch",true,"quotation",true,
        "salesOrder",true,"purchOrder",true,"storageTransfer",true,"additionOrder",true,
        "exchangeOrder",true,"damageAdjustment",true,"isScalesItem",false,
        "subUnits",List.of(),"components",List.of(),"additions",List.of(),"offers",List.of(),"discounts",List.of(),
        "stores",List.of(fields("storeId",store(1).id(),"itemPlace","QA","openingBalance",0,"currentBalance",0,
            "minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true)));
  }
  Record item(int index){
    Map<String,Object> values=itemValues();
    values.put("stores",List.of(fields("storeId",store(1).id(),"itemPlace","QA","openingBalance",1000,"currentBalance",1000,"minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true),
      fields("storeId",store(2).id(),"itemPlace","QA","openingBalance",0,"currentBalance",0,"minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true)));
    return fixture("item-"+index,"Items",values);
  }
  Record manufactured(){return manufactured(1);}
  Record manufactured(int index){
    Record component=item(2);Map<String,Object> values=itemValues();values.put("isManufacturingItem",true);
    values.put("stores",List.of(fields("storeId",store(1).id(),"itemPlace","QA","openingBalance",0,"currentBalance",0,"minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true),
      fields("storeId",store(2).id(),"itemPlace","QA","openingBalance",0,"currentBalance",0,"minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true)));
    values.put("componentsStoreId",store(1).id());values.put("components",List.of(fields("componentId",component.id(),"unitId",unit(1).id(),"requiredQuantity",2)));
    return fixture("manufactured-"+index,"Items",values);
  }
  Record agent(){return agent(false);}
  Record agent(boolean purchase){return fixture(purchase?"agent-purchase":"agent-sales","Agents",fields("nature",purchase?"Purch":"Sales","isActive",true,"commissionSlices",List.of(),"itemCommissions",List.of()));}
  Record destination(){return fixture("destination","ExchangeDestinations",fields("accountId",account("destination").id()));}
  Record supplier(){return fixture("supplier","Suppliers",fields("isActive",true,"cashPurchase",true,"creditPurchase",true,"isTaxable",false,"isClosed",false,"purchaseReturn",true,"purchaseOrder",true,"dealerType","Supplier","isSameBranch",true,"allowanceDays",0,"accountId",account("supplier").id()));}
  Record customer(){return fixture("customer","Customers",fields("isActive",true,"cashSales",true,"creditSales",true,"returnSales",true,"isTaxable",false,"isClosed",false,"salesOrder",true,"dealerType","Customer","isSameBranch",true,"allowanceDays",0,"accountId",account("customer").id()));}
  Object balance(Record item,int index){Map<String,Object> dto=read(item);for(Object value:(List<?>)dto.get("stores")){Map<?,?> row=(Map<?,?>)value;if(store(index).id().equals(row.get("storeId")))return row.get("currentBalance");}throw new AssertionError("Owned store balance missing");}

  @SuppressWarnings("unchecked") Map<String,Object> lookup(String endpoint){return (Map<String,Object>)call("get",endpoint,fields()).get("entity");}
  @SuppressWarnings("unchecked") void recoverAttemptedItems(List<Map<String,Object>> attempts){
    for(Map<String,Object> attempt:attempts){
      if(!"Items".equals(attempt.get("endpoint")))continue;
      Map<String,Object> values=(Map<String,Object>)attempt.get("values");String name=java.util.stream.Stream.of("arabicName","englishName","notes")
          .map(values::get).filter(String.class::isInstance).map(String.class::cast).filter(v->v.startsWith("QA-ST-")).findFirst().orElse(null);
      if(name==null||!name.startsWith("QA-ST-")||created.stream().anyMatch(r->r.name().equals(name)))continue;
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
      if(added==0)throw new AssertionError("Store cleanup lookup pagination did not advance");
    }
  }

  @io.qameta.allure.Step("إنشاء بيانات مساعدة مستقلة: {key}")
  Record fixture(String key, String endpoint, Map<String, Object> values) {
    key=fixtureKey(key);
    if (fixtures.containsKey(key)) return fixtures.get(key);
    String name = key.startsWith("QA-ST-") ? key : "QA-ST-" + key + "-" + suffix;
    values.put("arabicName", name);
    values.put("englishName", name);
    values.put("notes","Owned Store test fixture");
    Map<String,Object> attempt=MASTERS.contains(endpoint)?rememberFixtureAttempt(endpoint,name,values):null;
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
    if(attempt!=null){uncertainDocuments.remove(attempt);journalAttempts();}
    fixtures.put(key, record);
    return record;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
    driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(action.equals("delete") ? 60 : 30));
    Object result = ((JavascriptExecutor) driver).executeAsyncScript("""
        const [action, endpoint, values, expectedOrigin, expectedBranch, done] = arguments;
        const root = document.querySelector('#app')?.__vue__;
        if (!root?.$store) { done({error:'Authenticated ERP session is not ready'}); return; }
        if(location.origin!==expectedOrigin||root.$store.getters['app/currentBranch']?.id!==expectedBranch){done({error:'ERP origin or branch changed; refusing fixture access'});return;}
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
        """, action, endpoint, values,origin,sessionBranchId);
    Map<String, Object> response = (Map<String, Object>) result;
    if (response.get("error") != null) {
      int status = response.get("status") instanceof Number number ? number.intValue() : 0;
      throw new ApiFailure(status, "Fixture " + action + " " + endpoint + ": " + response.get("error"));
    }
    return response;
  }

  void track(String endpoint, Object id, String note, Map<String,Object> values) {
    if(note==null||!note.startsWith("QA-ST-"))throw new AssertionError("Refusing unrelated warehouse record");
    if (id == null) throw new AssertionError("Saved movement did not return an ID");
    for(int i=0;i<created.size();i++)if(created.get(i).endpoint().equals(endpoint)&&created.get(i).id().equals(id)){
      created.remove(i);created.add(new Record(endpoint,id,note,new LinkedHashMap<>(values)));journal();return;
    }
    created.add(new Record(endpoint,id,note,new LinkedHashMap<>(values)));
    journal();
  }
  private void journal(){
    List<Map<String,Object>> rows=created.stream().map(r->fields("origin",origin,"branchId",sessionBranchId,"endpoint",r.endpoint(),"id",r.id(),"name",r.name(),"values",r.values())).toList();
    TestSupport.FixtureJournals.write(java.nio.file.Path.of("target","store-fixtures",suffix+".json"),rows);
  }

  private static boolean retryable(ApiFailure failure) {
    return failure.status == 0 || failure.status == 408 || failure.status == 429 || failure.status >= 500;
  }
  boolean owns(Record record,Map<String,Object> stored){
    if(!record.name().startsWith("QA-ST-"))return false;
    if(record.name().equals(stored.get("arabicName"))||record.name().equals(stored.get("englishName"))||record.name().equals(stored.get("notes")))return true;
    if(!MOVEMENTS.contains(record.endpoint())||!java.util.Objects.equals(record.values().get("storeId"),stored.get("storeId")))return false;
    if(created.stream().noneMatch(r->r.endpoint().equals("Stores")&&r.id().equals(stored.get("storeId"))&&r.name().startsWith("QA-ST-")))return false;
    if(!(stored.get("lines") instanceof List<?> lines)||lines.isEmpty()||!(record.values().get("lines") instanceof List<?> expected))return false;
    for(Object value:lines){if(!(value instanceof Map<?,?> line))return false;
      if(expected.stream().noneMatch(e->e instanceof Map<?,?> row&&java.util.Objects.equals(row.get("itemId"),line.get("itemId"))&&java.util.Objects.equals(row.get("unitId"),line.get("unitId"))))return false;
      if(created.stream().noneMatch(r->r.endpoint().equals("Items")&&r.id().equals(line.get("itemId"))&&r.name().startsWith("QA-ST-")))return false;
    }
    return true;
  }
  @SuppressWarnings("unchecked") void rememberAttempts(List<Map<String,Object>> attempts,List<Map<String,Object>> saves,String marker){
    if(!marker.startsWith("QA-ST-"))throw new AssertionError("Refuse unrelated warehouse attempt");
    List<Map<String,Object>> available=new ArrayList<>(saves);uncertainDocuments.removeIf(row->!Boolean.TRUE.equals(row.get("factory")));
    for(var attempt:attempts){String endpoint=(String)attempt.get("endpoint");if(!MOVEMENTS.contains(endpoint))throw new AssertionError("Unexpected warehouse attempt endpoint");
      Map<String,Object> values=(Map<String,Object>)attempt.get("values");Object id=values.get("id");boolean successful=false;
      for(var saved:new ArrayList<>(available))if(endpoint.equals(saved.get("endpoint"))&&values.equals(saved.get("values"))){id=saved.get("id");successful=true;available.remove(saved);break;}
      final Object knownId=id;if(successful&&id!=null&&created.stream().anyMatch(r->r.endpoint().equals(endpoint)&&r.id().equals(knownId)))continue;
      String name=values.get("notes") instanceof String note&&note.startsWith(marker)?note:marker;
      uncertainDocuments.add(fields("origin",origin,"branchId",sessionBranchId,"endpoint",endpoint,"id",id,"name",name,"values",new LinkedHashMap<>(values)));
    }journalAttempts();
  }
  private void journalAttempts(){String json=new org.json.JSONArray(uncertainDocuments).toString(2);if(json.equals(lastAttemptJournal))return;
    TestSupport.FixtureJournals.write(java.nio.file.Path.of("target","store-attempts",suffix+".json"),uncertainDocuments);lastAttemptJournal=json;
  }
  Map<String,Object> rememberFixtureAttempt(String endpoint,String name,Map<String,Object> values){
    if(!MASTERS.contains(endpoint)||!name.startsWith("QA-ST-"))throw new AssertionError("Refuse unrelated fixture recovery");
    Map<String,Object> attempt=fields("origin",origin,"branchId",sessionBranchId,"factory",true,"endpoint",endpoint,"name",name,"values",new LinkedHashMap<>(values));
    uncertainDocuments.add(attempt);journalAttempts();return attempt;
  }
  @SuppressWarnings("unchecked") private void recoverFixtureAttempt(Map<String,Object> attempt){
    String endpoint=(String)attempt.get("endpoint"),name=(String)attempt.get("name");Map<String,Object> values=(Map<String,Object>)attempt.get("values");
    if(!MASTERS.contains(endpoint)||!name.startsWith("QA-ST-"))throw new AssertionError("Invalid fixture recovery endpoint or name");
    List<Record> matches=new ArrayList<>();java.util.Set<Object> seen=new java.util.HashSet<>();
    for(int page=1;;page++){
      Object entity=call("get",endpoint+"?pageNumber="+page+"&pageSize=500",fields()).get("entity");List<?> rows;long total;
      if(entity instanceof List<?> list){rows=list;total=list.size();}
      else if(entity instanceof Map<?,?> result&&result.get("pageData") instanceof List<?> list&&result.get("totalRecords") instanceof Number count){rows=list;total=count.longValue();}
      else throw new AssertionError("Cannot verify uncertain fixture through its list: "+endpoint);
      int added=0;for(Object value:rows){
        if(!(value instanceof Map<?,?> row)||row.get("id")==null||(!row.containsKey("arabicName")&&!row.containsKey("englishName")))throw new AssertionError("Fixture list omits ownership fields: "+endpoint);
        if(!seen.add(row.get("id")))continue;added++;
        if(!name.equals(row.get("arabicName"))&&!name.equals(row.get("englishName")))continue;
        Record candidate=new Record(endpoint,row.get("id"),name,values);Map<String,Object> stored=read(candidate);if(stored==null)continue;
        if(!owns(candidate,stored))throw new AssertionError("Uncertain fixture ownership changed");
        for(String reference:List.of("accountId","parentId","parentCategoryId","categoryId","mainUnitId"))
          if(values.containsKey(reference)&&!java.util.Objects.equals(values.get(reference),stored.get(reference)))throw new AssertionError("Uncertain fixture reference changed: "+reference);
        matches.add(candidate);
      }
      if(seen.size()>=total)break;if(added==0)throw new AssertionError("Uncertain fixture pagination did not advance");
    }
    if(matches.size()>1)throw new AssertionError("Ambiguous uncertain fixture: "+name);
    if(matches.size()==1){Record found=matches.get(0);track(endpoint,found.id(),name,values);
      if(endpoint.equals("Stores")){Record link=new Record(BRANCH_STORE_LINK,found.id(),name,fields("branchId",sessionBranchId,"storeId",found.id()));
        if(containsStore(branchFor(link),found.id()))track(BRANCH_STORE_LINK,found.id(),name,link.values());}
    }
    uncertainDocuments.remove(attempt);journalAttempts();
  }
  @SuppressWarnings("unchecked") private void recoverUncertainDocuments(){
    for(var attempt:new ArrayList<>(uncertainDocuments)){
      if(Boolean.TRUE.equals(attempt.get("factory"))){recoverFixtureAttempt(attempt);continue;}
      String endpoint=(String)attempt.get("endpoint"),name=(String)attempt.get("name");Map<String,Object> values=(Map<String,Object>)attempt.get("values");
      List<Record> matches=new ArrayList<>();Object id=attempt.get("id");
      if(id!=null){Record candidate=new Record(endpoint,id,name,values);Map<String,Object> stored=read(candidate);
        if(stored!=null){boolean verified=owns(candidate,stored)||created.stream().anyMatch(r->r.endpoint().equals(endpoint)&&r.id().equals(id)&&owns(r,stored));
          if(!verified)throw new AssertionError("Cannot verify uncertain warehouse update ownership");matches.add(candidate);}
      }else{
        String list=endpoint.equals("DamageAdjustments")?"DamageAdjustments":endpoint.equals("ItemTransactions-storageTransfer")?"ItemTransactions-StorageTransfer":"InventoryTransactions";
        String type=switch(endpoint){case "AdditionOrders"->"AdditionOrder";case "ManufacturingOrders"->"Manufacturing";case "ItemTransactions-ExchangeOrder"->"ExchangeOrder";case "DamageAdjustments"->"DamageAdjustment";default->"StorageTransfer";};
        java.util.Set<Object> seen=new java.util.HashSet<>();
        for(int page=1;;page++){Object entity=call("get",list+"?pageNumber="+page+"&pageSize=500",fields()).get("entity");
          if(!(entity instanceof Map<?,?> result)||!(result.get("pageData") instanceof List<?> rows)||!(result.get("totalRecords") instanceof Number total))throw new AssertionError("Cannot verify uncertain warehouse create through its list");
          int added=0;for(Object value:rows){if(!(value instanceof Map<?,?> row)||row.get("id")==null)throw new AssertionError("Warehouse list has no record identity");if(!seen.add(row.get("id")))continue;added++;
            if(!row.containsKey("notes")&&!row.containsKey("storeId"))throw new AssertionError("Warehouse list omits fields needed to verify an uncertain create");
            if(name.equals(row.get("notes"))||(java.util.Objects.equals(values.get("storeId"),row.get("storeId"))&&type.equalsIgnoreCase(String.valueOf(row.get("transactionType"))))){
              Record candidate=new Record(endpoint,row.get("id"),name,values);Map<String,Object> stored=read(candidate);
              if(stored!=null){if(!owns(candidate,stored))throw new AssertionError("Cannot verify uncertain warehouse candidate ownership");matches.add(candidate);}
            }
          }
          if(seen.size()>=total.longValue())break;if(added==0)throw new AssertionError("Uncertain warehouse lookup pagination did not advance");
        }
      }
      if(matches.size()>1)throw new AssertionError("Ambiguous uncertain warehouse create: "+name);
      if(matches.size()==1){Record found=matches.get(0);Map<String,Object> stored=read(found);if(stored==null)throw new AssertionError("Recovered warehouse document disappeared before tracking");
        String actualName=stored.get("notes") instanceof String note&&note.startsWith("QA-ST-")?note:name;track(endpoint,found.id(),actualName,stored);}
      uncertainDocuments.remove(attempt);journalAttempts();
    }
  }

  private void deleteRecord(Record record) {
    if(record.endpoint().equals(BRANCH_STORE_LINK)&&read(new Record("Stores",record.values().get("storeId"),record.name(),fields()))==null){
      // The owned store may already be gone; remove only its journaled membership ID.
      changeBranchStore(record,false);return;
    }
    Map<String,Object> identity=read(record);
    if(identity==null)return;
    if(!owns(record,identity))
      throw new AssertionError("Refusing cleanup: fixture ownership changed for "+record.id());
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
        if (!owns(record,existing)) {
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
    // Resolve lost create/update responses before any parent item or warehouse can be removed.
    recoverUncertainDocuments();
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
