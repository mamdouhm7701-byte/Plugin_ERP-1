package ProductsTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Products.ProductCategories;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductCategoriesTest extends BaseTest {




    @Test(priority = 1)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("شاشة الفئات")
    @Story("إنشاء فئة منتج رئيسية بنجاح")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع إنشاء فئة منتج رئيسية بنجاح عن طريق إدخال اسم عربي واسم إنجليزي صحيحين مع الملاحظات. " +
            "ويتأكد الاختبار من إضافة الفئة الجديدة إلى النظام وإمكانية عرضها في قائمة فئات المنتجات.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddProductCategory() {
        ProductCategories productCategoriesPage = new ProductCategories(driver);
        productCategoriesPage.NavigationHelper(Pages.CATEGORIES);
        productCategoriesPage.ClickNew();
        productCategoriesPage.AddArabicName("فئة اختبار رئيسية");
        productCategoriesPage.AddEnglishName("Test Category");
        productCategoriesPage.AddNoteForProductCategory("فئة منتجات للاختبار");
        productCategoriesPage.SelectCheckboxByText("رئيسية", true);
        productCategoriesPage.ClickSaveButton();
        productCategoriesPage.AssertReceiptAdded("فئة اختبار رئيسية");

    }



    @Test(priority = 2)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("شاشة الفئات")
    @Story("إنشاء فئة منتج فرعية بنجاح تحت الفئة الرئيسية")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع إنشاء فئة منتج فرعية بنجاح" +
            " تحت فئة رئيسية موجودة بالفعل." +
            " ويتأكد الاختبار من إضافة الفئة الفرعية الجديدة إلى النظام و" +
            "إمكانية عرضها في قائمة فئات المنتجات تحت الفئة الرئيسية الصحيحة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddBranchProduct(){
        ProductCategories productCategoriesPage = new ProductCategories(driver);

        productCategoriesPage.NavigationHelper(Pages.CATEGORIES);
        productCategoriesPage.OpenProductCategoriesPage();
        productCategoriesPage.ClickNew();
        productCategoriesPage.AddArabicName("فئة اختبار فرعية");
        productCategoriesPage.AddEnglishName("Test Category Branch");
        productCategoriesPage.AddNoteForProductCategory("فئة منتجات فرعية للاختبار");
        productCategoriesPage.SetCheckboxValue("فئة نقاط بيع", true);
        productCategoriesPage.SelectFromDropDawn("فئة اختبار رئيسية", "الفئه الكبري");
        productCategoriesPage.ClickSaveButton();
        productCategoriesPage.AssertReceiptAdded("فئة اختبار فرعية");

    }

    @Test(priority = 3)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("شاشة الفئات")
    @Story("تعديل فئة منتج فرعية بنجاح تحت الفئة الرئيسية")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع تعديل فئة منتج فرعية بنجاح تحت فئة رئيسية موجودة بالفعل." +
            " ويتأكد الاختبار من تحديث بيانات الفئة الفرعية في النظام و" +
            "إمكانية عرضها في قائمة فئات المنتجات بالتفاصيل الجديدة تحت الفئة الرئيسية الصحيحة.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void EditBranchProduct(){
        ProductCategories productCategoriesPage = new ProductCategories(driver);
        productCategoriesPage.NavigationHelper(Pages.CATEGORIES);
        productCategoriesPage.Edit();
        productCategoriesPage.AddArabicName("فئة اختبار فرعية معدلة");
        productCategoriesPage.AddEnglishName("Test Category Branch Modified");
        productCategoriesPage.AddNoteForProductCategory("فئة منتجات فرعية للاختبار");
        productCategoriesPage.SetCheckboxValue("فئة مطعم", true);
        productCategoriesPage.SetCheckboxValue("فئة نقاط بيع", false);
        productCategoriesPage.ClickSaveButton();
        productCategoriesPage.AssertReceiptAdded("فئة اختبار فرعية معدلة");
    }

    @Test(priority = 4)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.NORMAL)
    @Epic("Web Cloud")
    @Feature("شاشة الفئات")
    @Story("حذف فئة منتج رئيسية بنجاح")
    @Description("يتحقق هذا الاختبار من أن المستخدم يستطيع حذف فئة منتج رئيسية بنجاح من النظام. " +
            "ويتأكد الاختبار من إزالة الفئة الرئيسية من قائمة فئات المنتجات وعدم إمكانية الوصول إليها مرة أخرى.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void DeleteBranchProduct(){
        ProductCategories productCategoriesPage = new ProductCategories(driver);
        productCategoriesPage.NavigationHelper(Pages.CATEGORIES);
        productCategoriesPage.Delete();
        productCategoriesPage.Delete();
    }


    @Test(priority = 5)
    @Owner("MAMDOUH")
    @Severity(SeverityLevel.CRITICAL)
    @Epic("Web Cloud")
    @Feature("شاشة الفئات")
    @Story("التحقق - إنشاء فئة منتج بدون اسم عربي والتحقق من رسالة الخطأ")
    @Description("يتحقق هذا الاختبار من أن النظام يتعامل بشكل صحيح مع محاولة إنشاء فئة منتج بدون إدخال اسم عربي، ويتحقق من ظهور رسالة الخطأ المناسبة التي تفيد بأن الاسم العربي مطلوب.")
    @Link(name = "Link", url = "https://binfares.plugin-erp.com/#/login")
    public void AddProductCategoryWithoutArabicName() {
        ProductCategories productCategoriesPage = new ProductCategories(driver);
        productCategoriesPage.NavigationHelper(Pages.CATEGORIES);
        productCategoriesPage.ClickNew();
        productCategoriesPage.AddArabicName("");
        productCategoriesPage.AddEnglishName("Test Category");
        Assert.assertTrue(productCategoriesPage.AssertErrorMessageForRequiredField(),
                "Error message for required Arabic name is not displayed.");



    }




}