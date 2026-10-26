package Purches;

import Plugin_ERP.Tests.LoginTest.BaseTest;

/** Recover one exact journal in its original origin and branch. */
public final class PurchaseFixtureRecovery extends BaseTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !args[0].matches("[a-f0-9-]+"))
            throw new IllegalArgumentException("Pass the owned journal suffix");
        var runner = new PurchaseFixtureRecovery();
        try {
            runner.setup();
            new Plugin_ERP.System_Modules.Login.purches.PurchaseModulePage(runner.driver)
                    .puAwaitSession();
            PurchaseTestData.resume(runner.driver, args[0]).close();
        } finally {
            runner.tearDown();
        }
    }
}
