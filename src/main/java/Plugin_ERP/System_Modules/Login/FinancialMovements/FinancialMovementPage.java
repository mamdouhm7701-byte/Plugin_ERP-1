package Plugin_ERP.System_Modules.Login.FinancialMovements;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.math.BigDecimal;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Shared interactions for financial forms; business expectations belong in tests. */
public class FinancialMovementPage extends Abstract {
  private final WebDriverWait ready;
  private String listUrl;
  private final Map<String,String> ownedCodes=new HashMap<>();
  public FinancialMovementPage(WebDriver driver) {
    super(driver);
    ready = new WebDriverWait(driver, Duration.ofSeconds(25));
    ready.ignoring(StaleElementReferenceException.class).ignoring(ElementClickInterceptedException.class);
  }
  @Step("فتح قائمة الحركة المالية: {route}")
  public void openList(Pages route) {
    listUrl=PropertyReader.getApplicationUrl()+route.getRoute();
    driver.get(listUrl);
    driver.navigate().refresh();
    ready.until(d -> d.findElements(By.cssSelector("button[data-action-type='new']"))
        .stream().anyMatch(WebElement::isDisplayed));
    dismissPopup();
  }
  @Step("فتح نموذج حركة مالية جديدة")
  public void openNew() {
    String before=driver.getCurrentUrl();
    ready.until(d -> {
      if(isFormVisible())return true;
      if(!d.getCurrentUrl().equals(before))return false;
      WebElement b=d.findElements(By.xpath("//button[@data-action-type='new' and normalize-space()='جديد']"))
          .stream().filter(WebElement::isDisplayed).findFirst().orElse(null);
      if(b==null)return false;
      b.click();return false;
    });
    ready.until(d -> d.findElements(By.cssSelector("input[name='value']"))
        .stream().anyMatch(WebElement::isDisplayed));
  }
  @Step("إدخال قيمة الحركة: {amount}")
  public void amount(String amount) {type(By.cssSelector("input[name='value']"),amount);}
  @Step("إدخال ملاحظات الحركة المملوكة للاختبار: {note}")
  public void notes(String note) {type(By.cssSelector("textarea[label-text='Notes'],textarea"),note);}
  public String notesValue() {return driver.findElement(By.cssSelector("textarea")).getDomProperty("value");}
  @Step("اختيار {name} من {label}")
  public void choose(String label,String name) {
    By input=By.xpath("//*[self::label or self::legend][normalize-space()='"+label+
        "']/following::input[contains(@class,'vs__search')][1]");
    chooseInput(input,name);
  }
  private void chooseInput(By locator,String name) {
    try { ready.until(d->{
      WebElement input=d.findElements(locator).stream().filter(WebElement::isDisplayed).findFirst().orElse(null);
      if(input==null||!input.isEnabled())return false;
      ((JavascriptExecutor)d).executeScript("arguments[0].scrollIntoView({block:'center'})",input);
      if(!name.equals(input.getDomProperty("value"))){
        input.click();input.sendKeys(Keys.CONTROL+"a");input.sendKeys(name);
      }
      String listId=input.getDomAttribute("aria-controls");
      By options=By.xpath("//ul[@id='"+listId+"']//li[not(@aria-disabled='true')]");
      List<WebElement> candidates=d.findElements(options).stream().filter(WebElement::isDisplayed)
          .filter(e->e.getText().trim().equals(name)).toList();
      if(candidates.isEmpty())candidates=d.findElements(options).stream().filter(WebElement::isDisplayed)
          .filter(e->e.getText().contains(name)).toList();
      if(candidates.isEmpty())return false;
      if(candidates.size()!=1)throw new AssertionError("Ambiguous owned dropdown option: "+name);
      candidates.get(0).click();return true;
    }); } catch(TimeoutException failure) {
      Object snapshot=((JavascriptExecutor)driver).executeScript("""
          const queue=[document.querySelector('#app')?.__vue__], result=[];
          while(queue.length){const vm=queue.shift();if(!vm)continue;
            if(Array.isArray(vm.defaultCustomers))result.push({
              branchId:vm.currentBranch?.id, customerCount:vm.defaultCustomers.length,
              matches:vm.defaultCustomers.filter(c=>[c.arabicName,c.englishName].includes(arguments[0])),
              paymentCount:vm.paymentMethods?.length, agentCount:vm.Agents?.length});
            queue.push(...(vm.$children||[]));}
          return JSON.stringify(result);
          """,name);
      io.qameta.allure.Allure.addAttachment("Financial dropdown lookup evidence",String.valueOf(snapshot));
      throw failure;
    }
  }
  @Step("اختيار طريقة الدفع المسجلة للاختبار")
  public void payment(String methodName) {
    choose("طريقة الدفع","اخري");
    // The dependent payment-method picker follows the payment-way picker.
    By input=By.xpath("//*[self::label or self::legend][normalize-space()='طريقة الدفع']/following::input[contains(@class,'vs__search')][2]");
    chooseInput(input,methodName);
  }
  @Step("اختيار طرف التحويل {side}: {type} / {name}")
  public void transferParty(int side,String type,String name) {
    selectTransferType(side,type);
    By input=By.xpath("(//div[@role='radiogroup'])["+side+"]/following::input[contains(@class,'vs__search')][1]");
    chooseInput(input,name);
  }
  public void selectTransferType(int side,String type){
    String label=switch(type){case "Safe"->"خزينة";case "Bank"->"البنك";case "Customer"->"العميل";case "Supplier"->"المورد";default->throw new IllegalArgumentException(type);};
    By radio=By.xpath("(//div[@role='radiogroup'])["+side+"]//span[normalize-space()='"+label+"']");
    click(radio);
  }
  public void movementType(String value){
    By locator=By.cssSelector("input[type='radio'][value='"+value+"']");
    WebElement input=driver.findElement(locator);
    if(!input.isSelected())click(By.xpath("//input[@type='radio' and @value='"+value+"']/ancestor::label[1]"));
    ready.until(d->d.findElement(locator).isSelected());
  }
  public void clearField(String label,int index){
    By input=By.xpath("//*[self::label or self::legend][normalize-space()='"+label+"']/following::input[contains(@class,'vs__search')]["+index+"]");
    WebElement e=ready.until(ExpectedConditions.elementToBeClickable(input));
    WebElement picker=e.findElement(By.xpath("ancestor::div[contains(concat(' ',normalize-space(@class),' '),' v-select ')][1]"));
    List<WebElement> clear=picker.findElements(By.cssSelector("button.vs__clear"));
    if(clear.stream().anyMatch(WebElement::isDisplayed))clear.stream().filter(WebElement::isDisplayed).findFirst().orElseThrow().click();
    else {e.click();e.sendKeys(Keys.BACK_SPACE);e.sendKeys(Keys.ESCAPE);}
  }
  public void clearTransferDestination(){
    WebElement input=driver.findElement(By.xpath("(//div[@role='radiogroup'])[2]/following::input[contains(@class,'vs__search')][1]"));
    input.click();input.sendKeys(Keys.BACK_SPACE);input.sendKeys(Keys.ESCAPE);
  }
  public void outsideFiscalYear(){
    String date=(String)((JavascriptExecutor)driver).executeScript("""
      const root=document.querySelector('#app').__vue__, q=[root];
      while(q.length){const vm=q.shift();q.push(...(vm.$children||[]));if(vm.currentYear?.startDate){
        return String(vm.currentYear.startDate).slice(0,10);}}
      throw new Error('Active fiscal year not available');
      """);
    WebElement input=driver.findElement(By.cssSelector("input[placeholder='YYYY-MM-DD']"));
    java.time.LocalDate previous=java.time.LocalDate.parse(date).minusDays(1);
    String current=input.getDomProperty("value").replaceAll("[\\p{Cf}]","");
    String separator=current.contains("/")?"/":"-";
    String pattern=current.split("[/\\-]")[0].length()==4?"yyyy"+separator+"MM"+separator+"dd":"dd"+separator+"MM"+separator+"yyyy";
    type(By.cssSelector("input[placeholder='YYYY-MM-DD']"),previous.format(java.time.format.DateTimeFormatter.ofPattern(pattern)));
  }
  public BigDecimal[] taxValues(){
    WebElement net=driver.findElement(By.cssSelector("input[name='netBeforeTax'],input[name='netValue']"));
    WebElement tax=driver.findElement(By.cssSelector("input[name='tax']"));
    WebElement total=driver.findElement(By.cssSelector("input[name='value']"));
    return new BigDecimal[]{new BigDecimal(net.getDomProperty("value")),new BigDecimal(tax.getDomProperty("value")),new BigDecimal(total.getDomProperty("value"))};
  }
  public boolean validationFor(String field,String label){
    String text=String.join(" ",validationMessages());
    return switch(field){
      case "value","negative" -> text.contains("القيمة")||text.contains("قيمة")||text.contains("value");
      case "party" -> text.contains(label)||text.contains("اختيار");
      case "payment","paymentOption" -> text.contains("الدفع")||text.contains("البنك");
      case "date" -> text.contains("سنة")||text.contains("السنة")||text.contains("عام")||text.contains("تاريخ");
      case "sameParty" -> text.contains("نفس");
      case "toParty" -> text.contains("مطلوب")||text.contains("اختيار");
      default -> false;
    };
  }
  public void taxable(boolean value){
    By checkbox=By.cssSelector("input[name='isTaxApplied']");
    WebElement input=driver.findElement(checkbox);
    if(input.isSelected()!=value){
      utilites.waits().waitForOverlayToDisappear();
      // Bootstrap draws this empty label with a pseudo-element; its native input is hidden.
      ((JavascriptExecutor)driver).executeScript("arguments[0].click()",input);
    }
    ready.until(d->d.findElement(checkbox).isSelected()==value);
  }
  @Step("حفظ الحركة المالية")
  public void saveMovement(){click(By.cssSelector("button[data-action-type='save']"));}
  @Step("حفظ وطباعة الحركة المالية")
  public void saveAndPrintMovement(){click(By.cssSelector("button[data-action-type='saveAndPrint']"));}
  public void awaitReport(){utilites.waits().waitForReportReady(60);utilites.waits().waitForOverlayToDisappear();}
  public void attachReportEvidence(){
    io.qameta.allure.Allure.addAttachment("Financial report failure","image/png",
        new java.io.ByteArrayInputStream(((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES)),"png");
    io.qameta.allure.Allure.addAttachment("Financial report text",driver.findElement(By.tagName("body")).getText());
  }
  @Step("تخصيص المبلغ لفاتورة الاختبار فقط: {invoiceCode}")
  public void allocate(String invoiceCode,String amount){
    click(By.xpath("//button[translate(normalize-space(.),' ','')='حفظوتخصيص']"));
    WebElement row=ready.until(d->d.findElements(By.cssSelector(".modal.show tbody tr"))
        .stream().filter(WebElement::isDisplayed)
        .filter(r->r.findElements(By.cssSelector("td")).stream().anyMatch(c->c.getText().trim().equals(invoiceCode)))
        .findFirst().orElse(null));
    WebElement value=row.findElement(By.cssSelector("input[type='number']"));
    ready.until(d->{
      value.click();value.sendKeys(Keys.CONTROL+"a");value.sendKeys(amount);
      value.sendKeys(Keys.END);value.sendKeys(Keys.TAB);
      try{return new java.math.BigDecimal(value.getDomProperty("value"))
          .compareTo(new java.math.BigDecimal(amount))==0;}
      catch(NumberFormatException invalid){return false;}
    });
    // Allocation is committed by the modal's own Save button.
    click(By.xpath("//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='حفظ']"));
  }
  public List<String> validationMessages(){
    return ready.until(d->d.findElements(By.cssSelector("small.text-danger,.invalid-feedback,.swal2-html-container,.swal2-title"))
        .stream().filter(WebElement::isDisplayed).map(WebElement::getText).filter(t->!t.isBlank()).toList());
  }
  public boolean isFormVisible(){return driver.findElements(By.cssSelector("input[name='value']")).stream().anyMatch(WebElement::isDisplayed);}
  public boolean isFormPresent(){return !driver.findElements(By.cssSelector("input[name='value']")).isEmpty();}
  public void identifyMovement(String note,Object code){if(code==null)throw new IllegalArgumentException("Saved movement has no code");ownedCodes.put(note,code.toString());}
  @Step("إغلاق الحركة دون حفظ")
  public void closeWithoutSaving(){dismissPopup();driver.get(listUrl);driver.navigate().refresh();ready.until(d->!isFormPresent());}
  public void dismissPopup(){
    ready.until(d->{
      if(d.findElements(By.cssSelector(".swal2-container")).stream().noneMatch(WebElement::isDisplayed))return true;
      for(WebElement b:d.findElements(By.cssSelector("button.swal2-confirm")))if(b.isDisplayed())b.click();
      return false;
    });
  }
  private WebElement row(String note){
    return ready.until(d->d.findElements(By.cssSelector("tbody tr"))
        .stream().filter(WebElement::isDisplayed)
        .filter(r->matchesOwnedRow(r,note))
        .findFirst().orElse(null));
  }
  private boolean matchesOwnedRow(WebElement row,String note){
    if(ownedCodes.containsKey(note)){
      List<WebElement> headers=driver.findElements(By.cssSelector("thead th"));
      for(int i=0;i<headers.size();i++)if(headers.get(i).getDomProperty("textContent").contains("الكود")){
        List<WebElement> cells=row.findElements(By.cssSelector("td"));
        return cells.size()>i&&cells.get(i).getDomProperty("textContent").trim().equals(ownedCodes.get(note));
      }
      throw new AssertionError("Financial list code column missing");
    }
    return row.findElements(By.cssSelector("td")).stream().anyMatch(c->c.getDomProperty("textContent").trim().equals(note));
  }
  public boolean hasRow(String note){return driver.findElements(By.cssSelector("tbody tr"))
      .stream().filter(WebElement::isDisplayed).anyMatch(r->matchesOwnedRow(r,note));}
  public void waitForRow(String note){row(note);}
  @Step("تعديل الحركة المحددة بملاحظاتها: {note}")
  public void editOwned(String note){clickOwnedAction(note,"edit");ready.until(d->isFormVisible());ready.until(d->notesValue().equals(note));}
  @Step("إلغاء التخصيص السابق للسند المملوك قبل تعديل قيمته وإعادة تخصيصه")
  public void clearAllocationForEdit(){
    By amount=By.cssSelector("input[name='value']");
    if(driver.findElement(amount).isEnabled()&&driver.findElement(amount).getDomAttribute("readonly")==null)return;
    WebElement previousDocument=driver.findElement(By.tagName("html"));
    String previousNote=notesValue();
    click(By.xpath("//button[normalize-space()='إلغاء التخصيص']"));
    // التطبيق يحفظ إلغاء التخصيص ثم يعيد تحميل الصفحة، فلا نعدل النموذج القديم.
    new WebDriverWait(driver,Duration.ofSeconds(60)).until(ExpectedConditions.stalenessOf(previousDocument));
    ready.until(d->isFormVisible());
    ready.until(d->previousNote.equals(notesValue()));
    ready.until(d->{
      for(WebElement button:d.findElements(By.cssSelector(".swal2-popup button.swal2-confirm")))
        if(button.isDisplayed()){button.click();return false;}
      return d.findElement(amount).isEnabled()&&d.findElement(amount).getDomAttribute("readonly")==null;
    });
  }
  @Step("طلب حذف الحركة المحددة فقط: {note}")
  public void requestDeleteOwned(String note){clickOwnedAction(note,"delete");ready.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".swal2-popup")));}
  private void clickOwnedAction(String note,String action){ready.until(d->{
    WebElement button=row(note).findElement(By.cssSelector("button[data-action-type='"+action+"']"));
    ((JavascriptExecutor)d).executeScript("arguments[0].scrollIntoView({block:'center'})",button);
    button.click();return true;
  });}
  public void confirmDelete(boolean confirm){click(By.cssSelector(confirm?"button.swal2-confirm":"button.swal2-cancel"));ready.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".swal2-container")));}
  public void waitForRemoval(String note){ready.until(d->!hasRow(note));}
  public boolean hasListHeaders(){return driver.findElements(By.cssSelector("thead th")).stream().anyMatch(WebElement::isDisplayed);}
  public void waitForList(){ready.until(d->!isFormPresent());dismissPopup();ready.until(d->hasListHeaders());}
  public void export(String format){
    if(!List.of("pdf","excel").contains(format))throw new IllegalArgumentException(format);
    click(By.cssSelector("button:has(img[src*='"+(format.equals("pdf")?"pdf.":"xls.")+"'])"));
  }
  public void type(By locator,String value){
    ready.until(d->{
      WebElement e=d.findElements(locator).stream().filter(WebElement::isDisplayed)
          .filter(WebElement::isEnabled).filter(v->v.getDomAttribute("readonly")==null)
          .findFirst().orElse(null);
      if(e==null)return false;
      ((JavascriptExecutor)d).executeScript("arguments[0].scrollIntoView({block:'center'})",e);
      e.click();e.sendKeys(Keys.CONTROL+"a");
      if(value.isEmpty())e.sendKeys(Keys.DELETE);else e.sendKeys(value);
      e.sendKeys(Keys.TAB);
      String actual=e.getDomProperty("value");
      if(value.equals(actual))return true;
      if("number".equals(e.getDomAttribute("type"))||"value".equals(e.getDomAttribute("name")))try{
        return new BigDecimal(value).compareTo(new BigDecimal(actual))==0;
      }catch(NumberFormatException ignored){return false;}
      return false;
    });
  }
  private void click(By by){ready.until(d->{WebElement e=d.findElements(by).stream().filter(WebElement::isDisplayed).findFirst().orElse(null);if(e==null||!e.isEnabled())return false;((JavascriptExecutor)d).executeScript("arguments[0].scrollIntoView({block:'center'})",e);e.click();return true;});}
  @Step("تسجيل نتيجة الحفظ من طلب الواجهة الفعلي")
  public void captureSaves(String marker) {
    ((JavascriptExecutor)driver).executeScript("""
      const store=document.querySelector('#app').__vue__.$store;
      window.__financialQA={marker:arguments[0],saves:[],attempts:[],pending:0,errors:[]};
      if(store.__financialCaptureInstalled)return;
      store.__financialCaptureInstalled=true;
      const dispatch=store.dispatch.bind(store);
      store.dispatch=function(action,payload,...rest){
        const cap=window.__financialQA;
        const owned=(action==='app/create'||action==='app/update')&&payload?.data?.notes?.startsWith(cap?.marker);
        const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;
        if(owned){cap.pending++;cap.attempts.push({endpoint:payload.url,values:sent});}
        return dispatch(action,payload,...rest).then(result=>{
          if(owned){cap.pending--;const entity=result?.data||result; cap.saves.push({endpoint:payload.url,
            id:sent.id||entity?.id||(typeof entity==='string'?entity:null),values:sent});}
          return result;
        }).catch(error=>{if(owned){cap.pending--;cap.errors.push(String(error));}throw error;});
      };
      """,marker);
  }
  @SuppressWarnings("unchecked")
  public List<Map<String,Object>> capturedSaves(){return (List<Map<String,Object>>)((JavascriptExecutor)driver).executeScript("return window.__financialQA?.saves||[]");}
  public String captureMarker(){return (String)((JavascriptExecutor)driver).executeScript("return window.__financialQA?.marker||null");}
  @SuppressWarnings("unchecked") public List<Map<String,Object>> capturedAttempts(){return (List<Map<String,Object>>)((JavascriptExecutor)driver).executeScript("return window.__financialQA?.attempts||[]");}
  public boolean hasSaveStarted(){return Boolean.TRUE.equals(((JavascriptExecutor)driver).executeScript("return !!window.__financialQA && (window.__financialQA.pending>0 || window.__financialQA.saves.length>0)"));}
  public void awaitSavesSettled(){ready.until(d->!Boolean.TRUE.equals(((JavascriptExecutor)d).executeScript("return (window.__financialQA?.pending||0)>0")));}
}
