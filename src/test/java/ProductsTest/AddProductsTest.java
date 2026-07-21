package ProductsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Products.AddProductsPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddProductsTest extends BaseTest {
    private String ProductURL="https://binfares.plugin-erp.com/#/Items/";

    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("إضافة صنف ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من إضافة صنف جديد ببيانات صحيحة، ويشمل إدخال الاسم العربي والإنجليزي وأسعار البيع والفئة والوحدة الأساسية والمخزن ومكان الصنف والرصيد الافتتاحي والحد الأدنى وسعر التكلفة، وإضافة وحدة تحويل ثانية، وتفعيل صلاحيات التحكم، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void addNewProductWithValidData() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.ClickNewProduct();
        addProductsPage.AddProductName("منتج جديد");
        addProductsPage.AddProductEnglishName("New Product");
        addProductsPage.AddBarcode(addProductsPage.generateRandomNumber());
        addProductsPage.AddSalesPrice("100");
        addProductsPage.AddMiniSalePrice("90");
        addProductsPage.AddMaxSalePrice("110");
        addProductsPage.SelectFromDropDawnByText("الفئة", "مقاولات");
        addProductsPage.SelectFromDropDawnByText("الوحدة الأساسية", "كجم");
        addProductsPage.SelectFromDropDawnByText("مخازن", "55");
        addProductsPage.AddPlace("مصنع 1");
        addProductsPage.AddOpenAmount("100");
        addProductsPage.AddLowestAvailableQuantity("50");
        addProductsPage.AddCostPrice("80");
        addProductsPage.AddStore();
        addProductsPage.ClickUnitProduct();
        addProductsPage.SelectFromDropDawnByText("الوحدة", "طن");
        addProductsPage.AddBarcode_2(addProductsPage.generateRandomNumber());
        addProductsPage.AddTransUnit("10");
        addProductsPage.AddSalePrice_2("120");
        addProductsPage.AddMiniSalePrice_2("110");
        addProductsPage.AddMaxSalePrice_2("130");
        addProductsPage.ClickAddUnit();
        addProductsPage.ClickControlProduct();
        addProductsPage.SelectCheckboxByText("مبيعات", true);
        addProductsPage.SelectCheckboxByText("المشتريات", true);
        addProductsPage.SelectCheckboxByText("مرتجع مبيعات", true);
        addProductsPage.SelectCheckboxByText("مرتجع مشتريات", true);
        addProductsPage.SelectCheckboxByText("عرض سعر", true);
        addProductsPage.SelectCheckboxByText("امر بيع", true);
        addProductsPage.SelectCheckboxByText("امر شراء", true);
        addProductsPage.SelectCheckboxByText("التحويل بين المخازن", true);
        addProductsPage.SelectCheckboxByText("اذن استلام", true);
        addProductsPage.SelectCheckboxByText("إذن صرف", true);
        addProductsPage.SelectCheckboxByText("تسوية التوالف", true);
        addProductsPage.ClickSaveEditButton();
        addProductsPage.AssertReceiptAdded("منتج جديد");
    }

    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("تعديل صنف ببيانات صحيحة")
    @Description("يتحقق هذا الاختبار من تعديل صنف موجود ببيانات صحيحة، ويشمل تعديل الاسم العربي والإنجليزي وأسعار البيع والفئة والوحدة الأساسية وحذف المخزن القديم وإضافة مخزن جديد، وتعديل وحدة التحويل وصلاحيات التحكم، وحفظ التعديلات والتحقق من تحديث السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditProduct() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.Edit();
        addProductsPage.AddProductName("منتج معدل");
        addProductsPage.AddProductEnglishName("New Product 2");
        addProductsPage.AddBarcode(addProductsPage.generateRandomNumber());
        addProductsPage.AddSalesPrice("150");
        addProductsPage.AddMiniSalePrice("100");
        addProductsPage.AddMaxSalePrice("200");
        addProductsPage.SelectFromDropDawnByText("الفئة", "وجبات فرايد");
        addProductsPage.SelectFromDropDawnByText("الوحدة الأساسية", "حبة");
        addProductsPage.ClickDeleteStore();
        addProductsPage.SelectFromDropDawnByText("مخازن", "مخزن الكافيه");
        addProductsPage.AddPlace("مصنع 2");
        addProductsPage.AddOpenAmount("300");
        addProductsPage.AddLowestAvailableQuantity("100");
        addProductsPage.AddCostPrice("100");
        addProductsPage.AddStore();
        addProductsPage.ClickUnitProduct();
        addProductsPage.SelectFromDropDawnByText("الوحدة", "م");
        addProductsPage.AddBarcode_2(addProductsPage.generateRandomNumber());
        addProductsPage.AddTransUnit("2");
        addProductsPage.AddSalePrice_2("150");
        addProductsPage.AddMiniSalePrice_2("100");
        addProductsPage.AddMaxSalePrice_2("300");
        addProductsPage.ClickAddUnit();
        addProductsPage.ClickControlProduct();
        addProductsPage.SelectCheckboxByText("مبيعات", true);
        addProductsPage.SelectCheckboxByText("المشتريات", true);
        addProductsPage.SelectCheckboxByText("مرتجع مبيعات", true);
        addProductsPage.SelectCheckboxByText("مرتجع مشتريات", true);
        addProductsPage.SelectCheckboxByText("عرض سعر", true);
        addProductsPage.SelectCheckboxByText("امر بيع", true);
        addProductsPage.SelectCheckboxByText("امر شراء", true);
        addProductsPage.SelectCheckboxByText("التحويل بين المخازن", true);
        addProductsPage.SelectCheckboxByText("اذن استلام", true);
        addProductsPage.SelectCheckboxByText("إذن صرف", true);
        addProductsPage.SelectCheckboxByText("تسوية التوالف", true);
        addProductsPage.ClickSaveEditButton();
        addProductsPage.AssertReceiptAdded("منتج معدل");
    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("إضافة مكونات لصنف مكتمل الصنع")
    @Description("يتحقق هذا الاختبار من إضافة صنف جديد من نوع مكتمل الصنع وإضافة مكونات له، ويشمل تحديد الصنف كمنتج مكتمل الصنع واختيار المخزن وإضافة المكون والكمية والملاحظات، وحفظ البيانات والتحقق من إضافة السجل بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddComponent() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.ClickNewProduct();
        addProductsPage.AddProductName("منتج تصنيع");
        addProductsPage.AddProductEnglishName("New Product 2");
        addProductsPage.AddBarcode(addProductsPage.generateRandomNumber());
        addProductsPage.AddSalesPrice("150");
        addProductsPage.AddMiniSalePrice("100");
        addProductsPage.AddMaxSalePrice("200");
        addProductsPage.SelectFromDropDawnByText("الفئة", "وجبات فرايد");
        addProductsPage.SelectFromDropDawnByText("الوحدة الأساسية", "حبة");
        addProductsPage.SelectFromDropDawnByText("مخازن", "مخزن الكافيه");
        addProductsPage.AddPlace("مصنع 2");
        addProductsPage.AddOpenAmount("300");
        addProductsPage.AddLowestAvailableQuantity("100");
        addProductsPage.AddCostPrice("100");
        addProductsPage.AddStore();
        addProductsPage.ClickCompletedProduct();
        addProductsPage.ClickComponentTab();
        addProductsPage.SelectFromDropDawn("مخزن الكافيه", "المخزن");
        addProductsPage.ClickAddComponent();
        addProductsPage.SearchComponent("منتج معدل");
        addProductsPage.SelectProduct("منتج معدل");
        addProductsPage.ClickCloseComponent();
        addProductsPage.AddComponentQuantity("5");
        addProductsPage.AddComponentNotes("مكونات المنتج");
        addProductsPage.ClickSaveEditButton();
        addProductsPage.AssertReceiptAdded("منتج تصنيع");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("حذف صنف")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف الأصناف المضافة بنجاح.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteProduct() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.Delete();
        addProductsPage.Delete();
    }

    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("التحقق - الباركود إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة صنف بدون إدخال الباركود، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل الباركود إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddProductWithOutBarcode() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.ClickNewProduct();
        addProductsPage.AddProductName("منتج بدون باركود");
        addProductsPage.AddProductEnglishName("New Product 3");
        Assert.assertTrue(addProductsPage.AssertBarcodeRequired(),
                "Unite Name Required Message is not displayed");
    }

    @Test(priority = 6)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("التحقق - الفئة إلزامية")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة صنف بدون اختيار الفئة، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل الفئة إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddProductWithOutCategory() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.ClickNewProduct();
        addProductsPage.AddProductName("منتج بدون فئة");
        addProductsPage.AddProductEnglishName("New Product 3");
        addProductsPage.AddBarcode(addProductsPage.generateRandomNumber());
        addProductsPage.ClickSaveButton();
        addProductsPage.isErrorPopupDisplayed();
    }

    @Test(priority = 7)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("التحقق - الوحدة الأساسية إلزامية")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة صنف بدون اختيار الوحدة الأساسية، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل الوحدة الأساسية إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddProductWithOutUnit() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.ClickNewProduct();
        addProductsPage.AddProductName("منتج بدون وحدة أساسية");
        addProductsPage.AddProductEnglishName("New Product 3");
        addProductsPage.AddBarcode(addProductsPage.generateRandomNumber());
        addProductsPage.SelectFromDropDawnByText("الوحدة الأساسية", "كجم");
        addProductsPage.ClickSaveButton();
        addProductsPage.isErrorPopupDisplayed();
    }

    @Test(priority = 8)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("التحقق - المخزن إلزامي")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة صنف بدون اختيار المخزن، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل المخزن إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddProductWithOutStock() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.ClickNewProduct();
        addProductsPage.ClickNewProduct();
        addProductsPage.AddProductName("منتج بدون مخزن");
        addProductsPage.AddProductEnglishName("New Product");
        addProductsPage.AddBarcode(addProductsPage.generateRandomNumber());
        addProductsPage.SelectFromDropDawnByText("الفئة", "مقاولات");
        addProductsPage.SelectFromDropDawnByText("الوحدة الأساسية", "كجم");
        addProductsPage.ClickSaveButton();
        addProductsPage.isErrorPopupDisplayed();
    }

    @Test(priority = 9)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("التحقق - خطأ عند تكرار وحدة التحويل")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة وحدة تحويل مكررة بنفس الوحدة الأساسية، ويتحقق من ظهور رسالة خطأ تفيد بعدم إمكانية تكرار الوحدة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddProductWithSameUnit() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.ClickNewProduct();
        addProductsPage.AddProductName("منتج بوحدة مكررة");
        addProductsPage.AddBarcode(addProductsPage.generateRandomNumber());
        addProductsPage.SelectFromDropDawnByText("الفئة", "مقاولات");
        addProductsPage.SelectFromDropDawnByText("الوحدة الأساسية", "كجم");
        addProductsPage.ClickUnitProduct();
        addProductsPage.SelectFromDropDawnByText("الوحدة", "كجم");
        addProductsPage.ClickAddUnit();
        addProductsPage.ClickSaveButton();
        addProductsPage.isErrorPopupDisplayed();
    }

    @Test(priority = 10)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("التحقق - معامل التحويل إلزامي في وحدة التحويل")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة وحدة تحويل بدون إدخال معامل التحويل، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل معامل التحويل إلزامي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddProductWithNoTransUnit() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.ClickNewProduct();
        addProductsPage.AddProductName("منتج بدون معامل تحويل");
        addProductsPage.AddBarcode(addProductsPage.generateRandomNumber());
        addProductsPage.SelectFromDropDawnByText("الفئة", "مقاولات");
        addProductsPage.SelectFromDropDawnByText("الوحدة الأساسية", "كجم");
        addProductsPage.ClickUnitProduct();
        addProductsPage.SelectFromDropDawnByText("الوحدة", "طن");
        addProductsPage.AddBarcode_2("94032949");
        addProductsPage.AddSalePrice_2("120");
        addProductsPage.AddMiniSalePrice_2("110");
        addProductsPage.AddMaxSalePrice_2("130");
        addProductsPage.ClickAddUnit();
        addProductsPage.ClickSaveButton();
        addProductsPage.isErrorPopupDisplayed();
    }

    @Test(priority = 11)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("الأصناف")
    @Story("التحقق - الباركود إلزامي في وحدة التحويل")
    @Description("يتحقق هذا الاختبار من أن النظام يمنع إضافة وحدة تحويل بدون إدخال الباركود، ويتحقق من ظهور رسالة خطأ تفيد بأن حقل الباركود إلزامي في وحدة التحويل.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddTransWithoutBarcode() {
        AddProductsPage addProductsPage = new AddProductsPage(driver);
        addProductsPage.NavigationHelper(Pages.ADDITEM);
        addProductsPage.ClickNewProduct();
        addProductsPage.AddProductName("منتج بدون باركود في وحدة التحويل");
        addProductsPage.AddBarcode(addProductsPage.generateRandomNumber());
        addProductsPage.SelectFromDropDawnByText("الفئة", "مقاولات");
        addProductsPage.SelectFromDropDawnByText("الوحدة الأساسية", "كجم");
        addProductsPage.ClickUnitProduct();
        addProductsPage.SelectFromDropDawnByText("الوحدة", "طن");
        addProductsPage.AddTransUnit("2");
        addProductsPage.ClickAddUnit();
        addProductsPage.ClickSaveButton();
        addProductsPage.isErrorPopupDisplayed();
    }
}