package Plugin_ERP.System_Modules.Login.Products;

import Plugin_ERP.System_Modules.Login.Abstract;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UnitePage extends ProductsModulePage {

    // Constructors
    public UnitePage(WebDriver driver) {
        super(driver);
    }

    // locators
    private By OpenProducts =
            By.xpath("//span[contains(@class,'menu-title') and contains(text(),'الأصناف')]");
    private By OpenUnite =
            By.xpath("//span[@class='menu-title text-truncate' and normalize-space()='الوحدات']");
    private By UniteNameRequiredMsg =
            By.xpath("//small[@class='text-danger' and text()='الاسم العربي مطلوب']");

    // Methods
    @Step("Open Unite Page")
    public void OpenUnitePage() {
        utilites.ClickElement(OpenProducts);
        utilites.ClickElement(OpenUnite);
        utilites.ClickElement(OpenProducts);
    }

    @Step("Assert Unite Name Required Message is Displayed")
    public boolean AssertUniteNameRequired() {
        return utilites.isDisplayed(UniteNameRequiredMsg);
    }
}
