/* شرح الملف: تنفيذ حالات العملاء الخاصة بـعرض القوائم والبحث واستمرار السجلات.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Customers;
import io.qameta.allure.*;

final class CustomersListsChecks {
    private final CustomersTest owner;

    CustomersListsChecks(CustomersTest owner) {
        this.owner = owner;
    }

    void CU_001_CustomerListStructure() {
        Customers page = new Customers(owner.caseDriver());
        page.NavigationHelper(Pages.CUSTOMERS);
        owner.seedRecord(page, Pages.CUSTOMERS, "LIST");
        DealerModelScreenAssertions.assertListAndActionsDisplayed(page);
    }
}
