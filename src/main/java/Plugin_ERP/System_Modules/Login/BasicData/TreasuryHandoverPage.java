package Plugin_ERP.System_Modules.Login.BasicData;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.BrowserAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

public class TreasuryHandoverPage extends AbstractForDealer {

    // Constructors
    public TreasuryHandoverPage(WebDriver driver) {
        super(driver);
    }

    // Locators
    private By OpenBasicData = By.xpath("//span[text()='البيانات الأساسية']");
    private By OpenTreasuryHandover = By.xpath("//span[normalize-space()='إستلام الخزينة']");
    private By OpenBalnce = By.xpath("//*[@data-title='الرصيد الإفتتاحي']//input[@type='number']");
    private By SaveAndPrintButton =
            By.xpath(
                    "//button[@data-action-type='save' and contains(normalize-space(),'حفظ و طباعة')]");
    private By TotalBalnce =
            By.cssSelector("#container_Textbox43 div[style*='white-space: pre-wrap']");
    private By CashBalnce =
            By.cssSelector("#container_Textbox28 div[style*='white-space: pre-wrap']");
    private By DifferenceBalnce =
            By.cssSelector("#container_Textbox31 div[style*='white-space: pre-wrap']");
    private By ActualBalnce =
            By.cssSelector("#container_Textbox29 div[style*='white-space: pre-wrap']");
    private By CloseBalnce = By.xpath("//input[@name='actualBalance']");
    private By CashCloseBalnce = By.xpath("//input[@name='cashBalance']");
    private By DifferenceCloseBalnce = By.xpath("//input[@name='deficit']");
    private By TotalCloseBalnce = By.xpath("//input[@name='totalBalance']");
    private By OpenTreaury =
            By.xpath(
                    "(//tr[.//button[@data-action-type='close']])[last()]//button[@data-action-type='close']");

    // Methods

    @Step("فتح قائمة استلام الخزينة")
    public void OpenTreasuryHandoverPage() {
        utilites.ClickElement(OpenBasicData);
        utilites.ClickElement(OpenTreasuryHandover);
        utilites.ClickElement(OpenBasicData);
    }

    @Step("اضافه الرصيد الافتتاحى :{balnce}")
    public void AddOpenBalance(double balnce) {
        utilites.TaypeText(OpenBalnce, String.valueOf(balnce));
    }

    @Step("فتح الخزينه للمراجعه")
    public void OpenTreasuryForReview() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(OpenTreaury);
        utilites.waits().waitForOverlayToDisappear();
    }

    @Step("حفظ وطباعة التقرير")
    public void SaveAndPrint() {
        utilites.ClickElement(SaveAndPrintButton);
        utilites.waits().switchToNewTab();
        utilites.waits().waitForReportReady(60);
    }

    @Step("التاكد من ان الخزينه تم فتحها")
    public void GetTotalBalnceClose() {
        // انتظر حتى يكون cashBalance محمّل بقيمة حقيقية
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(
                driver -> {
                    WebElement el = driver.findElement(By.xpath("//input[@name='cashBalance']"));
                    String val = el.getDomProperty("value");
                    return val != null && !val.trim().isEmpty() && !val.trim().equals("0");
                });

        utilites.Utility().getInputValue(CloseBalnce, "0");
        utilites.Utility().getInputValue(CashCloseBalnce, "1000");
        utilites.Utility().getInputValue(DifferenceCloseBalnce, "-1000");
        utilites.Utility().getInputValue(TotalCloseBalnce, "1000");
    }

    @Step("التاكد من قيم التقرير")
    public void GetTotalBalnceinReport() {
        utilites.validation().assertValue("1000", TotalBalnce);
        utilites.validation().assertValue("1,000.000000", CashBalnce);
        utilites.validation().assertValue("-1,000.000000", DifferenceBalnce);
        utilites.validation().assertValue("0.000000", ActualBalnce);
        browserAction.navigateTo("https://binfares.plugin-erp.com/#/safe-transaction/");
    }
}
