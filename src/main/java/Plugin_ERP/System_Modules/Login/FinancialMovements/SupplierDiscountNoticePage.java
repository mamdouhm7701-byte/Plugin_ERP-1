package Plugin_ERP.System_Modules.Login.FinancialMovements;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SupplierDiscountNoticePage extends FinancialMovementPage {

    // Constructors
    public SupplierDiscountNoticePage(WebDriver driver) {
        super(driver);
    }

    // Locators
    private By OpenFinancialMovements = By.xpath("//span[normalize-space()='الحركات المالية']");
    private By SupplierNotice = By.xpath("//span[normalize-space()='اشعار خصم مورد']");
    private By Value = By.cssSelector("input[label-text='value']");
    private By CanselCustomReceipt = By.cssSelector(".btn-danger");
    private By SupplierRequiredError = By.xpath("//small[contains(text(),'المورد مطلوب')]");
    private By AmountRequiredError =
            By.xpath(
                    "//small[contains(text(),'قيمة الحقل القيمة يجب ان تكون اكبر من 1 او تساويها')]");
    private By NetBeforeTax = By.cssSelector("fieldset[label-text='netBeforeTax'] input");
    private By Tax = By.cssSelector("input[name='tax']");
    private By SaveAndAllocateButton = By.xpath("//button[normalize-space()='حفظ وتخصيص']");

    // Methods
    // To Get the Allocation Input Field for a specific row in the allocation table
    public By getAllocationInput(int rowIndex) {
        return By.xpath("(//td[@aria-colindex='8']//input[@type='number'])[" + rowIndex + "]");
    }

    @Step("Is Supplier Required Error Displayed")
    public boolean IsSupplierRequiredErrorDisplayed() {
        return utilites.isDisplayed(SupplierRequiredError);
    }

    @Step("Is Amount Required Error Displayed")
    public boolean IsAmountRequiredErrorDisplayed() {
        return utilites.isDisplayed(AmountRequiredError);
    }

    @Step("Open Supplier Discount Notice")
    public void OpenSupplierDiscountNotice() {
        utilites.ClickElement(OpenFinancialMovements);
        utilites.ClickElement(SupplierNotice);
        utilites.ClickElement(OpenFinancialMovements);
    }

    @Step("حفظ إشعار الخصم")
    public void SaveCustomButton() {
        utilites.ClickElement(SaveAndAllocateButton);
    }

    @Step("Cancel Supplier Receipt")
    public void CanselCustomReceipt() {
        utilites.ClickElement(CanselCustomReceipt);
    }

    // Handel Custom Receipt
    @Step("Handel Custom Receipt for Row Index: {rowIndex} with Number: {Number}")
    public void HandelCustomReceipt(int rowIndex, String Number) {
        utilites.ClickElement(getAllocationInput(rowIndex));
        utilites.typeTextAndVerify(getAllocationInput(rowIndex), Number);
    }

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
