package StoreTest;

import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.System_Modules.Login.StoreModel.*;
import io.qameta.allure.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

/** Isolated warehouse scenarios. Browser interactions belong to the StoreModel POM. */
public abstract class StoreScenarioTest extends BaseTest {
  protected abstract StoreMovementKind kind();
  protected abstract StoreModulePage movementPage();
  protected StoreTestData data;
  protected StoreModulePage page;
  protected StoreTestData.Record item;
  protected String marker,mainWindow;
  private final List<StoreTestData> pending=new ArrayList<>();
  private final Map<String,BigDecimal> initialBalances=new LinkedHashMap<>();
  private StoreTestData.Record selectedParty;
  @BeforeSuite(alwaysRun=true) public void awaitStoreSession(){movementPage().smAwaitSession();}
  @BeforeMethod(alwaysRun=true) public void beginStoreScenario(){
    marker="QA-ST-"+UUID.randomUUID().toString().substring(0,12);page=movementPage();mainWindow=driver.getWindowHandle();
    data=new StoreTestData(driver);pending.add(data);item=null;initialBalances.clear();selectedParty=null;
  }
  // Keep feature scenarios independent of the separately tested name/barcode lookup defects.
  protected String baselineMode(){return kind()==StoreMovementKind.EXCHANGE?"picker":"barcode";}
  protected String defaultParty(){return kind()==StoreMovementKind.EXCHANGE?"Customers":kind()==StoreMovementKind.ADDITION?"Suppliers":"Agents";}
  protected StoreTestData.Record ownItem(int index){return kind()==StoreMovementKind.MANUFACTURING?data.manufactured(index):data.item(index);}
  private StoreTestData.Record ownParty(String type){return switch(type){case "Customers"->data.customer();case "Suppliers"->data.supplier();case "Agents"->data.agent(kind()==StoreMovementKind.ADDITION);case "ExchangeDestinations"->data.destination();default->throw new IllegalArgumentException(type);};}
  private String partyLabel(String type){return switch(type){case "Customers"->"العملاء";case "Suppliers"->"الموردين";case "Agents"->"المندوبين";case "ExchangeDestinations"->"جهات الصرف";default->throw new IllegalArgumentException(type);};}
  protected void prepare(String omitted,String partyType){
    item=ownItem(1);data.store(2);
    if(kind().party())selectedParty=ownParty(partyType);
    snapshot(item);if(kind()==StoreMovementKind.MANUFACTURING)snapshot(data.item(2));
    page.smOpen(kind().route+"/new");page.smCapture(marker,data.store(1).id(),item.id());
    if(!"store".equals(omitted))page.smChoose(kind()==StoreMovementKind.TRANSFER?"من مخزن":"المخزن",data.store(1).name());
    else if(kind()!=StoreMovementKind.TRANSFER)page.smClearChoice("المخزن");
    if(kind()==StoreMovementKind.TRANSFER&&!"destination".equals(omitted))page.smChoose("الي مخزن",data.store(2).name());
    if(kind().party()&&!"type".equals(omitted)){
      page.smChoose("نوع البند",partyLabel(partyType));
      if(!"party".equals(omitted))page.smChoose(partyLabel(partyType),ownParty(partyType).name());
    }
    if(kind()!=StoreMovementKind.MANUFACTURING)page.smNotes(marker);
  }
  private void snapshot(StoreTestData.Record record){for(int index=1;index<=2;index++)initialBalances.put(record.id()+":"+index,number(data.balance(record,index)));}
  private BigDecimal initial(StoreTestData.Record record,int index){return initialBalances.get(record.id()+":"+index);}
  protected void fill(String mode){add(item,mode);page.smQuantity(item.name(),"3");}
  private void add(StoreTestData.Record record,String mode){switch(mode){case "barcode"->page.smAddBarcode(record.values().get("mainUnitBarcode").toString(),record.name());case "picker"->page.smAddPicker(record.name());default->page.smAddName(record.name());}}
  @SuppressWarnings("unchecked") protected List<StoreTestData.Record> capture(){List<StoreTestData.Record> records=new ArrayList<>();
    data.rememberAttempts(page.smAttempts(),page.smSaves(),marker);
    for(var saved:page.smSaves()){
      Assert.assertEquals(saved.get("endpoint"),kind().endpoint);Map<String,Object> values=(Map<String,Object>)saved.get("values");
      String note=kind()==StoreMovementKind.MANUFACTURING?marker:(String)values.get("notes");Assert.assertTrue(note!=null&&note.startsWith(marker),"Refuse unrelated warehouse document");
      data.track(kind().endpoint,saved.get("id"),note,values);records.add(new StoreTestData.Record(kind().endpoint,saved.get("id"),note,values));
    }data.rememberAttempts(page.smAttempts(),page.smSaves(),marker);return records;
  }
  protected StoreTestData.Record save(boolean print){int before=page.smSaves().size();page.smSave(print);
    try{new WebDriverWait(driver,Duration.ofSeconds(40)).until(d->{data.rememberAttempts(page.smAttempts(),page.smSaves(),marker);return page.smSaves().size()>before;});}catch(TimeoutException e){throw new AssertionError("Warehouse save failed: "+page.smSaveErrors(),e);}
    page.smSettled();var records=capture();var record=records.get(records.size()-1);var stored=data.read(record);
    Allure.addAttachment("Warehouse persisted entity","application/json",new org.json.JSONObject(StoreTestData.fields("sent",record.values(),"stored",stored)).toString(),".json");
    Assert.assertNotNull(stored);if(kind()!=StoreMovementKind.MANUFACTURING)Assert.assertEquals(stored.get("notes"),record.name());Assert.assertEquals(stored.get("storeId"),data.store(1).id());
    Assert.assertEquals(stored.get("transactionType").toString().toLowerCase(Locale.ROOT),kind().type.toLowerCase(Locale.ROOT));
    List<Map<String,Object>> lines=(List<Map<String,Object>>)stored.get("lines");Assert.assertFalse(lines.isEmpty());
    Assert.assertTrue(lines.stream().allMatch(l->l.get("itemId").equals(item.id())),"Unexpected item in owned document");
    Assert.assertTrue(lines.stream().allMatch(l->l.get("unitId").equals(data.unit(1).id())),"Unexpected unit in owned document");
    if(kind()==StoreMovementKind.TRANSFER)Assert.assertEquals(stored.get("destinationStoreId"),data.store(2).id());
    if(kind().party()){
      Assert.assertEquals(stored.get("selectedDestinationId"),selectedParty.id());
      Assert.assertEquals(stored.get("exchangeDestinationType"),record.values().get("exchangeDestinationType"));
    }
    if(print)verifyReport(record);else page.smDismiss();return record;
  }
  private void verifyReport(StoreTestData.Record record){new WebDriverWait(driver,Duration.ofSeconds(40)).until(d->d.getWindowHandles().size()>1);
    driver.switchTo().window(driver.getWindowHandles().stream().filter(h->!h.equals(mainWindow)).findFirst().orElseThrow());
    try{page.utilitesReportReady();page.AssertTextInReport(item.name());page.AssertNumberInReport("3");}
    catch(RuntimeException|AssertionError e){Allure.addAttachment("Warehouse report","image/png",new java.io.ByteArrayInputStream(((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES)),"png");throw e;}
    finally{driver.close();driver.switchTo().window(mainWindow);page.smDismiss();}
  }
  protected static BigDecimal number(Object n){Assert.assertNotNull(n);return new BigDecimal(n.toString());}
  protected void sameNumber(Object actual,String expected){Assert.assertTrue(number(actual).compareTo(new BigDecimal(expected))==0,"Expected "+expected+", actual "+actual);}
  private void balance(StoreTestData.Record record,int index,BigDecimal expected){new WebDriverWait(driver,Duration.ofSeconds(30)).until(d->number(data.balance(record,index)).compareTo(expected)==0);}
  protected void assertEffect(String quantity){BigDecimal qty=new BigDecimal(quantity);switch(kind()){
    case EXCHANGE,DAMAGE -> balance(item,1,initial(item,1).subtract(qty));
    case ADDITION -> balance(item,1,initial(item,1).add(qty));
    case TRANSFER -> {balance(item,1,initial(item,1).subtract(qty));balance(item,2,initial(item,2).add(qty));}
    case MANUFACTURING -> {balance(item,1,initial(item,1).add(qty));balance(data.item(2),1,initial(data.item(2),1).subtract(qty.multiply(BigDecimal.valueOf(2))));}
  }}
  protected StoreTestData.Record create(String mode,boolean print,String party){prepare(null,party);fill(mode);var record=save(print);assertEffect("3");return record;}
  protected void partyIdentity(String type){var record=create(baselineMode(),false,type);var stored=data.read(record);
    Object dealer=stored.get("dealerId"),agent=stored.get("agentId");
    if(type.equals("Customers")||type.equals("Suppliers")){if(hasId(dealer))Assert.assertEquals(dealer,selectedParty.id(),"Document contains a default dealer instead of its selected party");}
    else Assert.assertFalse(hasId(dealer),"Document contains an unexpected default dealer: "+dealer);
    if(type.equals("Agents")){if(hasId(agent))Assert.assertEquals(agent,selectedParty.id());}
    else Assert.assertFalse(hasId(agent),"Document contains an unexpected agent: "+agent);
  }
  private boolean hasId(Object id){return id!=null&&!id.toString().isBlank()&&!id.toString().equals("0")&&!id.toString().equals("00000000-0000-0000-0000-000000000000");}
  protected void editOwn(){var record=create(baselineMode(),false,defaultParty());page.smEdit(kind().route,record.id(),record.name(),item.name());page.smCapture(marker,data.store(1).id(),item.id());if(kind()!=StoreMovementKind.MANUFACTURING)page.smNotes(marker+" EDIT");page.smQuantity(item.name(),"5");
    var updated=save(false);Assert.assertEquals(updated.id(),record.id());sameNumber(((Map<?,?>)((List<?>)data.read(updated).get("lines")).get(0)).get("quantity"),"5");assertEffect("5");}
  private void find(StoreTestData.Record record){page.smFind(kind().list,record.name(),record.id(),data.read(record).get("code"));}
  protected void deleteOwn(){var record=create(baselineMode(),false,defaultParty());find(record);page.smDeleteOwn(record.name(),true);
    new WebDriverWait(driver,Duration.ofSeconds(30)).until(d->data.read(record)==null);balance(item,1,initial(item,1));balance(item,2,initial(item,2));
    if(kind()==StoreMovementKind.MANUFACTURING)balance(data.item(2),1,initial(data.item(2),1));}
  protected void required(String missing,String party,String expected){
    // The UI locks store selectors after adding a line; test the empty-store path before adding lines.
    boolean store=missing.equals("store")||missing.equals("destination");prepare(missing,party);if(!store)fill(baselineMode());
    String label=switch(missing){case "store"->kind()==StoreMovementKind.TRANSFER?"من مخزن":"المخزن";case "destination"->"الي مخزن";case "type"->"نوع البند";default->partyLabel(party);};
    page.smClearChoice(label);Assert.assertTrue(page.smChoiceEmpty(label),"Required field was not cleared: "+label);
    if(!page.smSaveDisabled()){page.smSave(false);String message=page.smValidation();Assert.assertTrue(message.replace('ة','ه').contains(expected.replace('ة','ه')),"Expected "+expected+": "+message);}
    page.smSettled();capture();Assert.assertTrue(page.smSaves().isEmpty(),"Required field was accepted");balance(item,1,initial(item,1));}
  protected void rejectQuantity(String quantity){prepare(null,defaultParty());fill(baselineMode());page.smQuantity(item.name(),quantity);if(!page.smSaveDisabled())page.smSave(false);
    String message=page.smValidation();page.smSettled();capture();Assert.assertTrue(page.smSaves().isEmpty(),"Invalid quantity was persisted: "+quantity);Assert.assertTrue(message.contains("كمي")||message.contains("رصيد"),message);balance(item,1,initial(item,1));}
  protected void noItems(){prepare(null,defaultParty());if(!page.smSaveDisabled()){page.smSave(false);String message=page.smValidation();Assert.assertTrue(message.contains("صنف")||message.contains("أصناف")||message.contains("اصناف"),message);}page.smSettled();capture();Assert.assertTrue(page.smSaves().isEmpty());}
  protected void oldDate(){prepare(null,defaultParty());fill(baselineMode());page.smDate(page.smCurrentDate().minusYears(1).toString());page.smSave(false);String message=page.smValidation();page.smSettled();capture();Assert.assertTrue(page.smSaves().isEmpty(),"Date outside active fiscal year was accepted");Assert.assertTrue(message.contains("تاريخ")||message.contains("فتر")||message.contains("سن"),message);}
  protected void pickerWithoutStore(){prepare("store",defaultParty());page.smButton("إضافة أصناف");Assert.assertTrue(page.smValidation().contains("مخزن"));Assert.assertTrue(page.smSaves().isEmpty());}
  protected void sameStore(){prepare("destination","Agents");page.smChoose("الي مخزن",data.store(1).name());Assert.assertTrue(page.smValidation().contains("مخزن"));Assert.assertTrue(page.smSaves().isEmpty());}
  protected void pickerWithoutDestination(){prepare("destination","Agents");page.smButton("إضافة أصناف");Assert.assertTrue(page.smValidation().contains("مخزن"));Assert.assertTrue(page.smSaves().isEmpty());}
  @Test(groups="stores") @Story("إلغاء إنشاء سند دون تغيير الرصيد") public void SM_CancelCreation(){prepare(null,defaultParty());fill(baselineMode());page.smOpen(kind().list);Assert.assertTrue(page.smSaves().isEmpty());balance(item,1,initial(item,1));}
  @Test(groups="stores") @Story("إلغاء تعديل السند يحافظ على بياناته ورصيده") public void SM_CancelEdit(){var record=create(baselineMode(),false,defaultParty());Object notes=data.read(record).get("notes");page.smEdit(kind().route,record.id(),record.name(),item.name());page.smCapture(marker,data.store(1).id(),item.id());page.smQuantity(item.name(),"8");if(kind()!=StoreMovementKind.MANUFACTURING)page.smNotes(marker+" UNSAVED");page.smOpen(kind().list);Assert.assertEquals(data.read(record).get("notes"),notes);assertEffect("3");}
  @Test(groups="stores") @Story("إلغاء حذف سند الاختبار") public void SM_CancelDelete(){var record=create(baselineMode(),false,defaultParty());find(record);page.smDeleteOwn(record.name(),false);Assert.assertNotNull(data.read(record));assertEffect("3");}
  @Test(groups="stores") @Story("المخزن مطلوب") public void SM_RequiredStore(){required("store",defaultParty(),"مخزن");}
  @Test(groups="stores") @Story("رفض الكمية الصفرية") public void SM_ZeroQuantity(){rejectQuantity("0");}
  @Test(groups="stores") @Story("رفض الكمية السالبة") public void SM_NegativeQuantity(){rejectQuantity("-1");}
  @Test(groups="stores") @Story("قبول كمية كسرية والتحقق من أثرها على الرصيد") public void SM_DecimalQuantity(){prepare(null,defaultParty());fill(baselineMode());page.smQuantity(item.name(),"1.25");var record=save(false);sameNumber(((Map<?,?>)((List<?>)data.read(record).get("lines")).get(0)).get("quantity"),"1.25");assertEffect("1.25");}
  @Test(groups="stores") @Story("كمية كبيرة صحيحة والوصول لحد الرصيد في حركات الخصم") public void SM_StockBalanceBoundary(){prepare(null,defaultParty());fill(baselineMode());String quantity=kind()==StoreMovementKind.MANUFACTURING?"500":"1000";page.smQuantity(item.name(),quantity);var record=save(false);sameNumber(((Map<?,?>)((List<?>)data.read(record).get("lines")).get(0)).get("quantity"),quantity);assertEffect(quantity);}
  @Test(groups="stores") @Story("حساب إجمالي التكلفة وسعر البيع حسب أعمدة الشاشة") public void SM_LineCalculations(){
    prepare(null,defaultParty());fill(baselineMode());Map<String,String> values=page.smLineValues(item.name());
    Allure.addAttachment("Displayed warehouse line values","application/json",new org.json.JSONObject(values).toString(),".json");
    BigDecimal quantity=number(values.get("الكمية"));int checked=0;
    String totalLabel=values.containsKey("الاجمالى")?"الاجمالى":"الإجمالي";
    if(values.containsKey("سعر التكلفة")&&values.containsKey(totalLabel)){
      sameNumber(number(values.get(totalLabel).replace(",","")),number(values.get("سعر التكلفة").replace(",","")).multiply(quantity).toPlainString());checked++;
    }
    if(values.containsKey("سعر البيع")&&values.containsKey("إجمالي سعر البيع")){
      sameNumber(number(values.get("إجمالي سعر البيع").replace(",","")),number(values.get("سعر البيع").replace(",","")).multiply(quantity).toPlainString());checked++;
    }
    Assert.assertTrue(checked>0,"No displayed price/total pair was checked: "+values);var record=save(false);assertEffect("3");
    Map<?,?> stored=(Map<?,?>)((List<?>)data.read(record).get("lines")).get(0);
    if(values.containsKey("سعر التكلفة"))sameNumber(stored.get("costPrice"),values.get("سعر التكلفة").replace(",",""));
    if(values.containsKey("سعر البيع"))sameNumber(stored.get("salesPrice"),values.get("سعر البيع").replace(",",""));
  }
  @Test(groups="stores") @Story("حذف سطر صنف مملوك للاختبار قبل الحفظ") public void SM_RemoveItem(){prepare(null,defaultParty());fill(baselineMode());var second=ownItem(2);add(second,baselineMode());page.smRemoveLine(second.name());var record=save(false);Assert.assertEquals(((List<?>)data.read(record).get("lines")).size(),1);assertEffect("3");}
  @Test(groups="stores") @Story("إضافة نفس الصنف لا تنشئ سطرين متطابقين") public void SM_DuplicateItem(){prepare(null,defaultParty());fill(baselineMode());add(item,baselineMode());page.smDismiss();Assert.assertEquals(page.smLineCount(item.name()),1);page.smQuantity(item.name(),"3");var record=save(false);Assert.assertEquals(((List<?>)data.read(record).get("lines")).size(),1);assertEffect("3");}
  protected void notesRoundTrip(){prepare(null,defaultParty());fill(baselineMode());page.smNotes(marker+" ملاحظات warehouse QA");var record=save(false);Assert.assertEquals(data.read(record).get("notes"),marker+" ملاحظات warehouse QA");assertEffect("3");}
  @Test(groups="stores") @Story("عرض هيكل قائمة سندات المخازن") public void SM_ListStructure(){var record=create(baselineMode(),false,defaultParty());find(record);Assert.assertTrue(page.smHeaders().size()>=3);}
  @Test(groups="stores") @Story("تصدير PDF فعلي لقائمة السندات") public void SM_ExportPdf(){export("pdf");}
  @Test(groups="stores") @Story("تصدير Excel فعلي لقائمة السندات") public void SM_ExportExcel(){export("excel");}
  private void export(String format){var record=create(baselineMode(),false,defaultParty());find(record);StoreExportAssertions.assertExportDownloaded(driver,()->page.smExport(format),format);}
  @Test(groups="stores") @Story("البحث عن سند الاختبار وتصفير نتيجة بحث غير موجود") public void SM_SearchAndReset(){var record=create(baselineMode(),false,defaultParty());find(record);page.smSearch(kind()==StoreMovementKind.DAMAGE?record.name()+"-NO-MATCH":"999999999999");new WebDriverWait(driver,Duration.ofSeconds(25)).until(d->!page.smHasDocument(record.name()));page.smSearch(kind()==StoreMovementKind.DAMAGE?record.name():data.read(record).get("code").toString());new WebDriverWait(driver,Duration.ofSeconds(25)).until(d->page.smHasDocument(record.name()));}
  @AfterMethod(alwaysRun=true) public void cleanStoreScenario(org.testng.ITestResult result){if(driver==null||page==null||data==null||pending.isEmpty())return;
    if(result!=null&&!result.isSuccess()){Allure.addAttachment("Warehouse failure DOM","text/html",page.smDiagnosticMarkup(),".html");Allure.addAttachment("Warehouse lookup diagnostics","application/json",page.smDiagnostics(),".json");}
    for(String window:new ArrayList<>(driver.getWindowHandles()))if(!window.equals(mainWindow)){driver.switchTo().window(window);driver.close();}driver.switchTo().window(mainWindow);
    List<String> failures=new ArrayList<>();if(pending.contains(data))try{page.smSettled();capture();}catch(AssertionError|RuntimeException e){failures.add(e.getMessage());}
    for(var context:new ArrayList<>(pending))try{context.close();pending.remove(context);}catch(AssertionError|RuntimeException e){failures.add(e.getMessage());}
    if(!failures.isEmpty())throw new AssertionError(String.join("; ",failures));
  }
  @AfterClass(alwaysRun=true) public void retryStoreCleanup(){cleanStoreScenario(null);}
}
