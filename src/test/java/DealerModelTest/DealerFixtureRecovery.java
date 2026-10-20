package DealerModelTest;

/** Explicit recovery: page records first, then their fixture identities and pending writes. */
public final class DealerFixtureRecovery extends DealerScenarioTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 2
                || !java.util.Set.of("page", "fixtures").contains(args[0])
                || !args[1].matches("[a-f0-9]{8}-[a-f0-9]{3}"))
            throw new IllegalArgumentException("Pass page|fixtures and one exact journal suffix");
        var runner = new DealerFixtureRecovery();
        try {
            runner.setup();
            new TestSupport.FixtureSession(driver);
            if (args[0].equals("page")) {
                runner.resumeOwnedRecords(args[1]);
                runner.retryRemainingScenarioCleanup();
            } else DealerTestData.resume(driver, args[1]).close();
        } finally {
            runner.tearDown();
        }
    }
}
