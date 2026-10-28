/* شرح الملف: شاشة فاتورة المشتريات: بنسجل المورد والأصناف والكميات والأسعار والسداد ويمكن الانتقال
 * للمرتجع. فائدتها توثيق الشراء ومراجعة المبلغ المستحق للمورد.
 */
package Plugin_ERP.System_Modules.Login.purches;

import Plugin_ERP.System_Modules.Login.AbstractForPurchases;
import io.qameta.allure.Step;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PurchesBillsPage extends AbstractForPurchases {
    public PurchesBillsPage(WebDriver driver) {
        super(driver);
    }

    private By ClickToTransToReturnOrder =
            By.xpath("//button[.//*[contains(@id,'fileText-icon')]]");

    @Step("تحويل فاتورة المشتريات إلى مرتجع")
    public void ClickToTransToReturnOrder() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ClickToTransToReturnOrder);
        utilites.waits().waitForOverlayToDisappear();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath(
                                        "//th[@role='columnheader'][.//div[normalize-space()='الاسم']]")));
    }
}
