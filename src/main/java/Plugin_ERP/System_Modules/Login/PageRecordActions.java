package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.*;
import Plugin_ERP.Enums.Pages;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

/** Page Record Actions: focused operations used by the original page facade. */
final class PageRecordActions {
    private final Abstract owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElmentAction utilites;
    PageRecordActions(Abstract owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }
    // Locators
    private By ClickNew = By.cssSelector("button[data-action-type='new']");
    private By SaveButton = By.cssSelector("button[data-action-type='save']");
    private By CloseButton = By.xpath("//button[normalize-space()='إغلاق']");

    void ClickNew() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(ClickNew);
        utilites.waits().waitForOverlayToDisappear();
    }

    void ClickCloseButton() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(CloseButton);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم الضغط على زر الإغلاق");
    }

    void ClickSaveButton() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(SaveButton);
        LogUtils.info("تم الضغط على زر الحفظ");
    }

    void Edit() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.ClickElement(utilites.getLastEditButtonForCustomerReceipt());
        utilites.waits().waitForOverlayToDisappear();
        utilites.waits().waitForExchangeEditPage(SaveButton);

        LogUtils.error("تم الضغط على زر التعديل لآخر سجل في الجريد");
    }

    void Delete() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.grid().deleteRow(utilites.grid().getLastDeleteButton(), owner.Clickyestodelet);
        LogUtils.info("تم حذف آخر سجل في الجريد");
    }

    void DeleteAll() {
        utilites.grid().deleteAllRows(owner.Clickyestodelet);
        LogUtils.info("تم حذف جميع السجلات في الجريد");
    }

    void AssertReceiptAdded(String ArabicName) {
        utilites.waits().waitForOverlayToDisappear();
        String pk = utilites.grid().getPkByText(ArabicName);
        Assert.assertNotNull(pk, "❌ السجل غير موجود: " + ArabicName);
        LogUtils.info("✅ السجل موجود بـ PK = " + pk);
    }
}
