package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MainBage {
    // Variables
    private WebDriver driver;
    private ElementActions utilites;

    // Constructors
    public MainBage(WebDriver driver) {
        this.driver = driver;
        this.utilites = new ElementActions(driver);
    }

    // Locators

    private By mainpage = By.xpath("//h4[contains(text(),'الصفحة الرئيسية')]");

    // Methods

}
