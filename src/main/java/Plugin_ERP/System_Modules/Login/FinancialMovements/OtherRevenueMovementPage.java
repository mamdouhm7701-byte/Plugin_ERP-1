package Plugin_ERP.System_Modules.Login.FinancialMovements;

import io.qameta.allure.Step;
import javax.swing.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OtherRevenueMovementPage extends FinancialMovementPage {

    // Constructors
    public OtherRevenueMovementPage(WebDriver driver) {
        super(driver);
    }

    // Locators
    private By OpenFinancialMovements = By.xpath("//span[normalize-space()='الحركات المالية']");
    private By OtherRevenueMovement = By.xpath("//span[normalize-space()='حركة الايرادات']");
    private By RevenueType = By.cssSelector("input[value='Incomes']");
    private By AcountType = By.xpath("//label[.//span[contains(text(),'دليل الحسابات')]]");
    private By AmountRequiredError =
            By.xpath(
                    "//small[contains(text(),'قيمة الحقل القيمة يجب ان تكون اكبر من 0.01 او تساويها')]");
    private By TypeRequiredError = By.xpath("//small[contains(text(),'أنواع الإيرادات مطلوب')]");
    private By PaymentMethod = By.xpath("//small[contains(text(),'طريقة الدفع مطلوب')]");

    // Methods
    @Step("Open Other Revenue Movement Page")
    public void OpenOtherRevenueMovementPage() {
        utilites.ClickElement(OpenFinancialMovements);
        utilites.ClickElement(OtherRevenueMovement);
        utilites.ClickElement(OpenFinancialMovements);
    }

    @Step("Select Revenue Type")
    public void SelectRevenueTypeButton() {
        utilites.ClickElement(RevenueType);
    }

    @Step("Select Account Type")
    public void SelectAccountTypeButton() {
        utilites.ClickElement(AcountType);
    }

    // Assert Error Methods
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
}
