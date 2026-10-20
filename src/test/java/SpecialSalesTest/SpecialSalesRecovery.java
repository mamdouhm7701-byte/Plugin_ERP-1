package SpecialSalesTest;

import Plugin_ERP.Tests.LoginTest.BaseTest;

/** Explicit recovery accepts one exact owned journal, never tenant-wide deletion. */
public final class SpecialSalesRecovery extends BaseTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 1)
            throw new IllegalArgumentException("Supply the exact owned journal suffix");
        var run = new SpecialSalesRecovery();
        try {
            run.setup();
            try (var data = SpecialSalesTestData.resume(driver, args[0])) {}
        } finally {
            run.tearDown();
        }
    }
}
