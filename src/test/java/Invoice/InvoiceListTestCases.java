package Invoice;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.Invoice.Invoice;
import Plugin_ERP.Tests.LoginTest.BaseTest;

public class InvoiceListTestCases extends BaseTest {

    private Invoice openInvoiceList() {
        Invoice invoice = new Invoice(driver);
        invoice.NavigationHelper(Pages.INVOICELIST);
        return invoice;
    }


}
