package Plugin_ERP.System_Modules.Login.FinancialMovements;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.BrowserAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import Plugin_ERP.System_Modules.Login.Abstract;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class CustomerReceiptPage extends Abstract {
    //Constructors
       public CustomerReceiptPage(WebDriver driver) {
        super(driver);
    }
    //Locators//
    private By OpenFinancialMovements = By.xpath("//span[normalize-space()='الحركات المالية']");
    private By OpenCustomerReceipt = By.xpath("//span[normalize-space()='سند قبض عميل']");
    private By SaveAndAllocateButton =
            By.xpath("//button[normalize-space()='حفظ وتخصيص']");
    private By AgentRequiredError = By.xpath("//small[contains(text(),'العميل مطلوب')]");
    private By AmountRequiredError = By.xpath("//small[contains(text(),'قيمة الحقل القيمة يجب ان تكون اكبر من 0.01 او تساويها')]");
    private By PaymentMethodRequiredError = By.xpath("//small[contains(text(),'طريقة الدفع مطلوب')]");



    //Methods
    //To Get the Allocation Input Field for a specific row in the allocation table
    public By getAllocationInput(int rowIndex) {
        return By.xpath("(//td[@aria-colindex='8']//input[@type='number'])[" + rowIndex + "]");
    }
    @Step("Open Customer Receipt Page")
    public void OpenFinancialMovementsPage() {
        utilites.ClickElement(OpenFinancialMovements);
        utilites.ClickElement(OpenCustomerReceipt);
        utilites.ClickElement(OpenFinancialMovements);
    }

    @Step("Click Save And Allocate Button")
    public void SaveCustomButton() {
        utilites.ClickElement(SaveAndAllocateButton);
    }

    //Handel Custom Receipt
    @Step("Handel Custom Receipt for Row Index: {rowIndex} with Number: {Number}")
    public void HandelCustomReceipt(int rowIndex,String Number){
        utilites.ClickElement(getAllocationInput(rowIndex));
        utilites.TaypeTextB(getAllocationInput(rowIndex), Number);
    }



    //Assert Required Error
        @Step("Is Agent Required Error Displayed")
        public boolean IsAgentRequiredErrorDisplayed() {
            return utilites.isDisplayed(AgentRequiredError);
        }
        @Step("Is Amount Required Error Displayed")
        public boolean IsAmountRequiredErrorDisplayed() {
            return utilites.isDisplayed(AmountRequiredError);
        }
        @Step("Is Payment Method Required Error Displayed")
        public boolean IsPaymentMethodRequiredErrorDisplayed() {
            return utilites.isDisplayed(PaymentMethodRequiredError);
        }



    }




