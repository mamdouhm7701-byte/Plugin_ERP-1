package Plugin_ERP.System_Modules.Login.Products;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Product Components: focused operations used by the original page facade. */
final class ProductComponents {
    private final AddProductsPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;

    ProductComponents(AddProductsPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }

    private By CompletedProduct = By.xpath("//input[@value='PerfectlyMade']");
    private By Component = By.xpath("//a[@role='tab' and text()='المكونات']");
    private By AddComponent = By.xpath("//button[contains(text(),'اضافة مكونات')]");
    private By SerchComponent = By.xpath("//input[@id='arabicName']");
    private By CloseComponent = By.xpath("//button[contains(.,'إغلاق')]");
    private By ComponentQuantity =
            By.xpath("//input[@type='number' and contains(@class,'form-control-sm')]");
    private By ComponetNotest = By.xpath("//input[@id='textarea']");

    void ClickCompletedProduct() {
        utilites.ClickElement(CompletedProduct);
    }

    void ClickComponentTab() {
        utilites.ClickElement(Component);
    }

    void ClickAddComponent() {
        utilites.ClickElement(AddComponent);
    }

    void SearchComponent(String searchComponent) {
        utilites.typeTextAndVerify(SerchComponent, searchComponent);
    }

    void SelectProduct(String productName) {
        utilites.dropdown().selectProductByName(productName);
    }

    void ClickCloseComponent() {
        utilites.ClickElement(CloseComponent);
    }

    void AddComponentQuantity(String value) {
        utilites.typeTextAndVerify(ComponentQuantity, value);
    }

    void AddComponentNotes(String notes) {
        utilites.typeTextAndVerify(ComponetNotest, notes);
    }
}
