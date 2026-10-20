package Plugin_ERP.System_Modules.Login.FinancialMovements;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.BrowserAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import Plugin_ERP.System_Modules.Login.Abstract;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.math.BigDecimal;
import java.time.Duration;

public class ExpenseMovementPage extends FinancialMovementPage {

    // Constructors
    public ExpenseMovementPage(WebDriver driver) {
        super(driver);
    }

    // Locators
    private By OpenFinancialMovements = By.xpath("//span[normalize-space()='الحركات المالية']");
    private By ExpenseMovement = By.xpath("//span[contains(normalize-space(),'حركة المصروفات')]");
    private By ExpenseType = By.cssSelector("input[value='Expenses']");
    private By AcountType = By.xpath("//label[.//span[contains(text(),'دليل الحسابات')]]");
    private By Value = By.cssSelector("input[label-text='value']");
    private By AmountRequiredError =
            By.xpath(
                    "//small[contains(text(),'قيمة الحقل القيمة يجب ان تكون اكبر من 0.01 او تساويها')]");
    private By NetBeforeTax = By.cssSelector("input[name='netValue']");
    private By Tax = By.cssSelector("input[name='tax']");
    private By TypeRequiredError = By.xpath("//small[contains(text(),'النوع مطلوب')]");
    private By PaymentMethod = By.xpath("//small[contains(text(),'طريقة الدفع مطلوب')]");

    // Methods
    @Step("Open Expense Movement Page")
    public void OpenExpenseMovementPage() {
        utilites.ClickElement(OpenFinancialMovements);
        utilites.ClickElement(ExpenseMovement);
        utilites.ClickElement(OpenFinancialMovements);
    }

    @Step("Select Expense Type")
    public void SelectExpenseType() {
        utilites.ClickElement(ExpenseType);
    }

    @Step("Select Acount Type")
    public void SelectAcountType() {
        utilites.ClickElement(AcountType);
    }

    // Assertions
    @Step("Is Amount Required Error Displayed")
    public boolean IsAmountRequiredErrorDisplayed() {
        return utilites.isDisplayed(AmountRequiredError);
    }

    @Step("Is Type Required Error Displayed")
    public boolean IsTypeRequiredErrorDisplayed() {
        return utilites.isDisplayed(TypeRequiredError);
    }

    @Step("Is Payment Method Required Error Displayed")
    public boolean IsPaymentMethodRequiredErrorDisplayed() {
        return utilites.isDisplayed(PaymentMethod);
    }

    //    Get Financial Values
    public BigDecimal[] getFinancialValues() {

        BigDecimal netBeforeTax = utilites.Utility().getNormalizedAmount(NetBeforeTax);

        BigDecimal tax = utilites.Utility().getNormalizedAmount(Tax);

        BigDecimal total = utilites.Utility().getNormalizedAmount(Value);

        return new BigDecimal[] {netBeforeTax, tax, total};
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

        double netBeforeTax = utilites.Utility().getNumber(NetBeforeTax);

        double expectedTax = netBeforeTax * 0.15;

        Allure.step("Expected Tax (15%) = " + expectedTax);

        return expectedTax;
    }

    @Step("Calculate Expected Total")
    public double getExpectedTotal() {

        double netBeforeTax = utilites.Utility().getNumber(NetBeforeTax);

        double expectedTax = netBeforeTax * 0.15;

        double expectedTotal = netBeforeTax + expectedTax;

        Allure.step("Expected Total = " + expectedTotal);

        return expectedTotal;
    }
}
