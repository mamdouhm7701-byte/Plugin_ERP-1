/* شرح الملف: تنفيذ حالات العملاء الخاصة بـتصدير القوائم والتحقق من الملف.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Customers;
import io.qameta.allure.*;
import java.io.IOException;

final class CustomersExportsChecks {
    private final CustomersTest owner;

    CustomersExportsChecks(CustomersTest owner) {
        this.owner = owner;
    }

    void ExportCustomersToPdf() throws IOException {
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        DealerModelExportAssertions.assertExportDownloaded(
                owner.caseDriver(), () -> customers.ExportList("pdf"), "pdf");
    }

    void ExportCustomersToExcel() throws IOException {
        Customers customers = new Customers(owner.caseDriver());
        customers.NavigationHelper(Pages.CUSTOMERS);
        DealerModelExportAssertions.assertExportDownloaded(
                owner.caseDriver(), () -> customers.ExportList("excel"), "excel");
    }
}
