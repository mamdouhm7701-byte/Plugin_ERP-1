package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.Models.TaxMode;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

/** Document Payment Controls: focused operations used by the original page facade. */
final class DocumentPaymentControls {
    private final DocumentPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElmentAction utilites;
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
        utilites.TaypeTextB(DiscountPercntge, discountPercntge);
    }

    void AddDiscountValue(String discountValue) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(DiscountValue, discountValue);
    }
}
