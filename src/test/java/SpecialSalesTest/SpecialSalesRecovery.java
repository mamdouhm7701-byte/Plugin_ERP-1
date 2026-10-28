/* شرح الملف: استكمال تنظيف بيانات تجربة سابقة من سجلها المحدد، ضمن المطعم ونقاط البيع.
 */
package SpecialSalesTest;

import Plugin_ERP.Tests.LoginTest.BaseTest;

public final class SpecialSalesRecovery extends BaseTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 1)
            throw new IllegalArgumentException("Supply the exact owned journal suffix");
        var run = new SpecialSalesRecovery();
        try {
            run.setup();
            try (var data = SpecialSalesTestData.resume(run.driver, args[0])) {}
        } finally {
            run.tearDown();
        }
    }
}
