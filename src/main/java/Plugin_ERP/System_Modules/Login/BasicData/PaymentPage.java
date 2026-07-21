package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PaymentPage extends AbstractForDealer {

    //Constructors
    public PaymentPage(WebDriver driver) {
        super(driver);
    }

        //Locators
        private By OpenBasicData = By.xpath("//span[contains(@class,\"menu-title\") and contains(text(),\"البيانات الأساسية\")]");
        private By OpenPayment = By.xpath("//span[contains(@class,'menu-title') and contains(text(),'طرق الدفع')]");
        private By BankNameRequiredMsg =  By.xpath("//small[contains(text(),'الاسم العربي مطلوب')]");


    //Methods
    @Step("فتح صفحه طرق الدفع")
    public void OpenPaymentPage() {
        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenPayment);
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(OpenBasicData);
        utilites.waits().waitForOverlayToDisappear();

    }

    @Step("التأكد من أن اسم البنك مطلوب")
    public boolean IsBankRequiredErrorDisplayed() {
        return utilites.isDisplayed(BankNameRequiredMsg);
    }








    }









