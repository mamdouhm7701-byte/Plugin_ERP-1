package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MainBage {
    //Variables
    private WebDriver driver;
    private ElmentAction utilites;



    //Constructors
    public  MainBage(WebDriver driver) {
        this.driver = driver;
        this.utilites = new ElmentAction(driver);
    }
    //Locators

    private By mainpage= By.xpath("//h4[contains(text(),'الصفحة الرئيسية')]");






    //Methods


}
