package ProductsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Products.ProductListPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductListTest extends BaseTest {




    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("قائمة الأسعار")
    @Story("إنشاء قائمة أسعار")
    @Description("يتحقق هذا الاختبار من إنشاء قائمة أسعار جديدة والتأكد من إضافتها إلى النظام.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddNewProductList() {
        ProductListPage productListPage = new ProductListPage(driver);
        productListPage.NavigationHelper(Pages.PRICELIST);
        productListPage.ClickNew();
        productListPage.AddArabicName("قائمة أسعار جديدة");
        productListPage.AddEnglishName("New Price List");
        productListPage.ClickAddProduct();
        productListPage.SelectProductByCode(1,2,3,4,5);
        productListPage.ClickCloseButton();
        productListPage.AddNewProductList(100,200,300,400,500);
        productListPage.ClickSaveAndPrintButton_1();
        productListPage.AssertPriceListReport();
        productListPage.AssertTextInReport("قائمة أسعار جديدة");
        productListPage.AssertTextInReport("New Price List");
        productListPage.CloserReport();
        productListPage.AssertReceiptAdded("قائمة أسعار جديدة");


    }


    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("قائمة الأسعار")
    @Story("تعديل قائمة أسعار")
    @Description("يتحقق هذا الاختبار من تعديل قائمة أسعار موجودة بالفعل والتأكد من حفظ التعديلات في النظام.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditPriceList(){
        ProductListPage productListPage = new ProductListPage(driver);
        productListPage.NavigationHelper(Pages.PRICELIST);
        productListPage.EditPriceList();
        productListPage.AddArabicName("قائمة أسعار معدلة");
        productListPage.AddEnglishName("Modified Price List");
        productListPage.AddNewProductList(1000,2000,3000,4000,5000);
        productListPage.ClickSaveAndPrintButton_1();
        productListPage.AssertPriceListReport();
        productListPage.AssertTextInReport("قائمة أسعار معدلة");
        productListPage.AssertTextInReport("Modified Price List");
        productListPage.CloserReport();
        productListPage.AssertReceiptAdded("قائمة أسعار معدلة");



    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("قائمة الأسعار")
    @Story("حذف قائمة أسعار")
    @Description("يتحقق هذا الاختبار من حذف قائمة أسعار موجودة بالفعل والتأكد من إزالتها من النظام.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeletePriceList() {
        ProductListPage productListPage = new ProductListPage(driver);
        productListPage.NavigationHelper(Pages.PRICELIST);
        productListPage.DeletePriceList();


    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("قائمة الأسعار")
    @Story("التحقق - رسالة الخطأ عند عدم إدخال الاسم العربي")
    @Description("يتحقق هذا الاختبار من ظهور رسالة الخطأ عند" +
            " محاولة إنشاء قائمة أسعار بدون إدخال اسم عربي.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AssertErorrMsg(){

        ProductListPage productListPage = new ProductListPage(driver);
        productListPage.NavigationHelper(Pages.PRICELIST);
        productListPage.ClickNew();
        productListPage.AddArabicName("");
        productListPage.AddEnglishName("New Price List");
        Assert.assertTrue(productListPage.AssertErorrMsg(),
                "Error message for required Arabic name is not displayed.");


    }


}