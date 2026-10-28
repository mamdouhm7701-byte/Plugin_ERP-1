/* شرح الملف: شاشة قوائم أسعار الأصناف: بنضيف الأصناف وأسعارها ونعدل القائمة أو نطبعها. فائدتها
 * تجهيز قائمة أسعار واضحة ومراجعة السعر المحدد لكل صنف.
 */
package Plugin_ERP.System_Modules.Login.Products;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ProductListPage extends ProductsModulePage {
    @Step("إضافة الصنف {name} إلى قائمة الأسعار بسعر {price}")
    public void prAddItem(String name, String price) {
        prButton("اضافة اصناف");
        prPickItem(name);
        prClosePicker();
        prItemPrice(name, price);
    }

    @Step("تعديل سعر الصنف {name} إلى {price}")
    public void prItemPrice(String name, String price) {
        prType(
                By.xpath(
                        "//form//tr[.//td[contains(normalize-space(),'"
                                + name
                                + "')]]//input[@type='number'][1]"),
                price);
    }

    // Constructors
    public ProductListPage(WebDriver driver) {
        super(driver);
    }

    // locators
    private By OpenProducts =
            By.xpath("//span[contains(@class,'menu-title') and contains(text(),'الأصناف')]");
    private By OpenProductList =
            By.xpath("//span[@class='menu-title text-truncate' and text()='قوائم الأسعار']");
    private By UniteNameRequiredMsg =
            By.xpath("//small[@class='text-danger' and text()='اسم القائمة (عربي) مطلوب']");
    private By AddProductToPriceList = By.xpath("//button[normalize-space()='اضافة اصناف']");
    private By CloseButton = By.xpath("//button[normalize-space()='إغلاق']");
    private By SaveAndPrintButton = By.xpath("//button[normalize-space()='حفظ و طباعة']");

    private By reportText(String text) {
        return By.xpath("//*[contains(normalize-space(),'" + text + "')]");
    }

    private By reportRow(String productName, String price) {
        return By.xpath(
                "//*[contains(normalize-space(),'"
                        + productName
                        + "')]"
                        + "/ancestor::*[self::tr or self::div][1]"
                        + "//*[contains(normalize-space(),'"
                        + price
                        + "')]");
    }

    // Methods
    @Step("Open Product List Page")
    public void OpenProductListPage() {
        utilites.ClickElement(OpenProducts);
        utilites.ClickElement(OpenProductList);
        utilites.ClickElement(OpenProducts);
    }

    @Step("Click Add Product")
    public void ClickAddProduct() {
        utilites.ClickElement(AddProductToPriceList);
    }

    @Step("Click Close Button")
    public void ClickCloseButton() {
        utilites.ClickElement(CloseButton);
    }

    @Step("Click Save And Print Button")
    public void ClickSaveAndPrintButton_1() {
        var originalTabs = driver.getWindowHandles();
        utilites.ClickElement(SaveAndPrintButton);
        utilites.waits().switchToNewTab(originalTabs);
        utilites.waits().waitForReportReady(60);
    }

    @Step("Select Product By Code: {codes}")
    public void SelectProductByCode(int... codes) {
        utilites.PriceList().selectUsersByCodeFprPriceList(codes);
    }

    @Step("Edit Price List")
    public void EditPriceList() {
        utilites.ClickElement(utilites.PriceList().getLastEditButtonForPriceList());
    }

    @Step("حذف قائمة الأسعار")
    public void DeletePriceList() {
        utilites.PriceList().deleteRowForPriceList();
    }

    @Step("Add Product List")
    public void AddNewProductList(int... prices) {
        for (int i = 0; i < prices.length; i++) {
            utilites.PriceList().enterPriceByRowIndex(i + 1, prices[i]);
        }
    }

    @Step("Assert Price List Report")
    public void AssertPriceListReport() {

        for (String productName : utilites.PriceList().getSelectedProducts()) {

            Assert.assertTrue(
                    utilites.validation().IsTextDisplayed(productName),
                    "الصنف غير موجود: " + productName);
        }

        for (String price : utilites.PriceList().getEnteredPrices()) {

            Assert.assertTrue(
                    utilites.validation().IsPriceDisplayed(price), "السعر غير موجود: " + price);
        }
    }

    @Step("Assert Erorr Msg")
    public boolean AssertErorrMsg() {
        return utilites.isDisplayed(UniteNameRequiredMsg);
    }
}
