package Plugin_ERP.System_Modules.Login.FinancialMovements;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.BrowserAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import Plugin_ERP.System_Modules.Login.Abstract;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.math.BigDecimal;
import java.time.Duration;

public class CustomerDiscountNoticePage extends Abstract {
    //Constructors
    public CustomerDiscountNoticePage(WebDriver driver) {
        super(driver);
    }

    //Locators
    private By OpenFinancialMovements = By.xpath("//span[normalize-space()='الحركات المالية']");
    private By CustomerNotice = By.xpath("//span[normalize-space()='اشعار خصم عميل']");
    private By Value = By.cssSelector("input[label-text='value']");
    private By SaveAndAllocateButton =
            By.xpath("//button[normalize-space()='حفظ وتخصيص']");
    private By CanselCustomReceipt = By.cssSelector(".btn-danger");
    private By AgentRequiredError = By.xpath("//small[contains(text(),'العميل مطلوب')]");
    private By AmountRequiredError = By.xpath("//small[contains(text(),'قيمة الحقل القيمة يجب ان تكون اكبر من 0.01 او تساويها')]");
    private By NetBeforeTax = By.cssSelector("fieldset[label-text='netBeforeTax'] input");
    private By Tax = By.cssSelector("input[name='tax']");

    //Methods
    //To Get the Allocation Input Field for a specific row in the allocation table
    public By getAllocationInput(int rowIndex) {
        return By.xpath("(//td[@aria-colindex='8']//input[@type='number'])[" + rowIndex + "]");
    }


    @Step("Is Agent Required Error Displayed")
    public boolean IsAgentRequiredErrorDisplayed() {
        return utilites.isDisplayed(AgentRequiredError);
    }
    @Step("Is Amount Required Error Displayed")
    public boolean IsAmountRequiredErrorDisplayed() {
        return utilites.isDisplayed(AmountRequiredError);
    }




    @Step("Open Customer Discount Notice")
    public void OpenCustomerDiscountNotice() {
        utilites.ClickElement(OpenFinancialMovements);
        utilites.ClickElement(CustomerNotice);
        utilites.ClickElement(OpenFinancialMovements);
    }
    
    @Step("Click Save And Allocate Button")
    public void SaveCustomButton() {
        utilites.ClickElement(SaveAndAllocateButton);
    }

    @Step("Cancel Custom Receipt")
    public void CanselCustomReceipt() {
        utilites.ClickElement(CanselCustomReceipt);
    }


    //Handel Custom Receipt
    @Step("Handel Custom Receipt for Row Index: {rowIndex} with Number: {Number}")
    public void HandelCustomReceipt(int rowIndex, String Number) {
        utilites.ClickElement(getAllocationInput(rowIndex));
        utilites.TaypeTextB(getAllocationInput(rowIndex), Number);
    }







///Financial Calculations

    public BigDecimal[] getFinancialValues() {

        BigDecimal netBeforeTax =
                utilites.Utility().getNormalizedAmount(NetBeforeTax);

        BigDecimal tax =
                utilites.Utility().getNormalizedAmount(Tax);

        BigDecimal total =
                utilites.Utility().getNormalizedAmount(Value);

        return new BigDecimal[]{
                netBeforeTax,
                tax,
                total
        };
    }

        public double getNetBeforeTax() {
            double value = utilites.Utility().getNumber(NetBeforeTax);

            Allure.step("Net Before Tax = " + value);

            return value;
        }

        public double getTax() {
            double value = utilites.Utility().getNumber(Tax);

            Allure.step(" Tax = " + value);

            return value;
        }

        public double getTotal() {
            double value = utilites.Utility().getNumber(Value);

            Allure.step("Value = " + value);

            return value;
        }
        @Step("Calculate Expected Tax (15%)")
    public double getExpectedTax() {

        double netBeforeTax =
                utilites.Utility().getNumber(NetBeforeTax);

        double expectedTax =
                netBeforeTax * 0.15;

        Allure.step("Expected Tax (15%) = " + expectedTax);

        return expectedTax;
    }
    @Step("Calculate Expected Total")
    public double getExpectedTotal() {

        double netBeforeTax =
                utilites.Utility().getNumber(NetBeforeTax);

        double expectedTax =
                netBeforeTax * 0.15;

        double expectedTotal =
                netBeforeTax + expectedTax;

        Allure.step("Expected Total = " + expectedTotal);

        return expectedTotal;
    }







    }









