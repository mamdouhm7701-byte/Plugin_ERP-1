package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Driver;
import java.time.Duration;

public class BankPage extends AbstractForDealer {
    //Constructors
    public  BankPage(WebDriver driver) {
        super(driver);

    }
        //Locators
        private By OpenBasicData = By.xpath("//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");
    private By OpenBanks = By.xpath("//span[text()='البنوك']\n");;
//Methods

    @Step("Open Bank Page")
    public void OpenBankPage() {
        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenBanks);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();


    }


}


