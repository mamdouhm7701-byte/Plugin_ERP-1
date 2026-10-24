package Plugin_ERP.System_Modules.Login.FinancialMovements;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.Utiel.FormInteractions;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Financial Record List: focused operations used by the original page facade. */
final class FinancialRecordList {
    private final FinancialMovementPage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;
    FinancialRecordList(FinancialMovementPage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    private final Map<String,String> ownedCodes = new HashMap<>();
    private void click(By locator) { owner.clickFinancialControl(locator); }

    void identifyMovement(String note,Object code){if(code==null)throw new IllegalArgumentException("Saved movement has no code");ownedCodes.put(note,code.toString());}

    WebElement row(String note){
    return ready.until(d->d.findElements(By.cssSelector("tbody tr"))
        .stream().filter(WebElement::isDisplayed)
        .filter(r->matchesOwnedRow(r,note))
        .findFirst().orElse(null));
  }

    boolean matchesOwnedRow(WebElement row,String note){
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

    boolean hasRow(String note){return driver.findElements(By.cssSelector("tbody tr"))
      .stream().filter(WebElement::isDisplayed).anyMatch(r->matchesOwnedRow(r,note));}

    void waitForRow(String note){row(note);}

    void editOwned(String note){clickOwnedAction(note,"edit");ready.until(d->owner.isFormVisible());ready.until(d->owner.notesValue().equals(note));}

    void requestDeleteOwned(String note){clickOwnedAction(note,"delete");ready.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".swal2-popup")));}

    void clickOwnedAction(String note,String action){ready.until(d->{
    WebElement button=row(note).findElement(By.cssSelector("button[data-action-type='"+action+"']"));
    ((JavascriptExecutor)d).executeScript("arguments[0].scrollIntoView({block:'center'})",button);
    button.click();return true;
  });}

    void confirmDelete(boolean confirm){click(By.cssSelector(confirm?"button.swal2-confirm":"button.swal2-cancel"));ready.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".swal2-container")));}

    void waitForRemoval(String note){ready.until(d->!owner.hasRow(note));}

    boolean hasListHeaders(){return driver.findElements(By.cssSelector("thead th")).stream().anyMatch(WebElement::isDisplayed);}

    void waitForList(){ready.until(d->!owner.isFormPresent());owner.dismissPopup();ready.until(d->owner.hasListHeaders());}

    void export(String format){
    if(!List.of("pdf","excel").contains(format))throw new IllegalArgumentException(format);
    click(By.cssSelector("button:has(img[src*='"+(format.equals("pdf")?"pdf.":"xls.")+"'])"));
  }
}
