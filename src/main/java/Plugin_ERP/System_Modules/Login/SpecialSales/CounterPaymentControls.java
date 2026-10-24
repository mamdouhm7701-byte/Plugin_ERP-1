package Plugin_ERP.System_Modules.Login.SpecialSales;

import Plugin_ERP.System_Modules.Login.Abstract;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/** Counter Payment Controls: focused operations used by the original page facade. */
final class CounterPaymentControls {
    private final SpecialSalesPage owner;
    private final WebDriver driver;
    private final WebDriverWait ready;
    CounterPaymentControls(SpecialSalesPage owner, WebDriver driver, WebDriverWait ready) {
        this.owner = owner;
        this.driver = driver;
        this.ready = ready;
    }

    void discount(String value, boolean percentage) {
        owner.text(
                percentage
                        ? By.cssSelector("input[placeholder='النسبة']")
                        : By.cssSelector("input[name='discountValue']"),
                value);
    }

    void closePayment() {
        for (var e : driver.findElements(By.cssSelector(".b-sidebar-backdrop")))
            if (e.isDisplayed()) {
                e.click();
                break;
            }
    }

    void saveAndPrint() {
        By sidebar =
                By.xpath(
                        "//div[@id='sidebar-invoice-add-payment']//button[normalize-space()='حفظ و"
                                + " طباعة']");
        if (driver.findElements(sidebar).stream().anyMatch(WebElement::isDisplayed)) owner.click(sidebar);
        else owner.button("حفظ و طباعة");
    }

    void addOtherPayment(String name, String value) {
        int count = ((List<?>) owner.document().get("payments")).size();
        owner.click(By.xpath("//label[normalize-space()='آخرى']"));
        owner.choose("طريقة الدفع", name);
        owner.text(By.cssSelector("input[name='value']:not([disabled])"), value);
        owner.click(
                By.xpath(
                        "//fieldset[@label-text='paymentWay']/ancestor::div[contains(concat('"
                                + " ',normalize-space(@class),' '),' row"
                                + " ')][1]//button[.//*[local-name()='svg' and"
                                + " contains(@class,'feather-plus')]]"));
        ready.until(d -> ((List<?>) owner.document().get("payments")).size() == count + 1);
    }
}
