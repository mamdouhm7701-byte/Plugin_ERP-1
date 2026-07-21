package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class AreaPage extends AbstractForDealer {

    public AreaPage(WebDriver driver) {
       super(driver);
    }
    //locators
    private By OpenBasicData = By.xpath("//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");
    private By OpenArea = By.xpath("//span[contains(@class,'menu-title') and contains(text(),'المناطق')]");


    //Methods
    @Step("فتح قائمه المناطق")
    public void OpenAreaPage() {
        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(OpenArea);
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();

    }

}
