package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Drivers.WebDriverFactory;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.text.Utilities;
import java.time.Duration;

public class CashPage extends AbstractForDealer {
    //Constructors
    public CashPage
    (WebDriver driver) {
        super(driver);
    }

    //Locators
    private By OpenBasicData = By.xpath("//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");

    private By BasicData = By.xpath("//span[contains(text(),'البيانات الأساسية')]");
    private By Enteringthetreasury = By.xpath("//span[contains(text(),'البيانات الأساسية')]/following::span[contains(text(),'الخزن')]\n");

    //Methods
    @Step("فتح قائمه الخزن")
    public void OpenCashPage() {
        utilites.ClickElement(BasicData);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(Enteringthetreasury);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenBasicData);


    }
    

}
