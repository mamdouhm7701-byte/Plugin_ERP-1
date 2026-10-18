package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Customers extends AbstractForDealer {
  public Customers(WebDriver driver) {
    super(driver);
  }

  // Locators
  private By OpenDealerModel = By.xpath("//span[text()='المتعاملين']");
  private By customers = By.xpath("//span[text()='العملاء']");
  private By AddAddress = By.cssSelector("input[name='customerAddress']");
  private By mobileNumber = By.cssSelector("div[data-title='جوال العميل'] input");
  private By AddLocation = By.cssSelector("input[name='customerLocation']");
  private By AddAgentName = By.cssSelector("input[name='agentName']");
  private By ClickLinkDataTax =
      By.xpath("//a[@role='tab' and contains(normalize-space(),'بيانات الربط مع الهيئة')]");

  // Methods

  @Step("Open Customers")
  public void OpenCustomers() {
    utilites.ClickElement(OpenDealerModel);
    utilites.ClickElement(customers);
    utilites.ClickElement(OpenDealerModel);
  }

  @Step("Add Address; {address}")
  public void AddAddress(String address) {
    utilites.TaypeTextB(AddAddress, address);
  }

  @Step("Add Mobile Number: {MobileNumber}")
  public void AddMobileNumber(String MobileNumber) {
    utilites.TaypeTextB(mobileNumber, MobileNumber);
  }

  @Step("Add Location: {Location}")
  public void AddLocation(String Location) {
    utilites.TaypeTextB(AddLocation, Location);
  }

  @Step("Add Agent Name: {AgentName}")
  public void AddAgentName(String AgentName) {
    utilites.TaypeTextB(AddAgentName, AgentName);
  }

  @Step("Click Link Data Tax")
  public void ClickLinkDataTax() {
    utilites.ClickElement(ClickLinkDataTax);
  }

  @Step("تصدير قائمة العملاء بصيغة {format}")
  public void ExportList(String format) {
    String normalizedFormat = format == null ? "" : format.trim().toLowerCase();
    if (!List.of("pdf", "xls", "xlsx", "excel").contains(normalizedFormat)) {
      throw new IllegalArgumentException("Supported export formats are PDF and Excel");
    }
    String imageName = normalizedFormat.equals("pdf") ? "pdf." : "xls.";
    By exportButton = By.cssSelector("button:has(img[src*='" + imageName + "'])");
    WebElement button =
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(
                d ->
                    d.findElements(exportButton).stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(null));
    button.click();
  }
}
