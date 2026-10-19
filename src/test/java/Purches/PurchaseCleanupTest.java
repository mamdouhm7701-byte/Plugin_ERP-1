package Purches;

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
import io.qameta.allure.Story;

public class PurchaseCleanupTest {
  @Test @Story("فشل قراءة المورد لا يبرر حذف عميل موجود أو حساباته") public void customerLookupErrorCannotDeleteAnExistingCustomerOrItsAccounts(){
    Api api=new Api();var data=new PurchaseTestData(api.driver());var customer=data.supplier();
    api.records.put("existing-customer",new LinkedHashMap<>(Map.of("id","existing-customer","arabicName","Unrelated customer")));
    api.customerGetAlways500=true;Assert.expectThrows(AssertionError.class,data::close);Assert.assertTrue(api.deletes.isEmpty());Assert.assertTrue(api.records.containsKey(customer.id()));
    api.customerGetAlways500=false;api.customerGet500WhenMissing=true;data.close();Assert.assertEquals(api.records.keySet(),java.util.Set.of("existing-customer"));Assert.assertTrue(api.deletes.contains(customer.id()));
  }
  @Test @Story("استعادة حساب ضاعت استجابته من شجرة الحسابات دون حذف حسابات أخرى") public void recoversLostAccountFromItsTreeWithoutDeletingExistingAccounts(){
    Api api=new Api();var data=new PurchaseTestData(api.driver());String name="QA-PU-lost-account";
    var values=PurchaseTestData.fields("arabicName",name,"englishName",name,"code","90012345","type","Main");
    var stored=new LinkedHashMap<>(values);stored.put("id","lost-account");api.records.put("lost-account",stored);
    api.records.put("existing-account",new LinkedHashMap<>(Map.of("id","existing-account","arabicName","Existing business account")));
    data.rememberFixtureAttempt("Accounts",name,values);data.close();
    Assert.assertTrue(api.usedAccountTree);Assert.assertEquals(api.deletes,List.of("lost-account"));Assert.assertEquals(api.records.keySet(),java.util.Set.of("existing-account"));
  }
  @Test @Story("انتظار تحميل الفرع الأصلي قبل الوصول إلى بيانات الاختبار") public void waitsForOriginalBranchHydrationBeforeFixtureAccess(){
    Api api=new Api();var data=new PurchaseTestData(api.driver());api.branchUnavailableReads=2;
    var unit=data.unit(1);Assert.assertTrue(api.records.containsKey(unit.id()));Assert.assertEquals(api.branchUnavailableReads,0);data.close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("مشاركة بيانات الصفحة الثابتة مع عزل الأصناف والموردين والأرصدة بين الحالات") public void pageFixturesAreSharedButCaseBalancesStayIsolated(){
    Api api=new Api();var data=new PurchaseTestData(api.driver());data.beginScenario();
    var first=data.seedPurchase(1,"Purchases","2026-01-01","3","0");var firstItem=data.item(1);var firstCustomer=data.supplier();var sharedStore=data.store(1);var sharedUnit=data.unit(1);
    data.beginScenario();var second=data.seedPurchase(1,"Purchases","2026-01-01","1","0");var secondItem=data.item(1);var secondCustomer=data.supplier();
    Assert.assertNotEquals(first.id(),second.id());Assert.assertNotEquals(firstItem.id(),secondItem.id());Assert.assertNotEquals(firstCustomer.id(),secondCustomer.id());
    Assert.assertNotEquals(firstCustomer.values().get("accountId"),secondCustomer.values().get("accountId"));
    Assert.assertEquals(data.store(1).id(),sharedStore.id());Assert.assertEquals(data.unit(1).id(),sharedUnit.id());
    Assert.assertTrue(api.deletes.isEmpty(),"No cleanup is allowed between cases on the page");Assert.assertTrue(api.records.containsKey(first.id()));
    data.close();Assert.assertTrue(api.deletes.indexOf(first.id())<api.deletes.indexOf(sharedStore.id()));Assert.assertTrue(api.deletes.indexOf(second.id())<api.deletes.indexOf(sharedStore.id()));Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("الانتقال للحالة التالية يحتفظ بمحاولة الحفظ الفاشلة السابقة ويستعيدها") public void pageTransitionRetainsAndRecoversThePreviousFailedSave(){
    Api api=new Api();var data=new PurchaseTestData(api.driver());data.beginScenario();var item=data.item(1);String marker="QA-PU-case-a-lost";
    var values=PurchaseTestData.fields("notes",marker,"transactionType","Purchases","storeId",data.store(1).id(),"lines",List.of(Map.of("itemId",item.id(),"unitId",data.unit(1).id())));
    var stored=new LinkedHashMap<>(values);stored.put("id","previous-lost");api.records.put("previous-lost",stored);
    data.rememberAttempts(List.of(PurchaseTestData.fields("endpoint","Purchases","values",values)),List.of(),marker);
    data.rememberAttempts(List.of(),List.of(),"QA-PU-different-case");data.beginScenario();
    Assert.assertTrue(data.documentIdentities().contains("Purchases/previous-lost"));Assert.assertTrue(api.deletes.isEmpty());
    data.item(1);data.close();Assert.assertTrue(api.records.isEmpty());Assert.assertTrue(api.deletes.indexOf("previous-lost")<api.deletes.indexOf(item.id()));
  }
  @Test @Story("اكتشاف حفظ فعلي خلف استجابة فاشلة في اختبار رفض الحفظ") public void negativeSaveDetectsACommitBehindFailedResponse(){
    Api api=new Api();var data=new PurchaseTestData(api.driver());var item=data.item(1);var before=data.documentIdentities();String marker="QA-PU-invalid-partial-commit";
    var values=PurchaseTestData.fields("id","partial-invalid","notes",marker,"transactionType","Purchases","storeId",data.store(1).id(),"lines",List.of(Map.of("itemId",item.id(),"unitId",data.unit(1).id(),"quantity",0)));
    api.records.put("partial-invalid",new LinkedHashMap<>(values));var attempted=new LinkedHashMap<>(values);attempted.remove("id");
    data.rememberAttempts(List.of(PurchaseTestData.fields("endpoint","Purchases","values",attempted)),List.of(),marker);
    Assert.assertTrue(Assert.expectThrows(AssertionError.class,()->data.verifyNoNewDocuments(before)).getMessage().contains("Invalid sales document committed"));
    data.close();Assert.assertEquals(api.deletes.get(0),"partial-invalid");Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("استعادة المرتجع الثاني دون الخلط بينه وبين المرتجع الأول المعروف") public void recoversSecondReturnWithoutConfusingKnownFirstReturn(){
    Api api=new Api();var data=new PurchaseTestData(api.driver());var item=data.item(1);
    var common=PurchaseTestData.fields("transactionType","ReturnPurch","storeId",data.store(1).id(),"lines",List.of(Map.of("itemId",item.id(),"unitId",data.unit(1).id())));
    var first=data.fixture("known-return","ReturnPurchases",new LinkedHashMap<>(common));String marker="QA-PU-second-lost-return";
    var second=new LinkedHashMap<>(common);second.put("notes",marker);second.put("id","lost-second-return");api.records.put("lost-second-return",second);
    var attempt=new LinkedHashMap<>(second);attempt.remove("id");data.rememberAttempts(List.of(PurchaseTestData.fields("endpoint","ReturnPurchases","values",attempt)),List.of(),marker);
    data.close();Assert.assertTrue(api.deletes.indexOf("lost-second-return")<api.deletes.indexOf(first.id()));Assert.assertTrue(api.records.isEmpty());
  }
  @org.testng.annotations.DataProvider public Object[][] lostSalesKinds(){return new Object[][]{{"Purchases","Purch"},{"PurchaseOrders","PurchOrder"},{"ReturnPurchases","ReturnPurch"}};}
  @Test(dataProvider="lostSalesKinds") @Story("استعادة مستند محفوظ ضاعت استجابته لكل نوع من مستندات المشتريات") public void recoversLostResponseForEachSalesDocumentKind(String endpointType,String storedType){
    Api api=new Api();var data=new PurchaseTestData(api.driver());var item=data.item(1);String marker="QA-PU-lost-"+endpointType;
    var values=PurchaseTestData.fields("notes",marker,"transactionType",storedType,"storeId",data.store(1).id(),"lines",List.of(Map.of("itemId",item.id(),"unitId",data.unit(1).id())));
    api.records.put("lost-document",new LinkedHashMap<>(values));api.records.get("lost-document").put("id","lost-document");
    data.rememberAttempts(List.of(PurchaseTestData.fields("endpoint",endpointType,"values",values)),List.of(),marker);
    data.close();Assert.assertEquals(api.deletes.get(0),"lost-document");Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("رفض استعادة سجل التنظيف في تننت تختلف عن تننت إنشاء البيانات") public void refusesJournalRecoveryInAnotherTenant()throws Exception{rejectForeignContext(true);}
  @Test @Story("رفض استعادة سجل التنظيف في فرع مختلف") public void refusesJournalRecoveryInAnotherBranch()throws Exception{rejectForeignContext(false);}
  private void rejectForeignContext(boolean tenant)throws Exception{
    Api api=new Api();var data=new PurchaseTestData(api.driver());var unit=data.unit(1);String suffix=unit.name().substring(unit.name().length()-12);
    if(tenant)api.origin="https://different-tenant.invalid";else api.activeBranch="different-branch";
    Assert.expectThrows(AssertionError.class,()->PurchaseTestData.resume(api.driver(),suffix));Assert.assertTrue(api.deletes.isEmpty());Assert.assertTrue(api.records.containsKey(unit.id()));
    api.origin="offline";api.activeBranch="branch-test";data.close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("حذف المرتجع المرتبط قبل الفاتورة الأصلية") public void linkedReturnIsDeletedBeforeSourceInvoice(){
    Api api=new Api();var data=new PurchaseTestData(api.driver());
    var source=data.seedPurchase(1,"Purchases","2026-01-01","3","0");
    var values=PurchaseTestData.fields("notes","QA-PU-linked-return","sourceTransactionId",source.id(),"storeId",data.store(1).id(),"lines",source.values().get("lines"));
    api.records.put("owned-return",new LinkedHashMap<>(values));data.track("ReturnPurchases","owned-return","QA-PU-linked-return",values);
    data.close();Assert.assertTrue(api.deletes.indexOf("owned-return")<api.deletes.indexOf(source.id()));Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("حذف الفاتورة المدفوعة قبل وسيلة الدفع والبنك") public void paidInvoiceIsDeletedBeforePaymentMethodAndBank(){
    Api api=new Api();var data=new PurchaseTestData(api.driver());var source=data.seedPurchase(1,"Purchases","2026-01-01","3","300");var method=data.payment();var bank=data.bank();
    data.close();Assert.assertTrue(api.deletes.indexOf(source.id())<api.deletes.indexOf(method.id()));Assert.assertTrue(api.deletes.indexOf(method.id())<api.deletes.indexOf(bank.id()));Assert.assertTrue(api.records.isEmpty());
  }
  private static final class Api {
    String origin="offline",activeBranch="branch-test";
    int branchUnavailableReads;
    boolean usedAccountTree;
    boolean customerGetAlways500,customerGet500WhenMissing;
    final Map<String, Map<String, Object>> records = new LinkedHashMap<>();
    final List<String> deletes = new ArrayList<>();
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
    Map<String,Object> branch=new LinkedHashMap<>(Map.of("id","branch-test","defaultStoreId","existing-store",
        "phone","original","stores",new ArrayList<>(List.of(Map.of("storeId","existing-store")))));

    WebDriver driver() {
      Object timeouts = Proxy.newProxyInstance(getClass().getClassLoader(),
          new Class<?>[] {WebDriver.Timeouts.class}, (proxy, method, args) -> proxy);
      Object options = Proxy.newProxyInstance(getClass().getClassLoader(),
          new Class<?>[] {WebDriver.Options.class}, (proxy, method, args) -> timeouts);
      return (WebDriver) Proxy.newProxyInstance(getClass().getClassLoader(),
          new Class<?>[] {WebDriver.class, JavascriptExecutor.class}, (proxy, method, args) -> {
            if (method.getName().equals("executeScript")) {
              String script=(String)args[0];if(script.equals("return location.origin"))return origin;
              if(script.startsWith("return document.querySelector")){if(branchUnavailableReads>0){branchUnavailableReads--;return null;}return activeBranch;}
              return true;
            }
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
      if(endpoint.equals("Branches")){
        if(action.equals("get"))return Map.of("entity",new LinkedHashMap<>(branch));
        if(!action.equals("update"))throw new AssertionError("Never delete an existing branch");
        boolean unlink=((List<?>)values.get("stores")).size()<((List<?>)branch.get("stores")).size();
        if(unlink&&rejectBranchUnlink)return Map.of("error","Cannot unlink owned store","status",400);
        branch=new LinkedHashMap<>(values);
        if(unlink&&timeoutBranchUnlinkAfterCommit){timeoutBranchUnlinkAfterCommit=false;throw new TimeoutException("Lost unlink response after commit");}
        return Map.of("id","branch-test");
      }
      if (action.equals("create")) {
        String id = endpoint + "-" + (records.size() + 1);
        Map<String, Object> saved = new LinkedHashMap<>(values);
        saved.put("id", id);
        records.put(id, saved);
        if(endpoint.equals("Items")&&failAfterItemInsert)return Map.of("error","Failed after insert","status",500);
        if(endpoint.equals("Stores")&&failAfterStoreInsert)return Map.of("error","Lost store create response","status",500);
        return Map.of("id", id);
      }
      if(action.equals("get")&&endpoint.startsWith("Items/Summary"))return Map.of("entity",new ArrayList<>(records.values()));
      if(action.equals("get")&&endpoint.startsWith("Accounts?"))return Map.of("error","Unsupported account list","status",405);
      if(action.equals("get")&&endpoint.startsWith("Aggregates/AccountTreeViewModel?")){usedAccountTree=true;return Map.of("entity",Map.of("Accounts",new ArrayList<>(records.values()),"AccountsTree",new ArrayList<>(records.values())));}
      if(action.equals("get")&&endpoint.startsWith("PurchaseLists?")){
        if(failMovementLookup)return Map.of("error","Lookup unavailable","status",503);
        var rows=records.values().stream().filter(r->r.containsKey("lines")).toList();return Map.of("entity",Map.of("pageData",rows,"totalRecords",rows.size()));
      }
      if(action.equals("get")&&endpoint.contains("?")){
        if(failFixtureLookup)return Map.of("error","Fixture lookup unavailable","status",503);
        String namePart=endpoint.contains("arabicNamePart=")?java.net.URLDecoder.decode(endpoint.split("arabicNamePart=",2)[1].split("&",2)[0],java.nio.charset.StandardCharsets.UTF_8):null;
        var rows=records.values().stream().filter(r->r.containsKey("id")&&(r.containsKey("arabicName")||r.containsKey("englishName"))).filter(r->namePart==null||String.valueOf(r.get("arabicName")).contains(namePart)).toList();
        return Map.of("entity",Map.of("pageData",rows,"totalRecords",rows.size()));
      }
      String id = values.get("id").toString();
      if (action.equals("get")) {
        if(endpoint.equals("Suppliers")&&(customerGetAlways500||(customerGet500WhenMissing&&!records.containsKey(id))))return Map.of("error","Customer lookup failed","status",500);
        return records.containsKey(id) ? Map.of("entity", records.get(id))
            : defaultDeletedMasterDto ? Map.of("entity",Map.of("subUnits",List.of(),"type","RawMaterial"))
            : defaultDeletedDto ? Map.of("entity",Map.of("conversionRates",List.of()))
            : emptyDeletedResponse ? Map.of("entity", "")
            : Map.of("error", "Missing owned record", "status", 404);
      }
      deletes.add(id);
      if(endpoint.equals("Purchases")&&rejectMovement)return Map.of("error","Movement is linked","status",400);
      if(missingDeleteForExisting||(missingDelete400&&!records.containsKey(id)))return Map.of("error","Common.ObjectNotFound","status",400);
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

  @Test @Story("حذف الحركة المملوكة للاختبار قبل أصنافها ومخازنها") public void deletesOwnedMovementBeforeItsItemsAndStores(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());var item=data.item(1);var store=data.store(1);
    var movement=data.fixture("movement","Purchases",PurchaseTestData.fields("storeId",store.id(),"lines",List.of(Map.of("itemId",item.id()))));
    api.records.put("existing-movement",new LinkedHashMap<>(Map.of("id","existing-movement","notes","Business record")));
    data.close();Assert.assertTrue(api.deletes.indexOf(movement.id())<api.deletes.indexOf(item.id()));
    Assert.assertTrue(api.deletes.indexOf(item.id())<api.deletes.indexOf(store.id()));Assert.assertEquals(api.records.keySet(),java.util.Set.of("existing-movement"));
  }
  @Test @Story("استعادة مخزن حُفظ قبل فقد استجابة الإنشاء") public void recoversStoreCommittedBeforeLostCreateResponse(){
    Api api=new Api();api.failAfterStoreInsert=true;PurchaseTestData data=new PurchaseTestData(api.driver());
    Assert.expectThrows(AssertionError.class,()->data.store(1));
    Object storeId=api.records.keySet().stream().filter(k->k.startsWith("Stores-")).findFirst().orElseThrow();
    data.rememberAttempts(List.of(),List.of(),"QA-PU-no-ui-attempts");data.close();
    Assert.assertEquals(api.deletes.get(0),storeId);Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("فشل التأكد من المخزن يحتفظ بحساباته") public void uncertainStoreLookupFailurePreservesItsAccounts(){
    Api api=new Api();api.failAfterStoreInsert=true;PurchaseTestData data=new PurchaseTestData(api.driver());
    Assert.expectThrows(AssertionError.class,()->data.store(1));api.failFixtureLookup=true;
    Assert.expectThrows(AssertionError.class,data::close);Assert.assertTrue(api.deletes.isEmpty());Assert.assertEquals(api.records.size(),3);
    api.failFixtureLookup=false;data.close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("التحقق من استمرار ارتباط المخزن غير المؤكد بحسابه المملوك للاختبار") public void uncertainStoreMustStillReferenceItsOwnedAccount(){
    Api api=new Api();api.failAfterStoreInsert=true;PurchaseTestData data=new PurchaseTestData(api.driver());
    Assert.expectThrows(AssertionError.class,()->data.store(1));var stored=api.records.values().stream().filter(r->r.get("id").toString().startsWith("Stores-")).findFirst().orElseThrow();
    Object account=stored.put("accountId","another-account");Assert.expectThrows(AssertionError.class,data::close);Assert.assertTrue(api.deletes.isEmpty());
    stored.put("accountId",account);data.close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("استئناف إنشاء مخزن معلق من سجل التنظيف المحفوظ") public void resumesPendingStoreCreationFromDurableJournal()throws Exception{
    Api api=new Api();api.failAfterStoreInsert=true;WebDriver driver=api.driver();PurchaseTestData data=new PurchaseTestData(driver);
    Assert.expectThrows(AssertionError.class,()->data.store(1));String name=api.records.values().stream().filter(r->r.get("id").toString().startsWith("Stores-")).map(r->r.get("arabicName").toString()).findFirst().orElseThrow();
    PurchaseTestData.resume(driver,name.substring(name.length()-12)).close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("الاحتفاظ بتبعيات المخزون عند فشل حذف الحركة") public void retainsStockDependenciesWhenMovementDeletionFails(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());var item=data.item(1);var store=data.store(1);
    data.fixture("movement","Purchases",PurchaseTestData.fields("storeId",store.id(),"lines",List.of(Map.of("itemId",item.id()))));
    api.rejectMovement=true;Assert.expectThrows(AssertionError.class,data::close);
    Assert.assertTrue(api.records.containsKey(item.id()));Assert.assertTrue(api.records.containsKey(store.id()));
    api.rejectMovement=false;data.close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("رفض تسجيل سجل غير مملوك للاختبار ضمن بيانات التنظيف") public void refusesTrackingUnownedRecord(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());
    Assert.expectThrows(AssertionError.class,()->data.track("Items","business-item","Business record",Map.of()));data.close();Assert.assertTrue(api.deletes.isEmpty());
  }
  @Test @Story("التحقق من هوية البيانات قبل أول عملية حذف") public void checksIdentityBeforeFirstDeletion(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());var unit=data.unit(1);
    api.records.put(unit.id().toString(),new LinkedHashMap<>(Map.of("id",unit.id(),"arabicName","Different business record")));
    Assert.expectThrows(AssertionError.class,data::close);Assert.assertTrue(api.deletes.isEmpty());
    api.records.put(unit.id().toString(),new LinkedHashMap<>(Map.of("id",unit.id(),"arabicName",unit.name())));data.close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("تنظيف مستند بلا ملاحظات بعد التحقق من ملكية مخزنه وأصنافه") public void cleansSalesDocumentWithoutNotesUsingOwnedStoreAndItems(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());var item=data.item(1);var store=data.store(1);
    Map<String,Object> values=PurchaseTestData.fields("id","owned-salesDocument","notes","","storeId",store.id(),"lines",List.of(Map.of("itemId",item.id(),"unitId",data.unit(1).id())));
    api.records.put("owned-salesDocument",new LinkedHashMap<>(values));data.track("Purchases","owned-salesDocument","QA-PU-salesDocument",values);
    data.close();Assert.assertEquals(api.deletes.get(0),"owned-salesDocument");Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("رفض حذف مستند تغيرت هوية مخزنه") public void rejectsSalesDocumentWithChangedStoreIdentity(){salesDocumentIdentityMismatch(true);}
  @Test @Story("رفض حذف مستند تغيرت هوية صنفه") public void rejectsSalesDocumentWithChangedItemIdentity(){salesDocumentIdentityMismatch(false);}
  private void salesDocumentIdentityMismatch(boolean storeChanged){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());var item=data.item(1);var store=data.store(1);
    Map<String,Object> values=PurchaseTestData.fields("id","owned-salesDocument","notes","","storeId",store.id(),"lines",List.of(Map.of("itemId",item.id(),"unitId",data.unit(1).id())));
    data.track("Purchases","owned-salesDocument","QA-PU-salesDocument",values);Map<String,Object> changed=new LinkedHashMap<>(values);
    if(storeChanged)changed.put("storeId","business-store");else changed.put("lines",List.of(Map.of("itemId","business-item","unitId",data.unit(1).id())));
    api.records.put("owned-salesDocument",changed);Assert.expectThrows(AssertionError.class,data::close);Assert.assertFalse(api.deletes.contains("owned-salesDocument"));
    Assert.assertTrue(api.records.containsKey(item.id()));api.records.put("owned-salesDocument",values);data.close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("استعادة إذن مخزني حُفظ قبل فقد استجابة الإنشاء") public void recoversWarehouseCreateCommittedBeforeLostResponse(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());var item=data.item(1);var store=data.store(1);String marker="QA-PU-lost-create";
    Map<String,Object> values=PurchaseTestData.fields("notes",marker,"storeId",store.id(),"transactionType","Purchases","lines",List.of(Map.of("itemId",item.id(),"unitId",data.unit(1).id())));
    Map<String,Object> stored=new LinkedHashMap<>(values);stored.put("id","lost-create");api.records.put("lost-create",stored);
    data.rememberAttempts(List.of(PurchaseTestData.fields("endpoint","Purchases","values",values)),List.of(),marker);
    data.close();Assert.assertEquals(api.deletes.get(0),"lost-create");Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("فشل البحث عن إذن مخزني يحتفظ بجميع تبعياته") public void uncertainWarehouseLookupFailureRetainsAllParents(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());var item=data.item(1);var store=data.store(1);
    Map<String,Object> values=PurchaseTestData.fields("notes","QA-PU-unresolved","storeId",store.id(),"lines",List.of(Map.of("itemId",item.id(),"unitId",data.unit(1).id())));
    data.rememberAttempts(List.of(PurchaseTestData.fields("endpoint","Purchases","values",values)),List.of(),"QA-PU-unresolved");
    api.failMovementLookup=true;Assert.expectThrows(AssertionError.class,data::close);Assert.assertTrue(api.deletes.isEmpty());Assert.assertTrue(api.records.containsKey(item.id()));
    api.failMovementLookup=false;data.close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("استعادة الملاحظات المعدلة بعد فقد استجابة التعديل") public void recoversUpdatedNoteAfterLostUpdateResponse(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());var item=data.item(1);var store=data.store(1);String marker="QA-PU-lost-update";
    Map<String,Object> original=PurchaseTestData.fields("id","lost-update","notes",marker,"storeId",store.id(),"lines",List.of(Map.of("itemId",item.id(),"unitId",data.unit(1).id())));
    api.records.put("lost-update",new LinkedHashMap<>(original));data.track("Purchases","lost-update",marker,original);
    Map<String,Object> updated=new LinkedHashMap<>(original);updated.put("notes",marker+" EDIT");api.records.put("lost-update",updated);
    data.rememberAttempts(List.of(PurchaseTestData.fields("endpoint","Purchases","values",updated)),List.of(),marker);
    data.close();Assert.assertEquals(api.deletes.get(0),"lost-update");Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("قبول استجابة عدم وجود السجل عند الحذف بعد تأكيد غيابه بالقراءة") public void acceptsMissingDeleteOnlyAfterReadConfirmsAbsence() {
    Api api=new Api();api.missingDelete400=true;
    PurchaseTestData data=new PurchaseTestData(api.driver());
    PurchaseTestData.Record store=data.store(1);api.records.remove(store.id());
    data.close();Assert.assertTrue(api.records.isEmpty());
  }

  @Test @Story("رفض اعتبار السجل محذوفاً عندما تؤكد القراءة وجوده") public void rejectsMissingDeleteWhenOwnedRecordStillExists(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());
    PurchaseTestData.Record unit=data.unit(1);api.missingDeleteForExisting=true;
    Assert.expectThrows(AssertionError.class,data::close);Assert.assertTrue(api.records.containsKey(unit.id()));
    api.missingDeleteForExisting=false;data.close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("استعادة صنف أُنشئ من الواجهة رغم فشل الاستجابة") public void recoversUiItemCommittedBeforeFailedResponse(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());
    PurchaseTestData.Record unit=data.unit(1),category=data.category();
    String name="QA-PU-recover-"+java.util.UUID.randomUUID();
    Map<String,Object> values=PurchaseTestData.fields("arabicName",name,"mainUnitId",unit.id(),"categoryId",category.id());
    Map<String,Object> saved=new LinkedHashMap<>(values);saved.put("id","Items-ui");api.records.put("Items-ui",saved);
    api.records.put("shared-item",new LinkedHashMap<>(Map.of("id","shared-item","arabicName","Existing item")));
    data.recoverAttemptedItems(List.of(Map.of("endpoint","Items","values",values)));
    data.close();Assert.assertEquals(api.deletes.get(0),"Items-ui");Assert.assertEquals(api.records.keySet(),java.util.Set.of("shared-item"));
  }
  @Test @Story("استعادة صنف غير صالح بعلامة الاختبار الإنجليزية الفريدة") public void recoversInvalidItemByUniqueEnglishMarker(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());
    var unit=data.unit(1);var category=data.category();String marker="QA-PU-invalid-"+java.util.UUID.randomUUID();
    Map<String,Object> values=PurchaseTestData.fields("arabicName","","englishName",marker,"mainUnitId",unit.id(),"categoryId",category.id());
    Map<String,Object> saved=new LinkedHashMap<>(values);saved.put("id","Items-invalid");api.records.put("Items-invalid",saved);
    api.records.put("shared-empty",new LinkedHashMap<>(Map.of("id","shared-empty","arabicName","","englishName","Existing item")));
    data.recoverAttemptedItems(List.of(Map.of("endpoint","Items","values",values)));data.close();
    Assert.assertEquals(api.deletes.get(0),"Items-invalid");Assert.assertEquals(api.records.keySet(),java.util.Set.of("shared-empty"));
  }
  @Test @Story("إزالة ارتباط الفرع الخاص بالاختبار فقط مع حفظ التغييرات المتزامنة") public void removesOnlyOwnedBranchMembershipAndPreservesConcurrentChanges(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());var store=data.store(1);
    Assert.assertTrue(((List<?>)api.branch.get("stores")).stream().anyMatch(v->store.id().equals(((Map<?,?>)v).get("storeId"))));
    List<Object> concurrent=new ArrayList<>((List<?>)api.branch.get("stores"));concurrent.add(Map.of("storeId","another-user-store"));
    api.branch.put("stores",concurrent);api.branch.put("phone","changed-by-user");data.close();
    Assert.assertEquals(api.branch.get("phone"),"changed-by-user");Assert.assertEquals(api.branch.get("defaultStoreId"),"existing-store");
    Assert.assertEquals(((List<?>)api.branch.get("stores")).stream().map(v->((Map<?,?>)v).get("storeId")).collect(java.util.stream.Collectors.toSet()),java.util.Set.of("existing-store","another-user-store"));
    Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("الاحتفاظ بالمخزن وحساباته عند تعذر إزالة ارتباطه المملوك للاختبار") public void retainsStoreAndAccountsWhenOwnedMembershipCannotBeRemoved(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());var store=data.store(1);api.rejectBranchUnlink=true;
    Assert.expectThrows(AssertionError.class,data::close);Assert.assertTrue(api.deletes.isEmpty());Assert.assertTrue(api.records.containsKey(store.id()));
    api.rejectBranchUnlink=false;data.close();Assert.assertTrue(api.records.isEmpty());
  }
  @Test @Story("تأكيد فك ارتباط الفرع المنفذ بعد فقد الاستجابة") public void verifiesCommittedBranchUnlinkAfterLostResponse(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());data.store(1);api.timeoutBranchUnlinkAfterCommit=true;
    data.close();Assert.assertTrue(api.records.isEmpty());Assert.assertEquals(((List<?>)api.branch.get("stores")).size(),1);
  }

  @Test @Story("إعادة محاولة الحذف غير المنفذ قبل حذف الحسابات التابعة") public void retriesUncommittedDeleteBeforeParentAccounts() {
    Api api = new Api(); api.timeOutStore = true;
    PurchaseTestData data = new PurchaseTestData(api.driver());
    PurchaseTestData.Record store = data.store(1);
    Object accountId = store.values().get("accountId");
    data.close();
    Assert.assertTrue(api.records.isEmpty());
    Assert.assertEquals(api.deletes.subList(0, 2), List.of(store.id(), store.id()));
    Assert.assertEquals(api.deletes.get(2), accountId);
  }

  @Test @Story("عدم تكرار الحذف عندما نُفذ الطلب الذي انتهت مهلته") public void doesNotDeleteAgainWhenTimedOutRequestAlreadyCommitted() {
    Api api = new Api(); api.timeOutStore = true; api.commitBeforeTimeout = true;
    PurchaseTestData data = new PurchaseTestData(api.driver());
    PurchaseTestData.Record store = data.store(1);
    data.close();
    Assert.assertTrue(api.records.isEmpty());
    Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
  }

  @Test @Story("قبول قراءة نص فارغ بعد تأكيد تنفيذ الحذف المنتهية مهلته") public void acceptsEmptyStringReadAfterCommittedDeleteTimeout() {
    Api api = new Api(); api.timeOutStore = true; api.commitBeforeTimeout = true;
    api.emptyDeletedResponse = true;
    PurchaseTestData data = new PurchaseTestData(api.driver());
    PurchaseTestData.Record store = data.store(1);
    data.close();
    Assert.assertTrue(api.records.isEmpty());
    Assert.assertEquals(api.deletes.stream().filter(store.id()::equals).count(), 1L);
  }

  @Test @Story("الاحتفاظ بالتبعيات المعطلة وإعادة محاولة تنظيف سجلات الاختبار فقط") public void retainsBlockedParentsAndRetriesOnlyItsOwnRecords() {
    Api api = new Api(); api.rejectStore = true;
    api.records.put("shared-record", new LinkedHashMap<>(Map.of("arabicName", "Existing business data")));
    PurchaseTestData data = new PurchaseTestData(api.driver());
    PurchaseTestData.Record store = data.store(1);
    Assert.expectThrows(AssertionError.class, data::close);
    Assert.assertEquals(api.deletes, List.of(store.id()));
    Assert.assertEquals(api.records.size(), 4);
    api.rejectStore = false;
    data.close();
    Assert.assertEquals(api.records.keySet(), java.util.Set.of("shared-record"));
  }
  @Test @Story("تنظيف صنف الاختبار الذي حُفظ رغم فشل طلب الإنشاء") public void cleansOwnedItemCommittedBeforeCreateFailure(){
    Api api=new Api();api.failAfterItemInsert=true;
    api.records.put("shared-item",new LinkedHashMap<>(Map.of("id","shared-item","arabicName","Existing item")));
    PurchaseTestData data=new PurchaseTestData(api.driver());
    PurchaseTestData.Record unit=data.fixture("unit","Units",PurchaseTestData.fields());
    PurchaseTestData.Record category=data.fixture("category","ItemCategories",PurchaseTestData.fields());
    Assert.expectThrows(AssertionError.class,()->data.fixture("item","Items",PurchaseTestData.fields("mainUnitId",unit.id(),"categoryId",category.id())));
    Assert.assertEquals(api.records.size(),4);
    data.close();
    Assert.assertTrue(api.deletes.get(0).startsWith("Items-"));
    Assert.assertEquals(api.records.keySet(),java.util.Set.of("shared-item"));
  }
  @Test @Story("قبول نموذج حركة افتراضي بعد التحقق من غياب الحركة") public void acceptsVerifiedMissingMovementDefaultDto(){
    Api api=new Api();api.defaultDeletedDto=true;
    PurchaseTestData data=new PurchaseTestData(api.driver());
    PurchaseTestData.Record record=data.fixture("transaction","Purchases",PurchaseTestData.fields());
    data.close();
    Assert.assertTrue(api.records.isEmpty());Assert.assertEquals(api.deletes,List.of(record.id()));
  }
  @Test @Story("قبول نموذج بيانات أساسية افتراضي بعد التحقق من غياب السجل") public void acceptsVerifiedMissingMasterDefaultDto(){
    Api api=new Api();api.defaultDeletedMasterDto=true;
    PurchaseTestData data=new PurchaseTestData(api.driver());
    PurchaseTestData.Record record=data.fixture("item","Items",PurchaseTestData.fields());
    data.close();
    Assert.assertTrue(api.records.isEmpty());Assert.assertEquals(api.deletes,List.of(record.id()));
  }
  @Test @Story("حذف السجل المعدل قبل المرجع الجديد الذي أصبح مرتبطاً به") public void updatedRecordDeletesBeforeItsNewReference(){
    Api api=new Api();PurchaseTestData data=new PurchaseTestData(api.driver());
    PurchaseTestData.Record original=data.store(1);
    PurchaseTestData.Record movement=data.fixture("movement","Purchases",PurchaseTestData.fields("storeId",original.id()));
    PurchaseTestData.Record replacement=data.store(2);
    Map<String,Object> updated=new LinkedHashMap<>(movement.values());updated.put("storeId",replacement.id());
    api.records.put(movement.id().toString(),updated);data.track(movement.endpoint(),movement.id(),movement.name(),updated);
    data.close();
    Assert.assertEquals(api.deletes.get(0),movement.id());
    Assert.assertTrue(api.deletes.indexOf(movement.id())<api.deletes.indexOf(replacement.id()));
    Assert.assertTrue(api.records.isEmpty());
  }
}
