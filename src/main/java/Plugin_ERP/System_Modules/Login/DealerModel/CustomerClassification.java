package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CustomerClassification extends AbstractForDealer {
  public CustomerClassification(WebDriver driver) {
    super(driver);
  }

  // Locators
  private By OpenDealerModel = By.xpath("//span[text()='المتعاملين']");
  private By CustomerClassification = By.xpath("//span[text()='تصنيف المتعاملين']");

  // Methods

  @Step("فتح قائمه تصنيف المتعاملين")
  public void OpenDealerModel() {
    utilites.ClickElement(OpenDealerModel);
    utilites.ClickElement(CustomerClassification);
    utilites.ClickElement(OpenDealerModel);
  }

  @Step("تصدير قائمة تصنيفات المتعاملين بصيغة {format}")
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
