package Plugin_ERP.System_Modules.Login;

import Plugin_ERP.Bots.LogUtils;
import Plugin_ERP.Models.InvoiceRow;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.Models.ReportTotals;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

/** Document Grid Editing: focused operations used by the original page facade. */
final class DocumentGridEditing {
    private final DocumentPage owner;
    private final WebDriver driver;
    private final Plugin_ERP.Bots.ElmentAction utilites;
    DocumentGridEditing(DocumentPage owner, WebDriver driver) {
        this.owner = owner;
        this.driver = driver;
        this.utilites = owner.context().elements();
    }
    private final By SaveAndPrintButton = By.cssSelector("button[data-action-type='saveAndPrint']");
    private final By EditableRows = By.xpath("//tbody//tr[@role='row' and .//input[not(@type='hidden')]]");

    void WaitEditeForBills() {
        utilites.waits().waitForOverlayToDisappear();
        utilites.waits().waitForExchangeEditPageForEditBills(SaveAndPrintButton,EditableRows);
    }

    void clickDeleteRows(int... rowNumbers){
        utilites.waits().waitForOverlayToDisappear();
        owner.WaitEditeForBills();
        utilites.grid().clickDeleteRows(rowNumbers);
        utilites.waits().waitForOverlayToDisappear();
        LogUtils.info("تم حذف اى صف");

    }

    void EditForDamage(){
        utilites.waits().waitForOverlayToDisappear();

        utilites.ClickElement(
                utilites.getLastEditButtonForAdjustDamage()
        );
        owner.WaitEditeForBills();
        LogUtils.info("تم الضغط على تعديل اخر صف موجود");
    }

    void DeleteForDamage(){
        utilites.grid().deleteAllRowsSvg(owner.Clickyestodelet);
        LogUtils.info("تم حذف جميع السجلات في الجريد");
    }

    void DeleteByName(String Text  ) {
        utilites.waits().waitForOverlayToDisappear();
        utilites.grid().deleteAllRowsByType(Text,owner.Clickyestodelet);
    }
}
