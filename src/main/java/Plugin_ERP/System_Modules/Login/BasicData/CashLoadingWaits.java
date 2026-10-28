/* شرح الملف: مساعدة للتأكد من اكتمال عرض بيانات الخزائن وانتهاء الرسائل المؤقتة. فائدتها إتاحة
 * التعامل مع القائمة بعد ظهور بياناتها.
 */
package Plugin_ERP.System_Modules.Login.BasicData;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

final class CashLoadingWaits {
    private final WebDriver driver;

    CashLoadingWaits(WebDriver driver) {
        this.driver = driver;
    }

    void waitForCashPageLoading() {
        By loadingOverlays =
                By.cssSelector("div.b-overlay, div.position-absolute.bg-light.rounded-lg");
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(
                        d ->
                                d.findElements(loadingOverlays).stream()
                                        .noneMatch(
                                                element -> {
                                                    try {
                                                        return element.isDisplayed();
                                                    } catch (
                                                            org.openqa.selenium
                                                                            .StaleElementReferenceException
                                                                    ignored) {
                                                        Plugin_ERP.Bots.LogUtils.debug(
                                                                "CashPage condition unavailable: "
                                                                        + ignored.getClass()
                                                                                .getSimpleName());
                                                        return false;
                                                    }
                                                }));
    }

    void waitForCashAlertToDisappear() {
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(
                        d ->
                                d
                                        .findElements(
                                                By.cssSelector(
                                                        ".swal2-container.swal2-backdrop-show"))
                                        .stream()
                                        .noneMatch(WebElement::isDisplayed));
    }
}
