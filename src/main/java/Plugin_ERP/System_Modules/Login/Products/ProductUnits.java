package Plugin_ERP.System_Modules.Login.Products;

import Plugin_ERP.Bots.AlertAction;
import Plugin_ERP.Bots.ElmentAction;
import Plugin_ERP.Bots.WaitMangment;
import Plugin_ERP.System_Modules.Login.Abstract;
import io.qameta.allure.Step;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Product Units: focused operations used by the original page facade. */
final class ProductUnits {
    private final AddProductsPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElmentAction utilites;
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
        utilites.TaypeTextB(TransUnit, transUnit);
    }

    void AddBarcode_2(String addBarcode) {
        utilites.TaypeTextB(AddBarcode, addBarcode);
    }

    void AddSalePrice_2(String addSalePrice) {
        utilites.TaypeTextB(AddSalePrice, addSalePrice);
    }

    void AddMiniSalePrice_2(String addMiniSalePrice) {
        utilites.TaypeTextB(AddMiniSalePrice, addMiniSalePrice);
    }

    void AddMaxSalePrice_2(String addMaxSalePrice) {
        utilites.TaypeTextB(AddMaxSalePrice, addMaxSalePrice);
    }

    void ClickAddUnit() {
        utilites.ClickElement(AddUnite);
    }
}
