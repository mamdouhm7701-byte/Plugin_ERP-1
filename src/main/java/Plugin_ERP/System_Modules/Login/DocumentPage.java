/* شرح الملف: مساعدة لتسجيل بنود المستند وكمياتها وأسعارها وخصوماتها ومدفوعاتها. فائدتها مراجعة
 * المبالغ ومطابقة المستند مع النسخة المطبوعة.
 */
package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.Models.TaxMode;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.*;

public abstract class DocumentPage extends Abstract {
    protected DocumentPage(WebDriver driver) {
        super(driver);
        quantityEditor = new StoreQuantityEditor(this, driver);
        productSearch = new StoreProductSearch(this, driver);
        itemControls = new DocumentItemControls(this, driver);
        gridEditing = new DocumentGridEditing(this, driver);
        quantityPrint = new DocumentQuantityPrintCheck(this, driver);
        businessValidation = new InvoiceBusinessValidation(this);
        invoiceRows = new PurchaseInvoiceRows(driver);
        paymentControls = new DocumentPaymentControls(this, driver);
        columnEditing = new DocumentColumnEditing(this, driver);
        snapshotReader = new PurchaseInvoiceSnapshot(this, driver);
        printChecks = new DocumentPrintChecks(this, driver);
        summaryReader = new DocumentSummaryReader(this, driver);
    }

    private final DocumentQuantityPrintCheck quantityPrint;
    private final DocumentGridEditing gridEditing;
    private final DocumentItemControls itemControls;
    private final StoreQuantityEditor quantityEditor;
    private final StoreProductSearch productSearch;

    private By AssertExsistItemTaype = By.xpath("//small[contains(text(),'نوع البند مطلوب')]");

    private By AssertExsistCustomre = By.xpath("//small[contains(text(),'العملاء مطلوب')]");
    private By AssertExpenditureCategories =
            By.xpath("//small[contains(text(),'جهات الصرف مطلوب')]");
    private By AssertAgnt = By.xpath("//small[contains(text(),'المندوبين مطلوب')]");
    private By AssertSupplier = By.xpath("//small[contains(text(),'الموردين مطلوب')]");

    // Methods
    @Step("التأكيد على ان نوع البند مطلوب")
    public Boolean AssertExsistItemTaype() {
        return itemControls.AssertExsistItemTaype();
    }

    @Step("التأكيد على ان العملاء مطلوب")
    public Boolean AssertExsistCustomre() {
        return itemControls.AssertExsistCustomre();
    }

    @Step("التأكيد على ان جهات الصرف مطلوب")
    public Boolean AssertExpenditureCategories() {
        return itemControls.AssertExpenditureCategories();
    }

    @Step("التأكيد على ان المندوبي مطلوب")
    public Boolean AssertAgnt() {
        return itemControls.AssertAgnt();
    }

    @Step("التاكيد ان المخزن مطلوب")
    public Boolean AssertSotreRequired() {
        return itemControls.AssertSotreRequired();
    }

    @Step("التأكيد ان الموردين مطلوب")
    public Boolean AssertSupplier() {
        return itemControls.AssertSupplier();
    }

    @Step("الضغط على ايقونه الباركود")
    public void ClickOnBarcodeRadioBtn() {
        itemControls.ClickOnBarcodeRadioBtn();
    }

    @Step("الضغط على ايقونه الاسم")
    public void ClickOnNameRadioBtn() {
        itemControls.ClickOnNameRadioBtn();
    }

    @Step("البحث عن : {searchText}")
    public void Search(String searchText) {
        itemControls.Search(searchText);
    }

    @Step("الضغط على حفظ من قائمه الاصناف")
    public void ClickSaveForProductBtn() {
        itemControls.ClickSaveForProductBtn();
    }

    @Step("الضغط على زرار اضافه الاصناف")
    public void ClickAddItemsBtn() {
        itemControls.ClickAddItemsBtn();
    }

    @Step("البحث عن منتج واختياره من القائمة البحث بالاسم")
    public void SearchAndSelectProduct(String productName) {
        productSearch.SearchAndSelectProduct(productName);
    }

    /**
     * Invoice-specific selection path. Once the suggestion is clicked, do not click it again just
     * because the invoice grid updates asynchronously.
     */
    protected void searchAndSelectInvoiceProduct(String productName) {
        productSearch.searchAndSelectInvoiceProduct(productName);
    }

    @Step("الضغط على زر الزياده لأول عدد أصناف معين")
    public void ClickPlusButtonByRow(int numberOfRows, int timesPerRow) {
        utilites.ClickPlusButtonByRow(numberOfRows, timesPerRow);
    }

    @Step("الضغط على زر النقصان لأول عدد أصناف معين")
    public void ClickMinusButtonByRow(int numberOfRows, int timesPerRow) {
        utilites.ClickMinusButtonByRow(numberOfRows, timesPerRow);
    }

    public void debugPrintAllRows() {
        utilites.grid().debugPrintAllRows();
    }

    public void WaitEditeForBills() {
        gridEditing.WaitEditeForBills();
    }

    @Step("حذف الصفوف: {rowNumbers}")
    public void clickDeleteRows(int... rowNumbers) {
        gridEditing.clickDeleteRows(rowNumbers);
    }

    @Step("التعديل على اخر صف موجود")
    public void EditForBills() {
        utilites.waits().waitForOverlayToDisappear();
        super.Edit();
        WaitEditeForBills();
        LogUtils.info("تم الضغط على تعديل اخر صف موجود");
    }

    @Step("التعديل على اخر صف موجود")
    public void EditForDamage() {
        gridEditing.EditForDamage();
    }

    @Step("حذف سند تسوية التالف")
    public void DeleteForDamage() {
        gridEditing.DeleteForDamage();
    }

    @Step("اختيار الكميات : {rowNumber} {quantity}")
    public void SetQuantityByRow(int rowNumber, int quantity) {
        quantityEditor.SetQuantityByRow(rowNumber, quantity);
    }

    public String normalizeQuantity(String value) {
        return quantityEditor.normalizeQuantity(value);
    }

    @Step("اضافة الكمية فى سطر : {values}")
    public void SetQuantitiesForRows(int... values) {
        quantityEditor.SetQuantitiesForRows(values);
    }

    public Map<String, String> GetNamesAndQuantities() {
        return quantityEditor.GetNamesAndQuantities();
    }

    @Step("مقارنة الأسماء والكميات مع التقرير")
    public void CompareNamesAndQuantitiesWithReport(Map<String, String> expectedData) {
        quantityPrint.CompareNamesAndQuantitiesWithReport(expectedData);
    }

    public void waitUntilMessageDisappear(String messageText) {
        quantityPrint.waitUntilMessageDisappear(messageText);
    }

    @Step("حذف كل :{Text}")
    public void DeleteByName(String Text) {
        gridEditing.DeleteByName(Text);
    }

    private final DocumentSummaryReader summaryReader;
    private final DocumentPrintChecks printChecks;
    private final PurchaseInvoiceSnapshot snapshotReader;
    private final DocumentColumnEditing columnEditing;
    private final DocumentPaymentControls paymentControls;
    private final InvoiceBusinessValidation businessValidation;
    private final PurchaseInvoiceRows invoiceRows;

    private By ClickCash = By.xpath("//label[.//span[normalize-space()='خزينة']]");
    private By ClickOtherPay = By.xpath("//label[.//span[normalize-space()='آخرى']]");
    private By ClickPlusToAddPay =
            By.xpath("(//button[contains(@class,'btn-gradient-primary')])[2]");

    @Step("الضغط على طريقه الدفع خزينة")
    public void ClickCash() {
        paymentControls.ClickCash();
    }

    @Step("الضغط على طريقه الدفع اخري")
    public void ClickOtherPay() {
        paymentControls.ClickOtherPay();
    }

    @Step("الضغط على علامه ال+ لاضافه طريقه الدفع")
    public void ClickPlusToAddPay() {
        paymentControls.ClickPlusToAddPay();
    }

    @Step("اضافه خصم النسبة")
    public void AddDiscountPercntge(String discountPercntge) {
        paymentControls.AddDiscountPercntge(discountPercntge);
    }

    @Step("اضافه خصم القيمة")
    public void AddDiscountValue(String discountValue) {
        paymentControls.AddDiscountValue(discountValue);
    }

    // Purches

    @Step("تعديل قيمة الصف {rowNumber} فى العمود {columnName} إلى {value}")
    public void SetValueByRow(String columnName, int rowNumber, int value) {
        columnEditing.SetValueByRow(columnName, rowNumber, value);
    }

    @Step("إضافة قيم فى العمود {columnName}")
    public void SetValuesForRows(String columnName, int... values) {
        columnEditing.SetValuesForRows(columnName, values);
    }

    public Map<String, String> GetNamesQuantitiesAndPrices() {
        return snapshotReader.GetNamesQuantitiesAndPrices();
    }

    @Step("مقارنة الأسماء والكميات والأسعار مع التقرير")
    public void CompareNamesQuantitiesAndPricesWithReport(Map<String, String> expectedData) {
        printChecks.CompareNamesQuantitiesAndPricesWithReport(expectedData);
    }

    public List<InvoiceRow> getInvoiceRows() {
        return invoiceRows.getInvoiceRows();
    }

    protected BigDecimal getSummaryValue(String label) {
        return summaryReader.getSummaryValue(label);
    }

    public BigDecimal getTotal() {
        return summaryReader.getTotal();
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

    protected BigDecimal getReportTotal(String label) {
        return printChecks.getReportTotal(label);
    }

    public ReportTotals getReportTotals() {
        return printChecks.getReportTotals();
    }

    public void attachBusinessValidation(InvoiceTotals totals) {
        businessValidation.attachBusinessValidation(totals);
    }

    public void attachReportValidation(
            ReportTotals reportTotals, InvoiceTotals totals, boolean isExclusive) {
        businessValidation.attachReportValidation(reportTotals, totals, isExclusive);
    }

    @Step("مطابقة إجماليات الفاتورة مع القيم المتوقعة: {totals}")
    public void assertInvoiceTotals(InvoiceTotals totals) {
        businessValidation.assertInvoiceTotals(totals);
    }

    // TODO:
    // Current report parsing depends on report labels.
    // Replace with a generic report parser in the future to support customized reports.
    @Step("مطابقة إجماليات التقرير المطبوع مع الفاتورة: {expected}")
    public void assertReportTotals(ReportTotals report, InvoiceTotals expected, TaxMode taxMode) {
        businessValidation.assertReportTotals(report, expected, taxMode);
    }

    public void attachReportValidation(ReportTotals reportTotals, InvoiceTotals totals) {
        businessValidation.attachReportValidation(reportTotals, totals);
    }

    // TODO:
    // Current report parsing depends on report labels.
    // Replace with a generic report parser in the future to support customized reports.
    @Step("مطابقة إجماليات التقرير المطبوع مع الفاتورة: {expected}")
    public void assertReportTotals(ReportTotals report, InvoiceTotals expected) {
        businessValidation.assertReportTotals(report, expected);
    }

    @Step("احتساب  ارقام الفاتوره ومطابقتها بالفرونت")
    public InvoiceTotals validateBusinessTotals() {
        return businessValidation.validateBusinessTotals();
    }

    @Step("الاحتفاظ بارقام الفواتير لمراجعتها مع التقرير")
    public void saveAndPrintForReport() {
        Map<String, String> dataBeforeSave = GetNamesQuantitiesAndPrices();

        ClickSaveAndPrintButton();

        CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
    }

    @Step("مراجعه ارقام الفاتوره بالتقرير")
    public void validateReport(InvoiceTotals totals) {

        ReportTotals reportTotals = getReportTotals();

        attachReportValidation(reportTotals, totals);

        assertReportTotals(reportTotals, totals);

        CloserReport();
    }

    @Step("احتساب الفاتورة ومطابقتها مع الفرونت والحفظ والمطابقه مع التقرير")
    public void validateInvoiceAllBusiness() {

        InvoiceTotals totals = validateBusinessTotals();

        saveAndPrintForReport();

        validateReport(totals);
    }
}
