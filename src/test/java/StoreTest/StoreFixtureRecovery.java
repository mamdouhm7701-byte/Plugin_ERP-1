package StoreTest;

import Plugin_ERP.System_Modules.Login.StoreModel.StoreModulePage;
import Plugin_ERP.Tests.LoginTest.BaseTest;

/** Explicit maintenance entry point; arguments are journal IDs, never tenant IDs. */
public final class StoreFixtureRecovery extends BaseTest {
    public static void main(String[] journalIds) throws Exception {
        if (journalIds.length == 0)
            throw new IllegalArgumentException("Pass the warehouse journal IDs to recover");
        StoreFixtureRecovery recovery = new StoreFixtureRecovery();
        try {
            recovery.setup();
            new StoreModulePage(recovery.driver).smAwaitSession();
            java.util.List<String> failures = new java.util.ArrayList<>();
            for (String id : journalIds)
                try {
                    StoreTestData.resume(recovery.driver, id).close();
                } catch (AssertionError | RuntimeException e) {
                    failures.add(id + ": " + e.getMessage());
                }
            if (!failures.isEmpty()) throw new AssertionError(String.join("; ", failures));
        } finally {
            recovery.tearDown();
        }
    }
}
