package GenralAccountantTest;

import Plugin_ERP.Enums.Pages;
import Plugin_ERP.System_Modules.Login.GenralAccountant.VouchersPage;
import Plugin_ERP.Tests.LoginTest.BaseTest;
import org.testng.annotations.Test;

public class VouchersTest extends BaseTest {

    @Test
    public void DeleteAllVouchersTest() {
        VouchersPage v = new VouchersPage(driver);
        v.NavigationHelper(Pages.ORDERS);
        v.DeleteAll();
    }



}
