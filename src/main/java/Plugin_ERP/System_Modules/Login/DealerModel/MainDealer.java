package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MainDealer extends AbstractForDealer {
    public MainDealer(WebDriver driver) {
        super(driver);
    }


    //Locators
    private By OpenDealerModel = By.xpath("//span[text()='المتعاملين']");
    private By OpenMainDealer = By.xpath("//span[text()='المتعامل الرئيسي']");


    //Methods

    @Step("فتح قائمه المتعامل الرئيسي")
    public void OpenMainDealerModel() {
        utilites.ClickElement(OpenDealerModel);
        utilites.ClickElement(OpenMainDealer);
        utilites.ClickElement(OpenDealerModel);
    }














}
