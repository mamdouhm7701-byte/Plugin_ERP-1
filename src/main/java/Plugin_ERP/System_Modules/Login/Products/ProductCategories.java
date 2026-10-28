/* شرح الملف: شاشة مجموعات الأصناف: بنسجل اسم المجموعة وملاحظاتها. فائدتها تنظيم الأصناف في مجموعات
 * تسهل الوصول إليها.
 */
package Plugin_ERP.System_Modules.Login.Products;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductCategories extends ProductsModulePage {

    // Constructors
    public ProductCategories(WebDriver driver) {
        super(driver);
    }

    // locators
    private By OpenProducts =
            By.xpath("//span[contains(@class,'menu-title') and contains(text(),'الأصناف')]");
    private By OpenProductCategories =
            By.xpath(
                    "//span[@class='menu-title text-truncate' and normalize-space()='فئات الأصناف']");
    private By AddNote = By.xpath("//textarea[contains(@class,'form-control') and @label='Notes']");
    private By UniteNameRequiredMsg =
            By.xpath("//small[@class='text-danger' and text()='الاسم العربي مطلوب']");

    // Methods
    @Step("Open the product categories page")
    public void OpenProductCategoriesPage() {
        utilites.ClickElement(OpenProducts);
        utilites.ClickElement(OpenProductCategories);
        utilites.ClickElement(OpenProducts);
    }

    @Step("Add note for product category")
    public void AddNoteForProductCategory(String Notes) {
        utilites.typeText(AddNote, Notes);
    }

    @Step("التحقق من ظهور رسالة الحقل المطلوب")
    public boolean AssertErrorMessageForRequiredField() {
        return utilites.isDisplayed(UniteNameRequiredMsg);
    }
}
