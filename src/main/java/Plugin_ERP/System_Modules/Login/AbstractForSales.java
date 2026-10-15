package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
import java.util.stream.Collectors;

public class AbstractForSales extends AbstractForPurchases{
    public AbstractForSales(WebDriver driver){super(driver);}
    private By ArabicNameInput = By.xpath("//input[@name='arabicName']");
    private By SearchInput = By.xpath("//input[@id='barcode' and @name='barcode']");
    private static final By INVOICE_FULLSCREEN_TOGGLE = By.xpath(
            "//button[normalize-space()='توزيع مراكز التكلفة']/following-sibling::button[2]"
    );
    private static final By INVOICE_SIDE_MENU = By.xpath(
            "//a[@aria-label='الصفحة الرئيسية'] | //a[contains(@href,'#/invoice-list')]"
    );
    private static final By ADD_CUSTOMER_SHORTCUT = By.xpath(
            "//*[normalize-space()='العميل']/following::*[self::button or self::a or @role='button' or self::img][1] | " +
                    "//input[@id='vs3__combobox']/ancestor::*[contains(@class,'vs__dropdown-toggle')][1]" +
                    "//button | //input[@id='vs3__combobox']/ancestor::*[contains(@class,'vs__dropdown-toggle')][1]//img"
    );
    private static final By ADD_PRODUCT_SHORTCUT = By.xpath(
            "//button[contains(@title,'إضافة صنف') or contains(@aria-label,'إضافة صنف') or " +
                    "contains(@data-bs-original-title,'إضافة صنف')] | " +
                    "//*[contains(@title,'إضافة صنف') or contains(@aria-label,'إضافة صنف') or " +
                    "contains(@alt,'إضافة صنف')]"
    );
    private static final By FORM_DIALOG = By.cssSelector(".modal-content");



    @Step("قراءة خيارات القائمة المنسدلة: {label}")
    public List<String> getDropdownOptions(String label) {
        By dropdownInput = dropdownInput(label);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(dropdownInput));
        input.click();

        String listboxId = input.getAttribute("aria-controls");
        if (listboxId == null || listboxId.isBlank()) {
            String comboboxId = input.findElement(By.xpath("ancestor::div[contains(@class,'v-select')]")).getAttribute("id");
            listboxId = comboboxId == null ? "" : comboboxId.replace("__combobox", "__listbox");
        }
        if (listboxId.isBlank()) {
            throw new IllegalStateException("تعذر تحديد قائمة الخيارات للحقل: " + label);
        }

        By optionsLocator = By.cssSelector("#" + listboxId + " li");
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionsLocator));
        List<WebElement> options = driver.findElements(optionsLocator).stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());

        List<String> optionTexts = options.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
        input.sendKeys(org.openqa.selenium.Keys.ESCAPE);
        return optionTexts;
    }

    @Step("قراءة الاختيار الحالي من القائمة المنسدلة: {label}")
    public String getSelectedDropdownOption(String label) {
        By selectedOption = selectedDropdownOption(label);
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(selectedOption))
                .getText()
                .trim();
    }

    @Step("البحث في نافذة إضافة الأصناف باستخدام {fieldLabel}: {query}")
    public void searchInAddItemsDialog(String fieldLabel, String query) {
        By radioOption = By.cssSelector(".modal-content input[type='radio'][value='" +
                (fieldLabel.equals("الباركود") ? "barcode" : "name") + "']");
        By searchField = By.cssSelector(".modal-content input[placeholder='بحث ....']");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(radioOption)).click();
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));
        input.click();
        input.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        input.sendKeys(org.openqa.selenium.Keys.BACK_SPACE);
        input.sendKeys(query);
        utilites.waits().waitForOverlayToDisappear();

        By updatedResult = By.xpath(
                "//div[contains(@class,'modal-content')]//tbody//tr[" +
                        "contains(normalize-space(.), '" + query + "') or " +
                        "contains(normalize-space(.), 'لا يوجد بيانات')]"
        );
        wait.until(ExpectedConditions.visibilityOfElementLocated(updatedResult));
    }

    @Step("قراءة صفوف الأصناف الظاهرة في نافذة الإضافة")
    public List<String> getAddItemsDialogRows() {
        By rowsLocator = By.cssSelector(".modal-content tbody tr");
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    List<WebElement> rows = d.findElements(rowsLocator).stream()
                            .filter(WebElement::isDisplayed)
                            .collect(Collectors.toList());
                    return rows.isEmpty() ? null : rows;
                })
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isEmpty() && !text.contains("لا يوجد بيانات"))
                .collect(Collectors.toList());
    }

    @Step("تغيير خيار عرض الأصناف المختارة إلى {shouldBeChecked}")
    public void setShowSelectedItems(boolean shouldBeChecked) {
        utilites.checkbox().stableFastSetCheckboxByText("عرض الأصناف المختارة", shouldBeChecked);
    }

    @Step("التحقق من ظهور خانة اسم العميل")
    public boolean isCustomerNameFieldDisplayed() {
        By nameField = By.xpath("//*[normalize-space()='اسم العميل']/following::input[1]");
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(nameField))
                .isDisplayed();
    }

    @Step("تبديل وضع عرض شاشة فاتورة المبيعات")
    public void toggleInvoiceFullscreen() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(INVOICE_FULLSCREEN_TOGGLE))
                .click();
    }

    @Step("التحقق من ظهور القائمة الجانبية")
    public boolean isInvoiceSideMenuDisplayed() {
        return driver.findElements(INVOICE_SIDE_MENU).stream().anyMatch(WebElement::isDisplayed);
    }

    @Step("الضغط على اختصار إضافة عميل جديد")
    public void clickAddCustomerShortcut() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(ADD_CUSTOMER_SHORTCUT))
                .click();
    }

    @Step("الضغط على اختصار إضافة صنف جديد")
    public void clickAddProductShortcut() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(ADD_PRODUCT_SHORTCUT))
                .click();
    }

    @Step("بدء فاتورة مبيعات جديدة")
    public void clickNewInvoice() {
        By newInvoiceButton = By.xpath("//button[normalize-space()='جديد']");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(newInvoiceButton))
                .click();
        utilites.waits().waitForOverlayToDisappear();
    }

    @Step("انتظار نافذة إضافة بيانات جديدة")
    public boolean isFormDialogDisplayed() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(FORM_DIALOG))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
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
        ).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());


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

            List<WebElement> quantityInputs = row.findElements(By.xpath(
                    ".//td[@aria-colindex='" + quantityColIndex + "']//input[not(@type='hidden')]"
            ));
            List<WebElement> priceInputs = row.findElements(By.xpath(
                    ".//td[@aria-colindex='" + priceColIndex + "']//input[not(@type='hidden')]"
            ));
            if (quantityInputs.isEmpty() || priceInputs.isEmpty()) {
                continue;
            }

            String name = nameCells.get(0)
                    .getText()
                    .trim();


            BigDecimal quantity = new BigDecimal(
                    quantityInputs.get(0)
                            .getAttribute("value")
                            .trim()
            );


            BigDecimal price = new BigDecimal(
                    priceInputs.get(0)
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

    private By dropdownInput(String label) {
        return switch (label) {
            case "المخزن" -> By.cssSelector(".v-select[name='StoreId'] input.vs__search");
            case "العميل", "العملاء" -> By.cssSelector("fieldset[label='arabicName'] input.vs__search");
            case "مندوب" -> By.cssSelector(".v-select[name='agent'] input.vs__search");
            case "قوائم الأسعار" -> By.cssSelector(".v-select[name='priceLists'] input.vs__search");
            default -> By.xpath(
                    "//*[self::legend or self::label][normalize-space()='" + label + "']" +
                            "/following::input[contains(@class,'vs__search')][1]"
            );
        };
    }

    private By selectedDropdownOption(String label) {
        if (label.equals("العميل") || label.equals("العملاء")) {
            return By.xpath("//input[@id='vs3__combobox']/ancestor::div[contains(@class,'vs__dropdown-toggle')][1]" +
                    "//*[contains(concat(' ', normalize-space(@class), ' '), ' vs__selected ')]");
        }
        return By.xpath(
                "//*[self::legend or self::label][normalize-space()='" + label + "']" +
                        "/following::*[contains(concat(' ', normalize-space(@class), ' '), ' vs__selected ')][1]"
        );
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
    @Step("البحث عن : {searchText}")
    public void SearchBarcodForInv(String searchText) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(SearchInput,searchText);
    }

    @Step("الضغط على ايقونه الاسم وارسال الاسم {searchText}")
    public void ClickOnNameRadioBtnForInv(String searchText) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ArabicNameInput );
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(ArabicNameInput, searchText);
    }


}
