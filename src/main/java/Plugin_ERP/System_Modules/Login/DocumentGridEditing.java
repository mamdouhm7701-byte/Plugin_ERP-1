/* شرح الملف: مساعدة لتعديل المستندات وحذف البنود وسجلات التالف. فائدتها تصحيح تفاصيل الحركة عند
 * المراجعة.
 */
package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

final class DocumentGridEditing {
    private final DocumentPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElementActions utilites;

    DocumentGridEditing(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }

    private final By SaveAndPrintButton = By.cssSelector("button[data-action-type='saveAndPrint']");
    private final By EditableRows =
            By.xpath("//tbody//tr[@role='row' and .//input[not(@type='hidden')]]");

    void WaitEditeForBills() {
        utilites.waits().waitForExchangeEditPageForEditBills(SaveAndPrintButton, EditableRows);
    }

    void clickDeleteRows(int... rowNumbers) {
        owner.WaitEditeForBills();
        utilites.grid().clickDeleteRows(rowNumbers);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم حذف اى صف");
    }

    void EditForDamage() {
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(utilites.getLastEditButtonForAdjustDamage());
        owner.WaitEditeForBills();
        LogUtils.info("تم الضغط على تعديل اخر صف موجود");
    }

    void DeleteForDamage() {
        utilites.grid().deleteAllRowsSvg(owner.Clickyestodelet);
        LogUtils.info("تم حذف جميع السجلات في الجريد");
    }

    void DeleteByName(String Text) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.grid().deleteAllRowsByType(Text, owner.Clickyestodelet);
    }
}
