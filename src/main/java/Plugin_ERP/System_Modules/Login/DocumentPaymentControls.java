package Plugin_ERP.System_Modules.Login;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Document Payment Controls: focused operations used by the original page facade. */
final class DocumentPaymentControls {
    private final DocumentPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;

    DocumentPaymentControls(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }

    private By DiscountValue = By.xpath("//input[@name='discountValue']");
    private By DiscountPercntge = By.xpath("//input[@placeholder='النسبة']");
    private By ClickCash = By.xpath("//label[.//span[normalize-space()='خزينة']]");
    private By ClickOtherPay = By.xpath("//label[.//span[normalize-space()='آخرى']]");
    private By ClickPlusToAddPay =
            By.xpath("(//button[contains(@class,'btn-gradient-primary')])[2]");

    void ClickCash() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ClickCash);
    }

    void ClickOtherPay() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ClickOtherPay);
    }

    void ClickPlusToAddPay() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ClickPlusToAddPay);
    }

    void AddDiscountPercntge(String discountPercntge) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(DiscountPercntge, discountPercntge);
    }

    void AddDiscountValue(String discountValue) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.typeTextAndVerify(DiscountValue, discountValue);
    }
}
