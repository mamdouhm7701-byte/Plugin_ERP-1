package Plugin_ERP.System_Modules.Login.Invoice;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.System_Modules.Login.AbstractForPurchases;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Invoice extends AbstractForPurchases {

    public Invoice(WebDriver driver) {
        super(driver);
    }

    /** Sales-invoice search override: avoid retrying a successful suggestion click. */
    @Override
    public void SearchAndSelectProduct(String productName) {
        searchAndSelectInvoiceProduct(productName);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> {
                List<WebElement> rows = d.findElements(By.xpath("//tbody//tr[@role='row']"));
                return rows.stream().anyMatch(row -> row.getText().contains(productName));
            });
        } catch (TimeoutException e) {
            throw new IllegalStateException(
                    "تم اختيار الصنف من القائمة لكن لم يظهر في جدول فاتورة المبيعات: " + productName,
                    e
            );
        }
    }

    // =========================================
    // Override
    // =========================================
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
                input = driver.findElement(
                        By.xpath(
                                "//span[normalize-space()='قبل الضريبة']" +
                                        "/parent::div/following-sibling::div//input"
                        )
                );
                break;

            case "الضريبة":
                input = driver.findElement(
                        By.xpath(
                                "//span[normalize-space()='الضريبة']" +
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

        System.out.println(
                "SUMMARY FROM INVOICE => " + label + " = [" + value + "]"
        );

        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value.trim());
    }

    // =========================================
    // Invoice Validation
    // =========================================

    @Step("احتساب الفاتورة ومطابقتها مع الفرونت والحفظ والمطابقه مع التقرير")
    public void validateInvoiceAllBusiness() {

        InvoiceTotals totals = validateBusinessTotals();

        saveAndPrintForReport();

        validateReport(totals);
    }


    @Step("احتساب أرقام الفاتورة ومطابقتها بالفرونت")
    public InvoiceTotals validateBusinessTotals() {

        InvoiceTotals totals = InvoiceCalculator.calculate(
                getInvoiceRows(),
                getDiscount()
        );

        attachBusinessValidation(totals);

        System.out.println(totals);

        assertInvoiceTotals(totals);

        return totals;
    }


    // =========================================
    // Get Invoice Rows
    // =========================================

    public List<InvoiceRow> getInvoiceRows() {

        List<InvoiceRow> invoiceRows = new ArrayList<>();

        String nameColIndex = driver.findElement(
                By.xpath(
                        "//th[@role='columnheader'][.//div[normalize-space()='الاسم']]"
                )
        ).getAttribute("aria-colindex");

        String quantityColIndex = driver.findElement(
                By.xpath(
                        "//th[@role='columnheader'][.//div[normalize-space()='الكمية']]"
                )
        ).getAttribute("aria-colindex");

        String priceColIndex = driver.findElement(
                By.xpath(
                        "//th[@role='columnheader'][.//div[normalize-space()='السعر']]"
                )
        ).getAttribute("aria-colindex");


        List<WebElement> rows = driver.findElements(
                By.xpath("//tbody//tr[@role='row']")
        );


        for (WebElement row : rows) {

            List<WebElement> nameCells = row.findElements(
                    By.xpath(
                            ".//td[@aria-colindex='" +
                                    nameColIndex +
                                    "']"
                    )
            );

            if (nameCells.isEmpty()) {
                continue;
            }

            String name = nameCells.get(0)
                    .getText()
                    .trim();


            BigDecimal quantity = new BigDecimal(
                    row.findElement(
                                    By.xpath(
                                            ".//td[@aria-colindex='" +
                                                    quantityColIndex +
                                                    "']//input[not(@type='hidden')]"
                                    )
                            )
                            .getAttribute("value")
                            .trim()
            );


            BigDecimal price = new BigDecimal(
                    row.findElement(
                                    By.xpath(
                                            ".//td[@aria-colindex='" +
                                                    priceColIndex +
                                                    "']//input[not(@type='hidden')]"
                                    )
                            )
                            .getAttribute("value")
                            .trim()
            );


            invoiceRows.add(
                    new InvoiceRow(
                            name,
                            quantity,
                            price
                    )
            );
        }

        return invoiceRows;
    }


    // =========================================
    // Save + Report
    // =========================================

    @Step("الاحتفاظ بأرقام الفواتير لمراجعتها مع التقرير")
    public void saveAndPrintForReport() {

        Map<String, String> dataBeforeSave =
                getNamesQuantitiesAndPrices();

        ClickSaveAndPrintButton();

        CompareNamesQuantitiesAndPricesWithReport(
                dataBeforeSave
        );
    }


    public Map<String, String> getNamesQuantitiesAndPrices() {

        Map<String, String> data =
                new LinkedHashMap<>();

        String nameColIndex = driver.findElement(
                By.xpath(
                        "//th[@role='columnheader'][.//div[normalize-space()='الاسم']]"
                )
        ).getAttribute("aria-colindex");


        String quantityColIndex = driver.findElement(
                By.xpath(
                        "//th[@role='columnheader'][.//div[normalize-space()='الكمية']]"
                )
        ).getAttribute("aria-colindex");


        String priceColIndex = driver.findElement(
                By.xpath(
                        "//th[@role='columnheader'][.//div[normalize-space()='السعر']]"
                )
        ).getAttribute("aria-colindex");


        List<WebElement> rows = driver.findElements(
                By.xpath("//tbody//tr[@role='row']")
        );


        for (WebElement row : rows) {

            List<WebElement> nameCells = row.findElements(
                    By.xpath(
                            ".//td[@aria-colindex='" +
                                    nameColIndex +
                                    "']"
                    )
            );

            if (nameCells.isEmpty()) {
                continue;
            }


            String name = nameCells.get(0)
                    .getText()
                    .trim();


            String quantity = row.findElement(
                            By.xpath(
                                    ".//td[@aria-colindex='" +
                                            quantityColIndex +
                                            "']//input[not(@type='hidden')]"
                            )
                    ).getAttribute("value")
                    .trim();


            String price = row.findElement(
                            By.xpath(
                                    ".//td[@aria-colindex='" +
                                            priceColIndex +
                                            "']//input[not(@type='hidden')]"
                            )
                    ).getAttribute("value")
                    .trim();


            System.out.printf(
                    "Item=%s , Quantity=%s , Price=%s%n",
                    name,
                    quantity,
                    price
            );


            data.put(
                    name,
                    quantity + "|" + price
            );
        }

        return data;
    }


    // =========================================
    // Wait Summary
    // =========================================

    public void waitUntilSummaryValuesLoaded() {

        WebDriverWait wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(10)
                );


        wait.until(driver -> {

            try {

                String beforeTax =
                        driver.findElement(
                                By.xpath(
                                        "//label[normalize-space()='قبل الضريبة']" +
                                                "/parent::div/following-sibling::div//input"
                                )
                        ).getAttribute("value");


                String tax =
                        driver.findElement(
                                By.xpath(
                                        "//label[normalize-space()='الضريبة']" +
                                                "/parent::div/following-sibling::div//input"
                                )
                        ).getAttribute("value");


                String net =
                        driver.findElement(
                                By.cssSelector(
                                        "input[name='Net']"
                                )
                        ).getAttribute("value");


                return beforeTax != null
                        && !beforeTax.trim().isEmpty()
                        && tax != null
                        && !tax.trim().isEmpty()
                        && net != null
                        && !net.trim().isEmpty();

            } catch (Exception e) {

                return false;
            }
        });
    }
    @Override
    public ReportTotals getReportTotals() {

        ReportTotals totals = new ReportTotals();

        totals.setTotal(getInvoiceReportTotal("الاجمالي (غير شامل ضريبة القيمة المضافة)"));

        totals.setDiscount(getInvoiceReportTotal("اجمالي الخصومات"));

        totals.setBeforeTax(
                getInvoiceReportTotal("الاجمالي الخاضع للضريبة (غير شامل الضريبة )")
        );

        totals.setTax(getInvoiceReportTotal("ضريبة القيمة المضافة %15"));

        totals.setNet(getInvoiceReportTotal("الصافي بعد الضريبة"));

        return totals;
    }

    private BigDecimal getInvoiceReportTotal(String label) {
        String xpath = String.format(
                "//tr[.//*[normalize-space()='%s']]//td[1]//div[last()]",
                label
        );
        String displayedValue = driver.findElement(By.xpath(xpath)).getText().trim();
        String normalizedValue = displayedValue
                .replace(",", "")
                .replace("٬", "")
                .replace("٫", ".")
                .replaceAll("\\s+", "");
        return new BigDecimal(normalizedValue);
    }


    public Map<String, String> GetNamesQuantitiesAndPricesForInv() {

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
