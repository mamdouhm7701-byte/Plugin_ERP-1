package Plugin_ERP.System_Modules.Login.Products;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.BrowserAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import Plugin_ERP.System_Modules.Login.Abstract;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductCategories extends Abstract {


    //Constructors
    public ProductCategories(WebDriver driver) {
        super(driver);
    }
    //locators
    private By OpenProducts = By.xpath("//span[contains(@class,'menu-title') and contains(text(),'الأصناف')]");
    private By OpenProductCategories = By.xpath("//span[@class='menu-title text-truncate' and normalize-space()='فئات الأصناف']");
    private By AddNote = By.xpath("//textarea[contains(@class,'form-control') and @label='Notes']");
    private By UniteNameRequiredMsg = By.xpath("//small[@class='text-danger' and text()='الاسم العربي مطلوب']");


    //Methods
    @Step("Open the product categories page")
    public void OpenProductCategoriesPage() {
        utilites.ClickElement(OpenProducts);
        utilites.ClickElement(OpenProductCategories);
        utilites.ClickElement(OpenProducts);

    }
@Step("Add note for product category")
    public void AddNoteForProductCategory(String Notes) {
        utilites.TaypeText(AddNote, Notes);
    }

  public boolean AssertErrorMessageForRequiredField() {
        return utilites.isDisplayed(UniteNameRequiredMsg);


        }
    }










