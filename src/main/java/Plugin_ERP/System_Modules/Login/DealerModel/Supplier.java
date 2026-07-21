package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Supplier extends AbstractForDealer {

    public Supplier(WebDriver driver) {
        super(driver);
    }

    private By OpenDealerModel = By.xpath("//span[text()='المتعاملين']");
    private By Supplier = By.xpath("//span[text()='الموردين']");
    private By SupplierAdress = By.cssSelector("input#SupplierAddress");
    private By SupllierMobileNumer=By.cssSelector("div[data-title='جوال المورد'] input");
    private By AddAgentForSupplier=By.cssSelector("#agentName");



    //Method
@Step("فتح قائمه الموردين")
    public void OpenSupplier(){

        utilites.ClickElement(OpenDealerModel);
        utilites.ClickElement(Supplier);
        utilites.ClickElement(OpenDealerModel);
    }


    @Step("اضافه عنوان المورد")
    public void AddAdressForSupplier(String Adress){
    utilites.TaypeTextB(SupplierAdress,Adress);

    }

    @Step("{MobileNumber}:اضافه رقم جوال المورد")
    public void AddMobileNumberForSupplier(String MobileNumber){
        utilites.TaypeTextB(SupllierMobileNumer,MobileNumber);

    }
    @Step("اضافه اسم المندوب للمورد: {AgentName}")
    public void AddAgentNameForSupplier(String AgentName) {
        utilites.TaypeTextB(AddAgentForSupplier, AgentName);
    }

}
