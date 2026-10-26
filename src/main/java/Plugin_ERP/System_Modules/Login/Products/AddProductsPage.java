package Plugin_ERP.System_Modules.Login.Products;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import Plugin_ERP.System_Modules.Login.Abstract;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class AddProductsPage extends ProductsModulePage {
    private final ProductComponents components;
    private final ProductUnits units;
    @Step("ربط الصنف بالمخزن {store}: رصيد {opening}، حد أدنى {minimum}، تكلفة {cost}")
    public void prStock(String store, String opening, String minimum, String cost) {
        prChoose("مخازن", store);
        prTitledField("مكان الصنف", "QA");
        prTitledField("الرصيد الإفتتاحي", opening);
        prTitledField("الحد الأدني", minimum);
        prTitledField("سعر التكلفة", cost);
        prButton("اضافة مخزن");
    }

    @Step("إضافة الوحدة الفرعية {unit}: باركود {barcode}، معامل تحويل {rate}")
    public void prSubunit(String unit, String barcode, String rate) {
        prTab("وحدات الصنف");
        prChoose("الوحدة", unit);
        prTitledField("معامل التحويل", rate);
        prType(
                By.cssSelector("[data-title='الباركود'] input:not([name='mainUnitBarcode'])"),
                barcode);
        prButton("اضافة وحدة");
    }

    @Step("إضافة المكون {item} من المخزن {store} بكمية {quantity}")
    public void prComponent(String store, String item, String quantity) {
        prTab("المكونات");
        prChoose("المخزن", store);
        prButton("اضافة مكونات");
        prPickItem(item);
        prClosePicker();
        prType(
                By.xpath(
                        "//form//tr[.//td[contains(normalize-space(),'"
                                + item
                                + "')]]//input[@type='number'][1]"),
                quantity);
    }

    // Constructors
    public AddProductsPage(WebDriver driver) {
        super(driver);
        units = new ProductUnits(this, driver);
        components = new ProductComponents(this, driver);
    }

    // locators
    private By OpenProducts =
            By.xpath("//span[contains(@class,'menu-title') and contains(text(),'الأصناف')]");
    private By OpenAddProduct =
            By.xpath("//span[@class='menu-title text-truncate' and text()='قائمة الاصناف']");
    private By AddProduct = By.xpath("//button[contains(text(),'جديد')]");
    private By ProductName = By.xpath("//div[@data-title='الاسم العربي']//input");
    private By ProductEnglishName = By.xpath("//div[@data-title='الاسم الإنجليزي']//input");
    private By Barcode = By.xpath("//input[@name='mainUnitBarcode']");
    private By SalePrice = By.xpath("//input[@name='salesPrice']");
    private By MiniSalePrice = By.xpath("//input[@name='minSalesPrice']");
    private By MaxSalePrice = By.xpath("//input[@name='maxSalesPrice']");
    private By PlaceOfProduction = By.xpath("//div[@data-title='مكان الصنف']//input[@type='text']");
    private By OpenPlaceOfProduction =
            By.xpath("//div[@data-title='الرصيد الإفتتاحي']//input[@type='number']");
    private By CostPrice = By.xpath("//div[@data-title='سعر التكلفة']//input[@type='number']");
    private By LowestAvailableQuantity = By.xpath("//div[@data-title='الحد الأدني']//input");
    private By AddStore = By.xpath("//button[contains(.,'اضافة مخزن')]");

    private By AddBarcode = By.xpath("(//div[@data-title='الباركود']//input)[3]");

    private By AddMiniSalePrice =
            By.xpath("//div[@data-title='أقل سعر بيع']//input[@type='number']");
    private By AddMaxSalePrice = By.xpath("//input[@name='maxSalesPrice']");

    private By CntrolleProduct = By.xpath("//a[contains(.,'التحكم في الأصناف')]");

    private By DeletStore = By.xpath("//*[contains(@id,'delete-icon')]");
    private By SaveEditeButton = By.cssSelector("button[data-action-type='save']");
    private By UniteNameRequiredMsg =
            By.xpath("//small[@class='text-danger' and text()='mainUnitBarcode مطلوب']");

    // Methods

    @Step("فتح صفحة إضافة الأصناف")
    public void OpenAddProductPage() {
        utilites.ClickElement(OpenProducts);
        utilites.ClickElement(OpenAddProduct);
        utilites.ClickElement(OpenProducts);
    }

    @Step("الضغط على زر إضافة صنف جديد")
    public void ClickNewProduct() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.waits().waitForScreenReady(AddProduct);
        utilites.ClickElement(AddProduct);
        utilites.waits().waitForOverlayToDisappear();
    }

    @Step("إدخال اسم الصنف العربي: {arabicName}")
    public void AddProductName(String arabicName) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.waits().waitForScreenReady(ProductName);
        utilites.TaypeTextB(ProductName, arabicName);
    }

    @Step("إدخال اسم الصنف الإنجليزي: {englishName}")
    public void AddProductEnglishName(String englishName) {
        utilites.TaypeTextB(ProductEnglishName, englishName);
    }

    @Step("إدخال الباركود: {barcode}")
    public void AddBarcode(String barcode) {
        utilites.TaypeTextB(Barcode, barcode);
    }

    @Step("إدخال سعر البيع: {salePrice}")
    public void AddSalesPrice(String salePrice) {
        utilites.TaypeTextB(SalePrice, salePrice);
    }

    @Step("إدخال أقل سعر بيع: {miniSalePrice}")
    public void AddMiniSalePrice(String miniSalePrice) {
        utilites.TaypeTextB(MiniSalePrice, miniSalePrice);
    }

    @Step("إدخال أعلى سعر بيع: {maxSalePrice}")
    public void AddMaxSalePrice(String maxSalePrice) {
        utilites.TaypeTextB(MaxSalePrice, maxSalePrice);
    }

    @Step("إدخال مكان الصنف: {placeOfProduction}")
    public void AddPlace(String placeOfProduction) {
        utilites.TaypeTextB(PlaceOfProduction, placeOfProduction);
    }

    @Step("إدخال سعر التكلفة: {costPrice}")
    public void AddCostPrice(String costPrice) {
        utilites.TaypeTextB(CostPrice, costPrice);
    }

    @Step("إدخال الرصيد الافتتاحي: {OpenAmount}")
    public void AddOpenAmount(String OpenAmount) {
        utilites.TaypeText(OpenPlaceOfProduction, OpenAmount);
    }

    @Step("إدخال الحد الأدنى للكمية المتاحة: {lowestAvailableQuantity}")
    public void AddLowestAvailableQuantity(String lowestAvailableQuantity) {
        utilites.TaypeTextB(LowestAvailableQuantity, lowestAvailableQuantity);
    }

    @Step("إضافة مخزن للصنف")
    public void AddStore() {
        utilites.ClickElement(AddStore);
    }

    @Step("الضغط على تبويب وحدات الصنف")
    public void ClickUnitProduct() {
        units.ClickUnitProduct();
    }

    @Step("إدخال معامل التحويل: {transUnit}")
    public void AddTransUnit(String transUnit) {
        units.AddTransUnit(transUnit);
    }

    @Step("إدخال الباركود للوحدة الثانية: {addBarcode}")
    public void AddBarcode_2(String addBarcode) {
        units.AddBarcode_2(addBarcode);
    }

    @Step("إدخال سعر البيع للوحدة الثانية: {addSalePrice}")
    public void AddSalePrice_2(String addSalePrice) {
        units.AddSalePrice_2(addSalePrice);
    }

    @Step("إدخال أقل سعر بيع للوحدة الثانية: {addMiniSalePrice}")
    public void AddMiniSalePrice_2(String addMiniSalePrice) {
        units.AddMiniSalePrice_2(addMiniSalePrice);
    }

    @Step("إدخال أعلى سعر بيع للوحدة الثانية: {addMaxSalePrice}")
    public void AddMaxSalePrice_2(String addMaxSalePrice) {
        units.AddMaxSalePrice_2(addMaxSalePrice);
    }

    @Step("الضغط على زر إضافة وحدة")
    public void ClickAddUnit() {
        units.ClickAddUnit();
    }

    @Step("الضغط على تبويب التحكم في الأصناف")
    public void ClickControlProduct() {
        utilites.ClickElement(CntrolleProduct);
    }

    @Step("الضغط على زر الحفظ")
    public void ClickSaveEditButton() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(SaveEditeButton);
        utilites.waits().waitForOverlayToDisappear();
    }

    @Step("الضغط على زر حذف المخزن")
    public void ClickDeleteStore() {
        utilites.ClickElement(DeletStore);
    }

    @Step("تحديد الصنف كمنتج مكتمل الصنع")
    public void ClickCompletedProduct() {
        components.ClickCompletedProduct();
    }

    @Step("الضغط على تبويب المكونات")
    public void ClickComponentTab() {
        components.ClickComponentTab();
    }

    @Step("الضغط على زر إضافة مكونات")
    public void ClickAddComponent() {
        components.ClickAddComponent();
    }

    @Step("البحث عن مكون: {searchComponent}")
    public void SearchComponent(String searchComponent) {
        components.SearchComponent(searchComponent);
    }

    @Step("اختيار الصنف: {productName}")
    public void SelectProduct(String productName) {
        components.SelectProduct(productName);
    }

    @Step("الضغط على زر إغلاق نافذة المكونات")
    public void ClickCloseComponent() {
        components.ClickCloseComponent();
    }

    @Step("إدخال كمية المكون: {value}")
    public void AddComponentQuantity(String value) {
        components.AddComponentQuantity(value);
    }

    @Step("إدخال ملاحظات المكون: {notes}")
    public void AddComponentNotes(String notes) {
        components.AddComponentNotes(notes);
    }

    @Step("التحقق من ظهور رسالة خطأ الباركود الإلزامي")
    public boolean AssertBarcodeRequired() {
        return utilites.isDisplayed(UniteNameRequiredMsg);
    }
}
