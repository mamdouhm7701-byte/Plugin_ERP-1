package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.System_Modules.Login.AbstractForPurchases;
import Plugin_ERP.System_Modules.Login.AbstractForStore;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PurchesBillsPage extends AbstractForPurchases {
    public PurchesBillsPage(WebDriver driver) {super(driver);}


private By DiscountValue= By.xpath("//input[@name='discountValue']");
private By DiscountPercntge= By.xpath("//input[@placeholder='النسبة']");







@Step("اضافه خصم النسبة")
public void AddDiscountPercntge(String discountPercntge){
    utilites.waits().waitForOverlayToDisappear();
    utilites.TaypeTextB(DiscountPercntge,discountPercntge);



}











}
