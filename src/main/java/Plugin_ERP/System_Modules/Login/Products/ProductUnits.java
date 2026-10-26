package Plugin_ERP.System_Modules.Login.Products;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Product Units: focused operations used by the original page facade. */
final class ProductUnits {
    private final AddProductsPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;

    ProductUnits(AddProductsPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }

    private By UnitProduct = By.xpath("//a[contains(.,'وحدات الصنف')]");
    private By TransUnit = By.xpath("//div[@data-title='معامل التحويل']//input[@type='number']");
    private By AddBarcode = By.xpath("(//div[@data-title='الباركود']//input)[3]");
    private By AddSalePrice = By.xpath("//div[@data-title='سعر البيع']//input[@type='number']");
    private By AddMiniSalePrice =
            By.xpath("//div[@data-title='أقل سعر بيع']//input[@type='number']");
    private By AddMaxSalePrice = By.xpath("//input[@name='maxSalesPrice']");
    private By AddUnite = By.xpath("//button[contains(.,'اضافة وحدة')]");

    void ClickUnitProduct() {
        utilites.ClickElement(UnitProduct);
    }

    void AddTransUnit(String transUnit) {
        utilites.typeTextAndVerify(TransUnit, transUnit);
    }

    void AddBarcode_2(String addBarcode) {
        utilites.typeTextAndVerify(AddBarcode, addBarcode);
    }

    void AddSalePrice_2(String addSalePrice) {
        utilites.typeTextAndVerify(AddSalePrice, addSalePrice);
    }

    void AddMiniSalePrice_2(String addMiniSalePrice) {
        utilites.typeTextAndVerify(AddMiniSalePrice, addMiniSalePrice);
    }

    void AddMaxSalePrice_2(String addMaxSalePrice) {
        utilites.typeTextAndVerify(AddMaxSalePrice, addMaxSalePrice);
    }

    void ClickAddUnit() {
        utilites.ClickElement(AddUnite);
    }
}
