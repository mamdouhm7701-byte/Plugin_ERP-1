/* شرح الملف: مساعدة لقراءة ملخص فاتورة البيع بعد اكتمال الحساب. فائدتها مراجعة الإجمالي والخصم
 * والضريبة والصافي.
 */
package Plugin_ERP.System_Modules.Login;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class SalesSummaryReader {
    private final AbstractForSales owner;
    private final WebDriver driver;

    SalesSummaryReader(AbstractForSales owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    BigDecimal getSummaryValue(String label) {

        WebElement input;

        switch (label) {
            case "خصم":
                input = driver.findElement(By.cssSelector("input[name='discountValue']"));
                break;

            case "قبل الضريبة":
                input =
                        driver.findElement(
                                By.xpath(
                                        "//span[normalize-space()='قبل الضريبة']"
                                                + "/parent::div/following-sibling::div//input"));
                break;

            case "الضريبة":
                input =
                        driver.findElement(
                                By.xpath(
                                        "//span[normalize-space()='الضريبة']"
                                                + "/parent::div/following-sibling::div//input"));
                break;

            case "الصافي":
                input = driver.findElement(By.cssSelector("input[name='Net']"));
                break;

            default:
                return owner.readDefaultSummary(label);
        }

        String value = input.getAttribute("value");

        System.out.println("SUMMARY FROM INVOICE => " + label + " = [" + value + "]");

        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value.trim());
    }

    void waitUntilSummaryValuesLoaded() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(
                driver -> {
                    try {

                        String beforeTax =
                                driver.findElement(
                                                By.xpath(
                                                        "//label[normalize-space()='قبل الضريبة']"
                                                                + "/parent::div/following-sibling::div//input"))
                                        .getAttribute("value");

                        String tax =
                                driver.findElement(
                                                By.xpath(
                                                        "//label[normalize-space()='الضريبة']"
                                                                + "/parent::div/following-sibling::div//input"))
                                        .getAttribute("value");

                        String net =
                                driver.findElement(By.cssSelector("input[name='Net']"))
                                        .getAttribute("value");

                        return beforeTax != null
                                && !beforeTax.trim().isEmpty()
                                && tax != null
                                && !tax.trim().isEmpty()
                                && net != null
                                && !net.trim().isEmpty();

                    } catch (Exception e) {
                        Plugin_ERP.Bots.LogUtils.debug(
                                "SalesSummaryReader condition unavailable: "
                                        + e.getClass().getSimpleName());
                        return false;
                    }
                });
    }
}
