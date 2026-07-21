package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Drivers.AbstractDriver;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.sql.Driver;
import java.time.Duration;

public class RoutLine extends AbstractForDealer {



    //Constructors
    public RoutLine(WebDriver driver) {
        super(driver);

    }

    //Locators
    private By OpenBasicData = By.xpath("//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");
    private By OpenRoutLine = By.xpath("//span[contains(@class,'menu-title') and contains(text(),'خط السير')]");

    //Methods
    @Step("Open Rout Line Page")
    public void OpenRoutLinePage() {
        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenRoutLine);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();

    }

    @Step("اختيار راديو بوتوم:{RadiBouton}")
    public void SelectRadiBouton(String RadiBouton) {
        utilites.checkbox().selectRadioButton(RadiBouton);
    }



}
