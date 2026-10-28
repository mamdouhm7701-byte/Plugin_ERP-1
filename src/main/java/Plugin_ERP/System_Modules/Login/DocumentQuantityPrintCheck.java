/* شرح الملف: مساعدة لمقارنة أسماء الأصناف وكمياتها مع الطباعة. فائدتها مراجعة أن الكميات المطبوعة
 * تطابق الحركة المسجلة.
 */
package Plugin_ERP.System_Modules.Login;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

final class DocumentQuantityPrintCheck {
    private final DocumentPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;

    DocumentQuantityPrintCheck(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }

    void CompareNamesAndQuantitiesWithReport(Map<String, String> expectedData) {

        for (Map.Entry<String, String> entry : expectedData.entrySet()) {
            String name = entry.getKey();
            String quantity = entry.getValue();

            boolean nameFound = utilites.validation().IsTextDisplayed(name);
            Assert.assertTrue(nameFound, "الاسم غير موجود في التقرير: " + name);

            boolean quantityFound = utilites.validation().IsTextDisplayed(quantity);
            Assert.assertTrue(
                    quantityFound, "الكمية غير موجودة في التقرير للصنف " + name + ": " + quantity);

            System.out.println("✅ تم التأكد من: " + name + " - الكمية: " + quantity);
        }
    }

    void waitUntilMessageDisappear(String messageText) {
        int maxRetries = 3;
        boolean disappeared = false;

        for (int attempt = 1; attempt <= maxRetries && !disappeared; attempt++) {
            utilites.waits().waitForOverlayToDisappear();

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

                wait.until(
                        ExpectedConditions.invisibilityOfElementLocated(
                                By.xpath("//small[contains(text(),'" + messageText + "')]")));

                disappeared = true;
                System.out.println(
                        "✅ الرسالة '" + messageText + "' اختفت في المحاولة رقم " + attempt);

            } catch (TimeoutException e) {
                System.out.println(
                        "⚠️ المحاولة "
                                + attempt
                                + " فشلت، الرسالة '"
                                + messageText
                                + "' لسه ظاهرة، جاري عمل Refresh...");

                if (attempt < maxRetries) {
                    driver.navigate().refresh();
                    utilites.waits().waitForOverlayToDisappear();
                }
            }
        }

        if (!disappeared) {
            throw new AssertionError(
                    "الرسالة '"
                            + messageText
                            + "' لم تختف خلال الوقت المحدد بعد "
                            + maxRetries
                            + " محاولات.");
        }
    }
}
