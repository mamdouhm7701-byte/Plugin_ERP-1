/* شرح الملف: تنفيذ حالات العملاء الخاصة بـفتح النماذج وإلغاء التغييرات.
 */
package DealerModelTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.DealerModel.Customers;
import io.qameta.allure.*;

final class CustomersFormsChecks {
    private final CustomersTest owner;

    CustomersFormsChecks(CustomersTest owner) {
        this.owner = owner;
    }

    void CU_002_CloseCustomerFormWithoutSaving() {
        Customers page = new Customers(owner.caseDriver());
        page.NavigationHelper(Pages.CUSTOMERS);
        DealerModelScreenAssertions.assertNewFormCanBeClosedWithoutSaving(page);
    }

    void CU_003_CancelCustomerEditWithoutSaving() {
        Customers page = new Customers(owner.caseDriver());
        page.NavigationHelper(Pages.CUSTOMERS);
        owner.assertOwnedEditCancelled(page, Pages.CUSTOMERS);
    }

    void CU_004_CancelCustomerDelete() {
        Customers page = new Customers(owner.caseDriver());
        page.NavigationHelper(Pages.CUSTOMERS);
        owner.assertOwnedDeleteCancelled(page, Pages.CUSTOMERS);
    }
}
