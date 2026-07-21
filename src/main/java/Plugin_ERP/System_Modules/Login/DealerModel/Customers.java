package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Customers extends AbstractForDealer {
    public Customers(WebDriver driver) {
        super(driver);
    }


    //Locators
    private By OpenDealerModel = By.xpath("//span[text()='المتعاملين']");
    private By customers = By.xpath("//span[text()='العملاء']");
    private By AddAddress = By.cssSelector("input[name='customerAddress']");
    private By mobileNumber = By.cssSelector("div[data-title='جوال العميل'] input");
    private By AddLocation = By.cssSelector("input[name='customerLocation']");
    private By AddAgentName = By.cssSelector("input[name='agentName']");
    private By ClickLinkDataTax = By.cssSelector("a.nav-link[aria-posinset='3']");












    //Methods

    @Step("Open Customers")
    public void OpenCustomers() {
        utilites.ClickElement(OpenDealerModel);
        utilites.ClickElement(customers);
        utilites.ClickElement(OpenDealerModel);
    }


@Step("Add Address; {address}")
    public void AddAddress(String address){
        utilites.TaypeTextB(AddAddress,address);
}

@Step("Add Mobile Number: {MobileNumber}")
    public void AddMobileNumber(String MobileNumber){
        utilites.TaypeTextB(mobileNumber,MobileNumber);
}

@Step("Add Location: {Location}")
    public void AddLocation(String Location ){
        utilites.TaypeTextB(AddLocation,Location);

}

@Step("Add Agent Name: {AgentName}")
    public void AddAgentName(String AgentName){
        utilites.TaypeTextB(AddAgentName,AgentName);

}

@Step("Click Link Data Tax")
    public void ClickLinkDataTax() {
    utilites.ClickElement(ClickLinkDataTax);
}





    }
