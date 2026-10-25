package Plugin_ERP.Utiel;

import Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class SalesNavigationTest {
    private abstract static class Access extends SalesModulePage {
        Access(org.openqa.selenium.WebDriver driver){super(driver);}
        static String path(String route){return navigationPath(route);}
        static boolean reused(String current,String next){return reusesFormComponent(current,next);}
    }

    @Test public void linkedReturnQueryDoesNotBecomePartOfRouterPath() {
        Assert.assertEquals(Access.path("return-sales/new?sourceTransactionId=owned-id&sourceTransactionType=Sales"),"/return-sales/new");
        Assert.assertEquals(Access.path("invoice/new?sourceTransactionId=owned-id&action=copy"),"/invoice/new");
    }
    @Test public void regularRoutesKeepTheirOwnIdentity() {
        Assert.assertEquals(Access.path("/invoice/new/"),"/invoice/new");
        Assert.assertEquals(Access.path("return-sales/edit/owned-id"),"/return-sales/edit/owned-id");
        Assert.assertNotEquals(Access.path("invoice/new"),Access.path("return-sales/new"));
    }

    @Test public void sourceQueryChangeRequiresReloadingTheMountedReturnForm() {
        String base = "https://demo.example/#/return-sales/new";
        Assert.assertTrue(Access.reused(base, base + "?sourceTransactionId=owned"));
        Assert.assertTrue(Access.reused(base + "?sourceTransactionId=first", base + "?sourceTransactionId=second"));
        Assert.assertTrue(Access.reused(base, base));
        Assert.assertFalse(Access.reused("https://demo.example/#/invoice/new", base));
    }
}
