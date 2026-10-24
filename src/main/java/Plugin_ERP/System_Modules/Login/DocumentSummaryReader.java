package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Business.Calculator.InvoiceCalculator;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.Models.TaxMode;
import Plugin_ERP.Utiel.PropertyReader;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

/** Document Summary Reader: focused operations used by the original page facade. */
final class DocumentSummaryReader {
    private final DocumentPage owner;
    private final WebDriver driver;
    DocumentSummaryReader(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    BigDecimal getSummaryValue(String label) {

        WebElement input =
                driver.findElement(
                        By.xpath(
                                "//span[normalize-space()='"
                                        + label
                                        + "']/ancestor::div[contains(@class,'align-items-center')]"
                                        + "/following-sibling::div//input"));

        String value = input.getAttribute("value").trim();

        if (value.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value);
    }

    BigDecimal getTotal() {

        String value =
                driver.findElement(By.xpath("//input[@placeholder='الاجمالى ']"))
                        .getAttribute("value");

        return new BigDecimal(value);
    }
}
