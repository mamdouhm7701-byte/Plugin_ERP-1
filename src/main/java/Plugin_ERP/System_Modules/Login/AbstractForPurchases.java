package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.Models.TaxMode;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbstractForPurchases extends AbstractForStore{
    public AbstractForPurchases(WebDriver driver){super(driver);}






    //Purches

    @Step("تعديل قيمة الصف {rowNumber} فى العمود {columnName} إلى {value}")
    public void SetValueByRow(String columnName, int rowNumber, int value) {

        WebElement input = utilites.waits().waitForInputByRow(columnName, rowNumber);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                input
        );

        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.DELETE);
        input.sendKeys(String.valueOf(value));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                input
        );

        input.sendKeys(Keys.TAB);

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
            String actual = utilites.waits()
                    .waitForInputByRow(columnName, rowNumber)
                    .getAttribute("value");

            return actual != null &&
                    normalizeQuantity(actual).equals(String.valueOf(value));
        });

        LogUtils.info(columnName + " فى الصف " + rowNumber + " أصبح " + value);
    }
    @Step("إضافة قيم فى العمود {columnName}")
    public void SetValuesForRows(String columnName, int... values) {

        utilites.waits().waitForOverlayToDisappear();

        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("Values must be pairs: rowNumber, value");
        }

        for (int i = 0; i < values.length; i += 2) {
            SetValueByRow(columnName, values[i], values[i + 1]);
        }
    }


    public Map<String, String> GetNamesQuantitiesAndPrices() {

        Map<String, String> data = new LinkedHashMap<>();

        String nameColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الاسم']]")
        ).getAttribute("aria-colindex");

        String quantityColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الكمية']]")
        ).getAttribute("aria-colindex");

        String priceColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='سعر الشراء']]")
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





    @Step("مقارنة الأسماء والكميات والأسعار مع التقرير")
    public void CompareNamesQuantitiesAndPricesWithReport(Map<String, String> expectedData) {

        for (Map.Entry<String, String> entry : expectedData.entrySet()) {

            String name = entry.getKey();

            String[] values = entry.getValue().split("\\|");

            String quantity = values[0];
            String price = values[1];

            Assert.assertTrue(
                    utilites.validation().IsTextDisplayed(name),
                    "الاسم غير موجود في التقرير: " + name
            );

            Assert.assertTrue(
                    utilites.validation().IsTextDisplayed(quantity),
                    "الكمية غير موجودة في التقرير للصنف " + name + ": " + quantity
            );

            Assert.assertTrue(
                    utilites.validation().IsTextDisplayed(price),
                    "سعر الشراء غير موجود في التقرير للصنف " + name + ": " + price
            );

            System.out.println(
                    "✅ تم التأكد من: " + name +
                            " - الكمية: " + quantity +
                            " - السعر: " + price
            );
        }
    }


    public List<InvoiceRow> getInvoiceRows() {

        List<InvoiceRow> invoiceRows = new ArrayList<>();

        String nameColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الاسم']]")
        ).getAttribute("aria-colindex");

        String quantityColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='الكمية']]")
        ).getAttribute("aria-colindex");

        String priceColIndex = driver.findElement(
                By.xpath("//th[@role='columnheader'][.//div[normalize-space()='سعر الشراء']]")
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

    private BigDecimal getSummaryValue(String label) {

        WebElement input = driver.findElement(By.xpath(
                "//span[normalize-space()='" + label + "']/ancestor::div[contains(@class,'align-items-center')]" +
                        "/following-sibling::div//input"
        ));

        String value = input.getAttribute("value").trim();

        if (value.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value);
    }

    public BigDecimal getTotal() {

        String value = driver.findElement(
                By.xpath("//input[@placeholder='الاجمالى ']")
        ).getAttribute("value");

        return new BigDecimal(value);
    }

    public BigDecimal getBeforeTax() {
        return getSummaryValue("قبل الضريبة");
    }

    public BigDecimal getTax() {
        return getSummaryValue("الضريبة");
    }

    public BigDecimal getNet() {
        return getSummaryValue("الصافي");
    }

    public BigDecimal getDiscount() {
        return getSummaryValue("خصم");
    }

    private BigDecimal getReportTotal(String title) {

        String xpath = String.format(
                "//tr[.//*[normalize-space()='%s']]//td[1]//div[last()]",
                title
        );

        return new BigDecimal(driver.findElement(By.xpath(xpath)).getText().trim());
    }

    public ReportTotals getReportTotals() {

        ReportTotals totals = new ReportTotals();

        totals.setTotal(getReportTotal("الاجمالي (غير شامل ضريبة القيمة المضافة)"));

        totals.setDiscount(getReportTotal("اجمالي الخصومات"));

        totals.setBeforeTax(getReportTotal("الاجمالي الخاضع للضريبة (غير شامل ضريبة القيمة المضافة)"));

        totals.setTax(getReportTotal("ضريبة القيمة المضافة %15"));

        totals.setNet(getReportTotal("الصافي بعد الضريبة"));

        return totals;
    }
    private static final BigDecimal TOLERANCE = new BigDecimal("0.02");

    private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {

        BigDecimal difference = expected.subtract(actual).abs();

        Assert.assertTrue(
                difference.compareTo(TOLERANCE) <= 0,
                "Expected: " + expected +
                        " , Actual: " + actual +
                        " , Difference: " + difference
        );
    }

    public void attachBusinessValidation(

            InvoiceTotals totals) {

        String report = String.format("""
            Business Validation

            UI Total              = %s
            Expected Total        = %s

            UI Before Tax         = %s
            Expected Before Tax   = %s

            UI Tax                = %s
            Expected Tax          = %s

            UI Net                = %s
            Expected Net          = %s
            """,
                getTotal(),
                totals.getTotal(),

                getBeforeTax(),
                totals.getBeforeTax(),

                getTax(),
                totals.getTax(),

                getNet(),
                totals.getNet()
        );

        Allure.addAttachment("Invoice Calculation", report);
    }
    public void attachReportValidation(
            ReportTotals reportTotals,
            InvoiceTotals totals) {

        String report = String.format("""
            Report Validation

            Report Total          = %s
            Expected Total        = %s

            Report Discount       = %s
            Expected Discount     = %s

            Report Before Tax     = %s
            Expected Before Tax   = %s

            Report Tax            = %s
            Expected Tax          = %s

            Report Net            = %s
            Expected Net          = %s
            """,
                reportTotals.getTotal(),
                totals.getTotal(),

                reportTotals.getDiscount(),
                totals.getDiscount(),

                reportTotals.getBeforeTax(),
                totals.getBeforeTax(),

                reportTotals.getTax(),
                totals.getTax(),

                reportTotals.getNet(),
                totals.getNet()
        );

        Allure.addAttachment("Report Validation", report);
    }
    public void assertInvoiceTotals(InvoiceTotals totals) {

        assertBigDecimalEquals(totals.getTotal(), getTotal());
        assertBigDecimalEquals(totals.getBeforeTax(), getBeforeTax());
        assertBigDecimalEquals(totals.getTax(), getTax());
        assertBigDecimalEquals(totals.getNet(), getNet());
    }


    public void assertReportTotals(
            ReportTotals report,
            InvoiceTotals expected,
            TaxMode taxMode) {

        if (taxMode == TaxMode.INCLUSIVE) {
            assertBigDecimalEquals(expected.getBeforeTax(), report.getTotal());
        } else {
            assertBigDecimalEquals(expected.getTotal(), report.getTotal());
        }

        assertBigDecimalEquals(expected.getDiscount(), report.getDiscount());
        assertBigDecimalEquals(expected.getBeforeTax(), report.getBeforeTax());
        assertBigDecimalEquals(expected.getTax(), report.getTax());
        assertBigDecimalEquals(expected.getNet(), report.getNet());
    }

















}
