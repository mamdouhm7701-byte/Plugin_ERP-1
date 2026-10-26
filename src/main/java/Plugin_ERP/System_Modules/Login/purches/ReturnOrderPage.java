package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.System_Modules.Login.AbstractForPurchases;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ReturnOrderPage extends AbstractForPurchases {
    private final ReturnOrderRows rowReader;

    public ReturnOrderPage(WebDriver driver) {

        super(driver);
        rowReader = new ReturnOrderRows(this, driver);
    }

    private By ClickPlusToAddPayForReturn =
            By.xpath("(//button[contains(@class,'btn-gradient-primary')])[1]");
    private By AssertSupplierForReturn = By.xpath("//small[contains(text(),'المورد مطلوب')]");

    // Methods
    @Step("احتساب الفاتورة ومطابقتها مع الفرونت والحفظ والمطابقه مع التقرير")
    public void validateInvoiceAllBusinessForReturnOrder() {

        InvoiceTotals totals = validateBusinessTotalsForReturnOrder();

        saveAndPrintForReport();

        validateReport(totals);
    }

    @Override
    protected BigDecimal getSummaryValue(String label) {

        WebElement input;

        switch (label) {
            case "خصم":
                input = driver.findElement(By.cssSelector("input[name='discountValue']"));
                break;

            case "قبل الضريبة":
            case "الضريبة":
                input =
                        driver.findElement(
                                By.xpath(
                                        "//label[normalize-space()='"
                                                + label
                                                + "']"
                                                + "/parent::div/following-sibling::div//input"));
                break;

            case "الصافي":
                input = driver.findElement(By.cssSelector("input[name='Net']"));
                break;

            default:
                BigDecimal value = super.getSummaryValue(label);
                System.out.println("SUMMARY FROM SUPER => " + label + " = " + value);
                return value;
        }

        String value = input.getAttribute("value");

        System.out.println("SUMMARY FROM RETURN => " + label + " = [" + value + "]");

        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value.trim());
    }

    public List<InvoiceRow> getInvoiceRowsForReturnOrder() {
        return rowReader.getInvoiceRowsForReturnOrder();
    }

    @Step("احتساب  ارقام الفاتوره ومطابقتها بالفرونت")
    public InvoiceTotals validateBusinessTotalsForReturnOrder() {

        InvoiceTotals totals =
                InvoiceCalculator.calculate(getInvoiceRowsForReturnOrder(), getDiscount());

        attachBusinessValidation(totals);

        System.out.println(totals);

        assertInvoiceTotals(totals);

        return totals;
    }

    @Step("الاحتفاظ بارقام الفواتير لمراجعتها مع التقرير")
    public void saveAndPrintForReport() {
        Map<String, String> dataBeforeSave = GetNamesQuantitiesAndPricesForReturnOrder();

        ClickSaveAndPrintButton();

        CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
    }

    public Map<String, String> GetNamesQuantitiesAndPricesForReturnOrder() {
        return rowReader.GetNamesQuantitiesAndPricesForReturnOrder();
    }

    @Step("الضغط على علامه ال+ لاضافه طريقه الدفع")
    public void ClickPlusToAddPayForReturnOrder() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ClickPlusToAddPayForReturn);
    }

    @Step("التأكيد ان الموردين مطلوب")
    public Boolean AssertSupplierForReturnOrder() {
        return utilites.isDisplayed(AssertSupplierForReturn);
    }

    public void waitUntilSummaryValuesLoaded() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(
                driver -> {
                    try {

                        String beforeTax =
                                driver.findElement(
                                                By.xpath(
                                                        "//label[normalize-space()='قبل الضريبة']"
                                                                + "/parent::div/following-sibling::div//input"))
                                        .getAttribute("value");

                        String tax =
                                driver.findElement(
                                                By.xpath(
                                                        "//label[normalize-space()='الضريبة']"
                                                                + "/parent::div/following-sibling::div//input"))
                                        .getAttribute("value");

                        String net =
                                driver.findElement(By.cssSelector("input[name='Net']"))
                                        .getAttribute("value");

                        return beforeTax != null
                                && !beforeTax.trim().isEmpty()
                                && tax != null
                                && !tax.trim().isEmpty()
                                && net != null
                                && !net.trim().isEmpty();

                    } catch (Exception e) {
                        Plugin_ERP.Bots.LogUtils.debug(
                                "ReturnOrderPage condition unavailable: "
                                        + e.getClass().getSimpleName());
                        return false;
                    }
                });
    }
}
