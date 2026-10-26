package TestSupport;

/** Explicit recovery never scans or selects another run's journal. */
public final class BasicDataFixturesRecovery extends Plugin_ERP.Tests.LoginTest.BaseTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !args[0].matches("[a-f0-9]{8}-[a-f0-9]{3}"))
            throw new IllegalArgumentException("Pass one exact basic-data fixture journal suffix");
        var runner = new BasicDataFixturesRecovery();
        try {
            runner.setup();
            BasicDataFixtures.resume(runner.driver, args[0]).close();
        } finally {
            runner.tearDown();
        }
    }
}
