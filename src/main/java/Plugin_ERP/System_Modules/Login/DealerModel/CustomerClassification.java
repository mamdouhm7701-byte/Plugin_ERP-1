package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CustomerClassification extends AbstractForDealer {
    public CustomerClassification(WebDriver driver) {
        super(driver);
    }


    //Locators
        private By OpenDealerModel = By.xpath("//span[text()='المتعاملين']");
    private By CustomerClassification = By.xpath("//span[text()='تصنيف المتعاملين']");



//Methods

@Step("فتح قائمه تصنيف المتعاملين")
public void OpenDealerModel() {
    utilites.ClickElement(OpenDealerModel);
    utilites.ClickElement(CustomerClassification);
    utilites.ClickElement(OpenDealerModel);

    }


}

