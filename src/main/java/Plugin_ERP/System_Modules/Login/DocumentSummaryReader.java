/* شرح الملف: مساعدة لقراءة إجماليات المستند الظاهرة. فائدتها إتاحة مراجعة قيمة الحركة ومبالغها
 * النهائية.
 */
package Plugin_ERP.System_Modules.Login;

import java.math.BigDecimal;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

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
