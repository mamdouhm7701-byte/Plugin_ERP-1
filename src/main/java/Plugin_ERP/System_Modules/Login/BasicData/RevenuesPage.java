package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class RevenuesPage extends AbstractForDealer {


    //Constructors
    public RevenuesPage(WebDriver driver) {
        super(driver);

    }
    //locators
    private By OpenBasicData = By.xpath("//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");
    private By OpenRevenues = By.xpath("//span[contains(@class,'menu-title') and contains(text(),'الإيرادات')]");



//Methods
    @Step("فتح قائمه الايرادات")
    public void OpenRevenuesPage() {
        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenRevenues);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();

    }

}