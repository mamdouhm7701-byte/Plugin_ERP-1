/* شرح الملف: مساعدة لفتح تقرير الشراء المطبوع ومراجعته وإغلاق نوافذ الطباعة. فائدتها التأكد من ظهور
 * بيانات المستند في نسخته المطبوعة.
 */
package Plugin_ERP.System_Modules.Login.purches;

import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class PurchasePrintWindows {
    private final PurchaseModulePage owner;
    private final WebDriver driver;

    PurchasePrintWindows(PurchaseModulePage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
    }

    String puWindowHandle() {
        return driver.getWindowHandle();
    }

    void puCloseAuxiliaryWindows(String main) {
        for (String window : new ArrayList<>(driver.getWindowHandles()))
            if (!window.equals(main)) {
                driver.switchTo().window(window);
                driver.close();
            }
        driver.switchTo().window(main);
    }

    void puVerifyPrintedReport(String main, String item) {
        owner.puVerifyPrintedReport(main, item, () -> {});
    }

    void puVerifyPrintedReport(String main, String item, Runnable contentAssertions) {
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
            owner.puDismiss();
        }
    }
}
