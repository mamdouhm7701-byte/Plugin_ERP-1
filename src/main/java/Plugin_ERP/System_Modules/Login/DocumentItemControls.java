package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

/** Document Item Controls: focused operations used by the original page facade. */
final class DocumentItemControls {
    private final DocumentPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElmentAction utilites;
    DocumentItemControls(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }
    private By BarcodeRadioBtn = By.cssSelector("input[type='radio'][value='barcode']");
    private By NameRadioBtn = By.cssSelector("input[type='radio'][value='name']");
    private By SearchInput = By.cssSelector("input[placeholder='بحث ....']");
    private By AddItemsBtn = By.xpath("//button[contains(text(), 'إضافة أصناف')]");
    private By SaveForBageProductBtn = By.xpath("//div[contains(@class,'modal-content')]//button[normalize-space()='حفظ']");
    private By AssertExsistItemTaype  = By.xpath("//small[contains(text(),'نوع البند مطلوب')]");
    private By AssertExsistCustomre  = By.xpath("//small[contains(text(),'العملاء مطلوب')]");
    private By AssertExpenditureCategories    = By.xpath("//small[contains(text(),'جهات الصرف مطلوب')]");
    private By AssertAgnt    = By.xpath("//small[contains(text(),'المندوبين مطلوب')]");
    private By AssertSupplier    = By.xpath("//small[contains(text(),'الموردين مطلوب')]");
    private By AssertStore    = By.xpath("//small[contains(text(),'المخزن مطلوب')]");

    Boolean AssertExsistItemTaype(){
        return utilites.isDisplayed(AssertExsistItemTaype);
    }

    Boolean AssertExsistCustomre(){
        return utilites.isDisplayed(AssertExsistCustomre);
    }

    Boolean AssertExpenditureCategories(){
        return utilites.isDisplayed(AssertExpenditureCategories);
    }

    Boolean AssertAgnt(){
        return utilites.isDisplayed(AssertAgnt);
    }

    Boolean AssertSotreRequired(){
        return utilites.isDisplayed(AssertStore);
}

    Boolean AssertSupplier(){
    return utilites.isDisplayed(AssertSupplier);
}

    void ClickOnBarcodeRadioBtn() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(BarcodeRadioBtn);
    }

    void ClickOnNameRadioBtn() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(NameRadioBtn);
    }

    void Search(String searchText) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(SearchInput,searchText);
     }

    void ClickSaveForProductBtn() {
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(SaveForBageProductBtn);
        utilites.waits().waitForOverlayToDisappear();

    }

    void ClickAddItemsBtn() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(AddItemsBtn);
        utilites.waits().waitForOverlayToDisappear();

    }
}
