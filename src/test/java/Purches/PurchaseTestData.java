package Purches;

import Purches.PurchaseFixtureApi.ApiFailure;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Page-owned fixtures with isolated case namespaces, bound to the logged-in origin and branch. */
final class PurchaseTestData implements AutoCloseable {
  record Record(String endpoint, Object id, String name, Map<String, Object> values) {}
  private final WebDriver driver;
  private final PurchaseFixtureApi api;
  private final PurchaseFixtureDefinitions definitions = new PurchaseFixtureDefinitions(this);
  private final String origin;
  private final Object sessionBranchId;
  private final String suffix;
  private final List<Record> created = new ArrayList<>();
  private final Map<String, Record> fixtures = new LinkedHashMap<>();
  private static final String BRANCH_STORE_LINK="PurchaseBranchStoreMembership";
  private static final java.util.Set<String> MOVEMENTS=java.util.Set.of("Purchases","PurchaseOrders","ReturnPurchases");
  private static final java.util.Set<String> MASTERS=java.util.Set.of("Accounts","Units","ItemCategories","Stores","Items","Agents","Customers","Suppliers","ExchangeDestinations","Banks","Safes","PaymentMethods","PriceLists","DiscountTypes");
  private final List<Map<String,Object>> uncertainDocuments=new ArrayList<>();
  private String lastAttemptJournal="";
  private int scenarioSequence;
  private String scenarioScope="";

  /** Share page prerequisites, while every case keeps independent business entities. */
  void beginScenario(){awaitContext();recoverUncertainDocuments();scenarioScope="case-"+(++scenarioSequence)+"-";}
  private void awaitContext(){
    new WebDriverWait(driver,Duration.ofSeconds(30)).until(d->{
      Object activeOrigin=((JavascriptExecutor)d).executeScript("return location.origin");
      Object activeBranch=((JavascriptExecutor)d).executeScript("return document.querySelector('#app')?.__vue__?.$store?.getters['app/currentBranch']?.id");
      if(!origin.equals(activeOrigin)||(activeBranch!=null&&!java.util.Objects.equals(sessionBranchId,activeBranch)))throw new AssertionError("ERP origin or branch changed; refusing fixture access");
      return java.util.Objects.equals(sessionBranchId,activeBranch);
    });
  }
  private String fixtureKey(String key){
    boolean shared=key.startsWith("store-")||key.startsWith("unit-")||key.equals("category-main")
      ||key.startsWith("agent-")
      ||key.startsWith("account-main-store-")||key.startsWith("account-sub-store-");
    return shared?key:scenarioScope+key;
  }

  PurchaseTestData(WebDriver driver) {this(driver,UUID.randomUUID().toString().substring(0,12));}
  private PurchaseTestData(WebDriver driver,String suffix) {
    this.suffix=suffix;
    this.driver = driver;
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(d ->
        ((JavascriptExecutor) d).executeScript("return Boolean(document.querySelector('#app')"
            + "?.__vue__?.$store?.getters['app/currentBranch']?.id)"));
    Object currentOrigin=((JavascriptExecutor)driver).executeScript("return location.origin");
    origin=currentOrigin instanceof String value?value:"offline";
    sessionBranchId=((JavascriptExecutor)driver).executeScript("return document.querySelector('#app').__vue__.$store.getters['app/currentBranch'].id");
    api = new PurchaseFixtureApi(driver, origin, sessionBranchId, this::awaitContext);
  }

  /** Resume only an explicitly chosen journal, in its original tenant and branch. */
  @SuppressWarnings("unchecked") static PurchaseTestData resume(WebDriver driver,String suffix)throws java.io.IOException{
    if(!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))throw new IllegalArgumentException("Invalid sales journal ID");
    PurchaseTestData data=new PurchaseTestData(driver,suffix);
    for(String folder:List.of("purchase-fixtures","purchase-attempts")){
      var path=java.nio.file.Path.of("target",folder,suffix+".json");if(!java.nio.file.Files.exists(path))continue;
      for(Object value:new org.json.JSONArray(java.nio.file.Files.readString(path)).toList()){
        Map<String,Object> row=(Map<String,Object>)value;
        if(!data.origin.equals(row.get("origin"))||!java.util.Objects.equals(data.sessionBranchId,row.get("branchId")))
          throw new AssertionError("Sales journal belongs to another origin or branch");
        String endpoint=(String)row.get("endpoint"),name=(String)row.get("name");
        if(name==null||!name.startsWith("QA-PU-")||endpoint==null||!(row.get("values") instanceof Map<?,?>))throw new AssertionError("Invalid sales journal record");
        if(folder.equals("purchase-fixtures"))data.created.add(new Record(endpoint,row.get("id"),name,(Map<String,Object>)row.get("values")));
        else data.uncertainDocuments.add(row);
      }
    }
    return data;
  }

  static Map<String, Object> fields(Object... pairs) {
    Map<String, Object> values = new LinkedHashMap<>();
    for (int i = 0; i < pairs.length; i += 2) values.put((String) pairs[i], pairs[i + 1]);
    return values;
  }

  Record account(String key) {return definitions.account(key);}
  Record unit(int index) {return definitions.unit(index);}
  Record mainCategory() {return definitions.mainCategory();}
  Record category() {return definitions.category();}
  Record store(int index){
        synchronized (TestSupport.BranchMutationLock.monitor()) {
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

  private void changeBranchStore(Record link,boolean add){
        synchronized (TestSupport.BranchMutationLock.monitor()) {
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
    }
  Map<String,Object> itemValues() {return definitions.itemValues();}
  Record item(int index) {return definitions.item(index);}
  Record agent() {return definitions.agent();}
  Record otherSalesAgent() {return definitions.otherSalesAgent();}
  Record agent(boolean purchase) {return definitions.agent(purchase);}
  Record destination() {return definitions.destination();}
  Record supplier() {return definitions.supplier();}
  Record customer() {return definitions.customer();}
  Record customer(int allowedDiscount) {return definitions.customer(allowedDiscount);}
  Object balance(Record item,int index){Map<String,Object> dto=read(item);for(Object value:(List<?>)dto.get("stores")){Map<?,?> row=(Map<?,?>)value;if(store(index).id().equals(row.get("storeId")))return row.get("currentBalance");}throw new AssertionError("Owned store balance missing");}

  private Map<String,Object> rememberSeedAttempt(String endpoint,String name,Map<String,Object> values){
    Map<String,Object> attempt=fields("origin",origin,"branchId",sessionBranchId,"fixture",true,"endpoint",endpoint,"name",name,"values",new LinkedHashMap<>(values));
    uncertainDocuments.add(attempt);journalAttempts();return attempt;
  }
  Record bank() {return definitions.bank();}
  Record payment() {return definitions.payment();}
  Record safe() {return definitions.safe();}
  Record discountType() {return definitions.discountType();}
  Record priceList() {return definitions.priceList();}
  Record seedPurchase(int index,String type,String date,String quantity,String paid){
    return seedPurchase(index,type,date,quantity,paid,agent());
  }
  Record seedPurchase(int index,String type,String date,String quantity,String paid,Record salesAgent){
    Record item=item(1),customer=supplier();java.math.BigDecimal qty=new java.math.BigDecimal(quantity),total=qty.multiply(new java.math.BigDecimal("100")),amount=new java.math.BigDecimal(paid);
    Map<String,Object> line=fields("lineSerial",1,"storeId",store(1).id(),"itemId",item.id(),"unitId",unit(1).id(),"unitConversionRate",1,"quantity",qty,
      "price",100,"total",total,"net",total,"costPrice",qty.multiply(new java.math.BigDecimal("80")),"discountPercentage",0,"discountValue",0,"additions",List.of(),"attributes",List.of(),"attachmentDetails",List.of(),"roundedAdditions",0,"roundedNet",total);
    List<?> payments=amount.signum()==0?List.of():List.of(fields("paymentMethod","Other","otherPaymentMethodId",payment().id(),"bankId",bank().id(),"value",amount,"cashPaid",0));
    return fixture("seed-"+index,type,fields("transactionType",switch(type){case "Purchases"->"Purch";case "PurchaseOrders"->"PurchOrder";case "ReturnPurchases"->"ReturnPurch";default->throw new AssertionError(type);},"transactionChannel","Normal","transactionSource","Web","transactionDate",date,"transactionTime","12:00:00",
      "dueDate",date,"referenceNumber","REF-"+scenarioScope+index+"-"+suffix,"paymentType",amount.signum()==0?"Credit":"Cash","dealerId",customer.id(),"agentId",salesAgent.id(),"storeId",store(1).id(),"isOffline",false,"isPosted",false,"isComplete",false,
      "paid",amount,"total",total,"netBeforeTaxes",total,"net",total,"totalTaxes",0,"discountValue",0,"discountPercentage",0,"totalAdditionalTaxes",0,
      "lines",List.of(line),"taxes",List.of(),"payments",payments,"expenses",List.of(),"costCenters",List.of(),"extraProperties",List.of()));
  }
  void deleteOwned(Record record){deleteRecord(record);}
  @SuppressWarnings("unchecked") List<Map<String,Object>> ownedSupplierReceipts(Object supplierId){
    if(created.stream().noneMatch(r->r.endpoint().equals("Suppliers")&&r.id().equals(supplierId)))throw new AssertionError("Supplier is not owned by this page");
    var found=new ArrayList<Map<String,Object>>();var seen=new java.util.HashSet<Object>();
    for(int page=1;;page++){
      Object entity=call("get","Collections?pageNumber="+page+"&pageSize=500&dealerId="+supplierId,fields()).get("entity");
      if(!(entity instanceof Map<?,?> dto)||!(dto.get("pageData") instanceof List<?> rows)||!(dto.get("totalRecords") instanceof Number total))throw new AssertionError("Cannot inspect supplier receipts");
      int added=0;for(Object value:rows){if(!(value instanceof Map<?,?> row)||row.get("id")==null)throw new AssertionError("Receipt list has no identity");if(!seen.add(row.get("id")))continue;added++;
        Map<String,Object> receipt=row.containsKey("dealerId")?(Map<String,Object>)row:read(new Record("Collections",row.get("id"),"QA-PU-read-only-receipt",fields()));
        if(receipt!=null&&supplierId.equals(receipt.get("dealerId")))found.add(receipt);
      }
      if(seen.size()>=total.longValue())return found;if(added==0)throw new AssertionError("Receipt pagination did not advance");
    }
  }
  java.util.Set<String> documentIdentities(){return created.stream().filter(r->MOVEMENTS.contains(r.endpoint())).map(r->r.endpoint()+"/"+r.id()).collect(java.util.stream.Collectors.toSet());}
  void verifyNoNewDocuments(java.util.Set<String> before){recoverUncertainDocuments();var added=new java.util.HashSet<>(documentIdentities());added.removeAll(before);
    if(!added.isEmpty())throw new AssertionError("Invalid sales document committed: "+added);}
  @SuppressWarnings("unchecked") Map<String,Object> lookup(String endpoint){return (Map<String,Object>)call("get",endpoint,fields()).get("entity");}
  @SuppressWarnings("unchecked") void recoverAttemptedItems(List<Map<String,Object>> attempts){
    for(Map<String,Object> attempt:attempts){
      if(!"Items".equals(attempt.get("endpoint")))continue;
      Map<String,Object> values=(Map<String,Object>)attempt.get("values");String name=java.util.stream.Stream.of("arabicName","englishName","notes")
          .map(values::get).filter(String.class::isInstance).map(String.class::cast).filter(v->v.startsWith("QA-PU-")).findFirst().orElse(null);
      if(name==null||!name.startsWith("QA-PU-")||created.stream().anyMatch(r->r.name().equals(name)))continue;
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
      if(failure.status==500&&(record.endpoint().equals("Customers")||record.endpoint().equals("Suppliers"))&&!existsInList(record))return null;
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
      Object entity=call("get",record.endpoint()+"?pageNumber="+page+"&pageSize=500&arabicNamePart="+java.net.URLEncoder.encode(record.name(),java.nio.charset.StandardCharsets.UTF_8),fields()).get("entity");
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

  Record fixture(String key, String endpoint, Map<String, Object> values) {
    key=fixtureKey(key);
    if (fixtures.containsKey(key)) return fixtures.get(key);
    String name = key.startsWith("QA-PU-") ? key : "QA-PU-" + key + "-" + suffix;
    values.put("arabicName", name);
    values.put("englishName", name);
    values.put("notes",MOVEMENTS.contains(endpoint)?name:"Owned Sales test fixture");
    Map<String,Object> attempt=MASTERS.contains(endpoint)?rememberFixtureAttempt(endpoint,name,values):MOVEMENTS.contains(endpoint)?rememberSeedAttempt(endpoint,name,values):null;
    Map<String, Object> response;
    try {response = createFixture(endpoint, values);}
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

  private Map<String,Object> createFixture(String endpoint,Map<String,Object> values){
    for(int attempt=1;;attempt++)try{return call("create",endpoint,values);}
    catch(ApiFailure failure){
      String message=failure.getMessage().toLowerCase(java.util.Locale.ROOT);
      // A SQL deadlock victim is explicitly rolled back; other failures may have committed.
      if(attempt>=3||failure.status!=500||!message.contains("deadlocked")||!message.contains("victim"))throw failure;
    }
  }

  private Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
    return api.call(action, endpoint, values);
  }

  void track(String endpoint, Object id, String note, Map<String,Object> values) {
    if(note==null||!note.startsWith("QA-PU-"))throw new AssertionError("Refusing unrelated sales record");
    if (id == null) throw new AssertionError("Saved movement did not return an ID");
    for(int i=0;i<created.size();i++)if(created.get(i).endpoint().equals(endpoint)&&created.get(i).id().equals(id)){
      created.remove(i);created.add(new Record(endpoint,id,note,new LinkedHashMap<>(values)));journal();return;
    }
    created.add(new Record(endpoint,id,note,new LinkedHashMap<>(values)));
    journal();
  }
  private void journal(){
    List<Map<String,Object>> rows=created.stream().map(r->fields("origin",origin,"branchId",sessionBranchId,"endpoint",r.endpoint(),"id",r.id(),"name",r.name(),"values",r.values())).toList();
    try{
      java.nio.file.Path directory=java.nio.file.Path.of("target","purchase-fixtures");
      java.nio.file.Files.createDirectories(directory);
      java.nio.file.Files.writeString(directory.resolve(suffix+".json"),new org.json.JSONArray(rows).toString(2));
    }catch(java.io.IOException e){throw new AssertionError("Cannot record owned store fixture IDs",e);}
  }

  private static boolean retryable(ApiFailure failure) {
    return failure.status == 0 || failure.status == 408 || failure.status == 429 || failure.status >= 500;
  }
  boolean owns(Record record,Map<String,Object> stored){
    if(!record.name().startsWith("QA-PU-"))return false;
    if(record.name().equals(stored.get("arabicName"))||record.name().equals(stored.get("englishName"))||record.name().equals(stored.get("notes")))return true;
    if(!MOVEMENTS.contains(record.endpoint())||!java.util.Objects.equals(record.values().get("storeId"),stored.get("storeId")))return false;
    if(created.stream().noneMatch(r->r.endpoint().equals("Stores")&&r.id().equals(stored.get("storeId"))&&r.name().startsWith("QA-PU-")))return false;
    if(!(stored.get("lines") instanceof List<?> lines)||lines.isEmpty()||!(record.values().get("lines") instanceof List<?> expected))return false;
    for(Object value:lines){if(!(value instanceof Map<?,?> line))return false;
      if(expected.stream().noneMatch(e->e instanceof Map<?,?> row&&java.util.Objects.equals(row.get("itemId"),line.get("itemId"))&&java.util.Objects.equals(row.get("unitId"),line.get("unitId"))))return false;
      if(created.stream().noneMatch(r->r.endpoint().equals("Items")&&r.id().equals(line.get("itemId"))&&r.name().startsWith("QA-PU-")))return false;
    }
    return true;
  }
  @SuppressWarnings("unchecked") void rememberAttempts(List<Map<String,Object>> attempts,List<Map<String,Object>> saves,String marker){
    if(!marker.startsWith("QA-PU-"))throw new AssertionError("Refuse unrelated sales attempt");
    List<Map<String,Object>> available=new ArrayList<>(saves);uncertainDocuments.removeIf(row->!Boolean.TRUE.equals(row.get("factory"))&&!Boolean.TRUE.equals(row.get("fixture"))&&row.get("name") instanceof String name&&name.startsWith(marker));
    for(var attempt:attempts){String endpoint=(String)attempt.get("endpoint");if(!MOVEMENTS.contains(endpoint))throw new AssertionError("Unexpected sales attempt endpoint");
      Map<String,Object> values=(Map<String,Object>)attempt.get("values");Object id=values.get("id");boolean successful=false;
      for(var saved:new ArrayList<>(available))if(endpoint.equals(saved.get("endpoint"))&&values.equals(saved.get("values"))){id=saved.get("id");successful=true;available.remove(saved);break;}
      final Object knownId=id;if(successful&&id!=null&&created.stream().anyMatch(r->r.endpoint().equals(endpoint)&&r.id().equals(knownId)))continue;
      String name=values.get("notes") instanceof String note&&note.startsWith(marker)?note:marker;
      uncertainDocuments.add(fields("origin",origin,"branchId",sessionBranchId,"endpoint",endpoint,"id",id,"name",name,"values",new LinkedHashMap<>(values)));
    }journalAttempts();
  }
  private void journalAttempts(){String json=new org.json.JSONArray(uncertainDocuments).toString(2);if(json.equals(lastAttemptJournal))return;
    try{var directory=java.nio.file.Path.of("target","purchase-attempts");java.nio.file.Files.createDirectories(directory);java.nio.file.Files.writeString(directory.resolve(suffix+".json"),json);lastAttemptJournal=json;}
    catch(java.io.IOException e){throw new AssertionError("Cannot journal uncertain sales request",e);}
  }
  Map<String,Object> rememberFixtureAttempt(String endpoint,String name,Map<String,Object> values){
    if(!MASTERS.contains(endpoint)||!name.startsWith("QA-PU-"))throw new AssertionError("Refuse unrelated fixture recovery");
    Map<String,Object> attempt=fields("origin",origin,"branchId",sessionBranchId,"factory",true,"fixture",true,"endpoint",endpoint,"name",name,"values",new LinkedHashMap<>(values));
    uncertainDocuments.add(attempt);journalAttempts();return attempt;
  }
  @SuppressWarnings("unchecked") private void recoverFixtureAttempt(Map<String,Object> attempt){
    String endpoint=(String)attempt.get("endpoint"),name=(String)attempt.get("name");Map<String,Object> values=(Map<String,Object>)attempt.get("values");
    if(!MASTERS.contains(endpoint)||!name.startsWith("QA-PU-"))throw new AssertionError("Invalid fixture recovery endpoint or name");
    List<Record> matches=new ArrayList<>();java.util.Set<Object> seen=new java.util.HashSet<>();
    for(int page=1;;page++){
      Object entity=call("get",endpoint.equals("Accounts")?"Aggregates/AccountTreeViewModel?showBalance=false&type=Main&branchId="+sessionBranchId:endpoint+"?pageNumber="+page+"&pageSize=500",fields()).get("entity");List<?> rows;long total;
      if(endpoint.equals("Accounts")){if(!(entity instanceof Map<?,?> tree)||(!(tree.get("Accounts") instanceof List<?>)&&!(tree.get("AccountsTree") instanceof List<?>)))throw new AssertionError("Cannot verify uncertain account through its tree");List<Map<String,Object>> found=new ArrayList<>();findOwned(entity,name,found);Map<Object,Map<String,Object>> unique=new LinkedHashMap<>();for(var row:found)unique.put(row.get("id"),row);rows=new ArrayList<>(unique.values());total=rows.size();}
      else if(entity instanceof List<?> list){rows=list;total=list.size();}
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
          if(!verified)throw new AssertionError("Cannot verify uncertain sales update ownership");matches.add(candidate);}
      }else{
        String list="PurchaseLists";
        String type=switch(endpoint){case "Purchases"->"Purch";case "PurchaseOrders"->"PurchOrder";case "ReturnPurchases"->"ReturnPurch";default->throw new AssertionError("Unknown purchase endpoint");};
        java.util.Set<Object> seen=new java.util.HashSet<>();
        for(int page=1;;page++){Object entity=call("get",list+"?pageNumber="+page+"&pageSize=500&transactionType="+type+"&dealerId="+values.get("dealerId"),fields()).get("entity");
          if(!(entity instanceof Map<?,?> result)||!(result.get("pageData") instanceof List<?> rows)||!(result.get("totalRecords") instanceof Number total))throw new AssertionError("Cannot verify uncertain sales create through its list");
          int added=0;for(Object value:rows){if(!(value instanceof Map<?,?> row)||row.get("id")==null)throw new AssertionError("Sales list has no record identity");if(!seen.add(row.get("id")))continue;added++;
            if(created.stream().anyMatch(r->r.endpoint().equals(endpoint)&&java.util.Objects.equals(r.id(),row.get("id"))))continue;
            if((!row.containsKey("notes")&&!row.containsKey("storeId"))||name.equals(row.get("notes"))||(java.util.Objects.equals(values.get("storeId"),row.get("storeId"))&&type.equalsIgnoreCase(String.valueOf(row.get("transactionType"))))){
              Record candidate=new Record(endpoint,row.get("id"),name,values);Map<String,Object> stored=read(candidate);
              if(stored!=null&&owns(candidate,stored))matches.add(candidate);
            }
          }
          if(seen.size()>=total.longValue())break;if(added==0)throw new AssertionError("Uncertain sales lookup pagination did not advance");
        }
      }
      if(matches.size()>1)throw new AssertionError("Ambiguous uncertain sales create: "+name);
      if(matches.size()==1){Record found=matches.get(0);Map<String,Object> stored=read(found);if(stored==null)throw new AssertionError("Recovered sales document disappeared before tracking");
        String actualName=stored.get("notes") instanceof String note&&note.startsWith("QA-PU-")?note:name;track(endpoint,found.id(),actualName,stored);}
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

  @Override public void close() {
    // Resolve lost create/update responses before any parent item or sales can be removed.
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
      try {deleteRecord(record);created.remove(index);fixtures.entrySet().removeIf(entry->entry.getValue().equals(record));journal();}
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
