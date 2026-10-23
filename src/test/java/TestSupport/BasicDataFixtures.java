package TestSupport;

import TestSupport.BasicDataFixturesApi.ApiFailure;
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
public final class BasicDataFixtures implements AutoCloseable {

  public record Record(String endpoint, Object id, String name, Map<String, Object> values) {
    public Record { values=java.util.Collections.unmodifiableMap(new LinkedHashMap<>(values)); }
  }
  public static final java.util.Set<String> ALLOWED = java.util.Set.of("Accounts","Banks","Safes","PaymentMethods","Stores","Areas","ExchangeDestinations","Incomes","Expenses","DiscountTypes","AgentItinerariesStatus","Vouchers","SafeTransactions","users");
  private final java.util.Set<String> reservedNames = new java.util.HashSet<>();
  private final WebDriver driver;
  private final BasicDataFixturesApi api;
  private final BasicDataFixturesDefinitions definitions = new BasicDataFixturesDefinitions(this);
  private final String suffix;
  private final TestSupport.FixtureSession session;
  private final TestSupport.PendingFixtureWrites pendingWrites;
  private final List<Record> created = new ArrayList<>();
  private final Map<String, Record> fixtures = new LinkedHashMap<>();

  public BasicDataFixtures(WebDriver driver) {this(driver,UUID.randomUUID().toString().substring(0,12));}
  private BasicDataFixtures(WebDriver driver,String suffix) {
    this.suffix=suffix;
    this.driver = driver;
    this.session=new TestSupport.FixtureSession(driver);
    this.pendingWrites=new TestSupport.PendingFixtureWrites(session,"basic-data",suffix);
    this.api=new BasicDataFixturesApi(driver,session::verify);
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(d ->
        ((JavascriptExecutor) d).executeScript("return Boolean(document.querySelector('#app')"
            + "?.__vue__?.$store?.getters['app/currentBranch']?.id)"));
  }

  /** Closed and reviewed treasury history keeps its linked master records for audit. */
  public void preserveAuditHistory() {
    session.verify(); awaitUiSettled(); collectUi(); recoverPendingFixtures();
    var rows=created.stream().map(r->fields("origin",session.origin,"branchId",session.branchId,
        "endpoint",r.endpoint(),"id",r.id(),"name",r.name(),"values",r.values())).toList();
    TestSupport.FixtureJournals.write(java.nio.file.Path.of("target","treasury-audit-masters",suffix+".json"),rows);
    created.clear(); fixtures.clear(); retainedForRecovery=null; journal();
  }

  private String retainedForRecovery;
  /** A separately managed child failed cleanup; leave every prerequisite journaled. */
  public void retainForRecovery(String reason) {
    retainedForRecovery=java.util.Objects.requireNonNull(reason);
    journal();
  }
  private int scenarioSequence;
  private String scenarioScope="";
  /** Every method receives independent mutable records. */
  public void beginScenario(){session.verify();awaitUiSettled();collectUi();recoverPendingFixtures();scenarioScope="case-"+(++scenarioSequence)+"-";}
  private String fixtureKey(String key){return scenarioScope+key;}


  @SuppressWarnings("unchecked") public static BasicDataFixtures resume(WebDriver driver,String suffix)throws java.io.IOException{
    if(!suffix.matches("[a-f0-9]{8}-[a-f0-9]{3}"))throw new IllegalArgumentException("Invalid fixture journal ID");
    var data=new BasicDataFixtures(driver,suffix);var path=java.nio.file.Path.of("target","basic-data-fixtures",suffix+".json");
    if(java.nio.file.Files.exists(path))for(Object value:new org.json.JSONArray(java.nio.file.Files.readString(path)).toList()){
      var row=(Map<String,Object>)value;data.session.verify(row);
      String endpoint=(String)row.get("endpoint"),name=(String)row.get("name");
      if(name==null||(!name.startsWith("QA-BD-")||!name.endsWith("-"+suffix))||!ALLOWED.contains(endpoint)||row.get("id")==null||!(row.get("values") instanceof Map<?,?>))throw new AssertionError("Invalid owned fixture journal record");
      data.reservedNames.add(name);data.created.add(new Record(endpoint,row.get("id"),name,(Map<String,Object>)row.get("values")));
    }
    var attempts=java.nio.file.Path.of("target","basic-data-attempts",suffix+".json");
    if(java.nio.file.Files.exists(attempts))for(Object value:new org.json.JSONArray(java.nio.file.Files.readString(attempts)).toList()) {
      if(!(value instanceof Map<?,?> row)||!(row.get("name") instanceof String name)||!name.endsWith("-"+suffix))
        throw new AssertionError("Pending name belongs to another fixture run");
    }
    data.pendingWrites.resume(ALLOWED,"QA-BD-");return data;
  }

  public static Map<String, Object> fields(Object... pairs) {
    if(pairs.length%2!=0)throw new IllegalArgumentException("Expected field/value pairs");
    Map<String, Object> values = new LinkedHashMap<>();
    for (int i = 0; i < pairs.length; i += 2) values.put((String) pairs[i], pairs[i + 1]);
    return values;
  }

  public Record account(String key) {
    return definitions.account(key);
  }
  public Record bank(int index) { return definitions.bank(index); }
  public Record safe(int index) { return definitions.safe(index); }
  public Record user(String key) {
    Record saved=fixture("user-"+key,"users",fields("userName","qa_bd_"+UUID.randomUUID().toString().replace("-",""),
      "passwordHash",UUID.randomUUID().toString()+"aA1!","isAdmin",false,"isActive",true,"maxAllowedDiscount",0,
      "permissions",List.of(),"branches",List.of(fields("branchId",session.branchId)),"defaultOtherPaymentMethods",List.of(),"imageUrl","","phone","","email","","base64Image","","imageExtension",""));
    var stored=read(saved);var values=new LinkedHashMap<>(saved.values());
    if(stored!=null&&stored.get("code")!=null)values.put("code",stored.get("code"));
    return new Record(saved.endpoint(),saved.id(),saved.name(),values);
  }
  @SuppressWarnings("unchecked") Map<String,Object> lookup(String endpoint){return (Map<String,Object>)call("get",endpoint,fields()).get("entity");}

  @SuppressWarnings("unchecked")
  public Map<String, Object> read(Record record) {
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
    if(dto.get("id")==null&&dto.get("arabicName")==null&&dto.get("englishName")==null&&dto.get("notes")==null&&dto.get("fullName")==null
        &&!dto.containsKey("pageData")&&!dto.containsKey("totalRecords")
        &&!dto.containsKey("error")&&!dto.containsKey("message"))return null;
    return (Map<String, Object>) entity;
  }

  public Record fixture(String key, String endpoint, Map<String, Object> values) {
    requireEndpoint(endpoint);
    values=new LinkedHashMap<>(values);
    key=fixtureKey(key);
    if (fixtures.containsKey(key)) return fixtures.get(key);
    String name = "QA-BD-" + key + "-" + suffix;
    reservedNames.add(name);
    values.put("arabicName", name);
    values.put("englishName", name);
    values.put("notes",name);
    if(endpoint.equals("users"))values.put("fullName",name);
    Map<String,Object> persisted=new LinkedHashMap<>(values);persisted.remove("passwordHash");
    pendingWrites.start(endpoint,name,persisted);
    Map<String, Object> response;
    try {response = call("create", endpoint, values);}
    catch(ApiFailure|org.openqa.selenium.TimeoutException failure){
      // Preserve the pending journal even if the server committed before losing the response.
      throw failure;
    }
    Object id = response.get("id");
    if (id == null) throw new AssertionError("Fixture API did not return an ID: " + endpoint);
    Record record = new Record(endpoint, id, name, persisted);
    created.add(record);
    journal();
    fixtures.put(key, record);pendingWrites.committed(endpoint,name);
    return record;
  }

  private Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
    return api.call(action, endpoint, values);
  }

  public void track(String endpoint, Object id, String note, Map<String,Object> values) {
    requireEndpoint(endpoint);
    values=new LinkedHashMap<>(values);values.remove("passwordHash");
    if(note==null||!reservedNames.contains(note))throw new AssertionError("Refusing to track an unowned basic-data record");
    if (id == null) throw new AssertionError("Saved movement did not return an ID");
    for(int i=0;i<created.size();i++)if(created.get(i).endpoint().equals(endpoint)&&created.get(i).id().equals(id)){
      created.remove(i);created.add(new Record(endpoint,id,note,new LinkedHashMap<>(values)));journal();pendingWrites.committed(endpoint,note);return;
    }
    created.add(new Record(endpoint,id,note,new LinkedHashMap<>(values)));
    journal();pendingWrites.committed(endpoint,note);
  }
  private void recoverPendingFixtures(){pendingWrites.recover((endpoint,values)->call("get",endpoint,values),(endpoint,id,name,values)->{reservedNames.add(name); var full=new LinkedHashMap<String,Object>(values); Object stored=call("get",endpoint,fields("id",id)).get("entity"); if(stored instanceof Map<?,?> dto) for(var entry:dto.entrySet())full.put((String)entry.getKey(),entry.getValue()); full.remove("passwordHash"); track(endpoint,id,name,full);});}
  public String journalSuffix(){return suffix;}
  private void journal(){
    var rows=created.stream().map(r->fields("origin",session.origin,"branchId",session.branchId,"endpoint",r.endpoint(),"id",r.id(),"name",r.name(),"values",r.values())).toList();
    TestSupport.FixtureJournals.write(java.nio.file.Path.of("target","basic-data-fixtures",suffix+".json"),rows);
  }

  private static void requireEndpoint(String endpoint) {
    if (!ALLOWED.contains(endpoint)) throw new IllegalArgumentException("Unsupported owned fixture endpoint: " + endpoint);
  }
  public String reserveName(String endpoint, String label) {
    requireEndpoint(endpoint);
    String name="QA-BD-"+label.replaceAll("[^a-zA-Z0-9-]","-")+"-"+UUID.randomUUID().toString().substring(0,12)+"-"+suffix;
    reservedNames.add(name);
    pendingWrites.start(endpoint,name,fields());
    return name;
  }
  @SuppressWarnings("unchecked")
  public void collectUi() {
    Object raw=((JavascriptExecutor)driver).executeScript("return window.__basicDataQA?.saves||[]");
    if (!(raw instanceof List<?> rows)) return;
    for(Object value:rows) {
      var row=(Map<String,Object>)value; var values=(Map<String,Object>)row.get("values");
      String name=java.util.stream.Stream.of("arabicName","englishName","notes","fullName").map(values::get).filter(String.class::isInstance).map(String.class::cast).filter(reservedNames::contains).findFirst().orElse(null);
      if(name==null) throw new AssertionError("Unreserved basic data save");
      track((String)row.get("endpoint"),row.get("id"),name,values);
    }
  }
  public void installCapture() {
    ((JavascriptExecutor)driver).executeScript("""
      const store=document.querySelector('#app')?.__vue__?.$store;
      if(!store)return;
      const storageKey='qa-basic-data-'+arguments[1];
      if(window.__basicDataQA?.storageKey!==storageKey){
        const previous=sessionStorage.getItem(storageKey);
        window.__basicDataQA=previous?JSON.parse(previous):{saves:[],pending:0,storageKey};
      }
      if(store.__basicDataCaptureInstalled)return;
      store.__basicDataCaptureInstalled=true;
      const dispatch=store.dispatch.bind(store), allowed=new Set(arguments[0]);
      const persist=cap=>sessionStorage.setItem(cap.storageKey,JSON.stringify(cap));
      store.dispatch=function(action,payload,...rest){
        const data=payload?.data, names=window.__basicDataReservedNames||[],cap=window.__basicDataQA;
        const owned=['app/create','app/update'].includes(action)&&allowed.has(payload?.url)&&
          ['arabicName','englishName','notes','fullName'].some(key=>names.includes(data?.[key]));
        const sent=owned?JSON.parse(JSON.stringify(data)):null;
        if(owned){delete sent.passwordHash;cap.pending++;persist(cap);}
        let response;
        try{response=dispatch(action,payload,...rest);}
        catch(error){if(owned){cap.pending--;persist(cap);}throw error;}
        return Promise.resolve(response).then(result=>{
          if(owned){cap.pending--;const entity=result?.data||result;
            cap.saves.push({endpoint:payload.url,id:sent.id||entity?.id||
              (typeof entity==='string'||typeof entity==='number'?entity:null),values:sent});persist(cap);}
          return result;
        },error=>{if(owned){cap.pending--;persist(cap);}throw error;});
      };
      """,new ArrayList<>(ALLOWED),suffix);
    ((JavascriptExecutor)driver).executeScript("window.__basicDataReservedNames=arguments[0]",new ArrayList<>(reservedNames));
  }
  /** Reloaded pages retain unresolved request counters; never delete their prerequisites. */
  public void awaitUiSettled() {
    installCapture();
    new WebDriverWait(driver,Duration.ofSeconds(30)).until(d ->
      !Boolean.TRUE.equals(((JavascriptExecutor)d).executeScript("return (window.__basicDataQA?.pending||0)>0")));
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
        if(!record.name().equals(owned.get("arabicName"))&&!record.name().equals(owned.get("englishName"))&&!record.name().equals(owned.get("notes"))&&!record.name().equals(owned.get("fullName")))throw new AssertionError("Refusing cleanup: fixture ownership changed for "+record.id());
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
        if (!record.name().equals(existing.get("arabicName")) && !record.name().equals(existing.get("englishName")) && !record.name().equals(existing.get("notes")) && !record.name().equals(existing.get("fullName"))) {
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

  private Map<String,Object> cleanupIdentity(Record record){
    Map<String,Object> stored=read(record);
    if(stored!=null && !java.util.Objects.equals(record.id(),stored.get("id")))
      throw new AssertionError("Owned fixture identity changed");
    return stored;
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
    session.verify();
    if(retainedForRecovery!=null)throw new AssertionError("Prerequisites retained for child recovery: "+retainedForRecovery);
    awaitUiSettled();
    collectUi();
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
