/* شرح الملف: مساعدة لفتح تقرير البيع المطبوع ومراجعته وإغلاق نوافذ الطباعة. فائدتها التأكد من محتوى
 * النسخة المطبوعة للمستند.
 */
package Plugin_ERP.System_Modules.Login.Invoice;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class SalesPrintWindows {
    private final SalesModulePage owner;
    private final WebDriver driver;

    SalesPrintWindows(SalesModulePage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    String slWindowHandle() {
        return driver.getWindowHandle();
    }

    void slCloseAuxiliaryWindows(String main) {
        for (String window : new ArrayList<>(driver.getWindowHandles()))
            if (!window.equals(main)) {
                driver.switchTo().window(window);
                driver.close();
            }
        driver.switchTo().window(main);
    }

    void slVerifyPrintedReport(String main, String item) {
        owner.slVerifyPrintedReport(main, item, () -> {});
    }

    void slVerifyPrintedReport(String main, String item, Runnable contentAssertions) {
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(d -> d.getWindowHandles().size() > 1);
        driver.switchTo()
                .window(
                        driver.getWindowHandles().stream()
                                .filter(w -> !w.equals(main))
                                .findFirst()
                                .orElseThrow());
        try {
            owner.utilitesReportReady();
            owner.AssertTextInReport(item);
            contentAssertions.run();
        } finally {
            driver.close();
            driver.switchTo().window(main);
            owner.slDismiss();
        }
    }
}
