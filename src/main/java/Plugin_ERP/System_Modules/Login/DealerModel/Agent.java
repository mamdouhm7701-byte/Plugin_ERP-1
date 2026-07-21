package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class Agent extends AbstractForDealer {

    public Agent(WebDriver driver) {
        super(driver);
    }

    //Locators
    private By OpenDealerModel = By.xpath("//span[text()='المتعاملين']");

    private By AgentsMenu = By.xpath("//a[.//span[normalize-space()='المناديب']]");
    private By AgentCommissionTab = By.xpath("//a[@role='tab' and normalize-space()='عموله المندوب']");
    private By ItemCommissionTab = By.xpath("//a[@role='tab' and normalize-space()='عمولات الأصناف']");
    private By FromAmountInput = By.xpath("//label[normalize-space()='من']/following::input[@type='number'][1]");
    private By ToAmountInput = By.xpath("//div[@data-title='الي']//input[@type='number']");
    private By AddCommissionButton = By.xpath("//button[.//*[contains(@class,'feather-plus-circle')]]");
    private By PercentageInput = By.xpath("//input[@name='value' and @type='number']");
    private By SearchInput = By.cssSelector("input[placeholder='بحث ....']");
    private By AddItemsButton = By.xpath("//button[normalize-space()='اضافة اصناف']");
    private By AddAllItemsButton = By.xpath("//button[normalize-space()='إضافة جميع الأصناف']");
    private By AddAgentNumber = By.cssSelector("div[data-title=\"الجوال\"][data-step=\"5\"] input.form-control");

    //Method
    @Step("فتح صفحة المندوب ")
    public void OpenAgentPage(){
        utilites.ClickElement(OpenDealerModel);
        utilites.ClickElement(AgentsMenu);
        utilites.ClickElement(OpenDealerModel);
    }

  @Step("الضغط علي تبويب عموله المندوب")
    public void ClickAgentCommissionTab(){
        utilites.ClickElement(AgentCommissionTab);
    }

    @Step("الضغط علي تبويب عمولات الأصناف")
    public void ClickItemCommissionTab(){
        utilites.ClickElement(ItemCommissionTab);
    }

    @Step("ادخال المبلغ من: {fromAmount}")
    public void EnterFromAmount(String fromAmount){
        utilites.TaypeTextB(FromAmountInput,fromAmount);
    }
    @Step("ادخال المبلغ الي: {toAmount}")
    public void EnterToAmount(String toAmount) {
        utilites.TaypeTextB(ToAmountInput, toAmount);
    }

    @Step("الضغط علي زر اضافه عموله")
    public void ClickAddCommissionButton() {
        utilites.ClickElement(AddCommissionButton);
    }

    @Step("ادخال النسبة: {percentage}")
    public void EnterPercentage(String percentage) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(PercentageInput, percentage);
    }

    @Step("البحث عن منتج: {productName}")
    public void SearchProduct(String productName) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(SearchInput, productName);
    }

    @Step("الضغط علي زر اضافه اصناف")
    public void ClickAddItemsButton() {
        utilites.ClickElement(AddItemsButton);
    }

    @Step("الضغط علي زر اضافه جميع الاصناف")
    public void ClickAddAllItems(){
        utilites.ClickElement(AddAllItemsButton);
    }

@Step("اضافه رقم موباييل المندوب:{}")
    public void AddAgentMobileNumber(){
        utilites.TaypeTextB(AddAgentNumber,utilites.Utility().generateRandomNumber());
    LogUtils.info("تم اضافه رقم الموبايل بنجاح");
    }

}
