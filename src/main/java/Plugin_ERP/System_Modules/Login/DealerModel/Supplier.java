package Plugin_ERP.System_Modules.Login.DealerModel;

import Plugin_ERP.System_Modules.Login.AbstractForDealer;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Supplier extends AbstractForDealer {

    public Supplier(WebDriver driver) {
        super(driver);
    }

    private By OpenDealerModel = By.xpath("//span[text()='المتعاملين']");
    private By Supplier = By.xpath("//span[text()='الموردين']");
    private By SupplierAdress = By.cssSelector("input#SupplierAddress");
    private By SupllierMobileNumer = By.cssSelector("div[data-title='جوال المورد'] input");
    private By AddAgentForSupplier = By.cssSelector("#agentName");

    // Method
    @Step("فتح قائمه الموردين")
    public void OpenSupplier() {

        utilites.ClickElement(OpenDealerModel);
        utilites.ClickElement(Supplier);
        utilites.ClickElement(OpenDealerModel);
    }

    @Step("تصدير قائمة الموردين بصيغة {format}")
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

    @Step("اضافه عنوان المورد")
    public void AddAdressForSupplier(String Adress) {
        utilites.typeTextAndVerify(SupplierAdress, Adress);
    }

    @Step("{MobileNumber}:اضافه رقم جوال المورد")
    public void AddMobileNumberForSupplier(String MobileNumber) {
        utilites.typeTextAndVerify(SupllierMobileNumer, MobileNumber);
    }

    @Step("اضافه اسم المندوب للمورد: {AgentName}")
    public void AddAgentNameForSupplier(String AgentName) {
        utilites.typeTextAndVerify(AddAgentForSupplier, AgentName);
    }
}
