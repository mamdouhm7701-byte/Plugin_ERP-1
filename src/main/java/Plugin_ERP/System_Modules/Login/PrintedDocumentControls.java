/* شرح الملف: مساعدة لحفظ المستند وطباعته ومراجعة النصوص والأرقام والتاريخ في التقرير. فائدتها
 * التأكد من ظهور بيانات المستند في الطباعة.
 */
package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.*;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

final class PrintedDocumentControls {
    private final Abstract owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;
    private final Plugin_ERP.Bots.BrowserAction browserAction;

    PrintedDocumentControls(Abstract owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
        this.browserAction = owner.context().browser();
    }

    private By SaveAndPrintButton = By.cssSelector("button[data-action-type='saveAndPrint']");

    void ClickSaveAndPrintButton() {
        utilites.waits().waitForOverlayToDisappear();
        var originalTabs = driver.getWindowHandles();
        utilites.ClickElement(SaveAndPrintButton);
        LogUtils.info("تم الحفظ والطباعة");
        utilites.waits().switchToNewTab(originalTabs);
        LogUtils.info("تم فتح التقرير");
        utilites.waits().waitForReportReady(60);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم ظهور كل بيانات التقرير");
    }

    void CloserReport() {
        browserAction.closeCurrentTab();
        browserAction.switchToMainTab();
        LogUtils.info("تم إغلاق التقرير والرجوع للتاب الرئيسي");
    }

    void AssertTextInReport(String Text) {
        Assert.assertTrue(utilites.validation().IsTextDisplayed(Text));

        LogUtils.info("تم التحقق من النص في التقرير: " + Text);
    }

    void AssertNumberInReport(String Number) {
        Assert.assertTrue(utilites.validation().IsPriceDisplayedForFin(Number));
        LogUtils.info("تم التحقق من الرقم في التقرير: " + Number);
    }

    void AssertDateInReport(String Date) {
        Assert.assertTrue(utilites.validation().IsDateDisplayed(Date));
        LogUtils.info("تم التحقق من التاريخ في التقرير: " + Date);
    }
}
