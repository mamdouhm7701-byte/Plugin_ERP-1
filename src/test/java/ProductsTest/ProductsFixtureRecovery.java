package ProductsTest;

public final class ProductsFixtureRecovery extends Plugin_ERP.Tests.LoginTest.BaseTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !args[0].matches("[a-f0-9]{8}-[a-f0-9]{3}"))
            throw new IllegalArgumentException("Pass one exact product fixture journal suffix");
        var runner = new ProductsFixtureRecovery();
        try {
            runner.setup();
            new Plugin_ERP.System_Modules.Login.Products.ProductsModulePage(runner.driver)
                    .prAwaitSession();
            ProductsTestData.resume(runner.driver, args[0]).close();
        } finally {
            runner.tearDown();
        }
    }
}
