package FinancialMovementsTest;

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
  private static final class ApiFailure extends AssertionError {
    private final int status;
    ApiFailure(int status, String message) { super(message); this.status = status; }
  }
  record Record(String endpoint, Object id, String name, Map<String, Object> values) {}
  private final WebDriver driver;
  private final String suffix = UUID.randomUUID().toString().substring(0, 12);
  private final List<Record> created = new ArrayList<>();
  private final Map<String, Record> fixtures = new LinkedHashMap<>();

  FinancialTestData(WebDriver driver) {
    this.driver = driver;
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(d ->
        ((JavascriptExecutor) d).executeScript("return Boolean(document.querySelector('#app')"
            + "?.__vue__?.$store?.getters['app/currentBranch']?.id)"));
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
  Record customer(int index) {
    return fixture("customer-"+index,"Customers", fields("isActive",true,
        "cashSales",true,"creditSales",true,"returnSales",true,"isTaxable",true,
        "isOnTheFly",false,"isClosed",false,"salesReturn",true,"quotation",true,
        "salesOrder",true,"salesDiscount",true,"creditNote",true,"allowanceDays",0,
        "dealerType","Customer","isSameBranch",true,"startDealingDate",java.time.LocalDate.now().toString(),
        "accountId",account("customer-"+index).id()));
  }
  Record supplier(int index) {
    return fixture("supplier-"+index,"Suppliers", fields("isActive",true,
        "cashPurchase",true,"creditPurchase",true,"isTaxable",true,
        "isOnTheFly",false,"isClosed",false,"purchaseReturn",true,"purchaseDiscount",true,
        "purchaseOrder",true,"debitNote",true,"allowanceDays",0,
        "dealerType","Supplier","isSameBranch",true,"startDealingDate",java.time.LocalDate.now().toString(),
        "accountId",account("supplier-"+index).id()));
  }
  Record bank(int index) {return fixture("bank-"+index,"Banks",fields("accountId",account("bank-"+index).id()));}
  Record safe(int index) {return fixture("safe-"+index,"Safes",fields("accountId",account("safe-"+index).id()));}
  Record payment() {return fixture("payment","PaymentMethods",fields("bankId",bank(1).id()));}
  Record expense() {return fixture("expense","Expenses",fields("accountId",account("expense").id()));}
  Record income() {return fixture("income","Incomes",fields("accountId",account("income").id()));}
  Record discount() {return fixture("discount","DiscountTypes",fields("accountId",account("discount").id()));}
  Record agent(boolean purchase){return fixture(purchase?"agent-purch":"agent-sales","Agents",fields(
      "nature",purchase?"Purch":"Sales","isActive",true,"commissionSlices",List.of(),"itemCommissions",List.of()));}
  Record invoice(boolean purchase,java.math.BigDecimal allocation) {
    Record dealer=purchase?supplier(1):customer(1);
    Record store=fixture("invoice-store","Stores",fields("accountId",account("store").id()));
    Record unit=fixture("invoice-unit","Units",fields("conversionRates",List.of()));
    Record mainCategory=fixture("invoice-category-main","ItemCategories",fields("isMain",true));
    Record category=fixture("invoice-category","ItemCategories",fields("isMain",false,"parentCategoryId",mainCategory.id()));
    Record item=fixture("invoice-item","Items",fields("categoryId",category.id(),"mainUnitId",unit.id(),
        "mainUnitBarcode","FM"+suffix.replace("-",""),"type","RawMaterial","isStoreItem",true,
        "isTaxable",false,"itemTax",0,"salesPrice",100,"minSalesPrice",0,"maxSalesPrice",1000000,
        "sales",true,"purch",true,"returnSales",true,"returnPurch",true,"quotation",true,
        "salesOrder",true,"purchOrder",true,"storageTransfer",true,"additionOrder",true,
        "exchangeOrder",true,"damageAdjustment",true,"creditSales",true,
        "isScalesItem",false,"minSalesPrice",0,"subUnits",List.of(),"components",List.of(),
        "additions",List.of(),"offers",List.of(),"discounts",List.of(),
        "stores",List.of(fields("storeId",store.id(),"itemPlace","QA","openingBalance",0,"currentBalance",0,
            "minimumLimit",0,"reorderLimit",0,"costPrice",80,"isActive",true))));
    java.math.BigDecimal value=allocation.multiply(java.math.BigDecimal.valueOf(2));
    Map<String,Object> line=fields("lineSerial",1,"storeId",store.id(),"itemId",item.id(),"unitId",unit.id(),
        "unitConversionRate",1,"quantity",1,"price",value,"total",value,"net",value,"costPrice",0,
        "discountPercentage",0,"discountValue",0,"additions",List.of(),"attributes",List.of(),
        "attachmentDetails",List.of(),"roundedAdditions",0,"roundedNet",value);
    String date=java.time.LocalDate.now().toString();
    return fixture(purchase?"purchase-invoice":"sales-invoice",purchase?"Purchases":"ItemTransactions-Sales",fields(
        "transactionType",purchase?"Purch":"Sales","transactionChannel","Normal","transactionSource","Web",
        "transactionDate",date,"transactionTime","12:00:00","dueDate",date,"paymentType","Credit",
        "dealerId",dealer.id(),"storeId",store.id(),"isOffline",false,"isPosted",false,"isComplete",false,
        "paid",0,"total",value,"netBeforeTaxes",value,"net",value,"totalTaxes",0,
        "discountValue",0,"discountPercentage",0,"totalAdditionalTaxes",0,
        "lines",List.of(line),"taxes",List.of(),"payments",List.of(),"expenses",List.of(),
        "costCenters",List.of(),"extraProperties",List.of()));
  }
  Record party(String type,int index) {return switch(type){
    case "Safe" -> safe(index); case "Bank" -> bank(index);
    case "Customer" -> customer(index); case "Supplier" -> supplier(index);
    default -> throw new IllegalArgumentException(type);
  };}
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
    if (fixtures.containsKey(key)) return fixtures.get(key);
    String name = "QA-FM-" + key + "-" + suffix;
    values.put("arabicName", name);
    values.put("englishName", name);
    values.put("notes", endpoint.equals("Purchases")||endpoint.equals("ItemTransactions-Sales")
        ?name:"Owned Financial test fixture");
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
    fixtures.put(key, record);
    return record;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> call(String action, String endpoint, Map<String, Object> values) {
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
        const invoiceRead = action === 'get' && ['ItemTransactions-Sales','Purchases'].includes(endpoint);
        const args = {url: invoiceRead ? endpoint + '/' + values.id : endpoint};
        if (action === 'create') {
          if (!branchId) { done({error:'No current branch selected'}); return; }
          args.data = {...values, branchId};
        } else if (!invoiceRead) { args.id = values.id; }
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
    if (id == null) throw new AssertionError("Saved movement did not return an ID");
    for(int i=0;i<created.size();i++)if(created.get(i).endpoint().equals(endpoint)&&created.get(i).id().equals(id)){
      created.remove(i);created.add(new Record(endpoint,id,note,new LinkedHashMap<>(values)));journal();return;
    }
    created.add(new Record(endpoint,id,note,new LinkedHashMap<>(values)));
    journal();
  }
  private void journal(){
    List<Map<String,Object>> rows=created.stream().map(r->fields("endpoint",r.endpoint(),"id",r.id(),"name",r.name(),"values",r.values())).toList();
    try{
      java.nio.file.Path directory=java.nio.file.Path.of("target","financial-fixtures");
      java.nio.file.Files.createDirectories(directory);
      java.nio.file.Files.writeString(directory.resolve(suffix+".json"),new org.json.JSONArray(rows).toString(2));
    }catch(java.io.IOException e){throw new AssertionError("Cannot record owned financial fixture IDs",e);}
  }

  private static boolean retryable(ApiFailure failure) {
    return failure.status == 0 || failure.status == 408 || failure.status == 429 || failure.status >= 500;
  }

  private void deleteRecord(Record record) {
    AssertionError lastFailure = null;
    for (int attempt = 1; attempt <= 3; attempt++) {
      try {
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
      try { deleteRecord(record); }
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
