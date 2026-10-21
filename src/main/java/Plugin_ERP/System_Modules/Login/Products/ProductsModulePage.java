package Plugin_ERP.System_Modules.Login.Products;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import Plugin_ERP.Utiel.FormInteractions;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Product-module interactions shared by the four existing Page Objects. */
public class ProductsModulePage extends Abstract {
  protected final WebDriverWait ready;
  private final FormInteractions choices;
  private final FormInteractions inputs;
  private String listUrl;
  public ProductsModulePage(WebDriver driver){
    super(driver);ready=new WebDriverWait(driver,Duration.ofSeconds(30));
    ready.ignoring(StaleElementReferenceException.class).ignoring(ElementClickInterceptedException.class);
    choices=new FormInteractions(ready,e->((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'})",e));
    inputs=new FormInteractions(ready,e->{if(e.isEnabled())((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'})",e);});
  }
  public void prAwaitSession(){
    java.util.function.Function<WebDriver,Boolean> authenticated=d->!d.getCurrentUrl().contains("/login")&&Boolean.TRUE.equals(((JavascriptExecutor)d)
        .executeScript("return Boolean(document.querySelector('#app')?.__vue__?.$store?.getters['app/currentBranch']?.id)"));
    try{new WebDriverWait(driver,Duration.ofSeconds(45)).until(authenticated);}
    catch(TimeoutException first){
      if(!driver.getCurrentUrl().contains("/login"))throw first;
      // The shared login helper can click before Vue attaches the submit handler.
      new WebDriverWait(driver,Duration.ofSeconds(60)).ignoring(ElementClickInterceptedException.class).ignoring(StaleElementReferenceException.class).until(d->{
        if(authenticated.apply(d))return true;
        for(WebElement b:d.findElements(By.xpath("//button[contains(normalize-space(),'تسجيل الدخول')]")))
          if(b.isDisplayed()&&b.isEnabled()){b.click();return true;}return false;});
      new WebDriverWait(driver,Duration.ofSeconds(45)).until(authenticated);
    }
    }
  @Step("فتح قائمة موديول الأصناف: {route}")
  public void prOpen(Pages route){
    listUrl=PropertyReader.getApplicationUrl()+route.getRoute();driver.get(listUrl);driver.navigate().refresh();
    ready.until(d->d.findElements(By.cssSelector("button[data-action-type='new']")).stream().anyMatch(WebElement::isDisplayed));
    prDismiss();
  }
  @Step("فتح سجل جديد") public void prNew(){
    String before=driver.getCurrentUrl();
    ready.until(d->{if(prFormVisible())return true;if(!before.equals(d.getCurrentUrl()))return false;
      List<WebElement> buttons=d.findElements(By.xpath("//button[@data-action-type='new' and normalize-space()='جديد']"));
      for(WebElement b:buttons)if(b.isDisplayed()){b.click();break;}return false;});
  }
  public boolean prFormVisible(){return prNameInputs().stream().anyMatch(WebElement::isDisplayed);}
  public boolean prFormPresent(){return !prNameInputs().isEmpty();}
  private List<WebElement> prNameInputs(){return driver.findElements(By.cssSelector("form input[name='arabicName'],form [data-title='الاسم العربي'] input"));}
  protected WebElement visible(By locator){return ready.until(d->d.findElements(locator).stream().filter(WebElement::isDisplayed).findFirst().orElse(null));}
  public void prType(By locator,String value){inputs.type(locator,value,true,FormInteractions.InputMode.CLEAR_REPLACE);}
  protected void prClick(By locator){inputs.click(locator);}
  @Step("إدخال الاسم العربي: {name}") public void prArabic(String name){prType(By.cssSelector("form input[name='arabicName'],form [data-title='الاسم العربي'] input"),name);}
  public void prEnglish(String name){prType(By.cssSelector("form input[name='englishName'],form [data-title='الاسم الإنجليزي'] input"),name);}
  public void prNotes(String notes){prType(By.cssSelector("form textarea"),notes);}
  public void prField(String name,String value){prType(By.cssSelector("form input[name='"+name+"']"),value);}
  public void prTitledField(String title,String value){prType(By.cssSelector("form [data-title='"+title+"'] input"),value);}
  @Step("اختيار {name} من {label}") public void prChoose(String label,String name) {
    By locator=By.xpath("//*[self::label or self::legend][normalize-space()='"+label+"']/following::input[contains(@class,'vs__search')][1]");
    choices.choose(locator,name,FormInteractions.OptionMatch.EXACT,"Ambiguous test-owned option: ");
  }
  @Step("تغيير اختيار {label}: {selected}") public void prCheck(String label,boolean selected){
    By locator=By.xpath("//label[normalize-space()='"+label+"']/preceding-sibling::input[@type='checkbox'] | //label[normalize-space()='"+label+"']/following::input[@type='checkbox'][1]");
    WebElement input=driver.findElements(locator).stream().findFirst().orElseThrow(()->new AssertionError("Checkbox missing: "+label));
    if(input.isSelected()!=selected)((JavascriptExecutor)driver).executeScript("arguments[0].click()",input);
    ready.until(d->input.isSelected()==selected);
  }
  public void prTab(String text){prClick(By.xpath("//*[@role='tab' and normalize-space()='"+text+"']"));}
  public void prButton(String text){prClick(By.xpath("//button[normalize-space()='"+text+"']"));}
  @Step("حفظ السجل") public void prSave(){prClick(By.xpath("//form//button[@data-action-type='save' or normalize-space()='حفظ']"));}
  public void prSavePrint(){prButton("حفظ و طباعة");}
  public void prDismiss(){ready.until(d->{List<WebElement> overlays=d.findElements(By.cssSelector(".swal2-container"));
    if(overlays.stream().noneMatch(WebElement::isDisplayed))return true;
    for(WebElement b:d.findElements(By.cssSelector("button.swal2-confirm")))if(b.isDisplayed())b.click();return false;});}
  public void prWaitList(){ready.until(d->!prFormVisible());prDismiss();}
  @Step("إغلاق النموذج دون حفظ") public void prDiscard(){prDismiss();driver.get(listUrl);driver.navigate().refresh();ready.until(d->!prFormVisible());}
  public String prValidation(){return ready.until(d->{
    List<String> messages=new ArrayList<>();
    for(WebElement e:d.findElements(By.cssSelector("small.text-danger,.invalid-feedback,.swal2-title,.swal2-html-container"))){
      if(e.getDomProperty("textContent").isBlank())continue;
      ((JavascriptExecutor)d).executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'})",e);
      if(e.isDisplayed()&&!e.getText().isBlank())messages.add(e.getText());
    }
    for(WebElement input:d.findElements(By.cssSelector("form:not([novalidate]) input:invalid,form:not([novalidate]) textarea:invalid"))){
      String message=input.getDomProperty("validationMessage");
      if(message==null||message.isBlank())continue;
      String label=String.valueOf(((JavascriptExecutor)d).executeScript("const e=arguments[0];return e.closest('[data-title]')?.getAttribute('data-title')||Array.from(e.labels||[]).map(l=>l.textContent).join(' ')||e.name",input));
      messages.add(label+": "+message);
    }
    // Some controls show a red invalid border while their message remains d-none.
    // Read the actual accessibility validation state, without revealing hidden text.
    for(WebElement input:d.findElements(By.cssSelector("form input[aria-invalid='true'],form textarea[aria-invalid='true']"))){
      ((JavascriptExecutor)d).executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'})",input);
      if(!input.isDisplayed()||!input.isEnabled())continue;
      String label=String.valueOf(((JavascriptExecutor)d).executeScript("const e=arguments[0];return e.closest('[data-title]')?.getAttribute('data-title')||Array.from(e.labels||[]).map(l=>l.textContent).join(' ')||e.name",input));
      if(!label.isBlank())messages.add(label+": aria-invalid=true");
    }
    return messages.isEmpty()?null:String.join(" | ",new LinkedHashSet<>(messages));
  });}
  @Step("البحث عن سجل الاختبار: {name}") public void prSearch(String name){
    By locator=By.xpath("//input[@id='arabicName' or @name='arabicNamePart' or contains(@placeholder,'بحث') or (@type='search' and not(contains(@class,'vs__search')))] | //*[self::label or self::legend][normalize-space()='الاسم العربي']/following::input[1]");
    ready.until(d->{
      if(d.findElements(locator).stream().anyMatch(WebElement::isDisplayed))return true;
      for(WebElement b:d.findElements(By.xpath("//button[contains(normalize-space(),'عرض التصفية')]")))
        if(b.isDisplayed()){b.click();break;}
      return false;
    });
    prType(locator,name);
  }
  public boolean prHasRow(String name){return driver.findElements(By.cssSelector("tbody tr")).stream().filter(WebElement::isDisplayed)
      .anyMatch(r->r.findElements(By.cssSelector("td")).stream().anyMatch(c->c.getText().trim().equals(name)));}
  public void prWaitRow(String name){ready.until(d->prHasRow(name));}
  @Step("تعديل سجل مملوك للاختبار: {name}") public void prEdit(String name){
    prRowAction(name,"edit");
    // The form mounts before its API response; typing earlier is overwritten by hydration.
    ready.until(d->prNameInputs().stream().anyMatch(e->e.isDisplayed()&&e.isEnabled()&&name.equals(e.getDomProperty("value"))));
  }
  @Step("طلب حذف سجل مملوك للاختبار: {name}") public void prDelete(String name){prRowAction(name,"delete");visible(By.cssSelector("button.swal2-confirm"));}
  private void prRowAction(String name,String action){
    ready.until(d->{WebElement row=d.findElements(By.cssSelector("tbody tr")).stream().filter(WebElement::isDisplayed)
      .filter(r->r.findElements(By.cssSelector("td")).stream().anyMatch(c->c.getText().trim().equals(name))).findFirst().orElse(null);
      if(row==null)return false;List<WebElement> buttons=row.findElements(By.cssSelector("[id*='"+action+"'],[data-action-type='"+action+"']"+(action.equals("edit")?",[id$='-preview-icon']":"")));
      for(WebElement b:buttons){((JavascriptExecutor)d).executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'})",b);if(b.isDisplayed()){b.click();return true;}}return false;});
  }
  public void prConfirm(boolean confirm){prClick(By.cssSelector(confirm?"button.swal2-confirm":"button.swal2-cancel"));ready.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".swal2-container")));}
  public void prExport(String format){prClick(By.cssSelector("button:has(img[src*='"+(format.equals("pdf")?"pdf.":"xls.")+"'])"));}
  public List<String> prHeaders(){return driver.findElements(By.cssSelector("thead th")).stream().filter(WebElement::isDisplayed).map(WebElement::getText).toList();}
  @Step("تسجيل طلبات الحفظ الخاصة بالاختبار") public void prCapture(String marker){
    ((JavascriptExecutor)driver).executeScript("""
      const store=document.querySelector('#app').__vue__.$store;
      window.__productsQA={marker:arguments[0],saves:[],attempts:[],pending:0,errors:[]};
      if(store.__productsCaptureInstalled)return;store.__productsCaptureInstalled=true;
      const dispatch=store.dispatch.bind(store);
      store.dispatch=function(action,payload,...rest){const cap=window.__productsQA;
        const owned=(action==='app/create'||action==='app/update')&&cap&&
          [payload?.data?.arabicName,payload?.data?.englishName,payload?.data?.notes].some(v=>typeof v==='string'&&v.startsWith(cap.marker));
        const sent=owned?JSON.parse(JSON.stringify(payload.data)):null;
        if(owned){cap.pending++;cap.attempts.push({endpoint:payload.url,values:sent});}
        return dispatch(action,payload,...rest).then(result=>{if(owned){cap.pending--;const entity=result?.data||result;
          cap.saves.push({endpoint:payload.url,id:sent.id||entity?.id||(typeof entity==='string'?entity:null),values:sent});}return result;
        }).catch(error=>{if(owned){cap.pending--;cap.errors.push(String(error));}throw error;});};
      """,marker);
  }
  public String prCaptureMarker(){return (String)((JavascriptExecutor)driver).executeScript("return window.__productsQA?.marker||null");}
  @SuppressWarnings("unchecked") public List<Map<String,Object>> prSaves(){return (List<Map<String,Object>>)((JavascriptExecutor)driver).executeScript("return window.__productsQA?.saves||[]");}
  @SuppressWarnings("unchecked") public List<Map<String,Object>> prAttempts(){return (List<Map<String,Object>>)((JavascriptExecutor)driver).executeScript("return window.__productsQA?.attempts||[]");}
  public void prSettled(){ready.until(d->!Boolean.TRUE.equals(((JavascriptExecutor)d).executeScript("return (window.__productsQA?.pending||0)>0")));}
  public String prSaveErrors(){return String.valueOf(((JavascriptExecutor)driver).executeScript("return window.__productsQA?.errors||[]"));}
  public void prAwaitReport(){utilites.waits().waitForReportReady(60);}
  @Step("اختيار صنف الاختبار من النافذة: {name}") public void prPickItem(String name){
    prType(By.cssSelector(".modal.show input#arabicName,.modal.show input[placeholder*='بحث']"),name);
    prClick(By.xpath("//div[contains(@class,'modal') and contains(@class,'show')]//tr[.//td[normalize-space()='"+name+"']]//button[contains(@class,'btn-outline-primary')]"));
  }
  public void prClosePicker(){prClick(By.xpath("//div[contains(@class,'modal') and contains(@class,'show')]//button[normalize-space()='إغلاق']"));
    ready.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal.show")));}
  public int prFormRowsContaining(String name){return (int)driver.findElements(By.cssSelector("form tbody tr")).stream().filter(WebElement::isDisplayed).filter(r->r.getText().contains(name)).count();}
}
