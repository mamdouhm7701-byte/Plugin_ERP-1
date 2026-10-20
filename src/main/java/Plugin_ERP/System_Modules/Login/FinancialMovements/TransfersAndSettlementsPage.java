package Plugin_ERP.System_Modules.Login.FinancialMovements;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.BrowserAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import Plugin_ERP.System_Modules.Login.Abstract;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TransfersAndSettlementsPage extends FinancialMovementPage {
    // Constructors
    public TransfersAndSettlementsPage(WebDriver driver) {
        super(driver);
    }

    // Locators
    private By OpenFinancialMovements = By.xpath("//span[normalize-space()='الحركات المالية']");
    private By OpenTransMovementsBtn = By.xpath("//span[text()='تحويلات وتسويات']");
    private By CridtBank =
            By.xpath("(//div[@role='radiogroup'])[2]//span[normalize-space()='البنك']");
    private By CridtCash =
            By.xpath("(//div[@role='radiogroup'])[2]//span[normalize-space()='خزينة']");
    private By CridtAgent =
            By.xpath("(//div[@role='radiogroup'])[2]//span[normalize-space()='العميل']");
    private By CridtSupplier =
            By.xpath("(//div[@role='radiogroup'])[2]//span[normalize-space()='المورد']");
    private By DebitBank =
            By.xpath("(//div[@role='radiogroup'])[1]//span[normalize-space()='البنك']");
    private By DebittCash =
            By.xpath("(//div[@role='radiogroup'])[1]//span[normalize-space()='خزينة']");
    private By DebitAgent =
            By.xpath("(//div[@role='radiogroup'])[1]//span[normalize-space()='العميل']");
    private By DebitSupplier =
            By.xpath("(//div[@role='radiogroup'])[1]//span[normalize-space()='المورد']");

    // Methods
    @Step("Open Transfers And Settlements Page")
    public void OpenTransMovementsPage() {
        utilites.ClickElement(OpenFinancialMovements);
        utilites.ClickElement(OpenTransMovementsBtn);
        utilites.ClickElement(OpenFinancialMovements);
    }

    @Step("Select Right Side Option: {Label} - {Name}")
    public void SelectRightSide(String Label, String Name) {
        utilites.dropdown().selectVueOptionboth(Label, Name);
    }

    @Step("Select Left Side Option: {Label} - {Name}")
    public void SelectLeftSide(String Label, String Name) {
        utilites.dropdown().selectVueOptionLeft(Label, Name);
    }

    @Step("Select Debit Option: {Option}")
    public void SelectDebitOption(String Option) {
        switch (Option) {
            case "البنك":
                utilites.ClickElement(DebitBank);
                break;
            case "خزينة":
                utilites.ClickElement(DebittCash);
                break;
            case "العميل":
                utilites.ClickElement(DebitAgent);
                break;
            case "المورد":
                utilites.ClickElement(DebitSupplier);
                break;
        }
    }

    @Step("Select Credit Option: {Option}")
    public void SelectCreditOption(String Option) {
        switch (Option) {
            case "البنك":
                utilites.ClickElement(CridtBank);
                break;
            case "خزينة":
                utilites.ClickElement(CridtCash);
                break;
            case "العميل":
                utilites.ClickElement(CridtAgent);
                break;
            case "المورد":
                utilites.ClickElement(CridtSupplier);
                break;
        }
    }
}
