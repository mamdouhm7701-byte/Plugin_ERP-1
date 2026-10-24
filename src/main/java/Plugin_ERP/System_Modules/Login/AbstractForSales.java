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

public class AbstractForSales extends DocumentPage {
    private final SalesPrintedTotals printedTotals;
    private final SalesSummaryReader salesSummary;
    private final SalesFormControls formControls;
    private final SalesInvoiceSnapshot snapshotReader;
    private final SalesItemDialog itemDialog;
    private final SalesDropdownControls dropdownControls;
    private final SalesInvoiceRows invoiceRows;
    public AbstractForSales(WebDriver driver) {
        super(driver);
        snapshotReader = new SalesInvoiceSnapshot(driver);
        itemDialog = new SalesItemDialog(this, driver);
        dropdownControls = new SalesDropdownControls(driver);
        invoiceRows = new SalesInvoiceRows(driver);
        formControls = new SalesFormControls(this, driver);
        salesSummary = new SalesSummaryReader(this, driver);
        printedTotals = new SalesPrintedTotals(this, driver);
    }

    @Step("قراءة خيارات القائمة المنسدلة: {label}")
    public List<String> getDropdownOptions(String label) {
        return dropdownControls.getDropdownOptions(label);
    }

    @Step("قراءة الاختيار الحالي من القائمة المنسدلة: {label}")
    public String getSelectedDropdownOption(String label) {
        return dropdownControls.getSelectedDropdownOption(label);
    }

    @Step("البحث في نافذة إضافة الأصناف باستخدام {fieldLabel}: {query}")
    public void searchInAddItemsDialog(String fieldLabel, String query) {
        itemDialog.searchInAddItemsDialog(fieldLabel, query);
    }

    @Step("قراءة صفوف الأصناف الظاهرة في نافذة الإضافة")
    public List<String> getAddItemsDialogRows() {
        return itemDialog.getAddItemsDialogRows();
    }

    @Step("تغيير خيار عرض الأصناف المختارة إلى {shouldBeChecked}")
    public void setShowSelectedItems(boolean shouldBeChecked) {
        itemDialog.setShowSelectedItems(shouldBeChecked);
    }

    @Step("التحقق من ظهور خانة اسم العميل")
    public boolean isCustomerNameFieldDisplayed() {
        return formControls.isCustomerNameFieldDisplayed();
    }

    @Step("تبديل وضع عرض شاشة فاتورة المبيعات")
    public void toggleInvoiceFullscreen() {
        formControls.toggleInvoiceFullscreen();
    }

    @Step("التحقق من ظهور القائمة الجانبية")
    public boolean isInvoiceSideMenuDisplayed() {
        return formControls.isInvoiceSideMenuDisplayed();
    }

    @Step("الضغط على اختصار إضافة عميل جديد")
    public void clickAddCustomerShortcut() {
        formControls.clickAddCustomerShortcut();
    }

    @Step("الضغط على اختصار إضافة صنف جديد")
    public void clickAddProductShortcut() {
        formControls.clickAddProductShortcut();
    }

    @Step("بدء فاتورة مبيعات جديدة")
    public void clickNewInvoice() {
        formControls.clickNewInvoice();
    }

    @Step("انتظار نافذة إضافة بيانات جديدة")
    public boolean isFormDialogDisplayed() {
        return formControls.isFormDialogDisplayed();
    }

    /** Sales-invoice search override: avoid retrying a successful suggestion click. */
    @Override
    @Step("البحث عن الصنف {productName} وإضافته إلى الفاتورة")
    public void SearchAndSelectProduct(String productName) {
        formControls.SearchAndSelectProduct(productName);
    }

    // =========================================
    // Override
    // =========================================
    @Override
    protected BigDecimal getSummaryValue(String label) {
        return salesSummary.getSummaryValue(label);
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

        InvoiceTotals totals = InvoiceCalculator.calculate(getInvoiceRows(), getDiscount());

        attachBusinessValidation(totals);

        System.out.println(totals);

        assertInvoiceTotals(totals);

        return totals;
    }

    // =========================================
    // Get Invoice Rows
    // =========================================

    public List<InvoiceRow> getInvoiceRows() {
        return invoiceRows.getInvoiceRows();
    }

    // =========================================
    // Save + Report
    // =========================================

    @Step("الاحتفاظ بأرقام الفواتير لمراجعتها مع التقرير")
    public void saveAndPrintForReport() {

        Map<String, String> dataBeforeSave = getNamesQuantitiesAndPrices();

        ClickSaveAndPrintButton();

        CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
    }

    public Map<String, String> getNamesQuantitiesAndPrices() {
        return snapshotReader.getNamesQuantitiesAndPrices();
    }

    // =========================================
    // Wait Summary
    // =========================================

    public void waitUntilSummaryValuesLoaded() {
        salesSummary.waitUntilSummaryValuesLoaded();
    }

    @Override
    public ReportTotals getReportTotals() {
        return printedTotals.getReportTotals();
    }

    public Map<String, String> GetNamesQuantitiesAndPricesForInv() {
        return snapshotReader.GetNamesQuantitiesAndPricesForInv();
    }

    @Step("البحث عن : {searchText}")
    public void SearchBarcodForInv(String searchText) {
        formControls.SearchBarcodForInv(searchText);
    }

    @Step("الضغط على ايقونه الاسم وارسال الاسم {searchText}")
    public void ClickOnNameRadioBtnForInv(String searchText) {
        formControls.ClickOnNameRadioBtnForInv(searchText);
    }
    java.math.BigDecimal readDefaultSummary(String label) { return super.getSummaryValue(label); }
}
