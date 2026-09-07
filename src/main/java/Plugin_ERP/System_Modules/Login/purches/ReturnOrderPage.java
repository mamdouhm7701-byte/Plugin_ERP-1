package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.System_Modules.Login.AbstractForPurchases;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReturnOrderPage extends AbstractForPurchases {

    public ReturnOrderPage(WebDriver  driver) {

        super(driver);


    }

    private By ClickPlusToAddPayForReturn = By.xpath("(//button[contains(@class,'btn-gradient-primary')])[1]");
    private By AssertSupplierForReturn    = By.xpath("//small[contains(text(),'المورد مطلوب')]");

    //Methods
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
                input = driver.findElement(
                        By.cssSelector("input[name='discountValue']")
                );
                break;

            case "قبل الضريبة":
            case "الضريبة":
                input = driver.findElement(
                        By.xpath(
                                "//label[normalize-space()='" + label + "']" +
                                        "/parent::div/following-sibling::div//input"
                        )
                );
                break;

            case "الصافي":
                input = driver.findElement(
                        By.cssSelector("input[name='Net']")
                );
                break;

            default:
                return super.getSummaryValue(label);
        }

        String value = input.getAttribute("value");

        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value.trim());
    }


    public List<InvoiceRow> getInvoiceRowsForReturnOrder() {

        List<InvoiceRow> invoiceRows = new ArrayList<>();

        String nameColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الاسم']]")
        ).getAttribute("aria-colindex");

        String quantityColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الكمية']]")
        ).getAttribute("aria-colindex");

        String priceColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='السعر']]")
        ).getAttribute("aria-colindex");

        List<WebElement> rows = driver.findElements(
                By.xpath("//tbody//tr[@role='row']")
        );

        for (WebElement row : rows) {

            List<WebElement> nameCells = row.findElements(
                    By.xpath(".//td[@aria-colindex='" + nameColIndex + "']")
            );

            if (nameCells.isEmpty())
                continue;

            String name = nameCells.get(0).getText().trim();

            BigDecimal quantity = new BigDecimal(
                    row.findElement(
                            By.xpath(".//td[@aria-colindex='" + quantityColIndex + "']//input[not(@type='hidden')]")
                    ).getAttribute("value").trim()
            );

            BigDecimal price = new BigDecimal(
                    row.findElement(
                            By.xpath(".//td[@aria-colindex='" + priceColIndex + "']//input[not(@type='hidden')]")
                    ).getAttribute("value").trim()
            );

            invoiceRows.add(new InvoiceRow(name, quantity, price));
        }

        return invoiceRows;
    }

    @Step("احتساب  ارقام الفاتوره ومطابقتها بالفرونت")
    public InvoiceTotals validateBusinessTotalsForReturnOrder() {

        InvoiceTotals totals = InvoiceCalculator.calculate(
                getInvoiceRowsForReturnOrder(),
                getDiscount()
        );

        attachBusinessValidation(totals);

        System.out.println(totals);

        assertInvoiceTotals(totals);

        return totals;
    }

    @Step("الاحتفاظ بارقام الفواتير لمراجعتها مع التقرير")

    public void saveAndPrintForReport()
    {Map<String, String> dataBeforeSave =
            GetNamesQuantitiesAndPricesForReturnOrder();

        ClickSaveAndPrintButton();

        CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);}



    public Map<String, String> GetNamesQuantitiesAndPricesForReturnOrder() {

        Map<String, String> data = new LinkedHashMap<>();

        String nameColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الاسم']]")
        ).getAttribute("aria-colindex");

        String quantityColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الكمية']]")
        ).getAttribute("aria-colindex");

        String priceColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='السعر']]")
        ).getAttribute("aria-colindex");

        List<WebElement> rows = driver.findElements(
                By.xpath("//tbody//tr[@role='row']")
        );

        for (WebElement row : rows) {

            List<WebElement> nameCells = row.findElements(
                    By.xpath(".//td[@aria-colindex='" + nameColIndex + "']")
            );

            if (nameCells.isEmpty())
                continue;

            String name = nameCells.get(0).getText().trim();

            String quantity = row.findElement(
                    By.xpath(".//td[@aria-colindex='" + quantityColIndex + "']//input[not(@type='hidden')]")
            ).getAttribute("value").trim();

            String price = row.findElement(
                    By.xpath(".//td[@aria-colindex='" + priceColIndex + "']//input[not(@type='hidden')]")
            ).getAttribute("value").trim();

            System.out.printf(
                    "Item=%s , Quantity=%s , Price=%s%n",
                    name,
                    quantity,
                    price
            );

            data.put(name, quantity + "|" + price);
        }

        return data;
    }
    @Step("الضغط على علامه ال+ لاضافه طريقه الدفع")
    public void ClickPlusToAddPayForReturnOrder(){
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ClickPlusToAddPayForReturn);
    }
    @Step("التأكيد ان الموردين مطلوب")
    public Boolean AssertSupplierForReturnOrder(){
        return utilites.isDisplayed(AssertSupplierForReturn);
    }
}
