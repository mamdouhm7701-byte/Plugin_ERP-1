package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Sales Form Controls: focused operations used by the original page facade. */
final class SalesFormControls {
    private final AbstractForSales owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElmentAction utilites;
    SalesFormControls(AbstractForSales owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }
    private By ArabicNameInput = By.xpath("//input[@name='arabicName']");
    private By SearchInput = By.xpath("//input[@id='barcode' and @name='barcode']");
    private static final By INVOICE_FULLSCREEN_TOGGLE =
            By.xpath(
                    "//button[normalize-space()='توزيع مراكز التكلفة']/following-sibling::button[2]");
    private static final By INVOICE_SIDE_MENU =
            By.xpath("//a[@aria-label='الصفحة الرئيسية'] | //a[contains(@href,'#/invoice-list')]");
    private static final By ADD_CUSTOMER_SHORTCUT =
            By.xpath(
                    "//*[normalize-space()='العميل']/following::*[self::button or self::a or @role='button' or self::img][1] | "
                            + "//input[@id='vs3__combobox']/ancestor::*[contains(@class,'vs__dropdown-toggle')][1]"
                            + "//button | //input[@id='vs3__combobox']/ancestor::*[contains(@class,'vs__dropdown-toggle')][1]//img");
    private static final By ADD_PRODUCT_SHORTCUT =
            By.xpath(
                    "//button[contains(@title,'إضافة صنف') or contains(@aria-label,'إضافة صنف') or "
                            + "contains(@data-bs-original-title,'إضافة صنف')] | "
                            + "//*[contains(@title,'إضافة صنف') or contains(@aria-label,'إضافة صنف') or "
                            + "contains(@alt,'إضافة صنف')]");
    private static final By FORM_DIALOG = By.cssSelector(".modal-content");

    boolean isCustomerNameFieldDisplayed() {
        By nameField = By.xpath("//*[normalize-space()='اسم العميل']/following::input[1]");
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(nameField))
                .isDisplayed();
    }

    void toggleInvoiceFullscreen() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(INVOICE_FULLSCREEN_TOGGLE))
                .click();
    }

    boolean isInvoiceSideMenuDisplayed() {
        return driver.findElements(INVOICE_SIDE_MENU).stream().anyMatch(WebElement::isDisplayed);
    }

    void clickAddCustomerShortcut() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(ADD_CUSTOMER_SHORTCUT))
                .click();
    }

    void clickAddProductShortcut() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(ADD_PRODUCT_SHORTCUT))
                .click();
    }

    void clickNewInvoice() {
        By newInvoiceButton = By.xpath("//button[normalize-space()='جديد']");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(newInvoiceButton))
                .click();
        utilites.waits().waitForOverlayToDisappear();
    }

    boolean isFormDialogDisplayed() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(FORM_DIALOG))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    void SearchAndSelectProduct(String productName) {
        owner.searchAndSelectInvoiceProduct(productName);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(
                            d -> {
                                List<WebElement> rows =
                                        d.findElements(By.xpath("//tbody//tr[@role='row']"));
                                return rows.stream()
                                        .anyMatch(row -> row.getText().contains(productName));
                            });
        } catch (TimeoutException e) {
            throw new IllegalStateException(
                    "تم اختيار الصنف من القائمة لكن لم يظهر في جدول فاتورة المبيعات: "
                            + productName,
                    e);
        }
    }

    void SearchBarcodForInv(String searchText) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(SearchInput, searchText);
    }

    void ClickOnNameRadioBtnForInv(String searchText) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ArabicNameInput);
        utilites.waits().waitForOverlayToDisappear();
        utilites.TaypeTextB(ArabicNameInput, searchText);
    }
}
