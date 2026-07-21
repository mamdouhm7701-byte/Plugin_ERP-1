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

public class AddProductsPage extends Abstract {

    //Constructors
    public AddProductsPage(WebDriver driver) {
        super(driver);
    }

    //locators
    private By OpenProducts = By.xpath("//span[contains(@class,'menu-title') and contains(text(),'الأصناف')]");
    private By OpenAddProduct = By.xpath("//span[@class='menu-title text-truncate' and text()='قائمة الاصناف']");
    private By AddProduct = By.xpath("//button[contains(text(),'جديد')]");
    private By ProductName = By.xpath("//div[@data-title='الاسم العربي']//input");
    private By ProductEnglishName = By.xpath("//div[@data-title='الاسم الإنجليزي']//input");
    private By Barcode = By.xpath("//input[@name='mainUnitBarcode']");
    private By SalePrice = By.xpath("//input[@name='salesPrice']");
    private By MiniSalePrice = By.xpath("//input[@name='minSalesPrice']");
    private By MaxSalePrice = By.xpath("//input[@name='maxSalesPrice']");
    private By PlaceOfProduction = By.xpath("//div[@data-title='مكان الصنف']//input[@type='text']");
    private By OpenPlaceOfProduction = By.xpath("//div[@data-title='الرصيد الإفتتاحي']//input[@type='number']");
    private By CostPrice = By.xpath("//div[@data-title='سعر التكلفة']//input[@type='number']");
    private By LowestAvailableQuantity = By.xpath("//div[@data-title='الحد الأدني']//input");
    private By AddStore = By.xpath("//button[contains(.,'اضافة مخزن')]");
    private By UnitProduct = By.xpath("//a[contains(.,'وحدات الصنف')]");
    private By TransUnit = By.xpath("//div[@data-title='معامل التحويل']//input[@type='number']");
    private By AddBarcode = By.xpath("(//div[@data-title='الباركود']//input)[3]");
    private By AddSalePrice = By.xpath("//div[@data-title='سعر البيع']//input[@type='number']");
    private By AddMiniSalePrice = By.xpath("//div[@data-title='أقل سعر بيع']//input[@type='number']");
    private By AddMaxSalePrice = By.xpath("//input[@name='maxSalesPrice']");
    private By AddUnite = By.xpath("//button[contains(.,'اضافة وحدة')]");
    private By CntrolleProduct = By.xpath("//a[contains(.,'التحكم في الأصناف')]");
    private By CompletedProduct = By.xpath("//input[@value='PerfectlyMade']");
    private By Component = By.xpath("//a[@role='tab' and text()='المكونات']");
    private By AddComponent = By.xpath("//button[contains(text(),'اضافة مكونات')]");
    private By SerchComponent = By.xpath("//input[@id='arabicName']");
    private By CloseComponent = By.xpath("//button[contains(.,'إغلاق')]");
    private By ComponentQuantity = By.xpath("//input[@type='number' and contains(@class,'form-control-sm')]");
    private By ComponetNotest = By.xpath("//input[@id='textarea']");
    private By DeletStore = By.xpath("//*[contains(@id,'delete-icon')]");
    private By SaveEditeButton = By.cssSelector("button[data-action-type='save']");
    private By UniteNameRequiredMsg = By.xpath("//small[@class='text-danger' and text()='mainUnitBarcode مطلوب']");

    //Methods

    @Step("فتح صفحة إضافة الأصناف")
    public void OpenAddProductPage() {
        utilites.ClickElement(OpenProducts);
        utilites.ClickElement(OpenAddProduct);
        utilites.ClickElement(OpenProducts);
    }

    @Step("الضغط على زر إضافة صنف جديد")
    public void ClickNewProduct() {
        utilites.waits().waitForOverlayToDisappear();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        utilites.ClickElement(AddProduct);
        utilites.waits().waitForOverlayToDisappear();

    }

    @Step("إدخال اسم الصنف العربي: {arabicName}")
    public void AddProductName(String arabicName) {
        utilites.waits().waitForOverlayToDisappear();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        utilites.ClickElement(UnitProduct);
    }

    @Step("إدخال معامل التحويل: {transUnit}")
    public void AddTransUnit(String transUnit) {
        utilites.TaypeTextB(TransUnit, transUnit);
    }

    @Step("إدخال الباركود للوحدة الثانية: {addBarcode}")
    public void AddBarcode_2(String addBarcode) {
        utilites.TaypeTextB(AddBarcode, addBarcode);
    }

    @Step("إدخال سعر البيع للوحدة الثانية: {addSalePrice}")
    public void AddSalePrice_2(String addSalePrice) {
        utilites.TaypeTextB(AddSalePrice, addSalePrice);
    }

    @Step("إدخال أقل سعر بيع للوحدة الثانية: {addMiniSalePrice}")
    public void AddMiniSalePrice_2(String addMiniSalePrice) {
        utilites.TaypeTextB(AddMiniSalePrice, addMiniSalePrice);
    }

    @Step("إدخال أعلى سعر بيع للوحدة الثانية: {addMaxSalePrice}")
    public void AddMaxSalePrice_2(String addMaxSalePrice) {
        utilites.TaypeTextB(AddMaxSalePrice, addMaxSalePrice);
    }

    @Step("الضغط على زر إضافة وحدة")
    public void ClickAddUnit() {
        utilites.ClickElement(AddUnite);
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
        utilites.ClickElement(CompletedProduct);
    }

    @Step("الضغط على تبويب المكونات")
    public void ClickComponentTab() {
        utilites.ClickElement(Component);
    }

    @Step("الضغط على زر إضافة مكونات")
    public void ClickAddComponent() {
        utilites.ClickElement(AddComponent);
    }

    @Step("البحث عن مكون: {searchComponent}")
    public void SearchComponent(String searchComponent) {
        utilites.TaypeTextB(SerchComponent, searchComponent);
    }

    @Step("اختيار الصنف: {productName}")
    public void SelectProduct(String productName) {
        utilites.dropdown().selectProductByName(productName);
    }

    @Step("الضغط على زر إغلاق نافذة المكونات")
    public void ClickCloseComponent() {
        utilites.ClickElement(CloseComponent);
    }

    @Step("إدخال كمية المكون: {value}")
    public void AddComponentQuantity(String value) {
        utilites.TaypeTextB(ComponentQuantity, value);
    }

    @Step("إدخال ملاحظات المكون: {notes}")
    public void AddComponentNotes(String notes) {
        utilites.TaypeTextB(ComponetNotest, notes);
    }

    @Step("التحقق من ظهور رسالة خطأ الباركود الإلزامي")
    public boolean AssertBarcodeRequired() {
        return utilites.isDisplayed(UniteNameRequiredMsg);
    }
}