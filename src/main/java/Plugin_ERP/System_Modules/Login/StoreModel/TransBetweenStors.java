package Plugin_ERP.System_Modules.Login.StoreModel;

import Plugin_ERP.System_Modules.Login.AbstractForStore;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TransBetweenStors extends StoreModulePage {

    public TransBetweenStors(WebDriver driver) {
        super(driver);
    }

    private By AssertToStore = By.xpath("//small[contains(text(),'الي مخزن مطلوب')]");

    @Step("التأكيد على ان الى مخزن مطلوب")
    public Boolean AssertToStore() {
        return utilites.isDisplayed(AssertToStore);
    }
}
