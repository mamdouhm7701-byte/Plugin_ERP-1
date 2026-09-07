package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.System_Modules.Login.AbstractForPurchases;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class purchOrderPage extends AbstractForPurchases {
    public purchOrderPage(WebDriver  driver) {
        super(driver);

    }

private By ClickToTransToPurchesOrder= By.xpath("//button[@data-action-type='edit'][.//*[contains(@class,'feather-repeat')]]");


    @Step("الضغط على التحويل الى فاتوره مشتريات")
    public void  clickToTransToPurchesOrder()
    {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ClickToTransToPurchesOrder);
        utilites.waits().waitForOverlayToDisappear();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الاسم']]")
                ));
    }

    @Override
    protected BigDecimal getSummaryValue(String label) {

        String inputName;

        switch (label) {

            case "خصم":
                inputName = "discountValue";
                break;

            case "قبل الضريبة":
                inputName = "total";
                break;

            case "الصافي":
                inputName = "Net";
                break;

            default:
                return super.getSummaryValue(label);
        }

        WebElement input = driver.findElement(
                By.cssSelector("input[name='" + inputName + "']")
        );

        String value = input.getAttribute("value");

        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value.trim());
    }



    public List<InvoiceRow> getInvoiceRowsForPurchOrder() {

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
    public InvoiceTotals validateBusinessTotalsForPurchOrder() {

        InvoiceTotals totals = InvoiceCalculator.calculate(
                getInvoiceRowsForPurchOrder(),
                getDiscount()
        );

        attachBusinessValidation(totals);

        System.out.println(totals);

        assertInvoiceTotals(totals);

        return totals;
    }

    @Step("الاحتفاظ بارقام الفواتير لمراجعتها مع التقرير")

    public void saveAndPrintForReport()
    {
        Map<String, String> dataBeforeSave =
                GetNamesQuantitiesAndPricesForPurchOrder();

        ClickSaveAndPrintButton();

        CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);}



    public Map<String, String> GetNamesQuantitiesAndPricesForPurchOrder() {

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











}
