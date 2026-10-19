package Invoice;
import Plugin_ERP.Tests.LoginTest.BaseTest;
/** Explicit recovery of a single journal, guarded by its recorded origin and branch. */
public final class SalesFixtureRecovery extends BaseTest {
  public static void main(String[] args) throws Exception {
    if(args.length!=1||!args[0].matches("[a-f0-9-]+"))throw new IllegalArgumentException("Pass exactly the owned journal suffix");
    var runner=new SalesFixtureRecovery();
    try{runner.setup();new Plugin_ERP.System_Modules.Login.Invoice.SalesModulePage(driver).slAwaitSession();SalesTestData.resume(driver,args[0]).close();}
    finally{runner.tearDown();}
  }
}
