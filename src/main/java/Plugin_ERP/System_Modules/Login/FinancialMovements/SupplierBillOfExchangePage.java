/* شرح الملف: شاشة سند صرف لمورد: بنحدد المورد والمبلغ وطريقة الدفع ونوزع السداد على مستنداته.
 * فائدتها توثيق المدفوع للمورد وتسوية مستحقاته.
 */
package Plugin_ERP.System_Modules.Login.FinancialMovements;

import io.qameta.allure.Step;
import javax.swing.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SupplierBillOfExchangePage extends FinancialMovementPage {

    // Constructors
    public SupplierBillOfExchangePage(WebDriver driver) {
        super(driver);
    }

    private By OpenFinancialMovements = By.xpath("//span[normalize-space()='الحركات المالية']");
    private By OpenSupplierBillOfExchange = By.xpath("//span[normalize-space()='سند صرف مورد']");
    private By SaveAndAllocateButton = By.xpath("//button[normalize-space()='حفظ وتخصيص']");
    private By CanselCustomReceipt = By.cssSelector(".btn-danger");
    private By SupplierRequiredError = By.xpath("//small[contains(text(),'المورد مطلوب')]");
    private By AmountRequiredError =
            By.xpath(
                    "//small[contains(text(),'قيمة الحقل القيمة يجب ان تكون اكبر من 0.01 او تساويها')]");
    private By PaymentMethodRequiredError =
            By.xpath("//small[contains(text(),'طريقة الدفع مطلوب')]");

    // Methods
    // To Get the Allocation Input Field for a specific row in the allocation table
    public By getAllocationInput(int rowIndex) {
        return By.xpath("(//td[@aria-colindex='8']//input[@type='number'])[" + rowIndex + "]");
    }

    @Step("Open Supplier Bill Of Exchange Page")
    public void OpenFinancialMovementsPage() {
        utilites.ClickElement(OpenFinancialMovements);
        utilites.ClickElement(OpenSupplierBillOfExchange);
        utilites.ClickElement(OpenFinancialMovements);
    }

    @Step("Click Save And Allocate Button")
    public void SaveCustomButton() {
        utilites.ClickElement(SaveAndAllocateButton);
    }

    // Handel Custom Receipt
    @Step("Handel Custom Receipt for Row Index: {rowIndex} with Number: {Number}")
    public void HandelCustomReceipt(int rowIndex, String Number) {
        utilites.ClickElement(getAllocationInput(rowIndex));
        utilites.typeTextAndVerify(getAllocationInput(rowIndex), Number);
    }

    @Step("Cancel Supplier Receipt")
    public void CanselCustomReceipt() {
        utilites.ClickElement(CanselCustomReceipt);
    }

    // Assertions
    @Step("Is Supplier Required Error Displayed")
    public boolean IsSupplierRequiredErrorDisplayed() {
        return utilites.isDisplayed(SupplierRequiredError);
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
