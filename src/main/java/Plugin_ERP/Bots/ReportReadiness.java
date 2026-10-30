/* شرح الملف: ينتظر تقريرًا انتهى تحميله وظهر فيه نص، بمهلة واحدة تشمل كل علامات التحميل بدل انتظار مهلة كاملة لكل علامة. */
package Plugin_ERP.Bots;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

final class ReportReadiness {
    private static final By LOADERS =
            By.cssSelector(
                    ".e-reportviewer-progressbar,.e-reportviewer-loadingindicator,[class*='loading'],[class*='spinner']");
    private static final By TEXT =
            By.cssSelector("div.e-reportviewer-paragrap div[style*='white-space: pre-wrap']");

    private ReportReadiness() {}

    static void await(WebDriver driver, Duration timeout) {
        Plugin_ERP.Util.UiPolling.configure(new WebDriverWait(driver, timeout))
                .withMessage("التقرير لم يكتمل: ننتظر انتهاء التحميل وظهور نص التقرير")
                .until(ReportReadiness::ready);
    }

    static boolean ready(WebDriver driver) {
        try {
            if (!"complete"
                    .equals(
                            ((JavascriptExecutor) driver)
                                    .executeScript("return document.readyState"))) return false;
            if (driver.findElements(LOADERS).stream().anyMatch(WebElement::isDisplayed))
                return false;
            return driver.findElements(TEXT).stream()
                    .anyMatch(element -> !element.getText().trim().isEmpty());
        } catch (NoSuchElementException | StaleElementReferenceException replaced) {
            return false;
        }
    }
}
