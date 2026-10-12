package Invoice;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.Models.InvoiceTotals;
import Plugin_ERP.System_Modules.Login.Invoice.Invoice;
import Plugin_ERP.System_Modules.Login.Login.LoginPage;
import Plugin_ERP.System_Modules.Login.purches.PurchesBillsPage;
import Plugin_ERP.System_Modules.Login.purches.ReturnOrderPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import Plugin_ERP.Utiel.PropertyReader;
import org.testng.annotations.Test;

import java.util.Map;

public class InvoiceTest extends BaseTest {


    @Test
    public void AddInvoice() {

        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);
        invoice.SelectFromDropDawn("آجل", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.SelectFromDropDawn("مندوب تست", "مندوب");
        invoice.SelectFromDropDawn("قائمه اسعار", "قوائم الأسعار");
        invoice.ClickAddItemsBtn();
        invoice.ClickPlusButtonByRow(2, 2);
        invoice.ClickSaveForProductBtn();
        invoice.validateInvoiceAllBusiness();
    }


    @Test
    public void AddInvoiceCash() {

        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);
        invoice.SelectFromDropDawn("نقدي", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.SelectFromDropDawn("مندوب تست", "مندوب");
        invoice.SelectFromDropDawn("قائمه اسعار", "قوائم الأسعار");
        invoice.ClickAddItemsBtn();
        invoice.ClickPlusButtonByRow(2, 2);
        invoice.ClickSaveForProductBtn();
        // ================= Business Validation =================
        InvoiceTotals totals = invoice.validateBusinessTotals();

        Map<String, String> dataBeforeSave =
                invoice. GetNamesQuantitiesAndPricesForInv();

        invoice.ClickOtherPay();
        invoice.SelectFromDropDawn("طريقة دفع", "طريقة الدفع");
        invoice.ClickPlusToAddPay();
        invoice.ClickSaveAndPrintButton();
        invoice.CompareNamesQuantitiesAndPricesWithReport(dataBeforeSave);
        invoice.validateReport(totals);
    }

    @Test(priority = 3)
    public void AddPurchesBillsByNameTest() {

        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICE);
        invoice.SelectFromDropDawn("آجل", "نوع السداد");
        invoice.SelectFromDropDawn("المخزن الرئيسي", "المخزن");
        invoice.ClickShift();
        invoice.ClickOnNameRadioBtn();
        invoice.SearchAndSelectProduct("سكر");
        invoice.SetValueByRow("السعر", 1, 200);
        invoice.SearchAndSelectProduct("كرواسون");
        invoice.SetValueByRow("السعر", 2, 200);
        invoice.SearchAndSelectProduct("دقيق");
        invoice.SetValueByRow("السعر", 3, 200);
        invoice.SetQuantitiesForRows(1, 10, 2, 10, 3, 10);
        invoice.validateInvoiceAllBusiness();
        invoice.ClickSaveButton();

    }




}


