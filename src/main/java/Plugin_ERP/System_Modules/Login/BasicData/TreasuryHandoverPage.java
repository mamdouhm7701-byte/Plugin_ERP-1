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

    @Override
    public boolean isErrorPopupDisplayed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".swal2-icon.swal2-error")));
            return true;
        } catch (org.openqa.selenium.TimeoutException missingError) { return false; }
    }

    @Step("فتح تسليم الاختبار بالهوية المحددة")
    public void OpenTreasuryForReview(Object id) {
        if (id == null || !id.toString().matches("[A-Za-z0-9-]+"))
            throw new IllegalArgumentException("Invalid treasury transaction identity");
        String origin = ((JavascriptExecutor)driver).executeScript("return location.origin").toString();
        driver.get(origin + "/#/safe-transaction/close/" + id);
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                ExpectedConditions.visibilityOfElementLocated(TotalCloseBalnce));
    }


    @Step("حفظ وطباعة التقرير")
    public void SaveAndPrint() {
        var originalTabs = driver.getWindowHandles();
        utilites.ClickElement(SaveAndPrintButton);
        utilites.waits().switchToNewTab(originalTabs);
        utilites.waits().waitForReportReady(60);
    }

    @Step("مطابقة رصيد الإغلاق: نقدي {cash}، إجمالي {total}، فعلي {actual}، عجز {deficit}")
    public void AssertClosingAmounts(java.math.BigDecimal cash, java.math.BigDecimal total,
            java.math.BigDecimal actual, java.math.BigDecimal deficit) {
        assertNumber(driver.findElement(CashCloseBalnce).getDomProperty("value"), cash);
        assertNumber(driver.findElement(TotalCloseBalnce).getDomProperty("value"), total);
        assertNumber(driver.findElement(CloseBalnce).getDomProperty("value"), actual);
        assertNumber(driver.findElement(DifferenceCloseBalnce).getDomProperty("value"), deficit);
    }

    @Step("مطابقة التقرير المطبوع: نقدي {cash}، إجمالي {total}، فعلي {actual}، عجز {deficit}")
    public void AssertPrintedAmounts(java.math.BigDecimal cash, java.math.BigDecimal total,
            java.math.BigDecimal actual, java.math.BigDecimal deficit) {
        assertNumber(driver.findElement(CashBalnce).getText(), cash);
        assertNumber(driver.findElement(TotalBalnce).getText(), total);
        assertNumber(driver.findElement(ActualBalnce).getText(), actual);
        assertNumber(driver.findElement(DifferenceBalnce).getText(), deficit);
    }

    private void assertNumber(String text, java.math.BigDecimal expected) {
        Assert.assertEquals(new java.math.BigDecimal(text.replace(",", "").trim()).compareTo(expected), 0,
                "Treasury amount differs from its transaction ledger: " + text + " expected " + expected);
    }
}
